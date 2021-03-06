package battleship.model.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Pattern;

import battleship.gui.game.GameServerViewController;
import battleship.model.board.BoardState;
import battleship.model.board.ShipFactory;
import battleship.model.procedure.GameProcedure;
import battleship.model.procedure.GameProcedure.Procedure;
import battleship.model.user.Player;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

/**
 * Klasa odpowiedzialna za uruchomienie gry w trybie "server" w osobnym watku.
 * 
 * @author Wojciech Antczak
 *
 */
public class ServerNetworkGameThread extends Thread {
	private volatile TextArea textLogServer;
	private GameProcedure serverProcedure;
	private GameServerViewController gameServerViewController;
	
	private boolean connectedToClient;
	private volatile boolean gameOver = false;
	private volatile boolean playerTurn = true;
	private volatile boolean opponentShipsReady = false;
	private int shipsCount = ShipFactory.iloscStatkow;

	private ServerSocket serverSocket; //Deklaracja pojedynczego serverSocketa
    private Socket serverConnection; //Socket polaczenia
	private int connectionPort = 12345;
	
	//WATKI W KLASIE
	private Thread threadWaitingForOpponentShips;
	private Thread threadReadingSocket;

	//BUFFORY IN I OUT
	DataInputStream inStreamServer;
	DataOutputStream outStreamServer ;
	
	
	private CommunicationMessage communicationMessage;

	public ServerNetworkGameThread(TextArea textLogServer, GameProcedure serverProcedure, GameServerViewController gameServerViewController) {
		this.textLogServer = textLogServer;
		this.serverProcedure = serverProcedure;
		this.gameServerViewController = gameServerViewController;
	}

	/**
	 * Metoda tworzaca oddzielny watek do obslugi przebiegu rozgrywki
	 * @author Wojciech Antczak
	 */
	public void run(){
		while(!isGameOver()){
			try{
				switch (serverProcedure.getProcedure()){
				case CONNECT_TO_CLIENT:{
					gameServerViewController.setTextAreaLogi("[SERVER] Connect to client process");
					connectToClient();
					gameServerViewController.setTextAreaLogi("[SERVER] Connected to client: "+connectedToClient);
					gameServerViewController.setTextAreaLogi("ROZPOCZECIE GRY!");
					gameServerViewController.setTextAreaLogi("ROZSTAW STATKI!");
					break;
				}
				
				case DEPLOY_SHIPS:{
					break;
				}
				
				case READY_TO_START:{
					Thread.sleep(100);

					if(threadWaitingForOpponentShips ==null) waitingForDeployedShips(); 
					break;
				}
				
				case PLAYING_GAME:{
					if(threadReadingSocket ==null) readingCommandGame();
					
					break;
				}

				default:
					break;
				}
			}
			
			catch(Exception ex){
				
			}
		}
		closeSocket();
	}
	
	
	/**Metoda obslugujaca polaczenie servera do klienta
	 * @author Wojciech Antczak
	 */
	private void connectToClient(){
		connectedToClient = false;
		try{
			serverSocket = new ServerSocket(connectionPort);
			serverSocket.setReuseAddress(true);
			gameServerViewController.setTextAreaLogi("[SERVER] Oczekiwanie na polaczenie z klientem...");
			serverConnection = serverSocket.accept();
			gameServerViewController.setTextAreaLogi("[SERVER] Nazwiazano polaczenie z klientem: [IP]: "+serverConnection.getInetAddress()+" [PORT]: "+serverConnection.getPort());

			if (serverConnection.isConnected()){
				connectedToClient = true;
				inStreamServer = new DataInputStream(serverConnection.getInputStream());
				outStreamServer = new DataOutputStream(serverConnection.getOutputStream());
				serverProcedure.setProcedure(Procedure.DEPLOY_SHIPS);
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void waitingForDeployedShips() throws InterruptedException{
		Runnable serverWaitingForOpponentShips = ()->{
			try{
				gameServerViewController.setTextAreaLogi("[SERVER]: Oczekiwanie na zakonczenie rozstawiania statkow przez Przeciwnika ");
				Thread.sleep(500);
				while(!opponentShipsReady){
					if(inStreamServer.readUTF().equals("READY")){
						outStreamServer.writeUTF("READY");
						serverProcedure.setProcedure(Procedure.PLAYING_GAME);
						opponentShipsReady = true;
					}
					else{
						Thread.sleep(500);
					}
				}
				gameServerViewController.setTextAreaLogi("[SERVER]: ODEBRANO INFO OD PRZECIWNIKA O ZAKONCZENIU USTAWIANIU STATKOW");
				gameServerViewController.setTextAreaLogi("TWOJA KOLEJ - ROZPOCZNIJ STRZALEM");

			}
			
			catch(Exception ex){
				ex.printStackTrace();
			}
		};
		threadWaitingForOpponentShips = new Thread(serverWaitingForOpponentShips); //utworzenie nowego Threada z metoda do polaczenia
		threadWaitingForOpponentShips.setName("Server - Waiting for ships");
		threadWaitingForOpponentShips.start(); //wystartowanie Threada
		//threadWaitingForOpponentShips.join(); //oczekiwanie na zakonczenie metody
	}
	
	
	private void readingCommandGame() throws InterruptedException{
		Runnable serverReading = ()->{
			while(!isGameOver()){
				try{
					String packetAll = inStreamServer.readUTF(); //pobranie pakietu ze Streama i zapisanie do nowego Stringa
					String[] packet = packetAll.split(Pattern.quote(";")); // Podzielenie komendy na poszczegolne parametry (podzielenie dzieki ";")
					Thread.sleep(10);
					gameServerViewController.setTextAreaLogi(packetAll);
					gameServerViewController.setTextAreaLogi("=============================");
					gameServerViewController.setTextAreaLogi("[COMMAND]: "+packet[0]);
					gameServerViewController.setTextAreaLogi("[USER]: "+packet[1]);
					gameServerViewController.setTextAreaLogi("[X]: "+packet[2]);
					gameServerViewController.setTextAreaLogi("[Y]: "+packet[3]);
					if(packet[0].equals("ANSWER")){
						gameServerViewController.setTextAreaLogi("[Stan Strzalu]: "+packet[4]);
					}
					//switch odpowiadajacy za obsluge komend
					switch (packet[0]){
					case "SHOT":{
						BoardState shotState = checkBoard(Integer.parseInt(packet[2]),Integer.parseInt(packet[3])); //parsowanie 
						gameServerViewController.setTextAreaLogi("[Stan Strzalu]: "+shotState);
						handlingCommand(Command.ANSWER, Player.SERVER_PLAYER, Integer.parseInt(packet[2]), Integer.parseInt(packet[3]),shotState);//odpowiedź
						if(shotState.toString().equals("STATEK_ZATOPIONY")||shotState.toString().equals("STATEK_TRAFIONY")){
							playerTurn = false;
						}else{
							playerTurn = true;
						}
						Platform.runLater(()->gameServerViewController.redraw1GridPane());
						Platform.runLater(()->gameServerViewController.redraw2GridPane());
						break;
					}
					
					case "ANSWER":{
						BoardState state = BoardState.valueOf(packet[4]);
						gameServerViewController.getClientBoard().setBoardCell(Integer.parseInt(packet[2]), Integer.parseInt(packet[3]), state);
						if(packet[4].equals("STATEK_ZATOPIONY")){
							gameServerViewController.getClientBoard().setSunk(Integer.parseInt(packet[2]),Integer.parseInt(packet[3]));
							shipsCount--;
							if(shipsCount == 0){
								handlingCommand(Command.END_GAME, Player.SERVER_PLAYER);//odpowiedz
								Platform.runLater(()->{
									Alert alert = new Alert(AlertType.INFORMATION);
									alert.setTitle("Information Dialog");
									alert.setHeaderText(null);
									alert.setContentText("KONIEC GRY! Wygral: SERVER ");
									alert.showAndWait();
								});
								Platform.runLater(()->gameServerViewController.getMenuViewController().setContentMain());
								setGameOver(true);
							}
						}
						if(packet[4].equals("STATEK_ZATOPIONY")||packet[4].equals("STATEK_TRAFIONY")){
							setPlayerTurn(true);
						}else{
							setPlayerTurn(false);
						}
						Platform.runLater(()->gameServerViewController.redraw1GridPane());
						Platform.runLater(()->gameServerViewController.redraw2GridPane());
						break;
					}
					
					case "END_GAME":{
						Platform.runLater(()->{
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Information Dialog");
							alert.setHeaderText(null);
							alert.setContentText("KONIEC GRY! Wygral: CLIENT ");
							alert.showAndWait();
						});
						Platform.runLater(()->gameServerViewController.getMenuViewController().setContentMain());
						setGameOver(true);
						break;
					}
					
					
					default: break;

					}
					Thread.sleep(100);
				}
				catch (EOFException ex){
					Platform.runLater(()->{
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error Dialog");
						alert.setHeaderText("Blad polaczenia");
						alert.setContentText("Klient zamknal polaczenie!");
						alert.showAndWait();
					});
					Platform.runLater(()->gameServerViewController.getMenuViewController().setContentMain());
					setGameOver(true);

				}

				catch (Exception ex){
					ex.printStackTrace();
				}
			}
		};
		threadReadingSocket = new Thread(serverReading);
		threadReadingSocket.start();
	}
	
	/**
	 * metoda odpowiedzialna za przesylanie informacji przez internet
	 * po oddaniu strzalu.
	 * 
	 * @author Pawel Czernek
	 *
	 */
	public void handlingCommand(Command command,Player own, int x, int y){
		try{
			communicationMessage = new CommunicationMessage(command,own,x,y);
			outStreamServer.writeUTF(communicationMessage.toString());
		}
		catch (SocketException ex){
			Platform.runLater(()->{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText("Blad polaczenia");
				alert.setContentText("Klient zamknal polaczenie!");
				alert.showAndWait();
			});
			Platform.runLater(()->gameServerViewController.getMenuViewController().setContentMain());
			setGameOver(true);

		} 
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * metoda odpowiedzialna za przesylanie informacji przez internet
	 * odpowiedz na strzal.
	 * 
	 * @author Pawel Czernek
	 *
	 */
	public void handlingCommand(Command command, Player own, int x, int y, BoardState state) {
		try{
			communicationMessage = new CommunicationMessage(command, own, x, y, state);
			outStreamServer.writeUTF(communicationMessage.toString());
		}
		catch (SocketException ex){
			Platform.runLater(()->{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText("Blad polaczenia");
				alert.setContentText("Klient zamknal polaczenie!");
				alert.showAndWait();
			});
			Platform.runLater(()->gameServerViewController.getMenuViewController().setContentMain());
			setGameOver(true);

		} 
		catch (IOException ex) {
			ex.printStackTrace();
		}

	}
	
	public void handlingCommand(Command command, Player own) {
		try{
			communicationMessage = new CommunicationMessage(command, own);
			outStreamServer.writeUTF(communicationMessage.toString());
		}
		catch (SocketException ex){
			Platform.runLater(()->{
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText("Blad polaczenia");
				alert.setContentText("Klient zamknal polaczenie!");
				alert.showAndWait();
			});
			Platform.runLater(()->gameServerViewController.getMenuViewController().setContentMain());
			setGameOver(true);

		} 
		catch (IOException ex) {
			ex.printStackTrace();
		}

	}



	public boolean isPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(boolean playerTurn) {
		this.playerTurn = playerTurn;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
	/**
	 * Sprawdzenie stanu planszy po strzale
	 * 
	 * @author Pawel Czernek
	 * @param x -wspolrzedna x sprawdzanego punktu
	 * @param y -wspolrzedna y sprawdzanego punktu
	 * @return zwraca aktualny stan planszy dla punktu
	 */
	public BoardState checkBoard(int x, int y) {
		gameServerViewController.getServerBoard().shot(x, y);
		if (gameServerViewController.getServerBoard().getBoardCell(x, y) == BoardState.STATEK_ZATOPIONY) {
			gameServerViewController.getServerBoard().setSunk(x, y);
		}
		return gameServerViewController.getServerBoard().getBoardCell(x, y);
	}
	
	private void closeSocket(){
		try{
			inStreamServer.close();
			outStreamServer.close();
			serverSocket.close(); //Deklaracja pojedynczego serverSocketa
		    serverConnection.close(); //Socket polaczenia
		}
		
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
}

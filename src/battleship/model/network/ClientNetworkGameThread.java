package battleship.model.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import battleship.gui.game.GameClientViewController;
import battleship.model.board.BoardState;
import battleship.model.procedure.GameProcedure;
import battleship.model.procedure.GameProcedure.Procedure;
import battleship.model.server.Server;
import battleship.model.user.Player;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ClientNetworkGameThread extends Thread {

	private volatile TextArea textLogClient;
	private GameProcedure clientProcedure;
	private GameClientViewController gameClientViewController;
	private Server gameServer;
	
	private Socket clientSocket;
	private boolean gameOver = false;
	private boolean playerTurn = true;
	private boolean opponentShipsReady = false;

	//WATKI W KLASIE
	private Thread threadWaitingForOpponentShips;
	private Thread threadConnectionToServer;
	private Thread threadReadingSocket;

	//BUFFORY IN I OUT
	DataInputStream inStreamClient;
	DataOutputStream outStreamClient;
	
	private CommunicationMessage communicationMessage;
	
	public ClientNetworkGameThread(TextArea textLogClient, GameProcedure clientProcedure, GameClientViewController gameClientViewController, Server gameServer) {
		this.textLogClient = textLogClient;
		this.clientProcedure = clientProcedure;
		this.gameClientViewController = gameClientViewController;
		this.gameServer = gameServer;
	}
	
	//=========================METODA GLOWNA WATKU=================================	Q
	public void run(){
		while(!isGameOver()){
			
			switch (clientProcedure.getProcedure()){
			case CONNECT_TO_SERVER:{
				connectToServer();
				textLogClient.appendText("ROZPOCZECIE GRY! \n ");
				textLogClient.appendText("ROZSTAW STATKI! \n ");
				break;
			}
			
			case DEPLOY_SHIPS:{
				break;
			}
			
			case READY_TO_START:{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(threadWaitingForOpponentShips ==null) waitingForDeployedShips(); 
				break;
			}
			
			case PLAYING_GAME:{
				if(threadReadingSocket ==null) readingCommandGame();

			}

			
			default:
				break;

		}
		}
	}
	
	/**Metoda polaczenia clienta do servera
	 * 
	 * @author Wojciech Antczak
	 */
	private void connectToServer() {
		if(clientSocket == null){
			try {
				Runnable clientConnection = ()->{
					try{
						textLogClient.appendText("[CLIENT]: Proba podlaczenia do serwera:  "+gameServer.getServerIP()+"\n");
						Thread.sleep(10);
						InetAddress serverAddress = InetAddress.getByName(gameServer.getServerIP()); 
						clientSocket = new Socket(serverAddress.getHostName(), 12345);
						textLogClient.appendText("[CLIENT]: Status polaczenia:  "+clientSocket.isConnected()+"\n");
						Thread.sleep(10);
						if (clientSocket.isConnected()){
							inStreamClient = new DataInputStream(clientSocket.getInputStream());
							outStreamClient = new DataOutputStream(clientSocket.getOutputStream());
							clientProcedure.setProcedure(Procedure.DEPLOY_SHIPS);
						}
					}
					
					catch(Exception ex){
						ex.printStackTrace();
					}
				};
				threadConnectionToServer = new Thread(clientConnection); //utworzenie nowego Threada z metoda do polaczenia
				threadConnectionToServer.start(); //wystartowanie Threada
				threadConnectionToServer.join(); //oczekiwanie na zakonczenie metody
			}
			
			catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	/**Metoda oczekiwania na ustawienie statkow przez przeciwnika
	 * @author Wojciech Antczak
	 */
	private void waitingForDeployedShips(){
		Runnable clientWaitingForOpponentShips = ()->{
			try{
				textLogClient.appendText("\n [CLIENT]: Oczekiwanie na zakonczenie rozstawiania statkow przez Przeciwnika \n ");
				while(!opponentShipsReady){
					Thread.sleep(100);
					outStreamClient.writeUTF("READY");
					if(inStreamClient.readUTF().equals("READY")){
						textLogClient.appendText("[CLIENT]: ODEBRANO INFO OD PRZECIWNIKA O ZAKONCZENIU USTAWIANIU STATKOW \n");
						clientProcedure.setProcedure(Procedure.PLAYING_GAME);
						opponentShipsReady = true;						
					}
					else{
						Thread.sleep(500);
					}
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		};
		threadWaitingForOpponentShips = new Thread(clientWaitingForOpponentShips); //utworzenie nowego Threada z metoda do polaczenia
		threadWaitingForOpponentShips.setName("Client - Waiting for ships");
		threadWaitingForOpponentShips.start(); //wystartowanie Threada
		//threadWaitingForOpponentShips.join(); //oczekiwanie na zakonczenie metody
	}

	
	/**Metoda odpowiedzialna za obsluge pakietow komunikacyjnych od serwera
	 * @author Wojciech Antczak
	 * 
	 */
	private void readingCommandGame(){
		Runnable clientReading = ()->{
			while(!isGameOver()){
				try{
					String packetAll = inStreamClient.readUTF(); //pobranie pakietu ze Streama i zapisanie do nowego Stringa
					String[] packet = packetAll.split(Pattern.quote(";")); // Podzielenie komendy na poszczegolne parametry (podzielenie dzieki ";")
					textLogClient.appendText("\n [COMMAND]: "+packet[0]);
					textLogClient.appendText("\n [USER]: "+packet[1]);
					textLogClient.appendText("\n [X]: "+packet[2]);
					textLogClient.appendText("\n [Y]: "+packet[3]);

					//switch odpowiadajacy za obsluge komend
					switch (packet[0]){
					case "SHOT":{
						checkBoard(Integer.parseInt(packet[2]),Integer.parseInt(packet[3])); //parsowanie 
						break;
					}
					
					case "ANSWER":{
						break;
					}
					
					case "INFORMATION":{
						break;
					}
					
					case "ERROR":{
						break;
					}
					
					default: break;

					}
					Thread.sleep(100);
				}
				
				catch (Exception ex){
					ex.printStackTrace();
				}
			}
		};
		threadReadingSocket = new Thread(clientReading);
		threadReadingSocket.start();
		
	}
	
	
	public void handlingCommand(Command command,Player own, int x, int y) throws IOException{
		communicationMessage = new CommunicationMessage(command,own,x,y);
		outStreamClient.writeUTF(communicationMessage.toString());
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
	
	
	/**Sprawdzenie planszy po strzale
	 * @author Pawel Czernek
	 */
	private void checkBoard(int x,int y){
		if(gameClientViewController.getServerBoard().getBoardCell(x, y) == BoardState.PUSTE_POLE){
			gameClientViewController.getServerBoard().setBoardCell(x,y,gameClientViewController.getClientBoard().shot(x, y));
			if(gameClientViewController.getServerBoard().getBoardCell(x, y) == BoardState.STATEK_ZATOPIONY)
				gameClientViewController.getServerBoard().setSunk(x, y);
		} else {
			//TODO:Wyslanie informacji do servera o zlym polu
			//textLogClient.appendText("Pole bylo juz ostrzelane, strzelaj jeszcze raz!");
		}
		//redraw1GridPane(clientBoard);
		//redraw2GridPane(serverBoard);*/
	}

}

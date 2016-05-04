package battleship.model.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Pattern;
import battleship.gui.game.GameClientViewController;
import battleship.model.board.BoardState;
import battleship.model.procedure.GameProcedure;
import battleship.model.procedure.GameProcedure.Procedure;
import battleship.model.server.Server;
import battleship.model.user.Player;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ClientNetworkGameThread extends Thread {

	private volatile TextArea textLogClient;
	private GameProcedure clientProcedure;
	private GameClientViewController gameClientViewController;
	private Server gameServer;

	private Socket clientSocket;
	private boolean gameOver = false;
	private boolean playerTurn = false;
	private boolean opponentShipsReady = false;
	private int shipsCount = 8;

	// WATKI W KLASIE
	private Thread threadWaitingForOpponentShips;
	private Thread threadConnectionToServer;
	private Thread threadReadingSocket;

	// BUFFORY IN I OUT
	DataInputStream inStreamClient;
	DataOutputStream outStreamClient;

	private CommunicationMessage communicationMessage;

	public ClientNetworkGameThread(TextArea textLogClient, GameProcedure clientProcedure,
			GameClientViewController gameClientViewController, Server gameServer) {
		this.textLogClient = textLogClient;
		this.clientProcedure = clientProcedure;
		this.gameClientViewController = gameClientViewController;
		this.gameServer = gameServer;
	}

	// =========================METODA GLOWNA
	// WATKU================================= Q
	public void run() {
		while (!isGameOver()) {

			switch (clientProcedure.getProcedure()) {
			case CONNECT_TO_SERVER: {
				connectToServer();
				gameClientViewController.setTextAreaLogi("ROZPOCZECIE GRY!");
				gameClientViewController.setTextAreaLogi("ROZSTAW STATKI!");
				break;
			}

			case DEPLOY_SHIPS: {
				break;
			}

			case READY_TO_START: {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (threadWaitingForOpponentShips == null)
					waitingForDeployedShips();
				break;
			}

			case PLAYING_GAME: {
				if (threadReadingSocket == null)
					readingCommandGame();

			}

			default:
				break;

			}
		}
	}

	/**
	 * Metoda polaczenia clienta do servera
	 * 
	 * @author Wojciech Antczak
	 */
	private void connectToServer() {
		if (clientSocket == null) {
			try {
				Runnable clientConnection = () -> {
					try {
						gameClientViewController.setTextAreaLogi("[CLIENT]: Proba podlaczenia do serwera:  " + gameServer.getServerIP());
						Thread.sleep(10);
						InetAddress serverAddress = InetAddress.getByName(gameServer.getServerIP());
						clientSocket = new Socket(serverAddress.getHostName(), 12345);
						gameClientViewController.setTextAreaLogi("[CLIENT]: Status polaczenia:  " + clientSocket.isConnected());
						Thread.sleep(10);
						if (clientSocket.isConnected()) {
							inStreamClient = new DataInputStream(clientSocket.getInputStream());
							outStreamClient = new DataOutputStream(clientSocket.getOutputStream());
							clientProcedure.setProcedure(Procedure.DEPLOY_SHIPS);
						}
					}

					catch (Exception ex) {
						ex.printStackTrace();
					}
				};
				threadConnectionToServer = new Thread(clientConnection); // utworzenie
																			// nowego
																			// Threada
																			// z
																			// metoda
																			// do
																			// polaczenia
				threadConnectionToServer.start(); // wystartowanie Threada
				threadConnectionToServer.join(); // oczekiwanie na zakonczenie
													// metody
			}

			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Metoda oczekiwania na ustawienie statkow przez przeciwnika
	 * 
	 * @author Wojciech Antczak
	 */
	private void waitingForDeployedShips() {
		Runnable clientWaitingForOpponentShips = () -> {
			try {
				gameClientViewController.setTextAreaLogi("[CLIENT]: Oczekiwanie na zakonczenie rozstawiania statkow przez Przeciwnika");
				while (!opponentShipsReady) {
					Thread.sleep(100);
					outStreamClient.writeUTF("READY");
					if (inStreamClient.readUTF().equals("READY")) {
						gameClientViewController.setTextAreaLogi("[CLIENT]: ODEBRANO INFO OD PRZECIWNIKA O ZAKONCZENIU USTAWIANIU STATKOW");
						clientProcedure.setProcedure(Procedure.PLAYING_GAME);
						opponentShipsReady = true;
					} else {
						Thread.sleep(500);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		};
		threadWaitingForOpponentShips = new Thread(clientWaitingForOpponentShips); // utworzenie nowego Threada z metoda do polaczenia
		threadWaitingForOpponentShips.setName("Client - Waiting for ships");
		threadWaitingForOpponentShips.start(); // wystartowanie Threada
		// threadWaitingForOpponentShips.join(); //oczekiwanie na zakonczenie
		// metody
	}

	/**
	 * Metoda odpowiedzialna za obsluge pakietow komunikacyjnych od serwera
	 * 
	 * @author Wojciech Antczak
	 * 
	 */
	private void readingCommandGame() {
		Runnable clientReading = () -> {
			while (!isGameOver()) {
				try {
					String packetAll = inStreamClient.readUTF(); // pobranie pakietu ze	Streama i zapisanie do nowego Stringa
					String[] packet = packetAll.split(Pattern.quote(";")); // Podzielenie komendy na poszczegolne parametry (podzielenie dzieki ";")
					Thread.sleep(10);

					gameClientViewController.setTextAreaLogi(packetAll);
					gameClientViewController.setTextAreaLogi("=============================");
					gameClientViewController.setTextAreaLogi("[COMMAND]: " + packet[0]);
					gameClientViewController.setTextAreaLogi("[USER]: " + packet[1]);
					gameClientViewController.setTextAreaLogi("[X]: " + packet[2]);
					gameClientViewController.setTextAreaLogi("[Y]: " + packet[3]);
					if(packet[0].equals("ANSWER")){
						gameClientViewController.setTextAreaLogi("[Stan Strzalu]: "+packet[4]);
					}

					// switch odpowiadajacy za obsluge komend
					switch (packet[0]) {
					case "SHOT": {

						BoardState shotState = checkBoard(Integer.parseInt(packet[2]), Integer.parseInt(packet[3])); // parsowanie
						handlingCommand(Command.ANSWER, Player.CLIENT_PLAYER, Integer.parseInt(packet[2]),	Integer.parseInt(packet[3]), shotState);// odpowiedź
						if(shotState.equals("STATEK_ZATOPIONY")||shotState.equals("STATEK_TRAFIONY")){
							playerTurn = false;
						}else{
							playerTurn = true;
						}
						Platform.runLater(()->gameClientViewController.redraw1GridPane());
						Platform.runLater(()->gameClientViewController.redraw2GridPane());
						break;
					}

					case "ANSWER": {
						BoardState state = BoardState.valueOf(packet[4]);
						gameClientViewController.getServerBoard().setBoardCell(Integer.parseInt(packet[2]), Integer.parseInt(packet[3]), state);
						if(packet[4].equals("STATEK_ZATOPIONY")){
							gameClientViewController.getServerBoard().setSunk(Integer.parseInt(packet[2]),Integer.parseInt(packet[3]));
							shipsCount--;
							if(shipsCount == 0){
								gameOver = true;
								Platform.runLater(()->gameClientViewController.setTextAreaLogi("Koniec gry, wygrales" + packet[1]));
							}
						}
						if(packet[4].equals("STATEK_ZATOPIONY")||packet[4].equals("STATEK_TRAFIONY")){
							playerTurn = true;
						}else{
							playerTurn = false;
						}
						Platform.runLater(()->gameClientViewController.redraw1GridPane());
						Platform.runLater(()->gameClientViewController.redraw2GridPane());
						break;
					}

					case "INFORMATION": {
						break;
					}

					case "ERROR": {
						break;
					}

					default:
						break;

					}
					Thread.sleep(100);
				}

				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		threadReadingSocket = new Thread(clientReading);
		threadReadingSocket.start();

	}

	public void handlingCommand(Command command, Player own, int x, int y) throws IOException {
		communicationMessage = new CommunicationMessage(command, own, x, y);
		outStreamClient.writeUTF(communicationMessage.toString());
	}

	public void handlingCommand(Command command, Player own, int x, int y, BoardState state) throws IOException {
		communicationMessage = new CommunicationMessage(command, own, x, y, state);
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

	/**
	 * Sprawdzenie planszy po strzale
	 * 
	 * @author Pawel Czernek
	 */
	private BoardState checkBoard(int x, int y) {
		gameClientViewController.getClientBoard().shot(x, y);
		if (gameClientViewController.getClientBoard().getBoardCell(x, y) == BoardState.STATEK_ZATOPIONY) {
			gameClientViewController.getClientBoard().setSunk(x, y);
		}
		return gameClientViewController.getClientBoard().getBoardCell(x, y);
	}
}

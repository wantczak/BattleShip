package battleship.model.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import battleship.gui.game.GameClientViewController;
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
	
	//BUFFORY IN I OUT
	DataInputStream inStreamClient;
	DataOutputStream outStreamClient;
	
	CommunicationMessage communicationMessage;
	
	public ClientNetworkGameThread(TextArea textLogClient, GameProcedure clientProcedure, GameClientViewController gameClientViewController, Server gameServer) {
		this.textLogClient = textLogClient;
		this.clientProcedure = clientProcedure;
		this.gameClientViewController = gameClientViewController;
		this.gameServer = gameServer;
	}
	
	//=========================METODA GLOWNA WATKU=================================	Q
	public void run(){
		while(!gameOver){
			
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
				if(threadWaitingForOpponentShips ==null) waitingForDeployedShips(); 
				break;
			}
			
			case PLAYING_GAME:{
				
			}

			
			default:
				break;

		}
		}
	}
	
	
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

}

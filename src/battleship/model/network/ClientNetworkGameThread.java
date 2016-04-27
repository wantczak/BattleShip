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
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ClientNetworkGameThread extends Thread {

	@FXML private TextArea textLogClient;
	private GameProcedure clientProcedure;
	private GameClientViewController gameClientViewController;
	private Server gameServer;
	
	private Socket clientSocket;
	private boolean gameOver = false;
	private boolean playerTurn = false;
	
	//Deklaracja thread
	Thread threadConnectionToServer;
	
	//BUFFORY IN I OUT
	DataInputStream inStreamClient;
	DataOutputStream outStreamClient;

	
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
				clientConnectionProcedure();
				textLogClient.appendText("\n ROZPOCZECIE GRY!");
				textLogClient.appendText("\n ROZSTAW STATKI!");
				break;
			}
			
			case DEPLOY_SHIPS:{
				break;
			}
			
			case READY_TO_START:{
				try {
					Thread.sleep(500);
					
					while(true){
						outStreamClient.writeUTF("READY");

					}

					//System.out.println(inStreamClient.readUTF());
					//while(!inStreamClient.readUTF().equals("READY")){
						//outStreamClient.writeUTF("READY");

					//}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}					
				
				break;
			}
			
			default:
				break;

		}
		}
	}
	
	
	private void clientConnectionProcedure() {
		if(clientSocket == null){
			try {
				Runnable clientConnection = ()->{
					try{
						//textLogClient.appendText("[CLIENT]: Proba podlaczenia do serwera:  "+gameServer.getServerIP()+"\n");
						Thread.sleep(10);
						InetAddress serverAddress = InetAddress.getByName(gameServer.getServerIP()); 
						clientSocket = new Socket(serverAddress.getHostName(), 12345);
						//textLogClient.appendText("[CLIENT]: Status polaczenia:  "+clientSocket.isConnected()+"\n");
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

	private void playingGame(){
		
	}

}

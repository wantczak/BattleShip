package battleship.model.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import battleship.gui.game.GameClientViewController;
import battleship.model.client.ClientProcedure;
import battleship.model.client.ClientProcedure.Procedure;
import battleship.model.server.Server;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ClientNetworkGameThread extends Thread {

	@FXML private TextArea textLogClient;
	private ClientProcedure clientProcedure;
	private GameClientViewController gameClientViewController;
	private Server gameServer;
	
	private Socket clientSocket;
	private boolean gameOver = false;
	
	
	public ClientNetworkGameThread(TextArea textLogClient, ClientProcedure clientProcedure, GameClientViewController gameClientViewController, Server gameServer) {
		this.textLogClient = textLogClient;
		this.clientProcedure = clientProcedure;
		this.gameClientViewController = gameClientViewController;
		this.gameServer = gameServer;
	}
	
	//=========================METODA GLOWNA WATKU=================================	Q
	public void run(){
		while(!gameOver){
			if(clientSocket == null){
				try {
					textLogClient.appendText("[CLIENT]: Proba podlaczenia do serwera:  "+gameServer.getServerIP()+"\n");
					InetAddress serverAddress = InetAddress.getByName(gameServer.getServerIP()); 
					clientSocket = new Socket(serverAddress.getHostName(), 12345);
					textLogClient.appendText("[CLIENT]: Status polaczenia:  "+clientSocket.isConnected()+"\n");
					if (clientSocket.isConnected()) clientProcedure.setClientProcedure(Procedure.DEPLOY_SHIPS);
					textLogClient.appendText("\n ROZPOCZECIE GRY!");
					textLogClient.appendText("\n ROZSTAW STATKI!");


				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			 
		}
	}

}

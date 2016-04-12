package battleship.model.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import battleship.gui.game.GameClientViewController;
import battleship.model.client.ClientProcedure;
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
					System.out.println(textLogClient);
					textLogClient.appendText("Server IP: "+gameServer.getServerIP());
					
					InetAddress serverAddress = InetAddress.getByName(gameServer.getServerIP()); 
					clientSocket = new Socket(serverAddress, 12345);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			   
		}
	}

}

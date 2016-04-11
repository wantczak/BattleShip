package battleship.model.network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import battleship.gui.game.GameClientViewController;
import battleship.model.client.ClientProcedure;
import battleship.model.server.Server;
import javafx.scene.control.TextArea;

public class ClientNetworkGameThread extends Thread {

	private TextArea textLogServer;
	private ClientProcedure clientProcedure;
	private GameClientViewController gameClientViewController;
	private Server gameServer;
	
	private Socket clientSocket;
	private boolean gameOver = false;
	
	
	public ClientNetworkGameThread(TextArea textLogServer, ClientProcedure clientProcedure, GameClientViewController gameClientViewController, Server gameServer) {
		this.textLogServer = textLogServer;
		this.clientProcedure = clientProcedure;
		this.gameClientViewController = gameClientViewController;
	}
	
	//=========================METODA GLOWNA WATKU=================================	Q
	public void run(){
		while(!gameOver){
			if(clientSocket == null){
				try {
					textLogServer.appendText("Server IP: "+gameServer.getServerIP());
					clientSocket = new Socket(gameServer.getServerIP(), 8080);
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

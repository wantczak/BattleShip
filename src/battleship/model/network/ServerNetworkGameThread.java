package battleship.model.network;

import java.net.ServerSocket;
import java.net.Socket;

import battleship.gui.game.GameServerViewController;
import battleship.model.server.ServerProcedure;
import battleship.model.server.ServerProcedure.Procedure;
import javafx.scene.control.TextArea;

public class ServerNetworkGameThread extends Thread {
	private TextArea textLogServer;
	private ServerProcedure serverProcedure;
	private GameServerViewController gameServerViewController;
	
	private boolean connectedToClient;
	private boolean gameOver = false;

	private ServerSocket serverSocket; //Deklaracja pojedynczego serverSocketa
    private Socket serverConnection; //Socket polaczenia
	private int connectionPort = 12345;
	
	public ServerNetworkGameThread(TextArea textLogServer, ServerProcedure serverProcedure, GameServerViewController gameServerViewController) {
		this.textLogServer = textLogServer;
		this.serverProcedure = serverProcedure;
		this.gameServerViewController = gameServerViewController;
	}

	public void run(){
		textLogServer.appendText("[SERVER] DRUGI WATEK \n");

		while(!gameOver){
			try{
				switch (serverProcedure.getServerProcedure()){
				case CONNECT_TO_CLIENT:{
					textLogServer.appendText("[SERVER] Connect to client process \n");
					connectToClient();
					textLogServer.appendText("[SERVER] Connected to client: "+connectedToClient);
				}
				
				case DEPLOY_SHIPS:{
					//textLogServer.appendText("\n ROZPOCZECIE GRY!");
					//textLogServer.appendText("\n ROZSTAW STATKI!");

					//Thread.sleep(1000);
				}
				default:
					break;
				}
				
			}
			
			catch(Exception ex){
				
			}
		}
	}
	
	private void connectToClient(){
		connectedToClient = false;
		try{
			serverSocket = new ServerSocket(connectionPort);
			serverSocket.setReuseAddress(true);
			textLogServer.appendText("[SERVER] Oczekiwanie na polaczenie z klientem... \n");
			serverConnection = serverSocket.accept();
			textLogServer.appendText("[SERVER] Nazwiazano polaczenie z klientem: [IP]: "+serverConnection.getInetAddress()+" [PORT]: "+serverConnection.getPort()+"\n");

			if (serverConnection.isConnected()){
				connectedToClient = true;
				serverProcedure.setServerProcedure(Procedure.DEPLOY_SHIPS);
			}
			
		}
		
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void handlingGame(){
		
	}
}

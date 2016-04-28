package battleship.model.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import battleship.gui.game.GameServerViewController;
import battleship.model.procedure.GameProcedure;
import battleship.model.procedure.GameProcedure.Procedure;
import javafx.scene.control.TextArea;

public class ServerNetworkGameThread extends Thread {
	private volatile TextArea textLogServer;
	private GameProcedure serverProcedure;
	private GameServerViewController gameServerViewController;
	
	private boolean connectedToClient;
	private boolean gameOver = false;
	private boolean playerTurn = true;
	private boolean opponentShipsReady = false;

	private ServerSocket serverSocket; //Deklaracja pojedynczego serverSocketa
    private Socket serverConnection; //Socket polaczenia
	private int connectionPort = 12345;
	
	//WATKI W KLASIE
	private Thread threadWaitingForOpponentShips;
	
	//BUFFORY IN I OUT
	DataInputStream inStreamServer;
	DataOutputStream outStreamServer ;
	
	public ServerNetworkGameThread(TextArea textLogServer, GameProcedure serverProcedure, GameServerViewController gameServerViewController) {
		this.textLogServer = textLogServer;
		this.serverProcedure = serverProcedure;
		this.gameServerViewController = gameServerViewController;
	}

	public void run(){
		textLogServer.appendText("[SERVER] DRUGI WATEK \n");

		while(!gameOver){
			try{
				switch (serverProcedure.getProcedure()){
				case CONNECT_TO_CLIENT:{
					textLogServer.appendText("[SERVER] Connect to client process \n");
					connectToClient();
					textLogServer.appendText("[SERVER] Connected to client: "+connectedToClient);
					textLogServer.appendText("\n ROZPOCZECIE GRY!");
					textLogServer.appendText("\n ROZSTAW STATKI!");
					break;
				}
				
				case DEPLOY_SHIPS:{
					break;
				}
				
				case READY_TO_START:{
					if(threadWaitingForOpponentShips ==null)waitingForDeployedShips(); 
					break;
				}
				
				case PLAYING_GAME:{
					playingGame();
					break;
				}

				default:
					break;
				}
			}
			
			catch(Exception ex){
				
			}
		}
	}
	
	
	/**Metoda obslugujaca polaczenie servera do klienta
	 * @author Wojciech Antczak
	 */
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
				textLogServer.appendText("\n [SERVER]: Oczekiwanie na zakonczenie rozstawiania statkow przez Przeciwnika ");
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
				textLogServer.appendText("\n [SERVER]: ODEBRANO INFO OD PRZECIWNIKA O ZAKONCZENIU USTAWIANIU STATKOW");
				
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
	
	
	private void playingGame() throws InterruptedException{
		
		
	}

	public boolean isPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(boolean playerTurn) {
		this.playerTurn = playerTurn;
	}
}

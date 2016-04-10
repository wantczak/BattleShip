package battleship.gui.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

import battleship.gui.menu.MenuViewController;
import battleship.model.board.Board;
import battleship.model.board.BoardState;
import battleship.model.network.NetworkConnection;
import battleship.model.network.ServerNetworkConnectionThread;
import battleship.model.network.ServerNetworkGameThread;
import battleship.model.server.ServerProcedure;
import battleship.model.server.ServerProcedure.Procedure;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.GridPane;

public class GameServerViewController {

	@FXML private Parent root;
	@FXML private GridPane Player1GridPane;
	@FXML private Button btnStartGame;
	
	//TextFieldy ukazujace dane
	@FXML private TextField textFieldServerGame; 
	@FXML private TextField textFieldServerIP;
	@FXML private TextField textFieldServerPort;
	@FXML private TextArea textLogServer;	
	
	private ServerProcedure serverProcedure;
	private NetworkConnection networkConnection;
	
	//Watki
	private ServerNetworkConnectionThread serverNetworkConnectionThread;
	private ServerNetworkGameThread serverNetworkGameThread;
    private ServerSocket serverTCPSocket;
 
    //Zmienne okreslajace clienta
    private String clientIP;
    public String getClientIP(){
    	return clientIP;
    }
    
    public void setClientIP(String clientIP){
    	this.clientIP = clientIP;
    }
	Board player1board = new Board();
	
	//Deklaracja thread�w
	Thread startGameThread;
	
	public Parent getView() {
		return root;
	}

	private MenuViewController menuViewController;

	public void setMenuViewController(MenuViewController menuViewController) {
		this.menuViewController = menuViewController;
	}
	
	private GameServerViewController getGameServerViewController(){
		return this;
	}
	
	//METODA UZUPELNIAJACA AREALOGS
	public void setTextAreaLogi(String message){
		this.textLogServer.appendText("\n"+message);
	}
	
	//METODA USTAWIAJACA PROCEDURE Z ZEWNATRZ KLASY
	public void setProcedure(Procedure procedure){
		serverProcedure.setServerProcedure(procedure);
	}
	
	//ZMIENNA OKRESLAJACA POLACZENIE CLIENTA
	
	//METODA ODPALANA PRZY TWORZENIU NOWEGO SERWERA
	@FXML
	private void initialize(){
		serverProcedure = new ServerProcedure(Procedure.START_GAME);
		btnStartGame.setOnAction(e->{
			if (serverProcedure.getServerProcedure()==Procedure.START_GAME)	startGameProcedure(); //Odpalenie metody startGame, i utworzenie nowego Thread
			else if(serverProcedure.getServerProcedure()==Procedure.DEPLOY_SHIPS) textLogServer.appendText("\n Nie skonczono procedury ukladania statkow. Dokoncz procedury i kliknij w przycisk START");
			
		});		
	}

//==========================================================================
	/**
	 * Metoda odpalana w momencie nacisniecia przycisku pola gry
	 * @author Wojciech Antczak
	 * @param e - pobranie Eventu od przycisniecia myszy
	 */
	@FXML
	private void Player1ClickedAction(MouseEvent e) {
		Node src = (Node) e.getSource();
		if (serverProcedure.getServerProcedure() == Procedure.DEPLOY_SHIPS){
			//TO BEDZIE DZIALAC PRZY TESTACH SIECIOWYCH.
		}
		
		player1board.setViewControllerReference(this);
		player1board.locateShips((int)GridPane.getColumnIndex(src),(int) GridPane.getRowIndex(src));
		checkFields(player1board);
	}

	private void checkFields(Board board) {
		Button btn;
		BoardState[][] boardSt = board.getBoardState();
		for (int i = 0; i < boardSt.length; i++) {
			for (int j = 0; j < boardSt[i].length; j++){
				btn = (Button)getNodeFromGridPane(Player1GridPane, i, j);
				if (boardSt[i][j] == BoardState.STATEK) {
					btn.setStyle("-fx-background-color: slateblue;");
				}
				if (boardSt[i][j] == BoardState.PUSTE_POLE) {
					btn.setStyle("default");
				}
			}
		}
	}
	
	private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
	    for (Node node : gridPane.getChildren()) {
	        if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
	            return node;
	        }
	    }
	    return null;
	}
	
	/**
	 * Metoda rozpoczynajaca gre. Procedura jest nastepujaca:
	 * <ul>
	 * <li> Utworzenie nowego Thread, ktory bedzie odpowiedzialny za operacje Network;
	 * <li> 
	 * <li> 
	 * </ul>
	 * <p>
	 * @author Wojciech Antczak
	 */
	private void startGameProcedure(){
		startGameThread = new Thread(new Runnable() {
		     public void run() {
		    	 
		    	 //PROCEDURA START_GAME
				if (serverProcedure.getServerProcedure() == Procedure.START_GAME){
					if (networkConnection == null) networkConnection = new NetworkConnection();
					textFieldServerIP.setText(networkConnection.getLocalIP());
					textFieldServerPort.setText(String.valueOf(networkConnection.getConnectionPort()));
					serverProcedure.setServerProcedure(Procedure.OPEN_CONNECTION);
				}
				
				//PROCEDURA OPEN_CONNECTION
				if (serverProcedure.getServerProcedure() == Procedure.OPEN_CONNECTION){
					try {
						serverProcedure.setServerProcedure(Procedure.READY_TO_START);
						serverNetworkConnectionThread = new ServerNetworkConnectionThread(textLogServer,serverProcedure,getGameServerViewController());
						serverNetworkConnectionThread.start(); //odpalenie watka
						serverNetworkConnectionThread.join(); //oczekiwanie na zakonczenie threada
						
						//Odpalenie nowego threada do gry
						serverNetworkGameThread = new ServerNetworkGameThread(textLogServer,serverProcedure,getGameServerViewController());
						serverNetworkGameThread.start(); //odpalenie watka
						serverNetworkGameThread.join(); //oczekiwanie na zakonczenie threada
						//textLogServer.appendText("\n BBBB!");
						//serverProcedure.setServerProcedure(Procedure.DEPLOY_SHIPS);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				//PROCEDURA DEPLOY_SHIPS
				if (serverProcedure.getServerProcedure() == Procedure.DEPLOY_SHIPS){
					textLogServer.appendText("\n ROZPOCZECIE GRY!");
					textLogServer.appendText("\n ROZSTAW STATKI!");
				}
		     }
		});  
		startGameThread.start();
	}
}
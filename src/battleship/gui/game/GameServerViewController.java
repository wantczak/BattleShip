package battleship.gui.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Optional;

import battleship.gui.menu.MenuViewController;
import battleship.model.board.Board;
import battleship.model.board.BoardState;
import battleship.model.board.ShipFactory;
import battleship.model.network.Command;
import battleship.model.network.NetworkConnection;
import battleship.model.network.ServerNetworkConnectionThread;
import battleship.model.network.ServerNetworkGameThread;
import battleship.model.procedure.GameProcedure;
import battleship.model.procedure.GameProcedure.Procedure;
import battleship.model.user.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.GridPane;

public class GameServerViewController implements GameViewController{

	@FXML private Parent root;
	@FXML private GridPane serverGridPane;
	@FXML private GridPane clientGridPane;
	@FXML private Button btnStartGame;
	
	//TextFieldy ukazujace dane
	@FXML private TextField textFieldServerGame; 
	@FXML private TextField textFieldServerIP;
	@FXML private TextField textFieldServerPort;
	@FXML private TextArea textLogServer;	
	
	private GameProcedure serverProcedure;
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
	private Board serverBoard = new Board();	
	private Board clientBoard = new Board();
	ShipFactory shipFactory = new ShipFactory(getServerBoard(), this);

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
	@Override
	public void setTextAreaLogi(String message){
		Platform.runLater(()->this.textLogServer.appendText(message+"\n"));
	}
	
	//METODA USTAWIAJACA PROCEDURE Z ZEWNATRZ KLASY
	@Override
	public void setProcedure(Procedure procedure){
		serverProcedure.setProcedure(procedure);
	}
	
	
//=====================METODA INITIALIZE ODPALANA PRZY TWORZENIU NOWEGO SERWERA===========
//========================================================================================
	@FXML
	private void initialize(){
		serverProcedure = new GameProcedure(Procedure.START_GAME);
		btnStartGame.setOnAction(e->{
			if (serverProcedure.getProcedure()==Procedure.START_GAME){nameDialog();	startGameProcedure(); btnStartGame.setVisible(false);} //Odpalenie metody startGame, i utworzenie nowego Thread
			else if(serverProcedure.getProcedure()==Procedure.DEPLOY_SHIPS) setTextAreaLogi("Nie skonczono procedury ukladania statkow. Dokoncz procedure");
			
		});		
	}

	
	/**
	 * Metoda rozpoczynajaca gre.
	 * <p>
	 * @author Wojciech Antczak
	 */
	private void startGameProcedure(){
		startGameThread = new Thread(new Runnable() {
		     public void run() {
		    	 
		    	 //PROCEDURA START_GAME
				if (serverProcedure.getProcedure() == Procedure.START_GAME){
					if (networkConnection == null) networkConnection = new NetworkConnection();
					textFieldServerIP.setText(networkConnection.getLocalIP());
					textFieldServerPort.setText(String.valueOf(networkConnection.getConnectionPort()));
					serverProcedure.setProcedure(Procedure.OPEN_CONNECTION);
				}
				
				//PROCEDURA OPEN_CONNECTION
				if (serverProcedure.getProcedure() == Procedure.OPEN_CONNECTION){
					try {
						serverProcedure.setProcedure(Procedure.READY_TO_START);
						setServerNetworkConnectionThread(new ServerNetworkConnectionThread(textLogServer,serverProcedure,getGameServerViewController(),textFieldServerGame));
						getServerNetworkConnectionThread().start(); //odpalenie watka
						getServerNetworkConnectionThread().join(); //oczekiwanie na zakonczenie threada
						setTextAreaLogi("[SERVER] PO PROCEDURZE CONNECTION... ");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				//PROCEDURA CONNECT_TO_CLIENT
				if (serverProcedure.getProcedure() == Procedure.CONNECT_TO_CLIENT){
					try {
						//Odpalenie nowego threada do gry
						setServerNetworkGameThread(new ServerNetworkGameThread(textLogServer,serverProcedure,getGameServerViewController()));
						getServerNetworkGameThread().start(); //odpalenie watka
						//serverNetworkGameThread.join(); //oczekiwanie na zakonczenie threada

					}
					
					catch (Exception ex){
						
					}
				}

				//PROCEDURA DEPLOY_SHIPS
				if (serverProcedure.getProcedure() == Procedure.DEPLOY_SHIPS){
				}
		     }
		});  
		startGameThread.start();
	}


	//====================TWORZENIE DIALOGU DO WYBORU NICKA GRACZA================
	private void nameDialog(){
		TextInputDialog dialog = new TextInputDialog("Gracz1");
		dialog.setTitle("Imie gracza");
		dialog.setHeaderText("Imie gracza");
		dialog.setContentText("Podaj swoje imie:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			textFieldServerGame.setText(result.get());
		}		
	}
	
//==============================METODY OBSLUGI PRZYCISKOW I POLA GRY=====================
//=======================================================================================
	/**
	 * Metoda odpalana w momencie nacisniecia przycisku pola gry
	 * @author Wojciech Antczak , Paweł Czernek
	 * @param e - pobranie Eventu od przycisniecia myszy
	 */
	@FXML
	private void ServerBoardClickedAction(MouseEvent e) {
		try{
			Node src = (Node) e.getSource();
			if (serverProcedure.getProcedure() == Procedure.DEPLOY_SHIPS){
				//TO BEDZIE DZIALAC PRZY TESTACH SIECIOWYCH.
				serverBoard.setViewControllerReference(this);
				shipFactory.locateShip((int)GridPane.getColumnIndex(src),(int) GridPane.getRowIndex(src));
				redraw1GridPane();
			}
		}
		
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@FXML
	private void ClientBoardClickedAction(MouseEvent e){
		try{
			Node src = (Node) e.getSource();
			clientBoard.setViewControllerReference(this);
			int x = (int) GridPane.getColumnIndex(src);
			int y = (int) GridPane.getRowIndex(src);
			
			if (serverProcedure.getProcedure() == Procedure.PLAYING_GAME&&getServerNetworkGameThread().isPlayerTurn()){
				this.setTextAreaLogi("NACISNIETO: ["+x+"] ["+y+"]");
				if(clientBoard.getBoardCell(x, y) != BoardState.PUSTE_POLE){
					this.setTextAreaLogi("To pole bylo juz ostrzelane, strzelaj jeszcze raz!");
				} else {
					serverNetworkGameThread.handlingCommand(Command.SHOT, Player.SERVER_PLAYER, x, y);
				}
				
			} else if (serverProcedure.getProcedure() == Procedure.PLAYING_GAME) {
				this.setTextAreaLogi("Kolej na strzal przeciwnika!");
			}
		}
		
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	//metoda przerysowujaca pierwsza plansze
	public void redraw1GridPane() {
		try{
			Platform.runLater(()->{
			Button btn;
			BoardState[][] boardSt = serverBoard.getBoardState();
			for (int i = 0; i < boardSt.length; i++) {
				for (int j = 0; j < boardSt[i].length; j++){
					btn = (Button)getNodeFromGridPane(serverGridPane, i, j);
					if (boardSt[i][j] == BoardState.STATEK) 
						btn.setStyle("-fx-background-color: slateblue;");
					if (boardSt[i][j] == BoardState.PUSTE_POLE)
						btn.setStyle("default");
					if (boardSt[i][j] == BoardState.PUDLO) 
						btn.setStyle("-fx-background-color: grey;");
					if (boardSt[i][j] == BoardState.STATEK_TRAFIONY) 
						btn.setStyle("-fx-background-color: red;");
					if (boardSt[i][j] == BoardState.STATEK_ZATOPIONY) 
						btn.setStyle("-fx-background-color: black;");
					
				}
			}
			});
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	//metoda przerysowujaca druga plansze
	public void redraw2GridPane() {
		try{
			Platform.runLater(()->{
			Button btn;
			BoardState[][] boardSt = clientBoard.getBoardState();
			for (int i = 0; i < boardSt.length; i++) {
				for (int j = 0; j < boardSt[i].length; j++){
					btn = (Button)getNodeFromGridPane(clientGridPane, i, j);
					if (boardSt[i][j] == BoardState.STATEK) 
						btn.setStyle("-fx-background-color: slateblue;");
					if (boardSt[i][j] == BoardState.PUSTE_POLE)
						btn.setStyle("default");
					if (boardSt[i][j] == BoardState.PUDLO) 
						btn.setStyle("-fx-background-color: grey;");
					if (boardSt[i][j] == BoardState.STATEK_TRAFIONY) 
						btn.setStyle("-fx-background-color: red;");
					if (boardSt[i][j] == BoardState.STATEK_ZATOPIONY) 
						btn.setStyle("-fx-background-color: black;");
				}
			}
			});
		}
		
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
		try{
		    for (Node node : gridPane.getChildren()) {
		        if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
		            return node;
		        }
		    }
		}
		
		catch (Exception ex){
			ex.printStackTrace();
		    return null;
		}
	    return null;
	}

	public ServerNetworkGameThread getServerNetworkGameThread() {
		return serverNetworkGameThread;
	}

	public void setServerNetworkGameThread(ServerNetworkGameThread serverNetworkGameThread) {
		this.serverNetworkGameThread = serverNetworkGameThread;
	}
	public Board getServerBoard() {
		return serverBoard;
	}

	public void setServerBoard(Board serverBoard) {
		this.serverBoard = serverBoard;
	}

	public Board getClientBoard() {
		return clientBoard;
	}

	public void setClientBoard(Board clientBoard) {
		this.clientBoard = clientBoard;
	}

	public ShipFactory getShipFactory() {
		return shipFactory;
	}

	public void setShipFactory(ShipFactory shipFactory) {
		this.shipFactory = shipFactory;
	}

	public ServerNetworkConnectionThread getServerNetworkConnectionThread() {
		return serverNetworkConnectionThread;
	}

	public void setServerNetworkConnectionThread(ServerNetworkConnectionThread serverNetworkConnectionThread) {
		this.serverNetworkConnectionThread = serverNetworkConnectionThread;
	}
	

}
package battleship.gui.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Optional;

import battleship.gui.menu.MenuViewController;
import battleship.model.board.Board;
import battleship.model.board.BoardState;
import battleship.model.board.ShipFactory;
import battleship.model.network.NetworkConnection;
import battleship.model.network.ServerNetworkConnectionThread;
import battleship.model.network.ServerNetworkGameThread;
import battleship.model.procedure.GameProcedure;
import battleship.model.procedure.GameProcedure.Procedure;
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
	Board serverBoard = new Board();	
	Board clientBoard = new Board();
	ShipFactory shipFactory = new ShipFactory(serverBoard, this);

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
		this.textLogServer.appendText("\n"+message);
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
			if (serverProcedure.getProcedure()==Procedure.START_GAME){nameDialog();	startGameProcedure();} //Odpalenie metody startGame, i utworzenie nowego Thread
			else if(serverProcedure.getProcedure()==Procedure.DEPLOY_SHIPS) textLogServer.appendText("\n Nie skonczono procedury ukladania statkow. Dokoncz procedury i kliknij w przycisk START");
			
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
						serverNetworkConnectionThread = new ServerNetworkConnectionThread(textLogServer,serverProcedure,getGameServerViewController());
						serverNetworkConnectionThread.start(); //odpalenie watka
						serverNetworkConnectionThread.join(); //oczekiwanie na zakonczenie threada
						textLogServer.appendText("[SERVER] PO PROCEDURZE CONNECTION... \n");

						//Odpalenie nowego threada do gry
						serverProcedure.setProcedure(Procedure.CONNECT_TO_CLIENT);
						serverNetworkGameThread = new ServerNetworkGameThread(textLogServer,serverProcedure,getGameServerViewController());
						serverNetworkGameThread.start(); //odpalenie watka
						//serverNetworkGameThread.join(); //oczekiwanie na zakonczenie threada
						
					} catch (Exception e) {
						e.printStackTrace();
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
	 * @author Wojciech Antczak
	 * @param e - pobranie Eventu od przycisniecia myszy
	 */
	@FXML
	private void ServerBoardClickedAction(MouseEvent e) {
		Node src = (Node) e.getSource();
		if (serverProcedure.getProcedure() == Procedure.DEPLOY_SHIPS){
			//TO BEDZIE DZIALAC PRZY TESTACH SIECIOWYCH.
			serverBoard.setViewControllerReference(this);
			shipFactory.locateShip((int)GridPane.getColumnIndex(src),(int) GridPane.getRowIndex(src));
			redraw1GridPane(serverBoard);
		}
	}
	
	@FXML
	private void ClientBoardClickedAction(MouseEvent e){
		Node src = (Node) e.getSource();
		clientBoard.setViewControllerReference(this);
		int x = (int) GridPane.getColumnIndex(src);
		int y = (int) GridPane.getRowIndex(src);
		
		if (serverProcedure.getProcedure() == Procedure.PLAYING_GAME&&serverNetworkGameThread.isPlayerTurn()){
			textLogServer.appendText("\n NACISNIETO: ["+x+"] ["+y+"]");
			
			//clientNetworkGameThread.handlingCommand(Command.SHOT, Player.CLIENT_PLAYER, x, y);

		}
		/*
		if(clientBoard.getBoardCell(x, y) == BoardState.PUSTE_POLE){
			clientBoard.setBoardCell(x,y,serverBoard.shot(x, y));
			if(clientBoard.getBoardCell(x, y) == BoardState.STATEK_ZATOPIONY)
				clientBoard.setSunk(x, y);
		} else {
			setTextAreaLogi("Pole bylo juz ostrzelane, strzelaj jeszcze raz!");
		}
		redraw1GridPane(serverBoard);
		redraw2GridPane(clientBoard);
		*/
	}
	//metoda przerysowujaca pierwsza plansze
	private void redraw1GridPane(Board board) {
		Button btn;
		BoardState[][] boardSt = board.getBoardState();
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
	}
	
	//metoda przerysowujaca druga plansze
	private void redraw2GridPane(Board board) {
		Button btn;
		BoardState[][] boardSt = board.getBoardState();
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
	}
	
	private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
	    for (Node node : gridPane.getChildren()) {
	        if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
	            return node;
	        }
	    }
	    return null;
	}

	

}
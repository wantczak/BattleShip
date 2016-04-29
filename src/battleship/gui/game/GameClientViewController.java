package battleship.gui.game;

import java.io.IOException;
import java.util.Optional;

import battleship.gui.menu.MenuViewController;
import battleship.model.board.Board;
import battleship.model.board.BoardState;
import battleship.model.board.ShipFactory;
import battleship.model.network.ClientNetworkGameThread;
import battleship.model.network.Command;
import battleship.model.network.CommunicationMessage;
import battleship.model.network.NetworkConnection;
import battleship.model.procedure.GameProcedure;
import battleship.model.procedure.GameProcedure.Procedure;
import battleship.model.server.Server;
import battleship.model.user.Player;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 * 
 * @author Wojciech Antczak
 *
 */
public class GameClientViewController implements GameViewController{

	@FXML private Parent root;
	//TextFieldy ukazujace dane
	@FXML private TextField textFieldClientGame; 
	@FXML private TextField textFieldClientIP;
	@FXML private TextField textFieldClientPort;
	@FXML private TextArea textLogClient;
	
	//Button
	@FXML private Button btnStartGame;
	//Grid Pane do gry
	@FXML private GridPane clientGridPane;
	@FXML private GridPane serverGridPane;

	
	//Zmienne sieciowe
	private Server gameServer = null;
	private GameProcedure clientProcedure;
	private NetworkConnection networkConnection;
	private CommunicationMessage clientCommunicationMessage;
	
	
	//WATKI
	private ClientNetworkGameThread clientNetworkGameThread;

	//ZMIENNE POLA GRY
	private Board serverBoard = new Board();	
	private Board clientBoard = new Board();
	private ShipFactory shipFactory = new ShipFactory(getClientBoard(), this);

	
	public Parent getView() {
		return root;
	}

	private MenuViewController menuViewController;
	private GameChooserViewController gameChooserViewController;
	
	public void setMenuViewController(MenuViewController menuViewController) {
		this.menuViewController = menuViewController;
	}

	public void setGameChooserViewController(GameChooserViewController gameChooserViewController) {
		this.gameChooserViewController = gameChooserViewController;
	}	
	
	public Server getGameServer(){
		return gameServer;
	}
	
	public void setGameServer(Server gameServer){
		this.gameServer = gameServer;
	}

	//METODA UZUPELNIAJACA AREALOGS
		@Override
		public void setTextAreaLogi(String message){
			this.textLogClient.appendText("\n"+message);
		}
		
		//METODA USTAWIAJACA PROCEDURE Z ZEWNATRZ KLASY
		@Override
		public void setProcedure(Procedure procedure){
			clientProcedure.setProcedure(procedure);
		}	
//=====================METODA INITIALIZE ODPALANA PRZY TWORZENIU NOWEGO KLIENTA===========
//========================================================================================
	@FXML
	public void initialize(){
		clientProcedure = new GameProcedure(Procedure.START_GAME);

		btnStartGame.setOnAction(e->{
			if (clientProcedure.getProcedure()==Procedure.START_GAME){
				nameDialog();
				if (networkConnection == null) networkConnection = new NetworkConnection();
				textFieldClientIP.setText(networkConnection.getLocalIP());
				textFieldClientPort.setText(String.valueOf(networkConnection.getConnectionPort()));
				setClientNetworkGameThread(new ClientNetworkGameThread(textLogClient, clientProcedure, this,gameServer));
				clientProcedure.setProcedure(Procedure.CONNECT_TO_SERVER);
				getClientNetworkGameThread().start();
			}
			else if(clientProcedure.getProcedure()==Procedure.DEPLOY_SHIPS) textLogClient.appendText("\n Nie skonczono procedury ukladania statkow. Dokoncz procedury i kliknij w przycisk START");
			
		});
	}
	
	private void nameDialog(){
		TextInputDialog dialog = new TextInputDialog("Pawel");
		dialog.setTitle("Imie gracza");
		dialog.setHeaderText("Imie gracza");
		dialog.setContentText("Podaj swoje imie:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			textFieldClientGame.setText(result.get());
		}
		else{
			textFieldClientGame.setText("Gracz2");
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
	public void ClientBoardClickedAction(MouseEvent e){
		Node src = (Node) e.getSource();
		if (clientProcedure.getProcedure() == Procedure.DEPLOY_SHIPS){
			//TO BEDZIE DZIALAC PRZY TESTACH SIECIOWYCH.
			getClientBoard().setViewControllerReference(this);
			getShipFactory().locateShip((int)GridPane.getColumnIndex(src),(int) GridPane.getRowIndex(src));
			redraw1GridPane(getClientBoard());
		}

	}
	
	@FXML
	public void ServerBoardClickedAction(MouseEvent e) throws IOException{
		Node src = (Node) e.getSource();
		getServerBoard().setViewControllerReference(this);
		int x = (int) GridPane.getColumnIndex(src);
		int y = (int) GridPane.getRowIndex(src);
		
		if (clientProcedure.getProcedure() == Procedure.PLAYING_GAME&&getClientNetworkGameThread().isPlayerTurn()){
			//clientNetworkGameThread.handlingCommand(Command.SHOT, Player.CLIENT_PLAYER, x, y);

		}
		
	}
	
	//metoda przerysowujaca pierwsza plansze
	private void redraw1GridPane(Board board) {
		try{
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
		
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	//metoda przerysowujaca druga plansze
	private void redraw2GridPane(Board board) {
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
	
	private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
	    for (Node node : gridPane.getChildren()) {
	        if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
	            return node;
	        }
	    }
	    return null;
	}

	public ClientNetworkGameThread getClientNetworkGameThread() {
		return clientNetworkGameThread;
	}

	public void setClientNetworkGameThread(ClientNetworkGameThread clientNetworkGameThread) {
		this.clientNetworkGameThread = clientNetworkGameThread;
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
}

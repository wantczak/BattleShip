package battleship.gui.game;

import java.io.IOException;
import java.net.SocketException;

import battleship.gui.menu.MenuViewController;
import battleship.model.board.Board;
import battleship.model.board.BoardState;
import battleship.model.board.ShipFactory;
import battleship.model.network.NetworkConnection;
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

public class GameServerViewController implements GameViewController{

	@FXML private Parent root;
	@FXML private GridPane Player1GridPane;
	@FXML private GridPane Player2GridPane;
	@FXML private Button btnStartGame;
	
	//TextFieldy ukazujace dane
	@FXML private TextField textFieldServerGame; 
	@FXML private TextField textFieldServerIP;
	@FXML private TextField textFieldServerPort;
	@FXML private TextArea textLogServer;	
	
	
	private ServerProcedure serverProcedure;
	private NetworkConnection networkConnection;

	Board player1board = new Board();
	Board player2board = new Board();
	ShipFactory shipFactory = new ShipFactory(player1board, this);

	//Deklaracja thread�w
	Thread startGameThread;
	
	public Parent getView() {
		return root;
	}

	private MenuViewController menuViewController;

	public void setMenuViewController(MenuViewController menuViewController) {
		this.menuViewController = menuViewController;
	}
	
	//METODA UZUPELNIAJACA AREALOGS
	@Override
	public void setTextAreaLogi(String message){
		this.textLogServer.appendText("\n"+message);
	}
	
	//METODA USTAWIAJACA PROCEDURE Z ZEWNATRZ KLASY
	@Override
	public void setProcedure(Procedure procedure){
		serverProcedure.setServerProcedure(procedure);
	}

	
	//METODA ODPALANA PRZY TWORZENIU NOWEGO SERWERA
	@FXML
	private void initialize(){
		serverProcedure = new ServerProcedure(Procedure.START_GAME);
		btnStartGame.setOnAction(e->{
			if (serverProcedure.getServerProcedure()==Procedure.START_GAME)	startGameProcedure(); //Odpalenie metody startGame, i utworzenie nowego Thread
			else if(serverProcedure.getServerProcedure()==Procedure.DEPLOY_SHIPS) textLogServer.appendText("\n Nie skonczono procedury ukladania statkow. Dokoncz procedury i kliknij w przycisk START");
			
			//DOCELOWO TUTAJ BEDZIE ODPALANIE NOWEGO WATKU OBSLUGUJACEGO PROCEDURY
			//ODPALANIA NOWEJ GRY:
			//1. Pobranie IP z komputera ( to mo�na w sumie zrealizowac zawsze przy odpalaniu okna)
			//2. Proba stworzenia Hosta
			//3. Okno informujace ze trzeba rozmiescic statki na planszy
			//4. Oczekiwanie na przeciwnika
			//5. Podlaczenie sie do clienta - test pingowania
			//6. Start gry 
		});		
	}
	
	
	
	
	

	@FXML
	private void Player1ClickedAction(MouseEvent e) {
		Node src = (Node) e.getSource();
		if (serverProcedure.getServerProcedure() == Procedure.DEPLOY_SHIPS){
			//TO BEDZIE DZIALAC PRZY TESTACH SIECIOWYCH.
		}
//		 System.out.println("Row: "+ GridPane.getRowIndex(src));
//		 System.out.println("Column: "+ GridPane.getColumnIndex(src));
		player1board.setViewControllerReference(this);
		shipFactory.locateShip((int)GridPane.getColumnIndex(src),(int) GridPane.getRowIndex(src));
		redraw1GridPane(player1board);

	}
	
	@FXML
	private void Player2ClickedAction(MouseEvent e){
		Node src = (Node) e.getSource();
//		 System.out.println("Row: "+ GridPane.getRowIndex(src));
//		 System.out.println("Column: "+ GridPane.getColumnIndex(src));
		player2board.setViewControllerReference(this);
		int x = (int) GridPane.getColumnIndex(src);
		int y = (int) GridPane.getRowIndex(src);
		if(player2board.getBoardCell(x, y) == BoardState.PUSTE_POLE){
			player2board.setBoardCell(x,y,player1board.shot(x, y));
			if(player2board.getBoardCell(x, y) == BoardState.STATEK_ZATOPIONY)
				player2board.setSunk(x, y);
		} else {
			setTextAreaLogi("Pole było już ostrzelane, strzelaj jeszcze raz!");
		}
		redraw1GridPane(player1board);
		redraw2GridPane(player2board);
	}
	//metoda przerysowująca pierwszą planszę
	private void redraw1GridPane(Board board) {
		Button btn;
		BoardState[][] boardSt = board.getBoardState();
		for (int i = 0; i < boardSt.length; i++) {
			for (int j = 0; j < boardSt[i].length; j++){
				btn = (Button)getNodeFromGridPane(Player1GridPane, i, j);
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
	
	//metoda przerysowująca drugą planszę
	private void redraw2GridPane(Board board) {
		Button btn;
		BoardState[][] boardSt = board.getBoardState();
		for (int i = 0; i < boardSt.length; i++) {
			for (int j = 0; j < boardSt[i].length; j++){
				btn = (Button)getNodeFromGridPane(Player2GridPane, i, j);
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
	
	/**
	 * Metoda rozpoczynajaca gre. Procedura jest nastepujaca:
	 * <ul>
	 * <li> Utworzenie nowego Thread, ktory bedzie odpowiedzialny za operacje Network;
	 * <li> Odpalenie Timera, ktory bedzie sprawdzal dolaczenie nowego klienta do serwera;
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
						if(networkConnection.createServerConnection(textLogServer)){
							serverProcedure.setServerProcedure(Procedure.DEPLOY_SHIPS);
						}
					} catch (IOException e) {
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

// System.out.println("Row: "+ GridPane.getRowIndex(src));
// System.out.println("Column: "+ GridPane.getColumnIndex(src));
// Player1GameBoard[1][5] = 1;
// Player1GameBoard[8][6] = 1;
// Player1GameBoard[2][7] = 1;
// Player1GameBoard[4][8] = 1;
//
// checkField(GridPane.getRowIndex(src),GridPane.getColumnIndex(src),src);
// }
//
// private void checkField(int row,int column, Node button){
// if (Player1GameBoard[row][column] == 1){
// Button btn = (Button) button;
// btn.setStyle("-fx-background-color: slateblue;");
//
// }
// }
// }
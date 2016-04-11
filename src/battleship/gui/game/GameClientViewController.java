package battleship.gui.game;

import battleship.gui.menu.MenuViewController;
import battleship.model.client.ClientProcedure;
import battleship.model.network.ClientNetworkGameThread;
import battleship.model.server.Server;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 * 
 * @author Wojciech Antczak
 *
 */
public class GameClientViewController {

	@FXML private Parent root;
	//TextFieldy ukazujace dane
	@FXML private TextField textFieldServerGame; 
	@FXML private TextField textFieldServerIP;
	@FXML private TextField textFieldServerPort;
	@FXML private TextArea textLogServer;
	
	//Button
	@FXML private Button btnStartGame;
	//Grid Pane do gry
	@FXML private GridPane Player1GridPane;
	
	//Zmienne sieciowe
	private Server gameServer;
	private ClientProcedure clientProcedure;

	//WATKI
	private ClientNetworkGameThread clientNetworkGameThread;

	
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
	
	
	@FXML
	public void PlayerClickedAction(MouseEvent e){
		
	}
	
	//METODA ODPALANA PRZY TWORZENIU NOWEGO SERWERA
	@FXML
	public void initialize(){
		btnStartGame.setOnAction(e->{
			textLogServer.appendText(gameServer.getServerIP());
			clientNetworkGameThread = new ClientNetworkGameThread(textLogServer, clientProcedure, this,gameServer);
			clientNetworkGameThread.start();
			try {
				clientNetworkGameThread.join();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}
}

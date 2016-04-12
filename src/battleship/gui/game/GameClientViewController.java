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
	@FXML private TextField textFieldClientGame; 
	@FXML private TextField textFieldClientIP;
	@FXML private TextField textFieldClientPort;
	@FXML private TextArea textLogClient;
	
	//Button
	@FXML private Button btnStartGame;
	//Grid Pane do gry
	@FXML private GridPane Player1GridPane;
	
	//Zmienne sieciowe
	private Server gameServer = null;
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
			System.out.println(gameServer);
			System.out.println(gameServer.getServerIP());
			System.out.println(textLogClient);

			textLogClient.appendText(gameServer.getServerIP());
			clientNetworkGameThread = new ClientNetworkGameThread(textLogClient, clientProcedure, this,gameServer);
			clientNetworkGameThread.start();
			try {
				clientNetworkGameThread.join();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}
}

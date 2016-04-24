package battleship.gui.game;

import java.util.Optional;

import battleship.gui.menu.MenuViewController;
import battleship.model.client.ClientProcedure;
import battleship.model.client.ClientProcedure.Procedure;
import battleship.model.network.ClientNetworkGameThread;
import battleship.model.server.Server;
import battleship.model.server.ServerProcedure;
import javafx.fxml.FXML;
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

	@Override
	public void setTextAreaLogi(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProcedure(battleship.model.server.ServerProcedure.Procedure procedure) {
		// TODO Auto-generated method stub
		
	}
	
	
	@FXML
	public void PlayerClickedAction(MouseEvent e){
		
	}
	
	//METODA ODPALANA PRZY TWORZENIU NOWEGO KLIENTA
	@FXML
	public void initialize(){
		clientProcedure = new ClientProcedure(Procedure.START_GAME);

		btnStartGame.setOnAction(e->{
			if (clientProcedure.getClientProcedure()==Procedure.START_GAME){
				nameDialog();
				clientNetworkGameThread = new ClientNetworkGameThread(textLogClient, clientProcedure, this,gameServer);
				clientNetworkGameThread.start();
			}
			else if(clientProcedure.getClientProcedure()==Procedure.DEPLOY_SHIPS) textLogClient.appendText("\n Nie skonczono procedury ukladania statkow. Dokoncz procedury i kliknij w przycisk START");
			
		});
	}
	
	private void nameDialog(){
		TextInputDialog dialog = new TextInputDialog("Gracz1");
		dialog.setTitle("Imie gracza");
		dialog.setHeaderText("Imie gracza");
		dialog.setContentText("Podaj swoje imie:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			textFieldClientGame.setText(result.get());
		}		
	}

}

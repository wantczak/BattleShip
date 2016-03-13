package battleship.gui.menu;

import battleship.application.MainAppFactory;
import battleship.gui.game.GameChooserViewController;
import battleship.gui.game.GameServerViewController;
import battleship.gui.main.MainViewController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

/**
 * 
 * @author Wojciech Antczak
 *
 */
public class MenuViewController {
	//KONTROLKI FXML Z VIEW	
	@FXML private Parent root;
	@FXML private Button btnClose; //Button zamykajacy aplikacje
	@FXML private Button btnStartGame; //Button otwierajacy okno serwera do startu gry
	@FXML private Button btnJoinGame; //Button otwierajacy okno aktywnych gier do dolaczenia
	@FXML private BorderPane contentPane;
	MainAppFactory factory;
	
	
	
	public Parent getView() {
		return root;
	}
	//==============DOSTEP DO APP FACTORY===========
	public void init(MainAppFactory factory){
		this.factory = factory;
	}

	//===============KONTROLERY======================
	private MainViewController mainViewController;
	private GameChooserViewController gameChooserViewController;
	private GameServerViewController gameServerViewController;
	
	//=======================PRZYPISANIE REFERENCJI DO KONTROLEROW=============
	public void setMainViewController(MainViewController mainViewController) {
		this.mainViewController = mainViewController;
	}

	public void setGameChooserViewController(GameChooserViewController gameChooserViewController) {
		this.gameChooserViewController = gameChooserViewController;		
	}
	
	private void setGameServerViewController(GameServerViewController gameServerViewController) {
		this.gameServerViewController = gameServerViewController;
	}

	//METODA INICJALIZUJACA POLA I PRZYPISUJACA FUNKCJE DO BUTTONOW
	@FXML
	void initialize(){
		//metoda obslugujaca nacisniecia buttona btnClose
		btnClose.setOnAction(event->{
			Platform.exit(); //zamkniecie aplikacji
		});
		
		//metoda obslugujaca nacisniecia buttona btnStartGame
		btnStartGame.setOnAction(event->{
			this.setGameServerViewController(factory.getGameServerViewController());
			contentPane.setCenter(gameServerViewController.getView());
		});
		
		//metoda obslugujaca nacisniecia buttona btnJoinGame
		btnJoinGame.setOnAction(event->{
			
		});	
	}
	

    // Button - menuViewController.setGameServerViewController(getGameServerViewController());

	
}

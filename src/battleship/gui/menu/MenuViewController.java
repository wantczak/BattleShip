package battleship.gui.menu;

import battleship.application.MainAppFactory;
import battleship.gui.game.GameChooserViewController;
import battleship.gui.game.GameClientViewController;
import battleship.gui.game.GameServerViewController;
import battleship.gui.main.MainViewController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 
 * @author Wojciech Antczak
 *
 */
public class MenuViewController {
	//KONTROLKI FXML Z VIEW	
	@FXML private Parent menu;
	@FXML private Button btnClose; //Button zamykajacy aplikacje
	@FXML private Button btnStartGame; //Button otwierajacy okno serwera do startu gry
	@FXML private Button btnJoinGame; //Button otwierajacy okno aktywnych gier do dolaczenia
	@FXML private Button btnMinimize;
	@FXML private Button btnClose2;
	@FXML private BorderPane contentPane;
	private MainAppFactory factory;
	
	public Parent getView() {
		return menu;
	}
	//==============DOSTEP DO APP FACTORY===========
	public void init(MainAppFactory factory){
		this.factory = factory;
	}
	
	public MainAppFactory getFactory(){
		return factory;
	}

	//===============KONTROLERY======================
	private MainViewController mainViewController;
	private GameChooserViewController gameChooserViewController;
	private GameServerViewController gameServerViewController;
	private GameClientViewController gameClientViewController;

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
			if (gameServerViewController!=null){
				if (gameServerViewController.getServerNetworkGameThread()!=null){
					System.out.println("BBBBB");
					gameServerViewController.getServerNetworkGameThread().setGameOver(true);
				}
				else if(gameServerViewController.getServerNetworkConnectionThread()!=null){
					System.out.println("AAAAA");
					gameServerViewController.getServerNetworkConnectionThread().setClientConnectionOpen(true);
					
				}
			}
			
			if ((gameChooserViewController!=null)||(gameChooserViewController.getGameClientViewController()!=null)){
					if (gameChooserViewController.getGameClientViewController().getClientNetworkGameThread()!=null){
						gameChooserViewController.getGameClientViewController().getClientNetworkGameThread().setGameOver(true);
					}	
			}
			
			Platform.exit(); //zamkniecie aplikacji	

		});
		
		//metoda obslugujaca nacisniecia buttona btnStartGame
		btnStartGame.setOnAction(event->{
			this.setGameServerViewController(factory.getGameServerViewController());
			getContentPane().setCenter(gameServerViewController.getView());
		});
		
		//metoda obslugujaca nacisniecia buttona btnJoinGame
		btnJoinGame.setOnAction(event->{
			getContentPane().setCenter(gameChooserViewController.getView());

		});	
	}
	public BorderPane getContentPane() {
		return contentPane;
	}
	
	public void setContentPane(BorderPane contentPane) {
		this.contentPane = contentPane;
	}
	
	@FXML
	private void btnCloseEvent(){
		Platform.exit(); //zamkniecie aplikacji
	}
	
	@FXML
	private void btnMinimizeEvent(){
		  Stage stage = (Stage)btnMinimize.getScene().getWindow();
          stage.setIconified(true);
    }

    // Button - menuViewController.setGameServerViewController(getGameServerViewController());

	
}

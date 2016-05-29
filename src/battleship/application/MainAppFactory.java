package battleship.application;

import java.io.IOException;

import battleship.gui.game.GameChooserViewController;
import battleship.gui.game.GameClientViewController;
import battleship.gui.game.GameServerViewController;
import battleship.gui.main.MainViewController;
import battleship.gui.menu.MenuViewController;
import javafx.fxml.FXMLLoader;

/**
 * Klasa tworzaca instancje gry wywolywana w metodzie klasy {@see MainApp}.
 * 
 * @author Wojciech Antczak
 * 
 */
public class MainAppFactory {
	//KONTROLERY-main,log
	private MenuViewController menuViewController;
	private MainViewController mainViewController;
	private GameChooserViewController gameChooserViewController ;
	private GameClientViewController gameClientViewController ;
	private GameServerViewController gameServerViewController ;

	
//=============POBRANIE KONTROLERA MENU I PRZYPISANIE REFERENCJI===========
	public MenuViewController getMenuViewController() {
        if (menuViewController == null){
            try{
            	FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/MenuView.fxml"));
            	System.out.println(loader.getLocation());
                loader.load();
                menuViewController = (MenuViewController) loader.getController();
                menuViewController.setMainViewController(getMainViewController());
                menuViewController.setGameChooserViewController(getGameChooserViewController());
            }
            catch (IOException e){
                throw new RuntimeException("Nie mozna zaladowac MenuView.fxml", e);
            }
        }
        return menuViewController;
	}

//=============POBRANIE KONTROLERA MAIN I PRZYPISANIE REFERENCJI===========
	public MainViewController getMainViewController() {
        if (mainViewController == null){
            try{
            	FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/MainView.fxml"));
            	System.out.println(loader.getLocation());
                loader.load();
                mainViewController = (MainViewController) loader.getController();
                mainViewController.setMenuViewController(getMenuViewController());
            }
            catch (IOException e){
                throw new RuntimeException("Nie mozna zaladowac MainView.fxml", e);
            }
        }
        return mainViewController;
	}
	
//=============POBRANIE KONTROLERA gameChooserViewController I PRZYPISANIE REFERENCJI===========
	private GameChooserViewController getGameChooserViewController() {
        if (gameChooserViewController == null){
            try{
            	FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/GameChooserView.fxml"));
            	System.out.println(loader.getLocation());
                loader.load();
                gameChooserViewController = (GameChooserViewController) loader.getController();
                gameChooserViewController.setMenuViewController(getMenuViewController());
                gameChooserViewController.setGameClientViewController(getGameClientViewController());

            }
            catch (IOException e){
                throw new RuntimeException("Nie mozna zaladowac SettingsView.fxml", e);
            }
        }
        return gameChooserViewController;
	}
		
//=============POBRANIE KONTROLERA GameClientViewController I PRZYPISANIE REFERENCJI===========
	public GameClientViewController getGameClientViewController() {
        //if (gameClientViewController == null){
            try{
            	FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/GameClientView.fxml"));
            	System.out.println(loader.getLocation());
                loader.load();
                gameClientViewController = (GameClientViewController) loader.getController();
                gameClientViewController.setMenuViewController(getMenuViewController());
                gameClientViewController.setGameChooserViewController(getGameChooserViewController());
            }
            catch (IOException e){
                throw new RuntimeException("Nie mozna zaladowac gameClientView.fxml", e);
            }
        //}
        return gameClientViewController;
	}
	
//=============POBRANIE KONTROLERA GameServerViewController I PRZYPISANIE REFERENCJI===========
	public GameServerViewController getGameServerViewController() {
        //if (gameServerViewController == null){
            try{
            	FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/GameServerView.fxml"));
            	System.out.println(loader.getLocation());
                loader.load();
                gameServerViewController = (GameServerViewController) loader.getController();
                gameServerViewController.setMenuViewController(getMenuViewController());
            }
            catch (IOException e){
                throw new RuntimeException("Nie mozna zaladowac GameServerView.fxml", e);
            }
        //}
        return gameServerViewController;
	}
}

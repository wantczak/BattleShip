package battleship.gui.main;

import battleship.gui.menu.MenuViewController;
import javafx.fxml.FXML;
import javafx.scene.Parent;


/**
 * Klasa kontrolera glownego widoku gry
 * 
 * @author Wojciech Antczak
 *
 */
public class MainViewController {

	@FXML private Parent root;

	public Parent getView() {
		return root;
	}
	//========================DOSTEPNE KONTROLERY==============================
	private MenuViewController menuViewController;
	
	
	//=======================PRZYPISANIE REFERENCJI DO KONTROLEROW=============
	public void setMenuViewController(MenuViewController menuViewController) {
		this.menuViewController = menuViewController;
	}
	
}

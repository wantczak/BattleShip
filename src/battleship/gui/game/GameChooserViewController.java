package battleship.gui.game;

import battleship.gui.menu.MenuViewController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
/**
 * 
 * @author Wojciech Antczak
 *
 */
public class GameChooserViewController {

	@FXML private Parent chooserBorderPane;
	
	private MenuViewController menuViewController;
	public Parent getView() {
		return chooserBorderPane;
	}

	public void setMenuViewController(MenuViewController menuViewController) {
		this.menuViewController = menuViewController;		
	}
	
	// Button - menuViewController.setGameClientViewController(getGameClientViewController());

	
}

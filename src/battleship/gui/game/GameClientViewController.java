package battleship.gui.game;

import battleship.gui.menu.MenuViewController;
import javafx.fxml.FXML;
import javafx.scene.Parent;

/**
 * 
 * @author Wojciech Antczak
 *
 */
public class GameClientViewController {

	@FXML private Parent root;

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
}

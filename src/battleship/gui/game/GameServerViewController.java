package battleship.gui.game;

import battleship.gui.menu.MenuViewController;
import javafx.fxml.FXML;
import javafx.scene.Parent;

public class GameServerViewController {

	@FXML private Parent root;

	public Parent getView() {
		return root;
	}

	private MenuViewController menuViewController;
	
	public void setMenuViewController(MenuViewController menuViewController) {
		this.menuViewController = menuViewController;
	}
	
}

package battleship.gui.game;

import battleship.gui.menu.MenuViewController;
import battleship.model.server.ServerProcedure.Procedure;
import javafx.fxml.FXML;
import javafx.scene.Parent;

/**
 * 
 * @author Wojciech Antczak
 *
 */
public class GameClientViewController implements GameViewController{

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

	@Override
	public void setTextAreaLogi(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProcedure(Procedure procedure) {
		// TODO Auto-generated method stub
		
	}
	
}

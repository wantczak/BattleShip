package battleship.gui.game;

import battleship.gui.menu.MenuViewController;
import battleship.model.network.GameChooserThread;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
/**
 * 
 * @author Wojciech Antczak
 *
 */
public class GameChooserViewController {

	@FXML private Parent chooserBorderPane;
	@FXML private Button btnNetworkTest;
	
	
	private MenuViewController menuViewController;
	public Parent getView() {
		return chooserBorderPane;
	}

	public void setMenuViewController(MenuViewController menuViewController) {
		this.menuViewController = menuViewController;		
	}
	
	@FXML
	private void initialize(){
		btnNetworkTest.setOnAction(e->{
			System.out.println("GAME CHOOSER");
			GameChooserThread chooserThread = new GameChooserThread();
			chooserThread.start();
		});
	}

	
}

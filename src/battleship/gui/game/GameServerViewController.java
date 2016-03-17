package battleship.gui.game;

import battleship.gui.menu.MenuViewController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.GridPane;

public class GameServerViewController {

	@FXML private Parent root;
	@FXML private GridPane Player1GridPane;
	@FXML public Button button01;
	int[][] Player1GameBoard = new int[12][12];

	public Parent getView() {
		return root;
	}

	private MenuViewController menuViewController;
	
	public void setMenuViewController(MenuViewController menuViewController) {
		this.menuViewController = menuViewController;
	}
		
	@FXML
	private void Player1ClickedAction(MouseEvent e){
        Node src = (Node)e.getSource();
        System.out.println("Row: "+ GridPane.getRowIndex(src));
        System.out.println("Column: "+ GridPane.getColumnIndex(src));
        Player1GameBoard[1][5] = 1;
        Player1GameBoard[8][6] = 1;
        Player1GameBoard[2][7] = 1;
        Player1GameBoard[4][8] = 1;

        checkField(GridPane.getRowIndex(src),GridPane.getColumnIndex(src),src);
	}
	
	private void checkField(int row,int column, Node button){
		if (Player1GameBoard[row][column] == 1){
			Button btn = (Button) button;
			btn.setStyle("-fx-background-color: slateblue;");
		}
	}
	
}

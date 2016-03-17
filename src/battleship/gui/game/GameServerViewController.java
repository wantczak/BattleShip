package battleship.gui.game;

import battleship.gui.menu.MenuViewController;
import battleship.model.Board;
import battleship.model.BoardState;
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

	@FXML
	private Parent root;
	@FXML
	private GridPane Player1GridPane;

	Board player1board = new Board();

	public Parent getView() {
		return root;
	}

	private MenuViewController menuViewController;

	public void setMenuViewController(MenuViewController menuViewController) {
		this.menuViewController = menuViewController;
	}

	@FXML
	private void Player1ClickedAction(MouseEvent e) {
		Node src = (Node) e.getSource();
		player1board.locateShips((int)GridPane.getColumnIndex(src),(int) GridPane.getRowIndex(src));
		checkFields(player1board);
		
//		System.out.println("Row: "+ GridPane.getRowIndex(src));
//		System.out.println("Column: "+ GridPane.getColumnIndex(src));
	}

	private void checkFields(Board board) {
		Button btn;
		BoardState[][] boardSt = board.getBoardState();
		for (int i = 0; i < boardSt.length; i++) {
			for (int j = 0; j < boardSt[i].length; j++) {
				btn = (Button)getNodeFromGridPane(Player1GridPane, i, j);
				if (boardSt[i][j] == BoardState.STATEK) {
					btn.setStyle("-fx-background-color: slateblue;");
				}
			}
		}
	}
	
	private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
	    for (Node node : gridPane.getChildren()) {
	        if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
	            return node;
	        }
	    }
	    return null;
	}
}

// System.out.println("Row: "+ GridPane.getRowIndex(src));
// System.out.println("Column: "+ GridPane.getColumnIndex(src));
// Player1GameBoard[1][5] = 1;
// Player1GameBoard[8][6] = 1;
// Player1GameBoard[2][7] = 1;
// Player1GameBoard[4][8] = 1;
//
// checkField(GridPane.getRowIndex(src),GridPane.getColumnIndex(src),src);
// }
//
// private void checkField(int row,int column, Node button){
// if (Player1GameBoard[row][column] == 1){
// Button btn = (Button) button;
// btn.setStyle("-fx-background-color: slateblue;");
//
// }
// }
// }

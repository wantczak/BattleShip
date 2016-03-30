package battleship.gui.game;

import battleship.gui.menu.MenuViewController;
import battleship.model.network.GameChooserThread;
import battleship.model.server.Server;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
/**
 * 
 * @author Wojciech Antczak
 *
 */
public class GameChooserViewController {

	@FXML private Parent chooserBorderPane;
	@FXML private Button btnNetworkTest;
	@FXML private Button btnRefresh;
	@FXML private TableView<Server> serverTableView = new TableView<>();
	@FXML private TableColumn<Server,String> columnServerIP;
	@FXML private TableColumn<Server,String> columnServerStartedGame;
	@FXML private TableColumn<Server,String> columnServerPlayerName;

	private ObservableSet<Server> serverObservableSet;  
	@SuppressWarnings("unused")
	private MenuViewController menuViewController;
	public Parent getView() {
		return chooserBorderPane;
	}

	public void setMenuViewController(MenuViewController menuViewController) {
		this.menuViewController = menuViewController;		
	}
	
	@FXML
	private void initialize(){
		configureTableView(); //USTAWIENIE TABLEVIEW

		
		btnNetworkTest.setOnAction(e->{
			System.out.println("GAME CHOOSER");
			System.out.println("OS GC: "+serverObservableSet);
			GameChooserThread chooserThread = new GameChooserThread(serverObservableSet,this);
			chooserThread.start();
		});
		
		btnRefresh.setOnAction(e->{
			System.out.println("Button start");
			refreshTableView();
		});
	}
	
	private void configureTableView(){
		serverObservableSet = FXCollections.observableSet(); 
		serverObservableSet.add(new Server("AAA","BBB","CCC"));
		columnServerIP.setCellValueFactory(new PropertyValueFactory<Server,String>("serverIP"));
		columnServerStartedGame.setCellValueFactory(new PropertyValueFactory<Server,String>("serverStartedGame"));
		columnServerPlayerName.setCellValueFactory(new PropertyValueFactory<Server,String>("serverPlayerName"));
		
		serverTableView.setItems(FXCollections.observableArrayList(serverObservableSet));
	}
	
	public void refreshTableView(){
    	serverTableView.getItems().clear();
    	serverTableView.getItems().addAll(serverObservableSet);

	}
}

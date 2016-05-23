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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
/**
 * Klasa kontrolera wyboru trybu gry
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
	private boolean serverSelected = false;
	private GameChooserThread chooserThread;
	private GameClientViewController gameClientViewController;
	
	public Parent getView() {
		return chooserBorderPane;
	}

	//=======================PRZYPISANIE REFERENCJI DO KONTROLEROW=============
	public void setMenuViewController(MenuViewController menuViewController) {
		this.menuViewController = menuViewController;		
	}
	
	public void setGameClientViewController(GameClientViewController gameClientViewController) {
		this.gameClientViewController = gameClientViewController;		
	}
	
	public GameClientViewController getGameClientViewController() {
		return gameClientViewController;		
	}

	
	@FXML
	private void initialize(){
		configureTableView(); //USTAWIENIE TABLEVIEW

		btnNetworkTest.setOnAction(e->{
			setServerSelected(true);
			refreshTableView();
			setServerSelected(false);
			if (chooserThread==null){
				chooserThread = new GameChooserThread(serverObservableSet,this);
				chooserThread.start();
			}
		});
		
		btnRefresh.setOnAction(e->{
			System.out.println("Button start");
			setServerSelected(false);
			refreshTableView();
		});
		
		serverTableView.setRowFactory(tableview->{
			TableRow<Server> row = new TableRow<Server>();
			row.setOnMouseClicked(event->{
				//Obsluga klikniecia dwa razy
				if (event.getClickCount() == 2 && (! row.isEmpty()) ){
					Server rowData = row.getItem();
					setServerSelected(true);
					if(GameChooserThread.connectToServer(rowData.getServerIP())){
						ClientGameProcess(rowData);
					}
				}
			});
			return row;
		});		
	}
	
	private void configureTableView(){
		serverObservableSet = FXCollections.observableSet(); 
		columnServerIP.setCellValueFactory(new PropertyValueFactory<Server,String>("serverIP"));
		columnServerStartedGame.setCellValueFactory(new PropertyValueFactory<Server,String>("serverStartedGame"));
		columnServerPlayerName.setCellValueFactory(new PropertyValueFactory<Server,String>("serverPlayerName"));
		serverTableView.setItems(FXCollections.observableArrayList(serverObservableSet));
	}
	
	public void refreshTableView(){
    	serverTableView.getItems().clear();
    	serverTableView.getItems().addAll(serverObservableSet);
	}
	
	public boolean ServerSelected(){
		return isServerSelected();
	}
	
	private void ClientGameProcess(Server gameServer){
		try{
			this.setGameClientViewController(menuViewController.getFactory().getGameClientViewController()); //utworzenie nowej instancji gry
			System.out.println(gameServer);
			gameClientViewController.setGameServer(gameServer); //przekazanie wybranego Servera do okna gry
			menuViewController.getContentPane().setCenter(gameClientViewController.getView()); //pokazanie okna gry w BorderPane
			
		}
		
		catch(Exception ex){
			
		}
	}

	public boolean isServerSelected() {
		return serverSelected;
	}

	public void setServerSelected(boolean serverSelected) {
		this.serverSelected = serverSelected;
	}
}

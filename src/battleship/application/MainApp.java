package battleship.application;
	
import battleship.gui.menu.MenuViewController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

/**
 * Glowna klasa tworzaca gre
 * @author Wojciech Antczak
 *
 */
public class MainApp extends Application {
	
    private static double xOffset = 0;
    private static double yOffset = 0;
    
	@Override
	public void start(Stage primaryStage) {
		try {
			MainAppFactory factory = new MainAppFactory();
	        MenuViewController menuViewController = factory.getMenuViewController();
	        menuViewController.init(factory, primaryStage);
	        
	        Scene scene = new Scene(menuViewController.getView(), 1300, 850);
	        scene.getStylesheets().add("/css/application.css");
	        primaryStage.setScene(scene);
	        primaryStage.initStyle(StageStyle.UNDECORATED);
	        primaryStage.setMaximized(true);
	        primaryStage.setTitle("Battle S.H.I.P.");
	        primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

package battleship.application;
	
import battleship.gui.menu.MenuViewController;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class MainApp extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			MainAppFactory factory = new MainAppFactory();
	        MenuViewController menuViewController = factory.getMenuViewController();
	        menuViewController.init(factory);
	        Scene scene = new Scene(menuViewController.getView(), 800, 600);
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

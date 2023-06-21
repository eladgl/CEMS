package Client;

import java.io.IOException;
import java.net.URL;

import clientGUI.connectToServerController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 * The main class for the CEMS (Client-Server Exam Management System) client
 * user interface.
 */

public class CEMSClientUI extends Application {
	/**
	 * The starting stage that the Javafx gives is saved for future uses.
	 */
	public static Stage primary_stage;

	/**
	 * a CEMSClientController instance : used for the accept function in other
	 * classes
	 */
	public static CEMSClientController chat; // only one instance
	private static Parent root;
	private static Scene scene;
	/**
	 * connectToServerController instance
	 */
	public static connectToServerController clientConnect;

	private static double xOffset = 0;
	private static double yOffset = 0;
	/**
	 * This method is called when the JavaFX application is starting.
	 * It initializes the primary stage, loads the GUI from an FXML file,
	 * sets up the scene, and displays the primary stage.
	 *
	 * @param primaryStage The primary stage for the application.
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			primary_stage = primaryStage;
			root = FXMLLoader.load(getClass().getResource("/clientGUI/connectToServer.fxml"));
			scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("CEMS");
			primaryStage.setResizable(false);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			makeDraggableScreen(root);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Switches the current scene to the provided scene.
	 * 
	 * @param scene The new scene to switch to.
	 * @throws IOException 
	 */

	public static void switchScenes(URL screen_name) throws IOException {
    	root = FXMLLoader.load(screen_name);
    	makeDraggableScreen(root);
		scene = new Scene(root);
		primary_stage.setScene(scene);
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        	primary_stage.setX((screenBounds.getWidth() - primary_stage.getWidth()) / 2);
        	primary_stage.setY((screenBounds.getHeight() - primary_stage.getHeight()) / 2);
		
	}
	/**
	 * Makes the screen draggable by handling mouse events for the specified root node.
	 *
	 * @param root The root node of the screen.
	 */
	private static void makeDraggableScreen(Parent root) {
		root.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = primary_stage.getX() - event.getScreenX();
				yOffset = primary_stage.getY() - event.getScreenY();
			}
		});
		root.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				primary_stage.setX(event.getScreenX() + xOffset);
				primary_stage.setY(event.getScreenY() + yOffset);
			}
		});
	}
	/**
	 * The main method that launches the CEMSClientUI application.
	 * 
	 * @param args The command-line arguments.
	 */

	public static void main(String[] args) {
		launch(args);
	}
}
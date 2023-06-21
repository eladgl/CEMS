package serverUI;

import javafx.application.Application;
//import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Scene;
//import javafx.scene.image.Image;
//import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The user interface of the CEMS server. It starts the server and GUI.
 * 
 * This class extends the Application class from JavaFX, allowing it to launch
 * the JavaFX application.
 * 
 * This class contains a static boolean flag to indicate the server's connection
 * status.
 * 
 * The main method launches the JavaFX application.
 * 
 * The start method creates a ServerBoundary object and starts the server's
 * primary stage.
 * 
 * @author Group 5
 */
public class ServerUI extends Application {
	public Stage primaryStage;
	public static ServerBoundary sb;
	public static CemsServer cemsServer;

	public static boolean isConnected;
	// initialize it before the class is built
	static {
		ServerUI.isConnected = false;
	}
	/**
	 * The main method of the ServerUI class. It launches the JavaFX application.
	 * 
	 * @param args the command-line arguments
	 * @throws Exception if an exception occurs during the launch of the
	 *                   JavaFX application
	 */
	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	/**
	 * The start method of the ServerUI class. It creates a ServerBoundary object
	 * and starts the server's primary stage.
	 * 
	 * @param primaryStage the primary stage of the JavaFX application
	 * @throws Exception if an exception occurs during the start of the JavaFX
	 *                   application
	 */
	public void start(Stage primaryStage) throws Exception {
		try {
			sb = new ServerBoundary();
			sb.start(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
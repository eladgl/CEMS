package clientGUI;

import java.util.HashMap;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
/**
 * The screen controller class manages the switching of screens in a JavaFX application.
 * It maintains a mapping of screen names to their corresponding Pane objects.
 * Screens can be added, removed, and activated using this controller.
 */
public class ScreenController {
	private HashMap<String, Pane> screenMap = new HashMap<>();
	private Scene main;
	 /**
     * Constructs a new screen controller with the specified main scene.
     * 
     * @param main the main scene of the application
     */
	public ScreenController(Scene main) {
		this.main = main;
	}
	/**
     * Adds a screen to the screen controller.
     * Associates the specified screen name with the given pane.
     * 
     * @param name the name of the screen
     * @param pane the pane representing the screen
     */
	protected void addScreen(String name, Pane pane) {
		screenMap.put(name, pane);
	}
	/**
     * Removes a screen from the screen controller.
     * 
     * @param name the name of the screen to remove
     */
	protected void removeScreen(String name) {
		screenMap.remove(name);
	}
	/**
     * Activates a screen by setting it as the root of the main scene.
     * 
     * @param name the name of the screen to activate
     */
	protected void activate(String name) {
		main.setRoot(screenMap.get(name));
	}
}
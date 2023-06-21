package clientGUI;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import Client.CEMSClient;
import Client.CEMSClientUI;
import entities.User;
import headDepartmentUI.HeadDepartmentMainScreenController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import studentUi.studentMainScreenController;
import teacherUi.TeacherMainScreenController;
import utilities.CONSTANTS;
import utilities.Message;
/**
 * The controller class for the user login screen.
 * It handles user login and navigation to the appropriate main screen based on the user's role.
 */
public class UserLoginController {
	private static double xOffset = 0;
	private static double yOffset = 0;

	public static boolean isLoginValid = false;
	public static User user;
	private String nextScene;
	// public static CEMSClientController chat; // only one instance

	@FXML
	private TextField username_field;

	@FXML
	private PasswordField password_field;

	@FXML
	private Button login_in_btn;

	@FXML
	private ImageView xButton;

	@FXML
	private Label error_label;
	/**
     * Initializes the login screen.
     * Sets up event handlers and default values.
     */
	@FXML
	void initialize() {
		user = null; // each time client is logout we reset the user data and isLoginValid=false
		isLoginValid = false;
		// add click on X img event
		xButton.setPickOnBounds(true);
		xButton.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClient.exitButton();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		});

		username_field.setText("user6");
		password_field.setText("123456");

	}
	/**Switches screen to a registration screen
	 * 
	 * @param event
	 * @throws IOException
	 */
	@FXML
    void register(ActionEvent event) throws IOException {
		URL url = getClass().getResource("RegistrationSimulationScreen.fxml");
	    CEMSClientUI.switchScenes(url);
    }
	/**
     * Handles the key press event.
     * If the Enter key is pressed, it calls the handleLogin method.
     * 
     * @param event the key event
     * @throws IOException if an I/O error occurs
     */
	// Handle the Enter Button
	@FXML
	private void handleKeyPress(KeyEvent event) throws IOException {
		if (event.getCode() == KeyCode.ENTER) {
			handleLogin();
		}
	}
	/**
     * Handles the user login process.
     * Validates the username and password, and navigates to the appropriate main screen if successful.
     * Displays an error message if the login is invalid.
     * 
     * @throws IOException if an I/O error occurs
     */
	public void handleLogin() throws IOException {
		String username = username_field.getText();
		String password = password_field.getText();

		if (isValidLogin(username, password)) {
			error_label.setText("Correct!");

			switch (user.getUserPermission()) {
			case CONSTANTS.studentRole:
				studentMainScreenController.user = user;
				nextScene = "/studentUi/studentMainScreen.fxml";
				break;
			case CONSTANTS.teacherRole:
				TeacherMainScreenController.user = user;
				nextScene = "/teacherUi/TeacherMainScreen.fxml";
				break;
			case CONSTANTS.headOfDepartmentRole:
				HeadDepartmentMainScreenController.user = user;
				nextScene = "/headDepartmentUI/headDepartmentMainScreen.fxml";
				break;
			default:
				throw new IllegalArgumentException("Unknown user!");
			}
			ProfileScreenController.user = user;
			System.out.println(user.getUserName() + " logged in");

			CEMSClientUI.switchScenes(getClass().getResource(nextScene));

		} else {
			error_label.setText("Invalid username/password or already logged in. try again");
		}

	}
	/**
     * Validates the user login.
     * Checks the username and password against the server's database.
     * 
     * @param username the username
     * @param password the password
     * @return true if the login is valid, false otherwise
     */
	private boolean isValidLogin(final String username, final String password) {
		if (username.isEmpty() || password.isEmpty())
			return false;

		ArrayList<String> Data = new ArrayList<String>(); // create arraylist to send to server
		// to check username and password in DB
		Data.add(username); // insert user name and password that the user in the UI insert
		Data.add(password);
		Message message_to_server = new Message(CONSTANTS.CheckLogin, Data);
		try {
			CEMSClientUI.chat.accept(message_to_server);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isLoginValid) {
			isLoginValid = false;
			return true;
		}
		return false;
	}
	/**
     * Makes the screen draggable.
     * Allows the user to drag the screen by clicking and dragging on any part of it.
     * 
     * @param stage the stage
     * @param root  the root node of the scene
     */
	private void makeDraggableScreen(Stage stage, Parent root) {
		root.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = stage.getX() - event.getScreenX();
				yOffset = stage.getY() - event.getScreenY();
			}
		});
		root.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				stage.setX(event.getScreenX() + xOffset);
				stage.setY(event.getScreenY() + yOffset);
			}
		});
	}
}
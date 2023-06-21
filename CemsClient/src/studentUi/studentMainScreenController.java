package studentUi;


import java.io.IOException;
import Client.CEMSClient;
import Client.CEMSClientUI;
import clientGUI.ProfileScreenController;
import clientGUI.ScreenNames;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utilities.CONSTANTS;

import utilities.Message;

/**
 * Controller class for the student main screen.
 */
public class studentMainScreenController {
	/**
	 * The username of the user.
	 */
	public static String username;
	/**
	 * The user entity.
	 */
	public static User user;
	/**
	 * Indicates whether the user is logged out.
	 */
	public static boolean is_loged_out = false;
	/**
	 * Indicates if the logout button is clicked.
	 */
	public static boolean logoutClick = false;

	@FXML
	private Label welcomeLabel;

	@FXML
	private Button xButton;

	@FXML
	private ImageView user_profile_image;
	
	@FXML
	private ImageView sms_view_image;

	/**
	 * Initializes the controller and sets up event handlers.
	 */
	@FXML
	void initialize() 
	{
		// add click on profile image event
		user_profile_image.setPickOnBounds(true);
		user_profile_image.setOnMouseClicked((MouseEvent e) -> {
			try {
				ProfileScreenController.user = user;
				CEMSClientUI.switchScenes(getClass().getResource("/clientGUI/ProfileScreen.fxml"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});


		xButton.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClient.logout(user.getFirstName());
				CEMSClient.exitButton();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		});
		
		sms_view_image.setPickOnBounds(true);
		sms_view_image.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClientUI.switchScenes(getClass().getResource(ScreenNames.StudentSmsScreen));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		welcomeLabel.setText("Welcome " + user.getFirstName());
	}

	/**
	 * Handles the action when the start test button is clicked.
	 *
	 * @param event The action event.
	 * @throws Exception If an exception occurs.
	 */
	public void startTestAction(ActionEvent event) throws Exception {
		// get exams for student user
		Message dataToServer = new Message(CONSTANTS.getStudentNotTakenTests, user.getUserName());
		CEMSClientUI.chat.accept(dataToServer); //send data to the server from ui
		CEMSClientUI.switchScenes(getClass().getResource("startTestScreen.fxml"));
	}

	/**
	 * Handles the action when the test result button is clicked.
	 *
	 * @param event The action event.
	 * @throws IOException If an I/O exception occurs.
	 */
	public void testResultAction(ActionEvent event) throws IOException {
		// when pressing on testResult button move to screen with grades
		CEMSClientUI.switchScenes(getClass().getResource("viewGradesScreen.fxml"));
	}

	/**
	 * Handles the action when the logout button is clicked.
	 *
	 * @param event The action event.
	 * @throws IOException If an I/O exception occurs.
	 */
	public void logoutUserFromSystem(ActionEvent event) throws IOException {
		// send request for logout from the client UI
		logoutClick = true;
		CEMSClient.logout(user.getUserName());
		if (is_loged_out) {
			// quit connection and wait
			try {
				CEMSClientUI.switchScenes(getClass().getResource("/clientGUI/LoginScreen.fxml"));
			} catch (IOException e) {
				System.out.println("Failed to wait\n");
				e.printStackTrace();
			}
		} else {
			System.out.println("Failed to logout!");
		}
		logoutClick = false;
	}

	/**
	 * Retrieves the username of the user.
	 *
	 * @return The username.
	 */
	public static String getUsername() {
		return user.getUserName();
	}
}

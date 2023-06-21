package clientGUI;

import java.io.IOException;

import Client.CEMSClient;
import Client.CEMSClientController;
import Client.CEMSClientUI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utilities.CONSTANTS;
import javafx.scene.Parent;

/**
 * Controller class for the "connectToServer.fxml" view.
 */
public class connectToServerController {
	public static CEMSClientController chat; // only one instance

	@FXML
	private Button connectButton;

	@FXML
	private Label errorLbl;
	@FXML
	private TextField ipTextField;

	@FXML
	private ImageView xButton;

	/**
	 * Initializes the controller.
	 */
	@FXML
	void initialize() {
		xButton.setPickOnBounds(true);
		xButton.setOnMouseClicked((MouseEvent e) -> {
			System.exit(0);
		});
	}

	/**
	 * Event handler for the "connectButton" button. Connects to the server and
	 * changes the screen to the login screen if successful.
	 *
	 * @param event The action event.
	 */
	@FXML
	void connectToServer(ActionEvent event) {
		try {
			if (isValidIPAddress()) {
				CEMSClientUI.chat = new CEMSClientController(ipTextField.getText(), CONSTANTS.DEFAULT_PORT);
				if (CEMSClient.clientController != null) {
					try {
						CEMSClientUI.switchScenes(getClass().getResource(ScreenNames.LoginScreen));
					} catch (Exception e) {
						showError("Wrong IP/Could not connect");
						e.printStackTrace();
					}
				} else
					showError("Wrong IP/Could not connect");
			}
		} catch (Exception e) {
			showError("Wrong IP/Could not connect");
			e.printStackTrace();
		}

	}

	/**
	 * Starts the JavaFX stage with the "connectToServer.fxml" view.
	 *
	 * @param primaryStage The primary stage.
	 * @throws Exception if an error occurs during stage initialization.
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/clientGUI/connectToServer.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect To Server");
		primaryStage.show();
	}

	/**
	 * Checks if the entered IP address is valid.
	 *
	 * @return true if the IP address is valid, false otherwise.
	 */
	private boolean isValidIPAddress() {
		String ip = ipTextField.getText();
		// Check if the IP address is not null
		if (ip == null) {
			showError("You did not enter anything");
			return false;
		}
		// Check if the IP address is "localhost"
		if (ip.equalsIgnoreCase("localhost"))
			return true;
		// Check if the IP address matches the format xxx.xxx.xxx.xxx
		String ipAddressPattern = "^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$";
		if (ip.matches(ipAddressPattern)) {
			String[] octets = ip.split("\\.");
			for (String octet : octets) {
				int octetValue = Integer.parseInt(octet);
				if (octetValue < 0 || octetValue > 255)
					return false;
			}
			return true;
		}
		showError("You must enter a valid ip address xxx.xxx.xxx.xxx 0-255\n" + "for each x");
		return false;
	}
	/**
	 * Displays an error message on the errorLbl component.
	 *
	 * @param msg the error message to be displayed.
	 */
	private void showError(String msg) {
		errorLbl.setVisible(true);
		errorLbl.setText(msg);
	}
}
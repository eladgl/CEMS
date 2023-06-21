package serverUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.List;

import entities.ClientConnectionStatus;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import utilities.CONSTANTS;
import utilities.TextFieldIdentifier;
import utilities.checkInputHelper;


/**
*
*The ServerBoundary class is responsible for handling the user interface (UI) of the server application.
*It manages the UI components such as text fields, labels, buttons, and tables, and provides methods
*for interacting with these components.
*This class contains methods for initializing the UI, starting the server, handling user input, and updating
*the UI based on the server status and client connections.
*Note: This class is specific to the JavaFX framework and assumes the existence of corresponding FXML files
*and CSS stylesheets for UI layout and styling.
 * @author Elad Goldenberg
 * @author Raz Tibi
 * @author Razi Mograbi
 * @author Shimron Yifrah
 * @author Alex Baboshin
 * @author Shaked Oz
 * @version May 2023
*/

public class ServerBoundary 
{
	// These are used as logic behavior for UI components
	private final boolean[] ip_error_flags = { false, false, false, false };
	private final boolean[] enable_start_server_button = { true, true, true, true, true };
	//for mouse handle event for draggable
	private static double xOffset = 0;
    private static double yOffset = 0;

	private static ObservableList<ClientConnectionStatus> clientsList = FXCollections.observableArrayList();

	@FXML
	private TextField PortTxt, DBUserNameTxt, DBNameTxt;

	// ip address textFields
	@FXML
	private TextField ip1, ip2, ip3, ip4;

	private List<TextField> txtFields = new ArrayList<>();
	@FXML
	private Label serverMessagesUi;

	@FXML
	private Label serverMsgTxt, PortErrorLabel, ipErrorLabel, passwordErrorLabel, usernameErrorLabel;

	@FXML
	private Label dbNameErrorLabel;

	@FXML
	private PasswordField PasswordTxt;

	@FXML
	private Button startserverBtn,  stopServerBtn;
	
	@FXML
	private VBox TableViewContainer;
	
	@FXML
    private ImageView closeAppBtn;
	
	@FXML
	private TableView<ClientConnectionStatus> connStatusTable;
	
	@FXML
	private TableColumn<ClientConnectionStatus,String> IPCol;
	
	@FXML
	private TableColumn<ClientConnectionStatus,String> HostCol;
	
	@FXML
	private TableColumn<ClientConnectionStatus,String> StatusCol;
	
	@FXML
	private TableColumn<ClientConnectionStatus,String> StTimeCol;
	
	@FXML
	private Circle serverStartedCircle;

	// mouse click on start server button
	@FXML
	void serveStartAction(ActionEvent event) throws Exception {
		start_server();
	}
	
	/**
	 * Initializes the server UI components and sets their default values.
	 */
	
	@FXML
	void initialize() {
		// add all text fields to the txtFields List
		txtFields.add(ip1);
		txtFields.add(ip2);
		txtFields.add(ip3);
		txtFields.add(ip4);
		txtFields.add(PortTxt);
		txtFields.add(DBNameTxt);
		txtFields.add(DBUserNameTxt);
		txtFields.add(PasswordTxt);
		// set default values
		String ip = getIP();
		String[] Result = ip.split("\\."); // split ip into string array
		ip1.setText(Result[0]);
		ip2.setText(Result[1]);
		ip3.setText(Result[2]);
		ip4.setText(Result[3]);
		PortTxt.setText("5555");
		DBNameTxt.setText("jdbc:mysql://localhost/CEMS?serverTimezone=IST&allowLoadLocalInfile=TRUE");
		DBUserNameTxt.setText("root");
		PasswordTxt.setText("Aa123456");
		
		// add listeners
		addIPTextChangeListener(ip1, 0);
		addIPTextChangeListener(ip2, 1);
		addIPTextChangeListener(ip3, 2);
		addIPTextChangeListener(ip4, 3);

		add_TextChangeListener(DBNameTxt);
		add_TextChangeListener(DBUserNameTxt);
		add_TextChangeListener(PortTxt);
		addDB_PasswordTextChangeListener(PasswordTxt);
		
	}
	/**
	 * Move between textFields components with pressing tab.
	 * If the Tab key is pressed and the current target is a TextField,
	 * it moves the focus to the next TextField in the list.
	 * If the Enter key is pressed and the start server button is not disabled, it starts the server.
	 * 
	 * @param event The key event triggered by the user
	 */
	// Move between textFields components with pressing tab
	@FXML
	void moveInTextFields(KeyEvent event) {
		if (event.getCode() == KeyCode.TAB && event.getTarget() instanceof TextField) {
			TextField source = (TextField) event.getTarget();
			int size = txtFields.size();
			int idx = txtFields.indexOf(source); // get index focus of current element
			if (idx + 1 == size) // if got to end of list return from the
				txtFields.get(0).requestFocus(); // begining
			else
				txtFields.get(idx + 1).requestFocus();
		} else if (event.getCode() == KeyCode.ENTER) { // if click on enter
			if (startserverBtn.isDisabled() == false) { // if button is no disabled proceed
				start_server();
			}
		}
	}
	/**
	 * Starts the primary stage of the server application.
	 * Loads the server UI from the FXML file, sets the scene, and displays the stage.
	 * 
	 * @param primaryStage The primary stage of the JavaFX application
	 * @throws Exception if an error occurs during the operation
	 */
	// start method for primary stage of server
	public void start(Stage primaryStage) throws Exception {
		try {
			AnchorPane root = FXMLLoader.load(getClass().getResource("ServerAppScreen.fxml"));
			Scene scene = new Scene(root);
			//String imagePath ="images/server.png";
			//primaryStage.getIcons().add(new Image(new FileInputStream(imagePath)));
			root.setOnMousePressed(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                xOffset = primaryStage.getX() - event.getScreenX();
	                yOffset = primaryStage.getY() - event.getScreenY();
	            }
	        });
			
			root.setOnMouseDragged(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	                primaryStage.setX(event.getScreenX() + xOffset);
	                primaryStage.setY(event.getScreenY() + yOffset);
	            }
	        });
			
			primaryStage.setScene(scene);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.setTitle("CEMS Server");
			primaryStage.setResizable(false);	
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Adds a listener to the IP text field to check for any change in the text value.
	 * If the input is not a digit between 0 and 254, it shows an error label.
	 * 
	 * @param textField The IP text field to add the listener to
	 * @param ip_index The index of the IP text field
	 */
	// listener for any change in text value in ip text fields. if not a digit
	// between 0-254 show error label
	private void addIPTextChangeListener(TextField textField, int ip_index) {
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			boolean isValid = checkInputHelper.checkInput(newValue, CONSTANTS.IP_ADDRESS_MIN_VAL,
					CONSTANTS.IP_ADDRESS_MAX_VAL);
			if (isValid) {
				// Set the focus color back to the default color
				textField.setStyle("-fx-focus-color: -fx-outer-border;");
				// Decrement the ip_counters and hide the error label if necessary
				ip_error_flags[ip_index] = false;
				int count = 0;
				for (boolean flag : ip_error_flags)
					if (flag == false)
						count++;
				if (count == 4)
					ipErrorLabel.setVisible(false);
				enable_start_server_button[TextFieldIdentifier.ip_correct.getValue()] = true;

			} else {
				// Set the focus color to red
				textField.setStyle("-fx-focus-color: red;");
				// Increment the ip_counters and show the error label
				ip_error_flags[ip_index] = true;
				ipErrorLabel.setVisible(true);
				enable_start_server_button[TextFieldIdentifier.ip_correct.getValue()] = false;
			}
			checkIfToUnlockStartServerButton();
		});
	}
	/**
	 * Adds event listeners to the text fields in the server UI, including DB URL, DB name, and password.
	 * 
	 * @param textField The text field to add the listener to
	 */
	// add event listeners to textfields in server ui, db url, db name and password
	private void add_TextChangeListener(TextField textField) {
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			String id = textField.getId();
			if (id.equals("DBNameTxt"))
				TextFieldInputManager(dbNameErrorLabel, newValue, CONSTANTS.DB_URL_MAX_LENGTH,
						TextFieldIdentifier.db_url_correct);
			else if (id.equals("DBUserNameTxt"))
				TextFieldInputManager(usernameErrorLabel, newValue, CONSTANTS.DB_USERNAME_MAX_LENGTH,
						TextFieldIdentifier.username_correct);
			else if (id.equals("PortTxt"))
				IntFieldInputManager(PortErrorLabel, newValue, CONSTANTS.PORT_MIN_VAL, CONSTANTS.PORT_MAX_VAL,
						TextFieldIdentifier.db_url_correct);
		});
	}
	/**
	 * Adds a listener that fires when there is a change in the password text field.
	 * 
	 * @param passwordField The password text field to add the listener to
	 */
	// add a listener that fires when there is a change in the password text field
	private void addDB_PasswordTextChangeListener(PasswordField passwordField) {
		passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
			TextFieldInputManager(passwordErrorLabel, newValue, CONSTANTS.DB_PASSWORD_MAX_LENGTH,
					TextFieldIdentifier.password_correct);
		});

	}

	/**
	 * Checks if any of the text fields are empty.
	 * 
	 * @return true if any of the text fields are empty, false otherwise
	 */
	// go over the boolean array that checks if all the values of all the textfields
	// have passed the
	// test checks and exist/valid for each case and then enable/disable the button
	// ui for start server
	private boolean check_if_any_empty() {
		for (boolean bool : enable_start_server_button) {
			if (bool == false) {
				startserverBtn.setDisable(true); // lock the start server button
				return false;
			}
		}
		startserverBtn.setDisable(false); // unlock it
		return true;
	}
	
	/**
	 * Starts the server.
	 * Runs the server with the provided port, DB URL, DB username, and DB password.
	 * Sets up the dynamic table view and displays a success or failure message.
	 */
	// Method that returns the current ip of the host machine
	private String getIP() {
		String ipAddress = "";
		try {
			InetAddress address = InetAddress.getLocalHost();
			ipAddress = address.getHostAddress();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		}
		return ipAddress;
	}
	
	/**
	 * Runs the server with the specified port, DB URL, DB username, and DB password.
	 * 
	 * @param port The port number for the server
	 * @param dbUrl The database URL for the server
	 * @param dbUsername The database username for the server
	 * @param dbPassword The database password for the server
	 * @throws Exception if an error occurs during the operation
	 */
	public static void runServer(String p, String url_DB, String userNameDB, String passwordDB) throws Exception {
		int port = 0; // Port to listen on

		try {
			port = Integer.parseInt(p); // Set port to 5555
		} catch (Exception ex) {
			System.out.println("ERROR - Could not connect!"); 
			throw ex;
		}
		ServerUI.cemsServer = new CemsServer(port, url_DB, userNameDB, passwordDB);
		try {
			ServerUI.cemsServer.listen();
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
			throw ex;
		}
	}
	/**
	 * Retrieves the port number entered by the user.
	 * 
	 * @return The port number as an integer
	 */
	/* define getters */
	private String getPort() {
		return PortTxt.getText();
	}
	/**
	 * Retrieves the database URL entered by the user.
	 * 
	 * @return The database URL as a string
	 */
	private String getDB_url() {
		return DBNameTxt.getText();
	}
	/**
	 * Retrieves the database username entered by the user.
	 * 
	 * @return The database username as a string
	 */
	private String getDB_username() {
		return DBUserNameTxt.getText();
	}
	/**
	 * Retrieves the database password entered by the user.
	 * 
	 * @return The database password as a string
	 */
	private String getDB_password() {
		return PasswordTxt.getText();
	}

	/**
	 * Checks if any of the text fields have errors or if the server is already listening.
	 * If the server is listening, keeps the start server button disabled.
	 * If there are errors, keeps the button disabled.
	 * Otherwise, enables the button.
	 */
	// check if any errors in text fields or if the server already listens
	// if the server listens, keep the button disabled
	// if there are error keep it disabled
	// else unlock it
	// also check if it is null so it won;t fall
	private void checkIfToUnlockStartServerButton() {
		if (ServerUI.cemsServer != null && ServerUI.cemsServer.isListening()) {
			startserverBtn.setDisable(true);
		} else if (!check_if_any_empty())
			if (ServerUI.cemsServer != null && !ServerUI.cemsServer.isListening())
				startserverBtn.setDisable(false);
			else {
				startserverBtn.setDisable(true);
			}
	}
	
	/**
	 * Starts the server.
	 * Runs the server with the provided port, DB URL, DB username, and DB password.
	 * Sets up the dynamic table view and displays a success or failure message.
	 */
	private void start_server() 
	{
		try {	
			runServer(getPort(), getDB_url(), getDB_username(), getDB_password());
			//make tableview Dynamic
			connStatusTable.setItems(clientsList);
			this.IPCol.setCellValueFactory(new PropertyValueFactory<>("ip"));
			this.HostCol.setCellValueFactory(new PropertyValueFactory<>("host"));
			this.StatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
			this.StTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
			serverMessagesUi.setText("Server Started Succsessfully!");
			
			// turn the circle green to indicate that the server started.
			serverStartedCircle.setFill(Color.GREEN);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			serverMessagesUi.setText("Couldn't start server");
			return;
		} finally {
			checkIfToUnlockStartServerButton();
			//if there was a problem in runServer the CemsServer put null value in cemsServer static variable in ServerUI
			if(CemsServer.listenflag == false) {
				startserverBtn.setDisable(false); //enable the button
			}
			
		}
	}

	// this is a manager for TextFields that take strings
	private void TextFieldInputManager(Label errorLabel, String newValue, int max_value, TextFieldIdentifier id) {
		if (checkInputHelper.validateText(newValue, max_value) == true) { // check if the text is valid
			enable_start_server_button[id.getValue()] = true; // change the array index to true
			errorLabel.setVisible(false); // change visibility of error label
		} else {
			enable_start_server_button[id.getValue()] = false; // if it wasn't valid change to false
			errorLabel.setVisible(true); // show error label information
		}
		checkIfToUnlockStartServerButton(); // overall check if we need to
	} // disable/enable start server button
		
	// this is a manager for TextFields that take only int
	private void IntFieldInputManager(Label errorLabel, String newValue, int min, int max, TextFieldIdentifier id) {
		if (checkInputHelper.checkInput(newValue, min, max) == true) {
			enable_start_server_button[id.getValue()] = true; // change the array idx to true
			errorLabel.setVisible(false); // change visibility of error label
		} else {
			enable_start_server_button[id.getValue()] = false; // if it wasn't valid change to false
			errorLabel.setVisible(true); // show error label information
		}
		checkIfToUnlockStartServerButton(); // overall check if we need to
	}

	
	/**
	 * update the labels of serverBoundary after client connected
	 * 
	 * @param host
	 * @param ip
	 * @param username
	 * @param status   online or offline
	 * @param aff      affiliation - teacher, student, principal
	 */
	public void updateConnectedClient(InetAddress ip, String hostName, String status, String startTime) {
		ClientConnectionStatus client = new ClientConnectionStatus(ip.getHostAddress(), hostName, status);
		if(clientsList.indexOf(client) == -1 )
			clientsList.add(client);
		else {
			clientsList.remove(clientsList.indexOf(client));
			clientsList.add(client);
		}
		System.out.println(ip.getHostAddress() +" Connected succsessfully!");

	}
	
	/**

	Disconnects a client based on the provided IP address, host name, and status.
	@param ip the IP address of the client to be disconnected
	@param hostName the host name of the client to be disconnected
	@param status the status of the client to be disconnected
	*/
	
	public void disconnectClient(InetAddress ip, String hostName, String status) {
		ClientConnectionStatus client = new ClientConnectionStatus(ip.getHostAddress(), hostName, status);
		clientsList.remove(clientsList.indexOf(client));
		clientsList.add(client);
		System.out.println(ip.getHostAddress() +" Disconnected succsessfully!");
	}
	/**
	 * Handles the event when the "Close App" button is clicked.
	 * Closes the application by terminating the Java Virtual Machine.
	 *
	 * @param event The MouseEvent representing the click event.
	 */
	@FXML
    void closeApp(MouseEvent event) {
		System.exit(0);
	}
	/**
	 * Handles the event when the "Stop Server" button is clicked.
	 * Stops the server if it is running, updates the UI elements accordingly,
	 * and displays a message indicating the successful server stop.
	 *
	 * @param event The MouseEvent representing the click event.
	 */
	@FXML
    void stopServer(MouseEvent event) {
		if(ServerUI.cemsServer != null) {
			ServerUI.cemsServer.serverStopped();
			serverStartedCircle.setFill(Color.RED);
			serverMessagesUi.setText("Server Stopped Successfully!");
		}
    }
}
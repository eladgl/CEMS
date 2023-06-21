package clientGUI;

import java.io.IOException;
import java.util.regex.Pattern;

import Client.CEMSClient;
import Client.CEMSClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import entities.User;
import utilities.CONSTANTS;
import utilities.Message;
/**
 * The controller class for the Registration Simulation screen.
 * Handles user interactions and validation for registration form fields.
 */
public class RegistrationSimulationScreenController {
	 /**
     * The message to display on the registration form. Returns from CEMSClient
     */
	public static String message;
	/**
     * An array to track the validation status of form fields.
     */
	private boolean[] values = { false, false, false, false, false, false, false ,false};
	
	@FXML
	private ImageView backArrow, xBtn;

	@FXML
	private TextField fnameField, phoneField, emailField, lnameField, idField, usernameField;

	@FXML
	private Label emailLbl, fnameLbl, idLbl, lnameLbl, messageLabel, passwordLbl, roleLbl, userLbl, phoneLbl;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Button registerBtn;

	@FXML
	private ComboBox<String> roleComboBox;
	/**
     * Handles the register action event.
     * Validates form fields and registers the user if all fields are valid.
     *
     * @param event The action event.
     */
	@FXML
	void register(ActionEvent event) {
		for (boolean flag : values) {
			if (!flag) {
				showError("Correct any red comments showing");
				return;
			}
		}
		registerUser();
	}
	/**
     * Initializes the controller.
     * Sets up event listeners and initializes form field validation.
     */
	@FXML
	void initialize() {
		roleComboBox.getItems().addAll("Student", "Lecturer", "Head of Department");
		xBtn.setPickOnBounds(true);
		xBtn.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClient.exitButton();

			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		});

		backArrow.setPickOnBounds(true);
		// back arrow click
		backArrow.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClientUI.switchScenes(getClass().getResource("LoginScreen.fxml"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		// username
		usernameField.setOnKeyTyped(event -> {
			String text = usernameField.getText();
			values[0] = validateInput(text, "username");
			if (values[0])
				userLbl.setText("");
			else
				userLbl.setText("only english");
			updateFieldValidationStyle(usernameField, values[0], 0);
		});

		// Listener for passwordField
		passwordField.setOnKeyTyped(event -> {
			String text = passwordField.getText();
			System.out.println(text);
			values[1] = validateInput(text, "password");
			if (values[1])
				passwordLbl.setText("");
			else
				passwordLbl.setText("one capital and small and digits 8+ char");
			updatePasswordValidationStyle(passwordField, values[1], 1);
		});

		// Listener for emailField
		emailField.setOnKeyTyped(event -> {
			String text = emailField.getText();
			values[2] = validateInput(text, "email");
			if (values[2])
				emailLbl.setText("");
			else
				emailLbl.setText("one @");
			updateFieldValidationStyle(emailField, values[2], 2);
		});

		// Listener for fnameField
		fnameField.setOnKeyTyped(event -> {
			String text = fnameField.getText();
			values[3] = validateInput(text, "name");
			if (values[3])
				fnameLbl.setText("");
			else
				fnameLbl.setText("one capital only english");
			updateFieldValidationStyle(fnameField, values[3], 3);
		});

		// Listener for lnameField
		lnameField.setOnKeyTyped(event -> {
			String text = lnameField.getText();
			values[4] = validateInput(text, "name");
			if (values[4])
				lnameLbl.setText("");
			else
				lnameLbl.setText("one capital only english");
			updateFieldValidationStyle(lnameField, values[4], 4);
		});

		// Listener for idField
		idField.setOnKeyTyped(event -> {
			String text = idField.getText();
			values[5] = validateInput(text, "id");
			if (values[5])
				idLbl.setText("");
			else
				idLbl.setText("9 digits");
			updateFieldValidationStyle(idField, values[5], 5);
		});
		// Listener for idField
		phoneField.setOnKeyTyped(event -> {
			String text = phoneField.getText();
			values[6] = validateInput(text, "phone");
			if (values[6])
				phoneLbl.setText("");
			else
				phoneLbl.setText("10 digits");
			updateFieldValidationStyle(phoneField, values[6], 6);
		});
		roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
		    String selectedRole = newValue;
		    values[7] = true;
		    System.out.println("Selected Role: " + selectedRole);
		    // Perform any desired actions based on the selected role
		});
	}
	/**
	 * Registers a new user based on the input fields in the registration form.
	 * Retrieves the selected role from the roleComboBox and creates a new User object with the provided information.
	 * Sends a message to the server to register the user.
	 * Displays any error messages returned from the server.
	 */
	private void registerUser() {
		String role;
		String choice = roleComboBox.getValue();
		if(choice.equals("Student"))
			role = "student";
		else if(choice.equals("Lecturer"))
			role = "lecturer";
		else if(choice.equals("Head of Department"))
			role ="headDepartment";
		else {
			showError("You did not choose a role");
			return;
		}
		User userToRegister = new User(
				usernameField.getText(),
				lnameField.getText(),
				fnameField.getText(),
				passwordField.getText(),
				idField.getText(),
				emailField.getText(),
				phoneField.getText(),
				false,
				role
				);
		Message data_to_db = new Message(CONSTANTS.registerUser, userToRegister);
		try {
			CEMSClientUI.chat.accept(data_to_db);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Registration Simulation registerUser()");
		}
		showError(message);
	}
	/**
	 * Updates the visual style of the password field based on its validity.
	 * If the password is valid, sets the border color of the field to green; otherwise, sets it to red.
	 *
	 * @param field   The password field to update.
	 * @param isValid The validity status of the password.
	 * @param index   The index of the field (not used in this method).
	 */
	private void updatePasswordValidationStyle(PasswordField field, boolean isValid, int index) {
		if (isValid) {
			field.setStyle("-fx-border-color: green;");
		} else {
			field.setStyle("-fx-border-color: red;");
		}
	}
	/**
	 * Updates the visual style of the text field based on its validity.
	 * If the field value is valid, sets the border color of the field to green; otherwise, sets it to red.
	 *
	 * @param field   The text field to update.
	 * @param isValid The validity status of the field value.
	 * @param index   The index of the field (not used in this method).
	 */
	private void updateFieldValidationStyle(TextField field, boolean isValid, int index) {
		if (isValid) {
			field.setStyle("-fx-border-color: green;");
		} else {
			field.setStyle("-fx-border-color: red;");
		}
	}
	/**
	 * Displays an error message in the messageLabel.
	 *
	 * @param msg The error message to display.
	 */
	private void showError(String msg) {
		messageLabel.setText(msg);
	}
	/**
	 * Validates the input value based on the given type.
	 *
	 * @param input The input value to validate.
	 * @param type  The type of validation to perform.
	 * @return {@code true} if the input value is valid; {@code false} otherwise.
	 */
	private boolean validateInput(String input, String type) {
		switch (type) {
		case ("email"): {
			return Formatter.validateEmail(input);
		}
		case ("id"): {
			return Formatter.validateID(input);
		}
		case ("username"): {
			return Formatter.validateText(input);
		}
		case ("name"): {
			return Formatter.validateNameText(input);
		}
		case ("password"): {
			return Formatter.validatePassword(input);
		}
		case ("phone"): {
			return Formatter.validatePhoneNumber(input);
		}
		default: {
			return false;
		}
		}
	}
	/**
	 * Helper class containing various formatter patterns for input validation.
	 */
	private static class Formatter {
		private static final Pattern TEXT_PATTERN = Pattern.compile("[a-zA-Z0-9]+");
		private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@]+@[^@]+\\.[^@]+$");
		private static final Pattern NAME_TEXT_PATTERN = Pattern.compile("[A-Z][a-zA-Z]*");
		private static final Pattern ID_PATTERN = Pattern.compile("[0-9]{9}");
		private static final Pattern PASSWORD_PATTERN = Pattern
				.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,45}$");
		private static final Pattern PHONE_PATTERN = Pattern.compile("[0-9]{10}");
		/**
	     * Validates the input value as a text containing alphanumeric characters.
	     *
	     * @param input The input value to validate.
	     * @return {@code true} if the input value is a valid text; {@code false} otherwise.
	     */
		public static boolean validateText(String input) {
			return TEXT_PATTERN.matcher(input).matches();
		}
		 /**
	     * Validates the input value as an email address.
	     *
	     * @param input The input value to validate.
	     * @return {@code true} if the input value is a valid email address; {@code false} otherwise.
	     */
		public static boolean validateEmail(String input) {
			return EMAIL_PATTERN.matcher(input).matches();
		}
		/**
	     * Validates the input value as a text containing only uppercase and lowercase letters (starting with uppercase).
	     *
	     * @param input The input value to validate.
	     * @return {@code true} if the input value is a valid name text; {@code false} otherwise.
	     */
		public static boolean validateNameText(String input) {
			return NAME_TEXT_PATTERN.matcher(input).matches();
		}
		/**
	     * Validates the input value as an ID consisting of 9 digits.
	     *
	     * @param input The input value to validate.
	     * @return {@code true} if the input value is a valid ID; {@code false} otherwise.
	     */
		public static boolean validateID(String input) {
			return ID_PATTERN.matcher(input).matches();
		}
		/**
	     * Validates the input value as a password that meets certain complexity requirements.
	     *
	     * @param input The input value to validate.
	     * @return {@code true} if the input value is a valid password; {@code false} otherwise.
	     */
		public static boolean validatePassword(String input) {
			return PASSWORD_PATTERN.matcher(input).matches();
		}
		 /**
	     * Validates the input value as a phone number consisting of 10 digits.
	     *
	     * @param input The input value to validate.
	     * @return {@code true} if the input value is a valid phone number; {@code false} otherwise.
	     */
		public static boolean validatePhoneNumber(String input) {
			return PHONE_PATTERN.matcher(input).matches();
		}
	}
}
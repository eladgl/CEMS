/**
 * Controller class for the start test screen.
 * This class handles the user interface logic and actions related to starting a test.
*/

package studentUi;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Client.CEMSClient;
import Client.CEMSClientUI;
import clientGUI.ProfileScreenController;
import clientGUI.UserLoginController;
import entities.ConductExam;
import entities.Exam;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utilities.CONSTANTS;
import utilities.Message;

public class startTestScreenController {
	/**
	 * The list of relevant exams for the start test screen.
	 * This list contains ConductExam objects representing the exams available for the student to take.
	 * It is initially set to null and populated with the relevant exams retrieved from the server.
	 */
	public static ConductExam relevantExams = null;
	/**
	 * The currently selected ConductExam object for conducting the exam.
	 * This variable is used to store the selected exam from the relevantExams list for further processing.
	 * It is initially set to null and gets assigned a value when the student selects an exam to take.
	 */
	public static ConductExam exam_at_conduct = null;
	
	public static Exam exam = null;
	@FXML
	private Button back_arrow_image;

	@FXML
	private ToggleGroup TypeOfTest;
	
	@FXML
	private RadioButton CumputerizedRadioBtn, manualRadioBtn;

	@FXML
	private TextField IDTextField;

	@FXML
	private Button takeTestBtn;

	@FXML
	private Label errorLbl, howToDoTheTest, idInstructionLbl, idLbl, testLbl;

	@FXML
	private Button xButton;

	@FXML
	private TextField testCode;

	@FXML
	private Button submitBtn;

	/**
	 * Initializes the start test screen.
	 * This method is automatically called when the FXML file is loaded.
	 * It sets up the event handlers and populates the tests ComboBox with relevant exams.
	 */
	@FXML
	void initialize() {
		// add click on back img event
		back_arrow_image.setPickOnBounds(true);
		back_arrow_image.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClientUI.switchScenes(getClass().getResource("studentMainScreen.fxml"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		xButton.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClient.logout(studentMainScreenController.getUsername());
				CEMSClient.exitButton();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		});

		// get tests for ComboBox
		if (relevantExams != null)
			try {
				testLbl.setText(relevantExams.getConduct_exam_name());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	/**
	 * Handles the start test button action.
	 * This method is triggered when the user clicks the "Start Test" button.
	 * It checks which type of test (computerized or manual) is selected and calls the appropriate method to start the test.
	 *
	 * @param event The action event triggered by clicking the "Start Test" button.
	 */
	@FXML
	void startTest(ActionEvent event) {
	    Toggle selectedToggle = TypeOfTest.getSelectedToggle();
	    List<String> data = new ArrayList<>();
	    data.add(Integer.toString(relevantExams.getConduct_exam_id()));
	    data.add(UserLoginController.user.getUserName());
	    Message data_to_db = new Message(CONSTANTS.setStudenIsTakenExamForCurrectConductTestToOne, data);
		if (selectedToggle != null && selectedToggle.equals(CumputerizedRadioBtn)) {
	        // Computerized radio button is selected
			String id = IDTextField.getText();
			if(!checkInput(id, "\\d{9}")) {
				errorLbl.setText("Please insert a correct ID");
			}
			else if(!id.equals(UserLoginController.user.getIDNumber())) {
				errorLbl.setText("The ID you have entered does not match\nYour profile ID");
			}
			else {
				try {
					CEMSClientUI.chat.accept(data_to_db);
					startComputerizedTest();
				} catch (Exception e) {
					showError("Error starting computerized test");
					e.printStackTrace();
				} 
				
			}
				
	    } else if (selectedToggle != null && selectedToggle.equals(manualRadioBtn)) {
	        // Manual radio button is selected
	    	try {
				CEMSClientUI.chat.accept(data_to_db);
				startManualTest();
			} catch (Exception e) {
				showError("Error starting manual test");
				e.printStackTrace();
			}
	    } else {
	        // No radio button is selected
	        errorLbl.setText("Please choose a type of test");
	    }
	}
	/**
	 * Starts a computerized test.
	 * This method is called when the user selects the computerized test option and clicks the "Start Test" button.
	 * It changes the screen to the computerized test exam screen.
	 */
	private void startComputerizedTest() {
		try {
			CEMSClientUI.switchScenes(getClass().getResource("ComputerizedTestExamScreen.fxml"));
		} catch (IOException e) {
			showError("Error loading computerized test screen");
			e.printStackTrace();
		}
	}
	/**
	 * Starts a manual test.
	 * This method is called when the user selects the manual test option and clicks the "Start Test" button.
	 * It changes the screen to the manual test exam screen.
	 */
	private void startManualTest() {
		try {
			CEMSClientUI.switchScenes(getClass().getResource("ManualTestExamScreen.fxml"));
		} catch (IOException e) {
			showError("Error loading manual test screen");
			e.printStackTrace();
		}
	}
	/**
	 * Submits the test code entered by the user.
	 * This method is called when the user clicks the "Submit" button after entering the test code.
	 * It checks the validity of the entered code and sends a message to the database to check if the password matches and the test file exists.
	 * and then retrieves the relevant conduct test 
	 * @param event The action event triggered by clicking the "Submit" button.
	 */
	@FXML
	void submitCode(ActionEvent event) {
		if (relevantExams != null) {
			String username = ProfileScreenController.user.getUserName();
			String password = testCode.getText();
			if (checkInput(password, "\\d{4}") == false) {
				errorLbl.setText("Please enter a 4 digit code");
				hideElements();
				return;
			}
			int conduct_exam_id = relevantExams.getConduct_exam_id();
			// organize data
			List<String> data = new ArrayList<>();
			data.add(username);
			data.add(password);
			data.add(Integer.toString(conduct_exam_id));
			// send message to db
			Message data_to_db = new Message(CONSTANTS.checkIfPasswordMatchesAndTheTestFile, data);
			try {
				CEMSClientUI.chat.accept(data_to_db);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (exam_at_conduct == null) {
				showError("Password incorrect - contact your lecturer");
				hideElements();
			} else {
				showElements();
				errorLbl.setText("");
				// add the correct exam from db and save it to the static Exam instance
				data_to_db = new Message(CONSTANTS.getTestInformationAtConduct, exam_at_conduct);
				try {
					CEMSClientUI.chat.accept(data_to_db);
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (exam == null) {
					showError("Exam could not load");
				}
			}
		}
		else {
			showError("You have no conduct exam yet");
		}
	}

	/**
	 * Hides the computerized components in the UI. This method is typically
	 * triggered by an ActionEvent. It calls the {@link #hideComputerizedElements()}
	 * method to hide specific elements.
	 *
	 * @param event The ActionEvent that triggered the method.
	 */
	@FXML
	void hideComputerizedComponents(ActionEvent event) {
		errorLbl.setText("");
		hideComputerizedElements();
	}

	/**
	 * Shows the computerized components in the UI. This method is typically
	 * triggered by an ActionEvent. It sets the visibility of the instruction label,
	 * ID label, and ID text field to true.
	 *
	 * @param event The ActionEvent that triggered the method.
	 */
	@FXML
	void showComputerizedComponents(ActionEvent event) {
		idInstructionLbl.setVisible(true);
		idLbl.setVisible(true);
		IDTextField.setVisible(true);
	}
	
	
	/**
	 * Shows an error message in the UI.
	 *
	 * @param msg The error message to be displayed.
	 */
	private void showError(String msg) {
		errorLbl.setVisible(true);
		errorLbl.setText(msg);
	}
	/**
	 * Shows the UI elements associated with the test. This includes showing the
	 * manual radio button, computerized radio button, take test button, manual
	 * button, computerized button, and the "how to do the test" label.
	 */
	private void showElements() {
		manualRadioBtn.setVisible(true);
		CumputerizedRadioBtn.setVisible(true);
		takeTestBtn.setVisible(true);
		howToDoTheTest.setVisible(true);
	}

	/**
	 * Hides the UI elements associated with the test. This includes hiding the
	 * manual radio button, computerized radio button, take test button, manual
	 * button, computerized button, "how to do the test" label, and untoggling the
	 * type of test. Additionally, it calls the hideComputerizedElements() method to
	 * hide the computerized-specific elements.
	 */
	private void hideElements() {
		manualRadioBtn.setVisible(false);
		CumputerizedRadioBtn.setVisible(false);
		takeTestBtn.setVisible(false);
		howToDoTheTest.setVisible(false);
		TypeOfTest.selectToggle(null);
		hideComputerizedElements();
	}

	/**
	 * Checks if the input is a valid not null value.
	 *
	 * @param input The input string to be checked.
	 * @param regex The regular expression pattern to match against.
	 * @return {@code true} if the input is a valid 4-digit number, {@code false}
	 *         otherwise.
	 */
	private boolean checkInput(String input, String regex) {
		// Check if the input is not null and matches the pattern
		if (input != null && input.matches(regex))
			return true;
		return false;
	}

	/**
	 * Hides the computerized elements in the UI. This includes hiding the
	 * instruction label, ID label, and ID text field.
	 */
	private void hideComputerizedElements() {
		idInstructionLbl.setVisible(false);
		idLbl.setVisible(false);
		IDTextField.setVisible(false);
	}

}

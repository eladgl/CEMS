package studentUi;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;

import Client.CEMSClient;
import Client.CEMSClientUI;
import clientGUI.ScreenNames;
import clientGUI.UserLoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import utilities.CONSTANTS;
import utilities.Message;
import entities.StudentsDataInExam;
import entities.SMSMessage;
/**
 * Controller class for the SMS screen in the student UI.
 * This class handles the display of SMS messages in a table view and provides event handlers for navigation and exit.
 */
public class SmsScreenController {
	public static ObservableList<SMSMessage> smsList = FXCollections.observableArrayList();
	public static List<StudentsDataInExam> tempList = new ArrayList<>();

	
	@FXML
	private TableView<SMSMessage> messageTable;

	@FXML
	private TableColumn<SMSMessage, String> messageColumn;

	@FXML
	private TableColumn<SMSMessage, String> timeStampColumn;
	/**
     * Initializes the SMS screen.
     * Clears the SMS list, sets up the table view, and retrieves SMS messages.
     */
	@FXML
	void initialize() {
		smsList.clear();
		tempList.clear();
		stopTheTimerThread();
		messageTable.setItems(smsList);
		messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
		timeStampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

		setUpTable();
		getSMS();
		
	}
	/**
     * Event handler for the back arrow image click.
     * Navigates back to the student main screen.
     *
     * @param e the MouseEvent object
     */
	@FXML
	public void backArrowImageClicked(MouseEvent e) {
		try {
			stopTheTimerThread();
			CEMSClientUI.switchScenes(getClass().getResource(ScreenNames.StudentMainScreen));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	/**
     * Event handler for the X button click.
     * Logs out the user and exits the application.
     *
     * @param e the MouseEvent object
     */
	@FXML
	public void XButtonClicked(MouseEvent e) {
		try {
			stopTheTimerThread();
			CEMSClient.logout(studentMainScreenController.getUsername());
			CEMSClient.exitButton();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.exit(0);
	}
	/**
     * Sets up the SMS table.
     * Retrieves SMS messages and adds them to the table view.
     */
	private void setUpTable() {
		Message data_to_db = new Message(CONSTANTS.getStudentGradeSMS_Simulation,
				UserLoginController.user.getUserName());
		SMSMessage temp;
		try {
			CEMSClientUI.chat.accept(data_to_db);
			if (smsList != null) {
				for (SMSMessage studentPersonalizedMsg : smsList) {
					temp = new SMSMessage(studentPersonalizedMsg.getMessage());
					if (smsList.contains(temp))
						continue;
					smsList.add(temp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error in SmsScreenController");
		}
	}
	
	/**
     * Thread class that checks for time changes and updates the SMS table.
     */
	private class CheckForTimeChange extends Thread {
	    private volatile boolean running = true;

	    @Override
	    public void run() {
	        while (running) {
	            setUpTable();
	            try {
	                Thread.sleep(8000); // Wait for 8 seconds
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    /**
         * Stops the running thread.
         */
	    public void stopRunning() {
	        running = false;
	    }
	}

	private CheckForTimeChange timer;
	/**
     * Retrieves SMS messages and starts the timer thread.
     */
	private void getSMS() {
	    timer = new CheckForTimeChange();
	    timer.start();
	}
	/**
     * Stops the timer thread.
     */
	private void stopTheTimerThread() {
	    if (timer != null) {
	        timer.stopRunning();
	    }
	}

}
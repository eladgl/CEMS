package studentUi;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import Client.CEMSClientUI;
import clientGUI.ScreenNames;
import entities.Exam;
import entities.ExamTimeChangeObject;
import entities.ExamType;
import entities.ManualExamConductObject;
import entities.Question;
import entities.StudentsDataInExam;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import utilities.CONSTANTS;
import utilities.Message;

/**
 *
 * This keeps track of the current operation to do. DOWNALOD_SCREEN is for
 * downloading the word file Upload_screen is for uploading the file.
 *
 */
enum ExamScreens {
	DOWNLOAD_SCREEN, UPLOAD_SCREEN;

	/*
	 * you can get the next element in the enum (current_screen.next() =
	 * UPLOAD_SCREEN)
	 */

	private static final ExamScreens[] screens = values();

	public ExamScreens next() {
		return screens[(this.ordinal() + 1) % screens.length];
	}

}

/**
 *
 * The Class Handles the Manual Test Screen Operations.
 *
 */
public class ManualTestExam {

	@FXML
	private Label timerLabel;

	@FXML
	private Button next_button;

	@FXML
	private Label no_selected_file_label;

	@FXML
	private Label current_screen_label;

	@FXML
	private Circle first_circle;

	@FXML
	private Circle second_circle;

	@FXML
	private Line line_between_circles;
	
	@FXML private ImageView correct_image_popup;

	/*
	 * Holds the Download/Upload Shapes
	 */
	@FXML
	private AnchorPane operation_container;

	private Timeline timeline;
	private boolean has_timer_reached_zero_before_flag = false;
	private boolean has_timer_started_flag = false;
	private boolean has_circle_been_clicked = false;
	private LocalTime exam_start_time = null;

	private String DOWNLOAD_DESTINATION = null;
	private String UPLOAD_DESTINATION = null;
	private final String WORD_FILE_NAME_EXTENTION = "EXAM.docx";
	private FileChooser fileChooser;
	private ExamScreens current_screen = ExamScreens.DOWNLOAD_SCREEN;
	public static int timeDifferenceToShowUser = 0;
	private static int exam_time_in_seconds = 14;
	private static Object exam_time_lock = new Object(); // locks the exam_time_in_seconds to prevent over-write

	public static Exam exam = null;
	public static boolean uploaded_file_successfully_to_the_db = false;
	public static int timeDifferenceForThread = 0;
	public static boolean trigger_additional_time_alert = false;
	public static boolean trigger_exam_is_locked_alert = false;

	private static CheckForTimeChange checkTimeThread;
	private SendStudentDataInExamThread sendDataExamThread;
	private static CheckIfExamIsLockThread checkIfExamIsLockedThread;

	@FXML
	public void initialize() {

		exam = startTestScreenController.exam;
		if(exam == null) {
			try {
				changeScreens(ScreenNames.StudentMainScreen);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		
		exam_time_in_seconds = (int) exam.getDuration().getSeconds();
		updateTimerLabel();
		startCheckingForTimeChangeUsingThread();
		startCheckingIfExamIsLockedUsingThread();
	}

	/**
	 * This method is only called from the CEMSCLIENT , to increase the time of the
	 * test.
	 * 
	 * @param duration_in_seconds how many seconds the teacher added to the test.
	 *
	 */

	public static void postponeExamTime(int duration_in_seconds) {
		if (duration_in_seconds <= 0)
			return;
		synchronized (exam_time_lock) {
			exam_time_in_seconds += duration_in_seconds;
		}
		timeDifferenceToShowUser = duration_in_seconds;
		trigger_additional_time_alert = true;
		// createAlertForAdditionalTimeAdded(duration_in_seconds); THIS IS A BUG
	}

	/*
	 * Show An alert PopUP confirmation with a yes and no
	 */
	@FXML
	public void BackArrowAction(MouseEvent e) {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to quit the test?");

		ButtonType buttonYes = new ButtonType("Yes"); // Set the buttons for Yes and No
		ButtonType buttonNo = new ButtonType("No");
		alert.getButtonTypes().setAll(buttonYes, buttonNo);

		alert.showAndWait().ifPresent(response -> {
			if (response == buttonNo) {
				alert.close();
				return;
			}
			try {
				changeScreens(ScreenNames.StudentMainScreen); // don't change this method (go to method to see why)
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});
	}

	/**
	 * Changes the label to upload and hides items
	 * 
	 * @param e EVENT
	 */
	@FXML
	public void nextButtonAction(ActionEvent e) {
		if (!has_timer_started_flag) {
			no_selected_file_label.setText("Please Download The Exam!");
			return;
		}

		if (current_screen == ExamScreens.DOWNLOAD_SCREEN) {
			next_button.setVisible(false);
			no_selected_file_label.setText("");
			current_screen_label.setText("Upload");
			current_screen = current_screen.next();
			second_circle.setFill(Color.DODGERBLUE);
		}
	}	
	
	// TRIED TO FIX IT
	@FXML
	public void ClickedOnTheSecondCircle(MouseEvent e) {
		if(has_circle_been_clicked)
			return;
		
		if(! has_timer_started_flag) {
			no_selected_file_label.setText("Please Download The Exam!");
			return;
		}
		has_circle_been_clicked = true;

		if(current_screen == ExamScreens.DOWNLOAD_SCREEN) {
			next_button.setVisible(false);
			no_selected_file_label.setText("");
			current_screen_label.setText("Upload");
			current_screen = current_screen.next();
			second_circle.setFill(Color.DODGERBLUE);
		}
		
	}
	



	/**
	 * if currently on the download screen and you clicked on download, perform the
	 * download operation. if currently on the upload screen and you clicked on
	 * upload, perform the upload operation
	 * 
	 * @param e Mouse Event (not needed)
	 */

	@FXML
	public void performTheCurrentScreenActionIfClicked(MouseEvent e) {
		chooseFileLocationAction();
		if (current_screen == ExamScreens.DOWNLOAD_SCREEN) {
			downloadFileAction();
		} else {
			uploadFileAction();
		}
	}

	private void chooseFileLocationAction() {
		if (current_screen == ExamScreens.DOWNLOAD_SCREEN) {
			chooseFileLocationForDownalod();
		} else {
			chooseFileLocationForUpload();
		}
	}

	/**
	 * When Clicking on the Download Button , It will download the file and open it.
	 *
	 * @param e - mouse Event
	 */

	private void downloadFileAction() {
		if (DOWNLOAD_DESTINATION == null || exam == null) {
			no_selected_file_label.setText("Choose a file");
			return;
		}

		generateWordFile(DOWNLOAD_DESTINATION);
		openFile(DOWNLOAD_DESTINATION);

		exam_start_time = LocalTime.now(); // set the exam start time for the student.
	}

	/**
	 * Generates a Word File based on the file_destination. and then opens it.
	 * 
	 * @param file_destination file destination in the computer.
	 */

	private void generateWordFile(String file_destination) {
		ArrayList<Question> exam_questions = exam.getQuestions();

		XWPFDocument document = new XWPFDocument();
		try {
			FileOutputStream out = new FileOutputStream(file_destination);

			// Add description
			XWPFParagraph descriptionParagraph = document.createParagraph();
			descriptionParagraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRun descriptionRun = descriptionParagraph.createRun();
			descriptionRun.setBold(true);
			descriptionRun.setText("Description: " + exam.getStudentDescription() + "\n\n");
			descriptionRun = descriptionParagraph.createRun();

			// Add time required
			XWPFParagraph timeParagraph = document.createParagraph();
			timeParagraph.setAlignment(ParagraphAlignment.LEFT);
			XWPFRun timeRun = timeParagraph.createRun();
			timeRun.setBold(true);
			timeRun.setText("Time Required: ");
			timeRun = timeParagraph.createRun();

			synchronized (exam_time_lock) {

				timeRun.setText(String.format(" %d hours, %02d minutes, seconds  %02d", exam_time_in_seconds / 3600,
						(exam_time_in_seconds % 3600) / 60, (exam_time_in_seconds % 60)) + "\n\n");
			}

			// Add questions
			for (int i = 0; i < exam_questions.size(); i++) {
				Question question = exam_questions.get(i);

				XWPFParagraph questionParagraph = document.createParagraph();
				questionParagraph.setAlignment(ParagraphAlignment.LEFT);

				// Add question number and description
				String questionText = "Question " + (i + 1) + ": " + question.getQuestionDescription()
						+ ",     Question Score: " + String.valueOf(exam.getQuestionsScores().get(i) + "\n");
				XWPFRun questionRun = questionParagraph.createRun();
				questionRun.setBold(true);
				questionRun.setText(questionText);

				// Add choices with descriptions
				for (int j = 0; j < question.getOptionsText().length; j++) {
					String choiceText = (j + 1) + ". " + (question.getOptionsText()[j]);
					questionParagraph = document.createParagraph();
					questionRun = questionParagraph.createRun();
					questionRun.setText(choiceText);
				}

				// Add correct answer
				String answerText = "Correct Answer: ________\n\n";
				questionParagraph = document.createParagraph();
				questionRun = questionParagraph.createRun();
				questionRun.setBold(true);
				questionRun.setText(answerText);
			}

			// Add "Thank You" message
			XWPFParagraph thankYouParagraph = document.createParagraph();
			thankYouParagraph.setAlignment(ParagraphAlignment.CENTER);
			XWPFRun thankYouRun = thankYouParagraph.createRun();
			thankYouRun.setBold(true);
			thankYouRun.setFontSize(14);
			thankYouRun.setText("\n\nThank You!");

			// Save the document
			document.write(out);
			out.close();
			document.close();
			System.out.println("Word file generated successfully!");

			first_circle.setFill(Color.GREEN);
			line_between_circles.setStroke(Color.GREEN);
			// adding the Correct Image On The Circle
			
			if(this.getClass().getResourceAsStream("/images/checked.png") != null) {
	        	Image image = new Image(this.getClass().getResourceAsStream("/images/checked.png"));
	        	correct_image_popup.setImage(image);
	        }else {
	        	correct_image_popup.setVisible(false);
	        }
			
			initializeTimerThread();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Uploading the file to the db.
	 */
	private void uploadFileAction() {

		// send the file that the user wants to upload to the DataBase.

		ManualExamConductObject manual_exam_object = new ManualExamConductObject(
				studentMainScreenController.user.getIDNumber(),
				startTestScreenController.exam_at_conduct.getConduct_exam_id(), new File(UPLOAD_DESTINATION));

		Message file_to_db = new Message(CONSTANTS.UploadStudentExamWordFileSolution, manual_exam_object);

		try {
			CEMSClientUI.chat.accept(file_to_db);
		} catch (Exception e) {
			no_selected_file_label.setText("Failed to uplead file. Try Again");
			e.printStackTrace();
			return;
		}
		if (uploaded_file_successfully_to_the_db) {

			startSendDataExamThread(); // send the Data To the DB After Upload Succeeded
			System.out.println("Upload done!");
			second_circle.setFill(Color.GREEN);

			createAlertForExamHasFinished();
		}

	}

	private void chooseFileLocationForDownalod() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose a folder");

		File selectedDirectory = directoryChooser.showDialog(CEMSClientUI.primary_stage);

		if (selectedDirectory != null) {
			DOWNLOAD_DESTINATION = selectedDirectory.getAbsolutePath() + "\\" + WORD_FILE_NAME_EXTENTION;
			operation_container.setVisible(true);
		}
	}

	private void chooseFileLocationForUpload() {
		// initialize file_chooser to pick only word files (ending with docx)

		fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word Files", "*.docx"));
		File selectedFile = fileChooser.showOpenDialog(CEMSClientUI.primary_stage);
		if (selectedFile != null) {
			UPLOAD_DESTINATION = selectedFile.getAbsolutePath();
			operation_container.setVisible(true);
		}
	}

	private static void openFile(String filePath) {
		try {
			File file = new File(filePath);
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			System.out.println("Error opening file: " + e.getMessage());
		}
	}

	/**
	 * This Method is triggered every second by the animation thread. Handle the
	 * time decrease animation , and alert manegment.
	 * 
	 * @param event
	 */
	private void decreaseTimer(ActionEvent event) {

		if (trigger_additional_time_alert) {
			Platform.runLater(this::createAlertForAdditionalTimeAdded);
			trigger_additional_time_alert = false;
			StopCheckingTimeThread(); // stop the thread.
		}
		synchronized (exam_time_lock) {
			
			if (exam_time_in_seconds > 0) {
				exam_time_in_seconds--;
				updateTimerLabel();
			}
			if (exam_time_in_seconds == 0) {
				if (has_timer_reached_zero_before_flag) {
					timeline.stop();
					Platform.runLater(this::createAlertForExamHasFinished); // Schedule alert to run on a the
																			// application thread.
				} else {
					exam_time_in_seconds += 60; // Increase remainingSeconds by 60 seconds (one minute)
					has_timer_reached_zero_before_flag = true;
					Platform.runLater(this::createAlertMessageForUploadingExam); // Schedule alert to run on the JavaFX
																					// Application Thread
				}
			}
		}
	}

	/**
	 * If the exam was locked then send the student info to the DB + Switch Screens
	 */
	private void handleExamIsLocked() {

		// send the exam data to db
		startSendDataExamThread();

		// send an empty word file to db.
		ManualExamConductObject manual_exam_object = new ManualExamConductObject(
				studentMainScreenController.user.getIDNumber(),
				startTestScreenController.exam_at_conduct.getConduct_exam_id(), null);

		Message file_to_db = new Message(CONSTANTS.UploadStudentExamWordFileSolution, manual_exam_object);

		try {
			CEMSClientUI.chat.accept(file_to_db);
		} catch (Exception e) {
			no_selected_file_label.setText("Failed to uplead file. Try Again");
			e.printStackTrace();
		}

	}

	private void updateTimerLabel() {
		int hours = exam_time_in_seconds / 3600;
		int minutes = (exam_time_in_seconds % 3600) / 60;
		int seconds = exam_time_in_seconds % 60;

		String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		timerLabel.setText(formattedTime);
	}

	/**
	 * Starts the timer thread, with relevant methods to decrease the time.
	 */
	private void initializeTimerThread() {
		if (has_timer_started_flag)
			return;

		// Create A timer animation
		timeline = new Timeline(new KeyFrame(Duration.seconds(1), this::decreaseTimer));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
		has_timer_started_flag = true;
	}

	private void createAlertMessageForUploadingExam() {
		Alert reminderAlert = new Alert(AlertType.INFORMATION);
		reminderAlert.setTitle("Reminder");
		reminderAlert.setHeaderText("One Minute Left");
		reminderAlert.setContentText("Upload File Now!");

		// Show the reminder alert
		reminderAlert.showAndWait();
	}

	private void createAlertForExamHasFinished() {
		Alert reminderAlert = new Alert(AlertType.INFORMATION);
		reminderAlert.setTitle("Message");
		reminderAlert.setHeaderText("Exam Finished");
		reminderAlert.setContentText("We Wish you a good day!");

		// Show the reminder alert
		reminderAlert.showAndWait();
		try {
			changeScreens(ScreenNames.StudentMainScreen);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createAlertForAdditionalTimeAdded() {
		Alert reminderAlert = new Alert(AlertType.INFORMATION);
		reminderAlert.setTitle("Important Message");
		reminderAlert
				.setHeaderText("The Teacher Added : " + String.valueOf(timeDifferenceToShowUser / 60) + "minutes.\n");
		reminderAlert.setContentText("Good Luck !");

		// Show the reminder alert after the animation frame is done
		reminderAlert.showAndWait();
	}

	public void createAlertForExamIsLocked() {
		handleExamIsLocked(); // send the relevant data to db and exit the exam.
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Locked Exam");
		alert.setHeaderText("The Exam Has Been Locked");
		alert.setContentText(
				"We apologies for the inconvenience, but the Exam\nHas been locked by the lecturer.\nPlease contact "
						+ "your lecturer for more information\nHave a nice day!");

		// Load and set a custom icon
		Image image;
		ImageView blueLockImageviewIcon;
		if (this.getClass().getResourceAsStream("/images/blue_lock.png") != null) {
			image = new Image(this.getClass().getResourceAsStream("/images/blue_lock.png"));
			blueLockImageviewIcon = new ImageView(image);
		} else {
			blueLockImageviewIcon = new ImageView();
		}
		alert.setGraphic(blueLockImageviewIcon);

		// Apply custom style
		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("/resources/custom-alert.css").toExternalForm());
		dialogPane.getStyleClass().add("custom-alert");

		// Adjust the size of the alert
		dialogPane.setMinHeight(Region.USE_PREF_SIZE);

		// Show the alert
		alert.showAndWait();

		try {
			changeScreens(ScreenNames.StudentMainScreen);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * DO NOT REMOVE THIS METHOD BECAUSE ITS RESPONSIBLE FOR STOPING THE THREAD SO
	 * DO NOT DELETE OR EDIT THIS METHOD
	 */
	private void changeScreens(String screen_name) throws IOException {
		resetControllerFieldsWhenExitScreen();
		StopCheckingTimeThread();
		stopExamLockedThread();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(screen_name));
		Parent root = loader.load();

		Stage stage = CEMSClientUI.primary_stage;
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	private void resetControllerFieldsWhenExitScreen() {
		has_timer_reached_zero_before_flag = false;
		has_timer_started_flag = false;
		if (timeline != null)
			timeline.stop();
	}

	private void startSendDataExamThread() {
		final int grade = 0;
		final int time_addition_to_test_in_minutes = timeDifferenceToShowUser / 60;
		sendDataExamThread = new SendStudentDataInExamThread(grade, null, "manual", exam, exam_start_time,
				time_addition_to_test_in_minutes);
		sendDataExamThread.start();
	}

	private void startCheckingForTimeChangeUsingThread() {
		checkTimeThread = new CheckForTimeChange();
		checkTimeThread.start(); // Start the thread
	}

	private void startCheckingIfExamIsLockedUsingThread() {
		checkIfExamIsLockedThread = new CheckIfExamIsLockThread(
				startTestScreenController.relevantExams.getConduct_exam_id(), ExamType.MANUAL);

		checkIfExamIsLockedThread.start();
	}

	/**
	 * Stop the time checking thread . if it is still running.
	 */
	public static void StopCheckingTimeThread() {
		if (checkTimeThread != null) {
			checkTimeThread.stopRunning(); // Stop the thread
			/*
			 * try { checkTimeThread.join(); // Wait for the thread to finish (Dont Think
			 * Its important) } catch (InterruptedException e) { e.printStackTrace(); }
			 */

			checkTimeThread = null;
		}
	}

	public static void stopExamLockedThread() {
		if (checkIfExamIsLockedThread == null)
			return;
		checkIfExamIsLockedThread.stopRunning();
		checkIfExamIsLockedThread = null;
	}

	/**
	 * This Class Handles the Thread Behavior. Checks Every 15 seconds whether the
	 * time inside conduct_exam has been changed. if It was changed the thread
	 * triggers flags that will increase the time and display a message.
	 * 
	 * @author razi
	 *
	 */
	private class CheckForTimeChange extends Thread {
		private volatile boolean running = true; // all threads see the most up-to-date value.
		private int init_exam_time = (int) exam.getDuration().getSeconds(); // original test duration


		@Override
		public void run() {

			try {
				Thread.sleep(10000); // Wait for 10 seconds before starting to check for time change
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			while (running) {
				// updateInitTime();

				ExamTimeChangeObject timeChange = new ExamTimeChangeObject(init_exam_time,
						startTestScreenController.relevantExams.getConduct_exam_id(), ExamType.MANUAL);

				try {
					CEMSClientUI.chat.accept(new Message(CONSTANTS.CheckForExamTimeChange, timeChange));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if (trigger_additional_time_alert)// the time has been changed
					break;

				try {
					Thread.sleep(15000); // Wait for 15 seconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void stopRunning() {
			running = false;
		}

	}

}
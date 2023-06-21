package studentUi;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import Client.CEMSClientUI;
import entities.Exam;
import entities.ExamTimeChangeObject;
import entities.ExamType;
import entities.Question;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import utilities.CONSTANTS;
import utilities.Message;
/**
 * Controller class for the ComputerizedTestScreen.
 */

public class ComputerizedTestScreenController {

	private int currentQuestionIndex = 0;
	ArrayList<Question> questionsForTest = new ArrayList<>();
	ArrayList<String> userAnswers = new ArrayList<>();
	ArrayList<String> correct_questions_answers = new ArrayList<>();
	private LocalTime exam_start_time = null;
	private SendStudentDataInExamThread sendDataExamThread;
	/**
	 * The thread used for checking if the exam is locked.
	 */
	private static CheckIfExamIsLockThread checkIfExamIsLockedThread;
	/**
	 * The instance of the ComputerizedTestScreenController class.
	 */
	private static ComputerizedTestScreenController instance;
	private Timeline timeline;
	/**
	 * The remaining time in seconds for the exam.
	 */
	private static int remainingSeconds;
	/**
	 * The Exam object representing the current exam.
	 */
	public static Exam exam = null;
	/**
	 * The time difference in seconds to show to the user.
	 */
	private static int timeDifferenceToShowUser = 0;
	/**
	 * The lock object for synchronizing access to the exam time.
	 */
	private static Object exam_time_lock = new Object();
	/**
	 * The time difference in seconds for the thread.
	 */
	public static int timeDifferenceForThread = 0;
	/**
	 * The thread used for checking for time changes in the exam.
	 */
	private static CheckForTimeChange checkTimeThread;
	/**
	 * A flag indicating whether an additional time alert should be triggered.
	 */
	public boolean trigger_additional_time_alert = false;
	/**
	 * A flag indicating whether an exam locked alert should be triggered.
	 */
	public static boolean trigger_exam_is_locked_alert = false;

	@FXML
	private TextArea answer1, answer2, answer3, answer4;

	@FXML
	private ToggleGroup answers;

	@FXML
	private Button back_arrow_image;

	@FXML
	private Label points;

	@FXML
	private TextArea question;

	@FXML
	private Text questionNumber;

	@FXML
	private RadioButton r1,r2 ,r3, r4;

	@FXML
	private Label subject, timer;

	@FXML
	private Text totalNumberOfQuestions;
	/**
     * Sets the instance of the Controller class.
     *
     * @param controller The Controller instance to set.
     */
	public static void setInstance(ComputerizedTestScreenController controller) {
		instance = controller;
	}
	/**
     * Retrieves the instance of the Controller class.
     *
     * @return The Controller instance.
     */
	public static ComputerizedTestScreenController getInstance() {
		return instance;
	}
	/**
     * Initializes the controller.
     */
	@FXML
	void initialize() {
		instance = this;
		exam = startTestScreenController.exam;
		questionsForTest = exam.getQuestions();
		subject.setText(exam.getTestName());
		totalNumberOfQuestions.setText(String.valueOf(questionsForTest.size()));
		remainingSeconds = (int) exam.getDuration().getSeconds();

		// timer
		updateTimerLabel();
		// Create A timer animation
		timeline = new Timeline(new KeyFrame(Duration.seconds(1), this::decreaseTimer));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

		showQuestion();
		initializeUserAnswers();

		for (Question q : exam.getQuestions()) {
			correct_questions_answers.add(String.valueOf(q.getCorrectAnswer()));
		}
		exam_start_time = LocalTime.now();
		startCheckingForTimeChangeUsingThread();
		startCheckingIfExamIsLockedUsingThread();
	}
	/**
     * Handles the action when the back arrow is clicked.
     *
     * @param e The MouseEvent that triggered the action.
     */
	@FXML
	public void BackArrowAction(MouseEvent e) {
		showAlert("Confirmation", "Are you sure you want to quit the test?");
	}
	/**
     * Handles the backward button action.
     *
     * @param event The ActionEvent that triggered the action.
     */
	@FXML
	void backward(ActionEvent event) {
		if (currentQuestionIndex == 0)
			return;
		checkSelectedAnswer();
		currentQuestionIndex--;
		showQuestion();
		showSavedUserAnswer();
	}
	/**
     * Handles the next button action.
     *
     * @param event The ActionEvent that triggered the action.
     */
	@FXML
	void next(ActionEvent event) {
		if (currentQuestionIndex == questionsForTest.size() - 1)
			return;
		checkSelectedAnswer();
		currentQuestionIndex++;
		showQuestion();
		showSavedUserAnswer();
	}
	/**
	 * Handles the action when the test is submitted.
	 *
	 * @param event The ActionEvent that triggered the action.
	 */
	@FXML
	void submitTest(ActionEvent event) {
		submit("Not Force Quit");
	}
	/**
	 * Submits the test.
	 *
	 * @param mode The mode in which the test is being submitted.
	 */
	private void submit(String mode) {
		checkSelectedAnswer();
		Alert alert;
		for (int i = 0; i < questionsForTest.size(); i++) {
			if (userAnswers.get(i) == "-1") {
				alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Submit test");
				alert.setHeaderText(null);
				alert.setContentText("There are unanswered questions, are you sure you want to submit?");

				// Set the buttons for Yes and No
				ButtonType buttonYes = new ButtonType("Yes");
				ButtonType buttonNo = new ButtonType("No");
				alert.getButtonTypes().setAll(buttonYes, buttonNo);

				// Show the dialog and wait for the user's response
				alert.showAndWait().ifPresent(response -> {
					if (response == buttonNo) {
						alert.close();
						return;
					}
					try {
						startSendDataExamThread();
						timeline.stop();
						checkIfExamIsLockedThread.running=false;
						changeScreens("studentMainScreen.fxml");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				});
				return;
			}
		}
		
		showAlert("Submit test", "Are you sure you want to submit?");
		startSendDataExamThread(); // send Computerized Data To DB.
	}
	/**
	 * Displays the test description in an alert dialog.
	 *
	 * @param event The ActionEvent that triggered the action.
	 */
	@FXML
	void descriptionBTN(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Test Description");
		alert.setHeaderText(null);
		alert.setContentText(exam.getStudentDescription());

		ButtonType buttonClose = new ButtonType("close");
		alert.getButtonTypes().setAll(buttonClose);

		// Show the dialog and wait for the user's response
		alert.showAndWait().ifPresent(response -> {
			if (response == buttonClose) {
				alert.close();
				return;
			}
		});
	}
	/**
	 * Creates an alert dialog to inform the user that the exam has been locked.
	 * Performs necessary actions and redirects to the main screen.
	 */
	public void createAlertForExamIsLocked() {
		handleExamIsLocked();
		checkSelectedAnswer();
		startSendDataExamThread(); // send the relevant data to db and exit the exam.
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
		} else 
			blueLockImageviewIcon = new ImageView();
		
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
			changeScreens("studentMainScreen.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Starts a thread to check if the exam is locked.
	 * Uses a separate thread to prevent blocking the main thread.
	 */
	private void startCheckingIfExamIsLockedUsingThread() {
		checkIfExamIsLockedThread = new CheckIfExamIsLockThread(
				startTestScreenController.relevantExams.getConduct_exam_id(), ExamType.COMPUTERIZED);
		checkIfExamIsLockedThread.start();
	}
	/**
	 * Stops the exam locked thread if it is running.
	 */
	public static void stopExamLockedThread() {
		if (checkIfExamIsLockedThread == null)
			return;
		checkIfExamIsLockedThread.stopRunning();
		checkIfExamIsLockedThread = null;
	}
	/**
	 * Starts a thread to send the exam data to the server.
	 * Uses a separate thread to prevent blocking the main thread.
	 */
	private void startSendDataExamThread() {
		sendDataExamThread = new SendStudentDataInExamThread(calculateGrade(), userAnswers, "computerized", exam,
				exam_start_time, timeDifferenceToShowUser);
		sendDataExamThread.start();
	}
	/**
	 * Initializes the userAnswers list with default values.
	 * Each element is set to "-1" indicating no answer has been selected.
	 */
	private void initializeUserAnswers() {
		for (int i = 0; i < questionsForTest.size(); i++) {
			userAnswers.add("-1");
		}
	}
	/**
	 * Checks which answer option is selected and updates the userAnswers list accordingly.
	 * Called when the user selects an answer for a question.
	 */
	private void checkSelectedAnswer() {
		if (r1.isSelected()) {
			userAnswers.set(currentQuestionIndex, "1");
			return;
		}
		if (r2.isSelected()) {
			userAnswers.set(currentQuestionIndex, "2");
			return;
		}
		if (r3.isSelected()) {
			userAnswers.set(currentQuestionIndex, "3");
			return;
		}
		if (r4.isSelected()) {
			userAnswers.set(currentQuestionIndex, "4");
			return;
		}
	}
	/**
	 * Shows the saved user answer for the current question.
	 * Checks the corresponding radio button based on the user's answer.
	 * Called when displaying a new question.
	 */
	private void showSavedUserAnswer() {
		switch (userAnswers.get(currentQuestionIndex)) {
		case "1":
			r1.setSelected(true);
			break;
		case "2":
			r2.setSelected(true);
			break;
		case "3":
			r3.setSelected(true);
			break;
		case "4":
			r4.setSelected(true);
			break;
		default:
			uncheckButtons();
			break;
		}
	}
	/**
	 * Displays the current question and its options on the screen.
	 */
	private void showQuestion() {
		question.setText(questionsForTest.get(currentQuestionIndex).getQuestionDescription());
		answer1.setText(questionsForTest.get(currentQuestionIndex).getOptionsText()[0]);
		answer2.setText(questionsForTest.get(currentQuestionIndex).getOptionsText()[1]);
		answer3.setText(questionsForTest.get(currentQuestionIndex).getOptionsText()[2]);
		answer4.setText(questionsForTest.get(currentQuestionIndex).getOptionsText()[3]);
		questionNumber.setText(String.valueOf(currentQuestionIndex + 1));
		points.setText(exam.getQuestionsScores().get(currentQuestionIndex) + " points");
	}
	/**
	 * Unchecks all radio buttons.
	 * Called when loading a new question.
	 */
	public void uncheckButtons() {
		r1.setSelected(false);
		r2.setSelected(false);
		r3.setSelected(false);
		r4.setSelected(false);
	}
	/**
	 * Decreases the timer and updates the timer label.
	 * If the remaining time reaches 0, stops the timer and displays an alert for exam finish.
	 * If additional time has been added, displays an alert for the added time.
	 * Called on each timer tick.
	 *
	 * @param event The ActionEvent triggered by the timer.
	 */
	private void decreaseTimer(ActionEvent event) {
		if (trigger_additional_time_alert) {
			Platform.runLater(this::createAlertForAdditionalTimeAdded);
			trigger_additional_time_alert = false;
			StopCheckingTimeThread(); // stop the thread.
		}
		if (remainingSeconds > 0) {
			remainingSeconds--;
			updateTimerLabel();
		}
		if (remainingSeconds == 0) {
			timeline.stop();
			checkIfExamIsLockedThread.running=false;
			Platform.runLater(this::createAlertForExamHasFinished); // Schedule alert to run on a the application //
		}
	}
	/**
	 * Updates the timer label to display the remaining time.
	 */
	private void updateTimerLabel() {
		int hours = remainingSeconds / 3600;
		int minutes = (remainingSeconds % 3600) / 60;
		int seconds = remainingSeconds % 60;
		String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		timer.setText(formattedTime);
	}
	/**
	 * Creates an alert to inform the user that the exam has finished.
	 * Checks the selected answer, sends the exam data to the server, and displays a message to the user.
	 * Finally, changes the screen to the student main screen.
	 */
	private void createAlertForExamHasFinished() {

		checkSelectedAnswer();
		startSendDataExamThread();
		Alert reminderAlert = new Alert(AlertType.INFORMATION);
		reminderAlert.setTitle("Message");
		reminderAlert.setHeaderText("Exam Finished");
		reminderAlert.setContentText("We Wish you a good day!");

		// Show the reminder alert
		reminderAlert.showAndWait();
		try {
			changeScreens("studentMainScreen.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Creates an alert to inform the user that additional time has been added to the exam.
	 * Displays the added time in minutes and wishes the user good luck.
	 */
	private void createAlertForAdditionalTimeAdded() {
		Alert reminderAlert = new Alert(AlertType.INFORMATION);
		reminderAlert.setTitle("Important Message");
		reminderAlert.setHeaderText("The Teacher Added : " + String.valueOf(timeDifferenceToShowUser / 60)

				+ "minutes.\n");
		reminderAlert.setContentText("Good Luck !");

		// Show the reminder alert after the animation frame is done
		reminderAlert.showAndWait();
	}
	/**
	 * Shows a confirmation dialog with the specified title and content text.
	 * Provides Yes and No buttons for the user to choose from.
	 * Executes different actions based on the user's response.
	 *
	 * @param title       The title of the dialog.
	 * @param contentText The content text of the dialog.
	 */
	private void showAlert(String title, String contentText) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(contentText);

		// Set the buttons for Yes and No
		ButtonType buttonYes = new ButtonType("Yes");
		ButtonType buttonNo = new ButtonType("No");
		alert.getButtonTypes().setAll(buttonYes, buttonNo);

		// Show the dialog and wait for the user's response
		alert.showAndWait().ifPresent(response -> {
			if (response == buttonNo) {
				alert.close();
				return;
			}
			try {
				timeline.stop();
				checkIfExamIsLockedThread.running=false;
				changeScreens("studentMainScreen.fxml");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}
	/**
	 * Calculates the grade of the student based on their answers.
	 * Compares each user answer with the corresponding correct answer and adds the score of correct answers.
	 *
	 * @return The calculated grade.
	 */
	private int calculateGrade() {
		int grade = 0;
		for (int i = 0; i < questionsForTest.size(); i++) {
			if (userAnswers.get(i).equals(correct_questions_answers.get(i))) {
				grade += Integer.parseInt(exam.getQuestionsScores().get(i));
			}
		}
		return grade;
	}
	/**
	 * Changes the screen to the specified screen name.
	 * Stops the checking time thread and loads the new screen using FXMLLoader.
	 *
	 * @param screen_name The name of the screen file.
	 * @throws IOException If an I/O error occurs while loading the screen.
	 */
	private void changeScreens(String screen_name) throws IOException {
		StopCheckingTimeThread();
		CEMSClientUI.switchScenes(getClass().getResource("studentMainScreen.fxml"));
	}
	/**
	 * Postpones the exam time by the specified duration in seconds.
	 * If the duration is zero or negative, no changes are made.
	 *
	 * @param duration_in_seconds The duration in seconds to add to the remaining time.
	 */
	public static void postponeExamTime(int duration_in_seconds) {
		if (duration_in_seconds <= 0)
			return;
		synchronized (exam_time_lock) {
			remainingSeconds += duration_in_seconds;
		}
	}
	/**
	 * Starts the thread for checking if the exam time has changed.
	 * Creates an instance of the CheckForTimeChange thread and starts it.
	 */
	public void startCheckingForTimeChangeUsingThread() {
		checkTimeThread = new CheckForTimeChange();
		checkTimeThread.start(); // Start the thread
	}
	/**
	 * Stops the checking time thread if it is running.
	 */
	public static void StopCheckingTimeThread() {
		if (checkTimeThread != null) {
			checkTimeThread.stopRunning(); // Stop the thread
			checkTimeThread = null;
		}
	}

	/**
	 * Handles the situation when the exam is locked.
	 * Sends the exam data to the server and switches screens.
	 */
	private void handleExamIsLocked() {
		//Also send the exam data to db
		startSendDataExamThread();
	}
	/**
	 * A thread class that checks for changes in exam time.
	 * The thread continuously checks for time changes and notifies the server.
	 */
	private class CheckForTimeChange extends Thread {
		private volatile boolean running = true; // all threads see the most up-to-date value.
		private int init_exam_time = (int) exam.getDuration().getSeconds(); // original test duration
		/**
	     * Updates the initial exam time with the time difference obtained from the server.
	     * If a positive time difference exists, it indicates that additional time has been added.
	     * Sets the time difference for the user, triggers the additional time alert, and resets the time difference.
	     */
		private synchronized void updateInitTime() {
			if (timeDifferenceForThread > 0) {
				init_exam_time += timeDifferenceForThread;
				timeDifferenceToShowUser = timeDifferenceForThread;
				trigger_additional_time_alert = true;
				timeDifferenceForThread = 0;
			}
		}

		@Override
		public void run() {
			try {
				Thread.sleep(10000); // Wait for 10 seconds before starting to check for time change
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (running) {
				updateInitTime();

				ExamTimeChangeObject timeChange = new ExamTimeChangeObject(init_exam_time,
						startTestScreenController.relevantExams.getConduct_exam_id(), ExamType.COMPUTERIZED);
				try {
					CEMSClientUI.chat.accept(new Message(CONSTANTS.CheckForExamTimeChange, timeChange));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(15000); // Wait for 15 seconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		 /**
	     * Stops the execution of the thread by setting the running flag to false.
	     */
		public void stopRunning() {
			running = false;
		}
	}
}
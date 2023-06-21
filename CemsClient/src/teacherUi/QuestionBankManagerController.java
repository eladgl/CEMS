package teacherUi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Client.CEMSClient;
import Client.CEMSClientController;
import Client.CEMSClientUI;
import entities.Course;
import entities.Question;
import entities.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import utilities.CONSTANTS;
import utilities.Message;

public class QuestionBankManagerController {
	public static Subject choosenSubject = null;
	public static ArrayList<Question> generalQuestions = null, // save all relevant questions for this lecturer
			myBankQuestion = null; // save all questions in the bankQuestion of this lecturer

	@FXML
	private Label answerA;

	@FXML
	private Label answerB;

	@FXML
	private Label answerC;

	@FXML
	private Label answerD;

	@FXML
	private VBox sliding_courses;

	@FXML
	private ImageView course_image;

	@FXML
	private Label relevantCoursesLabel;

	@FXML
	private Button generalBtn;
	@FXML
	private Button myBankBtn;

	@FXML
	private Label questionDescription;

	@FXML
	private TableView<Question> questionListTable;

	@FXML
	private TableColumn<Question, String> numberQuestionColl;

	@FXML
	private TableColumn<Question, String> descriptionQuestionCol;

	@FXML
	private TableColumn<Question, String> course_col;

	@FXML
	private Label subjectLabel;

	@FXML
	private Label view_questions_label;

	@FXML
	private Button left_arrow_image;

	@FXML
	private Button xButton;

	@FXML
	private Button edit_question_btn;

	@FXML
	private Button insert_new_qustion;

	@FXML
	private Button manage_personal_bank_btn;

	@FXML
	void initialize() {

		setStyleToComponents();

		generalBtn.setOnAction(e -> filterQuestions(e)); // set the filter action when those buttons pressed
		myBankBtn.setOnAction(e -> filterQuestions(e));

		fetchQuestions(); // send "getQuestions" request to server, and inject them inside
							// generalQuestions , myBankQuestion.

		setTableView(generalQuestions); // as default set table view to display all relevant questions

		questionListTable.setOnMouseClicked(event -> {
			if (event.getClickCount() == 1) {
				// Check if the table is empty
				if (!questionListTable.getItems().isEmpty()) {
					// Get the selected item from the table view
					Question selectedQuestion = questionListTable.getSelectionModel().getSelectedItem();

					// Perform the desired action with the selected item
					// For example, you can call a method or trigger an event
					if (selectedQuestion != null)
						displayQuestion(selectedQuestion);
				}
			}
		});

		subjectLabel.setText(choosenSubject.getName()); // set the subject label in the screen

		left_arrow_image.setPickOnBounds(true);
		// back arrow click
		left_arrow_image.setOnMouseClicked((MouseEvent e) -> {

			try {
				CEMSClientUI.switchScenes(getClass().getResource("TeacherMainScreen.fxml"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});

		xButton.setPickOnBounds(true);
		xButton.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClient.logout(TeacherMainScreenController.getUsername());
				CEMSClient.exitButton();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		});

		// adding the questions to the list of questions
	}

	private void setStyleToComponents() {
		String cssFile = "/resources/QuestionBankStyle.css";
		sliding_courses.getStyleClass().add("vboxHide");
		sliding_courses.setMouseTransparent(true);

		course_image.setOnMouseEntered(event -> {
			sliding_courses.getStyleClass().remove("vboxHide");
			sliding_courses.getStyleClass().add("vboxShow");
		});
		course_image.setOnMouseExited(event -> {
			sliding_courses.getStyleClass().remove("vboxShow");
			sliding_courses.getStyleClass().add("vboxHide");
		});

		for (Course course : choosenSubject.getCourses()) {
			Label courseLabel = new Label(course.getName());
			courseLabel.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
			courseLabel.getStyleClass().add("light-blue-label");
			sliding_courses.getChildren().add(courseLabel);
		}
	}

	private void filterQuestions(ActionEvent e) {
		Button pressedButton = (Button) e.getSource();
		if (pressedButton.equals(generalBtn)) {
			setTableView(generalQuestions);
			generalBtn.getStyleClass().add("green-border"); // add light green border because as default this filter is
															// choosen
			myBankBtn.getStyleClass().remove("green-border"); // disable green border to the other button

		} else if (pressedButton.equals(myBankBtn)) {
			setTableView(myBankQuestion);
			myBankBtn.getStyleClass().add("green-border");
			generalBtn.getStyleClass().remove("green-border");
		}

	}

	private void setTableView(ArrayList<Question> questions) {
		// Set cell value factory for numberQuestionColl
		numberQuestionColl.setCellValueFactory(new PropertyValueFactory<>("questionNumber"));
		// Set cell value factory for descriptionQuestionCol
		descriptionQuestionCol.setCellValueFactory(new PropertyValueFactory<>("questionDescription"));
		course_col.setCellValueFactory(new PropertyValueFactory<>("courseName"));

		ObservableList<Question> data = FXCollections.observableArrayList();
		if (questions != null)
			for (Question quesiton : questions)
				data.add(quesiton);
		questionListTable.setItems(data);
	}

	private void displayQuestion(Question question) {
		String[] choices = question.getOptionsText();
		answerA.setStyle("-fx-font-weight: normal; -fx-text-fill: red;"); // reset the color of all answers
		answerB.setStyle("-fx-font-weight: normal; -fx-text-fill: red;");
		answerC.setStyle("-fx-font-weight: normal; -fx-text-fill: red;");
		answerD.setStyle("-fx-font-weight: normal; -fx-text-fill: red;");
		// set all text to display question
		questionDescription.setText(question.getQuestionDescription());
		answerA.setText("1. " + choices[0]);
		answerB.setText("2. " + choices[1]);
		answerC.setText("3. " + choices[2]);
		answerD.setText("4. " + choices[3]);
		Label[] labels = { answerA, answerB, answerC, answerD }; // instead of if else
		labels[question.getCorrectAnswer() - 1].setStyle("-fx-font-weight: bold; -fx-text-fill: #008000;");
		relevantCoursesLabel.setText(question.getCourseName());
	}

	public void addNewQuestionAction(ActionEvent event) throws IOException {
		CEMSClientUI.switchScenes(getClass().getResource("CreateQuestionScreen.fxml"));
	}

	public void ManagePersonalBankAction(ActionEvent event) throws IOException {

		// sending the question bank name to the other screen.
		CEMSClientUI.switchScenes(getClass().getResource("ExistingQuestionInsertScreen.fxml"));
	}

	protected void fetchQuestions() {
		try {
			CEMSClientUI.chat.accept(new Message(CONSTANTS.getQuestionsBySubject, choosenSubject)); // fetch all
																									// relevant
																									// questions by
																									// subject
			// filterQuestionByCourses(); // filter all the question in generalQuestionBank
			// to have only questions that relevant to this lecturer
			utilities.TeacherMethodHelper.filterQuestionByCourses(choosenSubject.getCourses(), generalQuestions);
			ArrayList<Object> data = new ArrayList<>();
			data.add(TeacherMainScreenController.user.getUserName()); // add user name to search for the lecturer bank
			data.add(choosenSubject); // add choosen subject to search for specific subject
			CEMSClientUI.chat.accept(new Message(CONSTANTS.getQuestionsBySubjectAndByLecturerBank, data)); // fetch all
																											// relevant
																											// questions
		} catch (Exception e) {

		}
	}

	// clicked on the edit question button
	public void editQuestionAction(ActionEvent event) {

		// copy the questions into the EditQuestionController
		EditQuestionController.questions = new ArrayList<Question>();
		for (int i = 0; i < myBankQuestion.size(); i++) {
			EditQuestionController.questions.add(new Question(myBankQuestion.get(i))); // used shallow copy constructor.
		}

		try {
			CEMSClientUI.switchScenes(getClass().getResource("EditQuestionScreen.fxml"));
		} catch (IOException e) {
			System.out.println("Couldnt change screens ---> EditQuestionScreen.fxml\n");
			e.printStackTrace();
		}
	}
}

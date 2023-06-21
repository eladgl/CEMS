package headDepartmentUI;

import java.io.IOException;
import java.util.ArrayList;

import Client.CEMSClient;
import Client.CEMSClientUI;

import entities.Course;
import entities.Exam;
import entities.Question;
import entities.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import utilities.HeadDepartmentMethodHelper;

/**
 * Controller class for the ViewExamsData2.fxml file.
 */
public class ViewExamsData2Controller {
	public static Course[] courses = null;
	public static Course[] examCourses = null;
	public static Subject[] subjects = null;
	public static Subject choosenSubject = null;
	public static Course choosenCourse = null;
	public static ArrayList<Question> myBankQuestoins = null;
	public static ArrayList<Exam> myExams = new ArrayList<Exam>();
	public static ArrayList<Exam> generalExams = new ArrayList<Exam>();
	public static ArrayList<Exam> selectedList = new ArrayList<Exam>();

	@FXML
	private Button left_arrow_image;

	@FXML
	private ComboBox<String> subject_choice_box;

	@FXML
	private Button general_btn;

	@FXML
	private ComboBox<String> filter_courses;

	@FXML
	private Label course_choice_error_message;

	@FXML
	private Label subjectLabel;

	@FXML
	private Label emptyLabel;

	@FXML
	private Button xButton;
	// table view for Exam

	@FXML
	private TableView<Exam> exam_table_view;

	@FXML
	private TableColumn<Exam, String> number_col;

	@FXML
	private TableColumn<Exam, String> name_col;

	@FXML
	private TableColumn<Exam, String> subject_col;

	@FXML
	private TableColumn<Exam, String> course_col;

	@FXML
	private TableColumn<Exam, String> lecturer_col;

	// components to build the exam view
	@FXML
	private Label Time_label;

	@FXML
	private Label exam_name;

	@FXML
	private Label lecturer_description;

	@FXML
	private Label student_description;

	@FXML
	private VBox questions_VBox;

	/**
	 * Initializes the controller.
	 *
	 * @throws Exception if an error occurs during initialization
	 */
	@FXML
	void initialize() throws Exception {
		subjects = HeadDepartmentMainScreenController.subjects;
		courses = HeadDepartmentMainScreenController.extractCoursesFromSubjects(subjects);
		subjects = HeadDepartmentMainScreenController.removeSubjectDuplicates(subjects);
		selectedList = HeadDepartmentMainScreenController.generalExams;

		// add subjects
		subject_choice_box.getItems().add("All Subjects");

		filter_courses.getItems().add("All Courses");

		for (Subject subject : subjects) {
			subject_choice_box.getItems().add(subject.getName());
		}
		subject_choice_box.setVisibleRowCount(15);
		
		for (Course course : courses) {
			filter_courses.getItems().add(course.getName());
		}
		filter_courses.setVisibleRowCount(15);
		
		name_col.setCellValueFactory(new PropertyValueFactory<>("testName"));
		number_col.setCellValueFactory(new PropertyValueFactory<>("testNumber"));
		subject_col.setCellValueFactory(new PropertyValueFactory<>("subject"));
		course_col.setCellValueFactory(new PropertyValueFactory<>("course"));
		lecturer_col.setCellValueFactory(new PropertyValueFactory<>("AuthorName"));

		setUpChoiceBoxes();
		// define the table view of the exams

		setUpTableViewExam(selectedList); // at start insert the general exam (for the choosen Subject)

		// setUp
		left_arrow_image.setPickOnBounds(true);
		// back arrow click
		left_arrow_image.setOnMouseClicked((MouseEvent e) -> {

			try {
				CEMSClientUI.switchScenes(getClass().getResource("headDepartmentMainScreen.fxml"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});

		xButton.setPickOnBounds(true);
		xButton.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClient.logout(HeadDepartmentMainScreenController.getUsername());
				CEMSClient.exitButton();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		});
	}

	/**
	 * Sets up the table view for exams.
	 *
	 * @param exams the list of exams to display in the table view
	 */
	private void setUpTableViewExam(ArrayList<Exam> exams) {
		exam_table_view.getItems().clear();
		if (exams != null) {
			ObservableList<Exam> examList = FXCollections.observableArrayList();
			for (Exam exam : exams) {
				examList.add(exam);

			}
			exam_table_view.setItems(examList);
			exam_table_view.refresh();
		}
	}

	/**
	 * Sets up the choice boxes and attaches listeners to handle value changes.
	 */
	private void setUpChoiceBoxes() {

		filter_courses.getSelectionModel().select(0);
		filter_courses.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!oldValue.equals(newValue))
				updateTableViewExam();
		});

		subject_choice_box.getSelectionModel().select(0);
		subject_choice_box.valueProperty().addListener((observable2, oldValue2, newValue2) -> {
			if (!oldValue2.equals(newValue2))
				updateTableViewExam();
		});
	}

	/**
	 * Updates the table view based on the selected course and subject.
	 */
	private void updateTableViewExam() {
		String filterCourse = filter_courses.getValue();

		String filterSubject = subject_choice_box.getValue();

		myExams = HeadDepartmentMethodHelper.filterExamsByCourseAndSubject(selectedList, filterCourse, filterSubject);

		setUpTableViewExam(myExams);
	}

	/**
	 * Displays the exam details in a grid pane.
	 *
	 * @param exam The exam to be displayed.
	 */
	private void DisplayExamView(Exam exam) // build gridPane to displat the exam
	{
		questions_VBox.getChildren().clear();
		Time_label.setText(exam.getDurationTimeFormat()); // set al constants label of the exan
		exam_name.setText(exam.getTestName());
		student_description.setText(exam.getStudentDescription());
		lecturer_description.setText(exam.getTeacherDescription());
		// create for each quesiton the vbox of of the question and insert it to the
		// quesitons_VBox
		ArrayList<Question> questinos = exam.getQuestions();
		ArrayList<String> points = exam.getQuestionsScores();
		for (int i = 0; i < questinos.size(); i++) // create for each question vbox and insert it to the main vbox
													// questions_VBox
		{
			Question currentQuestion = questinos.get(i);
			VBox vbox = new VBox();
			vbox.setSpacing(10);
			vbox.setStyle("-fx-padding: 0 0 40px 0; -fx-border-color:black; -fx-border-width: 0 0 1 0 ;");

			Label question_number = new Label("Question " + (i + 1) + " (" + points.get(i) + ")");
			// each label will get this style will make the label expand accordingly
			// to the size of the text
			question_number.setStyle("-fx-padding: 30px 0 0 0;-fx-font-weight: bold; -fx-underline: true; -fx-font-size: 14px;");
			Label questoin_description = new Label(currentQuestion.getQuestionDescription());
			questoin_description.setStyle("-fx-font-weight: bold; -fx-padding: 8px 0;");
			String[] choices = currentQuestion.getOptionsText();
			Label[] labelChoices = new Label[choices.length];
			for (int j = 0; j < labelChoices.length; j++) {
				labelChoices[j] = new Label((j + 1) + ". " + choices[j]); // set to label choice[0] the string choice[0]
				labelChoices[j].setStyle("-fx-font-weight: bold");
			}
			labelChoices[currentQuestion.getCorrectAnswer() - 1]
					.setStyle("-fx-font-weight: bold; -fx-text-fill: green;"); // color the label in green if is the
																				// correct answer
			labelChoices[currentQuestion.getCorrectAnswer() - 1]
					.setText(labelChoices[currentQuestion.getCorrectAnswer() - 1].getText() + " (Correct Answer)");
			// add all children in the correct order
			vbox.getChildren().add(question_number);
			VBox.setVgrow(question_number, Priority.ALWAYS); // make vbox expand according to the tallest children
			vbox.getChildren().add(questoin_description);
			VBox.setVgrow(questoin_description, Priority.ALWAYS);
			for (int j = 0; j < labelChoices.length; j++) {
				vbox.getChildren().add(labelChoices[j]);
				VBox.setVgrow(labelChoices[j], Priority.ALWAYS);
			}
			// after creating the vbox of the question we add it the the main vbox
			// questions_vbox
			questions_VBox.getChildren().add(vbox);
		}
		questions_VBox.applyCss();
		questions_VBox.layout();
	}

	/**
	 * Handles the mouse click event on the exam table view.
	 *
	 * @param event The mouse event triggered by the user.
	 */
	@FXML
	void ExamTableViewMouseClick(MouseEvent event) // when clicked get the selected row and display the exam
	{
		if (!exam_table_view.getItems().isEmpty()) // if the table view is not empty
		{
			Exam selectedExam = exam_table_view.getSelectionModel().getSelectedItem();
			if (selectedExam != null) // and selected item is not empty
			{
				DisplayExamView(selectedExam); // display the exam it self
			}
		}
	}
}

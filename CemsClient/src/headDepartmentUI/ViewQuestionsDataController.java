package headDepartmentUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Client.CEMSClient;
import Client.CEMSClientController;
import Client.CEMSClientUI;
import clientGUI.UserLoginController;
import entities.Course;
import entities.Question;
import entities.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import utilities.CONSTANTS;
import utilities.Message;

public class ViewQuestionsDataController {

	public static String choosenSubject = "";
	public static String choosenCourse = "";
	public static Question[] questions = null, filteredQuestions = null;
	public static Subject[] subjects = null;
	public static Course[] courses = null;

	@FXML
	private Button left_arrow_image;

	@FXML
	private Button xButton;

	@FXML
	private Label select_item_error_msg_label;

	@FXML
	private ComboBox<String> subject_choice;

	@FXML
	private ComboBox<String> course_choice;

	@FXML
	private Button view_questions_btn;

	@FXML
	private TableView<Question> questions_table;

	@FXML
	private TableColumn<Question, String> id_column;
	@FXML
	private TableColumn<Question, String> subject_column;
	@FXML
	private TableColumn<Question, String> subject_id_column;
	@FXML
	private TableColumn<Question, String> course_column;
	@FXML
	private TableColumn<Question, String> question_text_column;
	@FXML
	private TableColumn<Question, String> num_column;
	@FXML
	private TableColumn<Question, String> lecturer_column;
	@FXML
	private TableColumn<Question, String> choice1_column;
	@FXML
	private TableColumn<Question, String> choice2_column;
	@FXML
	private TableColumn<Question, String> choice3_column;
	@FXML
	private TableColumn<Question, String> choice4_column;
	@FXML
	private TableColumn<Question, String> answer_column;

	@FXML
	void initialize() {

		subjects = HeadDepartmentMainScreenController.subjects;
		courses = HeadDepartmentMainScreenController.extractCoursesFromSubjects(subjects);
		subjects = HeadDepartmentMainScreenController.removeSubjectDuplicates(subjects);
		
		// add subjects
		subject_choice.getItems().add("All Subjects");
		for (Subject subject : subjects) {
			subject_choice.getItems().add(subject.getName());
		}

		subject_choice.setVisibleRowCount(15);
		
		// add dataToShow
		course_choice.getItems().add("All Courses");
		for (Course course : courses) {
			course_choice.getItems().add(course.getName());
		}
		course_choice.setVisibleRowCount(15);
		
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

	private boolean isBoxEmpty(ComboBox<String> user_choice) {
		if (user_choice.getValue() == null)
			return true;
		return false;
	}

	@FXML
	public void showQuestionsAction() throws IOException {
		if (isBoxEmpty(subject_choice) || isBoxEmpty(course_choice)) {
			select_item_error_msg_label.setText("Must choose subject and course");
			return;
		}

		choosenSubject = (String) subject_choice.getValue();
		choosenCourse = (String) course_choice.getValue();

		// change screens depends on comboBox selection
		fetchQuestions();

	}

	private void updateTable() {
		// initiating each column to show the correct data from Question class
		id_column.setCellValueFactory(new PropertyValueFactory<Question, String>("ID"));
		subject_column.setCellValueFactory(new PropertyValueFactory<Question, String>("subject"));
		subject_id_column.setCellValueFactory(new PropertyValueFactory<Question, String>("SubjectCode"));
		course_column.setCellValueFactory(new PropertyValueFactory<Question, String>("CourseName"));
		question_text_column.setCellValueFactory(new PropertyValueFactory<Question, String>("questionDescription"));
		num_column.setCellValueFactory(new PropertyValueFactory<Question, String>("questionNumber"));
		lecturer_column.setCellValueFactory(new PropertyValueFactory<Question, String>("authorName"));
		choice1_column.setCellValueFactory(new PropertyValueFactory<Question, String>("optionsText1"));
		choice2_column.setCellValueFactory(new PropertyValueFactory<Question, String>("optionsText2"));
		choice3_column.setCellValueFactory(new PropertyValueFactory<Question, String>("optionsText3"));
		choice4_column.setCellValueFactory(new PropertyValueFactory<Question, String>("optionsText4"));
		answer_column.setCellValueFactory(new PropertyValueFactory<Question, String>("correctAnswer"));
		ObservableList<Question> questionList = FXCollections.observableArrayList(questions);
		questions_table.setItems(questionList);
	}

	private void fetchQuestions() {

		try {

			Message dataToServer = new Message(CONSTANTS.getQuestionsFromDB, null);
			CEMSClientUI.chat.accept(dataToServer);
		}

		catch (Exception e) {
		}

		// choosenSubject = "Mathematics";
		// choosenCourse = "Calculus";

		Question[] allQuestions = questions;

		questions = filterQuestionsByCourseAndSubject(allQuestions, choosenCourse, choosenSubject);
		updateTable();
	}

	public Question[] filterQuestionsByCourseAndSubject(Question[] allQuestions, String courseName, String subject) {
		ArrayList<Question> filteredQuestions = new ArrayList<>();

		if (courseName.equals("All Courses") && subject.equals("All Subjects")) {
			return allQuestions;
		}

		for (Question question : allQuestions) {

			if (courseName.equals("All Courses")) {
				if (question.getSubject().equals(subject))
					filteredQuestions.add(question);
			}

			else {
				if (question.getCourseName().equals(courseName)) {
					filteredQuestions.add(question);
				}
			}
		}

		return filteredQuestions.toArray(new Question[0]);
	}
}

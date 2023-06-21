package teacherUi;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import Client.CEMSClient;
import Client.CEMSClientUI;
import clientGUI.UserLoginController;
import entities.ConductExam;
import entities.Question;
import entities.StudentsDataInExam;
import entities.Subject;
import entities.StudentsExamDataGetter;
import utilities.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import javafx.scene.layout.Priority;

import java.io.File;

import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import utilities.CONSTANTS;

/**
 * The GradeExamScreenController class is a controller class for the grade exam
 * screen in the teacher's UI. It handles the display and interaction logic for
 * the grade exam screen.
 */

public class GradeExamScreenController 
{
	/**
	 * The currently selected subject.
	 */
	public static Subject subject;
	/**
	 * The list of exam results to be approved.
	 */
	public static ObservableList<StudentsDataInExam> ExamsResultsToApprove = FXCollections.observableArrayList();
	/**
	 * The message to be displayed.
	 */
	public static boolean ConductExanAllGradesAprroved=false;
	public static String message;
	private List<String> QuestionsCommentsID = new ArrayList<>();
	private boolean checkedExam = false;
	private StudentsDataInExam currentSelectedData;
	private int answerCounter = 0;

	@FXML
	private TextField filterTextField;

	@FXML
	private ScrollPane gradeScrollScreen;

	@FXML
	private Button left_arrow;
	
    @FXML
    private Label subjectLabel;
    

    @FXML
    private ComboBox<ConductExam> ConductedExamComboBox;

	@FXML
	private TableColumn<StudentsDataInExam, String> studentIdCol;

	@FXML
	private TableColumn<StudentsDataInExam, String> testNameCol;

	@FXML
	private TableColumn<StudentsDataInExam, String> subjectCol;

	@FXML
	private TableView<StudentsDataInExam> testsTakenTable;


    @FXML
    private Button xButton;

	@FXML
	private Label msgLbl;

	/**
	 * Refreshes the exams results to approve list.
	 *
	 * @param event The mouse event that triggered the refresh action.
	 */
	@FXML
	void refresh(MouseEvent event) 
	{
		ExamsResultsToApprove.clear();
		getStudentsDataInExamList();
		setUpComboBox(); // after refresh student need to refresh condcuted exam combo box
	}

	/**
	 * Filters the tests taken table based on the input in the filter text field.
	 *
	 * @param event The key event that triggered the filter action.
	 */
	@FXML
	void filterTable(KeyEvent event) {
		String newValue = filterTextField.getText().toLowerCase();
		FilteredList<StudentsDataInExam> filteredData = new FilteredList<>(ExamsResultsToApprove);
		filteredData.setPredicate(studentData -> {
			if (newValue.isEmpty()) {
				return true;
			}
			String filterText = newValue.toLowerCase();
			return studentData.getConductExamName().toLowerCase().startsWith(filterText);
		});
		testsTakenTable.setItems(filteredData);
	}

	/**
	 * Approves the grade for the selected exam.
	 *
	 * @param event The action event that triggered the approve grade action.
	 */
	@FXML
	void approveGrade(ActionEvent event) {
		if (!checkedExam) {
			showMessage("You did not choose any test yet");
			return;
		}
		currentSelectedData.setQuestionsComments(getQuestionCommentsFromSelection()); // set new question comments
		currentSelectedData.setNewGradeComment(getGradeCommentFromSelection());
		getNewGradeFromSelection();
		checkedExam = false;

		// update the exam data in db table - students_data_in_exam
		Message data_to_db = new Message(CONSTANTS.updateStudentDataInExam, currentSelectedData);
		try {
			CEMSClientUI.chat.accept(data_to_db);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		showMessage(message);
		if (message.equals("Exam approved Successfully - SMS sent to student")) 
		{
			StudentsDataInExam studentsDataInExamToCheck = currentSelectedData; // save it to check if this is the last student to approve is grade in the selected conduct exam
			ExamsResultsToApprove.remove(currentSelectedData);
			if(!ExistGradesForConductExam(studentsDataInExamToCheck.getConductExamId())) // if there is no grades left to approve for this conduct exam we approve the conduct exam grades and create statistic
			{
				Message sendToServer = new Message(CONSTANTS.approveAllGradesConductExamAndCreateStatistics,Integer.parseInt(studentsDataInExamToCheck.getConductExamId())); // send conduct exam id and approve all grades for conduct exam and create statistic
				try 
				{
					CEMSClientUI.chat.accept(sendToServer);
				} 
				catch (Exception e) {e.printStackTrace();}
				if(!ConductExanAllGradesAprroved) // check success for create statistic and approve all grades for conduct exam
					showMessage("could not approve all grades and create statistic for "+ studentsDataInExamToCheck.getConductExamName() );
				ConductExanAllGradesAprroved=false;
				refresh(null); // after updated in DB all the students approved grades need to fetch again all students grade that need to be approved
				checkedExam=false;
				currentSelectedData=null;
				clearScrollPaneContent();
			}
		}
	}
	
    private boolean ExistGradesForConductExam(String conductExamId) // return true if there is still grades to approve for given conduct exam id
    {
    	for(StudentsDataInExam studentExamResult : ExamsResultsToApprove) // go over all the current grades
    	{
    		if(studentExamResult.getConductExamId().equals(conductExamId)) // if there is grade to approve for given conduct exam id return true
    			return true;
    	}
		return false;
	}

	@FXML
    void AprroveAllGradeAction(MouseEvent event) 
    {
		ConductExam conductExamToApprove = ConductedExamComboBox.getValue();
    	if(conductExamToApprove.getConduct_exam_name().equals("Empty"))
    	{
    		showMessage("Exam did not selected To Aprrove All Grade.");
    		return;
    	}
		Message sendToServer = new Message(CONSTANTS.approveAllGradesConductExamAndCreateStatistics,conductExamToApprove.getConduct_exam_id()); // send conduct exam id and approve all grades for conduct exam and create statistic
		try 
		{
			CEMSClientUI.chat.accept(sendToServer);
		} 
		catch (Exception e) {e.printStackTrace();}
		if(ConductExanAllGradesAprroved) // check success for create statistic and approve all grades for conduct exam
			showMessage("all grades of "+conductExamToApprove.getConduct_exam_name()+" have been approved.");
		else
			showMessage("could not approve all grades and create statistic for "+ conductExamToApprove.getConduct_exam_name() );
		ConductExanAllGradesAprroved=false;
		refresh(null); // after updated in DB all the students approved grades need to fetch again all students grade that need to be approved
		checkedExam=false;
		currentSelectedData=null;
		clearScrollPaneContent();
		
		
    }
    

	/**
	 * Initializes the grade exam screen controller.
	 */
	@FXML
	void initialize() {
		ExamsResultsToApprove = FXCollections.observableArrayList();
		subjectLabel.setText(subject.getName());
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

		left_arrow.setPickOnBounds(true);
		// back arrow click
		left_arrow.setOnMouseClicked((MouseEvent e) -> {

			try {
				CEMSClientUI.switchScenes(getClass().getResource("TeacherMainScreen.fxml"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});

		// get exam data
		getStudentsDataInExamList();
		setUpComboBox();

		// create the tableview
		testsTakenTable.setItems(ExamsResultsToApprove);
		studentIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
		testNameCol.setCellValueFactory(new PropertyValueFactory<>("conductExamName"));
		subjectCol.setCellValueFactory(new PropertyValueFactory<>("course"));
//		studentIdCol.setReorderable(false);
//		testNameCol.setReorderable(false);
//		testNameCol.setReorderable(false);
//		subjectCol.setReorderable(false);

		testsTakenTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) 
			{
				currentSelectedData = newSelection;
				createScrollableScreen(newSelection);
				checkedExam = true;
				answerCounter = 0;
				System.out.println(newSelection.getUserId());
			}
		});
	}

	private void setUpComboBox() 
	{
		ArrayList<ConductExam> conductedExams  = getAllConductedExams(); // go over all the ExamsResultsToApprove and get the conducted Exams with out duplicates
		ConductedExamComboBox.getItems().clear(); // clear conduct exam combo box
		ConductExam fakeExam = new ConductExam(0, null, null, answerCounter, "Empty", null, null);
		ConductedExamComboBox.getItems().add(fakeExam);
		ConductedExamComboBox.getItems().addAll(conductedExams); // add it all
		ConductedExamComboBox.getSelectionModel().selectFirst(); // empty fist fake test to presented
	}

	/**
	 * Retrieves the list of students' data in exams from the server.
	 */
	private void getStudentsDataInExamList() {
		StudentsExamDataGetter data = new StudentsExamDataGetter(subject.getCode(),
				UserLoginController.user.getUserName());
		Message data_to_db = new Message(CONSTANTS.getStudentsDoneTestData, data);
		try {
			CEMSClientUI.chat.accept(data_to_db);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	//
	// Start build of scrollPane
	//
	//
	private void createScrollableScreen(StudentsDataInExam selectedData) {
		clearScrollPaneContent();
		VBox scrollContent = new VBox();
		QuestionsCommentsID = new ArrayList<>();
		createGradeBox(selectedData, scrollContent);
		createQuestionComponents(selectedData, scrollContent);
		StackPane centeredContent = createCenteredContent(scrollContent);
		gradeScrollScreen.setContent(centeredContent);
	}

	private void clearScrollPaneContent() {
		gradeScrollScreen.setContent(null);
	}

	private void createGradeBox(StudentsDataInExam selectedData, VBox scrollContent) {
		HBox gradeBox = new HBox();
		gradeBox.setAlignment(Pos.CENTER_LEFT);

		Label currentGradeLabel = new Label("Current Grade: " + selectedData.getGrade());
		gradeBox.getChildren().add(currentGradeLabel);

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		gradeBox.getChildren().add(spacer);

		Label setNewGradeLabel = new Label("Set new grade: ");
		gradeBox.getChildren().add(setNewGradeLabel);

		TextField newGradeTextField = createNewGradeTextField(selectedData);
		gradeBox.getChildren().add(newGradeTextField);

		HBox commentNewGradeBox = new HBox(10);
		commentNewGradeBox.setPrefWidth(100);

		Label noteLabel = new Label("Note for grade change: ");
		commentNewGradeBox.getChildren().add(noteLabel);

		TextField noteTextField = createGradeCommentTextField();
		commentNewGradeBox.getChildren().add(noteTextField);

		scrollContent.getChildren().add(gradeBox);
		scrollContent.getChildren().add(commentNewGradeBox);
	}

	private TextField createNewGradeTextField(StudentsDataInExam selectedData) {
		TextField newGradeTextField = new TextField();
		newGradeTextField.setPrefWidth(50);
		newGradeTextField.setId("new_grade");
		newGradeTextField.setText(Integer.toString(selectedData.getGrade()));
		newGradeTextField.setOnKeyTyped(this::handleNewGradeTextFieldKeyPress);
		return newGradeTextField;
	}

	private void handleNewGradeTextFieldKeyPress(KeyEvent event) {
		TextField newGradeTextField = (TextField) event.getSource();
		String newText = newGradeTextField.getText();
		if (!newText.matches("[0-9]{1,3}") || Integer.parseInt(newText) > 100) {
			showMessage("Grade is a number 0-100");
			newGradeTextField.clear();
		}
	}

	private TextField createGradeCommentTextField() {
		TextField noteTextField = new TextField();
		noteTextField.setId("grade_comment");
		return noteTextField;
	}

	private void createQuestionComponents(StudentsDataInExam selectedData, VBox scrollContent) {
		int questionNumber = 1;
		for (Question question : selectedData.getQuestions()) {
			Label questionNumberLabel = createQuestionNumberLabel(questionNumber);
			scrollContent.getChildren().add(questionNumberLabel);

			Label questionTextLabel = createQuestionTextLabel(question);
			scrollContent.getChildren().add(questionTextLabel);

			HBox answersBox = createAnswersBox(question, selectedData, questionNumber);
			scrollContent.getChildren().add(answersBox);

			questionNumber++;
		}
	}

	private Label createQuestionNumberLabel(int questionNumber) {
		Label questionNumberLabel = new Label("Question " + questionNumber);
		questionNumberLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14pt;");
		return questionNumberLabel;
	}

	private Label createQuestionTextLabel(Question question) {
		Label questionTextLabel = new Label(question.getQuestionDescription());
		return questionTextLabel;
	}

	private HBox createAnswersBox(Question question, StudentsDataInExam selectedData, int questionNumber) {
		HBox answersBox = new HBox(5);
		answersBox.setPadding(new Insets(5, 10, 5, 20));
		VBox answersVBox = createAnswersVBox(question, selectedData);
		VBox commentRow = createCommentRow(questionNumber);
		answersBox.getChildren().addAll(answersVBox, commentRow);
		return answersBox;
	}

	private VBox createAnswersVBox(Question question, StudentsDataInExam selectedData) {
		VBox answersVBox = new VBox(10);
		String number_answered = selectedData.getAnswers().get(answerCounter);
		answerCounter++;
		question.getCorrectAnswer();
		boolean wasThereAnAnswer = false;
		for (int i = 0; i < 4; i++) {
			Label questionAnswerText = new Label(question.getOptionsText()[i]);
			Label bulletPoint = new Label("\u2022");
			bulletPoint.setStyle("-fx-font-size: 8pt; -fx-padding: 0 5 0 0;");
			HBox answerRow = new HBox(5, bulletPoint, questionAnswerText);

			if (question.getCorrectAnswer() == i + 1) { // if this is the correct answer
				questionAnswerText.setStyle("-fx-text-fill: green;"); // mark the questions correct answer as green
				if (number_answered.equals(Integer.toString(i + 1))) { // and you marked it
					ImageView correctImage = createCorrectAnswerImage();
					HBox.setMargin(correctImage, new Insets(0, 10, 0, 10));
					answerRow.getChildren().add(correctImage);
					wasThereAnAnswer = true;

				}
			} else if (question.getCorrectAnswer() != i + 1) {
				questionAnswerText.setStyle("-fx-text-fill: blue;"); // mark the questions correct answer as green
				String optionNumber = Integer.toString(i + 1);
				if (number_answered.equals(optionNumber) )
				{
					ImageView xImage = createXImage();
					answerRow.getChildren().add(xImage);
					HBox.setMargin(xImage, new Insets(0, 10, 0, 10));
					questionAnswerText.setStyle("-fx-text-fill: red;");
					wasThereAnAnswer = true;

				}
			}
			answersVBox.getChildren().add(answerRow);
		}
		if (!wasThereAnAnswer) { // no answer was submitted at all - just put a X image
			ImageView xImage = createXImage();
			HBox noAnswerSubmitted = new HBox();
			Label noAnswerLabel = new Label("No answer was submitted");
			noAnswerSubmitted.getChildren().addAll(noAnswerLabel, xImage);
			HBox.setMargin(xImage, new Insets(0, 10, 0, 10));
			answersVBox.getChildren().add(noAnswerSubmitted);
		}
		return answersVBox;
	}

	private ImageView createCorrectAnswerImage() {

		Image image;
		ImageView correctImage;
		if (this.getClass().getResourceAsStream("/images/correct.png") != null) {
			image = new Image(this.getClass().getResourceAsStream("/images/correct.png"));
			correctImage = new ImageView(image);
		} else {
			correctImage = new ImageView();
		}
		correctImage.setFitWidth(15);
		correctImage.setFitHeight(15);
		correctImage.setTranslateX(10);
		return correctImage;
	}

	private ImageView createXImage() {
		Image image;
		ImageView xImage;
		if (this.getClass().getResourceAsStream("/images/X.png") != null) {
			image = new Image(this.getClass().getResourceAsStream("/images/X.png"));
			xImage = new ImageView(image);
		} else {
			xImage = new ImageView();
		}
		xImage.setFitWidth(15);
		xImage.setFitHeight(15);
		xImage.setTranslateX(10);
		return xImage;
	}

	private VBox createCommentRow(int questionNumber) {
		VBox labelAndTxtFieldContainer = new VBox(10);
		Label commentLabel = new Label("Your note on this question:");
		commentLabel.setStyle("-fx-font-weight: bold;");
		labelAndTxtFieldContainer.getChildren().add(commentLabel);
		TextField textField = createQuestionCommentTextField(questionNumber);
		HBox commentRow = new HBox(10, textField);
		labelAndTxtFieldContainer.getChildren().add(commentRow);
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		commentRow.getChildren().add(spacer);
		return labelAndTxtFieldContainer;
	}

	private TextField createQuestionCommentTextField(int questionNumber) {
		TextField textField = new TextField();
		String textFieldId = "q_" + questionNumber + "_comment";
		textField.setId(textFieldId);
		QuestionsCommentsID.add(textFieldId);
		return textField;
	}

	private StackPane createCenteredContent(VBox scrollContent) {
		StackPane centeredContent = new StackPane(scrollContent);
		centeredContent.setAlignment(Pos.CENTER);
		return centeredContent;
	}

	//
	// End build of scrollPane
	//
	//

	/**
	 * Displays a message to the user.
	 *
	 * @param message The message to display.
	 */
	private void showMessage(String msg) {
		msgLbl.setText(msg);
	}

	//
	//
	// start getters for scrollPane TextFields
	//
	private List<String> getQuestionCommentsFromSelection() {
		List<String> comments = new ArrayList<>();
		for (String commentId : QuestionsCommentsID) 
		{
			TextField questionCommentTextField = (TextField) gradeScrollScreen.getScene().lookup("#" + commentId);
			String questionComment = questionCommentTextField.getText();
			if(questionComment==null || questionComment.isEmpty())
				questionComment=" ";
			comments.add(questionComment);
		}
		return comments;
	}

	private String getGradeCommentFromSelection() {
		TextField questionCommentTextField = (TextField) gradeScrollScreen.getScene().lookup("#grade_comment");
		return questionCommentTextField.getText().replace(',', ' ');
	}

	private void getNewGradeFromSelection() {
		TextField newGradeTextField = (TextField) gradeScrollScreen.getScene().lookup("#new_grade");
		if (newGradeTextField.getText() != null)
			currentSelectedData.setGrade(Integer.parseInt(newGradeTextField.getText().replace(',', ' ')));
	}
	
	private ArrayList<ConductExam> getAllConductedExams()
	{
		ArrayList<ConductExam> coundectedExams = new ArrayList<>(); // creare empty array list of conduted Exams
		for(StudentsDataInExam studentExamResult : ExamsResultsToApprove) // go over all the data student result and get only the conudct exam data withput dupplications
		{
			ConductExam conductExamToAdd = new ConductExam(Integer.parseInt(studentExamResult.getConductExamId()), null, null, answerCounter, studentExamResult.getConductExamName(), null, null);
			if(!coundectedExams.contains(conductExamToAdd)) // if this student have conduct exam we didnt mapped we add it to the list
				coundectedExams.add(conductExamToAdd);
		}
		return coundectedExams;
	}

	//
	//
	// end getters for scrollPane TextFields
	//

}
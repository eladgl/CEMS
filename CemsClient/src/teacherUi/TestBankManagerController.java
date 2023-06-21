package teacherUi;

import java.io.IOException;
import java.util.ArrayList;
import Client.CEMSClient;
import Client.CEMSClientUI;
import clientGUI.UserLoginController;
import entities.Course;
import entities.Exam;
import entities.Question;
import entities.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import utilities.CONSTANTS;
import utilities.Message;

public class TestBankManagerController 
{
	public static Subject choosenSubject=null;
	public static Course choosenCourse=null;
	public static ArrayList<Question> myBankQuestoins =null;
	public static ArrayList<Exam> myExams =null,
						          generalExams=null,
						          selectedList = null;

	
	@FXML
	private Button left_arrow_image;
	
	@FXML
	private ComboBox<String> course_choice_box;
	
	@FXML
	private Button insert_new_test_btn;
	
	@FXML
	private Button insert_existing_test_btn;
	
    @FXML
    private Button general_btn;
    
    @FXML
    private Button myBank_Btn;
    
    @FXML
    private ComboBox<String> filter_courses;
	
	@FXML
	private Label course_choice_error_message;
	
    @FXML
    private Label subjectLabel;
    
	@FXML
	private ImageView add_new_testbank_image;
	
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
    

    @FXML
    private ScrollPane exam_scroll_pane;
    
    @FXML
    private Pane exam_pane;
		
	
	@FXML
	void initialize() throws Exception 
	{
		fetchQuestionsBySubject();
		fetchExams();
		setUpChoiceBoxes();
		//define the table view of the exams
		name_col.setCellValueFactory(new PropertyValueFactory<>("testName"));
		number_col.setCellValueFactory(new PropertyValueFactory<>("testNumber"));
		subject_col.setCellValueFactory(new PropertyValueFactory<>("subject"));
		course_col.setCellValueFactory(new PropertyValueFactory<>("course"));
		setUpTableViewExam( generalExams ); // at start insert the general exam (for the choosen Subject)
		selectedList=generalExams; // save the selected list
		//setUp 
		subjectLabel.setText(choosenSubject.getName());
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
	}


	protected static void fetchExams() throws Exception 
	{
		Message dataToServerGeneralExams = new Message(CONSTANTS.getExamsBySubject,choosenSubject); // get exams by subject to general Exams
		CEMSClientUI.chat.accept(dataToServerGeneralExams);
		Message dataToServerPresonalBank = new Message(CONSTANTS.getPersonalExamBank,UserLoginController.user.getUserName()); //get presonal bank exams to myExams
		CEMSClientUI.chat.accept(dataToServerPresonalBank);
		// after fetching the exams banks filter the personal exam bank (myExams) to have only exams form the choosenSubject
		utilities.TeacherMethodHelper.filterExamsBySubject(choosenSubject, myExams);
		utilities.TeacherMethodHelper.filterExamsByCourses(choosenSubject.getCourses(), generalExams); // the general exams have courses that this lecturer dont teach we filter only for the courses this teacher teach
	}



	private void setUpTableViewExam(ArrayList<Exam> exams) 
	{
		exam_table_view.getItems().clear();
		String FilterCourse = filter_courses.getValue();
		if(exams!=null)
		{
			if(FilterCourse.equals("All Courses"))
			{
				ObservableList<Exam> examList = FXCollections.observableArrayList(exams);
				exam_table_view.setItems(examList);
			}
			else
			{
				ObservableList<Exam> examList = FXCollections.observableArrayList();
				for(Exam exam :exams )
				{
					if(exam.getCourse().getName().equals(FilterCourse))
					{
						examList.add(exam);
					}
				}
				exam_table_view.setItems(examList);
			}

		}
		exam_table_view.refresh();

	}


	private void setUpChoiceBoxes() 
	{
		filter_courses.getItems().add("All Courses"); // Default for this choice box 
		for (Course course : choosenSubject.getCourses()) 
		{
		    course_choice_box.getItems().add(course.getName());
		    filter_courses.getItems().add(course.getName());
		}
		filter_courses.getSelectionModel().select(0);
		filter_courses.valueProperty().addListener((observable, oldValue, newValue) -> 
		{
			if(!oldValue.equals(newValue))
			{
				setUpTableViewExam(selectedList);
			}

	    });
	}


	private void fetchQuestionsBySubject() throws Exception 
	{
		ArrayList<Object> data = new ArrayList<>();
		data.add(TeacherMainScreenController.user.getUserName()); // add user name to search for the lecturer bank
		data.add(choosenSubject); // add choosen subject to search for specific subject
		CEMSClientUI.chat.accept(new Message(CONSTANTS.getQuestionsBySubjectAndByLecturerBank, data)); // fetch all relevant questions (fetch to QuestoinBankMangerController)
		myBankQuestoins=QuestionBankManagerController.myBankQuestion; // get the myBankQuestion form QuestionBankMenagerController.
	}
	
	
	public void addExistingTestIntoBankAction(ActionEvent event) throws IOException {
		if(isEmptyChoice())
		{
			course_choice_error_message.setText("Please choose a course.");
			return;
		}
		course_choice_error_message.setText("");
		String course = (String) course_choice_box.getValue(); // get the Course name from choicebox and save him
		choosenCourse=getCourse(course); // get the choosen course for the list of courses in the choosenSubject
		CEMSClientUI.switchScenes(getClass().getResource("ExistingTestInsertScreen.fxml")); // should be changed
	}


	private boolean isEmptyChoice() 
	{
		return course_choice_box.getValue() == null;
	}
	
	private Course getCourse(String courseName) 
	{
		for(Course course : choosenSubject.getCourses())  // go over all the courses this lectuer can teach and get the relevent instance of course by seraching via the name
		{
			if(course.getName().equals(courseName))
				return course;
		}
		return null;
	}
	
	private void DisplayExamView (Exam exam) // build gridPane to displat the exam
	{
		questions_VBox.getChildren().clear();
		Time_label.setText(exam.getDurationTimeFormat());  // set al constants label of the exan
		exam_name.setText(exam.getTestName());
		student_description.setText(exam.getStudentDescription());
		lecturer_description.setText(exam.getTeacherDescription());
		// create for each quesiton the vbox of of the question and insert it to the quesitons_VBox
		ArrayList<Question> questinos = exam.getQuestions();
		ArrayList<String> points  = exam.getQuestionsScores();
		for(int i=0 ; i<questinos.size();i++) // create for each question vbox and insert it to the main vbox questions_VBox
		{
			Question currentQuestion = questinos.get(i);
	        VBox vbox = new VBox();
	        vbox.setSpacing(10);
	        vbox.setStyle("-fx-padding: 0 0 40px 0;");
	        
	        Label question_number = new Label( "Question "+(i+1)+" ("+points.get(i)+")" );
	        question_number.setStyle("-fx-font-weight: bold; -fx-underline: true; -fx-font-size: 14px;"); // each label will get this style will make the label expand acorrdinglly to the size of the text
	        Label questoin_description = new Label(currentQuestion.getQuestionDescription());
	        questoin_description.setStyle("-fx-font-weight: bold; -fx-padding: 8px 0;" );
	        String[] choices = currentQuestion.getOptionsText();
	        Label[] labelChoices = new Label[choices.length];
	        for(int j=0 ;j<labelChoices.length;j++)
	        {
	        	labelChoices[j] = new Label((j+1)+". "+choices[j]); // set to label choice[0] the string choice[0]
	        	labelChoices[j].setStyle("-fx-font-weight: bold");
	        }
        	labelChoices[currentQuestion.getCorrectAnswer()-1].setStyle("-fx-font-weight: bold; -fx-text-fill: green;"); // color the label in green if is the correct answer
        	labelChoices[currentQuestion.getCorrectAnswer()-1].setText(labelChoices[currentQuestion.getCorrectAnswer()-1].getText()+" (Correct Answer)");
	        // add all children in the correct order
            vbox.getChildren().add(question_number);
            VBox.setVgrow(question_number, Priority.ALWAYS); // make vbox expand according to the tallest children
            vbox.getChildren().add(questoin_description);
            VBox.setVgrow(questoin_description, Priority.ALWAYS);
            for(int j=0;j<labelChoices.length;j++)
            {
                vbox.getChildren().add(labelChoices[j]);
                VBox.setVgrow(labelChoices[j], Priority.ALWAYS);
            }
            // after creating the vbox of the question we add it the the main vbox questions_vbox
            questions_VBox.getChildren().add(vbox);
		}
		questions_VBox.applyCss();
		questions_VBox.layout();
	}


	public   void addNewTestBankAction(MouseEvent event ) throws IOException  
	{
		if(isEmptyChoice())
		{
			course_choice_error_message.setText("Please choose a course.");
			return;
		}
		course_choice_error_message.setText("");
		String course = (String) course_choice_box.getValue(); // get the Course name from choicebox and save him
		choosenCourse=getCourse(course); // get the choosen course for the list of courses in the choosenSubject
		CEMSClientUI.switchScenes(getClass().getResource("CreateNewTestScreenImproved.fxml")); // should be changed
	}
	
    @FXML
    void ExamTableViewMouseClick(MouseEvent event) // when clicked get the selected row and display the exam
    {
    	if (!exam_table_view.getItems().isEmpty()) // if the table view is not empty
    	{
    	    Exam selectedExam = exam_table_view.getSelectionModel().getSelectedItem();
    	    if (selectedExam != null) // and selected item is not empty
    	    {
    	    	DisplayExamView(selectedExam); //display the exam it self
    	    }
    	}
    }
    
    @FXML
    void generalBtnAction(MouseEvent event) 
    {
    	if(! (general_btn.getStyleClass().contains("green-border") ) )
    	{
        	general_btn.getStyleClass().add("green-border");
    	}
    	myBank_Btn.getStyleClass().remove("green-border");
    	setUpTableViewExam(generalExams);
    	selectedList=generalExams;
    }

    @FXML
    void myBankBtnAction(MouseEvent event) 
    {
    	if(! (myBank_Btn.getStyleClass().contains("green-border") ) )
    	{
    		myBank_Btn.getStyleClass().add("green-border");
    	}
    	general_btn.getStyleClass().remove("green-border");
    	setUpTableViewExam(myExams);
    	selectedList=myExams;
    }
	
}

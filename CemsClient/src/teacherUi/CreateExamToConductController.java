package teacherUi;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Client.CEMSClient;
import Client.CEMSClientUI;
import clientGUI.UserLoginController;
import entities.ConductExam;
import entities.Course;
import entities.Exam;
import entities.Question;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import utilities.CONSTANTS;
import utilities.Message;
import utilities.TeacherMethodHelper;

public class CreateExamToConductController implements Initializable
{

	public static ArrayList<User> students;
	public static  boolean availablePassword = false;
	public static  boolean examToConductStartSuccessfully = false;
	private static ArrayList<UserWrapper> studentsToTableView;
	private static ArrayList<Exam> Exams;
	private static Exam selectedExam;
	
    // enroll students table view
    @FXML
    private TableColumn<UserWrapper, CheckBox> check_box_col;
    
    @FXML
    private TableColumn<UserWrapper, String> first_name_col;

    @FXML
    private TableColumn<UserWrapper, String> last_name_col;
    
    @FXML
    private TableColumn<UserWrapper, String> student_id_col;
    
    @FXML
    private TableView<UserWrapper> enroll_student_table;
    // exam table view

    @FXML
    private TableColumn<Exam, String> exam_number_col;
    
    @FXML
    private TableColumn<Exam, String> exam_name_col;

    @FXML
    private TableColumn<Exam, String> exam_subject_col;
    
    @FXML
    private TableColumn<Exam, String> exam_course_col;

    @FXML
    private TableView<Exam> exam_table_view;
    

    @FXML
    private Label course_label;
    
    @FXML
    private Label subjectLabel;

    @FXML
    private Label error_mag_label;
    
    @FXML
    private TextField exam_date_name_text;
    
    @FXML
    private TextField password_text;
    
    // display Exam
    @FXML
    private Label Time_label;
    
    @FXML
    private Label exam_name;

    @FXML
    private Label lecturer_description;

    @FXML
    private VBox questions_VBox;

    @FXML
    private Label student_description;
    // x button and return button
    @FXML
    private Button left_arrow_image;
    
    @FXML
    private Button xButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
    	fetchUsers();
    	fetchExms(); // only exams from the personal exam bank that relevant for the choosen course and subject
    	setUpTables();
    	displayDataTable(enroll_student_table);
    	displayDataTable(exam_table_view);
    	selectedExam=null;
    	cleeanScreen();
    	subjectLabel.setText(ConductExamController.choosenSubject.getName());
    	course_label.setText(ConductExamController.choosenCourse.getName());
    	
		left_arrow_image.setOnMouseClicked((MouseEvent e) -> {
			
			try {
				CEMSClientUI.switchScenes(getClass().getResource("ConductExamScreen.fxml"));
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
    
    private void cleeanScreen() 
    {
    	password_text.setText("");
    	password_text.setPromptText("4-length");
    	exam_date_name_text.setText("");
    	questions_VBox.getChildren().clear(); // clear displayed questions
    	lecturer_description.setText("");
        student_description.setText("");
        exam_name.setText("");
        Time_label.setText("00:00:00");
    	
    	
	}

	private void fetchExms() 
    {
		// using the ExistingTestInsertController to get the presonal bank exam by subject and by coursed
    	TestBankManagerController.choosenSubject= ConductExamController.choosenSubject; // assign the correct subject in the TestBankManagerController
    	try {
			TestBankManagerController.fetchExams(); // fetch the personal bank exam by subject to the myExams in the TestBankManagerController
		} catch (Exception e) {e.printStackTrace();}
    	Exams =TestBankManagerController.myExams;
    	ArrayList<Course> courseToFilter = new ArrayList<>();
    	courseToFilter.add(ConductExamController.choosenCourse);
    	TeacherMethodHelper.filterExamsByCourses(courseToFilter, Exams);
	}

	private void fetchUsers() 
    {
		Message sendToServer = new  Message (CONSTANTS.getStudentsUsers,null);
		try {
			CEMSClientUI.chat.accept(sendToServer);
		} catch (Exception e) {e.printStackTrace();}
    	studentsToTableView = new ArrayList<>();
    	for(User student : students)
    	{
    		studentsToTableView.add(new UserWrapper(student,new CheckBox()));
    	}
	}

	private void displayDataTable(TableView<?> tableToDisplay) 
    {
    	tableToDisplay.getItems().clear();
    	if(tableToDisplay.getId().equals("enroll_student_table"))
    	{
    		enroll_student_table.getItems().addAll(studentsToTableView);
    		
    	}
    	else
    	{
    		exam_table_view.getItems().addAll(Exams);
    	}
		
	}

	private void setUpTables() 
    {
    	check_box_col.setCellValueFactory(new PropertyValueFactory<>("userCheckBox"));
    	student_id_col.setCellValueFactory(new PropertyValueFactory<>("IDNumber"));
    	first_name_col.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
    	last_name_col.setCellValueFactory(new PropertyValueFactory<>("LastName"));
    	
    	exam_course_col.setCellValueFactory(new PropertyValueFactory<>("course")); // setting the tables
		exam_name_col.setCellValueFactory(new PropertyValueFactory<>("testName"));
		exam_number_col.setCellValueFactory(new PropertyValueFactory<>("testNumber"));
		exam_subject_col.setCellValueFactory(new PropertyValueFactory<>("subject"));
	}
    
    
    @FXML
    void ExamTableViewMouseClick(MouseEvent event) // when clicked get the selected row and display the exam
    {
    	error_mag_label.setText("");
    	if (!exam_table_view.getItems().isEmpty()) // if the table view is not empty
    	{
    	    selectedExam = exam_table_view.getSelectionModel().getSelectedItem();
    	    if (selectedExam != null) // and selected item is not empty
    	    {
    	    	DisplayExamView(selectedExam); //display the exam it self
    	    }
    	}
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
	
	public boolean isValidPassword() 
	{
		String password = password_text.getText();
	    // Check if the password is empty or null
	    if (password == null || password.isEmpty()) {
	        return false;
	    }

	    // Check if the password is exactly 4 characters long
	    if (password.length() != 4) {
	        return false;
	    }

	    // Check if the password contains only digits and characters
	    if (!password.matches("[a-zA-Z0-9]+")) {
	        return false;
	    }

	    return true;
	}


    @FXML
    void coductExamAction(MouseEvent event) 
    {
    	error_mag_label.setText("");
    	String Error = "";
    	int errorCount = 0;
    	if(exam_date_name_text.getText().isEmpty())
    	{
    		errorCount++;
    		Error+=errorCount+". Exam Date Name is empty\n";
    	}
    	if(!isValidPassword())
    	{
    		errorCount++;
    		Error+=errorCount+". Password should be 4 characters long and can contain digits or characters.\n";
    	}
    	ArrayList<User> enrolledStudents = getEnrolledStudents();
    	if(enrolledStudents.isEmpty())
    	{
    		errorCount++;
    		Error+=errorCount+". select one or more students to submit for the exam.\n";
    	}
    	if(selectedExam==null)
    	{
    		errorCount++;
    		Error+=errorCount+". exam must be selected. ";
    	}
    	if(errorCount!=0)
    	{
        	error_mag_label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        	error_mag_label.setText(Error);
        	return;
    	}
    	isPasswordAvailable(); // check all password in DB and if the given password by user is avialable availablePassword=true
    	if(availablePassword)
    	{
    		//create conductExam instance to send to server
    		String password = password_text.getText(),
    						  test_id = selectedExam.getTestID(),
    						  conduct_exam_name = exam_date_name_text.getText(),
    						  lecturerUserName=UserLoginController.user.getUserName();
    		Duration duration= selectedExam.getDuration();
    		ConductExam examToConduct = new ConductExam(-1, password, test_id, 1, conduct_exam_name, lecturerUserName, duration);
    		examToConduct.setRegisteredStudents(enrolledStudents);
    		Message sendToServer = new Message(CONSTANTS.startExamToConduct,examToConduct);
    		try {
				CEMSClientUI.chat.accept(sendToServer);
			} catch (Exception e) {e.printStackTrace();}
    		if(examToConductStartSuccessfully)
    		{
    			error_mag_label.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
    			error_mag_label.setText("The conduct exam has been successfully opened.");
    			examToConductStartSuccessfully=false;
    		}
    		else
    		{
    			error_mag_label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    			error_mag_label.setText("Failed to open the conduct exam. Please try again.");
    		}
    		initialize(null,null);
    			
    	}
    	else
    	{
        	error_mag_label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        	error_mag_label.setText("password already in use");
    	}
    	
    }
    
    private void isPasswordAvailable() 
    {
		Message sendToServer = new Message(CONSTANTS.availablePasswordForConductExam,password_text.getText());
		try {
			CEMSClientUI.chat.accept(sendToServer); // check if the password is available and set the flag  availablePassword accordingly
		} catch (Exception e) {e.printStackTrace();} 
	}

	private ArrayList<User> getEnrolledStudents()
    {
    	ArrayList<User> enrolledStudents = new ArrayList<>();
    	for(UserWrapper student : studentsToTableView)
    	{
    		CheckBox selected = check_box_col.getCellObservableValue(student).getValue();
    		if(selected.isSelected())
    			enrolledStudents.add(student.getUser());
    	}
    	return enrolledStudents;
    }
    
    
    public class UserWrapper
    {
    	private User user;
    	private CheckBox userCheckBox;
    	
    	public UserWrapper(User user,CheckBox userCheckBox) 
    	{
    		this.user=user;
    		this.userCheckBox=userCheckBox;
    	}
    	
    	public CheckBox getUserCheckBox() 
    	{
    	    return userCheckBox;
    	}
    	
    	public String getIDNumber()
    	{
    		return user.getIDNumber();
    	}
    	
    	public String getFirstName()
    	{
    		return user.getFirstName();
    	}
    	
    	public String getLastName()
    	{
    		return user.getLastName();
    	}
    	
    	public User getUser()
    	{
    		return user;
    	}
    	
    	
    }


}


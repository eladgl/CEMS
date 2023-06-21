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
import entities.RequestTimeExtension;
import entities.Subject;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
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

public class ConductExamController  implements Initializable
{

	public static Subject choosenSubject=null;
	public static Course choosenCourse=null;
	public static ArrayList<ConductExam> conductExams, // list of conduct Exam By choosenSubject
										 refreshedData; // conduct exam with up to date data from DB (when refresh button pressed)
	public static boolean requestAcceptedSusseccfully=false;
	public static boolean endedConductExamSusseccfully=false;
	private static ConductExam selectedConductExam;
	
	// table view for conduct exam
    @FXML
    private TableView<ConductExam> conduct_exams_table;
    
    @FXML
    private TableColumn<ConductExam, String> duration_col;
    
    @FXML
    private TableColumn<ConductExam, String> exam_date_name_col;
    
    @FXML
    private TableColumn<ConductExam, String> password_col;
    
    @FXML
    private TableColumn<ConductExam, String> request_status_col;
    
    @FXML
    private TableColumn<ConductExam, String> time_extension_col;

    @FXML
    private ComboBox<Course> course_choice_box;

    // registered srudents table view
    @FXML
    private TableView<User> registered_students_table;
    
    @FXML
    private TableColumn<User, String> first_name_col;

    @FXML
    private TableColumn<User, String> id_col;

    @FXML
    private TableColumn<User, String> last_name_col;
    
    // buttons
    @FXML
    private Button create_conduct_exam_Btn;

    @FXML
    private Button end_exam_Btn;
    
    @FXML
    private Button left_arrow_image;

    @FXML
    private ImageView refresh_Btn;

    @FXML
    private Button request_time_ext_Btn;
    
    @FXML
    private Button start_exam_Btn;
    
    @FXML
    private Button xButton;
    
    // labels
    
    @FXML
    private Label error_msg_label;
    
    @FXML
    private Label request_time_status_label;


    @FXML
    private Label subjectLabel;
    
    @FXML
    private Label choice_course_error_label;
    
    @FXML
    private Label empty_label;
    
    @FXML
    private TextField seconds_text;
    
    @FXML
    private TextField minuts_text;
    
    @FXML
    private TextField hours_text;

    // exam display
    
    @FXML
    private Label exam_date_name;
    
    @FXML
    private Label student_description;
    
    @FXML
    private Label Time_label;
    
    @FXML
    private VBox questions_VBox;
    
    @FXML
    private Label lecturer_description;
    
    @FXML
    private Label exam_name;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
    	
    	setUpCourseChoiceBox();
    	fetchConductExams();
    	setUpTableViews();
    	displayDataInTable(conduct_exams_table);
    	displayDataInTable(registered_students_table);
    	selectedConductExam=null;
    	requestAcceptedSusseccfully=false;
    	refreshedData=null;
    	subjectLabel.setText(choosenSubject.getName());
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
    
    private void displayDataInTable(TableView<?> table) 
    {
    	table.getItems().clear();
		if(table.getId().equals("conduct_exams_table"))
		{
			conduct_exams_table.getItems().addAll(conductExams);
			applyCellStyleToRequestStatusColumn();
		}
		else
		{
			if(selectedConductExam!=null)
				registered_students_table.getItems().addAll(selectedConductExam.getRegisteredStudents());
		}
	}

	private void setUpTableViews() 
    {
    	// conduct exam table view
    	exam_date_name_col.setCellValueFactory(new PropertyValueFactory<>("conduct_exam_name"));
    	password_col.setCellValueFactory(new PropertyValueFactory<>("password"));
    	duration_col.setCellValueFactory(new PropertyValueFactory<>("DurationTimeFormat"));
    	request_status_col.setCellValueFactory(new PropertyValueFactory<>("requestStatus"));
    	time_extension_col.setCellValueFactory(new PropertyValueFactory<>("TimeExtensionStringFormat"));
    	// registered students
    	id_col.setCellValueFactory(new PropertyValueFactory<>("IDNumber"));
    	first_name_col.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
    	last_name_col.setCellValueFactory(new PropertyValueFactory<>("LastName"));
	}

	private void fetchConductExams() 
    {
    	ArrayList<Object> SubjectAndUserName = new ArrayList<>();
    	SubjectAndUserName.add(choosenSubject);
    	SubjectAndUserName.add(UserLoginController.user.getUserName());
		Message sendToServer = new Message (CONSTANTS.getExamsToConductBySubjectByLecturer,SubjectAndUserName);
		try {
			CEMSClientUI.chat.accept(sendToServer); // send request to Server
		} catch (Exception e) {e.printStackTrace();}
	}

	private void setUpCourseChoiceBox() 
    {
		for(Course course : choosenSubject.getCourses() )
		{
			course_choice_box.getItems().add(course);
		}
	}

	@FXML
    void removeEmptyLabel(MouseEvent event) 
    {
		empty_label.setText("");
    }
	
    @FXML
    void ExamTableViewMouseClick(MouseEvent event) // when clicked get the selected row and display the exam
    {
    	error_msg_label.setText("");
    	hours_text.clear();
    	minuts_text.clear();;
    	seconds_text.clear();
    	if (!conduct_exams_table.getItems().isEmpty()) // if the table view is not empty
    	{
    		selectedConductExam = conduct_exams_table.getSelectionModel().getSelectedItem();
    	    if (selectedConductExam != null) // and selected item is not empty
    	    {
    	    	
    	    	DisplayExamView(selectedConductExam.getExamToConduct()); //display the exam it self
    	    	displayDataInTable(registered_students_table); // display the registered student for this selected conduct exam in the table of the registered student
    	    	if(selectedConductExam.getHasRequest().equals(CONSTANTS.requested)) // if request have been made for this conduct exam
    	    		request_time_ext_Btn.setDisable(true); // disable button of request
    	    	else
    	    		request_time_ext_Btn.setDisable(false); // enable button of request
    	    	request_time_status_label.setText(selectedConductExam.getRequestStatus());
    	    	request_time_status_label.getStyleClass().clear();
    	    	request_time_status_label.getStyleClass().setAll(selectedConductExam.getRequestStatus());
    	    }
    	    applyCellStyleToRequestStatusColumn();
    	    conduct_exams_table.refresh();
    	    
    	}
    }
    
	private void DisplayExamView (Exam exam) // build gridPane to displat the exam
	{
		exam_date_name.setText(selectedConductExam.getConduct_exam_name());
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
	
	private boolean validDuration(String seconds, String minutes, String hours) 
	{
		if(!utilities.TeacherMethodHelper.isPositiveNumber(seconds) || !utilities.TeacherMethodHelper.isPositiveNumber(minutes) || !utilities.TeacherMethodHelper.isPositiveNumber(hours) || seconds.length()!=2 || minutes.length()!=2 || hours.length()!=2 ) // check if each time inserted is number and only to digits
			return false;
		return (Integer.parseInt(seconds) + Integer.parseInt(minutes) + Integer.parseInt(hours))!=0; // check if the total time is not zero;
	}
	
	private void applyCellStyleToRequestStatusColumn() 
	{
	    request_status_col.setCellFactory(column -> new TableCell<ConductExam, String>() 
	    {
	        @Override
	        protected void updateItem(String requestStatus, boolean empty) 
	        {
	            super.updateItem(requestStatus, empty);

	            if (empty || requestStatus == null) 
	            {
	                setText(null);
	                setStyle(""); // Clear the style if cell is empty or null
	            } else {
	                setText(requestStatus);

	                // Customize the cell style based on the requestStatus
	                if ("Pending".equals(requestStatus)) {
	                    setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
	                } else if ("Approved".equals(requestStatus)) {
	                    setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
	                } else if ("Denied".equals(requestStatus)) {
	                    setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
	                } else 
	                {
	                    setStyle("-fx-font-weight: bold;"); // Apply default style
	                }
	            }
	        }
	    });
	}

    @FXML
    void endConductExamAction(MouseEvent event) 
    {
    	if(selectedConductExam==null) 
    	{
    		error_msg_label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    		error_msg_label.setText("no Exam selected.");
    		return;
    	}
    	Message sendToServer= new Message(CONSTANTS.endConductExam,selectedConductExam.getConduct_exam_id());
    	try 
    	{
			CEMSClientUI.chat.accept(sendToServer);
		} 
    	catch (Exception e) {e.printStackTrace();}
    	if(endedConductExamSusseccfully)
    	{
    		endedConductExamSusseccfully=false;
    		error_msg_label.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
    		error_msg_label.setText("Exam ended Susseccfully.");
    		conductExams.remove(selectedConductExam); // delete selectedConductExam form the origin conductExams list (should directly delete from table view)
    		selectedConductExam=null;
    		displayDataInTable(conduct_exams_table); // display data after the change to original list of conduct Exams
    		registered_students_table.getItems().clear(); // clear the registered student list
    		cleanScreen();
    	}
    	
    }
    
    private void cleanScreen() 
    {
    	exam_date_name.setText("");
    	questions_VBox.getChildren().clear(); // clear displayed questions
    	lecturer_description.setText("");
    	student_description.setText("");
    	exam_name.setText("");
    	Time_label.setText("00:00:00");
    	hours_text.clear();
    	minuts_text.clear();
    	seconds_text.clear();
    	request_time_status_label.setText("");
	}

	@FXML
    void RequestTimeExtensionAction(MouseEvent event) 
    {
    	String Error = "";
    	int countError=0;
    	if(!validDuration(seconds_text.getText(), minuts_text.getText(), hours_text.getText()))
    	{
    		countError++;
    		Error+=countError +". duration must be in the format 'hh:mm:ss' and should not be zero.\n";
    	}
    	if(selectedConductExam==null)
    	{
    		countError++;
    		Error+=countError +". Conduct Exam must be selected\n";
    	}
    	if(countError!=0)
    	{
        	error_msg_label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        	error_msg_label.setText(Error);
        	return;
    	}
    	request_time_ext_Btn.setDisable(true);
    	selectedConductExam.setHasRequest(CONSTANTS.requested); // set to this exam request time extension have been made
    	String timeExtension = hours_text.getText()+":"+minuts_text.getText()+":"+seconds_text.getText(); // get duration string format
    	Duration timeExtensionDuration = utilities.TeacherMethodHelper.convertDurationStringToDurationTime(timeExtension); // convert it to duration format
    	RequestTimeExtension requestTimeExtension = new RequestTimeExtension(timeExtensionDuration, CONSTANTS.Pending, selectedConductExam.getConduct_exam_id()); // create request time extension (extension time duration ,status of request Pending , Conduct Exam ID)
    	Message sendToServer = new Message(CONSTANTS.lectuerRequestTimeExtension,requestTimeExtension);
    	try 
    	{
			CEMSClientUI.chat.accept(sendToServer);
		} 
    	catch (Exception e) {e.printStackTrace();}
    	if(requestAcceptedSusseccfully)
    	{
    		requestAcceptedSusseccfully=false;
    		selectedConductExam.setTimeExtension(timeExtensionDuration); // change the duration of the selected conduct exam (should directly change the table view)
    		selectedConductExam.setRequestStatus(CONSTANTS.Pending); // change the request status (should directly change in the table view)
    		
	    	request_time_status_label.setText(selectedConductExam.getRequestStatus()); // set the label of the request status to be pending
	    	request_time_status_label.getStyleClass().setAll(selectedConductExam.getRequestStatus()); // set the style
	    	
    		error_msg_label.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
    		error_msg_label.setText("request sent successfully.");
    		applyCellStyleToRequestStatusColumn();
    		conduct_exams_table.refresh();
    		
    	}
    	else
    	{
    		error_msg_label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    		error_msg_label.setText("failed to send request.");
    	}
    }
    

    @FXML
    void refreshButtonAction(MouseEvent event) 
    {
    	error_msg_label.setText("");
    	Message sendToServer =  new Message(CONSTANTS.refreshRequestTimeExtensionStatus,conductExams); // send all conduct exam data (need only the conduct exam id) 
    	try 
    	{
			CEMSClientUI.chat.accept(sendToServer);
		} 
    	catch (Exception e) {e.printStackTrace();}
    	if(refreshedData==null)
    	{
    		error_msg_label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    		error_msg_label.setText("failed to refresh Ongoing Conduct Exam table.");
    		return;
    	}
    	for(ConductExam refreshData : refreshedData)
    	{
    		int indexToRefresh = conductExams.indexOf(refreshData);
    		conductExams.get(indexToRefresh).setDuration(refreshData.getDuration());
    		conductExams.get(indexToRefresh).setRequestStatus(refreshData.getRequestStatus());
    	}
    	conduct_exams_table.refresh();
    	if(selectedConductExam!=null)
    	{
    		request_time_status_label.setText(selectedConductExam.getRequestStatus());
    		request_time_status_label.getStyleClass().clear();
    		request_time_status_label.getStyleClass().add(selectedConductExam.getRequestStatus());
    	}
    	applyCellStyleToRequestStatusColumn();
	    conduct_exams_table.refresh();
    }
    
    
    
    
    @FXML
    void CreateExamToConductAction(MouseEvent event) 
    {
    	if(course_choice_box.getValue() == null)
    	{
    		choice_course_error_label.setText("Please choose a course");
    		return;
    	}
    	try 
    	{
    		choosenCourse = course_choice_box.getValue();
			CEMSClientUI.switchScenes(getClass().getResource("CreateConductExamScreen.fxml"));
		} catch (IOException e) {e.printStackTrace();}
    }
    
    

}

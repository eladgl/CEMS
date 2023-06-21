package teacherUi;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;


import Client.CEMSClient;
import Client.CEMSClientUI;
import clientGUI.ProfileScreenController;
import entities.Subject;
import entities.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import utilities.CONSTANTS;
import utilities.Message;
import javafx.scene.layout.StackPane;

public class TeacherMainScreenController 
{
	public static List<Subject> subjects = null;
	public static String username;
	public static User user;
	public static boolean is_loged_out = false;
	public static boolean logoutClick = false;
	
	@FXML
	private Label welcomeLabel;
	
    @FXML
    private BorderPane mainScreen;
	
    @FXML
    private ComboBox<String> subject_choice;
	
/*	@FXML
	private ImageView back_arrow_image;*/
	
	@FXML
	private ImageView user_profile_image;
	
	@FXML
	private Button xButton;
	
	@FXML
	private Label select_item_error_msg_label;
	
	
	
	@FXML
	void initialize() throws Exception 
	{
 
		fetchTeacherSubject(); // send request to server to fetch all subjects that lecturer is teaching ( it will be assign the list of subjects : subjects)
		// add subjects 
		for(Subject subject : subjects){
			subject_choice.getItems().add(subject.getName());
		}
		
		// add click on profile image event
		user_profile_image.setPickOnBounds(true);
		user_profile_image.setOnMouseClicked((MouseEvent e) -> {
			
			try {
				ProfileScreenController.user = user;
				CEMSClientUI.switchScenes(getClass().getResource("/clientGUI/ProfileScreen.fxml"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		});
		
		xButton.setPickOnBounds(true);
		xButton.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClient.logout(user.getUserName());
				CEMSClient.exitButton();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);		
		});
		
		welcomeLabel.setText("Welcome " + user.getFirstName());
	}
	
	private void fetchTeacherSubject() throws Exception 
	{
		// create Message to Server to fetch all Subject and Course this lecturer is teaching
		Message dataToServer = new Message(CONSTANTS.getSubjectAndCoursesTeacher,user.getUserName()); // send command to get teacher subject and course and in the data String of the userName of the teacher
		CEMSClientUI.chat.accept(dataToServer);
	}

	@FXML
	public void createTestAction() throws IOException {
		if(isChoiceBoxEmpty()) {
			select_item_error_msg_label.setText("Choose a subject please");
			return;
		}
		
		String choosenSubject = (String) subject_choice.getValue(); // get the Subject name from choicebox
		TestBankManagerController.choosenSubject = getSubjectFromList(choosenSubject) ; //inform the QuestionBankManager that the choosenSubject is choosenSubject
		// save the subject and pass it to the other controller class
		CEMSClientUI.switchScenes(getClass().getResource("TestBankScreen.fxml"));
	}
	
	@FXML
	public void manageQuestionBankAction() throws IOException 
	{
		if(isChoiceBoxEmpty()) 
		{
			select_item_error_msg_label.setText("Choose a subject please");
			return;
		}
		String choosenSubject = (String) subject_choice.getValue(); // get the Subject name from choicebox
		
		QuestionBankManagerController.choosenSubject = getSubjectFromList(choosenSubject) ; //inform the QuestionBankManager the correct chosen subject
		// save the subject and pass it to the other controller class
		CEMSClientUI.switchScenes(getClass().getResource("QuestionBankScreen.fxml"));
	}
	
	private Subject getSubjectFromList(String choosenSubject) // get name of the subjet and return instance of Subject from the list of the lecturer subjects
	{
		for(Subject subject : subjects)
			if(subject.getName().equals(choosenSubject))
				return subject;
		return null;
	}

	@FXML
	public void getStatisticsReportAction() throws IOException {
		if(isChoiceBoxEmpty()) 
		{
			select_item_error_msg_label.setText("Choose a subject please");
			return;
		}
		// save the subject and pass it to the other controller class
		String choosenSubject = (String) subject_choice.getValue(); // get the Subject name from choicebox
		ConductExamController.choosenSubject=getSubjectFromList(choosenSubject); // Inform the ConductExamController the correct chosen subject
		CEMSClientUI.switchScenes(getClass().getResource("StatisticsReportScreen.fxml"));
	}
	
	@FXML
    void changeScreenToGradeExams(MouseEvent event) throws IOException {
		if(isChoiceBoxEmpty()) {
			select_item_error_msg_label.setText("Choose a subject please");
			return;
		}
		String choosenSubject = (String) subject_choice.getValue(); // get the Subject name from choicebox
		GradeExamScreenController.subject = getSubjectFromList(choosenSubject) ; //inform the QuestionBankManager that the choosenSubject is choosenSubject
		CEMSClientUI.switchScenes(getClass().getResource("GradeExamsScreen.fxml"));
    }
	
	
	public static String getUsername()
	{
		return user.getUserName();
	}
	
    @FXML
    void conductExamsAction(MouseEvent event) throws IOException 
    {
		if(isChoiceBoxEmpty()) 
		{
			select_item_error_msg_label.setText("Choose a subject please");
			return;
		}
		String choosenSubject = (String) subject_choice.getValue(); // get the Subject name from choicebox
		ConductExamController.choosenSubject = getSubjectFromList(choosenSubject) ; //inform the QuestionBankManager that the choosenSubject is choosenSubject
		// save the subject and pass it to the other controller class
		CEMSClientUI.switchScenes(getClass().getResource("ConductExamScreen.fxml"));
    }
	
	
	public void logoutUserFromSystem(ActionEvent event) throws IOException{
		// send request for logout from the client UI
		logoutClick = true;
		CEMSClient.logout(user.getUserName());		
		if(is_loged_out) {
			//quit connection and wait
			try {
				CEMSClientUI.switchScenes(getClass().getResource("/clientGUI/LoginScreen.fxml"));
			} catch (IOException e) {
				System.out.println("Failed to wait\n");
				e.printStackTrace();
			}		
		}else {
			System.out.println("Failed to logout!");
		}
		logoutClick = false;
	}
	
	private boolean isChoiceBoxEmpty() {
		if(subject_choice.getValue()==null)
			return true;
		return false;
	} 
	 
}

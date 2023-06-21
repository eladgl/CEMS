package teacherUi;

import java.io.IOException;
import java.util.ArrayList;

import Client.CEMSClient;
import Client.CEMSClientUI;
import entities.Course;
import entities.Question;
import entities.Subject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utilities.CONSTANTS;
import utilities.Message;
import javafx.scene.control.ToggleGroup;

public class CreateQuestionController 
{
	private static double xOffset = 0;
	private static double yOffset = 0;
	
	public static boolean successfullyAdded=false;
	private ArrayList<CheckBox> checkBoxCourses;
	private static String selectedCourses=null;
	final ToggleGroup radioGroup = new ToggleGroup();
	
    @FXML
    private VBox VboxCourses;
    
    @FXML
    private Label subjectLabel;
    
    @FXML
    private Label relevantCourseLabel;
    
	@FXML
	private Button left_arrow_image;
	
	@FXML
	private Button xButton;

	@FXML
	private TextArea question_description;
	
	@FXML
	private TextArea answer1_text, answer2_text , answer3_text, answer4_text;
	
	@FXML
	private Button add_question_btn;

	@FXML
	private RadioButton r1, r2,r3,r4;

	@FXML
	private Label fill_item_error_message;
	
	
	@FXML
	void initialize() 
	{
		ArrayList<Course> relevantCourses = QuestionBankManagerController.choosenSubject.getCourses(); // get all the courses this lecturer teach for the choosen subject
		checkBoxCourses= new ArrayList<>(); // create new list of checkbox for the courses
		selectedCourses=""; // assign selected courses to be empty
		relevantCourseLabel.setText(selectedCourses);
		subjectLabel.setText(QuestionBankManagerController.choosenSubject.getName()); // set the subject in the title
		for (Course course : relevantCourses) 
		{
		    CheckBox checkBox = new CheckBox(course.getName());
		    checkBox.setOnAction(event -> AddRemoveSelectedCourse(checkBox));
		    checkBoxCourses.add(checkBox); // Add the created checkbox to the list
		}
		
        for (CheckBox checkBox : checkBoxCourses) // add all check box we created to the vbox in the scroll pane
        {
        	VboxCourses.getChildren().add(checkBox);
        }
        
        VboxCourses.setSpacing(10); // make all checkbox to have gap of 10 pixels
		
			
		left_arrow_image.setPickOnBounds(true);
		// back arrow click
		left_arrow_image.setOnMouseClicked((MouseEvent e) -> {
			
			try {
				CEMSClientUI.switchScenes(getClass().getResource("QuestionBankScreen.fxml"));
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
		
		r1.setToggleGroup(radioGroup);
		r2.setToggleGroup(radioGroup);
		r3.setToggleGroup(radioGroup);
		r4.setToggleGroup(radioGroup);
		
        String cssFile = "/resources/QuestionBankStyle.css";
        add_question_btn.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());
        add_question_btn.getStyleClass().addAll("save-Button");
		
	}
	
	private void AddRemoveSelectedCourse(CheckBox checkBox) 
	{
        String course = checkBox.getText();
        boolean isSelected = checkBox.isSelected();

        if (isSelected) 
        {
        	selectedCourses = selectedCourses +course+",";
        } 
        else 
        {
        	selectedCourses="";
        	for(CheckBox box : checkBoxCourses) // rebuild selectedCourses string only from selected checkbox courses
        	{
        		if(box.isSelected())
        			selectedCourses=selectedCourses+box.getText()+",";
        	}
        }
        String toDisplay = selectedCourses;
        if(!toDisplay.equals(""))
        	toDisplay=toDisplay.substring(0, toDisplay.length()-1);
        relevantCourseLabel.setText(toDisplay); //take selectedCourses without the ',' at the end
		
	}

	public void addQuestionButtonClicked(ActionEvent event) 
	{
		fill_item_error_message.setText("");
		String error ="";
		int errorCount=0;
		if(isTextAreasEmpty())
		{
			errorCount++;
			error=error+errorCount+". "+"Please Fill All Text\n";
		}
		
		if(!(r1.isSelected() || r2.isSelected() || r3.isSelected() || r4.isSelected()))
		{
			errorCount++;
			error=error+errorCount+". "+"Please Choose Correct Answer\n";
		}
		if(selectedCourses.equals(""))
		{
			errorCount++;
			error=error+errorCount+". "+"Please Choose one or more Courses\n";
		}
		if(errorCount!=0)
		{
			fill_item_error_message.setStyle("-fx-text-fill: red;");
			fill_item_error_message.setText(error);
			return;
		}
			
		Question question;
		Subject choosenSubject=QuestionBankManagerController.choosenSubject;
		String questionID=null,
			   subjectName=choosenSubject.getName(),
			   subjectCode= choosenSubject.getCode(),
			   courseName=selectedCourses.substring(0, selectedCourses.length()-1), // take selectedCourses without the ',' at the end,
			   questionText=question_description.getText(),
			   questionNumber=null,
			   authorName=TeacherMainScreenController.user.getFirstName()+" "+TeacherMainScreenController.user.getLastName(),
			   choise1=answer1_text.getText(),
			   choise2=answer2_text.getText(),
			   choise3=answer3_text.getText(),
			   choise4=answer4_text.getText();
		int correctAnswer;
		if(r1.isSelected())
			correctAnswer=1;
		else if(r2.isSelected())
			correctAnswer=2;
		else if(r3.isSelected())
			correctAnswer=3;
		else
			correctAnswer=4;
		String[] choices= {choise1,choise2,choise3,choise4};
		question = new Question(null, questionText, subjectName,subjectCode, courseName, questionNumber, authorName,null, choices, correctAnswer);
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(question); // add the new question
		data.add(TeacherMainScreenController.user.getUserName()); // add the lecturer userName so we can add this question to hes bank as well (we identify question_bank by the userName)
		Message sendDataToServer = new Message(CONSTANTS.AddQuestion, data);
		try 
		{
			CEMSClientUI.chat.accept(sendDataToServer);
		} 
		catch (Exception e) {e.printStackTrace();}
		if(successfullyAdded)
		{
			fill_item_error_message.setStyle("-fx-text-fill: blue;");
			fill_item_error_message.setText("Question Added Succesfully");
		}
		else
			fill_item_error_message.setText("Failed to add  question");
		// transfer question to question bank ...
		
		// remove all the text and check-box choices
		answer1_text.setText("");
		answer2_text.setText("");
		answer3_text.setText("");
		answer4_text.setText("");
				
		question_description.setText("");
		
	}
	
	private short mapBooleanToInteger(boolean bool) {
		if(bool)
			return 1;
		return 0;
	}
	

	private boolean isTextAreasEmpty() {
		return question_description.getText().isEmpty() | answer1_text.getText().isEmpty() |
				answer2_text.getText().isEmpty() | answer3_text.getText().isEmpty() | 
				answer4_text.getText().isEmpty();
	}	
}

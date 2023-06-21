package teacherUi;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Client.CEMSClient;
import Client.CEMSClientUI;
import clientGUI.UserLoginController;
import entities.Question;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import utilities.CONSTANTS;
import utilities.Message;
import utilities.checkEditQuestionInputHelper;

public class EditQuestionController implements Initializable 
{
	private static double xOffset = 0;
	private static double yOffset = 0;
	private static ToggleGroup toggleGroup;
	
	public static boolean successEdit=false;
	public static ArrayList<Question> questions = null;
	
	
    @FXML
    private RadioButton choice1;

    @FXML
    private RadioButton choice2;
    
    @FXML
    private RadioButton choice3;
    
    @FXML
    private RadioButton choice4;
    
    @FXML
    private TextArea choice1Text;
    
    @FXML
    private TextArea choice2Text;

    @FXML
    private TextArea choice3Text;

    @FXML
    private TextArea choice4Text;
    
    @FXML
    private Label subjectLabel;
    
    @FXML
    private Label IDLabel;

    @FXML
    private Label relevantCourseLabel;
    
	@FXML
	private TableView<Question> questions_table;
	
	@FXML
	private TableColumn<Question, String> id_column;
	
	@FXML
	private TableColumn<Question, String> number_column;
	
	@FXML
	private TableColumn<Question, String> description_column;
	
    @FXML
    private TableColumn<Question, String> course_col;
	
	@FXML
	private TextField number_field;
	
	@FXML
	private TextArea description_field;
	
	@FXML
	private Button left_arrow_image;
	
	@FXML
	private Button xButton;

    @FXML
    private Label editLabel;
	

    @FXML
    private Button editBtn;
	
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		id_column.setCellValueFactory(new PropertyValueFactory<Question, String>("ID"));
		number_column.setCellValueFactory(new PropertyValueFactory<Question, String>("questionNumber"));
		description_column.setCellValueFactory(new PropertyValueFactory<Question, String>("questionDescription"));
		course_col.setCellValueFactory(new PropertyValueFactory<Question, String>("courseName"));
		setUpTable();
		subjectLabel.setText(QuestionBankManagerController.choosenSubject.getName());
	    toggleGroup = new ToggleGroup();
	    choice1.setToggleGroup(toggleGroup);
	    choice2.setToggleGroup(toggleGroup);
	    choice3.setToggleGroup(toggleGroup);
	    choice4.setToggleGroup(toggleGroup);
		left_arrow_image.setPickOnBounds(true);// so we can click on it
		
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
	
	@FXML
	void rowClicked(MouseEvent event) // when question pressed in the table insert all data to the fields
	{
		editLabel.setText(""); // if user click on question we reset the errors
		Question clicked_question = questions_table.getSelectionModel().getSelectedItem(); 
		IDLabel.setText(clicked_question.getID());
		number_field.setText(clicked_question.getQuestionNumber());
		description_field.setText(clicked_question.getQuestionDescription());
		String[] choices = clicked_question.getOptionsText(); // get all choices and insert them to the fields
		choice1Text.setText(choices[0]);
		choice2Text.setText(choices[1]);
		choice3Text.setText(choices[2]);
		choice4Text.setText(choices[3]);
		RadioButton[] radioChoices = {choice1,choice2,choice3,choice4};
		toggleGroup.selectToggle(radioChoices[clicked_question.getCorrectAnswer()-1]); // select the radio button  that represent the correct answer ( in that way with the toggle gruop all the other buttons will be unselcted)
		relevantCourseLabel.setText(clicked_question.getCourseName());
	}
	
	@FXML
	void backImageClicked(MouseEvent event) {
		try {
			CEMSClientUI.switchScenes(getClass().getResource("QuestionBankScreen.fxml"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
    @FXML
    void editAction(ActionEvent event) 
    {
    	String idText,numberText,DescreptionText;
    	boolean validQnumber=false;
    	int indexOfOldQuestion = -1;
    	// save the data user provided via the UI
    	idText = IDLabel.getText(); 
    	numberText =number_field.getText();
    	DescreptionText = description_field.getText();
    	// check validation of question ID and question number
    	validQnumber= checkEditQuestionInputHelper.ValidQuestionNumber(numberText);
    	editLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");// just make the label red for errors messages
    	if(!validQnumber)
    	{
    		editLabel.setText("invalid Question Number");
    		return;
    	}
    	//check if the given Question number already exist for the subject of the question
    	String newID = idText.substring(0,2)+numberText; // take the two character (the subject of the question) + the new given QuestionNumber
    	for(int i=0 ;i<questions.size();i++)
    	{
    		if(!questions.get(i).getID().equals(idText) && questions.get(i).getID().equals(newID)) // we search if there is question id as the new id and we pass on the question with the old id
    		{
    			editLabel.setText("Question Number already exist in Subject: "+questions.get(i).getSubject());
    			return;
    		}
    		if(questions.get(i).getID().equals(idText)) // save the index of the old question (in order to change it later)
    			indexOfOldQuestion=i;
    	}

    	if(!choice1.isSelected() && !choice2.isSelected() && !choice3.isSelected() && !choice4.isSelected())
    	{
    		editLabel.setText("must choose correct Answer");
    		return;
    	}
    	String error_msg = validateTextFields() ;
    	if(!error_msg.isEmpty())
    	{
    		editLabel.setText(error_msg);
    		return;
    	}
    	// after pass all checks send request to Server to change the question + create the new question after the update
    	String subjectName=questions.get(indexOfOldQuestion).getSubject(), // this data didnt changed we get if from the old question
    			subjectCode =questions.get(indexOfOldQuestion).getSubjectCode(),
    			courseName = questions.get(indexOfOldQuestion).getCourseName(),
    			authorName = questions.get(indexOfOldQuestion).getAuthorName();
    	String[] choices = new String[4];
    	choices[0]= choice1Text.getText();
    	choices[1]= choice2Text.getText();
    	choices[2]= choice3Text.getText();
    	choices[3]= choice4Text.getText();
    	int correctAnswer = getCorrectAnswer();
    	Question newQuestion = new Question(newID, DescreptionText,subjectName, subjectCode, courseName, numberText, authorName,null,choices,correctAnswer);
    	Question oldQuestion = new Question(idText,null,null,null,null,null,null,null, null, 0); // send the id of the question to update in database
    	ArrayList<Question> questionData = new ArrayList<>();
    	questionData.add(oldQuestion); // add the id of the question we need to update
    	questionData.add(newQuestion); // add the data we need to update
    	Message dataToServer = new Message(CONSTANTS.EditQuestion,questionData);
    	try {
			CEMSClientUI.chat.accept(dataToServer);
		} catch (Exception e) 
    	{
			e.printStackTrace();
		}
    	questions.set(indexOfOldQuestion, newQuestion); // update the local questions array
    	editLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
    	editLabel.setText("Question seuccefuly updated");
    	IDLabel.setText(newID); // in case the user changed the question number we will update the new id question in the text field
    	setUpTable(); // call the function to update question table in the UI
    	
    }
    
	
	private int getCorrectAnswer() 
	{
		if(choice1.isSelected())
			return 1;
		if(choice2.isSelected())
			return 2;
		if(choice3.isSelected())
			return 3;
		else
			return 4;
	}
	
	private String validateTextFields() 
	{
	    StringBuilder errorMsg = new StringBuilder();
	    int errorCount = 1;
	    
	    if (choice1Text.getText().isEmpty() || choice2Text.getText().isEmpty() || choice3Text.getText().isEmpty() || choice4Text.getText().isEmpty()) 
	    {
	        errorMsg.append(errorCount++).append(". one of the choices text is empty.\n");
	    }
	    if (number_field.getText().isEmpty()) 
	    {
	        errorMsg.append(errorCount++).append(". Number field is empty.\n");
	    }
	    
	    if (description_field.getText().isEmpty()) 
	    {
	        errorMsg.append(errorCount++).append(". Description field is empty.\n");
	    }
	    
	    return errorMsg.toString();
	}

	private void setUpTable() 
	{
		questions_table.getItems().clear();
		if(questions == null)
			questions_table.getItems().add(new Question("10", "Test", "Test", "Test", "Test", "Test",null,null, null, 1));
		else
			questions_table.getItems().addAll(questions);
	}

}

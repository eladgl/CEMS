package teacherUi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Client.CEMSClient;
import Client.CEMSClientUI;
import clientGUI.UserLoginController;
import entities.Course;
import entities.Question;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import utilities.CONSTANTS;
import utilities.Message;

public class ExistingQuestionInsertController 
{
	private ArrayList<Question> eligibleQuestionsToAdd =null, // save all questions that not exist in the personal bank questions
			                    myQuestionBank = null;
	private Map<VBox,Question> questionInVbox=null;
	public static boolean successfulySavedChanges = false;
	
    @FXML
    private VBox VboxQuestion;
	
    @FXML
    private Label answerA;

    @FXML
    private Label answerB;

    @FXML
    private Label answerC;

    @FXML
    private Label answerD;
    
    @FXML
    private Label questionDescription;

    @FXML
    private Label relevantCoursesLabel;
    
    @FXML
    private Label question_number_label;
    
    @FXML
    private Label error_msg;
    
    @FXML
    private TableView<Question> myBankTavleView;
    
    @FXML
    private TableColumn<Question, String> number_col_myBank;
    
    @FXML
    private TableColumn<Question, String> description_cal_myBank;

    @FXML
    private TableColumn<Question, String> course_col_mayBank;
    
    @FXML
    private TableView<Question> generalBankTableView;

    @FXML
    private TableColumn<Question, String> number_col_general;
    
    @FXML
    private TableColumn<Question, String> description_col_general;
    
    @FXML
    private TableColumn<Question, String> course_col_general;


    @FXML
    private ImageView left_addBtn;

    @FXML
    private Button left_arrow_image;

    @FXML
    private ImageView right_addBtn;

    @FXML
    private Label subjectLabel;

    @FXML
    private Button xButton;
    
    @FXML
    private Button saveBtn;

	@FXML
	void initialize() 
	{
		setUpTableViews();	
		displayTableData(myBankTavleView,myQuestionBank); // load data for my bank table
		displayTableData(generalBankTableView,eligibleQuestionsToAdd); // load data for general bank table
		subjectLabel.setText(QuestionBankManagerController.choosenSubject.getName()); // SET the subject label
		left_arrow_image.setPickOnBounds(true);
		// back arrow click
		left_arrow_image.setOnMouseClicked((MouseEvent e) -> 
		{
			
			try {
				CEMSClientUI.switchScenes(getClass().getResource("QuestionBankScreen.fxml"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		});
		
		xButton.setPickOnBounds(true);
		xButton.setOnMouseClicked((MouseEvent e) -> 
		{
			try {
				CEMSClient.logout(TeacherMainScreenController.getUsername());
				CEMSClient.exitButton();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);		
		});
	}


	private void setUpTableViews() {
		questionInVbox= new HashMap<>(); // this map contain only one pair <Vbox , question inside Vbox>
		questionInVbox.put(VboxQuestion, null); // at start there is no question displayed inside vbox so we put null
		getEligibleQuestionsToAdd(); // assign all eligible question to ADD to eligibleQuestionsToAdd list
		getMyQuestionBank(); // copy myBank from questionBanMenagerCotroller to myQuestionBank in here 
		number_col_myBank.setCellValueFactory(new PropertyValueFactory<>("questionNumber")); // setting the tables
		description_cal_myBank.setCellValueFactory(new PropertyValueFactory<>("questionDescription"));
		course_col_mayBank.setCellValueFactory(new PropertyValueFactory<>("courseName"));
		number_col_general.setCellValueFactory(new PropertyValueFactory<>("questionNumber"));
		description_col_general.setCellValueFactory(new PropertyValueFactory<>("questionDescription"));
		course_col_general.setCellValueFactory(new PropertyValueFactory<>("courseName"));
		
		myBankTavleView.setOnMouseClicked(event -> { // event handle when choosing question from table will display in the vbox
	        if (event.getClickCount() == 1) {
	            // Check if the table is empty
	            if (!myBankTavleView.getItems().isEmpty()) {
	                // Get the selected item from the table view
	                Question selectedQuestion = myBankTavleView.getSelectionModel().getSelectedItem();

	                // Perform the desired action with the selected item
	                // For example, you can call a method or trigger an event
	                if(selectedQuestion!=null) // in case user didnt clicked on a row (didnt choose a question)
	                	displayQuestion(selectedQuestion);
	            }
	        }
	    });
		
		generalBankTableView.setOnMouseClicked(event -> {
	        if (event.getClickCount() == 1) {
	            // Check if the table is empty
	            if (!generalBankTableView.getItems().isEmpty()) {
	                // Get the selected item from the table view
	                Question selectedQuestion = generalBankTableView.getSelectionModel().getSelectedItem();
	                
	                // Perform the desired action with the selected item
	                // For example, you can call a method or trigger an event
	                if(selectedQuestion!=null)
	                	displayQuestion(selectedQuestion);
	            }
	        }
	    });
	}
	
	
	private void displayQuestion(Question selectedQuestion) 
	{
		error_msg.setText(""); // reset error message
		questionInVbox.put(VboxQuestion, selectedQuestion); // update the Vbox now hold selectedQuestion
		String[] choices = selectedQuestion.getOptionsText();
		answerA.setStyle("-fx-font-weight: normal; -fx-text-fill: red;"); // reset all labels
		answerB.setStyle("-fx-font-weight: normal; -fx-text-fill: red;");
		answerC.setStyle("-fx-font-weight: normal; -fx-text-fill: red;");
		answerD.setStyle("-fx-font-weight: normal; -fx-text-fill: red;");
		// set all text to display question
		questionDescription.setText(selectedQuestion.getQuestionDescription());
		answerA.setText("1. "+choices[0]);
		answerB.setText("2. "+choices[1]);
		answerC.setText("3. "+choices[2]);
		answerD.setText("4. "+choices[3]);
		Label[] labels = {answerA,answerB,answerC,answerD}; // instead of if else
		labels[selectedQuestion.getCorrectAnswer()-1].setStyle("-fx-font-weight: bold; -fx-text-fill: #008000;");
		relevantCoursesLabel.setText(selectedQuestion.getCourseName());
		question_number_label.setText(selectedQuestion.getQuestionNumber());
		
		
	}

	private void getEligibleQuestionsToAdd()
	{
		eligibleQuestionsToAdd =  new ArrayList<>();
		ArrayList<Course> lecturerTeachingCourses = QuestionBankManagerController.choosenSubject.getCourses();
		ArrayList<Question> generalBank = QuestionBankManagerController.generalQuestions
				  ,myBank = QuestionBankManagerController.myBankQuestion;
		for(Question question : generalBank) // go over all question (for the relevant subject)
			if( !myBank.contains(question)) // if the question is not in myBank is eligle to add
				eligibleQuestionsToAdd.add(question);
		
		int initialSize = eligibleQuestionsToAdd.size();
		for (int i = 0; i < initialSize; i++)  // filter all the questions that dont have relevant courses this lecturer teach
		{
		    Question question = eligibleQuestionsToAdd.get(i);
		    boolean toDelete = true;
		    for (Course course : lecturerTeachingCourses) 
		    {
		        if (question.getCourseName().contains(course.getName())) 
		        {
		            toDelete = false;
		            break;
		        }
		    }
		    if (toDelete) {
		        eligibleQuestionsToAdd.remove(i);
		        i--; // Decrement the loop counter since the list size has decreased
		        initialSize--; // Update the initial size of the list
		    }
		}

	}
	
	
	private void getMyQuestionBank()
	{
		this.myQuestionBank = new ArrayList<>();
		for(Question questin :QuestionBankManagerController.myBankQuestion )
			myQuestionBank.add(questin);
	}
	
	private void displayTableData(TableView<Question> tableView,ArrayList<Question> questionList) 
	{
		tableView.getItems().clear();
	    for (Question question : questionList) 
	    {
	    	tableView.getItems().add(question);
	    }
	    tableView.refresh();
	}
	
    @FXML
    void left_addBtnAction(MouseEvent event) 
    {
    	error_msg.setText(""); // reset error message
    	Question selectedQuestion = questionInVbox.get(VboxQuestion); // get the question in the VBox
    	if(!myQuestionBank.contains(selectedQuestion) && selectedQuestion!=null) // the selected question not in the presonal question bank
    	{
            int selectedIndex = generalBankTableView.getSelectionModel().getSelectedIndex(); // get the last selected row
            if(selectedIndex==generalBankTableView.getItems().size()-1 ||selectedIndex==-1 ) // in case the selected row was the last after the change we want the last row in the table
            	selectedIndex=generalBankTableView.getItems().size()-2;
    		
    		myQuestionBank.add(selectedQuestion); // add the selected question to the presonal question bank
    		eligibleQuestionsToAdd.remove(selectedQuestion); // delete the question from the list of the eligible questions to add
    		//update the question in both tables
    		displayTableData(myBankTavleView,myQuestionBank);
    		displayTableData(generalBankTableView,eligibleQuestionsToAdd);
    		
            // Select the last selected index if it is still valid
            if (selectedIndex >= 0 && selectedIndex < generalBankTableView.getItems().size()) 
            {
            	generalBankTableView.getSelectionModel().select(selectedIndex);
            	
                generalBankTableView.scrollTo(selectedIndex);
                generalBankTableView.requestFocus();

                // Programmatically simulate a mouse click event on the selected row
                MouseEvent clickEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED,0, 0, 0, 0,MouseButton.PRIMARY,1,
                		true, true, true, true,
                        true, true, true, true,
                        true, true, null
                );
                generalBankTableView.fireEvent(clickEvent);
            	
            }
    		
    	}
    }

    @FXML
    void right_addBtnAction(MouseEvent event) 
    {
    	error_msg.setText(""); // reset error message
        Question selectedQuestion = questionInVbox.get(VboxQuestion); // get the question in the VBox
        

        if (!eligibleQuestionsToAdd.contains(selectedQuestion) && selectedQuestion!=null) // if the selected question not in the personal question bank
        { 
            eligibleQuestionsToAdd.add(selectedQuestion); // add the selected question to the personal question bank
            myQuestionBank.remove(selectedQuestion); // delete the question from the list of the eligible questions to add
            
            // Store the selected index
            int selectedIndex = myBankTavleView.getSelectionModel().getSelectedIndex(); // get the last selected row
            if(selectedIndex==myBankTavleView.getItems().size()-1 || selectedIndex==-1) // in case the selected row was the last after the change we want the last row in the table
            	selectedIndex=myBankTavleView.getItems().size()-2;
            
            // Update the question in both tables
            displayTableData(myBankTavleView, myQuestionBank);
            displayTableData(generalBankTableView, eligibleQuestionsToAdd);
            
            // Select the last selected index if it is still valid
            
            if (selectedIndex >= 0 && selectedIndex < myBankTavleView.getItems().size()) // this is when user moved question from bank to another it will automatically select the next question
            {
                myBankTavleView.getSelectionModel().select(selectedIndex);
                myBankTavleView.scrollTo(selectedIndex);
                myBankTavleView.requestFocus();

                // Programmatically simulate a mouse click event on the selected row
                MouseEvent clickEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED,0, 0, 0, 0,MouseButton.PRIMARY,1,
                		true, true, true, true,
                        true, true, true, true,
                        true, true, null
                );
                myBankTavleView.fireEvent(clickEvent);
            }
        }
    }
    
    @FXML
    void saveBtnAction(ActionEvent event) throws Exception 
    {
    	ArrayList<Object> data = new ArrayList<>(); 
    	data.add(UserLoginController.user.getUserName());
    	data.add(myQuestionBank);
    	data.add(QuestionBankManagerController.choosenSubject);
    	Message sendToServer = new Message(CONSTANTS.ChangePersonalBankQuestion,data);
    	CEMSClientUI.chat.accept(sendToServer);
    	if(successfulySavedChanges)
    	{
    		error_msg.setStyle("-fx-text-fill: blue;");
    		error_msg.setText("Changes Saved successfully");
    		successfulySavedChanges=false;
    		QuestionBankManagerController.myBankQuestion=myQuestionBank; // locally save the changes
    	}
    	else
    	{
    		error_msg.setStyle("-fx-text-fill: red;");
    		error_msg.setText("unable to save changes");
    	}
    	initialize();	
    }
    
	// all the 4 event action for the hover left right add buttons
	@FXML
	void enterMouseLeftAdd(MouseEvent event) 
	{
		Image hoverImage = new Image(this.getClass().getResourceAsStream("/images/left_add_hover.png"));
	    left_addBtn.setImage(hoverImage);
	}

	
	@FXML
	void exitMouseLeftAdd(MouseEvent event) 
	{
		Image hoverImage = new Image(this.getClass().getResourceAsStream("/images/left_add.png"));
	    left_addBtn.setImage(hoverImage);
	}

	@FXML
	void enterMouseRightAdd(MouseEvent event) 
	{
		Image hoverImage = new Image(this.getClass().getResourceAsStream("/images/right_add_hover.png"));
	    right_addBtn.setImage(hoverImage);
	}

	@FXML
	void exitMouseRightAdd(MouseEvent event) 
	{
		Image hoverImage = new Image(this.getClass().getResourceAsStream("/images/right_add.png"));
	    right_addBtn.setImage(hoverImage);
	}
	
}

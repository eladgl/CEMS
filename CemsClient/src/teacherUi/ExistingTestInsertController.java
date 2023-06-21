package teacherUi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Client.CEMSClient;
import Client.CEMSClientUI;
import clientGUI.UserLoginController;
import entities.Course;
import entities.Exam;
import entities.Question;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utilities.CONSTANTS;
import utilities.Message;
import utilities.TeacherMethodHelper;

public class ExistingTestInsertController 
{
	
	private static ArrayList<Exam> presonalBank=null, 
								   ExamsToInsert=null;
	private static Exam selectedExam = null;
	
	public static boolean successfulySavedChanges=false;
	// general bank table view
	@FXML
	private TableColumn<Exam, String> general_course_col;

	@FXML
	private TableColumn<Exam, String> general_name_col;

	@FXML
	private TableColumn<Exam, String> general_number_col;

	@FXML
	private TableView<Exam> examToInsert_bank_tableView;
	 // Personal bank table view
	@FXML
	private TableColumn<Exam, String> my_bank_course_col;

	@FXML
	private TableColumn<Exam, String> my_bank_name_col;

	@FXML
	private TableColumn<Exam, String> my_bank_number_col;

	@FXML
	private TableView<Exam> personal_bank_tableView;

	@FXML
	private Label lecturer_description;

	// display exam
	@FXML
	private VBox questions_VBox;

	@FXML
	private Label student_description;

	@FXML
	private Label subjectLabel;
	    
	@FXML
	private Label Time_label;

	@FXML
	private Label courseLabel;
		
	@FXML
	private Label exam_name;

	@FXML
	private Button left_arrow_image;
	    
	@FXML
	private Button xButton;
	
    @FXML
    private ImageView left_addBtn;
    
    @FXML
    private ImageView right_addBtn;
    
    @FXML
    private Button saveBtn;
    
    @FXML
    private Label error_msg;

	    


	@FXML
	void initialize() 
	{
		fetchExams();
		setUpTableView();
		setUpTableView();
		selectedExam=null;
		subjectLabel.setText(TestBankManagerController.choosenSubject.getName());
		courseLabel.setText(TestBankManagerController.choosenCourse.getName());
		displayTableData(personal_bank_tableView,presonalBank);
		displayTableData(examToInsert_bank_tableView,ExamsToInsert);
		
		
		
		
		
		
		
		left_arrow_image.setPickOnBounds(true);
		// back arrow click
		left_arrow_image.setOnMouseClicked((MouseEvent e) -> {
			
			try {
				CEMSClientUI.switchScenes(getClass().getResource("TestBankScreen.fxml"));
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
	
	private void displayTableData(TableView<Exam> examTableView, ArrayList<Exam> ExamsList) // get table get examList and set the data in the table
	{
		examTableView.getItems().clear();
	    for (Exam exam : ExamsList) 
	    {
	    	examTableView.getItems().add(exam);
	    }
	    examTableView.refresh();
	}

	private void setUpTableView() 
	{
		general_course_col.setCellValueFactory(new PropertyValueFactory<>("course")); // setting the tables
		general_name_col.setCellValueFactory(new PropertyValueFactory<>("testName"));
		general_number_col.setCellValueFactory(new PropertyValueFactory<>("testNumber"));
		
		my_bank_course_col.setCellValueFactory(new PropertyValueFactory<>("course")); // setting the tables
		my_bank_name_col.setCellValueFactory(new PropertyValueFactory<>("testName"));
		my_bank_number_col.setCellValueFactory(new PropertyValueFactory<>("testNumber"));
		
		personal_bank_tableView.setOnMouseClicked(event -> { // event handle when choosing Exam from table will display in the ScrollPane
	        if (event.getClickCount() == 1) {
	            // Check if the table is empty
	            if (!personal_bank_tableView.getItems().isEmpty()) {
	                // Get the selected item from the table view
	            	selectedExam = personal_bank_tableView.getSelectionModel().getSelectedItem();

	                // Perform the desired action with the selected item
	                // For example, you can call a method or trigger an event
	                if(selectedExam!=null) // in case user didnt clicked on a row (didnt choose a question)
	                	DisplayExamView(selectedExam);
	            }
	        }
	    });
		
		examToInsert_bank_tableView.setOnMouseClicked(event -> {
	        if (event.getClickCount() == 1) {
	            // Check if the table is empty
	            if (!examToInsert_bank_tableView.getItems().isEmpty()) {
	                // Get the selected item from the table view
	            	selectedExam = examToInsert_bank_tableView.getSelectionModel().getSelectedItem();
	                
	                // Perform the desired action with the selected item
	                // For example, you can call a method or trigger an event
	                if(selectedExam!=null)
	                	DisplayExamView(selectedExam);
	            }
	        }
	    });
		
		
	}
	


	private void fetchExams() 
	{
		try 
		{
			TestBankManagerController.fetchExams();// force the testBankManagerController to get the most update generalExams and MyEXAM lists from DB
		} 
		catch (Exception e) {e.printStackTrace();} 
		ArrayList<Exam> copyOfGeneralExams = new ArrayList<>(TestBankManagerController.generalExams); // get copy of generalExam and presonalBankExam
		ExamsToInsert = new ArrayList<>(); //create empty array list for the exams to insert
		presonalBank = new ArrayList<>(TestBankManagerController.myExams);
		// filter them by course
		ArrayList<Course> courseToFilter = new ArrayList<>();
		courseToFilter.add(TestBankManagerController.choosenCourse);
		TeacherMethodHelper.filterExamsByCourses(courseToFilter, copyOfGeneralExams);
		TeacherMethodHelper.filterExamsByCourses(courseToFilter, presonalBank);
		// each exam that appear in personal bank we delete him from ExamsToInsert
		for(Exam examToInsert : copyOfGeneralExams )
		{
			if(!presonalBank.contains(examToInsert)) // if the exam in the generaExams bank dont exist in the presonal bank he is ok to insert to presonal bank
				ExamsToInsert.add(examToInsert); // add exam to ExamsToInsert
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
	
    @FXML
    void saveBtnAction(ActionEvent event) throws Exception 
    {
    	ArrayList<Object> data = new ArrayList<>(); 
    	data.add(UserLoginController.user.getUserName()); // send userName of lecturer to recognize the personal bank exam
    	data.add(presonalBank); // send the new change we need to do the personal bank exam
    	data.add(TestBankManagerController.choosenSubject); // send the subject of the exams
    	data.add(TestBankManagerController.choosenCourse); // send the course of the exams  (had to send course and subject in case the personalBank is empty so there is only deleting exam we need to know which exams)
    	Message sendToServer = new Message(CONSTANTS.ChangePersonalBankExams,data);
    	CEMSClientUI.chat.accept(sendToServer);
    	if(successfulySavedChanges)
    	{
    		error_msg.setStyle("-fx-text-fill: blue;");
    		error_msg.setText("Changes Saved successfully");
    		successfulySavedChanges=false;
    	}
    	else
    	{
    		error_msg.setStyle("-fx-text-fill: red;");
    		error_msg.setText("unable to save changes");
    	}
    	initialize();	
    }
    
    @FXML
    void left_addBtnAction(MouseEvent event) 
    {
    	error_msg.setText(""); // reset error message
    	if(!presonalBank.contains(selectedExam) && selectedExam!=null) // the selected Exam not in the personal Exam bank
    	{
            int selectedIndex = examToInsert_bank_tableView.getSelectionModel().getSelectedIndex(); // get the last selected row
            if(selectedIndex==examToInsert_bank_tableView.getItems().size()-1 ||selectedIndex==-1 ) // in case the selected row was the last after the change we want the last row in the table
            	selectedIndex=examToInsert_bank_tableView.getItems().size()-2;
    		
            presonalBank.add(selectedExam); // add the selected Exam to the personal question bank
            ExamsToInsert.remove(selectedExam); // delete the question from the list of the eligible questions to add
    		//update the question in both tables
    		displayTableData(personal_bank_tableView,presonalBank);
    		displayTableData(examToInsert_bank_tableView,ExamsToInsert);
    		
            // Select the last selected index if it is still valid
            if (selectedIndex >= 0 && selectedIndex < examToInsert_bank_tableView.getItems().size()) 
            {
            	examToInsert_bank_tableView.getSelectionModel().select(selectedIndex);
            	
            	examToInsert_bank_tableView.scrollTo(selectedIndex);
            	examToInsert_bank_tableView.requestFocus();

                // Programmatically simulate a mouse click event on the selected row
                MouseEvent clickEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED,0, 0, 0, 0,MouseButton.PRIMARY,1,
                		true, true, true, true,
                        true, true, true, true,
                        true, true, null
                );
                examToInsert_bank_tableView.fireEvent(clickEvent);
            	
            }
    		
    	}
    }

    @FXML
    void right_addBtnAction(MouseEvent event) 
    {
    	error_msg.setText(""); // reset error message

        if (!ExamsToInsert.contains(selectedExam) && selectedExam!=null) // if the selected Exam not in the ExanToInsert bank and not null
        { 
        	ExamsToInsert.add(selectedExam); // add the selected Exam to the ExamToInsert  bank
        	presonalBank.remove(selectedExam); // delete the Exam from the list of the personal Exam Bank 
            
            // Store the selected index
            int selectedIndex = personal_bank_tableView.getSelectionModel().getSelectedIndex(); // get the last selected row
            if(selectedIndex==personal_bank_tableView.getItems().size()-1 || selectedIndex==-1) // in case the selected row was the last after the change we want the last row in the table
            	selectedIndex=personal_bank_tableView.getItems().size()-2;
            
            // Update the question in both tables
            displayTableData(personal_bank_tableView, presonalBank);
            displayTableData(examToInsert_bank_tableView, ExamsToInsert);
            
            // Select the last selected index if it is still valid
            
            if (selectedIndex >= 0 && selectedIndex < personal_bank_tableView.getItems().size()) // this is when user moved Exam from bank to another it will automatically select the next Exam
            {
            	personal_bank_tableView.getSelectionModel().select(selectedIndex);
            	personal_bank_tableView.scrollTo(selectedIndex);
            	personal_bank_tableView.requestFocus();

                // Programmatically simulate a mouse click event on the selected row
                MouseEvent clickEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED,0, 0, 0, 0,MouseButton.PRIMARY,1,
                		true, true, true, true,
                        true, true, true, true,
                        true, true, null
                );
                personal_bank_tableView.fireEvent(clickEvent);
            }
        }
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

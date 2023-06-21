package teacherUi;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.poi.util.SystemOutLogger;

import Client.CEMSClient;
import Client.CEMSClientUI;
import clientGUI.UserLoginController;
import entities.Course;
import entities.Exam;
import entities.Question;
import entities.Subject;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utilities.CONSTANTS;
import utilities.Message;

public class CreateNewTestControllerImproved implements Initializable
{
	public static Question [] questions=null;
	private HashMap<VBox, Question> map_VBox_to_Question;
	private VBox currently_selected_VBox = null;
	private ArrayList<TextField> pointAreas = null;
	public static boolean testAddedSuccesfully=false;
	
    @FXML
    private Button createTextBtn;
    
    @FXML
    private Label messageLabel;
    
    @FXML
    private Label courseLabel;
    
    @FXML
    private Label subjectLabel;
    
    @FXML
    private Label totalPointsLabel;
    
    @FXML
    private TextField numberOfQuestionText;
    
	@FXML
	private Button left_arrow_image;
	
	@FXML
	private Button xButton;
	
    @FXML
    private TextArea studentrDescriptionText;
    
    @FXML
    private TextField testNameText;
    
    @FXML
    private TextField hours_time;
    
    @FXML
    private TextField minutes_time;
    
    @FXML
    private TextField seconds_time;

    @FXML
    private TextArea teacherDescriptionText;
    
    @FXML
    private TextField timeTestText;

	@FXML
	private ScrollPane scrollPane;
	
	@FXML
	private VBox parent_VBox_container;
	
	@FXML
	private TableView<Question> questions_table;
	
	@FXML
	private TableColumn<Question, String> number_column;
	
	@FXML
	private TableColumn<Question, String> description_column;
	
	@FXML
	private TableColumn<Question, String> course_column;
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		subjectLabel.setText(TestBankManagerController.choosenSubject.getName());
		courseLabel.setText(TestBankManagerController.choosenCourse.getName());
		getQuestionByCourse(); // get all relevant questions by course from myBankQuestion and insert it to the array questions
		left_arrow_image.setPickOnBounds(true);
		xButton.setPickOnBounds(true);
		cleanScreen();
		map_VBox_to_Question= new HashMap<>(); // create new hashamp
		pointAreas= new ArrayList<>();
		currently_selected_VBox=null ; //reset selected vbox
		
		parent_VBox_container.getChildren().clear(); // clean container
		parent_VBox_container.setSpacing(10); // Set spacing between VBoxes
		scrollPane.setContent(parent_VBox_container);
		
		// init table
		number_column.setCellValueFactory(new PropertyValueFactory<Question, String>("questionNumber"));
		description_column.setCellValueFactory(new PropertyValueFactory<Question, String>("questionDescription"));
		course_column.setCellValueFactory(new PropertyValueFactory<Question, String>("courseName"));
		setUpTable();

		
	}

    private void cleanScreen() 
    {
    	totalPointsLabel.setText("0");
        numberOfQuestionText.clear();
        studentrDescriptionText.clear();
        testNameText.clear();
        hours_time.setText("00");
        minutes_time.setText("00");
        seconds_time.setText("00");
        teacherDescriptionText.clear();
	}

	private void getQuestionByCourse()
    {

    	ArrayList<Question> questionsByCourse = new ArrayList<>();
    	for(Question question :TestBankManagerController.myBankQuestoins) //go over all the question the personal bank (myBankQuestion have only chossenSubject questions)
    	{
    		if(question.getCourseName().contains(TestBankManagerController.choosenCourse.getName())) // if the question have the relevant chosen course name we add that question to the list
    			questionsByCourse.add(question);
    	}
    	questions = questionsByCourse.toArray(new Question[questionsByCourse.size()]); // set into the array questions all the relevant questions
		
	}

	@FXML
    void CreateTestAction(ActionEvent event) throws Exception 
    {
    	String StudentDescription = studentrDescriptionText.getText(),
    		   teacherDescription =teacherDescriptionText.getText(),
    		   TestName=testNameText.getText(),
    		   seconds = seconds_time.getText(),
    		   minutes=minutes_time.getText(),
    		   hours=hours_time.getText();
    	Subject subject=TestBankManagerController.choosenSubject; // gather course and subject from manage bank  controller
        Course   course = TestBankManagerController.choosenCourse;
    	// check all cases before send
    	if(!aprroveCreateExam(StudentDescription, teacherDescription, TestName, seconds,minutes,hours) ) // check all cases before create the exam that will be send to the server
    		return;
    	System.out.println(course);
    	long parsedHours = Long.parseLong(hours);
    	long parsedMinutes = Long.parseLong(minutes);
    	long parsedSeconds = Long.parseLong(seconds);

    	Duration duration = Duration.ofHours(parsedHours)
    	        .plusMinutes(parsedMinutes)
    	        .plusSeconds(parsedSeconds);
    	Exam examToCreate = buildExamFromGUI(TestName,teacherDescription,StudentDescription,duration,subject,course); // build exam instance based on GUI data
    	Message sendDataToServer = new Message(CONSTANTS.AddCreatedExam,examToCreate);
    	CEMSClientUI.chat.accept(sendDataToServer); // send that to server
    	updateMessageLabel( );
    	initialize(null, null);
    }

	private boolean aprroveCreateExam(String StudentDescription, String teacherDescription, String TestName,String seconds, String minutes, String hours) 
	{
		String problem ="";
    	int numberOfProblems=0;
		if(!isPositiveNumberOnly(numberOfQuestionText.getText())) // in case user didn't enter correct value of  number of questions
		{
			numberOfProblems++;
			problem=String.format("%s%d.invalid number of questions\n", problem,numberOfProblems); // add message for the specific problem
		}
		else if(!TestIsFull()) // the number of question the enter is correct check if test is not full
		{
			numberOfProblems++;
			problem=String.format("%s%d.not enough question have been chossen\nToo much question have choosen\n", problem,numberOfProblems); // add message for the specific problem
		}
		else if(!ValidPointContribute()) // the number of question is correct and test is full check contribuite pointes to each question
		{
			numberOfProblems++;
			problem=String.format("%s%d.invalid point number\n or total sum of point not 100.\n", problem,numberOfProblems); // add message for the specific problem
		}
		if(TestName==null || TestName.isEmpty())
		{
			numberOfProblems++;
			problem=String.format("%s%d.Test Name field is empty\n", problem,numberOfProblems); // add message for the specific problem
		}
		if(teacherDescription==null || teacherDescription.isEmpty())
		{
			numberOfProblems++;
			problem=String.format("%s%d.Teacher Description field is empty\n", problem,numberOfProblems); // add message for the specific problem
		}
		if(StudentDescription==null ||StudentDescription.isEmpty())
		{
			numberOfProblems++;
			problem=String.format("%s%d.Student Description field is empty\n", problem,numberOfProblems); // add message for the specific problem
		}
		if(!validDuration(seconds,minutes,hours))
		{
			numberOfProblems++;
			problem=String.format("%s%d.Test duration must be in the format 'hh:mm:ss'\nand should not be zero.\n", problem,numberOfProblems); // add message for the specific problem
		}
		if(numberOfProblems!=0) // we found at list one problem
		{
			messageLabel.setStyle("-fx-text-fill: red;");
			messageLabel.setText(problem); // assign all problem we found to labael message text
			return false;
		}
		return true;
	}
    
	
	private boolean validDuration(String seconds, String minutes, String hours) 
	{
		if(!isPositiveNumberOnly(seconds) || !isPositiveNumberOnly(minutes) || !isPositiveNumberOnly(hours) || seconds.length()!=2 || minutes.length()!=2 || hours.length()!=2 ) // check if each time inserted is number and only to digits
			return false;
		return (Integer.parseInt(seconds) + Integer.parseInt(minutes) + Integer.parseInt(hours))!=0; // check if the total time is not zero;
	}

	@FXML
	private void backArrowMouseClickHandle(MouseEvent e) {
		try 
		{
			CEMSClientUI.switchScenes(getClass().getResource("TestBankScreen.fxml"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	@FXML
	private void xButtonMouseClickHandle(MouseEvent e) 
	{
		try {
			CEMSClient.logout(TeacherMainScreenController.getUsername());
			CEMSClient.exitButton();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.exit(0);
	}
	
	@FXML
	void rowClicked(MouseEvent event) 
	{
		Question clicked_question = questions_table.getSelectionModel().getSelectedItem();
		if(clicked_question==null) // in case clicked on empty table or didn't clicked on row in the table
			return;
		messageLabel.setStyle("-fx-text-fill: red;");
		messageLabel.setText("");
		// check all cases where user can't add a question to the test
		if(!isPositiveNumberOnly(numberOfQuestionText.getText()) || Integer.parseInt(numberOfQuestionText.getText())==0 ) // in case user didn't enter correct value of questions
		{
			messageLabel.setText("invalid number of questions");
			return;
		}
		if(TestIsFull()) // in case the user try to add question to test when the test if full
		{
			messageLabel.setText("The test is full. You cannot add more questions.");
			return;
		}
		messageLabel.setText(""); // clear message label
		addVBox(clicked_question, parent_VBox_container.getChildren().size() + 1);
		
		//remove the element
		ObservableList<Question> questionList = questions_table.getItems();
        questionList.remove(clicked_question);

        // Refresh the TableView
        questions_table.refresh();
	}
	
	@FXML
	void getInfoClicked(MouseEvent e) {
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Informaition");
        alert.setHeaderText("How To Use The Test");
        alert.setContentText("Select the question from the Table on the left " + 
        "Then Adjust the question position by clicking on the question then adjusting it via the arrows" + 
        		" On the left.\n You may Also Delete The Question.\n\n Enjoy!");
        
        alert.showAndWait();
	}
	
	@FXML
	void upArrowClicked(MouseEvent e) 
	{
		if(currently_selected_VBox == null)
			return;
		
		// exchange the selected question with the above question(change position)
		switchCurrentVBoxWithAboveVBox(currently_selected_VBox);
	}
	
	
	@FXML
	void removeQuestionFromScrollPain(MouseEvent e) 
	{
		if(currently_selected_VBox == null)
			return;
		
		updateTestQuestionPositionFromDeleting(currently_selected_VBox);
		// update each element number inside the VBoxs
		// remove it from the ScrollPain and update the TableView
		parent_VBox_container.getChildren().remove(currently_selected_VBox);
		//remove the point areaText
		HBox hbox =(HBox)currently_selected_VBox.getChildren().get(6);
		TextField pointToDelete = (TextField)hbox.getChildren().get(1); // get the correct text field of the point in the vbox
		if(isPositiveNumberOnly(pointToDelete.getText())) // only if the point TextFild hold positive number we correct totalPointsLabel
		{
			int newPoints = Integer.parseInt(totalPointsLabel.getText()) - Integer.parseInt(pointToDelete.getText()); // calculate the new total points
			totalPointsLabel.setText(String.valueOf(newPoints)); // insert into the total points label
	        changeColorLabel(newPoints); // change the color of the label depending on the points
		}
        pointAreas.remove(pointToDelete); // remove the TextField point form the list (so when listening it wont count this score field)
		// add the currently_selected_vbox to the table
		questions_table.getItems().add(map_VBox_to_Question.get(currently_selected_VBox));
		map_VBox_to_Question.remove(currently_selected_VBox);
		currently_selected_VBox = null;
		questions_table.refresh();
		
	}
	/**
	 * Find the location of the deleted VBox and from that point on start to decrease the position value
	 * of the question
	 * @param deleted_VBox the VBox that we want to delete.
	 */
	private void updateTestQuestionPositionFromDeleting(VBox deleted_VBox) 
	{
		int index_deleted_VBox = parent_VBox_container.getChildren().indexOf(deleted_VBox);
		for(int i = index_deleted_VBox + 1; i < parent_VBox_container.getChildren().size(); i++) 
		{
			VBox current_VBox = (VBox)parent_VBox_container.getChildren().get(i);
			// The Fist Label Is the Question Number
			HBox first_hbox = (HBox)current_VBox.getChildren().get(0); // get the first Hbox
			Label first_label = (Label)first_hbox.getChildren().get(1); // get the number of question
			first_label.setText(String.valueOf(i));
		}
	}
	
	private void switchCurrentVBoxWithAboveVBox(VBox current_VBox) {
		if(current_VBox == null)
			return;
		
	    int index_current_VBox = parent_VBox_container.getChildren().indexOf(current_VBox);
	    if (index_current_VBox == 0)
	        return;

	    VBox above_VBox = (VBox) parent_VBox_container.getChildren().get(index_current_VBox - 1);
	    // switch label text

	    HBox first_hbox_current = (HBox) current_VBox.getChildren().get(0);
	    Label first_label_text_current = (Label) first_hbox_current.getChildren().get(1);
	    HBox second_hbox_current = (HBox) above_VBox.getChildren().get(0);
	    Label first_label_text_above = (Label) second_hbox_current.getChildren().get(1);

	    String tempText = first_label_text_current.getText();
	    first_label_text_current.setText(first_label_text_above.getText());
	    first_label_text_above.setText(tempText);

	    // switch current VBox with above VBox
	    ObservableList<Node> children = parent_VBox_container.getChildren();
	    children.set(index_current_VBox - 1, new VBox());
	    children.set(index_current_VBox, new VBox());
	    children.set(index_current_VBox, above_VBox);
	    children.set(index_current_VBox - 1, current_VBox);
	}
	
	private void switchCurrentVBoxWithBelowVBox(VBox current_VBox) 
	{
		if(current_VBox == null)
			return;
		
	    int index_current_VBox = parent_VBox_container.getChildren().indexOf(current_VBox),
	    	sizeOfparent_VBox_container =parent_VBox_container.getChildren().size() ;
	    if (index_current_VBox == sizeOfparent_VBox_container-1)
	        return;

	    VBox below_VBox = (VBox) parent_VBox_container.getChildren().get(index_current_VBox + 1);
	    // switch label text

	    HBox first_hbox_currenct = (HBox)current_VBox.getChildren().get(0);
	    Label first_label_text_current = (Label)first_hbox_currenct.getChildren().get(1);
	    HBox first_hbox_below = (HBox)below_VBox.getChildren().get(0);
	    Label first_label_text_below = (Label) first_hbox_below.getChildren().get(1);

	    String tempText = first_label_text_current.getText();
	    first_label_text_current.setText(first_label_text_below.getText());
	    first_label_text_below.setText(tempText);

	    // switch current VBox with above VBox
	    ObservableList<Node> children = parent_VBox_container.getChildren();
	    children.set(index_current_VBox + 1, new VBox());
	    children.set(index_current_VBox, new VBox());
	    children.set(index_current_VBox, below_VBox);
	    children.set(index_current_VBox + 1, current_VBox);
	}

	
	
	private void setUpTable() 
	{
		questions_table.getItems().clear();
		questions_table.getItems().addAll(questions);
	}
	
	private void addVBox(Question q, int question_number_inside_VBox) 
	{
	    VBox vBox = convertQuestionIntoVBox(q, question_number_inside_VBox);
	    parent_VBox_container.getChildren().add(vBox);
	    map_VBox_to_Question.put(vBox, q);
	    
	}
	
	private VBox convertQuestionIntoVBox(Question question, int question_number) {
		
		VBox question_VBox = new VBox();
		question_VBox.getStyleClass().add("question-vbox"); 
		question_VBox.getStyleClass().add("vboxQuestion");
		question_VBox.setSpacing(5); 

        // Add the question description
		Label question_label = new Label("Question  ");
		question_label.getStyleClass().add("question-description-label");
		Label question_number_label = new Label(String.valueOf(question_number));
		question_number_label.getStyleClass().add("question-description-label");
		HBox hbox  = new HBox();
		hbox.getChildren().add(question_label);
		hbox.getChildren().add(question_number_label);
		question_VBox.getChildren().add(hbox);
		
        Label question_Description_Label = new Label(question.getQuestionDescription());
        question_VBox.getChildren().add(question_Description_Label);
        question_Description_Label.getStyleClass().add("question-description-label");
        
        int i = 1;
        for (String option : question.getOptionsText()) 
        {
            Label option_Label = new Label(String.valueOf(i)+ ". " + option);
            if(i==question.getCorrectAnswer())
            	 option_Label.getStyleClass().add("correct-option-label");
            else
                option_Label.getStyleClass().add("answer-option-label");
            question_VBox.getChildren().add(option_Label);	
            i += 1;
        }
        // Add the question score TextField
        HBox row = new HBox();
        TextField question_Score_Text_Field = new TextField();
        pointAreas.add(question_Score_Text_Field); // Add the text field to the pointAreas list
        question_Score_Text_Field.textProperty().addListener((observable, oldValue, newValue) -> 
        { // this listen i added to each points area to sun the points
                int totalPoints = 0;
                for (TextField area : pointAreas) // sum all the correct points
                {
                    String scoreText = area.getText();
                    if (isPositiveNumberOnly(scoreText)) 
                    {
                        totalPoints += Integer.parseInt(scoreText);
                    }
                }
                totalPointsLabel.setText(String.valueOf(totalPoints));
                changeColorLabel(totalPoints);
                
        });
        question_Score_Text_Field.getStyleClass().add("question-score-textfield");
        Label instruction = new Label("Question Score : ");
        row.getChildren().addAll(instruction, question_Score_Text_Field);
        question_VBox.getChildren().add(row);
        
        // adding the up and down and remove Images
        Image image;
        ImageView up_arrow_image;
        ImageView down_arrow_image;
        ImageView remove_arrow_image;
        
        if(this.getClass().getResourceAsStream("/images/up_chevron.png") != null) {
        	image = new Image(this.getClass().getResourceAsStream("/images/up_chevron.png"));
        	up_arrow_image = new ImageView(image);
        }else {
        	up_arrow_image = new ImageView();
        }
        
        if(this.getClass().getResourceAsStream("/images/down_chevron.png") != null) {
        	image = new Image(this.getClass().getResourceAsStream("/images/down_chevron.png"));
        	down_arrow_image = new ImageView(image);
        }else {
        	down_arrow_image = new ImageView();
        }
        
        if(this.getClass().getResourceAsStream("/images/trash_bin.png") != null) {
        	image = new Image(this.getClass().getResourceAsStream("/images/trash_bin.png"));
        	remove_arrow_image = new ImageView(image);
        }else {
        	remove_arrow_image = new ImageView();
        }
        
     // Add the ImageView to your JavaFX scene or layout
        up_arrow_image.setFitWidth(20); 
        up_arrow_image.setFitHeight(20); 
        down_arrow_image.setFitWidth(20);
        down_arrow_image.setFitHeight(20);
        remove_arrow_image.setFitWidth(40);
        remove_arrow_image.setFitHeight(40);
        remove_arrow_image.getStyleClass().add("image-view");
        
        up_arrow_image.setPickOnBounds(true);
        down_arrow_image.setPickOnBounds(true);
        remove_arrow_image.setPickOnBounds(true);
        
        up_arrow_image.setOnMouseClicked((MouseEvent e) -> 
        {
            //VBox selectd_VBox = currently_selected_VBox;
        	currently_selected_VBox=question_VBox;
            switchCurrentVBoxWithAboveVBox(currently_selected_VBox);
        });
        down_arrow_image.setOnMouseClicked((MouseEvent e) -> 
        {
            //VBox selectd_VBox = currently_selected_VBox;
        	currently_selected_VBox=question_VBox;
        	switchCurrentVBoxWithBelowVBox(currently_selected_VBox);
        });
        remove_arrow_image.setOnMouseClicked((MouseEvent e) ->{
        	currently_selected_VBox=question_VBox;
        	removeQuestionFromScrollPain(e);
        });
        question_VBox.getChildren().add(up_arrow_image);
        HBox downArrowPluRemoveArrow_row = new HBox();
        downArrowPluRemoveArrow_row.getChildren().addAll(down_arrow_image, remove_arrow_image);

        HBox.setHgrow(down_arrow_image, Priority.ALWAYS); // Allow down_image to expand horizontally
        HBox.setHgrow(remove_arrow_image, Priority.NEVER); // Prevent remove_image from expanding horizontally

        downArrowPluRemoveArrow_row.setAlignment(Pos.CENTER_LEFT); // Align the entire HBox to the left
        downArrowPluRemoveArrow_row.setSpacing(10); // Add spacing between the images
        question_VBox.getChildren().add(downArrowPluRemoveArrow_row);
        return question_VBox;
	}

	private void changeColorLabel(int totalPoints) {
		if(totalPoints==100)
			totalPointsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: green;");
		if(totalPoints>100)
			totalPointsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
		if(totalPoints<100)
			totalPointsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: blue;");
	}
	
 	private boolean isPositiveNumberOnly(String number) 
	{
	    return utilities.TeacherMethodHelper.isPositiveNumber(number);

	}


	private boolean TestIsFull()
	{
		int numberOfQuestionInTest = map_VBox_to_Question.size(), // save the number of question in the test
			allowedNumberOfQuestion = Integer.parseInt(numberOfQuestionText.getText());
		return numberOfQuestionInTest==allowedNumberOfQuestion; // the number of question in test is thhe same as allowed we return fasle else return true
	}
	
	private boolean ValidPointContribute() // check if each question have points and the total sum of all questions points is 100
	{
		ArrayList<VBox> VboxQuestionForTest = new ArrayList<>( map_VBox_to_Question.keySet()); // the Vbox question for test is the keys in the hashmap i extracted the set of keys and enter them to arrayList of vBox
		int sum =0; // sum up all questions points
		// we know that the point field text is in index 5 for the VBOX in index 1 in the hbox
		for(int i =0  ; i<map_VBox_to_Question.size( ) ; i++)
		{
			HBox pointHbox =(HBox)VboxQuestionForTest.get(i).getChildren().get(6);
			String point = ((TextField)pointHbox.getChildren().get(1)).getText();
			if(!isPositiveNumberOnly(point)) // in case the the point is empty or not a positive number
				return false;
			sum+= Integer.parseInt(point); // sum up this point
		}
		return sum==100; // if sum=100 we get true else false
	}
	
	private void updateMessageLabel( ) 
	{
	    if (testAddedSuccesfully) 
	    {
	    	messageLabel.setText("Test Added successfully.");
	    	messageLabel.setStyle("-fx-text-fill: blue;");
	        testAddedSuccesfully = false;
	    }
	    else
	    {
	    	messageLabel.setText("Failed to Add Test.");
	    	messageLabel.setStyle("-fx-text-fill: red;");
	    }
	}
	
	private Exam buildExamFromGUI(String testName, String teacherDescription, String studentDescription,Duration duration, Subject subject, Course course) 
	{
		Question[] questions = new Question[map_VBox_to_Question.size()]; // in questionScores[i] is the score of questionID[i]
		String[] questionScores = new String[map_VBox_to_Question.size()];
		ArrayList<VBox> VboxQuestionForTest = new ArrayList<>( map_VBox_to_Question.keySet()); // list of VBox each VBox contain he number of the order the question will appear and the score of the questions
		for(VBox questionVBox : VboxQuestionForTest)
		{
			HBox pointHbox =(HBox)questionVBox.getChildren().get(6); // extract score of the question
			String score = ((TextField)pointHbox.getChildren().get(1)).getText();
			String numberOfQuestion = ((Label)((HBox)questionVBox.getChildren().get(0)).getChildren().get(1)).getText(); // i know each VBox the first child is Hbox and in the second child of the HBox is the  number in which order the question will appear in the test
			questions[Integer.parseInt(numberOfQuestion)-1]= map_VBox_to_Question.get(questionVBox); // number of questions start from 1.  get the match question for the current vBox using the hash map
			questionScores[Integer.parseInt(numberOfQuestion)-1]=score; // insert the score of the question
		}
		// create instance of exam
		String authorName = TeacherMainScreenController.user.getFirstName()+" "+TeacherMainScreenController.user.getLastName(); // take the name of the user who creating this exam
		String authorID = UserLoginController.user.getIDNumber(); // take author id
		ArrayList<Question > questionsList = new ArrayList<>(Arrays.asList(questions));
		ArrayList<String> questioScoresList = new ArrayList<>(Arrays.asList(questionScores));
 		Exam exam = new Exam(null,testName,subject,course,null,duration, teacherDescription ,studentDescription,authorName,authorID,questionsList,questioScoresList);
 		exam.setLecturerUserName(UserLoginController.user.getUserName()); // to add this exam to personal Bank
		return exam;
	}

	/**
	 * Change the scroll bar position up or down
	 * @param scroll_up : used to indicate whether you want to scroll up or down
	 */
	private void fixedAmountScroll(boolean scroll_up) {
		final double scroll_Amount = 0.5;
		if(scroll_up)
			scrollPane.setVvalue(scrollPane.getVvalue() - scroll_Amount);
		else
			scrollPane.setVvalue(scrollPane.getVvalue() + scroll_Amount);
	}
}

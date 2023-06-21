package studentUi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Client.CEMSClient;
import Client.CEMSClientUI;
import Client.WordDocument;
import clientGUI.UserLoginController;
import entities.Grade;
import entities.GradedTest;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import utilities.CONSTANTS;
import utilities.Message;

/**
 * Controller class for the student main screen.
 */
public class viewGradesScreenController {
	
	public static ArrayList<Grade> grades = null;
	public static GradedTest wordToDownload = null;
	private String pathToSave;
			
	@FXML
	private Button back_arrow_image;
	
	@FXML
	private Button xButton;
	
	@FXML
	private TableView<Grade> gradesTable;
	
    @FXML
    private TableColumn<Grade, String> nameCol;

    @FXML
    private TableColumn<Grade, String> subjectCol;
    
    @FXML
    private TableColumn<Grade, String> gradeCol;

	@FXML
	private Button downloadBtn;
	
	@FXML
	private Button clearBtn;
	
	@FXML
	private Label errorLabel;


	/**
	 * Initializes the controller and sets up event handlers.
	 */
	@FXML
	void initialize() 
	{
		back_arrow_image.setPickOnBounds(true);
		back_arrow_image.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClientUI.switchScenes(getClass().getResource("studentMainScreen.fxml"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		xButton.setPickOnBounds(true);
		xButton.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClient.logout(studentMainScreenController.getUsername());
				CEMSClient.exitButton();
			} catch (Exception e1) {
				e1.printStackTrace(); 
			}
			System.exit(0);
		});
		
		gradesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		fetchGrades();
		setTableView(grades); 
				
		downloadBtn.disableProperty().bind(gradesTable.getSelectionModel().selectedItemProperty().isNull());
		downloadBtn.setOnAction(e -> downloadTest(gradesTable.getSelectionModel().getSelectedItems().get(0)));
		clearBtn.setOnAction(e -> gradesTable.getSelectionModel().select(null));
		
	}
	/**
	 * Fetches the grades for the currently logged-in student.
	 */
	protected void fetchGrades() 
	{
		try 
		{
			CEMSClientUI.chat.accept(new Message(CONSTANTS.getGradesByUsername, studentMainScreenController.getUsername()));
		}
		catch (Exception e) {
		}
	}
	/**
	 * Sets up the table view with the grades data.
	 * 
	 * @param grades The list of grades to display in the table.
	 */
	private void setTableView(ArrayList<Grade> grades) 
	{
		//define username column
	    nameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
	    nameCol.setResizable(false);

//	    nameCol.setReorderable(false);
		//define subject column
	    subjectCol.setCellValueFactory(new PropertyValueFactory<>("course"));
	    subjectCol.setResizable(false);
//	    subjectCol.setReorderable(false);
		//define grade column
	    gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
	    gradeCol.setResizable(false);
//	    gradeCol.setReorderable(false);

	    
	    ObservableList<Grade> data = FXCollections.observableArrayList();
	    if(grades!=null)
	    	for(Grade grade : grades )
	    		data.add(grade);
	    
	    gradesTable.setItems(data);
	}
	/**
	 * Downloads the selected test for a grade.
	 * 
	 * @param grade The grade object representing the selected test.
	 */
	protected void downloadTest(Grade grade)
	{
		String username = grade.getUsername();
		String testId = grade.getConductExamId();
		String testName = grade.getCourse();
		List<String> data = new ArrayList<>();
	    data.add(username);
	    data.add(testId);
		Message gradeTestDownload = new Message(CONSTANTS.getGradedWordTest,data);
		try {
			CEMSClientUI.chat.accept(gradeTestDownload);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("falied to get Word from DB");
			return;
		}
		DownloadFile(username + "_" + testId +".docx",testName+" Test");
	}
	/**
	 * Downloads the file to the selected directory.
	 * 
	 * @param fileName The name of the file to be saved.
	 * @param testName The name of the test.
	 */
	private void DownloadFile(String fileName, String testName) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose a folder");
		File selectedDirectory = directoryChooser.showDialog(CEMSClientUI.primary_stage);

		if (selectedDirectory != null) {
			pathToSave =  selectedDirectory.getAbsolutePath() + "\\" + fileName;
			if(wordToDownload != null)
				WordDocument.GeneratedWord(pathToSave,wordToDownload,testName);
			else
				errorLabel.setText("Test not found,\n Please contact adminstiration");
		}
	}
}
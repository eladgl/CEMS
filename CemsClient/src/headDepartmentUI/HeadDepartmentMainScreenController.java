package headDepartmentUI;

import java.io.IOException;
import Client.CEMSClient;
import Client.CEMSClientUI;
import clientGUI.ProfileScreenController;
import entities.Course;
import entities.Exam;
import entities.Subject;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import utilities.CONSTANTS;
import utilities.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HeadDepartmentMainScreenController {
	public static User user;
	public static boolean is_loged_out = false;
	public static boolean logoutClick = false;
	public static Subject[] subjects = null;
	public static ArrayList<Exam> myExams = null, generalExams = null, selectedList = null;

	@FXML
	private Button show_data;

	@FXML
	private Label welcomeLabel;

	@FXML
	private Button xButton;

	@FXML
	private ImageView user_profile_image;

	@FXML
	void initialize() {
		generalExams = new ArrayList<Exam>();

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

	public void logoutUserFromSystem(ActionEvent event) throws IOException {
		// send request for logout from the client UI
		logoutClick = true;
		CEMSClient.logout(user.getUserName());
		if (is_loged_out) {
			// quit connection and wait
			try {
				CEMSClientUI.switchScenes(getClass().getResource("/clientGUI/LoginScreen.fxml"));
			} catch (IOException e) {
				System.out.println("Failed to wait\n");
				e.printStackTrace();
			}
		} else {
			System.out.println("Failed to logout!");
		}
		logoutClick = false;
	}

	public static String getUsername() {
		return user.getUserName();
	}

	public void viewStatistics() throws IOException {
		fetchExams();
		CEMSClientUI.switchScenes(getClass().getResource("StatisticsReportScreenHD.fxml"));
	}

	public void approveExtensionTimeScreen() throws IOException {
		CEMSClientUI.switchScenes(getClass().getResource("ApproveTimeExtension.fxml"));
	}

	public void viewQuestionData() throws IOException {
		getSubjectsFromDb();
		CEMSClientUI.switchScenes(getClass().getResource("ViewQuestionsData.fxml"));
	}

	public void viewExamData() throws IOException {
		getSubjectsFromDb();
		fetchExams();
		CEMSClientUI.switchScenes(getClass().getResource("ViewExamsData2.fxml"));
	}

	protected static void fetchExams() {
		// get exams by subject to general Exams
		Message dataToServerGeneralExams = new Message(CONSTANTS.getExamsFromDB, null);
		try {
			CEMSClientUI.chat.accept(dataToServerGeneralExams);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private void getSubjectsFromDb() {
		// fetch subjects from DB
		try {
			Message dataToServer = new Message(CONSTANTS.GetSubjectsFromDB, null);
			CEMSClientUI.chat.accept(dataToServer);
		} catch (Exception e) {
		}
	}

	public static Course[] extractCoursesFromSubjects(Subject[] subjects) {
		List<Course> resultList = new ArrayList<>();

		for (Subject subject : subjects) {
			ArrayList<Course> courses = subject.getCourses();
			if (courses != null) {
				resultList.addAll(courses);
			}
		}

		Course[] finalResult = new Course[resultList.size()];
		resultList.toArray(finalResult);

		return finalResult;
	}

	public static Subject[] removeSubjectDuplicates(Subject[] subjects) {
	    HashSet<String> uniqueNames = new HashSet<>();
	    Subject[] result = new Subject[subjects.length];
	    int index = 0;

	    for (Subject subject : subjects) {
	        String name = subject.getName();
	        if (!uniqueNames.contains(name)) {
	            uniqueNames.add(name);
	            result[index] = subject;
	            index++;
	        }
	    }

	    // Create a new array without null values (if any)
	    Subject[] finalResult = new Subject[index];
	    System.arraycopy(result, 0, finalResult, 0, index);

	    return finalResult;
	}

}

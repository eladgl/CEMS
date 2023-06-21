package Client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import ocsf.client.AbstractClient;
import studentUi.ComputerizedTestScreenController;
import studentUi.ManualTestExam;
import studentUi.SmsScreenController;
import studentUi.startTestScreenController;
import studentUi.studentMainScreenController;
import studentUi.viewGradesScreenController;
import teacherUi.ConductExamController;
import teacherUi.CreateExamToConductController;
import teacherUi.CreateNewTestControllerImproved;
import teacherUi.CreateQuestionController;
import teacherUi.ExistingQuestionInsertController;
import teacherUi.ExistingTestInsertController;
import teacherUi.GradeExamScreenController;
import teacherUi.QuestionBankManagerController;
import teacherUi.StatisticsReportController;
import teacherUi.TeacherMainScreenController;
import teacherUi.TestBankManagerController;
import clientGUI.ProfileScreenController;
import clientGUI.RegistrationSimulationScreenController;
import clientGUI.UserLoginController;
import entities.*;
import headDepartmentUI.ApproveTimeExtension;
import headDepartmentUI.ApproveTimeExtensionController;
import headDepartmentUI.HeadDepartmentMainScreenController;
import headDepartmentUI.StatisticsReportControllerHD;
import headDepartmentUI.ViewExamsData2Controller;
import headDepartmentUI.ViewQuestionsDataController;
import utilities.CONSTANTS;
import utilities.Message;

/**
 * The client class for the CEMS (Client-Server Exam Management System)
 * application.
 */

public class CEMSClient extends AbstractClient {
	@Override
	protected void connectionEstablished() {
		super.connectionEstablished();
	}

	/**
	 * Flag indicating whether the client is awaiting a response from the server.
	 */
	public static boolean awaitResponse = false;

	/**
	 * The client controller instance.
	 */
	public static CEMSClientController clientController;

	/**
	 * Creates a new instance of the CEMSClient class.
	 * 
	 * @param host       The hostname of the server.
	 * @param port       The port number to connect to.
	 * @param controller The controller for the client.
	 * @throws IOException if an I/O error occurs while creating the client.
	 */
	public CEMSClient(String host, int port, CEMSClientController controller) throws IOException {
		super(host, port);
		clientController = controller;
	}

	/**
	 * Handles the message that was sent by the server
	 * 
	 * @param msg the message that the server sent (it must be of type
	 *            Utilities.utilities.Message)
	 * 
	 */
	@Override
	protected void handleMessageFromServer(Object msg) {
		Message data_from_server; // save data thatcame from server in form of DataFromServer[0] = operation to do
									// , DataFromServer[i>0] data from server
		if (msg instanceof Message) // converts message to arrayList type
			data_from_server = (Message) msg;
		else // in case the server didnt send this type of data arraylist<String>
		{
			System.out.println("server didnt deliver data to client in type of Message");
			awaitResponse = false; // release the waiting client
			return; // return and do nothing

		}
		switch (data_from_server.getOperation()) // insert to switch the command the client need to do
		{
		case CONSTANTS.ValidLoginAnswer: // check the answer that server send to if the username and password is in DB
		{
			ArrayList<User> valid_login_responds = (ArrayList<User>) data_from_server.getCommands();
			checkValidUserAnswer(valid_login_responds);
			if (UserLoginController.isLoginValid)
				UserLoginController.user = valid_login_responds.get(0);
			awaitResponse = false;
			break;
		}
		case CONSTANTS.receiveQuestions: {
			ArrayList<Question> questions_from_db = data_from_server.convertCommandsToArrayList();
			Question[] questions = new Question[questions_from_db.size()];

			questions = questions_from_db.toArray(questions);

			switch (UserLoginController.user.getUserPermission()) {

			case CONSTANTS.teacherRole:
				QuestionBankManagerController.generalQuestions = null;
				awaitResponse = false;
				break;

			case CONSTANTS.headOfDepartmentRole:
				ViewQuestionsDataController.questions = questions;
				awaitResponse = false;
				break;

			default:
				throw new IllegalArgumentException("Cant retrive questions - user selection -");

			}
		}

		case CONSTANTS.receiveUserLogOutResponse: {
			ArrayList<Boolean> response = data_from_server.convertCommandsToArrayList();
			// added support for different users
			if (studentMainScreenController.logoutClick)
				studentMainScreenController.is_loged_out = response.get(0);
			else if (TeacherMainScreenController.logoutClick)
				TeacherMainScreenController.is_loged_out = response.get(0);
			else if (HeadDepartmentMainScreenController.logoutClick)
				HeadDepartmentMainScreenController.is_loged_out = response.get(0);

			awaitResponse = false;
			break;
		}

		case CONSTANTS.ResponseAddQuestion: {
			ArrayList<Boolean> response = data_from_server.convertCommandsToArrayList();
			CreateQuestionController.successfullyAdded = response.get(0);
			awaitResponse = false;
			break;
		}

		case CONSTANTS.ResponseEditQuestion: {
			awaitResponse = false;
			break;
		}

		case CONSTANTS.ResponseExitClient: {
			System.out.println("Exiting...");
			awaitResponse = false;
			break;
		}

		case CONSTANTS.ResponseAddCreatedExam: {
			// Inform GUI if exam added successfully or not
			CreateNewTestControllerImproved.testAddedSuccesfully = (boolean) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}

		case CONSTANTS.ResponsegetQuestionsBySubjectAndCourse: {
			ArrayList<Question> questionsFromServer = (ArrayList<Question>) data_from_server.getCommands();
			CreateNewTestControllerImproved.questions = questionsFromServer
					.toArray(new Question[questionsFromServer.size()]); // just convert arraylist of question to array
																		// of questions
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseToGetStudentNotTakenTests: {
			startTestScreenController.relevantExams = (ConductExam) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responsegetSubjectAndCoursesTeacher: {
			// set the subject from DB to the subject in the teacher main screen controller
			TeacherMainScreenController.subjects = (ArrayList<Subject>) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseCheckIfPasswordMatchesAndTheTestFile: {
			startTestScreenController.exam_at_conduct = (ConductExam) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}
		
		case CONSTANTS.responseGetTestInformationAtConduct: {
            startTestScreenController.exam = (Exam) data_from_server.getCommands();
            awaitResponse = false;
            break;
        }

		case CONSTANTS.responsegetQuestionsBySubject: {
			// set the arraylist question from db to the questionbankcontroller
			QuestionBankManagerController.generalQuestions = (ArrayList<Question>) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responsegetQuestionsBySubjectAndByLecturerBank: {
			// set the arraylist question from db to the questionbankcontroller
			QuestionBankManagerController.myBankQuestion = (ArrayList<Question>) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}
		case CONSTANTS.ResponseToGetSubjectsFromDB: {
			ArrayList<Subject> subjects_from_db = data_from_server.convertCommandsToArrayList();
			Subject[] subjects = new Subject[subjects_from_db.size()];

			subjects = subjects_from_db.toArray(subjects);

			switch (UserLoginController.user.getUserPermission()) {

			case CONSTANTS.headOfDepartmentRole:
				HeadDepartmentMainScreenController.subjects = subjects;
				awaitResponse = false;
				break;

			default:
				throw new IllegalArgumentException("Cant retrive questions - user selection -");

			}
		}
		case CONSTANTS.responseChangePersonalBankQuestion: {
			ExistingQuestionInsertController.successfulySavedChanges = (boolean) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseSetStudenIsTakenExamForCurrectConductTestToOne: {
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseGetExamsBySubject:
		{
			ArrayList<Exam> ExamsFromDB = (ArrayList<Exam>)data_from_server.getCommands(); // get the data form the server
			switch(UserLoginController.user.getUserPermission()) {
			
			case CONSTANTS.teacherRole:
				
				TestBankManagerController.generalExams=ExamsFromDB; // insert the exams to the general exam in the testBankMenagerController
				awaitResponse = false;
				break;
				
			
			case CONSTANTS.headOfDepartmentRole:
				
				ViewExamsData2Controller.generalExams=ExamsFromDB; // insert the exams to the general exam in the testBankMenagerController
				awaitResponse = false;
				break;
				
			}	
		}
		
		case CONSTANTS.responseGetExamsFromDB: {
			
			ArrayList<Exam> ExamsFromDB = (ArrayList<Exam>) data_from_server
					.getCommands();
			switch (UserLoginController.user.getUserPermission()) {
				case CONSTANTS.teacherRole:
					TestBankManagerController.generalExams = ExamsFromDB;																	
					break;
				case CONSTANTS.headOfDepartmentRole:
					HeadDepartmentMainScreenController.generalExams = ExamsFromDB;				
					break;
				default:
					throw new IllegalArgumentException("Cant retrive questions - user selection -");
			}
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseGetPersonalExamBank: {
			ArrayList<Exam> ExamsFromDB = (ArrayList<Exam>) data_from_server.getCommands(); // get the data form the
																							// server
			TestBankManagerController.myExams = ExamsFromDB; // insert the exams to the general exam in the
																// testBankMenagerController
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseForUploadingWordFile: {
			boolean is_added_successfully = (Boolean) data_from_server.getCommands();
			ManualTestExam.uploaded_file_successfully_to_the_db = is_added_successfully;
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseApproveTimeExtension: {
			Integer exam_id = (Integer) data_from_server.getCommands();
			ApproveTimeExtension spokenTest = null;
			if (exam_id != null) {
				// get the the right object then remove it
				for (ApproveTimeExtension test : ApproveTimeExtensionController.timeExtensionList) {
					if (exam_id == test.getConductExamID()) {
						spokenTest = test;
						break;
					}
				}
				if (spokenTest != null)
					ApproveTimeExtensionController.timeExtensionList.remove(spokenTest);
			}
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseGetStudentsDoneTestData: {
			if (data_from_server.getCommands() != null) {
				ArrayList<StudentsDataInExam> commands = (ArrayList<StudentsDataInExam>) data_from_server.getCommands();
				GradeExamScreenController.ExamsResultsToApprove.addAll(commands);
			}
			awaitResponse = false;
			break;
		}

		case CONSTANTS.resonseForCheckIfExamTimeChange: {
			TimeDifferenceObject time_difference_object = (TimeDifferenceObject) data_from_server.getCommands();

			int time_difference_in_seconds = time_difference_object.getTime_difference_in_seconds();
			if (time_difference_in_seconds > 0) {
				if (time_difference_object.getExam_type() == ExamType.MANUAL) {
					ManualTestExam.postponeExamTime(time_difference_in_seconds);
				} else {
					ComputerizedTestScreenController.postponeExamTime(time_difference_in_seconds);
					ComputerizedTestScreenController.timeDifferenceForThread = time_difference_in_seconds;
				}
			}
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseChangePersonalBankExams: {
			ExistingTestInsertController.successfulySavedChanges = (boolean) data_from_server.getCommands(); // infrom
																												// if
																												// succeed
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseGetStudentsUsers: {
			CreateExamToConductController.students = (ArrayList<User>) data_from_server.getCommands(); // assign the
																										// arrayList of
																										// User to the
																										// CreateExamToConductController
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseUpdateStudentDataInExam: {
			if (!(boolean) data_from_server.getCommands())
				GradeExamScreenController.message = "Error on approving exam - contact IT";
			else
				GradeExamScreenController.message = "Exam approved Successfully - SMS sent to student";
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseWithUpdateExamDataInDB: {
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseForCheckForExamLock: {
			Pair<Boolean, ExamType> data = (Pair<Boolean, ExamType>) data_from_server.getCommands();

			if (data.getFirst() == true) { // is_open = true
				awaitResponse = false;
				break;
			}

			if (data.getSecond() == ExamType.MANUAL) {
				ManualTestExam.trigger_exam_is_locked_alert = true;
				ManualTestExam.StopCheckingTimeThread();
				ManualTestExam.stopExamLockedThread();

			} else if (data.getSecond() == ExamType.COMPUTERIZED) {
				ComputerizedTestScreenController.trigger_exam_is_locked_alert = true;
				ComputerizedTestScreenController.StopCheckingTimeThread();
				ComputerizedTestScreenController.stopExamLockedThread();
			}
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseApproveAllGradesConductExamAndCreateStatistics: {
			GradeExamScreenController.ConductExanAllGradesAprroved = (boolean) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseWithGetConductExamAndRelevantDataForLecturer: {
			ArrayList<StatisticsReportDataForLecturer> report_data = (ArrayList<StatisticsReportDataForLecturer>)
					data_from_server.getCommands();

			switch (UserLoginController.user.getUserPermission()) {

			case CONSTANTS.teacherRole:
				StatisticsReportController.lecturer_statistics_data = report_data;
				awaitResponse = false;
				break;
				
			default:
				throw new IllegalArgumentException("Cant retrive exam grades");

			}

		}

		case CONSTANTS.responseTakenExamsForStatistics: {
			StatisticsReportControllerHD.takenExams = (HashMap<String,Exam>) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseStatisticsData: {
			StatisticsReportControllerHD.statisticsData = (ArrayList<StatisticsReport>) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}
		
		case CONSTANTS.responseAvailablePasswordForConductExam: {
			CreateExamToConductController.availablePassword = (boolean) data_from_server.getCommands(); // infrom if
																										// succeed
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseStartExamToConduct: {
			CreateExamToConductController.examToConductStartSuccessfully = (boolean) data_from_server.getCommands(); // infrom
																														// if
																														// succeed
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseGetExamsToConductBySubjectByLecturer: {
			ConductExamController.conductExams = (ArrayList<ConductExam>) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseLectuerRequestTimeExtension: {
			ConductExamController.requestAcceptedSusseccfully = (boolean) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseRefreshRequestTimeExtensionStatus: {
			ConductExamController.refreshedData = (ArrayList<ConductExam>) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}

		case CONSTANTS.responseEndConductExam: {
			ConductExamController.endedConductExamSusseccfully = (boolean) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseDenyTimeExtension: {
			if ((boolean) data_from_server.getCommands())
				ApproveTimeExtensionController.message = "Time extension denied successfully";
			else {
				ApproveTimeExtensionController.message = "Time extension denied unsuccessfully - Contact IT";
			}
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseGetTimeExtensionRequests: {
			if (data_from_server.getCommands() != null) {
				for (ConductExam exam : (ArrayList<ConductExam>) data_from_server.getCommands()) {
					ApproveTimeExtensionController.timeExtensionList.add(new ApproveTimeExtension(
							exam.getConduct_exam_id(), exam.getConduct_exam_name(), exam.getLecturer_user_name(), // here
																													// is
																													// the
																													// full
																													// name
							exam.getTimeExtensionStringFormat()));
				}
			}
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseGetGradesByUsername: {
			viewGradesScreenController.grades = (ArrayList<Grade>) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseGetGradedWordTest: {
			viewGradesScreenController.wordToDownload = (GradedTest) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseStudentGradeSMS_Simulation: {
			String message;
			SMSMessage temp;
			if (data_from_server.getCommands() != null) {
				for (StudentsDataInExam data : (ArrayList<StudentsDataInExam>) data_from_server.getCommands()) {
					message = data.getUsername() + ".\n" + "New grade achieved, you can check it in the see grades"
							+ " section.\n" + Integer.toString(data.getGrade()) + " " + "in "
							+ data.getConductExamName();
					temp = new SMSMessage(message);
					if (!SmsScreenController.smsList.contains(temp))
						SmsScreenController.smsList.add(temp);
				}
			}
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseRegisterUser: {
			if ((boolean) data_from_server.getCommands())
				RegistrationSimulationScreenController.message = "User registered Successfully";
			else
				RegistrationSimulationScreenController.message = "user/id already exists";
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseStudentsTakenTests:{
			StatisticsReportControllerHD.students = (HashMap<String, ArrayList<Grade>>) data_from_server.getCommands();
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseUpdateProfilePicture:{
			System.out.println("picture updated");
			awaitResponse = false;
			break;
		}
		case CONSTANTS.responseGetProfilePicture:{
			byte[] bytePic =  (byte[]) data_from_server.getCommands();
			if(bytePic != null)
				ProfileScreenController.profilePic =  new ByteArrayInputStream(bytePic);
			awaitResponse = false;
			break;
		}
		default: // if the first element in the array list is not defined in the operations of
					// clients in CONSTANTS we send this exception
		{
			System.out.println("server send unknown operation for the client to do");
			awaitResponse = false;
			break;
		}

		}

	}

	/**
	 * Handles a message received from the client's user interface.
	 * 
	 * @param message The message received from the user interface.
	 * @throws Exception if an exception occurs during message handling.
	 */

	public synchronized void handleMessageFromClientUI(Object message) throws Exception {
		try {
			Message DataFromUI;
			if (message instanceof Message)
				DataFromUI = (Message) message;
			else
				throw new Exception("ClientUI delivered non ArrayList data type");

			System.out.println("handle meesage from UI");
			openConnection();// in order to send more than one message
			System.out.println(DataFromUI.toString());
			awaitResponse = true; // before sending data to server we make statement that we wait for answer from
									// server
			sendToServer(DataFromUI);
			System.out.println("send message to server and waits for response");
			// wait for response
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("done waiting");
		} catch (IOException e) {
			e.printStackTrace();
			quit();
		}
	}

	/**
	 * Closes the client connection. If failed exits the application.
	 */
	public void quit() {
		try {
			closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
	}

	/**
	 * Checks the answer returned by the server regarding the validation of the
	 * username and password in the database.
	 * 
	 * @param msg The message containing the server's response.
	 */
	void checkValidUserAnswer(ArrayList<User> msg) // check if the answer server return about user name and password
													// validation in DB
	{
		UserLoginController.isLoginValid = !(msg.get(0) == null); // if the answer the server returned is true we notify
																	// in the UserLoginController that the username and
																	// paswwrod is correct else isLoginValid will be
																	// false
	}

	/**
	 * Logs out a user from the system.
	 *
	 * @param username The username of the user to log out.
	 */
	public static void logout(String username) {
		ArrayList<String> data_to_server = new ArrayList<>();
		data_to_server.add(username);
		try {
			CEMSClientUI.chat.accept(new Message(CONSTANTS.logOutUser, data_to_server));
		} catch (Exception e) {
			System.out.println("Failed to LogOut user!\n");
			e.printStackTrace();
		}
	}

	/**
	 * Handles the exit button action in the client application.
	 *
	 * @throws Exception If an error occurs during the process.
	 */
	public static void exitButton() throws Exception {
		// quit connection and wait
		try {
			CEMSClientUI.chat.accept(new Message(CONSTANTS.ExitClient, null));
			CEMSClientUI.chat.client.quit();
			Thread.sleep(200);
		} catch (InterruptedException e) {
			System.out.println("Failed to Exit\n");
			e.printStackTrace();
		}
	}
}

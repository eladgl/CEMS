package serverUI;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import entities.ConductExam;
import entities.ConductExamIdWithScreenTypeObject;
import entities.Course;
import entities.Exam;
import entities.Grade;
import entities.GradedTest;
import entities.StudentsExamDataGetter;
import entities.ExamTimeChangeObject;
import entities.ManualExamConductObject;
import entities.Pair;
import entities.Question;
import entities.RequestTimeExtension;
import entities.StatisticsReport;
import entities.StudentsDataInExam;
import entities.Subject;
import entities.User;
import entities.Pair;
import ocsf.server.ConnectionToClient;
import utilities.CONSTANTS;
import utilities.Message;

/**
 * The MessageHandler class handles incoming messages from clients and performs corresponding operations.
 * It provides methods to handle various client actions such as login, fetching question banks, retrieving questions from the database,
 * logging out users, and editing questions.
 * 
 * This class is responsible for processing the messages received from clients and invoking the appropriate methods
 * in the database controller (mysqlConnection) to perform the required operations.
 * 
 * Note: It is assumed that the mysqlConnection class is properly implemented and provides the necessary methods for interacting with the database.
 * It is a design pattern named COMMAND that starts chain of responsibility behavioral design pattern.
 * 
 * @author Elad Goldenberg
 * @author Raz Tibi
 * @author Razi Mograbi
 * @author Shimron Yifrah
 * @author Alex Baboshin
 * @author Shaked Oz
 * @version May 2023
 */

public class MessageHandler 
{
    /**
     * Handles the incoming message from the client and performs the corresponding action.
     *
     * @param msg    The message received from the client.
     * @param client The connection from which the message originated.
     * @throws IOException If an I/O error occurs while handling the message.
     */
	/**
	 * @param msg
	 * @param client
	 * @throws IOException
	 * 
	 */
	public static void HandleMessage(final Object msg, final ConnectionToClient client) throws IOException 
	{
		// the message isnt an ArrayList
		if(! (msg instanceof Message) ) {
			client.sendToClient(null);
			return;
		}
			
		Message data_from_client = (Message)msg;
		
		if(data_from_client.getOperation() == null) {
			client.sendToClient(new Message(null, null));
			return;
		}
		
		final String client_action = data_from_client.getOperation();
		/**
		 * Handles the client action based on the specified client action constant.
		 *
		 * @param client_action The client action constant indicating the action to be performed.
		 * @param data_from_client The data received from the client.
		 * @param client The client connection.
		 */
		switch(client_action) {
		
			case CONSTANTS.CheckLogin:
			{
				/**
		         * Handles the client action for checking login credentials.
		         * Sends the login result to the client.
		         */
				client.sendToClient(mysqlConnection.userLogin(data_from_client));
				break;
			}
						
			case CONSTANTS.getQuestionsFromDB:
			{
				/**
		         * Handles the client action for retrieving questions from the database.
		         * Sends the question table to the client.
		         */
				client.sendToClient(mysqlConnection.getQuestionTableFromDB());
				break;
			}
			
			case CONSTANTS.logOutUser:
			{
				/**
		         * Handles the client action for logging out a user.
		         * Sets the "isLogged" flag to zero for the specified user and sends the logout result to the client.
		         */
				Message log_out_message = mysqlConnection.setIsLoggedToZero(data_from_client);
				client.sendToClient(log_out_message);
				ArrayList<Boolean> is_logout_successful = log_out_message.convertCommandsToArrayList();
				break;
			}
			case CONSTANTS.EditQuestion:
			{
				/**
		         * Handles the client action for editing a question.
		         * Updates the question in the database and sends a response to the client.
		         */
				@SuppressWarnings("unchecked") 
				ArrayList<Question> questionToUpDate= (ArrayList<Question>)data_from_client.getCommands(); // get the data
				mysqlConnection.updateQuestion(questionToUpDate); // send to DB to update the question
				Message sendDataToClient = new Message(CONSTANTS.ResponseEditQuestion,null);
				client.sendToClient(sendDataToClient);
				break;
			}
			case CONSTANTS.AddQuestion:
			{
				/**
		         * Handles the client action for adding a question to the database.
		         * Adds the question to the database and sends a response to the client.
		         */
				//adds a question to the database
				Message sendDataToClient;
				boolean successfullyAdded=false;
				ArrayList<Object> data = (ArrayList<Object>)data_from_client.getCommands();
				Question questionToAdd = (Question)data.get(0);
				String lecturerUserName= (String)data.get(1);
				successfullyAdded=mysqlConnection.addNewQuestion(questionToAdd,lecturerUserName);
				ArrayList<Boolean> respons = new ArrayList<Boolean>();
				respons.add(successfullyAdded);
				sendDataToClient = new Message(CONSTANTS.ResponseAddQuestion,respons);
				client.sendToClient(sendDataToClient);
				break;
			}
			case CONSTANTS.ExitClient:
			{
				/**
		         * Handles the client action for exiting the client connection.
		         * Disconnects the client and sends an exit response to the client.
		         */
				CemsServer.disconnectClient(client.getInetAddress(),
							client.getInetAddress().getHostName(), "Disconnected");
				client.sendToClient(new Message(CONSTANTS.ResponseExitClient,""));
				break;
			}
			case CONSTANTS.AddCreatedExam:
			{
				/**
		         * Handles the client action for adding a newly created exam to the database.
		         * Adds the exam to the database and sends a response to the client.
		         */
				//adds an exam that was just created to the database.
				Exam examToInsert = (Exam)data_from_client.getCommands();
				boolean response = mysqlConnection.addNewExam(examToInsert); // return true if exam was added successfuly to DB else false
				Message sendToClient = new Message(CONSTANTS.ResponseAddCreatedExam,response);
				client.sendToClient(sendToClient);
				break;
			}
			case CONSTANTS.getConductExam:
			{
				/**
		         * Handles the client action for retrieving conduct exam data.
		         * Sends the conduct exam data to the client.
		         */
				//handles a getter for conduct exam, it is fired when a student presses start exam after 
				//entering the exam code. It is done in startTestScreen
				
				ConductExam data = (ConductExam)data_from_client.getCommands(); // get the data
				
				Message dataToClient = new Message(CONSTANTS.getConductExam, data);
				client.sendToClient(dataToClient); // send to client
				break;
			}
			case CONSTANTS.getStudentNotTakenTests: 
			{
				/**
			     * Handles the client action for retrieving tests that a student has not taken yet.
			     * Retrieves relevant exams for the student from the database and sends them to the client.
			     *
			     * @param username The username of the student.
			     */
				//handles a getter for student's tests that has yet to be done. If the student is signed for 
				//some test, that test will be composed in the window startTestScreen. The test button will be locked
				//until the lecturer approves the test.
				String username = (String)data_from_client.getCommands();
				ConductExam relevantExams = null;
				try {
					relevantExams = mysqlConnection.getRelevantConductExams(username);
				} catch (SQLException e) 
				{
					e.printStackTrace();
				}
				
				//create message to client to give it the exams
				Message dataToClient = new Message(CONSTANTS.responseToGetStudentNotTakenTests, relevantExams);
				client.sendToClient(dataToClient);
				break;
			}
			case CONSTANTS.downloadTest:
			{
				/**
			     * Handles the client action for downloading a test.
			     * This case is not implemented in the current code.
			     */
				//handles a getter for when the student chooses to take the test manually. 
				break;
			}
			
			case CONSTANTS.getSubjectAndCoursesTeacher:
			{
				/**
			     * Handles the client action for retrieving subjects and courses taught by a teacher.
			     * Retrieves the subjects and their relevant courses from the database and sends them to the client.
			     *
			     * @param userName The username of the teacher.
			     */
				String userName = (String) data_from_client.getCommands();
				ArrayList<Subject> subjects = mysqlConnection.fetchTeacherSubjectAndCourses(userName); // get list of all subject that lecturer is teaching (in subject entity there is all the relevant courses for this subject the the lecturer teach)
				Message dataToClient = new Message(CONSTANTS.responsegetSubjectAndCoursesTeacher,subjects);
				client.sendToClient(dataToClient);
				break;
			}
			
			case CONSTANTS.getQuestionsBySubject:
			{
				/**
			     * Handles the client action for retrieving questions by subject.
			     * Retrieves questions for a specific subject from the database and sends them to the client.
			     *
			     * @param subject The subject for which to retrieve the questions.
			     */
				Subject subject = (Subject)data_from_client.getCommands(); // get the subject
				ArrayList<Question> questions = mysqlConnection.getQuestionBySubject(subject);
				Message dataToClient = new Message(CONSTANTS.responsegetQuestionsBySubject,questions);
				client.sendToClient(dataToClient);
				break;
			}
			
			case CONSTANTS.getQuestionsBySubjectAndByLecturerBank:
			{
				 /**
			     * Handles the client action for retrieving questions by subject from the lecturer's question bank.
			     * Retrieves questions for a specific subject from the lecturer's question bank in the database and sends them to the client.
			     *
			     * @param data An ArrayList containing the username (String) and subject (Subject) parameters.
			     */
				ArrayList<Object> data = (ArrayList<Object>)data_from_client.getCommands(); // extact data from the message we got from the client
				ArrayList<Question> questions = mysqlConnection.getQuestionsFromQuestionBankBySubject((String)data.get(0),(Subject)data.get(1)); //function get userName and object of Subject (return list of relevant questions)
				Message dataToClient = new Message(CONSTANTS.responsegetQuestionsBySubjectAndByLecturerBank,questions); // build message to client
				client.sendToClient(dataToClient); // send to client
				break;
			}
			case CONSTANTS.GetSubjectsFromDB:
			{
				/**
			     * Handles the client action for retrieving subjects from the database.
			     * Retrieves all subjects from the database and sends them to the client.
			     */
				client.sendToClient(mysqlConnection.GetSubjectsFromDB());
				break;
			}
			
			case CONSTANTS.ChangePersonalBankQuestion:
			{
				/**
			     * Handles the client action for changing the personal bank of questions.
			     * Changes the personal bank of questions for a user in the database.
			     *
			     * @param data An ArrayList containing the username (String), new list of questions (ArrayList<Question>),
			     *             and subject (Subject) parameters.
			     * @return The response indicating whether the personal bank change was successful or not.
			     */
				ArrayList<Object> data = (ArrayList<Object>)data_from_client.getCommands(); // extact data from the message we got from the client
				boolean DBresponse = mysqlConnection.changePersonalBank((String)data.get(0),(ArrayList<Question>)data.get(1),(Subject)data.get(2)); //function get userName and new list of question to insert into the presonal question bank  and the subject of the questions 
				Message dataToClient = new Message(CONSTANTS.responseChangePersonalBankQuestion,DBresponse); // build message to client
				client.sendToClient(dataToClient); // send to client
				break;
			}
			case CONSTANTS.checkIfPasswordMatchesAndTheTestFile:{
				/**
			     * Handles the client action for checking if the password matches for a test file.
			     * Checks if the password provided by the client matches the test file for a given username and conduct exam ID.
			     *
			     * @param data A List containing the username (String), password (String), and conduct exam ID (int) parameters.
			     * @return The conduct exam details if the password matches, null otherwise.
			     */
				List<String> data = (ArrayList<String>)data_from_client.getCommands();
				String username = data.get(0);
				String password = data.get(1);
				int conduct_exam_id = Integer.parseInt(data.get(2));
				ConductExam exam;
				try {
					exam = mysqlConnection.getCheckIfPasswordMatchesAndTheTestFile(username, password, conduct_exam_id);
				} catch (SQLException e) {
					exam = null;
					e.printStackTrace();
				}
				Message dataToClient = new Message(CONSTANTS.responseCheckIfPasswordMatchesAndTheTestFile,exam); // build message to client
				client.sendToClient(dataToClient); // send to client
				break;
			}
			case CONSTANTS.setStudenIsTakenExamForCurrectConductTestToOne:{
				/**
			     * Handles the client action for setting the "is taken exam" flag to 1 for a student in a conduct exam.
			     * Sets the "is taken exam" flag to 1 for the specified conduct exam and student in the database.
			     *
			     * @param data A List containing the conduct exam ID (int) and username (String) parameters.
			     */
				List<String> data = (ArrayList<String>)data_from_client.getCommands();
				int conduct_exam_id = Integer.parseInt(data.get(0)); //get conduct exam id
				String username = data.get(1);		 				 //get username
				try {
					mysqlConnection.setStudenIsTakenExamForCurrectConductTestToOne(conduct_exam_id, username);
				}catch(SQLException e) {
					e.printStackTrace();
				}
				Message dataToClient = new Message(CONSTANTS.responseSetStudenIsTakenExamForCurrectConductTestToOne, null); // build message to client
				client.sendToClient(dataToClient); // send to client
				break;
			}
			case CONSTANTS.getTestInformationAtConduct:{
				/**
			     * Handles the client action for retrieving test information at conduct.
			     * Retrieves the test information for a conduct exam from the database and sends it to the client.
			     *
			     * @param data The ConductExam object representing the conduct exam.
			     * @return The Exam object containing the test information.
			     */
				ConductExam data = (ConductExam)data_from_client.getCommands();
				Exam exam;
				try {
					exam = mysqlConnection.getTestInformationAtConduct(data);
				}catch(SQLException e) {
					exam = null;
					e.printStackTrace();
				}
				Message dataToClient = new Message(CONSTANTS.responseGetTestInformationAtConduct, exam); // build message to client
				client.sendToClient(dataToClient); // send to client
				break;
			}
			case CONSTANTS.getExamsFromDB:
			{
				/**
			     * Handles the client action for retrieving all exams from the database.
			     * Retrieves the exam table from the database and sends it to the client.
			     */
				try {
					client.sendToClient(mysqlConnection.getExamTableFromDB());
				} catch (IOException | SQLException e) {

					e.printStackTrace();
				}
				break;
			}
			
			case CONSTANTS.getExamsBySubject:
			{
				/**
			     * Handles the client action for retrieving exams by subject.
			     * Retrieves exams for a specific subject from the database and sends them to the client.
			     *
			     * @param subject The subject for which to retrieve the exams.
			     */
				ArrayList<Exam> examsToReturn;
				Subject subject = (Subject)data_from_client.getCommands();
				examsToReturn=mysqlConnection.getExamBySubject(subject); // get exams by subject
				Message dataToClient = new Message(CONSTANTS.responseGetExamsBySubject,examsToReturn); // create message to client
				client.sendToClient(dataToClient); //send to client
				break;
			}
			
			case CONSTANTS.getPersonalExamBank:
			{
				/**
			     * Handles the client action for retrieving exams from the personal exam bank.
			     * Retrieves exams from the personal exam bank of a user from the database and sends them to the client.
			     *
			     * @param userName The username of the user.
			     */
				ArrayList<Exam> examsToReturn;
				String userName = (String)data_from_client.getCommands();
				examsToReturn=mysqlConnection.getPersonalExamBank(userName); // get exams from presonal bank of user name
				Message dataToClient = new Message(CONSTANTS.responseGetPersonalExamBank,examsToReturn); // create message to client
				client.sendToClient(dataToClient); // send to client
				break;
			}
			
			case CONSTANTS.ChangePersonalBankExams:
			{
				/**
			     * Handles the client action for changing the personal bank of exams.
			     * Changes the personal bank of exams for a user in the database.
			     *
			     * @param data An ArrayList containing the username (String), new list of exams (ArrayList<Exam>),
			     *             subject (Subject), and course (Course) parameters.
			     * @return The response indicating whether the personal bank change was successful or not.
			     */
				ArrayList<Object> data = (ArrayList<Object>)data_from_client.getCommands(); // extact data from the message we got from the client
				boolean DBresponse = mysqlConnection.changePersonalExamBank((String)data.get(0),(ArrayList<Exam>)data.get(1),(Subject)data.get(2),(Course)data.get(3)); //function get userName and new list of exam to insert to personal exam bank and the subject and course of the exams
				Message dataToClient = new Message(CONSTANTS.responseChangePersonalBankExams,DBresponse); // build message to client
				client.sendToClient(dataToClient); // send to client
				break;
			}
			case CONSTANTS.UploadStudentExamWordFileSolution:{
				/**
			     * Handles the client action for uploading a student's exam word file solution.
			     * Uploads the student's exam word file solution to the database.
			     *
			     * @param data The ManualExamConductObject containing the necessary information for uploading the file.
			     * @return The response indicating whether the upload was successful or not.
			     */
				ManualExamConductObject data = (ManualExamConductObject) data_from_client.getCommands();
				client.sendToClient(mysqlConnection.uploadWordFileToDataBase(data));
				break;
			}
			case CONSTANTS.approveTimeExtension:{
				/**
			     * Handles the client action for approving a time extension for an exam.
			     * Updates the duration of the exam in the conduct_exam table in the database.
			     *
			     * @param data A List containing the conduct exam ID (String) and the time extension (String).
			     * @return A List containing the updated exam ID and duration.
			     */
				//this creates a message to update the duration at conduct_exam
				//id is data(0) and extension is 2nd
				List<String> data = (ArrayList)data_from_client.getCommands();
				List<String> arr;
				try {
					arr = mysqlConnection.updateExamTime(data);
				}catch(SQLException e) {
					arr = null;
					e.printStackTrace();
				}
				Message dataToClient = new Message(CONSTANTS.responseApproveTimeExtension, arr);
				client.sendToClient(dataToClient);
				break;
			}
			case CONSTANTS.getGradesByUsername:{
				/**
			     * Handles the client action for retrieving grades by username.
			     * Retrieves the grades for a specific username from the database and sends them to the client.
			     *
			     * @param username The username of the user.
			     */
				String username = (String)data_from_client.getCommands(); // get the username
				ArrayList<Grade> grades = mysqlConnection.getGradesByUsername(username);
				Message dataToClient = new Message(CONSTANTS.responseGetGradesByUsername,grades);
				client.sendToClient(dataToClient);
				break;
			}
			case CONSTANTS.getGradedWordTest:{
				/**
			     * Handles the client action for retrieving a graded word test.
			     * Retrieves the graded word test for a specific username and test ID from the database and sends it to the client.
			     *
			     * @param data A List containing the username (String) and test ID (String) parameters.
			     * @return The GradedTest object containing the graded word test.
			     */
				ArrayList<String> data = (ArrayList<String>) data_from_client.getCommands();
				GradedTest gradedTest = mysqlConnection.getGradedTestByUsernameAndTestId(data.get(0),data.get(1));
				Message dataToClient = new Message(CONSTANTS.responseGetGradedWordTest,gradedTest);
				client.sendToClient(dataToClient);
				break;
			}
			case CONSTANTS.getStudentsDoneTestData:{
				/**
			     * Handles the client action for retrieving students' done test data.
			     * Retrieves the data of students who have completed a specific exam for a subject from the database
			     * and sends it to the client.
			     *
			     * @param data The StudentsExamDataGetter object containing the necessary parameters.
			     * @return A List of StudentsDataInExam objects containing the students' data.
			     */
				//this creates a message to get all exam data for a specific subject
				//each exam when the time is finished/submitted
				StudentsExamDataGetter data = (StudentsExamDataGetter)data_from_client.getCommands();
				List<StudentsDataInExam> students_data = null;
				try {
					students_data = mysqlConnection.getStudentsDoneTestData(data);
				}catch(SQLException e) {
					e.printStackTrace();
				}
				Message dataToClient = new Message(CONSTANTS.responseGetStudentsDoneTestData, students_data);
				client.sendToClient(dataToClient);
				break;
			}
			case CONSTANTS.updateStudentDataInExam:{
				/**
			     * Handles the client action for updating student data in an exam.
			     * Updates the values of a StudentsDataInExam entity in the students_data_in_exam table.
			     *
			     * @param data The StudentsDataInExam object containing the updated values.
			     * @return A boolean indicating whether the update was successful or not.
			     */
				//this gets an entity StudentsDataInExam and updates its values in the table students_data_in_exam
				StudentsDataInExam data = (StudentsDataInExam)data_from_client.getCommands();
				boolean success = false;
				try {
					success = mysqlConnection.updateStudentDataInExam(data);
				}catch (SQLException e) {
					e.printStackTrace();
				}
				Message dataToClient = new Message(CONSTANTS.responseUpdateStudentDataInExam, success);
				client.sendToClient(dataToClient);
				break;
			}
			case CONSTANTS.CheckForExamTimeChange:{
				/**
			     * Handles the client action for checking if an exam time change has occurred.
			     * Checks the database for any time change in the specified exam.
			     *
			     * @param data The ExamTimeChangeObject containing the necessary information for the check.
			     * @return A boolean indicating whether a time change has occurred or not.
			     */
				ExamTimeChangeObject data = (ExamTimeChangeObject) data_from_client.getCommands();
				client.sendToClient(mysqlConnection.checkForTimeChangeInDataBase(data));
				break;
			}
			case CONSTANTS.getStudentsUsers:
			{
				/**
			     * Handles the client action for retrieving student users.
			     * Retrieves all users with the student role from the database and sends them to the client.
			     *
			     * @return An ArrayList of User objects representing the student users.
			     */
				ArrayList<User> students = mysqlConnection.getUsersByRole(CONSTANTS.studentRole);
				Message dataToClient = new Message(CONSTANTS.responseGetStudentsUsers,students);
				client.sendToClient(dataToClient);
				break;
			}
			case CONSTANTS.InsertStudentExamDataIntoDB:{
				/**
			     * Handles the client action for inserting student exam data into the database.
			     * Inserts the StudentsDataInExam object into the students_data_in_exam table.
			     *
			     * @param data The StudentsDataInExam object to be inserted.
			     * @return A boolean indicating whether the insertion was successful or not.
			     */
				StudentsDataInExam data = (StudentsDataInExam) data_from_client.getCommands();
				client.sendToClient(mysqlConnection.InsertStudentDataExamIntoDB(data));
				break;
			}
			case CONSTANTS.availablePasswordForConductExam:
			{
				/**
			     * Handles the client action for checking the availability of a password for conducting an exam.
			     * Checks the database to determine if the password is available for use.
			     *
			     * @param password The password to be checked.
			     * @return A boolean indicating whether the password is available or not.
			     */
				String password = (String)data_from_client.getCommands();
				boolean DBrespones = mysqlConnection.checkAvailablePasswordForConductExam(password);
				Message dataToClient = new Message(CONSTANTS.responseAvailablePasswordForConductExam,DBrespones);
				client.sendToClient(dataToClient);
				break;
			}
			case CONSTANTS.startExamToConduct:
			{
				/**
			     * Handles the client action for starting an exam to conduct.
			     * Adds the ConductExam object to the conduct_exam table in the database.
			     *
			     * @param examToConduct The ConductExam object representing the exam to be conducted.
			     * @return A boolean indicating whether the addition was successful or not.
			     */
				ConductExam examToConduct = (ConductExam)data_from_client.getCommands();
				boolean DBrespones = mysqlConnection.addStartExamToConduct(examToConduct);
				Message dataToClient = new Message(CONSTANTS.responseStartExamToConduct,DBrespones);
				client.sendToClient(dataToClient);
				break;
			}
			case CONSTANTS.getExamsToConductBySubjectByLecturer:
			{
				 /**
			     * Handles the client action for retrieving exams to conduct by subject and lecturer.
			     * Retrieves all exams to be conducted for a specific subject and lecturer from the database.
			     *
			     * @param data A List containing the Subject object and the lecturer's username (String).
			     * @return An ArrayList of ConductExam objects representing the exams to be conducted.
			     */
				ArrayList<Object> data = (ArrayList<Object>)data_from_client.getCommands();
				Subject subject = (Subject) data.get(0);
				String UserName = (String) data.get(1);
				ArrayList<ConductExam> conductExams = mysqlConnection.getConductExamsBySubjectByLecturer(subject, UserName);
				Message sendToClient = new Message(CONSTANTS.responseGetExamsToConductBySubjectByLecturer,conductExams);
				client.sendToClient(sendToClient);
				break;
			}
			
			case CONSTANTS.lectuerRequestTimeExtension:
			{
				/**
			     * Handles the client action for requesting a time extension as a lecturer.
			     * Adds the RequestTimeExtension object to the time_extension_requests table in the database.
			     *
			     * @param requestTimeExtension The RequestTimeExtension object representing the time extension request.
			     * @return A boolean indicating whether the addition was successful or not.
			     */
				//Executes the logic for refreshing the request time extension status.
				RequestTimeExtension requestTimeExtension = (RequestTimeExtension) data_from_client.getCommands();
				boolean DBrespons =mysqlConnection.addLecturerRequestTimeExtension(requestTimeExtension);
				Message sendToClient = new Message(CONSTANTS.responseLectuerRequestTimeExtension,DBrespons);
				client.sendToClient(sendToClient);
				break;
			}
			
			case CONSTANTS.refreshRequestTimeExtensionStatus:
			{
				/**
			     * Handles the client action for refreshing the request time extension status.
			     * Retrieves the updated status of time extension requests from the database and sends them to the client.
			     *
			     * @param outDatedData An ArrayList of ConductExam objects containing the outdated data.
			     */
				ArrayList<ConductExam> outDatedData = (ArrayList<ConductExam>)data_from_client.getCommands(),upDoDateData;	               
				upDoDateData = mysqlConnection.getUpDoDateConductExanDurationAndRequestStatus(outDatedData); // *return fake list only with conduct exam id duration and request status*	
				Message sendToClient = new Message(CONSTANTS.responseRefreshRequestTimeExtensionStatus,upDoDateData);
				client.sendToClient(sendToClient);
				break;
			}
			
			case CONSTANTS.endConductExam:
			{
				/**
			     * Handles the client action for ending a conducted exam.
			     * Updates the is_open column to 0 and deletes the request for the conduct_exam_id from the time_extension_requests table.
			     *
			     * @param conductExamIdToEnd The ID of the conducted exam to be ended.
			     * @return A boolean indicating whether the operation was successful or not.
			     */
				//Executes the logic for checking if an exam is locked.
				int conductExamIdToEnd = (int)data_from_client.getCommands();               
				boolean DBresponse = mysqlConnection.endConductExamByConductExamID(conductExamIdToEnd); // change is_open=0 and delete request for he conduct_exam_id from the table time_extension_requests
				Message sendToClient = new Message (CONSTANTS.responseEndConductExam,DBresponse);
				client.sendToClient(sendToClient);
				break;
			}
			case CONSTANTS.CheckForExamLock:{
				/**
			     * Handles the client action for checking if an exam is locked.
			     * Checks the database to determine if the specified exam is locked for a specific screen type.
			     *
			     * @param data The ConductExamIdWithScreenTypeObject containing the conduct exam ID and screen type.
			     * @return A boolean indicating whether the exam is locked or not.
			     */
				ConductExamIdWithScreenTypeObject data = (ConductExamIdWithScreenTypeObject) data_from_client.getCommands();
				client.sendToClient(mysqlConnection.checkIfExamIsLocked(data));
				break;
			}
			case CONSTANTS.denyTimeExtension:{
				/**
			     * Handles the client action for denying a time extension request.
			     * Denies the time extension for a conduct_exam based on its ID.
			     *
			     * @param conductExamIdToDenyItsTimeExtension The ID of the conduct_exam to deny its time extension.
			     * @return A boolean indicating whether the operation was successful or not.
			     */
				//this is a message handled for the head of department. It denies a time extension for a conduct_exam based on id (int)
				int conductExamIdToDenyItsTimeExtension = (int)data_from_client.getCommands();
				boolean DBresponse = false;
				try{
					DBresponse = mysqlConnection.denyTimeExtensionForConductExamId(conductExamIdToDenyItsTimeExtension);
				}catch(SQLException e) {
					e.printStackTrace();
				}	
				Message sendToClient = new Message(CONSTANTS.responseDenyTimeExtension, DBresponse);
				client.sendToClient(sendToClient);
				break;
			}
			case CONSTANTS.getTimeExtensionRequests:{
				/**
			     * Handles the client action for retrieving time extension requests.
			     * Retrieves all time extension requests from the time_extension_requests table in the database.
			     *
			     * @return A List of ConductExam objects representing the time extension requests.
			     */
				//Executes the logic for handling the time extension requests command.
				List<ConductExam> timeExtensionList = null;
				try {
					timeExtensionList = mysqlConnection.getTimeExtensionRequests();
				}catch(SQLException e) {
					e.printStackTrace();
				}	
				Message sendToClient = new Message(CONSTANTS.responseGetTimeExtensionRequests, timeExtensionList);
				client.sendToClient(sendToClient);
				break;
			}

			case CONSTANTS.getStudentGradeSMS_Simulation:{
				/**
				 * Handles the client action for retrieving student grade SMS simulation.
				 * Retrieves the SMS simulation data for a specific student from the database.
				 *
				 * @param data_from_client The data received from the client.
				 */
				//Executes the logic for handling the student grade SMS simulation command.
				List<StudentsDataInExam> smsList = null;
				String username = (String)data_from_client.getCommands();
				try {
					smsList = mysqlConnection.getSMS_Simulation(username);
				}catch(SQLException e) {
					e.printStackTrace();
				}	
				Message sendToClient = new Message(CONSTANTS.responseStudentGradeSMS_Simulation, smsList);
				client.sendToClient(sendToClient);
				break;
			}

			case CONSTANTS.getCourseNameAndRelevantDataExamsForLecturer:{
				Pair<String, String> data = (Pair<String, String>) data_from_client.getCommands();
		
				client.sendToClient(mysqlConnection.GetAllLecturerCourseDataIncludingExamsGrades(data));
				break;
			}
			
			case CONSTANTS.getTakenExamsForStatistics:{
				HashMap<String, Exam> data = mysqlConnection.getTakenExamsForStatistics();	
				Message sendToClient = new Message(CONSTANTS.responseTakenExamsForStatistics, data);
				client.sendToClient(sendToClient);
				break;
			}
			case CONSTANTS.getStatisticsData:{
				ArrayList<StatisticsReport> data = mysqlConnection.getStatisticsData();	
				Message sendToClient = new Message(CONSTANTS.responseStatisticsData, data);
				client.sendToClient(sendToClient);
				break;
			}
			case CONSTANTS.approveAllGradesConductExamAndCreateStatistics :
			{
				int conductExamId = (int)data_from_client.getCommands();
				boolean DBresponse = mysqlConnection.approveAllGradeOfConductExamAddCreateStatistic(conductExamId);
				Message sendToClient = new Message(CONSTANTS.responseApproveAllGradesConductExamAndCreateStatistics,DBresponse);
				client.sendToClient(sendToClient);
				break;
			}
			case CONSTANTS.registerUser:{
				/**
				 * Handles the client action for registering simulation.
				 * sends User object to register, if exists gets false else gets true.
				 *
				 * @param data_from_client The data received from the client.
				 */
				boolean registered = false;
				User userToRegister = (User)data_from_client.getCommands();
				try {
					registered = mysqlConnection.registerUser(userToRegister);
				}catch(SQLException e) {
					e.printStackTrace();
					System.out.println("Error in messagehandler resiterUser SQLException");
				}
				Message sendToClient = new Message(CONSTANTS.responseRegisterUser, registered);
				client.sendToClient(sendToClient);
				break;
			}
			case CONSTANTS.getStudentsTakenTests:{
				HashMap<String,ArrayList<Grade>> data = mysqlConnection.getStudentsTakenTests();
				Message sendToClient = new Message(CONSTANTS.responseStudentsTakenTests, data);
				client.sendToClient(sendToClient);
				break;
			}
			case CONSTANTS.updateProfilePicture:{
				ArrayList<Object> data = (ArrayList<Object>)data_from_client.getCommands();
				String username = (String)data.get(0);
				byte[] pic = (byte[])data.get(1);
				mysqlConnection.insertProfilePicture(username,pic);
				Message sendToClient = new Message(CONSTANTS.responseUpdateProfilePicture, null);
				client.sendToClient(sendToClient);
				break;
			}
			case CONSTANTS.getProfilePicture:{
				String username = (String)data_from_client.getCommands();
				byte[] data = mysqlConnection.getProfilePicture(username);
				Message sendToClient = new Message(CONSTANTS.responseGetProfilePicture, data);
				client.sendToClient(sendToClient);
				break;
			}
			default:{
				/**
				 * Handles the default case when the command is unknown.
				 * Sends a message indicating an unknown command to the client.
				 */
				client.sendToClient(new Message(CONSTANTS.UnknownCommand, null));
				break;
			}
		}
	}
}
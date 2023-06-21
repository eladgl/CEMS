package serverUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entities.ConductExam;
import entities.ConductExamIdWithScreenTypeObject;
import entities.Course;
import entities.Exam;
import entities.Grade;
import entities.GradedTest;
import entities.ExamTimeChangeObject;
import entities.ManualExamConductObject;
import entities.Question;
import entities.RequestTimeExtension;
import entities.StatisticsReport;
import entities.StatisticsReportDataForLecturer;
import entities.StudentsDataInExam;
import entities.StudentsExamDataGetter;
import entities.Subject;
import entities.TimeDifferenceObject;
import entities.User;
import entities.ExamType;
import entities.Pair;
import utilities.CONSTANTS;
import utilities.Message;

/**
 * The mysqlConnection class provides methods for connecting to a MySQL database
 * and performing various operations on the database tables. It also includes
 * methods for retrieving and updating question data. It is a singleton
 */
public class mysqlConnection {
	/**
	 * The mysqlConnection class provides methods for connecting to a MySQL database
	 * and performing various operations on the database tables.
	 */
	private static Connection conn = null;

	private mysqlConnection() {
	}

	/**
	 * Connects to the MySQL database using the provided credentials.
	 * 
	 * @param db_url      the URL of the database
	 * @param db_username the username for accessing the database
	 * @param db_password the password for accessing the database
	 * @return the connection object if successful, or null if the connection fails
	 */
	public static Connection connectToDB(String db_url, String db_username, String db_password) {
		if (conn == null) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
				System.out.println("Driver definition succeed");
			} catch (Exception ex) {
				/* handle the error */
				System.out.println("Driver definition failed");
				return null;
			}

			try {
				conn = DriverManager.getConnection(db_url, db_username, db_password);
				System.out.println("SQL connection succeed");

			} catch (SQLException ex) {/* handle any errors */
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
				return null;
			}
		}
		return conn;
	}

	/**
	 * Method to retrieve a list of all the questions from the "cems.question" table
	 * 
	 * @return an ArrayList of Question objects
	 */
	public static Message getQuestionTableFromDB() {
		ArrayList<Question> questions = new ArrayList<Question>();
		try {
			// Create a PreparedStatement object to execute a precompiled SQL statement
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM cems.question");
			ResultSet resultQuestionSet = ps.executeQuery();
			while (resultQuestionSet.next()) {
				// Add a new Question object to the questions ArrayList for each row in the
				// result set
				questions.add(new Question(resultQuestionSet.getString("id"),
						resultQuestionSet.getString("question_text"), resultQuestionSet.getString("subject"),
						resultQuestionSet.getString("subject_code"), resultQuestionSet.getString("course_name"),
						resultQuestionSet.getString("question_number"), resultQuestionSet.getString("lecturer"), null,
						new String[] { resultQuestionSet.getString("choice1"), resultQuestionSet.getString("choice2"),
								resultQuestionSet.getString("choice3"), resultQuestionSet.getString("choice4") },
						resultQuestionSet.getInt("answer")));

			}
		} catch (Exception e) {
			// error message if the questions cannot be retrieved
			System.out.println("Importing questions from cems.question is unsuccesful!");
			return null;
		}
		return new Message(CONSTANTS.receiveQuestions, questions);
	}

	/**
	 * 
	 * Updates the question text and number in the database table "cems.question"
	 * using the primary key 'id' to identify which question to update.
	 * 
	 * @param updatedQuestionTextAndNumberValues An ArrayList of Question objects
	 *                                           where the first element is the
	 *                                           original question and the second
	 *                                           store the new data
	 * @param lecturerUserName
	 */
	public static void updateQuestion(ArrayList<Question> updatedQuestionTextAndNumberValues) {
		PreparedStatement ps = null;
		String idToUpdate = updatedQuestionTextAndNumberValues.get(0).getID(); // get the id of the question we need to
		Question updateData = updatedQuestionTextAndNumberValues.get(1); // get the data we need to update
		// SQL query to update the row with the specified ID
		String sql = "UPDATE cems.question " + "SET id = ?, " + "    subject = ?, " + "    subject_code = ?, "
				+ "    course_name = ?, " + "    question_text = ?, " + "    question_number = ?, "
				+ "    lecturer = ?, " + "    choice1 = ?, " + "    choice2 = ?, " + "    choice3 = ?, "
				+ "    choice4 = ?, " + "    answer = ? " + "WHERE id = ?";

		try {
			// edit question in the general talbe question
			String[] choices = updateData.getOptionsText();
			PreparedStatement statement = conn.prepareStatement(sql);
			// Set the values for the update query parameters
			statement.setString(1, updateData.getID()); // new ID value
			statement.setString(2, updateData.getSubject());
			statement.setString(3, updateData.getSubjectCode());
			statement.setString(4, updateData.getCourseName());
			statement.setString(5, updateData.getQuestionDescription());
			statement.setString(6, updateData.getQuestionNumber());
			statement.setString(7, updateData.getAuthorName());
			statement.setString(8, choices[0]);
			statement.setString(9, choices[1]);
			statement.setString(10, choices[2]);
			statement.setString(11, choices[3]);
			statement.setInt(12, updateData.getCorrectAnswer()); // new_answer
			statement.setString(13, idToUpdate); // ID of the row to update
			statement.executeUpdate();
			// update in the question_bank the oldID into the newID for all the banks that
			// question is exist (in case user changed number of question so the id of the
			// question as been changed as well)
			sql = "UPDATE cems.question_bank SET questionID = ? WHERE questionID = ?";
			statement = conn.prepareStatement(sql);
			statement.setString(1, updateData.getID()); // Update to newID
			statement.setString(2, idToUpdate); // In all rows we found the oldID
			statement.executeUpdate();

			// Execute the update query
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param data_from_client an Message<String> containing {username, password}
	 *                         and operation.
	 * @return an Message<Boolean> containing a respond to the client operation
	 *         {true\false}
	 */
	public static Message userLogin(Message data_from_client) {
		// db_respond.add(CONSTANTS.ValidLoginAnswer);
		ArrayList<User> db_respond = new ArrayList<>();
		User user;
		ArrayList<String> commands_from_client = data_from_client.convertCommandsToArrayList();
		final String username = commands_from_client.get(0);
		final String password = commands_from_client.get(1);

		try {
			PreparedStatement ps = conn
					.prepareStatement("SELECT * FROM cems.users WHERE `userName` = ? AND `password` = ? AND `isLogged` = '0'");
			// ps.setString(1, "lecturer");
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet resultQuestionSet = ps.executeQuery();
			// the user and pass is found in the db

			if (resultQuestionSet.next()) {
				user = new User(username, resultQuestionSet.getString(4), resultQuestionSet.getString(3), password,
						resultQuestionSet.getString(1), resultQuestionSet.getString(6), null,
						resultQuestionSet.getString(8).equals("1"), resultQuestionSet.getString(7));
				db_respond.add(user);
				// Change the users isLogged value to 1
				ps = mysqlConnection.conn.prepareStatement("UPDATE cems.users SET `isLogged` = ? WHERE `userName` = ?");
				ps.setBoolean(1, true);
				ps.setString(2, username);
				ps.executeUpdate();
			} else {
				db_respond.add(null);
			}
			ps.close();
			resultQuestionSet.close();
			return new Message(CONSTANTS.ValidLoginAnswer, db_respond);

		} catch (Exception e) {
			System.out.println("Error in the user Name From DB");
			return null;
		}
	}

	/**
	 * Adds a new question to the database.
	 *
	 * @param question          The Question object representing the new question to
	 *                          be added.
	 * @param lecturer_userName The username of the lecturer adding the question.
	 * @return True if the question is successfully added, false otherwise.
	 */
	public static boolean addNewQuestion(Question question, String lecturer_userName) {
		ArrayList<String> questionNumbers = new ArrayList<>();
		String SubjectCodeVersion = question.getSubjectCode(), SubjectNameVersion = question.getSubject();
		try {

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM cems.question WHERE `subject` = ?");
			ps.setString(1, SubjectNameVersion);
			ResultSet resultQuestionNumbers = ps.executeQuery();
			// calculate first available question number in the given subject
			while (resultQuestionNumbers.next()) {
				questionNumbers.add(resultQuestionNumbers.getString(6));
			}
			String QuestionNumberToInsert = "001";
			for (int i = 0; i < 1000; i++) {
				if (!questionNumbers.contains(QuestionNumberToInsert)) {
					break;
				}
				int number = Integer.parseInt(QuestionNumberToInsert);
				number++; // Increment the number
				QuestionNumberToInsert = String.format("%03d", number); // Pad with leading zeros if necessary
			}
			// add this question to the general bank
			String newIdQuestion = SubjectCodeVersion + QuestionNumberToInsert;
			ps = conn.prepareStatement(
					"INSERT INTO cems.question (id, subject, subject_code, course_name, question_text, question_number, lecturer, choice1, choice2, choice3, choice4, answer) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, newIdQuestion);
			ps.setString(2, SubjectNameVersion);
			ps.setString(3, SubjectCodeVersion);
			ps.setString(4, question.getCourseName());
			ps.setString(5, question.getQuestionDescription());
			ps.setString(6, QuestionNumberToInsert);
			ps.setString(7, lecturer_userName);
			ps.setString(8, question.getOptionsText()[0]);
			ps.setString(9, question.getOptionsText()[1]);
			ps.setString(10, question.getOptionsText()[2]);
			ps.setString(11, question.getOptionsText()[3]);
			ps.setInt(12, question.getCorrectAnswer());
			ps.executeUpdate();
			// update in add this question to question_bank of the lecturer
			ps = conn.prepareStatement(
					"INSERT INTO cems.question_bank (lecturer_userName, questionID, subject, course) VALUES (?,?,?,?)");
			ps.setString(1, lecturer_userName);
			ps.setString(2, newIdQuestion);
			ps.setString(3, SubjectNameVersion);
			ps.setString(4, question.getCourseName());
			ps.executeUpdate();
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param msg its a Message<String> containing the userName
	 * @return returns a message<Boolean> saying if the operation was successfull
	 */
	public static Message setIsLoggedToZero(Message msg) {
		ArrayList<Boolean> db_respond = new ArrayList<>();

		ArrayList<String> username_arraylist = msg.convertCommandsToArrayList();
		final String username = username_arraylist.get(0);
		PreparedStatement ps;

		try {
			ps = mysqlConnection.conn.prepareStatement("UPDATE cems.users SET `isLogged` = ? WHERE `userName` = ?");
			ps.setBoolean(1, false);
			ps.setString(2, username);
			ps.executeUpdate();
			ps.close();

		} catch (SQLException e) {
			System.out.println("User : ' " + username + " ' was not found in the DB");
			db_respond.add(false);
			return new Message(CONSTANTS.receiveUserLogOutResponse, db_respond);
			// e.printStackTrace();
		}
		db_respond.add(true);
		return new Message(CONSTANTS.receiveUserLogOutResponse, db_respond);
	}

	/**
	 * Adds a new exam to the database.
	 *
	 * @param examToInsert The Exam object representing the new exam to be added.
	 * @return True if the exam is successfully added, false otherwise.
	 */
	public static boolean addNewExam(Exam examToInsert) {
		Subject subject = examToInsert.getSubject();
		Course course = examToInsert.getCourse();
		String ID = subject.getCode() + course.getCode(),
				// get the first examNumber that available in the data base
				examNumber = firstAvailableIndexExamTable(course.getName(), subject.getName()), questionsID = "",
				questionScores = "";
		ArrayList<Question> questionsList = examToInsert.getQuestions();
		ArrayList<String> questionScoresList = examToInsert.getQuestionsScores();

		ID = ID + examNumber; // build the ID if the exam
		System.out.println(ID);

		for (int i = 0; i < questionsList.size(); i++) // build question id and questions scores
		{
			questionsID = questionsID + questionsList.get(i).getID() + ",";
			questionScores = questionScores + questionScoresList.get(i) + ",";
		}
		questionsID = questionsID.substring(0, questionsID.length() - 1); // remove last comma ','
		questionScores = questionScores.substring(0, questionScores.length() - 1);
		// insert the exam to the DB
		try {
			// add to general exam_bank
			String sql = "INSERT INTO cems.exams (test_id, test_name, subject_code, subject_name, course_code, course_name, test_number, duration, description_for_lecturer, description_for_student, author_id, author_name, questions_id, points) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, ID);
			ps.setString(2, examToInsert.getTestName());
			ps.setString(3, subject.getCode());
			ps.setString(4, subject.getName());
			ps.setString(5, course.getCode());
			ps.setString(6, course.getName());
			ps.setString(7, examNumber);
			// Convert Duration to formatted time string
			Duration duration = examToInsert.getDuration();
			long totalSeconds = duration.getSeconds();
			long hours = totalSeconds / 3600;
			long minutes = (totalSeconds % 3600) / 60;
			long seconds = totalSeconds % 60;
			String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
			ps.setString(8, formattedTime);
			ps.setString(9, examToInsert.getTeacherDescription());
			ps.setString(10, examToInsert.getStudentDescription());
			ps.setString(11, examToInsert.getAuthorID());
			ps.setString(12, examToInsert.getAuthorName());
			ps.setString(13, questionsID);
			ps.setString(14, questionScores);
			// Execute the prepared statement
			ps.executeUpdate();
			// add exam_id to the personal bank of user
			sql = "INSERT INTO cems.exam_banks (lecturer_username, test_id, subject_name, course_name) VALUES (?, ?, ?, ?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, examToInsert.getLecturerUserName());
			ps.setString(2, ID);
			ps.setString(3, subject.getName());
			ps.setString(4, course.getName());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Retrieves the first available index for the exam table based on the given
	 * course name and subject name.
	 *
	 * @param courseName  The name of the course.
	 * @param subjectName The name of the subject.
	 * @return The first available index as a String, or null if an error occurs.
	 */
	// get all the number of exams that have subjectname and coursename and return
	// first available index among those exams
	private static String firstAvailableIndexExamTable(String courseName, String subjectName) {
		try {
			// save all number of exans for the given subject and course
			ArrayList<String> examNumbers = new ArrayList<>();
			PreparedStatement ps = conn.prepareStatement("SELECT test_number FROM cems.exams WHERE subject_name = ?");
			ps.setString(1, subjectName);
			// ps.setString(2, courseName);
			// get all exams with with the given subjet and given course
			ResultSet resultExamsNumbers = ps.executeQuery();
			while (resultExamsNumbers.next()) {
				examNumbers.add(resultExamsNumbers.getString("test_number"));
			}
			String examnNumberToInsert = "01";
			for (int i = 0; i < 100; i++) {
				if (examNumbers.isEmpty() || !examNumbers.contains(examnNumberToInsert)) {
					break;
				}
				int number = Integer.parseInt(examnNumberToInsert);
				number++; // Increment the number
				examnNumberToInsert = String.format("%02d", number); // Pad with leading zeros if necessary
			}
			return examnNumberToInsert;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves relevant conducted exams for a given username.
	 * 
	 * @param username the username for which to retrieve the relevant conducted
	 *                 exams
	 * @return a ConductExam object representing the relevant conducted exam, or
	 *         null if no relevant exams are found
	 * @throws SQLException if a database access error occurs
	 */
	public static ConductExam getRelevantConductExams(String username) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM conduct_exam ce " + "WHERE ce.conduct_exam_id IN ("
				+ "    SELECT ste.conduct_exam_id " + "    FROM students_taken_exams ste"
				+ "    WHERE ste.username = ? AND ste.isTaken = 0" + ") AND ce.is_open = 1;");
		ps.setString(1, username);
		ResultSet resultExamsSet = ps.executeQuery();
		String dur;
		if (resultExamsSet != null && resultExamsSet.next()) {
			dur = resultExamsSet.getString(7);
			// Parse the string into a LocalTime object
			LocalTime localTime = LocalTime.parse(dur);

			// Convert the LocalTime object to a Duration object
			Duration duration = Duration.ofHours(localTime.getHour()).plusMinutes(localTime.getMinute())
					.plusSeconds(localTime.getSecond());
			return new ConductExam(resultExamsSet.getInt(1), // conduct_exam_id in db
					resultExamsSet.getString(2), // password - 4 digits
					resultExamsSet.getString(3), // test_id 6 digits
					resultExamsSet.getInt(4), // is_open ternary flag 0 - closed, 1 - at conduct, 2 -finished
					resultExamsSet.getString(5), // conduct_exam_name varchar(100)
					resultExamsSet.getString(6), // lecturer_user_name varchar(45)
					duration // duration of exam
			);
		}
		return null;
	}

	/**
	 * Fetches the subjects and courses taught by a specific teacher.
	 *
	 * @param userName The username of the teacher.
	 * @return An ArrayList of Subject objects representing the subjects taught by
	 *         the teacher, including their associated courses. Returns null if an
	 *         error occurs.
	 */
	public static ArrayList<Subject> fetchTeacherSubjectAndCourses(String userName) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		// create empty arrayList of subject to save all subject the
		// lectrurer is teaching
		ArrayList<Subject> subjects = new ArrayList<>();
		try {
			String teacherUserName = userName; // Replace with the actual teacher user name.

			// Prepare the SQL query
			// first we get all subject this lecturer is teaching
			String query = "SELECT DISTINCT subject_id, subject_name FROM teacher WHERE TeacherUserName = ?";

			// Create a PreparedStatement with the query
			statement = conn.prepareStatement(query);
			statement.setString(1, teacherUserName);

			// Execute the query
			resultSet = statement.executeQuery();

			// Process the result set
			while (resultSet.next()) {
				String subjectId = resultSet.getString("subject_id");
				String subjectName = resultSet.getString("subject_name");
				subjects.add(new Subject(subjectName, subjectId, null));
			}
			// Prepare the SQL query
			// get all courses data for specific subject that the lecturer is teaching
			query = "SELECT course_id, course_name FROM teacher WHERE TeacherUserName = ? AND subject_name = ? AND subject_id = ?";

			// Create a PreparedStatement with the query
			statement = conn.prepareStatement(query);
			ArrayList<Course> coursesOfSubject = null;
			for (Subject subject : subjects) // go over all the subjects
			{
				coursesOfSubject = new ArrayList<>(); // create empty arrayList of Course
				statement.setString(1, teacherUserName);
				statement.setString(2, subject.getName());
				statement.setString(3, subject.getCode());

				// Execute the query
				resultSet = statement.executeQuery();
				while (resultSet.next()) // get all courses of this subject
				{
					String courseId = resultSet.getString("course_id");
					String courseName = resultSet.getString("course_name");
					coursesOfSubject.add(new Course(courseName, courseId));
				}
				subject.setCourses(coursesOfSubject); // set the list of courses to be the courses for this subject
			}
			return subjects; // return list of subject each subject have the list of relevant courses
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves questions for a specific subject.
	 *
	 * @param subject The Subject object representing the subject for which to
	 *                retrieve the questions.
	 * @return An ArrayList of Question objects representing the questions for the
	 *         subject. Returns null if an error occurs.
	 */
	public static ArrayList<Question> getQuestionBySubject(Subject subject) {
		// Create the SQL query
		String query = "SELECT * FROM cems.question WHERE subject = ?";
		ArrayList<Question> questions = new ArrayList<Question>();

		try {
			// Prepare the statement
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, subject.getName());
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				// build the array of answer in size of 4
				String[] answers = { resultSet.getString(8), resultSet.getString(9), resultSet.getString(10),
						resultSet.getString(11) };
				// gather all data of question
				String ID = resultSet.getString(1), subjectName = resultSet.getString(2),
						subjectCode = resultSet.getString(3), course_name = resultSet.getString(4),
						question_text = resultSet.getString(5), question_number = resultSet.getString(6),
						lecturer = resultSet.getString(7);
				int correctAnswer = resultSet.getInt(12);
				questions.add(new Question(ID, question_text, subjectName, subjectCode, course_name, question_number,
						lecturer, null, answers, correctAnswer)); // insert into list
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return questions;
	}

	/**
	 * Retrieves questions from the question bank for a specific subject and
	 * lecturer.
	 *
	 * @param userName The username of the lecturer.
	 * @param subject  The Subject object representing the subject for which to
	 *                 retrieve the questions.
	 * @return An ArrayList of Question objects representing the questions from the
	 *         question bank. Returns null if an error occurs.
	 */
	public static ArrayList<Question> getQuestionsFromQuestionBankBySubject(String userName, Subject subject) {
		ArrayList<String> relevanQuestionsID = new ArrayList<>(); // save all relevant questionID of the lecturer
		ArrayList<Question> questionsList = new ArrayList<>(); // the list of the actual questions we want to save
		try {
			// get all questions ID of by subject and by lectuerer bankQuestion
			String sql = "SELECT questionID FROM cems.question_bank WHERE lecturer_userName = ? AND subject = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userName);
			statement.setString(2, subject.getName());
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				relevanQuestionsID.add(resultSet.getString("questionID")); // add all questionsID to the list

			}
			sql = "SELECT * FROM cems.question WHERE subject = ?"; // get all questions relevat to the given subject
			statement = conn.prepareStatement(sql);
			statement.setString(1, subject.getName());
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				// get the question id of current row
				String questionIDinDB = resultSet.getString("id");
				// if the quesionId in the DB exist in our
				// relevantquestionsId WE SAVE THIS QUESTION
				if (relevanQuestionsID.contains(questionIDinDB)) {
					// build the array of answer in size of 4
					String[] answers = { resultSet.getString(8), resultSet.getString(9), resultSet.getString(10),
							resultSet.getString(11) };
					// gather all data of question
					String ID = resultSet.getString(1), subjectName = resultSet.getString(2),
							subjectCode = resultSet.getString(3), course_name = resultSet.getString(4),
							question_text = resultSet.getString(5), question_number = resultSet.getString(6),
							lecturer = resultSet.getString(7);
					int correctAnswer = resultSet.getInt(12);
					questionsList.add(new Question(ID, question_text, subjectName, subjectCode, course_name,
							question_number, lecturer, null, answers, correctAnswer)); // insert into list
				}
			}
			return questionsList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves subjects from the database.
	 *
	 * @return A Message object containing the response and the ArrayList of Subject
	 *         objects representing the subjects. Returns null if an error occurs.
	 */
	public static Message GetSubjectsFromDB() {
		ArrayList<Subject> subjects = new ArrayList<Subject>();
		try {
			// Create a PreparedStatement object to execute a precompiled SQL statement
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM cems.subjects");
			ResultSet resultSubjectsSet = ps.executeQuery();
			while (resultSubjectsSet.next()) {
				// Add a new Subject object to the questions ArrayList for each row in the
				// result set
				String subID = new String(resultSubjectsSet.getString("subject_id"));
				String subName = new String(resultSubjectsSet.getString("subject_name"));
				Course c = new Course(resultSubjectsSet.getString("course_name"),
						resultSubjectsSet.getString("course_id"));
				// ArrayList<Course> courseArr = new ArrayList<Course>((new
				// Course(resultSubjectsSet.getString("course_name"),resultSubjectsSet.getString("course_id"))
				// ;
				ArrayList<Course> cArr = new ArrayList<Course>();
				cArr.add(c);

				subjects.add(new Subject(subName, subID, cArr));

			}
		} catch (Exception e) {
			// error message if the questions cannot be retrieved
			System.out.println("Importing subjects from cems.subjects is unsuccesful!");
			return null;
		}
		return new Message(CONSTANTS.ResponseToGetSubjectsFromDB, subjects);
	}

	/**
	 * Checks if the provided password matches and retrieves the conducted exam
	 * information for the given username and conductExamID.
	 *
	 * @param username      the username for which to check the password and
	 *                      retrieve the conducted exam
	 * @param password      the password to check against the conducted exam
	 *                      password
	 * @param conductExamID the ID of the conducted exam to retrieve
	 * @return a ConductExam object representing the conducted exam if the password
	 *         matches and the exam is open and not taken, or null if no matching
	 *         exam is found
	 * @throws SQLException if a database access error occurs
	 */
	public static ConductExam getCheckIfPasswordMatchesAndTheTestFile(String username, String password,
			int conductExamID) throws SQLException {

		PreparedStatement ps = conn.prepareStatement("SELECT ce.* FROM conduct_exam ce "
				+ "LEFT JOIN students_taken_exams ste ON ce.conduct_exam_"
				+ "id = ste.conduct_exam_id AND ce.conduct_exam_id = ? " + "WHERE ce.password = ? AND ce.is_open = 1 "
				+ "AND (ste.isTaken IS NULL OR ste.isTaken = 0) " + "AND ste.username = ?;");

		ps.setInt(1, conductExamID);
		ps.setString(2, password);
		ps.setString(3, username);
		ResultSet resultConductTest = ps.executeQuery();
		String dur;
		if (resultConductTest != null && resultConductTest.next()) {
			dur = resultConductTest.getString(7);
			// Parse the string into a LocalTime object
			LocalTime localTime = LocalTime.parse(dur);

			// Convert the LocalTime object to a Duration object
			Duration duration = Duration.ofHours(localTime.getHour()).plusMinutes(localTime.getMinute())
					.plusSeconds(localTime.getSecond());
			return new ConductExam(resultConductTest.getInt(1), // conduct_exam_id
					resultConductTest.getString(2), // password
					resultConductTest.getString(3), // test_id
					resultConductTest.getInt(4), // is_open
					resultConductTest.getString(5), // conduct_exam_name
					resultConductTest.getString(6), // lecturer_user_name
					duration // get duration
			);

		}
		return null;
	}

	/**
	 * Changes the personal bank of a lecturer for a specific subject.
	 *
	 * @param lecturerUserName The username of the lecturer.
	 * @param newPersonalBank  The ArrayList of Question objects representing the
	 *                         new personal bank.
	 * @param subject          The Subject object representing the subject.
	 * @return True if the personal bank is successfully changed, false otherwise.
	 */
	public static boolean changePersonalBank(String lecturerUserName, ArrayList<Question> newPersonalBank,
			Subject subject) {
		String subjectName = subject.getName(); // save the subject of the questions
		try {
			// delete all questions of subject in the quesion bank of lecturerUserName
			String query = "DELETE FROM cems.question_bank WHERE lecturer_userName = ? AND subject = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, lecturerUserName);
			statement.setString(2, subjectName);
			statement.executeUpdate();
			// add the newPresonalBank (only question that related to the given subject)
			query = "INSERT INTO cems.question_bank (lecturer_userName, questionID, subject, course) VALUES (?, ?, ?, ?)";
			statement = conn.prepareStatement(query);

			// Assuming you have a List of rows to insert, where each row is represented as
			// an object

			for (Question question : newPersonalBank) {
				// Set the parameter values for each row
				statement.setString(1, lecturerUserName);
				statement.setString(2, question.getID());
				statement.setString(3, subjectName);
				statement.setString(4, question.getCourseName());

				// Add the statement to the batch
				statement.addBatch();
			}
			statement.executeBatch();// execute all updates
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Sets the isTaken flag to 1 for the student's exam in the students_taken_exams
	 * table, based on the provided conduct_exam_id and username.
	 *
	 * @param conduct_exam_id the ID of the conducted exam for which to set the
	 *                        isTaken flag to 1
	 * @param username        the username of the student for whom to set the
	 *                        isTaken flag to 1
	 * @throws SQLException if a database access error occurs
	 */
	public static void setStudenIsTakenExamForCurrectConductTestToOne(int conduct_exam_id, String username)
			throws SQLException {
		PreparedStatement ps = conn.prepareStatement(
				"UPDATE students_taken_exams " + "SET isTaken = 1 " + "WHERE username = ? AND conduct_exam_id = ?;");
		ps.setString(1, username);
		ps.setInt(2, conduct_exam_id);
		ps.executeUpdate();
	}

	/**
	 * Retrieves the information of a conducted exam.
	 *
	 * @param data The ConductExam object containing the test ID and conduct exam
	 *             ID.
	 * @return An Exam object representing the conducted exam, or null if no exam is
	 *         found.
	 * @throws SQLException If a database access error occurs.
	 */
	public static Exam getTestInformationAtConduct(ConductExam data) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("SELECT ce.*, ex.* " + "FROM conduct_exam ce "
				+ "JOIN exams ex ON ? = ex.test_id " + "WHERE ce.conduct_exam_id = ?;");
		ps.setString(1, data.getTest_id());
		ps.setInt(2, data.getConduct_exam_id());

		ResultSet resultSet = ps.executeQuery();

		if (resultSet != null && resultSet.next()) {
			String testID = resultSet.getString("test_id");
			String testName = resultSet.getString("test_name");
			String subjectCode = resultSet.getString("subject_code");
			String subjectName = resultSet.getString("subject_name");
			Subject subject = new Subject(subjectName, subjectCode, null); // don't need list of courses now
			String courseCode = resultSet.getString("course_code");
			String courseName = resultSet.getString("course_name");
			Course course = new Course(courseName, courseCode);
			String testNumber = resultSet.getString("test_number");
			String durationString = resultSet.getString("duration").trim();

			// Split the duration string into hours, minutes, and seconds components
			String[] components = durationString.split(":");
			int hours = Integer.parseInt(components[0]);
			int minutes = Integer.parseInt(components[1]);
			int seconds = Integer.parseInt(components[2]);

			// Calculate the total duration in seconds
			long totalSeconds = (hours * 3600) + (minutes * 60) + seconds;

			// Create the Duration object
			Duration duration = Duration.ofSeconds(totalSeconds);
			String teacherDescription = resultSet.getString("description_for_lecturer");
			String studentDescription = resultSet.getString("description_for_student");
			String authorName = resultSet.getString("author_name");
			String authorID = resultSet.getString("author_id");

			// Create the Subject and Course objects based on retrieved data if needed

			// Create the Subject and Course objects based on retrieved data if needed

			ArrayList<Question> questions = new ArrayList<>();
			ArrayList<String> questionIDs = new ArrayList<>(
					Arrays.asList(resultSet.getString("questions_id").split(",")));
			ArrayList<String> questionsScores = new ArrayList<>(
					Arrays.asList(resultSet.getString("points").split(",")));

			for (String questionID : questionIDs) {
				Question question = getQuestionFromDB(questionID);
				if (question != null) {
					questions.add(question);
				}
			}

			Exam exam = new Exam(testID, testName, subject, course, testNumber, duration, teacherDescription,
					studentDescription, authorName, authorID, questions, questionsScores);

			return exam;
		}
		return null;
	}

	/**
	 * Retrieves all exams for a given subject.
	 *
	 * @param subject The Subject object representing the subject.
	 * @return An ArrayList of Exam objects representing the exams for the subject,
	 *         or null if an error occurs.
	 */
	public static ArrayList<Exam> getExamBySubject(Subject subject)
	{
		ArrayList<Exam> exams = new ArrayList<>();
		System.out.println(subject.getName());
		if(!(subject.getName().equals("All Subjects"))) {
			try
			{
			
				PreparedStatement ps = conn.prepareStatement("SELECT * FROM cems.exams WHERE subject_name = ?");
			    ps.setString(1, subject.getName()); // Set the subject name as the parameter in the prepared statement
			    ResultSet resultSet = ps.executeQuery();
			    while (resultSet.next()) 
			    {
			        Exam exam = createExamHelper(resultSet); // this method is the resultSet and create the exam (Refactored for reuse)
			        exams.add(exam); // Add the exam object to the list
			    }
			    resultSet.close();
			    return exams;
			} catch (SQLException e) 
			{
			   e.printStackTrace();
			   return null;
			}
		}
		else {
			try
			{
				
				PreparedStatement ps = conn.prepareStatement("SELECT * FROM cems.exams");
			    ResultSet resultSet = ps.executeQuery();
			    while (resultSet.next()) 
			    {
			        Exam exam = createExamHelper(resultSet); // this method is the resultSet and create the exam (Refactored for reuse)
			        exams.add(exam); // Add the exam object to the list
			    }
			    resultSet.close();
			    return exams;
			} catch (SQLException e) 
			{
			   e.printStackTrace();
			   return null;
			}
		}
			
	}	

	/**
	 * Retrieves all exams from the personal exam bank of a given user.
	 *
	 * @param userName The username of the lecturer.
	 * @return An ArrayList of Exam objects representing the exams in the personal
	 *         exam bank, or null if an error occurs.
	 */
	public static ArrayList<Exam> getPersonalExamBank(String UserName) {
		ArrayList<Exam> exams = new ArrayList<>();

		try {
			PreparedStatement ps = conn
					.prepareStatement("SELECT test_id FROM cems.exam_banks WHERE lecturer_username = ?");
			ps.setString(1, UserName); // Set the Usse name as the parameter in the prepared statement
			ResultSet resultSet = ps.executeQuery(); // get all the test_id that belong to the personal bank of this
			// user name
			while (resultSet.next()) {
				// create prepare statment to retrive exam according to the test id
				PreparedStatement psExamData = conn.prepareStatement("SELECT * FROM cems.exams WHERE test_id = ?");

				// get the test id i want to retrieve his data
				String testIdToRetrieve = resultSet.getString("test_id");

				// set in the prepare statement
				psExamData.setString(1, testIdToRetrieve);
				ResultSet resultExamData = psExamData.executeQuery();
				// move to the first row
				resultExamData.next();
				// this method use the resultSet and create the exam (Refactored for reuse)
				Exam exam = createExamHelper(resultExamData);
				exams.add(exam); // Add the exam object to the list
			}
			resultSet.close();
			return exams;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Helper method to create an Exam object using the data from a ResultSet.
	 *
	 * @param resultSet The ResultSet object containing the data from the Exam
	 *                  table.
	 * @return An Exam object created using the retrieved data.
	 * @throws SQLException If an error occurs while retrieving data from the
	 *                      ResultSet.
	 */
	// this function get ResultSet of Exam table and create exam using the resultSet
	private static Exam createExamHelper(ResultSet resultSet) throws SQLException {
		String testId = resultSet.getString("test_id");
		String testName = resultSet.getString("test_name");
		String subjectCode = resultSet.getString("subject_code");
		String subjectName = resultSet.getString("subject_name");
		String courseCode = resultSet.getString("course_code");
		String courseName = resultSet.getString("course_name");
		String testNumber = resultSet.getString("test_number");
		String durationString = resultSet.getString("duration").trim();
		String lecturerDescription = resultSet.getString("description_for_lecturer");
		String studentDescription = resultSet.getString("description_for_student");
		String authorId = resultSet.getString("author_id");
		String authorName = resultSet.getString("author_name");
		String questionsId = resultSet.getString("questions_id");
		String points = resultSet.getString("points");
		// create subject and course
		Subject examSubject = new Subject(subjectName, subjectCode, null);
		Course examCourse = new Course(courseName, courseCode);

		// create duration

		// Split the duration string into hours, minutes, and seconds components
		Duration duration = convertDurationStringToDurationTime(durationString);

		// create list of questions
		ArrayList<Question> questions = getQuestionByID(questionsId.split(",")); // the functin get array of string

		// question id and return list of
		// the questions from the DB

		// create list of points
		String[] ArrayPoints = points.split(",");
		ArrayList<String> listOfPoints = new ArrayList<>(Arrays.asList(ArrayPoints));
		// Create an Exam object with the retrieved data

		Exam exam = new Exam(testId, testName, examSubject, examCourse, testNumber, duration, lecturerDescription,
				studentDescription, authorName, authorId, questions, listOfPoints);
		return exam;
	}

	/**
	 * Helper method to convert a duration string in the format "HH:MM:SS" to a
	 * Duration object.
	 *
	 * @param durationString The duration string in the format "HH:MM:SS".
	 * @return A Duration object representing the duration.
	 */
	private static Duration convertDurationStringToDurationTime(String durationString) {
		String[] components = durationString.split(":");
		int hours = Integer.parseInt(components[0]);
		int minutes = Integer.parseInt(components[1]);
		int seconds = Integer.parseInt(components[2]);
		// Calculate the total duration in seconds
		long totalSeconds = (hours * 3600) + (minutes * 60) + seconds;
		Duration duration = Duration.ofSeconds(totalSeconds);
		return duration;
	}

	/**
	 * Retrieves a question from the database based on the question ID.
	 *
	 * @param questionID The ID of the question to retrieve.
	 * @return The Question object representing the retrieved question, or null if
	 *         the question does not exist.
	 * @throws SQLException If an SQL exception occurs while retrieving the
	 *                      question.
	 */
	public static Question getQuestionFromDB(String questionID) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM question WHERE id = ?");
		ps.setString(1, questionID);
		ResultSet resultSet = ps.executeQuery();

		if (resultSet != null && resultSet.next()) {
			String iD = resultSet.getString("id");
			String questionDescription = resultSet.getString("question_text");
			String subject = resultSet.getString("subject");
			String subjectCode = resultSet.getString("subject_code");
			String coursName = resultSet.getString("course_name");
			String questionNumber = resultSet.getString("question_number");
			String authorName = resultSet.getString("lecturer");
			String authorID = ""; // Set the appropriate column name for author ID in your database
			String[] optionsText = new String[] { resultSet.getString("choice1"), resultSet.getString("choice2"),
					resultSet.getString("choice3"), resultSet.getString("choice4") };
			int correctAnswer = resultSet.getInt("answer");

			return new Question(iD, questionDescription, subject, subjectCode, coursName, questionNumber, authorName,
					authorID, optionsText, correctAnswer);
		}
		return null;
	}

	/**
	 * Method to retrieve a list of all the questions from the "cems.question" table
	 * 
	 * @return an ArrayList of Exams objects
	 */

	public static Message getExamTableFromDB() throws SQLException {
		ArrayList<Exam> exams = new ArrayList<Exam>();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM exams");
		ResultSet resultSet = ps.executeQuery();
		try {
			while (resultSet.next()) {
				Exam exam = createExamHelper(resultSet); // this method is the resultSet and create the exam (Refactored
															// for reuse)
				exams.add(exam); // Add the exam object to the list
			}
		} catch (Exception e) {
			// error message if the questions cannot be retrieved
			System.out.println("Importing exams from cems.question is unsuccesful!");
			return null;
		}
		return new Message(CONSTANTS.responseGetExamsFromDB, exams);

	}

	/**
	 * Retrieves multiple questions from the database based on an array of question
	 * IDs.
	 *
	 * @param questionsID An array of question IDs.
	 * @return An ArrayList containing the Question objects representing the
	 *         retrieved questions, or null if an SQL query error occurs.
	 */
	private static ArrayList<Question> getQuestionByID(String[] questionsID) {
		ArrayList<Question> questionsToReturn = new ArrayList<>();
		for (String question_id : questionsID) {
			try {
				Question questionFullData = getQuestionFromDB(question_id);
				questionsToReturn.add(questionFullData); // add the question to the list of questions to return
			} catch (SQLException e) {
				e.printStackTrace();
				questionsToReturn = null; // if sql query when wrong questionsToReturn null
				break;
			}
		}
		return questionsToReturn;
	}

	/**
	 * Updates the personal exam bank of a lecturer by deleting existing exams of a
	 * specific subject and course and inserting new exams.
	 *
	 * @param userName             The username of the lecturer.
	 * @param changeOfPersonalBank An ArrayList containing the new Exam objects to
	 *                             be added to the personal exam bank.
	 * @param subject              The Subject object representing the subject of
	 *                             the exams.
	 * @param course               The Course object representing the course of the
	 *                             exams.
	 * @return True if the update operation is successful, false otherwise.
	 */
	public static boolean changePersonalExamBank(String userName, ArrayList<Exam> changeOfPersonalBank, Subject subject,
			Course course) {
		String subjectName = subject.getName(), courseName = course.getName();
		try {
			// delete all exams of subjectName in and courseName from the personal exam bank
			// of lecturerUserName
			String query = "DELETE FROM cems.exam_banks WHERE lecturer_username = ? AND subject_name = ? AND  course_name = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, userName);
			statement.setString(2, subjectName);
			statement.setString(3, courseName);
			statement.executeUpdate();
			// add the newPresonalBank (only question that related to the given subject)
			query = "INSERT INTO cems.exam_banks (lecturer_username, test_id, subject_name, course_name) VALUES (?, ?, ?, ?)";
			statement = conn.prepareStatement(query);

			// Assuming you have a List of rows to insert, where each row is represented as
			// an object

			for (Exam exam : changeOfPersonalBank) {
				// Set the parameter values for each row
				statement.setString(1, userName);
				statement.setString(2, exam.getTestID());
				statement.setString(3, subjectName);
				statement.setString(4, courseName);

				// Add the statement to the batch
				statement.addBatch();
			}
			statement.executeBatch();// execute all updates
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Updates the personal exam bank of a lecturer by deleting existing exams of a
	 * specific subject and course and inserting new exams.
	 *
	 * @param userName             The username of the lecturer.
	 * @param changeOfPersonalBank An ArrayList containing the new Exam objects to
	 *                             be added to the personal exam bank.
	 * @param subject              The Subject object representing the subject of
	 *                             the exams.
	 * @param course               The Course object representing the course of the
	 *                             exams.
	 * @return True if the update operation is successful, false otherwise.
	 */
	public static Message uploadWordFileToDataBase(ManualExamConductObject manual_exam) {
		String sql = "INSERT INTO cems.manual_test_results (student_id,condut_exam_id,word_file_solution) VALUES (?,?,?)";
		FileInputStream fis = null;
		try {
			PreparedStatement statement = conn.prepareStatement(sql);

			statement.setString(1, manual_exam.getStudentId());
			statement.setInt(2, manual_exam.getConductExamId());

			if (manual_exam.getWordFileSolution() == null) {
				statement.setNull(3, java.sql.Types.BLOB);
			} else {
				// Read the Word file as a binary stream
				fis = new FileInputStream(manual_exam.getWordFileSolution());
				statement.setBinaryStream(3, fis, (int) manual_exam.getWordFileSolution().length());
			}

			// Execute the query
			int rowsAffected = statement.executeUpdate();

			// Check if the query was successful
			if (rowsAffected > 0) {
				System.out.println("Word file uploaded successfully!");
			} else {
				System.out.println("Failed to upload the word file.");
				return new Message(CONSTANTS.responseForUploadingWordFile, false);
			}

			if (fis != null) {
				fis.close();
			}

			statement.close();

		} catch (SQLException | IOException e) {
			e.printStackTrace();
			return new Message(CONSTANTS.responseForUploadingWordFile, false);
		}
		return new Message(CONSTANTS.responseForUploadingWordFile, true);
	}

	/**
	 * Updates the personal exam bank of a lecturer by deleting existing exams of a
	 * specific subject and course and inserting new exams.
	 *
	 * @param userName             The username of the lecturer.
	 * @param changeOfPersonalBank An ArrayList containing the new Exam objects to
	 *                             be added to the personal exam bank.
	 * @param subject              The Subject object representing the subject of
	 *                             the exams.
	 * @param course               The Course object representing the course of the
	 *                             exams.
	 * @return True if the update operation is successful, false otherwise.
	 */
	public static List<String> updateExamTime(List<String> data) throws SQLException {
		boolean flag = true;
		String existingDurationString = "";
		PreparedStatement ps = conn.prepareStatement(
				"UPDATE cems.conduct_exam SET duration = ? ,request_status = ? WHERE conduct_exam_id = ?");

		PreparedStatement ps2 = conn.prepareStatement("SELECT duration FROM conduct_exam WHERE conduct_exam_id = ?");
		ps2.setInt(1, Integer.parseInt(data.get(0)));
		ResultSet durationSet = ps2.executeQuery();
		if (durationSet != null && durationSet.next()) {
			existingDurationString = durationSet.getString("duration");
		}
		PreparedStatement updateTimeExtensionRequestStatusTable = conn.prepareStatement(
				"" + "UPDATE cems.time_extension_requests SET status = ? WHERE " + "conduct_exam_id = ?");
		updateTimeExtensionRequestStatusTable.setString(1, CONSTANTS.Approved);
		updateTimeExtensionRequestStatusTable.setInt(2, Integer.parseInt(data.get(0)));

		String durationString = durationSet.getString("duration").trim();
		String[] components = durationString.split(":");
		int hours = Integer.parseInt(components[0]);
		int minutes = Integer.parseInt(components[1]);
		int seconds = Integer.parseInt(components[2]);

		// Calculate the total duration in seconds
		long totalSeconds = (hours * 3600) + (minutes * 60) + seconds;
		// add minutes
		String[] extensionComponents = data.get(1).split(":");
		long extHours = Long.parseLong(extensionComponents[0]) * 3600;
		long extMinutes = Long.parseLong(extensionComponents[0]) * 60;
		long extSeconds = Long.parseLong(extensionComponents[0]);
		totalSeconds += extHours + extMinutes + extSeconds;

		// Create duration object
		Duration newDuration = Duration.ofSeconds(totalSeconds);
		// Calculate the updated hours, minutes, and seconds
		int updatedHours = (int) (totalSeconds / 3600);
		int updatedMinutes = (int) ((totalSeconds % 3600) / 60);
		int updatedSeconds = (int) (totalSeconds % 60);

		String formattedDuration = String.format("%02d:%02d:%02d", updatedHours, updatedMinutes, updatedSeconds);

		ps.setString(1, formattedDuration);
		ps.setString(2, CONSTANTS.Approved); // add status request Approved (if head department approved)
		ps.setInt(3, Integer.parseInt(data.get(0)));

		if (ps.executeUpdate() <= 0)
			flag = false;
		if (updateTimeExtensionRequestStatusTable.executeUpdate() <= 0)
			flag = false;
		return null;
	}

	/**
	 * Uploads a Word file solution to the database for a manual exam.
	 *
	 * @param manual_exam The ManualExamConductObject containing the student ID,
	 *                    conduct exam ID, and Word file solution.
	 * @return A Message object indicating the success or failure of the upload
	 *         operation.
	 */
	public static Message checkForTimeChangeInDataBase(ExamTimeChangeObject exam_time_object) {
		String sql = "SELECT duration from cems.conduct_exam where conduct_exam_id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, exam_time_object.getConductExamId());
			ResultSet resultSet = ps.executeQuery();

			if (resultSet == null)
				throw new SQLException("Exam Not Found!");

			if (resultSet.next()) {
				String durationString = resultSet.getString("duration");
				String[] timeParts = durationString.split(":");
				int hours = Integer.parseInt(timeParts[0]);
				int minutes = Integer.parseInt(timeParts[1]);
				int seconds = Integer.parseInt(timeParts[2]);
				int time_in_seconds = hours * 3600 + minutes * 60 + seconds;
				if (exam_time_object.getInitTime() < time_in_seconds) {
					return new Message(CONSTANTS.resonseForCheckIfExamTimeChange, new TimeDifferenceObject(
							time_in_seconds - exam_time_object.getInitTime(), exam_time_object.getExam_type()));
				}
				return new Message(CONSTANTS.resonseForCheckIfExamTimeChange,
						new TimeDifferenceObject(0, exam_time_object.getExam_type()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("\n\nFailed to Check Time!");
		return null;
	}

	/**
	 * Retrieves a list of users with a specific role from the database.
	 *
	 * @param userRole The role of the users to be retrieved.
	 * @return An ArrayList of User objects representing the users with the
	 *         specified role. Returns null if an error occurs.
	 */
	public static ArrayList<User> getUsersByRole(String UserRole) {
		ArrayList<User> students = new ArrayList<>();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM cems.users WHERE role = ?");
			ps.setString(1, UserRole);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				String ID = resultSet.getString("ID"), userName = resultSet.getString("userName"),
						firstName = resultSet.getString("firstName"), lastName = resultSet.getString("lastName"),
						password = resultSet.getString("password"), email = resultSet.getString("email"),
						role = resultSet.getString("role");
				int isLogged = resultSet.getInt("isLogged");
				User student = new User(userName, lastName, firstName, password, ID, email, null, isLogged == 1, role);
				students.add(student);
			}
			return students;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Checks if a password is available for conducting an exam.
	 *
	 * @param password The password to be checked.
	 * @return true if the password is available for conducting an exam, false
	 *         otherwise or if an error occurs.
	 */
	public static boolean checkAvailablePasswordForConductExam(String password) {
		try {
			boolean isAvailable = true;
			PreparedStatement ps = conn.prepareStatement("SELECT password FROM cems.conduct_exam WHERE is_open = ?");
			ps.setInt(1, 1);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				if (resultSet.getString("password").equals(password)) {
					isAvailable = false;
					break;
				}
			}
			return isAvailable;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Adds a start exam to the conduct exam table in the database.
	 *
	 * @param examToConduct The ConductExam object representing the exam to be
	 *                      conducted.
	 * @return true if the start exam is successfully added, false otherwise or if
	 *         an error occurs.
	 */
	public static boolean addStartExamToConduct(ConductExam examToConduct) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT conduct_exam_id FROM cems.conduct_exam"); // get all
																											// conduct_exam_id

			ResultSet resultSet = ps.executeQuery();
			int conduct_exam_id = getFirstAvailableCondictExamID(resultSet); // get first available id for the conduct
																				// exam
			// add the conduct Exam data to DB
			String password = examToConduct.getPassword();
			String test_id = examToConduct.getTest_id();
			int is_open = 1;
			String conduct_exam_name = examToConduct.getConduct_exam_name();
			String lecturer_user_name = examToConduct.getLecturer_user_name();
			String duration = examToConduct.getDurationTimeFormat();
			String time_extension_request_status = examToConduct.getHasRequest();
			String extensionTime = examToConduct.getTimeExtensionStringFormat();

			ps = conn.prepareStatement(
					"INSERT INTO cems.conduct_exam (conduct_exam_id, password, test_id, is_open, conduct_exam_name, lecturer_user_name, duration) VALUES (?, ?, ?, ?, ?, ?, ?)");
			ps.setInt(1, conduct_exam_id);
			ps.setString(2, password);
			ps.setString(3, test_id);
			ps.setInt(4, is_open);
			ps.setString(5, conduct_exam_name);
			ps.setString(6, lecturer_user_name);
			ps.setString(7, duration);
			ps.execute();

			// add all the registered students (to the exam) to DB
			ArrayList<User> registeredStudents = examToConduct.getRegisteredStudents();
			ps = conn.prepareStatement(
					"INSERT INTO cems.students_taken_exams (conduct_exam_id, username, isTaken) VALUES (?, ?, ?)");

			for (User student : registeredStudents) {
				ps.setInt(1, conduct_exam_id);
				ps.setString(2, student.getUserName());
				ps.setInt(3, 0);
				ps.addBatch(); // Add the current student to the batch
			}
			ps.executeBatch();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Retrieves the first available conduct exam ID from the given ResultSet.
	 *
	 * @param resultSet The ResultSet containing the conduct exam IDs.
	 * @return The first available conduct exam ID.
	 * @throws SQLException If an error occurs while accessing the ResultSet.
	 */
	private static int getFirstAvailableCondictExamID(ResultSet resultSet) throws SQLException {
		int conduct_exam_id = 1;
		Set<Integer> usedIds = new HashSet<>();
		while (resultSet.next()) {
			int usedId = resultSet.getInt("conduct_exam_id");
			usedIds.add(usedId);
		}

		// Find the first available index
		while (usedIds.contains(conduct_exam_id)) {
			conduct_exam_id++;
		}
		return conduct_exam_id;
	}

	/**
	 * Retrieves a list of conduct exams for a specific subject and lecturer.
	 *
	 * @param subject          The subject for which conduct exams are retrieved.
	 * @param lecturerUserName The username of the lecturer.
	 * @return The list of conduct exams for the subject and lecturer.
	 */
	public static ArrayList<ConductExam> getConductExamsBySubjectByLecturer(Subject subject, String lecturerUserName) {
		String subjectCode = subject.getCode();
		ArrayList<ConductExam> conductExams = new ArrayList<>();
		try {
			String query = "SELECT * FROM cems.conduct_exam WHERE test_id LIKE ? AND lecturer_user_name = ? AND is_open = 1";
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, subjectCode + "%"); // get only conduct_exam that have testid of the given subject
			ps.setString(2, lecturerUserName);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				int conduct_exam_id = resultSet.getInt("conduct_exam_id");
				String password = resultSet.getString("password");
				String test_id = resultSet.getString("test_id");
				int is_open = resultSet.getInt("is_open");
				String conduct_exam_name = resultSet.getString("conduct_exam_name");
				String lecturer_user_name = resultSet.getString("lecturer_user_name");
				Duration duration = convertDurationStringToDurationTime(resultSet.getString("duration"));
				String time_extension_request_status = resultSet.getString("time_extension_request_status");
				String requestStatus = resultSet.getString("request_status");
				Duration timeExtension = convertDurationStringToDurationTime(resultSet.getString("time_extension"));
				ConductExam conductExamToAdd = new ConductExam(conduct_exam_id, password, test_id, is_open,
						conduct_exam_name, lecturer_user_name, duration);
				conductExamToAdd.setHasRequest(time_extension_request_status); // set the time request extension status
																				// we fetch from db
				conductExamToAdd.setRequestStatus(requestStatus);
				conductExamToAdd.setTimeExtension(timeExtension); // get the time extension (in case request have been
																	// approved)
				conductExams.add(conductExamToAdd);
			}
			// get all the exam for each conduct Exam element in the list conductExams and
			// all the registered students
			ArrayList<Exam> ExamsBySubject = getExamBySubject(subject); // get all the exams in the personal bank of the
																		// lecturer using the lecturer user name
			for (ConductExam conductExam : conductExams) // the test_id that in conductExam are certainly exist in
															// ExamsBySubject (list of existing exams by given subject)
			{
				Exam fakeExam = new Exam(conductExam.getTest_id(), null, null, null, null, null, null, null, null, null,
						null, null); // fake exam to get the real exam
				int indexOfRealExam = ExamsBySubject.indexOf(fakeExam); // get the index of the exam we want to add
				Exam realExamToAdd = ExamsBySubject.get(indexOfRealExam); // get the real exam from the genear Exam list
																			// by given subject
				conductExam.setExamToConduct(realExamToAdd); // add to the conductExam

				// get all list of students that registered to the conduct exams
				conductExam.setRegisteredStudents(getStudentUserByConductExamID(conductExam.getConduct_exam_id()));
			}

			return conductExams;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the grades of a user by their username.
	 *
	 * @param username The username of the user.
	 * @return The list of grades for the user.
	 */
	public static ArrayList<Grade> getGradesByUsername(String username) {
		ArrayList<Grade> grades = new ArrayList<Grade>();
		try {
			PreparedStatement ps = conn.prepareStatement(
					"SELECT u.username, e.course_name , sd.conduct_exam_id , sd.grade  from users u ,exams e, students_data_in_exam sd, conduct_exam ce\r\n"
							+ "WHERE u.username = sd.username AND u.username = ? \r\n"
							+ "AND sd.conduct_exam_id = ce.conduct_exam_id AND ce.test_id = e.test_id \r\n" + "");
			ps.setString(1, username);
			ResultSet resultSet = ps.executeQuery();
			while (resultSet != null && resultSet.next()) {

				String name = resultSet.getString("username");
				String course = resultSet.getString("course_name");
				String examId = resultSet.getString("conduct_exam_id");
				int grade = resultSet.getInt("grade");
				grades.add(new Grade(name, examId, course, grade));
			}
			return grades;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the graded test of a user by their username and test ID.
	 *
	 * @param username The username of the user.
	 * @param testId   The ID of the test.
	 * @return The graded test for the user and test ID.
	 */
	public static GradedTest getGradedTestByUsernameAndTestId(String username, String testId) {
		try {
			PreparedStatement ps = conn.prepareStatement(
					"SELECT q.question_text,q.choice1,q.choice2,q.choice3,q.choice4 ,q.answer ,q.id, sd.*\r\n"
							+ "FROM question q\r\n"
							+ "JOIN students_data_in_exam sd ON FIND_IN_SET(q.id, sd.questionsID) > 0\r\n"
							+ "where sd.username = ? AND sd.conduct_exam_id = ?");
			ps.setString(1, username);
			ps.setString(2, testId);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet != null && resultSet.next()) {
				String name = resultSet.getString("username");
				String conductExamId = resultSet.getString("conduct_exam_id");
				int grade = resultSet.getInt("grade");
				String startTime = resultSet.getString("start_time");
				String endTime = resultSet.getString("actual_end_time");
				String duration = resultSet.getString("duration");
				String extensionTime = resultSet.getString("extensionTime");
				int waitingApprove = resultSet.getInt("waitingApprove");
				String newGradeComment = resultSet.getString("newGradeComment");
				String methodTaken = resultSet.getString("methodTaken");

				String allComments = resultSet.getString("quesionsComments");
				String[] questionsComments = allComments.split(",");

				String allCorrect = resultSet.getString("correctAnswers");
				String[] correctAnswers = allCorrect.split(",");

				ArrayList<String> questions = new ArrayList<String>();

				String allSanswers = resultSet.getString("answers");
				String[] studentAnswers = allSanswers.split(",");

				ArrayList<ArrayList<String>> options = new ArrayList<ArrayList<String>>();

				do {
					ArrayList<String> temp = new ArrayList<String>();
					temp.add(resultSet.getString("choice1"));
					temp.add(resultSet.getString("choice2"));
					temp.add(resultSet.getString("choice3"));
					temp.add(resultSet.getString("choice4"));
					options.add(temp);
					questions.add(resultSet.getString("question_text"));
				} while (resultSet != null && resultSet.next());

				return new GradedTest(name, conductExamId, grade, startTime, endTime, duration, extensionTime,
						questions, questionsComments, options, studentAnswers, waitingApprove, newGradeComment,
						correctAnswers, methodTaken);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of student users who are registered for a specific conduct
	 * exam.
	 *
	 * @param conductExamID The ID of the conduct exam.
	 * @return The list of student users registered for the conduct exam.
	 */
	private static ArrayList<User> getStudentUserByConductExamID(int conductExamID) {
		try {
			ArrayList<User> users = new ArrayList<>();
			// Fetch usernames from students_taken_exams table
			String fetchUsernamesSql = "SELECT username FROM cems.students_taken_exams WHERE conduct_exam_id = ?";

			PreparedStatement fetchUsernamesStatement = conn.prepareStatement(fetchUsernamesSql);
			fetchUsernamesStatement.setInt(1, conductExamID);
			ResultSet usernamesResultSet = fetchUsernamesStatement.executeQuery(); // here all the relevant user names

			String fetchUserDetailsSql = "SELECT * FROM cems.users WHERE userName = ?";
			PreparedStatement fetchUserDetailsStatement = conn.prepareStatement(fetchUserDetailsSql);
			while (usernamesResultSet.next()) // add all sql quarry
			{
				String userName = usernamesResultSet.getString("username");
				// Fetch user details from users_table
				fetchUserDetailsStatement.setString(1, userName);
				ResultSet userDetailsResultSet = fetchUserDetailsStatement.executeQuery();

				if (userDetailsResultSet.next()) {
					String id = userDetailsResultSet.getString("ID");
					String firstName = userDetailsResultSet.getString("firstName");
					String lastName = userDetailsResultSet.getString("lastName");
					String password = userDetailsResultSet.getString("password");
					String email = userDetailsResultSet.getString("email");
					String role = userDetailsResultSet.getString("role");
					boolean isLogged = userDetailsResultSet.getBoolean("isLogged");

					User userToAdd = new User(userName, lastName, firstName, password, id, email, null, isLogged, role);
					users.add(userToAdd);
				}
			}
			usernamesResultSet.close();
			fetchUsernamesStatement.close();
			return users;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Adds a time extension request for a conduct exam and updates the
	 * corresponding conduct exam entry.
	 *
	 * @param requestTimeExtension The time extension request to add.
	 * @return True if the request is successfully added and the conduct exam is
	 *         updated, false otherwise.
	 */
	public static boolean addLecturerRequestTimeExtension(RequestTimeExtension requestTimeExtension) {
		try {
			// create request in the table of time extension requests
			String query = "INSERT INTO cems.time_extension_requests (conduct_exam_id, time_extension, status) VALUES (?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(query);

			ps.setInt(1, requestTimeExtension.getconductExamId());
			ps.setString(2, requestTimeExtension.getStringExtensionDurationTimeFormat());
			ps.setString(3, requestTimeExtension.getRequestStatus());

			ps.execute();

			// update status and extentionTime in the conduct_exam table
			query = "UPDATE cems.conduct_exam SET request_status = ?, time_extension_request_status = ?, time_extension = ? WHERE conduct_exam_id = ?";
			ps = conn.prepareStatement(query);

			ps.setString(1, requestTimeExtension.getRequestStatus()); // need to be pending
			ps.setString(2, CONSTANTS.requested); // time extension have been requested
			ps.setString(3, requestTimeExtension.getStringExtensionDurationTimeFormat()); // update the duration of the
																							// requested time extension
			ps.setInt(4, requestTimeExtension.getconductExamId());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Retrieves the up-to-date duration and request status for a list of conduct
	 * exams.
	 *
	 * @param outDatedData The list of conduct exams with outdated data.
	 * @return The list of conduct exams with updated duration and request status,
	 *         or null if an error occurs.
	 */
	public static ArrayList<ConductExam> getUpDoDateConductExanDurationAndRequestStatus(
			ArrayList<ConductExam> outDatedData) {
		ArrayList<ConductExam> upToDateData = new ArrayList<>();
		String query = "SELECT request_status, duration FROM cems.conduct_exam WHERE conduct_exam_id = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(query);
			for (ConductExam outDated : outDatedData) // for each given conduct exam we fetch the updated information
														// and add it to the list upToDateData
			{
				ps.setInt(1, outDated.getConduct_exam_id());
				ResultSet resultSet = ps.executeQuery();
				if (resultSet.next()) {
					String duration = resultSet.getString("duration"),
							requestStatus = resultSet.getString("request_status");
					ConductExam upToDateConductExam = new ConductExam(outDated.getConduct_exam_id(), null, null, 0,
							null, null, convertDurationStringToDurationTime(duration)); // only conduct_exam_id and
																						// duration
					upToDateConductExam.setRequestStatus(requestStatus); // updated reuestStatus
					upToDateData.add(upToDateConductExam);
				}
			}
			return upToDateData;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Ends the conduct of an exam by setting the is_open flag to 0 and deleting
	 * time extension requests for the specified conduct exam ID.
	 *
	 * @param conductExamIdToEnd the ID of the conduct exam to end
	 * @return true if the conduct exam is successfully ended, false otherwise
	 */
	public static boolean endConductExamByConductExamID(int conductExamIdToEnd) {
		try {
			// change is_open=0
			String sql = "UPDATE cems.conduct_exam SET is_open = ?, password = ? WHERE conduct_exam_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);

			// Set the values for the prepared statement parameters
			ps.setInt(1, 0); // Assuming the parameter index for is_open is 1
			ps.setString(2, ""); // Assuming the parameter index for password is 2
			ps.setInt(3, conductExamIdToEnd); // Assuming the parameter index for conduct_exam_id is 3

			// Execute the update
			ps.executeUpdate();
			// delete time extension requests form the table time_extension_requests
			sql = "DELETE FROM time_extension_requests WHERE conduct_exam_id = ?";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, conductExamIdToEnd);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Retrieves the data of students who have completed a test in a specific
	 * subject, based on the given parameters.
	 *
	 * @param data the StudentsExamDataGetter object containing the subject and
	 *             username information
	 * @return a list of StudentsDataInExam objects representing the data of
	 *         students who have completed the test, or null if no data is found
	 * @throws SQLException if a database error occurs while retrieving the student
	 *                      data
	 */
	public static List<StudentsDataInExam> getStudentsDoneTestData(StudentsExamDataGetter data) throws SQLException {
		// data[0] = Subject
		// data[1] = username
		List<StudentsDataInExam> data_to_send = new ArrayList<>();
		PreparedStatement ps = conn.prepareStatement(""
				+ "SELECT s.*, c.conduct_exam_name, sb.course_name, sb.course_id, u.ID\r\n"
				+ "FROM students_data_in_exam s\r\n"
				+ "JOIN conduct_exam c ON s.conduct_exam_id = c.conduct_exam_id\r\n"
				+ "JOIN exams e ON c.test_id = e.test_id\r\n" + "JOIN subjects sb ON e.course_code = sb.course_id\r\n"
				+ "JOIN users u ON s.username = u.username\r\n" + "WHERE c.lecturer_user_name = ?\r\n"
				+ "    AND s.methodTaken = 'computerized'\r\n" + "    AND s.waitingApprove = 1 AND c.is_open = 0\r\n"
				+ "    AND sb.subject_id = ?;");
		String subject = (String) data.getSubject();
		String lecturer_username = data.getUsername();
		ps.setString(1, lecturer_username);
		ps.setString(2, subject);

		try (ResultSet rs = ps.executeQuery()) {
			while (rs != null && rs.next()) {
				String temp;
				List<String> questionsComments = null, answers = null, correctAnswers = null;
				String[] questionsId = null;
				String username = rs.getString("username");
				String ID = rs.getString("ID");
				String conductExamId = rs.getString("conduct_exam_id");
				int grade = rs.getInt("grade");
				Duration startTime = DurationConversion(rs.getString("start_time"));
				Duration actualEndTime = DurationConversion(rs.getString("actual_end_time"));
				Duration duration = DurationConversion(rs.getString("duration"));
				String extensionTime = rs.getString("extensionTime");
				if (rs.getString("questionsID") != null)
					questionsId = rs.getString("questionsID").split(",");
				temp = rs.getString("quesionsComments");
				if (temp != null)
					questionsComments = Arrays.asList(temp.replaceAll(",", " ,").split(","));
				temp = rs.getString("answers");
				if (temp != null) {
					answers = Arrays.asList(temp.split(","));
				}
				boolean waitingApprove = rs.getBoolean("waitingApprove");
				String newGradeComment = rs.getString("newGradeComment");
				temp = rs.getString("correctAnswers");
				if (temp != null)
					correctAnswers = Arrays.asList(temp.split(","));
				String methodTaken = rs.getString("methodTaken");
				String conductExamName = rs.getString("conduct_exam_name");
				Course course = new Course(rs.getString("course_name"), rs.getString("course_id")); // Assuming you have
				// a Course
				// constructor that
				// takes the course
				// name

				StudentsDataInExam studentData = new StudentsDataInExam(username, ID, conductExamId, grade, startTime,
						actualEndTime, duration, extensionTime, getQuestionByID(questionsId), questionsComments,
						answers, waitingApprove, newGradeComment, correctAnswers, methodTaken, conductExamName, course);

				data_to_send.add(studentData);
			}
		}
		if (data_to_send.isEmpty())
			return null;
		return data_to_send;
	}

	/**
	 * Converts a string representation of time in the format "HH:mm:ss" into a
	 * Duration object.
	 *
	 * @param time the string representation of time
	 * @return a Duration object representing the converted time
	 */
	private static Duration DurationConversion(String time) {
		String durationString = time.trim();
		String[] components = durationString.split(":");
		int hours = Integer.parseInt(components[0]);
		int minutes = Integer.parseInt(components[1]);
		int seconds = Integer.parseInt(components[2]);

		// Calculate the total duration in seconds
		long totalSeconds = (hours * 3600) + (minutes * 60) + seconds;

		// Create duration object
		Duration newDuration = Duration.ofSeconds(totalSeconds);
		return newDuration;
	}

	/**
	 * Updates the student data in the exam with the provided StudentsDataInExam
	 * object.
	 *
	 * @param data the StudentsDataInExam object containing the updated data
	 * @return true if the update is successful, false otherwise
	 * @throws SQLException if a database error occurs while updating the student
	 *                      data in the exam
	 */
	public static boolean updateStudentDataInExam(StudentsDataInExam data) throws SQLException {
		String QuestionComments = null, gradeComment = null;
		if (data.getQuestionsComments() != null)
			QuestionComments = String.join(",", data.getQuestionsComments());
		if (data.getNewGradeComment() != null)
			gradeComment = data.getNewGradeComment();
		PreparedStatement ps = conn.prepareStatement("" + "UPDATE students_data_in_exam\r\n"
				+ "SET quesionsComments = ?,\r\n" + "    grade = ?,\r\n" + "    newGradeComment = ?,\r\n"
				+ "    waitingApprove = 2\r\n" + "WHERE username = ? AND conduct_exam_id = ?;");
		// set questions' comments in query
		if (QuestionComments != null)
			ps.setString(1, QuestionComments);
		else
			ps.setString(1, null);
		// set grade
		ps.setInt(2, data.getGrade()); // the returned grade is the previous grade if it was not updated in
		// the controller
		// set grade comment in query
		if (gradeComment != null)
			ps.setString(3, gradeComment);
		else
			ps.setString(3, null);

		// set required username and conduct exam id to query
		ps.setString(4, data.getUsername());
		ps.setString(5, data.getConductExamId());

		int updatedRow = ps.executeUpdate();
		if (updatedRow > 0)
			return true;
		return false;
	}

	/**
	 * Convert a Duration Into a HH:MM:SS String
	 * 
	 * @param duration
	 * @return An HH:MM:SS representation of the duration object
	 */

	private static String formatDuration(Duration duration) {
		long totalSeconds = duration.getSeconds();

		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;

		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	// Helper methods to format data for insertion
	/**
	 * Retrieves a string representation of question IDs from a list of questions.
	 *
	 * @param questionsList the list of questions
	 * @return a string representation of question IDs separated by commas, or null
	 *         if the input list is null
	 */
	private static String getQuestionsIdAsString(List<Question> questionsList) {
		if (questionsList == null)
			return null;
		StringBuilder sb = new StringBuilder();
		for (Question question : questionsList) {
			sb.append(question.getID()).append(",");
		}
		return sb.substring(0, sb.length() - 1);
	}

	// convert any List of String into a
	/**
	 * Converts a list of strings into a single string with comma-separated values.
	 *
	 * @param commands the list of strings
	 * @return a string representation of the list elements separated by commas, or
	 *         null if the input list is null
	 */
	private static String convertStringArrayIntoStringWithCommaBetweenThem(List<String> commands) {
		if (commands == null)
			return null;
		return String.join(",", commands);
	}

	/**
	 * Converts a boolean value into a tinyint representation.
	 *
	 * @param expression the boolean value
	 * @return 1 if the expression is true, 0 if the expression is false
	 */
	private static int convertBooleanToTinyInt(boolean excpretion) {
		if (excpretion)
			return 1;
		return 0;
	}

	/**
	 * WE NEED TO CHNAGE THE isWaitingApprove INTO AN INT OBJECT.
	 * 
	 * @param studentData
	 * @return
	 */
	public static Message InsertStudentDataExamIntoDB(StudentsDataInExam studentData) {

		// SQL query to insert the data
		String sql = "INSERT INTO students_data_in_exam (username, conduct_exam_id, grade, start_time, actual_end_time, duration, extensionTime, questionsID, quesionsComments, answers, waitingApprove, newGradeComment, correctAnswers, methodTaken) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, studentData.getUsername());
			statement.setString(2, studentData.getConductExamId());
			statement.setInt(3, studentData.getGrade());
			statement.setString(4, formatDuration(studentData.getStartTime()));
			statement.setString(5, formatDuration(studentData.getActualEndTime()));
			statement.setString(6, formatDuration(studentData.getDuration()));
			statement.setString(7, studentData.getExtensionTime());
			statement.setString(8, getQuestionsIdAsString(studentData.getQuestions()));
			ArrayList<String> emptyQuestionComments = new ArrayList<>();
			for (Question questionInExam : studentData.getQuestions()) // for each question add fake comment " "
				emptyQuestionComments.add(" ");
			statement.setString(9, convertStringArrayIntoStringWithCommaBetweenThem(emptyQuestionComments));
			statement.setString(10, convertStringArrayIntoStringWithCommaBetweenThem(studentData.getAnswers()));
			statement.setInt(11, convertBooleanToTinyInt(studentData.isWaitingApprove()));
			statement.setString(12, studentData.getNewGradeComment());
			statement.setString(13, convertStringArrayIntoStringWithCommaBetweenThem(studentData.getCorrectAnswers()));
			statement.setString(14, studentData.getMethodTaken());

			statement.executeUpdate();

			return new Message(CONSTANTS.responseWithUpdateExamDataInDB, null);

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Failed to add row!");
			return null;
		}
	}

	/**
	 * Checks if an exam is locked based on the provided
	 * ConductExamIdWithScreenTypeObject data.
	 *
	 * @param data the ConductExamIdWithScreenTypeObject containing the conduct exam
	 *             ID and exam type
	 * @return a Message object containing the exam lock status and exam type pair,
	 *         or null if the conduct exam is not found
	 */
	public static Message checkIfExamIsLocked(ConductExamIdWithScreenTypeObject data) {
		String sql = "SELECT is_open FROM cems.conduct_exam WHERE conduct_exam_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setInt(1, data.getConduct_exam_id());
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				int isOpenValue = rs.getInt("is_open");
				return new Message(CONSTANTS.responseForCheckForExamLock,
						new Pair<Boolean, ExamType>(isOpenValue == 1, data.getExam_type()));
			} else {
				System.out.println("Conduct_exam not found Errorr!");
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Denies the time extension for a given conduct exam ID and updates the enum
	 * request_status to denied in the tables time_extension_requests and
	 * conduct_exam.
	 * 
	 * @param conduct_exam_id The ID of the conduct exam.
	 * @return {@code true} if the update is successful, {@code false} otherwise.
	 * @throws SQLException if a database access error occurs.
	 */
	public static boolean denyTimeExtensionForConductExamId(int conduct_exam_id) throws SQLException {
		PreparedStatement updateQueryAtTimeExtensionTable = conn.prepareStatement(
				"UPDATE time_extension_requests\r\n" + "SET status = 'Denied'\r\n" + "WHERE conduct_exam_id = ?;");
		updateQueryAtTimeExtensionTable.setInt(1, conduct_exam_id);
		if (updateQueryAtTimeExtensionTable.executeLargeUpdate() <= 0)
			return false;
		PreparedStatement updateQueryAtConductExamTable = conn.prepareStatement(
				"UPDATE conduct_exam\r\n" + "SET request_status = 'Denied' \r\n" + "WHERE conduct_exam_id = ?;");
		updateQueryAtConductExamTable.setInt(1, conduct_exam_id);
		if (updateQueryAtConductExamTable.executeUpdate() > 0)
			return true;
		return false;
	}

	public static List<ConductExam> getTimeExtensionRequests() throws SQLException {
		List<ConductExam> timeExtensionList = new ArrayList<>();
		PreparedStatement getConductExamTimeExtensionRequests = conn
				.prepareStatement("SELECT ce.*, u.firstName, u.lastName " + "FROM conduct_exam ce "
						+ "JOIN users u ON ce.lecturer_user_name = u.userName "
						+ "WHERE ce.request_status = 'Pending'");
		ResultSet rs = getConductExamTimeExtensionRequests.executeQuery();
		while (rs != null && rs.next()) {
			Duration endTime = convertDurationStringToDurationTime(rs.getString("time_extension"));

			timeExtensionList.add(new ConductExam(rs.getInt("conduct_exam_id"), rs.getString("password"),
					rs.getString("test_id"), rs.getInt("is_open"), rs.getString("conduct_exam_name"),
					rs.getString("firstName") + " " + rs.getString("lastName"), endTime));
		}
		if (timeExtensionList.size() > 0)
			return timeExtensionList;
		return null;
	}

	/**
	 * Gets All the statistics data from courses and approved conducts exams.
	 * 
	 * @param lecturer_id_and_userName The first is the lecturer_id . The second is
	 *                                 the lecturer UserName.
	 * @return
	 */
	public static Message GetAllLecturerCourseDataIncludingExamsGrades(Pair<String, String> lecturer_id_and_userName) {
		String query = "SELECT st.*, e.course_name " + "FROM exams e, conduct_exam ce "
				+ "JOIN `statistics` st ON st.conduct_exam_id = ce.conduct_exam_id "
				+ "WHERE e.test_id = ce.test_id AND ce.is_open = '0' AND ce.approved_grades = 'Approved' AND "
				+ "(e.author_id = ? OR ce.lecturer_user_name = ?)";

		ArrayList<StatisticsReportDataForLecturer> lecturer_statistics_data = new ArrayList<>();

		try {

			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, lecturer_id_and_userName.getFirst());
			statement.setString(2, lecturer_id_and_userName.getSecond());
			ResultSet rs = statement.executeQuery();
			while (rs != null && rs.next()) {
				int conduct_exam_id = rs.getInt("conduct_exam_id");
				String conduct_exam_name = rs.getString("conduct_exam_name");
				String course_name = rs.getString("course_name");
				String username_exam_conductor = rs.getString("username_exam_conductor");
				int number_of_students = rs.getInt("num_of_students");
				int[] gradeCountInEachCategory = new int[9];

				for (int i = 0; i < gradeCountInEachCategory.length; i++) {
					gradeCountInEachCategory[i] = rs.getInt(i + 3); // the categories column start at index 3
				}
				float median = rs.getFloat("median");
				float average = rs.getFloat("average");
				int max_grade = rs.getInt("max_grade");
				int min_grade = rs.getInt("min_grade");

				lecturer_statistics_data.add(new StatisticsReportDataForLecturer(conduct_exam_id, conduct_exam_name,
						course_name, username_exam_conductor, number_of_students, gradeCountInEachCategory, median,
						average, max_grade, min_grade));
			}
			return new Message(CONSTANTS.responseWithGetConductExamAndRelevantDataForLecturer,
					lecturer_statistics_data);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the SMS simulation data for a specific user.
	 *
	 * @param username the username of the user
	 * @return a list of StudentsDataInExam objects representing the SMS simulation
	 *         data, or null if no data is found
	 * @throws SQLException if a database error occurs while retrieving the SMS
	 *                      simulation data
	 */
	public static List<StudentsDataInExam> getSMS_Simulation(String username) throws SQLException {
		List<StudentsDataInExam> smsList = new ArrayList<>();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT sd.*, ce.conduct_exam_name, u.firstName, u.lastName \r\n" + "FROM students_data_in_exam sd\r\n"
						+ "JOIN conduct_exam ce ON ce.conduct_exam_id = sd.conduct_exam_id \r\n"
						+ "JOIN users  u ON sd.username = u.userName \r\n"
						+ "WHERE sd.username = ? AND sd.waitingApprove = 2;");
		ps.setString(1, username);
		ResultSet rs = ps.executeQuery();
		while (rs != null && rs.next()) {
			smsList.add(new StudentsDataInExam(rs.getInt("grade"), rs.getString("conduct_exam_name"),
					rs.getString("firstName"), rs.getString("lastName")));
		}
		if (smsList.size() > 0)
			return smsList;
		return null;
	}

	public static HashMap<String, Exam> getTakenExamsForStatistics() {
		HashMap<String, Exam> result = new HashMap<>();
		try {
			PreparedStatement ps = conn
					.prepareStatement("select ce.conduct_exam_id,e.* from exams e, conduct_exam ce\r\n"
							+ "where e.test_id = ce.test_id and ce.is_open = '0' and ce.approved_grades = \"Approved\"");

			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				int conId = resultSet.getInt("conduct_exam_id");
				String conString = conId + "";
				Exam exam = createExamHelper(resultSet);
				result.put(conString, exam);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	public static ArrayList<StatisticsReport> getStatisticsData() {
		ArrayList<StatisticsReport> result = new ArrayList<>();
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM cems.statistics");

			ResultSet resultSet = ps.executeQuery();
			while (resultSet != null && resultSet.next()) {
				int conductExamId = resultSet.getInt("conduct_exam_id");
				int numOfStd = resultSet.getInt("num_of_students");
				int g54_9 = resultSet.getInt("range_0_54_9");
				int g55_64 = resultSet.getInt("range_55_64");
				int g65_69 = resultSet.getInt("range_65_69");
				int g70_74 = resultSet.getInt("range_70_74");
				int g75_79 = resultSet.getInt("range_75_79");
				int g80_84 = resultSet.getInt("range_80_84");
				int g85_89 = resultSet.getInt("range_85_89");
				int g90_94 = resultSet.getInt("range_90_94");
				int g95_100 = resultSet.getInt("range_95_100");
				String conExamName = resultSet.getString("conduct_exam_name");
				String userExamAuth = resultSet.getString("username_exam_author");
				String userExamCond = resultSet.getString("username_exam_conductor");
				float med = resultSet.getFloat("median");
				float avg = resultSet.getFloat("average");
				int max = resultSet.getInt("max_grade");
				int min = resultSet.getInt("min_grade");
				result.add(new StatisticsReport(conductExamId, numOfStd, g54_9, g55_64, g65_69, g70_74, g75_79, g80_84,
						g85_89, g90_94, g95_100, conExamName, userExamAuth, userExamCond, med, avg, max, min));

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	public static boolean approveAllGradeOfConductExamAddCreateStatistic(int conductExamId) {
		try {
			String sql = "UPDATE cems.students_data_in_exam SET waitingApprove = 2 WHERE conduct_exam_id = ?"; // approve
																												// all
																												// students
																												// grades
																												// in
																												// this
																												// exam
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, conductExamId);
			ps.executeUpdate();

			sql = "UPDATE cems.conduct_exam SET approved_grades = ? WHERE conduct_exam_id = ?"; // update in conduct
																								// exam table that all
																								// grades of conduct
																								// exam id are approved
			ps = conn.prepareStatement(sql);
			ps.setString(1, CONSTANTS.Approved);
			ps.setInt(2, conductExamId);
			ps.executeUpdate();

			sql = "SELECT conduct_exam_name, test_id, lecturer_user_name FROM cems.conduct_exam WHERE conduct_exam_id = ?";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, conductExamId);
			ResultSet resultSet = ps.executeQuery();
			String ExamId = null, conductExamName = null, userNameExamConductor = null;

			if (resultSet.next()) {
				conductExamName = resultSet.getString("conduct_exam_name");
				ExamId = resultSet.getString("test_id");
				userNameExamConductor = resultSet.getString("lecturer_user_name");
			}
			sql = "SELECT author_id FROM cems.exams WHERE test_id = ?"; // get the id of the lecturer who created this
																		// exam
			ps = conn.prepareStatement(sql);
			ps.setString(1, ExamId);
			resultSet = ps.executeQuery();
			String AuthorIdExamCreator = null;
			if (resultSet.next())
				AuthorIdExamCreator = resultSet.getString("author_id");

			sql = "SELECT userName FROM cems.users WHERE ID = ?"; // get the user name of the lecturer who created this
																	// exam
			ps = conn.prepareStatement(sql);
			ps.setString(1, AuthorIdExamCreator);
			resultSet = ps.executeQuery();
			String ExamCreatorUserName = null;
			if (resultSet.next())
				ExamCreatorUserName = resultSet.getString("userName");

			// add statistic of this conduct exam to DB

			return addStatisticToConductExan(conductExamId, conductExamName, ExamCreatorUserName,
					userNameExamConductor); // add statisticReport of this conduct exam return true on success else
											// false
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static HashMap<String, ArrayList<Grade>> getStudentsTakenTests() {
		HashMap<String, ArrayList<Grade>> result = new HashMap<>();
		try {
			PreparedStatement ps = conn.prepareStatement(
					"SELECT ce.username, GROUP_CONCAT(DISTINCT ce.conduct_exam_id SEPARATOR ',')AS exam_ids, GROUP_CONCAT(DISTINCT sd.grade SEPARATOR ',')  AS test_score\r\n"
							+ "FROM cems.students_taken_exams ce, cems.students_data_in_exam sd\r\n"
							+ "WHERE ce.isTaken = '1' AND ce.username = sd.username\r\n" + "GROUP BY ce.username");

			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) {
				String username = resultSet.getString("username");
				String exams = resultSet.getString("exam_ids");
				String allGrades = resultSet.getString("test_score");

				String[] examIds = exams.split(",");
				String[] grades = allGrades.split(",");
				ArrayList<Grade> gr = new ArrayList<Grade>();

				for (int i = 0; i < grades.length; i++) {
					gr.add(new Grade(username, examIds[i], "", Integer.parseInt(grades[i])));
				}
				result.put(username, gr);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	public static void insertProfilePicture(String username, byte[] in) {
		try {
			PreparedStatement ps = conn.prepareStatement("UPDATE profile_pictures SET picture = ? WHERE username = ? ");
			
			ps.setBlob(1,new javax.sql.rowset.serial.SerialBlob(in));
			ps.setString(2, username);
			// Executing the statement
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] getProfilePicture(String username) {
		byte[] b;
		try {
			PreparedStatement ps = conn
					.prepareStatement("SELECT p.picture FROM cems.profile_pictures p\r\n" + "where p.username = ?");
			ps.setString(1, username);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				b = resultSet.getBytes("picture");
				return b;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	// this function get conduct exam id , conductExamName , usernameExamAuthor ,
	// userNameExamConductor and create statistic data in the table statistic for
	// the given conduct exam id
	private static boolean addStatisticToConductExan(int conductExamId, String conductExamName,
			String usernameExamAuthor, String userNameExamConductor) {
		HashMap<String, int[]> gardesFields = new HashMap<>();
		int minumunGrade, maximumGrade; // minimum grade maximum grade
		float averageGrade, median; // average grade median grade

		ArrayList<Integer> listOfGrades = new ArrayList<>();
		gardesFields.put("0_54.9", new int[1]); // each grade field get 0 counting for start
		gardesFields.put("55_64", new int[1]);
		gardesFields.put("65_69", new int[1]);
		gardesFields.put("70_74", new int[1]);
		gardesFields.put("75_79", new int[1]);
		gardesFields.put("80_84", new int[1]);
		gardesFields.put("85_89", new int[1]);
		gardesFields.put("90_94", new int[1]);
		gardesFields.put("95_100", new int[1]);

		try {
			String getStudentGardesInExam = "SELECT * FROM cems.students_data_in_exam WHERE conduct_exam_id = ? AND waitingApprove = ?";
			PreparedStatement ps = conn.prepareStatement(getStudentGardesInExam);
			ps.setInt(1, conductExamId);
			ps.setInt(2, 2); // the student grade must be approved
			ResultSet resultSet = ps.executeQuery();
			while (resultSet.next()) // go over all the students gardes and add them to the gardesFields hash map and
										// count the students
			{
				int grade = resultSet.getInt("grade");

				if (grade >= 0 && grade <= 54) // advance counter of grade fields according to the given grade
				{
					gardesFields.get("0_54.9")[0]++;
				} else if (grade >= 55 && grade <= 64) {
					gardesFields.get("55_64")[0]++;
				} else if (grade >= 65 && grade <= 69) {
					gardesFields.get("65_69")[0]++;
				} else if (grade >= 70 && grade <= 74) {
					gardesFields.get("70_74")[0]++;
				} else if (grade >= 75 && grade <= 79) {
					gardesFields.get("75_79")[0]++;
				} else if (grade >= 80 && grade <= 84) {
					gardesFields.get("80_84")[0]++;
				} else if (grade >= 85 && grade <= 89) {
					gardesFields.get("85_89")[0]++;
				} else if (grade >= 90 && grade <= 94) {
					gardesFields.get("90_94")[0]++;
				} else if (grade >= 95 && grade <= 100) {
					gardesFields.get("95_100")[0]++;
				}

				listOfGrades.add(grade); // add the grade to the list
			}
			minumunGrade = Collections.min(listOfGrades); // calculate max
			maximumGrade = Collections.max(listOfGrades); // calculate min
			int sum = 0;
			for (int grade : listOfGrades) // calcualte avarage
			{
				sum += grade;
			}
			averageGrade = (float) sum / listOfGrades.size();
			median = getMedian(listOfGrades); // calcualte median grade

			String sql = "INSERT INTO cems.statistics (conduct_exam_id, num_of_students, range_0_54_9, range_55_64, range_65_69, range_70_74, range_75_79, range_80_84, range_85_89, range_90_94, range_95_100, conduct_exam_name, username_exam_author, username_exam_conductor, median, average, max_grade, min_grade) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			ps = conn.prepareStatement(sql);

			ps.setInt(1, conductExamId); // conduct_exam_id
			ps.setInt(2, listOfGrades.size()); // num_of_students
			ps.setInt(3, gardesFields.get("0_54.9")[0]); // 0_54_9
			ps.setInt(4, gardesFields.get("55_64")[0]); // 55_64
			ps.setInt(5, gardesFields.get("65_69")[0]); // 65_69
			ps.setInt(6, gardesFields.get("70_74")[0]); // 70_74
			ps.setInt(7, gardesFields.get("75_79")[0]); // 75_79
			ps.setInt(8, gardesFields.get("80_84")[0]); // 80_84
			ps.setInt(9, gardesFields.get("85_89")[0]); // 85_89
			ps.setInt(10, gardesFields.get("90_94")[0]); // 90_94
			ps.setInt(11, gardesFields.get("95_100")[0]); // 95_100
			ps.setString(12, conductExamName); // conduct_exam_name
			ps.setString(13, usernameExamAuthor); // username_exam_author
			ps.setString(14, userNameExamConductor); // username_exam_conductor
			ps.setFloat(15, median); // median
			ps.setFloat(16, averageGrade); // average
			ps.setInt(17, maximumGrade); // max_grade
			ps.setInt(18, minumunGrade); // min_grade
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	private static float getMedian(ArrayList<Integer> numbers) {
		Collections.sort(numbers);

		int size = numbers.size();

		if (size == 0) {
			throw new IllegalArgumentException("List should contain at least 1 element.");
		}

		int middleIndex = (size - 1) / 2;

		if (size % 2 == 1) {
			return numbers.get(middleIndex);
		} else {
			return (float) (numbers.get(middleIndex) + numbers.get(middleIndex + 1)) / 2.0f;
		}
	}

	/**
	 * Sends User object to register
	 *
	 * @param User
	 * @return false if exists else true
	 * @throws SQLException if a database error occurs while inserting new User to
	 *                      users
	 */
	public static boolean registerUser(User user) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(
				"INSERT INTO users (ID, userName, firstName, lastName, password, email, role, isLogged)\r\n"
						+ "SELECT ?, ?, ?, ?, ?, ?, ?, 0\r\n" + "FROM dual\r\n" + "WHERE NOT EXISTS (\r\n"
						+ "    SELECT * FROM users us1 WHERE us1.ID = ? OR us1.userName = ?\r\n" + ")");
		ps.setString(1, user.getIDNumber());
		ps.setString(2, user.getUserName());
		ps.setString(3, user.getFirstName());
		ps.setString(4, user.getLastName());
		ps.setString(5, user.getPassword());
		ps.setString(6, user.getEmail());
		ps.setString(7, user.getUserPermission());
		ps.setString(8, user.getIDNumber());
		ps.setString(9, user.getUserName());
		int rows = ps.executeUpdate();
		if (rows > 0)
			return true;
		return false;
	}
}

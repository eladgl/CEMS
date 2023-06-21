package entities;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;

/**
 * Represents the data of a student in an exam.
 */
public class StudentsDataInExam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3664696129269604649L;
	private String username;
	private String userId;
	private String conductExamId;
	private int grade;
	private Duration startTime;
	private Duration actualEndTime;
	private Duration duration;
	private String extensionTime;
	private List<Question> questionsList;
	private List<String> questionsComments;
	private List<String> answers;
	private boolean waitingApprove;
	private String newGradeComment;
	private List<String> correctAnswers;
	private String methodTaken;
	private String conductExamName;
	private Course course;

	/**
	 * Constructs a StudentsDataInExam object with the provided parameters.
	 *
	 * @param username          the username of the student
	 * @param userId            the ID of the student
	 * @param conductExamId     the ID of the conducted exam
	 * @param grade             the grade of the student
	 * @param startTime         the start time of the exam
	 * @param actualEndTime     the actual end time of the exam
	 * @param duration          the duration of the exam
	 * @param extensionTime     the extension time of the exam
	 * @param questionsList     the IDs of the questions in the exam
	 * @param questionsComments the comments on each question in the exam
	 * @param answers           the answers provided by the student
	 * @param waitingApprove    indicates if the student's exam is waiting for
	 *                          approval
	 * @param newGradeComment   the comment for a changed grade
	 * @param correctAnswers    the correct answers for the questions
	 * @param methodTaken       the method used to take the exam
	 * @param conductExamName   the name of the conducted exam
	 * @param course            the course of the exam
	 */

	public StudentsDataInExam(String username, String userId, String conductExamId, int grade, Duration startTime,
			Duration actualEndTime, Duration duration, String extensionTime, List<Question> questionsList,
			List<String> questionsComments, List<String> answers, boolean waitingApprove, String newGradeComment,
			List<String> correctAnswers, String methodTaken, String conductExamName, Course course) {
		super();
		this.username = username;
		this.userId = userId;
		this.conductExamId = conductExamId;
		this.grade = grade;
		this.startTime = startTime;
		this.actualEndTime = actualEndTime;
		this.duration = duration;
		this.extensionTime = extensionTime;
		this.questionsList = questionsList;
		this.questionsComments = questionsComments;
		this.answers = answers;
		this.waitingApprove = waitingApprove;
		this.newGradeComment = newGradeComment;
		this.correctAnswers = correctAnswers;
		this.methodTaken = methodTaken;
		this.conductExamName = conductExamName;
		this.course = course;
	}

	// this is a constructor for building SMS messages for the student
	public StudentsDataInExam(int grade, String conductExamName, String fname, String lname) {
		this.grade = grade;
		this.conductExamName = conductExamName;
		this.username = fname + " " + lname;
	}

	// this is a constructor for building SMS messages for the student
	public StudentsDataInExam(String message) {
		this.newGradeComment = message; // using this to store a built message
	}

	/**
	 * Returns the ID of the student.
	 *
	 * @return the ID of the student
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the ID of the student.
	 *
	 * @param userId the ID of the student
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Returns the ID of the conducted exam.
	 *
	 * @return the ID of the conducted exam
	 */
	public String getConductExamId() {
		return conductExamId;
	}

	/**
	 * Sets the ID of the conducted exam.
	 *
	 * @param conductExamId the ID of the conducted exam
	 */
	public void setConductExamId(String conductExamId) {
		this.conductExamId = conductExamId;
	}

	/**
	 * Returns the grade of the student.
	 *
	 * @return the grade of the student
	 */
	public int getGrade() {
		return grade;
	}

	/**
	 * Sets the grade of the student.
	 *
	 * @param grade the grade of the student
	 */
	public void setGrade(int grade) {
		this.grade = grade;
	}

	/**
	 * Returns the start time of the exam.
	 *
	 * @return the start time of the exam
	 */
	public Duration getStartTime() {
		return startTime;
	}

	/**
	 * Sets the start time of the exam.
	 *
	 * @param startTime the start time of the exam
	 */
	public void setStartTime(Duration startTime) {
		this.startTime = startTime;
	}

	/**
	 * Returns the actual end time of the exam.
	 *
	 * @return the actual end time of the exam
	 */
	public Duration getActualEndTime() {
		return actualEndTime;
	}

	/**
	 * Sets the actual end time of the exam.
	 *
	 * @param actualEndTime the actual end time of the exam
	 */
	public void setActualEndTime(Duration actualEndTime) {
		this.actualEndTime = actualEndTime;
	}

	/**
	 * Returns the duration of the exam.
	 *
	 * @return the duration of the exam
	 */
	public Duration getDuration() {
		return duration;
	}

	/**
	 * Sets the duration of the exam.
	 *
	 * @param duration the duration of the exam
	 */
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	/**
	 * Returns the extension time of the exam.
	 *
	 * @return the extension time of the exam
	 */
	public String getExtensionTime() {
		return extensionTime;
	}

	/**
	 * Sets the extension time of the exam.
	 *
	 * @param extensionTime the extension time of the exam
	 */
	public void setExtensionTime(String extensionTime) {
		this.extensionTime = extensionTime;
	}

	/**
	 * Returns the IDs of the questions in the exam.
	 *
	 * @return the IDs of the questions in the exam
	 */
	public List<Question> getQuestions() {
		return questionsList;
	}

	/**
	 * Sets the IDs of the questions in the exam.
	 *
	 * @param questionsId the IDs of the questions in the exam
	 */
	public void setQuestionsId(List<Question> questionsId) {
		this.questionsList = questionsId;
	}

	/**
	 * Returns the comments on each question in the exam.
	 *
	 * @return the comments on each question in the exam
	 */
	public List<String> getQuestionsComments() {
		return questionsComments;
	}

	/**
	 * Sets the comments on each question in the exam.
	 *
	 * @param questionsComments the comments on each question in the exam
	 */
	public void setQuestionsComments(List<String> questionsComments) {
		this.questionsComments = questionsComments;
	}

	/**
	 * Returns the answers provided by the student.
	 *
	 * @return the answers provided by the student
	 */
	public List<String> getAnswers() {
		return answers;
	}

	/**
	 * Returns true if the student's exam is waiting for approval, false otherwise.
	 *
	 * @return true if the student's exam is waiting for approval, false otherwise
	 */
	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

	/**
	 * Sets whether the student's exam is waiting for approval.
	 *
	 * @param waitingApprove true if the student's exam is waiting for approval,
	 *                       false otherwise
	 */
	public boolean isWaitingApprove() {
		return waitingApprove;
	}

	/**
	 * Returns the comment for a changed grade.
	 *
	 * @return the comment for a changed grade
	 */
	public void setWaitingApprove(boolean waitingApprove) {
		this.waitingApprove = waitingApprove;
	}

	/**
	 * Sets the comment for a changed grade.
	 *
	 * @param newGradeComment the comment for a changed grade
	 */
	public String getNewGradeComment() {
		return newGradeComment;
	}

	/**
	 * Returns the correct answers for the questions.
	 *
	 * @return the correct answers for the questions
	 */
	public void setNewGradeComment(String newGradeComment) {
		this.newGradeComment = newGradeComment;
	}

	/**
	 * Returns the correct answers for the questions.
	 *
	 * @return the correct answers for the questions
	 */
	public List<String> getCorrectAnswers() {
		return correctAnswers;
	}

	/**
	 * Sets the correct answers for the questions.
	 *
	 * @param correctAnswers the correct answers for the questions
	 */
	public void setCorrectAnswers(List<String> correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	/**
	 * Returns the method used to take the exam.
	 *
	 * @return the method used to take the exam
	 */
	public String getMethodTaken() {
		return methodTaken;
	}

	/**
	 * Sets the method used to take the exam.
	 *
	 * @param methodTaken the method used to take the exam
	 */
	public void setMethodTaken(String methodTaken) {
		this.methodTaken = methodTaken;
	}

	/**
	 * Returns the name of the conducted exam.
	 *
	 * @return the name of the conducted exam
	 */
	public String getConductExamName() {
		return conductExamName;
	}

	/**
	 * Sets the name of the conducted exam.
	 *
	 * @param conductExamName the name of the conducted exam
	 */
	public void setConductExamName(String conductExamName) {
		this.conductExamName = conductExamName;
	}

	/**
	 * Returns the course of the exam.
	 *
	 * @return the course of the exam
	 */
	public Course getCourse() {
		return course;
	}

	/**
	 * Sets the course of the exam.
	 *
	 * @param course the course of the exam
	 */
	public void setCourse(Course course) {
		this.course = course;
	}

	/**
	 * Returns the username of the student.
	 *
	 * @return the username of the student
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 *
	 * @param obj the reference object with which to compare
	 * @return true if this object is the same as the obj argument, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		StudentsDataInExam other = (StudentsDataInExam) obj;
		return username.equals(other.username) && conductExamId.equals(other.conductExamId);
	}

	/**
	 * Returns a hash code value for the object.
	 *
	 * @return a hash code value for this object
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (userId != null ? userId.hashCode() : 0);
		result = 31 * result + (conductExamName != null ? conductExamName.hashCode() : 0);
		return result;
	}

}

package entities;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * This class represents a graded test.
 * It contains information such as the test name, conduct exam ID, grade, start time, end time, duration,
 * extension time, questions, question comments, options, student answers, waiting approval status,
 * new grade comment, correct answers, and the method taken.
 * Implements Serializable to support serialization.
 */
public class GradedTest implements Serializable {

	private static final long serialVersionUID = -6119570358181868525L;
	
	private String name;
	private String conductExamId;
	private int grade;
	private String startTime;
	private String endTime;
	private String duration;
	private String extensionTime;
	private ArrayList<String> questions = null;
	private String[] questionsComments = null;
	private ArrayList<ArrayList<String>> options = null;
	private String[] studentAnswers = null;
	private int waitingApprove;
	private String newGradeComment;
	private String[] correctAnswers = null;
	private String methodTaken;
	/**
     * Constructs a GradedTest object with the specified details.
     *
     * @param name the test name
     * @param conductExamId the conduct exam ID
     * @param grade the grade value
     * @param startTime the start time of the test
     * @param endTime the end time of the test
     * @param duration the duration of the test
     * @param extensionTime the extension time of the test
     * @param questions the list of questions
     * @param questionsComments the comments for each question
     * @param options the options for each question
     * @param studentAnswers the student's answers for each question
     * @param waitingApprove the waiting approval status
     * @param newGradeComment the new grade comment
     * @param correctAnswers the correct answers for each question
     * @param methodTaken the method taken for the test
     */
	public GradedTest(String name, String conductExamId, int grade, String startTime, String endTime, String duration,
			String extensionTime, ArrayList<String> questions, String[] questionsComments, ArrayList<ArrayList<String>> options,
			String[] studentAnswers, int waitingApprove, String newGradeComment, String[] correctAnswers, String methodTaken) {
		super();
		this.name = name;
		this.conductExamId = conductExamId;
		this.grade = grade;
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = duration;
		this.extensionTime = extensionTime;
		this.questions = questions;
		this.questionsComments = questionsComments;
		this.options = options;
		this.studentAnswers = studentAnswers;
		this.waitingApprove = waitingApprove;
		this.newGradeComment = newGradeComment;
		this.correctAnswers = correctAnswers;
		this.methodTaken = methodTaken;
	}
	 /**
     * Returns the test name.
     *
     * @return the test name
     */
	public String getName() {
		return name;
	}
	/**
     * Returns the conduct exam ID.
     *
     * @return the conduct exam ID
     */
	public String getConductExamId() {
		return conductExamId;
	}
	 /**
     * Returns the grade value.
     *
     * @return the grade value
     */
	public int getGrade() {
		return grade;
	}
	/**
     * Returns the start time of the test.
     *
     * @return the start time
     */
	public String getStartTime() {
		return startTime;
	}
	/**
     * Returns the end time of the test.
     *
     * @return the end time
     */
	public String getEndTime() {
		return endTime;
	}
	/**
     * Returns the duration of the test.
     *
     * @return the duration
     */
	public String getDuration() {
		return duration;
	}
	/**
     * Returns the extension time of the test.
     *
     * @return the extension time
     */
	public String getExtensionTime() {
		return extensionTime;
	}
	/**
     * Returns the list of questions.
     *
     * @return the list of questions
     */
	public ArrayList<String> getQuestions() {
		return questions;
	}
	/**
     * Returns the comments for each question.
     *
     * @return the comments for each question
     */
	public String[] getQuestionsComments() {
		return questionsComments;
	}
	/**
     * Returns the options for each question.
     *
     * @return the options for each question
     */
	public ArrayList<ArrayList<String>> getOptions() {
		return options;
	}
	/**
     * Returns the student's answers for each question.
     *
     * @return the student's answers for each question
     */
	public String[] getStudentAnswers() {
		return studentAnswers;
	}
	/**
     * Returns the waiting approval status.
     *
     * @return the waiting approval status
     */
	public int getWaitingApprove() {
		return waitingApprove;
	}
	/**
     * Returns the new grade comment.
     *
     * @return the new grade comment
     */
	public String getNewGradeComment() {
		return newGradeComment;
	}
	/**
     * Returns the correct answers for each question.
     *
     * @return the correct answers for each question
     */
	public String[] getCorrectAnswers() {
		return correctAnswers;
	}
	/**
     * Returns the method taken for the test.
     *
     * @return the method taken
     */
	public String getMethodTaken() {
		return methodTaken;
	}
}
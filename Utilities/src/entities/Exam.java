package entities;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
/**
 * This class represents an exam.
 * It contains information such as the test ID, test name, subject, course, test number, duration, descriptions, author details, questions, and scores.
 * Implements Serializable to support serialization.
 */
public class Exam implements Serializable 
{
    private static final long serialVersionUID = 3L;
    private String testID;
    private String testName;
    private Subject subject;
    private Course course;
    private String testNumber;
    private Duration duration;
    private String teacherDescription;
    private String studentDescription;
    private String authorName;
    private String authorID;
    private ArrayList<Question> questions;
    private ArrayList<String> questionsScores;
    private String lecturerUserName =null; // when adding exam need to save the userName who added that exam
    /**
     * Constructs an Exam object with the specified details.
     *
     * @param testID the test ID
     * @param testName the test name
     * @param subject the subject
     * @param course the course
     * @param testNumber the test number
     * @param duration the duration of the exam
     * @param teacherDescription the description for the teacher
     * @param studentDescription the description for the student
     * @param authorName the name of the author
     * @param authorID the ID of the author
     * @param questions the list of questions
     * @param questionsScores the list of question scores
     */
    public Exam(String testID, String testName, Subject subject, Course course, String testNumber,
    		Duration duration, String teacherDescription, String studentDescription, String authorName,
                String authorID, ArrayList<Question> questions, ArrayList<String> questionsScores) {
        this.testID = testID;
        this.testName = testName;
        this.subject = subject;
        this.course = course;
        this.testNumber = testNumber;
        this.duration = duration;
        this.teacherDescription = teacherDescription;
        this.studentDescription = studentDescription;
        this.authorName = authorName;
        this.authorID = authorID;
        this.questions = questions;
        this.questionsScores = questionsScores;
    }
    /**
     * Returns the test ID.
     *
     * @return the test ID
     */
    public String getTestID() {
        return testID;
    }
    /**
     * Returns the test name.
     *
     * @return the test name
     */
    public String getTestName() {
        return testName;
    }
    /**
     * Returns the subject.
     *
     * @return the subject
     */
    public Subject getSubject() 
    {
        return subject;
    }
    /**
     * Returns the string representation of the subject.
     *
     * @return the string representation of the subject
     */
    public String getStringSubject() {
        return subject.getName();
    }
    /**
     * Returns the course.
     *
     * @return the course
     */
    public Course getCourse() {
        return course;
    }
    /**
     * Returns the string representation of the course.
     *
     * @return the string representation of the course
     */
    public String getCourseName() {
        return course.getName();
    }
    /**
     * Returns the test number.
     *
     * @return the test number
     */
    public String getTestNumber() {
        return testNumber;
    }
    /**
     * Returns the duration of the exam.
     *
     * @return the duration of the exam
     */
    public Duration	 getDuration() {
        return duration;
    }
    /**
     * Returns the teacher description.
     *
     * @return the teacher description
     */
    public String getTeacherDescription() {
        return teacherDescription;
    }
    /**
     * Returns the student description.
     *
     * @return the student description
     */
    public String getStudentDescription() {
        return studentDescription;
    }
    /**
     * Returns the author name.
     *
     * @return the author name
     */
    public String getAuthorName() {
        return authorName;
    }
    /**
     * Returns the author ID.
     *
     * @return the author ID
     */
    public String getAuthorID() {
        return authorID;
    }
    /**
     * Returns the list of questions.
     *
     * @return the list of questions
     */
    public ArrayList<Question> getQuestions() {
        return questions;
    }
    /**
     * Returns the list of question scores.
     *
     * @return the list of question scores
     */
    public ArrayList<String> getQuestionsScores() {
        return questionsScores;
    }
    /**
     * Returns the lecturer's username.
     *
     * @return the lecturer's username
     */
	public String getLecturerUserName() 
	{
		return lecturerUserName;
	}
	/**
     * Sets the lecturer's username.
     *
     * @param lecturerUserName the lecturer's username to set
     */
	public void setLecturerUserName(String lecturerUserName) 
	{
		this.lecturerUserName = lecturerUserName;
	}
	/**
     * Checks if this Exam object is equal to another object.
     * The equality is determined based on the test ID.
     *
     * @param other the other object to compare
     * @return true if the objects are equal, false otherwise
     */
	@Override
	public boolean equals(Object other) // check equals by testid
	{
		if( !( other instanceof Exam ))
			return false;
		Exam otherExam = (Exam)other;
		return this.testID.equals(otherExam.getTestID());
	}
	/**
     * Returns the duration of the exam in time format (HH:MM:SS).
     *
     * @return the duration of the exam in time format
     */
	public String getDurationTimeFormat() 
	{
	    long seconds = duration.getSeconds();
	    long hours = seconds / 3600;
	    long minutes = (seconds % 3600) / 60;
	    seconds = seconds % 60;
	    
	    String formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
	    return formattedDuration;
	}
}
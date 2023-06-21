package entities;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;

import utilities.CONSTANTS;

/**
 * The ConductExam class represents a conducted exam. It implements the
 * Serializable interface to support object serialization.
 */
public class ConductExam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int conduct_exam_id;
	private String password;
	private String test_id;
	private int is_open = 0;// can be 0 - not done 1 - at conduct or 2 - test was conducted and now is done
	private String conduct_exam_name;
	private String lecturer_user_name;
	private Duration duration;
	private ArrayList<User> registeredStudents =null;
	private Exam examToConduct = null;
	private String hasRequest;
	private String requestStatus; // can be Pending Approved Denied or null if hasRequest = not_requested
	private Duration timeExtension;

	/**
	 * Constructs a ConductExam object with the specified parameters.
	 *
	 * @param conduct_exam_id    the ID of the conducted exam
	 * @param password           the password for the exam
	 * @param test_id            the ID of the test associated with the exam
	 * @param is_open            the status of the exam (0 - not done, 1 - in
	 *                           progress, 2 - completed)
	 * @param conduct_exam_name  the name of the conducted exam
	 * @param lecturer_user_name the username of the lecturer conducting the exam
	 */
	public ConductExam(int conduct_exam_id, String password, String test_id, int is_open, String conduct_exam_name,
			String lecturer_user_name, Duration duration) {
		super();
		this.conduct_exam_id = conduct_exam_id;
		this.password = password;
		this.test_id = test_id;
		this.is_open = is_open;
		this.conduct_exam_name = conduct_exam_name;
		this.lecturer_user_name = lecturer_user_name;
		this.duration = duration;
		requestStatus=CONSTANTS.not_requested; // assign not requested at start
		requestStatus=CONSTANTS.None; // no status exist about the request
		if(duration == null)
			timeExtension=Duration.ofHours(0).plusMinutes(0).plusSeconds(0); // duration extension time is 00:00:00
		else
			timeExtension = duration;
	}

	/**
	 * Returns the status of the exam.
	 *
	 * @return the status of the exam (0 - not done, 1 - in progress, 2 - completed)
	 */
	public int getIs_open() {
		return is_open;
	}

	/**
	 * Sets the status of the exam.
	 *
	 * @param is_open the status of the exam to set (0 - not done, 1 - in progress,
	 *                2 - completed)
	 */
	public void setIs_open(int is_open) {
		this.is_open = is_open;
	}

	/**
	 * Returns the ID of the conducted exam.
	 *
	 * @return the ID of the conducted exam
	 */
	public int getConduct_exam_id() {
		return conduct_exam_id;
	}

	/**
	 * Returns the password for the exam.
	 *
	 * @return the password for the exam
	 */
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the ID of the test associated with the exam.
	 *
	 * @return the ID of the test associated with the exam
	 */
	public String getTest_id() {
		return test_id;
	}

	/**
	 * Returns the name of the conducted exam.
	 *
	 * @return the name of the conducted exam
	 */
	public String getConduct_exam_name() {
		return conduct_exam_name;
	}
	/**
	 * Sets the conduct_exam_name of the conducted exam.
	 *
	 * @param input the conduct_exam_name the conducted exam.
	 */
	public void setConduct_exam_name(String conduct_exam_name) {
		this.conduct_exam_name = conduct_exam_name;
	}

	/**
	 * Returns the username of the lecturer conducting the exam.
	 *
	 * @return the username of the lecturer conducting the exam
	 */
	public String getLecturer_user_name() {
		return lecturer_user_name;
	}

	/**
	 * Sets the username of the lecturer conducting the exam.
	 *
	 * @param input the username of the lecturer conducting the exam
	 */
	public void setLecturer_user_name(String lecturer_user_name) {
		this.lecturer_user_name = lecturer_user_name;
	}
	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	
	/**
	 * Returns a string representation of the conducted exam.
	 *
	 * @return a string representation of the conducted exam
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
	/**
	 * Returns the time extension for the exam.
	 *
	 * @return the time extension
	 */
	public Duration getTimeExtension()
	{
		return timeExtension;
	}
	/**
	 * Returns the formatted string representation of the time extension.
	 *
	 * @return the formatted time extension
	 */
	public String getTimeExtensionStringFormat()
	{
	    long seconds = timeExtension.getSeconds();
	    long hours = seconds / 3600;
	    long minutes = (seconds % 3600) / 60;
	    seconds = seconds % 60;
	    
	    String formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
	    return formattedDuration;
	}
	/**
	 * Sets the time extension for the exam.
	 *
	 * @param timeExtension the time extension to set
	 */
	public void setTimeExtension(Duration timeExtension)
	{
		this.timeExtension=timeExtension;
	}
	/**
	 * Returns the exam to conduct.
	 *
	 * @return the exam to conduct
	 */
	public Exam getExamToConduct()
	{
		return this.examToConduct;
	}
	
	/**
	 * Sets the exam to conduct.
	 *
	 * @param examToConduct the exam to conduct
	 */
	public void setExamToConduct(Exam examToConduct)
	{
		this.examToConduct=examToConduct;
	}
	/**
	 * Returns the list of registered students.
	 *
	 * @return the list of registered students
	 */
	
	public ArrayList<User> getRegisteredStudents()
	{
		return this.registeredStudents;
	}
	/**
	 * Sets the list of registered students.
	 *
	 * @param registeredStudents the list of registered students
	 */
	public void setRegisteredStudents(ArrayList<User> registeredStudents)
	{
		this.registeredStudents=registeredStudents;
	}
	/**
	 * Returns the request status.
	 *
	 * @return the request status
	 */

    public String getHasRequest() 
    {
        return hasRequest;
    }
    /**
     * Sets the request status.
     *
     * @param requestStatus the request status to set
     */
    public void setHasRequest(String requestStatus) 
    {
        this.hasRequest = requestStatus;
    }
    /**
     * Returns the request status.
     *
     * @return the request status
     */
    public String getRequestStatus() 
    {
        return requestStatus;
    }
    /**
     * Sets the request status.
     *
     * @param requestStatus the request status to set
     */

    public void setRequestStatus(String requestStatus) 
    {
        this.requestStatus = requestStatus;
    }
    /**
     * Checks if this ConductExam object is equal to another object.
     *
     * @param other the other object to compare
     * @return true if the objects are equal, false otherwise
     */
	@Override
	public boolean equals(Object other)
	{
		 if (other == this) {
	            return true;
	        }
		 
		if(!(other instanceof ConductExam))
			return false;
		return this.conduct_exam_id== ((ConductExam)other).getConduct_exam_id();
	}
	
	/**
	 * Returns a string representation of the conducted exam.
	 *
	 * @return a string representation of the conducted exam
	 */
	@Override
	public String toString() {
		return conduct_exam_name;
	}
}
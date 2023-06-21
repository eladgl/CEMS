package entities;

import java.io.Serializable;
/**
*
*The StudentsExamDataGetter class represents the data of a student's exam.
*It is a serializable class that stores the subject code and username of the student.
*/
public class StudentsExamDataGetter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5393171045538194101L;
	
	private String subject_code;
	private String username;
	/**
	 * Constructs a StudentsExamDataGetter object with the specified subject code and username.
	 *
	 * @param subject_code the subject code of the exam
	 * @param username the username of the student
	 */
	public StudentsExamDataGetter(String subject_code, String username) {
		super();
		this.subject_code = subject_code;
		this.username = username;
	}
	/**
	 * Retrieves the subject code of the exam.
	 *
	 * @return the subject code
	 */
	public String getSubject() {
		return subject_code;
	}
	/**
	 * Sets the subject code of the exam.
	 *
	 * @param subject_code the subject code to set
	 */
	public void setSubject(String subject_code) {
		this.subject_code = subject_code;
	}
	/**
	 * Retrieves the username of the student.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * Sets the username of the student.
	 *
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
}

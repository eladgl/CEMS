package entities;

import java.io.Serializable;
/**
 * This class represents a grade for an exam.
 * It contains information such as the username, exam ID, course, and grade value.
 * Implements Serializable to support serialization.
 */
public class Grade implements Serializable {

	private static final long serialVersionUID = 3L;
	private String username;
	private String conductExamId;
	private String course;
	private int grade;
	/**
     * Constructs a Grade object with the specified details.
     *
     * @param username the username associated with the grade
     * @param examId the ID of the exam
     * @param course the course name
     * @param grade the grade value
     */
	public Grade(String username, String conductExamId, String course, int grade) {
		super();
		this.username = username;
		this.conductExamId = conductExamId;
		this.course = course;
		this.grade = grade;
	}
	/**
     * Returns the username associated with the grade.
     *
     * @return the username
     */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
     * Returns the ID of the exam.
     *
     * @return the exam ID
     */
	public String getUsername() {
		return username;
	}
	/**
     * Returns the course name.
     *
     * @return the course name
     */
	public String getConductExamId() {
		return conductExamId;
	}
	/**
     * Returns the course name.
     *
     * @return the course name
     */
	public String getCourse() {
		return course;
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
     * Checks if this Grade object is equal to another object.
     * The equality is determined based on the exam ID and username.
     *
     * @param other the other object to compare
     * @return true if the objects are equal, false otherwise
     */
	@Override
	public boolean equals(Object other) // check equals by testId and username
	{
		if (!(other instanceof Grade))
			return false;
		Grade otherGrade = (Grade) other;
		return this.conductExamId.equals(otherGrade.getConductExamId()) && this.username.equals(otherGrade.getUsername());
	}

}
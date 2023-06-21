package entities;

import java.io.Serializable;
import java.util.ArrayList;
/**
*
*The Subject class represents a subject in an educational context.
*It is a serializable class that stores information about the subject, such as its name, code, and associated courses.
*/
public class Subject implements Serializable 
{
    private static final long serialVersionUID = 1L;
	private ArrayList<Course> courses;
	private String subjectName,
				   subjectCode;
	/**
	 * Constructs a Subject object with the specified subject name, subject code, and associated courses.
	 *
	 * @param subjectName the name of the subject
	 * @param subjectCode the code of the subject
	 * @param courses the list of associated courses
	 */
	public Subject(String subjectName,String subjectCode, ArrayList<Course> courses)
	{
		this.subjectName=subjectName;
		this.subjectCode=subjectCode;
		this.courses=courses;
	}
	/**
	 * Retrieves the name of the subject.
	 *
	 * @return the subject name
	 */
	public String getName()
	{
		return subjectName;
	}
	/**
	 * Retrieves the code of the subject.
	 *
	 * @return the subject code
	 */
	public String getCode()
	{
		return subjectCode;
	}
	/**
	 * Retrieves the list of associated courses.
	 *
	 * @return the list of courses
	 */
	public ArrayList<Course> getCourses()
	{
		return courses;
	}
	/**
	 * Sets the list of associated courses.
	 *
	 * @param courses the list of courses to set
	 */
	public void setCourses(ArrayList<Course> courses)
	{
		this.courses=courses ;
	}
	/**
	 * Returns a string representation of the Subject object.
	 *
	 * @return the subject name as a string
	 */
    @Override
    public String toString () 
    { 
    	return subjectName;
    }
	
}

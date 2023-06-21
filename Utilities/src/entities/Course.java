package entities;

import java.io.Serializable;
/**
 * This class represents a course.
 * It contains information such as the course name and course code.
 * Implements Serializable to support serialization.
 * 
 */
public class Course implements Serializable 
{
    private static final long serialVersionUID = 1L;

    private String courseName;
    private String courseCode;
    /**
     * Constructs a Course object with the specified course name and course code.
     *
     * @param courseName the name of the course
     * @param courseCode the code of the course
     */
    public Course(String courseName, String courseCode) {
        this.courseName = courseName;
        this.courseCode = courseCode;
    }
    /**
     * Returns the name of the course.
     *
     * @return the name of the course
     */
    public String getName() {
        return courseName;
    }
    /**
     * Returns the code of the course.
     *
     * @return the code of the course
     */
    public String getCode() {
        return courseCode;
    }
    /**
     * Checks if this Course object is equal to another object.
     *
     * @param other the other object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object other)
    {
    	if(!(other instanceof Course))
    		return false;
    	Course otherCourse = (Course)other;
    	return this.courseName.equals(otherCourse.getName());
    }
    /**
     * Returns the string representation of the course.
     *
     * @return the string representation of the course
     */
    @Override
    public String toString () 
    { 
    	return courseName;
    }
}
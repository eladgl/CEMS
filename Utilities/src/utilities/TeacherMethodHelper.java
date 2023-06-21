package utilities;

import java.time.Duration;
import java.util.ArrayList;

import entities.Course;
import entities.Exam;
import entities.Question;
import entities.Subject;
/**
 * The TeacherMethodHelper class provides utility methods for filtering questions and exams based on courses, subjects, and other operations.
 */
public class TeacherMethodHelper 
{
	/**
     * Filters the given list of questions based on the provided list of courses.
     *
     * @param courses   the list of courses to filter by
     * @param questions the list of questions to filter
     */
	public static void filterQuestionByCourses(ArrayList<Course> courses,ArrayList<Question> questions) 
	{
		int initialSize = questions.size();
		for (int i = 0; i < initialSize; i++)  // filter all the questions that dont have relevant courses this lecturer teach
		{
		    Question question = questions.get(i);
		    boolean toDelete = true;
		    for (Course course : courses) 
		    {
		        if (question.getCourseName().contains(course.getName())) 
		        {
		            toDelete = false;
		            break;
		        }
		    }
		    if (toDelete) {
		    	questions.remove(i);
		        i--; // Decrement the loop counter since the list size has decreased
		        initialSize--; // Update the initial size of the list
		    }
		}
	}
	/**
     * Filters the given list of exams based on the provided subject.
     *
     * @param subject the subject to filter by
     * @param exams   the list of exams to filter
     */
	public static void filterExamsBySubject(Subject subject,ArrayList<Exam> exams) 
	{
		int initialSize = exams.size();
		for (int i = 0; i < initialSize; i++)  // filter all the exams that dont have relevant subject 
		{
		    Exam exma = exams.get(i);
		    if(!exma.getSubject().getName().equals(subject.getName())) 
		    {
		    	exams.remove(i);
		        i--; // Decrement the loop counter since the list size has decreased
		        initialSize--; // Update the initial size of the list
		    }
		}
	}
	/**
     * Filters the given list of exams based on the provided list of courses.
     *
     * @param courses the list of courses to filter by
     * @param exams   the list of exams to filter
     */
	public static void filterExamsByCourses(ArrayList<Course> courses,ArrayList<Exam> exams) 
	{
		int initialSize = exams.size();
		for (int i = 0; i < initialSize; i++)  // filter all the exams that dont have relevant courses this lecturer teach
		{
		    Exam exam = exams.get(i);
		    boolean toDelete = true;
		    for (Course course : courses) 
		    {
		        if (exam.getCourse().getName().equals(course.getName())) 
		        {
		            toDelete = false;
		            break;
		        }
		    }
		    if (toDelete) 
		    {
		    	exams.remove(i);
		        i--; // Decrement the loop counter since the list size has decreased
		        initialSize--; // Update the initial size of the list
		    }
		}
	}
	/**
     * Checks if the provided string represents a positive number.
     *
     * @param number the string to check
     * @return true if the string represents a positive number, false otherwise
     */
	public static boolean isPositiveNumber(String number) {
		if (number != null && !number.isEmpty()) 
	    {	
	        // Check if the string contains only numbers
	        return number.matches("\\d+");
	    }
	    return false;
	}
	/**
     * Converts a duration string in the format "HH:mm:ss" to a Duration object.
     *
     * @param durationString the duration string to convert
     * @return the Duration object representing the duration time
     */
	public static Duration convertDurationStringToDurationTime(String durationString) 
	{
		String[] components = durationString.split(":");
		 int hours = Integer.parseInt(components[0]);
		 int minutes = Integer.parseInt(components[1]);
		 int seconds = Integer.parseInt(components[2]);
		 // Calculate the total duration in seconds
		 long totalSeconds = (hours * 3600) + (minutes * 60) + seconds;
		 Duration duration = Duration.ofSeconds(totalSeconds);
		return duration;
	}
}
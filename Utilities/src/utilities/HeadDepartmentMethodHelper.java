package utilities;

import java.util.ArrayList;
import java.util.HashSet;

import entities.Course;
import entities.Exam;
import entities.Question;
import entities.Subject;
/**
 * The HeadDepartmentMethodHelper class provides utility methods for filtering and manipulating
 * data related to courses, exams, questions, subjects, etc.
 */
public class HeadDepartmentMethodHelper 
{
	/**
     * Filters the given list of questions based on the specified list of courses.
     * Removes questions that are not relevant to any of the courses.
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
     * Filters the given list of exams based on the specified subject.
     * Removes exams that are not related to the subject.
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
     * Filters the given list of exams based on the specified list of courses.
     * Removes exams that are not related to any of the courses.
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
     * Removes duplicate subjects from the given array.
     *
     * @param subjects the array of subjects to remove duplicates from
     * @return the array of subjects without duplicate entries
     */
	public static Subject[] removeSubjectDuplicates(Subject[] subjects) {
	    HashSet<String> uniqueNames = new HashSet<>();
	    Subject[] result = new Subject[subjects.length];
	    int index = 0;

	    for (Subject subject : subjects) {
	        String name = subject.getName();
	        if (!uniqueNames.contains(name)) {
	            uniqueNames.add(name);
	            result[index] = subject;
	            index++;
	        }
	    }

	    // Create a new array without null values (if any)
	    Subject[] finalResult = new Subject[index];
	    System.arraycopy(result, 0, finalResult, 0, index);

	    return finalResult;
	}
	/**
     * Removes duplicate course names from the given array of subjects.
     *
     * @param subjects the array of subjects containing courses
     * @return the array of unique course names
     */
	public static String[] removeCourseDuplicates(Subject[] subjects) {
	    HashSet<String> uniqueNames = new HashSet<>();
	    //Subject[] result = new Subject[subjects.length];
	    String[] result = new String[subjects.length];
	    int index = 0;

	    for (Subject subject : subjects) {
	        ArrayList<Course> course = subject.getCourses();
	        String name = course.get(0).getName();
	        if (!uniqueNames.contains(name)) {
	            uniqueNames.add(name);
	            result[index] = name;
	            index++;
	        }
	    }

	    // Create a new array without null values (if any)
	    String[] finalResult = new String[index];
	    System.arraycopy(result, 0, finalResult, 0, index);

	    return finalResult;
	}
	/**
     * Filters the given list of exams based on the specified course name and subject.
     * Returns a new list of exams that match the specified criteria.
     *
     * @param allExams   the list of all exams
     * @param courseName the course name to filter by ("All Courses" to include all courses)
     * @param subject    the subject to filter by ("All Subjects" to include all subjects)
     * @return the filtered list of exams
     */
	public static ArrayList<Exam> filterExamsByCourseAndSubject(ArrayList<Exam> allExams, String courseName, String subject) {
	    ArrayList<Exam> filteredExams = new ArrayList<Exam>();
	    
        if (courseName.equals("All Courses") && subject.equals("All Subjects")) 
            return allExams;
        
	    for (Exam exam : allExams) {

	        if (courseName.equals("All Courses")) 
	        {
	            if (exam.getStringSubject().equals(subject))
	                filteredExams.add(exam);
	        }
	        
	        else {
	            if (exam.getCourseName().equals(courseName) && (exam.getStringSubject().equals(subject) || subject.equals("All Subjects"))) {
	                filteredExams.add(exam);

	            }
	        }
	    }
	    return filteredExams;
	}
}

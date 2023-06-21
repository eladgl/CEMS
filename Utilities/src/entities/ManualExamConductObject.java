package entities;

import java.io.File;
import java.io.Serializable;
/**
 * This class represents a manual exam conduct object.
 * It contains information such as the student ID, conduct exam ID, and the word file solution.
 * Implements Serializable to support serialization.
 */
public class ManualExamConductObject  implements Serializable{
	
	private static final long serialVersionUID = 5L;
	private String student_id;
    private int conduct_exam_id;
    private File word_file_solution;
    /**
     * Constructs a ManualExamConductObject with the specified details.
     *
     * @param studentId the student ID
     * @param conductExamId the conduct exam ID
     * @param wordFileSolution the word file solution
     */
    public ManualExamConductObject(String studentId, int conductExamId, File wordFileSolution) {
        this.student_id = studentId;
        this.conduct_exam_id = conductExamId;
        this.word_file_solution = wordFileSolution;
    }
    /**
     * Returns the student ID.
     *
     * @return the student ID
     */
    public String getStudentId() {
        return student_id;
    }
    /**
     * Sets the student ID.
     *
     * @param studentId the student ID
     */
    public void setStudentId(String student_id) {
        this.student_id = student_id;
    }
    /**
     * Returns the conduct exam ID.
     *
     * @return the conduct exam ID
     */
    public int getConductExamId() {
        return conduct_exam_id;
    }
    /**
     * Sets the conduct exam ID.
     *
     * @param conductExamId the conduct exam ID
     */
    public void setConductExamId(int conduct_exam_id) {
        this.conduct_exam_id = conduct_exam_id;
    }
    /**
     * Returns the word file solution.
     *
     * @return the word file solution
     */
    public File getWordFileSolution() {
        return word_file_solution;
    }
    /**
     * Sets the word file solution.
     *
     * @param wordFileSolution the word file solution
     */
    public void setWordFileSolution(File word_file_solution) {
        this.word_file_solution = word_file_solution;
    }
}
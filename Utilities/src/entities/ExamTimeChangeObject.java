package entities;

import java.io.Serializable;

/**
 * This class represents an object used to change the exam time.
 * It contains information such as the initial time in seconds, conduct exam ID, and exam type.
 * Implements Serializable to support serialization.
 */
public class ExamTimeChangeObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6L;
	
	private int init_time_in_seconds;
    private int conduct_exam_id;
    private ExamType exam_type;
    /**
     * Constructs an ExamTimeChangeObject with the specified details.
     *
     * @param init_time_in_seconds the initial time in seconds
     * @param exam_id the conduct exam ID
     * @param exam_type the exam type
     */
    public ExamTimeChangeObject(int init_time_in_seconds, int exam_id, ExamType exam_type) {
        this.init_time_in_seconds = init_time_in_seconds;
        this.conduct_exam_id = exam_id;
        this.exam_type = exam_type;
    }
    /**
     * Returns the exam type.
     *
     * @return the exam type
     */
    public ExamType getExam_type() {
		return exam_type;
	}
    /**
     * Sets the exam type.
     *
     * @param exam_type the exam type to set
     */
	public void setExam_type(ExamType exam_type) {
		this.exam_type = exam_type;
	}
	/**
     * Returns the initial time in seconds.
     *
     * @return the initial time in seconds
     */
	public int getInitTime() {
        return init_time_in_seconds;
    }
	/**
     * Sets the initial time in seconds.
     *
     * @param init_time_in_seconds the initial time in seconds to set
     */
    public void setInitTime(int init_time_in_seconds) {
        this.init_time_in_seconds = init_time_in_seconds;
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
     * @param exam_id the conduct exam ID to set
     */
    public void setConductExamId(int exam_id) {
        this.conduct_exam_id = exam_id;
    }
    /**
     * Returns the string representation of the ExamTimeChangeObject.
     *
     * @return the string representation of the ExamTimeChangeObject
     */
    @Override
	public String toString() {
		return "ExamTimeChangeObject [init_time_in_seconds=" + init_time_in_seconds + ", exam_id=" + conduct_exam_id + "]";
	}

}

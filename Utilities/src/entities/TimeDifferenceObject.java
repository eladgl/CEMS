package entities;

import java.io.Serializable;
/**
 * The TimeDifferenceObject class represents an object that holds the time difference in seconds and the exam type.
 * It implements the Serializable interface for object serialization.
 */
public class TimeDifferenceObject implements Serializable{

	private static final long serialVersionUID = 40L;
	
	private int time_difference_in_seconds;
	private ExamType exam_type;
	/**
     * Constructs a TimeDifferenceObject with the specified time difference in seconds and exam type.
     *
     * @param timeDifferenceInSeconds the time difference in seconds
     * @param examType                the exam type
     */
	public TimeDifferenceObject(int time_difference_in_seconds, ExamType exam_type) {
		this.time_difference_in_seconds = time_difference_in_seconds;
		this.exam_type = exam_type;
	}
	/**
     * Returns the time difference in seconds.
     *
     * @return the time difference in seconds
     */
	public int getTime_difference_in_seconds() {
		return time_difference_in_seconds;
	}
	/**
     * Sets the time difference in seconds.
     *
     * @param timeDifferenceInSeconds the time difference in seconds
     */
	public void setTime_difference_in_seconds(int time_difference_in_seconds) {
		this.time_difference_in_seconds = time_difference_in_seconds;
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
     * @param examType the exam type
     */
	public void setExam_type(ExamType exam_type) {
		this.exam_type = exam_type;
	}
}
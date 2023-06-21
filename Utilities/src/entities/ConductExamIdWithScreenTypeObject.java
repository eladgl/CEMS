package entities;

import java.io.Serializable;

/**
 * This Method Is only Used By the Manual / Computerized Exam Controllers To Check If the exam Has been locked.
 * @author razi
 *
 */
public class ConductExamIdWithScreenTypeObject implements Serializable {
	private static final long serialVersionUID = 30L;
	private int conduct_exam_id;
	private ExamType exam_type;
	/**
     * Constructs a ConductExamIdWithScreenTypeObject with the specified conduct exam ID and exam type.
     *
     * @param conduct_exam_id the conduct exam ID
     * @param exam_type the exam type
     */
	public ConductExamIdWithScreenTypeObject(int conduct_exam_id, ExamType exam_type) {
		this.conduct_exam_id = conduct_exam_id;
		this.exam_type = exam_type;
	}
	/**
     * Returns the conduct exam ID.
     *
     * @return the conduct exam ID
     */
	public int getConduct_exam_id() {
		return conduct_exam_id;
	}
	/**
     * Sets the conduct exam ID.
     *
     * @param conduct_exam_id the conduct exam ID to set
     */
	public void setConduct_exam_id(int conduct_exam_id) {
		this.conduct_exam_id = conduct_exam_id;
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
}
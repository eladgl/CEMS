package entities;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents an answered exam. 
 *
 * An AnsweredExam object stores information about the score, answered questions, wrong answers,
 * correct answers, and the original exam.
*/
public class AnsweredExam{

	private int score = 0;
	private List<Integer> answeredQuestions = new ArrayList<>();
	private List<Question> wrongAnswers = new ArrayList<>();
	private List<Question> correctAnswers = new ArrayList<>();
	private Exam exam;
	/**
	 * Constructs an AnsweredExam object with the specified exam and answered questions.
	 * 
	 * @param exam             the original Exam object
	 * @param answeredQuestions a list of answered question indices
	 */
	public AnsweredExam(Exam exam, List<Integer> answeredQuestions) {
		this.exam = exam;
		this.answeredQuestions = answeredQuestions;
	}
	
	/**
	 * Sets the score of the answered exam.
	 * 
	 * @param points the score to set
	 */
	public void setScore(int points) {
		score = points;
	}
	/**
	 * Adds points to the score of the answered exam.
	 * 
	 * @param points the points to add to the score
	 */
	public void addScore(int points) {
		score += points;
	}
	/**
	 * Retrieves the list of wrong answers.
	 * 
	 * @return a List of Question objects representing the wrong answers
	 */
	public int getScore() {
		return score;
	}
	/**
	 * Adds a wrong answer to the list of wrong answers.
	 * If the question is already present, it will not be added again.
	 * 
	 * @param q the Question object representing the wrong answer
	 */
	public List<Question> getWrongAnswers(){
		return wrongAnswers;
	}
	/**
	 * Adds a correct answer to the list of correct answers.
	 * If the question is already present, it will not be added again.
	 * 
	 * @param q the Question object representing the correct answer
	 */
	public List<Question> getCorrectAnswers(){
		return correctAnswers;
	}
	/**
	 * Adds a wrong answer to the list of wrong answers.
	 * If the question is already present, it will not be added again.
	 * 
	 * @param q the Question object representing the wrong answer
	 */
	public void addWrongAnswer(Question q) {
		if(wrongAnswers == null) {
			wrongAnswers.add(q);
		}
		else {
			//check if already inside
			for(Question q2 : wrongAnswers) {
				if(q2.equals(q)) {
					return;
				}
			}
			wrongAnswers.add(q);
		}
	}
	/**
	 * Adds a correct answer to the list of correct answers.
	 * If the question is already present, it will not be added again.
	 * 
	 * @param q the Question object representing the correct answer
	 */
	public void addCorrectAnswer(Question q) {
		if(correctAnswers == null) {
			correctAnswers.add(q);
		}
		else {
			//check if already inside
			for(Question q2 : correctAnswers) {
				if(q2.equals(q)) {
					return;
				}
			}
			correctAnswers.add(q);
		}
	}	
}
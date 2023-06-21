package entities;

/**
 * Represents a bank of questions.
 * 
 * 
 * The {@code BankQuestions} class stores information about the bank name and
 * the subject of the questions.
 */
public class BankQuestions {
	private final String bankName;
	private final String subject;

	/**
	 * Constructs a {@code BankQuestions} object with the specified bank name and
	 * subject.
	 * 
	 * @param bankName the name of the question bank
	 * @param subject  the subject of the questions
	 */
	public BankQuestions(String bankName, String subject) {
		super();
		this.bankName = bankName;
		this.subject = subject;
	}

}

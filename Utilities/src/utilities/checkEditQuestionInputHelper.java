package utilities;

/**
 * The checkEditQuestionInputHelper class provides helper methods to validate input for editing questions.
 */
public class checkEditQuestionInputHelper 
{
	/**
     * Checks if the given question ID is valid.
     *
     * @param ID the question ID to check
     * @return true if the ID is valid, false otherwise
     */
	public static boolean ValidId(String ID) // check the given question id if valid
	{
		if(ID.length()!=5) // must be 5 characters
			return false;
		System.out.println(ID);
		return ID.matches("\\d+"); // this function check if all the characters are numbers return true if so false else
	}
	/**
     * Checks if the given question number is valid.
     *
     * @param Qnumber the question number to check
     * @return true if the number is valid, false otherwise
     */
	public static boolean ValidQuestionNumber(String Qnumber) 
	{
	    if (Qnumber.length() != 3) { // must be 3 characters
	        return false;
	    }
	    if (Qnumber.equals("000")) { // check if Qnumber is zero
	        return false;
	    }
	    return Qnumber.matches("\\d+"); // check if all characters are numbers
	}
}
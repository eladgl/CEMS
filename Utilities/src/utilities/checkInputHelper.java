package utilities;
/**
 * The checkInputHelper class provides helper methods to check valid inputs for server connections.
 */
public class checkInputHelper {
	/**
	 * Checks if the input is a valid integer between the specified minimum and maximum values.
	 * This method is used for IP address and port fields.
	 *
	 * @param o       the input object to check
	 * @param minVal  the minimum allowed value (inclusive)
	 * @param maxVal  the maximum allowed value (inclusive)
	 * @return true if the input is a valid integer within the specified range, false otherwise
	 */
	public static boolean checkInput(Object o, int minVal, int maxVal) {
		if(o instanceof String) {
			String ip = (String) o;
			try {
				int value = Integer.parseInt(ip);
				if(value < minVal || value > maxVal)
					return false;
			} catch(NumberFormatException e) {
				return false;
			}
			return true;
		}
		else
			return false;
	}
	/**
	 * Validates the text by checking if it is not null, not empty, and does not exceed the maximum length.
	 *
	 * @param text       the text to validate
	 * @param max_length the maximum allowed length of the text
	 * @return true if the text is valid, false otherwise
	 */
	public static boolean validateText(String text, int max_length) {
	    if (text == null || text.isEmpty() || text.length() > max_length)
	        return false;
	    return true;
	}
}
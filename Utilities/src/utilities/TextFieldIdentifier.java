package utilities;

/**
 * The TextFieldIdentifier enum class is used to help and get the values for each value if entered in the GUI.
 */
public enum TextFieldIdentifier {
	/**
     * Represents the IP correctness value.
     */
	ip_correct(0),
	/**
     * Represents the port correctness value.
     */
	port_correct(1),
	/**
     * Represents the database URL correctness value.
     */
	db_url_correct(2),
	/**
     * Represents the username correctness value.
     */
	username_correct(3),
	/**
     * Represents the password correctness value.
     */
	password_correct(4);

    private int value;
    /**
     * Constructs a TextFieldIdentifier with the specified value.
     *
     * @param value the value associated with the TextFieldIdentifier
     */
    private TextFieldIdentifier(int value) {
        this.value = value;
    }
    /**
     * Returns the value associated with the TextFieldIdentifier.
     *
     * @return the value of the TextFieldIdentifier
     */
    public int getValue() {
        return value;
    }
}
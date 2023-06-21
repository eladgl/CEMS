package entities;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
/**
 * Represents an SMS message.
 */
public class SMSMessage {
	private String message;
	private String timestamp;
	/**
     * Constructs an SMSMessage object with the specified message and timestamp.
     *
     * @param message   the content of the SMS message
     * @param timestamp the timestamp of the SMS message
     */
	public SMSMessage(String message, String timestamp) {
		this.message = message;
		this.timestamp = timestamp;
	}
	/**
     * Constructs an SMSMessage object with the specified message and the current timestamp.
     *
     * @param message the content of the SMS message
     */
	public SMSMessage(String message) {
		LocalTime currentTime = LocalTime.now(); // Get the current time
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // Define the desired time format
		this.timestamp = currentTime.format(formatter); // Format the current time as a string
		this.message = message;

	}

	/**
     * Retrieves the content of the SMS message.
     *
     * @return the content of the SMS message
     */
	public String getMessage() {
		return message;
	}
	 /**
     * Sets the content of the SMS message.
     *
     * @param message the content of the SMS message
     */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
     * Retrieves the timestamp of the SMS message.
     *
     * @return the timestamp of the SMS message
     */
	public String getTimestamp() {
		return timestamp;
	}
	/**
     * Sets the timestamp of the SMS message.
     *
     * @param timestamp the timestamp of the SMS message
     */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	/**
     * Checks if this SMSMessage is equal to another object.
     * Two SMSMessage objects are considered equal if their messages are equal.
     *
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		SMSMessage other = (SMSMessage) obj;
		// Compare attributes for equality
		return Objects.equals(message, other.message);
	}
}
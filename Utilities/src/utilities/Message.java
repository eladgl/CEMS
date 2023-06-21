package utilities;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * The Message class represents a message object that can be serialized and sent over a network.
 * It contains an operation and a set of commands as its data.
 */
public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	private String operation;
	private Object commands;
	/**
     * Constructs a Message object with the specified operation and commands.
     *
     * @param operation the operation associated with the message
     * @param commands  the commands associated with the message
     */
	public Message(String operation, Object commands) {
		this.operation = operation;
		this.commands = commands;
	}
	/**
     * Returns the operation associated with the message.
     *
     * @return the operation of the message
     */
	public String getOperation() {
		return operation;
	}
	/**
     * Returns the commands associated with the message.
     *
     * @return the commands of the message
     */
	public Object getCommands() {
		return commands;
	}
	/**
     * Converts the commands to an ArrayList of the specified type.
     *
     * @param <T> the type of the elements in the commands ArrayList
     * @return an ArrayList containing the commands
     */
	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> convertCommandsToArrayList(){
		return (ArrayList<T>)this.commands;
	}
	/**
     * Creates an ArrayList with the provided parameters.
     *
     * @param <T>        the type of the elements in the ArrayList
     * @param parameters the parameters used to populate the ArrayList
     * @return an ArrayList containing the provided parameters
     */
	@SafeVarargs
	public static<T> ArrayList<T> createDataArrayList(T... parameters){
		ArrayList<T> data = new ArrayList<>();
		for(T param : parameters) {
			data.add(param);
		}
		return data;
	}
	/**
     * Returns a string representation of the Message object.
     *
     * @return a string representation of the Message object
     */
	@Override
	public String toString() {
		if(commands != null)
			return "Operation: " + operation.toString() + " Input ArrayList : " + commands.toString();
		return "Operation: " + operation.toString() + " Input ArrayList : " + "NULL";
	}	
}
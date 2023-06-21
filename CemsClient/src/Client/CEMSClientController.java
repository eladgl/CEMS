package Client;

import java.io.IOException;

/**
 * The controller class for the CEMS client.
 */
public class CEMSClientController implements ChatIF {
	/**
	 * CEMSClient instance
	 */
	public CEMSClient client;

	/**
	 * Default port to the server.
	 */
	public static int DEAFAULT_PORT;

	/**
	 * Creates a new instance of the CEMSClientController class, Also tries to
	 * connect to the server by instantiating the cient instance and running it.
	 * 
	 * @param host The hostname of the server.
	 * @param port The port number to connect to.
	 */
	public CEMSClientController(String host, int port) {
		try {
			client = new CEMSClient(host, port, this);
			try {
				client.run();
				client.openConnection();
				System.out.println("Connected to server Successfully " + client);
			} catch (Exception e) {
				e.printStackTrace();
				CEMSClient.clientController = null; // when refused connection in the constructor - the server is not on
			}

		} catch (IOException exception) {
			System.out.println("Error: Can't setup connection!" + " Terminating client.");
			System.exit(1);
		}
	}

	/**
	 * Accepts a message from the client's UI and handles it.
	 * 
	 * @param str The message received from the UI.
	 * @throws Exception if an exception occurs during message handling.
	 */

	public void accept(Object str) throws Exception {
		client.handleMessageFromClientUI(str);
	}

	/**
	 * Displays a message (prints it)
	 */

	@Override
	public void display(String message) {
		System.out.println("> " + message);
	}

}

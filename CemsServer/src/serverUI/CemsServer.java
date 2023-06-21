package serverUI;

import java.io.*;
import java.net.InetAddress;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import entities.ClientConnectionStatus;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 *
 * @author Elad Goldenberg
 * @author Raz Tibi
 * @author Razi Mograbi
 * @author Shimron Yifrah
 * @author Alex Baboshin
 * @author Shaked Oz
 * @version May 2023
 */
public class CemsServer extends AbstractServer {
	/**
	 * The default port to listen on.
	 */

	// private String username, password;
	private String db_url, db_name, db_password;

	/**
	 * The controller for the database. Provides access to SQL methods and
	 * establishes a connection to the database when an instance is created.
	 */
	public static DBController DBcontroller = null;
	ArrayList<User> userList = new ArrayList<User>();
	ArrayList<String> mathQbanks = new ArrayList<String>(); // exsisting Qbanks in math

	/**
	 * Observable map of client connections, where the key is the client ID and the
	 * value is the client's connection status.
	 */
	public static ObservableMap<Integer, ClientConnectionStatus> clientMap = FXCollections.observableHashMap();

	/**
	 * Observable list of client connection statuses.
	 */
	public static ObservableList<ClientConnectionStatus> clientList = FXCollections
			.observableArrayList(clientMap.values());
	/**
	 * Flag indicating whether the server is listening for connections.
	 */
	public static boolean listenflag = false;
	private static int i = 0; 

	/**
	 * Constructs a CemsServer with the specified port and database information.
	 *
	 * @param port        The port to listen on.
	 * @param db_url      The URL of the database.
	 * @param db_name     The name of the database.
	 * @param db_password The password for the database.
	 */

	public CemsServer(int port, String db_url, String db_name, String db_password) {
		super(port);
		this.db_url = db_url;
		this.db_name = db_name;
		this.db_password = db_password;
	}

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	@Override
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		try {
			MessageHandler.HandleMessage(msg, client); // for now only for cases we sent logout get a message, this is
														// because //from here
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called when the server has started successfully.
	 */
	@Override
	protected void serverStarted() {
		if (listenflag == false) {
			DBcontroller = new DBController(db_url, db_name, db_password);
			if (DBcontroller.conn != null) {
				System.out.println("Server listening for connections on port " + getPort());
				listenflag = true;
			} else {
				try {
					System.out.println("Server could not be started");
					close(); // close server because DBcontroller.conn couldn't be instantiated
					listenflag = false;
				} catch (Exception e) {
					System.out.println("Failed to close server");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * Called when a client is disconnected from the server.
	 * 
	 * @param client The disconnected client object.
	 */
	// trigger this event every time a client disconnect
	@Override
	protected void clientDisconnected(ConnectionToClient client) {
		updateClientDetails(client.getInetAddress(), client.getInetAddress().getHostName(), "Disconnected");
	}

	@Override
	protected void serverStopped() {
		try {
			if (ServerUI.cemsServer != null) {
				for (Thread client : getClientConnections()) {
					ConnectionToClient clientConn = (ConnectionToClient) client;
					clientDisconnected(clientConn);
					System.out.println(clientConn.toString() + " disconnected");
				}
				close();
				listenflag = false;
				System.out.println("server stopped listening for connections");
			}

		} catch (NullPointerException e) {
			// do nothing, its because close triggers client update, but close already
			// triggers server to stop listening and put nulls in all the clients
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ServerUI.cemsServer != null)
				ServerUI.cemsServer = null;
		}
	}

	/**
	 * 
	 * Called when a client is successfully connected to the server.
	 * 
	 * @param client The connected client object.
	 */

	@Override
	protected void clientConnected(ConnectionToClient client) {
		super.clientConnected(client);
		String ip = client.getInetAddress().getHostAddress(); // get their ip address
		String host = client.getInetAddress().getHostName(); // get their host name
		ClientConnectionStatus clientStatus = new ClientConnectionStatus(ip, host, "Connected"); // create a new object
																									// to push to
																									// observable list
		clientMap.put(i++, clientStatus);
		CemsServer.clientList.add(clientStatus);
		clientList.setAll(clientMap.values()); // Set all elements of the clientList with values from the clientMap
		System.out.println("\nClient " + client.toString() + " connected from " + ip + "/" + host + "\n");

		/* output client details */

		updateClientDetails(client.getInetAddress(), host, "Connected"); // update the cems server clients list

	}

	/**
	 * Returns the list of client connection statuses.
	 *
	 * @return The list of client connection statuses.
	 */

	public static ObservableList<ClientConnectionStatus> getClientsList() {
		return clientList;
	}

	/**
	 * Sets the list of client connection statuses.
	 *
	 * @param newClientList The new list of client connection statuses.
	 */
	public static void setClientsList(final ObservableList<ClientConnectionStatus> newClientList) {
		clientList = newClientList;
	}

	/**
	 * Sends client details to be shown in the serverBoundary UI.
	 *
	 * @param ip       The client's IP address.
	 * @param hostName The client's host name.
	 * @param username The client's username.
	 * @param status   The client's connection status.
	 */
	public static void updateClientDetails(InetAddress ip, String hostName, String status) {
		ServerUI.sb.updateConnectedClient(ip, hostName, status, getStartTime());
	}

	/**
	 * Returns the current time as a formatted string.
	 *
	 * @return The current time as a formatted string.
	 */
	// get current time method
	private static String getStartTime() {
		LocalTime currentTime = LocalTime.now();

		// Define the format of the time string
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		return currentTime.format(formatter);
	}

	/**
	 * Disconnects a client and updates the client details in the serverBoundary UI.
	 *
	 * @param ip       The client's IP address.
	 * @param hostName The client's host name.
	 * @param status   The client's connection status.
	 */

	public static void disconnectClient(InetAddress ip, String hostName, String status) {
		ServerUI.sb.disconnectClient(ip, hostName, status);

	}
}

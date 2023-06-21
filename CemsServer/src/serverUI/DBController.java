package serverUI;

import java.sql.Connection;

public class DBController 
{
	Connection conn;
	/**
     * Constructs a DBController object and establishes a connection to the database.
     *
     * @param DB_url      the URL of the database
     * @param userNameDB  the username for the database connection
     * @param passwordDB  the password for the database connection
     */
	public DBController(String DB_url,String userNameDB,String passwordDB)
	{
		conn=mysqlConnection.connectToDB(DB_url, userNameDB, passwordDB); 
	}
}

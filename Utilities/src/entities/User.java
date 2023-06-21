package entities;

import java.io.Serializable;
/**
*
*The User class represents a user in the system.
*It is a serializable class that stores information about the user, such as username, name, password, ID number, email, phone number, login type, and user permission.
*/
public class User implements Serializable
{
	private static final long serialVersionUID = 2L;
	private String user_name;
    private String last_name;
    private String first_name;
    private String password;
    private String id_number;
    private String email;
    private String phone_number;
    private String user_permission;
    private String login_type;
    private boolean is_logged;
    
    /**
     * Constructs a User object with the specified user information.
     *
     * @param userName the username of the user
     * @param lastName the last name of the user
     * @param firstName the first name of the user
     * @param password the password of the user
     * @param idNumber the ID number of the user
     * @param email the email of the user
     * @param phoneNumber the phone number of the user
     * @param isLogged a flag indicating if the user is logged in
     * @param userPermission the permission level of the user
     */
    public User(final String userName, final String lastName, final String firstName, final String password, final String iDNumber, final String email, final String phoneNumber, final boolean isLogged, final String userPermission)
    {
    	super();
        this.user_name = userName;
        this.last_name = lastName;
        this.first_name = firstName;
        this.password = password;
        this.id_number = iDNumber;
        this.email = email;
        this.phone_number = phoneNumber;
        this.is_logged = isLogged;
        this.user_permission = userPermission;
    }
    /**
    * Returns the username of the user.
    *
    * @return the username of the user
    */
    public String getUserName() {
        return this.user_name;
    }
    /**
    * Sets the username of the user.
    *
    * @param userName the username to be set
    */
    public void setUserName(final String userName) {
        this.user_name = userName;
    }
    /**
    * Returns the login type of the user.
    *
    * @return the login type of the user
    */
    public String getLoginType() {
        return this.login_type;
    }
    /**
    * Sets the login type of the user.
    *
    * @param loginType the login type to be set
    */
    public void setLoginType(final String LoginType) {
        this.login_type = LoginType;
    }
    /**
    * Returns the last name of the user.
    *
    * @return the last name of the user
    */
    public String getLastName() {
        return this.last_name;
    }
    /**
    * Sets the last name of the user.
    *
    * @param lastName the last name to be set
    */
    public void setLastName(final String lastName) {
        this.last_name = lastName;
    }
    /**
    * Returns the first name of the user.
    *
    * @return the first name of the user
    */
    public String getFirstName() {
        return this.first_name;
    }
    /**
    * Sets the first name of the user.
    *
    * @param firstName the first name to be set
    */
    public void setFirstName(final String firstName) {
        this.first_name = firstName;
    }
    /**
    * Returns the password of the user.
    *
    * @return the password of the user
    */
    public String getPassword() {
        return this.password;
    }
    /**
    * Sets the password of the user.
    *
    * @param password the password to be set
    */
    public void setPassword(final String password) {
        this.password = password;
    }
    /**
    * Returns the ID number of the user.
    *
    * @return the ID number of the user
    */
    public String getIDNumber() {
        return this.id_number;
    }
    /**
    * Sets the ID number of the user.
    *
    * @param iDNumber the ID number to be set
    */
    public void setIDNumber(final String iDNumber) {
        this.id_number = iDNumber;
    }
    /**
    * Returns the email address of the user.
    *
    * @return the email address of the user
    */
    public String getEmail() {
        return this.email;
    }
    /**
    * Sets the email address of the user.
    *
    * @param email the email address to be set
    */
    public void setEmail(final String email) {
        this.email = email;
    }
    /**
    * Returns the phone number of the user.
    *
    * @return the phone number of the user
    */
    public String getPhoneNumber() {
        return this.phone_number;
    }
    /**
    * Sets the phone number of the user.
    *
    * @param phoneNumber the phone number to be set
    */
    public void setPhoneNumber(final String phoneNumber) {
        this.phone_number = phoneNumber;
    }
    /**
    * Returns the login status of the user.
    *
    * @return the login status of the user
    */
    public boolean isLogged() {
        return this.is_logged;
    }
    /**
    * Sets the login status of the user.
    *
    * @param isLogged the login status to be set
    */
    public void setLogged(final boolean isLogged) {
        this.is_logged = isLogged;
    }
    /**
    * Returns the user permission level.
    *
    * @return the user permission level
    */
    public String getUserPermission() {
        return this.user_permission;
    }
    /**
    * Sets the user permission level.
    *
    * @param userPermission the user permission level to be set
    */
    public void setUserPermission(final String userPermission) {
        this.user_permission = userPermission;
    }
}

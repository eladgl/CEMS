package entities;

import java.io.Serializable;
import java.time.Duration;

import utilities.CONSTANTS;
/**
 * The RequestTimeExtension class represents a time extension request entity.
 * It implements the Serializable interface for object serialization.
 */
public class RequestTimeExtension implements Serializable 
{
    private static final long serialVersionUID = 3L;

    private Duration extensionDuration;
    private String requestStatus = CONSTANTS.Pending;
    private int  conductExamId;
    /**
     * Constructs a RequestTimeExtension object with the specified parameters.
     *
     * @param extensionDuration the duration of the time extension
     * @param requestStatus     the status of the request
     * @param conductExamId     the ID of the conducted exam
     */
    public RequestTimeExtension(Duration extensionDuration, String requestStatus, int conductExamId) 
    {
        this.extensionDuration = extensionDuration;
        this.requestStatus = requestStatus;
        this.conductExamId=conductExamId;
        
    }
    /**
     * Returns the duration of the time extension.
     *
     * @return the duration of the time extension
     */
    public Duration getExtensionDuration() 
    {
        return extensionDuration;
    }
    /**
     * Returns the status of the request.
     *
     * @return the status of the request
     */
    public String getRequestStatus() 
    {
        return requestStatus;
    }
    /**
     * Returns the ID of the conducted exam.
     *
     * @return the ID of the conducted exam
     */
    public int getconductExamId() 
    {
        return conductExamId;
    }
    /**
     * Sets the status of the request.
     *
     * @param requestStatus the status of the request
     */
    public void setRequestStatus(String requestStatus) 
    {
        this.requestStatus = requestStatus;
    }
    /**
     * Returns the formatted extension duration in HH:MM:SS format.
     *
     * @return the formatted extension duration
     */
	public String getStringExtensionDurationTimeFormat() 
	{
	    long seconds = extensionDuration.getSeconds();
	    long hours = seconds / 3600;
	    long minutes = (seconds % 3600) / 60;
	    seconds = seconds % 60;  
	    String formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
	    return formattedDuration;
	}
}
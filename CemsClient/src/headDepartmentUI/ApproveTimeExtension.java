package headDepartmentUI;

import java.io.File;

import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
/**
 * Represents an approval for a time extension.
 * Stores information about the time extension request and provides methods to access and modify the data.
 * is used in a table view so each field has a getter and setter
 */
public class ApproveTimeExtension {

	static int id = 0;
	private int conductExamID;
	private String conductTestName;
	private String lecturerName;
	private String timeAsked;
	private ImageView yesImg;
	private ImageView noImg;
	private CheckBox checkButton;
	/**
     * Constructs an `ApproveTimeExtension` object with the specified information.
     * Initializes the approval images and the checkbox.
     *
     * @param conductExamID   the ID of the conduct exam
     * @param conductTestName the name of the conduct test
     * @param lecturerName    the name of the lecturer
     * @param timeAsked       the requested time extension
     */
	public ApproveTimeExtension(int conductExamID, String conductTestName, String lecturerName, String timeAsked) {
		super();
		this.conductExamID = conductExamID;
		this.conductTestName = conductTestName;
		this.lecturerName = lecturerName;
		this.timeAsked = timeAsked;

		// approve time extension image
		if (this.getClass().getResourceAsStream("/images/checked.png") != null) {
			Image image = new Image(this.getClass().getResourceAsStream("/images/checked.png"));
			yesImg = new ImageView(image);
		} else {
			yesImg = new ImageView();
		}
		yesImg.getStyleClass().add("genericButtonPressed");

		yesImg.setFitWidth(20);
		yesImg.setFitHeight(20);
		yesImg.setPickOnBounds(true);
		
		// don't approve time extension image
		if (this.getClass().getResourceAsStream("/images/reject.png") != null) {
			Image image = new Image(this.getClass().getResourceAsStream("/images/reject.png"));
			noImg = new ImageView(image);
		} else {
			noImg = new ImageView();
		}
		noImg.setFitWidth(20);
		noImg.setFitHeight(20);
		noImg.setPickOnBounds(true);
		noImg.getStyleClass().add("genericButtonPressed");

		checkButton = new CheckBox();
		checkButton.getStyleClass().add("Cemscheckbox");
		checkButton.setId(Integer.toString(id++));

	}
	/**
     * Returns the image view for the "don't approve time extension" image.
     *
     * @return the "don't approve time extension" image view
     */
	public ImageView getNoImg() {
		return noImg;
	}
	/**
     * Sets the image view for the "don't approve time extension" image.
     *
     * @param noImg the "don't approve time extension" image view to set
     */
	public void setNoImg(ImageView noImg) {
		this.noImg = noImg;
	}
	/**
     * Returns the ID of the conduct exam.
     *
     * @return the conduct exam ID
     */
	public int getConductExamID() {
		return conductExamID;
	}
	/**
     * Sets the ID of the conduct exam.
     *
     * @param conductExamID the conduct exam ID to set
     */
	public void setConductExamID(int conductExamID) {
		this.conductExamID = conductExamID;
	}
	 /**
     * Returns the name of the conduct test.
     *
     * @return the conduct test name
     */
	public String getConductTestName() {
		return conductTestName;
	}
	/**
     * Sets the name of the conduct test.
     *
     * @param conductTestName the conduct test name to set
     */
	public void setConductTestName(String conductTestName) {
		this.conductTestName = conductTestName;
	}
	/**
     * Returns the name of the lecturer.
     *
     * @return the lecturer name
     */
	public String getLecturerName() {
		return lecturerName;
	}
	/**
     * Sets the name of the lecturer.
     *
     * @param lecturerName the lecturer name to set
     */
	public void setLecturerName(String lecturerName) {
		this.lecturerName = lecturerName;
	}
	/**
     * Returns the requested time extension.
     *
     * @return the requested time extension
     */
	public String getTimeAsked() {
		return timeAsked;
	}
	/**
     * Sets the requested time extension.
     *
     * @param timeAsked the requested time extension to set
     */
	public void setTimeAsked(String timeAsked) {
		this.timeAsked = timeAsked;
	}
	/**
     * Returns the image view for the "approve time extension" image.
     *
     * @return the "approve time extension" image view
     */
	public ImageView getYesImg() {
		return yesImg;
	}
	/**
     * Sets the image view for the "approve time extension" image.
     *
     * @param yesImg the "approve time extension" image view to set
     */
	public void setYesImg(ImageView yesImg) {
		this.yesImg = yesImg;
	}
	/**
     * Returns the checkbox for approving the time extension.
     *
     * @return the checkbox for approving the time extension
     */
	public CheckBox getCheckButton() {
		return checkButton;
	}
	/**
     * Sets the checkbox for approving the time extension.
     *
     * @param checkButton the checkbox for approving the time extension to set
     */
	public void setCheckButton(CheckBox checkButton) {
		this.checkButton = checkButton;
	}
}
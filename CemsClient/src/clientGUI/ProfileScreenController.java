package clientGUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

import Client.CEMSClient;
import Client.CEMSClientUI;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import utilities.CONSTANTS;
import utilities.Message;

/**
 * The controller class for the profile screen. This class handles user
 * interactions and screen transitions in the profile screen.
 */
public class ProfileScreenController {

	public static InputStream profilePic;

	public static User user;

	@FXML
	private Button left_arrow_image;

	@FXML
	private ImageView xButton;

	@FXML
	private Label usernameLabel, userRolelabel;

	@FXML
	private ImageView picture;

	@FXML
	private Button editClick;

	/**
	 * Initializes the profile screen controller. Configures event handlers for the
	 * arrow and close button.
	 */
	@FXML
	void initialize() {

		getProfilePicStartup();
		usernameLabel.setText(user.getFirstName() + " " + user.getLastName());
		userRolelabel.setText(user.getUserPermission());

		refreshProfile();

		left_arrow_image.setPickOnBounds(true);
		left_arrow_image.setOnMouseClicked((MouseEvent e) -> {
			try {
				if (user.getUserPermission().equals(CONSTANTS.teacherRole))
					CEMSClientUI.switchScenes(getClass().getResource("/teacherUi/TeacherMainScreen.fxml"));
				else if (user.getUserPermission().equals(CONSTANTS.studentRole))
					CEMSClientUI.switchScenes(getClass().getResource("/studentUi/studentMainScreen.fxml"));
				else if (user.getUserPermission().equals(CONSTANTS.headOfDepartmentRole))
					CEMSClientUI
							.switchScenes(getClass().getResource("/headDepartmentUI/headDepartmentMainScreen.fxml"));
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

		xButton.setPickOnBounds(true);
		xButton.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClient.logout(user.getUserName());
				CEMSClient.exitButton();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		});
	}

	private void getProfilePicStartup() {
		try {
			CEMSClientUI.chat.accept(new Message(CONSTANTS.getProfilePicture, user.getUserName()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateProfilePic() {
		byte[] pic = fileChoose();
		if (pic != null) {
			try {
				ArrayList<Object> data = new ArrayList<>();
				data.add(user.getUserName());
				data.add(pic);
				CEMSClientUI.chat.accept(new Message(CONSTANTS.updateProfilePicture, data));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		refreshProfile();
	}

	private byte[] fileChoose() {
		FileChooser Fc = new FileChooser();
		Fc.setTitle("Choose a Picture");
		ExtensionFilter imageFilter = new ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif");
		Fc.getExtensionFilters().add(imageFilter);
		File selectedFile = Fc.showOpenDialog(CEMSClientUI.primary_stage);

		if (selectedFile != null) {
			File pictureFile = new File(selectedFile.getAbsolutePath());
			try {
				byte[] pictureData = Files.readAllBytes(pictureFile.toPath());
				profilePic = new FileInputStream(selectedFile.getAbsolutePath());
				return pictureData;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else
			return null;
	}

	private void refreshProfile() {
		if (profilePic == null) {
			picture.setImage(new Image(this.getClass().getResourceAsStream("/images/user.png")));
		} else {
			Image image = new Image(profilePic);
			picture.setImage(image);
		}
	}
}
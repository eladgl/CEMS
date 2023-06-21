package teacherUi;

import java.io.IOException;

import Client.CEMSClient;
import Client.CEMSClientUI;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class CreateNewTestController { 
	private static double xOffset = 0;
	private static double yOffset = 0;
	
	@FXML
	private ImageView left_arrow_image;
	
	@FXML
	private ImageView xButton;

	
	@FXML
	void initialize() {
		left_arrow_image.setPickOnBounds(true);
		// back arrow click
		left_arrow_image.setOnMouseClicked((MouseEvent e) -> {
			
			try {
				CEMSClientUI.switchScenes(getClass().getResource("TestBankScreen.fxml"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		});
		
		xButton.setPickOnBounds(true);
		xButton.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClient.logout(TeacherMainScreenController.getUsername());
				CEMSClient.exitButton();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);		
		});
	}
}

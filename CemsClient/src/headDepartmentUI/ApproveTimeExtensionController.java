package headDepartmentUI;

import java.io.IOException;

import Client.CEMSClient;
import Client.CEMSClientUI;
import clientGUI.UserLoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import utilities.CONSTANTS;
import utilities.Message;

import java.util.List;
import java.util.ArrayList;
/**
 * Controller class for the Approve Time Extension UI.
 */
public class ApproveTimeExtensionController {
	/**
     * Observable list to store time extension requests.
     */
	public static ObservableList<ApproveTimeExtension> timeExtensionList = FXCollections.observableArrayList();
	/**
     * Error message to be displayed on the UI.
     */
	public static String message = null; 

	@FXML
	private Label errorLbl;

	@FXML
	private Button approveAllBtn;

	@FXML
	private Button approveAllSelectedBtn;

	@FXML
	private Button back_arrow_image;

	@FXML
	private Button xButton;

	@FXML
	private TableView<ApproveTimeExtension> extensionTimeTable;

	@FXML
	private TableColumn<ApproveTimeExtension, String> lecturerNameCol;
	@FXML
	private TableColumn<ApproveTimeExtension, String> testNameCol;
	@FXML
	private TableColumn<ApproveTimeExtension, String> timeExtensionCol;
	@FXML
	private TableColumn<ApproveTimeExtension, ImageView> approveCol;
	@FXML
	private TableColumn<ApproveTimeExtension, ImageView> disproveCol;
	@FXML
	private TableColumn<ApproveTimeExtension, CheckBox> checkCol;

	@FXML
	private CheckBox selectAllCheckBox;
	/**
     * Initializes the controller and sets up the UI elements.
     */
	@FXML
	void initialize() {
		xButton.setPickOnBounds(true);
		xButton.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClient.logout(UserLoginController.user.getUserName());
				CEMSClient.exitButton();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		});

		back_arrow_image.setPickOnBounds(true);
		// back arrow click
		back_arrow_image.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClientUI.switchScenes(getClass().getResource("headDepartmentMainScreen.fxml"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		extensionTimeTable.setItems(timeExtensionList);
		extensionTimeTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
//		lecturerNameCol.setReorderable(false);
//		testNameCol.setReorderable(false);
//		timeExtensionCol.setReorderable(false);
//		approveCol.setReorderable(false);
//		checkCol.setReorderable(false);
//		disproveCol.setReorderable(false);
		testNameCol.setCellValueFactory(new PropertyValueFactory<>("conductTestName"));
		lecturerNameCol.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));
		timeExtensionCol.setCellValueFactory(new PropertyValueFactory<>("timeAsked"));
		approveCol.setCellValueFactory(new PropertyValueFactory<>("yesImg"));
		approveCol.setCellFactory(column -> {
			TableCell<ApproveTimeExtension, ImageView> cell = new TableCell<ApproveTimeExtension, ImageView>() {
				private final ImageView imageView = new ImageView();

				{
					setGraphic(imageView);
				}

				@Override
				protected void updateItem(ImageView item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						imageView.setImage(null);
					} else {
						imageView.setImage(item.getImage());
						imageView.setFitWidth(20);
						imageView.setFitHeight(20);
					}
				}
			};

			// Add mouse click event handler to the TableCell
			cell.setOnMouseClicked(event -> {
				if (!cell.isEmpty()) {
					ApproveTimeExtension timeExtension = cell.getTableView().getItems().get(cell.getIndex());
					ImageView image = cell.getItem();
					// Handle the mouse click event
					System.out.println("Clicked on approveCol. Time asked: " + timeExtension.getTimeAsked());
					approveExtensionForExam(timeExtension.getConductExamID(), timeExtension.getTimeAsked());
					// Remove the row from the TableView
					TableView<ApproveTimeExtension> tableView = cell.getTableView();
					tableView.getItems().remove(timeExtension);
				}
			});
			return cell;
		});

		disproveCol.setCellValueFactory(new PropertyValueFactory<>("noImg"));
		disproveCol.setCellFactory(column -> {
			TableCell<ApproveTimeExtension, ImageView> cell = new TableCell<ApproveTimeExtension, ImageView>() {
				private final ImageView imageView = new ImageView();
				{
					setGraphic(imageView);
				}

				@Override
				protected void updateItem(ImageView item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						imageView.setImage(null);
					} else {
						imageView.setImage(item.getImage());
						imageView.setFitWidth(20);
						imageView.setFitHeight(20);
					}
				}
			};

			// Add mouse click event handler to the TableCell
			cell.setOnMouseClicked(event -> {
				if (!cell.isEmpty()) {
					ApproveTimeExtension timeExtension = cell.getTableView().getItems().get(cell.getIndex());
					ImageView image = cell.getItem();
					// Handle the mouse click event
					System.out.println("Clicked on reject.");
					denyExtensionForExam(timeExtension.getConductExamID());
					// Remove the row from the TableView
					TableView<ApproveTimeExtension> tableView = cell.getTableView();
					tableView.getItems().remove(timeExtension);
				}
			});
			return cell;
		});
		checkCol.setCellValueFactory(new PropertyValueFactory<>("checkButton"));
		getTimeExtensionRequests();
	}
	/**
     * Refreshes the time extension requests by calling the backend.
     * @param event The mouse event triggering the refresh action.
     */
	@FXML
	void refresh(MouseEvent event) {
		getTimeExtensionRequests();
	}
	/**
     * Approves all the time extension requests.
     * @param event The action event triggering the approval.
     */
	@FXML
	void approveAll(ActionEvent event) {

		List<Integer> approvedIDs = new ArrayList<>();

		for (ApproveTimeExtension timeExtension : timeExtensionList) {
			// Perform the approval logic for each row
			// ...
			System.out.println(
					"exam " + timeExtension.getConductExamID() + " " + "has approved " + timeExtension.getTimeAsked());
			// Add the conductExamID to the list of approved IDs
			approvedIDs.add(timeExtension.getConductExamID());
			approveExtensionForExam(timeExtension.getConductExamID(), timeExtension.getTimeAsked());
		}

		// Clear the TableView
		if (timeExtensionList != null) {
			timeExtensionList.clear();
		}
	}
	/**
     * Approves the selected time extension requests.
     * @param event The action event triggering the approval.
     */
	@FXML
	void approveAllSelected(ActionEvent event) {

		List<Integer> approvedIDs = new ArrayList<>();

		for (ApproveTimeExtension timeExtension : timeExtensionList) {
			CheckBox checkBox = timeExtension.getCheckButton();
			if (checkBox.isSelected()) {
				// Perform the approval logic for each selected row
				// ...
				System.out.println(
						"exam " + timeExtension.getConductExamID() + " has approved " + timeExtension.getTimeAsked());
				// Add the conductExamID to the list of approved IDs
				approvedIDs.add(timeExtension.getConductExamID());
				approveExtensionForExam(timeExtension.getConductExamID(), timeExtension.getTimeAsked());
			}
		}

		timeExtensionList.removeIf(timeExtension -> timeExtension.getCheckButton().isSelected());

		// Remove the selected rows from the TableView
		// Remove the selected rows from the TableView

	}
	 /**
     * Selects or deselects all time extension requests.
     * @param event The action event triggering the selection/deselection.
     */
	@FXML
	void selectAll(ActionEvent event) {
		boolean selectAllChecked = selectAllCheckBox.isSelected();

		for (ApproveTimeExtension timeExtension : timeExtensionList) {
			CheckBox checkBox = timeExtension.getCheckButton();
			checkBox.setSelected(selectAllChecked);
		}
	}
	/**
     * Rejects all the time extension requests.
     * @param event The action event triggering the rejection.
     */
	@FXML
	void rejectAll(ActionEvent event) {
		List<Integer> rejectedIDs = new ArrayList<>();

		for (ApproveTimeExtension timeExtension : timeExtensionList) {
			// Perform the approval logic for each row
			// ...
			System.out.println(
					"exam " + timeExtension.getConductExamID() + " has rejected "
							+ "" + timeExtension.getTimeAsked() + " time extension");// Add the conductExamID to the list of approved IDs
			rejectedIDs.add(timeExtension.getConductExamID());
			denyExtensionForExam(timeExtension.getConductExamID());
		}

		// Clear the TableView
		if (timeExtensionList != null) {
			timeExtensionList.clear();
		}
	}
	/**
     * Rejects the selected time extension requests.
     * @param event The action event triggering the rejection.
     */
	@FXML
	void rejectSelected(ActionEvent event) {
		List<Integer> rejectedIDs = new ArrayList<>();

		for (ApproveTimeExtension timeExtension : timeExtensionList) {
			CheckBox checkBox = timeExtension.getCheckButton();
			if (checkBox.isSelected()) {
				// Perform the approval logic for each selected row
				// ...
				System.out.println(
						"exam " + timeExtension.getConductExamID() + " has rejected "
								+ "" + timeExtension.getTimeAsked() + " time extension");
				// Add the conductExamID to the list of approved IDs
				rejectedIDs.add(timeExtension.getConductExamID());
				denyExtensionForExam(timeExtension.getConductExamID());
			}
		}

		timeExtensionList.removeIf(timeExtension -> timeExtension.getCheckButton().isSelected());
	}
	/**
     * Displays an error message on the error label.
     * @param msg The error message to be displayed.
     */
	private void showError(String msg) {
		errorLbl.setText(msg);
	}
	/**
     * Sends a request to the backend to approve a time extension for an exam.
     * @param id The ID of the exam.
     * @param duration The time duration of the extension.
     */
	private void approveExtensionForExam(int id, String duration) {
		List<String> data = new ArrayList<>();
		data.add(Integer.toString(id));
		data.add(duration);
		Message data_to_db = new Message(CONSTANTS.approveTimeExtension, data);
		try {
			CEMSClientUI.chat.accept(data_to_db);
			showError("");
		} catch (Exception e) {
			showError("Could not approve time extension - contact IT");
			e.printStackTrace();
		}
	}
	/**
     * Sends a request to the backend to deny a time extension for an exam.
     * @param id The ID of the exam.
     */
	private void denyExtensionForExam(int id) {
		Message data_to_db = new Message(CONSTANTS.denyTimeExtension, id);
		try {
			CEMSClientUI.chat.accept(data_to_db);
			showError("");
		}catch(Exception e) {
			showError("Could not deny time extension - contact IT");
			e.printStackTrace();
		}
		showError(message);
	}
	/**
     * Sends a request to the backend to get time extension requests.
     */
	private void getTimeExtensionRequests() {
		timeExtensionList.clear();
		Message data_to_db = new Message(CONSTANTS.getTimeExtensionRequests, null);
		try {
			CEMSClientUI.chat.accept(data_to_db);
			showError("");
		}catch(Exception e) {
			showError("Could not get time extension - contact IT");
			e.printStackTrace();
		}
	}
}
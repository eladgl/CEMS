package headDepartmentUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import Client.CEMSClient;
import Client.CEMSClientUI;
import entities.Exam;
import entities.Grade;
import entities.StatisticsReport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import utilities.CONSTANTS;
import utilities.Message;

public class StatisticsReportControllerHD {

	private static final String[] GRADE_CATEGORIES = { "0-54.9", "55-64", "65-69", "70-74", "75-79", "80-84", "85-89",
			"90-94", "95-100" };
	private static int[] gradesIndex = {55,65,70,75,80,85,90,95,101};
	public static ArrayList<Exam> generalExams = null;
	public static HashMap<String, Exam> takenExams = null;
	public static ArrayList<String> lecturers = null;
	public static ArrayList<XYChart.Series<String, Number>> seriesList = new ArrayList<XYChart.Series<String, Number>>();
	public static ArrayList<StatisticsReport> statisticsData;
	public static HashMap<String, ArrayList<Grade>> students;

	/**
	 * This Variable will store all grades of the students that participated in a
	 * computerized conduct exam of a course.
	 */

	@FXML
	private Label stat;

	@FXML
	private Button left_arrow_image;

	@FXML
	private Button xButton;

	@FXML
	private BarChart<String, Number> grade_chart;

	@FXML
	private ComboBox<String> CompareTypeBox;

	@FXML
	private ComboBox<String> dataSelectBox;

	@FXML
	private TableView<statisticTable> exam_table;

	@FXML
	private TableColumn<statisticTable, String> exam_name_col;
	@FXML
	private TableColumn<statisticTable, CheckBox> select_exam_col;

	@FXML
	private Label failedLabel, meidanLabel, avgLabel, studentNumLabel,noteLabel;

	@FXML
	private HBox hboxData;

	@FXML
	void initialize() throws Exception {
		seriesList = new ArrayList<>();

		// init the test choice box
		CompareTypeBox.getItems().addAll("Lecturer", "Course", "Student");
		CompareTypeBox.setValue("Select Statistic");
		dataSelectBox.setValue("Select Object");
		dataSelectBox.setVisibleRowCount(15);

		generalExams = HeadDepartmentMainScreenController.generalExams;

		lecturers = GetLecturers();
		GetStudents();

		CompareTypeBox.valueProperty().addListener((observable1, oldValue1, newValue1) -> {
			dataSelectBox.getItems().clear();
			updateAcordingToCompareType();
		});

		dataSelectBox.valueProperty().addListener((observable2, oldValue2, newValue2) -> {

			try {
				CEMSClientUI.chat.accept(new Message(CONSTANTS.getTakenExamsForStatistics, null));
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(newValue2!=null)
			{
				updateTableView();
			}
			
		});

		
		// back arrow click
		left_arrow_image.setOnMouseClicked((MouseEvent e) -> {

			try {
				CEMSClientUI.switchScenes(getClass().getResource("headDepartmentMainScreen.fxml"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});

		
		xButton.setOnMouseClicked((MouseEvent e) -> {
			try {
				CEMSClient.logout(HeadDepartmentMainScreenController.getUsername());
				CEMSClient.exitButton();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		});

		hboxData.setVisible(false);
		noteLabel.setVisible(false);
	}

	private void updateAcordingToCompareType() {
		String compareType = CompareTypeBox.getValue();
		switch (compareType) {

		case ("Lecturer"): {
			// Loop through each entry in the HashMap
			for (String lc : lecturers) {
				// Add the name and ID to the ChoiceBox
				dataSelectBox.getItems().add(lc);
			}
			break;
		}
		case ("Course"): {
			for (Exam entry : generalExams) {
				if (!dataSelectBox.getItems().contains(entry.getCourseName())) {
					dataSelectBox.getItems().add(entry.getCourseName());
				}
			}
			break;
		}
		case ("Student"): {
			for (String username : students.keySet())
				if (!dataSelectBox.getItems().contains(username)) {
					dataSelectBox.getItems().add(username);
				}
			break;
		}
		default:
			System.out.println("Select compersion type");
		}
	}

	public static ArrayList<String> GetLecturers() {

		ArrayList<String> lecturersToReturn = new ArrayList<>();
		for (Exam exam : generalExams) {
			String lecturer = exam.getAuthorName() + "_" + exam.getAuthorID();
			if (!lecturersToReturn.contains(lecturer)) {
				lecturersToReturn.add(lecturer);
			}
		}
		return lecturersToReturn;
	}

	public static void GetStudents() {
		try {
			CEMSClientUI.chat.accept(new Message(CONSTANTS.getStudentsTakenTests, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void updateChart() {

		hboxData.setVisible(false);
		noteLabel.setVisible(true);
		fetchStatisticData();
		seriesList.clear();

		if (!CompareTypeBox.getValue().equals("Student"))
			updateChartData();
		else
			updateChartStudentData();
		
		grade_chart.setAnimated(false);
		grade_chart.getData().clear();
		grade_chart.getData().addAll(seriesList);
		for (Node node : grade_chart.lookupAll(".chart-legend-item")) {
			node.setOnMouseClicked((MouseEvent event) -> {
				addChartAdditionalData(((Label) node).getText());
				hboxData.setVisible(true);
			});
		}
	}

	private void updateChartData() {
		for (statisticTable st : exam_table.getItems()) {
			boolean isChecked = st.getBox().isSelected();
			if (isChecked) {
				XYChart.Series<String, Number> series = new XYChart.Series<>();
				series.setName(st.getTestName());
				for (StatisticsReport stats : statisticsData) {
					if (st.getConductId().equals(stats.getConductExamId() + "")) {
						series.getData().addAll(addDataToSeries(stats));
						break;
					}
				}
				seriesList.add(series);
			}
		}
	}

	private void updateChartStudentData()
	{
		for (statisticTable st : exam_table.getItems()) {
			boolean isChecked = st.getBox().isSelected();
			if (isChecked) {
				XYChart.Series<String, Number> series = new XYChart.Series<>();
				series.setName(st.getTestName());
				ArrayList<Grade> grs = students.get(dataSelectBox.getValue());
				for (Grade g : grs) {
					if (st.getConductId().equals(g.getConductExamId())) {
						series.getData().addAll(addDataToSeries(g.getGrade()));
						break;
					}
				}			
				seriesList.add(series);
			}
		}
	}

	private ArrayList<XYChart.Data<String, Number>> addDataToSeries(int grade) {

		ArrayList<XYChart.Data<String, Number>> tmp = new ArrayList<XYChart.Data<String, Number>>();
		int i;
		for (i = 0; i < 9; i++) {
			tmp.add(new XYChart.Data<>(GRADE_CATEGORIES[i], 0));
		}

		for(i=0; i < 9; i++) 
			if (grade < gradesIndex[i]) {
					tmp.get(i).setYValue(1);
					break;
			}
		
		return tmp;
	}

	private ArrayList<XYChart.Data<String, Number>> addDataToSeries(StatisticsReport stats) {
		ArrayList<XYChart.Data<String, Number>> tmp = new ArrayList<XYChart.Data<String, Number>>();
		tmp.add(new XYChart.Data<>(GRADE_CATEGORIES[0], stats.getGrade0_54_9()));
		tmp.add(new XYChart.Data<>(GRADE_CATEGORIES[1], stats.getGrade55_64()));
		tmp.add(new XYChart.Data<>(GRADE_CATEGORIES[2], stats.getGrade65_69()));
		tmp.add(new XYChart.Data<>(GRADE_CATEGORIES[3], stats.getGrade70_74()));
		tmp.add(new XYChart.Data<>(GRADE_CATEGORIES[4], stats.getGrade75_79()));
		tmp.add(new XYChart.Data<>(GRADE_CATEGORIES[5], stats.getGrade80_84()));
		tmp.add(new XYChart.Data<>(GRADE_CATEGORIES[6], stats.getGrade85_89()));
		tmp.add(new XYChart.Data<>(GRADE_CATEGORIES[7], stats.getGrade90_94()));
		tmp.add(new XYChart.Data<>(GRADE_CATEGORIES[8], stats.getGrade95_100()));
		return tmp;
	}

	private void addChartAdditionalData(String name) {
		for (StatisticsReport sr : statisticsData) {
			if (sr.getConductExamName().equals(name)) {
				failedLabel.setText(sr.getGrade0_54_9() + "%");
				meidanLabel.setText(sr.getMedian() + "");
				avgLabel.setText(sr.getAverage() + "");
				studentNumLabel.setText(sr.getNumOfStudents() + "");
				break;
			}
		}

	}

	private void updateTableView() {

		exam_table.getItems().clear();
		exam_name_col.setCellValueFactory(new PropertyValueFactory<>("testName"));
		select_exam_col.setCellValueFactory(new PropertyValueFactory<>("box"));

		String compareType = CompareTypeBox.getValue();
		String selectedType = dataSelectBox.getValue();
		ObservableList<statisticTable> examsOnTable = FXCollections.observableArrayList();

		switch (compareType) {
		case "Lecturer": {
			for (Entry<String, Exam> val : takenExams.entrySet()) {
				String auth = val.getValue().getAuthorName() + "_" + val.getValue().getAuthorID();
				if (auth.equals(selectedType)) {
					examsOnTable.add(new statisticTable(val.getKey(), val.getValue().getTestName()));
				}
			}
			break;
		}
		case "Course": {
			for (Entry<String, Exam> val : takenExams.entrySet()) {
				if (val.getValue().getCourse().getName().equals(selectedType)) {
					examsOnTable.add(new statisticTable(val.getKey(), val.getValue().getTestName()));
				}
			}
			break;
		}
		case "Student": 
		{
			ArrayList<Grade> stdGrades = students.get(selectedType);
			for (Grade conductExamId : stdGrades) 
			{
				examsOnTable.add(new statisticTable(conductExamId.getConductExamId(),
						takenExams.get(conductExamId.getConductExamId()).getTestName()));
			}
			break;
		}
		default:
			System.out.println("Select comparison type");
		}

		exam_table.setItems(examsOnTable);
		exam_table.refresh();

	}

	private void fetchStatisticData() {

		try {
			CEMSClientUI.chat.accept(new Message(CONSTANTS.getStatisticsData, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

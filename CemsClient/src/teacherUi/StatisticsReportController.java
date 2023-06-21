package teacherUi;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ResourceBundle;

import Client.CEMSClient;
import Client.CEMSClientUI;
import entities.Pair;
import entities.StatisticsReportDataForLecturer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import utilities.CONSTANTS;
import utilities.Message;


public class StatisticsReportController implements Initializable{

	private static final String[] GRADE_CATEGORIES = {"0-54.9", "55-64", "65-69", "70-74", "75-79", "80-84", "85-89", "90-94", "95-100"};
	private final String lecturer_id = TeacherMainScreenController.user.getIDNumber();
	private final String lecturer_userName = TeacherMainScreenController.user.getUserName();
	private boolean hasRowbeenClickedBeforeFlag = false;

	/**
	 * The Variable(lecturer_statistics_data) will store All the exam data (resulting grades and distribution) that the current
	 * lecturer conducted or was the author for that particular exam.
	 */

	public static ArrayList<StatisticsReportDataForLecturer> lecturer_statistics_data = null;
	private ObservableList<LecturerExamDataTable> list_of_exams = FXCollections.observableArrayList();


	@FXML
	private Label stat;
	
	@FXML private AnchorPane arrow_wrapper;

	// Left Labels
	@FXML private Label CourseNameLabel;
	@FXML private Label AverageScoreLabel;
	@FXML private Label MedianLabel;
	@FXML private Label NumberOfStudentsLabel;
	@FXML private Label HighestScoreLabel;
	@FXML private Label LowestScoreLable;
	@FXML private Label FailurePercantageLabel;
	
	
	// constant Labels (only declared them for visibility)
	@FXML
	private Label courseNameFixedTextLabel;
	@FXML
	private Label numberOfStudentsFixedTextLabel;
	@FXML
	private Label HighestScoreFixedTextLabel;
	@FXML
	private Label LowestScoreFixedTextLabel;
	@FXML
	private Label averageScoreFixedTextLabel;
	@FXML
	private Label medianFixedTextLabel;
	@FXML
	private Label FailurPercentageFixedTextLabel;

	@FXML
	private Button left_arrow_image;

	@FXML
	private Button xButton;

	@FXML
	private BarChart<String, Number> gradesBar;

	@FXML
	private CategoryAxis xAxis;

	@FXML
	private NumberAxis yAxis;

	@FXML private TableView<LecturerExamDataTable> examsTable;
	@FXML private TableColumn<LecturerExamDataTable, String> conduct_exam_name;
	@FXML private TableColumn<LecturerExamDataTable, String> lecturer_name;
	@FXML private TableColumn<LecturerExamDataTable, String> course_name;


	// Distribution table
	@FXML
	private TableView<GradeCategory> examDistributionTable;

	@FXML
	private TableColumn<GradeCategory, String> categoryColumn;

	@FXML
	private TableColumn<GradeCategory, Integer> countColumn;

	@FXML
	private TableColumn<GradeCategory, Double> percentageColumn;

	/**
	 * Initialize The Table Columns for both tables.
	 * Fetch The Lecturer Data From the DB and Initialize the first table by these values.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		conduct_exam_name.setCellValueFactory(new PropertyValueFactory<LecturerExamDataTable, String>("conductExamName"));
		lecturer_name.setCellValueFactory(new PropertyValueFactory<LecturerExamDataTable, String>("LecturerName"));
		course_name.setCellValueFactory(new PropertyValueFactory<LecturerExamDataTable, String>("CourseName"));


		// Initialize the table columns
		categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
		countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
		percentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));

		// add a '%' before each percentage
		percentageColumn.setCellFactory(new Callback<TableColumn<GradeCategory, Double>, TableCell<GradeCategory, Double>>() {
			@Override
			public TableCell<GradeCategory, Double> call(TableColumn<GradeCategory, Double> column) {
				TableCell<GradeCategory, Double> cell = new TableCell<GradeCategory, Double>() {
					@Override
					protected void updateItem(Double item, boolean empty) {
						super.updateItem(item, empty);
						if (empty || item == null) {
							setText(null);
						} else {
							setText(String.format("%.2f%%", item));
						}
					}
				};
				return cell;
			}
		});
		
		fetchLecturerExamsFromDB();
		initializeExamTable();
		setLabelAndBarChartComponentsVisibility(false);
		initializeBlinkingArrowAnimation();
		left_arrow_image.setPickOnBounds(true);
		xButton.setPickOnBounds(true);
	}


	/**
	 * When clicking on the exam Table, Take the clicked exam info and Display The Statistics of this exam + Initialize the
	 * Distribution table and then initialize the label texts.
	 * @param e Mouse Event Info.
	 */
	@FXML
	void rowClicked(MouseEvent e) {
		LecturerExamDataTable lecturerExamChoice = examsTable.getSelectionModel().getSelectedItem();
		if(lecturerExamChoice == null)
			return;
		String conductExamName = lecturerExamChoice.getConductExamName();
		int conduct_exam_id = lecturerExamChoice.getConduct_exam_id();

		StatisticsReportDataForLecturer relevant_exam = null;

		// finding exam by conduct exam id
		for(StatisticsReportDataForLecturer statistics_data : lecturer_statistics_data) {
			if(statistics_data.getConduct_exam_id() == conduct_exam_id && statistics_data.getConduct_exam_name().equals(
					conductExamName)) {
				relevant_exam = statistics_data;
				break;
			}
		}
		
		if(relevant_exam == null)
			return;
		
		if(! hasRowbeenClickedBeforeFlag) {
			setLabelAndBarChartComponentsVisibility(true);
		}

		initializeChart(relevant_exam);
		initializeDistributionTabale(relevant_exam);
		initializeLabel(relevant_exam);
	}

	/**
	 * Clicking On the Top Left Image Will Take you back to the previous Screen.
	 * @param e
	 */
	@FXML
	void leftArrowClickedAction(MouseEvent e) {
		try {
			CEMSClientUI.switchScenes(getClass().getResource("TeacherMainScreen.fxml"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Clicking on the top right xButton will Exit the Hole application.
	 * @param e
	 */

	@FXML
	void xButtonClickedAction(MouseEvent e) {
		try {
			CEMSClient.logout(TeacherMainScreenController.getUsername());
			CEMSClient.exitButton();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		System.exit(0);
	}


	/**
	 * Initializing the distribution table with relevant data from db.
	 */
	private void initializeExamTable() {
		if(lecturer_statistics_data.size() == 0)
			return;

		for(StatisticsReportDataForLecturer data : lecturer_statistics_data) {
			String conductExamName = data.getConduct_exam_name();
			String courseName = data.getCourse_name();
			String exam_conductor_username = data.getUsername_exam_conductor();
			int conduct_exam_id = data.getConduct_exam_id();
			this.list_of_exams.add(new LecturerExamDataTable(conductExamName, exam_conductor_username, courseName, conduct_exam_id));
		}

		examsTable.setItems(this.list_of_exams);		
	}


	/**
	 * Initializes the bar-chart with relevant exam data and statistics.
	 * 
	 * @param relevant_exam relevant data exam
	 */
	private void initializeChart(StatisticsReportDataForLecturer relevant_exam){
		gradesBar.setAnimated(true);
		
		if(!hasRowbeenClickedBeforeFlag) { // disable the animation only for the first row click. (prevent javafx bug).
			hasRowbeenClickedBeforeFlag = true;
			gradesBar.setAnimated(false);
		}
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		gradesBar.getData().clear();

		for (int i = 0; i < GRADE_CATEGORIES.length; i++) {
			// Inject the series with data from the grade categories
			series.getData().add(new XYChart.Data<>(GRADE_CATEGORIES[i],
					relevant_exam.getGradeCountInEachCategory()[i]));
		}

		gradesBar.getData().add(series);

		NumberAxis yAxis = (NumberAxis) gradesBar.getYAxis();  // Configure the yAxis to display only integer values
		yAxis.setTickUnit(1); // Set the tick unit to 1 (integer)
		yAxis.setMinorTickVisible(false); // Hide minor tick marks

		// Set a custom string converter to display integer values on the yAxis
		yAxis.setTickLabelFormatter(new StringConverter<Number>() {
			@Override
			public String toString(Number object) {
				return String.valueOf(object.intValue());
			}

			@Override
			public Number fromString(String string) {
				return null;
			}
		});
	}

	
	/**
	 * Initializes the label with statistics data for a relevant exam.
	 *
	 * @param relevant_exam The statistics report data for the lecturer's relevant exam
	 */
	private void initializeLabel(StatisticsReportDataForLecturer relevant_exam) {
		DecimalFormat decimalFormat = new DecimalFormat("#.#");
		
		CourseNameLabel.setText(String.valueOf(relevant_exam.getCourse_name()));
		AverageScoreLabel.setText(decimalFormat.format(relevant_exam.getAverage()));
		MedianLabel.setText(decimalFormat.format(relevant_exam.getMedian()));
		NumberOfStudentsLabel.setText(String.valueOf(relevant_exam.getNumber_of_students()));

		HighestScoreLabel.setText(String.valueOf(relevant_exam.getMax_grade()));
		LowestScoreLable.setText(String.valueOf(relevant_exam.getMin_grade()));

		int gradesCategory[] = relevant_exam.getGradeCountInEachCategory();
		double failure_percentage = (double) gradesCategory[0] / relevant_exam.getNumber_of_students();
		FailurePercantageLabel.setText(decimalFormat.format(failure_percentage * 100) + "%");
	}
	
	
	/**
	 * Initializes the distribution table with statistics data for a relevant exam.
	 *
	 * @param relevant_exam The statistics report data for the lecturer's relevant exam
	 */
	
	private void initializeDistributionTabale(StatisticsReportDataForLecturer relevant_exam) {
		examDistributionTable.getItems().clear();
		examDistributionTable.refresh();

		int totalStudents = relevant_exam.getNumber_of_students();
		double[] percentage = new double[GRADE_CATEGORIES.length];
		int [] gradeCount = relevant_exam.getGradeCountInEachCategory();

		for (int i = 0; i < gradeCount.length; i++) {// Calculate the percentage of students in each category
			percentage[i] = (double) gradeCount[i] / totalStudents * 100;
		}

		for (int i = 0; i < GRADE_CATEGORIES.length; i++) {
			String category = GRADE_CATEGORIES[i];
			int count = gradeCount[i];
			double percent = percentage[i];

			GradeCategory gradeCategory = new GradeCategory(category, count, percent);
			examDistributionTable.getItems().add(gradeCategory);
		}
		// shift the values to the right
		examDistributionTable.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-size: 18px;");

		// Apply custom CSS styles to the table cells
		for (TableColumn<?, ?> column : examDistributionTable.getColumns()) {

			column.setStyle("-fx-alignment: CENTER-RIGHT;");
			column.setStyle("-fx-font-size: 14px;");

		}
	}

	/**
	 * Fetches the lecturer's exams from the database.
	 */
	private void fetchLecturerExamsFromDB() {
		if(lecturer_id == null || lecturer_userName == null) {
			System.out.println("Null Values");
			return;
		}

		try {
			CEMSClientUI.chat.accept(new Message(CONSTANTS.getCourseNameAndRelevantDataExamsForLecturer,
					new Pair<String, String>(lecturer_id, lecturer_userName)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initializeBlinkingArrowAnimation() {
		Arrow arrow = new Arrow(55, 50, 0, 50);
		arrow_wrapper.getChildren().add(arrow);
		// Show the arrow after a delay
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            arrow.setVisible(false);
        });
        delay.play();

        // Animate the arrow with a blinking effect
        Timeline blinkTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), new KeyValue(arrow.opacityProperty(), 0)),
                new KeyFrame(Duration.seconds(1), new KeyValue(arrow.opacityProperty(), 1))
        );
        blinkTimeline.setCycleCount(Animation.INDEFINITE);
        blinkTimeline.setAutoReverse(true);
        blinkTimeline.play();
	}
	
	/**
	 * Sets all the components visibility to the is_visible parameter.
	 * @param is_visible whether to set the components visibility to true/false
	 */
	private void setLabelAndBarChartComponentsVisibility(boolean is_visible) {
	    examDistributionTable.setVisible(is_visible);	 
	    gradesBar.setVisible(is_visible);
	    
	    courseNameFixedTextLabel.setVisible(is_visible);
	    numberOfStudentsFixedTextLabel.setVisible(is_visible);
	    HighestScoreFixedTextLabel.setVisible(is_visible);
	    LowestScoreFixedTextLabel.setVisible(is_visible);
	    averageScoreFixedTextLabel.setVisible(is_visible);
	    medianFixedTextLabel.setVisible(is_visible);
	    FailurPercentageFixedTextLabel.setVisible(is_visible);
	}


	/**
	 * Class For the lecturer Exam.
	 * @author razi
	 *
	 */
	public class LecturerExamDataTable{
		private String conductExamName;
		private String LecturerName;
		private String CourseName;
		private int conduct_exam_id;

		public LecturerExamDataTable(String conductExamName, String lecturerName, String courseName, int conduct_exam_id) {
			this.conductExamName = conductExamName;
			this.conduct_exam_id = conduct_exam_id;
			LecturerName = lecturerName;
			CourseName = courseName;
		}

		public int getConduct_exam_id() {
			return conduct_exam_id;
		}

		public void setConduct_exam_id(int conduct_exam_id) {
			this.conduct_exam_id = conduct_exam_id;
		}

		public String getConductExamName() {
			return conductExamName;
		}

		public String getLecturerName() {
			return LecturerName;
		}

		public String getCourseName() {
			return CourseName;
		}
	}

	/**
	 * Class For Distribution exam data.
	 * @author razi
	 *
	 */
	public class GradeCategory{
		private String category;
		private int count;
		private double percentage;
		public GradeCategory(String category, int count, double percentage) {
			this.category = category;
			this.count = count;
			this.percentage = percentage;
		}
		public String getCategory() {
			return category;
		}
		public int getCount() {
			return count;
		}
		public double getPercentage() {
			return percentage;
		}

	}
	
	
	/**
	 * This Class constructs an arrow shape.
	 * 
	 * Thank you kn0412 for helping.
	 * @author kn0412
	 *
	 */
	private class Arrow extends Path{
	    private static final double defaultArrowHeadSize = 7.0;
	    
	    public Arrow(double startX, double startY, double endX, double endY, double arrowHeadSize){
	        super();
	        strokeProperty().bind(fillProperty());
	        setFill(Color.BLACK);
	        
	        //Line
	        getElements().add(new MoveTo(startX, startY));
	        getElements().add(new LineTo(endX, endY));
	        
	        //ArrowHead
	        double angle = Math.atan2((endY - startY), (endX - startX)) - Math.PI / 2.0;
	        double sin = Math.sin(angle);
	        double cos = Math.cos(angle);
	        //point1
	        double x1 = (- 1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
	        double y1 = (- 1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;
	        //point2
	        double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + endX;
	        double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + endY;
	        
	        getElements().add(new LineTo(x1, y1));
	        getElements().add(new LineTo(x2, y2));
	        getElements().add(new LineTo(endX, endY));
	    }
	    
	    public Arrow(double startX, double startY, double endX, double endY){
	        this(startX, startY, endX, endY, defaultArrowHeadSize);
	    }
	}

}


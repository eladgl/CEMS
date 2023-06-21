package studentUi;

import java.time.LocalTime;
import java.util.ArrayList;

import Client.CEMSClientUI;
import entities.Exam;
import entities.Question;
import entities.StudentsDataInExam;
import utilities.CONSTANTS;
import utilities.Message;

public class SendStudentDataInExamThread extends Thread {

	private int grade;
	private ArrayList<String> answers;
	private String examType;
	private Exam exam;
	private LocalTime examStartTime;
	private int timeDelayInSeconds;



	public SendStudentDataInExamThread(int grade, ArrayList<String> answers, String examType, Exam exam, LocalTime examStartTime,
			int timeDelayInSeconds) {
		this.grade = grade;
		this.answers = answers;
		this.examType = examType;
		this.exam = exam;
		this.examStartTime = examStartTime;
		this.timeDelayInSeconds = timeDelayInSeconds;
	}

	@Override
	public void run() {
		
		ArrayList<String> correct_questions_answers = new ArrayList<>();
		for(Question q : exam.getQuestions()) {
			correct_questions_answers.add(String.valueOf(q.getCorrectAnswer()));
		}

		/*
		 * Converting Locale Time into A Duration Object.
		 */
		System.out.println("Sending Data EXAM to DB");
		
		if(this.examStartTime == null) { // if he didn't even start the exam and the lecturer locked it
			this.examStartTime = LocalTime.now();
			this.examStartTime = this.examStartTime.minusSeconds(20);// minus 20 seconds for more accurate time.
		}
		java.time.Duration exam_start_time_duration = java.time.Duration.ofHours(this.examStartTime.getHour());
		exam_start_time_duration = exam_start_time_duration.plusMinutes(this.examStartTime.getMinute());
		exam_start_time_duration = exam_start_time_duration.plusSeconds(this.examStartTime.getSecond());

		LocalTime exam_end_time = LocalTime.now();

		java.time.Duration exam_end_time_duration = java.time.Duration.ofHours(exam_end_time.getHour());
		exam_end_time_duration = exam_end_time_duration.plusMinutes(exam_end_time.getMinute());
		exam_end_time_duration = exam_end_time_duration.plusSeconds(exam_end_time.getSecond());

		StudentsDataInExam student_data = new StudentsDataInExam(studentMainScreenController.user.getUserName(),
				studentMainScreenController.user.getIDNumber(),
				String.valueOf(startTestScreenController.relevantExams.getConduct_exam_id()), this.grade,
				exam_start_time_duration, exam_end_time_duration, exam.getDuration(),
				String.valueOf(this.timeDelayInSeconds), exam.getQuestions(), null, this.answers, true, null,
				correct_questions_answers, this.examType,
				startTestScreenController.relevantExams.getConduct_exam_name(), exam.getCourse());

		try {
			CEMSClientUI.chat.accept(new Message(CONSTANTS.InsertStudentExamDataIntoDB, student_data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done SENDING");
	}

}

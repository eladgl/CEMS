package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class StatisticsReportDataForLecturer implements Serializable{
	private static final long serialVersionUID = 55L;
	private int conduct_exam_id;
	private String conduct_exam_name;
	private String course_name;
	private String username_exam_conductor;
	private int number_of_students;
	private int[] gradeCountInEachCategory;
	private float median;
	private float average;
	private int max_grade;
	private int min_grade;
	
	
	public StatisticsReportDataForLecturer(int conduct_exam_id, String conduct_exam_name, String course_name,
			String username_exam_conductor, int number_of_students, int[] gradeCountInEachCategory, float median,
			float average, int max_grade, int min_grade) {
		super();
		this.conduct_exam_id = conduct_exam_id;
		this.conduct_exam_name = conduct_exam_name;
		this.course_name = course_name;
		this.username_exam_conductor = username_exam_conductor;
		this.number_of_students = number_of_students;
		this.gradeCountInEachCategory = gradeCountInEachCategory;
		this.median = median;
		this.average = average;
		this.max_grade = max_grade;
		this.min_grade = min_grade;
	}
	public int getConduct_exam_id() {
		return conduct_exam_id;
	}


	public void setConduct_exam_id(int conduct_exam_id) {
		this.conduct_exam_id = conduct_exam_id;
	}


	public String getConduct_exam_name() {
		return conduct_exam_name;
	}


	public void setConduct_exam_name(String conduct_exam_name) {
		this.conduct_exam_name = conduct_exam_name;
	}


	public String getCourse_name() {
		return course_name;
	}


	public void setCourse_name(String course_name) {
		this.course_name = course_name;
	}


	public String getUsername_exam_conductor() {
		return username_exam_conductor;
	}


	public void setUsername_exam_conductor(String username_exam_conductor) {
		this.username_exam_conductor = username_exam_conductor;
	}


	public int getNumber_of_students() {
		return number_of_students;
	}


	public void setNumber_of_students(int number_of_students) {
		this.number_of_students = number_of_students;
	}


	public int[] getGradeCountInEachCategory() {
		return gradeCountInEachCategory;
	}


	public void setGradeCountInEachCategory(int[] gradeCountInEachCategory) {
		this.gradeCountInEachCategory = gradeCountInEachCategory;
	}


	public float getMedian() {
		return median;
	}


	public void setMedian(float median) {
		this.median = median;
	}


	public float getAverage() {
		return average;
	}


	public void setAverage(float average) {
		this.average = average;
	}


	public int getMax_grade() {
		return max_grade;
	}


	public void setMax_grade(int max_grade) {
		this.max_grade = max_grade;
	}


	public int getMin_grade() {
		return min_grade;
	}


	public void setMin_grade(int min_grade) {
		this.min_grade = min_grade;
	}	
	
}

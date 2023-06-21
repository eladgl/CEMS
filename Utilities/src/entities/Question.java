package entities;

import java.io.Serializable;
/**
 * The Question class represents a question entity.
 * It implements the Serializable interface for object serialization.
 */
public class Question implements Serializable
{
	private static final long serialVersionUID = 2L;
	
	private  String ID;
	private  String questionDescription;
	private  String subject;
	private  String subjectCode;
	private  String courseName;
	private  String questionNumber;
	private  String authorName;
	private  String authorID= "";
	private  String[] optionsText = null; // can be only 4 text for each option
	private  int correctAnswer =0; // a b c d
	/**
     * Constructs a Question object with the specified parameters.
     *
     * @param iD                  the ID of the question
     * @param subject             the subject of the question
     * @param subjectCode         the subject code of the question
     * @param coursName           the course name of the question
     * @param questionDescription the description of the question
     * @param questionNumber      the number of the question
     * @param authorName          the name of the author
     */
	public Question(String iD, String subject,String subjectCode, String coursName ,String questionDescription, String questionNumber,String authorName) 
	{
		super();
		ID = iD;
		this.questionDescription = questionDescription;
		this.subject = subject;
		this.subjectCode=subjectCode;
		this.courseName = coursName;
		this.questionNumber = questionNumber;
		this.authorName = authorName;
		
	}
	/**
     * Copy constructor.
     *
     * @param q the Question object to be copied
     */
	public Question(Question q) {
		ID = q.getID();
		this.questionDescription = q.getQuestionDescription();
		this.subject = q.getSubject();
		this.subjectCode=q.getSubjectCode();
		this.courseName = q.getCourseName();
		this.questionNumber = q.getQuestionNumber();
		this.authorName = q.getAuthorName();
		this.optionsText = q.getOptionsText();
		this.correctAnswer = q.getCorrectAnswer();
	}
	/**
     * Constructs a Question object with the specified parameters.
     *
     * @param iD                  the ID of the question
     * @param questionDescription the description of the question
     * @param subject             the subject of the question
     * @param subjectCode         the subject code of the question
     * @param coursName           the course name of the question
     * @param questionNumber      the number of the question
     * @param authorName          the name of the author
     * @param authorID            the ID of the author
     * @param optionsText         the options text for the question
     * @param correctAnswer       the index of the correct answer
     */
	public Question(String iD, String questionDescription, String subject, String subjectCode,String coursName, String questionNumber,String authorName, String authorID, String[] optionsText, int correctAnswer) 
	{
		super();
		ID = iD;
		this.questionDescription = questionDescription;
		this.subject = subject;
		this.subjectCode=subjectCode;
		this.courseName = coursName;
		this.questionNumber = questionNumber;
		this.authorName = authorName;
		this.optionsText = optionsText;
		this.correctAnswer = correctAnswer;
	}
	/**
     * Returns the ID of the question.
     *
     * @return the ID of the question
     */
	public String getID() {
		return ID;
	}
	/**
     * Returns the description of the question.
     *
     * @return the description of the question
     */
	public String getQuestionDescription() 
	{
		return questionDescription;
	}
	/**
     * Returns the subject of the question.
     *
     * @return the subject of the question
     */
	public String getSubject() 
	{
		return subject;
	}
	/**
     * Returns the subject code of the question.
     *
     * @return the subject code of the question
     */
	public String getSubjectCode()
	{
		return subjectCode;
	}
	/**
     * Returns the course name of the question.
     *
     * @return the course name of the question
     */
	public String getCourseName() 
	{
		return courseName;
	}
	/**
     * Returns the number of the question.
     *
     * @return the number of the question
     */
	public String getQuestionNumber() 
	{
		return questionNumber;
	}
	/**
     * Returns the name of the author.
     *
     * @return the name of the author
     */
	public String getAuthorName() 
	{
		return authorName;
	}
	/**
     * Returns the ID of the author.
     *
     * @return the ID of the author
     */
	public String getAuthorID() 
	{
		return authorID;
	}
	/**
     * Returns the options text for the question.
     *
     * @return the options text for the question
     */
	public String[] getOptionsText() 
	{
		return optionsText;
	}
	/**
     * Returns the index of the correct answer.
     *
     * @return the index of the correct answer
     */
	public int getCorrectAnswer() 
	{
		return correctAnswer;
	}
	/**
     * Returns the first options text for the question.
     *
     * @return the first options text for the question
     */
	public String getOptionsText1() 
	{
		return optionsText[0];
	}
	/**
     * Returns the second options text for the question.
     *
     * @return the second options text for the question
     */
	public String getOptionsText2() 
	{
		return optionsText[1];
	}
	/**
     * Returns the third options text for the question.
     *
     * @return the third options text for the question
     */
	public String getOptionsText3() 
	{
		return optionsText[2];
	}
	/**
     * Returns the fourth options text for the question.
     *
     * @return the fourth options text for the question
     */
	public String getOptionsText4() 
	{
		return optionsText[3];
	}
	/**
     * Compares this Question object to another object for equality.
     *
     * @param other the object to compare
     * @return true if the objects are equal, false otherwise
     */
	@Override
	public boolean equals(Object other)
	{
		if(! (other instanceof Question))
			return false;
		else
		{
			Question otherQuestion = (Question)other;
			return this.ID.equals(otherQuestion.getID());
		}	 
	}
	/**
     * Returns a string representation of the Question object.
     *
     * @return a string representation of the Question object
     */
	@Override
	public String toString() {
		return "Number=" + questionNumber + ", question=" + questionDescription +  ", subject=" + subject
				+ ", coursName=" + courseName;
	}	
}
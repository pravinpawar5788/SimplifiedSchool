package com.simplifiedschooling.app.helper;

public class HomeworkData {

	private String studentName;
	private String subject;
	private String givenon;
	private String homeworkDecription;
	private String attachementPath;
	private String submissionDate;

	public HomeworkData(String studentName,String subject,String givenon,String homeworkDecription, String attachementPath,
			String submissionDate) {
		super();
       
		this.studentName = studentName;
		this.subject = subject;
		this.givenon = givenon;
		this.homeworkDecription = homeworkDecription;
		this.attachementPath = attachementPath;
		this.submissionDate = submissionDate;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getGivenon() {
		return givenon;
	}

	public void setGivenon(String givenon) {
		this.givenon = givenon;
	}

	public String getHomeworkDecription() {
		return homeworkDecription;
	}

	public void setHomeworkDecription(String homeworkDecription) {
		this.homeworkDecription = homeworkDecription;
	}

	public String getAttachementPath() {
		return attachementPath;
	}

	public void setAttachementPath(String attachementPath) {
		this.attachementPath = attachementPath;
	}

	public String getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(String submissionDate) {
		this.submissionDate = submissionDate;
	}
}

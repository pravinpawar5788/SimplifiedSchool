package com.simplifiedschooling.app.helper;

public class StudentData {
	private String studentFName;
	private String studentLName;
	private String studentId;
	private String studentPhoto;

	public StudentData(String studentFName, String studentLName,
			String studentId, String studentPhoto) {
		super();
		this.studentFName = studentFName;
		this.studentLName = studentLName;
		this.studentId = studentId;
		this.studentPhoto = studentPhoto;
	}

	public StudentData() {
		// TODO Auto-generated constructor stub
	}

	public String getStudentPhoto() {
		return studentPhoto;
	}

	public void setStudentPhoto(String studentPhoto) {
		this.studentPhoto = studentPhoto;
	}

	public String getStudentFName() {
		return studentFName;
	}

	public void setStudentFName(String studentFName) {
		this.studentFName = studentFName;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getStudentLName() {
		return studentLName;
	}

	public void setStudentLName(String studentLName) {
		this.studentLName = studentLName;
	}

}

package com.simplifiedschooling.app.adapter;

public class StudentList {
private String studentFName;
private String studentLName;
private String studentId;
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

public StudentList(String studentFName, String studentLName,String studentId) {
	super();
	this.studentFName = studentFName;
	this.studentLName = studentLName;
	this.studentId = studentId;
}
}

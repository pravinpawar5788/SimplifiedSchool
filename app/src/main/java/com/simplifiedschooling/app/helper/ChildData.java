package com.simplifiedschooling.app.helper;

public class ChildData {
private String s_id;
private String s_classdivId;
private String s_FirstName;
private String s_LastName;
public String getS_id() {
	return s_id;
}
public void setS_id(String s_id) {
	this.s_id = s_id;
}
public String getS_classdivId() {
	return s_classdivId;
}
public void setS_classdivId(String s_classdivId) {
	this.s_classdivId = s_classdivId;
}
public String getS_FirstName() {
	return s_FirstName;
}
public void setS_FirstName(String s_FirstName) {
	this.s_FirstName = s_FirstName;
}
public ChildData(String s_id, String s_classdivId, String s_FirstName,
		String s_LastName) {
	super();
	this.s_id = s_id;
	this.s_classdivId = s_classdivId;
	this.s_FirstName = s_FirstName;
	this.s_LastName = s_LastName;
}
public String getS_LastName() {
	return s_LastName;
}
public void setS_LastName(String s_LastName) {
	this.s_LastName = s_LastName;
}
}

package com.simplifiedschooling.app.helper;

public class AssignmentData {

	private String assignmentDecription;
	private String assignmentPath;
	private String assignmentsubmissionDate;

	public String getAssignmentDecription() {
		return assignmentDecription;
	}

	public void setAssignmentDecription(String assignmentDecription) {
		this.assignmentDecription = assignmentDecription;
	}

	public String getAssignmentPath() {
		return assignmentPath;
	}

	public void setAssignmentPath(String assignmentPath) {
		this.assignmentPath = assignmentPath;
	}

	public String getAssignmentsubmissionDate() {
		return assignmentsubmissionDate;
	}

	public void setAssignmentsubmissionDate(String assignmentsubmissionDate) {
		this.assignmentsubmissionDate = assignmentsubmissionDate;
	}

	public AssignmentData(String assignmentDecription,
			String assignmentPath, String assignmentsubmissionDate) {
		super();
	
		this.assignmentDecription = assignmentDecription;
		this.assignmentPath = assignmentPath;
		this.assignmentsubmissionDate = assignmentsubmissionDate;
	}
}

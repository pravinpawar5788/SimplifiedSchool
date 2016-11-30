package com.simplifiedschooling.app.util;

public interface Config {

	// used to share GCM regId with application server - using php app server
	static final String APP_SERVER_URL = "http://127.0.0.1/gcm.php?shareRegId=1";

	// GCM server using java
	// static final String APP_SERVER_URL =
	// "http://192.168.1.17:8080/GCM-App-Server/GCMNotification?shareRegId=1";

	// Google Project Number
	static final String GOOGLE_PROJECT_ID = "208651238201";
	static final String MESSAGE_KEY = "m";
	static final String MESSAGE_DATE = "date";
	static final String MESSAGE_SUBJECT = "subject";
	public static final boolean DEVELOPER_MODE = false;

	

}
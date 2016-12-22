package com.simplifiedschooling.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;
import com.simplifiedschooling.app.util.NetworkUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Teacher_ClassAnnouncement extends ActionBarActivity {
	private static final String TAG = Teacher_ClassAnnouncement.class
			.getSimpleName();
	private String BASEURL = AppConfig.BASE_URL;
	String Username, jsonStr = null, dateS1, dateS2;
	private ProgressDialog pDialog;
	TextView usernameTxt;
	TextView filename;
	ListView lv;
	ImageButton backBtn;
	ImageView iv;
	CheckBox tch, pch, sch;
	EditText dateEdit, announcementEdit, sub;
	private int year;
	Spinner tclass, tdiv;
	private int month;
	private int day;
	static final int DATE_PICKER_ID = 1111;
	ArrayList<String> calssList = new ArrayList<String>();
	ArrayList<String> classId = new ArrayList<String>();
	ArrayList<String> divList = new ArrayList<String>();
	ArrayList<String> classDivIdList = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				Drawable.createFromPath(this.getExternalFilesDir(null)
						.getAbsolutePath() + "/" + "innerpage_top.png"));
		setContentView(R.layout.teacher_class_announcement);
		/*
		 * Bundle b = getIntent().getExtras(); Username =
		 * b.getString("Username"); usernameTxt = (TextView)
		 * findViewById(R.id.textUsername); usernameTxt.setText("   Welcome[" +
		 * Username + "]");
		 */

		iv = (ImageView) findViewById(R.id.calender);
		iv.setOnClickListener(new DateOnClickListener());
		dateEdit = (EditText) findViewById(R.id.editText1);
		dateEdit.setOnClickListener(new DateOnClickListener());
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		tclass = (Spinner) findViewById(R.id.spinnerclass);
		tdiv = (Spinner) findViewById(R.id.spinnerdiv);
		tclass.setOnItemSelectedListener(new spclassListener());
		
		sub = (EditText) findViewById(R.id.editTextSubject);
		announcementEdit = (EditText) findViewById(R.id.editTextAnnounce);
		Button submitBtn = (Button) findViewById(R.id.button1);
		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		submitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (dateEdit.getText().toString().equals("")) {
					mt("Please enter date!");
				} else if (sub.getText().toString().equals("")) {
					mt("Please enter subject!");
				} else if (announcementEdit.getText().toString().equals("")) {
					mt("Please enter Announcement!");
				} else if (NetworkUtilities.isInternet(getBaseContext())) {

					// new insertAnnouncementTask().execute();
					InsertAnnouncementTask();
				} else {
					Toast.makeText(getBaseContext(), "No network",
							Toast.LENGTH_LONG).show();
				}

			}
		});
		getClassList();
	}

	
	private void getClassList() {
		// TODO Auto-generated method stub
		String tag_string_req = "req_login";

		pDialog.setMessage("Please wait ...");
		showDialog();

		StringRequest strReq = new StringRequest(Request.Method.GET,
				AppConfig.BASE_URL + "classlists",
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Login Response: " + response.toString());
						hideDialog();

						try {

							JSONObject json = new JSONObject(response);
							JSONObject json1 = json.getJSONObject("response");
							JSONArray calssdetails = json1
									.getJSONArray("classinfo");

							for (int i = 0; i < calssdetails.length(); i++) {

								JSONObject c = calssdetails.getJSONObject(i);

								// no_student=c1.getString("SubjectName");
								String class_name = c.getString("classname");
								String class_Id = c.optString("classid");

								calssList.add(class_name);
								classId.add(class_Id);
								if(calssList.size()>0) {
									ArrayAdapter<String> classlistadapter = new ArrayAdapter<String>(
											Teacher_ClassAnnouncement.this,
											android.R.layout.simple_spinner_item,
											calssList);

									tclass.setAdapter(classlistadapter);

									classlistadapter.notifyDataSetChanged();
								}else{
									Toast.makeText(getApplicationContext(),
											"There is no class", Toast.LENGTH_LONG).show();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Data Error: " + error.getMessage());
						Toast.makeText(getApplicationContext(),
								error.getMessage(), Toast.LENGTH_LONG).show();
						hideDialog();
					}
				});

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

	}

	private class spclassListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

			// new getDivListTask().execute();
			GetDivListTask();
		}

		private void GetDivListTask() {
			// TODO Auto-generated method stub
			String tag_string_req = "req_login";
			classDivIdList.clear();
			divList.clear();
			String class_id = null;

			int pos = tclass.getSelectedItemPosition();
			class_id = classId.get(pos).toString();
			pDialog.setMessage("Please wait ...");
			showDialog();

			StringRequest strReq = new StringRequest(Request.Method.GET,
					AppConfig.BASE_URL + "showdivisionlists/" + class_id,
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							Log.d(TAG, "Login Response: " + response.toString());
							hideDialog();

							try {

								JSONObject json = new JSONObject(response);
								JSONObject json1 = json
										.getJSONObject("response");
								JSONArray calssdetails = json1
										.getJSONArray("users");

								for (int i = 0; i < calssdetails.length(); i++) {

									JSONObject c = calssdetails
											.getJSONObject(i);

									// no_student=c1.getString("SubjectName");
									String division = c.getString("divname");
									String classdivId = c
											.getString("classdivid");

									// Constant.classdiv.add(new
									// ClassDiv(class_name, division));
									divList.add(division);
									classDivIdList.add(classdivId);

									ArrayAdapter<String> classlistadapter = new ArrayAdapter<String>(
											Teacher_ClassAnnouncement.this,
											android.R.layout.simple_spinner_item,
											divList);

									tdiv.setAdapter(classlistadapter);

									classlistadapter.notifyDataSetChanged();

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Log.e(TAG, "Data Error: " + error.getMessage());
							Toast.makeText(getApplicationContext(),
									error.getMessage(), Toast.LENGTH_LONG)
									.show();
							hideDialog();
						}
					});

			// Adding request to request queue
			AppController.getInstance().addToRequestQueue(strReq,
					tag_string_req);

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

	protected void InsertAnnouncementTask() {
		// TODO Auto-generated method stub
		String tag_string_req = "Req_InsertGenAnnouncementTask";
		pDialog.setMessage("Please wait ...");
		showDialog();

		StringRequest strReq = new StringRequest(Request.Method.POST,
				BASEURL + "classannouncementacts",
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Login Response: " + response.toString());
						hideDialog();

						dateEdit.setText("");
						
						sub.setText("");
						announcementEdit.setText("");
						mt("Successfully Posted");

					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Data Error: " + error.getMessage());
						Toast.makeText(getApplicationContext(),
								error.getMessage(), Toast.LENGTH_LONG).show();
						hideDialog();
					}
				}) {

			@Override
			protected Map<String, String> getParams() {

				
				int pos=tdiv.getSelectedItemPosition();
				String ClassDivID=classDivIdList.get(pos).toString();
				// String dateStr = dateEdit.getText().toString();
				String subStr = sub.getText().toString();
				String AnnounceStr = announcementEdit.getText().toString();
				
				// Posting parameters to login url
				Map<String, String> params = new HashMap<String, String>();

				params.put("fstart", dateS1.trim().toString());
				params.put("subject", subStr);
				params.put("announcement", AnnounceStr);
				params.put("classdivid", ClassDivID);
				

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

	}

	private void mt(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	private class DateOnClickListener implements OnClickListener {

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			showDialog(DATE_PICKER_ID);

		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_PICKER_ID:

			// open datepicker dialog.
			// set date picker for current date
			// add pickerListener listner to date picker
			return new DatePickerDialog(this, pickerListener, year, month, day);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			// Show selected date
			dateS1 = year + "-" + (month + 1) + "-" + day;
			dateEdit.setText(dateS1);
		}
	};

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}
}

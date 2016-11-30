package com.simplifiedschooling.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.simplifiedschooling.app.adapter.CustomListAdapter;
import com.simplifiedschooling.app.helper.StudentData;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;
import com.simplifiedschooling.app.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllStudentList extends ActionBarActivity {
	private static final String TAG = AllStudentList.class.getSimpleName();
	private ProgressDialog pDialog;
	private String User_id, Role_id, id, jsonStr = null;
	Spinner tclass;
	ArrayList<String> calssList = new ArrayList<String>();
	ArrayList<String> calssDivIdList = new ArrayList<String>();
	private CustomListAdapter adapter;
	private ListView mStudentList;
	private List<StudentData> studentInfo = new ArrayList<StudentData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.studentlist);
		User_id = getIntent().getExtras().getString("userid");
		Role_id = getIntent().getExtras().getString("roleid");
		id = getIntent().getExtras().getString("id");
		tclass = (Spinner) findViewById(R.id.spinnerClass);
		// new getClassAttendence().execute();
		tclass.setOnItemSelectedListener(new TeacherclassListener());
		mStudentList = (ListView) findViewById(R.id.studentList);
		mStudentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				String Student_Id = null;
				Student_Id = studentInfo.get(arg2).getStudentId().toString();
				Intent i = new Intent(AllStudentList.this,
						Student_details.class);

				i.putExtra("Student_Id", Student_Id);


				startActivity(i);

			}
		});

		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		GetClassAttendence();
		// mStudentList.setAdapter(adapter);

	}

	private void GetClassAttendence() {
		// TODO Auto-generated method stub
		// Tag used to cancel the request
		String tag_string_req = "req_login";

		pDialog.setMessage("Please wait ...");
		showDialog();

		StringRequest strReq = new StringRequest(Request.Method.GET,
				AppConfig.BASE_URL + "classesofteachers/" + id,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Login Response: " + response.toString());
						hideDialog();

						try {

							JSONObject json = new JSONObject(response);
							JSONObject resp = json.getJSONObject("response");
							JSONArray calssdetails = resp
									.getJSONArray("classinfo");
							for (int i = 0; i < calssdetails.length(); i++) {

								JSONObject c = calssdetails.getJSONObject(i);

								String classdivid = c.getString("classdivid");
								String classname = c.getString("classname");
								String divname = c.getString("divname");
								calssList.add(classname + " " + divname);
								calssDivIdList.add(classdivid);
								ArrayAdapter<String> classlistadapter = new ArrayAdapter<String>(
										AllStudentList.this,
										android.R.layout.simple_spinner_item,
										calssList);
								tclass.setAdapter(classlistadapter);
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

	private class TeacherclassListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			// submit.setText("Submit");
			// Constant.studentData.clear();

			// new getStudentDetailsList().execute();
			GetStudentDetailsList();

		}

		private void GetStudentDetailsList() {
			// TODO Auto-generated method stub
			String tag_string_req = "req_login";
			Constant.studentData.clear();
			int pos = tclass.getSelectedItemPosition();
			String classDivId = calssDivIdList.get(pos).toString();
			pDialog.setMessage("Please wait ...");
			showDialog();

			StringRequest strReq = new StringRequest(Request.Method.GET,
					AppConfig.BASE_URL + "studentalllists/" + classDivId,
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							Log.d(TAG, "Login Response: " + response.toString());
							hideDialog();

							try {

								JSONObject json = new JSONObject(response);

								JSONObject calssdetails = json
										.getJSONObject("response");

								JSONArray calssdetailsArray = calssdetails
										.getJSONArray("userslist");

								for (int k = 0; k < calssdetailsArray.length(); k++) {
									JSONObject c1 = calssdetailsArray
											.getJSONObject(k);
									StudentData movie = new StudentData();
									movie.setStudentFName(c1
											.getString("firstname"));
									movie.setStudentLName(c1
											.getString("lastname"));
									movie.setStudentPhoto(c1.getString("photo"));
									movie.setStudentId(c1.getString("user_id"));

									// adding movie to movies array
									studentInfo.add(movie);
									if (studentInfo.size() == 0) {
										// submit.setVisibility(View.INVISIBLE);
										Toast.makeText(AllStudentList.this,
												"There is no student!",
												Toast.LENGTH_LONG).show();

										// adapter.notifyDataSetChanged();
									} else if (studentInfo.size() > 0) {
										// submit.setVisibility(View.VISIBLE);
										// mStudentList.setAdapter(new
										// ListAdapter(AllStudentList.this));
										adapter = new CustomListAdapter(
												AllStudentList.this,
												studentInfo);
										mStudentList.setAdapter(adapter);
										adapter.notifyDataSetChanged();
									}

								}
								// adapter.notifyDataSetChanged();

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
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}

}

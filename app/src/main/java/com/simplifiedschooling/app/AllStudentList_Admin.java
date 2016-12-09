package com.simplifiedschooling.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.simplifiedschooling.app.adapter.CustomListAdapter;
import com.simplifiedschooling.app.helper.ListItem;
import com.simplifiedschooling.app.helper.StudentData;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class AllStudentList_Admin extends ActionBarActivity {
	private static final String TAG = AllStudentList_Admin.class
			.getSimpleName();

	private ProgressDialog pDialog;
	String busNo, jsonStr = null, Username, mUsername;
	ListView studentList;
	TextView boardingPointTxt, authPersonTxt, usernameTxt;
	// DisplayImageOptions options;
	Button submitBtn;
	ImageButton backBtn;
	Spinner tclass, tdiv;
	private CustomListAdapter adapter;
	ArrayList<ListItem> gridArray = new ArrayList<ListItem>();
	// protected ImageLoader imageLoader = ImageLoader.getInstance();
	ArrayList<String> checkList = new ArrayList<String>();
	ArrayList<ListItem> data = new ArrayList<ListItem>();
	ArrayList<String> calssList = new ArrayList<String>();
	ArrayList<String> classId = new ArrayList<String>();
	ArrayList<String> divList = new ArrayList<String>();
	ArrayList<String> classDivIdList = new ArrayList<String>();
	ArrayList<String> StudentId = new ArrayList<String>();
	private static String classdivId = null;
	private List<StudentData> studentInfo = new ArrayList<StudentData>();
	private ListView mStudentList;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		studentInfo.clear();
		Intent i = new Intent(AllStudentList_Admin.this,
				AdminHomeActivity.class);
		startActivity(i);
		finish();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		studentInfo.clear();
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				Drawable.createFromPath(this.getExternalCacheDir()
						.getAbsolutePath() + "/" + "innerpage_top.png"));
		setContentView(R.layout.admin_studentlist);
		/*
		 * Bundle b = getIntent().getExtras(); Username =
		 * b.getString("Username"); mUsername = b.getString("mUsername");
		 * usernameTxt = (TextView) findViewById(R.id.textUsername);
		 * 
		 * usernameTxt.setText("   Welcome[" + mUsername + "]");
		 */

		tclass = (Spinner) findViewById(R.id.spinnerClass);
		tdiv = (Spinner) findViewById(R.id.spinnerStudent);
		tclass.setOnItemSelectedListener(new spclassListener());
		tdiv.setOnItemSelectedListener(new spdivListener());

		/*
		 * options = new DisplayImageOptions.Builder()
		 * .showImageOnLoading(R.drawable.ic_stub)
		 * .showImageForEmptyUri(R.drawable.ic_empty)
		 * .showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
		 * .cacheOnDisk(true).considerExifParams(true)
		 * .bitmapConfig(Bitmap.Config.RGB_565).build();
		 */
		mStudentList = (ListView) findViewById(R.id.studentList);

		mStudentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				String Student_Id = null;
				Student_Id = studentInfo.get(arg2).getStudentId().toString();
				Intent i = new Intent(AllStudentList_Admin.this,
						Student_details.class);

				i.putExtra("Student_Id", Student_Id);


				startActivity(i);

			}
		});
		// studentList.setOnItemClickListener(this);
		// new getClassListTask().execute();
		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
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
								ArrayAdapter<String> classlistadapter = new ArrayAdapter<String>(
										AllStudentList_Admin.this,
										android.R.layout.simple_spinner_item,
										calssList);

								tclass.setAdapter(classlistadapter);

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
								error.getMessage(), Toast.LENGTH_LONG).show();
						hideDialog();
					}
				});

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

	}

	private class spdivListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

			// new getStudentDetailsList().execute();
			GetStudentDetailsList();
		}

		private void GetStudentDetailsList() {
			// TODO Auto-generated method stub
			String tag_string_req = "req_login";
			studentInfo.clear();
			int pos = tclass.getSelectedItemPosition();
			String classDivId = classDivIdList.get(pos).toString();
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
										Toast.makeText(
												AllStudentList_Admin.this,
												"There is no student!",
												Toast.LENGTH_LONG).show();

										// adapter.notifyDataSetChanged();
									} else if (studentInfo.size() > 0) {
										// submit.setVisibility(View.VISIBLE);
										// mStudentList.setAdapter(new
										// ListAdapter(AllStudentList.this));
										adapter = new CustomListAdapter(
												AllStudentList_Admin.this,
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
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

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
											AllStudentList_Admin.this,
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		finish();
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

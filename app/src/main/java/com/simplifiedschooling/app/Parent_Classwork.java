package com.simplifiedschooling.app;

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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.simplifiedschooling.app.adapter.CustomHomeworkAdapter;
import com.simplifiedschooling.app.helper.ChildData;
import com.simplifiedschooling.app.helper.HomeworkData;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;
import com.simplifiedschooling.app.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Parent_Classwork extends ActionBarActivity {
	private static final String TAG = Class_Homework.class.getSimpleName();
	private String BASEURL = AppConfig.BASE_URL;
	private String User_id, Role_id, id;
	private ProgressDialog pDialog;
	private int year;
	private int month;
	private int day;
	ListView homeworkList, weekhomeworkList, ArchivehomeworkList;
	Spinner studentList, weekList;
	private TabHost myTabHost;
	DateFormat formatter = null;
	CustomHomeworkAdapter homeworkAdapter;
	ArrayList<String> student_name = new ArrayList<String>();
	ArrayList<String> weeks = new ArrayList<String>();
	ArrayList<String> weekNo = new ArrayList<String>();
	ArrayList <HomeworkData> archivehomeworkdata= Constant.homeworkdata;
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub



		super.onSaveInstanceState(savedInstanceState);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				Drawable.createFromPath(this.getExternalCacheDir()
						.getAbsolutePath() + "/" + "innerpage_top.png"));
		setContentView(R.layout.parent_classwork);
		User_id = getIntent().getExtras().getString("userid");
		Role_id = getIntent().getExtras().getString("roleid");
		id = getIntent().getExtras().getString("id");
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		myTabHost = (TabHost) findViewById(R.id.TabHost01);
		myTabHost.setup();
		TabSpec spec = myTabHost.newTabSpec("Today's Classwork");
		spec.setContent(R.id.tabTodayshomwork);
		spec.setIndicator("Today's Classwork");
		myTabHost.addTab(spec);
		TabSpec spec1 = myTabHost.newTabSpec("Classwork This week");
		spec1.setContent(R.id.tabWeekhomework);
		spec1.setIndicator("Classwork This week");
		myTabHost.addTab(spec1);

		TabSpec spec2 = myTabHost.newTabSpec("Archive");
		spec2.setContent(R.id.tabArchivehomework);
		spec2.setIndicator("Archive");
		myTabHost.addTab(spec2);
		myTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				Log.i("selected tab index", "Current index - "+tabId);
			}
		});
		myTabHost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId() == R.id.tabTodayshomwork) {
					GetTodaysHomework(); 

				} else if (v.getId() == R.id.tabWeekhomework) {
					GetWeekHomework();

				} else if (v.getId() == R.id.tabArchivehomework) {
					// GetArichiveHomework();
					GetStudentList();
				}
			}
		});

		homeworkList = (ListView) findViewById(R.id.homeworklist);
		weekhomeworkList = (ListView) findViewById(R.id.homeworklist1);
		ArchivehomeworkList = (ListView) findViewById(R.id.homeworklist2);
		studentList = (Spinner) findViewById(R.id.selectStudent);
		weekList = (Spinner) findViewById(R.id.selectWeek);
		weekList.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				GetArichiveHomework(position);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});





		GetTodaysHomework();
		GetWeekHomework();
		GetStudentList();
		Getweek();
	}

	protected void GetArichiveHomework(final int position) {
		// TODO Auto-generated method stub
		String tag_string_req = "Get Archive Classwork ";

		pDialog.setMessage("Please wait ...");
		showDialog();
		archivehomeworkdata.clear();

		StringRequest strReq = new StringRequest(
				Request.Method.POST,
				AppConfig.BASE_URL+"weekwiseclassworkmobdatas",
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Login Response: " + response.toString());
						hideDialog();

						try {

							JSONObject json = new JSONObject(response);
							JSONObject resp = json.getJSONObject("response");
							JSONArray data = resp.getJSONArray("weeks");
							for (int i = 0; i < data.length(); i++) {

								JSONObject c = data.getJSONObject(i);

								String studentfirstname = c
										.getString("firstname");
								String studentlastname = c
										.getString("lastname");
								String studentfullname=studentfirstname+" "+studentlastname;
								String subjectname = c.getString("subjectname");
								String givendate1 = c.getString("givendate");
								String submissiondate1 = c
										.getString("submissiondate");
								String homeworkdescription = c
										.getString("homeworkdescription");
								String homeworkattachpath = c
										.getString("homeworkattachpath");
								String dates[] = submissiondate1.split("T");
								String submissiondate = dates[0];
								String dates1[] = givendate1.split("T");
								String givendate = dates1[0];
								archivehomeworkdata.add(new HomeworkData(
										studentfullname, subjectname,
										givendate, homeworkdescription,
										homeworkattachpath, submissiondate));

								if (archivehomeworkdata.size() == 0) {
									// submit.setVisibility(View.INVISIBLE);
									Toast.makeText(Parent_Classwork.this,
											"There is no Data!",
											Toast.LENGTH_LONG).show();

									// adapter.notifyDataSetChanged();
								} else if (archivehomeworkdata.size() > 0) {
									// submit.setVisibility(View.VISIBLE);
									// mStudentList.setAdapter(new
									// ListAdapter(AllStudentList.this));
									homeworkAdapter = new CustomHomeworkAdapter(
											Parent_Classwork.this,
											archivehomeworkdata);
									ArchivehomeworkList
											.setAdapter(homeworkAdapter);
									homeworkAdapter.notifyDataSetChanged();
								}

								/*
								 * calssList.add(classname + " " + divname);
								 * calssDivIdList.add(classdivid);
								 * ArrayAdapter<String> classlistadapter = new
								 * ArrayAdapter<String>( Class_Homework.this,
								 * android.R.layout.simple_spinner_item,
								 * calssList);
								 * tclass.setAdapter(classlistadapter);
								 */
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
				}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting parameters to login url
				int pos=studentList.getSelectedItemPosition();
				Map<String, String> params = new HashMap<String, String>();
				params.put("classdivid", Constant.childData.get(pos).getS_classdivId());
				params.put("weekno", weekNo.get(position));
				params.put("id", User_id);
				Log.e("Param",params.toString());

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

	}

	protected void Getweek() {
		// TODO Auto-generated method stub
		String tag_string_req = "Weeklist";
		weeks.clear();
		weekNo.clear();
		pDialog.setMessage("Please wait ...");
		showDialog();

		StringRequest strReq = new StringRequest(
				Request.Method.GET,
				AppConfig.BASE_URL+"weeks",
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
									.getJSONArray("weeks");

							for (int k = 0; k < calssdetailsArray.length(); k++) {
								JSONObject c1 = calssdetailsArray
										.getJSONObject(k);

								String weekno = c1.getString("weekno");
								String fromdate = c1.getString("fromdate");
								String fDate[] = fromdate.split("T");
								String fdate = fDate[0];
								String sdate[] = fdate.split("-");
								String fyear = sdate[0];
								String fmonth = sdate[1];
								String fday = sdate[2];
								Date fFdate = new Date(Integer.valueOf(fyear),
										Integer.valueOf(fmonth) - 1, Integer
												.valueOf(fday));
								String todate = c1.getString("todate");
								String tDate[] = todate.split("T");
								String tdate = tDate[0];
								String mdate[] = tdate.split("-");
								String tyear = mdate[0];
								String tmonth = mdate[1];
								String tday = mdate[2];
								Date tFdate = new Date(Integer.valueOf(tyear),
										Integer.valueOf(tmonth) - 1, Integer
												.valueOf(tday));

								String fromDate = null;
								formatter = new SimpleDateFormat("dd MMM yy");
								fromDate = formatter.format(fFdate);
								String toDate = formatter.format(tFdate);
								/*
								 * Date fromDate=null; try { fromDate = (Date)
								 * formatter.parse(fdate); } catch
								 * (ParseException e) { // TODO Auto-generated
								 * catch block e.printStackTrace(); } Date
								 * toDate=null; try { toDate = (Date)
								 * formatter.parse(tdate); } catch
								 * (ParseException e) { // TODO Auto-generated
								 * catch block e.printStackTrace(); }
								 */
								weeks.add("Week No:" + weekno + " " + fromDate
										+ "-" + toDate);
								weekNo.add(weekno);

								if (weeks.size() > 0) {
									ArrayAdapter<String> studentlistadapter = new ArrayAdapter<String>(
											Parent_Classwork.this,
											android.R.layout.simple_spinner_item,
											weeks);
									weekList.setAdapter(studentlistadapter);
								} else {

									Toast.makeText(getBaseContext(),
											"There is no week!",
											Toast.LENGTH_SHORT).show();
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
								error.getMessage(), Toast.LENGTH_LONG).show();
						hideDialog();
					}
				});

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

	}

	protected void GetStudentList() {
		// TODO Auto-generated method stub
		String tag_string_req = "StudentList";
		Constant.studentData.clear();

		pDialog.setMessage("Please wait ...");
		showDialog();

		StringRequest strReq = new StringRequest(Request.Method.GET,
				AppConfig.BASE_URL+"studentlists/"
						+ User_id, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Login Response: " + response.toString());
						hideDialog();

						try {

							JSONObject json = new JSONObject(response);

							JSONObject calssdetails = json
									.getJSONObject("response");

							JSONArray calssdetailsArray = calssdetails
									.getJSONArray("users");

							for (int k = 0; k < calssdetailsArray.length(); k++) {
								JSONObject c1 = calssdetailsArray
										.getJSONObject(k);

								String sFname = c1.getString("firstname");
								String sLname = c1.getString("lastname");
								String sLid = c1.getString("id");
								String sclassdivid = c1.getString("classdivid");
								student_name.add(sFname + " " + sLname);
								Constant.childData.add(new ChildData(sLid,
										sclassdivid, sFname, sLname));

								if (Constant.childData.size() > 0) {
									ArrayAdapter<String> studentlistadapter = new ArrayAdapter<String>(
											Parent_Classwork.this,
											android.R.layout.simple_spinner_item,
											student_name);
									studentList.setAdapter(studentlistadapter);

								} else {

									Toast.makeText(getBaseContext(),
											"There is no Student!",
											Toast.LENGTH_SHORT).show();
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
								error.getMessage(), Toast.LENGTH_LONG).show();
						hideDialog();
					}
				});

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

	}

	protected void GetWeekHomework() {
		// TODO Auto-generated method stub
		String tag_string_req = "Get Week Homework ";

		pDialog.setMessage("Please wait ...");
		showDialog();
		Constant.homeworkdata.clear();
		StringRequest strReq = new StringRequest(
				Request.Method.GET,
				AppConfig.BASE_URL+"parentcurrentclassweeks/"
						+ User_id, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Login Response: " + response.toString());
						hideDialog();

						try {

							JSONObject json = new JSONObject(response);
							JSONObject resp = json.getJSONObject("response");
							JSONArray data = resp.getJSONArray("homework");
							for (int i = 0; i < data.length(); i++) {

								JSONObject c = data.getJSONObject(i);

								String studentfullname = c
										.getString("studentfullname");
								String subjectname = c.getString("subjectname");
								String givendate1 = c.getString("givendate");
								String submissiondate1 = c
										.getString("submissiondate");
								String homeworkdescription = c
										.getString("homeworkdescription");
								String homeworkattachpath = c
										.getString("homeworkattachpath");
								String dates[] = submissiondate1.split("T");
								String submissiondate = dates[0];
								String dates1[] = givendate1.split("T");
								String givendate = dates1[0];
								Constant.homeworkdata.add(new HomeworkData(
										studentfullname, subjectname,
										givendate, homeworkdescription,
										homeworkattachpath, submissiondate));

								if (Constant.homeworkdata.size() == 0) {
									// submit.setVisibility(View.INVISIBLE);
									Toast.makeText(Parent_Classwork.this,
											"There is no Data!",
											Toast.LENGTH_LONG).show();

									// adapter.notifyDataSetChanged();
								} else if (Constant.homeworkdata.size() > 0) {
									// submit.setVisibility(View.VISIBLE);
									// mStudentList.setAdapter(new
									// ListAdapter(AllStudentList.this));
									homeworkAdapter = new CustomHomeworkAdapter(
											Parent_Classwork.this,
											Constant.homeworkdata);
									weekhomeworkList
											.setAdapter(homeworkAdapter);
									homeworkAdapter.notifyDataSetChanged();
								}

								/*
								 * calssList.add(classname + " " + divname);
								 * calssDivIdList.add(classdivid);
								 * ArrayAdapter<String> classlistadapter = new
								 * ArrayAdapter<String>( Class_Homework.this,
								 * android.R.layout.simple_spinner_item,
								 * calssList);
								 * tclass.setAdapter(classlistadapter);
								 */
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

	private void GetTodaysHomework() {
		// TODO Auto-generated method stub
		String tag_string_req = "Get Todays Homework ";

		pDialog.setMessage("Please wait ...");
		showDialog();
		Constant.homeworkdata.clear();
		StringRequest strReq = new StringRequest(
				Request.Method.GET,
				AppConfig.BASE_URL+"parentclassworks/"
						+ User_id, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Login Response: " + response.toString());
						hideDialog();

						try {

							JSONObject json = new JSONObject(response);
							JSONObject resp = json.getJSONObject("response");
							JSONArray data = resp.getJSONArray("homework");
							for (int i = 0; i < data.length(); i++) {

								JSONObject c = data.getJSONObject(i);

								String studentfullname = c
										.getString("studentfullname");
								String subjectname = c.getString("subjectname");
								String givendate1 = c.getString("givendate");
								String submissiondate1 = c
										.getString("submissiondate");
								String homeworkdescription = c
										.getString("homeworkdescription");
								String homeworkattachpath = c
										.getString("homeworkattachpath");
								String dates[] = submissiondate1.split("T");
								String submissiondate = dates[0];
								String dates1[] = givendate1.split("T");
								String givendate = dates1[0];
								Constant.homeworkdata.add(new HomeworkData(
										studentfullname, subjectname,
										givendate, homeworkdescription,
										homeworkattachpath, submissiondate));

								if (Constant.homeworkdata.size() == 0) {
									// submit.setVisibility(View.INVISIBLE);
									Toast.makeText(Parent_Classwork.this,
											"There is no Data!",
											Toast.LENGTH_LONG).show();

									// adapter.notifyDataSetChanged();
								} else if (Constant.homeworkdata.size() > 0) {
									// submit.setVisibility(View.VISIBLE);
									// mStudentList.setAdapter(new
									// ListAdapter(AllStudentList.this));
									homeworkAdapter = new CustomHomeworkAdapter(
											Parent_Classwork.this,
											Constant.homeworkdata);
									homeworkList.setAdapter(homeworkAdapter);
									homeworkAdapter.notifyDataSetChanged();
								}

								/*
								 * calssList.add(classname + " " + divname);
								 * calssDivIdList.add(classdivid);
								 * ArrayAdapter<String> classlistadapter = new
								 * ArrayAdapter<String>( Class_Homework.this,
								 * android.R.layout.simple_spinner_item,
								 * calssList);
								 * tclass.setAdapter(classlistadapter);
								 */
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

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}

}

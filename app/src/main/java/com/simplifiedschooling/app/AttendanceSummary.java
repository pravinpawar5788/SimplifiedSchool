package com.simplifiedschooling.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.simplifiedschooling.app.adapter.StudentList;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;
import com.simplifiedschooling.app.util.Constant;

public class AttendanceSummary extends ActionBarActivity {
	private static final String TAG = AttendanceSummary.class.getSimpleName();
	Spinner tclass;
	private int year;
	private int month;
	private int day;
	ListView lv;
	ImageView startCal, endCal;
	Button mGetAttendanceDetails;
	EditText startDate, endDate;
	private ProgressDialog pDialog;
	private String User_id, Role_id, id, jsonStr = null, dateS1;
	ArrayList<String> calssList = new ArrayList<String>();
	ArrayList<String> calssDivIdList = new ArrayList<String>();
	ArrayList<String> s_present = new ArrayList<String>();
	ArrayList<String> s_absent = new ArrayList<String>();
	ArrayList<String> s_halfday = new ArrayList<String>();
	ArrayList<String> remark = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.attendence_summary);
		User_id = getIntent().getExtras().getString("userid");
		Role_id = getIntent().getExtras().getString("roleid");
		id = getIntent().getExtras().getString("id");
		tclass = (Spinner) findViewById(R.id.spinnerClass);
		// tclass.setOnItemSelectedListener(new GetclassListener());
		lv = (ListView) findViewById(R.id.attendenceList1);
		mGetAttendanceDetails = (Button) findViewById(R.id.attendencedetails);
		mGetAttendanceDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// new getAttendanceSummary().execute();
				GetAttendanceSummary();
			}
		});
		startDate = (EditText) findViewById(R.id.submissionDate);
		startCal = (ImageView) findViewById(R.id.calender);
		endDate = (EditText) findViewById(R.id.endDate);
		endCal = (ImageView) findViewById(R.id.calender1);
		startDate.setOnClickListener(new DateOnClickListener());
		startCal.setOnClickListener(new DateOnClickListener());
		endDate.setOnClickListener(new EndDateOnClickListener());
		endCal.setOnClickListener(new EndDateOnClickListener());
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		// new getClassAttendence().execute();
		GetClassAttendence();
	}

	protected void GetAttendanceSummary() {
		// TODO Auto-generated method stub
		String tag_string_req = "req_GetAttendanceSummary";

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

							JSONObject calssdetails = json
									.getJSONObject("response");

							JSONArray calssdetailsArray = calssdetails
									.getJSONArray("userslist");

							for (int k = 0; k < calssdetailsArray.length(); k++) {
								JSONObject c1 = calssdetailsArray
										.getJSONObject(k);
								String s_fname = c1.getString("firstname");
								String s_lname = c1.getString("lastname");
								String s_id = c1.getString("rollno");
								String s_p = c1.getString("present");
								String s_a = c1.getString("totalatt");
								String s_hd = c1.getString("halfday");
								String s_remark = c1.getString("remark");
								remark.add(s_remark);
								s_present.add(s_p);
								s_absent.add(s_a);
								s_halfday.add(s_hd);
								Constant.studentData.add(new StudentList(
										s_fname, s_lname, s_id));

								if (Constant.studentData.size() == 0) {
									// submit.setVisibility(View.INVISIBLE);
									Toast.makeText(AttendanceSummary.this,
											"There is no student!",
											Toast.LENGTH_LONG).show();

									// adapter.notifyDataSetChanged();
								} else if (Constant.studentData.size() > 0) {
									lv.setAdapter(new ListAdapter(
											AttendanceSummary.this));
									// lv2.setAdapter(new
									// ListAdapter1(Attendence_summary.this));
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
				}) {

			@Override
			protected Map<String, String> getParams() {
				Constant.studentData.clear();
				int pos = tclass.getSelectedItemPosition();
				String classDivId = calssDivIdList.get(pos).toString();
				// Posting parameters to login url
				Map<String, String> params = new HashMap<String, String>();

				params.put("id", classDivId);
				params.put("selectstartdate", startDate.getText().toString());
				params.put("selectenddate", endDate.getText().toString());
				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

	}

	private void GetClassAttendence() {
		// TODO Auto-generated method stub
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
										AttendanceSummary.this,
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

	// /////////////////////ListAdapter//////////////////////////////////////

	class ListAdapter extends BaseAdapter {
		Context context;

		public ListAdapter(Context con) {
			this.context = con;
		}

		@Override
		public int getCount() { // TODO Auto-generated method stub
			return Constant.studentData.size();
		}

		@Override
		public Object getItem(int position) { // TODO Auto-generated method
												// stub
			return position;
		}

		public long getItemId(int position) { // TODO Auto-generated
			return 0;
		}

		public int getViewTypeCount() {
			// Count=Size of ArrayList.
			return Constant.studentData.size();
		}

		public int getItemViewType(int position) {

			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) { // TODO Auto-generated method stub View
									// v;

			// pos=position;

			View v;

			if (convertView == null) {

				LayoutInflater inf = AttendanceSummary.this.getLayoutInflater();
				v = inf.inflate(R.layout.attendence_summary_list_row, null);

			} else {
				v = convertView;
			}
			TextView tv2 = (TextView) v.findViewById(R.id.textRoll);
			TextView tv3 = (TextView) v.findViewById(R.id.textSFname);
			TextView tv4 = (TextView) v.findViewById(R.id.textOutOf);
			EditText remarkText = (EditText) v.findViewById(R.id.editText1);

			tv2.setText(""
					+ Constant.studentData.get(position).getStudentId()
							.toString());

			tv3.setText(Constant.studentData.get(position).getStudentFName()
					.toString()
					+ " "

					+ Constant.studentData.get(position).getStudentLName()
							.toString());
			tv4.setText(s_present.get(position) + "/" + s_absent.get(position));
			remarkText.setText(remark.get(position));

			return v;
		}

	}

	private class DateOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {

			DialogFragment newFragment = new SelectDateFragment();
			newFragment.show(getFragmentManager(), "DatePicker");

		}

	}

	public class SelectDateFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar calendar = Calendar.getInstance();
			int yy = calendar.get(Calendar.YEAR);
			int mm = calendar.get(Calendar.MONTH);
			int dd = calendar.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, yy, mm, dd);
		}

		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
			populateSetDate(yy, mm + 1, dd);
		}

		public void populateSetDate(int year, int month, int day) {
			startDate.setText(day + "-" + (month) + "-" + year);
		}

	}

	private class EndDateOnClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {

			DialogFragment newFragment = new SelectDateFragment1();
			newFragment.show(getFragmentManager(), "DatePicker");

		}

	}

	public class SelectDateFragment1 extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar calendar = Calendar.getInstance();
			int yy = calendar.get(Calendar.YEAR);
			int mm = calendar.get(Calendar.MONTH);
			int dd = calendar.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, yy, mm, dd);
		}

		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
			populateSetDate(yy, mm + 1, dd);
		}

		public void populateSetDate(int year, int month, int day) {
			endDate.setText(day + "-" + (month) + "-" + year);
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

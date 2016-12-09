package com.simplifiedschooling.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendanceFragment extends ActionBarActivity {
    private static final String TAG = AttendanceFragment.class.getSimpleName();
    private ProgressDialog pDialog;
    private String User_id, Role_id, id;
    Spinner tclass;
    private String status = null, mRemark = null;
    Context context;
    String jsonStr;
    int response;
    private Button mSubmit, b1,mButton;
    ArrayList<String> calssList = new ArrayList<String>();
    ArrayList<String> s_present = new ArrayList<String>();
    ArrayList<String> s_absent = new ArrayList<String>();
    ArrayList<String> s_halfday = new ArrayList<String>();
    ArrayList<String> value = new ArrayList<String>();
    ArrayList<String> remark = new ArrayList<String>();
    ArrayList<String> calssDivIdList = new ArrayList<String>();
    ListAdapter  adapter=null;
    private ListView mStudentList;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(
                Drawable.createFromPath(this.getExternalCacheDir()
                        .getAbsolutePath() + "/" + "innerpage_top.png"));
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_attendance);

        tclass = (Spinner) findViewById(R.id.spinnerClass);
          adapter=new ListAdapter(AttendanceFragment.this);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            // categoryName1.clear();
            Constant.studentData = (ArrayList<StudentList>) savedInstanceState
                    .getSerializable("categoryName");
            value = savedInstanceState.getStringArrayList("value");
            remark = savedInstanceState.getStringArrayList("remark");
            mStudentList.setAdapter(new ListAdapter(AttendanceFragment.this));
            User_id = savedInstanceState.getString("userid");
            Role_id = savedInstanceState.getString("roleid");
            id = savedInstanceState.getString("id");
            // lv.setAdapter(new
            // ArrayAdapter<String>(Category.this,R.layout.category_list_row,
            // R.id.textView1, categoryName1));

        }else{

            User_id = getIntent().getExtras().getString("userid");
            Role_id = getIntent().getExtras().getString("roleid");
            id = getIntent().getExtras().getString("id");
        }

        // new getClassAttendence().execute();
        GetClassAttendence();
        tclass.setOnItemSelectedListener(new TeacherclassListener());
        mSubmit = (Button) findViewById(R.id.SubmitBtn);
        mSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // new InserAttendenceTask().execute();
                InserAttendenceTask();
            }

            private void InserAttendenceTask() {
                // TODO Auto-generated method stub
                String tag_string_req = "req_login";

                pDialog.setMessage("Please wait ...");
                showDialog();

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.BASE_URL + "addclassattendanceactmobs",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response1) {
                                Log.d(TAG,
                                        "Login Response: "
                                                + response1.toString());
                                hideDialog();

                                try {

                                    JSONObject json = new JSONObject(response1);

                                    response = json.getInt("response");
                                    // response = calssdetails.toString();

                                    Log.d("Response: ", "> " + response);

                                    GetStudentAlreadyInsertedAttendenceList();

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
                }) {

                    @Override
                    protected Map<String, String> getParams() {

                        int pos = tclass.getSelectedItemPosition();
                        String classDivId = calssDivIdList.get(pos).toString();
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        for (int i = 0; i < Constant.studentData.size(); i++) {

							/*params.put("StudentIdArray[]", Constant.studentData
                                    .get(i).getStudentId());*/
                            params.put("AttendanceArray[" + Constant.studentData
                                    .get(i).getStudentId() + "]", value.get(i));
                            params.put("RemarkArray[" + i + "]", remark.get(i));
                        }

                        params.put("classDivId", classDivId);

                        return params;
                    }

                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq,
                        tag_string_req);

            }
        });
        mStudentList = (ListView) findViewById(R.id.student_list);

		/*
		 * mAdapter = new StudentListAdapter(movieList);
		 * RecyclerView.LayoutManager mLayoutManager = new
		 * LinearLayoutManager(getActivity());
		 * recyclerView.setLayoutManager(mLayoutManager);
		 * recyclerView.setItemAnimator(new DefaultItemAnimator());
		 * recyclerView.setAdapter(mAdapter);
		 */

    }
    @Override
    public void onResume() {
        super.onResume();
       // reload the items from database
        adapter.notifyDataSetChanged();
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
                                        AttendanceFragment.this,
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putSerializable("categoryName", Constant.studentData);
        outState.putStringArrayList("value", value);
        outState.putStringArrayList("remark", remark);
        outState.putString("userid", User_id);
        outState.putString("roleid", Role_id);
        outState.putString("id", id);
    }

    private class TeacherclassListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub
            // submit.setText("Submit");
            // Constant.studentData.clear();

            // new getStudentAttendenceList().execute();
            // new getStudentAttendenceListChecker().execute();
            GetStudentAttendenceListChecker(); /*
												 * int pos =
												 * tclass.getSelectedItemPosition
												 * (); String classdivId =
												 * calssDivIdList
												 * .get(pos).toString();
												 * startWebView(BASEURL +
												 * "retrieve_ClassTeacher_Attendance.php"
												 * + "?ClassDivId=" +
												 * classdivId);
												 */

        }

        private void GetStudentAttendenceListChecker() {
            // TODO Auto-generated method stub
            String tag_string_req = "req_login";
            int pos = tclass.getSelectedItemPosition();
            String classDivId = calssDivIdList.get(pos).toString();
            pDialog.setMessage("Please wait ...");
            showDialog();

            StringRequest strReq = new StringRequest(Request.Method.GET,
                    AppConfig.BASE_URL + "studentattendancelistcheakers/"
                            + classDivId, new Response.Listener<String>() {

                @Override
                public void onResponse(String response1) {
                    Log.d(TAG,
                            "Login Response: " + response1.toString());
                    hideDialog();

                    try {

                        JSONObject json = new JSONObject(response1);

                        response = json.getInt("response");
                        // response = calssdetails.toString();

                        Log.d("Response: ", ">>> " + response);

                        if (response == 0) {
                            // submit.setVisibility(View.INVISIBLE);

                            // new getStudentAttendenceList().execute();

                            GetStudentAttendenceList();
                            // adapter.notifyDataSetChanged();
                        } else {

                            // new
                            // getStudentAlreadyInsertedAttendenceList().execute();
                            GetStudentAlreadyInsertedAttendenceList();
                            // submit.setVisibility(View.VISIBLE);
                            // lv.setAdapter(adapter);
                            // adapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                private void GetStudentAttendenceList() {
                    // TODO Auto-generated method stub
                    String tag_string_req = "req_login";
                    Constant.studentData.clear();
                    int pos = tclass.getSelectedItemPosition();
                    String classDivId = calssDivIdList.get(pos)
                            .toString();
                    pDialog.setMessage("Please wait ...");
                    showDialog();

                    StringRequest strReq = new StringRequest(
                            Request.Method.GET, AppConfig.BASE_URL
                            + "studentalllists/" + classDivId,
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "Login Response: "
                                            + response.toString());
                                    hideDialog();

                                    try {

                                        JSONObject json = new JSONObject(
                                                response);

                                        JSONObject calssdetails = json
                                                .getJSONObject("response");

                                        JSONArray calssdetailsArray = calssdetails
                                                .getJSONArray("userslist");

                                        for (int k = 0; k < calssdetailsArray
                                                .length(); k++) {
                                            JSONObject c1 = calssdetailsArray
                                                    .getJSONObject(k);
                                            String s_fname = c1
                                                    .getString("firstname");
                                            String s_lname = c1
                                                    .getString("lastname");
                                            String s_id = c1
                                                    .getString("id");
                                            Constant.studentData
                                                    .add(new StudentList(
                                                            s_fname,
                                                            s_lname,
                                                            s_id));
                                        }

                                        if (Constant.studentData.size() == 0) {
                                            // submit.setVisibility(View.INVISIBLE);
                                            Toast.makeText(
                                                    AttendanceFragment.this,
                                                    "There is no student!",
                                                    Toast.LENGTH_LONG)
                                                    .show();

                                            // adapter.notifyDataSetChanged();
                                        } else if (Constant.studentData
                                                .size() > 0) {
                                            // submit.setVisibility(View.VISIBLE);
                                            mStudentList
                                                    .setAdapter(new ListAdapter(
                                                            AttendanceFragment.this));
                                            // mStudentList.setAdapter(adapter);
                                            // adapter.notifyDataSetChanged();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(
                                VolleyError error) {
                            Log.e(TAG,
                                    "Data Error: "
                                            + error.getMessage());
                            Toast.makeText(
                                    getApplicationContext(),
                                    error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    });

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(
                            strReq, tag_string_req);

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

    private void GetStudentAlreadyInsertedAttendenceList() {
        // TODO Auto-generated method stub
        String tag_string_req = "req_login";
        //mStudentList=null;
        Constant.studentData.clear();
        s_present.clear();
        s_absent.clear();
        s_halfday.clear();
        int pos = tclass.getSelectedItemPosition();
        String classDivId = calssDivIdList.get(pos).toString();
        pDialog.setMessage("Please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.BASE_URL + "studentattendancelists/" + classDivId,
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
                                String s_id = c1.getString("selfid");
                                String s_p = c1.getString("present");
                                String s_a = c1.getString("absent");
                                String s_hd = c1.getString("halfday");
                                String s_remark = c1.getString("remark");
                                remark.add(s_remark);
                                s_present.add(s_p);
                                s_absent.add(s_a);
                                s_halfday.add(s_hd);
                                Constant.studentData.add(new StudentList(
                                        s_fname, s_lname, s_id));
                            }

                            if (Constant.studentData.size() == 0) {
                                // submit.setVisibility(View.INVISIBLE);
                                Toast.makeText(AttendanceFragment.this,
                                        "There is no student!",
                                        Toast.LENGTH_LONG).show();

                                // adapter.notifyDataSetChanged();
                            } else if (Constant.studentData.size() > 0) {
                                // submit.setVisibility(View.VISIBLE);

                                mStudentList.setAdapter(adapter);


                                // mStudentList.notifyDataSetChanged(adapter);
                                 adapter.notifyDataSetChanged();
                            } /*
							 * else { new getAlreadyInsertedValue().execute(); }
							 * }
							 */

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

    // //////////////////////////insertAttendence//////////////////////////

    // /////////////////Modify Attendence of row//////////////////////////

    // ////////////////////////getting student list///////////////////////

    // ////////////////////////////GetAlreadyInsertedVales//////////////////

    // ///////////////////////////////ListAdapter///////////////////////////////////

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
        public Object getItem(int position) { // TODO Auto-generated method stub
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
                            ViewGroup parent) { // TODO Auto-generated method stub View v;

            // pos=position;

            View v;

            if (convertView == null) {

                LayoutInflater inf = AttendanceFragment.this
                        .getLayoutInflater();
                v = inf.inflate(R.layout.student_attendence_list_child, null);

            } else {
                v = convertView;
            }
            TextView tv2 = (TextView) v.findViewById(R.id.textRoll);
            TextView tv3 = (TextView) v.findViewById(R.id.textSFname);
            // TextView tv4 = (TextView)v.findViewById(R.id.textSLName);
            EditText remarkText = (EditText) v.findViewById(R.id.editText1);
            Button btnModify = (Button) v.findViewById(R.id.btnModify);
            b1 = (Button) v.findViewById(R.id.button1);
            // Log.d("entry", entry);
            // TextView tv5 = (TextView)v.findViewById(R.id.View5);

			/*
			 * rg = (RadioGroup) v.findViewById(R.id.radioGroup1); r1 =
			 * (RadioButton)v.findViewById(R.id.radio0); r2 =
			 * (RadioButton)v.findViewById(R.id.radio1); r3 =
			 * (RadioButton)v.findViewById(R.id.radio2);
			 */
            TextView status_txt = (TextView) v.findViewById(R.id.status);
            TextView remark_txt = (TextView) v.findViewById(R.id.remark);

            tv2.setText("" + (position + 1));

            tv3.setText(Constant.studentData.get(position).getStudentFName()
                    .toString()
                    + " "
                    + Constant.studentData.get(position).getStudentLName()
                    .toString());
            // remark.add("");
            if (response == 0) {
                mSubmit.setVisibility(View.VISIBLE);
                value.add("P");
                remark.add("");

                b1.setOnClickListener(new OnClickListener() {
                    int count = 0;

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        LinearLayout rl = (LinearLayout) v.getParent();
                        Button mBtn = (Button) rl.findViewById(R.id.button1);
                        // String text = tv.getText().toString();
                        // mBtn.setBackgroundColor(Color.RED);
                        // mBtn.setText("A");
                        count++;
                        if (count == 0) {
                            mBtn.setBackgroundColor(Color.parseColor("#0E8DE2"));
                            mBtn.setText("Present");
                            value.set(position, "P");
                            //remark.add(position, "");
                        } else if (count == 1) {
                            mBtn.setBackgroundColor(Color.parseColor("#A82929"));
                            mBtn.setText("Absent");
                            value.set(position, "A");
                            //	remark.add(position, "");
                        } else if (count == 2) {
                            mBtn.setBackgroundColor(Color.parseColor("#368B36"));
                            mBtn.setText("HalfDay");
                            value.set(position, "HD");
                            //remark.add(position, "");
                        }
                        if (count > 2) {
                            mBtn.setBackgroundColor(Color.parseColor("#0E8DE2"));
                            mBtn.setText("Present");
                            value.add(position, "P");
                            remark.add(position, "");
                            count = 0;
                        }

                    }
                });

            } else {
                mSubmit.setVisibility(View.GONE);
                remark_txt.setVisibility(View.GONE);
               // btnModify.setVisibility(View.VISIBLE);
                remarkText.setVisibility(View.VISIBLE);

                remarkText.addTextChangedListener(new keyListener(position));
                b1.setOnClickListener(new OnClickListener() {
                    int count = 0;

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        final Dialog dialog3 = new Dialog(context);
                        dialog3.setContentView(R.layout.student_attendance_list_alertbox);
                        dialog3.setTitle("Modify Attendance");
                        dialog3.setCancelable(true);

                        TextView tv2 = (TextView) dialog3.findViewById(R.id.textRoll);
                        TextView tv3 = (TextView) dialog3.findViewById(R.id.textSFname);
                        // TextView tv4 = (TextView)v.findViewById(R.id.textSLName);
                        EditText remarkText = (EditText) dialog3.findViewById(R.id.editText1);


                        //Button btnModify = (Button) v.findViewById(R.id.btnModify);
                        Button mBtnModify = (Button) dialog3.findViewById(R.id.btnModify);
                        mBtnModify.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ModifyAttendenceTask(position);
                                dialog3.dismiss();
                                Intent refresh = new Intent(AttendanceFragment.this, AttendanceFragment.class);
                                refresh.putExtra("userid", User_id);
                                refresh.putExtra("roleid", Role_id);
                                refresh.putExtra("id", id);
                                startActivity(refresh);
                                AttendanceFragment.this.finish();
                            }
                        });
                        Button mBtnCancle = (Button) dialog3.findViewById(R.id.btnCancel);
                        mBtnCancle.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog3.dismiss();
                            }
                        });
                        mButton = (Button) dialog3.findViewById(R.id.button1);
                        ////////////////////////////////////
                        remarkText.setText(remark.get(position));
                        if (s_present.get(position) == "true") {
                            mButton.setBackgroundColor(Color.parseColor("#0E8DE2"));
                            mButton.setText("Present");
                            value.add(position, "P");


                        } else if (s_absent.get(position) == "true") {
                            mButton.setBackgroundColor(Color.parseColor("#A82929"));
                            mButton.setText("Absent");
                            value.add(position, "A");


                        } else if (s_halfday.get(position) == "true") {
                            mButton.setBackgroundColor(Color.parseColor("#368B36"));
                            mButton.setText("HalfDay");
                            value.add(position, "HD");


                        }



                        ///////////////////////////////////////
                        mButton.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LinearLayout rl = (LinearLayout) v.getParent();
                                Button mBtn = (Button) rl.findViewById(R.id.button1);
                                if (mBtn.getText().toString() == "HalfDay") {
                                    mBtn.setBackgroundColor(Color.parseColor("#0E8DE2"));
                                    mBtn.setText("Present");
                                    //value.set(position, "P");
                                    status = "P";
                                    Log.d("Value", value.get(position));

                                } else if (mBtn.getText().toString() == "Present") {
                                    mBtn.setBackgroundColor(Color.parseColor("#A82929"));
                                    mBtn.setText("Absent");
                                    //value.set(position, "A");
                                    status = "A";
                                    Log.d("Value", value.get(position));
                                } else if (mBtn.getText().toString() == "Absent") {
                                    mBtn.setBackgroundColor(Color.parseColor("#368B36"));
                                    mBtn.setText("HalfDay");
                                    //value.set(position, "HD");
                                    status = "HD";
                                    Log.d("Value", value.get(position));
                                }

                            }
                        });
                        tv2.setText("" + (position + 1));

                        tv3.setText(Constant.studentData.get(position).getStudentFName()
                                .toString()
                                + " "
                                + Constant.studentData.get(position).getStudentLName()
                                .toString());
                        remarkText.setVisibility(View.VISIBLE);
                        remarkText.setEnabled(true);
                        remarkText.addTextChangedListener(new keyListener(position));
                        // String text = tv.getText().toString();
                        // mBtn.setBackgroundColor(Color.RED);
                        // mBtn.setText("A");

                        //count++;

						/*if (count > 2) {
							mBtn.setBackgroundColor(Color.parseColor("#0E8DE2"));
							mBtn.setText("Present");
							value.add(position, "P");
							count = 0;
						}*/
                        dialog3.show();
                    }
                });


                btnModify.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        // new ModifyAttendenceTask(position).execute();
                       AlertDialog.Builder builder = new AlertDialog.Builder(
                                AttendanceFragment.this);
                        builder.setMessage("Do you want Modify the Record?")
                                .setCancelable(true)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                ModifyAttendenceTask(position);
                                            }

                                        })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick (DialogInterface dialog,int id){
                                    dialog.dismiss();
                            }

                        })
                        ;

                        builder.show();


                        /*final Dialog dialog3 = new Dialog(context);
                        dialog3.setContentView(R.layout.student_attendence_list_child);
                        dialog3.setTitle("Modify Attendance");
                        dialog3.setCancelable(false);
                        TextView tv2 = (TextView) dialog3.findViewById(R.id.textRoll);
                        TextView tv3 = (TextView) dialog3.findViewById(R.id.textSFname);
                        // TextView tv4 = (TextView)v.findViewById(R.id.textSLName);
                        EditText remarkText = (EditText) dialog3.findViewById(R.id.editText1);
                        //Button btnModify = (Button) v.findViewById(R.id.btnModify);
                        b1 = (Button) dialog3.findViewById(R.id.button1);
                        tv2.setText("" + (position + 1));

                        tv3.setText(Constant.studentData.get(position).getStudentFName()
                                .toString()
                                + " "
                                + Constant.studentData.get(position).getStudentLName()
                                .toString());
                        remarkText.setVisibility(View.VISIBLE);
                        remarkText.addTextChangedListener(new keyListener(position));*/

                    }


                });
                remarkText.setText(remark.get(position));
                if (s_present.get(position) == "true") {
                    b1.setBackgroundColor(Color.parseColor("#0E8DE2"));
                    b1.setText("Present");
                    value.add(position, "P");


                } else if (s_absent.get(position) == "true") {
                    b1.setBackgroundColor(Color.parseColor("#A82929"));
                    b1.setText("Absent");
                    value.add(position, "A");


                } else if (s_halfday.get(position) == "true") {
                    b1.setBackgroundColor(Color.parseColor("#368B36"));
                    b1.setText("HalfDay");
                    value.add(position, "HD");


                }


            }
            return v;
        }
    }


    private void ModifyAttendenceTask(final int position) {
        // TODO Auto-generated method stub
        String tag_string_req = "req_login";
        //status=value.get(position);
        pDialog.setMessage("Please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(
                Request.Method.POST, AppConfig.BASE_URL
                + "studentattendancesavemobs",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: "
                                + response.toString());
                        hideDialog();
                        Toast.makeText(getApplicationContext(),
                                "Successfully Modified!",
                                Toast.LENGTH_LONG).show();

                        if (response.toString() == "1") {
                            Toast.makeText(getApplicationContext(),
                                    "Successfully Modified!",
                                    Toast.LENGTH_LONG).show();
                            //GetStudentAlreadyInsertedAttendenceList();

                           // GetClassAttendence();
                        } else {

                        }
										/*try {

											JSONObject json = new JSONObject(
													response);
											JSONObject resp = json
													.getJSONObject("response");
											*//*JSONArray calssdetails = resp
													.getJSONArray("classinfo");
											for (int i = 0; i < calssdetails
													.length(); i++) {

												JSONObject c = calssdetails
														.getJSONObject(i);

												String classdivid = c
														.getString("classdivid");
												String classname = c
														.getString("classname");
												String divname = c
														.getString("divname");
												calssList.add(classname + " "
														+ divname);
												calssDivIdList.add(classdivid);
												ArrayAdapter<String> classlistadapter = new ArrayAdapter<String>(
														AttendanceFragment.this,
														android.R.layout.simple_spinner_item,
														calssList);
												tclass.setAdapter(classlistadapter);
											}*//*
										} catch (JSONException e) {
											e.printStackTrace();
										}*/
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(
                    VolleyError error) {
                Log.e(TAG,
                        "Data Error: "
                                + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(),
                        Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("id",
                        Constant.studentData.get(position)
                                .getStudentId());
                if (status != null) {
                    params.put("val2", status);
                } else {
                    params.put("val2", value.get(position));
                }
                                /*if (mRemark != null) {
                                    params.put("val3", mRemark);
                                } else {*/
                params.put("val3", remark.get(position));
                //}
                Log.d("params:", params.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq,
                tag_string_req);

    }


    private class keyListener implements TextWatcher {
        int pos;

        public keyListener(int position) {
            // TODO Auto-generated constructor stub
            pos = position;

        }

       /* @Override
        public void onFocusChange(View v, boolean arg1) { // TODO Auto-generated
            // method stub
            EditText v1 = (EditText) v;

            remark.set(pos, v1.getText().toString());
           // mRemark = v1.getText().toString();
        }
*/
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            remark.set(pos, s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

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
package com.simplifiedschooling.app;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.simplifiedschooling.app.helper.AssignmentData;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;
import com.simplifiedschooling.app.util.Constant;
import com.simplifiedschooling.app.util.FileOpen;
import com.simplifiedschooling.app.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class


Teacher_Classwork extends ActionBarActivity {
    private static final String TAG = Teacher_Classwork.class.getSimpleName();
    private String BASEURL = AppConfig.BASE_URL;
    private String User_id, Role_id, id;
    String Username, mUsername, jsonStr = null, ass_path = null, assignment_path,
            download_path, dateS, dateS1, entry = null, dateS2;
    private ProgressDialog pDialog;
    TextView usernameTxt, dateText;
    TextView filename, div, filenameTxt;
    ListView lv1, lv2;
    ImageButton backBtn;
    Uri currImageURI;
    String realpath, path;
    ListAdapter adapter;
    Button chooseBtn, submitBtn;
    Spinner tclass, tsubject;
    LinearLayout container;
    ImageView cal;
    private int year;
    private int month;
    private int day;
    String ftp_url=null,SchoolFolder;
    SharedPreferences userinfo;
    String assment_path = null;
    ArrayList<String> calssList = new ArrayList<String>();
    ArrayList<String> calssDivIdList = new ArrayList<String>();
    ArrayList<String> sub_name = new ArrayList<String>();
    ArrayList<String> sub_id = new ArrayList<String>();
    ArrayList<String> ass_down_path = new ArrayList<String>();
    ArrayList<String> classId = new ArrayList<String>();
    EditText assignmentDes, submissionDate;
    static final int DATE_PICKER_ID = 1111;
    private static final int REQUEST_CODE = 6384;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(
                Drawable.createFromPath(this.getExternalCacheDir()
                        .getAbsolutePath() + "/" + "innerpage_top.png"));
        setContentView(R.layout.classwork);
        adapter = new ListAdapter(getApplicationContext());
        adapter.notifyDataSetChanged();
        Bundle b = getIntent().getExtras();
        Username = b.getString("Username");
        mUsername = b.getString("mUsername");
        User_id = getIntent().getExtras().getString("userid");
        Role_id = getIntent().getExtras().getString("roleid");
        userinfo = getSharedPreferences("User", MODE_PRIVATE);
        ftp_url = userinfo.getString("schoolftp", "");
        SchoolFolder =userinfo.getString("schoolfolder", "");
        Log.e("ftp_url",ftp_url);
        id = getIntent().getExtras().getString("id");
        /*usernameTxt = (TextView) findViewById(R.id.textUsername);
    //	div = (TextView) findViewById(R.id.div);
		usernameTxt.setText("   Welcome[" + mUsername + "]");
		dateText = (TextView) findViewById(R.id.textDate);
		Date tmpDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		dateS = formatter.format(tmpDate);
		dateText.setText(dateS);
*/

        lv1 = (ListView) findViewById(R.id.assignmentList);
        lv1.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }

        });
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        tclass = (Spinner) findViewById(R.id.spinnerClass);
        tsubject = (Spinner) findViewById(R.id.spinnerSubject);
        container = (LinearLayout) findViewById(R.id.container);
        tclass.setOnItemSelectedListener(new TeacherclassListener());
        tsubject.setOnItemSelectedListener(new TeacherSubjectListener());
        filenameTxt = (TextView) findViewById(R.id.textfilename);
        cal = (ImageView) findViewById(R.id.calender);
        cal.setOnClickListener(new DateOnClickListener());
        assignmentDes = (EditText) findViewById(R.id.assignmentDes);
        submissionDate = (EditText) findViewById(R.id.submissionDate);
        submissionDate.setOnClickListener(new DateOnClickListener());
        chooseBtn = (Button) findViewById(R.id.ChooseBtn);
        chooseBtn.setOnClickListener(new chooseBtnListener());
        submitBtn = (Button) findViewById(R.id.SubmitBtn);
        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        submitBtn.setOnClickListener(new submitBtnListener());

        //new getClassList().execute();
        getClassList();
    }

    private void getClassList() {
        // TODO Auto-generated method stub
        String tag_string_req = "Get Class ";

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
                                        Teacher_Classwork.this,
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


    private class submitBtnListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            // new submitAssignmentTask().execute();

            if (assignmentDes.getText().toString().trim().equals("")) {
                Toast.makeText(getBaseContext(),
                        "Please enter Classwork Description!",
                        Toast.LENGTH_LONG).show();
			/*} else if (filenameTxt.getText().toString().trim()
					.equals("No file chosen")
					|| filenameTxt.getText().toString().trim().equals("")) {
				Toast.makeText(getBaseContext(),
						"Please select file to upload!", Toast.LENGTH_LONG)
						.show();*/
            } else if (submissionDate.getText().toString().trim().equals("")) {
                Toast.makeText(getBaseContext(), "Please select date!",
                        Toast.LENGTH_LONG).show();
            } else

            {

                String urlServer = BASEURL
                        + "classworkactmobs";
                int pos = tclass.getSelectedItemPosition();
                int pos1 = tsubject.getSelectedItemPosition();
                String classdiv = calssList.get(pos).toString();
                String classdivId = calssDivIdList.get(pos).toString();
                String subject = sub_name.get(pos1).toString();
                String subjectId = sub_id.get(pos1).toString();
                RequestParams params1 = new RequestParams();
                params1.put("profilePicturePath", path);
               // params1.put("Username", Username);
                params1.put("teacherid", id);
                params1.put("assignmentDescription", assignmentDes.getText()
                        .toString().trim());
                //params1.put("ClassDivName", classdiv.toString());
                params1.put("classId", classdivId.toString());
               // params1.put("SubjectName", subject.toString());
                params1.put("subjectid", subjectId.toString());
                params1.put("droot", ftp_url);
                params1.put("schoolfolder", SchoolFolder);
                params1.put("fstart", dateS1);
                // here write your parameter name and its value
                if (filenameTxt.getText().toString().trim()
                        .equals("No file chosen")
                        || filenameTxt.getText().toString().trim().equals("")) {
                } else {
                    try {
                        params1.put("assignmentAttatchment", new File(realpath));
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(urlServer, params1, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);
                        Log.v("from response", arg0);
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        //new getAssignmentList().execute();
                        GetAssignmentList();
                        assignmentDes.setText("");
                        submissionDate.setText("");
                        path = null;
                        filenameTxt.setText(path);
                        // Toast.makeText(getBaseContext(), arg0,
                        // Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        pDialog = new ProgressDialog(Teacher_Classwork.this);
                        pDialog.setMessage("Please wait...");
                        pDialog.setCancelable(false);
                        pDialog.show();

                    }
                });
            }

        }

    }

    private class chooseBtnListener implements OnClickListener {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //Intent intent = new Intent();
            //intent.setType("*/*");
			/*intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select File"), 1);
			*/
			/*Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT,
			        android.provider.MediaStore.Files.getContentUri(volumeName, rowId)getContentUri(STORAGE_SERVICE));
			// Start the Intent
			startActivityForResult(
					Intent.createChooser(galleryIntent, "Select File"), 1);*/
			/*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("file/*");
			startActivityForResult(intent, 1);*/
            showChooser();
        }

    }

    private void showChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(target, "Select a File to Upload");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        final Uri uri = data.getData();
                        Log.i(TAG, "Uri = " + uri.toString());
                        try {
                            // Get the file path from the URI
                            realpath = FileUtils.getPath(this, uri);
						/*Toast.makeText(Class_Homework.this,
								"File Selected: " + realpath, Toast.LENGTH_LONG)
								.show();*/
                            String imagepath[] = realpath.split("/");

                            path = imagepath[imagepath.length - 1];
                            filenameTxt.setText(path);

                        } catch (Exception e) {
                            Log.e("FileSelector", "File select error",
                                    e);
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String getPath(Context context, Uri uri)
            throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,
                        null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, // Which columns to
                // return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        Log.e("getit", cursor.getString(column_index));
        return cursor.getString(column_index);
    }

    private class DateOnClickListener implements OnClickListener {

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
            submissionDate.setText(dateS1);
        }
    };

    /*@SuppressLint("NewApi")
    private class DateOnClickListener implements OnClickListener {

        @SuppressLint("NewApi")
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub

            final Dialog dialog2 = new Dialog(Assignment_project.this);
            dialog2.setContentView(R.layout.custom);
            dialog2.setTitle("Select Date");
            dialog2.setCancelable(false);
            final CalendarView myCalendar = (CalendarView) dialog2
                    .findViewById(R.id.calendarView1);

            myCalendar.setOnDateChangeListener(new OnDateChangeListener() {

                @SuppressLint("NewApi")
                @Override
                public void onSelectedDayChange(CalendarView myCalendar,
                        int year, int month, int date) {

                    Date tmpDate = new Date(myCalendar.getDate());
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                            "dd MMM yyyy");
                    dateS1 = formatter.format(tmpDate);
                    dateS2 = formatter1.format(tmpDate);
                    ;
                }
            });

            Button cancel = (Button) dialog2.findViewById(R.id.cancel);
            Button ok = (Button) dialog2.findViewById(R.id.ok);

            cancel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    dialog2.dismiss();
                }

            });
            ok.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                     * if (dateS1 != null) { submissionDate.setText(dateS1); }
                     * else {

                    Date d = new Date(myCalendar.getDate());

                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    SimpleDateFormat formatter1 = new SimpleDateFormat(
                            "dd MMM yyyy");
                    dateS1 = formatter.format(d);

                    dateS2 = formatter1.format(d);

                    submissionDate.setText(dateS2);
                    // }
                    dialog2.dismiss();

                }

            });
            dialog2.show();

        }

    }
*/
    private class TeacherSubjectListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub
            Constant.assessmentdata.clear();
            //new getAssignmentList().execute();
            GetAssignmentList();

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }

    }

    private class TeacherclassListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub

            //new getSubjectList().execute();
            GetSubjectList();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }

    }

    // ////////////////////////
    class ListAdapter extends BaseAdapter {

        // ImageView ib2;

        Context context;

        public ListAdapter(Context con) {
            context = con;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return Constant.assessmentdata.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View v;
            // pos=position;

            if (convertView == null) {

                LayoutInflater inf = getLayoutInflater();
                v = inf.inflate(R.layout.assignment_row, null);

            } else {
                v = convertView;
            }

            TextView tv3 = (TextView) v.findViewById(R.id.textAssignment);
            filename = (TextView) v.findViewById(R.id.textFile);
            TextView tv5 = (TextView) v.findViewById(R.id.textSubmissionDate);

            tv3.setText(Constant.assessmentdata.get(position)
                    .getAssignmentDecription().toString());
            filename.setText(Constant.assessmentdata.get(position)
                    .getAssignmentPath().toString());
            tv5.setText(Constant.assessmentdata.get(position)
                    .getAssignmentsubmissionDate().toString());
            filename.setOnClickListener(new downloadListener(position));
            return v;
        }

    }

    private class downloadListener implements OnClickListener {
        int pos;

        public downloadListener(int position) {
            // TODO Auto-generated constructor stub
            this.pos = position;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            // Toast.makeText(getBaseContext(), "There is no file to display",
            // Toast.LENGTH_LONG).show();

            download_path = ass_down_path.get(pos).replaceAll("\\\\", "/");
            download_path = download_path.replaceAll(" ", "%20");
            Log.e("Error", "Path" + download_path);
            new DownloadTask(Teacher_Classwork.this).execute(AppConfig.CLIENT_URL + "../" + "uploads/" + download_path);
        }

    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            pDialog = new ProgressDialog(Teacher_Classwork.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;

            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);

                String path[] = sUrl[0].split("/");

                assment_path = path[path.length - 1];

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP "
                            + connection.getResponseCode() + " "
                            + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream("/mnt/sdcard/" + assment_path);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            pDialog.dismiss();
            if (result != null)
                // Toast.makeText(context,"Download error: ",
                // Toast.LENGTH_LONG).show();
                Log.e("Error", BASEURL + "../" + download_path + result);
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT)
                        .show();
            File myFile = new File("/mnt/sdcard/" + assment_path);
            try {
                FileOpen.openFile(Teacher_Classwork.this, myFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ActivityNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                Toast.makeText(
                        getApplicationContext(),
                        "There is no app install to view" + " " + assment_path
                                + " file.", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public void GetAssignmentList() {
        // TODO Auto-generated method stub
        String tag_string_req = "Get Classworklist";

        pDialog.setMessage("Logging in ...");
        showDialog();
        Constant.assessmentdata.clear();
        ass_down_path.clear();
        int pos = tsubject.getSelectedItemPosition();
        String subjectid = sub_id.get(pos).toString();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.BASE_URL + "classworkalllists/" + subjectid,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Class worklist Response: " + response.toString());
                        hideDialog();


                        try {

                            JSONObject json = new JSONObject(response);
                            JSONObject resp = json.getJSONObject("response");
                            JSONArray calssdetails = resp
                                    .getJSONArray("userslist");
                            for (int i = 0; i < calssdetails.length(); i++) {

                                JSONObject c = calssdetails.getJSONObject(i);
                                String assignmentDescription = c
                                        .getString("homeworkdescription");
                                String assignmentAttachPath = c
                                        .getString("homeworkattachpath");
                                String submissionDate = c.getString("givendate");
                                String gDates[] = submissionDate.split("T");
                                String givendate = gDates[0];
                                String path[] = assignmentAttachPath.split("/");

                                ass_path = assignmentAttachPath;

                                Constant.assessmentdata
                                        .add(new AssignmentData(assignmentDescription,
                                                ass_path, givendate));
                                ass_down_path.add(assignmentAttachPath);


                                if (Constant.assessmentdata.size() > 0) {
                                    container.setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();
                                    lv1.setAdapter(adapter);
                                } else {
                                    Toast.makeText(getBaseContext(), "There is no Assignment!",
                                            Toast.LENGTH_SHORT).show();
                                    container.setVisibility(View.VISIBLE);
                                    adapter.notifyDataSetChanged();
                                    lv1.setAdapter(adapter);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public void GetSubjectList() {
        // TODO Auto-generated method stub
        String tag_string_req = "Get Subject";

        pDialog.setMessage("Logging in ...");
        showDialog();
        sub_name.clear();
        sub_id.clear();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASE_URL + "classsubjectmobes",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Class subject Response: " + response.toString());
                        hideDialog();


                        try {

                            JSONObject json = new JSONObject(response);
                            JSONObject resp = json.getJSONObject("response");
                            JSONArray calssdetails = resp
                                    .getJSONArray("subjects");
                            for (int i = 0; i < calssdetails.length(); i++) {

                                JSONObject c = calssdetails.getJSONObject(i);

                                String subject_name = c.getString("subjectname");
                                String subject_id = c.getString("subjectid");

                                sub_name.add(subject_name);
                                sub_id.add(subject_id);

                                ArrayAdapter<String> classlistadapter = new ArrayAdapter<String>(
                                        Teacher_Classwork.this,
                                        android.R.layout.simple_spinner_item, sub_name);
                                tsubject.setAdapter(classlistadapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                int pos = tclass.getSelectedItemPosition();
                String classdivId = calssDivIdList.get(pos).toString();
                Map<String, String> params = new HashMap<String, String>();
                params.put("classid", classdivId);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

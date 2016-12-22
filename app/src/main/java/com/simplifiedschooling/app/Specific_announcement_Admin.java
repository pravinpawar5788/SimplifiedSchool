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
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;
import com.simplifiedschooling.app.util.FileUtils;
import com.simplifiedschooling.app.util.MultiSelectionSpinner;
import com.simplifiedschooling.app.util.NetworkUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Specific_announcement_Admin extends ActionBarActivity implements MultiSelectionSpinner.OnMultipleItemsSelectedListener {
    private static final String TAG = Specific_announcement_Admin.class
            .getSimpleName();
    private String BASEURL = AppConfig.BASE_URL;
    String Username, jsonStr = null, dateS1, dateS2, realpath, path;
    private ProgressDialog pDialog;
    TextView usernameTxt;
    TextView filename, filenameTxt;
    ListView lv;
    ImageButton backBtn;
    LinearLayout teacherlayout, studentparentlayout;
    Spinner spcat, spclass, spdiv;
    MultiSelectionSpinner spteacher, spstudent;
    String categery[] = {"Teacher", "Student", "Parent"};
    EditText dateEditSpec, announcementEditSpec, subSpec;
    private int year;
    private int month;
    private int day;
    Button chooseBtn;
    static final int DATE_PICKER_ID = 1111;
    DateFormat formatter = null;
    ArrayList<String> calssList = new ArrayList<String>();
    ArrayList<String> calssIdList = new ArrayList<String>();
    List<String> teacherList = new ArrayList<String>();
    ArrayList<String> teacherIdList = new ArrayList<String>();
    ArrayList<String> divList = new ArrayList<String>();
    ArrayList<String> classdivIdList = new ArrayList<String>();
    ArrayList<String> studentList = new ArrayList<String>();
    ArrayList<String> studentIdList = new ArrayList<String>();
    ArrayList<String> assignclass = new ArrayList<String>();
    SharedPreferences userinfo;
    String ftp_url = null;
    Uri currImageURI;
    private static final int REQUEST_CODE = 6384;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(
                Drawable.createFromPath(this.getExternalFilesDir(null)
                        .getAbsolutePath() + "/" + "innerpage_top.png"));
        setContentView(R.layout.announcementspci);
        userinfo = getSharedPreferences("User", MODE_PRIVATE);
        ftp_url = userinfo.getString("schoolftp", "");
        ImageView iv = (ImageView) findViewById(R.id.calender);
        iv.setOnClickListener(new DateOnClickListener());
        dateEditSpec = (EditText) findViewById(R.id.editTextDateS);
        dateEditSpec.setOnClickListener(new DateOnClickListener());
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        Button submitBtn = (Button) findViewById(R.id.button1);
        submitBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                if (validateForm()) {

                    if (NetworkUtilities.isInternet(getBaseContext())) {
                        // new insertSpcificAnnouncementTask().execute();
                        InsertSpcificAnnouncementTask();
                    } else {
                        Toast.makeText(getBaseContext(), "No network",
                                Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
        filenameTxt = (TextView) findViewById(R.id.textfilename);
        chooseBtn = (Button) findViewById(R.id.ChooseBtn);
        chooseBtn.setOnClickListener(new chooseBtnListener());

        subSpec = (EditText) findViewById(R.id.editTextSubjectS);
        announcementEditSpec = (EditText) findViewById(R.id.editTextAnnounceS);
        teacherlayout = (LinearLayout) findViewById(R.id.teacherlayout);
        studentparentlayout = (LinearLayout) findViewById(R.id.studenparentlayout);
        spcat = (Spinner) findViewById(R.id.spinnercat);
        spcat.setAdapter(new ArrayAdapter<String>(
                Specific_announcement_Admin.this,
                android.R.layout.simple_spinner_item, categery));
        spcat.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                if (spcat.getSelectedItem().toString().equals("Student")
                        || spcat.getSelectedItem().toString().equals("Parent")) {

                    teacherlayout.setVisibility(View.GONE);

                    studentparentlayout.setVisibility(View.VISIBLE);
                    // new getclassdetails1().execute();
                    Getclassdetails();
                } else {
                    teacherlayout.setVisibility(View.VISIBLE);
                    studentparentlayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        GetTeacherList();
        spteacher = (MultiSelectionSpinner) findViewById(R.id.spinnerteacher);

        spteacher.setListener(this);
        spclass = (Spinner) findViewById(R.id.spinnerClass);
        spclass.setOnItemSelectedListener(new spclassListener());
        spdiv = (Spinner) findViewById(R.id.spinnerDiv);
        spdiv.setOnItemSelectedListener(new spdivlistener());
        spstudent = (MultiSelectionSpinner) findViewById(R.id.spinnerStudent);
        spstudent.setListener(this);
    }

    private class chooseBtnListener implements OnClickListener {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
           // Intent intent = new Intent();
           // intent.setType("*/*");
           /* intent.setAction(Intent.ACTION_GET_CONTENT);
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
            String[] projection = { "_data" };
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


    protected void InsertSpcificAnnouncementTask() {
        // TODO Auto-generated method stub
        String subStr = subSpec.getText().toString();
        String AnnounceStr = announcementEditSpec.getText().toString();
        String spcificCat = spcat.getSelectedItem().toString();
        /*String urlServer = AppConfig.BASE_URL
                + "specificannouncementmobs";
*/
        String urlServer = AppConfig.CLIENT_URL
                +"../"+ "test.php";
        RequestParams params = new RequestParams();
        if (spcificCat.equals("Teacher")) {

            List<Integer> pos = spteacher.getSelectedIndices();
            // String tId = teacherIdList.get(pos).toString();
            //Log.e("TeacherId",pos.toString());
            Integer index[] = pos.toArray(new Integer[pos.size()]);
            String tIds[] = new String[index.length];
            for (int i = 0; i < index.length; i++) {
                //  tIds [i]=teacherIdList.get(index[i]).toString();
                params.put("technameArray[" + i + "]", teacherIdList.get(index[i]).toString());

                Log.e("TeacherId", teacherIdList.get(index[i]).toString());
            }
            // Log.e("technameArray",params.get("technameArray[]"));
            params.put("fstart", dateS1.toString().trim());
            params.put("subject", subStr);
            params.put("announcement", AnnounceStr);
            params.put("selectedid", "1");

        } else if (spcificCat.equals("Student")) {

            List<Integer> pos1 = spstudent.getSelectedIndices();
            Integer index[] = pos1.toArray(new Integer[pos1.size()]);
            String tIds[] = new String[index.length];
            for (int i = 0; i < index.length; i++) {
                //  tIds [i]=teacherIdList.get(index[i]).toString();
                params.put("studentIdArray[]", studentIdList.get(index[i]).toString());
                //Log.e("TeacherId",teacherIdList.get(index[i]).toString());
            }


            params.put("textFieldDate", dateS1.trim().toString());
            params.put("textFieldSubject", subStr);
            params.put("selectedid", "2");
            // params.put("userId", sId);
            params.put("textAreaAnnouncement", AnnounceStr);

        } else if (spcificCat.equals("Parent")) {
            List<Integer> pos1 = spstudent.getSelectedIndices();
            Integer index[] = pos1.toArray(new Integer[pos1.size()]);
            String tIds[] = new String[index.length];
            for (int i = 0; i < index.length; i++) {
                //  tIds [i]=teacherIdList.get(index[i]).toString();
                params.put("studentIdArray[]", studentIdList.get(index[i]).toString());
                //Log.e("TeacherId",teacherIdList.get(index[i]).toString());
            }
            int pos = spstudent.getSelectedItemPosition();
            String sId = studentIdList.get(pos).toString();
            params.put("textFieldDate", dateS1.trim().toString());
            params.put("textFieldSubject", subStr);
            params.put("selectedid", "3");
            //params.put("userId", sId);
            params.put("textAreaAnnouncement", AnnounceStr);

        }

        if(path!=null) {
            params.put("image", path);
            params.put("droot", "specificannouncementmobs");

        }
        if (filenameTxt.getText().toString().trim()
                .equals("No file chosen")
                || filenameTxt.getText().toString().trim().equals("")) {
        }else {
            try {
                params.put("uploadedfile", new File(realpath));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(urlServer, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String arg0) {
                super.onSuccess(arg0);
                Log.v("from response", arg0);
                if (pDialog.isShowing())
                    pDialog.dismiss();
                //new getAssignmentList().execute();
                dateEditSpec.setText("");
                subSpec.setText("");
                announcementEditSpec.setText("");
                mt("Successfully Posted");
                path = null;
                filenameTxt.setText(path);
                 Toast.makeText(getBaseContext(), "Send Successfully",
                 Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
                pDialog = new ProgressDialog(Specific_announcement_Admin.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();

            }
        });











       /* String tag_string_req = "Req_InsertSpcificAnnouncement";
        pDialog.setMessage("Please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASE_URL + "specificannouncementmobs",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();

                        dateEditSpec.setText("");
                        subSpec.setText("");
                        announcementEditSpec.setText("");
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


            protected Map<String, String> getParams() {

                String subStr = subSpec.getText().toString();
                String AnnounceStr = announcementEditSpec.getText().toString();
                String spcificCat = spcat.getSelectedItem().toString();
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                if (spcificCat.equals("Teacher")) {

                    List<Integer> pos = spteacher.getSelectedIndices();
                    // String tId = teacherIdList.get(pos).toString();
                    //Log.e("TeacherId",pos.toString());
                    Integer index[] = pos.toArray(new Integer[pos.size()]);
                    String tIds[] = new String[index.length];
                    for (int i = 0; i < index.length; i++) {
                        //  tIds [i]=teacherIdList.get(index[i]).toString();
                        params.put("technameArray[" + i + "]", teacherIdList.get(index[i]).toString());

                        Log.e("TeacherId", teacherIdList.get(index[i]).toString());
                    }
                    // Log.e("technameArray",params.get("technameArray[]"));
                    params.put("fstart", dateS1.toString().trim());
                    params.put("subject", subStr);
                    params.put("announcement", AnnounceStr);
                    params.put("selectedid", "1");

                } else if (spcificCat.equals("Student")) {

                    List<Integer> pos1 = spstudent.getSelectedIndices();
                    Integer index[] = pos1.toArray(new Integer[pos1.size()]);
                    String tIds[] = new String[index.length];
                    for (int i = 0; i < index.length; i++) {
                        //  tIds [i]=teacherIdList.get(index[i]).toString();
                        params.put("studentIdArray[]", studentIdList.get(index[i]).toString());
                        //Log.e("TeacherId",teacherIdList.get(index[i]).toString());
                    }


                    params.put("textFieldDate", dateS1.trim().toString());
                    params.put("textFieldSubject", subStr);
                    params.put("selectedid", "2");
                    // params.put("userId", sId);
                    params.put("textAreaAnnouncement", AnnounceStr);

                } else if (spcificCat.equals("Parent")) {
                    List<Integer> pos1 = spstudent.getSelectedIndices();
                    Integer index[] = pos1.toArray(new Integer[pos1.size()]);
                    String tIds[] = new String[index.length];
                    for (int i = 0; i < index.length; i++) {
                        //  tIds [i]=teacherIdList.get(index[i]).toString();
                        params.put("studentIdArray[]", studentIdList.get(index[i]).toString());
                        //Log.e("TeacherId",teacherIdList.get(index[i]).toString());
                    }
                    int pos = spstudent.getSelectedItemPosition();
                    String sId = studentIdList.get(pos).toString();
                    params.put("textFieldDate", dateS1.trim().toString());
                    params.put("textFieldSubject", subStr);
                    params.put("selectedid", "3");
                    //params.put("userId", sId);
                    params.put("textAreaAnnouncement", AnnounceStr);

                }
                if(path!=null) {
                    params.put("image", path);
                    params.put("droot", ftp_url);
                    params.put("imagepath", realpath);
                }
                Log.e("JsonData:", params.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
*/
    }

    protected void Getclassdetails() {
        // TODO Auto-generated method stub
        String tag_string_req = "Req_classdetails";
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

                                String class_name = c.getString("classname");
                                String calssId = c.getString("classid");

                                calssList.add(class_name);
                                calssIdList.add(calssId);
                       if(calssList.size()>0) {
                           ArrayAdapter<String> classlistadapter = new ArrayAdapter<String>(
                                   Specific_announcement_Admin.this,
                                   android.R.layout.simple_spinner_item,
                                   calssList);

                           spclass.setAdapter(classlistadapter);
                           spclass.setSelection(0);

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

    private void GetTeacherList() {
        // TODO Auto-generated method stub
        String tag_string_req = "Req_TeacherList";
        teacherIdList.clear();
        teacherIdList.clear();
        pDialog.setMessage("Please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.BASE_URL + "teacherlists",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONObject json = new JSONObject(response);
                            JSONObject json1 = json.getJSONObject("response");
                            JSONArray calssdetails = json1
                                    .getJSONArray("teacherinfo");

                            for (int i = 0; i < calssdetails.length(); i++) {

                                JSONObject c = calssdetails.getJSONObject(i);

                                String tId = c.getString("id");
                                String tFName = c.getString("firstname");
                                String tLName = c.getString("lastname");
                                String tname = tFName + " " + tLName;
                                teacherIdList.add(tId);
                                teacherList.add(tname);
                            }
                                /*ArrayAdapter<String> teacherlistadapter = new ArrayAdapter<String>(
                                        Specific_announcement_Admin.this,
										android.R.layout.simple_spinner_item,
										teacherList);*/
                            if(teacherList.size()>0) {
                                spteacher.setItems(teacherList);
                                //teacherlistadapter.notifyDataSetChanged();
                                spteacher.setSelection(new int[]{});
                            }else{
                                Toast.makeText(getApplicationContext(),
                                        "There is no data", Toast.LENGTH_LONG).show();
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
    public void selectedIndices(List<Integer> indices) {

    }

    @Override
    public void selectedStrings(List<String> strings) {
        Toast.makeText(this, strings.toString(), Toast.LENGTH_LONG).show();
    }

    private class spdivlistener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub
            studentList.clear();
            studentIdList.clear();

            // new getStudentByClassDivId().execute();
            GetStudentByClassDivId();
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

            divList.clear();
            classdivIdList.clear();
            // new getDivByClassNameTask().execute();
            GetDivByClassNameTask();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
            Log.e("class", spclass.getSelectedItem().toString());
        }

    }

    private boolean validateForm() {

        if (dateEditSpec.getText().toString().equals("")) {
            mt("Please select the date! ");
            return false;
        } else if (subSpec.getText().toString().equals("")) {
            mt("Please enter the Subject! ");
            return false;
        } else if (announcementEditSpec.getText().toString().equals("")) {
            mt("Please enter the announcement! ");
            return false;
        } /*
		 * else if (spstudent.getSelectedItem().toString().equals("")) {
		 * mt("Please select the student! "); return false; } else if
		 * (spteacher.getSelectedItem().toString().equals("")) {
		 * mt("Please select the teacher! "); return false; }
		 */

        return true;

    }

    public void GetStudentByClassDivId() {
        // TODO Auto-generated method stub
        String tag_string_req = "Req_StudentByClassDivId";
        int key = spdiv.getSelectedItemPosition();
        String keyclassdivId = classdivIdList.get(key);
        pDialog.setMessage("Please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.BASE_URL + "studentalllists/" + keyclassdivId,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONObject json = new JSONObject(response);
                            JSONObject json1 = json.getJSONObject("response");

                            JSONArray calssdetails = json1
                                    .getJSONArray("userslist");

                            for (int i = 0; i < calssdetails.length(); i++) {

                                JSONObject c = calssdetails.getJSONObject(i);

                                String sId = c.getString("id");
                                String sFname = c.getString("firstname");
                                String sLname = c.getString("lastname");
                                String Sname = sFname + " " + sLname;

                                studentIdList.add(sId);
                                studentList.add(Sname);
                               /* ArrayAdapter<String> divlistadapter = new ArrayAdapter<String>(
                                        Specific_announcement_Admin.this,
                                        android.R.layout.simple_spinner_item,
                                        studentList);
*/
/*
                                spstudent.setAdapter(divlistadapter);
                                divlistadapter.notifyDataSetChanged();
                                spstudent.setSelection(0);
*/
                                if(studentList.size()>0) {
                                    spstudent.setItems(studentList);
                                    //teacherlistadapter.notifyDataSetChanged();
                                    spstudent.setSelection(new int[]{});
                                }else{
                                    Toast.makeText(getApplicationContext(),
                                            "There is no data", Toast.LENGTH_LONG).show();
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

    public void GetDivByClassNameTask() {
        // TODO Auto-generated method stub
        String tag_string_req = "Req_DivByClassName";
        int key = spclass.getSelectedItemPosition();
        String keyclassId = calssIdList.get(key);
        pDialog.setMessage("Please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.BASE_URL + "divisionrows/" + keyclassId,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONObject json = new JSONObject(response);
                            JSONObject json1 = json.getJSONObject("response");
                            JSONArray calssdetails = json1
                                    .getJSONArray("divlist");

                            for (int i = 0; i < calssdetails.length(); i++) {

                                JSONObject c = calssdetails.getJSONObject(i);

                                String div = c.getString("divname");
                                String classdivId = c.getString("classdivid");
                                classdivIdList.add(classdivId);
                                divList.add(div);
                                ArrayAdapter<String> divlistadapter = new ArrayAdapter<String>(
                                        Specific_announcement_Admin.this,
                                        android.R.layout.simple_spinner_item,
                                        divList);

                                spdiv.setAdapter(divlistadapter);
                                divlistadapter.notifyDataSetChanged();
                                spdiv.setSelection(0);

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

    private void mt(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private class DateOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
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
            Date fFdate = new Date(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
            String fromDate = null;
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
            formatter = new SimpleDateFormat("dd MMM yy");
            fromDate = formatter.format(fFdate);
            String toDate = formatter.format(fFdate);
            dateEditSpec.setText(toDate);


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

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.FileUtils;
import com.simplifiedschooling.app.util.NetworkUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class General_announcement_Admin extends ActionBarActivity {
    private static final String TAG = General_announcement_Admin.class
            .getSimpleName();
    private String BASEURL = AppConfig.BASE_URL;
    String Username, jsonStr = null, dateS1, dateS2, realpath, path;
    private ProgressDialog pDialog;
    TextView usernameTxt;
    TextView filename, filenameTxt;
    ListView lv;
    ImageButton backBtn;
    Button chooseBtn;
    ImageView iv;
    CheckBox tch, pch, sch;
    DateFormat formatter = null;
    EditText dateEdit, announcementEdit, sub;
    private int year;
    private int month;
    private int day;
    static final int DATE_PICKER_ID = 1111;
    Uri currImageURI;
    SharedPreferences userinfo;
    String ftp_url = null,SchoolFolder;
    private static final int REQUEST_CODE = 6384;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.announcementgen);
        /*
         * Bundle b = getIntent().getExtras(); Username =
		 * b.getString("Username"); usernameTxt = (TextView)
		 * findViewById(R.id.textUsername); usernameTxt.setText("   Welcome[" +
		 * Username + "]");
		 */
        userinfo = getSharedPreferences("User", MODE_PRIVATE);
        ftp_url = userinfo.getString("schoolftp", "");
        SchoolFolder =userinfo.getString("schoolfolder", "");
        iv = (ImageView) findViewById(R.id.calender);
        iv.setOnClickListener(new DateOnClickListener());
        dateEdit = (EditText) findViewById(R.id.editText1);
        dateEdit.setOnClickListener(new DateOnClickListener());
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        tch = (CheckBox) findViewById(R.id.checkBox1);
        sch = (CheckBox) findViewById(R.id.checkBox2);
        pch = (CheckBox) findViewById(R.id.checkBox3);
        sub = (EditText) findViewById(R.id.editTextSubject);
        announcementEdit = (EditText) findViewById(R.id.editTextAnnounce);
        filenameTxt = (TextView) findViewById(R.id.textfilename);
        chooseBtn = (Button) findViewById(R.id.ChooseBtn);
        chooseBtn.setOnClickListener(new chooseBtnListener());


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
                } else if (!tch.isChecked() && !pch.isChecked()
                        && !sch.isChecked()) {
                    mt("Please select to which you want send Announcement!");

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

    }


    private class chooseBtnListener implements OnClickListener {

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            //Intent intent = new Intent();
           // intent.setType("**/*//*");
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


    protected void InsertAnnouncementTask() {
        // TODO Auto-generated method stub

        String teacherEnalbe = null;
        String studentEnalbe = null;
        String parentEnalbe = null;
        // String dateStr = dateEdit.getText().toString();
        String subStr = sub.getText().toString();
        String AnnounceStr = announcementEdit.getText().toString();
        String urlServer = AppConfig.BASE_URL
                + "generalannouncementmobs";

        if (tch.isChecked() == true) {

            teacherEnalbe = "checked";
        } else {

            teacherEnalbe = "unchecked";
        }

        if (sch.isChecked() == true) {

            studentEnalbe = "checked";
        } else {

            studentEnalbe = "unchecked";
        }
        if (pch.isChecked() == true) {

            parentEnalbe = "checked";
        } else {

            parentEnalbe = "unchecked";
        }

        // Posting parameters to login url
        RequestParams params = new RequestParams();

        params.put("fstart", dateS1.toString().trim());
        params.put("subject", subStr);
        params.put("announcement", AnnounceStr);
        params.put("checkBoxTeachers", teacherEnalbe.toString().trim());
        params.put("checkBoxStudent", studentEnalbe.toString().trim());
        params.put("checkBoxParent", parentEnalbe.toString().trim());
        params.put("tempPath", path);
        params.put("droot", ftp_url);
        params.put("schoolfolder", SchoolFolder);
        params.put("imagepath", realpath);
        if (filenameTxt.getText().toString().trim()
                .equals("No file chosen")
                || filenameTxt.getText().toString().trim().equals("")) {
        }else {
            try {
                params.put("imagepath", new File(realpath));
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
                dateEdit.setText("");
                tch.setChecked(false);
                pch.setChecked(false);
                sch.setChecked(false);
                sub.setText("");
                announcementEdit.setText("");
                mt("Successfully Posted");
                path = null;
                filenameTxt.setText(path);
                // Toast.makeText(getBaseContext(), arg0,
                // Toast.LENGTH_LONG).show();

            }

            @Override
            public void onStart() {
                // TODO Auto-generated method stub
                super.onStart();
                pDialog = new ProgressDialog(General_announcement_Admin.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();

            }
        });


        /*String tag_string_req = "Req_InsertGenAnnouncementTask";
        pDialog.setMessage("Please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                BASEURL + "generalannouncements",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();

                        dateEdit.setText("");
                        tch.setChecked(false);
                        pch.setChecked(false);
                        sch.setChecked(false);
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

                String teacherEnalbe = null;
                String studentEnalbe = null;
                String parentEnalbe = null;
                // String dateStr = dateEdit.getText().toString();
                String subStr = sub.getText().toString();
                String AnnounceStr = announcementEdit.getText().toString();
                if (tch.isChecked() == true) {

                    teacherEnalbe = "checked";
                } else {

                    teacherEnalbe = "unchecked";
                }

                if (sch.isChecked() == true) {

                    studentEnalbe = "checked";
                } else {

                    studentEnalbe = "unchecked";
                }
                if (pch.isChecked() == true) {

                    parentEnalbe = "checked";
                } else {

                    parentEnalbe = "unchecked";
                }

                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();

                params.put("fstart", dateS1.toString().trim());
                params.put("subject", subStr);
                params.put("announcement", AnnounceStr);
                params.put("checkBoxTeachers", teacherEnalbe.toString().trim());
                params.put("checkBoxStudent", studentEnalbe.toString().trim());
                params.put("checkBoxParent", parentEnalbe.toString().trim());
                params.put("tempPath", path);
                params.put("droot", ftp_url);
                params.put("imagepath", realpath);


                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
*/
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

			/*Calendar calendar = Calendar.getInstance();
		    calendar.set(year, month, day);
		     String mMonth=checkDigit(month);
			 Date d = new Date(new Date(year,month,day).toString());
			  SimpleDateFormat formatter = new SimpleDateFormat( "EEE, MMM d, ''yy");
			  SimpleDateFormat formatter1 = new SimpleDateFormat( "dd MMM yyyy");
			  //dateS1 = formatter.format(d);
			  
			  dateS2 = formatter.format(d);
			  
			  dateEdit.setText(dateS2);*/


            dateS1 = year + "-" + (month + 1) + "-" + day;
            Date fFdate = new Date(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
            String fromDate = null;
            formatter = new SimpleDateFormat("dd MMM yy");
            fromDate = formatter.format(fFdate);
            String toDate = formatter.format(fFdate);
            dateEdit.setText(toDate);
        }
    };

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
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

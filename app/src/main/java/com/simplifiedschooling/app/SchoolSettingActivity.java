package com.simplifiedschooling.app;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.simplifiedschooling.app.helper.SingleMediaScanner;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SchoolSettingActivity extends Activity {
    private long ms = 0;
    private long splashDuration = 3000;
    private boolean splashActive = true;
    private boolean paused = false;
    private String imagepath = null;
    private String imageName = null;
    // private boolean isfirstTime=true;
    SharedPreferences userinfo;
    private ProgressDialog pDialog;
    ProgressBar Loder;
    Intent intent;
    Spinner mCity;
    Spinner mSchool;
    String rolename;
    String images[] = {"background.png", "innerpage_top.png"};


    final Context context = this;
    Set<String> s = new HashSet<String>();
    private static final String TAG = SchoolSettingActivity.class
            .getSimpleName();
    public static ArrayList<String> CITY = new ArrayList<String>();
    public static ArrayList<String> SCHOOL = new ArrayList<String>();
    public static ArrayList<String> SCHOOLDBNAME = new ArrayList<String>();
    public static ArrayList<String> FTP_URL = new ArrayList<String>();
    public static ArrayList<String> SCHOOLFOLDER = new ArrayList<String>();
    public static String DataBaseName, SchoolFolder;

    // schoolinfomobs/1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashscreen);
        pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
        userinfo = this.getSharedPreferences("User", MODE_PRIVATE);
        DataBaseName = userinfo.getString("schooldbname", "");
        SchoolFolder = userinfo.getString("schoolfolder", "");

        if (DataBaseName.isEmpty()) {

            final Dialog dialog3 = new Dialog(context);
            dialog3.setContentView(R.layout.schoolsetting);
            dialog3.setTitle("School Setting");
            dialog3.setCancelable(false);
            mCity = (Spinner) dialog3.findViewById(R.id.spinnerCity);
            GetGroupData();
            mCity.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    // TODO Auto-generated method stub
                    GetSchoolFormCity();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub

                }
            });
            mSchool = (Spinner) dialog3.findViewById(R.id.spinnerSchool);
            Button mSubmit = (Button) dialog3.findViewById(R.id.buttonSubmit);
            mSubmit.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

					/*if (!mCity.isSelected() && !mSchool.isSelected()) {
						Toast.makeText(getApplicationContext(), "Please select city and school!", Toast.LENGTH_SHORT).show();
					} else {
*/

                    int pos = mCity.getSelectedItemPosition();
                    String city = CITY.get(pos - 1).toString();
                    int pos1 = mSchool.getSelectedItemPosition();
                    String school = SCHOOL.get(pos1 - 1).toString();
                    String schooldbname = SCHOOLDBNAME.get(pos1 - 1).toString();
                    String ftpurl = FTP_URL.get(pos1 - 1);
                    String schoolfolder = SCHOOLFOLDER.get(pos1 - 1);
                    AppConfig.CLIENT_URL = AppConfig.CLIENT_URL1 + schoolfolder
                            + "/web/";
                    AppConfig.BASE_URL = AppConfig.BASE_URL1 + schooldbname
                            + "/";
                    userinfo = getSharedPreferences("User", MODE_PRIVATE);
                    SharedPreferences.Editor preferencesEditer = userinfo
                            .edit();
                    // preferencesEditer.putBoolean("isfirstTime", false);
                    preferencesEditer.putString("city", city);
                    preferencesEditer.putString("school", school);
                    preferencesEditer.putString("schooldbname", schooldbname);
                    preferencesEditer.putString("schoolftp", ftpurl);
                    preferencesEditer.putString("schoolfolder", schoolfolder);
                    preferencesEditer.commit();
                    if (userinfo.getBoolean("saveLogin", false)) {
						/*
						 * intent = new Intent(SchoolSettingActivity.this,
						 * HomeActivity.class);
						 */
                        rolename = userinfo.getString("rolename", "");

                        if (rolename.equalsIgnoreCase("Teacher")) {
                            intent = new Intent(SchoolSettingActivity.this,
                                    HomeActivity.class);

                        } else if (rolename.equalsIgnoreCase("Admin")) {
                            intent = new Intent(SchoolSettingActivity.this,
                                    AdminHomeActivity.class);

                        } else if (rolename.equalsIgnoreCase("Parentinfo")) {
                            intent = new Intent(SchoolSettingActivity.this,
                                    ParentHomeActivity.class);
                        } else if (rolename.equalsIgnoreCase("Student")) {
                            intent = new Intent(SchoolSettingActivity.this,
                                    StudentHomeActivity.class);
                        } else {
                            intent = new Intent(SchoolSettingActivity.this,
                                    MainActivity.class);

                        }

                    } else {
                        intent = new Intent(SchoolSettingActivity.this,
                                MainActivity.class);

                    }
                    for (int i = 0; i < images.length; i++) {
                        imagepath = AppConfig.CLIENT_URL + "../androidimage/" + images[i];
                        Log.e("ImagePath>>>", imagepath);
                        new DownloadTask(SchoolSettingActivity.this)
                                .execute(imagepath);
                    }
                    startActivity(intent);
                    finish();

                }

            });

            dialog3.show();

        } else {
            AppConfig.CLIENT_URL = null;
            AppConfig.CLIENT_URL = AppConfig.CLIENT_URL1 + SchoolFolder
                    + "/web/app_dev.php/";

            AppConfig.BASE_URL = null;
            AppConfig.BASE_URL = AppConfig.BASE_URL1 + DataBaseName + "/";
            String file = context.getExternalFilesDir(null).getAbsolutePath() + "/"
                    + "background.png";
            File f = new File(file);
            if (f.exists() && f.length() > 0) {

            } else {
                for (int i = 0; i < images.length; i++) {
                    imagepath = AppConfig.CLIENT_URL + "../androidimage/"
                            + images[i];
                    Log.e("ImagePath>>>", imagepath);
                    new DownloadTask(SchoolSettingActivity.this)
                            .execute(imagepath);
                }
            }
            intent = new Intent(SchoolSettingActivity.this,
                    SplashScreenActivity.class);
            startActivity(intent);
            finish();

        }

    }


    /*
     * Thread mythread = new Thread() { public void run() { try { while
     * (splashActive && ms < splashDuration) { if (!paused) ms = ms + 100;
     * sleep(100); } } catch (Exception e) { } finally {} } }; mythread.start();
     * }
     */
    public void GetSchoolFormCity() {
        // TODO Auto-generated method stub
        String tag_string_req = "Get School";
        // GalleryName.clear();
        SCHOOL.clear();
        SCHOOLDBNAME.clear();
        FTP_URL.clear();
        SCHOOL.add("Select School");
        pDialog.setMessage("Please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.SERVER_URl + "schoollistmobs",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "School Data: " + response.toString());
                        hideDialog();

                        try {

                            JSONObject json = new JSONObject(response);
                            JSONObject json1 = json.getJSONObject("response");
                            JSONArray calssdetails = json1
                                    .getJSONArray("users");

                            for (int i = 0; i < calssdetails.length(); i++) {

                                JSONObject c = calssdetails.getJSONObject(i);
                                String school = c.getString("school_name");
                                // String city = c.getString("address");
                                String databasename = c
                                        .getString("school_databasename");
                                String schoolfolder = c
                                        .getString("schoolfolder");

                                String ftp_url = c.getString("ftp_url");
                                SCHOOL.add(school);
                                SCHOOLDBNAME.add(databasename);
                                FTP_URL.add(ftp_url);
                                SCHOOLFOLDER.add(schoolfolder);
                            }

                            if (SCHOOL.size() > 0) {
                                ArrayAdapter<String> cityadapter = new ArrayAdapter<String>(
                                        SchoolSettingActivity.this,
                                        android.R.layout.simple_spinner_item,
                                        SCHOOL);
                                mSchool.setAdapter(cityadapter);

                            } else {
                                Toast.makeText(getBaseContext(),
                                        "There is no Data!", Toast.LENGTH_SHORT)
                                        .show();
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
                int pos = mCity.getSelectedItemPosition();
                String city = CITY.get(pos).toString();
                Map<String, String> params = new HashMap<String, String>();
                params.put("group_id", AppConfig.GroupId);
                params.put("city", city);
                Log.d("Params", params.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private ProgressDialog pDialog;
        File outputFile = null;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            pDialog = new ProgressDialog(SchoolSettingActivity.this);
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

                imageName = path[path.length - 1];
				/*
				 * String path1[] = assment_path.split("_"); assment_path =
				 * path1[path1.length - 1]; String path2[] =
				 * assment_path.split("_"); assment_path = path2[path2.length -
				 * 1];
				 */
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
                // Environment.getExternalStorageState();

                File wallpaperDirectory = context.getExternalFilesDir(null);

                // File wallpaperDirectory = new File("/sdcard/photos/");
                if (wallpaperDirectory.exists()
                        && wallpaperDirectory.isDirectory()) {

                } else {
                    wallpaperDirectory.mkdirs();
                }
                outputFile = new File(wallpaperDirectory, imageName);
                input = connection.getInputStream();

                output = new FileOutputStream(outputFile);
                // FileOutputStream fos = new FileOutputStream(outputFile);

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

            //	pDialog.dismiss();

            if (result != null)
                // Toast.makeText(context,"Download error: ",
                // Toast.LENGTH_LONG).show();
                Log.e("Error", AppConfig.CLIENT_URL + "../" + "androidimage/"
                        + imageName + result);
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT)
                        .show();
            if (outputFile != null) {
                new SingleMediaScanner(SchoolSettingActivity.this, outputFile);
            } else {
                Log.e("ImageDownloadError", "There is no image");
            }
			/*
			 * File myFile = new File("/mnt/sdcard/" + assment_path); try {
			 * FileOpen.openFile(ImagePagerActivity.this, myFile); } catch
			 * (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } catch (ActivityNotFoundException e) { //
			 * TODO Auto-generated catch block e.printStackTrace();
			 * 
			 * Toast.makeText( getApplicationContext(),
			 * "There is no app install to view" + " " + assment_path +
			 * " file.", Toast.LENGTH_LONG).show(); }
			 */
        }

    }

    private void GetGroupData() {
        // TODO Auto-generated method stub
        String tag_string_req = "req_login";
        // GalleryName.clear();
        CITY.clear();
        CITY.add("Select City");
        pDialog.setMessage("Please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.SERVER_URl + "schoolinfomobs/" + AppConfig.GroupId,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Group Data: " + response.toString());
                        hideDialog();

                        try {

                            JSONObject json = new JSONObject(response);
                            JSONObject json1 = json.getJSONObject("response");
                            JSONArray calssdetails = json1
                                    .getJSONArray("users");

                            for (int i = 0; i < calssdetails.length(); i++) {

                                JSONObject c = calssdetails.getJSONObject(i);
                                // String school = c.getString("school_name");
                                String city = c.getString("city");
                                // String databasename =
                                // c.getString("school_databasename");

                                s.add(city);
                            }
                            CITY.addAll(s);
                            if (CITY.size() > 0) {
                                ArrayAdapter<String> cityadapter = new ArrayAdapter<String>(
                                        SchoolSettingActivity.this,
                                        android.R.layout.simple_spinner_item,
                                        CITY);

                                mCity.setAdapter(cityadapter);

                            } else {
                                Toast.makeText(getBaseContext(),
                                        "There is no Data!", Toast.LENGTH_SHORT)
                                        .show();
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
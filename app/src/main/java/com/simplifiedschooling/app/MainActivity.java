package com.simplifiedschooling.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.simplifiedschooling.app.helper.CustomDailog;
import com.simplifiedschooling.app.helper.SessionManager;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;
import com.simplifiedschooling.app.util.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    CheckBox mCheckBoxRememberme;
    private ProgressDialog pDialog;
    private SessionManager session;
    private String regGCMId, jsonStr = null, roleid, user_id, id, mUsername,
            rolename;
    GoogleCloudMessaging gcm;
    Context context;
    SharedPreferences userinfo;
    String dBname,schoolFolder;
    public static final String REG_ID = "regId";
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        userinfo = getSharedPreferences("User", MODE_PRIVATE);
        dBname = userinfo.getString("schooldbname", "");
        schoolFolder=userinfo.getString("schoolfolder", "");
        Log.e("dBname", dBname);
        if (AppConfig.BASE_URL == null) {
            AppConfig.CLIENT_URL = null;
            AppConfig.BASE_URL = AppConfig.BASE_URL1 + dBname + "/";
            AppConfig.CLIENT_URL =AppConfig.CLIENT_URL1 + schoolFolder + "/web/app_dev.php/";
        }

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);

        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        mCheckBoxRememberme = (CheckBox) findViewById(R.id.checkboxRememberme);
        btnLogin = (Button) findViewById(R.id.btnLogin);


        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));

        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            regGCMId = savedInstanceState.getString(REG_ID);

        } else {
            // Probably initialize members with default values for a new
            // instance
            regGCMId = registerGCM();
        }

        if (userinfo.getBoolean("saveLogin", false)) {
            mCheckBoxRememberme.setChecked(true);
        } else {
            mCheckBoxRememberme.setChecked(false);
        }
        if (mCheckBoxRememberme.isChecked() && userinfo.contains("USERNAME")) {
            inputEmail.setText(userinfo.getString("USERNAME", ""));
            inputPassword.setText(userinfo.getString("PAASSWORD", ""));
            // loginprocess();
            // new userLoginTask().execute();
            // checkLogin(userinfo.getString("USERNAME", ""),
            // userinfo.getString("PAASSWORD", ""));
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();


                if (com.simplifiedschooling.app.util.NetworkUtilities
                        .isInternet(context)) {

                    if (userinfo.getString("USERNAME", "").equals("")) {

                        checkLogin(email, password);

                    } else if (userinfo.getString("USERNAME", "").equals(
                            email)
                            && userinfo.getString("PAASSWORD", "").equals(
                            password)) {
                        checkLogin(email, password);
                        // mt("Login Successfully!");
                            /*
                             * Intent i = new Intent(MainActivity.this,
							 * HomeActivity.class); i.putExtra("Username",
							 * userName.getText().toString());
							 * i.putExtra("mUsername"
							 * ,userinfo.getString("mUSERNAME", "" ));
							 * startActivity(i); finish();
							 */
                    } else if (userinfo.getString("USERNAME", "") != (email)
                            && userinfo.getString("PAASSWORD", "") != (password)) {
                        checkLogin(email, password);

                    } else {
                        CustomDailog.displayDialog(
                                R.string.Incorrect_Username_Password,
                                context);

                    }
                    userinfo = getSharedPreferences("User", MODE_PRIVATE);
                    SharedPreferences.Editor preferencesEditer = userinfo
                            .edit();
                    if (mCheckBoxRememberme.isChecked()) {

                        preferencesEditer.putBoolean("saveLogin", true);
                        preferencesEditer.commit();
                    } else {

                        preferencesEditer.putBoolean("saveLogin", false);
                        preferencesEditer.commit();
                    }

                } else {
                    CustomDailog.displayDialog(
                            R.string.please_check_your_internet_connection,
                            context);
                }


                // Check for empty data in the form
				/*
				 * if (!email.isEmpty() && !password.isEmpty()) { // login user
				 * if (NetworkUtilities.isInternet(context)) { checkLogin(email,
				 * password); }else{
				 * 
				 * Toast.makeText(getApplicationContext(),
				 * "Please Check Internet Connection!", Toast.LENGTH_LONG)
				 * .show(); }
				 * 
				 * } else { // Prompt user to enter credentials
				 * Toast.makeText(getApplicationContext(),
				 * "Please enter the credentials!", Toast.LENGTH_LONG) .show();
				 * }
				 */
            }

        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        String mRegisterGCMId = getRegistrationId(context);
        outState.putString(REG_ID, mRegisterGCMId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        regGCMId = savedInstanceState.getString(REG_ID);

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.email:
                    validateName();
                    break;

                case R.id.password:
                    validatePassword();
                    break;
            }
        }
    }

    private boolean validateName() {
        if (inputEmail.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.Username_cannot_be_empty));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.Password_cannot_be_empty));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateForm() {
        if (inputEmail.getText().toString().trim().equals("")) {
            CustomDailog.displayDialog(R.string.Username_cannot_be_empty,
                    context);
            return false;
        } else if (inputPassword.getText().toString().trim().equals("")) {
            CustomDailog.displayDialog(R.string.Password_cannot_be_empty,
                    context);
            return false;
        }
        return true;
    }

    private String registerGCM() {
        // TODO Auto-generated method stub

        gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
        regGCMId = getRegistrationId(context);
        if (TextUtils.isEmpty(regGCMId)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(
                    MainActivity.this);

            builder.setTitle("GCM Registration")
                    .setMessage(
                            "Please accept GCM Registration request for receiving further Notification from Simplifiedschool.")
                    .setCancelable(true)
                    .setPositiveButton("Accept",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {

                                    registerInBackground();

                                    Log.d("RegisterActivity",
                                            "registerGCM - successfully registered with GCM server - regId: "
                                                    + regGCMId);
                                }

                            });
            builder.show();

        } else {
			/*
			 * Toast.makeText(getApplicationContext(),
			 * "RegId already available. RegId: " + regGCMId,
			 * Toast.LENGTH_LONG).show();
			 */
        }

        return regGCMId;

    }

    private void registerInBackground() {
        // TODO Auto-generated method stub

        new AsyncTask<Void, Void, String>() {

            ProgressDialog pDialog;

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Registering with Server.Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regGCMId = gcm.register(Config.GOOGLE_PROJECT_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regGCMId);
                    msg = "Device registered, registration ID=" + regGCMId;

                    storeRegistrationId(context, regGCMId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
                        .show();
            }
        }.execute(null, null, null);

    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        // int appVersion = getAppVersion(context);
        // Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        // editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }

    @SuppressLint("NewApi")
    private String getRegistrationId(Context context) {
        // TODO Auto-generated method stub
        final SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
		/*
		 * int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		 * int currentVersion = getAppVersion(context); if (registeredVersion !=
		 * currentVersion) { Log.i(TAG, "App version changed."); return ""; }
		 */
        return registrationId;

    }

    /**
     * function to verify login details in mysql db
     */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASE_URL + "userlogins",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            JSONObject resp = jObj.getJSONObject("response");
                            // JSONObject user = resp.getJSONObject("users");

                            String status = resp.getString("status");
                            if (status.equals("false")) {
                                String msg = resp.getString("message");
                                CustomDailog.displayErrorDialog(msg, context);

                            } else if (status.equals("true")) {
                                rolename = resp.getString("rolename");

                                // Check for error node in json
                                if (rolename.equalsIgnoreCase("Teacher")
                                        || rolename.equalsIgnoreCase("Admin")
                                        || rolename
                                        .equalsIgnoreCase("Parentinfo")
                                        || rolename.equalsIgnoreCase("Student")) {
                                    // user successfully logged in
                                    // Create login session
                                    // session.setLogin(true);

                                    // Now store the user in SQLite

                                    resp = jObj.getJSONObject("response");
                                    roleid = resp.getString("roleid");
                                    // rolename=resp.getString("rolename");
                                    JSONArray user = resp.getJSONArray("users");

                                    user_id = user.getJSONObject(0).getJSONObject("0").getString("user_id");
                                    id = user.getJSONObject(0).getJSONObject("0").getString("id");
                                    Log.d(TAG, "Login id: " + id.toString());
                                    if(user.getJSONObject(0).getJSONObject("0").getString("firstname")=="-" && rolename
                                            .equalsIgnoreCase("Parentinfo"))
                                    {
                                        mUsername = user.getJSONObject(0)
                                                .getJSONObject("0")
                                                .getString("motherfirstname")
                                                + " "
                                                + user.getJSONObject(0)
                                                .getJSONObject("0")
                                                .getString("lastname");
                                    }else {
                                        mUsername = user.getJSONObject(0)
                                                .getJSONObject("0")
                                                .getString("firstname")
                                                + " "
                                                + user.getJSONObject(0)
                                                .getJSONObject("0")
                                                .getString("lastname");
                                    }
                                    Log.d(TAG, "Login Response: " + roleid
                                            + " " + user_id);
                                    // Inserting row in users table
                                    // db.addUser(name, email, uid, created_at);

                                    // Launch main activity

                                    final SharedPreferences prefs = getSharedPreferences(
                                            MainActivity.class.getSimpleName(),
                                            Context.MODE_PRIVATE);
                                    String registrationId = prefs.getString(
                                            REG_ID, "");
                                    if (registrationId != null) {
                                        // new storeRegidTask().execute();
                                        StoreRegidTask();

                                    } else {

                                    }

                                    Editor edit = userinfo.edit();
                                    edit.putString("role_id", roleid);
                                    edit.putString("user_id", user_id);
                                    edit.putString("id", id);
                                    edit.putString("USERNAME", email);
                                    edit.putString("PAASSWORD", password);
                                    edit.putString("mUSERNAME", mUsername);
                                    edit.putString("rolename", rolename);
                                    edit.commit();
                                    Intent intent = null;
                                    if (rolename.equalsIgnoreCase("Teacher")) {
                                        intent = new Intent(MainActivity.this,
                                                HomeActivity.class);

                                    } else if (rolename
                                            .equalsIgnoreCase("Admin")) {
                                        intent = new Intent(MainActivity.this,
                                                AdminHomeActivity.class);

                                    } else if (rolename
                                            .equalsIgnoreCase("Parentinfo")) {
                                        intent = new Intent(MainActivity.this,
                                                ParentHomeActivity.class);

                                    } else if (rolename
                                            .equalsIgnoreCase("Student")) {
                                        intent = new Intent(MainActivity.this,
                                                StudentHomeActivity.class);

                                    } else {
                                        CustomDailog.displayErrorDialog(
                                                "Incorrect_Username_Password",
                                                context);

                                    }

                                    intent.putExtra("role_id", roleid);
                                    intent.putExtra("user_id", user_id);
                                    intent.putExtra("id", id);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    // Error in login. Get the error message
                                    String errorMsg = jObj.getString("message");
                                    Toast.makeText(getApplicationContext(),
                                            errorMsg, Toast.LENGTH_LONG).show();

                                }
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();

                            Toast.makeText(getApplicationContext(),
                                    "Json error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    protected void StoreRegidTask() {
        // TODO Auto-generated method stub
        String tag_string_req = "req_StoreRegidTask";

        // pDialog.setMessage("Logging in ...");
        // showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.BASE_URL + "gcms", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                // hideDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                // hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                final SharedPreferences prefs = getSharedPreferences(
                        MainActivity.class.getSimpleName(),
                        Context.MODE_PRIVATE);
                String registrationId = prefs.getString(REG_ID, "");
                Map<String, String> params = new HashMap<String, String>();

                params.put("gcm_id", registrationId);
                params.put("role_id", roleid);
                params.put("user_id", user_id);
                params.put("rolename", rolename);
                return params;
            }

        };

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

package com.simplifiedschooling.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.simplifiedschooling.app.adapter.GalleryGridViewAdapter;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;
import com.simplifiedschooling.app.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ClassGallery_list extends ActionBarActivity {
    private static final String TAG = ClassGallery_list.class.getSimpleName();
    private String BASEURL = AppConfig.BASE_URL;
    String Username,mUsername, jsonStr = null, ass_path = null, assignment_path,
            download_path,id;
    private ProgressDialog pDialog;
    TextView usernameTxt;
    GridView mGallery;
    ImageButton backBtn;
    Spinner sp;
    String u_classDivId = null;
    public static ArrayList<String> GalleryName = new ArrayList<String>();
    ArrayList<String> ch_name = new ArrayList<String>();
    ArrayList<String> calssList = new ArrayList<String>();
    ArrayList<String> calssDivIdList = new ArrayList<String>();
    SharedPreferences userinfo;
    Set<String> s = new LinkedHashSet<String>();
    Map<String,String> map = new HashMap<String,String>();
    private GalleryGridViewAdapter adapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(
                Drawable.createFromPath(this.getExternalCacheDir()
                        .getAbsolutePath() + "/" + "innerpage_top.png"));
        setContentView(R.layout.teacher_gallery);
        userinfo = getSharedPreferences("User", MODE_PRIVATE);
        id = userinfo.getString("id", "");

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            id = savedInstanceState.getString("id");

        } else {
            // Probably initialize members with default values for a new instance
            id = userinfo.getString("id", "");
        }
        sp = (Spinner) findViewById(R.id.spinnerChild);
        sp.setOnItemSelectedListener(new spChildListener());
        mGallery = (GridView) findViewById(R.id.gallery);
        //GridView = (ListView) findViewById(R.id.gallerylist);
        //new getClassAttendence().execute();
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        GetClassAttendence();
        mGallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                String galleryname = GalleryName.get(arg2);

                Intent i = new Intent(ClassGallery_list.this, ClassGalleryView.class);

                i.putExtra("GalleryName", galleryname);

                startActivity(i);



            }

        });

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putString("id", id);
    }












    private void GetClassAttendence() {
        // TODO Auto-generated method stub
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        calssList.clear();
        calssDivIdList.clear();
        pDialog.setMessage("Please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.BASE_URL + "classesofteachermob",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Login Response: " + response.toString());
                        hideDialog();

                        try {

                            JSONObject json = new JSONObject(response);
                            JSONObject resp = json.getJSONObject("response");
                            JSONArray calssdetails = resp.getJSONArray("classinfo");
                            for (int i = 0; i < calssdetails.length(); i++) {

                                JSONObject c = calssdetails.getJSONObject(i);

                                String classdivid = c.getString("classdivid");
                                String classname = c.getString("classname");
                                String divname = c.getString("divname");
                                calssList.add(classname + " " + divname);
                                calssDivIdList.add(classdivid);
                                ArrayAdapter<String> classlistadapter = new ArrayAdapter<String>(
                                        ClassGallery_list.this, android.R.layout.simple_spinner_item,
                                        calssList);
                                sp.setAdapter(classlistadapter);
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



    private class spChildListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub

            GetGalleyNameTask();
        }

        private void GetGalleyNameTask() {
            // TODO Auto-generated method stub
            String tag_string_req = "req_login";
            GalleryName.clear();
            Constant.ImagePath.clear();
            adapter=null;
            s.clear();
            pDialog.setMessage("Please wait ...");
            showDialog();
            int pos =sp.getSelectedItemPosition();
            String classDivId =calssDivIdList.get(pos).toString();
            StringRequest strReq = new StringRequest(Request.Method.GET,
                    AppConfig.BASE_URL + "classgallerylists/"+classDivId,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "Login Response: " + response.toString());
                            hideDialog();

                            try {

                                JSONObject json = new JSONObject(response);
                                JSONObject json1 = json.getJSONObject("response");
                                JSONArray calssdetails = json1
                                        .getJSONArray("schoolgallery");

                                for (int i = 0; i < calssdetails.length(); i++) {

                                    JSONObject c = calssdetails.getJSONObject(i);
                                    String galleryName = c.getString("galleryname");
                                    String ImagePath = c.getString("photo");

                                    if(s.add(galleryName))
                                    {

                                        Constant.ImagePath.add(ImagePath);

                                    }


                                }
                                GalleryName.addAll(s);
                                if (GalleryName.size() > 0) {
                                    adapter = new GalleryGridViewAdapter(
                                            ClassGallery_list.this,
                                            GalleryName);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getBaseContext(), "There is no Gallery!",
                                            Toast.LENGTH_SHORT).show();
                                }

                                mGallery.setAdapter(adapter);



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
        public void onNothingSelected(AdapterView<?> arg0) {
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

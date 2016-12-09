package com.simplifiedschooling.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
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
import java.util.LinkedHashSet;
import java.util.Set;

public class Parent_SchoolGalleryList extends ActionBarActivity {
	private static final String TAG = Parent_SchoolGalleryList.class.getSimpleName();
	private String BASEURL = AppConfig.BASE_URL;
	String Username, jsonStr = null, ass_path = null, assignment_path,
			download_path;
	private ProgressDialog pDialog;
	TextView usernameTxt;
	GridView mGallery;
	ImageButton backBtn;
	Set<String> s = new LinkedHashSet<String>();
	String u_classDivId = null;
	public static ArrayList<String> GalleryName = new ArrayList<String>();
	private GalleryGridViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				Drawable.createFromPath(this.getExternalCacheDir()
						.getAbsolutePath() + "/" + "innerpage_top.png"));
		setContentView(R.layout.gallery_list);

		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		mGallery = (GridView) findViewById(R.id.gallery);
		mGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				String galleryname = GalleryName.get(arg2);

				Intent i = new Intent(Parent_SchoolGalleryList.this, GalleryView.class);
				// i.putExtra("Username", Username);
				i.putExtra("GalleryName", galleryname);

				startActivity(i);

			}

		});
		// new getGalleyNameTask().execute();
		GetGalleyNameTask();
	}

	private void GetGalleyNameTask() {
		// TODO Auto-generated method stub
		String tag_string_req = "req_login";
		GalleryName.clear();
		s.clear();
		Constant.ImagePath.clear();
		pDialog.setMessage("Please wait ...");
		showDialog();

		StringRequest strReq = new StringRequest(Request.Method.GET,
				BASEURL + "schoolgallerylist",
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


								if (s.add(galleryName)) {
									Constant.ImagePath.add(ImagePath);
								}

							}
							GalleryName.addAll(s);
							if (GalleryName.size() > 0) {

								adapter = new GalleryGridViewAdapter(
										Parent_SchoolGalleryList.this,
										GalleryName);
								mGallery.setAdapter(adapter);
							} else {
								Toast.makeText(getBaseContext(),
										"There is no Gallery!",
										Toast.LENGTH_SHORT).show();
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

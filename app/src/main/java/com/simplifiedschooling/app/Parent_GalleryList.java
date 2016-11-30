package com.simplifiedschooling.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.simplifiedschooling.app.helper.ChildData;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;
import com.simplifiedschooling.app.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class Parent_GalleryList extends ActionBarActivity {
	private static final String TAG = Parent_GalleryList.class.getSimpleName();
	String Username, mUsername, jsonStr = null, ass_path = null,
			assignment_path, download_path, id;
	private ProgressDialog pDialog;
	TextView usernameTxt;
	GridView mGallery;
	ImageButton backBtn;
	Spinner sp;
	String u_classDivId = null;
	SharedPreferences userinfo;
	public static ArrayList<String> GalleryName = new ArrayList<String>();
	ArrayList<String> ch_name = new ArrayList<String>();
	Set<String> s = new LinkedHashSet<String>();
	private GalleryGridViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.class_gallery);

		userinfo = getSharedPreferences("User", MODE_PRIVATE);
		id = userinfo.getString("id", "");

		if (savedInstanceState != null) {
			// Restore value of members from saved state
			id = savedInstanceState.getString("id");

		} else {
			// Probably initialize members with default values for a new
			// instance
			id = userinfo.getString("id", "");
		}
		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		sp = (Spinner) findViewById(R.id.spinnerChild);
		sp.setOnItemSelectedListener(new spChildListener());
		mGallery = (GridView) findViewById(R.id.gallery);
		mGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String galleryname = GalleryName.get(arg2);

				Intent i = new Intent(Parent_GalleryList.this,
						GalleryView.class);
				i.putExtra("Username", Username);
				i.putExtra("GalleryName", galleryname);
				i.putExtra("classDivId", u_classDivId);

				startActivity(i);

			}

		});

		// new getChildListTask().execute();
		GetChildListTask();
	}

	private void GetChildListTask() {
		// TODO Auto-generated method stub
		String tag_string_req = "req_login";

		Constant.childData.clear();
		ch_name.clear();
		pDialog.setMessage("Please wait ...");
		showDialog();

		StringRequest strReq = new StringRequest(Request.Method.GET,
				AppConfig.BASE_URL + "studentlists/" + id,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Login Response: " + response.toString());
						hideDialog();

						try {

							JSONObject json = new JSONObject(response);
							JSONObject json1 = json.getJSONObject("response");
							JSONArray calssdetails = json1
									.getJSONArray("users");

							for (int i = 0; i < calssdetails.length(); i++) {

								JSONObject c = calssdetails.getJSONObject(i);
								String c_studentId = c.getString("id");
								String c_classdivId = c.getString("classdivid");
								String c_firstName = c.getString("firstname");
								String c_lastName = c.getString("lastname");
								ch_name.add(c_firstName + " " + c_lastName);
								Constant.childData.add(new ChildData(
										c_studentId, c_classdivId, c_firstName,
										c_lastName));

								if (Constant.childData.size() > 0) {
									sp.setAdapter(new ArrayAdapter<String>(
											Parent_GalleryList.this,
											android.R.layout.simple_spinner_item,
											ch_name));
								} else {
									Toast.makeText(Parent_GalleryList.this,
											"No data in the list",
											Toast.LENGTH_SHORT).show();

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

	private class spChildListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
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
			int pos = sp.getSelectedItemPosition();
			String classDivId = Constant.childData.get(pos).getS_classdivId()
					.toString();
			StringRequest strReq = new StringRequest(Request.Method.GET,
					AppConfig.BASE_URL + "classgallerylists/" + classDivId,
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							Log.d(TAG, "Login Response: " + response.toString());
							hideDialog();

							try {

								JSONObject json = new JSONObject(response);
								JSONObject json1 = json
										.getJSONObject("response");
								JSONArray calssdetails = json1
										.getJSONArray("schoolgallery");

								for (int i = 0; i < calssdetails.length(); i++) {

									JSONObject c = calssdetails
											.getJSONObject(i);
									String galleryName = c
											.getString("galleryname");
									String ImagePath = c.getString("photo");
									if (s.add(galleryName)) {
										Constant.ImagePath.add(ImagePath);
									}

								}
								GalleryName.addAll(s);
								if (GalleryName.size() > 0) {
									adapter = new GalleryGridViewAdapter(
											Parent_GalleryList.this,
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

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}

}

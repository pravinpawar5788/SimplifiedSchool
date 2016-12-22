package com.simplifiedschooling.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.simplifiedschooling.app.util.RondedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class ProfileDetails extends ActionBarActivity {
	private static final String TAG = ProfileDetails.class.getSimpleName();

	private String User_id, Role_id, id;
	private ProgressDialog pDialog;
	String Username, mUsername, jsonStr = null;
	TextView usernameTxt, tname, filename;
	String tFirstame, tLastname, taddress, tphone, tmobile, temail,tusername,tpassword,
			tqualification, thobbies, tteaching_sub, uPhotopath = null;
	EditText tMobile, tHobbies, tQualification, tEmail, tLname, tFname,tUsername,tPassword,
			tAddress, tPhone;
	Uri currImageURI;
	Button chooseBtn,modifyBtn,cancelBtn;
	String realpath, path;
	final Context context = this;
	RondedImageView iv;
	ArrayList<String> teachingSub = new ArrayList<String>();
	private static final int REQUEST_CODE = 6384;
	SharedPreferences userinfo;
	String ftp_url = null,SchoolFolder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				Drawable.createFromPath(this.getExternalFilesDir(null)
						.getAbsolutePath() + "/" + "innerpage_top.png"));

		/*
		 * InputStream ims; try { ims = getAssets().open("innerpage_top.png");
		 * getSupportActionBar
		 * ().setBackgroundDrawable(Drawable.createFromStream(ims, null)); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		setContentView(R.layout.update_profile);
		userinfo = getSharedPreferences("User", MODE_PRIVATE);
		ftp_url = userinfo.getString("schoolftp", "");
		SchoolFolder =userinfo.getString("schoolfolder", "");
		User_id = getIntent().getExtras().getString("userid");
		id = getIntent().getExtras().getString("id");
		filename = (TextView) findViewById(R.id.filename);
		tname = (TextView) findViewById(R.id.tname);
		tFname = (EditText) findViewById(R.id.tFname);
		tLname = (EditText) findViewById(R.id.tlname);
		tAddress = (EditText) findViewById(R.id.tAddress);
		tMobile = (EditText) findViewById(R.id.tMobile);
		tUsername = (EditText) findViewById(R.id.tusername);
		tPassword = (EditText) findViewById(R.id.tpassword);
		tEmail = (EditText) findViewById(R.id.tEmail);
		tQualification = (EditText) findViewById(R.id.tQualification);
		tHobbies = (EditText) findViewById(R.id.tHobbies);
		iv = (RondedImageView) findViewById(R.id.imageuser);
		chooseBtn = (Button) findViewById(R.id.btnChoose);
		chooseBtn.setOnClickListener(new chooseBtnListener());
		modifyBtn = (Button) findViewById(R.id.btnModify);
		modifyBtn.setOnClickListener(new modifyBtnListener());
		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		getTeacherDetails();

	}

	private class modifyBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String urlServer = AppConfig.CLIENT_URL
					+"../"+ "test.php";
			//String urlServer = AppConfig.BASE_URL + "editteacherprofilemobs";
			// List<RequestParams> params1 = new ArrayList<RequestParams>(11);
			RequestParams params1 = new RequestParams();

			if (path == null) {
				String image_Name[] = uPhotopath.split("_");
				int len = image_Name.length;
				String image_Name1 = new String();
				for (int z = 1; z < len - 1; z++)
					image_Name1 += image_Name[z] + "_";
				image_Name1 += image_Name[len - 1];
				params1.put("image", image_Name1);
				/*
				 * Toast.makeText(getBaseContext(), image_Name1 ,
				 * Toast.LENGTH_LONG).show();
				 */
			} else {
				params1.put("image", path);
				/*
				 * Toast.makeText(getBaseContext(), "set"+path,
				 * Toast.LENGTH_LONG).show();
				 */
			}
			//params1.put("Username", Username);
			//params1.put("textFieldFirstName", tFname.getText().toString().trim());
			//params1.put("textFieldLastName", tLname.getText().toString().trim());
			params1.put("address", tAddress.getText().toString()
					.trim());
			params1.put("password", tPassword.getText().toString().trim());
			params1.put("mobile", tMobile.getText().toString().trim());
			params1.put("email", tEmail.getText().toString().trim());
			params1.put("education", tQualification.getText()
					.toString().trim());
			params1.put("hobbies", tHobbies.getText().toString().trim());
			params1.put("id", User_id);
			params1.put("droot", "editteacherprofilemobs");
			params1.put("schoolfolder", SchoolFolder);
			params1.put("imagepath", realpath);
			if (filename.getText().toString().trim()
					.equals("No file chosen")
					|| filename.getText().toString().trim().equals("")) {
			}else {
				try {
					params1.put("uploadedfile", new File(realpath));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// here write your parameter name and its value
			/*try {
				if (realpath == null) {
					//params1.put("imagepath", new File(realpath));
				} else {
					params1.put("imagepath", new File(realpath));
					//params1.put("imagepath", new File(uPhotopath));
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(urlServer, params1, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
					Log.e("from response", arg0);
					if (pDialog.isShowing())
						pDialog.dismiss();


					Toast.makeText(getBaseContext(), "Update Successfully", Toast.LENGTH_LONG).show();

					

				}

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					pDialog = new ProgressDialog(ProfileDetails.this);
					pDialog.setMessage("Please wait...");
					pDialog.setCancelable(false);
					pDialog.show();

				}
			});

			// subjectListDailog.setVisibility(View.GONE);

		}

	}

	private void getTeacherDetails() {
		// TODO Auto-generated method stub

		// Tag used to cancel the request
		String tag_string_req = "req_login";

		pDialog.setMessage("Please wait ...");
		showDialog();

		StringRequest strReq = new StringRequest(Request.Method.GET,
				AppConfig.BASE_URL + "viewprofilemembers/" + User_id,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Login Response: " + response.toString());
						hideDialog();

						try {

							JSONObject json = new JSONObject(response);
							JSONObject resp = json.getJSONObject("response");
							JSONArray studentdetails = resp
									.getJSONArray("users");

							for (int i = 0; i < studentdetails.length(); i++) {

								JSONObject c = studentdetails.getJSONObject(i);

								tFirstame = c.getString("firstname");

								tLastname = c.getString("lastname");
								taddress = c.getString("address");
								// tphone = c1.getString("phone1");
								tmobile = c.getString("mobile");
								temail = c.getString("email");
								tusername = c.getString("username");
								tpassword = c.getString("password");
								tqualification = c.getString("education");
								thobbies = c.getString("hobbies");

								uPhotopath = c.getString("photo");

								//tname.setText(tFirstame + " " + tLastname);
								tFname.setText(tFirstame);
								//tLname.setHint(tLastname);
								tAddress.setText(taddress);
								// tPhone.setHint(tphone);
								tMobile.setText(tmobile);
								tEmail.setText(temail);
								tUsername.setText(tusername);
								tPassword.setText(tpassword);
								tQualification.setText(tqualification);
								tHobbies.setText(thobbies);
								if (uPhotopath != null) {





									new DownloadImageTask(iv)
											.execute(AppConfig.CLIENT_URL
													+ "../uploads/"
													+ uPhotopath);


								} else {
									Toast.makeText(getApplicationContext(),
											"Sorry there is no Image",
											Toast.LENGTH_SHORT).show();
								}
								// iv.setImageURI(uPhotopath);

							}
							/*
							 * else if (i == 1) { JSONArray studentnoArray = c
							 * .getJSONArray("teaching_sub");
							 * 
							 * for (int k = 0; k < studentnoArray.length(); k++)
							 * { JSONObject c2 = studentnoArray
							 * .getJSONObject(k); String assign_class = c2
							 * .getString("ClassName"); String subject = c2
							 * .getString("SubjectName"); tteaching_sub =
							 * assign_class + "-" + subject;
							 * teachingSub.add(tteaching_sub); } }
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

	private class chooseBtnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/*Intent intent = new Intent();
			intent.setType("image*//*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"), 1);
*/           Intent galleryIntent = new Intent(Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

			// Start the Intent
			startActivityForResult(
					Intent.createChooser(galleryIntent, "Select Picture"), REQUEST_CODE);
		}

	}

	/*public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			if (requestCode == 1) {

				// currImageURI is the global variable I'm using to hold the
				// content:// URI of the image
				currImageURI = data.getData();
				iv.setImageURI(currImageURI);
				Log.e("get", currImageURI.toString());
				realpath = this.getRealPathFromURI(currImageURI);
				String imagepath[] = realpath.split("/");
				path = imagepath[imagepath.length - 1];
				filename.setText(path);

				Log.e("get", realpath);

				// iv.setImageURI(currImageURI);

			}
		}
	}*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CODE:
				// If the file selection was successful
				if (resultCode == RESULT_OK) {

					if (data != null) {
						// Get the URI of the selected file


						final Uri uri = data.getData();
						iv.setImageURI(uri);
						Log.i(TAG, "Uri = " + uri.toString());
						try {
							// Get the file path from the URI
							realpath = FileUtils.getPath(this, uri);
						/*Toast.makeText(Class_Homework.this,
								"File Selected: " + realpath, Toast.LENGTH_LONG)
								.show();*/
							String imagepath[] = realpath.split("/");

							path = imagepath[imagepath.length - 1];
							filename.setText(path);

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
		String[] proj = { MediaStore.Images.Media.DATA };
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

	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		RondedImageView bmImage;

		public DownloadImageTask(RondedImageView bmImage) {
			this.bmImage = bmImage;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog.show();
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			bmImage.setImageBitmap(result);
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

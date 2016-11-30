package com.simplifiedschooling.app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class Add_Class_Photo extends ActionBarActivity {
	private static final String TAG = Add_Class_Photo.class.getSimpleName();
	private String BASEURL = AppConfig.BASE_URL;
	String Username,mUsername, jsonStr = null, ass_path = null, assignment_path,
			download_path, dateS, entry = null, realpath, path = null,id;
	String response = null;
	private ProgressDialog pDialog;
	TextView usernameTxt, dateText;
	TextView filenametab1, div, filenametab2;
	ListView lv;
	Uri currImageURI;
	EditText glleryEditText;
	ImageButton backBtn;
	int key;
	Button submitTab1, submitTab2;
	Button chooseBtntab1, takephotoBtn1, chooseBtntab2,takephotoBtn2;
	private TabHost myTabHost;
	SharedPreferences userinfo;
	String SchoolFolder,ftp_url;
	Spinner t1classspinner, t2classspinner, t1gallerynamespinner;
	ArrayList<String> calssList = new ArrayList<String>();
	ArrayList<String> calssDivIdList = new ArrayList<String>();
	ArrayList<String> GalleryName = new ArrayList<String>();
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	Set<String> s = new HashSet<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.add_class_photo);
		id = getIntent().getExtras().getString("id");
		userinfo = getSharedPreferences("User", MODE_PRIVATE);
		SchoolFolder =userinfo.getString("schoolfolder", "");
		ftp_url = userinfo.getString("schoolftp", "");
		myTabHost = (TabHost) findViewById(R.id.TabHost01);
		myTabHost.setup();
		TabSpec spec = myTabHost.newTabSpec("Add To Existing Gallery");
		spec.setContent(R.id.tab1);
		spec.setIndicator("Add To Existing Gallery");
		myTabHost.addTab(spec);
		TabSpec spec1 = myTabHost.newTabSpec("Create New Gallery");
		spec1.setContent(R.id.tab2);
		spec1.setIndicator("Create New Gallery");
		myTabHost.addTab(spec1);

		t1classspinner = (Spinner) findViewById(R.id.spinnerClass);
		t1classspinner.setOnItemSelectedListener(new TeacherclassListener());
		t2classspinner = (Spinner) findViewById(R.id.spinnerClasstab2); 
		// t1classspinner.setOnItemSelectedListener(new TeacherclassListener());
		t1gallerynamespinner = (Spinner) findViewById(R.id.spinnerGallery);
		filenametab1 = (TextView) findViewById(R.id.filename);
		filenametab2 = (TextView) findViewById(R.id.filenametab2);
		chooseBtntab1 = (Button) findViewById(R.id.btnChoose);
		chooseBtntab1.setOnClickListener(new ChoosePictureListener(1));
        takephotoBtn1=(Button)findViewById(R.id.btnTakePhoto);
		takephotoBtn1.setOnClickListener(new dispatchTakePictureIntent(3));
		takephotoBtn2=(Button)findViewById(R.id.btnTakePhoto2);
		takephotoBtn2.setOnClickListener(new dispatchTakePictureIntent(4));
		chooseBtntab2 = (Button) findViewById(R.id.btnChoosetab2);
		chooseBtntab2.setOnClickListener(new ChoosePictureListener(2));
		submitTab1 = (Button) findViewById(R.id.btnsubmittab1);
		submitTab1.setOnClickListener(new submitTab1Listener());
		submitTab2 = (Button) findViewById(R.id.btnSubmittab2);
		submitTab2.setOnClickListener(new submitTab2Listener());
		glleryEditText = (EditText) findViewById(R.id.editTextGallerytaab2);
		//new get1classspinnerAttendence().execute();
		// Progress dialog
				pDialog = new ProgressDialog(this);
				pDialog.setCancelable(false);
		GetClassAttendence();

	}

	private class dispatchTakePictureIntent implements OnClickListener {
		int k;

		public dispatchTakePictureIntent(int key) {
			// TODO Auto-generated constructor stub
			k = key;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			 
			currImageURI = getOutputMediaFileUri(1);
		    Log.e("currImageURI",currImageURI.toString());
		    intent.putExtra(MediaStore.EXTRA_OUTPUT, currImageURI);
		 
		    // start the image capture Intent
		    startActivityForResult(intent, k);
			
			
			 /* 	Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
		   
		    
		  if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
		        // Create the File where the photo should go
		        File photoFile = null;
		        try {
		            photoFile = createImageFile();
		        } catch (IOException ex) {
		            // Error occurred while creating the File
		            ex.printStackTrace();
		        }
		        // Continue only if the File was successfully created
		        if (photoFile != null) {
		        	currImageURI = FileProvider.getUriForFile(Add_School_Photo.this,
                            "com.simplifiedschooling.app.fileprovider",
                            photoFile);
		        	Log.e("currImageURI", currImageURI.toString());
		           
		            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currImageURI);
		            startActivityForResult(takePictureIntent, k);
		        }
		    }
		    */
			
		}

	}
	
	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	  return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	@SuppressLint("SimpleDateFormat")
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new   File(Environment.getExternalStoragePublicDirectory(
	          Environment.DIRECTORY_PICTURES), "my_images");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	    "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}

	
	private void GetClassAttendence() {
		// TODO Auto-generated method stub
		String tag_string_req = "req_login";

		pDialog.setMessage("Please wait ...");
		showDialog();

		StringRequest strReq = new StringRequest(Request.Method.GET,
				AppConfig.BASE_URL + "classesofteachers/"+id,
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
										Add_Class_Photo.this, android.R.layout.simple_spinner_item,
										calssList);
								t1classspinner.setAdapter(classlistadapter);
								t2classspinner.setAdapter(classlistadapter);
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

	private class submitTab1Listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// new submitTab1Task().execute();
			if (filenametab1.getText().toString().trim()
					.equals("No file chosen")) {
				Toast.makeText(getBaseContext(),
						"Please select image to upload!", Toast.LENGTH_LONG)
						.show();
			} else {
				String urlServer = BASEURL
						+ "addclassphotoactmobs";
				int pos = t1classspinner.getSelectedItemPosition();
				int gallerypos = t1gallerynamespinner.getSelectedItemPosition();

				String classdivId = calssDivIdList.get(pos);
				String galleryName = GalleryName.get(gallerypos);
				// Toast.makeText(getBaseContext(), ""+gallerypos+galleryName,
				// Toast.LENGTH_LONG).show();
				RequestParams params1 = new RequestParams();

				params1.put("image", path);
				params1.put("droot", ftp_url);
				params1.put("schoolfolder", SchoolFolder);
				params1.put("gname", galleryName.toString());
				params1.put("classid", classdivId.toString());
				// here write your parameter name and its value
				try {
					params1.put("uploadedfile", new File(realpath));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				AsyncHttpClient client = new AsyncHttpClient();
				client.post(urlServer, params1, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						Log.v("from response", arg0);

						if (pDialog.isShowing())
							pDialog.dismiss();
						filenametab1.setText("No file chosen");
						Toast.makeText(getBaseContext(), arg0,
								Toast.LENGTH_LONG).show();

					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						pDialog = new ProgressDialog(Add_Class_Photo.this);
						pDialog.setMessage("Please wait...");
						pDialog.setCancelable(false);
						pDialog.show();

					}

				});

			}
		}

	}

	private class submitTab2Listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// new submitTab1Task().execute();
			if (filenametab2.getText().toString().trim()
					.equals("No file chosen")) {
				Toast.makeText(getBaseContext(),
						"Please select image to upload!", Toast.LENGTH_LONG)
						.show();
			} else if (glleryEditText.getText().toString().trim().equals("")) {
				Toast.makeText(getBaseContext(),
						"Please enter the Gallery Name!", Toast.LENGTH_LONG)
						.show();
			} else

			{
				String urlServer = BASEURL + "addclassphotoactmobs";
				int pos = t2classspinner.getSelectedItemPosition();

				String classdivId = calssDivIdList.get(pos);

				RequestParams params1 = new RequestParams();
				params1.put("image", path);
				params1.put("droot", ftp_url);
				params1.put("schoolfolder", SchoolFolder);
				params1.put("gname", glleryEditText.getText().toString()
						.trim());
				params1.put("classid", classdivId.toString());
				// here write your parameter name and its value
				try {
					params1.put("uploadedfile", new File(realpath));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				AsyncHttpClient client = new AsyncHttpClient();
				client.post(urlServer, params1, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
						Log.v("from response", arg0);
						if (pDialog.isShowing())
							pDialog.dismiss();
						filenametab2.setText("No file chosen");
						glleryEditText.setText("");
						Toast.makeText(getBaseContext(), arg0,
								Toast.LENGTH_LONG).show();

					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						pDialog = new ProgressDialog(Add_Class_Photo.this);
						pDialog.setMessage("Please wait...");
						pDialog.setCancelable(false);
						pDialog.show();

					}
				});
			}
		}

	}

	// ////////////////////////////////////////////////////////
	private class ChoosePictureListener implements OnClickListener {
		int k;

		public ChoosePictureListener(int key) {
			// TODO Auto-generated constructor stub
			k = key;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			/*Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"), k);*/
			
			Intent galleryIntent = new Intent(Intent.ACTION_PICK,
			        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

			// Start the Intent
			startActivityForResult(
					Intent.createChooser(galleryIntent, "Select Picture"), k);
		}


	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == 3 ||requestCode == 4)
		{
			/*currImageURI = data.getData();
			Log.e("get", currImageURI.toString());
			realpath = this.getRealPathFromURI(currImageURI);*/
			realpath = currImageURI.getPath();
			String imagepath[] = realpath.split("/");
			path = imagepath[imagepath.length - 1];
			if (requestCode == 3) {
				filenametab1.setText(path);

			} else if (requestCode == 4) {
				filenametab2.setText(path);
			}

		}
		else if (resultCode == RESULT_OK) {

			// currImageURI is the global variable I'm using to hold the
			// content:// URI of the image
			currImageURI = data.getData();
			Log.e("get", currImageURI.toString());
			realpath = this.getRealPathFromURI(currImageURI);
			String imagepath[] = realpath.split("/");
			path = imagepath[imagepath.length - 1];
			if (requestCode == 1) {
				filenametab1.setText(path);

			} else if (requestCode == 2) {
				filenametab2.setText(path);
			}

			Log.e("get", realpath);
			// iv.setImageURI(currImageURI);

		}
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

	private class TeacherclassListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

			//new getGalleyNameTask().execute();
			GetGalleyNameTask();
		}

		private void GetGalleyNameTask() {
			// TODO Auto-generated method stub
			String tag_string_req = "req_login";
			GalleryName.clear();
			s.clear();
			pDialog.setMessage("Please wait ...");
			showDialog();
			int pos =t1classspinner.getSelectedItemPosition(); 
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
									s.add(galleryName);
									

								}
								GalleryName.addAll(s);
								if (GalleryName.size() > 0) {
									t1gallerynamespinner.setAdapter(new ArrayAdapter<String>(Add_Class_Photo.this,
											android.R.layout.simple_list_item_1, GalleryName));
									} else {
										Toast.makeText(getBaseContext(), "There is no Gallery!",
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

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	}

	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
 
        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", currImageURI);
    }
	@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
 
        // get the file url
        currImageURI = savedInstanceState.getParcelable("file_uri");
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
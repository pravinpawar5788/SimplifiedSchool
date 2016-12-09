package com.simplifiedschooling.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;
import com.simplifiedschooling.app.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParentSchool_GalleryView extends ActionBarActivity {
	private static final String TAG = ParentSchool_GalleryView.class.getSimpleName();
	private String BASEURL = AppConfig.BASE_URL;
	String Username, jsonStr = null;
	private ProgressDialog pDialog;
	TextView usernameTxt;
	ListView lv;
	ImageButton backBtn;
	String galleryName, image_Name = null, classDivId = null;
	GridView gallery;
	ImageView i;
	Bitmap bimage = null;
	ImageView selectedImage;
	//DisplayImageOptions options;
	//protected ImageLoader imageLoader = ImageLoader.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				Drawable.createFromPath(this.getExternalCacheDir()
						.getAbsolutePath() + "/" + "innerpage_top.png"));
		setContentView(R.layout.ac_image_gallery);
		Bundle b = getIntent().getExtras();
		//Username = b.getString("Username");
		galleryName = b.getString("GalleryName");
		
		
		/*options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
		.cacheOnDisk(true).considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565).build();*/
		gallery = (GridView) findViewById(R.id.gallery);
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				startImagePagerActivity(position);
			}
		});
		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		//new getGalleryImageTask().execute();
		GetGalleryImageTask();
	}

	private void GetGalleryImageTask() {
		// TODO Auto-generated method stub
		String tag_string_req = "req_login";

		pDialog.setMessage("Please wait ...");
		showDialog();

		StringRequest strReq = new StringRequest(Request.Method.GET,
				BASEURL + "imagelistmobs/"+galleryName.replaceAll(" ","%20" ),
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Login Response: " + response.toString());
						hideDialog();

						try {

							JSONObject json = new JSONObject(response);
							JSONObject json1 = json.getJSONObject("response");
							JSONArray calssdetails = json1
									.getJSONArray("imglist");

							for (int i = 0; i < calssdetails.length(); i++) {

								JSONObject c = calssdetails.getJSONObject(i);
								String galleryName = c.getString("photo");

								Constant.ImagePath.add(galleryName);
								if(Constant.ImagePath.size()>0){
								gallery.setAdapter(new ImageGalleryAdapter());
								}else{
									Toast.makeText(getApplicationContext(), "There is no images in gallery!", Toast.LENGTH_SHORT).show();
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

	private void startImagePagerActivity(int position) {
		Intent intent = new Intent(this, ParentSchool_ImagePagerActivity.class);

		intent.putExtra("position", position);
		startActivity(intent);
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Constant.ImagePath.clear();
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Constant.ImagePath.clear();
		finish();
	}
	private class ImageGalleryAdapter extends BaseAdapter {
		ImageLoader imageLoader = AppController.getInstance().getImageLoader();
		@Override
		public int getCount() {
			return Constant.ImagePath.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//ImageView imageView = (ImageView) convertView;
			if (convertView == null) {
				convertView = (ImageView) getLayoutInflater().inflate(
						R.layout.item_gallery_image, parent, false);
			}
			// imageLoader.displayImage(imageUrls[position], imageView,
			// options);
			
			if (imageLoader == null)
				imageLoader = AppController.getInstance().getImageLoader();
			NetworkImageView thumbNail = (NetworkImageView) convertView
					.findViewById(R.id.image);
			thumbNail.setImageUrl(AppConfig.CLIENT_URL+"../"+"gallery/"+Constant.ImagePath.get(position), imageLoader);
			/*imageLoader.displayImage(
					BASEURL +"../../" +Constant.ImagePath.get(position), imageView,
					options);*/
			//http://localhost/school/web/bundles/ijemswebservice/assets/img/gallery/Image01.jpg
			return convertView;
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

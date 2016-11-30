package com.simplifiedschooling.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.simplifiedschooling.app.helper.SingleMediaScanner;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;
import com.simplifiedschooling.app.util.Constant;

public class ImagePagerActivity extends Activity {
	private static final String TAG = ImagePagerActivity.class.getSimpleName();
	private static final String STATE_POSITION = "STATE_POSITION";
	private String BASEURL = AppConfig.BASE_URL;
	private ProgressDialog pDialog;
	//DisplayImageOptions options;
	String download_path = null, assment_path = null;
	int pagerPosition;
	ViewPager pager;
	Context mContext = null;
	//protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_image_pager);
		mContext = ImagePagerActivity.this;
		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
		// Progress dialog
				pDialog = new ProgressDialog(this);
				pDialog.setCancelable(false);
		pagerPosition = bundle.getInt("position", 0);

		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		/*options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();*/

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setLongClickable(true);

		/*
		 * ImageView download = (ImageView) findViewById(R.id.imageButton1);
		 * 
		 * download.setOnTouchListener(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) { // TODO
		 * Auto-generated method stub
		 * 
		 * download_path = BASEURL + "../../" + ((String)
		 * Constant.ImagePath.get(pagerPosition)) .replaceAll("\\\\", "/");
		 * download_path = download_path.replaceAll(" ", "%20"); Log.e("Error",
		 * "Path" + download_path); new DownloadTask(ImagePagerActivity.this)
		 * .execute(download_path);
		 * 
		 * return false; } });
		 *//*
			 * OnClickListener() {
			 * 
			 * 
			 * @Override public void onClick(DialogInterface arg0, int arg1) {
			 * // TODO Auto-generated method stub //
			 * //////////////////////////////////////////////////////////////
			 * 
			 * AlertDialog.Builder builder = new AlertDialog.Builder(
			 * ImagePagerActivity.this);
			 * 
			 * builder.setMessage("Do you want Save this image?")
			 * .setCancelable(true) .setNegativeButton("No", new
			 * OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * { // TODO Auto-generated method stub dialog.dismiss(); } })
			 * .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			 * public void onClick(DialogInterface dialog, int id) {
			 * 
			 * }
			 * 
			 * });
			 * 
			 * builder.show();
			 * 
			 * // //////////////////////////////////////////////////////////////
			 * 
			 * } });
			 */
		      pager.setAdapter(new ImagePagerAdapter(Constant.ImagePath));
		      pager.setCurrentItem(pagerPosition);
	}

	// ////////////////////////////////////////////////////////////////////////////

	private class downloadListener implements OnLongClickListener {
		int pos;

		public downloadListener(int position) {
			// TODO Auto-generated constructor stub
			this.pos = position;
		}

		/*
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub // Toast.makeText(getBaseContext(),
		 * "There is no file to display", // Toast.LENGTH_LONG).show();
		 * AlertDialog.Builder builder = new AlertDialog.Builder(
		 * ImagePagerActivity.this);
		 * 
		 * builder.setMessage("Do you want Save this image?")
		 * .setCancelable(true) .setPositiveButton("Yes", new
		 * DialogInterface.OnClickListener() { public void
		 * onClick(DialogInterface dialog, int id) { download_path = BASEURL +
		 * "../../" + ((String) Constant.ImagePath .get(pos)).replaceAll(
		 * "\\\\", "/"); download_path = download_path.replaceAll( " ", "%20");
		 * Log.e("Error", "Path" + download_path); new
		 * DownloadTask(ImagePagerActivity.this) .execute(download_path);
		 * 
		 * }
		 * 
		 * });
		 * 
		 * builder.show();
		 * 
		 * // //////////////////////////////////////////////////////////////
		 * 
		 * }
		 */
		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub

			AlertDialog.Builder builder = new AlertDialog.Builder(
					ImagePagerActivity.this);

			builder.setMessage("Do you want Save this image?")
					.setCancelable(true)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									download_path = AppConfig.CLIENT_URL+"../"+"gallery/"
											+  Constant.ImagePath.get(pos);
									/*download_path = download_path.replaceAll(
											" ", "%20");*/
									Log.e("Error", "Path" + download_path);
									new DownloadTask(ImagePagerActivity.this)
											.execute(download_path);
									//DownloadTask(download_path);
								}

							});

			builder.show();

			// //////////////////////////////////////////////////////////////

			return true;

		}
	}
		/*protected void DownloadTask(String download_path) {
			// TODO Auto-generated method stub
			String tag_string_req = "req_login";
			Context context;
			
			File outputFile = null;
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

								JSONObject json = new JSONObject(jsonStr);
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

*/	

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
			pDialog = new ProgressDialog(ImagePagerActivity.this);
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

				assment_path = path[path.length - 1];
				String path1[] = assment_path.split("_");
				assment_path = path1[path1.length - 1];
				String path2[] = assment_path.split("_");
				assment_path = path2[path2.length - 1];
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

				File wallpaperDirectory = new File("/sdcard/photos/");
				wallpaperDirectory.mkdirs();
				outputFile = new File(wallpaperDirectory, assment_path);
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

			pDialog.dismiss();

			if (result != null)
				// Toast.makeText(context,"Download error: ",
				// Toast.LENGTH_LONG).show();
				Log.e("Error", BASEURL + "../../../" + download_path + result);
			else
				Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT)
						.show();
			new SingleMediaScanner(ImagePagerActivity.this, outputFile);

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
			 */}

	}

	// ///////////////////////////////////////////////////////////////////////////////

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {
		ImageLoader imageLoader = AppController.getInstance().getImageLoader();
		private ArrayList<String> images;
		private LayoutInflater inflater;

		ImagePagerAdapter(ArrayList<String> imagePath) {
			this.images = imagePath;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image,
					view, false);
			assert imageLayout != null;
			/*ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);*/
			

			if (imageLoader == null)
				imageLoader = AppController.getInstance().getImageLoader();
			NetworkImageView imageView = (NetworkImageView) imageLayout
					.findViewById(R.id.image);
			imageView.setImageUrl(AppConfig.CLIENT_URL+"../"+"gallery/"+images.get(position), imageLoader);
			
			/*imageLoader.displayImage(BASEURL + "../../" + images.get(position),
					imageView, options, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "Input/Output error";
								break;
							case DECODING_ERROR:
								message = "Image can't be decoded";
								break;
							case NETWORK_DENIED:
								message = "Downloads are denied";
								break;
							case OUT_OF_MEMORY:
								message = "Out Of Memory error";
								break;
							case UNKNOWN:
								message = "Unknown error";
								break;
							}
							Toast.makeText(ImagePagerActivity.this, message,
									Toast.LENGTH_SHORT).show();

							spinner.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);
						}
					});
*/			imageView.setOnLongClickListener(new downloadListener(position));
			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
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

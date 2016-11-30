package com.simplifiedschooling.app;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.simplifiedschooling.app.util.AppConfig;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Parent_Profile extends AppCompatActivity{
    WebView webView;
    private static final String TAG = Parent_Profile.class.getSimpleName();
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR=1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(Build.VERSION.SDK_INT >= 21){
            Uri[] results = null;
            //Check if response is positive
            if(resultCode== Activity.RESULT_OK){
                if(requestCode == FCR){
                    if(null == mUMA){
                        return;
                    }
                    if(intent == null){
                        //Capture Photo if no image available
                        if(mCM != null){
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    }else{
                        String dataString = intent.getDataString();
                        if(dataString != null){
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        }else{
            if(requestCode == FCR){
                if(null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        String id = getIntent().getExtras().getString("userid");
        webView = (WebView) findViewById(R.id.dashBoard);
        assert webView != null;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);

        if(Build.VERSION.SDK_INT >= 21){
            webSettings.setMixedContentMode(0);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT >= 19){
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT >=11 && Build.VERSION.SDK_INT < 19){
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.setWebViewClient(new Callback());
        //webView.loadUrl(AppConfig.CLIENT_URL + "viewprofilemob/" + id);
        startWebView(AppConfig.CLIENT_URL + "viewprofilemob/" + id);
        webView.setWebChromeClient(new WebChromeClient(){
            //For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg){
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                Parent_Profile.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FCR);
            }
            // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
            public void openFileChooser(ValueCallback uploadMsg, String acceptType){
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                Parent_Profile.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FCR);
            }
            //For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                Parent_Profile.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), Parent_Profile.FCR);
            }
            //For Android 5.0+
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    WebChromeClient.FileChooserParams fileChooserParams){
                if(mUMA != null){
                    mUMA.onReceiveValue(null);
                }
                mUMA = filePathCallback;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePictureIntent.resolveActivity(Parent_Profile.this.getPackageManager()) != null){
                    File photoFile = null;
                    try{
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCM);
                    }catch(IOException ex){
                        Log.e(TAG, "Image file creation failed", ex);
                    }
                    if(photoFile != null){
                        mCM = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    }else{
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");
                Intent[] intentArray;
                if(takePictureIntent != null){
                    intentArray = new Intent[]{takePictureIntent};
                }else{
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, FCR);
                return true;
            }
        });
    }
    public class Callback extends WebViewClient{
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
    }
    // Create an image file
    private File createImageFile() throws IOException{
        @SuppressLint("SimpleDateFormat") 
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
    }
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode){
                case KeyEvent.KEYCODE_BACK:
                    if(webView.canGoBack()){
                        webView.goBack();
                    }else{
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }
    
    
    private void startWebView(String url) {

		// Create new webview Client to show progress dialog
		// When opening a url or click on link

    	webView.setWebViewClient(new WebViewClient() {
			ProgressDialog progressDialog;

			// If you will not use this method url links are opeen in new brower
			// not in webview
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			// Show loader on url load
			public void onLoadResource(WebView view, String url) {
				if (progressDialog == null) {
					// in standard case YourActivity.this
					progressDialog = new ProgressDialog(Parent_Profile.this);
					progressDialog.setMessage("Loading...");
					progressDialog.show();
				}
			}

			public void onPageFinished(WebView view, String url) {
				try {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
						//progressDialog = null;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

		});

		// Javascript inabled on webview
		webView.getSettings().setJavaScriptEnabled(true);

		webView.loadUrl(url);

	}

	@Override
	// Detect when the back button is pressed
	public void onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			// Let the system handle the back button
			super.onBackPressed();
		}
	}

}





/*import java.io.File;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;



import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AdvancedWebView;
public class Parent_Profile extends ActionBarActivity implements AdvancedWebView.Listener{

	AdvancedWebView web;

	private static final int FILECHOOSER_RESULTCODE = 2888;
	private ValueCallback<Uri> mUploadMessage;
	private Uri mCapturedImageURI = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.advance_webview);
		String id = getIntent().getExtras().getString("userid");

		web = (AdvancedWebView) findViewById(R.id.webview);
		web.setListener(this, this);
		
		web.setWebViewClient(new WebViewClient());
		web.setWebChromeClient(new WebChromeClient() {

			// openFileChooser for Android 3.0+
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType) {

				// Update message
				mUploadMessage = uploadMsg;

				try {

					// Create AndroidExampleFolder at sdcard

					File imageStorageDir = new File(
							Environment
									.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
							"AndroidExampleFolder");

					if (!imageStorageDir.exists()) {
						// Create AndroidExampleFolder at sdcard
						imageStorageDir.mkdirs();
					}

					// Create camera captured image file path and name
					File file = new File(imageStorageDir + File.separator
							+ "IMG_"
							+ String.valueOf(System.currentTimeMillis())
							+ ".jpg");

					mCapturedImageURI = Uri.fromFile(file);

					// Camera capture image intent
					final Intent captureIntent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

					captureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							mCapturedImageURI);

					Intent i = new Intent(Intent.ACTION_GET_CONTENT);
					i.addCategory(Intent.CATEGORY_OPENABLE);
					i.setType("image/*");

					// Create file chooser intent
					Intent chooserIntent = Intent.createChooser(i,
							"Image Chooser");

					// Set camera intent to file chooser
					chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
							new Parcelable[] { captureIntent });

					// On select image call onActivityResult method of activity
					startActivityForResult(chooserIntent,
							FILECHOOSER_RESULTCODE);

				} catch (Exception e) {
					Toast.makeText(getBaseContext(), "Exception:" + e,
							Toast.LENGTH_LONG).show();
				}

			}

			// openFileChooser for Android < 3.0
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				openFileChooser(uploadMsg, "");
			}

			// openFileChooser for other Android versions
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType, String capture) {

				openFileChooser(uploadMsg, acceptType);
			}

			// The webPage has 2 filechoosers and will send a
			// console message informing what action to perform,
			// taking a photo or updating the file

			public boolean onConsoleMessage(ConsoleMessage cm) {

				onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
				return true;
			}

			public void onConsoleMessage(String message, int lineNumber,
					String sourceID) {
				// Log.d("androidruntime",
				// "Show console messages, Used for debugging: " + message);

			}
		}); // End setWebChromeClient

		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setAllowFileAccess(true);
		web.getSettings().setPluginState(PluginState.ON);
		web.getSettings().setDomStorageEnabled(true);
		// web.loadUrl(AppConfig.CLIENT_URL+"schoolcalendarmob");

		startWebView(AppConfig.CLIENT_URL + "viewprofilemob/" + id);
		}
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		if (requestCode == FILECHOOSER_RESULTCODE) {

			if (null == this.mUploadMessage) {
				return;

			}

			Uri result = null;

			try {
				if (resultCode != RESULT_OK) {

					result = null;

				} else {

					// retrieve from the private variable if the intent is null
					result = intent == null ? mCapturedImageURI : intent
							.getData();
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "activity :" + e,
						Toast.LENGTH_LONG).show();
			}

			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;

		}

	}

	private void startWebView(String url) {

		// Create new webview Client to show progress dialog
		// When opening a url or click on link

		web.setWebViewClient(new WebViewClient() {
			ProgressDialog progressDialog;

			// If you will not use this method url links are opeen in new brower
			// not in webview
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			// Show loader on url load
			public void onLoadResource(WebView view, String url) {
				if (progressDialog == null) {
					// in standard case YourActivity.this
					progressDialog = new ProgressDialog(Parent_Profile.this);
					progressDialog.setMessage("Loading...");
					progressDialog.show();
				}
			}

			public void onPageFinished(WebView view, String url) {
				try {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
						progressDialog = null;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

		});

		// Javascript inabled on webview
		web.getSettings().setJavaScriptEnabled(true);

		web.loadUrl(url);

	}

	@Override
	// Detect when the back button is pressed
	public void onBackPressed() {
		if (web.canGoBack()) {
			web.goBack();
		} else {
			// Let the system handle the back button
			super.onBackPressed();
		}
	}
	@Override
	public void onPageStarted(String url, Bitmap favicon) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPageFinished(String url) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPageError(int errorCode, String description, String failingUrl) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDownloadRequested(String url, String suggestedFilename,
			String mimeType, long contentLength, String contentDisposition,
			String userAgent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onExternalPageRequest(String url) {
		// TODO Auto-generated method stub
		
	}
}*/
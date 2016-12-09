package com.simplifiedschooling.app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.simplifiedschooling.app.util.AppConfig;

@SuppressLint("SetJavaScriptEnabled")
@SuppressWarnings("deprecation")
public class Parent_RequestForLeave extends ActionBarActivity {

	WebView web;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				Drawable.createFromPath(this.getExternalCacheDir()
						.getAbsolutePath() + "/" + "innerpage_top.png"));
		setContentView(R.layout.fragment_home);
		String id = getIntent().getExtras().getString("userid");
		web = (WebView) findViewById(R.id.dashBoard);
		web.setWebViewClient(new WebViewClient());
		web.setWebChromeClient(new WebChromeClient());
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setAllowFileAccess(true);
		web.getSettings().setPluginState(PluginState.ON);
		// web.loadUrl(AppConfig.CLIENT_URL+"schoolcalendarmob");

		startWebView(AppConfig.CLIENT_URL + "requestforleavemob/"+id);

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
					progressDialog = new ProgressDialog(
							Parent_RequestForLeave.this);
					progressDialog.setMessage("Loading...");
					progressDialog.show();
				}
			}

			public void onPageFinished(WebView view, String url) {
				try {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					//	progressDialog = null;
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
}
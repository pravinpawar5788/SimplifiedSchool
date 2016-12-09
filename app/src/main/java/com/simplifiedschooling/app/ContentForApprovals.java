package com.simplifiedschooling.app;

import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.TabHost;

import com.simplifiedschooling.app.util.AppConfig;

public class ContentForApprovals extends ActionBarActivity {

	WebView web,web1,web2,web3;
	private String User_id, Role_id, id;
	private static final String TAG = Parent_Homework.class.getSimpleName();
	private String BASEURL = AppConfig.BASE_URL;
	private ProgressDialog pDialog;
	private TabHost myTabHost;
	ListView homeworkList,weekhomeworkList,ArchivehomeworkList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				Drawable.createFromPath(this.getExternalCacheDir()
						.getAbsolutePath() + "/" + "innerpage_top.png"));
		setContentView(R.layout.contentforapprovals);

		

		web = (WebView) findViewById(R.id.dashBoard);
		web.setWebViewClient(new WebViewClient());
		web.setWebChromeClient(new WebChromeClient());
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setAllowFileAccess(true);
		web.getSettings().setPluginState(WebSettings.PluginState.ON);

		/*web1 = (WebView) findViewById(R.id.dashBoard1);
		web1.setWebViewClient(new WebViewClient());
		web1.setWebChromeClient(new WebChromeClient());
		web1.getSettings().setJavaScriptEnabled(true);
		web1.getSettings().setAllowFileAccess(true);
		web1.getSettings().setPluginState(WebSettings.PluginState.ON);

		web2 = (WebView) findViewById(R.id.dashBoard2);
		web2.setWebViewClient(new WebViewClient());
		web2.setWebChromeClient(new WebChromeClient());
		web2.getSettings().setJavaScriptEnabled(true);
		web2.getSettings().setAllowFileAccess(true);
		web2.getSettings().setPluginState(WebSettings.PluginState.ON);*/
		//web.loadUrl(AppConfig.CLIENT_URL+"schoolcalendarmob");

        startWebView(AppConfig.CLIENT_URL+"embedvideomob");
		startWebView(AppConfig.CLIENT_URL+"streferencelinksmob");
		startWebView(AppConfig.CLIENT_URL+"worksheetsmob");
		User_id = getIntent().getExtras().getString("userid");
		Role_id = getIntent().getExtras().getString("roleid");
		id = getIntent().getExtras().getString("id");
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		myTabHost = (TabHost) findViewById(R.id.TabHost01);
		LocalActivityManager mLocalActivityManager = new LocalActivityManager(this, false);
		mLocalActivityManager.dispatchCreate(savedInstanceState);
		myTabHost.setup(mLocalActivityManager);
		//myTabHost.setup();
		/*TabHost.TabSpec spec = myTabHost.newTabSpec("Youtube Links");
		spec.setContent(R.id.tabTodayshomwork);
		spec.setIndicator("Youtube Links");*/
		//myTabHost.addTab(spec);

		myTabHost.addTab(myTabHost.newTabSpec("Youtube Links").setIndicator("Youtube Links").setContent(
				new Intent(this, EmbedVideo.class)));

		/*TabHost.TabSpec spec1 = myTabHost.newTabSpec("Ref.URL");
		spec1.setContent(R.id.tabWeekhomework);
		spec1.setIndicator("Ref.URL");*/
		//myTabHost.addTab(spec1);
		myTabHost.addTab(myTabHost.newTabSpec("Ref.URL").setIndicator("Ref.URL").setContent(
				new Intent(this, ContentLink.class)));

		/*TabHost.TabSpec spec2 = myTabHost.newTabSpec("Attached Files");
		spec2.setContent(R.id.tabArchivehomework);
		spec2.setIndicator("Attached Files");
		myTabHost.addTab(spec2);*/
		myTabHost.addTab(myTabHost.newTabSpec("Attached Files").setIndicator("Attached Files").setContent(
				new Intent(this, WorkSheets.class)));
		myTabHost.setCurrentTab(0);
		myTabHost.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId() == R.id.tabTodayshomwork) {
					//GetTodaysHomework();
					startWebView(AppConfig.CLIENT_URL+"embedvideomob");

				} else if (v.getId() == R.id.tabWeekhomework) {
					//GetWeekHomework();
					web = (WebView) findViewById(R.id.dashBoard1);
					web.setWebViewClient(new WebViewClient());
					web.setWebChromeClient(new WebChromeClient());
					web.getSettings().setJavaScriptEnabled(true);
					web.getSettings().setAllowFileAccess(true);
					web.getSettings().setPluginState(WebSettings.PluginState.ON);
					startWebView(AppConfig.CLIENT_URL+"streferencelinksmob");

				} else if (v.getId() == R.id.tabArchivehomework) {
					// GetArichiveHomework();
					//GetStudentList();
					web = (WebView) findViewById(R.id.dashBoard2);
					web.setWebViewClient(new WebViewClient());
					web.setWebChromeClient(new WebChromeClient());
					web.getSettings().setJavaScriptEnabled(true);
					web.getSettings().setAllowFileAccess(true);
					web.getSettings().setPluginState(WebSettings.PluginState.ON);
					startWebView(AppConfig.CLIENT_URL+"worksheetsmob");
				}
			}
		});

		homeworkList = (ListView) findViewById(R.id.homeworklist);
		weekhomeworkList = (ListView) findViewById(R.id.homeworklist1);
		ArchivehomeworkList = (ListView) findViewById(R.id.homeworklist2);
		/*studentList = (Spinner) findViewById(R.id.selectStudent);
		weekList = (Spinner) findViewById(R.id.selectWeek);
		weekList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				// TODO Auto-generated method stub


			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
*/


		
	}
	
	private void startWebView(String url) {

		// Create new webview Client to show progress dialog
		// When opening a url or click on link
		//WebView web;
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
					progressDialog = new ProgressDialog(ContentForApprovals.this);
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
		//WebView web=null;
		if (web.canGoBack()) {
			web.goBack();
		} else {
			// Let the system handle the back button
			super.onBackPressed();
		}
	}}
package com.simplifiedschooling.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

public class SplashScreenActivity extends Activity {
	private long ms = 0;
	private long splashDuration = 3000;
	private boolean splashActive = true;
	private boolean paused = false;
	SharedPreferences userinfo;
	Intent intent = new Intent();
	final Context context = this;
	String rolename;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.flashscreen);
		
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainlayout);

		mainLayout.setBackgroundDrawable(Drawable.createFromPath(context
				.getExternalFilesDir(null).getAbsolutePath()
				+ "/"
				+ "background.png"));
		
		
		userinfo = getSharedPreferences("User", MODE_PRIVATE);
		Thread mythread = new Thread() {
			public void run() {
				try {
					while (splashActive && ms < splashDuration) {
						if (!paused)
							ms = ms + 100;
						sleep(100);
					}
				} catch (Exception e) {
				} finally {
					if (userinfo.getBoolean("saveLogin", false)) {

						rolename = userinfo.getString("rolename", "");

						if (rolename.equalsIgnoreCase("Teacher")) {
							intent = new Intent(SplashScreenActivity.this,
									HomeActivity.class);

						} else if (rolename.equalsIgnoreCase("Admin")) {
							intent = new Intent(SplashScreenActivity.this,
									AdminHomeActivity.class);

						} else if (rolename.equalsIgnoreCase("Parentinfo")) {
							intent = new Intent(SplashScreenActivity.this,
									ParentHomeActivity.class);

						} else if (rolename.equalsIgnoreCase("Student")) {
							intent = new Intent(SplashScreenActivity.this,
									StudentHomeActivity.class);

						} else {
							intent = new Intent(SplashScreenActivity.this,
									MainActivity.class);

						}

					} else {
						intent = new Intent(SplashScreenActivity.this,
								MainActivity.class);

					}

					startActivity(intent);
					finish();
				}
			}
		};
		mythread.start();
	}
}
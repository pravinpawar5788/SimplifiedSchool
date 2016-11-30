package com.simplifiedschooling.app;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.simplifiedschooling.app.adapter.CustomGridViewAdapter;
import com.simplifiedschooling.app.adapter.Item;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.NetworkUtilities;

import java.util.ArrayList;
import java.util.Calendar;

public class StudentHomeActivity extends Activity {
	TextView usernameTxt;
	GridView grid;
	ArrayList<Item> gridArray = new ArrayList<Item>();
	CustomGridViewAdapter customGridAdapter;
	Button logoutbtn;
	String Username,mUsername;
	SharedPreferences userinfo;
	private String roleid,user_id,id;
	private  String dBname,schoolFolder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.admin_after_login);

		userinfo = getSharedPreferences("User", MODE_PRIVATE);
		dBname = userinfo.getString("schooldbname", "");
		schoolFolder=userinfo.getString("schoolfolder", "");
		Log.e("dBname", dBname);
		if (AppConfig.BASE_URL == null) {
			AppConfig.CLIENT_URL = null;
			AppConfig.BASE_URL = AppConfig.BASE_URL1 + dBname + "/";
			AppConfig.CLIENT_URL =AppConfig.CLIENT_URL1 + schoolFolder + "/web/app_dev.php/";
		}

		if (savedInstanceState != null) {
	        // Restore value of members from saved state
			//categoryName1.clear();
			
			roleid=savedInstanceState.getString("roleid");
			user_id=savedInstanceState.getString("user_id");
		    id=savedInstanceState.getString("id");
			
	        
	    }else{
	    	
	    	roleid = userinfo.getString("role_id", "");
			user_id = userinfo.getString("user_id", "");
			id = userinfo.getString("id", "");
			
	    	
	    }
		
			Username = userinfo.getString("USERNAME", "");
			mUsername = userinfo.getString("mUSERNAME", "");
		    usernameTxt = (TextView) findViewById(R.id.textUsername);
		    usernameTxt.setText("   Welcome[" + mUsername + "]");

		logoutbtn = (Button) findViewById(R.id.buttonLogout);
		logoutbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Editor edit = userinfo.edit();
				edit.putString("role_id", null);
				edit.putString("user_id", null);
				edit.putString("id", null);
				edit.putString("USERNAME",null);
				edit.putString("PAASSWORD", null);
				edit.putString("mUSERNAME", null);
				edit.putString("rolename", null);
				edit.commit();
				/*Editor edit = userinfo.edit();
				edit.putString("USERNAME", "");
				edit.putString("PAASSWORD", "");

				edit.commit();*/

				Intent i = new Intent(StudentHomeActivity.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		});
		Bitmap profile = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.user_profile);
		Bitmap Assignments = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.assignment1);
		/*Bitmap Communicator = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.communicator);*/
		Bitmap Homework = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.homework);
		Bitmap Announcements = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.announcement);
		Bitmap Bus = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.bus);
		Bitmap Gallery = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.gallery);
		Bitmap Courses = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.courses);
		Bitmap Homework_Help = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.help);
		Bitmap Online_Assessment = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.assessment);

		Bitmap Birthday = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.birthday);

		Bitmap ClassWeeklyPlan = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.plan);
		Bitmap Time_Table = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.time_table);
		Bitmap Mail = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.email);
		Bitmap Library = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.library);
		Bitmap Help = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.help);
		Bitmap Calendar = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.calendar);
		Bitmap VideoLinks = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.video);
		Bitmap ReferenceLinks = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.link);
		Bitmap ExtraStudyContent = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.study_conent);
		gridArray.add(new Item(profile, "My Profile"));
		gridArray.add(new Item(Assignments, "Assignments"));
		gridArray.add(new Item(Homework, "Homework"));
		//gridArray.add(new Item(Communicator, "Communicator"));
		gridArray.add(new Item(Announcements, "Announcements"));
		gridArray.add(new Item(Bus, "Bus Details"));
		gridArray.add(new Item(Gallery, "Gallery"));
		gridArray.add(new Item(Courses, "Courses"));
		/*gridArray.add(new Item(Homework_Help, "Homework Help"));
		gridArray.add(new Item(Online_Assessment, "Online Assessment"));*/
		gridArray.add(new Item(Birthday, "Birthdays"));
		gridArray.add(new Item(ClassWeeklyPlan, "Class Weekly Plan"));
		gridArray.add(new Item(Time_Table, "Time Table"));
		gridArray.add(new Item(Mail, "Mail"));
		gridArray.add(new Item(Library, "Library"));
		gridArray.add(new Item(Help, "Help"));
		gridArray.add(new Item(Calendar, "School Calendar"));
		gridArray.add(new Item(VideoLinks, "Video Links"));
		gridArray.add(new Item(ReferenceLinks, "Reference Links"));
		gridArray.add(new Item(ExtraStudyContent, "Extra Study Content"));
		grid = (GridView) findViewById(R.id.gridView1);
		customGridAdapter = new CustomGridViewAdapter(this, R.layout.row_grid,
				gridArray);
		grid.setAdapter(customGridAdapter);

		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub transaction
				if (NetworkUtilities.isInternet(getBaseContext())) {

				//	new userLoginTask().execute();

				

				Animator anim = AnimatorInflater.loadAnimator(
						StudentHomeActivity.this, R.animator.flip_on_vertical);
				anim.setTarget(arg1);
				anim.start();

				Intent intent = null;

				switch (position) {

				case 0:
					intent = new Intent(getApplicationContext(),
							Student_Profile.class);
					
					break;
				case 1:

					intent = new Intent(getApplicationContext(),
							Student_Assignments.class);
					

					break;
				case 2:
					intent = new Intent(getApplicationContext(), Student_Homework.class);
					
					break;
				/*case 3:
					intent = new Intent(getApplicationContext(),
							Communicator.class);
					intent.putExtra("Username", Username);
					startActivity(intent);
					break;*/
				case 3:
					intent = new Intent(getApplicationContext(),
							Student_Announcement.class);
					
					break;

				

				case 4:
					intent = new Intent(getApplicationContext(),
							Bus_details.class);
					
					break;

				case 5:
					intent = new Intent(getApplicationContext(),Student_GalleryList.class);
					
					break;

				case 6:
					intent = new Intent(getApplicationContext(), Student_Courses.class);
					
					intent.putExtra("type", "Courses");
					
					break;
				/*case 7:
					intent = new Intent(getApplicationContext(), Courses.class);
					
					intent.putExtra("type", "Homework Help");
					
					break;

				case 8:
					intent = new Intent(getApplicationContext(),
							Assessment.class);
					
					break;*/

				case 7:
					intent = new Intent(getApplicationContext(), Student_Birthday.class);
					
					break;
				case 8:
					intent = new Intent(getApplicationContext(),
							Student_ClassWeeklyPlan.class);
					
					break;
				case 9:
					intent = new Intent(getApplicationContext(),
							Student_Time_table.class);
					
					break;
				case 10:
					intent = new Intent(getApplicationContext(),
							Student_Mail.class);
					
					break;
				case 11:
					intent = new Intent(getApplicationContext(),
							Student_Library.class);
					
					break;
				case 12:
					intent = new Intent(getApplicationContext(),
							Student_Help.class);
					
					break;
				case 13:
					intent = new Intent(getApplicationContext(),
							Calendar.class);
					
					break;
					
				case 14:
					intent = new Intent(getApplicationContext(),
							Student_VideoLinks.class);
					
					break;
				case 15:
					intent = new Intent(getApplicationContext(),
							Student_ReferenceLinks.class);
					
					break;
				case 16:
					intent = new Intent(getApplicationContext(),
							Student_ExtraStudyContent.class);
					
					break;
				default:
					break;
				
				}
				intent.putExtra("Username", Username);
				intent.putExtra("mUsername", mUsername);
				startActivity(intent);
				} else {
					Toast.makeText(getBaseContext(), "No network", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

	}

}

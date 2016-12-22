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

public class AdminHomeActivity extends Activity {
	TextView usernameTxt;
	GridView grid;
	ArrayList<Item> gridArray = new ArrayList<Item>();
	CustomGridViewAdapter customGridAdapter;
	Button logoutbtn;
	String Username,mUsername;
	SharedPreferences userinfo1;
	private String roleid,user_id,id;
	String dBname,schoolFolder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.admin_after_login);

		userinfo1 = getSharedPreferences("User", MODE_PRIVATE);
		dBname = userinfo1.getString("schooldbname", "");
		schoolFolder=userinfo1.getString("schoolfolder", "");
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
	    	
	    	roleid = userinfo1.getString("role_id", "");
			user_id = userinfo1.getString("user_id", "");
			id = userinfo1.getString("id", "");
			
	    	
	    }
		
			Username = userinfo1.getString("USERNAME", "");
			mUsername = userinfo1.getString("mUSERNAME", "");
		
		
		usernameTxt = (TextView) findViewById(R.id.textUsername);
		usernameTxt.setText("   Welcome[" + mUsername + "]");

		logoutbtn = (Button) findViewById(R.id.buttonLogout);
		logoutbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Editor edit = userinfo1.edit();
				edit.putString("role_id", null);
				edit.putString("user_id", null);
				edit.putString("id", null);
				edit.putString("USERNAME",null);
				edit.putString("PAASSWORD", null);
				edit.putString("mUSERNAME", null);
				edit.putString("rolename", null);
				
				edit.commit();
				/*Editor edit = userinfo1.edit();
				edit.putString("USERNAME", "");
				edit.putString("PAASSWORD", "");
                
				edit.commit();*/

				Intent i = new Intent(AdminHomeActivity.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		});
		Bitmap General_announcement = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.announcement_group);
		Bitmap Specific_announcement = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.announcement_specific);
		Bitmap Gallery = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.school_gallery);
		/*Bitmap Communicator = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.communicator);*/
		Bitmap School_Bus = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.bus);
		/*Bitmap Locate_Bus = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.bus);*/
		Bitmap Student_Profile = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.student_profile);
		Bitmap Mail = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.email);
		Bitmap Library = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.library);
		Bitmap Add_school_photo = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.add_school_photo);
		Bitmap TaskCalendar = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.task_calendar);
		Bitmap SchoolCalendar = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.calendar);
		gridArray.add(new Item(General_announcement, "General Announcement"));
		gridArray.add(new Item(Specific_announcement, "Specific Announcement"));
		gridArray.add(new Item(Add_school_photo, "Create School Album"));
		gridArray.add(new Item(Gallery, "View Class Album"));
		gridArray.add(new Item(Gallery, "View School Album"));
		//gridArray.add(new Item(Communicator, "Communicator"));
		//gridArray.add(new Item(School_Bus, "School Bus"));
		/*gridArray.add(new Item(Locate_Bus, "Locate Bus"));*/
		gridArray.add(new Item(Student_Profile, "Student Details"));
		//gridArray.add(new Item(Mail, "Mail"));
		//gridArray.add(new Item(Library, "Library"));
		gridArray.add(new Item(TaskCalendar, "Task Calendar"));
		gridArray.add(new Item(SchoolCalendar, "School Calendar"));
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
				Animator anim = AnimatorInflater.loadAnimator(
						AdminHomeActivity.this, R.animator.flip_on_vertical);
				anim.setTarget(arg1);
				anim.start();

				Intent intent = null;

				switch (position) {

				case 0:
					intent = new Intent(getApplicationContext(),
							General_announcement_Admin.class);
										break;
				case 1:

					intent = new Intent(getApplicationContext(),
							Specific_announcement_Admin.class);
					
					break;

				case 2:
					intent = new Intent(getApplicationContext(),
							Add_School_Photo.class);
					
					break;
				/*case 3:
					intent = new Intent(getApplicationContext(),
							Communicator.class);
					intent.putExtra("Username", Username);
					startActivity(intent);
					break;
*/
				case 3:
					intent =new Intent(getApplicationContext(),
							ClassGallery_list.class);
					
					break;

				case 4:
					intent = new Intent(getApplicationContext(),
							GalleryList_Admin.class);
					intent.putExtra("Username", Username);
					startActivity(intent);
					break;
				case 5:
					intent = new Intent(getApplicationContext(),
							AllStudentList_Admin.class);
					
					break;

				/*case 6:
					intent = new Intent(getApplicationContext(),
							Mail_Admin.class);
					
					break;
				case 7:
					intent = new Intent(getApplicationContext(),
							Library_Admin.class);
					
					break;*/
				case 6:
					intent = new Intent(getApplicationContext(),
							Admin_TaskCalendar.class);
					
					break;
				case 7:
					intent = new Intent(getApplicationContext(),
							Admin_SchoolCalendar.class);
					
					break;
				default:
					break;
				}
				
				intent.putExtra("Username", Username);
				intent.putExtra("mUsername", mUsername);
				intent.putExtra("userid", user_id);
				intent.putExtra("roleid", roleid);
				intent.putExtra("id", id);
				startActivity(intent);
			
			} else {
				Toast.makeText(getBaseContext(), "No network",
						Toast.LENGTH_SHORT).show();
			}
				
		  }	
		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

}


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

public class ParentHomeActivity extends Activity {
    TextView usernameTxt;
    GridView grid;
    ArrayList<Item> gridArray = new ArrayList<Item>();
    CustomGridViewAdapter customGridAdapter;
    Button logoutbtn;
    String Username, mUsername;
    SharedPreferences userinfo;
    private String roleid, user_id, id, dBname,schoolFolder;

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
            // categoryName1.clear();

            roleid = savedInstanceState.getString("roleid");
            user_id = savedInstanceState.getString("user_id");
            id = savedInstanceState.getString("id");

        } else {

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
                edit.putString("USERNAME", null);
                edit.putString("PAASSWORD", null);
                edit.putString("mUSERNAME", null);
                edit.putString("rolename", null);

                edit.commit();
                /*
				 * Editor edit = userinfo.edit(); edit.putString("USERNAME",
				 * ""); edit.putString("PAASSWORD", "");
				 * 
				 * edit.commit();
				 */

                Intent i = new Intent(ParentHomeActivity.this,
                        MainActivity.class);
                startActivity(i);
                finish();
            }
        });
		/*
		 * Bitmap profile = BitmapFactory.decodeResource(this.getResources(),
		 * R.drawable.profile);
		 */
        Bitmap Assignments = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.project_work);
		/*
		 * Bitmap Communicator =
		 * BitmapFactory.decodeResource(this.getResources(),
		 * R.drawable.communicator);
		 */
        Bitmap Homework = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.homework);
        Bitmap Classwork = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.classwork);
        Bitmap Announcements = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.announcement);
        Bitmap Bus = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.request);
        Bitmap ClassGallery = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.gallery);
        Bitmap SchoolGallery = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.school_gallery);
        Bitmap Courses = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.courses);
        Bitmap Homework_Help = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.help);
		/*
		 * Bitmap Online_Assessment = BitmapFactory.decodeResource(
		 * this.getResources(), R.drawable.assessment);
		 */
        Bitmap ClassWeeklyPlan = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.plan);
        Bitmap Mail = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.email);
        Bitmap Calendar = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.calendar);
        Bitmap Profile = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.parent_profile);
        Bitmap StudentProfile = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.student_profile);
        Bitmap Authorizetocollect = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.parent_profile);
        Bitmap ReferenceMiultimediaLink = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.multimedia);
        Bitmap Attendance_Summery = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.attendance_summary);
        Bitmap RequestForleave = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.request);
        Bitmap TopicforRevision = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.topic_review);
        Bitmap RefrenceVideo = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.embed_video);
        Bitmap ReferenceLink = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.view_reference_links);
        Bitmap ContentLink = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.view_reference_links);
        Bitmap WeeklyReport = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.weekly_report);
        Bitmap Emergency = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.emergency);
        Bitmap ProgressReport = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.progress);
        gridArray.add(new Item(Profile, "Update Profile"));
        gridArray.add(new Item(Authorizetocollect, "Authorize to collect"));
        gridArray.add(new Item(StudentProfile, "Child Profile"));
        gridArray.add(new Item(Homework, "Homework"));
        gridArray.add(new Item(Classwork, "Classwork"));
        //gridArray.add(new Item(Assignments, "Assignment and Project Work"));
        //gridArray.add(new Item(Attendance_Summery, "Student Attendance"));
        gridArray.add(new Item(Announcements, "Announcements"));
        gridArray.add(new Item(ClassGallery, "Class Album"));
        gridArray.add(new Item(SchoolGallery, " School Album"));
        gridArray.add(new Item(RequestForleave, "Request For Leave"));
        //gridArray.add(new Item(Mail, "Mail"));
      /*  gridArray.add(new Item(TopicforRevision, "Topic for Revision"));
       // gridArray.add(new Item(ReferenceMiultimediaLink, "Reference Multimedia Link"));
        gridArray.add(new Item(RefrenceVideo, "Reference Video"));
       // gridArray.add(new Item(ReferenceLink, "Reference Link"));
        gridArray.add(new Item(ContentLink, "Content Link"));
        gridArray.add(new Item(WeeklyReport, "Weekly Report"));*/
        gridArray.add(new Item(Calendar, "School Calendar"));
        gridArray.add(new Item(Emergency, "In Case of Emergency"));
       // gridArray.add(new Item(Bus, "Mode Of Transport"));
       // gridArray.add(new Item(ProgressReport, "Progress Report"));

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
                            ParentHomeActivity.this,
                            R.animator.flip_on_vertical);
                    anim.setTarget(arg1);
                    anim.start();

                    Intent intent = null;

                    switch (position) {

                        case 0:

                            intent = new Intent(getApplicationContext(),
                                    Parent_Profile.class);

                            break;

                        case 1:

                            intent = new Intent(getApplicationContext(),
                                    Authorizetocollect.class);

                            break;

                        case 2:

                            intent = new Intent(getApplicationContext(),
                                    Child_profile.class);

                            break;

                        case 3:
                            intent = new Intent(getApplicationContext(),
                                    Parent_Homework.class);

                            break;
                        case 4:

                            intent = new Intent(getApplicationContext(),
                                    Parent_Classwork.class);

                            break;
                       /* case 5:
                            intent = new Intent(getApplicationContext(),
                                    Assignment_project.class);

                            break;
*/
                      /*  case 5:
                            intent = new Intent(getApplicationContext(),
                                    Student_attendance.class);

                            break;*/
                        case 5:
                            intent = new Intent(getApplicationContext(),
                                    Parent_Announcement.class);

                            break;

                        case 6:
                            intent = new Intent(getApplicationContext(),
                                    Parent_GalleryList.class);

                            break;

                        case 7:
                            intent = new Intent(getApplicationContext(),
                                    Parent_SchoolGalleryList.class);

                            break;

                        case 8:
                            intent = new Intent(getApplicationContext(),
                                    Parent_RequestForLeave.class);

                            break;

                        /*case 10:
                            intent = new Intent(getApplicationContext(),
                                    Parent_Mail.class);

                            break;*/

                        /*case 9:
                            intent = new Intent(getApplicationContext(),
                                    TopicforRevision.class);

                            break;*/
                        /*case 12:
                            intent = new Intent(getApplicationContext(),
                                    ReferenceMediaLink.class);

                            break;
*/
                      /*  case 10:
                            intent = new Intent(getApplicationContext(),
                                    ReferenceVideo.class);

                            break;*/
/*

                        case 14:
                            intent = new Intent(getApplicationContext(),
                                    ReferenceLink.class);

                            break;
*/

                      /*  case 11:
                            intent = new Intent(getApplicationContext(),
                                    ContentLink.class);

                            break;

                        case 12:
                            intent = new Intent(getApplicationContext(),
                                    WeeklyReport.class);

                            break;
*/
                        case 9:
                            intent = new Intent(getApplicationContext(),
                                    ParentSchoolCalendar.class);

                            break;

                        case 10:
                            intent = new Intent(getApplicationContext(),
                                    Incaseofemergancy.class);

                            break;

                       /* case 15:
                            intent = new Intent(getApplicationContext(),
                                    Modeoftransport.class);

                            break;

                        case 16:
                            intent = new Intent(getApplicationContext(),
                                    Progress_report.class);

                            break;
*/
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

}

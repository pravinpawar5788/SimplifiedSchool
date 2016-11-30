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
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.simplifiedschooling.app.adapter.CustomGridViewAdapter;
import com.simplifiedschooling.app.adapter.Item;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.NetworkUtilities;

import java.util.ArrayList;

public class HomeActivity extends Activity {
    TextView usernameTxt;
    GridView grid, grid1;
    ArrayList<Item> gridArray = new ArrayList<Item>();
    ArrayList<Item> gridArraySubjectTeacher = new ArrayList<Item>();
    CustomGridViewAdapter customGridAdapter, customGridAdapter1;
    Button logoutbtn;
    String Username, mUsername;
    private TabHost myTabHost;
    SharedPreferences userinfo;
    private String roleid, user_id, id;
    String dBname,schoolFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_after_login);
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

		
		/*userinfo = getSharedPreferences("User", MODE_PRIVATE);
        if (userinfo.getBoolean("saveLogin", false)) {
			
			Username = userinfo.getString("USERNAME", "");
			mUsername = userinfo.getString("mUSERNAME", "");
		} else {
			Bundle b = getIntent().getExtras();
			Username = b.getString("Username");
			mUsername = b.getString("mUsername");	       
		}*/
        usernameTxt = (TextView) findViewById(R.id.textUsername);
        usernameTxt.setText("   Welcome[" + mUsername + "]");

        logoutbtn = (Button) findViewById(R.id.mbuttonLogout);
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
                /*Editor edit = userinfo.edit();
				edit.putString("USERNAME", "");
				edit.putString("PAASSWORD", "");

				edit.commit();*/
                Intent i = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        myTabHost = (TabHost) findViewById(R.id.TabHost01);
        myTabHost.setup();
        TabSpec spec = myTabHost.newTabSpec("Class Teacher");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Class Teacher");
        myTabHost.addTab(spec);
        TabSpec spec1 = myTabHost.newTabSpec("Subject Teacher");
        spec1.setContent(R.id.tab2);
        spec1.setIndicator("Subject Teacher");
        myTabHost.addTab(spec1);

        Bitmap profile = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.user_profile);
        Bitmap attendence = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.attendence);
        Bitmap attendence_summary = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.att_summary);
        Bitmap Announcements = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.announcement);
        Bitmap add_class_photo = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.addclassphoto);
        Bitmap class_gallery = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.gallery);

        Bitmap class_plan = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.plan);
		/*
		 * Bitmap Communicator =
		 * BitmapFactory.decodeResource(this.getResources(),
		 * R.drawable.communicator);
		 */
        Bitmap Birthday = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.birthday);
        Bitmap progress = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.progress);
        Bitmap view_progress = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.view_progress);
        Bitmap Time_table = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.time_table);
        Bitmap Student_details = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.student_profile);
        Bitmap Mail = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.email);
        Bitmap Library = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.library);
        Bitmap Calendar = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.calendar);
        Bitmap TaskCalendar = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.task_calendar);

        gridArray.add(new Item(profile, "Update Profile"));
        gridArray.add(new Item(attendence, "Class Attendence"));
        gridArray.add(new Item(attendence_summary, "Attendence Summary"));
        gridArray.add(new Item(Announcements, "School Announcements"));
        gridArray.add(new Item(add_class_photo, "Create Class Album"));
        gridArray.add(new Item(class_gallery, "Class Album"));
        gridArray.add(new Item(class_gallery, "School Album"));
        gridArray.add(new Item(class_plan, "Class Lesson Plan Status"));
        // gridArray.add(new Item(Communicator, "Communicator"));
        gridArray.add(new Item(Birthday, "Birthdays This Month"));
        gridArray.add(new Item(progress, "Co-Scoharlist  Mark File"));
        gridArray.add(new Item(view_progress, "View Progress Report"));
        gridArray.add(new Item(Time_table, "Time Table"));
        gridArray.add(new Item(Student_details, "View Student Details"));
        gridArray.add(new Item(Mail, "Mail"));
        gridArray.add(new Item(Library, "Library"));
        gridArray.add(new Item(Calendar, "School Calendar"));
        gridArray.add(new Item(TaskCalendar, "Task Calendar"));

        customGridAdapter = new CustomGridViewAdapter(this, R.layout.row_grid,
                gridArray);
        grid = (GridView) findViewById(R.id.gridView1);
        grid1 = (GridView) findViewById(R.id.gridView2);
        grid.setAdapter(customGridAdapter);

        Bitmap ass_project = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ass_project);
        Bitmap class_homework = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.class_homework);
        Bitmap online_activity = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.online_multimedia);
		/*
		 * Bitmap subteacher_Communicator = BitmapFactory.decodeResource(
		 * this.getResources(), R.drawable.scommunicator);
		 */
        Bitmap cce_result = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.result);
        Bitmap Class_Plan_Status = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.lesson_status1);
        Bitmap Add_class_plan = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.addclassplan);
        Bitmap EmbedVideo = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.video);
        Bitmap AddReferenceLink = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.link);
        Bitmap UploadStudyContent = BitmapFactory.decodeResource(
                this.getResources(), R.drawable.approval_multimedia);

        gridArraySubjectTeacher.add(new Item(Announcements, "Class Announcements"));
        gridArraySubjectTeacher.add(new Item(ass_project,
                "Class Work"));
        gridArraySubjectTeacher.add(new Item(class_homework, "Class Homework"));
        gridArraySubjectTeacher
                .add(new Item(online_activity, "Topic for Revision"));

        gridArraySubjectTeacher.add(new Item(progress, "Scholastic Report"));
        gridArraySubjectTeacher.add(new Item(Class_Plan_Status,
                "Lesson Plan Status"));
        gridArraySubjectTeacher.add(new Item(Add_class_plan,
                "Create Lesson Plan"));
        /*gridArraySubjectTeacher.add(new Item(EmbedVideo,
                "Embed Videos"));
        gridArraySubjectTeacher.add(new Item(AddReferenceLink,
                "Add Reference Link"));*/
        gridArraySubjectTeacher.add(new Item(UploadStudyContent,
                "Content for approvals"));
        /*gridArraySubjectTeacher.add(new Item(class_homework,
                "Today's Homework in other Classes"));*/
        customGridAdapter1 = new CustomGridViewAdapter(this, R.layout.row_grid,
                gridArraySubjectTeacher);
        grid1.setAdapter(customGridAdapter1);

        grid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub transaction
                if (NetworkUtilities.isInternet(getBaseContext())) {

                    Animator anim = AnimatorInflater.loadAnimator(
                            HomeActivity.this, R.animator.flip_on_vertical);
                    anim.setTarget(arg1);
                    anim.start();

                    Intent intent = null;
					/*Fragment fragment = null;
					Bundle args=new Bundle();
					args.putString("userid", user_id);
					args.putString("roleid", roleid);
					args.putString("id", id);*/
                    switch (position) {

                        case 0:
                            intent = new Intent(getApplicationContext(),
                                    ProfileDetails.class);

                            break;

                        case 1:
                            intent = new Intent(getApplicationContext(),
                                    AttendanceFragment.class);

                            break;

                        case 2:

                            intent = new Intent(getApplicationContext(),
                                    AttendanceSummary.class);

                            break;
                        case 3:

                            intent = new Intent(getApplicationContext(),
                                    TeacherAnnouncement.class);

                            break;

                        case 4:

                            intent = new Intent(getApplicationContext(),
                                    Add_Class_Photo.class);

                            break;

                        case 5:
                            intent = new Intent(getApplicationContext(),
                                    TeacherClassGallery.class);

                            break;
                        case 6:
                            intent = new Intent(getApplicationContext(),
                                    GalleryList_Admin.class);

                            break;
                        case 7:
                            intent = new Intent(getApplicationContext(),
                                    ClassLessonPlanStatus.class);

                            break;
/*
					
					 * case 5: intent = new Intent(getApplicationContext(),
					 * AddClassPlan.class); intent.putExtra("Username",
					 * Username); startActivity(intent); break;
					 



					
					 * case 6: intent = new Intent(getApplicationContext(),
					 * Communicator.class); intent.putExtra("Username",
					 * Username); startActivity(intent); break;
				*/
                        case 8:
                            intent = new Intent(getApplicationContext(),
                                    Birthday.class);

                            break;

                        case 9:
                            intent = new Intent(getApplicationContext(),
                                    Progress_report.class);

                            break;

                        case 10:
                            intent = new Intent(getApplicationContext(),
                                    View_Progress.class);

                            break;

                        case 11:
                            intent = new Intent(getApplicationContext(),
                                    TimeTable.class);
                            break;
                        case 12:
                            intent = new Intent(getApplicationContext(),
                                    AllStudentList.class);

                            break;
						/*
					case 12:
						intent = new Intent(getApplicationContext(), Mail.class);

						break;
					case 13:
						intent = new Intent(getApplicationContext(),
								Library.class);

						break;
*/
                        case 15:
                            intent = new Intent(getApplicationContext(), SchoolCalendar.class);
                            break;

                        case 16:
                            intent = new Intent(getApplicationContext(), TaskCalendar.class);
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
					/*fragment.setArguments(args);
					
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction()
							.replace(R.id.frame_container, fragment).commit();*/

                } else {
                    Toast.makeText(getBaseContext(), "No network",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        grid1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub transaction
                if (NetworkUtilities.isInternet(getBaseContext())) {
                    Animator anim = AnimatorInflater.loadAnimator(
                            HomeActivity.this, R.animator.flip_on_vertical);
                    anim.setTarget(arg1);
                    anim.start();

                    Intent intent = null;

                    switch (position) {
                        case 0:
                            intent = new Intent(getApplicationContext(),
                                    Teacher_ClassAnnouncement.class);
                            break;

                        case 1:
                            intent = new Intent(getApplicationContext(),
                                    Teacher_Classwork.class);

                            break;

                        case 2:
                            intent = new Intent(getApplicationContext(),
                                    Class_Homework.class);

                            break;

                        case 3:

                            intent = new Intent(getApplicationContext(),
                                    Online_Activity.class);

                            break;
                        case 4:
                            intent = new Intent(getApplicationContext(),
                                    ScholasticReport.class);

                            break;

                        case 5:
                            intent = new Intent(getApplicationContext(),
                                    ClassLessonPlanStatus.class);

                            break;
                        case 6:
                            intent = new Intent(getApplicationContext(),
                                    CreateLessonPlan.class);

                            break;

                        case 7:
                            intent = new Intent(getApplicationContext(),
                                    ContentForApprovals.class);


                            break;

                        case 8:
                            intent = new Intent(getApplicationContext(),
                                    TodaysHomework.class);


                            break;
                        /*
                        case 9:
                            intent = new Intent(getApplicationContext(),
                                    UploadWorksheets.class);


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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        savedInstanceState.putString("roleid", roleid);
        savedInstanceState.putString("user_id", user_id);
        savedInstanceState.putString("id", id);
        super.onSaveInstanceState(savedInstanceState);
    }
}
	
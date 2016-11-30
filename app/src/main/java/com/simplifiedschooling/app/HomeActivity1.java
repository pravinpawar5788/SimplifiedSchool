package com.simplifiedschooling.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.simplifiedschooling.app.adapter.NavDrawerListAdapter;
import com.simplifiedschooling.app.helper.NavDrawerItem;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity1 extends ActionBarActivity {
	private static final String TAG = HomeActivity.class.getSimpleName();
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ProgressDialog pDialog;
	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	// private TypedArray navMenuIcons;
    String jsonStr=null;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private String roleid,user_id,id;
	ArrayList<String> mLable = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		roleid = getIntent().getExtras().getString("role_id");
		user_id = getIntent().getExtras().getString("user_id");
		id = getIntent().getExtras().getString("id");
		// Progress dialog
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		//new fetchmenuTask().execute();
		 //navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		 
 		//getMeun(roleid);
		
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items

		// nav drawer icons from resources
		// navMenuIcons =
		// getResources().obtainTypedArray(R.array.nav_drawer_icons);
         
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		/*navMenuTitles = new String[mLable.size()];
		for(int i=0;i<navMenuTitles.length;i++) {
			navMenuTitles[i] =mLable.get(i).toString();
		}*/

		/*for(int i=0;i<mLable.size();i++){
		  //navMenuTitles[i] = navDrawerItems.get(i).getTitle();
		  navDrawerItems.add(new NavDrawerItem(mLable.get(i).toString()));
		}*/
		// Find People
		// adding nav drawer items to array
        // Home
       /* navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
        // Find People
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
        // Photos
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
        // Communities, Will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));
        // Pages
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4]));
        // What's hot, We  will add a counter here
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5]));*/
		// Recycle the typed array
		// navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		/*adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);*/

		// enabling action bar app icon and behaving it as toggle button
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/***
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		Bundle args=new Bundle();
		args.putString("userid", user_id);
		args.putString("roleid", roleid);
		args.putString("id", id);
		switch (position) {
		
		case 0:
			//fragment = new HomeFragment();
			break;
		case 3:
		///	fragment = new AttendanceFragment();
			
			fragment.setArguments(args);
			break;	
		case 4:
		//	fragment = new ProfileDetails();
			
			fragment.setArguments(args);
			break;
		case 5:
			//fragment = new AttendanceSummary();
			
			fragment.setArguments(args);
			break;
		case 6:
			//fragment = new TimeTable();
			
			//fragment.setArguments(args);
			break;			
		/*
		 * case 1: fragment = new FindPeopleFragment(); break; case 2: fragment
		 * = new PhotosFragment(); break; case 3: fragment = new
		 * CommunityFragment(); break; case 4: fragment = new PagesFragment();
		 * break; case 5: fragment = new WhatsHotFragment(); break;
		 */
		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			//setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	//////////////////////////////////////////////////
	
	/*
	
	private class fetchmenuTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(HomeActivity1.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			
			//getMenu(roleid);
			
			
			
			// TODO Auto-generated method stub
		*//*	Constant.categoryData.clear();

			List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		     params.add(new BasicNameValuePair("ClassDivId",
			// u_classDivId.toString()));
			// params.add(new BasicNameValuePair("ClassID", id.toString()));
      *//*
			List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		     params.add(new BasicNameValuePair("id",roleid));
			ServiceHandler sh = new ServiceHandler();

			jsonStr = sh.makeServiceCall(AppConfig.BASE_URL + "menus/" + roleid,
					ServiceHandler.GET, params);

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					
					JSONObject jObj = new JSONObject(jsonStr);
					JSONObject resp = jObj.getJSONObject("response"); 
					
					JSONArray calssdetails = resp.getJSONArray("users");

					for (int i = 0; i < calssdetails.length(); i++) {

						JSONObject c = calssdetails.getJSONObject(i);

						String label = c.getString("label");
						String menu_id = c.getString("menu_id");
						String parent = c.getString("parent");
						
						*//*if( parent == "72"){
							JSONObject d = calssdetails.getJSONObject(i+1);
							mLable.add(label);
							while( d.getString("parent") == menu_id ){
							   	
							}
						}*//*
							
						 mLable.add(label);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");

			}
		
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
			 navMenuTitles = new String[mLable.size()];
			 for(int i=0;i<navMenuTitles.length;i++)
			 {
				navMenuTitles[i] =mLable.get(i).toString();
				
					  //navMenuTitles[i] = navDrawerItems.get(i).getTitle();
			    navDrawerItems.add(new NavDrawerItem(mLable.get(i).toString()));
			    adapter = new NavDrawerListAdapter(getApplicationContext(),
						navDrawerItems);
			   // adapter.notifyAll();
				mDrawerList.setAdapter(adapter);
				
				
				
				
			    
			}
		}

	}*/


	
	
	
	
	
	
	
	
	
	
	//////////////////////////////////////////////////
	/**
	 * function to verify login details in mysql db
	 * */
	private void getMenu(final String roleId) {
		// Tag used to cancel the request

		pDialog.setMessage("Please wait ...");
		showDialog();

		StringRequest strReq = new StringRequest(Request.Method.GET,
				AppConfig.BASE_URL + "menus/" + roleid,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, "Menu Response: " + response.toString());
						hideDialog();

						try {
							JSONObject jObj = new JSONObject(response);
							JSONObject resp = jObj.getJSONObject("response");
							// JSONObject user = resp.getJSONObject("users");
							// String status = resp.getString("status");
							Log.d(TAG, "Login Response: " + resp.toString());
							// Check forerror node in json
							if (!resp.toString().equals("")) {

								JSONArray user = resp.getJSONArray("users");
								for (int i = 0; i < user.length(); i++) {
									String label = user.getJSONObject(i)
											.getString("label");
									 mLable.add(label);
                                    
									 //navDrawerItems.add(new NavDrawerItem(label));
								}
								Log.d(TAG, "Menu Response: "
										+ mLable.get(0).toString());
							}

							else {

								Toast.makeText(getApplicationContext(),
										"Null Response from server",
										Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							// JSON error e.printStackTrace();
							Toast.makeText(getApplicationContext(),
									"Json error: " + e.getMessage(),
									Toast.LENGTH_LONG).show();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, "Menu Error: " + error.getMessage());
						Toast.makeText(getApplicationContext(),
								error.getMessage(), Toast.LENGTH_LONG).show();
						hideDialog();
					}
				}) {

			/*
			 * @Override protected Map<String, String> getParams() { // Posting
			 * parameters to login url Map<String, String> params = new
			 * HashMap<String, String>(); params.put("", roleid);
			 * 
			 * return params; }
			 */
		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq);
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
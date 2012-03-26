package com.disaster.idisaster;

//import org.societies.api.css.management.ICssRecord;
//import org.societies.api.css.management.ISocietiesApp;
// import org.societies.cis.android.client.SocietiesApp;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * This activity is responsible for interaction with the
 * main home page for iDisaster.
 * The home page relates to a specific Disaster community
 * and provide access to activity feeds, users and services
 * related to the community.
 * 
 * @authors Jacqueline.Floch@sintef.no
 * 			Babak.Farshchian@sintef.no
 *
 */
public class HomeActivity extends TabActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
// TODO: Add check that 1) a disaster is selected 2) the sleected disaster still exists...
        
        setContentView(R.layout.home_layout);

        // Set view label to selected disaster name
    	String disasterName = iDisasterApplication.getinstance().preferences.
        		getString ("pref.disastername","n/a");
		TextView title = (TextView)findViewById(R.id.disasterLabel);
		title.setText (disasterName);
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resuable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, FeedActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("activities").setIndicator("Activities",
                          res.getDrawable(R.drawable.ic_tab_home))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, UserActivity.class);
        spec = tabHost.newTabSpec("users").setIndicator("Users",
                          res.getDrawable(R.drawable.ic_tab_home))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ServiceActivity.class);
        spec = tabHost.newTabSpec("services").setIndicator("Services",
                          res.getDrawable(R.drawable.ic_tab_home))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Start with disasters tab visible:
        tabHost.setCurrentTab(0);

    }

    /** Called at start of the active lifetime. */
    @Override
	protected void onResume() {
		super.onResume();
	}//onResume

/**
 * onCreateOptionsMenu creates the activity menu.
 */
     
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.clear();
    	getMenuInflater().inflate(R.menu.home_menu, menu);
    	
//    	It is possible to set up a variable menu		
//    	menu.findItem (R.id....).setVisible(true);	
    	return true;
    }

 /**
  * onOptionsItemSelected handles the selection of an item in the activity menu.
  */
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Editor editor =iDisasterApplication.getinstance().editor;
		switch (item.getItemId()) {
    		case R.id.homeMenuSelectDisaster:
    			editor.putString ("pref.disastername", "n/a");
    			editor.commit ();
    			startActivity(new Intent(HomeActivity.this, StartActivity.class));
			break;
    	case R.id.homeMenuLogoff:
//TODO: Call the Societies platform
	    	editor.putString ("pref.username", "n/a");
	    	editor.putString ("pref.password", "n/a");
	    	editor.commit ();
    		startActivity(new Intent(HomeActivity.this, StartActivity.class));
    		break;

    		
    	default:
    		break;
    	}
    	return true;
    }

  
}
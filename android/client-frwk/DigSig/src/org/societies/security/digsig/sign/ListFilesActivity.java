package org.societies.security.digsig.sign;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.societies.security.digsig.api.Sign;
import org.societies.security.digsig.apiinternal.Trust;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListFilesActivity extends ListActivity {
	
	private static final String TAG = ListFilesActivity.class.getSimpleName();
	
	private ArrayList<String> exts = new ArrayList<String>(2);
	private String[] fileArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StringTokenizer tokenizer = new StringTokenizer(getIntent().getStringExtra(Trust.Params.EXTENSIONS), ";");
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();			
			if (token==null || token.length()==0) continue;
			exts.add("."+token);
		}
					
		/* populate list view with certificate list */
		Log.d(TAG, "External storage state = " + Environment.getExternalStorageState());
        File sdCard = new File(Environment.getExternalStorageDirectory().getPath());
        if (sdCard.exists() && sdCard.isDirectory()) {
        	fileArray = sdCard.list(new FilenameFilter() {				
				public boolean accept(File dir, String filename) {					
					for (String ext : exts) 
						if (filename.endsWith(ext)) return true;
					
					return false;
				}
			});
        	
        	setListAdapter(new ArrayAdapter<String>(this, R.layout.list_files, fileArray));
        }
        
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long itemPos) {
				if (fileArray==null || itemPos<0  || itemPos>=fileArray.length) {
					setResult(RESULT_CANCELED);
					finish();
					return;
				}
				
				Log.i(TAG, String.format("Selected item %d", itemPos));
				
				Intent intent = getIntent();
				intent.putExtra(Sign.Params.IDENTITY, Environment.getExternalStorageDirectory().getPath() + "/" + fileArray[(int) itemPos]);
				setResult(RESULT_OK,intent);
				finish();				
			}
		});
	}
}
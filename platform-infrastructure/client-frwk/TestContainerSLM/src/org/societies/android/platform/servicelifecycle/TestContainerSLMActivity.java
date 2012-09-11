package org.societies.android.platform.servicelifecycle;

import org.societies.android.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.android.api.internal.servicemonitor.ICoreServiceMonitor;
import org.societies.android.api.internal.servicemonitor.InstalledAppInfo;
import org.societies.android.api.servicelifecycle.AService;
import org.societies.android.api.servicelifecycle.AServiceResourceIdentifier;
import org.societies.android.api.servicelifecycle.IServiceUtilities;
import org.societies.android.platform.servicemonitor.CoreServiceMonitor;
import org.societies.android.platform.servicemonitor.ServiceManagement;
import org.societies.android.platform.servicemonitor.ServiceManagement.LocalBinder;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class TestContainerSLMActivity extends Activity {

    private IServiceDiscovery serviceDisco;
    private boolean serviceDiscoConnected = false;
    
    private ICoreServiceMonitor coreServiceMonitor;
    private boolean connectedtoCoreMonitor = false;
    
    private IServiceUtilities serviceUtil;
    private boolean serviceUtilConnected = false;
    
    private static final String LOG_TAG = TestContainerSLMActivity.class.getName();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //CREATE INTENT FOR SERVICE DISCO AND BIND
        Intent intentServiceDisco = new Intent(this.getApplicationContext(), ServiceManagement.class);
        this.getApplicationContext().bindService(intentServiceDisco, serviceDiscoConnection, Context.BIND_AUTO_CREATE);
        
        //CREATE INTENT FOR CORE SERVICE MONITOR AND BIND
        Intent intentServiceMon = new Intent(this.getApplicationContext(), CoreServiceMonitor.class);
        this.getApplicationContext().bindService(intentServiceMon, coreServiceMonitorConnection, Context.BIND_AUTO_CREATE);
        
        //CREATE INTENT FOR CORE SERVICE MONITOR AND BIND
        Intent intentServiceUtil = new Intent(this.getApplicationContext(), ServiceManagement.class);
        this.getApplicationContext().bindService(intentServiceUtil, serviceUtilConnection, Context.BIND_AUTO_CREATE);
        
        //REGISTER BROADCAST
        IntentFilter intentFilter = new IntentFilter() ;
        intentFilter.addAction(ServiceManagement.GET_SERVICES);
        intentFilter.addAction(ServiceManagement.GET_MY_SERVICES);
        intentFilter.addAction(ServiceManagement.GET_SERVICE);
        intentFilter.addAction(ServiceManagement.SEARCH_SERVICES);
        intentFilter.addAction(ServiceManagement.GET_MY_SERVICE_ID);
        intentFilter.addAction(CoreServiceMonitor.INSTALLED_APPLICATIONS);
        intentFilter.addAction(CoreServiceMonitor.ACTIVE_SERVICES);
        
        this.getApplicationContext().registerReceiver(new bReceiver(), intentFilter);
        
        //TEST THE SLM COMPONENT
        TestSLM task = new TestSLM(this);
        task.execute();
    }
    
    /**
     * IServiceDiscovery service connection
     */
    private ServiceConnection serviceDiscoConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to IServiceDiscovery service");
        	try {
	        	//GET LOCAL BINDER
	            LocalBinder binder = (LocalBinder) service;
	
	            //OBTAIN SERVICE DISCOVERY API
	            serviceDisco = (IServiceDiscovery) binder.getService();
	            serviceDiscoConnected = true;
	            Log.d(LOG_TAG, "Successfully connected to IServiceDiscovery service");
	            
        	} catch (Exception ex) {
        		Log.d(LOG_TAG, "Error binding to service: " + ex.getMessage());
        	}
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from ServiceDiscovery service");
        	serviceDiscoConnected = false;
        }
    };
    
    /**
     * CoreServiceMonitor service connection
     */
    private ServiceConnection coreServiceMonitorConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to CoreServiceMonitor service");
        	//get a local binder
        	org.societies.android.platform.servicemonitor.CoreServiceMonitor.LocalBinder binder = (org.societies.android.platform.servicemonitor.CoreServiceMonitor.LocalBinder) service;
            //obtain the service's API
            coreServiceMonitor = (ICoreServiceMonitor) binder.getService();
            connectedtoCoreMonitor = true;
            Log.d(LOG_TAG, "Successfully connected to ICoreServiceMonitor service");
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from CoreServiceMonitor service");
        	connectedtoCoreMonitor = false;
        }
    };

    /**
     * serviceUtilConnection service connection
     */
    private ServiceConnection serviceUtilConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to serviceUtil service");
        	//get a local binder
        	LocalBinder binder = (LocalBinder) service;
            //obtain the service's API
        	serviceUtil = (IServiceUtilities) binder.getService();
        	serviceUtilConnected = true;
        	Log.d(LOG_TAG, "Successfully connected to IServiceUtilities service");
        }
        
        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from serviceUtil service");
        	serviceUtilConnected = false;
        }
    };
    
    public void btnSendMessage_onClick(View view) {
    	Log.d(LOG_TAG, ">>>>>>>>btnSendMessage_onClick - serviceDiscoConnected: " + serviceDiscoConnected);
		if (serviceDiscoConnected)
			serviceDisco.getMyServices("org.societies.android.platform.servicelifecycle");
    }
    
    private class TestSLM extends AsyncTask<Void, Void, Void> {
    	
    	private Context context;
    	
    	public TestSLM(Context context) {
    		this.context = context;
    	}
    	
    	protected Void doInBackground(Void... args) {

    		try {//	WAIT TILL ALL THE SERVICES ARE CONNECTED
				Thread.currentThread();
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		
    		//TEST SERVICE DISCO API
    		Log.d(LOG_TAG, ">>>>>>>>serviceDiscoConnected: " + serviceDiscoConnected);
    		if (serviceDiscoConnected)
    			serviceDisco.getMyServices("org.societies.android.platform.servicelifecycle");
    		
    		//TEST CORE SERVICE MONITOR API
    		Log.d(LOG_TAG, ">>>>>>>>connectedtoCoreMonitor: " + connectedtoCoreMonitor);
    		if (connectedtoCoreMonitor) {
	            coreServiceMonitor.activeServices("org.societies.android.platform.servicelifecycle");
	            coreServiceMonitor.getInstalledApplications("org.societies.android.platform.servicelifecycle");
    		}
    		
    		//TEST SERVICE UTILITIES API
    		Log.d(LOG_TAG, ">>>>>>>>serviceUtilConnected: " + serviceUtilConnected);
    		if (serviceUtilConnected)
    			serviceUtil.getMyServiceId("org.societies.android.platform.servicelifecycle");    		
    		
    		return null;
    	}
    }
    
    private class bReceiver extends BroadcastReceiver  {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, intent.getAction());
			
			if ((intent.getAction().equals(ServiceManagement.GET_SERVICES)) || (intent.getAction().equals(ServiceManagement.GET_MY_SERVICES))) {
				//UNMARSHALL THE SERVICES FROM Parcels BACK TO Services
				Parcelable parcels[] =  intent.getParcelableArrayExtra(ServiceManagement.INTENT_RETURN_VALUE);
				for (int i = 0; i < parcels.length; i++) {
					AService aservice = (AService) parcels[i];
					Log.d(LOG_TAG, ">>>>>GET MY SERVICES RESULTS:\nService Name: " + aservice.getServiceName());
					Log.d(LOG_TAG, "Service Description: " + aservice.getServiceDescription());
				}
			}
			
			if (intent.getAction().equals(ServiceManagement.GET_MY_SERVICE_ID)) {
				//UNMARSHALL THE ID FROM THE RETURNED PARCEL
				Parcelable parcel =  intent.getParcelableExtra(ServiceManagement.INTENT_RETURN_VALUE);
				AServiceResourceIdentifier sri =  (AServiceResourceIdentifier) parcel;
				Log.d(LOG_TAG, ">>>>>GET MY SERVICE ID RESULTS:\nSRI.identifier: " + sri.getIdentifier().toString());
				Log.d(LOG_TAG, "SRI.ServiceInstanceId: " + sri.getServiceInstanceIdentifier());
			}
			
			if (intent.getAction().equals(CoreServiceMonitor.INSTALLED_APPLICATIONS)) {
				//UNMARSHALL THE APPS FROM Parcels BACK TO InstalledAppInfo
				Parcelable parcels[] =  intent.getParcelableArrayExtra(CoreServiceMonitor.INTENT_RETURN_KEY);
				for (int i = 0; i < parcels.length; i++) {
					InstalledAppInfo app = (InstalledAppInfo) parcels[i];
					Log.d(LOG_TAG, ">>>>>INSTALLED APPS RESULTS:\nApp Name: " + app.getApplicationName());
					Log.d(LOG_TAG, "Package Name: " + app.getPackageName());
				}
			}
			
			if (intent.getAction().equals(CoreServiceMonitor.ACTIVE_SERVICES)) {
				Parcelable parcels[] =  intent.getParcelableArrayExtra(CoreServiceMonitor.INTENT_RETURN_KEY);
				for (int i = 0; i < parcels.length; i++) {
					ActivityManager.RunningServiceInfo info = (ActivityManager.RunningServiceInfo) parcels[i];
					Log.d(LOG_TAG, ">>>>>ACTIVE SERVICES RESULTS:\nName: " + info.clientLabel);
					Log.d(LOG_TAG, "Package: " + info.clientPackage);
				}
			}
		}
	};
}
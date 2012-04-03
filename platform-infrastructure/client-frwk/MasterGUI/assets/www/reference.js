/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


function onDeviceReady() {
	console.log("PhoneGap Loaded, Device Ready");
	
	//Register any PhoneGap plugins here. Example shown for illustration
	 
	PhoneGap.addConstructor(function() {
		//Register the javascript plugin with PhoneGap
		console.log("Register CoreServiceMonitorService plugin ");
		PhoneGap.addPlugin('CoreServiceMonitorService', new CoreServiceMonitorService());
		
		console.log("Register LocalCSSManagerService plugin ");
		PhoneGap.addPlugin('LocalCSSManagerService', new LocalCSSManagerService());
	 
	});
}

/**
 * LocalCSSManagerService object
 * @return Instance of ConnectionListener
 */
var LocalCSSManagerService = function() { 
}

/**
 * @param successCallback The callback which will be called when Java method is successful
 * @param failureCallback The callback which will be called when Java method has an error
*/
LocalCSSManagerService.prototype.connectService = function(successCallback, failureCallback) {
	console.log("Call LocalCSSManagerService - connectService");

	return PhoneGap.exec(successCallback,    //Callback which will be called when plugin action is successful
	failureCallback,     //Callback which will be called when plugin action encounters an error
	'PluginCSSManager',  //Telling PhoneGap that we want to run specified plugin
	'connectService',          //Telling the plugin, which action we want to perform
	[]);        //Passing a list of arguments to the plugin
};

/**
 * @param successCallback The callback which will be called when Java method is successful
 * @param failureCallback The callback which will be called when Java method has an error
*/
LocalCSSManagerService.prototype.disconnectService = function(successCallback, failureCallback) {
	console.log("Call LocalCSSManagerService - disconnectService");

	return PhoneGap.exec(successCallback,    //Callback which will be called when plugin action is successful
	failureCallback,     //Callback which will be called when plugin action encounters an error
	'PluginCSSManager',  //Telling PhoneGap that we want to run specified plugin
	'disconnectService',          //Telling the plugin, which action we want to perform
	[]);        //Passing a list of arguments to the plugin
};


/**
 * @param successCallback The callback which will be called when Java method is successful
 * @param failureCallback The callback which will be called when Java method has an error
*/
LocalCSSManagerService.prototype.loginCSS = function(successCallback, failureCallback) {
	var client = "org.societies.android.platform.gui";
	var cssRecord = {
			  			"archiveCSSNodes": [],
	                    "cssIdentity": "android",
	                    "cssInactivation": null,
	                    "cssNodes": [{
	                        "identity": "android@societies.local/androidOne",
	                        "status": 0,
	                        "type": 0}],
	                    "cssRegistration": null,
	                    "cssHostingLocation" : null,
	                    "domainServer" : null,
	                    "cssUpTime": 0,
	                    "emailID": null,
	                    "entity": 0,
	                    "foreName": null,
	                    "homeLocation": null,
	                    "identityName": null,
	                    "imID": null,
	                    "name": null,
	                    "password": "androidpass",
	                    "presence": 0,
	                    "sex": 0,
	                    "socialURI": null,
	                    "status": 0
			                  }


	console.log("Call LocalCSSManagerService - loginCSS");

	return PhoneGap.exec(successCallback,    //Callback which will be called when plugin action is successful
	failureCallback,     //Callback which will be called when plugin action encounters an error
	'PluginCSSManager',  //Telling PhoneGap that we want to run specified plugin
	'loginCSS',          //Telling the plugin, which action we want to perform
	[client, cssRecord]);        //Passing a list of arguments to the plugin
};

/**
 * CoreServiceMonitorService object
 * @return Instance of CoreServiceMonitorService
 */
var CoreServiceMonitorService = function() { 
}

/**
 * Used to bind to service that plugin connects to
 * @param successCallback The callback which will be called when Java method is successful
 * @param failureCallback The callback which will be called when Java method has an error
*/
CoreServiceMonitorService.prototype.connectService = function(successCallback, failureCallback) {

	console.log("Call CoreServiceMonitorService - connectService");

	return PhoneGap.exec(successCallback,    //Callback which will be called when plugin action is successful
	failureCallback,     //Callback which will be called when plugin action encounters an error
	'PluginCoreServiceMonitor',  //Telling PhoneGap that we want to run specified plugin
	'connectService',              //Telling the plugin, which action we want to perform
	[]);        //Passing a list of arguments to the plugin
};

/**
 * Used to unbind service that plugin connects to
 * @param successCallback The callback which will be called when Java method is successful
 * @param failureCallback The callback which will be called when Java method has an error
*/
CoreServiceMonitorService.prototype.disconnectService = function(successCallback, failureCallback) {

	console.log("Call CoreServiceMonitorService - disconnectService");

	return PhoneGap.exec(successCallback,    //Callback which will be called when plugin action is successful
	failureCallback,     //Callback which will be called when plugin action encounters an error
	'PluginCoreServiceMonitor',  //Telling PhoneGap that we want to run specified plugin
	'disconnectService',              //Telling the plugin, which action we want to perform
	[]);        //Passing a list of arguments to the plugin
};

/**
 * @param successCallback The callback which will be called when Java method is successful
 * @param failureCallback The callback which will be called when Java method has an error
*/
CoreServiceMonitorService.prototype.activeServices = function(successCallback, failureCallback) {

	console.log("Call CoreServiceMonitorService - activeServices");

	return PhoneGap.exec(successCallback,    //Callback which will be called when plugin action is successful
	failureCallback,     //Callback which will be called when plugin action encounters an error
	'PluginCoreServiceMonitor',  //Telling PhoneGap that we want to run specified plugin
	'activeServices',              //Telling the plugin, which action we want to perform
	["org.societies.android.platform.gui"]);        //Passing a list of arguments to the plugin
};

/**
 * @param successCallback The callback which will be called when Java method is successful
 * @param failureCallback The callback which will be called when Java method has an error
*/
CoreServiceMonitorService.prototype.activeTasks = function(successCallback, failureCallback) {
	var clientPackage = "org.societies.android.platform.gui";

	console.log("Call CoreServiceMonitorService - activeTasks");

	return PhoneGap.exec(successCallback,    //Callback which will be called when plugin action is successful
	failureCallback,     //Callback which will be called when plugin action encounters an error
	'PluginCoreServiceMonitor',  //Telling PhoneGap that we want to run specified plugin
	'activeTasks',              //Telling the plugin, which action we want to perform
	["org.societies.android.platform.gui"]);        //Passing a list of arguments to the plugin
};

var deviceInfo = function() {
	console.log("Get device information");
	
	jQuery("#phoneGapVer").text(device.phonegap);
	jQuery("#platform").text(device.platform);
	jQuery("#version").text(device.version);
	jQuery("#uuid").text(device.uuid);
	jQuery("#name").text(device.name);
	jQuery("#width").text(screen.width);
	jQuery("#height").text(screen.height);
	jQuery("#colorDepth").text(screen.colorDepth);
	jQuery("#pixelDepth").text(screen.pixelDepth);
	jQuery("#browserAgent").text(navigator.userAgent);

};


var connectToLocalCSSManager = function(actionFunction) {
	console.log("Connect to LocalCSSManager");
		
	function success(data) {
		actionFunction();
	}
	
	function failure(data) {
		alert(data);
	}
    window.plugins.LocalCSSManagerService.connectService(success, failure);
}

var successfulLogin = function() {
	console.log("Login to CSS");

	function success(data) {
		$.mobile.changePage( ($("#menu")), { transition: "slideup"} );
	}
	
	function failure(data) {
		alert(data);
	}
    window.plugins.LocalCSSManagerService.loginCSS(success, failure);

};



var resetDeviceMgr = function(){
    jQuery("#connStatuslist").text("");
    jQuery("#battStatuslist").text("");
    jQuery("#locStatuslist").text("");
    
    
    
}

var connectToCoreServiceMonitor = function(actionFunction) {
	console.log("Connect to CoreServiceMonitor");
		
	function success(data) {
//		alert("Success result: " + data);
		actionFunction();
	}
	
	function failure(data) {
		alert(data);
	}
    window.plugins.CoreServiceMonitorService.connectService(success, failure);
}

var refreshActiveServices = function() {
	console.log("Refresh Active Service");

	function success(data) {
		//empty table
		jQuery('#activeServicesTable tbody').remove();
		
		for (i  = 0; i < data.length; i++) {
			var tableEntry = "<tr>" + 
			"<td>" + data[i].className + "</td>" + 
			"<td>" + convertMilliseconds(data[i].activeSince) + "</td>" + 
				+ "</tr>"

			jQuery('#activeServicesTable tr:last').after(tableEntry);
		}
	}
	
	function failure(data) {
		alert(data);
	}
	
	window.plugins.CoreServiceMonitorService.activeServices(success, failure);
	
};
var refreshActiveTasks = function() {
	console.log("Refresh Active Tasks");

	function success(data) {
		//empty table
		jQuery('#activeTasksTable tbody').remove();
		
		for (i  = 0; i < data.length; i++) {
			var tableEntry = "<tr>" + 
			"<td>" + data[i].className + "</td>" + 
			"<td>" + data[i].numRunningActivities + "</td>" + 
				+ "</tr>"

			jQuery('#activeTasksTable tr:last').after(tableEntry);
		}
	}
	
	function failure(data) {
		alert(data);
	}
	
	window.plugins.CoreServiceMonitorService.activeTasks(success, failure);
};

//Convert an elapsed time in milliseconds
var convertMilliseconds = function(milliseconds) {
	value = milliseconds / 1000
	seconds = value % 60
	value /= 60
	minutes = value % 60
	value /= 60
	hours = value % 24
	value /= 24
	days = value	
	return "d: " + days + " h: " + hours + " m:" + minutes;
}
/**
 * Add Javascript functions to various HTML tags using JQuery
 */

jQuery(function() {
	console.log("jQuery calls");

	document.addEventListener("deviceready", onDeviceReady, false);
	
	$('#deviceChar').click(function() {
		deviceInfo();
	});

	$('#connectXMPP').click(function() {
		connectToLocalCSSManager(successfulLogin);
	});
	
	$('#resetDeviceManager').click(function() {
		resetDeviceMgr();
	});

	$('#refreshServices').click(function() {
		connectToCoreServiceMonitor(refreshActiveServices);
	});

	$('#refreshTasks').click(function() {
		connectToCoreServiceMonitor(refreshActiveTasks);
	});

});



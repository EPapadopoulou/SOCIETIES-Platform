/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.disaster.idisaster;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

// added
import android.widget.Button;
import android.view.View.OnClickListener;

/**
 * This activity is responsible for loging in the user,
 * including handling wrong user name and password.
 * 
 * @author Jacqueline.Floch@sintef.no
 *
 */
public class LoginActivityCopy extends Activity { //implements OnClickListener {

	private EditText userName;
	private EditText userPassword;

	@Override
	    protected void onCreate(Bundle savedInstanceState) {
	    	// TODO Auto-generated method stub
	    	super.onCreate(savedInstanceState);
	    	setContentView (R.layout.login_layout);
	    	
/**	    	userName = (EditText) findViewById(R.id.editText1);
	    
         final Button button = (Button) findViewById(R.id.button1);
         button.setOnClickListener(this);

*/
	    }

 		// This method is called at button click because we assigned the name to the
 		// "On Click property" of the button

/**		public void onClick (View view) {
			switch (view.getId()) {
				case R.id.button1:
	    			RadioButton celsiusButton = (RadioButton) findViewById(R.id.radio0);
	    			RadioButton fahrenheitButton = (RadioButton) findViewById(R.id.radio1);
	    			if (userName.getText().length() == 0) {
	    				Toast.makeText(this, "Please enter a valid number",
	    						Toast.LENGTH_LONG).show();
	    				return;
	    			}

 				float inputValue = Float.parseFloat(userName.getText().toString());
				if (celsiusButton.isChecked()) {
   					userName.setText(String
   						.valueOf(convertFahrenheitToCelsius(inputValue)));
	    			celsiusButton.setChecked(false);
	    			fahrenheitButton.setChecked(true);
	    		} else {
	    			userName.setText(String
	    					.valueOf(convertCelsiusToFahrenheit(inputValue)));
	    			fahrenheitButton.setChecked(false);
	    			celsiusButton.setChecked(true);
	    		}
	    		break;
	    	}
	    }

	 	// Converts to celsius
  		private float convertFahrenheitToCelsius(float fahrenheit) {
   			return ((fahrenheit - 32) * 5 / 9);
   		}

  		// Converts to fahrenheit
	   	private float convertCelsiusToFahrenheit(float celsius) {
			return ((celsius * 9) / 5) + 32;
		}
*/
}

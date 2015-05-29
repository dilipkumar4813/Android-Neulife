package app.retailinsights.neulife.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.account.ConfirmPhoneNumber;
import app.retailinsights.neulife.account.LoginDialogue;
import app.retailinsights.neulife.account.LoginMain;


public class IncomingSms extends BroadcastReceiver {
	
	// Get the object of SmsManager
	final SmsManager sms = SmsManager.getDefault();
	
	public void onReceive(Context context, Intent intent) {
	
		// Retrieves a map of extended data from the intent.
		final Bundle bundle = intent.getExtras();

		try {
			
			if (bundle != null) {
				
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				
				for (int i = 0; i < pdusObj.length; i++) {
					
					SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
					String phoneNumber = currentMessage.getDisplayOriginatingAddress();
					
			        String senderNum = phoneNumber;
			        String message = currentMessage.getDisplayMessageBody();

			        Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
			        
			        

			        Log.d("Context",""+context);
			        Log.d("Intent",""+intent);
			        
					if(message.contains("Your Neulife verification code is ")){
						SessionStorage.confirmPhone=message.substring(33, message.length());
						
						Intent intent1 = new Intent(context.getApplicationContext(), LoginMain.class);
						intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent1);
						
						int duration = Toast.LENGTH_LONG;
						Toast toast = Toast.makeText(context, "senderNum: "+ senderNum + ", message: " + message, duration);
						toast.show();
					}
					
				} // end for loop
              } // bundle is null

		} catch (Exception e) {
			Log.e("SmsReceiver", "Exception smsReceiver" +e);
			
		}
	}

	
	
}
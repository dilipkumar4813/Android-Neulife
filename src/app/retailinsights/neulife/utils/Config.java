package app.retailinsights.neulife.utils;

public interface Config {
	 
    
/*    // CONSTANTS
    static final String YOUR_SERVER_URL =  
                          "YOUR_SERVER_URL/gcm_server_files/register.php";
     
    // Google project id
    static final String GOOGLE_SENDER_ID = "9432667788990"; 
 
    *//**
     * Tag used on log messages.
     *//*
    static final String TAG = "GCM Android Example";
 
    static final String DISPLAY_MESSAGE_ACTION =
            "com.example.homerun_app.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";
 */	
	static final String APP_SERVER_URL = "http://192.168.1.102/gcm/gcm.php?shareRegId=1";

	static final String GOOGLE_PROJECT_ID = "1082006795397";
	
	static final String MESSAGE_KEY = "message";
         
     
}
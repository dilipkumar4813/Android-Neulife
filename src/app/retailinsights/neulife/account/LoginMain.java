package app.retailinsights.neulife.account;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import app.retailinsights.neulife.InternetConnection;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.Message;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.account.LoginDialogue;
import app.retailinsights.neulife.database.AndroidDatabaseManager;
import app.retailinsights.neulife.utils.Config;
import app.retailinsights.neulife.utils.ConnectionDetector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginMain extends Activity implements View.OnClickListener,
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	GoogleCloudMessaging gcm;
	Context context;
	String regId;

	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";
	private String gcmid = "";
	static final String TAG1 = "Register Activity";

	ProgressDialog pd;
	public String email, pass, fname, lname, phon;

	// Authentication webservice method for G+ and FB
	private static final String METHOD = "authenticate";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/authenticate";
	public int status;

	// Registration webservice method for G+ and FB
	private static final String METHOD2 = "register";
	private final String SOAP_ACTION2 = SessionStorage.webserviceNamespace
			+ "service.php/register";

	// Webservice variables to get the profile information
	private static final String METHOD3 = "getProfile";
	private final String SOAP_ACTION3 = SessionStorage.webserviceNamespace
			+ "service.php/getProfile";

	private static final int RC_SIGN_IN = 0;
	// Logcat tag
	private static final String TAG = "MainActivity";
	private SharedPreferences mPrefs;
	// Profile pic image size in pixels
	private static final int PROFILE_PIC_SIZE = 400;
	Button btnGuestUser;
	Button btnShowDatabase;
	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;
	// Your Facebook APP ID
	// private static String APP_ID = "1380667308925935";
	// delete this
	private static String APP_ID = "1380667308925935";
	Facebook facebook;
	private AsyncFacebookRunner mAsyncRunner;
	String IMEI = "";

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;

	Button buttonRegister, buttonLogin;
	ImageView buttonGoogleLogin, buttonFBLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_main);

		if (!SessionStorage.confirmPhone.isEmpty()) {
			ConfirmPhoneNumber add = new ConfirmPhoneNumber(LoginMain.this);
			add.show();

			DisplayMetrics metrics = this.getResources().getDisplayMetrics();
			int wid = metrics.widthPixels / 2 + 180;
			int heig = metrics.heightPixels / 2;
			add.getWindow().setLayout(wid, heig);
		}

		// Facebook Login
		facebook = new Facebook(APP_ID);
		Log.d("Facebook Value", "" + facebook);
		// Getting IMEI id
		TelephonyManager tMgr = (TelephonyManager) this.getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		IMEI = tMgr.getDeviceId();

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(LoginMain.this);
			add.show();

			int wid = metrics.widthPixels / 2 + 180;
			int heig = metrics.heightPixels / 3;
			add.getWindow().setLayout(wid, heig);
		}

		initialize();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
		initialize();
		/**
		 * Revoking access from google
		 * */
		revokeGplusAccess();

		btnShowDatabase.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent dbmanager = new Intent(getApplicationContext(),
						AndroidDatabaseManager.class);
				startActivity(dbmanager);
			}
		});

	}

	void initialize() {
		buttonRegister = (Button) findViewById(R.id.btnNewUser);
		buttonRegister.setOnClickListener(this);
		buttonLogin = (Button) findViewById(R.id.btnLogin);
		buttonLogin.setOnClickListener(this);
		buttonGoogleLogin = (ImageView) findViewById(R.id.googleLogin);
		buttonGoogleLogin.setOnClickListener(this);
		buttonFBLogin = (ImageView) findViewById(R.id.fbLogin1);
		buttonFBLogin.setOnClickListener(this);
		btnGuestUser = (Button) findViewById(R.id.btnGuestUser);
		btnGuestUser.setOnClickListener(this);
		btnShowDatabase = (Button) findViewById(R.id.btnShowDatabase);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnNewUser:
			onSignupBtnClicked();
			break;
		case R.id.btnLogin:
			onLoginBtnClicked();
			break;
		case R.id.btnGuestUser:
			//Storing the guest user details
			SessionStorage.customerType = "0";
			AsyncCallWSGuestUser obj = new AsyncCallWSGuestUser();
			obj.execute();
			
			this.finish();
			onGuestUserBtnClicked();
			break;
		case R.id.googleLogin:
			SessionStorage.clientId = "1";
			/*
			 * Toast.makeText(getApplicationContext(), "Please Wait",
			 * Toast.LENGTH_SHORT).show();
			 */
			onGoogleBtnClicked();
			break;
		case R.id.fbLogin1:
			/*
			 * Toast.makeText(getApplicationContext(), "Please Wait",
			 * Toast.LENGTH_SHORT).show();
			 */
			onFacebookBtnClicked();
			break;
		default:
			break;
		}
	}

	private void onGuestUserBtnClicked() {
		Intent intent = new Intent(getApplicationContext(), LandingScreen.class);
		startActivity(intent);
	}

	void onFacebookBtnClicked() {
		/*
		 * SessionStorage.clientId = "1"; revokeGplusAccess();
		 */
		Log.d("Facebook Button:", "Clicked");
		loginToFacebook();
	}

	void onGoogleBtnClicked() {
		SessionStorage.clientId = "1";
		signInWithGplus();
	}

	void onLoginBtnClicked() {
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();

		LoginDialogue add = new LoginDialogue(LoginMain.this);
		add.show();

		int wid = metrics.widthPixels / 2 + 180;
		int heig = metrics.heightPixels / 2;
		add.getWindow().setLayout(wid, heig);
	}

	void onSignupBtnClicked() {
		Intent intent = new Intent(getApplicationContext(),
				SignUpActivity.class);
		startActivity(intent);
	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		/*
		 * Toast.makeText(this, "User is connected with google plus!",
		 * Toast.LENGTH_LONG).show();
		 */

		// Get user's information
		getProfileInformation();
		buttonGoogleLogin.setVisibility(View.GONE);
		Intent intent = new Intent(LoginMain.this, LandingScreen.class);
		startActivity(intent);
		// Update the UI after signin
		// updateUI(true);

	}

	protected void onStart() {
		super.onStart();
		if (!mGoogleApiClient.isConnected()) {
			mGoogleApiClient.connect();
		}
		// mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	/**
	 * Method to resolve any signin errors
	 * */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}

		// Facebook
		facebook.authorizeCallback(requestCode, responseCode, intent);
	}

	/**
	 * Sign-in into google
	 * */
	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}
	}

	/**
	 * Sign-out from google
	 * */
	private void signOutFromGplus() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			// updateUI(false);
		}
	}

	/**
	 * Fetching user's information name, email, profile pic
	 * */
	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				String personName = currentPerson.getDisplayName();
				String personPhotoUrl = currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();
				String eemail = Plus.AccountApi
						.getAccountName(mGoogleApiClient);
				int p = 0;
				for (String retval : personName.split(" ", 2)) {
					if (p == 0) {
						fname = retval;
						p++;
					} else {
						lname = retval;
					}
				}

				Log.e(TAG, "Name: " + personName + ", plusProfile: "
						+ personGooglePlusProfile + ", email: " + eemail
						+ ", Image: " + personPhotoUrl);
				email = eemail;
				pass = eemail;
				SessionStorage.fName = fname;
				SessionStorage.lName = lname;
				SessionStorage.email = eemail;
				SessionStorage.username = eemail;
				personPhotoUrl = personPhotoUrl.substring(0,
						personPhotoUrl.length() - 2)
						+ PROFILE_PIC_SIZE;

				// Calling the webservice and GCM to store the required
				// information on the database
				getGCMIDAndShare();

			} else {
				/*
				 * Toast.makeText(getApplicationContext(),
				 * "Person information is null", Toast.LENGTH_LONG).show();
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function to login into facebook
	 * */
	@SuppressWarnings("deprecation")
	public void loginToFacebook() {

		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);

		Log.d("Login to facebook function:", "Entered");
		if (access_token != null) {
			facebook.setAccessToken(access_token);
			Log.d("FB Sessions", "" + facebook.isSessionValid());
			getProfileInformationFacebook();
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(this,
					new String[] { "email", "public_profile" },
					new DialogListener() {

						@Override
						public void onCancel() {
							// Function to handle cancel event
						}

						@Override
						public void onComplete(Bundle values) {
							// Function to handle complete event
							// Edit Preferences and update facebook acess_token
							SharedPreferences.Editor editor = mPrefs.edit();
							editor.putString("access_token",
									facebook.getAccessToken());
							editor.putLong("access_expires",
									facebook.getAccessExpires());
							editor.commit();

						}

						@Override
						public void onError(DialogError error) {
							// Function to handle error

						}

						@Override
						public void onFacebookError(FacebookError fberror) {
							// Function to handle Facebook errors

						}

					});
		}

	}

	/**
	 * Get Profile information by making request to Facebook Graph API
	 * */
	@SuppressWarnings("deprecation")
	public void getProfileInformationFacebook() {
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		Log.w("mSync value...........", "" + mAsyncRunner);
		mAsyncRunner.request("me", new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				Log.d("Profile", response);
				String json = response;
				try {
					// Facebook Profile JSON data
					JSONObject profile = new JSONObject(json);

					// getting name of the user
					String personName = profile.getString("name");
					Log.d("Facebook Name", personName);
					String fName = "", lName = "";
					int p = 0;
					for (String retval : personName.split(" ", 2)) {
						if (p == 0) {
							fName = retval;
							p++;
						} else {
							lName = retval;
						}
					}

					// getting email of the user
					email = profile.getString("email");

					Log.d("First Name:", "" + SessionStorage.fName);
					Log.d("Last Name:", "" + SessionStorage.lName);
					SessionStorage.fName = fName;
					SessionStorage.lName = lName;
					SessionStorage.email = email;
					SessionStorage.username = email;

					Log.d("Facebook Login", SessionStorage.fName + " "
							+ SessionStorage.lName + " "
							+ SessionStorage.username);

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							getGCMIDAndShare();
							/*
							 * Toast.makeText(getApplicationContext(), "Name: "
							 * + firstName + "\nEmail: " + email,
							 * Toast.LENGTH_LONG).show();
							 */
						}

					});

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Revoking access from google
	 * */
	private void revokeGplusAccess() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
					.setResultCallback(new ResultCallback<Status>() {
						@Override
						public void onResult(Status arg0) {
							Log.e(TAG, "User access revoked!");
							mGoogleApiClient.connect();
							// updateUI(false);
						}

					});
		}
	}

	/***********
	 * Authentication Webservice
	 * 
	 */
	public void changeScreen() {
		Intent intent = new Intent(getApplicationContext(), LandingScreen.class);
		startActivity(intent);
	}

	// Webservice for authentication
	public void authenticate(String email, String password)
			throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("emailid", email);
		request.addProperty("password", password);
		request.addProperty("imei", IMEI);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("Login Status:", response.toString());

			JSONObject jsonObject = new JSONObject(response.toString());

			status = jsonObject.optInt("status");

			if (status != 0) {
				SessionStorage.clientId = "1";
				SessionStorage.username = email;
			}
		} catch (HttpResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getProfile();
	}

	// Get profile asynchronous class and methods
	public void getProfile() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD3);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("username", SessionStorage.username);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION3, envelope);
			Object response = envelope.getResponse();

			Log.d("Profile Details", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("profile");

			// Reading through the array of products
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = jsonarray.getJSONObject(i);

				SessionStorage.userId = object.getString("id");
				SessionStorage.username = object.getString("username");
			}

		} catch (HttpResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class AsyncCallWSAuthenticate extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(LoginMain.this);
			pd.setMessage("Loading...");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			if (status == 0) {
				Toast.makeText(LoginMain.this, "Incorrect Username/Password",
						Toast.LENGTH_LONG).show();
			} else {
				SessionStorage.clientId = "1";
				/*
				 * Toast.makeText(LoginMain.this, "Login Successfull",
				 * Toast.LENGTH_LONG).show();
				 */
				changeScreen();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				authenticate(email, pass);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	/************
	 * Webservice for Registration
	 * 
	 */

	// Getting the GCMID
	public void getGCMIDAndShare() {

		if (TextUtils.isEmpty(regId)) {
			regId = registerGCM();
			Log.d("RegisterActivity", "GCM RegId: " + regId);
		}

		if (TextUtils.isEmpty(regId)) {
			/*
			 * Toast.makeText(getApplicationContext(), "RegId is empty!",
			 * Toast.LENGTH_LONG).show();
			 */
		} else {
			Intent i = new Intent(getApplicationContext(), LoginMain.class);
			i.putExtra("regId", regId);
			Log.d("RegisterActivity",
					"onClick of Share: Before starting main activity.");
			startActivity(i);
			finish();
			Log.d("RegisterActivity", "onClick of Share: After finish.");
		}
	}

	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(this);
		regId = getRegistrationId(getApplicationContext());

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity",
					"registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		} else {
			gcmid = regId;
		}

		return regId;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(
				SignUpActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("RegisterActivity",
					"I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pd = new ProgressDialog(LoginMain.this);
				pd.setMessage("Loading...");
				pd.show();
			}

			@Override
			protected String doInBackground(Void... params) {
				Log.w("register() parameter values", "" + email + "pass: "
						+ pass + fname + lname + gcmid + IMEI);

				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					regId = gcm.register(Config.GOOGLE_PROJECT_ID);

					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regId);
					msg = "Device registered, registration ID=" + regId;

					gcmid = regId;

					try {
						register(email, pass, fname, lname, phon);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				pd.dismiss();

				if (status == 0) {
					Toast.makeText(LoginMain.this, "Registration Failed",
							Toast.LENGTH_LONG).show();
				} else if (status == 2) {
					AsyncCallWSAuthenticate authenticate = new AsyncCallWSAuthenticate();
					authenticate.execute();
				} else {
					changeScreen();
				}
			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(
				SignUpActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i("Saving the required informaiton", "Saving regId on app version "
				+ appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("Registration ID", regId);
		editor.putInt("App Version", appVersion);
		editor.commit();
	}

	// Webservice for Registration
	public void register(String email, String pass, String fname, String lname,
			String phon) throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		if (phon == null) {
			phon = "";
		}
		request.addProperty("emailId", email);
		request.addProperty("password", "");
		request.addProperty("phone", phon);
		request.addProperty("firstName", SessionStorage.fName);
		request.addProperty("lastName", SessionStorage.lName);
		request.addProperty("imei", IMEI);
		request.addProperty("gcm", gcmid);
		request.addProperty("sex", "Male");

		Log.w("Parameters", "" + request);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("Registration Status for Google+ and Facebook:",
					response.toString());

			JSONObject jsonObject = new JSONObject(response.toString());

			status = jsonObject.optInt("status");

		} catch (HttpResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Registration webservice method for Guest User
	private static final String METHOD4 = "guestUser";
	private final String SOAP_ACTION4 = SessionStorage.webserviceNamespace
			+ "service.php/guestUser";

	// Webservice for authentication
	public void guestUserRegistration() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD4);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION4, envelope);
			Object response = envelope.getResponse();

			Log.d("Guest User Status:", response.toString());

			JSONObject jsonObject = new JSONObject(response.toString());

			status = jsonObject.optInt("status");

		} catch (HttpResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class AsyncCallWSGuestUser extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				guestUserRegistration();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}

package app.retailinsights.neulife.account;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import app.retailinsights.neulife.InternetConnection;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.Message;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.utils.Config;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class SignUpActivity extends Activity {

	GoogleCloudMessaging gcm;
	Context context;
	String regId;

	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";
	private String gcmid = "";
	static final String TAG = "Register Activity";

	ProgressDialog pd;
	private static final String METHOD = "register";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/register";
	public int status;
	public String fname = "", lname = "", phon = "", email = "", pass = "",
			confirm_pass = "", IMEI = "", sex;
	public EditText firstName, lastName, username, phone, password,
			pass_confirm;
	public LinearLayout createAccount;
	TextView tvfirstName, tvlastName, tvEmail, tvPhone, tvPassword,
			tvPasswordConfirm, tvCreate;
	private RadioGroup radioSexGroup;
	private RadioButton radioSexButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.signup_screen);

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(SignUpActivity.this);
			add.show();

			int wid = metrics.widthPixels / 2 + 180;
			int heig = metrics.heightPixels / 3;
			add.getWindow().setLayout(wid, heig);
		}

		initialize();

		tvCreate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				fname = firstName.getText().toString();
				lname = lastName.getText().toString();
				phon = phone.getText().toString();
				email = username.getText().toString();
				pass = password.getText().toString();

				//Which will be used for confirming the phone number
				SessionStorage.email = email;

				// get selected radio button from radioGroup
				int selectedId = radioSexGroup.getCheckedRadioButtonId();
				// find the radiobutton by returned id
				radioSexButton = (RadioButton) findViewById(selectedId);
				sex = radioSexButton.getText().toString();

				if (isSignUpDataValid()) {
					getGCMIDAndShare();
				}
			}
		});
	}

	public void initialize() {
		radioSexGroup = (RadioGroup) findViewById(R.id.rbgGender);

		tvfirstName = (TextView) findViewById(R.id.tvfirstName);
		tvfirstName.setText(Html.fromHtml(getString(R.string.fName)));

		tvlastName = (TextView) findViewById(R.id.tvlastName);
		tvlastName.setText(Html.fromHtml(getString(R.string.lName)));

		tvEmail = (TextView) findViewById(R.id.tvEmail);
		tvEmail.setText(Html.fromHtml(getString(R.string.email)));

		tvPhone = (TextView) findViewById(R.id.tvPhone);
		tvPhone.setText(Html.fromHtml(getString(R.string.mobile)));

		tvPassword = (TextView) findViewById(R.id.tvPassword);
		tvPassword.setText(Html.fromHtml(getString(R.string.pass)));

		tvPasswordConfirm = (TextView) findViewById(R.id.tvPasswordConfirm);
		tvPasswordConfirm.setText(Html
				.fromHtml(getString(R.string.confirmpass)));

		firstName = (EditText) findViewById(R.id.editText0);
		lastName = (EditText) findViewById(R.id.editText);
		username = (EditText) findViewById(R.id.editText1);
		phone = (EditText) findViewById(R.id.editText3);
		password = (EditText) findViewById(R.id.etPassword);
		pass_confirm = (EditText) findViewById(R.id.etPasswordConfirm);

		tvCreate = (TextView) findViewById(R.id.tvCreate);

		// Getting the phone number if it is stored
		TelephonyManager tMgr = (TelephonyManager) SignUpActivity.this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();
		if(mPhoneNumber != null && !mPhoneNumber.isEmpty())
			{
				phon = mPhoneNumber.substring(2);
				phone.setText(phon);
			}
		

		// Getting IMEI id
		IMEI = tMgr.getDeviceId();
	}

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
			Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
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
				pd = new ProgressDialog(SignUpActivity.this);
				pd.setMessage("Loading...");
				pd.show();
			}

			@Override
			protected String doInBackground(Void... params) {
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
					Toast.makeText(SignUpActivity.this, "Registration Failed",
							Toast.LENGTH_SHORT).show();
				} else if (status == 2) {

					SessionStorage.message = "User already exists";
					DisplayMetrics metrics = SignUpActivity.this.getResources()
							.getDisplayMetrics();

					Message add = new Message(SignUpActivity.this);
					add.show();

					int wid = metrics.widthPixels / 2 + 120;
					int heig = metrics.heightPixels / 3;
					add.getWindow().setLayout(wid, heig);
				} else {
					Toast.makeText(SignUpActivity.this,
							"Registered Successfully", Toast.LENGTH_SHORT)
							.show();
					Intent intent = new Intent(SignUpActivity.this, LandingScreen.class);
					startActivity(intent);
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

		request.addProperty("emailId", email);
		request.addProperty("password", pass);
		request.addProperty("phone", phon);
		request.addProperty("firstName", fname);
		request.addProperty("lastName", lname);
		request.addProperty("imei", IMEI);
		request.addProperty("gcm", gcmid);
		request.addProperty("sex", sex);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("Register Status:", response.toString());

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

	public boolean isSignUpDataValid() {

		fname = firstName.getText().toString();
		lname = lastName.getText().toString();
		phon = phone.getText().toString();
		email = username.getText().toString();
		pass = password.getText().toString();
		confirm_pass = pass_confirm.getText().toString();
		// project_str = project.getText().toString();
		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w-_]+\\.)+[\\w]+[\\w]$";

		boolean b = email.matches(EMAIL_REGEX);
		boolean valFlag;
		if (b) {

			if (phon.length() <= 13 && phon.length() >= 10) {

				if (fname.trim().length() > 0) {
					if (lname.trim().length() > 0) {
						if (pass.equals(confirm_pass)) {
							valFlag = true;
							return valFlag;
						} else {
							Toast.makeText(getApplicationContext(),
									"Password mismatch", Toast.LENGTH_LONG)
									.show();
						}

					} else {
						Toast.makeText(getApplicationContext(),
								"Last name cannot be empty", Toast.LENGTH_LONG)
								.show();
					}

				} else {
					Toast.makeText(getApplicationContext(),
							"First name cannot be empty", Toast.LENGTH_LONG)
							.show();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Enter valid mobile nomber", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Enter valid email address", Toast.LENGTH_LONG).show();
		}
		valFlag = false;
		return valFlag;
	}
}

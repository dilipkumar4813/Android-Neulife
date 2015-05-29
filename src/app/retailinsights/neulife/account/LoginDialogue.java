package app.retailinsights.neulife.account;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;

public class LoginDialogue extends Dialog implements
		android.view.View.OnClickListener {

	ProgressDialog pd;
	private static final String METHOD = "authenticate";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/authenticate";
	public int status;

	// Webservice variables to get the profile information
	private static final String METHOD2 = "getProfile";
	private final String SOAP_ACTION2 = SessionStorage.webserviceNamespace
			+ "service.php/getProfile";

	public Activity c;
	public Dialog d;
	public Button yes, no;
	Context cntxt;
	public String email = "", pass = "";
	public EditText username, password;
	public TextView tvForgotPassword;
	public CheckBox chkPassword;

	public ImageView ivClose;

	String IMEI = "";

	public LoginDialogue(Activity a) {
		super(a);
		// TODO Auto-generated constructor stub
		this.c = a;
		cntxt = this.c;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_dialogue);
		initialize();

		// Getting IMEI id
		TelephonyManager tMgr = (TelephonyManager) cntxt
				.getSystemService(Context.TELEPHONY_SERVICE);
		IMEI = tMgr.getDeviceId();
	}

	public void initialize() {
		yes = (Button) findViewById(R.id.btnLogin);
		username = (EditText) findViewById(R.id.etUsername);
		password = (EditText) findViewById(R.id.etPassword);
		tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
		chkPassword = (CheckBox) findViewById(R.id.chkPassword);
		ivClose = (ImageView) findViewById(R.id.ivClose);
		ivClose.setOnClickListener(this);
		tvForgotPassword.setOnClickListener(this);
		yes.setOnClickListener(this);
		chkPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				// password.setInputType(InputType.TYPE_CLASS_TEXT);
				if (chkPassword.isChecked()) {
					password.setTransformationMethod(null);
					// password.setInputType(InputType.TYPE_CLASS_TEXT);
				} else {
					password.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
				}
			}

		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			email = username.getText().toString();
			pass = password.getText().toString();

			if ((!email.isEmpty()) && (!pass.isEmpty())) {
				AsyncCallWS some = new AsyncCallWS();
				some.execute();
			} else {
				Toast.makeText(this.c, "Please enter username/password",
						Toast.LENGTH_SHORT).show();
			}

			// c.startActivity(new Intent(c, TakePhotoFragmentActivity.class));
			break;
		case R.id.tvForgotPassword:
			this.dismiss();

			DisplayMetrics metrics = this.c.getResources().getDisplayMetrics();

			ForgotPassword add = new ForgotPassword(this.c);
			add.show();

			int wid = metrics.widthPixels / 2 + 200;
			int heig = metrics.heightPixels / 2;
			add.getWindow().setLayout(wid, heig);
			break;
		case R.id.ivClose:
			this.dismiss();
			break;
		default:
			break;
		}
		dismiss();
	}

	public void changeScreen() {
		Intent intent = new Intent(cntxt, LandingScreen.class);
		cntxt.startActivity(intent);
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

	private class AsyncCallWS extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			if (status == 0) {
				Toast.makeText(cntxt, "Incorrect Username/Password",
						Toast.LENGTH_LONG).show();
			} else {
				SessionStorage.customerType = "1";
				SessionStorage.clientId = "1";
				SessionStorage.username = email;
				Toast.makeText(cntxt, "Login Successfull", Toast.LENGTH_LONG)
						.show();

				// Get profile asynchronus call
				AsyncCallWSGetProfile obj = new AsyncCallWSGetProfile();
				obj.execute();

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

	// Get profile asynchronous class and methods
	public void getProfile() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD2);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("username", email);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION2, envelope);
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

	private class AsyncCallWSGetProfile extends AsyncTask<String, Void, Void> {

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
				getProfile();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}
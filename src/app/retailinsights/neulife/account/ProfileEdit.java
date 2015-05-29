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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.InternetConnection;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class ProfileEdit extends Activity {

	ProgressDialog pd;
	ImageView ivBack;
	Button btnSave;
	EditText firstName, lastName, email;
	private RadioGroup radioSexGroup;
	private RadioButton radioSexButton;
	String fName, lName,sex;

	// Webservice for updating the profile
	private static final String METHOD2 = "updateProfile";
	private final String SOAP_ACTION2 = SessionStorage.webserviceNamespace
			+ "service.php/updateProfile";
	public int status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.profile_edit);

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(ProfileEdit.this);
			add.show();

			int wid = metrics.widthPixels / 2 + 180;
			int heig = metrics.heightPixels / 3;
			add.getWindow().setLayout(wid, heig);
		}

		initialize();
		basicEvents();

		// Footer Related Methods
		footerInitialize();
		footerEvents();
	}

	private void initialize() {
		ivBack = (ImageView) findViewById(R.id.imageView1);
		btnSave = (Button) findViewById(R.id.buttonSave);
		firstName = (EditText) findViewById(R.id.etfirstName);
		lastName = (EditText) findViewById(R.id.etlastName);
		email = (EditText) findViewById(R.id.etEmail);
		radioSexGroup = (RadioGroup) findViewById(R.id.rbgGender);

		firstName.setText(SessionStorage.fName);
		lastName.setText(SessionStorage.lName);
		email.setText(SessionStorage.email);
	}

	private void basicEvents() {
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fName = firstName.getText().toString();
				lName = lastName.getText().toString();
				SessionStorage.fName = fName;
				SessionStorage.lName = lName;
				
				// get selected radio button from radioGroup
				int selectedId = radioSexGroup.getCheckedRadioButtonId();
				// find the radiobutton by returned id
				radioSexButton = (RadioButton) findViewById(selectedId);
				sex = radioSexButton.getText().toString();

				AsyncCallWSUpdateProfile obj = new AsyncCallWSUpdateProfile();
				obj.execute();
			}
		});

		ImageView ivNotifications = (ImageView) findViewById(R.id.ivNotifications);
		ivNotifications.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ProfileEdit.this,
						Notifications.class);
				startActivity(intent);
			}
		});

		ivBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	// Webservice for updating the profile information
	public void updateProfile() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD2);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("userId", SessionStorage.userId);
		request.addProperty("firstName", fName);
		request.addProperty("lastName", lName);
		request.addProperty("phone", SessionStorage.phone);
		request.addProperty("sex", sex);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION2, envelope);
			Object response = envelope.getResponse();

			Log.d("Update Profile Details", response.toString());

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

	private class AsyncCallWSUpdateProfile extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(ProfileEdit.this);
			pd.setMessage("Loading...");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();

			if (status == 0) {
				Toast.makeText(getApplicationContext(),
						"Oops something went wrong!", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Update Succesfull",
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				updateProfile();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	// Footer related methods
	ImageView accountBtn, wishlistImg, cartImg, searchImg, homeImg;

	public void footerInitialize() {
		searchImg = (ImageView) findViewById(R.id.searchImg);
		cartImg = (ImageView) findViewById(R.id.cartImg);
		accountBtn = (ImageView) findViewById(R.id.accountImg);
		wishlistImg = (ImageView) findViewById(R.id.wishlistImg);
		homeImg = (ImageView) findViewById(R.id.homeImg);
	}

	public void footerEvents() {

		homeImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						LandingScreen.class);
				startActivity(inten1);

			}
		});

		cartImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(), Cart.class);
				startActivity(inten1);
			}
		});

		searchImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SessionStorage.search = 1;
				Intent inten1 = new Intent(getApplicationContext(),
						LandingScreen.class);
				startActivity(inten1);
			}
		});

		accountBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent inten1 = new Intent(getApplicationContext(),
						ManageAccount.class);
				startActivity(inten1);

			}
		});

		wishlistImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (SessionStorage.clientId.isEmpty()) {
					Intent inten1 = new Intent(getApplicationContext(),
							LoginMain.class);
					startActivity(inten1);
				} else {
					Intent inten1 = new Intent(getApplicationContext(),
							WishList.class);
					startActivity(inten1);
				}
			}
		});
	}

}

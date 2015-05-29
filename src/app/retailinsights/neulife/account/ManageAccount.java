package app.retailinsights.neulife.account;

import java.io.File;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.InternetConnection;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class ManageAccount extends Activity implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener {

	GoogleApiClient mGoogleApiClient;
	boolean mSignInClicked;

	// Webservice variables
	ProgressDialog pd;
	private static final String METHOD = "getProfile";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/getProfile";
	String firstName, lastName, username, phone;
	TextView tvFullName;

	RelativeLayout rl_profile_header, rl_view_order, rl_notification_settings,
			rl_help_and_faqs;
	Button btnSignOut;
	EditText searchTxt;
	ImageView ivNotifications, imageView1;
	ImageView categories, offersBtn, topRatedBtn, newArrivalBtn, bestsellerBtn,
			recommendBtn, searchIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.manage_account);
		// getActionBar().hide();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API)
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(ManageAccount.this);
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

		AsyncCallWS obj = new AsyncCallWS();
		obj.execute();
	}

	private void initialize() {
		rl_profile_header = (RelativeLayout) findViewById(R.id.rl_profile_header);
		rl_view_order = (RelativeLayout) findViewById(R.id.rl_view_order);
		rl_notification_settings = (RelativeLayout) findViewById(R.id.rl_notification_settings);
		rl_help_and_faqs = (RelativeLayout) findViewById(R.id.rl_help_and_faqs);
		btnSignOut = (Button) findViewById(R.id.btnSignOut);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		searchTxt = (EditText) findViewById(R.id.searchTxt);
		tvFullName = (TextView) findViewById(R.id.tvFullName);
	}

	private void basicEvents() {
		rl_profile_header.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ManageAccount.this,
						ManageAccountProfile.class);
				startActivity(intent);
			}
		});
		rl_view_order.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ManageAccount.this,
						OrderTracking.class);
				startActivity(intent);
			}
		});
		rl_notification_settings.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ManageAccount.this,
						NotificationSetting.class);
				startActivity(intent);
			}
		});
		rl_help_and_faqs.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ManageAccount.this,
						HelpsAndFAqs.class);
				startActivity(intent);
			}
		});
		btnSignOut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SessionStorage.clientId = "0";
				SessionStorage.fName = "";
				SessionStorage.lName = "";
				SessionStorage.email = "";
				SessionStorage.phone = "";
				SessionStorage.userId = "0";
				SessionStorage.username = "";
				SessionStorage.customerType = "";

				if (mGoogleApiClient.isConnected()) {
					Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
					mGoogleApiClient.disconnect();
					mGoogleApiClient.connect();
					// updateUI(false);
					System.err.println("LOG OUT ^^^^^^^^^^^^^^^^^^^^ SUCESS");
				}

				// Delete the cache
				deleteCache(getApplicationContext());

				Intent intent = new Intent(ManageAccount.this, LoginMain.class);
				startActivity(intent);

				Toast.makeText(getApplicationContext(), "sign out successful",
						Toast.LENGTH_SHORT).show();
			}
		});

		ivNotifications = (ImageView) findViewById(R.id.ivNotifications);
		ivNotifications.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ManageAccount.this,
						Notifications.class);
				startActivity(intent);
			}
		});

		imageView1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ManageAccount.this,
						LandingScreen.class);
				startActivity(intent);
			}
		});

	}

	// Function to clear the cache
	public static void deleteCache(Context context) {
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
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

	public void printProfile() {
		tvFullName.setText(firstName + " " + lastName);
		SessionStorage.username = username;
		SessionStorage.email = username;
		SessionStorage.fName = firstName;
		SessionStorage.lName = lastName;
		SessionStorage.phone = phone;
	}

	public void getProfile() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("username", SessionStorage.username);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("Profile Details", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("profile");

			// Reading through the array of products
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = jsonarray.getJSONObject(i);

				SessionStorage.userId = object.getString("id");
				firstName = object.getString("firstName");
				lastName = object.getString("lastName");
				username = object.getString("username");
				phone = object.getString("phone");
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

	private class AsyncCallWS extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(ManageAccount.this);
			pd.setMessage("Loading...");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();

			if (username != null) {
				printProfile();
			}
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

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		mSignInClicked = false;

		// updateUI(true);
		Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(
				(ResultCallback<LoadPeopleResult>) this);
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		mGoogleApiClient.connect();
		// updateUI(false);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}

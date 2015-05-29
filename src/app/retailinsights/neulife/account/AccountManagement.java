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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.InternetConnection;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.productlisting.Search;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class AccountManagement extends Activity {

	// Webservice variables
	ProgressDialog pd;
	private static final String METHOD = "getProfile";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/getProfile";

	// Webservice for updating the profile
	private static final String METHOD2 = "updateProfile";
	private final String SOAP_ACTION2 = SessionStorage.webserviceNamespace
			+ "service.php/updateProfile";
	public int status;

	// Webservice for updating the password
	private static final String METHOD3 = "updatePassword";
	private final String SOAP_ACTION3 = SessionStorage.webserviceNamespace
			+ "service.php/updatePassword";

	// Webservice for updating the password
	private static final String METHOD4 = "updateAddress";
	private final String SOAP_ACTION4 = SessionStorage.webserviceNamespace
			+ "service.php/updateAddress";

	LinearLayout orderTracking;

	// Edit Texts for updating the account details
	String firstName, lastName, phone, username;
	String address1, address2, city, country, phoneaddress;
	String password, confirmPassword;
	EditText etFirstName, etLastName, etPhone, etUserName;
	EditText etAddress1, etAddress2, etCity, etCountry, etPhoneAddress;
	EditText etPassword, etConfirmPassword;
	RelativeLayout updateAddress, updatePassword, updateAccount;

	// Expand and Collapse
	ScrollView svDescription, svSupplement, svIngrediant;
	ImageView ivPlusDesc, ivMinusDesc, ivPlusSupplement, ivMinusSupplement,
			ivPlusIngrediants, ivMinusIngrediants;
	boolean flagDesc = true;
	boolean flagIngredint = false;
	boolean flagSuppliment = false;
	boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_management);

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(AccountManagement.this);
			add.show();

			int wid = metrics.widthPixels / 2 + 180;
			int heig = metrics.heightPixels / 3;
			add.getWindow().setLayout(wid, heig);
		}

		initialize();

		basicEvents();

		expandCollapse();

		updateButtons();

		// Footer Related Methods
		footerInitialize();
		footerEvents();

		AsyncCallWS some = new AsyncCallWS();
		some.execute();
	}

	public void initialize() {
		// Scrollview containers
		svDescription = (ScrollView) findViewById(R.id.svdescription);
		svSupplement = (ScrollView) findViewById(R.id.svsuppliment);
		svIngrediant = (ScrollView) findViewById(R.id.svingrediants);

		// Heading
		ivPlusDesc = (ImageView) findViewById(R.id.ivPlusDesc);
		ivPlusSupplement = (ImageView) findViewById(R.id.ivPlusSuppliment);
		ivPlusIngrediants = (ImageView) findViewById(R.id.ivPlusIngrediants);

		// Extracting the details stored in the edit texts
		etFirstName = (EditText) findViewById(R.id.etFirstName);
		etLastName = (EditText) findViewById(R.id.etLastName);
		etPhone = (EditText) findViewById(R.id.etPhone);
		etUserName = (EditText) findViewById(R.id.etUserName);
		etAddress1 = (EditText) findViewById(R.id.etAddress1);
		etAddress2 = (EditText) findViewById(R.id.etAddress2);
		etCity = (EditText) findViewById(R.id.etCity);
		etCountry = (EditText) findViewById(R.id.etCountry);
		etPhoneAddress = (EditText) findViewById(R.id.etPhoneAddress);
		etPassword = (EditText) findViewById(R.id.etPassword);
		etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);

		// Update Buttons
		updateAddress = (RelativeLayout) findViewById(R.id.updateAddress);
		updatePassword = (RelativeLayout) findViewById(R.id.updatePassword);
		updateAccount = (RelativeLayout) findViewById(R.id.updateAccount);

		// Order Tracking
		orderTracking = (LinearLayout) findViewById(R.id.ll_desc_main4);
	}

	public void basicEvents() {

		orderTracking.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						OrderTracking.class);
				startActivity(inten1);
			}
		});
	}

	// Expand and collapse methods
	public void expandCollapse() {
		ivPlusDesc.setImageResource(R.drawable.plus_img);
		ivPlusIngrediants.setImageResource(R.drawable.plus_img);
		ivPlusSupplement.setImageResource(R.drawable.plus_img);

		if (flagDesc == true) {
			svDescription.setVisibility(View.VISIBLE);
		}
		ivPlusDesc.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flagDesc == true) {
					svDescription.setVisibility(View.VISIBLE);
					flagDesc = false;
					svIngrediant.setVisibility(View.GONE);
					svSupplement.setVisibility(View.GONE);
					ivPlusDesc.setImageResource(R.drawable.minus_img);
				} else {
					svDescription.setVisibility(View.GONE);
					flagDesc = true;
					ivPlusDesc.setImageResource(R.drawable.plus_img);
				}

			}
		});
		ivPlusIngrediants.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flagIngredint == true) {
					svIngrediant.setVisibility(View.VISIBLE);
					flagIngredint = false;
					svDescription.setVisibility(View.GONE);
					svSupplement.setVisibility(View.GONE);
					ivPlusIngrediants.setImageResource(R.drawable.minus_img);
				} else {
					svIngrediant.setVisibility(View.GONE);
					flagIngredint = true;
					ivPlusIngrediants.setImageResource(R.drawable.plus_img);
				}
			}
		});
		ivPlusSupplement.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flagSuppliment == true) {
					svSupplement.setVisibility(View.VISIBLE);
					flagSuppliment = false;
					svIngrediant.setVisibility(View.GONE);
					svDescription.setVisibility(View.GONE);
					ivPlusSupplement.setImageResource(R.drawable.minus_img);
				} else {
					svSupplement.setVisibility(View.GONE);
					flagSuppliment = true;
					ivPlusSupplement.setImageResource(R.drawable.plus_img);
				}
			}
		});
	}

	// Extract the information before updating the required details
	public void updateButtons() {

		updateAddress.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				address1 = etAddress1.getText().toString();
				address2 = etAddress2.getText().toString();
				city = etCity.getText().toString();
				country = etCountry.getText().toString();
				phoneaddress = etPhoneAddress.getText().toString();

				Toast.makeText(getApplicationContext(), city,
						Toast.LENGTH_SHORT).show();
			}
		});

		updatePassword.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				password = etPassword.getText().toString();
				confirmPassword = etConfirmPassword.getText().toString();

				AsyncCallWSUpdatePassword obj = new AsyncCallWSUpdatePassword();
				obj.execute();
			}
		});

		updateAccount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				firstName = etFirstName.getText().toString();
				lastName = etLastName.getText().toString();
				phone = etPhone.getText().toString();
				username = etUserName.getText().toString();

				AsyncCallWSUpdateProfile obj = new AsyncCallWSUpdateProfile();
				obj.execute();
			}
		});
	}

	public void printProfile() {
		etUserName.setText(username);
		etPhone.setText(phone);
		etFirstName.setText(firstName);
		etLastName.setText(lastName);
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
			pd = new ProgressDialog(AccountManagement.this);
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

	// Webservice for updating the profile information
	public void updateProfile() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD2);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("userId", SessionStorage.userId);
		request.addProperty("firstName", firstName);
		request.addProperty("lastName", lastName);
		request.addProperty("phone", phone);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION2, envelope);
			Object response = envelope.getResponse();

			Log.d("Update Profile Details", response.toString());
			JSONObject json = new JSONObject(response.toString());

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
			pd = new ProgressDialog(AccountManagement.this);
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

				AsyncCallWS obj = new AsyncCallWS();
				obj.execute();
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

	// Webservice for updating the password
	public void updatePassword() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD3);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("userId", SessionStorage.userId);
		request.addProperty("password", password);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION3, envelope);
			Object response = envelope.getResponse();

			Log.d("Update Password Details", response.toString());
			JSONObject json = new JSONObject(response.toString());

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

	private class AsyncCallWSUpdatePassword extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(AccountManagement.this);
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
				updatePassword();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	// Webservice for updating the password
	public void updateAddress() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD4);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("userId", SessionStorage.userId);
		request.addProperty("password", password);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION4, envelope);
			Object response = envelope.getResponse();

			Log.d("Update Password Details", response.toString());

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

	private class AsyncCallWSUpdateAddress extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(AccountManagement.this);
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
				updateAddress();

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

				if (SessionStorage.clientId.equalsIgnoreCase("0")) {
					Toast.makeText(getApplicationContext(), "Please Login",
							Toast.LENGTH_SHORT).show();

					Intent inten1 = new Intent(getApplicationContext(),
							LoginMain.class);
					startActivity(inten1);
				} else {

					Intent inten1 = new Intent(getApplicationContext(),
							AccountManagement.class);
					startActivity(inten1);
				}

			}
		});

		wishlistImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (SessionStorage.clientId.equalsIgnoreCase("0")) {
					Toast.makeText(getApplicationContext(), "Please Login",
							Toast.LENGTH_SHORT).show();

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

package app.retailinsights.neulife.account;

import java.io.IOException;
import java.util.ArrayList;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.InternetConnection;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.adapters.AddressAdapter;
import app.retailinsights.neulife.adapters.ProductListingListviewAdapter;
import app.retailinsights.neulife.bean.BeanAddressListing;
import app.retailinsights.neulife.bean.BeanProductListing;
import app.retailinsights.neulife.productlisting.BestSellers;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class DeliveryAddress extends Activity {

	ImageView ivArraow5, backArrow;
	Button btnSave;

	// Webservice variables
	ProgressDialog pd;
	private static final String METHOD = "getAddress";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/getAddress";

	ArrayList<BeanAddressListing> alAddressListing;
	BeanAddressListing beanAddressListing;
	ListView listView;
	int addressCount = 0;

	RelativeLayout rl_delivery_address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.delivery_address);
		// getActionBar().hide();

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(
					DeliveryAddress.this);
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
	
	public void reloadAddresses(){
		AsyncCallWS obj = new AsyncCallWS();
		obj.execute();
	}

	private void initialize() {
		backArrow = (ImageView) findViewById(R.id.imageView1);
		listView = (ListView) findViewById(R.id.listView1);
		rl_delivery_address = (RelativeLayout) findViewById(R.id.rl_delivery_address);
	}

	private void basicEvents() {

		backArrow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		rl_delivery_address.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SessionStorage.addressId = "0";
				Intent intent = new Intent(getApplicationContext(),
						DeliveryAddressEdit.class);
				startActivity(intent);
			}
		});

		ImageView ivNotifications = (ImageView) findViewById(R.id.ivNotifications);
		ivNotifications.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DeliveryAddress.this,
						Notifications.class);
				startActivity(intent);
			}
		});

	}

	public void getAddress() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("userId", SessionStorage.userId);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("address Details", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("address");

			alAddressListing = new ArrayList<BeanAddressListing>();
			// Reading through the array of products
			for (int i = 0; i < jsonarray.length(); i++) {
				beanAddressListing = new BeanAddressListing();
				JSONObject object = jsonarray.getJSONObject(i);

				beanAddressListing.setAddressId(object.getString("id"));
				beanAddressListing
						.setAddressLine1(object.getString("address1"));
				beanAddressListing.setAddressLine2(object.getString("address2")
						+ " " + object.getString("address3"));
				beanAddressListing.setCity(object.getString("city"));
				// state = object.getString("state");
				beanAddressListing.setPhone(object.getString("phone"));
				beanAddressListing.setFullName(object.getString("firstName")
						+ " " + object.getString("lastName"));
				beanAddressListing.setPincode(object.getString("pincode"));

				addressCount++;
				alAddressListing.add(beanAddressListing);
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
			pd = new ProgressDialog(DeliveryAddress.this);
			pd.setMessage("Loading...");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();

			Log.d("Address Listing", "" + alAddressListing);

			// Disable the add new address Button
			if (addressCount == 3) {
				rl_delivery_address.setVisibility(View.GONE);
			}

			if (alAddressListing == null) {
				Toast.makeText(getApplicationContext(),
						"No Address, Please add an Address", Toast.LENGTH_SHORT)
						.show();
			} else {
				listView.setAdapter(new AddressAdapter(DeliveryAddress.this,
						alAddressListing));
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				getAddress();

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

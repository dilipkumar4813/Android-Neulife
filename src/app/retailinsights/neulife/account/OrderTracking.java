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
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.InternetConnection;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.adapters.AddressAdapter;
import app.retailinsights.neulife.adapters.OrdersAdapter;
import app.retailinsights.neulife.bean.BeanAddressListing;
import app.retailinsights.neulife.bean.BeanOrderListing;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class OrderTracking extends Activity {

	// Webservice variables
	ProgressDialog pd;
	private static final String METHOD = "getOrders";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/getOrders";

	ArrayList<BeanOrderListing> alOrderListing;
	BeanOrderListing beanOrderListing;
	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.order_tracking);

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(OrderTracking.this);
			add.show();

			int wid = metrics.widthPixels / 2 + 180;
			int heig = metrics.heightPixels / 3;
			add.getWindow().setLayout(wid, heig);
		}

		// Footer Related Methods
		footerInitialize();
		footerEvents();

		basicEvents();

		alOrderListing = new ArrayList<BeanOrderListing>();
		// Reading through the array of products
		for (int i = 0; i < 3; i++) {
			beanOrderListing = new BeanOrderListing();

			beanOrderListing.setOrderId(""+i);
			beanOrderListing.setOrderTitle("Some Title");
			beanOrderListing.setOrderDate("28-05-2015");
			beanOrderListing.setDeliveryDate("1-06-2015");
			beanOrderListing.setStatus("Shipped");
			beanOrderListing.setImage("http://retailinsights.com/images/retail-logo.com");

			alOrderListing.add(beanOrderListing);
		}
		
		listView.setAdapter(new OrdersAdapter(OrderTracking.this,
				alOrderListing));
		
		/*AsyncCallWS obj = new AsyncCallWS();
		obj.execute();*/

	}

	private void basicEvents() {
		ImageView ivNotifications = (ImageView) findViewById(R.id.ivNotification);
		ivNotifications.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(OrderTracking.this,
						Notifications.class);
				startActivity(intent);
			}
		});
	}

	// Footer related methods
	ImageView accountBtn, wishlistImg, cartImg, searchImg, homeImg;

	public void footerInitialize() {
		searchImg = (ImageView) findViewById(R.id.searchImg);
		cartImg = (ImageView) findViewById(R.id.cartImg);
		accountBtn = (ImageView) findViewById(R.id.accountImg);
		wishlistImg = (ImageView) findViewById(R.id.wishlistImg);
		homeImg = (ImageView) findViewById(R.id.homeImg);
		listView = (ListView) findViewById(R.id.listView1);
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
							ManageAccount.class);
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

	public void getOrders() throws JSONException {

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

			Log.d("Order Details", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("viewOrders");

			alOrderListing = new ArrayList<BeanOrderListing>();
			// Reading through the array of products
			for (int i = 0; i < jsonarray.length(); i++) {
				beanOrderListing = new BeanOrderListing();
				JSONObject object = jsonarray.getJSONObject(i);

				beanOrderListing.setOrderId(object.getString("id"));
				beanOrderListing.setOrderTitle(object.getString("title"));
				beanOrderListing.setOrderDate(object.getString("orderDate"));
				beanOrderListing.setDeliveryDate(object.getString("deliveryDate"));
				beanOrderListing.setStatus(object.getString("status"));
				beanOrderListing.setImage(object.getString("img"));

				alOrderListing.add(beanOrderListing);
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
			pd = new ProgressDialog(OrderTracking.this);
			pd.setMessage("Loading...");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();

			Log.d("Order Listing", "" + alOrderListing);

			if (alOrderListing == null) {
				Toast.makeText(getApplicationContext(),
						"No Address, Please add an Address", Toast.LENGTH_SHORT)
						.show();
			} else {
				listView.setAdapter(new OrdersAdapter(OrderTracking.this,
						alOrderListing));
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				getOrders();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}
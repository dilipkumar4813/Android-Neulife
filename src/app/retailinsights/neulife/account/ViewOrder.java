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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;

public class ViewOrder extends Activity {

	TextView tvDeliveryAddress, tvShippingAddress, tvStatus, tvOrderId,
			tvOrderDate, tvOrderDelivered, tvAmountDetails, tvTotal;
	ImageView backBtn, ivNotification;
	String quantity, deliveryCharge, savings, deliveryAddress, shippingAddress,
			status, orderDate, orderDelivered, amountDetails, total;

	// Webservice variables
	ProgressDialog pd;
	private static final String METHOD = "viewOrder";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/viewOrder";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_order);

		initialize();
		basicEvents();

		footerInitialize();
		footerEvents();

		/*
		 * AsyncCallWS obj = new AsyncCallWS(); obj.execute();
		 */
	}

	public void initialize() {
		tvDeliveryAddress = (TextView) findViewById(R.id.tvDeliveryAddress);
		tvShippingAddress = (TextView) findViewById(R.id.tvShippingAddress);
		tvStatus = (TextView) findViewById(R.id.tvStatus);
		tvOrderId = (TextView) findViewById(R.id.tvOrderId);
		tvOrderDate = (TextView) findViewById(R.id.tvOrderDate);
		tvOrderDelivered = (TextView) findViewById(R.id.tvOrderDelivered);
		tvAmountDetails = (TextView) findViewById(R.id.tvAmountDetails);
		tvTotal = (TextView) findViewById(R.id.tvTotal);
		backBtn = (ImageView) findViewById(R.id.imageView1);
		ivNotification = (ImageView) findViewById(R.id.ivNotification);
	}

	public void basicEvents() {
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		ivNotification.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ViewOrder.this, Notifications.class);
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

	public void printDetails() {
		tvDeliveryAddress.setText(deliveryAddress);
		tvShippingAddress.setText(shippingAddress);
		tvStatus.setText(status);
		tvOrderId.setText(SessionStorage.orderId);
		tvOrderDate.setText(orderDate);
		tvOrderDelivered.setText(orderDelivered);

		amountDetails = "Quantity : " + quantity + "\n Savings : " + savings
				+ "\nDelivery Charge : " + deliveryCharge;

		tvAmountDetails.setText(amountDetails);
		tvTotal.setText(total);
	}

	public void getOrder() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("orderId", SessionStorage.orderId);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("order Details", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("order");

			// Reading through the array of products
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = jsonarray.getJSONObject(i);

				quantity = object.getString("quantity");
				deliveryCharge = object.getString("deliveryCharge");
				savings = object.getString("savings");
				deliveryAddress = object.getString("deliveryAddress");
				shippingAddress = object.getString("shippingAddress");
				status = object.getString("status");
				orderDate = object.getString("orderDate");
				orderDelivered = object.getString("orderDelivered");
				total = object.getString("total");

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
			pd = new ProgressDialog(ViewOrder.this);
			pd.setMessage("Loading...");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();

			printDetails();
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				getOrder();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}

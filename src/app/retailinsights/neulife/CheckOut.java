package app.retailinsights.neulife;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import app.retailinsights.neulife.account.LoginMain;
import app.retailinsights.neulife.account.ManageAccount;
import app.retailinsights.neulife.account.ProfileEdit;
import app.retailinsights.neulife.account.WishList;
import app.retailinsights.neulife.adapters.AddToCartAdapter;
import app.retailinsights.neulife.bean.BeanProductListingLV;
import app.retailinsights.neulife.database.DatabaseHandler;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class CheckOut extends Activity {
	// Webservice variables
	ProgressDialog pd;
	private static final String METHOD = "getProfile";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/getProfile";

	private static final String METHOD3 = "getCart";
	private final String SOAP_ACTION3 = SessionStorage.webserviceNamespace
			+ "service.php/getCart";

	ArrayList<BeanProductListingLV> alProductListing;
	BeanProductListingLV beanProductListing;

	// Webservice for updating the profile
	private static final String METHOD2 = "getAddress";
	private final String SOAP_ACTION2 = SessionStorage.webserviceNamespace
			+ "service.php/getAddress";

	// Webservice for updating the profile
	private static final String METHOD4 = "insertOrderHeader";
	private final String SOAP_ACTION4 = SessionStorage.webserviceNamespace
			+ "service.php/insertOrderHeader";

	ImageView iv_order_ticked1, iv_order_ticked2, iv_order_ticked3,
			iv_order_ticked4;
	Button btnNextPayment, btnNextAddress, btnNextEmail, btnNextSummary;
	LinearLayout ll_payment, ll_address, ll_email, ll_summary;

	EditText etusername, etLandMark, etcity, etaddress1, etaddress2, etpincode,
			etphone, etname;
	String username, state, city, addressLine1, addressLine2, pincode, phone,
			name, addressId, fName, lName, landmark;
	TextView Summary, tvTotal;
	ScrollView svPayment;
	Integer[] prodID;
	Integer[] qty;

	String orders = "{\"products\":[";
	int status = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.check_out);

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();
			InternetConnection add = new InternetConnection(CheckOut.this);
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
		// get cart product id and qty
		DatabaseHandler handler = new DatabaseHandler(CheckOut.this);
		HashMap<Integer, Integer> hmCartIdAndQty = handler.getCartDetails();
		Log.w("hmCartIdAndQty.size: ", "" + hmCartIdAndQty.size());
		Set mapSet = (Set) hmCartIdAndQty.entrySet();
		// Create iterator on Set
		Iterator mapIterator = mapSet.iterator();
		System.out.println("Display the key/value of HashMap.");
		int i = 0;
		prodID = new Integer[hmCartIdAndQty.size()];
		qty = new Integer[hmCartIdAndQty.size()];
		while (mapIterator.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) mapIterator.next();
			// getKey Method of HashMap access a key of map
			prodID[i] = (Integer) mapEntry.getKey();
			// getValue method returns corresponding key's value
			qty[i] = (Integer) mapEntry.getValue();
			Log.w("prodID : " + prodID[i], "= qty : " + qty[i]);
			i++;
		}

		AsyncCallWS obj = new AsyncCallWS();
		obj.execute();
		
	   /*AsyncCallWSGetCart asyncCallWSGetCart = new AsyncCallWSGetCart();
		asyncCallWSGetCart.execute();*/
		
		tvTotal.setText(SessionStorage.cartSubTotal + "/-");
	}

	private void initialize() {
		svPayment = (ScrollView) findViewById(R.id.svPayment);
		iv_order_ticked1 = (ImageView) findViewById(R.id.iv_order_ticked1);
		iv_order_ticked2 = (ImageView) findViewById(R.id.iv_order_ticked2);
		iv_order_ticked3 = (ImageView) findViewById(R.id.iv_order_ticked3);
		iv_order_ticked4 = (ImageView) findViewById(R.id.iv_order_ticked4);
		btnNextPayment = (Button) findViewById(R.id.next_payment);
		btnNextSummary = (Button) findViewById(R.id.next_summary);
		btnNextEmail = (Button) findViewById(R.id.next_email);
		btnNextAddress = (Button) findViewById(R.id.next_address);
		ll_address = (LinearLayout) findViewById(R.id.ll_address);
		ll_payment = (LinearLayout) findViewById(R.id.ll_paymentmode);
		ll_email = (LinearLayout) findViewById(R.id.ll_email);
		ll_summary = (LinearLayout) findViewById(R.id.ll_summary);

		etname = (EditText) findViewById(R.id.etname);
		etusername = (EditText) findViewById(R.id.etUsername);
		etcity = (EditText) findViewById(R.id.etCity);
		etaddress1 = (EditText) findViewById(R.id.etAddress1);
		etaddress2 = (EditText) findViewById(R.id.etAddress2);
		etLandMark = (EditText) findViewById(R.id.etLandmark);
		etpincode = (EditText) findViewById(R.id.etPincode);
		etphone = (EditText) findViewById(R.id.etPhone);

		Summary = (TextView) findViewById(R.id.summary);
		tvTotal = (TextView) findViewById(R.id.tvTotal);
	}

	public void printCheckoutScreen() {
		etusername.setText(SessionStorage.username);
		etname.setText(name);

		if (city != null && !city.isEmpty()) {
			etcity.setText(city);
			etaddress1.setText(addressLine1);
			etaddress2.setText(addressLine2);
			etLandMark.setText(landmark);
			etphone.setText(phone);
		}
		else
		{
			//Toast.makeText(CheckOut.this, "City is empty", Toast.LENGTH_SHORT).show();
		}
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
				name = object.getString("firstName") + " "
						+ object.getString("lastName");
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

		//getAddress();
	}

	public void getAddress() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD2);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("userId", SessionStorage.userId);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION2, envelope);
			Object response = envelope.getResponse();

			Log.d("address Details", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("address");

			// Reading through the array of products
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = jsonarray.getJSONObject(i);

				addressId = object.getString("id");
				addressLine1 = object.getString("address1");
				addressLine2 = object.getString("address2");
				landmark = object.getString("address3");
				city = object.getString("city");
				state = object.getString("state");
				phone = object.getString("phone");
				fName = object.getString("firstName");
				lName = object.getString("lastName");
				pincode = object.getString("pincode");
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
		getCart();
	}

	private class AsyncCallWS extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(CheckOut.this);
			pd.setMessage("Loading... (get profile)");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();

			if (username != null) {
				printCheckoutScreen();
			}
			AsyncCallWSGetCart asyncCallWSGetCart = new AsyncCallWSGetCart();
			asyncCallWSGetCart.execute();
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

	public void basicEvents() {
		btnNextEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_order_ticked1.setVisibility(View.VISIBLE);
				ll_email.setVisibility(View.GONE);
				ll_address.setVisibility(View.VISIBLE);

				SessionStorage.oEmail = etusername.getText().toString();
			}
		});

		btnNextAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_order_ticked2.setVisibility(View.VISIBLE);
				ll_address.setVisibility(View.GONE);
				ll_summary.setVisibility(View.VISIBLE);

				SessionStorage.oName = name;
				//SessionStorage.oPincode = pincode;
				SessionStorage.oPincode = "560036";
				//SessionStorage.oaddress1 = addressLine1;
				SessionStorage.oaddress1 = "Kasturinagar";
				SessionStorage.oaddress2 = "Banasawadi";
				//SessionStorage.oaddress2 = addressLine2;
				//SessionStorage.oaddress3 = landmark;
				SessionStorage.oaddress3 = "Near Pizzza hut";
				SessionStorage.oPhone = phone;
				//SessionStorage.ocity = city;
				SessionStorage.ocity = "Bangalore";

				Summary.setText("Username: " + SessionStorage.oEmail + "\n\n"
						+ SessionStorage.oName + "\n" + SessionStorage.oPhone
						+ "\n" + SessionStorage.oaddress1 + "\n"
						+ SessionStorage.oaddress2 + "\n"
						+ SessionStorage.oaddress3 + "\n"
						+ SessionStorage.ocity + " " + SessionStorage.oPincode);
			}
		});

		btnNextSummary.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_order_ticked3.setVisibility(View.VISIBLE);
				ll_summary.setVisibility(View.GONE);
				svPayment.setVisibility(View.VISIBLE);
			}
		});

		btnNextPayment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_order_ticked4.setVisibility(View.VISIBLE);
				svPayment.setVisibility(View.GONE);
				
				AsyncCallWSPlaceOrder obj = new AsyncCallWSPlaceOrder();
				obj.execute();
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

	public void getCart() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD3);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("pids", SessionStorage.cid);
		Log.w("getCART(): SessionStorage.cid: ", "" + SessionStorage.cid);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION3, envelope);
			Object response = envelope.getResponse();

			Log.d("Cart in checkout: ", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("cart");

			alProductListing = new ArrayList<BeanProductListingLV>();
			// Reading through the array of products
			for (int i = 0; i < jsonarray.length(); i++) {
				beanProductListing = new BeanProductListingLV();
				// this object inside array you can do whatever you want
				JSONObject object = jsonarray.getJSONObject(i);

				int quantity = 0;
				//prodID[i] qty[i]
				Log.w("prodID.length: ", ""+prodID.length);
				for(int k=0;k<prodID.length;k++){
					
					if(String.valueOf(prodID[k]).equalsIgnoreCase(object.getString("id"))){
						quantity = qty[k];
					}
					
					double productPrice = Double.parseDouble(object.getString("sellingprice")) * quantity;
					double discountMoney = 0.0;
					if(object.getString("originalprice") != null && object.getString("originalprice").isEmpty())
					{
						discountMoney = Double.parseDouble(object
								.getString("originalprice")) - Double.parseDouble((object.getString("sellingprice")));
					}
					orders += "{\"productId\":\"" + object.getString("productId") + "\",";
					orders += "\"skuId\":\"" + object.getString("id") + "\",";
					orders += "\"skuCode\":\"" + object.getString("pcode") + "\",";
					orders += "\"quantity\":\"" + quantity + "\",";
					orders += "\"unitPrice\":\"" + object.getString("originalprice") + "\",";
					orders += "\"discountMoney\":\"" + discountMoney + "\",";
					orders += "\"price\":\"" + productPrice + "\"},";
				}
				
				
				/*int discountMoney = Integer.parseInt(object
							.getString("originalprice"))
							- Double.parseDouble((object.getString("sellingprice"));*/
				/*double discountMoney = 0.0;
				if(object.getString("originalprice") != null && object.getString("originalprice").isEmpty())
				{
					discountMoney = Double.parseDouble(object
							.getString("originalprice")) - Double.parseDouble((object.getString("sellingprice")));
				}
				orders += "{\"productId\":\"" + object.getString("productId") + "\",";
				orders += "\"skuId\":\"" + object.getString("id") + "\",";
				orders += "\"skuCode\":\"" + object.getString("pcode") + "\",";
				orders += "\"quantity\":\"1\",";
				orders += "\"unitPrice\":\"" + object.getString("originalprice") + "\",";
				orders += "\"discountMoney\":\"" + ""+discountMoney+ "\",";
				orders += "\"price\":\"" + object.getString("sellingprice") + "\"},";
				Log.w("orders: ", ""+orders);*/
				
				beanProductListing.setProductId(object.getString("id"));
				beanProductListing.setSKUId(object.getString("id"));
				beanProductListing.setProductName(object.getString("name"));
				beanProductListing.setOPrice(object.getString("originalprice"));
				beanProductListing.setPPrice(object.getString("sellingprice"));
				// beanProductListing.setProdQuantity(object.getString("quantity"));
				beanProductListing.setPAvaiability(object
						.getString("availability"));
				beanProductListing.setImgUrl(object.getString("image"));
				alProductListing.add(beanProductListing);
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

		orders = orders.substring(0, orders.length() - 1);
		orders += "]}";
		Log.w("orders: ", ""+orders);
		//getCart();
	}

	private class AsyncCallWSGetCart extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(CheckOut.this);
			pd.setMessage("Loading...(get cart)");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();

			if (alProductListing == null) {
				/*Toast.makeText(getApplicationContext(), "Cart is empty",
						Toast.LENGTH_SHORT).show();*/
			} else {/*
					 * listViewAddtoCart = (ListView)
					 * findViewById(R.id.listViewCart);
					 * listViewAddtoCart.setAdapter(new
					 * AddToCartAdapter(Cart.this, alProductListing));
					 * 
					 * cartProductNumber.setText("(" +
					 * SessionStorage.cartNumberOfProducts + " Item):");
					 * cartCost.setText(SessionStorage.cartSubTotal + "/-");
					 * //SessionStorage.cartSubTotal = 0.0;
					 * 
					 * Toast.makeText(getApplicationContext(), "Cart number:"+
					 * SessionStorage.cid, Toast.LENGTH_SHORT) .show();
					 */
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				getCart();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	// Webservice for updating the profile information
	public void PlaceOrder() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD4);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("userId", SessionStorage.username);
		request.addProperty("orderType", "SALESORDER");
		request.addProperty("totalLineItems", ""+SessionStorage.cartNumberOfProducts);
		request.addProperty("finalOrderTotal", ""+SessionStorage.cartSubTotal);
		request.addProperty("shipName", SessionStorage.oName);//SessionStorage.phone
		request.addProperty("shipAddress1", SessionStorage.oaddress1);
		request.addProperty("shipAddress2", SessionStorage.oaddress2);
		request.addProperty("shipCity", SessionStorage.ocity);
		request.addProperty("shipPhone", SessionStorage.oPhone);
		request.addProperty("userAgent", "ANDROID");
		request.addProperty("orders", orders);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION4, envelope); 
			Object response = envelope.getResponse();
			Log.d("Inser order header: ", ""+response);
			//Log.d("Inser order header: ", response.toString());Log.d("Inser order header: ", response.toString());

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

	private class AsyncCallWSPlaceOrder extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(CheckOut.this);
			pd.setMessage("Loading...(place order)");
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
				SessionStorage.cid="";
				SessionStorage.arraySKU="";
				Toast.makeText(getApplicationContext(),
						"Order placed succesfully", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				PlaceOrder();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}

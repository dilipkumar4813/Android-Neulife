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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.InternetConnection;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.adapters.WishListAdapter;
import app.retailinsights.neulife.bean.BeanWishlistListing;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class WishList extends Activity {

	ProgressDialog pd;
	private static final String METHOD = "getWishlist";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/getWishlist";

	GridView gridView;
	ArrayList<BeanWishlistListing> alProductListing;
	BeanWishlistListing beanWishlistListing;
	ListView lvWishList;
	ImageView ivback_btn;
	String wishlistId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wish_list);

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(WishList.this);
			add.show();

			int wid = metrics.widthPixels / 2 + 180;
			int heig = metrics.heightPixels / 3;
			add.getWindow().setLayout(wid, heig);
		}

		ivback_btn = (ImageView) findViewById(R.id.ivBackbtn);
		ivback_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(WishList.this, LandingScreen.class);
				startActivity(intent);
			}
		});

		// Footer Related Methods
		footerInitialize();
		footerEvents();

		AsyncCallWS some = new AsyncCallWS();
		some.execute();
	}

	public void getWishlist() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("username", SessionStorage.username);
		request.addProperty("customerType", SessionStorage.customerType);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("Product Details wishlist", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("wishlist");

			alProductListing = new ArrayList<BeanWishlistListing>();
			// Reading through the array of products
			for (int i = 0; i < jsonarray.length(); i++) {
				beanWishlistListing = new BeanWishlistListing();
				// this object inside array you can do whatever you want
				JSONObject object = jsonarray.getJSONObject(i);
				beanWishlistListing.setProductName(object.getString("name"));
				beanWishlistListing.setwishlistId(object
						.getString("wishlistid"));
				beanWishlistListing.setPrice(object.getString("price"));
				Log.w("object.getString_price: ", ""+object.getString("price"));
				beanWishlistListing.setImage(object.getString("image"));
				beanWishlistListing.setSKU(object.getString("skuId"));
				alProductListing.add(beanWishlistListing);
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
			pd = new ProgressDialog(WishList.this);
			pd.setMessage("Loading...");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();
			// Get gridview object from xml file
			lvWishList = (ListView) findViewById(R.id.lv_wish_list);
			// String[] str = { "1", "2", "1", "2", "1", "2" };
			// Set custom adapter (GridAdapter) to gridview
			lvWishList.setAdapter(new WishListAdapter(WishList.this,
					alProductListing));
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				getWishlist();

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
}

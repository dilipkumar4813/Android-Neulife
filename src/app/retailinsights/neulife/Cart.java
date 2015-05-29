package app.retailinsights.neulife;

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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import app.retailinsights.neulife.account.LoginMain;
import app.retailinsights.neulife.account.ManageAccount;
import app.retailinsights.neulife.account.Notifications;
import app.retailinsights.neulife.account.WishList;
import app.retailinsights.neulife.adapters.AddToCartAdapter;
import app.retailinsights.neulife.bean.BeanProductListingLV;
import app.retailinsights.neulife.database.DatabaseHandler;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class Cart extends Activity {

	ProgressDialog pd;
	private static final String METHOD = "getCart";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/getCart";
	ArrayList<BeanProductListingLV> alProductListing;
	BeanProductListingLV beanProductListing;
	GridView gridView;
	Button checkoutBtn;
	ListView listViewAddtoCart;
	String[] CART_DATA = { "1" };
	ImageView backBtn, notifications;
	SharedPreferences preferences;
	public static final String MyPREFERENCES = "MyPrefs";
	TextView cartProductNumber, cartCost, tvAddMoreProd;
	String[] productID = new String[50];
	String[] quantityID = new String[50];
	String[] pPID;
	String[] qQID;
	int q=0;
	int firstCart = 0;
	String skuArray;
	Double newTotal = 0.0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cart);

		// SessionStorage.cid
		DatabaseHandler databaseHandler = new DatabaseHandler(Cart.this);
		databaseHandler.resetCartTable();
		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(Cart.this);
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

		String sCid[] = SessionStorage.cid.split(",");
		//SessionStorage.cid = "0,";
		skuArray = "0,";
		int p;
		// Taking each and every element in the array
		for (int k = 0; k < sCid.length; k++) {
			p = 0;
			// Comparing the element taking in the k loop with the entire array
			for (int i = 0; i < sCid.length; i++) {
				if (sCid[i].equalsIgnoreCase(sCid[k])) {
					if(k!=i){
						sCid[i]="0";
					}
					p++;
				}
			}
			if (!sCid[k].equalsIgnoreCase("0")) {
				// Adding product ID and quantity to database table
				// DatabaseHandler databaseHandler2 = new DatabaseHandler(
				// this.getApplicationContext());
				// databaseHandler2.addToCartTable(Integer.parseInt(sCid[k]),
				// p);
				
				productID[q] = sCid[k];
				quantityID[q] = String.valueOf(p);
				q++;
				
				//SessionStorage.cid += sCid[k] + ",";
				skuArray += sCid[k] + ",";
				Log.d("Cart Tag", sCid[k]+" "+p);
				sCid[k] = "";
				
			}

		}
		
		pPID = new String[q];
		qQID = new String[q];
		System.arraycopy(productID, 0, pPID, 0, q);
		System.arraycopy(quantityID, 0, qQID, 0, q);

		Log.w("Original Size", ""+productID.length);
		Log.w("Trimmed Size", ""+pPID.length);
		Log.w("SessionStorage CID:", SessionStorage.cid);

		AsyncCallWS some = new AsyncCallWS();
		some.execute();
		
		Log.d("New Total", ""+newTotal);
	}

	public void changeCartTotal(){
		double roundOff = Math.round(SessionStorage.cartSubTotal * 100.0) / 100.0;
		cartCost.setText(""+roundOff+ "/-");
	}
	
	public void adapterCall() {
		AsyncCallWS some = new AsyncCallWS();
		some.execute();
	}

	public void initialize() {
		backBtn = (ImageView) findViewById(R.id.imageView1);
		checkoutBtn = (Button) findViewById(R.id.checkoutBtn);

		cartProductNumber = (TextView) findViewById(R.id.textView1);
		cartCost = (TextView) findViewById(R.id.textView3);

		notifications = (ImageView) findViewById(R.id.imageView2);
	}

	public void basicEvents() {

		notifications.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						Notifications.class);
				startActivity(inten1);
			}
		});

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		checkoutBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (SessionStorage.clientId.equalsIgnoreCase("570900858")) {
					Toast.makeText(getApplicationContext(), "Please Login",
							Toast.LENGTH_SHORT).show();

					Intent inten1 = new Intent(getApplicationContext(),
							LoginMain.class);
					startActivity(inten1);
				} else {
					if (cartProductNumber.getText().toString()
							.equalsIgnoreCase("(0 Item):")) {
						Toast.makeText(getApplicationContext(),
								"Your cart is empty", Toast.LENGTH_SHORT)
								.show();
					} else {
						Intent inten1 = new Intent(getApplicationContext(),
								CheckOut.class);
						startActivity(inten1);
					}
				}

			}
		});
	}

	public void getCart() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		if(firstCart==0){
			SessionStorage.arraySKU = skuArray;
			firstCart++;
		}
		
		request.addProperty("pids", SessionStorage.arraySKU);//SessionStorage.cid);
		request.addProperty("customerType", SessionStorage.customerType);
		
		Log.w("getCART(): SessionStorage.cid: ", "" + SessionStorage.cid);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("Product Details", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("cart");

			alProductListing = new ArrayList<BeanProductListingLV>();
			// Reading through the array of products
			for (int i = 0; i < jsonarray.length(); i++) {
				beanProductListing = new BeanProductListingLV();
				// this object inside array you can do whatever you want
				JSONObject object = jsonarray.getJSONObject(i);

				for(int qq=0;qq<pPID.length;qq++){
					if(pPID[qq].equalsIgnoreCase(object.getString("id"))){
						beanProductListing.setProdQuantity(qQID[qq]);
					}
				}
				
				//beanProductListing.setProdQuantity("1");
				beanProductListing.setProductId(object.getString("id"));
				beanProductListing.setSKUId(object.getString("id"));
				beanProductListing.setProductName(object.getString("name"));
				beanProductListing.setOPrice(object.getString("originalprice"));
				beanProductListing.setPPrice(object.getString("sellingprice"));
				newTotal+=Double.parseDouble(object.getString("sellingprice"));
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
	}

	private class AsyncCallWS extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(Cart.this);
			pd.setMessage("Loading...");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();

			if (alProductListing == null) {
				/*
				 * Toast.makeText(getApplicationContext(), "Cart is empty",
				 * Toast.LENGTH_SHORT).show();
				 */
			} else {
				listViewAddtoCart = (ListView) findViewById(R.id.listViewCart);
				listViewAddtoCart.setAdapter(new AddToCartAdapter(Cart.this,
						alProductListing));
				
				SessionStorage.cartNumberOfProducts = alProductListing.size();
				if(alProductListing.size()==0){
					SessionStorage.cartSubTotal=0.0;
				}
				cartProductNumber.setText("("
						+ SessionStorage.cartNumberOfProducts + " Item):");
				double roundOff = Math.round(SessionStorage.cartSubTotal * 100.0) / 100.0;
				//cartCost.setText(SessionStorage.cartSubTotal + "/-");
				cartCost.setText(roundOff + "/-");
				// SessionStorage.cartSubTotal = 0.0;
				/*
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

	// Footer related methods
	ImageView accountBtn, wishlistImg, cartImg, searchImg, homeImg;

	public void footerInitialize() {
		tvAddMoreProd = (TextView) findViewById(R.id.tvAddMoreProd);
		searchImg = (ImageView) findViewById(R.id.searchImg);
		cartImg = (ImageView) findViewById(R.id.cartImg);
		accountBtn = (ImageView) findViewById(R.id.accountImg);
		wishlistImg = (ImageView) findViewById(R.id.wishlistImg);
		homeImg = (ImageView) findViewById(R.id.homeImg);
	}

	public void footerEvents() {

		tvAddMoreProd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Cart.this, LandingScreen.class);
				startActivity(intent);
			}
		});

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

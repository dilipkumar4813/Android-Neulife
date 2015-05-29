package app.retailinsights.neulife.productlisting;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.InternetConnection;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.account.LoginMain;
import app.retailinsights.neulife.account.ManageAccount;
import app.retailinsights.neulife.account.Notifications;
import app.retailinsights.neulife.account.WishList;
import app.retailinsights.neulife.adapters.ProductListingAdapter;
import app.retailinsights.neulife.adapters.ProductListingListviewAdapter;
import app.retailinsights.neulife.bean.BeanProductListing;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class Search extends Activity {

	ProgressDialog pd;
	private static final String METHOD = "search";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/search";
	ArrayList<BeanProductListing> alProductListing;
	BeanProductListing beanProductListing;
	GridView gridView;
	ImageView backBtn, Notifications;
	ListView listView;
	ImageView gridV;
	TextView tvGrid, productCount;
	RelativeLayout viewButton;
	int whichView = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.product_listing);

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(Search.this);
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

		TextView tvHeader = (TextView) findViewById(R.id.tvHeader);
		tvHeader.setText("Search");

		// Get gridview object from xml file
		gridView = (GridView) findViewById(R.id.gridView1);
		AsyncCallWS some = new AsyncCallWS();
		some.execute();
	}

	public void initialize() {
		backBtn = (ImageView) findViewById(R.id.imageView1);
		Notifications = (ImageView) findViewById(R.id.imageView2);

		gridV = (ImageView) findViewById(R.id.gridV);
		tvGrid = (TextView) findViewById(R.id.tvGrid);
		viewButton = (RelativeLayout) findViewById(R.id.whichView);

		gridView = (GridView) findViewById(R.id.gridView1);
		listView = (ListView) findViewById(R.id.listView1);

		productCount = (TextView) findViewById(R.id.productCount);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		return;
	}

	public void basicEvents() {

		viewButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (whichView == 0) {
					listView.setVisibility(View.INVISIBLE);
					gridView.setVisibility(View.VISIBLE);
					tvGrid.setText("List View");
					whichView = 1;
				} else {
					listView.setVisibility(View.VISIBLE);
					gridView.setVisibility(View.INVISIBLE);
					tvGrid.setText("Grid View");
					whichView = 0;
				}
			}
		});

		gridV.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (whichView == 0) {
					listView.setVisibility(View.INVISIBLE);
					gridView.setVisibility(View.VISIBLE);
					tvGrid.setText("List View");
					whichView = 1;
				} else {
					listView.setVisibility(View.VISIBLE);
					gridView.setVisibility(View.INVISIBLE);
					tvGrid.setText("Grid View");
					whichView = 0;
				}
			}
		});

		tvGrid.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (whichView == 0) {
					listView.setVisibility(View.INVISIBLE);
					gridView.setVisibility(View.VISIBLE);
					tvGrid.setText("List View");
					whichView = 1;
				} else {
					listView.setVisibility(View.VISIBLE);
					gridView.setVisibility(View.INVISIBLE);
					tvGrid.setText("Grid View");
					whichView = 0;
				}
			}
		});

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		Notifications.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						Notifications.class);
				startActivity(inten1);
			}
		});
	}

	public void getSearchProducts() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("searchText", SessionStorage.searchText);
		request.addProperty("customerType", SessionStorage.customerType);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("Search Details", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("search");

			alProductListing = new ArrayList<BeanProductListing>();
			// Reading through the array of products
			for (int i = 0; i < jsonarray.length(); i++) {
				beanProductListing = new BeanProductListing();
				// this object inside array you can do whatever you want
				JSONObject object = jsonarray.getJSONObject(i);

				beanProductListing.setProductId(object.getString("id"));
				beanProductListing.setProductName(object.getString("name"));
				beanProductListing.setProductCode(object.getString("pcode"));
				beanProductListing.setProdSize(object.getString("sizeCode"));
				beanProductListing.setColor(object.getString("colorCode"));
				beanProductListing.setFlavour(object.getString("flavourCode"));
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
	}

	private class AsyncCallWS extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(Search.this);
			pd.setMessage("Loading...");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();

			if (alProductListing == null) {
				Toast.makeText(getApplicationContext(), "No Products",
						Toast.LENGTH_SHORT).show();
				productCount.setText("(0)");
			} else {
				// Set custom adapter (GridAdapter) to gridview
				gridView.setAdapter(new ProductListingAdapter(Search.this,
						alProductListing));

				listView.setAdapter(new ProductListingListviewAdapter(
						Search.this, alProductListing));

				productCount.setText("(" + listView.getCount() + ")");
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				getSearchProducts();

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

package app.retailinsights.neulife;

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
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import app.retailinsights.neulife.account.LoginMain;
import app.retailinsights.neulife.account.ManageAccount;
import app.retailinsights.neulife.account.Notifications;
import app.retailinsights.neulife.account.WishList;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class ProductAdvancedDetails extends Activity {

	ProgressDialog pd;
	private static final String METHOD = "getProductDetails";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/getProductDetails";

	private static final String METHOD2 = "ratingsAndReviews";
	private final String SOAP_ACTION2 = SessionStorage.webserviceNamespace
			+ "service.php/ratingsAndReviews";

	ImageView ivPlusDesc, ivMinusDesc, ivPlusSupplement, ivMinusSupplement,
			ivPlusIngrediants, ivMinusIngrediants, ivHowToUse, ivFaq, ivReview,
			notifications;
	TextView tvDescription, tvSupplement, tvIngrediant, tvProdName, tvHowToUSe,
			tvFaq, tvReview;
	ScrollView svDescription, svSupplement, svIngrediant, svHowToUse, svFaq,
			svReview;
	String description = "";
	String ingredint = "",
			howtouse = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Perferendis nostrum at sunt fuga, saepe eaque labore laborum dolores tempore sapiente, incidunt eLorem ipsum dolor sit amet, consectetur adipisicing elit. Perferendis nostrum at sunt fuga, saepe eaque labore laborum dolores tempore sapiente, incidunt exLorem ipsum dolor sit amet, consectetur adipisicing elit. Perferendis nostrum at sunt fuga, saepe eaque labore laborum dolores tempore sapiente, incidunt ecepturi placeat quae quasi quidem cum sit suscipit amet!",
			faqs = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Perferendis nostrum at sunt fuga, saepe eaque labore laborum dolores tempore sapiente, incidunt excepturi placeat quae quasi quidem cum sit suscipit amet!",
			review = "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Perferendis nostrum at sunt fuga, saepe eaque labore laborum dolores tempore sapiente, incidunt excepturi placeat quae quasi quidem cum sit suscipit amet!";
	String suppliment = "", prodName;
	boolean flagDesc = true;
	boolean flagIngredint = false;
	boolean flagSuppliment = false;
	boolean flag = true;
	ImageView backBtn;
	String ratings = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.product_advanced_details);

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(
					ProductAdvancedDetails.this);
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

		AsyncCallWS some = new AsyncCallWS();
		some.execute();

		ivPlusDesc.setImageResource(R.drawable.plus_img);
		ivPlusIngrediants.setImageResource(R.drawable.plus_img);
		ivPlusSupplement.setImageResource(R.drawable.plus_img);
		ivHowToUse.setImageResource(R.drawable.plus_img);
		ivFaq.setImageResource(R.drawable.plus_img);
		ivReview.setImageResource(R.drawable.plus_img);

		if (flagDesc == true) {
			tvDescription.setText(description);
			svDescription.setVisibility(View.GONE);
		}
		ivPlusDesc.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flagDesc == true) {
					tvDescription.setText(description);
					svDescription.setVisibility(View.VISIBLE);
					flagDesc = false;
					svIngrediant.setVisibility(View.GONE);
					svSupplement.setVisibility(View.GONE);
					svHowToUse.setVisibility(View.GONE);
					svFaq.setVisibility(View.GONE);
					svReview.setVisibility(View.GONE);
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
					tvIngrediant.setText(ingredint);
					svIngrediant.setVisibility(View.VISIBLE);
					flagIngredint = false;
					svDescription.setVisibility(View.GONE);
					svSupplement.setVisibility(View.GONE);
					svHowToUse.setVisibility(View.GONE);
					svFaq.setVisibility(View.GONE);
					svReview.setVisibility(View.GONE);
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
					tvSupplement.setText(suppliment);
					svSupplement.setVisibility(View.VISIBLE);
					flagSuppliment = false;
					svIngrediant.setVisibility(View.GONE);
					svDescription.setVisibility(View.GONE);
					svHowToUse.setVisibility(View.GONE);
					svFaq.setVisibility(View.GONE);
					svReview.setVisibility(View.GONE);
					ivPlusSupplement.setImageResource(R.drawable.minus_img);
				} else {
					svSupplement.setVisibility(View.GONE);
					flagSuppliment = true;
					ivPlusSupplement.setImageResource(R.drawable.plus_img);
				}
			}
		});
		ivHowToUse.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flagDesc == true) {
					tvHowToUSe.setText(howtouse);
					svHowToUse.setVisibility(View.VISIBLE);
					flagDesc = false;
					svIngrediant.setVisibility(View.GONE);
					svSupplement.setVisibility(View.GONE);
					svFaq.setVisibility(View.GONE);
					svReview.setVisibility(View.GONE);
					svReview.setVisibility(View.GONE);
					ivHowToUse.setImageResource(R.drawable.minus_img);
				} else {
					svHowToUse.setVisibility(View.GONE);
					flagDesc = true;
					ivHowToUse.setImageResource(R.drawable.plus_img);
				}

			}
		});
		ivFaq.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flagDesc == true) {
					tvFaq.setText(faqs);
					svFaq.setVisibility(View.VISIBLE);
					flagDesc = false;
					svIngrediant.setVisibility(View.GONE);
					svSupplement.setVisibility(View.GONE);
					svHowToUse.setVisibility(View.GONE);
					svReview.setVisibility(View.GONE);
					svReview.setVisibility(View.GONE);
					ivFaq.setImageResource(R.drawable.minus_img);
				} else {
					svFaq.setVisibility(View.GONE);
					flagDesc = true;
					ivFaq.setImageResource(R.drawable.plus_img);
				}

			}
		});
		ivReview.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (flagDesc == true) {
					tvReview.setText(review);
					svReview.setVisibility(View.VISIBLE);
					flagDesc = false;
					svIngrediant.setVisibility(View.GONE);
					svSupplement.setVisibility(View.GONE);
					svHowToUse.setVisibility(View.GONE);
					svDescription.setVisibility(View.GONE);
					svFaq.setVisibility(View.GONE);
					ivReview.setImageResource(R.drawable.minus_img);
				} else {
					svReview.setVisibility(View.GONE);
					flagDesc = true;
					ivReview.setImageResource(R.drawable.plus_img);
				}

			}
		});
	}

	private void initialize() {
		notifications = (ImageView) findViewById(R.id.ivNotifications);
		ivPlusDesc = (ImageView) findViewById(R.id.ivPlusDesc);
		// ivMinusDesc = (ImageView) findViewById(R.id.ivMinusDesc);
		ivPlusSupplement = (ImageView) findViewById(R.id.ivPlusSuppliment);
		// ivMinusSupplement = (ImageView) findViewById(R.id.ivMinusSuppliment);
		ivPlusIngrediants = (ImageView) findViewById(R.id.ivPlusIngrediants);
		ivHowToUse = (ImageView) findViewById(R.id.ivHowToUse);
		ivFaq = (ImageView) findViewById(R.id.ivFaq);
		ivReview = (ImageView) findViewById(R.id.ivReview);
		// ivMinusIngrediants = (ImageView)
		// findViewById(R.id.ivMinusIngrediants);
		tvDescription = (TextView) findViewById(R.id.tvDescription);
		tvSupplement = (TextView) findViewById(R.id.tvSuppliment);
		tvIngrediant = (TextView) findViewById(R.id.tvIngrediants);
		tvHowToUSe = (TextView) findViewById(R.id.tvHowToUSe);
		backBtn = (ImageView) findViewById(R.id.imageView1);
		tvProdName = (TextView) findViewById(R.id.tvProdName);
		tvFaq = (TextView) findViewById(R.id.tvFaq);
		tvReview = (TextView) findViewById(R.id.tvReview);

		svDescription = (ScrollView) findViewById(R.id.svdescription);
		svSupplement = (ScrollView) findViewById(R.id.svsuppliment);
		svIngrediant = (ScrollView) findViewById(R.id.svingrediants);
		svHowToUse = (ScrollView) findViewById(R.id.svHowToUse);
		svFaq = (ScrollView) findViewById(R.id.svFaq);
		svReview = (ScrollView) findViewById(R.id.svReview);
	}

	public void printDetails() {
		tvDescription.setText(description);
		tvIngrediant.setText(ingredint);
		tvSupplement.setText(suppliment);
		tvProdName.setText(prodName);
		tvReview.setText(ratings);
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
	}

	// Webservice for authentication
	public void getProductDetails() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("productid", SessionStorage.pid);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("Product Details", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("product");

			for (int i = 0; i < jsonarray.length(); i++) {
				// this object inside array you can do whatever you want
				JSONObject object = jsonarray.getJSONObject(i);
				description = object.getString("description");
				ingredint = object.getString("Indgredients");
				suppliment = object.getString("facts");
				prodName = object.getString("name");
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

		getRatings();
	}

	private class AsyncCallWS extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(ProductAdvancedDetails.this);
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
				getProductDetails();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	// Webservice for getting ratings and reviews
	public void getRatings() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD2);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("productid", SessionStorage.pid);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION2, envelope);
			Object response = envelope.getResponse();

			Log.d("Product Details", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("ratings");

			for (int i = 0; i < jsonarray.length(); i++) {
				// this object inside array you can do whatever you want
				JSONObject object = jsonarray.getJSONObject(i);
				ratings += object.getString("name") + "\n";
				ratings += object.getString("rating") + "\n";
				ratings += object.getString("review") + "\n";
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

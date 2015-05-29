package app.retailinsights.neulife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import app.retailinsights.neulife.account.AccountManagement;
import app.retailinsights.neulife.account.LoginMain;
import app.retailinsights.neulife.account.ManageAccount;
import app.retailinsights.neulife.account.Notifications;
import app.retailinsights.neulife.account.WishList;
import app.retailinsights.neulife.database.DatabaseHandler;
import app.retailinsights.neulife.productlisting.BestSellers;
import app.retailinsights.neulife.productlisting.NewArrival;
import app.retailinsights.neulife.productlisting.Offers;
import app.retailinsights.neulife.productlisting.Recommended;
import app.retailinsights.neulife.productlisting.Search;
import app.retailinsights.neulife.productlisting.TopRated;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class LandingScreen extends Activity {

	ImageView categories, offersBtn, topRatedBtn, newArrivalBtn, bestsellerBtn,
			recommendBtn, ivNotifications, searchIcon;
	EditText searchTxt;
	private boolean doubleBackToExitPressedOnce;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.landing_screen);
		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(LandingScreen.this);
			add.show();

			int wid = metrics.widthPixels / 2 + 180;
			int heig = metrics.heightPixels / 3;
			add.getWindow().setLayout(wid, heig);
		}

		initialize();
		basicEvents();

		if (SessionStorage.search == 1) {
			SessionStorage.search = 0;
			searchTxt.requestFocus();
			searchTxt.setHint("");
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
					InputMethodManager.HIDE_IMPLICIT_ONLY);
		}
		footerInitialize();
		footerEvents();
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}

	public void initialize() {
		categories = (ImageView) findViewById(R.id.shopByCategoryBtn);
		offersBtn = (ImageView) findViewById(R.id.offersBtn);
		offersBtn.bringToFront();
		topRatedBtn = (ImageView) findViewById(R.id.topRatedBtn);
		topRatedBtn.bringToFront();
		newArrivalBtn = (ImageView) findViewById(R.id.newArrivalBtn);
		bestsellerBtn = (ImageView) findViewById(R.id.bestsellerBtn);
		recommendBtn = (ImageView) findViewById(R.id.recommendBtn);
		searchIcon = (ImageView) findViewById(R.id.searchHeader);
		searchTxt = (EditText) findViewById(R.id.searchTxt);

		ivNotifications = (ImageView) findViewById(R.id.ivNotifications);
	}

	public void basicEvents() {

		searchTxt.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {
					searchTxt.setHint("Search for a product or brand");
				} else {
					searchTxt.setHint("");
				}
			}
		});

		ivNotifications.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						Notifications.class);
				startActivity(inten1);
			}
		});

		searchIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!searchTxt.getText().toString().isEmpty()) {
					SessionStorage.searchText = searchTxt.getText().toString();
					Intent inten1 = new Intent(getApplicationContext(),
							Search.class);
					startActivity(inten1);
				} else {
					Toast.makeText(getApplicationContext(),
							"Please enter search term", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		categories.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						Categories.class);
				startActivity(inten1);
			}
		});

		offersBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						Offers.class);
				startActivity(inten1);
			}
		});

		topRatedBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						TopRated.class);
				startActivity(inten1);
			}
		});

		newArrivalBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						NewArrival.class);
				startActivity(inten1);
			}
		});

		bestsellerBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						BestSellers.class);
				startActivity(inten1);
			}
		});

		recommendBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						Recommended.class);
				startActivity(inten1);
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
				/*
				 * Intent inten1 = new Intent(getApplicationContext(),
				 * LandingScreen.class); startActivity(inten1);
				 */
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
				searchTxt.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
						InputMethodManager.HIDE_IMPLICIT_ONLY);
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

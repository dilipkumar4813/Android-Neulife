package app.retailinsights.neulife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import app.retailinsights.neulife.account.LoginMain;
import app.retailinsights.neulife.account.ManageAccount;
import app.retailinsights.neulife.account.WishList;
import app.retailinsights.neulife.productlisting.ProductListing;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class Categories extends Activity {

	RelativeLayout generalHealthBtn, sportsNutritionBtn, proteinPowdersBtn,
			DietProductsBtn, healthFoodBtn, sportsAccessoriesBtn, apparelBtn,
			stealDealsBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.categories);

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(Categories.this);
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
	}

	public void initialize() {

		generalHealthBtn = (RelativeLayout) findViewById(R.id.generalHealthBtn);
		sportsNutritionBtn = (RelativeLayout) findViewById(R.id.sportsNutritionBtn);
		proteinPowdersBtn = (RelativeLayout) findViewById(R.id.proteinPowdersBtn);
		DietProductsBtn = (RelativeLayout) findViewById(R.id.DietProductsBtn);
		healthFoodBtn = (RelativeLayout) findViewById(R.id.healthFoodBtn);
		sportsAccessoriesBtn = (RelativeLayout) findViewById(R.id.sportsAccessoriesBtn);
		apparelBtn = (RelativeLayout) findViewById(R.id.apparelBtn);
		stealDealsBtn = (RelativeLayout) findViewById(R.id.stealDealsBtn);
	}

	public void basicEvents() {

		/*******************
		 * Categories selection ******************
		 */

		generalHealthBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SessionStorage.categoryId = "3";
				SessionStorage.headerTitle = "General Health";
				Intent inten1 = new Intent(getApplicationContext(),
						ProductListing.class);
				startActivity(inten1);
			}
		});

		sportsNutritionBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SessionStorage.categoryId = "4";
				SessionStorage.headerTitle = "Sports Nutrition";
				Intent inten1 = new Intent(getApplicationContext(),
						ProductListing.class);
				startActivity(inten1);
			}
		});

		proteinPowdersBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SessionStorage.categoryId = "56";
				SessionStorage.headerTitle = "Protein Powders";
				Intent inten1 = new Intent(getApplicationContext(),
						ProductListing.class);
				startActivity(inten1);
			}
		});

		DietProductsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SessionStorage.categoryId = "6";
				SessionStorage.headerTitle = "Diet Products";
				Intent inten1 = new Intent(getApplicationContext(),
						ProductListing.class);
				startActivity(inten1);
			}
		});

		healthFoodBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SessionStorage.categoryId = "267";
				SessionStorage.headerTitle = "Health Food";
				Intent inten1 = new Intent(getApplicationContext(),
						ProductListing.class);
				startActivity(inten1);
			}
		});

		sportsAccessoriesBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SessionStorage.categoryId = "8";
				SessionStorage.headerTitle = "Sports and Accessories";
				Intent inten1 = new Intent(getApplicationContext(),
						ProductListing.class);
				startActivity(inten1);
			}
		});

		apparelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SessionStorage.categoryId = "9";
				SessionStorage.headerTitle = "Apparel";
				Intent inten1 = new Intent(getApplicationContext(),
						ProductListing.class);
				startActivity(inten1);
			}
		});

		stealDealsBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SessionStorage.categoryId = "265";
				SessionStorage.headerTitle = "Steal Deals";
				Intent inten1 = new Intent(getApplicationContext(),
						ProductListing.class);
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

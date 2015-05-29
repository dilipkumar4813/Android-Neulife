package app.retailinsights.neulife.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.InternetConnection;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class ManageAccountProfile extends Activity {
	RelativeLayout rl_profile_header, rl_view_order, rl_notification_settings,
			rl_help_and_faqs, rl_delivery_address, rl_change_password;
	Button btnSignOut, button1;
	ImageView ivNotifications, backArrow;
	TextView tvViewOrder, tvPhone, tvFullName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.manage_account_profile);
		//getActionBar().hide();

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(ManageAccountProfile.this);
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

	private void initialize() {
		rl_profile_header = (RelativeLayout) findViewById(R.id.rl_profile_header);
		rl_view_order = (RelativeLayout) findViewById(R.id.rl_view_order);
		rl_notification_settings = (RelativeLayout) findViewById(R.id.rl_notification_settings);
		rl_help_and_faqs = (RelativeLayout) findViewById(R.id.rl_help_and_faqs);
		btnSignOut = (Button) findViewById(R.id.btnSignOut);
		ivNotifications = (ImageView) findViewById(R.id.ivNotifications);
		button1 = (Button) findViewById(R.id.button1);
		rl_delivery_address = (RelativeLayout) findViewById(R.id.rl_delivery_address);
		rl_change_password = (RelativeLayout) findViewById(R.id.rl_change_password);
		tvViewOrder = (TextView) findViewById(R.id.tvViewOrder);
		tvViewOrder.setText(SessionStorage.email);
		tvPhone = (TextView) findViewById(R.id.tvPhone);
		tvPhone.setText("+91 " + SessionStorage.phone);
		tvFullName = (TextView) findViewById(R.id.tvFullName);
		tvFullName.setText(SessionStorage.fName + " " + SessionStorage.lName);
		backArrow = (ImageView) findViewById(R.id.imageView1);
	}

	private void basicEvents() {

		ImageView ivNotifications = (ImageView) findViewById(R.id.ivNotifications);
		ivNotifications.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ManageAccountProfile.this,
						Notifications.class);
				startActivity(intent);
			}
		});

		backArrow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		rl_profile_header.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ManageAccountProfile.this,
						ManageAccountProfile.class);
				startActivity(intent);
			}
		});
		button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ManageAccountProfile.this,
						ProfileEdit.class);
				startActivity(intent);
			}
		});
		rl_delivery_address.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ManageAccountProfile.this,
						DeliveryAddress.class);
				startActivity(intent);
			}
		});
		rl_change_password.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ManageAccountProfile.this,
						ChangePassword.class);
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

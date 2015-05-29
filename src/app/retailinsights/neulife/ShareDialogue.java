package app.retailinsights.neulife;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShareDialogue extends Dialog implements
		android.view.View.OnClickListener {

	public Activity c;
	public Dialog d;
	public TextView facebook, twitter, email, sms;
	public RelativeLayout ivFacebook, ivTwitter, ivEmail, ivSms;
	Context cntxt;

	public ShareDialogue(Activity a) {
		super(a);
		// TODO Auto-generated constructor stub
		this.c = a;
		cntxt = this.c;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.share_dialogue);

		initialize();
	}

	public void initialize() {
		/*
		 * facebook = (TextView) findViewById(R.id.button3); twitter =
		 * (TextView) findViewById(R.id.btnTwitter); email = (TextView)
		 * findViewById(R.id.tvEmail); sms = (TextView)
		 * findViewById(R.id.btnSms);
		 */

		ivFacebook = (RelativeLayout) findViewById(R.id.rlFb);
		ivTwitter = (RelativeLayout) findViewById(R.id.rl_twitter);
		ivEmail = (RelativeLayout) findViewById(R.id.rl_email);
		ivSms = (RelativeLayout) findViewById(R.id.rl_sms);

		/*
		 * facebook.setOnClickListener(this); twitter.setOnClickListener(this);
		 * email.setOnClickListener(this); sms.setOnClickListener(this);
		 */

		ivFacebook.setOnClickListener(this);
		ivTwitter.setOnClickListener(this);
		ivEmail.setOnClickListener(this);
		ivSms.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlFb:
			inviteFacebook();
			break;

		case R.id.rl_twitter:
			sendTwitter();
			break;

		case R.id.rl_email:
			sendEmail();
			break;
		case R.id.rl_sms:
			sendSMSMessage();
			break;

		default:
			break;
		}
		dismiss();
	}

	protected void sendSMSMessage() {
		String phoneNumber = "";
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
				+ phoneNumber));
		intent.putExtra("sms_body", "Neulife, keep yourself fit");
		getContext().startActivity(intent);
	}

	protected void inviteFacebook() {
		String urlToShare = "http://neulife.com";
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
		// See if official Facebook app is found
		boolean facebookAppFound = false;
		List<ResolveInfo> matches = getContext().getPackageManager()
				.queryIntentActivities(intent, 0);
		for (ResolveInfo info : matches) {
			if (info.activityInfo.packageName.toLowerCase().startsWith(
					"com.facebook")) {
				intent.setPackage(info.activityInfo.packageName);
				facebookAppFound = true;
				break;
			}
		}
		// If facebook app not found, load sharer.php in a browser
		if (!facebookAppFound) {
			String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u="
					+ urlToShare;
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
		}
		getContext().startActivity(intent);
	}

	protected void sendEmail() {

		String[] TO = { "" };
		String[] CC = { "" };
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.setType("text/plain");

		emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
		emailIntent.putExtra(Intent.EXTRA_CC, CC);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT,
				"We want you to join Neulife");
		emailIntent.putExtra(Intent.EXTRA_TEXT, "Keep yourself fit");

		try {
			getContext().startActivity(
					Intent.createChooser(emailIntent, "Send mail..."));
			Log.i("Finished sending email...", "");
		} catch (android.content.ActivityNotFoundException ex) {

		}
	}

	public void sendTwitter() {
		// Create intent using ACTION_VIEW and a normal Twitter url:
		String tweetUrl = String.format(
				"https://twitter.com/intent/tweet?text=%s&url=%s",
				urlEncode("Keep yourself fit"),
				urlEncode("https://www.neulife.com/"));
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

		// Narrow down to official Twitter app, if available:
		List<ResolveInfo> matches = getContext().getPackageManager()
				.queryIntentActivities(intent, 0);
		for (ResolveInfo info : matches) {
			if (info.activityInfo.packageName.toLowerCase().startsWith(
					"com.twitter")) {
				intent.setPackage(info.activityInfo.packageName);
			}
		}

		getContext().startActivity(intent);
	}

	public static String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// Log.wtf(TAG, "UTF-8 should always be supported", e);
			throw new RuntimeException("URLEncoder.encode() failed for " + s);
		}
	}
}
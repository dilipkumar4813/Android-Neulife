package app.retailinsights.neulife.account;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;

public class ConfirmPhoneNumber extends Dialog implements
		android.view.View.OnClickListener {

	ProgressDialog pd;
	private static final String METHOD = "verifyNumber";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/verifyNumber";
	public int status;
	public Activity c;
	public Dialog d;
	public Button yes;
	Context cntxt;
	public String email = "",cnumber = "";
	public EditText confirmNumber;
	public ImageView ivClose;

	public ConfirmPhoneNumber(Activity a) {
		super(a);
		// TODO Auto-generated constructor stub
		this.c = a;
		cntxt = this.c;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.verify_phone_number);
		yes = (Button) findViewById(R.id.btnLogin);
		yes.setOnClickListener(this);
		
		ivClose = (ImageView) findViewById(R.id.ivClose);
		ivClose.setOnClickListener(this);

		confirmNumber = (EditText) findViewById(R.id.etUsername);
		confirmNumber.setText(SessionStorage.confirmPhone);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			email = SessionStorage.email;
			cnumber = confirmNumber.getText().toString();

			if (cnumber.isEmpty()) {
				Toast.makeText(this.c, "Please enter confirmation number",
						Toast.LENGTH_SHORT).show();
			} else {
				AsyncCallWS some = new AsyncCallWS();
				some.execute();
			}
			break;
			
		case R.id.ivClose:
			this.dismiss();
			break;
			
		default:
			break;
		}
		dismiss();
	}

	public void changeScreen() {
		this.dismiss();

		Intent inten1 = new Intent(c.getApplicationContext(),
				LoginMain.class);
		if(inten1!=null){
			c.startActivity(inten1);
		}
	}

	// Webservice for authentication
	public void confirmPhoneNumber(String email) throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("emailid", email);
		request.addProperty("cnumber", cnumber);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("Confirm phone number Status:", response.toString());

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

	private class AsyncCallWS extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			if (status == 0) {
				Toast.makeText(cntxt, "Oops something went wrong!",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(cntxt, "Phone number confirmed", Toast.LENGTH_LONG)
						.show();
				
				SessionStorage.confirmPhone = "";
				
				changeScreen();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				confirmPhoneNumber(email);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}
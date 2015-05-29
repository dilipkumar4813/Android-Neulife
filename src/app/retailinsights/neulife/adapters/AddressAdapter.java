package app.retailinsights.neulife.adapters;

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.account.DeliveryAddress;
import app.retailinsights.neulife.account.DeliveryAddressEdit;
import app.retailinsights.neulife.bean.BeanAddressListing;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddressAdapter extends BaseAdapter implements OnClickListener {

	private Context context;
	ArrayList<BeanAddressListing> alProductListing;

	private static final String METHOD4 = "deleteAddress";
	private final String SOAP_ACTION4 = SessionStorage.webserviceNamespace
			+ "service.php/deleteAddress";
	public int status;
	private String addressId;

	// Constructor to initialize values
	public AddressAdapter(Context context,
			ArrayList<BeanAddressListing> alProductListing) {
		this.context = context;
		this.alProductListing = alProductListing;
	}

	@Override
	public int getCount() {

		// Number of times getView method call depends upon gridValues.length
		return alProductListing.size();
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	/********* Create a holder Class to contain inflated xml file elements *********/
	public static class ViewHolder {

		public Button btnEditAddr1, btnRemove1;
		public TextView tvAddressLine1;
		public TextView tvAddressLine2;
		public TextView tvCity;
		public TextView tvPincode;
		public TextView tvFullName;
		public TextView tvPhone;

	}

	// Number of times getView method call depends upon gridValues.length

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		// LayoutInflator to call external grid_item.xml file
		View vi = convertView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (vi == null) {

			vi = inflater.inflate(R.layout.list_item_address, parent, false);
			// set value into textview
			holder = new ViewHolder();

			holder.btnEditAddr1 = (Button) vi.findViewById(R.id.btnEditAddr1);
			holder.btnRemove1  = (Button) vi.findViewById(R.id.btnRemove1);
			holder.tvAddressLine1  = (TextView) vi.findViewById(R.id.tvAddress_first_line1);
			holder.tvAddressLine2  = (TextView) vi.findViewById(R.id.tvAddress_second_line1);
			holder.tvCity  = (TextView) vi.findViewById(R.id.tvCity1);
			holder.tvPincode  = (TextView) vi.findViewById(R.id.tvPinCode1);
			holder.tvFullName  = (TextView) vi.findViewById(R.id.tvFullName1);
			holder.tvPhone  = (TextView) vi.findViewById(R.id.tvPhone1);
			
			holder.tvAddressLine1.setText(alProductListing.get(position).getAddressLine1());
			holder.tvAddressLine2.setText(alProductListing.get(position).getAddressLine2());
			holder.tvCity.setText(alProductListing.get(position).getCity());
			holder.tvPincode.setText(alProductListing.get(position).getPincode());
			holder.tvFullName.setText(alProductListing.get(position).getFullName());
			holder.tvPhone.setText(alProductListing.get(position).getPhone());
			
			Log.w("Address Name and Phone: ",
					"" + alProductListing.get(position).getFullName()
							+ alProductListing.get(position).getPhone());
			
			holder.btnRemove1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					addressId = alProductListing.get(position).getAddressId();
					Log.w("Address Id",
							"" + alProductListing.get(position).getAddressId());
					AsyncCallWSDeleteAddress obj = new AsyncCallWSDeleteAddress();
					obj.execute();
				}
			});
			
			
			holder.btnEditAddr1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					editAddress();
				}

				private void editAddress() {
					SessionStorage.addressId = alProductListing.get(position).getAddressId();
					Intent intent = new Intent(context.getApplicationContext(),
							DeliveryAddressEdit.class);
					context.startActivity(intent);
				}
			});

		} else {
			vi = (View) convertView;
		}

		vi.setOnClickListener(new OnItemClickListener(position));
		return vi;
	}

	/******* Called when Item click in ListView **********/
	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View arg0) {
			Log.d("Position:", "" + mPosition);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	// Webservice for deleting the address
	public void deleteAddress() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD4);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("userId", SessionStorage.userId);
		request.addProperty("addressId", addressId);
		
		Log.d("Parameters :",""+request);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION4, envelope);
			Object response = envelope.getResponse();

			Log.d("Delete Address Details", response.toString());

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

	private class AsyncCallWSDeleteAddress extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

			if (status == 0) {
				Toast.makeText(context, "Oops something went wrong! Try Again",
						Toast.LENGTH_SHORT).show();
			} else {
				((Activity) context).finish();
				Intent intent = new Intent(context, DeliveryAddress.class);
				context.startActivity(intent);
				Toast.makeText(context, "Address removed from your wishlist",
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				deleteAddress();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}

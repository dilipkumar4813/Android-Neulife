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
import app.retailinsights.neulife.account.OrderTracking;
import app.retailinsights.neulife.account.ViewOrder;
import app.retailinsights.neulife.bean.BeanOrderListing;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OrdersAdapter extends BaseAdapter implements OnClickListener {

	private Context context;
	ArrayList<BeanOrderListing> alProductListing;

	private static final String METHOD4 = "deleteOrder";
	private final String SOAP_ACTION4 = SessionStorage.webserviceNamespace
			+ "service.php/deleteOrder";
	public int status;
	private String orderId;

	// Constructor to initialize values
	public OrdersAdapter(Context context,
			ArrayList<BeanOrderListing> alProductListing) {
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

		public ImageView deleteOrderBtn;
		public Button viewOrderBtn;
		public TextView tvAdTiltle;
		public TextView ivDate;
		public TextView ivOrderDate;
		public ImageView ivAdImage;
		public ImageView ivApproval;
		public ImageView ivProcessing;
		public ImageView ivShipped;
		public ImageView ivDelivered;

	}

	// Number of times getView method call depends upon gridValues.length

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		// LayoutInflator to call external grid_item.xml file
		View vi = convertView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (vi == null) {

			vi = inflater.inflate(R.layout.list_my_orders, parent, false);
			// set value into textview
			holder = new ViewHolder();

			holder.deleteOrderBtn = (ImageView) vi.findViewById(R.id.deleteOrderBtn);
			holder.viewOrderBtn = (Button) vi.findViewById(R.id.viewOrderBtn);
			holder.tvAdTiltle = (TextView) vi.findViewById(R.id.tvAdTiltle);
			holder.ivDate  = (TextView) vi.findViewById(R.id.ivDate);
			holder.ivOrderDate  = (TextView) vi.findViewById(R.id.ivOrderDate);
			holder.ivAdImage  = (ImageView) vi.findViewById(R.id.ivAdImage);
			holder.ivApproval  = (ImageView) vi.findViewById(R.id.ivApproval);
			holder.ivProcessing  = (ImageView) vi.findViewById(R.id.ivProcessing);
			holder.ivShipped  = (ImageView) vi.findViewById(R.id.ivShipped);
			holder.ivDelivered  = (ImageView) vi.findViewById(R.id.ivDelivered);
			
			
			holder.tvAdTiltle.setText(alProductListing.get(position).getOrderTitle());
			holder.ivDate.setText(alProductListing.get(position).getDeliveryDate());
			holder.ivOrderDate.setText(alProductListing.get(position).getOrderDate());
			
			Log.w("Order Details: ",
					"" + alProductListing.get(position).getOrderTitle()
							+ alProductListing.get(position).getDeliveryDate());
			
			holder.deleteOrderBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					orderId = alProductListing.get(position).getOrderId(); 
					Log.w("Address Id",
							"" + alProductListing.get(position).getOrderId());
					
					AsyncCallWSDeleteOrder obj = new AsyncCallWSDeleteOrder();
					obj.execute();
				}
			});
			
			
			holder.viewOrderBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					viewOrder();
				}

				private void viewOrder() {
					SessionStorage.orderId = alProductListing.get(position).getOrderId(); 
					Intent intent = new Intent(context.getApplicationContext(),
							ViewOrder.class);
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
		request.addProperty("orderId", orderId);
		
		Log.d("Parameters :",""+request);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION4, envelope);
			Object response = envelope.getResponse();

			Log.d("Order Delete Details", response.toString());

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

	private class AsyncCallWSDeleteOrder extends
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
				Intent intent = new Intent(context, OrderTracking.class);
				context.startActivity(intent);
				Toast.makeText(context, "Order has been deleted",
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

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

import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.ProductDetails;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.ShareDialogue;
import app.retailinsights.neulife.account.LoginMain;
import app.retailinsights.neulife.account.WishList;
import app.retailinsights.neulife.bean.BeanProductListing;

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

public class ProductListingListviewAdapter extends BaseAdapter implements
		OnClickListener {

	// Add to wishlist webservice
	private static final String METHOD = "addToWishlist";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/addToWishlist";
	public int status;
	private String skuid, pcode, flavour, size, color;

	// private final String[] gridValues;
	private Activity activity;
	private ArrayList<BeanProductListing> listData;

	// Constructor to initialize values
	public ProductListingListviewAdapter(Activity activity,
			ArrayList<BeanProductListing> listData) {
		this.activity = activity;
		// this.gridValues = gridValues;
		this.listData = listData;
	}

	@Override
	public int getCount() {

		// Number of times getView method call depends upon gridValues.length
		return listData.size();
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

		public Button shareImage, cart, wishlist;
		public TextView tv_grid_item_label;
		public TextView tv_grid_item_label_static;
		public TextView tv_OPrice;
		public TextView tv_PPrice;
		public TextView tv_Review;
		public TextView tv_Rating;
		//public TextView tv_Discount;
		public ImageView iv_grid_item_image;

	}

	// Number of times getView method call depends upon gridValues.length

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		// LayoutInflator to call external grid_item.xml file

		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// View gridView = null;

		View vi = convertView;
		if (vi == null) {

			vi = inflater.inflate(R.layout.list_item, null);
			// set value into textview
			holder = new ViewHolder();

			holder.tv_grid_item_label = (TextView) vi
					.findViewById(R.id.grid_item_label);
			holder.tv_grid_item_label_static = (TextView) vi
					.findViewById(R.id.grid_item_label_static);
			holder.tv_OPrice = (TextView) vi.findViewById(R.id.tvOPrice);
			holder.tv_PPrice = (TextView) vi.findViewById(R.id.tvPPrice);
			holder.tv_Review = (TextView) vi.findViewById(R.id.tvReview);
			holder.tv_Rating = (TextView) vi.findViewById(R.id.tvRating);
			holder.iv_grid_item_image = (ImageView) vi
					.findViewById(R.id.grid_item_image);
			//holder.tv_Discount = (TextView) vi.findViewById(R.id.tvDiscount);
			/*
			 * holder.tvPropPrice = (TextView)
			 * vi.findViewById(R.id.tvPropPrice1); holder.tvPropPriceBox =
			 * (TextView) vi.findViewById(R.id.tvPropPriceBox);
			 * holder.iv_prop_image = (ImageView)
			 * vi.findViewById(R.id.imageView1);
			 */

			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);

			// Share Button
			holder.shareImage = (Button) vi.findViewById(R.id.button2);
			holder.shareImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					inviteFacebook();
				}
			});

			// Cart Functionality
			holder.cart = (Button) vi.findViewById(R.id.button1);
			holder.cart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					SessionStorage.pid = listData.get(position).getProductId();
					Log.d("position...", "" + position);
					productDetailsPage();
					
					/*SessionStorage.cartPresent = 1;
					SessionStorage.cartNumberOfProducts += 1;
					SessionStorage.cartSubTotal += Double.parseDouble(listData
							.get(position).getPPrice());
					SessionStorage.cid += listData.get(position).getProductId()
							+ ",";
					addToCart(position);*/
				}
			});
			Log.d("listData.get(position).getDiscount()",
					"" + listData.get(position).getDiscount());
			/*holder.tv_Discount.setText(listData.get(position).getDiscount()
					+ "% off");*/

			// Wishlist Functionality
			holder.wishlist = (Button) vi.findViewById(R.id.button3);
			holder.wishlist.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					if (SessionStorage.clientId.equalsIgnoreCase("0")) {
						login();
					} else {
						skuid = listData.get(position).getProductId();
						pcode = listData.get(position).getProductCode();
						flavour = listData.get(position).getFlavour();
						size = listData.get(position).getProdSize();
						color = listData.get(position).getColor();

						Log.d("Wishlist Details", "UserId:"
								+ SessionStorage.userId + " skuid:" + skuid
								+ " pcode:" + pcode + " Flavour:" + flavour
								+ " size:" + size + " color:" + color);

						AsyncCallWSWishlist obj = new AsyncCallWSWishlist();
						obj.execute();

						addToWishlist();
					}
				}
			});

			// set image based on selected text
			ImageView imageView = (ImageView) vi
					.findViewById(R.id.grid_item_image);
			imageView.setImageResource(R.drawable.neulife_product);
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SessionStorage.pid = listData.get(position).getProductId();
					Log.d("position...", "" + position);
					productDetailsPage();
				}
			});

		} else {
			holder = (ViewHolder) vi.getTag();
		}

		holder.tv_grid_item_label.setText(listData.get(position)
				.getProductName());
		holder.tv_grid_item_label_static.setText(listData.get(position)
				.getProductCode());
		holder.tv_OPrice.setText(listData.get(position).getOPrice() + "/- ");
		holder.tv_PPrice.setText(listData.get(position).getPPrice() + "/- ");

		if (listData.get(position).getRating() == null) {
			holder.tv_Rating.setText("0 Rating");
		} else {
			holder.tv_Rating.setText(listData.get(position).getRating());
		}

		if (listData.get(position).getReview() == null) {
			holder.tv_Review.setText("0 Reviews");
		} else {
			holder.tv_Review.setText(listData.get(position).getReview());
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

	public void login() {

		Toast.makeText(this.activity.getApplicationContext(), "Please Login",
				Toast.LENGTH_SHORT).show();

		Intent inten1 = new Intent(this.activity.getApplicationContext(),
				LoginMain.class);
		this.activity.startActivity(inten1);
	}

	public void addToCart(int pos) {
		Intent intent = new Intent(this.activity.getApplicationContext(),
				Cart.class);
		intent.putExtra("position", "" + pos);
		this.activity.startActivity(intent);
	}

	public void addToWishlist() {
		Intent intent = new Intent(this.activity.getApplicationContext(),
				WishList.class);
		this.activity.startActivity(intent);
	}

	protected void inviteFacebook() {
		ShareDialogue add = new ShareDialogue((Activity) this.activity);
		add.show();
	}

	protected void productDetailsPage() {
		Intent intent = new Intent(this.activity.getApplicationContext(),
				ProductDetails.class);
		this.activity.startActivity(intent);
	}

	// Webservice for add to wishlist
	public void addToWishlistWebservice() throws JSONException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("userId", SessionStorage.userId);
		request.addProperty("pCode", pcode);
		request.addProperty("skuId", skuid);
		request.addProperty("flavour", flavour);
		request.addProperty("color", color);
		request.addProperty("size", size);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("Add to wishlist Status:", response.toString());

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

	private class AsyncCallWSWishlist extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			if (status == 0) {
				Toast.makeText(activity, "Oops Something went wrong",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(activity, "Added to wishlist", Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				addToWishlistWebservice();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}

package app.retailinsights.neulife.adapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.ProductDetails;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.R.drawable;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.ShareDialogue;
import app.retailinsights.neulife.account.LoginMain;
import app.retailinsights.neulife.account.WishList;
import app.retailinsights.neulife.bean.BeanProductListing;
import app.retailinsights.neulife.productlisting.ProductListing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ProductListingAdapter extends BaseAdapter implements
		OnClickListener {

	ProgressDialog pd;
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
	public ProductListingAdapter(Activity activity,
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

		return position;
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

		Log.w("getView() method called..", "");
		if (convertView == null) {
			Log.w("convertView is null", "");
		} else {
			Log.w("convertView is not null", "");
		}
		ViewHolder holder;
		// LayoutInflator to call external grid_item.xml file

		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// View gridView = null;

		View vi = convertView;
		if (vi == null) {

			vi = inflater.inflate(R.layout.grid_item, null);
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
			//holder.tv_Discount = (TextView) vi.findViewById(R.id.tvDiscount1);
			holder.iv_grid_item_image = (ImageView) vi
					.findViewById(R.id.grid_item_image);
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

		} else {
			holder = (ViewHolder) vi.getTag();
			// holder = (ViewHolder)convertView.getTag();
		}
		ImageView imageView = (ImageView) vi.findViewById(R.id.grid_item_image);
		imageView.setImageResource(R.drawable.neulife_product);
		imageView.setTag(Integer.valueOf(position));
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Integer position1 = (Integer) v.getTag();
				SessionStorage.pid = listData.get(position1).getProductId();
				SessionStorage.flavCode = listData.get(position).getFlavourCode();
				SessionStorage.sizCode = listData.get(position).getSizeCode();
				SessionStorage.skuId_prod_listing = listData.get(position).getProductId();
				Log.d("SKU ID", "" + listData.get(position1).getProductId());
				Log.w("SessionStorage.pid: From Adapter", ""
						+ SessionStorage.pid);
				//Log.w("listData.get(position): ", "" + position1);
				Log.w("SessionStorage.sizCode and SessionStorage.flavCode: "+SessionStorage.sizCode+" "+SessionStorage.flavCode,
						"From adapter");
				productDetailsPage();
			}
		});

		// Cart Functionality
		holder.cart = (Button) vi.findViewById(R.id.button1);
		holder.cart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				
				Integer position1 = (Integer) view.getTag();
				SessionStorage.pid = listData.get(position).getProductId();
				SessionStorage.flavCode = listData.get(position).getFlavourCode();
				SessionStorage.sizCode = listData.get(position).getSizeCode();
				SessionStorage.skuId_prod_listing = listData.get(position).getProductId();
				Log.d("SKU ID", "" + listData.get(position).getProductId());
				Log.w("SessionStorage.pid: From Adapter", ""
						+ SessionStorage.pid);
				Log.w("listData.get(position): ", "" + position);
				Log.w("onClick on Image: getProductId()",
						"" + listData.get(position).getProductId());
				productDetailsPage();
				
				/*Log.d("onCLick CART(p_id)", ""
						+ listData.get(position).getProductId());
				Log.w("SessionStorage.pid: oNClick: CART", ""
						+ SessionStorage.pid);
				Log.w("listData.get(position): ", "" + position);
				Log.w("onClick CART: getProductId()",
						"" + listData.get(position).getProductId());
				SessionStorage.cartPresent = 1;
				SessionStorage.cartNumberOfProducts += 1;
				SessionStorage.cartSubTotal += Double.parseDouble(listData.get(
						position).getPPrice());
				SessionStorage.cid += listData.get(position).getProductId()
						+ ",";
				addToCart(position);*/
			}
		});

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

					Log.d("Wishlist Details", "UserId:" + SessionStorage.userId
							+ " skuid:" + skuid + " pcode:" + pcode
							+ " Flavour:" + flavour + " size:" + size
							+ " color:" + color);

					AsyncCallWSWishlist obj = new AsyncCallWSWishlist();
					obj.execute();

					addToWishlist();
				}

			}
		});

		holder.tv_grid_item_label.setText(listData.get(position)
				.getProductName());
		Log.w("listData.get(position).getProductNAme",
				"" + listData.get(position).getProductName());
		/*holder.tv_Discount.setText(listData.get(position).getDiscount()
				+ "% off");*/
		holder.tv_grid_item_label_static.setText(listData.get(position)
				.getProductCode());
		
		double oP = Double.parseDouble(listData.get(position).getOPrice());
		int resOPrice = (int) (oP);
		//int oPrice = Integer.parseInt(listData.get(position).getOPrice());
		//holder.tv_OPrice.setText(listData.get(position).getOPrice() + "/-");
		holder.tv_OPrice.setText(resOPrice + "/-");
		
		double pP = Double.parseDouble(listData.get(position).getPPrice());
		int resPPrice = (int) (pP);
		holder.tv_PPrice.setText(resPPrice + "/-");
		
		if (listData.get(position).getRating() == null
				|| listData.get(position).getReview().isEmpty()) {
			holder.tv_Rating.setText("0 Rating");
		} else {
			holder.tv_Rating.setText("0 Rating"/*
												 * listData.get(position).getRating
												 * ()
												 */);
		}

		if (listData.get(position).getReview() == null
				|| listData.get(position).getReview().isEmpty()) {
			holder.tv_Review.setText("0 Reviews");
		} else {
			holder.tv_Review.setText("0 Reviews"/*
												 * listData.get(position).getReview
												 * ()
												 */);
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
		add.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		add.show();
	}

	protected void productDetailsPage() {
		Intent intent = new Intent(this.activity.getApplicationContext(),
				ProductDetails.class);
		this.activity.startActivity(intent);
	}

	// Webservice for authentication
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

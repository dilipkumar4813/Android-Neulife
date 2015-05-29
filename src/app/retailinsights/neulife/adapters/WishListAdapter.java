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
import app.retailinsights.neulife.account.WishList;
import app.retailinsights.neulife.bean.BeanWishlistListing;
import app.retailinsights.neulife.database.DatabaseHandler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WishListAdapter extends BaseAdapter implements OnClickListener {

	private Context context;  
	private Activity activity;
	ArrayList<BeanWishlistListing> alProductListing;
	int it = 0;
	int resPrice;

	private static final String METHOD4 = "removeWishList";
	private final String SOAP_ACTION4 = SessionStorage.webserviceNamespace
			+ "service.php/removeWishList";
	public int status;
	public String skuId;

	// Constructor to initialize values
	public WishListAdapter(Context context,
			ArrayList<BeanWishlistListing> alProductListing) {
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

		public ImageView ivProdImage, deleteWishlist;
		public TextView tvProdTitle;
		public TextView tvProdSubTitle;
		public TextView tvContainer;
		public TextView tvQuantity;
		public TextView tvOriginalPrice;
		public TextView tvDiscountPrice;
		public TextView tvOff;
		public TextView tvPrice;
		public Button btnAdToCart;
		public EditText etQuantity;

	}

	// Number of times getView method call depends upon gridValues.length

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		// LayoutInflator to call external grid_item.xml file
		View vi = convertView;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// get layout from grid_item.xml
		
		//View listView = null;

		if (vi == null) {

			//listView = new View(context);

			// get layout from grid_item.xml
			//vi = inflater.inflate(R.layout.lv_row_wishlist, null);
			vi = inflater.inflate(R.layout.lv_row_wishlist, parent, false);
			// set value into textview
			holder = new ViewHolder();
			
			holder.etQuantity = (EditText) vi.findViewById(R.id.etQuantity);
			holder.deleteWishlist = (ImageView) vi
					.findViewById(R.id.deleteWishlist);
			holder.deleteWishlist.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					skuId = alProductListing.get(position).getSKU();
                    Log.w("SKU fromwebservice", ""+alProductListing.get(position).getSKU());
					AsyncCallWSDeleteWishlist obj = new AsyncCallWSDeleteWishlist();
					obj.execute();
				}
			});

			holder.tvProdTitle = (TextView) vi
					.findViewById(R.id.tvAdTiltle);
			holder.tvPrice = (TextView) vi
					.findViewById(R.id.oPrice);
			holder.tvProdTitle.setText(alProductListing.get(position)
					.getProductName());
			/*holder.tvPricefdctview1 = (TextView) listView
					.findViewById(R.id.tvPricefdctview1);*/
			//holder.tvPricefdctview1.setText("");// alProductListing.get(position).getPrice());
			Log.w("Wishlist prod name and price: ",
					"" + alProductListing.get(position).getProductName()
							+ alProductListing.get(position).getPrice());
			holder.btnAdToCart = (Button) vi
					.findViewById(R.id.buttonAdToCart);
			holder.btnAdToCart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Toast.makeText(context, "tvProdTitle clicked..",
					// Toast.LENGTH_SHORT).show();
					addToCartBtnClicked();
				}

				private void addToCartBtnClicked() {
					/*SessionStorage.cartPresent = 1;
					SessionStorage.cartNumberOfProducts += 1;
					SessionStorage.cartSubTotal += Double
							.parseDouble(alProductListing.get(position)
									.getPrice());*/
					SessionStorage.pid = alProductListing.get(position)
							.getSKU();

					Intent intent = new Intent(context.getApplicationContext(),
							ProductDetails.class);
					context.startActivity(intent);

					skuId = alProductListing.get(position).getSKU();
					AsyncCallWSDeleteWishlist obj = new AsyncCallWSDeleteWishlist();
					obj.execute();
				}
			});

			// Cart Functionality
			/*
			 * holder.tvProdPrice = (TextView) listView
			 * .findViewById(R.id.tvPriceInListview);
			 * holder.tvProdPrice.setText("6,000/-");
			 */

			// Wishlist Functionality
			/*
			 * holder.ivProdImage = (ImageView) listView
			 * .findViewById(R.id.ivAdImage1);
			 * holder.ivProdImage.setImageResource(R.drawable.neulife_product);
			 */

		} else {
			vi = (View) convertView;
		}
		final TextView tvPrice = (TextView) vi
				.findViewById(R.id.oPrice);
		Log.w("alProductListing.get(position).getPrice(): ", ""+alProductListing.get(position).getPrice());
		tvPrice.setText(alProductListing.get(position).getPrice()+"/-");
		final EditText etQuantity = (EditText) vi.findViewById(R.id.etQuantity);
		// Changing the product quantity as per the product details page
				etQuantity.setText(alProductListing.get(position).getQuantity());
				SessionStorage.cartNumberOfProducts += 1;
				etQuantity.setTag(Integer.valueOf(position));
				etQuantity.addTextChangedListener(new TextWatcher() {
					public void onTextChanged(CharSequence s, int start, int before,
							int count) {

					}

					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {

					}

					public void afterTextChanged(Editable s) {
						//String addToCartTable = alProductListing.get(position).getwishlistId();

						/*String[] kid_arr = SessionStorage.cid.split(",");
						String[] cid_arr = SessionStorage.arraySKU.split(",");*/
						/*SessionStorage.arraySKU = "";
						SessionStorage.cid = "";
						for (int k = 0; k < cid_arr.length; k++) {
							if (cid_arr[k]
									.equals(alProductListing.get(position).getwishlistId())) {
								SessionStorage.arraySKU += "0,";
								SessionStorage.cid += "0,";
								;
								Log.w("SKU ID: in for loop", "" + skuId);
							} else {
								SessionStorage.arraySKU += cid_arr[k] + ",";
								SessionStorage.cid += cid_arr[k] + ",";
							}
						}*/

						//int pId = Integer.parseInt(addToCartTable);
						//Log.w("ProdId(in afterTextChanged): ", "" + pId);
						String qty = etQuantity.getText().toString().trim();

						if (qty.isEmpty()) {
							qty = "0";
						}
						/*for (int q = 0; q < Integer.parseInt(qty); q++) {
							SessionStorage.arraySKU += alProductListing.get(position)
									.getwishlistId() + ",";
							SessionStorage.cid += alProductListing.get(position).getwishlistId()
									+ ",";
						}*/

						/*
						 * Double finalTotal =
						 * Double.valueOf(SessionStorage.cartSubTotal) -
						 * Double.parseDouble(tvProdPrice.getText().toString());
						 */

						

						Log.w("Qty", "" + qty);
						if (etQuantity.getText().toString().length() > 0)
							it = Integer.parseInt(etQuantity.getText().toString());
						if (!etQuantity.getText().toString().isEmpty()) {
							double pP = Double.parseDouble(alProductListing.get(position)
									.getPrice());
							resPrice = 0;
							resPrice = (int) (pP * it);
							Log.w("Multiply res: ", "" + resPrice);
							Log.w("position: ", "" + position);
							

							/*Double finalTotal = null;
							if (!etQuantity.getText().toString().isEmpty()) {
								finalTotal = Double.valueOf(SessionStorage.cartSubTotal)
										- Double.parseDouble(tvPrice.getText()
												.toString());
								finalTotal+=Double.valueOf(resPrice);
								SessionStorage.cartSubTotal = finalTotal;
								Log.d("Final Total:", "" + finalTotal);
								((Cart) context).changeCartTotal();
							}*/
							/*double roundOff = Math.round(resPrice * 100.0) / 100.0;
							//tvProdPrice.setText("" + Math.ceil(resPrice));
							tvPrice.setText("" +roundOff+"/-");
							Log.w("Math.ceil(res)", "" + Math.ceil(resPrice));*/
							//DatabaseHandler databaseHandler = new DatabaseHandler(context);
							//databaseHandler.addToCartTable(pId, it);
						}
						
					}
				});

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

	// Webservice for deleting the wishlist item
	public void deleteWishlistItem() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD4);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("userId", SessionStorage.userId);
		request.addProperty("skuId", skuId);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION4, envelope);
			Object response = envelope.getResponse();

			Log.d("Delete Wishlist Details", response.toString());

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

	private class AsyncCallWSDeleteWishlist extends
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
				Toast.makeText(context,
						"Oops something went wrong! Try Again",
						Toast.LENGTH_SHORT).show();
			} else {
				((Activity)context).finish();
				Intent intent = new Intent(context, WishList.class);
				context.startActivity(intent);
				Toast.makeText(context, "Product removed from your wishlist", Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				deleteWishlistItem();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
}

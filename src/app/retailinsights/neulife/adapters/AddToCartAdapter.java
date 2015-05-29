package app.retailinsights.neulife.adapters;

import java.util.ArrayList;
import android.text.TextWatcher;
import java.util.List;

import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.ProductDetails;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.ShareDialogue;
import app.retailinsights.neulife.account.WishList;
import app.retailinsights.neulife.adapters.ProductListingAdapter.ViewHolder;
import app.retailinsights.neulife.bean.BeanProductListing;
import app.retailinsights.neulife.bean.BeanProductListingLV;
import app.retailinsights.neulife.database.DatabaseHandler;
import app.retailinsights.neulife.productlisting.ProductListing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class AddToCartAdapter extends BaseAdapter implements OnClickListener {

	private Context context;
	// private final String[] prod_ids;
	// private Activity activity;
	ArrayList<BeanProductListingLV> listData;
	String skuId;
	int it = 0;
	int resPrice;

	// Constructor to initialize values
	public AddToCartAdapter(Context context,
			ArrayList<BeanProductListingLV> listData) {
		this.context = context;
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

		public ImageView ivProdImage;
		public TextView tvProdTitle;
		public TextView tvProdPrice;
		public EditText etQuantity;
		public TextView tvAvailabilty;
		public ImageView deleteFromCart;

	}

	// Number of times getView method call depends upon gridValues.length

	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		// LayoutInflator to call external grid_item.xml file
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View vi = convertView;

		if (vi == null) {

			// listView = new View(context);

			// get layout from grid_item.xml
			vi = inflater.inflate(R.layout.lv_row_shopping_cart, parent, false);

			// set value into textview
			holder = new ViewHolder();

			holder.tvProdTitle = (TextView) vi.findViewById(R.id.tvAdTiltle);
			holder.etQuantity = (EditText) vi.findViewById(R.id.etQuantity);
			holder.ivProdImage = (ImageView) vi.findViewById(R.id.ivAdImage);
			// Cart Functionality
			holder.tvProdPrice = (TextView) vi
					.findViewById(R.id.tvPriceInListview);
			Log.w("possition:(set price)", "" + position);
			holder.tvAvailabilty = (TextView) vi
					.findViewById(R.id.tvAvailabilty);
			holder.deleteFromCart = (ImageView) vi
					.findViewById(R.id.deleteFromCart);
		} else {
			holder = (ViewHolder) vi.getTag();
		}
		final TextView tvProdPrice = (TextView) vi
				.findViewById(R.id.tvPriceInListview);
		tvProdPrice.setText(listData.get(position).getPPrice());
		ImageView ivProdImage = (ImageView) vi.findViewById(R.id.ivAdImage);
		ivProdImage.setImageResource(R.drawable.neulife_product);
		TextView tvProdTitle = (TextView) vi.findViewById(R.id.tvAdTiltle);
		tvProdTitle.setText(listData.get(position).getProductName());
		DatabaseHandler databaseHandler = new DatabaseHandler(context);
		databaseHandler.addToCartTable(
				Integer.parseInt(listData.get(position).getProductId()), 1);
		final EditText etQuantity = (EditText) vi.findViewById(R.id.etQuantity);

		// Change the price based on quantity
		Double price = Double.parseDouble(listData.get(position).getPPrice())
				* Integer.parseInt(listData.get(position).getProdQuantity());
		// Set price
		double roundOff = Math.round(price * 100.0) / 100.0;
		//tvProdPrice.setText(String.valueOf(price));
		tvProdPrice.setText(roundOff+"");//+"/-");
		
		
		// Changing the product quantity as per the product details page
		etQuantity.setText(listData.get(position).getProdQuantity());
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
				String addToCartTable = listData.get(position).getProductId();

				String[] kid_arr = SessionStorage.cid.split(",");
				String[] cid_arr = SessionStorage.arraySKU.split(",");
				SessionStorage.arraySKU = "";
				SessionStorage.cid = "";
				for (int k = 0; k < cid_arr.length; k++) {
					if (cid_arr[k]
							.equals(listData.get(position).getProductId())) {
						SessionStorage.arraySKU += "0,";
						SessionStorage.cid += "0,";
						;
						Log.w("SKU ID: in for loop", "" + skuId);
					} else {
						SessionStorage.arraySKU += cid_arr[k] + ",";
						SessionStorage.cid += cid_arr[k] + ",";
					}
				}

				int pId = Integer.parseInt(addToCartTable);
				Log.w("ProdId(in afterTextChanged): ", "" + pId);
				String qty = etQuantity.getText().toString().trim();

				if (qty.isEmpty()) {
					qty = "0";
				}
				for (int q = 0; q < Integer.parseInt(qty); q++) {
					SessionStorage.arraySKU += listData.get(position)
							.getProductId() + ",";
					SessionStorage.cid += listData.get(position).getProductId()
							+ ",";
				}

				/*
				 * Double finalTotal =
				 * Double.valueOf(SessionStorage.cartSubTotal) -
				 * Double.parseDouble(tvProdPrice.getText().toString());
				 */

				

				Log.w("Qty", "" + qty);
				if (etQuantity.getText().toString().length() > 0)
					it = Integer.parseInt(etQuantity.getText().toString());
				if (!etQuantity.getText().toString().isEmpty()) {
					double pP = Double.parseDouble(listData.get(position)
							.getPPrice());
					resPrice = 0;
					resPrice = (int) (pP * it);
					Log.w("Multiply res: ", "" + resPrice);
					Log.w("position: ", "" + position);
					
					Double finalTotal = null;
					if (!etQuantity.getText().toString().isEmpty()) {
						finalTotal = Double.valueOf(SessionStorage.cartSubTotal)
								- Double.parseDouble(tvProdPrice.getText()
										.toString());
						finalTotal+=Double.valueOf(resPrice);
						SessionStorage.cartSubTotal = finalTotal;
						Log.d("Final Total:", "" + finalTotal);
						((Cart) context).changeCartTotal();
					}
					double roundOff = Math.round(resPrice * 100.0) / 100.0;
					//tvProdPrice.setText("" + Math.ceil(resPrice));
					tvProdPrice.setText("" +roundOff);//+"/-");
					Log.w("Math.ceil(res)", "" + Math.ceil(resPrice));
					DatabaseHandler databaseHandler = new DatabaseHandler(context);
					databaseHandler.addToCartTable(pId, it);
				}
				
			}
		});

		/*
		 * etQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener()
		 * { public void onFocusChange(View v, boolean hasFocus) { if
		 * (!hasFocus) { EditText et1 = (EditText)
		 * v.findViewById(R.id.etQuantity);
		 * et1.setText(et1.getText().toString().trim()); } } });
		 */

		// etQuantity.setText(""+it);

		final TextView tvAvailabilty = (TextView) vi
				.findViewById(R.id.tvAvailabilty);
		Log.w("listData.get(position).getPPrice().equals: ",
				"" + listData.get(position).getPPrice());
		if (listData.get(position).getPAvaiability().equals("1")) {
			tvAvailabilty.setText("In Stock");

		} else {
			tvAvailabilty.setText("Out of stock");
			tvAvailabilty.setTextColor(context.getResources().getColor(
					R.color.wallet_holo_blue_light));
		}
		ImageView deleteFromCart = (ImageView) vi
				.findViewById(R.id.deleteFromCart);

		deleteFromCart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				skuId = listData.get(position).getSKUId();
				Log.w("Pos of remove prod: ", "" + position);
				Log.w("SKU ID: ", "" + skuId);
				String[] kid_arr = SessionStorage.cid.split(",");
				String[] cid_arr = SessionStorage.arraySKU.split(",");
				SessionStorage.arraySKU = "";
				SessionStorage.cid = "";
				for (int k = 0; k < cid_arr.length; k++) {
					if (!cid_arr[k].equals(skuId)) {
						SessionStorage.arraySKU += cid_arr[k] + ",";
						SessionStorage.cid += cid_arr[k] + ",";
						Log.w("SKU ID: in for loop", "" + skuId);
					}
				}
				Log.w("SessionStorage.arraySKU: ", "" + SessionStorage.arraySKU);
				// Calling webservice method from the adapter
				SessionStorage.cartNumberOfProducts = SessionStorage.cartNumberOfProducts - 1;
				SessionStorage.cartSubTotal = SessionStorage.cartSubTotal
						- Double.parseDouble(listData.get(position).getPPrice());
				((Cart) context).adapterCall();
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
}

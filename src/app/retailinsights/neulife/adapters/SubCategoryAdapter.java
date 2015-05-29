package app.retailinsights.neulife.adapters;

import java.util.ArrayList;
import java.util.List;

import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.ShareDialogue;
import app.retailinsights.neulife.account.WishList;
import app.retailinsights.neulife.bean.BeanProductListing;
import app.retailinsights.neulife.bean.BeanProductListingLV;
import app.retailinsights.neulife.productlisting.ProductListing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
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

public class SubCategoryAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<String> listData;
	
	public SubCategoryAdapter(Context context, ArrayList<String> listData) {
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
	}

	// Number of times getView method call depends upon gridValues.length

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		// LayoutInflator to call external grid_item.xml file

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View listView = null;

		if (convertView == null) {

			listView = new View(context);

			// get layout from grid_item.xml
			listView = inflater.inflate(R.layout.lv_row_subcategory,
					null);

			// set value into textview
			holder = new ViewHolder();
			
			holder.tvProdTitle = (TextView) listView
					.findViewById(R.id.textView1);
			holder.tvProdTitle.setText(listData.get(position));

		} else {
			listView = (View) convertView;
		}

		//listView.setOnClickListener(new OnItemClickListener(position));
		return listView;
	}


}

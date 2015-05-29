package app.retailinsights.neulife.adapters;

import java.util.List;
import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.ShareDialogue;
import app.retailinsights.neulife.account.WishList;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class NotificationAdapter extends BaseAdapter implements OnClickListener {

	private Context context;
	private final String[] nTitle;
	private final String[] nDescription;
	private final String[] nImage;
	private Activity activity;

	// Constructor to initialize values
	public NotificationAdapter(Context context, String[] notTitle, String[] notDescription, String[] notImage) {
		this.context = context;
		this.nTitle = notTitle;
		this.nDescription = notDescription; 
		this.nImage = notImage;
	}

	@Override
	public int getCount() {
		// Number of times getView method call depends upon gridValues.length
		return nTitle.length;
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
		public TextView notificationTitle;
		public TextView notificationDescription;
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
			listView = inflater.inflate(R.layout.lv_notification, null);

			// set value into textview
			holder = new ViewHolder();

			holder.notificationTitle = (TextView) listView
					.findViewById(R.id.notificationTitle);
			holder.notificationTitle.setText(nTitle[position]);

			holder.notificationDescription = (TextView) listView
					.findViewById(R.id.notificationDescription);
			holder.notificationDescription.setText(nDescription[position]);

			holder.notificationTitle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Toast.makeText(context, "tvProdTitle clicked..",
					// Toast.LENGTH_SHORT).show();
					// addToCartBtnClicked();
				}
			});

		} else {
			listView = (View) convertView;
		}

		listView.setOnClickListener(new OnItemClickListener(position));
		return listView;
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

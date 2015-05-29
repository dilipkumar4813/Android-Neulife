package app.retailinsights.neulife.productlisting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.InternetConnection;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.account.LoginMain;
import app.retailinsights.neulife.account.ManageAccount;
import app.retailinsights.neulife.account.Notifications;
import app.retailinsights.neulife.account.WishList;
import app.retailinsights.neulife.adapters.ProductListingAdapter;
import app.retailinsights.neulife.adapters.ProductListingListviewAdapter;
import app.retailinsights.neulife.adapters.SubCategoryAdapter;
import app.retailinsights.neulife.bean.BeanProductListing;
import app.retailinsights.neulife.database.DatabaseHandler;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class NewArrival extends ListActivity {

	ProgressDialog pd;
	private static final String METHOD = "newArrivals";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/newArrivals";
	ArrayList<BeanProductListing> alProductListing;
	BeanProductListing beanProductListing;
	GridView gridView;
	ImageView backBtn, Notifications;
	int listFlag = 0;
	ListView listView;
	ImageView gridV;
	TextView tvGrid, productCount, tvNoneApplied;
	RelativeLayout viewButton;
	int whichView = 1;
	// sort n filter vars
	ArrayList<String> listData;
	AlertDialog levelDialog = null;
	LinearLayout ll_filter;
	RelativeLayout rl_filter, viewButton1, rl_cancel, rl_sort, rl_filter1;
	HashMap<String, ArrayList<String>> mapFilterData;
	ArrayList<Integer> listviewPos;
	ListView listview, lvSubCategories;
	ArrayList<String> alSubCategory;
	DatabaseHandler db;
	ArrayList<String> alBrandsSelected;
	ArrayList<String> alPriceSelected;
	ArrayList<String> alFlavourSelected;
	ArrayList<BeanProductListing> hmProductsOnPrice;
	String[] brand = {  };
	String[] price = { "0000-5000", "5000-10000", "10000-15000" };
	String[] ingrediants = { "ingrediants", "ingrediants", "ingrediants",
			"ingrediants", "ingrediants", "ingrediants", "ingrediants",
			"ingrediants" };
	String[] flavours = { "flavours", "flavours", "flavours", "flavours",
			"flavours", "flavours", "flavours", "flavours" };
	String[] more = { "more", "more", "more", "more", "more", "more", "more",
			"more" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.product_listing);

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(NewArrival.this);
			add.show();

			int wid = metrics.widthPixels / 2 + 180;
			int heig = metrics.heightPixels / 3;
			add.getWindow().setLayout(wid, heig);
		}

		initialize();
		basicEvents();
		alSubCategory = new ArrayList<String>();
		alBrandsSelected = new ArrayList<String>();
		alPriceSelected = new ArrayList<String>();
		alFlavourSelected = new ArrayList<String>();
		// Footer Related Methods
		footerInitialize();
		footerEvents();

		TextView tvHeader = (TextView) findViewById(R.id.tvHeader);
		tvHeader.setText("New Arrival");

		// Get gridview object from xml file
		gridView = (GridView) findViewById(R.id.gridView1);
		// sort and filter code
		viewButton1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				db = new DatabaseHandler(getApplicationContext());
				mapFilterData = new HashMap<String, ArrayList<String>>();
				ArrayList<BeanProductListing> alProducts = new ArrayList<BeanProductListing>();

				mapFilterData.put("brand", alBrandsSelected);
				mapFilterData.put("price", alPriceSelected);
				mapFilterData.put("flavour", alFlavourSelected);
				alProducts = db.getProductsOnFilteer(mapFilterData);
				db = new DatabaseHandler(getApplicationContext());
				db.resetTablesSort();
				for (int i = 0; i < alProducts.size(); i++) {
					db.addProductsSort(alProducts.get(i).getProductId(),
						alProducts.get(i).getProductName(),
						alProducts.get(i).getProductCode(),
						alProducts.get(i).getProdSize(),
						alProducts.get(i).getOPrice(),
						alProducts.get(i).getPPrice(),
						alProducts.get(i).getPAvaiability(),
						alProducts.get(i).getProdQuantity(),
						alProducts.get(i).getImgUrl(),
						alProducts.get(i).getFlavour(),
						alProducts.get(i).getReview(),
						alProducts.get(i).getRating(),
						alProducts.get(i).getBrandName(),
						alProducts.get(i).getFacts(),
						alProducts.get(i).getIngrediants(),
						alProducts.get(i).getDescription(),
						alProducts.get(i).getSKUID(),
						alProducts.get(i).getSKUNAME(),
						alProducts.get(i).getSKUCODE(),
						alProducts.get(i).getDiscount(),
						alProducts.get(i).getFlavourCode(),
						alProducts.get(i).getSizeCode());
				}

				gridView.setAdapter(new ProductListingAdapter(NewArrival.this,
						alProducts));

				listView.setAdapter(new ProductListingListviewAdapter(
						NewArrival.this, alProducts));
				lvSubCategories.setVisibility(View.GONE);
				listview.setVisibility(View.GONE);
				gridView.setVisibility(View.VISIBLE);

				ll_filter.setVisibility(View.VISIBLE);

				viewButton1.setVisibility(View.GONE);
				viewButton.setVisibility(View.VISIBLE);
				tvNoneApplied.setText("");

				rl_cancel.setVisibility(View.GONE);
				rl_filter.setVisibility(View.VISIBLE);

				rl_filter1.setVisibility(View.GONE);
				rl_sort.setVisibility(View.VISIBLE);

				listview.clearChoices();
				listviewPos.clear();
				alBrandsSelected.clear();
				alPriceSelected.clear();
				alFlavourSelected.clear();

			}
		});
		/*
		 * get procucts on price sort
		 */
		rl_sort.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Strings to Show In Dialog with Radio Buttons
				final CharSequence[] items = { " Price High to Low ",
						" Price Low to High " };

				// Creating and Building the Dialog
				AlertDialog.Builder builder = new AlertDialog.Builder(
						NewArrival.this);
				builder.setTitle("Sort order");
				builder.setSingleChoiceItems(items, -1,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {

								switch (item) {
								case 0:
									if(tvNoneApplied.getText().toString().equals(""))
									{
										db = new DatabaseHandler(
												getApplicationContext());
										hmProductsOnPrice = new ArrayList<BeanProductListing>();
										hmProductsOnPrice = db
												.getProductsOnPriceDESCSort();
										gridView.setAdapter(new ProductListingAdapter(
												NewArrival.this, hmProductsOnPrice));
					
										listView.setAdapter(new ProductListingListviewAdapter(
												NewArrival.this, hmProductsOnPrice));
									}
									else
									{
										db = new DatabaseHandler(
												getApplicationContext());
										hmProductsOnPrice = new ArrayList<BeanProductListing>();
										hmProductsOnPrice = db
												.getProductsOnPriceDESC();
										gridView.setAdapter(new ProductListingAdapter(
												NewArrival.this, hmProductsOnPrice));
					
										listView.setAdapter(new ProductListingListviewAdapter(
												NewArrival.this, hmProductsOnPrice));
									}
									
									break;
								case 1:
									if(tvNoneApplied.getText().toString().equals(""))
									{
										db = new DatabaseHandler(
												getApplicationContext());
										hmProductsOnPrice = new ArrayList<BeanProductListing>();
										hmProductsOnPrice = db
												.getProductsOnPriceASCSort();
										gridView.setAdapter(new ProductListingAdapter(
												NewArrival.this, hmProductsOnPrice));
					
										listView.setAdapter(new ProductListingListviewAdapter(
												NewArrival.this, hmProductsOnPrice));
									}else
									{ 
										db = new DatabaseHandler(
												getApplicationContext());
										hmProductsOnPrice = new ArrayList<BeanProductListing>();
										hmProductsOnPrice = db
												.getProductsOnPriceASC();
										gridView.setAdapter(new ProductListingAdapter(
												NewArrival.this, hmProductsOnPrice));
					
										listView.setAdapter(new ProductListingListviewAdapter(
												NewArrival.this, hmProductsOnPrice));
										
									}
				
									break;
								/*
								 * case 2: // Your code when 3rd option seletced
								 * break; case 3: // Your code when 4th option
								 * seletced break;
								 */
				
								}
								levelDialog.dismiss();
							}
						});
				levelDialog = builder.create();
				levelDialog.show();
			}
		});

		// filter
		listData = new ArrayList<String>();
		listData.add("Brand");
		listData.add("Price");
		listData.add("Flavours");
		listData.add("Ingrediants");
		// listData.add("More");

		lvSubCategories.setAdapter(new SubCategoryAdapter(
				getApplicationContext(), listData));

		// -- Display mode of the ListView
		listview = getListView();
		// listview.setChoiceMode(listview.CHOICE_MODE_NONE);
		// listview.setChoiceMode(listview.CHOICE_MODE_SINGLE);
		listview.setChoiceMode(listview.CHOICE_MODE_MULTIPLE);

		listview.clearChoices();
		// -- text filtering
		listview.setTextFilterEnabled(true);

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_checked, brand));
		alSubCategory = new ArrayList<String>();
		listviewPos = new ArrayList<Integer>();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (listFlag == 1) {
					String textValue = parent.getAdapter().getItem(position)
							.toString();
					Log.w("position of multiple select list n value: ", ""
							+ position + " : " + textValue);
					listviewPos.add(position);
					alBrandsSelected.add(textValue);
					alSubCategory.add(textValue);
				} else if (listFlag == 2) {
					String textValue = parent.getAdapter().getItem(position)
							.toString();
					Log.w("position of multiple select list n value: ", ""
							+ position + " : " + textValue);
					listviewPos.add(position);
					alPriceSelected.add(textValue);
					alSubCategory.add(textValue);
				} else if (listFlag == 3) {
					String textValue = parent.getAdapter().getItem(position)
							.toString();
					Log.w("position of multiple select list n value: ", ""
							+ position + " : " + textValue);
					listviewPos.add(position);
					alFlavourSelected.add(textValue);
					alSubCategory.add(textValue);
				}
			}
		});

		lvSubCategories.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.w("position of subcategory list ", "" + position);
				switch (position) {
				case 0:
					listFlag = 1;
					db = new DatabaseHandler(getApplicationContext());
					brand = db.getBrandNames();
					setListAdapter(new ArrayAdapter<String>(
							getApplicationContext(),
							android.R.layout.simple_list_item_checked, brand));
					break;
				case 1:
					listFlag = 2;
					setListAdapter(new ArrayAdapter<String>(
							getApplicationContext(),
							android.R.layout.simple_list_item_checked, price));
					break;
				case 2:
					listFlag = 3;
					db = new DatabaseHandler(getApplicationContext());
					flavours = db.getFlavours();
					setListAdapter(new ArrayAdapter<String>(
							getApplicationContext(),
							android.R.layout.simple_list_item_checked, flavours));
					break;

				default:
					break;
				}
			}
		});
		rl_filter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listview.clearChoices();
				alPriceSelected.clear();
				alBrandsSelected.clear();
				alFlavourSelected.clear();
				gridView.setVisibility(View.GONE);
				listView.setVisibility(View.GONE);
				ll_filter.setVisibility(View.VISIBLE);
				viewButton.setVisibility(View.GONE);
				viewButton1.setVisibility(View.VISIBLE);
				rl_filter.setVisibility(View.GONE);
				rl_cancel.setVisibility(View.VISIBLE);
				rl_sort.setVisibility(View.GONE);
				rl_filter1.setVisibility(View.VISIBLE);
				listview.setVisibility(View.VISIBLE);
				lvSubCategories.setVisibility(View.VISIBLE);
			}
		});
		rl_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listview.setVisibility(View.GONE);
				lvSubCategories.setVisibility(View.GONE);
				gridView.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
				ll_filter.setVisibility(View.GONE);
				viewButton.setVisibility(View.VISIBLE);
				viewButton1.setVisibility(View.GONE);
				rl_filter.setVisibility(View.VISIBLE);
				rl_cancel.setVisibility(View.GONE);
				rl_sort.setVisibility(View.VISIBLE);
				rl_filter1.setVisibility(View.GONE);
			}
		});
		AsyncCallWS some = new AsyncCallWS();
		some.execute();
	}

	public void initialize() {
		backBtn = (ImageView) findViewById(R.id.imageView1);
		Notifications = (ImageView) findViewById(R.id.imageView2);
		gridV = (ImageView) findViewById(R.id.gridV);
		tvGrid = (TextView) findViewById(R.id.tvGrid);
		viewButton = (RelativeLayout) findViewById(R.id.whichView);
		gridView = (GridView) findViewById(R.id.gridView1);
		listView = (ListView) findViewById(R.id.listView1);
		productCount = (TextView) findViewById(R.id.productCount);
		tvNoneApplied = (TextView) findViewById(R.id.tvNoneApplied);
		// filter ids
		productCount = (TextView) findViewById(R.id.productCount);
		ll_filter = (LinearLayout) findViewById(R.id.ll_filter);
		lvSubCategories = (ListView) findViewById(R.id.lvSubCategory);
		rl_filter = (RelativeLayout) findViewById(R.id.rl_filter);
		viewButton1 = (RelativeLayout) findViewById(R.id.whichView1);
		rl_cancel = (RelativeLayout) findViewById(R.id.rl_cancel);
		rl_sort = (RelativeLayout) findViewById(R.id.rl_sort);
		rl_filter1 = (RelativeLayout) findViewById(R.id.rl_filter1);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		return;
	}

	public void basicEvents() {

		viewButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (whichView == 0) {
					listView.setVisibility(View.INVISIBLE);
					gridView.setVisibility(View.VISIBLE);
					tvGrid.setText("List View");
					whichView = 1;
				} else {
					listView.setVisibility(View.VISIBLE);
					gridView.setVisibility(View.INVISIBLE);
					tvGrid.setText("Grid View");
					whichView = 0;
				}
			}
		});

		gridV.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (whichView == 0) {
					listView.setVisibility(View.INVISIBLE);
					gridView.setVisibility(View.VISIBLE);
					tvGrid.setText("List View");
					whichView = 1;
				} else {
					listView.setVisibility(View.VISIBLE);
					gridView.setVisibility(View.INVISIBLE);
					tvGrid.setText("Grid View");
					whichView = 0;
				}
			}
		});

		tvGrid.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (whichView == 0) {
					listView.setVisibility(View.INVISIBLE);
					gridView.setVisibility(View.VISIBLE);
					tvGrid.setText("List View");
					whichView = 1;
				} else {
					listView.setVisibility(View.VISIBLE);
					gridView.setVisibility(View.INVISIBLE);
					tvGrid.setText("Grid View");
					whichView = 0;
				}
			}
		});

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		Notifications.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						Notifications.class);
				startActivity(inten1);
			}
		});
	}

	public void getNewArrivals() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("customerType", SessionStorage.customerType);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("Product Details", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("newarrivals");

			alProductListing = new ArrayList<BeanProductListing>();
			// Reading through the array of products
			for (int i = 0; i < jsonarray.length(); i++) {
				beanProductListing = new BeanProductListing();
				// this object inside array you can do whatever you want
				JSONObject object = jsonarray.getJSONObject(i);
				beanProductListing.setProductId(object.getString("id"));
				beanProductListing.setProductName(object.getString("name"));
				beanProductListing.setOPrice(object.getString("originalprice"));
				beanProductListing.setPPrice(object.getString("sellingprice"));
				beanProductListing.setProductCode(object.getString("pcode"));
				beanProductListing.setSizeCode(object.getString("sizeCode"));
				beanProductListing.setFlavourCode(object.getString("flavourCode"));
				beanProductListing.setFlavour(object.getString("flavour"));
				beanProductListing.setBrandName(object.getString("brand"));
				beanProductListing.setDiscount(object
						.getString("WebstoreDiscount"));
				// beanProductListing.setProdQuantity(object.getString("quantity"));
				/*
				 * beanProductListing.setIngrediants(object
				 * .getString("Indgredients"));
				 */
				beanProductListing.setPAvaiability(object
						.getString("availability"));
				beanProductListing.setReview(object.getString("review"));
				beanProductListing.setProdSize(object.getString("size"));
				/*
				 * beanProductListing.setDescription(object
				 * .getString("description"));
				 */
				beanProductListing.setRating(object.getString("rating"));
				beanProductListing.setImgUrl(object.getString("image"));
				alProductListing.add(beanProductListing);
			}

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
			pd = new ProgressDialog(NewArrival.this);
			pd.setMessage("Loading...");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();

			if (alProductListing == null) {
				Toast.makeText(getApplicationContext(), "No Products",
						Toast.LENGTH_SHORT).show();
				productCount.setText("(0)");
			} else {
				// Set custom adapter (GridAdapter) to gridview
				gridView.setAdapter(new ProductListingAdapter(NewArrival.this,
						alProductListing));

				listView.setAdapter(new ProductListingListviewAdapter(
						NewArrival.this, alProductListing));

				productCount.setText("(" + listView.getCount() + ")");
				db = new DatabaseHandler(getApplicationContext());
				db.resetTables();
				for (int i = 0; i < alProductListing.size(); i++) {
					db.addProducts(alProductListing.get(i).getProductId(),
							alProductListing.get(i).getProductName(),
							alProductListing.get(i).getProductCode(),
							alProductListing.get(i).getProdSize(),
							alProductListing.get(i).getOPrice(),
							alProductListing.get(i).getPPrice(),
							alProductListing.get(i).getPAvaiability(),
							alProductListing.get(i).getProdQuantity(),
							alProductListing.get(i).getImgUrl(),
							alProductListing.get(i).getFlavour(),
							alProductListing.get(i).getReview(),
							alProductListing.get(i).getRating(),
							alProductListing.get(i).getBrandName(),
							alProductListing.get(i).getFacts(),
							alProductListing.get(i).getIngrediants(),
							alProductListing.get(i).getDescription(),
							alProductListing.get(i).getSKUID(),
							alProductListing.get(i).getSKUNAME(),
							alProductListing.get(i).getSKUCODE(),
							alProductListing.get(i).getDiscount(),
							alProductListing.get(i).getFlavourCode(),
							alProductListing.get(i).getSizeCode()

					);
					Log.w("data to be inserted in table", "pid: "
							+ alProductListing.get(i).getProductId()
							+ "p name: "
							+ alProductListing.get(i).getProductName()
							+ "p code: "
							+ alProductListing.get(i).getProductCode()
							+ "Ingrediants: "
							+ alProductListing.get(i).getIngrediants()
							+ "p price: " + alProductListing.get(i).getOPrice()
							+ "o price: " + alProductListing.get(i).getOPrice());
				}
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				getNewArrivals();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	// Footer related methods
	ImageView accountBtn, wishlistImg, cartImg, searchImg, homeImg;

	public void footerInitialize() {
		searchImg = (ImageView) findViewById(R.id.searchImg);
		cartImg = (ImageView) findViewById(R.id.cartImg);
		accountBtn = (ImageView) findViewById(R.id.accountImg);
		wishlistImg = (ImageView) findViewById(R.id.wishlistImg);
		homeImg = (ImageView) findViewById(R.id.homeImg);
	}

	public void footerEvents() {

		homeImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						LandingScreen.class);
				startActivity(inten1);

			}
		});

		cartImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(), Cart.class);
				startActivity(inten1);
			}
		});

		accountBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (SessionStorage.clientId.equalsIgnoreCase("0")) {
					Toast.makeText(getApplicationContext(), "Please Login",
							Toast.LENGTH_SHORT).show();

					Intent inten1 = new Intent(getApplicationContext(),
							LoginMain.class);
					startActivity(inten1);
				} else {

					Intent inten1 = new Intent(getApplicationContext(),
							ManageAccount.class);
					startActivity(inten1);
				}

			}
		});

		wishlistImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (SessionStorage.clientId.equalsIgnoreCase("0")) {
					Toast.makeText(getApplicationContext(), "Please Login",
							Toast.LENGTH_SHORT).show();

					Intent inten1 = new Intent(getApplicationContext(),
							LoginMain.class);
					startActivity(inten1);
				} else {
					Intent inten1 = new Intent(getApplicationContext(),
							WishList.class);
					startActivity(inten1);
				}
			}
		});
		searchImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SessionStorage.search = 1;
				Intent inten1 = new Intent(getApplicationContext(),
						LandingScreen.class);
				startActivity(inten1);
			}
		});
	}

}

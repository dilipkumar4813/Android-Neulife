package app.retailinsights.neulife;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import app.retailinsights.neulife.account.LoginMain;
import app.retailinsights.neulife.account.ManageAccount;
import app.retailinsights.neulife.account.Notifications;
import app.retailinsights.neulife.account.WishList;
import app.retailinsights.neulife.adapters.SimilarProdsAdapter;
import app.retailinsights.neulife.bean.BeanProductListing;
import app.retailinsights.neulife.database.DatabaseHandler;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class ProductDetails extends Activity {

	ProgressDialog pd;
	private static final String METHOD = "getProductDetails";
	private static final String METHOD_PARAMS = "getProductParams";
	private static final String METHOD_SKU_INFO = "getSkuInfo";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/getProductDetails";
	int getSKUInfoSTATUS = 0;

	// Authentication webservice method for G+ and FB
	private static final String METHOD2 = "addToWishlist";
	private final String SOAP_ACTION2 = SessionStorage.webserviceNamespace
			+ "service.php/addToWishlist";
	private final String SOAP_ACTION_PRODUCT_PARAMS = SessionStorage.webserviceNamespace
			+ "service.php/getProductParams";
	private final String SOAP_ACTION_SKU_INFO = SessionStorage.webserviceNamespace
			+ "service.php/getSkuInfo";
	private String flavour, size, color, tFlavour, tSize, tColor, description;
	public int status;

	int positionF = -1;
	int positionS = -1;
	int count1 = 0;
	int count2 = 0;

	String skuId;
	String flavourCode;
	String sizeCode;

	String[] arrUniqueFlavourCode;
	String[] arrUniqueSizeCode;

	String arrSize[];
	String arrFlavour[];
	String arrSizeFinal[];
	String arrFlavourFinal[];

	String arrSizeCode[];
	String arrFlavourCode[];
	String arrSizeFinalCode[];
	String arrFlavourFinalCode[];

	ArrayAdapter<String> adapterSize;
	ArrayAdapter<String> adapterFlavour;

	ArrayList<BeanProductListing> alProdParams;
	ArrayList<BeanProductListing> alProdParamsFinal;
	BeanProductListing beanProductParams;

	/*
	 * Spinner spinnerSizePack; Spinner spinnerFlavours;
	 */
	LinearLayout ll_prod_details_header;
	RelativeLayout rlNoCombination;
	EditText etQty;
	final String[] choices1 = { "Size", "Size", "Size", "Size" };
	final String[] choices2 = { "Flavours", "Flavours", "Flavours", "Flavours" };
	ListView listViewSimProds;
	Button moreDetails;
	ImageView backBtn, notifications;
	Button share, addToCart, WishList;
	public String pName, pPrice, pQuantity, pDescription, pImage, oPrice,
			pAvailable, pCode, disc, pid;
	TextView productName, productCode, productAvailability, originalPrice,
			sellingPrice, discount;
	Spinner spnrSize, spnrFlavour;
	String firstPrice = "";
	int first = 0;
	ImageView rs1,rs2;

	HashMap<String, String> hmSizeSizeCode;
	HashMap<String, String> hmFlavorFlavorCode;
	HashMap<String, String> hmSizeCodeFlavorCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.product_details);

		initialize();
		basicEvents();

		// Footer Related Methods
		footerInitialize();
		footerEvents();

		AsyncCallWS some = new AsyncCallWS();
		some.execute();

		/*
		 * AsyncCallWS_SKUInfo obj = new AsyncCallWS_SKUInfo();
		 * obj.execute(alProdParams
		 * .get(0).getProductId(),alProdParams.get(0).getSizeCode
		 * (),alProdParams.get(0).getFlavourCode());
		 */
		etQty = (EditText) findViewById(R.id.etQty);
		etQty.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				// TODO Auto-generated method stub
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void afterTextChanged(Editable s) {
				int it = 0;
				String qty = etQty.getText().toString().trim();
				if (qty.isEmpty()) {
					qty = "1";
				}
				Log.w("Qty", "" + qty);
				if (etQty.getText().toString().length() > 0)
					it = Integer.parseInt(etQty.getText().toString());
				double oP = Double.parseDouble(alProdParamsFinal.get(0)
						.getOPrice());
				double pP = Double.parseDouble(alProdParamsFinal.get(0)
						.getPPrice());
				int resOPrice = (int) (oP * it);
				int resSPrice = (int) (pP * it);
				// Log.w("Multiply res: ", ""+res);
				/*
				 * if(positionF == -1 && positionS == -1) {
				 * sellingPrice.setText(0 + "/-"); originalPrice.setText(0 +
				 * "/-"); discount.setText("0% Off"); } else {
				 * sellingPrice.setText(pP*it + "/-");
				 * originalPrice.setText(oP*it + "/-");
				 * discount.setText(alProdParamsFinal
				 * .get(0).getDiscount()+"% Off"); }
				 */

				sellingPrice.setText(resSPrice + "/-");
				originalPrice.setText(resOPrice + "/-");
				discount.setText(alProdParamsFinal.get(0).getDiscount()
						+ "% Off");

			}
		});
		spnrFlavour.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				skuId = alProdParams.get(position).getProductId();
				flavourCode = arrUniqueFlavourCode[position];
				// sizeCode = arrUniqueSizeCode[position];
				Log.w("(spnrFlavor)skuid flavourCode sizeCode: ", "" + skuId
						+ " " + flavourCode + " " + sizeCode);
				if (count1 > 0) {
					AsyncCallWS_SKUInfo obj = new AsyncCallWS_SKUInfo();
					obj.execute("", sizeCode, flavourCode);
				} else {
					count1++;
				}

				/*
				 * positionF = position; if(position == 0) { positionF = -1;
				 * sellingPrice.setText(0 + "/-"); originalPrice.setText(0 +
				 * "/-"); discount.setText("0% Off"); }
				 */
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// flavourCode = arrUniqueFlavourCode[0];
			}

		});
		spnrSize.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				skuId = alProdParams.get(position).getProductId();
				// flavourCode = arrUniqueFlavourCode[position];
				sizeCode = arrUniqueSizeCode[position];
				Log.w("(spnrSize)skuids flavourCode sizeCode: ", "" + skuId
						+ " " + flavourCode + " " + sizeCode);
				if (count2 > 0) {
					AsyncCallWS_SKUInfo obj = new AsyncCallWS_SKUInfo();
					obj.execute("", sizeCode, flavourCode);
				} else {
					count2++;
				}

				/*
				 * positionS = position; if(position == 0) { positionS = -1;
				 * sellingPrice.setText(0 + "/-"); originalPrice.setText(0 +
				 * "/-"); discount.setText("0% Off"); }
				 */
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// sizeCode = arrUniqueSizeCode[0];
			}

		});

	}

	public void initialize() {
		notifications = (ImageView) findViewById(R.id.ivNotifications);
		listViewSimProds = (ListView) findViewById(R.id.list_similar_products);
		listViewSimProds.setAdapter(new SimilarProdsAdapter(this, choices1));

		moreDetails = (Button) findViewById(R.id.buttonMoreDeatils);
		share = (Button) findViewById(R.id.button2);
		addToCart = (Button) findViewById(R.id.button1);
		WishList = (Button) findViewById(R.id.button3);
		backBtn = (ImageView) findViewById(R.id.imageView1);

		// Product details
		productName = (TextView) findViewById(R.id.productName);
		productCode = (TextView) findViewById(R.id.productCode);
		productAvailability = (TextView) findViewById(R.id.productAvailability);
		originalPrice = (TextView) findViewById(R.id.oPrice);
		sellingPrice = (TextView) findViewById(R.id.sPrice);
		discount = (TextView) findViewById(R.id.discount);
		spnrSize = (Spinner) findViewById(R.id.spnrSize);
		spnrFlavour = (Spinner) findViewById(R.id.spnrFlaours);

		ll_prod_details_header = (LinearLayout) findViewById(R.id.ll_prod_details_header);
		rlNoCombination = (RelativeLayout) findViewById(R.id.rlNoCombination);
		
		rs1 = (ImageView) findViewById(R.id.rs1);
		rs2 = (ImageView) findViewById(R.id.rs2);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		return;
	}

	public void basicEvents() {

		notifications.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent inten1 = new Intent(getApplicationContext(),
						Notifications.class);
				startActivity(inten1);
			}
		});

		addToCart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Checking if the product selected is in stock or not
				String availability = productAvailability.getText().toString();
				String quantity = etQty.getText().toString();

				if (quantity.isEmpty()) {
					Toast.makeText(ProductDetails.this,
							"Please enter the correct quantity",
							Toast.LENGTH_SHORT).show();
				} else {
					int qt1 = Integer.parseInt(quantity);
					if ((qt1 == 0)) {
						Toast.makeText(ProductDetails.this,
								"Quantity cannot be zero", Toast.LENGTH_SHORT)
								.show();
					} else {
						if (availability.equalsIgnoreCase(" Out of stock")) {
							Toast.makeText(ProductDetails.this,
									"The product is out of stock",
									Toast.LENGTH_SHORT).show();
						} else {
							if (alProdParamsFinal != null
									&& !alProdParamsFinal.isEmpty()
									&& pPrice != null && !pPrice.isEmpty()) {
								SessionStorage.cartNumberOfProducts++;

								String sellinp = sellingPrice.getText().toString();
								int qt = Integer.parseInt(quantity);
								Log.d("Dilip",""+qt);
								Log.d("Pavan:",""+sellinp);
								SessionStorage.cartSubTotal += Double
										.parseDouble(sellinp.substring(0, sellinp.length()-2));
								Log.d("Anuj",""+SessionStorage.cartSubTotal);

								Intent inten1 = new Intent(
										getApplicationContext(), Cart.class);

								SessionStorage.cartPresent = 1;
								String sCid[] = SessionStorage.cid.split(",");

								for (int jj = 0; jj < qt; jj++) {
									SessionStorage.cid += SessionStorage.skuid
											+ ",";
								}

								Log.w("SessionStorage.cid and pid", ""
										+ SessionStorage.cid
										+ SessionStorage.skuid
										+ " or "
										+ alProdParamsFinal.get(0)
												.getProductId());
								inten1.putExtra("position", "" + pid);

								startActivity(inten1);
							} else {
								Toast.makeText(ProductDetails.this,
										"Product not avaialable",
										Toast.LENGTH_SHORT).show();
							}
						}
					}

				}
			}
		});

		moreDetails.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(getSKUInfoSTATUS==1){
					Intent inten1 = new Intent(getApplicationContext(),
							ProductAdvancedDetails.class);
					startActivity(inten1);
				}
			}
		});

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ShareDialogue add = new ShareDialogue(ProductDetails.this);
				add.getWindow().setBackgroundDrawable(
						new ColorDrawable(android.graphics.Color.TRANSPARENT));
				add.show();
			}
		});

		WishList.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (SessionStorage.clientId.equalsIgnoreCase("0")) {
					Toast.makeText(getApplicationContext(), "Please Login",
							Toast.LENGTH_SHORT).show();

					Intent inten1 = new Intent(getApplicationContext(),
							LoginMain.class);
					startActivity(inten1);
				} else {
					AsyncCallWSWishlist obj = new AsyncCallWSWishlist();
					obj.execute();
				}
			}
		});

	}

	public void printDetails() {
		if (pName == null) {
			Toast.makeText(getApplicationContext(), "Network Error",
					Toast.LENGTH_SHORT).show();
		} else {
			productName.setText(pName);
			if (pAvailable.equals("1")) {
				productAvailability.setText(" In stock");
			} else {
				productAvailability.setText(" Out of stock");
				productAvailability.setTextColor(Color.rgb(200, 0, 0));
			}

			// Log.d("obj.execute(pid, tSize, tFlavour): "+pid+" "+tSize+" "+tFlavour,
			// "");

			Log.w("pid, SessionStorage.sizCode, SessionStorage.flavCode: ", ""
					+ pid + " " + " " + SessionStorage.sizCode + " "
					+ SessionStorage.flavCode);

			/*
			 * if (!tSize.isEmpty()) { tvSize.setText("Size : " + tSize); }
			 * 
			 * if (!tFlavour.isEmpty()) { tvFlaours.setText("Flavour : " +
			 * tFlavour); }
			 */

			/*
			 * if (pPrice.equals("0") || pPrice.isEmpty()) {
			 * sellingPrice.setText("NA");
			 * productAvailability.setText(" Out of stock");
			 * productAvailability.setTextColor(Color.rgb(200, 0, 0));
			 * 
			 * } else { if (pPrice.isEmpty()) { sellingPrice.setText("0"); }
			 * else { // Double sp = Double.parseDouble(pPrice);
			 * 
			 * if(positionF == -1 && positionS == -1) { sellingPrice.setText(0+
			 * "/-"); discount.setText("0% Off"); } else { sellingPrice.setText(
			 * Math.ceil(Double.parseDouble(pPrice))+ "/-"); }
			 * 
			 * int resSPrice = (int) (Double.parseDouble(pPrice));
			 * sellingPrice.setText(resSPrice + "/-");
			 * 
			 * } }
			 * 
			 * 
			 * if (oPrice.equals("0") || oPrice.isEmpty()) {
			 * originalPrice.setText("NA");
			 * productAvailability.setText("Out of stock");
			 * productAvailability.setTextColor(Color.rgb(200, 0, 0)); } else {
			 * productAvailability.setText("In stock");
			 * productAvailability.setTextColor(Color.rgb(0, 200, 0)); if
			 * (oPrice.isEmpty()) { originalPrice.setText("0/-"); } else {
			 * 
			 * int resOPrice = (int) (Double.parseDouble(oPrice));
			 * originalPrice.setText(resOPrice + "/-"); } }
			 * 
			 * if (disc != null && disc.isEmpty()) { discount.setText("0% Off");
			 * } else { discount.setText(disc + "% Off"); }
			 */
			// Log.w("states", ""+states[1]);
			Log.w("size in adapter", "" + arrSize.length);
			Log.w("flavourr in adapter", "" + arrFlavour.length);
			String[] arrUniqueFlavour = new HashSet<String>(
					Arrays.asList(arrFlavour)).toArray(new String[0]);
			arrUniqueFlavourCode = new HashSet<String>(
					Arrays.asList(arrFlavourCode)).toArray(new String[0]);
			/*
			 * for(int i = 0; i < arrUniqueFlavour.length; i++ ) {
			 * hmFlavorFlavorCode.put(arrUniqueFlavour[i],
			 * arrUniqueFlavourCode[i]);
			 * Log.w("arrUniqueFlavour: "+arrUniqueFlavour[i],
			 * "arrUniqueFlavourCode: "+arrUniqueFlavourCode[i]); }
			 * Log.w("hmFlavorFlavorCode.size"+hmFlavorFlavorCode.size(), "");
			 */
			String[] arrUniqueSize = new HashSet<String>(Arrays.asList(arrSize))
					.toArray(new String[0]);
			arrUniqueSizeCode = new HashSet<String>(Arrays.asList(arrSizeCode))
					.toArray(new String[0]);
			/*for(int i = 0; i < arrUniqueSizeCode.length; i++ ) 
			{
				Log.w("arrUniqueSizeCode: "+arrUniqueSizeCode[i], "");
			}*/

			/*
			 * for(int i = 0; i < arrUniqueSizeCode.length; i++ ) {
			 * hmSizeSizeCode.put(arrUniqueSize[i], arrUniqueSizeCode[i]);
			 * Log.w("arrUniqueSize: "+arrUniqueSize[i],
			 * "arrUniqueSizeCode: "+arrUniqueSizeCode[i]); }
			 * Log.w("hmSizeSizeCode.size"+hmSizeSizeCode.size(), "");
			 */
			/*
			 * String arrStrSizeDup[] = new String[arrUniqueSize.length];
			 * for(int i = 0; i < arrUniqueSize.length; i++) { arrStrSizeDup[i]
			 * = arrUniqueSize[i]; } if(tSize != null && !tSize.isEmpty()) {
			 * for(int i = 0; i < arrUniqueSize.length; i++) {
			 * if(hmSizeSizeCode.get(tSize).equals(arrUniqueSize[i])) {
			 * arrUniqueSize[0] = hmSizeSizeCode.get(tSize); for(int j = 1; j <
			 * arrUniqueSize.length; j++) {
			 * if(!hmSizeSizeCode.get(tSize).equals(arrUniqueSize[j]))
			 * arrUniqueSize[j] = arrStrSizeDup[j]; } } }
			 * 
			 * }
			 */
			int pos = 0;
			if (tSize != null && !tSize.isEmpty()) {
				for (int i = 0; i < arrUniqueSize.length; i++) {
					/*
					 * if(hmSizeSizeCode.get(tSize).equals(arrUniqueSize[i])) {
					 * pos = i; }
					 */
					if (hmSizeSizeCode.get(SessionStorage.sizCode).equals(
							arrUniqueSize[i])) {
						pos = i;
					}
				}
				String temp1 = arrUniqueSize[0];
				String temp2 = arrUniqueSize[pos];
				arrUniqueSize[0] = temp2;
				arrUniqueSize[pos] = temp1;
				int pos22 = 0;
				for (int i = 0; i < arrUniqueSizeCode.length; i++) {
					/*
					 * if(tSize.equals(arrUniqueSizeCode[i])) { pos22 = i; }
					 */
					if (SessionStorage.sizCode.equals(arrUniqueSizeCode[i])) {
						pos22 = i;
						break;
					}
				}

				String temp11 = arrUniqueSizeCode[0];
				String temp22 = arrUniqueSizeCode[pos22];
				arrUniqueSizeCode[0] = temp22;
				arrUniqueSizeCode[pos22] = temp11;

			}
			adapterSize = new ArrayAdapter<String>(ProductDetails.this,
					android.R.layout.simple_spinner_item, arrUniqueSizeCode);
			// adapterSize.notifyDataSetChanged();
			adapterSize
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spnrSize.setAdapter(adapterSize);
			/*
			 * int m = 0; for (int i = 0; i < arrUniqueSizeCode.length; i++) {
			 * if (arrUniqueSizeCode[i] == hmSizeCodeFlavorCode
			 * .get(arrSizeCode[0])) m = i; } spnrSize.setSelection(m);
			 */

			int pos1 = 0;
			if (tFlavour != null && !tFlavour.isEmpty()) {
				for (int i = 0; i < arrUniqueFlavour.length; i++) {
					Log.w("hmFlavorFlavorCode: "
							+ hmFlavorFlavorCode.get(tFlavour).equals(
									arrUniqueFlavour[i]), "");
					/*
					 * if(hmFlavorFlavorCode.get(tFlavour).equals(arrUniqueFlavour
					 * [i])) { pos1 = i; }
					 */
					if (hmFlavorFlavorCode.get(SessionStorage.flavCode).equals(
							arrUniqueFlavour[i])) {
						pos1 = i;
					}
				}
				int pos11 = 0;
				String temp1 = arrUniqueFlavour[0];
				String temp2 = arrUniqueFlavour[pos1];
				arrUniqueFlavour[0] = temp2;
				arrUniqueFlavour[pos1] = temp1;
				for (int i = 0; i < arrUniqueFlavour.length; i++) {
					/*
					 * if(tFlavour.equals(arrUniqueFlavourCode[i])) { pos11 = i;
					 * }
					 */
					if (SessionStorage.flavCode.equals(arrUniqueFlavourCode[i])) {
						pos11 = i;
					}
				}

				String temp11 = arrUniqueFlavourCode[0];
				String temp22 = arrUniqueFlavourCode[pos11];
				arrUniqueFlavourCode[0] = temp22;
				arrUniqueFlavourCode[pos11] = temp11;
			}

			adapterFlavour = new ArrayAdapter<String>(ProductDetails.this,
					android.R.layout.simple_spinner_item, arrUniqueFlavourCode);

			// adapterFlavour.notifyDataSetChanged();
			adapterFlavour
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spnrFlavour.setAdapter(adapterFlavour);
			/*
			 * int k = 0; for (int i = 0; i < arrUniqueFlavourCode.length; i++)
			 * { if (arrUniqueFlavourCode[i] == hmSizeCodeFlavorCode
			 * .get(arrFlavourCode[0])) k = i; }
			 * Toast.makeText(getApplicationContext(),
			 * ""+hmSizeSizeCode.get(tSize), Toast.LENGTH_LONG).show();
			 * spnrFlavour.setSelection(k);
			 */
			// arrUniqueFlavourCode[0].indexOf(string)
		}
		// Toast.makeText(getApplicationContext(), "Sp"+sp ,
		// Toast.LENGTH_SHORT).show();

	}

	public void printSKUInfo() {
		String pP = alProdParamsFinal.get(0).getPPrice();
		String oP = alProdParamsFinal.get(0).getOPrice();
		String pA = alProdParamsFinal.get(0).getPAvaiability();
		String pC = alProdParamsFinal.get(0).getProductCode();
		productCode.setText(pC);
		
		rs1.setVisibility(View.VISIBLE);
		rs2.setVisibility(View.VISIBLE);
		
		if (pP.isEmpty() || pP.equals("0") && oP.isEmpty() || oP.equals("0")) {

		} else {
			/*
			 * if(positionF == -1 || positionS == -1) { sellingPrice.setText(0 +
			 * "/-"); originalPrice.setText(0 + "/-"); discount.setText(0 +
			 * "% Off");
			 * 
			 * }else { sellingPrice.setText(alProdParamsFinal.get(0).getPPrice()
			 * + "/-");
			 * sellingPrice.setText(alProdParamsFinal.get(0).getPPrice() +
			 * "/-"); originalPrice.setText(alProdParamsFinal.get(0).getOPrice()
			 * + "/-"); discount.setText(alProdParamsFinal.get(0).getDiscount()
			 * + "% Off"); // Log.w("states", ""+states[1]); Log.w("sku info: ",
			 * "SPrice: "
			 * +alProdParamsFinal.get(0).getPPrice()+"OPrice: "+alProdParamsFinal
			 * .
			 * get(0).getOPrice()+"Disc: "+alProdParamsFinal.get(0).getDiscount(
			 * )); if(pA.equals("1")) { productAvailability.setText("In stock");
			 * } }
			 */

			// sellingPrice.setText(alProdParamsFinal.get(0).getPPrice() +
			// "/-");
			int resSPrice = (int) (Double.parseDouble(pP));
			sellingPrice.setText(resSPrice + "/-");

			int resOPrice = (int) (Double.parseDouble(oP));
			originalPrice.setText(resOPrice + "/-");

			/*
			 * if(first==0){ originalPrice.setText(SessionStorage.pPrice);
			 * }else{ first++; }
			 */
			/*
			 * sellingPrice.setText(alProdParamsFinal.get(0).getPPrice() +
			 * "/-"); originalPrice.setText(alProdParamsFinal.get(0).getOPrice()
			 * + "/-");
			 */
			discount.setText(alProdParamsFinal.get(0).getDiscount() + "% Off");
			// Log.w("states", ""+states[1]);
			Log.w("sku info: ", "SPrice: "
					+ alProdParamsFinal.get(0).getPPrice() + "OPrice: "
					+ alProdParamsFinal.get(0).getOPrice() + "Disc: "
					+ alProdParamsFinal.get(0).getDiscount());
			if (pA.equals("1")) {
				productAvailability.setText("In stock");
				productAvailability.setTextColor(Color.rgb(0, 200, 0));
			} else {
				productAvailability.setText("Out of stock");
				productAvailability.setTextColor(Color.rgb(200, 0, 0));
			}
		}
	}

	public void printSKUInfoNotAvailable() {
		rs1.setVisibility(View.INVISIBLE);
		rs2.setVisibility(View.INVISIBLE);
		
		productCode.setText("NA");

		sellingPrice.setText("");
		originalPrice.setText("NA");
		/*
		 * sellingPrice.setText(alProdParamsFinal.get(0).getPPrice() + "/-");
		 * originalPrice.setText(alProdParamsFinal.get(0).getOPrice() + "/-");
		 */
		discount.setText("");
		productAvailability.setText("Out of Stock");
		productAvailability.setTextColor(Color.rgb(200, 0, 0));

	}

	// Webservice for authentication
	public void getProductDetails() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("productid", SessionStorage.pid);
		request.addProperty("customerType", SessionStorage.customerType);
		Log.w("SessionStorage.pid: (Sending this pid to WS) ", ""
				+ SessionStorage.pid);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("Product Details", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("product");

			for (int i = 0; i < jsonarray.length(); i++) {
				// this object inside array you can do whatever you want
				JSONObject object = jsonarray.getJSONObject(i);
				pid = object.getString("id");
				pName = object.getString("name");
				oPrice = object.getString("originalprice");
				pPrice = object.getString("sellingprice");
				Log.d("pPrice", "" + pPrice);
				// flavour = object.getString("flavourCode");
				// size = object.getString("sizeCode");
				// color = object.getString("colorCode");
				tFlavour = object.getString("flavour");
				tSize = object.getString("size");
				// tColor = object.getString("color");
				// pQuantity = object.getString("quantity");
				pAvailable = object.getString("availability");
				pImage = object.getString("image");
				pCode = object.getString("pcode");
				disc = object.getString("discount");
				description = object.getString("description");
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
			pd = new ProgressDialog(ProductDetails.this);
			pd.setMessage("Loading...(Details)");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();
			AsyncCallWSProdParams obj = new AsyncCallWSProdParams();
			obj.execute(pid);
			// printDetails();
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				getProductDetails();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	// Webservice for add to wishlist
	public void addToWishlistWebservice() throws JSONException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD2);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("userId", SessionStorage.userId);
		request.addProperty("pCode", pCode);
		request.addProperty("skuId", alProdParamsFinal.get(0).getProductId());
		request.addProperty("flavour", flavour);
		request.addProperty("color", color);
		request.addProperty("size", size);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION2, envelope);
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
			pd = new ProgressDialog(ProductDetails.this);
			pd.setMessage("Loading...");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			if (status == 0) {
				Toast.makeText(getApplicationContext(),
						"Oops Something went wrong", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Added to wishlist",
						Toast.LENGTH_LONG).show();
			}
			pd.dismiss();
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

	// getSKU Info
	// Webservice for add to wishlist
	public void getSKUInfo(String skuId, String sizeCode, String flavourCode)
			throws JSONException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD_SKU_INFO);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("productid", pid);
		request.addProperty("customerType", SessionStorage.customerType);

		if (skuId != null && !skuId.isEmpty())
			request.addProperty("skuId", skuId);
		else
			request.addProperty("skuId", "");

		request.addProperty("size", sizeCode);
		request.addProperty("flavour", flavourCode);
		Log.w("getSKUInfo parameters", "pid: " + pid + "skuId: " + skuId
				+ "sizeCode: " + sizeCode + "flavourCode: " + flavourCode);
		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION_SKU_INFO, envelope);
			Object response = envelope.getResponse();

			Log.w("getSKUInfo", response.toString());

			JSONObject json = new JSONObject(response.toString());

			// status = json.optInt("status");

			JSONArray jsonarray = json.getJSONArray("productParams");

			alProdParamsFinal = new ArrayList<BeanProductListing>();
			JSONObject object1 = jsonarray.getJSONObject(0);
			Log.w("object1", "" + object1.toString());
			if (object1.length() > 0) {

				getSKUInfoSTATUS = 1;
				for (int i = 0; i < jsonarray.length(); i++) {
					// this object inside array you can do whatever you want
					JSONObject object = jsonarray.getJSONObject(i);
					beanProductParams = new BeanProductListing();
					beanProductParams.setProductId(object.getString("id"));
					SessionStorage.skuid = object.getString("id");
					Log.w("SessionStorage.skuid: " + SessionStorage.skuid, "");
					beanProductParams.setProductCode(object.getString("pcode"));
					beanProductParams.setOPrice(object
							.getString("originalprice"));
					beanProductParams.setPPrice(object
							.getString("sellingprice"));
					beanProductParams.setDiscount(object.getString("discount"));
					beanProductParams.setPAvaiability(object
							.getString("availability"));
					alProdParamsFinal.add(beanProductParams);
				}

			} else {
				getSKUInfoSTATUS = 0;
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

	private class AsyncCallWS_SKUInfo extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(ProductDetails.this);
			pd.setMessage("Loading...(SKU Info)");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();
			/*
			 * for(int i = 0; i < arrUniqueSizeCode.length; i++) {
			 * Log.w("arrUniqueSizeCode: "+i+"::"+arrUniqueSizeCode[i], ""); }
			 * for(int i = 0; i < arrUniqueFlavourCode.length; i++) {
			 * Log.w("arrUniqueFlavourCode: "+i+"::"+arrUniqueFlavourCode[i],
			 * ""); }
			 */
			Log.w("getSKUInfoSTATUS: " + getSKUInfoSTATUS, "");
			if (getSKUInfoSTATUS == 1) {
				printSKUInfo();
			} else {
				printSKUInfoNotAvailable();
				Toast.makeText(ProductDetails.this,
						"Oops.. This combination is not available!",
						Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				// Log.w("params[0]"++""+params[1]+"",params[2]", msg)
				getSKUInfo(params[0], params[1], params[2]);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	// getProductParams
	public void getProductParams(String id) throws JSONException {

		Log.w("getProductParams: parameter value", "" + id);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD_PARAMS);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("productid", pid);
		Log.w("productid: (Sending this pid to WS for size n flavour):" + pid,
				"");

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION_PRODUCT_PARAMS, envelope);
			Object response = envelope.getResponse();

			Log.d("getProductParams", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("productParams");
			alProdParams = new ArrayList<BeanProductListing>();
			if (jsonarray.length() > 0) {
				for (int i = 0; i < jsonarray.length(); i++) {
					// this object inside array you can do whatever you want
					JSONObject object = jsonarray.getJSONObject(i);
					beanProductParams = new BeanProductListing();
					beanProductParams.setProductId(object.getString("id"));
					beanProductParams.setProductName(object.getString("name"));
					beanProductParams.setSizeCode(object.getString("sizeCode"));
					beanProductParams.setProdSize(object.getString("size"));
					beanProductParams.setFlavourCode(object
							.getString("flavourCode"));
					beanProductParams.setFlavour(object.getString("flavour"));
					beanProductParams.setPAvaiability(object
							.getString("availability"));
					alProdParams.add(beanProductParams);
				}
			} else {
				Toast.makeText(ProductDetails.this, "Network error",
						Toast.LENGTH_SHORT).show();
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

	private class AsyncCallWSProdParams extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(ProductDetails.this);
			pd.setMessage("Loading...(Prod Params)");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();
			if (alProdParams != null && !alProdParams.isEmpty()) {
				arrFlavour = new String[alProdParams.size()];
				arrSize = new String[alProdParams.size()];
				arrFlavourCode = new String[alProdParams.size()];
				arrSizeCode = new String[alProdParams.size()];
				hmSizeCodeFlavorCode = new HashMap<String, String>();
				hmFlavorFlavorCode = new HashMap<String, String>();
				hmSizeSizeCode = new HashMap<String, String>();
				for (int i = 0; i < alProdParams.size(); i++) {
					arrFlavour[i] = alProdParams.get(i).getFlavour();
					arrSize[i] = alProdParams.get(i).getProdSize();

					arrFlavourCode[i] = alProdParams.get(i).getFlavourCode();
					arrSizeCode[i] = alProdParams.get(i).getSizeCode();
					hmFlavorFlavorCode.put(arrFlavourCode[i], arrFlavour[i]);
					hmSizeSizeCode.put(arrSizeCode[i], arrSize[i]);
					hmSizeCodeFlavorCode.put(arrSizeCode[i], arrFlavourCode[i]);
					Log.w("Size and flavour: ", "" + arrSize[i] + " "
							+ arrFlavour[i]);
					Log.w("arrSizeCode and arrFlavourCode: ", ""
							+ arrSizeCode[i] + " " + arrFlavourCode[i]);
				}
				Log.w("pid: " + pid + "tSize: " + tSize + "tFlavor: "
						+ tFlavour, "");
				/*
				 * Toast.makeText( ProductDetails.this, "" + "pid: " + pid +
				 * "tSize: " + tSize + "tFlavor: " + tFlavour,
				 * Toast.LENGTH_SHORT).show();
				 */
				String sizeFirstKey = (String) hmSizeSizeCode.keySet()
						.toArray()[0];
				String flavorFirstKey = (String) hmFlavorFlavorCode.keySet()
						.toArray()[0];
				Log.w("hmFirstSizeCode: "
						+ hmSizeSizeCode.get((String) hmSizeSizeCode.keySet()
								.toArray()[0]),
						"hmFlavorFirstCode: "
								+ hmFlavorFlavorCode
										.get((String) hmFlavorFlavorCode
												.keySet().toArray()[0]));
				// Log.d("obj.execute(pid, tSize, tFlavour): "+pid+" "+tSize+" "+tFlavour,
				// "");
				printDetails();
				AsyncCallWS_SKUInfo obj = new AsyncCallWS_SKUInfo();
				// obj.execute(pid, arrSizeCode[0], arrFlavourCode[0]);
				// obj.execute(pid, tSize, tFlavour);
				if (SessionStorage.sizCode != null
						&& !SessionStorage.sizCode.isEmpty()
						&& SessionStorage.flavCode != null
						&& !SessionStorage.flavCode.isEmpty())
					obj.execute("", SessionStorage.sizCode,
							SessionStorage.flavCode);
				else
					obj.execute(SessionStorage.skuId_prod_listing, "", "");
			} else {
				Toast.makeText(ProductDetails.this, "Params epmty or null",
						Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				getProductParams(params[0]);

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

		searchImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				SessionStorage.search = 1;
				Intent inten1 = new Intent(getApplicationContext(),
						LandingScreen.class);
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
	}

	// Related Products
	private static final String METHODRelated = "relatedProducts";
	private final String SOAP_ACTION_RELATED = SessionStorage.webserviceNamespace
			+ "service.php/relatedProducts";
	ArrayList<BeanProductListing> alProductListing;
	BeanProductListing beanProductListing;

	public void getRelatedProducts() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHODRelated);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("productid", SessionStorage.pid);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION_RELATED, envelope);
			Object response = envelope.getResponse();

			Log.d("Related Products", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("related");

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
				// beanProductListing.setProdSize(object.getString("sizeCode"));
				// beanProductListing.setColor(object.getString("colorCode"));
				beanProductListing.setFlavour(object.getString("flavour"));
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

	private class AsyncCallWSRelated extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(ProductDetails.this);
			pd.setMessage("Loading...(Prod Params)");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();
			// listViewSimProds.setAdapter(new SimilarProdsAdapter(this,
			// alProductListing));
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				getRelatedProducts();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	@Override
	protected void onStart() {
		// Checking if the internet connection is present or not
			ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
			if (!cd.isConnectingToInternet()) {
				DisplayMetrics metrics = this.getResources().getDisplayMetrics();
				InternetConnection add = new InternetConnection(ProductDetails.this);
				add.show();
				int wid = metrics.widthPixels / 2 + 180;
				int heig = metrics.heightPixels / 3;
				add.getWindow().setLayout(wid, heig);
			}
		
		super.onStart();
	}
}

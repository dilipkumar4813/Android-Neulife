package app.retailinsights.neulife.account;

import java.io.IOException;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import app.retailinsights.neulife.Cart;
import app.retailinsights.neulife.InternetConnection;
import app.retailinsights.neulife.LandingScreen;
import app.retailinsights.neulife.R;
import app.retailinsights.neulife.SessionStorage;
import app.retailinsights.neulife.database.DatabaseHandler;
import app.retailinsights.neulife.utils.ConnectionDetector;

public class DeliveryAddressEdit extends Activity {

	DatabaseHandler databaseHandler;
	ImageView ivBack, ivArraow5, backArrow;
	CheckBox cbDefaultAddress;
	Button btnSave;
	String setDefault="0";
	EditText firstName, lastName, address1, address2, nearestLandmark, pin, ph;
	RadioGroup rbgGender;
	RadioButton rbMale, rbFemale;
	String fName, lName, addressLine1, addressLine2, landmark, city, state,
			pincode, phone, addressId = "", addressLine3;
	Spinner cty, stte;
	final String[] city_array = { "Bangalore", "Gulbarga", "Mysore", "Hubli" };

	String[] states;
	String[] cities;

	// sTATE AND cITY INSERTION
	String[] state_array = { "KARNATAKA , ANDRA PRADESH , HARYANA , " +
			"MAHARASHTRA , MADHYA PRADESH , GOA , PUNJAB , PUDUCHERRY, DELHI" };
	String[] KARNATAKA = { "BANGALORE", "GULBARGA", "MYSORE", "BIDAR",
			"BIJAPUR", "YADGIR", "RAICHUR", "BELLARY", "BAGALKOT", "BELGAUM",
			"DHARWAD", "GADAG", "KOPPAL", "HAVERI", "KARWAR", "DAVANGERE",
			"CHITHRADURGA", "SHIMOGA", "UDUPI", "MANGALORE", "CHIKMANGALURU",
			"TUMKURU", "CHICKBULAPUR", "KOLAR", "HASSAN", "COORG", "MANDYA",
			"CHANNAPATNA", "CHAMRAJNAGAR" };
	String[] MAHARASHTRA = { "Ahmednagar", "Akola", "Akot", "Amalner",
			"Ambejogai", "Amravati", "Anjangaon", "Arvi", "Aurangabad",
			"Bhiwandi", "Dhule", "Kalyan-Dombivali", "Ichalkaranji", "Karjat",
			"Latur", "Loha", "Lonar", "Lonavla", "Mahad", "Malegaon",
			"Malkapur", "Mangalvedhe", "Mangrulpir", "Manjlegaon", "Manmad",
			"Manwath", "Mehkar", "Mhaswad", "Mira-Bhayandar", "Morshi",
			"Mukhed", "Mul", "Mumbai", "Murtijapur", "Nagpur",
			"Nanded-Waghala", "Nandgaon", "Nandura", "Nandurbar", "Narkhed",
			"Nashik", "Navi", "Nawapur", "Nilanga", "Osmanabad", "Ozar",
			"Pachora", "Paithan", "Palghar", "Pandharkaoda", "Pandharpur",
			"Panvel", "Parbhani", "Parli", "Partur", "Pathardi", "Pathri",
			"Patur", "Pauni", "Pen", "Phaltan", "Pulgaon", "Pune", "Purna",
			"Pusad", "Rahuri", "Rajura", "Ramtek", "Ratnagiri", "Raver",
			"Risod", "Sailu", "Sangamner", "Sangli", "Sangole", "Sasvad",
			"Satana", "Satara", "Savner", "Sawantwadi", "Shahade", "Shegaon",
			"Shendurjana", "Shirdi", "Shirpur-Warwade", "Shirur", "Shrigonda",
			"Shrirampur", "Sillod", "Sinnar", "Solapur", "Soyagaon",
			"Talegaon Dabhade", "Talode", "Tasgaon", "Thane", "Tirora",
			"Tuljapur", "Tumsar", "Uchgaon", "Udgir", "Umarga", "Umarkhed",
			"Umred", "Uran", "Uran Islampur", "Vadgaon Kasba", "Vaijapur",
			"Vasai-Virar", "Vita", "Wadgaon Road", "Wai", "Wani", "Wardha",
			"Warora", "Warud", "Washim", "Yavatmal", "Yawal" };
	String[] HARYANA = { "Rewari", "Kaithal", "Thanesar", "Jind",
			"Bahadurgarh", "Sirsa", "Bhiwani", "Panchkula", "Rohtak",
			"Faridabad", "Gurgaon", "Panipat", "Ambala", "Yamunanagar",
			"Hisar", "Karnal", "Sonipat", "Palwal" };
	String[] ANDRA_PRADESH = { "HYDRABAD", "Adoni", "Amalapuram", "Anakapalle",
			"Anantapur", "Bapatla", "Bheemunipatnam", "Bhimavaram", "Bobbili",
			"Chilakaluripet", "Chirala", "Chittoor", "Dharmavaram", "Eluru",
			"Gooty", "Gudivada", "Gudur", "Guntakal", "Guntur", "Hindupur",
			"Jaggaiahpet", "Jammalamadugu", "Kadapa", "Macherla",
			"Machilipatnam", "Madanapalle", "Mandapeta", "Markapur", "Nagari",
			"Naidupet", "Nandyal", "Narasapuram", "Narasaraopet",
			"Narsipatnam", "Nellore", "Nidadavole", "Nuzvid", "Ongole",
			"Palacole", "Palasa Kasibugga", "Parvathipuram", "Pedana",
			"Peddapuram", "Pithapuram", "Ponnur", "Proddatur", "Punganur",
			"Puttur", "Rajahmundry", "Rajam", "Rajampet", "Ramachandrapuram",
			"Rayachoti", "Rayadurg", "Renigunta", "Repalle", "Salur",
			"Samalkot", "Sattenapalle", "Srikakulam", "Srikalahasti",
			"Srisailam Project", "Sullurpeta", "Tadepalligudem", "Tadpatri",
			"Tanuku", "Tenali", "Tirupati", "Tiruvuru", "Tuni", "Uravakonda",
			"Venkatagiri", "Vijayawada", "Vinukonda", "Visakhapatnam",
			"Vizianagaram", "Yemmiganur", "Yerraguntla" };
	String[] MADHYA_PRADESH = { "Alirajpur", "Ashok Nagar", "Balaghat",
			"Bhopal", "Gwalior", "Indore", "Itarsi", "Jabalpur", "Lahar",
			"Maharajpur", "Mahidpur", "Maihar", "Malaj Khand", "Manasa",
			"Manawar", "Mandideep", "Mandla", "Mandsaur", "Mauganj",
			"Mhow Cantonment", "Mhowgaon", "Morena", "Multai", "Mundi",
			"Murwara", "Nagda", "Nainpur", "Narsinghgarh", "Neemuch",
			"Nepanagar", "Niwari", "Nowgong", "Nowrozabad", "Pachore", "Pali",
			"Panagar", "Pandhurna", "Panna", "Pasan", "Pipariya", "Pithampur",
			"Porsa", "Prithvipur", "Raghogarh-Vijaypur", "Rahatgarh", "Raisen",
			"Rajgarh", "Ratlam", "Rau", "Rehli", "Rewa", "Sabalgarh", "Sagar",
			"Sanawad", "Sarangpur", "Sarni", "Satna", "Sausar", "Sehore",
			"Sendhwa", "Seoni", "Seoni-Malwa", "Shahdol", "Shajapur",
			"Shamgarh", "Sheopur", "Shivpuri", "Shujalpur", "Sidhi", "Sihora",
			"Singrauli", "Sironj", "Sohagpur", "Tarana", "Tikamgarh", "Ujjain",
			"Umaria", "Vidisha", "Vijaypur", "Wara Seoni" };
	String[] GOA = { "Mapusa", "Margao", "Marmagao", "Panaji" };
	String[] PUNJAB = { "Amritsar", "Barnala", "Batala", "Bathinda", "Dhuri",
			"Faridkot", "Fazilka", "Firozpur", "Firozpur Cantt", "Gurdaspur",
			"Hoshiarpur", "Jagraon", "Jalandhar Cantt", "Jalandhar",
			"Kapurthala", "Khanna", "Kharar", "Kot Kapura", "Longowal",
			"Ludhiana", "Malerkotla", "Malout", "Mansa", "Moga", "Mohali",
			"Morinda", "Mukerian", "Muktsar", "Nabha", "Nakodar", "Nangal",
			"Nawanshahr", "Pathankot", "Patiala", "Pattran", "Patti",
			"Phagwara", "Phillaur", "Qadian", "Raikot", "Rajpura",
			"Rampura Phul", "Rupnagar", "Samana", "Sangrur",
			"Sirhind Fatehgarh Sahib", "Sujanpur", "Sunam", "Talwara",
			"Tarn Taran", "Urmar Tanda" };
	String[] PUDUCHERRY = { "Karaikal", "Mahe", "Pondicherry", "Yanam" };
	String[] DELHI = {"North Delhi","South Delhi","Central Delhi","East Delhi","West Delhi"};

	// Webservice related variables
	ProgressDialog pd;
	private static final String METHOD = "getAddress";
	private final String SOAP_ACTION = SessionStorage.webserviceNamespace
			+ "service.php/getAddress";

	private static final String METHOD4 = "updateAddress";
	private final String SOAP_ACTION4 = SessionStorage.webserviceNamespace
			+ "service.php/updateAddress";
	public int status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.delivery_address_edit);

		// Checking if the internet connection is present or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			DisplayMetrics metrics = this.getResources().getDisplayMetrics();

			InternetConnection add = new InternetConnection(
					DeliveryAddressEdit.this);
			add.show();

			int wid = metrics.widthPixels / 2 + 180;
			int heig = metrics.heightPixels / 3;
			add.getWindow().setLayout(wid, heig);
		}

		initialize();
		basicEvents();
		databaseHandler = new DatabaseHandler(getApplicationContext());
		databaseHandler.resetLocTables();
		databaseHandler.addState("KARNATAKA");
		for (int j = 0; j < KARNATAKA.length; j++) {
			databaseHandler.addCity(KARNATAKA[j],
					databaseHandler.getStateID("KARNATAKA"));
		}

		databaseHandler.addState("ANDRA PRADESH");
		for (int j = 0; j < ANDRA_PRADESH.length; j++) {
			databaseHandler.addCity(ANDRA_PRADESH[j],
					databaseHandler.getStateID("ANDRA PRADESH"));
		}

		databaseHandler.addState("MAHARASHTRA");
		for (int j = 0; j < MAHARASHTRA.length; j++) {
			databaseHandler.addCity(MAHARASHTRA[j],
					databaseHandler.getStateID("MAHARASHTRA"));
		}

		databaseHandler.addState("HARYANA");
		for (int j = 0; j < HARYANA.length; j++) {
			databaseHandler.addCity(HARYANA[j],
					databaseHandler.getStateID("HARYANA"));
		}

		databaseHandler.addState("MADHYA PRADESH");
		for (int j = 0; j < MADHYA_PRADESH.length; j++) {
			databaseHandler.addCity(MADHYA_PRADESH[j],
					databaseHandler.getStateID("MADHYA PRADESH"));
		}

		databaseHandler.addState("GOA");
		for (int j = 0; j < GOA.length; j++) {
			databaseHandler.addCity(GOA[j], databaseHandler.getStateID("GOA"));
		}

		databaseHandler.addState("PUNJAB");
		for (int j = 0; j < PUNJAB.length; j++) {
			databaseHandler.addCity(PUNJAB[j],
					databaseHandler.getStateID("PUNJAB"));
		}
		
		databaseHandler.addState("PUDUCHERRY");
		for (int j = 0; j < PUDUCHERRY.length; j++) {
			databaseHandler.addCity(PUDUCHERRY[j],
					databaseHandler.getStateID("PUDUCHERRY"));
		}
		
		databaseHandler.addState("DELHI");
		for (int j = 0; j < DELHI.length; j++) {
			databaseHandler.addCity(DELHI[j],
					databaseHandler.getStateID("DELHI"));
		}

		states = databaseHandler.getAllStates();
		ArrayAdapter<String> adapterState = new ArrayAdapter<String>(
				DeliveryAddressEdit.this, android.R.layout.simple_spinner_item,
				states);
		adapterState.notifyDataSetChanged();
		adapterState
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stte.setAdapter(adapterState);

		int state_id = databaseHandler.getStateID(stte.getSelectedItem()
				.toString());
		cities = databaseHandler.getAllCities(state_id);

		// Log.w("states", ""+states[1]);
		ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(
				DeliveryAddressEdit.this, android.R.layout.simple_spinner_item,
				cities);
		adapterCity.notifyDataSetChanged();
		adapterCity
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cty.setAdapter(adapterCity);

		// Footer Related Methods
		footerInitialize();
		footerEvents();

		stte.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {

				int state_id = databaseHandler.getStateID(stte
						.getSelectedItem().toString());
				cities = databaseHandler.getAllCities(state_id);

				// Log.w("states", ""+states[1]);
				ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(
						DeliveryAddressEdit.this,
						android.R.layout.simple_spinner_item, cities);
				adapterCity.notifyDataSetChanged();
				adapterCity
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				cty.setAdapter(adapterCity);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});

		if(!SessionStorage.addressId.equalsIgnoreCase("0")){
			AsyncCallWS obj = new AsyncCallWS();
			obj.execute();
		}
	}

	private void initialize() {
		cbDefaultAddress = (CheckBox) findViewById(R.id.cbDefaultAddress);
		
		backArrow = (ImageView) findViewById(R.id.imageView1);
		btnSave = (Button) findViewById(R.id.button1);

		// Edit text boxes
		firstName = (EditText) findViewById(R.id.etfirstName);
		lastName = (EditText) findViewById(R.id.etlastName);
		address1 = (EditText) findViewById(R.id.etAddrress1);
		address2 = (EditText) findViewById(R.id.etAddrress2);
		nearestLandmark = (EditText) findViewById(R.id.etLandmark);
		cty = (Spinner) findViewById(R.id.etCity);
		stte = (Spinner) findViewById(R.id.etState);
		pin = (EditText) findViewById(R.id.etPincode);
		ph = (EditText) findViewById(R.id.etMobileNumber);
	}

	private void basicEvents() {

		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				fName = firstName.getText().toString();
				lName = lastName.getText().toString();
				addressLine1 = address1.getText().toString();
				addressLine2 = address2.getText().toString();
				landmark = nearestLandmark.getText().toString();
				pincode = pin.getText().toString();
				phone = ph.getText().toString();
				city = cty.getSelectedItem().toString();
				state = stte.getSelectedItem().toString();
				
				if(cbDefaultAddress.isChecked()){
					setDefault="1";
				}

				if (fName.isEmpty()) {
					Toast.makeText(getApplicationContext(),
							"Please enter First Name", Toast.LENGTH_SHORT)
							.show();
				} else if (lName.isEmpty()) {
					Toast.makeText(getApplicationContext(),
							"Please enter Last Name", Toast.LENGTH_SHORT)
							.show();
				} else if (addressLine1.isEmpty()) {
					Toast.makeText(getApplicationContext(),
							"Please enter Address", Toast.LENGTH_SHORT).show();
				} else if (landmark.isEmpty()) {
					Toast.makeText(getApplicationContext(),
							"Please enter Landmark", Toast.LENGTH_SHORT).show();
				} else if (pincode.isEmpty()) {
					Toast.makeText(getApplicationContext(),
							"Please enter Pincode", Toast.LENGTH_SHORT).show();
				} else if (phone.isEmpty()) {
					Toast.makeText(getApplicationContext(),
							"Please enter Phone Number", Toast.LENGTH_SHORT)
							.show();
				} else {
					Log.w("form data: ", "fName: " + fName + " lName: " + lName
							+ " addressLine1: " + addressLine1
							+ "addressLine2: " + addressLine2 + "landmark: "
							+ landmark + "" + "pincode" + pincode + "phone: "
							+ phone + "city: " + city + "state" + state);

					// Calling the webservice method to store/update the address
					AsyncCallWSUpdateAddress obj = new AsyncCallWSUpdateAddress();
					obj.execute();
				}
			}
		});

		backArrow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		ImageView ivNotifications = (ImageView) findViewById(R.id.ivNotifications);
		ivNotifications.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DeliveryAddressEdit.this,
						Notifications.class);
				startActivity(intent);
			}
		});
	}

	// Webservice for updating the address
	public void updateAddress() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD4);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		if(SessionStorage.addressId.equalsIgnoreCase("0")){
			SessionStorage.addressId="";
		}
		
		request.addProperty("userId", SessionStorage.userId);
		request.addProperty("isDefaultAddress", setDefault);
		request.addProperty("addressId", SessionStorage.addressId);
		request.addProperty("firstName", fName);
		request.addProperty("lastName", lName);
		request.addProperty("addressLine1", addressLine1);
		request.addProperty("addressLine2", addressLine2);
		request.addProperty("addressLine3", landmark);
		request.addProperty("city", city);
		request.addProperty("postalCode", pincode);
		request.addProperty("telNumber", phone);
		// request.addProperty("state", state);
		
		Log.d("Add Address Parameters", ""+request);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION4, envelope);
			Object response = envelope.getResponse();

			Log.d("Delivery Address Edit Details", response.toString());

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

	private class AsyncCallWSUpdateAddress extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pd = new ProgressDialog(DeliveryAddressEdit.this);
			pd.setMessage("Loading...");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();

			if (status == 0) {
				Toast.makeText(getApplicationContext(),
						"Oops something went wrong! Try Again",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Update Succesfull",
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				updateAddress();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	public void printAddress() {
		/*
		 * if (fName.isEmpty()) { Toast.makeText(getApplicationContext(),
		 * "empty", Toast.LENGTH_SHORT) .show(); }
		 */
		firstName.setText(fName);
		lastName.setText(lName);
		address1.setText(addressLine1);
		address2.setText(addressLine2);
		nearestLandmark.setText(addressLine3);
		pin.setText(pincode);
		ph.setText(phone);
		/*
		 * tvName.setText(fName + " " + lName);
		 * tvAddress_first_line.setText(addressLine1);
		 * tvAddress_second_line.setText(addressLine2 + " " + addressLine3);
		 * tvCity.setText(state + " " + city); tvPinCode.setText(pincode);
		 * tvPhone.setText(phone); SessionStorage.addressId = addressId;
		 */
	}

	public void getAddress() throws JSONException {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject request = new SoapObject(SessionStorage.webserviceNamespace,
				METHOD);
		// bodyOut is the body object to be sent out with this envelope
		envelope.bodyOut = request;

		request.addProperty("userId", SessionStorage.userId);

		HttpTransportSE transport = new HttpTransportSE(
				SessionStorage.webserviceURL);
		try {
			transport.call(SOAP_ACTION, envelope);
			Object response = envelope.getResponse();

			Log.d("address Details", response.toString());
			JSONObject json = new JSONObject(response.toString());
			JSONArray jsonarray = json.getJSONArray("address");

			// Reading through the array of products
			for (int i = 0; i < jsonarray.length(); i++) {
				JSONObject object = jsonarray.getJSONObject(i);

				addressId = object.getString("id");
				addressLine1 = object.getString("address1");
				addressLine2 = object.getString("address2");
				addressLine3 = object.getString("address3");
				city = object.getString("city");
				state = object.getString("state");
				phone = object.getString("phone");
				fName = object.getString("firstName");
				lName = object.getString("lastName");
				pincode = object.getString("pincode");
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
			pd = new ProgressDialog(DeliveryAddressEdit.this);
			pd.setMessage("Loading...");
			pd.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			pd.dismiss();

			printAddress();
			/*
			 * if (!city.isEmpty()) { printAddress(); }
			 */
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				getAddress();

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

				Intent inten1 = new Intent(getApplicationContext(),
						ManageAccount.class);
				startActivity(inten1);

			}
		});

		wishlistImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (SessionStorage.clientId.isEmpty()) {
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

}

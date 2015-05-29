package app.retailinsights.neulife.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.StateSet;
import android.widget.Toast;
import app.retailinsights.neulife.bean.BeanProductListing;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 9;

	// Database Name
	public static final String DATABASE_NAME = "DB";
	// pRODUCTS table name
	private static final String TABLE_PRODUCTS = "products";
	// pRODUCTS table name
	private static final String TABLE_PRODUCTS_SORT = "products_sort";
	// pRODUCTS table name
	private static final String TABLE_CITY = "city";
	// pRODUCTS table name
	private static final String TABLE_STATE = "state";
	private static final String TABLE_FLAVOR = "flavor";
	private static final String TABLE_CART = "cart";
	// PRODUCTS Table Columns names
	private static final String KEY_PROD_ID = "prod_id";
	private static final String KEY_PROD_NAME = "productName";
	private static final String KEY_BRAND_NAME = "brandName";
	private static final String KEY_SKU_ID = "sku_id";
	private static final String KEY_SKU_NAME = "sku_name";
	private static final String KEY_PROD_CODE = "prod_code";
	private static final String KEY_SKU_CODE = "sku_code";
	private static final String KEY_PROD_DESCRIPTION = "prod_description";
	private static final String KEY_PROD_INGREDIANTS = "prod_ingrediants";
	private static final String KEY_PROD_FACTS = "prod_facts";
	private static final String KEY_PROD_O_PRICE = "prod_o_price";
	private static final String KEY_PROD_P_PRICE = "prod_p_price";
	private static final String KEY_PROD_AVAIL = "prod_availability";
	private static final String KEY_PROD_QTY = "prod_quantity";
	private static final String KEY_PROD_SIZE = "size";
	private static final String KEY_FLAVOUR = "flavours";
	private static final String KEY_SIZE_CODE = "sizeCode";
	private static final String KEY_FLAVOUR_CODE = "flavourCode";
	private static final String KEY_IMG_URL = "img_url";
	private static final String KEY_RATING = "rating";
	private static final String KEY_REVIEW = "review";
	private static final String KEY_DISCOUNTS = "discounts";
	private static final String KEY_P_ID = "p_id";
	private static final String KEY_P_QTY = "qty";
	private static final String KEY_CREATED_AT = "created_at";

	// City table
	private static final String KEY_C_ID = "c_id";
	private static final String KEY_C_NAME = "city_name";

	// City table
	private static final String KEY_F_ID = "f_id";
	private static final String KEY_FLAVOR_ID1 = "flavor_code";
	private static final String KEY_FLAVOR_NAME1 = "flavor_name";

	// State table
	private static final String KEY_S_ID = "s_id";
	private static final String KEY_S_NAME = "state_name";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
				+ KEY_PROD_ID + " TEXT," + KEY_PROD_NAME + " TEXT,"
				+ KEY_BRAND_NAME + " TEXT," + KEY_PROD_CODE + " TEXT,"
				+ KEY_PROD_SIZE + " TEXT," + KEY_SKU_ID + " INTEGER,"
				+ KEY_SKU_NAME + " TEXT," + KEY_SKU_CODE + " TEXT,"
				+ KEY_FLAVOUR_CODE + " TEXT," + KEY_SIZE_CODE + " TEXT,"
				+ KEY_PROD_DESCRIPTION + " TEXT," + KEY_PROD_INGREDIANTS
				+ " TEXT," + KEY_PROD_FACTS + " TEXT," + KEY_PROD_O_PRICE
				+ " INTEGER," + KEY_PROD_P_PRICE + " INTEGER," + KEY_PROD_AVAIL
				+ " TEXT," + KEY_PROD_QTY + " TEXT," + KEY_IMG_URL + " TEXT,"
				+ KEY_FLAVOUR + " TEXT," + KEY_REVIEW + " TEXT," + KEY_RATING
				+ " TEXT," + KEY_DISCOUNTS + " INTEGER" + ")";
		db.execSQL(CREATE_PRODUCT_TABLE);
		String CREATE_PRODUCT_SORT_TABLE = "CREATE TABLE "
				+ TABLE_PRODUCTS_SORT + "(" + KEY_PROD_ID + " TEXT,"
				+ KEY_PROD_NAME + " TEXT," + KEY_BRAND_NAME + " TEXT,"
				+ KEY_PROD_CODE + " TEXT," + KEY_PROD_SIZE + " TEXT,"
				+ KEY_SKU_ID + " INTEGER," + KEY_SKU_NAME + " TEXT,"
				+ KEY_SKU_CODE + " TEXT," + KEY_FLAVOUR_CODE + " TEXT,"
				+ KEY_SIZE_CODE + " TEXT," + KEY_PROD_DESCRIPTION + " TEXT,"
				+ KEY_PROD_INGREDIANTS + " TEXT," + KEY_PROD_FACTS + " TEXT,"
				+ KEY_PROD_O_PRICE + " INTEGER," + KEY_PROD_P_PRICE
				+ " INTEGER," + KEY_PROD_AVAIL + " TEXT," + KEY_PROD_QTY
				+ " TEXT," + KEY_IMG_URL + " TEXT," + KEY_FLAVOUR + " TEXT,"
				+ KEY_REVIEW + " TEXT," + KEY_RATING + " TEXT," + KEY_DISCOUNTS
				+ " INTEGER" + ")";
		db.execSQL(CREATE_PRODUCT_SORT_TABLE);

		String CREATE_CITY_TABLE = "CREATE TABLE " + TABLE_CITY + "("
				+ KEY_C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_C_NAME
				+ " TEXT," + KEY_S_ID + " INTEGER )";
		db.execSQL(CREATE_CITY_TABLE);
		String CREATE_STATE_TABLE = "CREATE TABLE " + TABLE_STATE + "("
				+ KEY_S_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_S_NAME
				+ " TEXT )";
		db.execSQL(CREATE_STATE_TABLE);
		String CREATE_STATE_CART = "CREATE TABLE " + TABLE_CART + "("
				+ KEY_C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_P_ID
				+ " INTEGER, " + KEY_P_QTY + " INTEGER)";
		db.execSQL(CREATE_STATE_CART);
		String CREATE_FLAVOR = "CREATE TABLE " + TABLE_FLAVOR + "(" + KEY_F_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_FLAVOR_ID1
				+ " TEXT, " + KEY_FLAVOR_NAME1 + " TEXT)";
		db.execSQL(CREATE_FLAVOR);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addProducts(String prod_id, String prod_name, String prod_code,
			String prod_size, String prod_o_price, String prod_p_price,
			String prod_avail, String prod_qty, String prod_img_url,
			String prod_flavourr, String prod_review, String prod_rating,
			String brand_name, String facts, String ingrediants,
			String description, String sku_id, String sku_name,
			String sku_code, String discounts, String flavorCode,
			String sizeCode) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_PROD_ID, prod_id);
		values.put(KEY_PROD_NAME, prod_name);
		values.put(KEY_PROD_CODE, prod_code);
		values.put(KEY_PROD_SIZE, prod_size);
		values.put(KEY_PROD_O_PRICE, prod_o_price);
		values.put(KEY_PROD_P_PRICE, prod_p_price);
		values.put(KEY_PROD_AVAIL, prod_avail);
		values.put(KEY_PROD_QTY, prod_qty);
		values.put(KEY_IMG_URL, prod_img_url);
		values.put(KEY_FLAVOUR, prod_flavourr);
		values.put(KEY_REVIEW, prod_review);
		values.put(KEY_RATING, prod_rating);
		values.put(KEY_BRAND_NAME, brand_name);
		values.put(KEY_PROD_FACTS, facts);
		values.put(KEY_PROD_INGREDIANTS, ingrediants);
		values.put(KEY_PROD_DESCRIPTION, description);
		values.put(KEY_SKU_ID, sku_id);
		values.put(KEY_SKU_NAME, sku_name);
		values.put(KEY_SKU_CODE, sku_code);
		values.put(KEY_DISCOUNTS, discounts);
		values.put(KEY_SIZE_CODE, sizeCode);
		values.put(KEY_FLAVOUR_CODE, flavorCode);
		// Inserting Row
		db.insert(TABLE_PRODUCTS, null, values);
		db.close(); // Closing database connection
	}

	/**
	 * Storing user details in database
	 * */
	public void addProductsSort(String prod_id, String prod_name,
			String prod_code, String prod_size, String prod_o_price,
			String prod_p_price, String prod_avail, String prod_qty,
			String prod_img_url, String prod_flavourr, String prod_review,
			String prod_rating, String brand_name, String facts,
			String ingrediants, String description, String sku_id,
			String sku_name, String sku_code, String discounts,
			String flavorCode, String sizeCode) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_PROD_ID, prod_id);
		values.put(KEY_PROD_NAME, prod_name);
		values.put(KEY_PROD_CODE, prod_code);
		values.put(KEY_PROD_SIZE, prod_size);
		values.put(KEY_PROD_O_PRICE, prod_o_price);
		values.put(KEY_PROD_P_PRICE, prod_p_price);
		values.put(KEY_PROD_AVAIL, prod_avail);
		values.put(KEY_PROD_QTY, prod_qty);
		values.put(KEY_IMG_URL, prod_img_url);
		values.put(KEY_FLAVOUR, prod_flavourr);
		values.put(KEY_REVIEW, prod_review);
		values.put(KEY_RATING, prod_rating);
		values.put(KEY_BRAND_NAME, brand_name);
		values.put(KEY_PROD_FACTS, facts);
		values.put(KEY_PROD_INGREDIANTS, ingrediants);
		values.put(KEY_PROD_DESCRIPTION, description);
		values.put(KEY_SKU_ID, sku_id);
		values.put(KEY_SKU_NAME, sku_name);
		values.put(KEY_SKU_CODE, sku_code);
		values.put(KEY_DISCOUNTS, discounts);
		values.put(KEY_SIZE_CODE, sizeCode);
		values.put(KEY_FLAVOUR_CODE, flavorCode);
		// Inserting Row
		db.insert(TABLE_PRODUCTS_SORT, null, values);
		db.close(); // Closing database connection
	}

	/**
	 * Storing CITY details in database
	 * */
	public void addCity(String city_name, int state_id) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_C_NAME, city_name);
		values.put(KEY_S_ID, state_id);

		// Inserting Row
		db.insert(TABLE_CITY, null, values);
		db.close(); // Closing database connection
	}

	/**
	 * Storing CITY details in database
	 * */
	public void addToCartTable(int p_id, int qty) {
		SQLiteDatabase db = this.getWritableDatabase();
		Log.w("in addToCartTable", "p_id: " + p_id + "qty: " + qty);
		String selectQuery = "SELECT p_id FROM " + TABLE_CART
				+ " WHERE p_id = " + p_id;
		Log.w("cartQuery", selectQuery);
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		if (cursor.getCount() > 0) {
			ContentValues data = new ContentValues();
			data.put("qty", qty);
			db.update(TABLE_CART, data, "p_id=" + p_id, null);
		} else {
			ContentValues values = new ContentValues();
			values.put(KEY_P_ID, p_id);
			values.put(KEY_P_QTY, qty);
			// Inserting Row
			db.insert(TABLE_CART, null, values);
		}

		db.close(); // Closing database connection
	}

	/**
	 * Storing STATE details in database
	 * */
	public void addState(String state_name) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_S_NAME, state_name);

		// Inserting Row
		db.insert(TABLE_STATE, null, values);
		db.close(); // Closing database connection
	}

	/**
	 * Storing STATE details in database
	 * */
	public void addFlavor(String flavCode, String flavor) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_FLAVOR_NAME1, flavor);
		values.put(KEY_FLAVOR_ID1, flavCode);
		// Inserting Row
		db.insert(TABLE_FLAVOR, null, values);
		db.close(); // Closing database connection
	}

	/**
	 * Storing STATE details in database
	 * */
	public ArrayList<HashMap<String, String>> getFlavor() {
		ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> hmFlavFlavCode;
		String selectQuery = "SELECT * FROM " + TABLE_FLAVOR;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			hmFlavFlavCode = new HashMap<String, String>();
			hmFlavFlavCode.put(
					cursor.getString(cursor.getColumnIndex(KEY_FLAVOR_NAME1)),
					cursor.getString(cursor.getColumnIndex(KEY_FLAVOR_ID1)));
			arrayList.add(hmFlavFlavCode);
		}
		cursor.close();
		db.close();
		// return user
		return arrayList;
	}

	/**
	 * Storing STATE details in database
	 * */
	public String[] getFlavorString() {
		String[] flavour;
		String selectQuery = "SELECT " + KEY_FLAVOR_NAME1 + " FROM "
				+ TABLE_FLAVOR;
		SQLiteDatabase db = this.getReadableDatabase();
		int i = 0;
		Cursor cursor = db.rawQuery(selectQuery, null);
		flavour = new String[cursor.getCount()];
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				flavour[i++] = cursor.getString(cursor
						.getColumnIndex(KEY_FLAVOR_NAME1));
			}
		}
		cursor.close();
		db.close();
		// return user
		return flavour;
	}

	/**
	 * Getting user data from database
	 * */
	public int getStateID(String state) {
		int stateId = 0;
		String selectQuery = "SELECT s_id FROM " + TABLE_STATE
				+ " WHERE state_name =  " + "'" + state + "'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			stateId = cursor.getInt(cursor.getColumnIndex(KEY_S_ID));
		}
		cursor.close();
		db.close();
		// return user
		return stateId;
	}

	public String[] getAllStates() {
		String[] states;
		String selectQuery = "SELECT state_name FROM " + TABLE_STATE;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		states = new String[cursor.getCount()];
		// Move to first row
		if (cursor.getCount() > 0) {
			int i = 0;
			while (cursor.moveToNext())
				states[i++] = cursor.getString(cursor
						.getColumnIndex(KEY_S_NAME));
		}
		cursor.close();
		db.close();
		// return user
		return states;
	}

	public String[] getAllCities(int state_id) {
		String[] cities;
		String selectQuery = "SELECT city_name FROM " + TABLE_CITY
				+ " WHERE s_id =" + state_id;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		cities = new String[cursor.getCount()];
		// Move to first row
		if (cursor.getCount() > 0) {
			int i = 0;
			while (cursor.moveToNext())
				cities[i++] = cursor.getString(cursor
						.getColumnIndex(KEY_C_NAME));
		}
		cursor.close();
		db.close();
		// return user
		return cities;
	}

	/**
	 * Getting products based on price
	 * */
	public ArrayList<BeanProductListing> getProductsOnPriceASC() {
		HashMap<String, String> hmProdOnPrice = new HashMap<String, String>();
		ArrayList<BeanProductListing> beanProductListings = new ArrayList<BeanProductListing>();
		BeanProductListing beanProductListing;
		String selectQuery = "select * from " + TABLE_PRODUCTS + " order by "
				+ KEY_PROD_P_PRICE + " ASC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		Log.w("ASC cursor count: ", "" + cursor.getCount());
		// Move to first row
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				beanProductListing = new BeanProductListing();
				beanProductListing.setProductId(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_ID)));
				beanProductListing.setProductName(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_NAME)));
				beanProductListing.setOPrice(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_O_PRICE)));
				beanProductListing.setPPrice(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_P_PRICE)));
				beanProductListing.setPAvaiability(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_AVAIL)));

				beanProductListing.setFlavour(cursor.getString(cursor
						.getColumnIndex(KEY_FLAVOUR)));
				beanProductListing.setDiscount(cursor.getString(cursor
						.getColumnIndex(KEY_DISCOUNTS)));
				beanProductListing.setDescription(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_DESCRIPTION)));
				beanProductListing.setIngrediants(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_INGREDIANTS)));

				beanProductListing.setSizeCode(cursor.getString(cursor
						.getColumnIndex(KEY_SIZE_CODE)));

				beanProductListing.setFlavourCode(cursor.getString(cursor
						.getColumnIndex(KEY_FLAVOUR_CODE)));

				beanProductListings.add(beanProductListing);
			}
		}
		cursor.close();
		db.close();
		// return user
		Log.w("hmProdOnPrice size in DBHandler: ",
				"" + beanProductListings.size());
		return beanProductListings;
	}

	/**
	 * Getting products based on price
	 * */
	public ArrayList<BeanProductListing> getProductsOnPriceASCSort() {
		HashMap<String, String> hmProdOnPrice = new HashMap<String, String>();
		ArrayList<BeanProductListing> beanProductListings = new ArrayList<BeanProductListing>();
		BeanProductListing beanProductListing;
		String selectQuery = "select * from " + TABLE_PRODUCTS_SORT
				+ " order by " + KEY_PROD_P_PRICE + " ASC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		Log.w("ASC cursor count: ", "" + cursor.getCount());
		// Move to first row
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				beanProductListing = new BeanProductListing();
				beanProductListing.setProductId(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_ID)));
				beanProductListing.setProductName(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_NAME)));
				beanProductListing.setOPrice(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_O_PRICE)));
				beanProductListing.setPPrice(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_P_PRICE)));
				beanProductListing.setPAvaiability(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_AVAIL)));

				beanProductListing.setFlavour(cursor.getString(cursor
						.getColumnIndex(KEY_FLAVOUR)));
				beanProductListing.setDiscount(cursor.getString(cursor
						.getColumnIndex(KEY_DISCOUNTS)));
				beanProductListing.setDescription(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_DESCRIPTION)));
				beanProductListing.setIngrediants(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_INGREDIANTS)));
				beanProductListing.setSizeCode(cursor.getString(cursor
						.getColumnIndex(KEY_SIZE_CODE)));

				beanProductListing.setFlavourCode(cursor.getString(cursor
						.getColumnIndex(KEY_FLAVOUR_CODE)));
				beanProductListings.add(beanProductListing);
			}
		}
		cursor.close();
		db.close();
		// return user
		Log.w("hmProdOnPrice size in DBHandler: ",
				"" + beanProductListings.size());
		return beanProductListings;
	}

	/**
	 * Getting products based on price
	 * */
	public ArrayList<BeanProductListing> getProductsOnPriceDESC() {
		HashMap<String, String> hmProdOnPrice = new HashMap<String, String>();
		ArrayList<BeanProductListing> beanProductListings = new ArrayList<BeanProductListing>();
		BeanProductListing beanProductListing;
		String selectQuery = "select * from " + TABLE_PRODUCTS + " order by "
				+ KEY_PROD_P_PRICE + " DESC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		Log.w("ASC cursor count: ", "" + cursor.getCount());
		// Move to first row
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				beanProductListing = new BeanProductListing();
				beanProductListing.setProductId(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_ID)));
				beanProductListing.setProductName(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_NAME)));
				beanProductListing.setOPrice(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_O_PRICE)));
				beanProductListing.setPPrice(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_P_PRICE)));
				beanProductListing.setPAvaiability(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_AVAIL)));

				beanProductListing.setFlavour(cursor.getString(cursor
						.getColumnIndex(KEY_FLAVOUR)));
				beanProductListing.setDiscount(cursor.getString(cursor
						.getColumnIndex(KEY_DISCOUNTS)));
				beanProductListing.setDescription(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_DESCRIPTION)));
				beanProductListing.setIngrediants(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_INGREDIANTS)));
				beanProductListing.setSizeCode(cursor.getString(cursor
						.getColumnIndex(KEY_SIZE_CODE)));

				beanProductListing.setFlavourCode(cursor.getString(cursor
						.getColumnIndex(KEY_FLAVOUR_CODE)));
				beanProductListings.add(beanProductListing);
			}
		}
		cursor.close();
		db.close();
		// return user
		Log.w("hmProdOnPrice size in DBHandler: ",
				"" + beanProductListings.size());
		return beanProductListings;
	}

	/**
	 * Getting products based on price
	 * */
	public ArrayList<BeanProductListing> getProductsOnPriceDESCSort() {
		HashMap<String, String> hmProdOnPrice = new HashMap<String, String>();
		ArrayList<BeanProductListing> beanProductListings = new ArrayList<BeanProductListing>();
		BeanProductListing beanProductListing;
		String selectQuery = "select * from " + TABLE_PRODUCTS_SORT
				+ " order by " + KEY_PROD_P_PRICE + " DESC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		Log.w("ASC cursor count: ", "" + cursor.getCount());
		// Move to first row
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				beanProductListing = new BeanProductListing();
				beanProductListing.setProductId(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_ID)));
				beanProductListing.setProductName(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_NAME)));
				beanProductListing.setOPrice(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_O_PRICE)));
				beanProductListing.setPPrice(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_P_PRICE)));
				beanProductListing.setPAvaiability(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_AVAIL)));

				beanProductListing.setFlavour(cursor.getString(cursor
						.getColumnIndex(KEY_FLAVOUR)));
				beanProductListing.setDiscount(cursor.getString(cursor
						.getColumnIndex(KEY_DISCOUNTS)));
				beanProductListing.setDescription(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_DESCRIPTION)));
				beanProductListing.setIngrediants(cursor.getString(cursor
						.getColumnIndex(KEY_PROD_INGREDIANTS)));
				beanProductListing.setSizeCode(cursor.getString(cursor
						.getColumnIndex(KEY_SIZE_CODE)));

				beanProductListing.setFlavourCode(cursor.getString(cursor
						.getColumnIndex(KEY_FLAVOUR_CODE)));
				beanProductListings.add(beanProductListing);
			}
		}
		cursor.close();
		db.close();
		// return user
		Log.w("hmProdOnPrice size in DBHandler: ",
				"" + beanProductListings.size());
		return beanProductListings;
	}

	/**
	 * Getting user login status return true if rows are there in table
	 * */
	public String[] getBrandNames() {
		String countQuery = "SELECT DISTINCT brandName FROM " + TABLE_PRODUCTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		ArrayList<String> alBrands = new ArrayList<String>();
		int rowCount = cursor.getCount();
		String[] brand = new String[rowCount];
		int i = 0;
		// cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				if (!cursor.getString(0).trim().equals("")
						&& cursor.getString(0).length() > 0) {
					alBrands.add(cursor.getString(0));
					/*
					 * brand[i++] = cursor.getString(0); Log.w("brand"+brand[i],
					 * "");
					 */
				}
			}
		}
		String brands[] = new String[alBrands.size()];
		for (int j = 0; j < alBrands.size(); j++) {
			brands[j] = alBrands.get(j);
		}

		db.close();
		cursor.close();
		// return row count
		return brands;
	}

	public HashMap<Integer, Integer> getCartDetails() {
		String countQuery = "SELECT * FROM " + TABLE_CART;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		HashMap<Integer, Integer> hmCartIdQty = new HashMap<Integer, Integer>();
		// cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				hmCartIdQty.put(cursor.getInt(1), cursor.getInt(2));
			}
		}
		db.close();
		cursor.close();
		// return row count
		return hmCartIdQty;
	}

	public int getCartPidQty(int pid) {
		String countQuery = "SELECT qty FROM " + TABLE_CART + " WHERE p_id = "
				+ pid;
		SQLiteDatabase db = this.getReadableDatabase();
		int qty = 0;
		Cursor cursor = db.rawQuery(countQuery, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				qty = cursor.getInt(2);
			}
		}
		db.close();
		cursor.close();
		Log.w("qty query: " + countQuery, "");
		return qty;
	}

	/*
	 * getFlavours
	 */
	public String[] getFlavours() {
		String countQuery = "SELECT flavours FROM " + TABLE_PRODUCTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		String[] brand = new String[rowCount];
		int i = 0;
		// cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				brand[i++] = cursor.getString(0);
			}
		}
		db.close();
		cursor.close();
		// return row count
		return brand;
	}
	
	/*
	 * getFlavours
	 */
	public String getFlavourCode(String flavName) {
		String countQuery = "SELECT "+KEY_FLAVOR_ID1+" FROM " + TABLE_FLAVOR+ " where "+KEY_FLAVOR_NAME1+" like "+"'%"+flavName+"%'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		String brand = "";
		// cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				brand = cursor.getString(cursor
						.getColumnIndex(KEY_FLAVOR_ID1));
			}
		}
		db.close();
		cursor.close();
		// return row count
		return brand;
	}


	/**
	 * *
	 * */
	public ArrayList<BeanProductListing> getProductsOnFilteer(
			HashMap<String, ArrayList<String>> mapFilterData) {
		ArrayList<BeanProductListing> beanProductListings = new ArrayList<BeanProductListing>();
		BeanProductListing beanProductListing;
		String where = "";
		ArrayList<String> alBrand = new ArrayList<String>();
		alBrand = mapFilterData.get("brand");
		Log.w("alBrand.size", "" + alBrand.size());
		if (alBrand.size() > 0) {
			where += " (";
			for (int i = 0; i < alBrand.size(); i++) {
				if (where.equals(" (")) {
					where += " (brandName like '%" + alBrand.get(i) + "%' )";
				} else {
					where += " or (brandName like '%" + alBrand.get(i) + "%' )";
				}
				// where += "  productName like '%" + alBrand.get(i) + "%' or";
			}
			/*
			 * String path = where.substring(0, where.length() - 2); where +=
			 * path;
			 */
			where += ") ";
		}

		ArrayList<String> alPrice = new ArrayList<String>();
		alPrice = mapFilterData.get("price");
		Log.w("alPrice.size", "" + alPrice.size());
		if (alPrice.size() > 0) {
			for (int i = 0; i < alPrice.size(); i++) {
				Log.w("alPrice.get(i)", "" + alPrice.get(i));
				Log.w("price separated: ", "" + alPrice.get(i).substring(0, 4));
				int price = Integer.valueOf(alPrice.get(i).substring(0, 4));
				price = price + 4000;
				String[] sPrice = alPrice.get(i).split("-");
				Log.w("price added: ", "" + price);

				if (where.isEmpty()) {
					where += " (prod_p_price >= " + sPrice[0] + " AND "
							+ " prod_p_price < " + sPrice[1] + ")";
				} else {
					where += " AND (prod_p_price >= " + sPrice[0] + " AND "
							+ " prod_p_price < " + sPrice[1] + ")";
				}
			}
		}
		ArrayList<String> alFlavour = new ArrayList<String>();
		alFlavour = mapFilterData.get("flavour");
		Log.w("alFlavour.size", "" + alFlavour.size());
		if (alFlavour.size() > 0) {
			for (int i = 0; i < alFlavour.size(); i++) {
				Log.w("alPrice.get(i)", "" + alFlavour.get(i));
				/*if (where.isEmpty()) {
					where += " (flavourCode like '%" + alFlavour.get(i) + "%') ";
				} else {
					where += " or (flavourCode like '%" + alFlavour.get(i)
							+ "%') ";
				}*/
				if (where.isEmpty()) {
					where += " (flavourCode = '" + alFlavour.get(i) + "') ";
				} else {
					where += " and (flavourCode = '" + alFlavour.get(i)+ "') ";
				}
			}
		}
		if (where.isEmpty()) {
			where = " 1";
		}
		if (!where.isEmpty()) {
			String query = "SELECT * FROM products WHERE " + where;
			// String query =
			// "SELECT * FROM products WHERE (  productName like '%ABB-Extreme XXL%' or  productName like '%BSN-AminoX%' or  productName like '%BSN-CellMass 2.0%' or  productName like '%BSN-HyperFx%' or  productName like '%BSN-Syntha-6%' or  productName like '%BSN-True Mass%' or  productName like '%CELLUCOR - C4 Extreme%' or  productName like '%DYMATIZE- Elite Casein%' or (  productName like '%ABB-Extreme XXL%' or  productName like '%BSN-AminoX%' or  productName like '%BSN-CellMass 2.0%' or  productName like '%BSN-HyperFx%' or  productName like '%BSN-Syntha-6%' or  productName like '%BSN-True Mass%' or  productName like '%CELLUCOR - C4 Extreme%' or  productName like '%DYMATIZE- Elite Casein%' )  AND (prod_p_price >= 5000 AND  prod_p_price < 10000)";
			Log.w("query: ", "" + query);
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(query, null);
			Log.w("query count for filter prods: ", "" + cursor.getCount());
			// Move to first row
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					beanProductListing = new BeanProductListing();
					beanProductListing.setProductId(cursor.getString(cursor
							.getColumnIndex(KEY_PROD_ID)));
					beanProductListing.setProductName(cursor.getString(cursor
							.getColumnIndex(KEY_PROD_NAME)));
					beanProductListing.setOPrice(cursor.getString(cursor
							.getColumnIndex(KEY_PROD_O_PRICE)));
					beanProductListing.setPPrice(cursor.getString(cursor
							.getColumnIndex(KEY_PROD_P_PRICE)));
					beanProductListing.setPAvaiability(cursor.getString(cursor
							.getColumnIndex(KEY_PROD_AVAIL)));

					beanProductListing.setFlavour(cursor.getString(cursor
							.getColumnIndex(KEY_FLAVOUR)));
					beanProductListing.setDiscount(cursor.getString(cursor
							.getColumnIndex(KEY_DISCOUNTS)));
					beanProductListing.setDescription(cursor.getString(cursor
							.getColumnIndex(KEY_PROD_DESCRIPTION)));
					beanProductListing.setIngrediants(cursor.getString(cursor
							.getColumnIndex(KEY_PROD_INGREDIANTS)));

					beanProductListing.setBrandName(cursor.getString(cursor
							.getColumnIndex(KEY_BRAND_NAME)));

					beanProductListing.setProductCode(cursor.getString(cursor
							.getColumnIndex(KEY_PROD_CODE)));

					beanProductListing.setSizeCode(cursor.getString(cursor
							.getColumnIndex(KEY_SIZE_CODE)));

					beanProductListing.setFlavourCode(cursor.getString(cursor
							.getColumnIndex(KEY_FLAVOUR_CODE)));

					/*
					 * beanProductListing.setProductCode(cursor.getString(cursor
					 * .getColumnIndex(KEY_PROD_CODE)));
					 */

					beanProductListings.add(beanProductListing);
				}
			}
			alBrand.clear();
			alPrice.clear();
			where = "";
			cursor.close();
			db.close();
			// return user
			Log.w("hmProdOnPrice size in DBHandler: ",
					"" + beanProductListings.size());
			return beanProductListings;
		} else {

			// return user
			Log.w("hmProdOnPrice size in DBHandler: ",
					"" + beanProductListings.size());
			return beanProductListings;
		}
	}

	/**
	 * Getting user login status return true if rows are there in table
	 * */
	public int getRowCount() {
		String countQuery = "SELECT  * FROM " + TABLE_PRODUCTS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();
		// return row count
		return rowCount;
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void resetTables() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_PRODUCTS, null, null);
		db.close();
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void resetTablesSort() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_PRODUCTS_SORT, null, null);
		db.close();
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void resetCartTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_CART, null, null);
		db.close();
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void resetLocTables() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_CITY, null, null);
		db.delete(TABLE_STATE, null, null);
		db.close();
	}

	/*
	 * view data
	 */
	public ArrayList<Cursor> getData(String Query) {
		// get writable database
		SQLiteDatabase sqlDB = this.getWritableDatabase();
		String[] columns = new String[] { "mesage" };
		// an array list of cursor to save two cursors one has results from the
		// query
		// other cursor stores error message if any errors are triggered
		ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
		MatrixCursor Cursor2 = new MatrixCursor(columns);
		alc.add(null);
		alc.add(null);

		try {
			String maxQuery = Query;
			// execute the query results will be save in Cursor c
			Cursor c = sqlDB.rawQuery(maxQuery, null);

			// add value to cursor2
			Cursor2.addRow(new Object[] { "Success" });

			alc.set(1, Cursor2);
			if (null != c && c.getCount() > 0) {

				alc.set(0, c);
				c.moveToFirst();

				return alc;
			}
			return alc;
		} catch (SQLException sqlEx) {
			Log.d("printing exception", sqlEx.getMessage());
			// if any exceptions are triggered save the error message to cursor
			// an return the arraylist
			Cursor2.addRow(new Object[] { "" + sqlEx.getMessage() });
			alc.set(1, Cursor2);
			return alc;
		} catch (Exception ex) {

			Log.d("printing exception", ex.getMessage());

			// if any exceptions are triggered save the error message to cursor
			// an return the arraylist
			Cursor2.addRow(new Object[] { "" + ex.getMessage() });
			alc.set(1, Cursor2);
			return alc;
		}

	}

}

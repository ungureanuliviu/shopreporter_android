package com.liviu.apps.shopreporter.managers;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.liviu.apps.shopreporter.data.Product;
import com.liviu.apps.shopreporter.data.Session;
import com.liviu.apps.shopreporter.data.User;
import com.liviu.apps.shopreporter.exceptions.InvalidUnitException;
import com.liviu.apps.shopreporter.utils.Console;
import com.liviu.apps.shopreporter.utils.Utils;

public class DatabaseManger {
	// Constants
	private final String TAG			= "Database";
	private final String TABLE_USERS	= "table_users";
	private final String TABLE_SESSIONS = "table_sessions";
	private final String TABLE_PRODUCTS = "table_products";
	private final String DB_NAME		= "shp_db";
	
	private final String CREATE_TABLE_USERS    		= "create table if not exists table_users( id integer primary key autoincrement," +
																							  "authname integer not null unique," +
																							  "password integer not null," +
																							  "fname text not null," +
																							  "sname text not null," +
																							  "is_active integer not null default 1," +
																							  "sessions_count integer default 0," +
																							  "sessions_total_money double default 0.00," +
																							  "sessions_total_time long default 0);";
	private final String USER_ID					= "id";
	private final String USER_FNAME 				= "fname";
	private final String USER_SNAME					= "sname";
	private	final String USER_SESSIONS_COUNT 		= "sessions_count";
	private final String USER_SESSIONS_TOTAL_MONEY  = "sessions_total_money";
	private final String USER_SESSIONS_TOTAL_TIME	= "sessions_total_time";
	private final String USER_IS_ACTIVE				= "is_active";
	private final String USER_AUTH_NAME				= "authname";
	private final String USER_PASSWORD				= "password";
	
	private final String CREATE_TABLE_SESSIONS		= "create table if not exists table_sessions ( id integer primary key autoincrement," +
																								   "user_id integer not null," +
																								   "location text not null," +
																								   "name text not null," +
																								   "total_time long default 0," +
																								   "total_money double default 0.00," +
																								   "start_time long not null," +
																								   "end_time long not null default -1," +
																								   "is_paused integer not null default 0," +																								   
																								   "total_products integer default 0);";
	private final String SESSION_ID 			= "id";
	private final String SESSION_USER_ID 		= "user_id";
	private final String SESSION_LOCATION 		= "location";
	private final String SESSION_NAME 			= "name";
	private final String SESSION_TOTAL_TIME 	= "total_time";
	private final String SESSION_TOTAL_MONEY 	= "total_money";
	private final String SESSION_START_TIME  	= "start_time";
	private final String SESSION_END_TIME		= "end_time";
	private final String SESSION_IS_PAUSED 		= "is_paused";
	private final String SESSION_TOTAL_PRODUCTS = "total_products";
	
	private final String CREATE_TABLE_PRODUCTS 	= "create table if not exists table_products( id integer primary key autoincrement," +											
																							 "session_id integer not null," +
																							 "name text not null," +
																							 "qty integer not null," +
																							 "price double not null," +
																							 "added_time long not null," +
																							 "units text not null);";
	private final String PRODUCT_ID			 	= "id";
	private final String PRODUCT_SESSION_ID 	= "session_id";
	private final String PRODUCT_NAME 			= "name";
	private final String PRODUCT_QTY 			= "qty";
	private final String PRODUCT_PRICE			= "price";
	private final String PRODUCT_ADDED_TIME 	= "added_time";
	private final String PRODUCT_UNITS 			= "units";
																							
	
	// Data
	private static DatabaseManger instance;
	private Context context;
	private SQLiteDatabase db;	
	
	private DatabaseManger(Context ctx) {
		context = ctx;		
		openOrCreateDatabase();
	}
	
	public static DatabaseManger getInstance(Context ctx){
		if(instance == null){					
			instance = new DatabaseManger(ctx);			
		}
		
		return instance;			
	}
	
	private synchronized boolean openOrCreateDatabase(){
		try{
			Console.debug(TAG, "here1");
			db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
			db.execSQL(CREATE_TABLE_USERS);
			db.execSQL(CREATE_TABLE_SESSIONS);
			db.execSQL(CREATE_TABLE_PRODUCTS);
			closeDatabase();
			return true;
		}
		catch (SQLException e){
			e.printStackTrace();
			return false;
		}		
		catch (IllegalStateException e) {
			e.printStackTrace();
			closeDatabase();
			return false;
		}			
	}
	
	private synchronized boolean openDatabase(){	
		if(db != null && db.isOpen())
			db.close();
		try{
			db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);			
			Log.e(TAG, "db.isOpen: " + db.isOpen());
			return true;
		}
		catch (SQLException e){
			e.printStackTrace();
			Log.e(TAG, "db.isOpen exception: " + db.isOpen());
			return false;
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
			Log.e(TAG, "database is not closed in openDatabase()");
			openOrCreateDatabase();
			closeDatabase();
			return openDatabase();
		}		
	}			
	private synchronized void closeDatabase(){		
		if(db.isOpen())
			db.close();
	}

	public int createUser(String userName, String userPassword, String userFName, String userSName) {
		ContentValues values = new ContentValues();
		
		values.put(USER_AUTH_NAME, userName);
		values.put(USER_PASSWORD, userPassword);
		values.put(USER_FNAME, userFName);
		values.put(USER_SNAME, userSName);
		values.put(USER_IS_ACTIVE, 1);
		values.put(USER_SESSIONS_TOTAL_MONEY, 0);
		values.put(USER_SESSIONS_COUNT, 0);
		values.put(USER_SESSIONS_TOTAL_TIME, 0);
		int userId = -1;
		
		openDatabase();
		try{
			userId = (int) db.insertOrThrow(TABLE_USERS, null, values);
			Console.debug(TAG, "new id: " + userId);
			if(userId != -1){
				// make other user inactive
				ContentValues updatedValues = new ContentValues(1);
				updatedValues.put(USER_IS_ACTIVE, 0);
				int affectedRows = db.update(TABLE_USERS, updatedValues, USER_ID + "<>" + userId, null);
				Console.debug(TAG, "AffectedRows: " + affectedRows);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			userId = -1;
		}
			
		closeDatabase();
		return userId;
	}	
	
	public Bundle loginUser(String userName, String password){
		Bundle			b 				= new Bundle();
		ContentValues 	values 			= new ContentValues();
		int 			affectedRows 	= 0;
		
		values.put(USER_IS_ACTIVE, 1);				
		openDatabase();
		affectedRows = db.update(TABLE_USERS, values, USER_AUTH_NAME +"='" + userName + "' AND " + USER_PASSWORD + "='" + password +"'", null);
		if(affectedRows > 0){
			// logout the other users
			ContentValues updateValues = new ContentValues(1);
			updateValues.put(USER_IS_ACTIVE, 0);
			int updatedRows = db.update(TABLE_USERS, updateValues, USER_AUTH_NAME +"<>'" + userName + "' AND " + USER_PASSWORD + "<>'" + password +"'", null);
			Console.debug(TAG, "[loginUser] updatedRows: " + updatedRows);
			String[] projection = new String[]{
												USER_ID,					// 0
												USER_AUTH_NAME,				// 1
												USER_PASSWORD,				// 2
												USER_FNAME,					// 3
												USER_SNAME,					// 4
												USER_SESSIONS_COUNT,		// 5
												USER_SESSIONS_TOTAL_MONEY,	// 6
												USER_SESSIONS_TOTAL_TIME	// 7
											   };
			Cursor c = db.query(TABLE_USERS, projection, USER_AUTH_NAME +"='" + userName + "' AND " + USER_PASSWORD + "='" + password +"'", null, null, null, null);
			
			if(c == null){
				closeDatabase();
				return b;
			}
			
			if(c.getCount() == 0){
				c.close();
				closeDatabase();
				return b;
			}
			
			c.moveToFirst();
			b.putInt(User.KEY_ID, c.getInt(0));
			b.putString(User.KEY_AUTH_NAME, c.getString(1));
			b.putString(User.KEY_PASSWORD, c.getString(2));
			b.putString(User.KEY_FNAME, c.getString(3));
			b.putString(User.KEY_SNAME, c.getString(4));
			b.putInt(User.KEY_SESSIONS_COUNT, c.getInt(5));
			b.putDouble(User.KEY_TOTAL_MONEY, c.getDouble(6));
			b.putLong(User.KEY_TOTAL_TIME, c.getLong(7));
			
			c.close();			
		}		
		closeDatabase();
		Console.debug(TAG, "bundle: " + b.toString());
		return b;
	}

	public synchronized Session createSession(int pUserId, String pSessionName, String pSessionLocation) {
		Session 		newSession 	= null;
		ContentValues	values		= new ContentValues(2);
		
		values.put(SESSION_NAME, pSessionName);
		values.put(SESSION_LOCATION, pSessionLocation);
		values.put(SESSION_START_TIME, Utils.now());
		values.put(SESSION_USER_ID, pUserId);
		
		openDatabase();	
		try{
			int sessionId = (int) db.insertOrThrow(TABLE_SESSIONS, null, values);
			newSession = new Session();
			newSession.setId(sessionId);
			newSession.setLocation(pSessionLocation);
			newSession.setName(pSessionName);						
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			closeDatabase();
		}
		
		return newSession;
	}

	public synchronized Product addProductToSession(int pSessionId, Product pProduct) {
		
		if(pSessionId == -1 || pProduct == null){			
			Console.debug(TAG, "invalid product or sessionId: sessionId = " + pSessionId + " product = " + pProduct);
			return null;
		}
		
		ContentValues values = new ContentValues();
		values.put(PRODUCT_ADDED_TIME, pProduct.getId());
		values.put(PRODUCT_NAME, pProduct.getName());
		values.put(PRODUCT_PRICE, pProduct.getPrice());
		values.put(PRODUCT_QTY, pProduct.getQuantity());
		values.put(PRODUCT_SESSION_ID, pSessionId);
		values.put(PRODUCT_UNITS, pProduct.getUnit());
		
		openDatabase();
		int productId = (int) db.insert(TABLE_PRODUCTS, null, values);
		closeDatabase();
		if(productId != -1){
			pProduct.setId(productId);
			pProduct.setSessionId(pSessionId);
			return pProduct;
		}
		else		
			return null;
	}

	public synchronized boolean removeProduct(Session pSession, Product pProductToRemove) {
		// checking...
		if(pSession == null || pProductToRemove == null){
			Console.debug(TAG, "something is null in removeProduct: pSession: " + pSession + " pProduct: " + pProductToRemove);
			return false;
		}		
		int affectedRows = 0;
		
		openDatabase();
		affectedRows = db.delete(TABLE_PRODUCTS, PRODUCT_SESSION_ID + "=" + pSession.getId() + " AND " + PRODUCT_ID + "=" + pProductToRemove.getId(), null);
		closeDatabase();
		
		return affectedRows > 0;		
	}

	public synchronized ArrayList<Session> getPausedSessions(int pUserId) {
		ArrayList<Session> 	pausedSessions 	= new ArrayList<Session>();
		String[] 			projection	 	= new String[]{
															SESSION_ID,				// 0
															SESSION_NAME,			// 1
															SESSION_LOCATION,		// 2
															SESSION_START_TIME,		// 3
															SESSION_TOTAL_MONEY,	// 4
															SESSION_END_TIME,		// 5
															SESSION_TOTAL_PRODUCTS, // 6
															SESSION_TOTAL_TIME		// 7		
														  };
		
		Cursor c = null;
		openDatabase(); 
		c = db.query(TABLE_SESSIONS, projection, SESSION_USER_ID + "=" + pUserId  + " AND " + SESSION_IS_PAUSED + "=1", null, null, null, null);
		
		if(c == null){
			closeDatabase();
			return pausedSessions;
		}
		
		if(c.getCount() == 0){
			Console.debug(TAG, "the user " + pUserId + " do not have any paused sessions");
			c.close();
			closeDatabase();
			return pausedSessions;
		}		
		return null;	
	}

	public synchronized ArrayList<Product> getAllProducts() {
		
		ArrayList<Product> products = new ArrayList<Product>();
		Cursor c 					= null;
		String[]projection 			= new String[]{
												    PRODUCT_ADDED_TIME, // 0
													PRODUCT_NAME,		// 1
													PRODUCT_PRICE,		// 2
													PRODUCT_QTY,		// 3
													PRODUCT_UNITS,		// 4
													PRODUCT_ID,			// 5
													PRODUCT_SESSION_ID  // 6
												 };		
		
		openDatabase();
		c = db.query(TABLE_PRODUCTS, projection, null, null, PRODUCT_NAME, null, null);
		
		if(c == null){
			closeDatabase();
			return products;
		}
		
		int numRows = c.getCount();
		
		if(numRows == 0){
			c.close();
			closeDatabase();
			return products;
		}
		
		c.moveToFirst();
		for(int i = 0; i < numRows; i++){
			Product p = new Product();
			p.setAddedTime(c.getLong(0));
			p.setName(c.getString(1));
			p.setPrice(c.getDouble(2));
			p.setQuantity(c.getDouble(3));
			p.setId(c.getInt(5));
			p.setSessionId(c.getInt(6));			
			try {
				p.setUnit(c.getString(4));
			} catch (InvalidUnitException e) {
				e.printStackTrace();
			}			
			
			products.add(p);			
			c.moveToNext();
		}
		
		c.close();
		closeDatabase();
		
		return products;
	}
}

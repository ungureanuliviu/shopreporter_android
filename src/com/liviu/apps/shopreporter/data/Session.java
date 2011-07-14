package com.liviu.apps.shopreporter.data;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.liviu.apps.shopreporter.interfaces.BundleAnnotation;
import com.liviu.apps.shopreporter.utils.Convertor;
import com.liviu.apps.shopreporter.utils.Console;
import com.liviu.apps.shopreporter.utils.Utils;

public class Session {

	// Constants
	private 	   final String TAG						= "Session";
	public 	static final String KEY_ID 					= "id";
	public 	static final String KEY_USER_ID 			= "user_id";
	public  static final String KEY_NAME				= "name";
	public  static final String KEY_LOCATION 			= "location";
	public  static final String KEY_TOTAL_TIME 			= "total_time";
	public  static final String KEY_TOTAL_MONEY 		= "total_money";
	public  static final String KEY_TOTAL_PRODUCTS 		= "total_products";
	public  static final String KEY_START_TIME			= "start_time";
	public  static final String KEY_END_TIME			= "end_time";
	public  static final String KEY_IS_PAUSED 			= "is_paused";
	public  static final String KEY_BUNDLE 				= "key_bundle";	
	public  static final String KEY_IS_CLOSED			= "key_is_closed";
	public  static final String KEY_LAST_STARTED_TIME 	= "key_last_started_time";
	
	@BundleAnnotation(bundleKey = KEY_ID)
	private int 	mId;
	
	@BundleAnnotation(bundleKey = KEY_USER_ID)
	private int 	mUserId;
	
	@BundleAnnotation(bundleKey = KEY_NAME)
	private String 	mName;
	
	@BundleAnnotation(bundleKey = KEY_LOCATION)
	private String 	mLocation;
	
	@BundleAnnotation(bundleKey = KEY_TOTAL_TIME)
	private long 	mTotalTime;
	
	@BundleAnnotation(bundleKey = KEY_TOTAL_MONEY)
	private double 	mTotalMoney;
	
	@BundleAnnotation(bundleKey = KEY_START_TIME)
	private long 	mStartTime;
	
	@BundleAnnotation(bundleKey = KEY_END_TIME)
	private long	mEndTime;
	
	@BundleAnnotation(bundleKey = KEY_IS_PAUSED)
	private boolean mIsPaused;
	
	@BundleAnnotation(bundleKey = KEY_TOTAL_PRODUCTS)
	private int		mTotalProducts;
	
	@BundleAnnotation(bundleKey = KEY_IS_CLOSED)
	private boolean	mIsClosed;
	
	@BundleAnnotation(bundleKey = KEY_LAST_STARTED_TIME)
	private long 	mLastStartedTime;
	
	private Object	mTag;
	
	private ArrayList<Product> mProducts;
	
	public Session() {
		mId 			= -1;
		mUserId 		= -1;
		mName 			= "";
		mLocation 		= "";
		mTotalTime 		= 0;
		mTotalMoney 	= 0.00;
		mTotalProducts 	= 0;
		mLastStartedTime= Utils.now();
		mStartTime 		= Utils.now();
		mEndTime 		= 0;
		mIsPaused 		= false;		
		mIsClosed		= false;
		mProducts 		= new ArrayList<Product>(){
							@SuppressWarnings("unused")
							public boolean contains(Product p) {				
								for(int i = 0; i < size(); i++)
									if(p.getId() == get(i).getId())
										return true;
								
								return false;
							};
						};
	}
	
	public Session(Bundle b) {
		Console.debug(TAG, "constructor bundle: " + b.toString());
		if(b != null){
			mId 			= b.getInt(KEY_ID);
			mUserId 		= b.getInt(KEY_USER_ID);
			mName 			= b.getString(KEY_NAME);
			mLocation 		= b.getString(KEY_LOCATION);
			mTotalTime 		= b.getLong(KEY_TOTAL_TIME);
			mTotalMoney 	= b.getDouble(KEY_TOTAL_MONEY);
			mTotalProducts 	= b.getInt(KEY_TOTAL_PRODUCTS);
			mStartTime 		= b.getLong(KEY_START_TIME);
			mEndTime 		= b.getLong(KEY_END_TIME);
			mIsPaused 		= b.getBoolean(KEY_IS_PAUSED);	
			mIsClosed		= b.getBoolean(KEY_IS_CLOSED);
			mLastStartedTime= b.getLong(KEY_LAST_STARTED_TIME);
			mProducts 		= new ArrayList<Product>(){
								@SuppressWarnings("unused")
								public boolean contains(Product p) {				
									for(int i = 0; i < size(); i++)
										if(p.getId() == get(i).getId())
											return true;
									
									return false;
								};
							};
		}
		else
			Console.debug(TAG, "bundle is null in constructor");
	}	
	
	public Session(JSONObject pJson) {		
		if(pJson != null){
			Console.debug(TAG, "constructor JSON: " + pJson.toString());
			try {
				mId 			= pJson.getInt("mId");		
				mUserId 		= pJson.getInt("mUserId");
				mName 			= pJson.getString("mName");
				mLocation 		= pJson.getString("mLocation");
				mTotalTime 		= pJson.getLong("mTotalTime");
				mTotalMoney 	= pJson.getDouble("mTotalMoney");
				mTotalProducts 	= pJson.getInt("mTotalProducts");
				mStartTime 		= pJson.getLong("mStartTime");
				mEndTime 		= pJson.getLong("mEndTime");
				mIsPaused 		= pJson.getBoolean("mIsPaused");	
				mIsClosed		= pJson.getBoolean("mIsClosed");
				mLastStartedTime= pJson.getLong("mLastStartedTime");
				mProducts 		= new ArrayList<Product>(){
									@SuppressWarnings("unused")
									public boolean contains(Product p) {				
										for(int i = 0; i < size(); i++)
											if(p.getId() == get(i).getId())
												return true;
										
										return false;
									};
								};
			}
			catch (JSONException e) {
				e.printStackTrace();				
			}							
		}
		else
			Console.debug(TAG, "bundle is null in constructor");		
	}

	public Session setId(int pId){
		mId = pId;
		return this;
	}
	
	public int getId(){
		return mId;
	}
	
	public Session setLastStartedTime(long pLastStartedTime){
		mLastStartedTime = pLastStartedTime;
		return this;
	}
	
	public long getLastStartedTime(){
		return mLastStartedTime;
	}
	
	public Session setUserId(int pUserId){
		mUserId = pUserId;
		return this;
	}
	
	public int getUserId(){
		return mUserId;
	}
	
	public Session setName(String pName){
		mName = pName;
		return this;
	}
	
	public String getName(){
		return mName;
	}
	
	public Session setLocation(String pLocation){
		mLocation = pLocation;
		return this;
	}
	
	public String getLocation(){
		return mLocation;
	}
	
	public Session setTotalTime(long pTotalTime){
		mTotalTime = pTotalTime;
		return this;
	}
	
	public long getTotalTime(){
		return mTotalTime;
	}
	
	public Session setTotalMoney(double pTotalMoney){
		mTotalMoney = pTotalMoney;
		return this;
	}
	
	public double getTotalMoney(){
		return mTotalMoney;
	}
	
	public Session setTotalProducts(int pTotalProducts){
		mTotalProducts = pTotalProducts;
		return this;
	}
	
	public int getTotalProducts(boolean pFromLocalCounter){
		if(pFromLocalCounter)
			return mTotalProducts;
		else
			return mProducts.size();
	}
	
	public Session setIsPausedValue(boolean pIsPaused){
		mIsPaused = pIsPaused;
		return this;
	}
	
	public boolean isPaused() {
		return mIsPaused;
	}
	
	public Session add(Product pProduct){
		if(!mProducts.contains(pProduct)){
			mProducts.add(pProduct);
			mTotalMoney += pProduct.getPrice() * pProduct.getQuantity();
		}
		
		return this;
	}
	
	public long getStartTime(){
		return mStartTime;
	}
	
	public Session setStartTime(long pStartTime){
		mStartTime = pStartTime;
		return this;
	}
	
	public long getEndTime(){
		return mEndTime;
	}
	
	public Session setEndTime(long pEndTime){
		mEndTime = pEndTime;
		return this;
	}
		
	public Product remove(Product pProduct){
		Product removedProduct = null;
		for(int i = 0; i < mProducts.size(); i++){
			if(pProduct.getId() == mProducts.get(i).getId()){
				removedProduct = mProducts.remove(i);
				mTotalMoney -= removedProduct.getPrice() * removedProduct.getQuantity();
			}
		}
		
		return removedProduct;
	}
	
	public ArrayList<Product> getProducts(){
		return mProducts;
	}
		
	
	public Bundle toBundle(){
		return Convertor.toBundle(this);
	}
	
	@Override
	public String toString() {
		return Convertor.toString(this);
	}

	public void setProducts(ArrayList<Product> pProducts) {
		mProducts 	= pProducts;
		mTotalMoney	= 0.00;
		
		for(int i = 0; i < pProducts.size(); i++)
			mTotalMoney += pProducts.get(i).getPrice() * pProducts.get(i).getQuantity();
		
		Console.debug(TAG, "setProducts: total money = " + mTotalMoney);
	}

	public String toMinimalString() {
		String content = "Name: " + getName() + "\n"			+
						 "Location: " + getLocation() + "\n"	+
						 "Started on: " + Utils.formatDate(getStartTime(), "E, dd MMM HH:mm") + " \n" +
						 "Products count: " + mTotalProducts + " \n" + 
						 "Total price:" + Utils.roundTwoDecimals(getTotalMoney());
					
		return content;
	}

	public Session setIsClosed(boolean pIsClosed) {
		mIsClosed = pIsClosed;
		return this;		
	}
	
	public boolean isClosed(){
		return mIsClosed;
	}

	public JSONObject toJson() {
		return Convertor.toJson(this);
	}
	
	public Session setTag(Object pTag){
		mTag = pTag;
		return this;
	}
	
	public Object getTag(){
		return mTag;
	}
	
	
}

package com.liviu.apps.shopreporter.data;

import java.util.ArrayList;

import android.os.Bundle;

import com.liviu.apps.shopreporter.interfaces.BundleAnnotation;
import com.liviu.apps.shopreporter.utils.Convertor;
import com.liviu.apps.shopreporter.utils.Console;

public class Session {

	// Constants
	private 	   final String TAG					= "Session";
	public 	static final String KEY_ID 				= "id";
	public 	static final String KEY_USER_ID 		= "user_id";
	public  static final String KEY_NAME			= "name";
	public  static final String KEY_LOCATION 		= "location";
	public  static final String KEY_TOTAL_TIME 		= "total_time";
	public  static final String KEY_TOTAL_MONEY 	= "total_money";
	public  static final String KEY_TOTAL_PRODUCTS 	= "total_products";
	public  static final String KEY_START_TIME		= "start_time";
	public  static final String KEY_END_TIME		= "end_time";
	public  static final String KEY_IS_PAUSED 		= "is_paused";
	public  static final String KEY_BUNDLE 			= "key_bundle";	
	
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
	private ArrayList<Product> mProducts;
	
	public Session() {
		mId 			= -1;
		mUserId 		= -1;
		mName 			= "";
		mLocation 		= "";
		mTotalTime 		= 0;
		mTotalMoney 	= 0.00;
		mTotalProducts 	= 0;
		mStartTime 		= 0;
		mEndTime 		= 0;
		mIsPaused 		= false;		
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
		Console.debug(TAG, "bundle: " + b.toString());
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
			Console.debug(TAG, "b is null in constructor");
	}	
	
	public Session setId(int pId){
		mId = pId;
		return this;
	}
	
	public int getId(){
		return mId;
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
	
	public int getTotalProducts(){
		return mTotalProducts;
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
			mTotalProducts++;
		}
		
		return this;
	}
	
	public Product remove(Product pProduct){
		Product removedProduct = null;
		for(int i = 0; i < mProducts.size(); i++){
			if(pProduct.getId() == mProducts.get(i).getId()){
				removedProduct = mProducts.remove(i);
				mTotalProducts--;
			}
		}
		
		return removedProduct;
	}
	
	public ArrayList<Product> getProducts(){
		return mProducts;
	}
		
	public Bundle toBunble(){
		return Convertor.toBundle(this);
	}
}

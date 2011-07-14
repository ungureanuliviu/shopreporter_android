package com.liviu.apps.shopreporter.data;

import android.os.Bundle;

import com.liviu.apps.shopreporter.exceptions.InvalidUnitException;
import com.liviu.apps.shopreporter.interfaces.BundleAnnotation;
import com.liviu.apps.shopreporter.utils.Convertor;
import com.liviu.apps.shopreporter.utils.Console;
import com.liviu.apps.shopreporter.utils.Utils;

public class Product {

	// Constants
	private final String TAG					= "Product";
	
	// Keys
	public static final String KEY_ID 			= "id";
	public static final String KEY_SESSION_ID 	= "seesion_id";
	public static final String KEY_NAME			= "name";
	public static final String KEY_QUANTITY	 	= "qty";
	public static final String KEY_PRICE		= "price";
	public static final String KEY_ADDED_TIME	= "added_time";
	public static final String KEY_UNITS		= "units";
	
	// Units
	public static final String UNIT_LITRE		= "liter";
	public static final String UNIT_KG			= "kg";
	public static final String UNIT_BOTTLE		= "bottle";
	public static final String UNIT_PIECE		= "piece";	
	private String[] 		   supportedUnits	= new String[]{ UNIT_BOTTLE, UNIT_KG, UNIT_LITRE, UNIT_PIECE };
	
	// Data
	@BundleAnnotation(bundleKey = KEY_ID)
	private int 		mId;
	
	@BundleAnnotation(bundleKey = KEY_SESSION_ID)
	private int 		mSessionId;
	
	@BundleAnnotation(bundleKey = KEY_NAME)
	private String 		mName;
	
	@BundleAnnotation(bundleKey = KEY_QUANTITY)
	private double		mQty;
	
	@BundleAnnotation(bundleKey = KEY_PRICE)
	private double 		mPrice;
	
	@BundleAnnotation(bundleKey = KEY_ADDED_TIME)
	private long		mAddedTime;		
	
	@BundleAnnotation(bundleKey = KEY_UNITS)
	private String 		mUnits;
	
	
	
	public Product() {
		 mId 			= -1;
		 mSessionId 	= -1;
		 mAddedTime 	= Utils.now();
		 mName 			= "No name " + mAddedTime;
		 mPrice 		= 0.00;
		 mQty 			= 0;		
		 mUnits			= UNIT_KG;
	}
	
	public Product(Bundle bProduct) {
		 if(bProduct != null){
			 mId 			= bProduct.getInt(KEY_ID);
			 mSessionId 	= bProduct.getInt(KEY_SESSION_ID);
			 mAddedTime 	= bProduct.getLong(KEY_ADDED_TIME);
			 mName 			= bProduct.getString(KEY_NAME);
			 mPrice 		= bProduct.getDouble(KEY_PRICE);
			 mQty 			= bProduct.getDouble(KEY_QUANTITY);
			 mUnits			= bProduct.getString(KEY_UNITS);
		 }
		 else
			 Console.debug(TAG, "Bundle is null");
	}	

	public Product setId(int pId){
		mId = pId;
		return this;
	}
	
	public int getId(){
		return mId;
	}
	
	public Product setSessionId(int pSessionId){
		mSessionId = pSessionId;
		return this;
	}
	
	public int getSessionId(){
		return mSessionId;
	}
	
	public Product setAddedTime(long pAddedTime){
		mAddedTime = pAddedTime;
		return this;
	}
	
	public long getAddedTime(){
		return mAddedTime;
	}
	
	public  Product setName(String pName){
		mName = pName;
		return this;
	}
	
	public String getName(){
		return mName;
	}
	
	public Product setPrice(double pPrice){
		mPrice = pPrice;
		return this;
	}
	
	public double getPrice(){
		return mPrice;
	}
	
	public Product setQuantity(double pQuantity){
		mQty = pQuantity;
		return this;
	}
	
	public double getQuantity(){
		return mQty;
	}
	
	public Product setUnit(String pUnit) throws InvalidUnitException{
		for(int i = 0; i < supportedUnits.length; i++){
			if(supportedUnits[i].toString().equals(pUnit)){
				mUnits = pUnit.toString();
				return this;
			}
		}
		
		throw new InvalidUnitException("The unit '" +  pUnit + "' is invalid");
	}
	
	public String getUnit(){
		return mUnits;
	}
	
	public Bundle toBundle(){
		return Convertor.toBundle(this);
	}
	
	@Override
	public String toString() {
		return Convertor.toString(this);
	}
}

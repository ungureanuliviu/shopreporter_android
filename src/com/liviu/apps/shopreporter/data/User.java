package com.liviu.apps.shopreporter.data;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.liviu.apps.shopreporter.interfaces.BundleAnnotation;
import com.liviu.apps.shopreporter.interfaces.OnLoginListener;
import com.liviu.apps.shopreporter.managers.DatabaseManger;
import com.liviu.apps.shopreporter.managers.ShoppingSessionManager;
import com.liviu.apps.shopreporter.utils.Convertor;
import com.liviu.apps.shopreporter.utils.Console;

public class User {
	
	// Constants
	@SuppressWarnings("unused")
	private 	  final String TAG 					= "User";
	public static final String KEY_FNAME			= "fname";
	public static final String KEY_SNAME			= "sname";
	public static final String KEY_SESSIONS_COUNT 	= "sessions_count";
	public static final String KEY_TOTAL_MONEY		= "total_money";
	public static final String KEY_TOTAL_TIME		= "total_time";
	public static final String KEY_ID				= "id";
	public static final String KEY_AUTH_NAME		= "authname";
	public static final String KEY_PASSWORD			= "password";
	public static final String KEY_IS_LOGGED_IN		= "is_logged_in";
	
	// Messages
	private final int MSG_LOGIN_SUCCESS				= 0;
	private final int MSG_LOGIN_ERROR				= 1;
	
	
	// Data
	private static 	User 					soul;
	private 		Context					mContext;
	private 		ShoppingSessionManager 	mShopMan;
	
	@BundleAnnotation(bundleKey = KEY_FNAME)
	private 		String					mFName;
	
	@BundleAnnotation(bundleKey = KEY_SNAME)
	private 		String					mSName;
	
	@BundleAnnotation(bundleKey = KEY_SESSIONS_COUNT)
	private 		int						mSessionsCount;
	
	@BundleAnnotation(bundleKey = KEY_TOTAL_MONEY)
	private 		double					mTotalMoney;
	
	@BundleAnnotation(bundleKey = KEY_TOTAL_TIME)
	private 		long					mTotalTime;
	
	@BundleAnnotation(bundleKey = KEY_ID)
	private 		int						mId;
	
	@BundleAnnotation(bundleKey = KEY_AUTH_NAME)
	private 		String 					mAuthName;
	
	@BundleAnnotation(bundleKey = KEY_PASSWORD)
	private 		String					mPassword;
	
	@BundleAnnotation(bundleKey = KEY_IS_LOGGED_IN)
	private 		boolean 				mIsLoggedIn;
	
	private DatabaseManger					dbMan;
	private Handler							handler;
	
	// Listeners
	private OnLoginListener					mOnLoginListener;
	
	private User(Context ctx, int pId) {
		mContext 		= ctx;
		mShopMan 		= new ShoppingSessionManager(mContext);		
		mFName			= "";
		mSName			= "";
		mSessionsCount 	= 0;
		mTotalMoney		= 0.00;
		mTotalTime		= 0;
		mId				= pId;
		mAuthName		= "";
		mPassword		= "";
		dbMan			= DatabaseManger.getInstance(ctx);
		mIsLoggedIn		= false;
		handler			= new Handler(){
			public void handleMessage(Message msg) {
				Console.debug(TAG, "[handler]: msg: " + msg.what + " " + msg.obj);
				
				switch (msg.what) {
				case MSG_LOGIN_SUCCESS:
					if(mOnLoginListener != null)
						mOnLoginListener.onLogin(true);
					break;
				case MSG_LOGIN_ERROR:
					if(mOnLoginListener != null)
						mOnLoginListener.onLogin(false);
					break;
				default:
					break;
				}
			};
		};
		mShopMan.setUserId(pId);
	}
	
	public static User getInstance(Context ctx){
		if(soul == null)
			soul = new User(ctx, -1);		
		return soul;
	}
	
	public ShoppingSessionManager getShoppingManager(){
		return mShopMan;
	}
		
	public static User create(Context ctx, String userName, String userPassword, String userFName, String userSName){
		DatabaseManger dbManager = DatabaseManger.getInstance(ctx);
		int userId 	= -1;
		userId 		= dbManager.createUser(userName, userPassword, userFName, userSName);
		
		if(userId == -1){			
			// this user already exists. 
			return null;
		}
		else{
			// success: We have a new user. Good job! :)
			soul = new User(ctx, userId)
				.setFName(userFName)
				.setSName(userSName)
				.setPassword(userPassword)
				.setUsername(userName);	
			return soul;
		}
	}
	
	public User	setFName(String pFName){
		mFName = pFName;
		return this;
	}
	
	public User setSName(String pSName){
		mSName = pSName;
		return this;
	}
	
	public User setSessionsCount(int pCount){
		mSessionsCount = pCount;
		return this;
	}
	
	public User setTotalMoney(double pMoney){
		mTotalMoney = pMoney;
		return this;
	}
	
	public User setTotalTime(long pTime){
		mTotalTime = pTime;
		return this;
	}
	
	public int getSessionsCount(){
		return mSessionsCount;
	}
	
	public double getTotalMoney(){
		return mTotalMoney;
	}
	
	public long getTotalTime(){
		return mTotalTime;
	}
	
	public String getFName(){
		return mFName;
	}
	
	public String getSName(){
		return mSName;
	}
	
	public int getId(){
		return mId;
	}
	
	public User setId(int pId){
		mId = pId;
		return this;
	}
	
	public User setUsername(String pUsername){
		mAuthName = pUsername;
		return this;
	}
	
	public User setPassword(String pPassword){
		mPassword = pPassword;
		return this;
	}
	
	public String getUsername(){
		return mAuthName;
	}
	
	public User setOnLoginListener(OnLoginListener pListener){
		mOnLoginListener = pListener;
		return this;
	}
	
	public User setLoginStatus(boolean pValue){
		mIsLoggedIn = pValue;
		return this;
	}

	public void login(String pUserName, String pUserPassword) {
		final String cUserName 		= pUserName;
		final String cUserPassword 	= pUserPassword;
		Thread tLogin = new Thread(new Runnable() {			
			@Override
			public void run() {
				Message msg = new Message();
				
				Bundle userDetails = dbMan.loginUser(cUserName, cUserPassword);
				if(!userDetails.isEmpty()){
					setDetails(userDetails);
					msg.what = MSG_LOGIN_SUCCESS;					
				}
				else{
					msg.what = MSG_LOGIN_ERROR;
				}
				handler.sendMessage(msg);
			}
		});
		
		tLogin.start();
	}
	
	public Bundle toBundle(){
		return Convertor.toBundle(this);
	}

	public boolean isLoggedIn() {
		return mIsLoggedIn;
	}
	
	private void setDetails(Bundle bundleWithDetails){
		if(bundleWithDetails != null){
			soul.setId(bundleWithDetails.getInt(KEY_ID))
				.setUsername(bundleWithDetails.getString(KEY_AUTH_NAME))
				.setPassword(bundleWithDetails.getString(KEY_PASSWORD))
				.setFName(bundleWithDetails.getString(KEY_FNAME))
				.setSName(bundleWithDetails.getString(KEY_SNAME))
				.setSessionsCount(bundleWithDetails.getInt(KEY_SESSIONS_COUNT))
				.setTotalMoney(bundleWithDetails.getDouble(KEY_TOTAL_MONEY))
				.setTotalTime(bundleWithDetails.getLong(KEY_TOTAL_TIME))
				.setLoginStatus(true);
			soul.getShoppingManager().setUserId(soul.getId());
		}
		else
			soul.setLoginStatus(false);
	}
	
	public Session getPausedSession(){
		return mShopMan.getPauseSession(mId);
	}	
}

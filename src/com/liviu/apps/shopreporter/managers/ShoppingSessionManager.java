package com.liviu.apps.shopreporter.managers;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.test.IsolatedContext;

import com.liviu.apps.shopreporter.data.Product;
import com.liviu.apps.shopreporter.data.Session;
import com.liviu.apps.shopreporter.interfaces.AutoCompleteLoadListener;
import com.liviu.apps.shopreporter.interfaces.LParam;
import com.liviu.apps.shopreporter.interfaces.OnGeoCoderDataReceived;
import com.liviu.apps.shopreporter.interfaces.SessionListener;
import com.liviu.apps.shopreporter.utils.Console;
import com.liviu.apps.shopreporter.utils.Convertor;
import com.liviu.apps.shopreporter.utils.Utils;

public class ShoppingSessionManager {

	
	// Constants
	private final String 	TAG 								= "ShoppingSessionManager";
	private final int	 	MSG_GEOCODER_OK						= 1;
	private final int	 	MSG_GEOCODER_ERROR					= 2;
	private final int    	MSG_SESSION_CREATED					= 3;
	private final int 	 	MSG_ADD_PRODUCT_TO_SESSION_ERROR	= 4;
	private final int		MSG_ADD_PRODCT_TO_SESSION_SUCCESS	= 5;
	private final int		MSG_SESSION_PRODUCT_REMOVED			= 6;
	private final int 		MSG_ALL_PRODUCTS_LOADED				= 7;
	private final int 		MSG_SESSION_LOAD_ERROR				= 8;
	private final int 		MSG_SESSION_LOAD_SUCCESS			= 9;
	private final int		MSG_ALL_SESSIONS_LOADED				= 10;
	private final int 		MSG_NO_COMMON_PRODUCTS 				= 11;
	private final int       MSG_COMMON_PRODUCTS_LOADED			= 12;
		
	// Data
	private Context 		context;
	private Geocoder		geoCoder;
	private Handler			mHandler;
	private DatabaseManger	dbMan;
	private int				mUserId;
	
	
	// Listeners
	private OnGeoCoderDataReceived 		onGeocoderDataReceived;
	private SessionListener				mSessionListener;
	private AutoCompleteLoadListener	mAutoCompleteLoadeListener;
	
	public ShoppingSessionManager(Context ctx) {
		context 		= ctx;
		geoCoder		= new Geocoder(context);
		dbMan			= DatabaseManger.getInstance(context);
		mUserId			= -1;
		
		mHandler			= new Handler(){
			@SuppressWarnings("unchecked")
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_GEOCODER_OK:
					if(onGeocoderDataReceived != null){
						if(msg.obj != null){
							if(msg.obj instanceof List<?>){
								try{
									List<Address> addresses = (List<Address>)msg.obj;									
									onGeocoderDataReceived.onGeoCoderDataReceived(true, addresses);
								}
								catch (ClassCastException e) {
									e.printStackTrace();
									onGeocoderDataReceived.onGeoCoderDataReceived(false, null);
								}
							}
							else{
								onGeocoderDataReceived.onGeoCoderDataReceived(false, null);
							}								
						}
						else{
							onGeocoderDataReceived.onGeoCoderDataReceived(false, null);
						}
					}
					else{
						Console.debug(TAG, "no geocoderDatareceiver listener setted");
					}
					break;
				case MSG_GEOCODER_ERROR:
					if(onGeocoderDataReceived != null)
						onGeocoderDataReceived.onGeoCoderDataReceived(false, null);
					break;
				case MSG_SESSION_CREATED:
					if(mSessionListener != null){
						if(msg.obj != null)
							mSessionListener.onSessionCreated(true, (Session)msg.obj);						
						else
							mSessionListener.onSessionCreated(false, null);
							
					}
					break;
				case MSG_ADD_PRODUCT_TO_SESSION_ERROR:
					if(mSessionListener != null){
						mSessionListener.onProductAddedToSession(false, null, null);
					}
					break;
				case MSG_ADD_PRODCT_TO_SESSION_SUCCESS:
					if(mSessionListener != null){
						Object[] objs = (Object[])msg.obj;
						Session session = (Session)objs[0];
						Product addedProduct = (Product)objs[1];
						mSessionListener.onProductAddedToSession(true, session, addedProduct);						
					}
					break;
				case MSG_SESSION_PRODUCT_REMOVED:
					if(mSessionListener != null){
						Object[] objs = (Object[])msg.obj;
						boolean isRemoved 	= (Boolean)objs[0];
						Session	session		= (Session)objs[1];
						Product	product 	= (Product)objs[2];
												
						mSessionListener.onProductRemovedFromSession(isRemoved, session, product);											
					}
					break;
				case MSG_ALL_PRODUCTS_LOADED:
					if(mAutoCompleteLoadeListener != null){
						mAutoCompleteLoadeListener.onAutoCompleteDataLoaded(true, (ArrayList<Product>)msg.obj);
					}
					break;
				case MSG_SESSION_LOAD_ERROR:
					if(mSessionListener != null){
						mSessionListener.onSessionLoaded(false, null);
					}
					break;
				case MSG_SESSION_LOAD_SUCCESS:
					if(mSessionListener != null){						
						mSessionListener.onSessionLoaded(true, (Session)msg.obj);
					}
					break;
				case MSG_ALL_SESSIONS_LOADED:
					if(mSessionListener != null){
						try{
							ArrayList<Session> loadedSessions = (ArrayList<Session>)msg.obj;
							mSessionListener.onUserSessionsLoaded(true, loadedSessions);
						}
						catch (ClassCastException e) {
							e.printStackTrace();
							mSessionListener.onUserSessionsLoaded(false, null);
						}						
					}
					break;
				case MSG_NO_COMMON_PRODUCTS:
					if(mSessionListener != null){
						mSessionListener.onCommonProductsLoaded(false, null);
					}
					break;
				case MSG_COMMON_PRODUCTS_LOADED:
					if(mSessionListener != null){
						try{
							ArrayList<Product> commonProducts = (ArrayList<Product>)msg.obj;
							if(commonProducts != null)
								mSessionListener.onCommonProductsLoaded(true, commonProducts);							
							else
								mSessionListener.onCommonProductsLoaded(false, null);
						}
						catch(ClassCastException e){
							e.printStackTrace();
							mSessionListener.onCommonProductsLoaded(false, null);
						}
					}					
					break;
				default:
					break;
				}
			};
		};
	}
	
	public void getLocationAddress(Location location){
		final Location location_ = location;
		Thread tGetAddress = new Thread(new Runnable() {			
			@Override
			public void run() {
				Message msg = new Message();				
				try {					
					List<Address> addresses = geoCoder.getFromLocation(location_.getLatitude(), location_.getLongitude(), 20);
					Console.debug(TAG, "Geocoder addresses size: " + addresses.size());
					msg.what = MSG_GEOCODER_OK;
					msg.obj  = addresses;
					
					mHandler.sendMessage(msg);
				}
				catch (IOException e) {
					e.printStackTrace();
					Console.debug(TAG, "[Geocoder]No internet connection");					
					msg.what = MSG_GEOCODER_ERROR;
					mHandler.sendMessage(msg);
				}
			}
		});
		
		tGetAddress.start();
	}
	
	public ShoppingSessionManager setOnGeoCoderDataListener(OnGeoCoderDataReceived listener){
		onGeocoderDataReceived = listener;
		return this;
	}
	
	public ShoppingSessionManager setSessionListener(SessionListener pListener){
		mSessionListener = pListener;
		return this;
	}
	
	public ShoppingSessionManager setAutoCompleteLoadListener(AutoCompleteLoadListener listener){
		mAutoCompleteLoadeListener = listener;
		return this;
	}

	public void createSession(String pSessionName, String pSessionLocation) {
		// make them constants...for life ;)
		final String cSessionName 		= pSessionName;
		final String cSessionLocation 	= pSessionLocation;
		
		Thread tCreateSession = new Thread(new Runnable() {			
			@Override
			public void run() {
				Message msg = new Message();				
				Console.debug(TAG, "USER ID: " + mUserId);
				Session newSession = dbMan.createSession(mUserId, cSessionName, cSessionLocation);
				
				msg.obj 	= newSession;
				msg.what 	= MSG_SESSION_CREATED;
				mHandler.sendMessage(msg);
			}
		});
		
		if(mUserId == -1){
			Console.debug(TAG, "[createSession] userId is -1. I can let you create a session for a unknown person. Is not fair!!!");
		}
		else
			tCreateSession.start();		
	}

	public void setUserId(int pUserId) {
		mUserId = pUserId;
	}

	public void addProductToSession(Session pSession, Product pProduct) {
		
		if(pSession == null){
			mHandler.sendEmptyMessage(MSG_ADD_PRODUCT_TO_SESSION_ERROR);
			Console.debug(TAG, "pSession is null");
			return;
		}
		final Session	cSession 	= pSession;
		final Product	cProduct	= pProduct;
		
		Thread tAdd = new Thread(new Runnable() {			
			@Override
			public void run() {
				Product addedProduct = dbMan.addProductToSession(cSession.getId(), cProduct);
				if(addedProduct == null){
					mHandler.sendEmptyMessage(MSG_ADD_PRODUCT_TO_SESSION_ERROR);
				}
				else{
					Message msg = new Message();
					msg.obj 	= new Object[]{cSession, cProduct};
					msg.what 	= MSG_ADD_PRODCT_TO_SESSION_SUCCESS;
					mHandler.sendMessage(msg);
				}
			}
		});
		
		tAdd.start();
	}

	
	public void removeProduct(Session pSession, Product pProductToRemove) {
		final Session cSession 			= pSession;
		final Product cProductToRemove 	= pProductToRemove;
		
		Thread tRemoveProduct = new Thread(new Runnable() {			
			@Override
			public void run() {
				boolean isRemoved 	= dbMan.removeProduct(cSession, cProductToRemove);
				Message msg 		= new Message();
				Object[] objs = new Object[]{isRemoved, cSession, cProductToRemove};					
				msg.obj  = objs;
				msg.what = MSG_SESSION_PRODUCT_REMOVED;
							
				mHandler.sendMessage(msg);
			}
		});		
		tRemoveProduct.start();
	}
	
	public Session getPauseSession(int pUserId){
		return dbMan.getPausedSession(pUserId);
	}
	
	/**
	 * Get all user's products from database
	 * @param isAutocompleteRelated if true  the method will call 
	 * 								call {@link AutoCompleteLoadListener#onAutoCompleteDataLoaded(boolean, ArrayList)} with 
	 * 				
	 */
	public void getAllProducts() {
		Thread tLoad = new Thread(new Runnable() {
			
			@Override
			public void run() {
				ArrayList<Product> loadedProducts = dbMan.getAllProducts();
				Message msg = new Message();
				msg.what = MSG_ALL_PRODUCTS_LOADED;
				msg.obj = loadedProducts;
				mHandler.sendMessage(msg);
			}
		});
		
		tLoad.start();
	}

	public void pauseSession(Session pSession) {
		pSession.setIsPausedValue(true);
		dbMan.pauseSession(pSession, mUserId);
	}

	public void resumeSession(int pSessionId) {
		if(pSessionId == -1){
			Console.debug(TAG, "resumeSession: session is null");
			mHandler.sendEmptyMessage(MSG_SESSION_LOAD_ERROR);
			return;
		}
		
		Console.debug(TAG, "resumeSession: to resume id: " + pSessionId);
		final int cSessionId = pSessionId;		
		Thread tResume = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Session loadedSession = dbMan.resumeSession(cSessionId, mUserId);
				Message msg = new Message();
				
				Console.debug(TAG, "loaded session: " + loadedSession);
				
				if(loadedSession == null)
					msg.what = MSG_SESSION_LOAD_ERROR;									
				else
					msg.what = MSG_SESSION_LOAD_SUCCESS;
				
				msg.obj = loadedSession;
				mHandler.sendMessage(msg);
			}
		});
		
		tResume.start();
	}

	public void finishSession(Session currentSession) {
		dbMan.finishSession(currentSession, mUserId);
	}

	public void getAllSessions(boolean pJustFinished) {
		final boolean cJustFinished = pJustFinished;
		Thread tLoadSessions = new Thread(new Runnable() {			
			@Override
			public void run() {
				ArrayList<Session> sessions = dbMan.getAllSessions(mUserId, cJustFinished);
				Message msg = new Message();
				
				msg.what = MSG_ALL_SESSIONS_LOADED;
				msg.obj	 = sessions;
				
				mHandler.sendMessage(msg);
			}
		});
				
		tLoadSessions.start();
	}

	public void getCommonProducts(Session pSession1, Session pSession2) {
		Console.debug(TAG, "getCOmmoneProducts");
		if(pSession1 == null || pSession2 == null){
			mHandler.sendEmptyMessage(MSG_NO_COMMON_PRODUCTS);			
		}
		else{
			final Session cSession1 = pSession1;
			final Session cSession2 = pSession2;
			
			Thread tCommon = new Thread(new Runnable() {				
				@Override
				public void run() {
					ArrayList<Product> commonProducts = new ArrayList<Product>();
					commonProducts = dbMan.getCommonProducts(cSession1, cSession2);
					Console.debug(TAG, "Common products size: " + commonProducts.size());
					Message msg = new Message();					
					if(commonProducts.size() == 0){
						msg.what = MSG_NO_COMMON_PRODUCTS;
						mHandler.sendMessage(msg);
					}					
					else{
						msg.what	= MSG_COMMON_PRODUCTS_LOADED;
						msg.obj  	= commonProducts;
						mHandler.sendMessage(msg);
					}
				}
			});
			
			tCommon.start();
		}
	}
}

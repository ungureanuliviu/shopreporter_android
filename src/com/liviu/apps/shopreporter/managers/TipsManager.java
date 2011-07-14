package com.liviu.apps.shopreporter.managers;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.liviu.apps.shopreporter.data.Tip;
import com.liviu.apps.shopreporter.interfaces.TipsProvider;
import com.liviu.apps.shopreporter.utils.Console;
import com.liviu.apps.shopreporter.utils.Utils;

public class TipsManager implements TipsProvider{
	
	// Interfaces
	public interface TipsListener{
		public void onTipsLoaded(boolean pIsSuccess, ArrayList<Tip> pTipsList);
		public void onTipAdded(boolean isSuccess, Tip pAddedTip);
		public void onTipRemoved(boolean isSuccess, Tip pRemovedTip);
	}	

	// Constants
	private final String 	TAG 					= "TipsManager";
	private final int		MSG_TIPS_LOAD_COMPLETE 	= 0;
	private final int 		MSG_TIP_ADDED			= 1;
	private final int		MSG_TIP_REMOVED			= 2;
	
	// Data 
	private DatabaseManger 	mDbMan;
	private ArrayList<Tip>	mTips;
	private Context 		mContenxt;
	private Handler			mHandler;
	
	// Listeners
	private TipsListener    mTipsListener;	
	
	public TipsManager(Context pContext) {
		mContenxt 	= pContext;
		mDbMan		= DatabaseManger.getInstance(mContenxt);
		mTips		= new ArrayList<Tip>();
		mHandler	= new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case MSG_TIPS_LOAD_COMPLETE:
					if(mTipsListener != null){
						if(msg.obj != null){
							ArrayList<Tip> loadedTips = (ArrayList<Tip>)msg.obj;
							mTips = loadedTips;
							
							mTipsListener.onTipsLoaded(true, loadedTips);										
						}
						else{
							mTipsListener.onTipsLoaded(false, null);
							mTips.clear();
						}
					}
					break;
				case MSG_TIP_ADDED:
					if(mTipsListener != null){
						if(msg.obj != null){
							if(msg.arg1 == 1){
								mTips.add((Tip)msg.obj);
							}
							mTipsListener.onTipAdded(true, (Tip)msg.obj);							
						}
						else{
							mTipsListener.onTipAdded(false, null);
						}
					}
					break;
				case MSG_TIP_REMOVED:
					if(mTipsListener != null){
						if(msg.obj != null){
							Tip removedTip = (Tip)msg.obj;
							mTipsListener.onTipRemoved(true, removedTip);							
						}
						else{
							mTipsListener.onTipRemoved(false, null);
						}
					}
					break;
				default:
					break;
				}
			};
		};
	}
		
	public TipsManager loadTipsByCategory(int pCategory){
		final int cCategory = pCategory;
		Thread tLoadTips = new Thread(new Runnable() {			
			@Override
			public void run() {
				ArrayList<Tip> tips = mDbMan.getTipsByCategory(cCategory);
				Message msg = new Message();
				
				msg.what 	= MSG_TIPS_LOAD_COMPLETE;
				msg.obj 	= tips;
				
				mHandler.sendMessage(msg);				
			}
		});
		
		tLoadTips.start();
		return this;
	}
	
	public TipsManager setTipsListener(TipsListener pListener){
		mTipsListener = pListener;
		return this;
	}
	
	/**
	 * Add a new tip in database
	 * @param pTip 	new tip object which will be added. The tip is ignored and alway the added tip will
	 * 				get a new id
	 * @param pAddToCurrentTipsList
	 * 				set it to true if you want to add new tip to the current tips list(after it was added in database)
	 * @return the listener method {@link TipsListener#onTipAdded(boolean, Tip)} will be called
	 */
	public void addTip(Tip pTip, boolean pAddToCurrentTipsList){
		final Tip 		cTip 					= pTip;
		final boolean 	cAddToCurrentTipsList 	= pAddToCurrentTipsList;
		
		Thread tAddTip = new Thread(new Runnable() {			
			@Override
			public void run() {
				Tip addedTip = mDbMan.addTip(cTip);
				
				Message msg = new Message();
				msg.what 	= MSG_TIP_ADDED;
				msg.obj 	= addedTip;
				msg.arg1 	= (cAddToCurrentTipsList == true ? 1 : 0);
				
				mHandler.sendMessage(msg);
			}
		});
		
		tAddTip.start();
	}
	
	public void removeTip(Tip pTip){
		final Tip cTip = pTip;
		
		Thread tRemoveTip = new Thread(new Runnable() {			
			@Override
			public void run() {
				Tip removedTip = mDbMan.removeTip(cTip);
				
				if(removedTip != null){
					// The tip was removed from database
					// We have to remove it from our tips list
					for(int i = 0; i < mTips.size(); i++){
						if(mTips.get(i).getId() == removedTip.getId()){
							mTips.remove(i);
							break;
						}
					}
				}
				Message msg = new Message();
				msg.what 	= MSG_TIP_REMOVED;
				msg.obj 	= removedTip;
				
				mHandler.sendMessage(msg);
			}
		});
		
		tRemoveTip.start();
	}
	
	/**
	 * Just for readability
	 * @return return the current instance of {@link TipsProvider}
	 */
	public TipsProvider getTipsProvider(){
		return this;
	}

	/** The client View will use this method to get a
	/*  new tip.
	/*  It will be random generated :)
	*/	
	@Override
	public Tip requestTip() {
		if(mTips == null)
			return null;
		
		if(mTips.isEmpty())
			return null;
		
		for(int i = 0; i < mTips.size(); i++){
			Console.debug(TAG, "TIP: " + mTips.get(i));
		}
		
		// generate a random number between [0, mTips.size() - 1]
		Random random = new Random(Utils.now());
		
		try{
			Console.debug(TAG, "requestTip: " + mTips.get(random.nextInt(mTips.size())));
			return mTips.get((int) (Utils.now() % mTips.size()));
		}
		catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return null;
	}
}

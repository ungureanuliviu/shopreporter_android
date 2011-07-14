package com.liviu.apps.shopreporter.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.liviu.apps.shopreporter.interfaces.OnAttachedImageClick;
import com.liviu.apps.shopreporter.utils.Console;


public class LAutocompleteEditText extends AutoCompleteTextView{

	// Constants
	private final String TAG = "LAutocompleteEditText";
	
	// Data
	private int attachedImageResourceID;
	
	// Listeners
	private OnAttachedImageClick onAttachedImageClick;
	
	public LAutocompleteEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LAutocompleteEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public LAutocompleteEditText(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		setDrawingCacheBackgroundColor(Color.TRANSPARENT);
		setDropDownBackgroundDrawable(new ColorDrawable(Color.WHITE));				
		attachedImageResourceID = -1;
		setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Console.debug(TAG, arg1 + " position: " + arg2);
			}			
		});
	}	

	/**
	 * In case of the event was set, we will fire it.
	 * Note: the listener should return a value:
	 * in case the listener return true, the event is consumed.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(event.getX() > LAutocompleteEditText.this.getWidth() - 30){
				if(onAttachedImageClick != null){
					onAttachedImageClick.onAttachedImageClick(LAutocompleteEditText.this);
					event.setAction(MotionEvent.ACTION_CANCEL);
				}
				else{
					LAutocompleteEditText.this.setText("");
				}
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * Add a listener to attached image
	 * @param a listener which will be triggered when use click on the attached image 
	 * {@link OnAttachedImageClick}
	 * <strong>Note: </strong> If the listener return true, the onTouchEvent will be consumed.
	 */
	public void setOnAttachedImageClickListener(OnAttachedImageClick listener){
		onAttachedImageClick = listener;
	}
	
	/**
	 * Attach a image to this view.
	 * The attached image will be displayed on right side of the view.
	 * @param resourceId
	 * @throws Resources.NotFoundException in case the resouce is not found. 
	 *         This exception is handled internal so no image will be attache but the application won't crash
	 */
	public void setAttachedImageFromResource(int resourceId){
		try{
	    	this.setCompoundDrawablesWithIntrinsicBounds(null, 
	    												 null,
	    												 getResources().getDrawable(resourceId), 
														 null);
	    	attachedImageResourceID = resourceId;
		}
		catch (Resources.NotFoundException e) {
			e.printStackTrace();
			Console.debug(TAG, "Cannot find a resource for id: " + resourceId);			
		}
	}
	
	public int getAttachedImageResourceId(){
		return attachedImageResourceID;
	}
		
}

package com.liviu.apps.shopreporter.ui;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.liviu.apps.shopreporter.utils.Console;

public class DragAreaView extends LinearLayout{

	// Constants 
	private final String TAG = "DragAreaView";
	
	// Data
	private ArrayList<View> mViews;	
	
	// UI
	private View 						mViewDropper;
	private LinearLayout.LayoutParams	mLayoutParams;
	
	// Listeners
	private OnTouchListener mTouchListener;
	
	public DragAreaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public DragAreaView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		mViews = new ArrayList<View>();
		setGravity(Gravity.TOP);
		
		mTouchListener = new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
					
					mLayoutParams 				= (LinearLayout.LayoutParams)v.getLayoutParams();
					mLayoutParams.leftMargin 	= (int) (event.getRawX() - v.getWidth());
					mLayoutParams.topMargin	 	= (int) (event.getRawY() - v.getHeight());
					
					v.setLayoutParams(mLayoutParams);
					if(mViewDropper != null){
						if(mViewDropper.getLeft() >= v.getLeft() && mViewDropper.getTop() + v.getHeight() == v.getTop()){
							Console.debug(TAG, "Drop it here");
							((Button)mViewDropper).setText("droppppp");
						}
					}

				}
				Console.debug(TAG, v.getLeft() + " " + v.getTop()+ " dropper: x: " + mViewDropper.getLeft() + " top:" + mViewDropper.getTop());
				return false;
			}
		};
	}
	
	public DragAreaView setViewDropper(View pDropper){
		mViewDropper = pDropper;
		return this;
	}
	
	@Override
	public void addView(View child) {
		super.addView(child);
		child.setOnTouchListener(mTouchListener);
		mViews.add(child);
		
	}

}

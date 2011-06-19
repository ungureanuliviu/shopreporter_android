package com.liviu.apps.shopreporter.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import com.liviu.apps.shopreporter.interfaces.OnLocationItemClick;
import com.liviu.apps.shopreporter.utils.Console;


public class LLocationsView extends HorizontalScrollView implements OnClickListener{

	// Constants
	private final String TAG = "LLocationsView";
	
	// Data
	private ArrayList<String> 	items;
	
	// UI
	private RelativeLayout	layoutButtonsHolder;
	
	// Listeners	
	private OnLocationItemClick onLocationItemClickListener;
	
	public LLocationsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initLayout();
	}

	public LLocationsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLayout();
	}
	
	public LLocationsView(Context context) {
		super(context);
		initLayout();
	}
	
	private void initLayout(){		
		items									 = new ArrayList<String>();
		layoutButtonsHolder 					 = new RelativeLayout(getContext());
		RelativeLayout.LayoutParams holderParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		addView(layoutButtonsHolder, holderParams);
	}
	
	public LLocationsView add(Address addr){
		String strAddress = new String();
		for(int i = 0; i < addr.getMaxAddressLineIndex(); i++){
			if(addr.getAddressLine(i) != null)
				strAddress += addr.getAddressLine(i) + " ";
		}
		
		if(items.contains(strAddress.replace(" ", ""))){
			return this;
		}
		
		Button newAddrButton = new Button(getContext());
		RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		Console.debug(TAG, "items size: " + items.size());
		if(items.size() == 0){
			// this is the first item
			Console.debug(TAG, "first item: " + addr);
			buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);					
		}
		else{
			Console.debug(TAG, "other item: " + " " + (items.size()) + " " + addr);
			buttonParams.addRule(RelativeLayout.RIGHT_OF, items.size());
		}
		
		buttonParams.rightMargin = 5;
		newAddrButton.setId(items.size() + 1);
		newAddrButton.setTextColor(Color.WHITE);		
		newAddrButton.setTextSize(13);
		newAddrButton.setBackgroundResource(com.liviu.apps.shopreporter.R.drawable.button_background);
		
		Console.debug(TAG, strAddress);
		
		if(strAddress.length() > 0){
			items.add(strAddress.replace(" ", ""));
			newAddrButton.setText(strAddress);
			newAddrButton.setPadding(5, 5, 5, 5);
			newAddrButton.setLayoutParams(buttonParams);			
			layoutButtonsHolder.addView(newAddrButton);
			newAddrButton.setOnClickListener(this);
		}				
		
		return this;
	}

	public LLocationsView setOnLocationItemClick(OnLocationItemClick listener){
		onLocationItemClickListener = listener;
		return this;
	}
	
	@Override
	public void onClick(View v) {
		if(onLocationItemClickListener != null){
			if(v instanceof Button)
				onLocationItemClickListener.onLocationItemClick(((Button)v).getText().toString());
		}
	}
	
	
}

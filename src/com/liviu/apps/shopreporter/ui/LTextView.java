package com.liviu.apps.shopreporter.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Liviu
 * This class create a custom TevtView with a default font
 */

public class LTextView extends TextView{

	// constants
	private final 	String 		TAG = "LTextView";	
	private 		Typeface	typeface;
	
	public LTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		typeface = Typeface.createFromAsset(context.getAssets(), "fonts/VAGRON.TTF");
		
		if(typeface != null)
			setTypeface(typeface);
	}

	public LTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		typeface = Typeface.createFromAsset(context.getAssets(), "fonts/VAGRON.TTF");
		
		if(typeface != null)
			setTypeface(typeface);
	}
	
	public LTextView(Context context) {
		super(context);
		
		typeface = Typeface.createFromAsset(context.getAssets(), "fonts/VAGRON.TTF");
		
		if(typeface != null)
			setTypeface(typeface);
	}	
}

package com.liviu.apps.shopreporter.ui;


import com.liviu.apps.shopreporter.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TabsHolder implements OnClickListener{
	
	// constants
	private final String		TAG = "TabsHolder";
	
	// data
	private Context context;
	private LayoutInflater lf;
	
	// ui
	private RelativeLayout 	tabsHolderLayout;
	private RelativeLayout	tab1;
	private RelativeLayout  tab2;
	private RelativeLayout  tab3;
	private RelativeLayout	tab4;
	
	// listeners
	private OnClickListener onClickListener;
	
	public TabsHolder(Context ctx, RelativeLayout tabsLayout) {
		context 			= ctx;
		lf					= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		tabsHolderLayout 	= tabsLayout;  
		tab1				= (RelativeLayout)tabsLayout.findViewById(com.liviu.apps.shopreporter.R.id.tab_1);
		tab2				= (RelativeLayout)tabsLayout.findViewById(com.liviu.apps.shopreporter.R.id.tab_2);
		tab3				= (RelativeLayout)tabsLayout.findViewById(com.liviu.apps.shopreporter.R.id.tab_3);
		tab4				= (RelativeLayout)tabsLayout.findViewById(com.liviu.apps.shopreporter.R.id.tab_4);
		
		tab1.setOnClickListener(this);
		tab2.setOnClickListener(this);  
		tab3.setOnClickListener(this);
		tab4.setOnClickListener(this);
		
	}
 
	@Override
	public void onClick(View v) {
		resetTabs();
		LayoutParams params = v.getLayoutParams();
		params.height = 60;		
		v.setLayoutParams(params);
		
		
		if(onClickListener != null)
			onClickListener.onClick(v);		
	}
	
	public void setOnTabClickListener(OnClickListener listener){
		onClickListener = listener;
	}
	
	private void resetTabs(){		
		LayoutParams params = tab1.getLayoutParams();
		params.height = 50;
		tab1.setLayoutParams(params);
		
		
		
		params = tab2.getLayoutParams();
		params.height = 50;
		tab2.setLayoutParams(params);

		params = tab3.getLayoutParams();  
		params.height = 50;		
		tab3.setLayoutParams(params);  
		
		params = tab4.getLayoutParams();
		params.height = 50;
		tab4.setLayoutParams(params);
	}

	public void setTypeface(Typeface typeface) {
		if(typeface != null){
			((TextView)tab1.findViewById(R.id.tab_text_home)).setTypeface(typeface);
			((TextView)tab2.findViewById(R.id.tab_text_new_session)).setTypeface(typeface);
			((TextView)tab3.findViewById(R.id.tab_text_my_session)).setTypeface(typeface);
			((TextView)tab4.findViewById(R.id.tab_text_reports)).setTypeface(typeface);
		}
	}

}

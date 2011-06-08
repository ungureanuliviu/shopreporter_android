package com.liviu.apps.shopreporter;


import com.liviu.apps.shopreporter.ui.TabsHolder;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	
	// constats 
	private final String TAG = "MainActivity";
	
	// ui
	private TabsHolder	 tabsHolder;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);        
        setContentView(R.layout.main);               
        
        tabsHolder = new TabsHolder(this, (RelativeLayout)findViewById(R.id.layout_tabs_holder));
    } 
}  
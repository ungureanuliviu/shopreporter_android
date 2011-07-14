package com.liviu.apps.shopreporter;

 
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liviu.apps.shopreporter.data.User;
import com.liviu.apps.shopreporter.managers.ActivityIdProvider;
import com.liviu.apps.shopreporter.utils.Console;

public class MainActivity extends Activity implements OnClickListener{
	
	// constats 
	private final 			String 	TAG 		= "MainActivity";
	public  final static 	int		REQUEST_ID  = ActivityIdProvider.getInstance().getNewId(MainActivity.class);
	public  final static 	String  PREFS_NAME  = "sp_prefs";
	
	// data
	private Typeface		typeface;
	private Typeface		typefaceSmall;
	private User			user;
	
	// ui
	private RelativeLayout	butNewShopSession;
	private RelativeLayout	butHours;
	private RelativeLayout	butMoney;
	private RelativeLayout	butReports;	
	private RelativeLayout	butShopLists;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);        
        setContentView(R.layout.main);               
        
        typeface			= Typeface.createFromAsset(getAssets(), "fonts/VAGROUN.TTF");
        typefaceSmall		= Typeface.createFromAsset(getAssets(), "fonts/VAGRON.TTF");
        butNewShopSession 	= (RelativeLayout)findViewById(R.id.main_menu_start_session);
        butHours			= (RelativeLayout)findViewById(R.id.main_menu_time);
        butMoney			= (RelativeLayout)findViewById(R.id.main_menu_money);
        butReports			= (RelativeLayout)findViewById(R.id.main_menu_reports);
        butShopLists		= (RelativeLayout)findViewById(R.id.main_menu_shopping_list);
        user				= User.getInstance(this);                
        
        Console.debug(TAG, "user.isLoggedIn()" + user.isLoggedIn() + " " + user.getFName());
        
        // set new font   
        ((TextView)butNewShopSession.findViewById(R.id.main_menu_item_text1)).setTypeface(typefaceSmall);        
        ((TextView)butHours.findViewById(R.id.main_menu_item_text2)).setTypeface(typefaceSmall);        
        ((TextView)butMoney.findViewById(R.id.main_menu_item_text3)).setTypeface(typefaceSmall);
        ((TextView)butReports.findViewById(R.id.main_menu_item_text4)).setTypeface(typefaceSmall);
        ((TextView)butShopLists.findViewById(R.id.main_menu_item_text5)).setTypeface(typefaceSmall);
        
        // set listeners
        butNewShopSession.setOnClickListener(this);
    }

    // interfaces   
	@Override   
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_menu_start_session:
			Intent toNewShoppinSession = new Intent(MainActivity.this, CreateNewSessionActivity.class);
			startActivity(toNewShoppinSession);
			break;

		default:
			break;
		}
	} 
}  
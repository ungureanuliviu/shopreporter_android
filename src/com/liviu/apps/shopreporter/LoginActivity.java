package com.liviu.apps.shopreporter;

 
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.liviu.apps.shopreporter.data.User;
import com.liviu.apps.shopreporter.interfaces.OnLoginListener;
import com.liviu.apps.shopreporter.managers.ActivityIdProvider;
import com.liviu.apps.shopreporter.ui.LEditText;
import com.liviu.apps.shopreporter.utils.Console;

public class LoginActivity extends Activity implements OnClickListener,
													   OnLoginListener{
	
	// Constants 
	private final 			String 	TAG 				= "LoginActivity";
	public  final static 	int		REQUEST_ID  		= ActivityIdProvider.getInstance().getNewId(LoginActivity.class);
	public  final static	String 	KEY_PARENT_ID		= "key_parent_id";
	
	// Data
	private User			user;
	private Typeface		typeface;
	private int 			parentActivityCode;
	
	// UI
	private LEditText		edtxUsername;
	private LEditText		edtxPassword;
	private Button			butSignUp;
	private Button			butLogin;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);        
        setContentView(R.layout.login_layout);   
        
        // get layout components references
        edtxPassword 		= (LEditText)findViewById(R.id.edtx_user_password);
        edtxUsername 		= (LEditText)findViewById(R.id.edtx_user_name);
        butLogin			= (Button)findViewById(R.id.but_login);
        butSignUp			= (Button)findViewById(R.id.but_signup);
        typeface			= Typeface.createFromAsset(getAssets(), "fonts/VAGRON.TTF");
        user				= User.getInstance(this);
        parentActivityCode	= MainActivity.REQUEST_ID;
                
        if(getIntent() != null){
        	parentActivityCode = getIntent().getIntExtra(KEY_PARENT_ID, MainActivity.REQUEST_ID);        	        		
    	}
        
        if(user.isLoggedIn()){
        	Class<?> clazz = ActivityIdProvider.getActivity(parentActivityCode);
        	if(clazz != null){
        		Intent toActivity = new Intent(LoginActivity.this, clazz);
        		startActivity(toActivity);
        		finish();
        	}
        	else{
        		Intent toActivity = new Intent(LoginActivity.this, MainActivity.class);
        		startActivity(toActivity);
        		finish();        		
        	}        		
        }
        
        butLogin.setTypeface(typeface);
        butSignUp.setTypeface(typeface);
        
        // set listeners
        butLogin.setOnClickListener(this);
        butSignUp.setOnClickListener(this);
        user.setOnLoginListener(this);
    }

    // interfaces   
	@Override   
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.but_login:
			
			// check if the user type something in input fields
			if(edtxPassword.getText().length() == 0 || edtxUsername.getText().length() == 0){
				Toast.makeText(LoginActivity.this, getString(R.string.error_incomplete_details), Toast.LENGTH_LONG).show();
				return;
			}
			
			if(!user.isLoggedIn()){
				user.login(edtxUsername.getText().toString(), edtxPassword.getText().toString());
			}						
			break;
		case R.id.but_signup:
			user = User.create(this, "Liviu", "test", "Liviu", "Ungureanu");
			if(user != null)
				user.setOnLoginListener(LoginActivity.this);
			break;
		default:
			break;
		}
	}

	@Override
	public void onLogin(boolean isSucces) {
		Console.debug(TAG, "onLogin: " + isSucces);
		if(!isSucces){
			Toast.makeText(LoginActivity.this, getString(R.string.error_login), Toast.LENGTH_LONG).show();
			return;
		}
		else{
        	Class<?> clazz = ActivityIdProvider.getActivity(parentActivityCode);
        	if(clazz != null){
        		Intent toActivity = new Intent(LoginActivity.this, clazz);
        		startActivity(toActivity);
        		finish();
        	}
        	else{
        		Intent toActivity = new Intent(LoginActivity.this, MainActivity.class);
        		startActivity(toActivity);
        		finish();        		
        	}			
		}
	} 
}  
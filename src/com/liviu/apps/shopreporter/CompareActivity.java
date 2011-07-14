package com.liviu.apps.shopreporter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ComponentInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liviu.apps.shopreporter.adapters.CompareAdapter;
import com.liviu.apps.shopreporter.adapters.SessionsAdapter;
import com.liviu.apps.shopreporter.data.CompareInfo;
import com.liviu.apps.shopreporter.data.Product;
import com.liviu.apps.shopreporter.data.Session;
import com.liviu.apps.shopreporter.data.User;
import com.liviu.apps.shopreporter.interfaces.SessionListener;
import com.liviu.apps.shopreporter.managers.ActivityIdProvider;
import com.liviu.apps.shopreporter.managers.CompareQuestionsGenerator;
import com.liviu.apps.shopreporter.ui.LTextView;
import com.liviu.apps.shopreporter.utils.Console;

public class CompareActivity extends Activity implements SessionListener,
														 OnItemClickListener,
														 OnClickListener,
														 android.content.DialogInterface.OnClickListener{

	// Constants
	private 		final String 	TAG 			= "CompareActivity";
	public 	static 	final int 		REQUEST_ID 		= ActivityIdProvider.getInstance().getNewId(CompareActivity.class);
	public  static  final String	CURRENT_SESSION = "current_session";
	public  static  final String	COMPARE_SESSION = "compare_session";
	
	// Data
	private Session 					currentSession;
	private Session 					compareSession;
	private User						user;
	private CompareAdapter				adapterCompare;
	private CompareQuestionsGenerator	compareQGen;
	private SessionsAdapter				adapterSessions;
	private SharedPreferences			prefs;
	
	// UI
	private ListView			listSessions;	
	private ProgressBar			progressBar;
	private Button				popupButBack;
	private RelativeLayout		popupOverlay;
	private ListView			listCompare;
	private LTextView			txtSessionName1;
	private LTextView			txtSessionName2;
	private Button				butBack;
	private Button				butSessions;
	private Button				butFinish;
	private AlertDialog			alertDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);    
		setContentView(R.layout.compare_sessions_layout);
		
		user 				= User.getInstance(this);
		listSessions 		= (ListView)findViewById(R.id.sessions_list);	
		adapterSessions		= new SessionsAdapter(this);
		progressBar			= (ProgressBar)findViewById(R.id.loading_progress);
		popupButBack		= (Button)findViewById(R.id.popup_but_back);
		popupOverlay		= (RelativeLayout)findViewById(R.id.layout_overlay_holder);
		listCompare			= (ListView)findViewById(R.id.compare_list);
		adapterCompare		= new CompareAdapter(this);
		compareQGen			= new CompareQuestionsGenerator();
		txtSessionName1		= (LTextView)findViewById(R.id.session_name_1);
		txtSessionName2		= (LTextView)findViewById(R.id.session_name_2);
		butBack				= (Button)findViewById(R.id.but_back);
		butSessions			= (Button)findViewById(R.id.but_pick_session);
		butFinish			= (Button)findViewById(R.id.but_finish);
		prefs				= getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
		alertDialog 		= new AlertDialog.Builder(this).create();
		
		
        alertDialog.setButton(AlertDialog.BUTTON1, "Finish it!", this);
        alertDialog.setButton(AlertDialog.BUTTON2, "Cancel", this);
        
		// check if the user is logged in.
		if(!user.isLoggedIn()){
			Intent toLogin = new Intent(CompareActivity.this, LoginActivity.class);
			toLogin.putExtra(LoginActivity.KEY_PARENT_ID, REQUEST_ID);
			startActivity(toLogin);
			finish();
		}
		
		Console.debug(TAG, "the user is logged in");		
        Console.debug(TAG, "session: " + getIntent().getBundleExtra(Session.KEY_BUNDLE));
        
        if(getIntent() != null){
        	if(getIntent().getBundleExtra(Session.KEY_BUNDLE) != null){
        		currentSession = new Session(getIntent().getBundleExtra(Session.KEY_BUNDLE));        		
        	}
        	else{
        		Toast.makeText(CompareActivity.this, getString(R.string.error_no_session_selected), Toast.LENGTH_LONG).show();
        		finish();
        	}
        }
        else{
    		Toast.makeText(CompareActivity.this, getString(R.string.error_no_session_selected), Toast.LENGTH_LONG).show();
    		finish();
        }
                
        if(currentSession == null){
    		Toast.makeText(CompareActivity.this, getString(R.string.error_no_session_selected), Toast.LENGTH_LONG).show();
    		finish();        	
        }
        
        // Set listeners
        user.getShoppingManager().setSessionListener(this);
        listSessions.setOnItemClickListener(this);
        popupButBack.setOnClickListener(this);
        butBack.setOnClickListener(this);
        butFinish.setOnClickListener(this);
        butSessions.setOnClickListener(this);
        
        // Watch for SessionListener.onUserSessionsLoaded
        user.getShoppingManager().getAllSessions(true);       
        compareQGen.setSession1(currentSession);
        txtSessionName1.setText(currentSession.getName());
		
        if(compareSession == null){
        	popupOverlay.setVisibility(View.VISIBLE);
        }
        
        alertDialog.setMessage("Do you want to mark this session: '" + currentSession.getName() + "' as finished?");
	}

	@Override
	public void onSessionCreated(boolean isSuccess, Session newSession) {
	}

	@Override
	public void onProductAddedToSession(boolean isSuccess, Session session,
			Product addedProduct) {
	}

	@Override   
	public void onProductRemovedFromSession(boolean isSucces, Session session,
			Product removedProduct) {
	}

	@Override
	public void onSessionLoaded(boolean isSuccess, Session pSession) {
	}

	@Override
	public void onUserSessionsLoaded(boolean isSuccess, ArrayList<Session> pLoadedSessions) {
		Console.debug(TAG, "onUserSessionsLoaded: " + isSuccess + " " + pLoadedSessions); 
		
		if(isSuccess){
			for(int i = 0 ; i < pLoadedSessions.size(); i++){
				adapterSessions.add(pLoadedSessions.get(i));
			}
			
			progressBar.setVisibility(View.GONE);
			listSessions.setVisibility(View.VISIBLE);
			listSessions.setAdapter(adapterSessions);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		compareSession = adapterSessions.getItem(position);
		Console.debug(TAG, "compareSession: " + compareSession);
		txtSessionName2.setText(compareSession.getName());
		popupOverlay.setVisibility(View.GONE);		
		compareQGen.setSession2(compareSession);
		adapterCompare.setItems(compareQGen.compare());
		listCompare.setAdapter(adapterCompare);
		
		user.getShoppingManager().getCommonProducts(compareSession, currentSession);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.but_back:
			finish();
			break;
		case R.id.popup_but_back:
			if(compareSession != null)
				popupOverlay.setVisibility(View.GONE);							
			else
				finish();
			break;
		case R.id.but_pick_session:
			popupOverlay.setVisibility(View.VISIBLE);
			break;
		case R.id.but_finish:
			alertDialog.show();
			// ask for user confirmation
			break;
		default:
			break;
		}
	}

	@Override
	public void onCommonProductsLoaded(boolean isSuccess, ArrayList<Product> pCommonProducts) {
		if(isSuccess){
			String textProducts = "";
			for (Product product : pCommonProducts){
				Console.debug(TAG, "common product: " + product);
				textProducts += product.getName() + " ";
			}
			
			CompareInfo cInfo = new CompareInfo();
			cInfo.setQuestion("Which are the common products?")
				 .setAnswerLeft(textProducts)
				 .setAnswerRight(textProducts)
				 .setCorrectAnswer(CompareInfo.ANSWER_NONE);
			
			adapterCompare.add(cInfo);
			adapterCompare.notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(compareSession != null && popupOverlay.getVisibility() == View.VISIBLE){
				popupOverlay.setVisibility(View.GONE);
				return true;
			}			
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		super.onPause();		
		if(compareSession != null && currentSession != null){
			SharedPreferences.Editor ed = prefs.edit();
			ed.putString(CURRENT_SESSION, currentSession.toJson().toString());
			ed.putString(COMPARE_SESSION, compareSession.toJson().toString());
			ed.commit();
		}
		else
			Console.debug(TAG, "[on pause] the session cannot be saved");
	}
	
	@Override
	protected void onResume() {
		Console.debug(TAG, "on resume: " + getIntent().getBundleExtra(Session.KEY_BUNDLE));
		if(getIntent().getBundleExtra(Session.KEY_BUNDLE) == null || compareSession == null){		
			prefs	= getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
						
			if(getIntent().getBundleExtra(Session.KEY_BUNDLE) == null){
				String currentSessionJsonString = prefs.getString(CURRENT_SESSION, null);			
				
				try {
					currentSession = new Session(new JSONObject(currentSessionJsonString));
				}
				catch (JSONException e) {			
					e.printStackTrace();
					Toast.makeText(CompareActivity.this, "The session cannot be loaded.", Toast.LENGTH_SHORT).show();
					finish();
				}				
			}						
			
			if(compareSession == null){
				String compareSessionJsonString = prefs.getString(COMPARE_SESSION, null);
				try {
					compareSession = new Session(new JSONObject(compareSessionJsonString));
				}
				catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(CompareActivity.this, "The session cannot be loaded.", Toast.LENGTH_SHORT).show();
					finish();					
				}
			}
			
			if(currentSession == null || compareSession == null){
				Toast.makeText(CompareActivity.this, "The session cannot be loaded.", Toast.LENGTH_SHORT).show();
				finish();
			}				
		}
		super.onResume();		
	}

	@Override
	public void onClick(DialogInterface arg0, int which) {
		switch (which) {
		case AlertDialog.BUTTON1:			
			if(currentSession != null)
				user.getShoppingManager().finishSession(currentSession);
				
			alertDialog.dismiss();
			Intent toMainActivity = new Intent(CompareActivity.this, MainActivity.class);
			startActivity(toMainActivity);
			finish();			
			break;
		case AlertDialog.BUTTON2:
			alertDialog.dismiss();
			break;
		default:
			break;
		}		
	}
	
}

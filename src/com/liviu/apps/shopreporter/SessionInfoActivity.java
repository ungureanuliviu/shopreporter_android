package com.liviu.apps.shopreporter;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.liviu.apps.shopreporter.adapters.QuestionsAdapter;
import com.liviu.apps.shopreporter.data.Question;
import com.liviu.apps.shopreporter.data.Session;
import com.liviu.apps.shopreporter.data.User;
import com.liviu.apps.shopreporter.managers.ActivityIdProvider;
import com.liviu.apps.shopreporter.ui.LTextView;
import com.liviu.apps.shopreporter.utils.Console;
import com.liviu.apps.shopreporter.utils.Utils;

public class SessionInfoActivity extends Activity implements OnClickListener,
															 android.content.DialogInterface.OnClickListener{

	// Constants
	private 		final String 	TAG 		= "SessionInfoActivity";
	public 	static 	final int 		REQUEST_ID 	= ActivityIdProvider.getInstance().getNewId(SessionInfoActivity.class);
	
	// Data
	private User 				mUser;
	private Session				mCurrentSession;
	private QuestionsAdapter	mAdapter;
	private SharedPreferences	mPrefs;
	private Typeface			mTypeface;
	
	// UI
	private ListView		mLst;
	private Button			mButBack;
	private Button			mButFinish;
	private LTextView		mBarTitle;
	private LTextView		mBarStarted;
	private Button			mBarButCompare;
	private Button			mBarButShowSessions;
	private Button			mBarButGenerateReport;
	private AlertDialog		alertFinishSession;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);    
		setContentView(R.layout.finish_session_layout);
		
		mUser 				= User.getInstance(this);
		mLst				= (ListView)findViewById(R.id.questions_list);
		mButBack			= (Button)findViewById(R.id.but_back);
		mButFinish			= (Button)findViewById(R.id.but_finish);
		mBarStarted 		= (LTextView)findViewById(R.id.bar_time);
		mBarTitle			= (LTextView)findViewById(R.id.bar_title);
		mBarButCompare 		= (Button)findViewById(R.id.but_compare_sessions);
		mBarButShowSessions = (Button)findViewById(R.id.but_show_sessions);
		mAdapter			= new QuestionsAdapter(this);
		mTypeface			= Typeface.createFromAsset(getAssets(), "fonts/VAGRON.TTF");
		mPrefs				= getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
		alertFinishSession 	= new AlertDialog.Builder(this).create();
		
		mBarButCompare.setTypeface(mTypeface);
        alertFinishSession.setButton(AlertDialog.BUTTON1, "Finish it!", this);
        alertFinishSession.setButton(AlertDialog.BUTTON2, "Cancel", this);
		
		if(!mUser.isLoggedIn()){
			Intent toLogin = new Intent(SessionInfoActivity.this, LoginActivity.class);
			toLogin.putExtra(LoginActivity.KEY_PARENT_ID, REQUEST_ID);
			startActivity(toLogin);
			finish();
		}
						
		Console.debug(TAG, "the user is logged in");		
        Console.debug(TAG, "session: " + getIntent().getBundleExtra(Session.KEY_BUNDLE));
        
        if(getIntent() != null){
        	if(getIntent().getBundleExtra(Session.KEY_BUNDLE) != null){
        		mCurrentSession = new Session(getIntent().getBundleExtra(Session.KEY_BUNDLE));        		
        	}
        	else{
        		Toast.makeText(SessionInfoActivity.this, getString(R.string.error_no_session_selected), Toast.LENGTH_LONG).show();
        		finish();
        	}
        }
        else{
    		Toast.makeText(SessionInfoActivity.this, getString(R.string.error_no_session_selected), Toast.LENGTH_LONG).show();
    		finish();
        }
                
        if(mCurrentSession == null){
    		Toast.makeText(SessionInfoActivity.this, getString(R.string.error_no_session_selected), Toast.LENGTH_LONG).show();
    		finish();        	
        }		

        mBarTitle.setText(mCurrentSession.getName());
        mBarStarted.setText("Started: " + Utils.formatDate(mCurrentSession.getStartTime(), "E, dd MMM HH:mm"));        
        
        Console.debug(TAG, "current loaded session: " + mCurrentSession + " seconds: " + mCurrentSession.getTotalTime()/1000);
		mAdapter.add(new Question("What session?", mCurrentSession.getName()+ "."))
				.add(new Question("Where?", mCurrentSession.getLocation() + "."))
				.add(new Question("How many products I bought?", Integer.toString(mCurrentSession.getTotalProducts(true)) + "."))
				.add(new Question("How much I paid for them?", "$" + Utils.roundTwoDecimals(mCurrentSession.getTotalMoney()) + "."))				
				.add(new Question("How long I was at shopping?", "About " + Utils.formatTime(mCurrentSession.getTotalTime()) + "."));		
		
		mLst.setAdapter(mAdapter);
		mButBack.setOnClickListener(this);
		mButFinish.setOnClickListener(this);
		mBarButCompare.setOnClickListener(this);
		mBarButShowSessions.setOnClickListener(this);
		
		alertFinishSession.setMessage("Do you want to mark this session: '" + mCurrentSession.getName() + "' as finished?");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {		
		case R.id.but_back:
			finish();
			break;
		case R.id.but_finish:
			alertFinishSession.show();
			break;
		case R.id.but_compare_sessions:			
			if(mCurrentSession != null){ 
				Intent toCompareActivity = new Intent(SessionInfoActivity.this, CompareActivity.class);
				toCompareActivity.putExtra(Session.KEY_BUNDLE, mCurrentSession.toBundle());
				startActivity(toCompareActivity);				
			}
			else{
				Toast.makeText(SessionInfoActivity.this, "Cannot find the right session.", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.but_show_sessions:
			Intent toSessions = new Intent(SessionInfoActivity.this, SessionsActivity.class);
			startActivity(toSessions);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences.Editor ed = mPrefs.edit();
		
		if(mCurrentSession != null){
			JSONObject json = mCurrentSession.toJson();
			
			if(json != null){
				ed.putString(Session.KEY_BUNDLE, json.toString());
				ed.commit();
				mCurrentSession = null;
			}
			else{
				finish();
			}						
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();		
		if(mCurrentSession == null){
			try {
				JSONObject json = new JSONObject(mPrefs.getString(Session.KEY_BUNDLE, ""));
				if(json != null){
					mCurrentSession = new Session(json);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Console.debug(TAG, "the session cannot be retrived from preference");
				Toast.makeText(SessionInfoActivity.this, "Cannot find the current session.", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case AlertDialog.BUTTON1:
				mUser.getShoppingManager().finishSession(mCurrentSession);
				alertFinishSession.dismiss();
				Intent toMainActivity = new Intent(SessionInfoActivity.this, MainActivity.class);
				startActivity(toMainActivity);
				finish();	
				break;
			case AlertDialog.BUTTON2:
				alertFinishSession.dismiss();
				break;
			default:
				break;
		}					
	}
	
	
}

package com.liviu.apps.shopreporter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.liviu.apps.shopreporter.adapters.ShowSessionsAdapter;
import com.liviu.apps.shopreporter.data.MonthReportPeriod;
import com.liviu.apps.shopreporter.data.Product;
import com.liviu.apps.shopreporter.data.Session;
import com.liviu.apps.shopreporter.data.User;
import com.liviu.apps.shopreporter.interfaces.SessionListener;
import com.liviu.apps.shopreporter.managers.ActivityIdProvider;
import com.liviu.apps.shopreporter.ui.DragAreaView;
import com.liviu.apps.shopreporter.utils.Console;
import com.liviu.apps.shopreporter.utils.Utils;

public class SessionsActivity extends Activity implements SessionListener{

	// Constants
	private 		final String 	TAG 		= "SessionsActivity";
	public 	static 	final int 		REQUEST_ID 	= ActivityIdProvider.getInstance().getNewId(SessionsActivity.class);
	
	// Data
	private User				user;	
	private ShowSessionsAdapter adapter;
	
	// UI
	private ListView			lstView;
	private DragAreaView		dragArea;
	private Button				butReports;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);    
		setContentView(R.layout.show_sessions_layout);
		
		// Init
		user	= User.getInstance(this);
		lstView	= (ListView)findViewById(R.id.sessions_list);
		adapter = new ShowSessionsAdapter(this);
		dragArea = (DragAreaView)findViewById(R.id.drag_area);
		butReports	= (Button)findViewById(R.id.but_report);
		
		Button b = new Button(this);
		b.setText("test");
		b.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		
		dragArea.addView(b);
		dragArea.setViewDropper(butReports);
		
		if(!user.isLoggedIn()){
			Intent toLogin = new Intent(SessionsActivity.this, LoginActivity.class);
			toLogin.putExtra(LoginActivity.KEY_PARENT_ID, REQUEST_ID);
			startActivity(toLogin);
			finish();
		}
						
		Console.debug(TAG, "the user is logged in");
		
		
		// Set listeners
		user.getShoppingManager().setSessionListener(this);
		
		// Load all sessions
		user.getShoppingManager().getAllSessions(true);
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
		//Console.debug(TAG, "onUserSessionsLoaded: " + isSuccess + " " + pLoadedSessions);
		if(isSuccess){
			MonthReportPeriod month 		= new MonthReportPeriod();
			int 			  monthIndex 	= -1;
			int 			  yearIndex		= -1;
			
			for(int i = 0; i < pLoadedSessions.size(); i++){
				if(monthIndex == -1){
					month = new MonthReportPeriod();
					monthIndex  = Integer.parseInt(Utils.formatDate(pLoadedSessions.get(i).getStartTime(), "M"));
					yearIndex	= Integer.parseInt(Utils.formatDate(pLoadedSessions.get(i).getStartTime(), "yyyy"));
					month.setMonth(monthIndex);
					month.setYear(yearIndex);
					month.setFormattedDate(Utils.formatDate(pLoadedSessions.get(i).getStartTime(), "MMM yyyy"));					
				}
				else if(monthIndex != Integer.parseInt(Utils.formatDate(pLoadedSessions.get(i).getStartTime(), "M"))){
					Console.debug(TAG, "add it to adapter" + month);
					adapter.add(month);
					month = new MonthReportPeriod();
					monthIndex  = Integer.parseInt(Utils.formatDate(pLoadedSessions.get(i).getStartTime(), "M"));
					yearIndex	= Integer.parseInt(Utils.formatDate(pLoadedSessions.get(i).getStartTime(), "yyyy"));
					month.setMonth(monthIndex);
					month.setYear(yearIndex);
					month.setFormattedDate(Utils.formatDate(pLoadedSessions.get(i).getStartTime(), "MMM yyyy"));					
				}
				
				month.addSession(pLoadedSessions.get(i));			
			}
			
			Console.debug(TAG, "add it to adapter2" + month);
			adapter.add(month);
			
			lstView.setAdapter(adapter);
		}		
	}

	@Override
	public void onCommonProductsLoaded(boolean isSuccess,
			ArrayList<Product> pCommonProducts) {
	}
	
}

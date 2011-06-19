package com.liviu.apps.shopreporter;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.liviu.apps.shopreporter.data.Product;
import com.liviu.apps.shopreporter.data.Session;
import com.liviu.apps.shopreporter.data.User;
import com.liviu.apps.shopreporter.interfaces.OnAttachedImageClick;
import com.liviu.apps.shopreporter.interfaces.OnGeoCoderDataReceived;
import com.liviu.apps.shopreporter.interfaces.OnLocationItemClick;
import com.liviu.apps.shopreporter.interfaces.SessionListener;
import com.liviu.apps.shopreporter.managers.ActivityIdProvider;
import com.liviu.apps.shopreporter.ui.LEditText;
import com.liviu.apps.shopreporter.ui.LLocationsView;
import com.liviu.apps.shopreporter.utils.Cache;
import com.liviu.apps.shopreporter.utils.Console;

public class CreateNewSessionActivity extends Activity implements OnAttachedImageClick,
																  LocationListener,
																  OnGeoCoderDataReceived,
																  OnLocationItemClick,
																  OnClickListener,
																  SessionListener{
	
	// Constants
	private final 			String 	TAG 		= "CreateNewSessionActivity";
	public  final static 	int		REQUEST_ID  = ActivityIdProvider.getInstance().getNewId(CreateNewSessionActivity.class);
	
	// Data
	private Typeface				typeface;
	private Typeface				typefaceSmall;
	private User					user;
	
	// UI
	private LEditText				edtxLocation;
	private LEditText				edtxName;
	private LLocationsView			locationView;
	private Button					butCreate;
	private Button					butBack;
	
	// Services
	private LocationManager	locMan;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);         
        setContentView(R.layout.create_new_session_layout);               
        
        typeface			= Typeface.createFromAsset(getAssets(), "fonts/VAGROUN.TTF");
        typefaceSmall		= Typeface.createFromAsset(getAssets(), "fonts/VAGRON.TTF");
        edtxLocation		= (LEditText)findViewById(R.id.edtx_location);
        edtxName			= (LEditText)findViewById(R.id.edtx_name);
        locMan				= (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        user				= User.getInstance(this);
        locationView		= (LLocationsView)findViewById(R.id.pick_location_holder);
        butBack				= (Button)findViewById(R.id.but_back);
        butCreate			= (Button)findViewById(R.id.but_create);
                
        // set new font
        butBack.setTypeface(typefaceSmall);
        butCreate.setTypeface(typefaceSmall);
        
        // set listeners
        edtxLocation.setOnAttachedImageClickListener(this);
        edtxName.setOnAttachedImageClickListener(this);       
        user.getShoppingManager().setOnGeoCoderDataListener(this);  
        user.getShoppingManager().setSessionListener(this);
        locationView.setOnLocationItemClick(this);
        butBack.setOnClickListener(this);
        butCreate.setOnClickListener(this);
    }

    // ==* Interfaces *==
	@Override
	public boolean onAttachedImageClick(EditText parentView) {
		switch (parentView.getId()) {
		case R.id.edtx_name:
			parentView.setText("");		
			return false;			
		case R.id.edtx_location:							
			if(edtxLocation.getAttachedImageResourceId() == -1 || edtxLocation.getAttachedImageResourceId() == R.drawable.ic_edtx_location){
				edtxLocation.setAttachedImageFromResource(R.drawable.ic_edtx_location_blue);
				
				if(Cache.currentLocation != null){
					user.getShoppingManager().getLocationAddress(Cache.currentLocation);
				}
				else{
			        //locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
					locMan.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
				}
				
		        edtxLocation.setHint("Searching for your location...");		        
		        return true;
			}
			else{
				if(edtxLocation.getAttachedImageResourceId() == R.drawable.ic_edtx_location_blue){			
					edtxLocation.setAttachedImageFromResource(R.drawable.ic_edtx_location);				
					locMan.removeUpdates(CreateNewSessionActivity.this);
			        edtxLocation.setHint("Location");
			        return true;
				}
				else{
					edtxLocation.setText("");
					return false;
				}
			}								
		default:
			break;
		}
		
		return false;
	}

	// Location
	@Override
	public void onLocationChanged(Location location) {
		locationChanged(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	} 
	
	// GeoCoder data
	@Override
	public void onGeoCoderDataReceived(boolean isSuccess, List<Address> addresses) {
		Console.debug(TAG, "onGeoCoderDataReceived: isSuccess: " + isSuccess + " addresses: " + addresses);
		if(isSuccess){
			edtxLocation.setAttachedImageFromResource(R.drawable.ic_edtx_location_green);
			for(int i = 0; i < addresses.size(); i++)
				locationView.add(addresses.get(i));
		}
		else{
			Toast.makeText(CreateNewSessionActivity.this, getString(R.string.error_no_internet_connection), Toast.LENGTH_LONG).show();
			edtxLocation.setAttachedImageFromResource(R.drawable.ic_edtx_location);					
		}
		edtxLocation.setHint("Location");
	}		
	
	// LocationView
	@Override
	public void onLocationItemClick(String locationName) {
		if(locationName != null){
			edtxLocation.setText(locationName);
		}
	}	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.but_back:
			finish();			
			break;
		case R.id.but_create:
			if(edtxLocation.getText().length() == 0 || edtxName.getText().length() == 0){
				Toast.makeText(CreateNewSessionActivity.this, getString(R.string.error_incomplete_details), Toast.LENGTH_LONG).show();
				return;
			}
			else{
				user.getShoppingManager().createSession(edtxName.getText().toString(), edtxLocation.getText().toString());
			}
			break;
		default:
			break;
		}
	}			
	
	// ==* End of interfaces *==
	
	// ==* Private methods *==
	private void locationChanged(Location location){
		if(location != null){
			user.getShoppingManager().getLocationAddress(location);
			Cache.currentLocation = location;
		}
		else{
			
		}		
	}
	
	// ==* end of private methods *==
	@Override
	protected void onPause() {
		locMan.removeUpdates(this);
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSessionCreated(boolean isSuccess, Session newSession) {
		Console.debug(TAG, "[onSessionCreated]: " + isSuccess + " " + newSession); 
		if(isSuccess){
			Bundle bSession = newSession.toBunble();
			Intent toManageSessionActivity = new Intent(CreateNewSessionActivity.this, ManageSessionProductsActivity.class);
			Console.debug(TAG, "bundle to send: " + bSession);
			toManageSessionActivity.putExtra(Session.KEY_BUNDLE, bSession);
			startActivity(toManageSessionActivity);
			finish();
		}
		else{
			Toast.makeText(CreateNewSessionActivity.this, getString(R.string.error_session_not_created), Toast.LENGTH_LONG).show();
		}		
	}

	@Override
	public void onProductAddedToSession(boolean isSuccess, Session session,
			Product addedProduct) {
	}

	@Override
	public void onProductRemovedFromSession(boolean isSucces, Session session,
			Product removedProduct) {
	}


}  
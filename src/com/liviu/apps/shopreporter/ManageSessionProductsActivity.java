package com.liviu.apps.shopreporter;

 
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liviu.apps.shopreporter.adapters.AutocompleteAdapter;
import com.liviu.apps.shopreporter.adapters.NewProductsAdapter;
import com.liviu.apps.shopreporter.data.Product;
import com.liviu.apps.shopreporter.data.Session;
import com.liviu.apps.shopreporter.data.Tip;
import com.liviu.apps.shopreporter.data.User;
import com.liviu.apps.shopreporter.exceptions.InvalidUnitException;
import com.liviu.apps.shopreporter.interfaces.AutoCompleteLoadListener;
import com.liviu.apps.shopreporter.interfaces.LParam;
import com.liviu.apps.shopreporter.interfaces.OnAutoCompleteButtonClickListener;
import com.liviu.apps.shopreporter.interfaces.OnItemActionListener;
import com.liviu.apps.shopreporter.interfaces.SessionListener;
import com.liviu.apps.shopreporter.managers.ActivityIdProvider;
import com.liviu.apps.shopreporter.managers.TipsManager;
import com.liviu.apps.shopreporter.managers.TipsManager.TipsListener;
import com.liviu.apps.shopreporter.ui.LAutocompleteEditText;
import com.liviu.apps.shopreporter.ui.LProgressBox;
import com.liviu.apps.shopreporter.ui.LProgressBox.DismissListener;
import com.liviu.apps.shopreporter.ui.LTextView;
import com.liviu.apps.shopreporter.utils.Console;
import com.liviu.apps.shopreporter.utils.Utils;

public class ManageSessionProductsActivity extends Activity implements OnClickListener,
																	   SessionListener,
																	   TextWatcher,
																	   OnItemActionListener,
																	   OnAutoCompleteButtonClickListener,
																	   AutoCompleteLoadListener,
																	   LProgressBox.DismissListener,
																	   TipsListener,
																	   android.content.DialogInterface.OnClickListener{
	
	// Constants 
	private final 			String 	TAG 				= "ManageSessionProductsActivity";
	public  final static 	int		REQUEST_ID  		= ActivityIdProvider.getInstance().getNewId(ManageSessionProductsActivity.class);
	private final 			int 	INVALID_POSITION	= -1;
	private final 			String	barTimePattern		= "dd MMM HH:mm";
	private final 			int		VIBRATE_LENGTH		= 30;
	
	
	// Data
	private User					user;
	private Typeface				typeface;
	private Session					currentSession;
	private NewProductsAdapter 		adapter;
	private int						lastRemovedProductPosition;
	private AutocompleteAdapter		nameAdapter;
	private ArrayAdapter<String> 	priceAdapter;
	private ArrayAdapter<String> 	unitsAdapter;
	private TipsManager				tipsMan;
	private PowerManager.WakeLock	wakeLock;
	
	// UI
	private ListView				lstProducts;
	private Button					butAddProduct;
	private Button					butFinishSession;
	private Button					butPopupCancel;
	private Button					butPopupReset;
	private Button					butPopupAddIt;
	private RelativeLayout			popupLayout;
	private LAutocompleteEditText 	edtxName;
	private LAutocompleteEditText	edtxQty;
	private LAutocompleteEditText	edtxUnits;
	private LAutocompleteEditText   edtxPrice;
	private LTextView				txtPopupTotal;
	private LTextView				txtBarTitle;
	private LTextView				txtBarTime;
	private LTextView				txtTopTotal;
	private LProgressBox			progresBox;
	private LTextView				txtNoProducts;
	private SharedPreferences		prefs;
	private AlertDialog				alertDialog;
	
	// Flags
	private boolean 				isPopupOpen;
	
	// Animations
	private Animation 				fadeInAnimation;
	private Animation				fadeOutAnimation;
	
	// System services
	private	InputMethodManager 		imm;
	private Vibrator				vbb;
	private PowerManager			pm;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		Window win = getWindow();
		win.setFormat(PixelFormat.RGBA_8888);
        requestWindowFeature(Window.FEATURE_NO_TITLE);        
        setContentView(R.layout.manage_session_products_layout);   
        
        // get layout components references
        typeface			= Typeface.createFromAsset(getAssets(), "fonts/VAGRON.TTF");
        user				= User.getInstance(this);
        lstProducts			= (ListView)findViewById(R.id.new_products_list);
        butAddProduct		= (Button)findViewById(R.id.but_add);
        butFinishSession	= (Button)findViewById(R.id.but_finish);        
        popupLayout			= (RelativeLayout)findViewById(R.id.layout_overlay_holder);
        butPopupAddIt		= (Button)popupLayout.findViewById(R.id.but_add_product);
        butPopupCancel		= (Button)popupLayout.findViewById(R.id.but_add_cancel);
        butPopupReset		= (Button)popupLayout.findViewById(R.id.but_reset);
        edtxName			= (LAutocompleteEditText)popupLayout.findViewById(R.id.layout_add_name);
        edtxPrice			= (LAutocompleteEditText)popupLayout.findViewById(R.id.layout_add_price);
        edtxQty				= (LAutocompleteEditText)popupLayout.findViewById(R.id.layout_add_qty);
        edtxUnits			= (LAutocompleteEditText)popupLayout.findViewById(R.id.layout_add_units);
        txtPopupTotal		= (LTextView)popupLayout.findViewById(R.id.layout_add_product_total);
        isPopupOpen			= false;
        imm 				= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);    
        adapter				= new NewProductsAdapter(this);
		priceAdapter 		= new ArrayAdapter<String>(ManageSessionProductsActivity.this, R.layout.simple_dropdown_item_1line);
		unitsAdapter 		= new ArrayAdapter<String>(ManageSessionProductsActivity.this, R.layout.simple_dropdown_item_1line);
        nameAdapter 		= new AutocompleteAdapter(this);
        txtBarTitle			= (LTextView)findViewById(R.id.bar_title);
        txtBarTime			= (LTextView)findViewById(R.id.bar_time);
        txtTopTotal			= (LTextView)findViewById(R.id.top_total);
        fadeInAnimation 	= AnimationUtils.loadAnimation(this, R.anim.fade_in_anim);
        fadeOutAnimation 	= AnimationUtils.loadAnimation(this, R.anim.fade_out_anim);
        progresBox			= (LProgressBox)findViewById(R.id.progress_box);
        txtNoProducts		= (LTextView)findViewById(R.id.no_products);
        tipsMan				= new TipsManager(this);
        prefs				= getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        vbb					= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        alertDialog 		= new AlertDialog.Builder(this).create();
        pm					= (PowerManager)getSystemService(POWER_SERVICE);
        wakeLock			= pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "SHOP_REPORTER");        
        lastRemovedProductPosition = INVALID_POSITION;
        
        wakeLock.acquire();
        
        alertDialog.setButton(AlertDialog.BUTTON1, "Remove it", this);
        alertDialog.setButton(AlertDialog.BUTTON2, "Cancel", this);       
        
        Console.debug(TAG, "session: " + getIntent().getBundleExtra(Session.KEY_BUNDLE));
        
        if(getIntent() != null){
        	if(getIntent().getBundleExtra(Session.KEY_BUNDLE) != null){
        		currentSession = new Session(getIntent().getBundleExtra(Session.KEY_BUNDLE));        		
        	}
        	else{
        		Toast.makeText(ManageSessionProductsActivity.this, getString(R.string.error_no_session_selected), Toast.LENGTH_LONG).show();
        		finish();
        	}
        }
        else{
    		Toast.makeText(ManageSessionProductsActivity.this, getString(R.string.error_no_session_selected), Toast.LENGTH_LONG).show();
    		finish();
        }
                
        if(currentSession == null){
    		Toast.makeText(ManageSessionProductsActivity.this, getString(R.string.error_no_session_selected), Toast.LENGTH_LONG).show();
    		finish();        	
        }
        
        // set session details
        Console.debug(TAG, "start time heeeeeeeeeeeeeere is: " + currentSession.getStartTime());
        txtBarTitle.setText(currentSession.getName() + " [" + currentSession.getLocation() + "]");
        txtBarTime.setText("Started: " + Utils.formatDate(currentSession.getStartTime(), "E, dd MMM HH:mm"));
        
        // set custom font
        butAddProduct.setTypeface(typeface);
        butFinishSession.setTypeface(typeface);
        butPopupAddIt.setTypeface(typeface);
        butPopupCancel.setTypeface(typeface);
        butPopupReset.setTypeface(typeface);
        
        // set listeners
        butAddProduct.setOnClickListener(this);
        butFinishSession.setOnClickListener(this);        
        butPopupAddIt.setOnClickListener(this);
        butPopupCancel.setOnClickListener(this);
        butPopupReset.setOnClickListener(this);
        user.getShoppingManager().setSessionListener(this);        
        edtxPrice.addTextChangedListener(this);
        adapter.setOnItemActionListener(this);         
        progresBox.setOnDismissListener(this);
        progresBox.setButtonOnClickListener(this);
        tipsMan.setTipsListener(this);                
        
		fadeInAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				popupLayout.setVisibility(View.VISIBLE);
			}
		});
		
		fadeOutAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				popupLayout.setVisibility(View.GONE);
			}
		});		
        
        nameAdapter.setOnButtonClick(this); 
        
        user.getShoppingManager().setAutoCompleteLoadListener(this);
        
        // load all products for autocomplete fields
        user.getShoppingManager().getAllProducts();
        /*
	        tipsMan.addTip(new Tip(-1, "testing tip 1", "testing tip 1 content", Tip.CATEGORY_FUNNY), true);
	        tipsMan.addTip(new Tip(-1, "testing tip 2", "testing tip 2 content", Tip.CATEGORY_FUNNY), true);
	        tipsMan.addTip(new Tip(-1, "testing tip 3", "testing tip 3 content", Tip.CATEGORY_FUNNY), true);
	        tipsMan.addTip(new Tip(-1, "testing tip 4", "testing tip 4 content", Tip.CATEGORY_FUNNY), true);
        */                
        tipsMan.loadTipsByCategory(Tip.CATEGORY_FUNNY);
                
    }
    // interfaces   
	@Override   
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loading_button:
			progresBox.dismiss();
			break;
		case R.id.but_finish:
			Intent toSessionDetails = new Intent(ManageSessionProductsActivity.this, SessionInfoActivity.class);
			Session tempSession = currentSession;
			tempSession.setEndTime(Utils.now());
			tempSession.setTotalTime(tempSession.getTotalTime() + (tempSession.getEndTime() - tempSession.getLastStartedTime()));			
			tempSession.setTotalProducts(adapter.getCount());
			tempSession.setUserId(user.getId());
			toSessionDetails.putExtra(Session.KEY_BUNDLE, tempSession.toBundle());			
			startActivity(toSessionDetails);					
			// user.getShoppingManager().finishSession(currentSession);
			// currentSession.setIsClosed(true);
			break;
		case R.id.but_add_product:
				if(edtxName.getText().length() == 0 || edtxPrice.getText().length() == 0 ||
				   edtxQty.getText().length()  == 0 || edtxUnits.getText().length() == 0){
					Toast.makeText(ManageSessionProductsActivity.this, "All fields are required!", Toast.LENGTH_LONG).show();					
				}
				else{
					Product newProduct = new Product();
					newProduct.setAddedTime(Utils.now());
					newProduct.setName(edtxName.getText().toString());
					try{
						newProduct.setPrice(Double.parseDouble(edtxPrice.getText().toString()));
					} catch (NumberFormatException e) {
						e.printStackTrace();
						Toast.makeText(ManageSessionProductsActivity.this, "Please type a valid price", Toast.LENGTH_LONG).show();
						break;
					}
					
					try{
						newProduct.setQuantity(Double.parseDouble(edtxQty.getText().toString()));
					}catch (NumberFormatException e) {
						e.printStackTrace();
						Toast.makeText(ManageSessionProductsActivity.this, "Please type a valid quantity", Toast.LENGTH_LONG).show();
						break;
					}
					
					try{
						newProduct.setUnit(edtxUnits.getText().toString());
					}catch (InvalidUnitException e) {
						e.printStackTrace();
						Toast.makeText(ManageSessionProductsActivity.this, "Please type a valid unit(kg, litre, bottle...", Toast.LENGTH_LONG).show();
						break;
					}
					
					newProduct.setSessionId(currentSession.getId());
					Console.debug(TAG, "before adding new product: " + currentSession.getTotalProducts(false));
					user.getShoppingManager().addProductToSession(currentSession, newProduct);
					
					resetPopupFields();
					hidePopup();
				}
				
			break;
		case R.id.but_add:
			if(progresBox.isOpen())
				progresBox.dismiss();
			
			showPopup();
			break;
		case R.id.but_add_cancel:
			hidePopup();
			break;
		case R.id.but_reset:
			resetPopupFields();
			break;
		default:
			break;
		}
	}	
	
	// Session's interfaces
	@Override
	public void onSessionCreated(boolean isSuccess, Session newSession) {	
	}	
	
	@Override
	public void onSessionLoaded(boolean isSuccess, Session pSession) {
		Console.debug(TAG, "[onSessionLoaded] isSucces: " + isSuccess + " session: " + pSession);			
		
		if(isSuccess){
			if(adapter.getCount() == 0){
				Console.debug(TAG, "Set the new adapter for listview");
				lstProducts.setAdapter(adapter);
			}
			else{
				adapter.clear();
				adapter.notifyDataSetChanged();
			}
			
			currentSession = pSession;			
			for(int i = 0; i < currentSession.getProducts().size(); i++){
				adapter.add(currentSession.getProducts().get(i));			
			}					
			
			adapter.notifyDataSetChanged();						
			
			txtBarTime.setText("Started: " + Utils.formatDate(currentSession.getStartTime(), barTimePattern));
			txtBarTitle.setText(currentSession.getName() + " [" + currentSession.getLocation() + "]");			
			updateTotalPrice();		
			
			if(adapter.getCount() == 0){
				txtNoProducts.setVisibility(View.VISIBLE);
				progresBox.dismiss();
			}
			else{
				txtNoProducts.setVisibility(View.GONE);
				progresBox.notifyDismissPossible(Integer.toString(currentSession.getTotalProducts(false)) + (currentSession.getTotalProducts(false) == 1 ? " product" : " products") + " loaded");
			}
		}
		else{
			if(adapter.getCount() == 0)
				txtNoProducts.setVisibility(View.VISIBLE);
			else
				txtNoProducts.setVisibility(View.GONE);
		}
	}	
	
	@Override
	public void onProductAddedToSession(boolean isSuccess, 
										Session session,
										Product addedProduct) {
		Console.debug(TAG, "onProductAddedToSession is_success: " + isSuccess + " session: " + session + " addedProduct: " + addedProduct);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);		
		imm.hideSoftInputFromWindow(edtxName.getWindowToken(), 0);
		currentSession.add(addedProduct);
		Console.debug(TAG, "after adding new product: " + currentSession.getTotalProducts(false));		
		if(isSuccess){
			if(adapter.getCount() == 0){
				Console.debug(TAG, "set adapter");
				lstProducts.setAdapter(adapter);
			}
			
			adapter.add(addedProduct);			
			adapter.notifyDataSetChanged();
			nameAdapter.add(addedProduct);
			
			// update autocomplete adapters
			priceAdapter.add(Utils.roundTwoDecimals(addedProduct.getPrice()));						
			unitsAdapter.add(addedProduct.getUnit());
			
			updateTotalPrice();
			txtNoProducts.setVisibility(View.GONE);
			vbb.vibrate(VIBRATE_LENGTH);
		}				
	}		
	
	@Override
	public void onProductRemovedFromSession(boolean isSucces, Session session, Product removedProduct) {
		Console.debug(TAG, "onProductRemoved: " + isSucces + " session: " + session + " removedProduct: " + removedProduct);
		if(lastRemovedProductPosition != INVALID_POSITION){
			adapter.removeItemAt(lastRemovedProductPosition);
			adapter.notifyDataSetChanged();
			lastRemovedProductPosition = INVALID_POSITION;
			
			currentSession.remove(removedProduct);
			vbb.vibrate(VIBRATE_LENGTH);

			// update total
			updateTotalPrice();
			
			if(adapter.getCount() == 0)
				txtNoProducts.setVisibility(View.VISIBLE);
		}				
	}
	
	private void resetPopupFields(){		
		edtxName.setText("");
		edtxPrice.setText("");
		edtxQty.setText("");
		edtxUnits.setText("");
		
		if(edtxName.requestFocus())
			imm.showSoftInput(edtxName, InputMethodManager.SHOW_IMPLICIT);
	}
	
	// View interfaces		
	@Override
	public void afterTextChanged(Editable s) {
		if(edtxQty.getText().length() > 0 && edtxPrice.getText().length() > 0){
			double price = 0.00;
			double qty	 = 0.00;
			
			try{
				price = Double.parseDouble(edtxPrice.getText().toString());
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			
			try{
				qty = Double.parseDouble(edtxQty.getText().toString());
			}catch (NumberFormatException e) {
				e.printStackTrace();
				return;
			}
			
			txtPopupTotal.setText("Total: $" + Utils.roundTwoDecimals(price * qty));					
		}		
	}
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}	
	
	private void showPopup(){
		resetPopupFields();		
		
		popupLayout.startAnimation(fadeInAnimation);
		isPopupOpen = true;
	}
	
	private void hidePopup(){
		popupLayout.startAnimation(fadeOutAnimation);
		imm.hideSoftInputFromWindow(edtxName.getWindowToken(), 0);
		isPopupOpen = false;
	} 		
	
	private boolean isPopupOpen(){
		return isPopupOpen;
	}
	
	@Override
	public void onRemoveItem(int pAdapterPosition) {
		Console.debug(TAG, "adapterPosition: " + pAdapterPosition);		
		if(pAdapterPosition != INVALID_POSITION){
			lastRemovedProductPosition = pAdapterPosition;
			alertDialog.setMessage("Do you want to remove the product \"" + adapter.getItem(pAdapterPosition).getName() + "\" from products list?");
			alertDialog.show();
			vbb.vibrate(VIBRATE_LENGTH + 200);
		}
	}

	@Override
	public void onClick(View view, int position) {
		Product selectedProduct = nameAdapter.getFullItem(position);
		if(selectedProduct != null){
			edtxName.setText(selectedProduct.getName());
			edtxPrice.setText(Utils.roundTwoDecimals(selectedProduct.getPrice()));
			edtxQty.setText(Double.toString(selectedProduct.getQuantity()));
			edtxUnits.setText(selectedProduct.getUnit());
		}
		else{
			// nothing yet
		}
	}
	
	@Override
	public void onAutoCompleteDataLoaded(boolean isSuccess, ArrayList<Product> products) {
		Console.debug(TAG, "onAutoCompleteDataLoaded: " + isSuccess + " " + products);
		if(isSuccess && products != null){
			ArrayList<Double> prices = new ArrayList<Double>(){
				@Override
				public boolean contains(Object object) {
					if(object instanceof Double){
						Double d = (Double)object;
						for(int i = 0; i < this.size(); i++)
							if(d == this.get(i))
								return true;
					}
					
					return false;
				}
			};
			
			ArrayList<String> units	= new ArrayList<String>(){
				@Override
				public boolean contains(Object object) {
					if(object instanceof String){
						String s = (String)object;
						for(int i = 0; i < this.size(); i++){
							if(s.compareToIgnoreCase(this.get(i)) == 0)
								return true;
						}
					}
					return false;
				}
			};	
			 
			
			Product  product 	  = null;
			for(int i = 0; i < products.size(); i++){
				product = products.get(i);
				
				nameAdapter.add(product);				
				if(!prices.contains(product.getPrice())){
					prices.add(product.getPrice());
					priceAdapter.add(Utils.roundTwoDecimals(product.getPrice()));
				}
				
				if(!units.contains(product.getUnit())){
					units.add(product.getUnit());
					unitsAdapter.add(product.getUnit());
				}
			}
			
			nameAdapter.commit();
			edtxName.setAdapter(nameAdapter);
			edtxPrice.setAdapter(priceAdapter);
			edtxUnits.setAdapter(unitsAdapter);
			
			edtxName.setThreshold(1);
			edtxPrice.setThreshold(1);
			edtxUnits.setThreshold(1);
		}		                    
	}
	
	private void updateTotalPrice(){
		txtTopTotal.setText("Total: $" + Utils.roundTwoDecimals(currentSession.getTotalMoney()));		
	}
	
	@Override
	protected void onPause() {
		wakeLock.release();
		if(alertDialog.isShowing())
			alertDialog.dismiss();
		Console.debug(TAG, "is currentSession paused: " + currentSession.isPaused());		
		currentSession.setUserId(user.getId());
		currentSession.setIsPausedValue(true);
		currentSession.setEndTime(Utils.now());
		Session tempSession = currentSession;
		tempSession.setTotalTime(tempSession.getTotalTime() + (tempSession.getEndTime() - tempSession.getLastStartedTime()));
		user.getShoppingManager().pauseSession(tempSession); 
		Console.debug(TAG, "is currentSession paused: " + tempSession.isPaused());
		Console.debug(TAG, "ON PAUSE");
		Tip tipToSave = tipsMan.getTipsProvider().requestTip();
		if(tipToSave != null){
			String jsonString = (tipToSave.toJSON() == null ? null : tipToSave.toJSON().toString());
			if(jsonString != null){
				SharedPreferences.Editor ed = prefs.edit();			
				ed.putString(Tip.BUNDLE_KEY, jsonString);
				ed.commit();
			}			
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		Console.debug(TAG, "ON RESUME");		
		super.onResume();
		wakeLock.acquire();
		if(user != null){
			if(user.isLoggedIn()){
				Session tempSession = user.getPausedSession();
				Console.debug(TAG, "tempSession: " + tempSession);
				if(tempSession != null){
					currentSession = tempSession;
					if(currentSession.getTotalProducts(false) > 0)
						progresBox.setTitle("Loading session " + currentSession.getName() + " with " + currentSession.getTotalProducts(false) + " products...");
					else
						progresBox.setTitle("Loading session " + currentSession.getName() + "...");

					lstProducts.setVisibility(View.GONE);
					tipsMan.loadTipsByCategory(Tip.CATEGORY_FUNNY);
					String jsonTipString = prefs.getString(Tip.BUNDLE_KEY, null);
					Console.debug(TAG, "resume jsonTipString is: " + jsonTipString);
					
					if(jsonTipString != null){
						try {
							Tip tip = Tip.create(new JSONObject(jsonTipString));
							progresBox.show(tip);	
						}
						catch (JSONException e) {
							e.printStackTrace();
						}						
					}
					else
						progresBox.show();
					user.getShoppingManager().setSessionListener(ManageSessionProductsActivity.this);
					user.getShoppingManager().resumeSession(currentSession.getId());				
				}
				else{
					if(currentSession != null){ 				
						if(currentSession.isClosed()){
							Toast.makeText(ManageSessionProductsActivity.this, "You do not have any paused session.", Toast.LENGTH_LONG).show();
							Intent toHome = new Intent(ManageSessionProductsActivity.this, MainActivity.class);					
							startActivity(toHome);
							finish();					
						}
						else{
							adapter.clear();
							adapter.notifyDataSetChanged();

							// We have a new - empty - session
							txtNoProducts.setVisibility(View.VISIBLE);
						}						
					}				
					else{
						Toast.makeText(ManageSessionProductsActivity.this, "You do nots have any paused session.", Toast.LENGTH_LONG).show();
						Intent toHome = new Intent(ManageSessionProductsActivity.this, MainActivity.class);					
						startActivity(toHome);
						finish();					
					}
				}								
				return;
			}
		}		
		
		Toast.makeText(ManageSessionProductsActivity.this, "Please login in first", Toast.LENGTH_LONG).show();
		Intent toLogin = new Intent(ManageSessionProductsActivity.this, LoginActivity.class);
		toLogin.putExtra(LoginActivity.KEY_PARENT_ID, ManageSessionProductsActivity.REQUEST_ID);
		startActivity(toLogin);
		finish();
		
	}
	@Override
	public void onDismiss(LProgressBox pProgressBox) {
		
		// Show product list
		lstProducts.setVisibility(View.VISIBLE);
	}		
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(progresBox.isOpen()){
				progresBox.dismiss();
				return true;
			}
			else if(isPopupOpen){
				hidePopup();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onTipsLoaded(boolean pIsSuccess, ArrayList<Tip> pTipsList) {
		Console.debug(TAG, "pIsSuccess: " + pIsSuccess + " " + (pTipsList == null ? " null " : pTipsList.size()));
		
		// connect view with the data source
        progresBox.setTipsProvider(tipsMan.getTipsProvider());
	}
	@Override
	public void onTipAdded(boolean isSuccess, Tip pAddedTip) {
	}
	@Override
	public void onTipRemoved(boolean isSuccess, Tip pRemovedTip) {
	}
	
	@Override
	public void onUserSessionsLoaded(boolean isSuccess, ArrayList<Session> pLoadedSessions) {
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
				
		switch (which) {
			case AlertDialog.BUTTON1:
				Product productToRemove = adapter.getItem(lastRemovedProductPosition);
				user.getShoppingManager().removeProduct(currentSession, productToRemove);
				alertDialog.dismiss();
				break;
			case AlertDialog.BUTTON2:
				alertDialog.dismiss();
				break;
			default:
				break;
		}
	}
	
	@Override
	public void onCommonProductsLoaded(boolean isSuccess, ArrayList<Product> pCommonProducts) {	
		// nothing for the moment
	}
}  
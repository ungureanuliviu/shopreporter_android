package com.liviu.apps.shopreporter;

 
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.liviu.apps.shopreporter.adapters.AutocompleteAdapter;
import com.liviu.apps.shopreporter.adapters.NewProductsAdapter;
import com.liviu.apps.shopreporter.data.Product;
import com.liviu.apps.shopreporter.data.Session;
import com.liviu.apps.shopreporter.data.User;
import com.liviu.apps.shopreporter.exceptions.InvalidUnitException;
import com.liviu.apps.shopreporter.interfaces.AutoCompleteLoadListener;
import com.liviu.apps.shopreporter.interfaces.OnAutoCompleteButtonClickListener;
import com.liviu.apps.shopreporter.interfaces.OnItemActionListener;
import com.liviu.apps.shopreporter.interfaces.SessionListener;
import com.liviu.apps.shopreporter.managers.ActivityIdProvider;
import com.liviu.apps.shopreporter.ui.LAutocompleteEditText;
import com.liviu.apps.shopreporter.ui.LTextView;
import com.liviu.apps.shopreporter.utils.Console;
import com.liviu.apps.shopreporter.utils.Utils;

public class ManageSessionProductsActivity extends Activity implements OnClickListener,
																	   SessionListener,
																	   TextWatcher,
																	   OnItemActionListener,
																	   OnAutoCompleteButtonClickListener,
																	   AutoCompleteLoadListener{
	
	// Constants 
	private final 			String 	TAG 				= "ManageSessionProductsActivity";
	public  final static 	int		REQUEST_ID  		= ActivityIdProvider.getInstance().getNewId(ManageSessionProductsActivity.class);
	private final 			int 	INVALID_POSITION	= -1;
	
	
	// Data
	private User					user;
	private Typeface				typeface;
	private Session					currentSession;
	private NewProductsAdapter 		adapter;
	private int						lastRemovedProductPosition;
	private AutocompleteAdapter		nameAdapter;
	private ArrayAdapter<String> 	priceAdapter;
	private ArrayAdapter<String> 	unitsAdapter;
	
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
	
	// Flags
	private boolean 				isPopupOpen;
	
	// System services
	InputMethodManager 				imm;
	
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
        lastRemovedProductPosition = INVALID_POSITION;
        
        Console.debug(TAG, "session: " + getIntent().getBundleExtra(Session.KEY_BUNDLE));
        
        if(getIntent() != null){
        	if(getIntent().getBundleExtra(Session.KEY_BUNDLE) != null){
        		currentSession = new Session(getIntent().getBundleExtra(Session.KEY_BUNDLE));
        		Console.debug(TAG, "currentSession"  + currentSession); 
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
        
        Console.debug(TAG, "currentSession2"  + currentSession); 
        if(currentSession == null){
    		Toast.makeText(ManageSessionProductsActivity.this, getString(R.string.error_no_session_selected), Toast.LENGTH_LONG).show();
    		finish();        	
        }
        
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
        
        nameAdapter.add(new Product());
        nameAdapter.add(new Product());
        nameAdapter.add(new Product());
        nameAdapter.add(new Product());
        nameAdapter.add(new Product());
        
        nameAdapter.setOnButtonClick(this); 
        
        user.getShoppingManager().setAutoCompleateLoadListener(this);
        
        // load all products for autocomplete fields
        user.getShoppingManager().getAllProducts();
        
    }
    // interfaces   
	@Override   
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.but_add_product:
				if(edtxName.getText().length() == 0 || edtxPrice.getText().length() == 0 ||
				   edtxQty.getText().length() == 0  || edtxUnits.getText().length() == 0){
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
					user.getShoppingManager().addProductToSession(currentSession, newProduct);
					resetPopupFields();
					hidePopup();
				}
				
			break;
		case R.id.but_add:
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
	public void onProductAddedToSession(boolean isSuccess, 
										Session session,
										Product addedProduct) {
		Console.debug(TAG, "onProductAddedToSession is_success: " + isSuccess + " session: " + session + " addedProduct: " + addedProduct);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);		
		imm.hideSoftInputFromWindow(edtxName.getWindowToken(), 0);
		
		if(isSuccess){
			if(adapter.getCount() == 0)
				lstProducts.setAdapter(adapter);
			
			adapter.add(addedProduct);			
			adapter.notifyDataSetChanged();
			nameAdapter.add(addedProduct);
			priceAdapter.add(Utils.roundTwoDecimals(addedProduct.getPrice()));
			unitsAdapter.add(addedProduct.getUnit());
		}
	}		
	
	private void resetPopupFields(){
		/*
		edtxName.setText("");
		edtxPrice.setText("");
		edtxQty.setText("");
		edtxUnits.setText("");
		*/
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
		popupLayout.setVisibility(View.VISIBLE);
		isPopupOpen = true;
	}
	
	private void hidePopup(){
		popupLayout.setVisibility(View.GONE);
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
			Product productToRemove = adapter.getItem(pAdapterPosition);
			user.getShoppingManager().removeProduct(currentSession, productToRemove);
		}
	}
	@Override
	public void onProductRemovedFromSession(boolean isSucces, Session session, Product removedProduct) {
		Console.debug(TAG, "onProductRemoved: " + isSucces + " session: " + session + " removedProduct: " + removedProduct);
		if(lastRemovedProductPosition != INVALID_POSITION){
			adapter.removeItemAt(lastRemovedProductPosition);
			adapter.notifyDataSetChanged();
			lastRemovedProductPosition = INVALID_POSITION;
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
			ArrayList<Double> 	 prices 	  = new ArrayList<Double>();
			ArrayList<String>	 units		  = new ArrayList<String>();	
			 
			
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
			
			edtxName.setThreshold(2);
			edtxPrice.setThreshold(2);
			edtxUnits.setThreshold(2);
		}		                    
	}
}  
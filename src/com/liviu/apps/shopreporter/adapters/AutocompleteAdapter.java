package com.liviu.apps.shopreporter.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.liviu.apps.shopreporter.R;
import com.liviu.apps.shopreporter.data.Product;
import com.liviu.apps.shopreporter.data.SuggestionsFilter;
import com.liviu.apps.shopreporter.interfaces.OnAutoCompleteButtonClickListener;
import com.liviu.apps.shopreporter.utils.Console;
import com.liviu.apps.shopreporter.utils.Utils;

public class AutocompleteAdapter extends BaseAdapter implements Filterable,
																OnClickListener{

	// Constants
	private final String TAG = "AutocompletAdapter";
	
	// Data
	private LayoutInflater 				mInflater;
	private ArrayList<Product> 			mItems;
	private Context 					mContext;
	private SuggestionsFilter			mFilter;
	private HashMap<String, Product> 	mOriginalProducts;	
	private	SimpleDateFormat			mFormatter;
	
	// Listener
	private OnAutoCompleteButtonClickListener		mOnButtonClick;
	
	public AutocompleteAdapter(Context pContext){

		mContext 	= pContext;
		mInflater 	= (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems		= new ArrayList<Product>();
		mFilter 	= new SuggestionsFilter(this);
		mFormatter 	= new SimpleDateFormat("E, dd MMM yyyy");		
		mOriginalProducts = new HashMap<String, Product>();				
	}
	
	public void add(Product pItem){
		mItems.add(pItem);
		mOriginalProducts.put(pItem.getName(), pItem);		
	}
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public String getItem(int position) {
		Product found = null;
		
		try{
			found = mItems.get(position);
		}catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			Console.debug(TAG, "Cannot find an item at position: " + position + " in autcomplete adapter");
		}
		
		return found.getName();		
	}

	public Product getFullItem(int position) {
		Product found = null;
		
		try{
			found = mItems.get(position);
		}catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			Console.debug(TAG, "Cannot find an item at position: " + position + " in autcomplete adapter");
		}
		
		return found;	
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 	ViewHolder vh;
		 	
	        if (convertView == null) {	        	
	            convertView = mInflater.inflate(R.layout.autocomplete_item_layout, parent, false);
	            vh 				= new ViewHolder();
	            vh.ac_date		= (TextView) convertView.findViewById(R.id.ac_date);
	            vh.ac_name		= (TextView) convertView.findViewById(R.id.ac_name);
	            vh.ac_fill_all 	= (Button)convertView.findViewById(R.id.ac_but_fill_all);
	            vh.ac_price    	= (TextView)convertView.findViewById(R.id.ac_price);
	            
		        if(mOnButtonClick != null)
		        	vh.ac_fill_all.setOnClickListener(this);
		        convertView.setTag(vh);
	        }	      
	        else
	        	vh = (ViewHolder)convertView.getTag();
	        
	        vh.ac_name.setText(mItems.get(position).getName());
	        vh.ac_date.setText("Date: " + mFormatter.format(new Date(mItems.get(position).getAddedTime())));
	        vh.ac_price.setText("Price: $" + Utils.roundTwoDecimals(mItems.get(position).getPrice()));
	        vh.ac_fill_all.setTag(position);

	        return convertView;
	}
	
	public AutocompleteAdapter setOnButtonClick(OnAutoCompleteButtonClickListener listener){
		mOnButtonClick = listener;
		return this;
	}

	@Override
	public Filter getFilter() {
		return mFilter;
	}
	
	public AutocompleteAdapter setItems(ArrayList<Product> pItems){
		mItems = pItems;
		return this;
	}
	
	private class ViewHolder{
		public TextView	ac_name;
		public TextView ac_date;
		public TextView ac_price;
		public Button	ac_fill_all;
	}

	@Override
	public void onClick(View v) {
		if(mOnButtonClick != null){
			Integer intObj = (Integer)v.getTag();
			if(intObj != null){
				mOnButtonClick.onClick(v, intObj.intValue());
			}
		}
	}

	public void commit() {
		mFilter.setSearchableItems(mOriginalProducts);		
	}
}

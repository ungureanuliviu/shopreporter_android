package com.liviu.apps.shopreporter.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.liviu.apps.shopreporter.adapters.AutocompleteAdapter;
import com.liviu.apps.shopreporter.utils.Console;

import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;

public class SuggestionsFilter extends Filter{

	// Constants
	private final String TAG = "SuggestionsFilter";
	
	// Data
	private AutocompleteAdapter			mAdapter;
	private HashMap<String, Product> 	mSearchableItems;
	
	public SuggestionsFilter(AutocompleteAdapter adapter) {
		mAdapter = adapter;
	}	
	
	public SuggestionsFilter setSearchableItems(HashMap<String, Product> pItems){
		mSearchableItems = pItems;
		return this;
	}
	
	@Override
	protected FilterResults performFiltering(CharSequence constraint) {		
		Console.debug(TAG, "[peformFilteriong] constraint:" + constraint);
		FilterResults 		finalResults 	= new FilterResults();
		ArrayList<Product>	results 		= new ArrayList<Product>();
		
		if(mSearchableItems == null){
			finalResults.values = results;
			return finalResults;
		}
		
		if(constraint != null){
			Set<String> keySet 			= mSearchableItems.keySet();
			Iterator<String> iterator 	= keySet.iterator();
			
			while(iterator.hasNext()){
				String key =iterator.next();
				if(key.contains(constraint))
					results.add(mSearchableItems.get(key));
			}
			
			finalResults.values = results;
		}
		return finalResults;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {		
		Console.debug(TAG, "mAdapter: " + mAdapter + " constraint: " + constraint + " results: " + results); 
		if(mAdapter != null){
			if(results != null){
				if(results.values != null){
					mAdapter.setItems((ArrayList<Product>) results.values).notifyDataSetChanged();
				}
			}
		}
	}

}

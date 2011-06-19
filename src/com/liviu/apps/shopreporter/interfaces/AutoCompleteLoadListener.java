package com.liviu.apps.shopreporter.interfaces;

import java.util.ArrayList;

import com.liviu.apps.shopreporter.data.Product;

public interface AutoCompleteLoadListener {
	public void onAutoCompleteDataLoaded(boolean isSuccess, ArrayList<Product> products);
}

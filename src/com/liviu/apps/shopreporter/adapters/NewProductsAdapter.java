package com.liviu.apps.shopreporter.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.liviu.apps.shopreporter.R;
import com.liviu.apps.shopreporter.data.Product;
import com.liviu.apps.shopreporter.interfaces.OnItemActionListener;
import com.liviu.apps.shopreporter.ui.LTextView;
import com.liviu.apps.shopreporter.utils.Console;
import com.liviu.apps.shopreporter.utils.Utils;

public class NewProductsAdapter extends BaseAdapter{
	
	// Constants
	private final String TAG = "NewProductsAdapter";
	
	// Data
	private ArrayList<Product> 	mItems;
	private Context 			mContext;
	private LayoutInflater		mInflater;
	private Typeface			mTypeface;
	
	// Listeners
	private OnItemActionListener mItemActionListener;
	
	public NewProductsAdapter(Context ctx) {
		mContext 	= ctx;
		mItems 		= new ArrayList<Product>();
		mInflater	= (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTypeface	= Typeface.createFromAsset(mContext.getAssets(), "fonts/VAGROUN.TTF");
	}
	
	public NewProductsAdapter add(Product pProduct){
		mItems.add(pProduct);
		return this;
	}
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Product getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if(convertView == null){
			convertView 	= (RelativeLayout)mInflater.inflate(com.liviu.apps.shopreporter.R.layout.new_product_list_item_layout, parent, false);
			vh				= new ViewHolder();
			vh.txt_title	= (LTextView)convertView.findViewById(R.id.item_title);
			vh.txt_total 	= (LTextView)convertView.findViewById(R.id.item_total);
			vh.but_remove	= (ImageButton)convertView.findViewById(R.id.item_but_remove);
			vh.layout_holder= (RelativeLayout)convertView.findViewById(R.id.item_layout);								
			
			vh.but_remove.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					Console.debug(TAG, "on click");
					int position = -1;
					try{
						position = (Integer)v.getTag();
					}
					catch (ClassCastException e) {
						e.printStackTrace();
						position = -1;
					}
					
					Console.debug(TAG, "remove from position: " + position);
					if(position != -1){
						if(mItemActionListener != null){
							mItemActionListener.onRemoveItem(position);
						}
						else
							Console.debug(TAG, "No onItemActionListener");
					}					
				}
			});
			convertView.setTag(vh);
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		vh.but_remove.setTag(position);
		vh.txt_title.setText(mItems.get(position).getName());
		vh.txt_total.setText(Double.toString(mItems.get(position).getQuantity()) + mItems.get(position).getUnit() + " x $" + Double.toString(mItems.get(position).getPrice()) + " = $" + Utils.roundTwoDecimals(mItems.get(position).getQuantity() * mItems.get(position).getPrice()));
		
		/*
		if(position % 2 == 0)
			vh.layout_holder.setBackgroundResource(R.drawable.bg_item_list_gray);
		else
			vh.layout_holder.setBackgroundResource(R.drawable.bg_item_list_white);
		*/
		
		return convertView;
	}
	
	private class ViewHolder{		
		public LTextView 		txt_title;
		public LTextView		txt_total;
		public ImageButton		but_remove;
		public RelativeLayout	layout_holder;
	}

	public NewProductsAdapter setOnItemActionListener(OnItemActionListener listener){
		mItemActionListener = listener;
		return this;
	}

	public void removeItemAt(int index) {
		try{
			mItems.remove(index);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}		
	}

	public void clear() {
		mItems.clear();
	}
	
}

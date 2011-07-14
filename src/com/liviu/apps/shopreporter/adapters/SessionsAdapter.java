package com.liviu.apps.shopreporter.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.liviu.apps.shopreporter.R;
import com.liviu.apps.shopreporter.data.Session;
import com.liviu.apps.shopreporter.ui.LTextView;
import com.liviu.apps.shopreporter.utils.Console;
import com.liviu.apps.shopreporter.utils.Utils;

public class SessionsAdapter extends BaseAdapter {
	
	// Constants
	private final String 	TAG = "SessionsAdapter";
	
	// Data
	private ArrayList<Session> 	mItems;
	private Context				mContext;
	private LayoutInflater		mLf;
	
	public SessionsAdapter(Context pContext) {
		mContext 	= pContext;
		mItems		= new ArrayList<Session>();
		mLf			= (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public SessionsAdapter add(Session pSession){
		mItems.add(pSession);
		return this;
	}
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Session getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public boolean areAllItemsEnabled() {
	    return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		
		if(convertView == null){
			convertView = (RelativeLayout)mLf.inflate( R.layout.session_item_layout, parent, false);
			Console.debug(TAG, "convertView = " + convertView);
			vh 			= new ViewHolder();
				 
			vh.layout		= (RelativeLayout)convertView.findViewById(R.id.item_layout);
			vh.txtDetails	= (LTextView)convertView.findViewById(R.id.s_details);
			vh.txtName		= (LTextView)convertView.findViewById(R.id.s_name);
			vh.txtTotal		= (LTextView)convertView.findViewById(R.id.s_money);
		
			convertView.setTag(vh);
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		vh.txtName.setText(mItems.get(position).getName());
		vh.txtDetails.setText("on " + mItems.get(position).getLocation() + ", " + Utils.formatDate(mItems.get(position).getStartTime(), "dd MMM yyyy"));
		vh.txtTotal.setText("$" + Utils.roundTwoDecimals(mItems.get(position).getTotalMoney()));
		
		return convertView;
	}
  
	private class ViewHolder{
		public LTextView	txtName;
		public LTextView	txtDetails;
		public LTextView	txtTotal;
		public RelativeLayout layout;
		
		public ViewHolder() {}
	}	
}

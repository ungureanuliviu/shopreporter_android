package com.liviu.apps.shopreporter.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.liviu.apps.shopreporter.R;
import com.liviu.apps.shopreporter.data.CompareInfo;
import com.liviu.apps.shopreporter.ui.LTextView;
import com.liviu.apps.shopreporter.utils.Console;

public class CompareAdapter extends BaseAdapter{

	// Constants
	private final String TAG = "CompareAdapter";
	
	// Data
	private ArrayList<CompareInfo> 	mItems;
	private LayoutInflater			mLf;
	private Context 				mContext;
	
	public CompareAdapter(Context pContext) {
		mContext 	= pContext;
		mLf  		= (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems		= new ArrayList<CompareInfo>();		
	}
	
	public CompareAdapter add(CompareInfo pItem){
		mItems.add(pItem);
		return this;
	}
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public CompareInfo getItem(int pPosition) {
		return mItems.get(pPosition);
	}

	@Override
	public long getItemId(int pPosition) {
		return pPosition;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder vh;
		
		if(convertView == null){
			convertView = mLf.inflate(R.layout.compare_item, parent, false);
			vh			= new ViewHolder();
			
			vh.txtAnswerLeft	= (LTextView)convertView.findViewById(R.id.session_comp_answer_left);
			vh.txtAnswerRight	= (LTextView)convertView.findViewById(R.id.session_comp_answer_right);
			vh.txtDetailsLeft	= (LTextView)convertView.findViewById(R.id.session_details_left);
			vh.txtDetailsRight	= (LTextView)convertView.findViewById(R.id.session_details_right);
			vh.txtQuestion		= (LTextView)convertView.findViewById(R.id.session_question);
			vh.viewSeparator	= (View)convertView.findViewById(R.id.border_center);
			
			convertView.setTag(vh);			
		}
		else
			vh = (ViewHolder)convertView.getTag();
	
		Console.debug(TAG, mItems.get(position).toString());
		
		if(mItems.get(position).getQuestion().contains("common")){			
			AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams)convertView.getLayoutParams();
			layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
			convertView.setLayoutParams(layoutParams);
			vh.txtAnswerRight.setVisibility(View.GONE);
			vh.viewSeparator.setVisibility(View.GONE);
			RelativeLayout.LayoutParams vhLeftParams = (RelativeLayout.LayoutParams)vh.txtAnswerLeft.getLayoutParams();
			vhLeftParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);			
			vh.txtAnswerLeft.setLayoutParams(vhLeftParams);
			vh.txtAnswerLeft.setGravity(Gravity.CENTER);
		}
		else if(vh.txtAnswerRight.getVisibility() == View.GONE){
			AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams)convertView.getLayoutParams();
			layoutParams.height = 95;
			convertView.setLayoutParams(layoutParams);
			
			vh.txtAnswerRight.setVisibility(View.VISIBLE);
			vh.viewSeparator.setVisibility(View.VISIBLE);
			
			RelativeLayout.LayoutParams vhLeftParams = (RelativeLayout.LayoutParams)vh.txtAnswerLeft.getLayoutParams();
			vhLeftParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			
			vh.txtAnswerLeft.setLayoutParams(vhLeftParams);			
			vh.txtAnswerLeft.setGravity(Gravity.CENTER_VERTICAL);
		}
		
		// update text
		vh.txtQuestion.setText(mItems.get(position).getQuestion());
		vh.txtAnswerLeft.setText(mItems.get(position).getAnswerLeft());
		vh.txtAnswerRight.setText(mItems.get(position).getAnswerRight());
		
		if(mItems.get(position).getCorrectAnswer() == CompareInfo.ANSWER_LEFT){
			vh.txtAnswerLeft.setTextColor(Color.parseColor("#3fc206"));
			vh.txtDetailsLeft.setText(mItems.get(position).getDetails());
			vh.txtDetailsLeft.setVisibility(View.VISIBLE);	
			
			vh.txtAnswerRight.setTextColor(Color.parseColor("#aaaaaa"));
			vh.txtDetailsRight.setVisibility(View.GONE);
		}
		else if(mItems.get(position).getCorrectAnswer() == CompareInfo.ANSWER_RIGHT){
			vh.txtAnswerRight.setTextColor(Color.parseColor("#3fc206"));
			vh.txtDetailsRight.setText(mItems.get(position).getDetails());
			vh.txtDetailsRight.setVisibility(View.VISIBLE);
			
			vh.txtAnswerLeft.setTextColor(Color.parseColor("#aaaaaa"));
			vh.txtDetailsLeft.setVisibility(View.GONE);			
		}
		else if(mItems.get(position).getCorrectAnswer() == CompareInfo.ANSWER_NONE){			
			vh.txtAnswerRight.setTextColor(Color.parseColor("#aaaaaa"));			
			vh.txtAnswerLeft.setTextColor(Color.parseColor("#aaaaaa"));
			vh.txtDetailsRight.setVisibility(View.GONE);
			vh.txtDetailsLeft.setVisibility(View.GONE);		
		}
		else if(mItems.get(position).getCorrectAnswer() == CompareInfo.ANSWER_BOTH){			
			vh.txtAnswerRight.setTextColor(Color.parseColor("#3fc206"));			
			vh.txtAnswerLeft.setTextColor(Color.parseColor("#3fc206"));
			vh.txtDetailsRight.setVisibility(View.GONE);
			vh.txtDetailsLeft.setVisibility(View.GONE);		
		}				
		
		return convertView;
	}
	
	private class ViewHolder{
		public LTextView 	txtQuestion;
		public LTextView 	txtAnswerLeft;
		public LTextView	txtAnswerRight;
		public LTextView	txtDetailsLeft;
		public LTextView	txtDetailsRight;
		public View			viewSeparator;
		
		public ViewHolder() {}
	}

	public CompareAdapter setItems(ArrayList<CompareInfo> pItems) {
		mItems = pItems;
		return this;
	}

	
}

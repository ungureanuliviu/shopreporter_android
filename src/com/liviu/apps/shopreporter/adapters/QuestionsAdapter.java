package com.liviu.apps.shopreporter.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liviu.apps.shopreporter.R;
import com.liviu.apps.shopreporter.data.Question;
import com.liviu.apps.shopreporter.ui.LTextView;

public class QuestionsAdapter  extends BaseAdapter{

	// Constants
	private final String TAG = "QuestionsAdapter";
	
	// Data
	private ArrayList<Question>		mItems;
	private LayoutInflater			mLf;
	private Context					mContext;
	
	public QuestionsAdapter(Context pContext) {
		mContext	= pContext;
		mLf			= (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mItems		= new ArrayList<Question>();
	}
	
	public QuestionsAdapter add(Question pQuestion){
		mItems.add(pQuestion);
		return this;
	}
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Question getItem(int position) {
		return mItems.get(position);	
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		
		if(convertView == null){
			convertView = mLf.inflate(R.layout.question_item_layout, parent, false);
			vh			= new ViewHolder();
			
			vh.txtQuestion	= (LTextView)convertView.findViewById(R.id.q_name);
			vh.txtAnswer	= (LTextView)convertView.findViewById(R.id.q_answer);
			
			convertView.setTag(vh);
		}
		else
			vh = (ViewHolder)convertView.getTag();
		
		vh.txtQuestion.setText(mItems.get(position).getQuestion());
		vh.txtAnswer.setText(mItems.get(position).getAnswer());
		
		return convertView;
	}
		
	private class ViewHolder{
		public LTextView	txtQuestion;
		public LTextView	txtAnswer;
		
		public ViewHolder() {}
	}
}

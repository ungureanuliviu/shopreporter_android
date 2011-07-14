package com.liviu.apps.shopreporter.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.liviu.apps.shopreporter.R;
import com.liviu.apps.shopreporter.data.Tip;
import com.liviu.apps.shopreporter.interfaces.TipsProvider;
import com.liviu.apps.shopreporter.utils.Console;

public class LProgressBox extends RelativeLayout{
	
	// Interfaces
	public interface DismissListener{
		public void onDismiss(LProgressBox pProgressBox);
	}	
	
	// Constants
	private final String 	TAG = "LProgressBox";
	
	// Data
	private Context 		mContext;
	private String			mTitleText;	
	
	// UI
	private RelativeLayout 	mContentLayout;
	private ImageView		mImage;
	private LTextView		mTitle;
	private LTextView		mContentTitle;
	private LTextView		mContentText;
	private Button			mButton;
	private ProgressBar		mProgressBar;
	private ImageView		mImageDone;

	// Services
	private LayoutInflater 	mLf;
	
	// Listeners
	private DismissListener	mDismissListener;
	private TipsProvider	mTipsProvider;
	
	public LProgressBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initLayout(attrs, defStyle);
		  
	}

	public LProgressBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initLayout(attrs, 0);
	}
	
	public LProgressBox(Context context) {
		super(context);
		mContext = context;
		initLayout(null, 0);
	}
	
	private void initLayout(AttributeSet attrs, int defStyle){		
		if(!isInEditMode()){
			mLf 			= (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mContentLayout 	= (RelativeLayout)mLf.inflate(R.layout.loading_layout, this, true);
			mImage			= (ImageView)mContentLayout.findViewById(R.id.loading_image);
			mTitle			= (LTextView)mContentLayout.findViewById(R.id.loading_title);
			mContentTitle	= (LTextView)mContentLayout.findViewById(R.id.loading_content_title);
			mContentText 	= (LTextView)mContentLayout.findViewById(R.id.loading_content_text);
			mButton			= (Button)mContentLayout.findViewById(R.id.loading_button);
			mProgressBar	= (ProgressBar)mContentLayout.findViewById(R.id.loading_progress);
			mImageDone		= (ImageView)mContentLayout.findViewById(R.id.loading_image_done);
			
			if(attrs != null){
				  TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.LProgressBox, defStyle, 0);
				  
				  // Title
				  String title = a.getString(R.styleable.LProgressBox_title);
				  if(title != null){
					  mTitle.setText(title);
				  }
				  else{
					  // we have a reference
					  int titleResId = a.getResourceId(R.styleable.LProgressBox_title, -1);
					  if(titleResId != -1){
						  mTitle.setText(titleResId);
					  }
					  else
						  mTitle.setText("Loading...");
				  }
				  				  
				  // Image
				  int imageId = a.getResourceId(R.styleable.LProgressBox_image, -1);				  				  
				  if(imageId != -1){
					  mImage.setImageResource(imageId);
				  }
				  
				  // Content title
				  String contentTitle = a.getString(R.styleable.LProgressBox_contentTitle);
				  if(contentTitle != null){
					  mContentTitle.setText(contentTitle);
				  }
				  else{
					  // we have a reference
					  int contentTitleResId = a.getResourceId(R.styleable.LProgressBox_contentTitle, -1);
					  if(contentTitleResId != -1){
						  mContentTitle.setText(contentTitleResId);
					  }
					  else
						  mContentTitle.setText("");
				  }
				  
				  // Content text
				  String contentText = a.getString(R.styleable.LProgressBox_contentText);
				  if(contentText != null){
					  mContentText.setText(contentText);
				  }
				  else{
					  // we have a reference
					  int contentTextResId = a.getResourceId(R.styleable.LProgressBox_contentText, -1);
					  if(contentTextResId != -1){
						  mContentText.setText(contentTextResId);
					  }
					  else
						  mContentText.setText("");
				  }				  
				  
				  mTitleText = mTitle.getText().toString();
				  
				  a.recycle();
			}
		}
				
	}
	
	public LProgressBox setImageResource(int pImageResource){
		if(mImage != null && pImageResource != -1)
			mImage.setImageResource(pImageResource);		
		return this;
	}
	
	public LProgressBox setTitle(String pTitle){
		if(mTitle != null && pTitle != null){
			mTitle.setText(pTitle);
			mTitleText = pTitle;
		}
		return this;
	}
	
	public LProgressBox setContentTitle(String pContentTitle){
		if(null != mContentTitle && null != pContentTitle)
			mContentTitle.setText(pContentTitle);
		return this;
	}
	
	public LProgressBox setContentText(String pContentText){
		if(null != mContentText && null != pContentText)
			mContentText.setText(pContentText);
		return this;
	}
	
	public LProgressBox setButtonText(String pButtonText){
		if(null != mButton && null != pButtonText)
			mButton.setText(pButtonText);
		return this;
	}
	
	public LProgressBox setButtonOnClickListener(OnClickListener pListener){
		if(null != mButton && null != pListener)
			mButton.setOnClickListener(pListener);
		return this;
	}
	
	public LProgressBox dismiss(){		
		setVisibility(View.GONE);
		if(mDismissListener != null)
			mDismissListener.onDismiss(this);	
		mTitle.setText(mTitleText);
		return this;
	}

	public LProgressBox show(Tip pTip){
		if(pTip != null){			
			Console.debug(TAG, "show(tip) : tip = " + pTip);
						
			mContentLayout.setVisibility(View.VISIBLE);
			mContentTitle.setText(pTip.getTitle());
			mContentText.setText(pTip.getContent());
		}
		else{
			mContentLayout.setVisibility(View.GONE);
		}	
		
		mProgressBar.setVisibility(View.VISIBLE);
		mImageDone.setVisibility(View.GONE);
		setVisibility(View.VISIBLE);
		mButton.setEnabled(false);
		
		// Reset UI data
		mTitle.setText(mTitleText);
		mTitle.setTextColor(Color.parseColor("#575757"));
		return this;
	}
	
	public LProgressBox show(){
		if(mTipsProvider != null){
			Tip tip = mTipsProvider.requestTip();
			Console.debug(TAG, "show() : tip = " + tip);
			
			if(tip != null){
				mContentLayout.setVisibility(View.VISIBLE);
				mContentTitle.setText(tip.getTitle());
				mContentText.setText(tip.getContent());
			}
			else{
				mContentLayout.setVisibility(View.GONE);
			}
		}
		else{
			mContentLayout.setVisibility(View.GONE);
		}
		
		mProgressBar.setVisibility(View.VISIBLE);
		mImageDone.setVisibility(View.GONE);
		setVisibility(View.VISIBLE);
		mButton.setEnabled(false);
		
		// Reset UI data
		mTitle.setText(mTitleText);
		mTitle.setTextColor(Color.parseColor("#575757"));
		return this;
	}

	public LProgressBox setOnDismissListener(DismissListener pListener) {
		mDismissListener = pListener;
		return this;
	}

	public void notifyDismissPossible(String pMessage) {
		mButton.setEnabled(true);
		mProgressBar.setVisibility(View.INVISIBLE);
		mImageDone.setVisibility(View.VISIBLE);
		mTitle.setTextColor(Color.parseColor("#6f9300"));
		mTitle.setText("Done (5)");
		
		CountDownTimer cTimer = new CountDownTimer(3000, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				Console.debug(TAG, "tick: " + (millisUntilFinished / 1000));		
				mTitle.setText("Done (" + (millisUntilFinished / 1000) + ")");
			}
			
			@Override
			public void onFinish() {
				dismiss();
			}
		};
		cTimer.start();
	}	
	
	public boolean isOpen(){
		return getVisibility() == View.VISIBLE;
	}
	
	public LProgressBox setTipsProvider(TipsProvider pProvider){
		mTipsProvider = pProvider;
		return this;
	}
}

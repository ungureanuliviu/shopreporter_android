package com.liviu.apps.shopreporter.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.liviu.apps.shopreporter.utils.Convertor;

public class Tip {
	
	// Constants
	private 		final String 	TAG 				= "Tip";
	public  static  final int		CATEGORY_GENERAL    = 0;
	public  static 	final int 		CATEGORY_FUNNY 		= 1;
	public  static 	final int 		CATEGORY_ADVICES 	= 2;
	public  static  final String 	BUNDLE_KEY 			= "tip_bundle_key";	
	
	// Data
	private String 	mTitle;
	private String 	mContent;
	private int    	mId;
	private int 	mCategory;
	
	public Tip() {
		mTitle 		= "";
		mContent 	= "";
		mId			= -1;
		mCategory	= 0;
	}
	
	public Tip(int pId, String pTitle, String pContent, int pCategory){
		mId			= pId;
		mTitle 		= pTitle;
		mContent 	= pContent;
		mCategory	= pCategory;
	}
	
	public int getCategory(){
		return mCategory;
	}
	
	public Tip setCategory(int pCategory){
		mCategory = pCategory;
		return this;
	}
	
	public int getId(){
		return mId;
	}
	
	public Tip setId(int pId){
		mId = pId;
		return this;
	}
	
	public String getTitle(){
		return mTitle;
	}
	
	public Tip setTitle(String pTitle){
		mTitle = pTitle;
		return this;
	}
	
	public String getContent(){
		return mContent;
	}
	
	public Tip setContent(String pContent){
		mContent = pContent;
		return this;
	}
	
	@Override
	public String toString() {
		String str = "===== Tip ==== \n " +
					 "title: " + mTitle + " \n" + 
					 "content: " + mContent + "\n" + 
					 "==============";
		return str;
	}
	
	public JSONObject toJSON(){
		return Convertor.toJson(this);
	}

	public static Tip create(JSONObject pJson) {
		if(pJson == null)
			return null;

		// get fields name
		try {
			int id 			= pJson.getInt("mId");
			int category 	= pJson.getInt("mCategory");
			String title 	= pJson.getString("mTitle");
			String content 	= pJson.getString("mContent");
			
			Tip tip = new Tip(id, title, content, category);
			return tip;
		}
		catch (JSONException e) {		
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	
}

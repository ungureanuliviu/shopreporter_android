package com.liviu.apps.shopreporter.managers;

import java.util.ArrayList;

import com.liviu.apps.shopreporter.data.CompareInfo;
import com.liviu.apps.shopreporter.data.Session;
import com.liviu.apps.shopreporter.utils.Console;
import com.liviu.apps.shopreporter.utils.Utils;

public class CompareQuestionsGenerator {
	// Constants
	private final String TAG = "CompareGenerator";
	
	// Data
	private Session mSession1;
	private Session mSession2;
	
	public CompareQuestionsGenerator() {
		mSession1 	= new Session();
		mSession2	= new Session();
	}
	
	public CompareQuestionsGenerator(Session pSession1, Session pSession2){
		mSession1	= pSession1;
		mSession2	= pSession2;
	}
	
	public Session getSession1() {
		return mSession1;
	}
	
	public CompareQuestionsGenerator setSession1(Session pSession1){
		mSession1 = pSession1;
		return this;
	}
	
	public Session	getSession2(){
		return mSession2;
	}
	
	public CompareQuestionsGenerator setSession2(Session pSession2){
		mSession2 = pSession2;
		return this;
	}
	
	public ArrayList<CompareInfo> compare(){
		ArrayList<CompareInfo> results = new ArrayList<CompareInfo>();
		
		if(mSession1 == null || mSession2 == null){
			Console.debug(TAG, "one of session is null: session1 = " + mSession1 + " session2 = " + mSession2);
			return results;
		}
		
		CompareInfo compareResult = new CompareInfo();
		compareResult.setQuestion("How much I paid?")
					 .setAnswerLeft("$" + Utils.roundTwoDecimals(mSession1.getTotalMoney()))
					 .setAnswerRight("$" + Utils.roundTwoDecimals(mSession2.getTotalMoney()));
		
		if(mSession1.getTotalMoney() >= mSession2.getTotalMoney()){
			compareResult.setCorrectAnswer(CompareInfo.ANSWER_RIGHT);
			compareResult.setDetails("Diff: " + Utils.roundTwoDecimals(mSession2.getTotalMoney() - mSession1.getTotalMoney()));			
		}
		else{
			compareResult.setCorrectAnswer(CompareInfo.ANSWER_LEFT);
			compareResult.setDetails("Diff: $" + Utils.roundTwoDecimals(mSession1.getTotalMoney() - mSession2.getTotalMoney()));			
		}
		
		if(mSession1.getTotalMoney() == mSession2.getTotalMoney())
			compareResult.setCorrectAnswer(CompareInfo.ANSWER_BOTH);
			
		results.add(compareResult);
		
		compareResult = new CompareInfo(); 
		compareResult.setQuestion("How many products I bought?");
		compareResult.setAnswerLeft(Integer.toString(mSession1.getTotalProducts(true)) + (mSession1.getTotalProducts(true) == 1 ? " product" : " products"));					 
		compareResult.setAnswerRight(Integer.toString(mSession2.getTotalProducts(true)) + (mSession2.getTotalProducts(true) == 1 ? " product" : " products"));
		
		if(mSession1.getTotalProducts(true) > mSession2.getTotalProducts(true)){
			compareResult.setCorrectAnswer(CompareInfo.ANSWER_LEFT);
			int diff = mSession1.getTotalProducts(true) - mSession2.getTotalProducts(true);			
			compareResult.setDetails("+" + Integer.toString(diff) + (diff == 1 ? " product" : " products"));						
		}
		else{
			compareResult.setCorrectAnswer(CompareInfo.ANSWER_RIGHT);
			int diff = mSession2.getTotalProducts(true) - mSession1.getTotalProducts(true);
			compareResult.setDetails("+" + Integer.toString(diff) + (diff == 1 ? " product" : " products"));	
		}
		
		if(mSession1.getTotalProducts(true) == mSession2.getTotalProducts(true))
			compareResult.setCorrectAnswer(CompareInfo.ANSWER_BOTH);
		
		results.add(compareResult);
		
		compareResult = new CompareInfo(); 
		compareResult.setQuestion("How much time I spent at shopping?");
		compareResult.setAnswerLeft(Utils.formatTime(mSession1.getTotalTime()));					 
		compareResult.setAnswerRight(Utils.formatTime(mSession2.getTotalTime()));
		
		if(mSession1.getTotalTime() >= mSession2.getTotalTime()){
			compareResult.setCorrectAnswer(CompareInfo.ANSWER_RIGHT);
			compareResult.setDetails("Diff: " + Utils.formatTime(mSession1.getTotalTime() - mSession2.getTotalTime()));			
		}
		else{
			compareResult.setCorrectAnswer(CompareInfo.ANSWER_LEFT);
			compareResult.setDetails("Diff: " + Utils.formatTime(mSession2.getTotalTime() - mSession1.getTotalTime()));	
		}
		
		if(mSession1.getTotalTime() == mSession2.getTotalTime())
			compareResult.setCorrectAnswer(CompareInfo.ANSWER_BOTH);
		
		results.add(compareResult);		
		
		// Location
		compareResult = new CompareInfo(); 
		compareResult.setQuestion("Which location?");
		compareResult.setAnswerLeft(mSession1.getLocation());					 
		compareResult.setAnswerRight(mSession2.getLocation());		
		compareResult.setCorrectAnswer(CompareInfo.ANSWER_NONE);		
		
		results.add(compareResult);	
		
		return results;
	}
}

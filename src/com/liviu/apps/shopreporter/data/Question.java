package com.liviu.apps.shopreporter.data;

import android.os.Bundle;

import com.liviu.apps.shopreporter.interfaces.BundleAnnotation;
import com.liviu.apps.shopreporter.utils.Convertor;

public class Question {

	// Constants
	private final 			String TAG = "Question";
	public 	final static 	String KEY_QUESTION 	= "key_question";
	public 	final static 	String KEY_ANSWER		= "key_answer";
	
	// Data
	@BundleAnnotation(bundleKey = KEY_QUESTION)
	private String mQuestion;
	
	@BundleAnnotation(bundleKey = KEY_ANSWER)
	private String mAnswer;
	
	public Question() {
		mQuestion 	= "-";
		mAnswer		= "-";
	}
	
	public Question(String pQuestion, String pAnswer){
		mQuestion 	= pQuestion;
		mAnswer		= pAnswer;
	}
	
	public static Question createFrom(Bundle pBundle){
		if(pBundle != null){
			return new Question(pBundle.getString(KEY_QUESTION), pBundle.getString(KEY_ANSWER));
		}
		else{
			return null;
		}
	}
	
	public String getQuestion(){
		return mQuestion;
	}
	
	public Question setQuestion(String pQuestion){
		mQuestion = pQuestion;
		return this;
	}
	
	public String getAnswer(){
		return mAnswer;
	}
	
	public Question setAnswer(String pAnswer){
		mAnswer = pAnswer;
		return this;
	}
	
	public Bundle toBundle(){
		return Convertor.toBundle(this);
	}
	
	public String toString(){
		return Convertor.toString(this);
	}
	
	
	
}

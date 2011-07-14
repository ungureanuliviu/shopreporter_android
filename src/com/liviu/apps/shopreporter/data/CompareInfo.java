package com.liviu.apps.shopreporter.data;

import com.liviu.apps.shopreporter.utils.Convertor;

public class CompareInfo {
	// Constants
	private final String TAG 				= "CompareInfo";
	public  final static int ANSWER_LEFT 	= 1;
	public  final static int ANSWER_RIGHT	= 2;
	public  final static int ANSWER_NONE 	= 3;
	public final  static int ANSWER_BOTH 	= 4;

	// Data	
	private String mQuestion;
	private String mAnswerLeft;
	private String mAnswerRight;
	private int	   mCorrectAnswer; 
	private String mDetails;
	
	public CompareInfo() {
		mQuestion 		= "";
		mAnswerLeft 	= "";
		mAnswerRight 	= "";
		mCorrectAnswer 	= ANSWER_LEFT;
		mDetails 		= "";
	}
	
	public CompareInfo(String pQuestion, String pAnswerLeft, String pAnswerRight, int pCorrectAnswer, String pDetails) {
		mQuestion 		= pQuestion;
		mAnswerLeft 	= pAnswerLeft;
		mAnswerRight 	= pAnswerRight;
		mCorrectAnswer 	= (pCorrectAnswer > 2 || pCorrectAnswer < 1 ? ANSWER_NONE : pCorrectAnswer);
		mDetails 		= pDetails;
	}
	
	
	public String getAnswerLeft() {
		return mAnswerLeft;
	}
	
	public CompareInfo setAnswerLeft(String pAnswerLeft) {
		mAnswerLeft = pAnswerLeft;
		return this;
	}
	
	public CompareInfo setAnswerRight(String pAnswerRight) {
		mAnswerRight = pAnswerRight;
		return this;
	}
	
	public String getAnswerRight(){
		return mAnswerRight;
	}
	
	public String getQuestion(){
		return mQuestion;
	}
	
	public CompareInfo setQuestion(String pQuestion){
		mQuestion = pQuestion;
		return this;
	}
	
	public int getCorrectAnswer(){
		return mCorrectAnswer;
	}
	
	public CompareInfo setCorrectAnswer(int pCorrectAnswer){
		mCorrectAnswer = pCorrectAnswer;
		return this;
	}
	
	public CompareInfo setDetails(String pDetails){
		mDetails = pDetails;
		return this;
	}
	
	public String getDetails(){
		return mDetails;
	}
	
	public String toString(){
		return Convertor.toString(this);
	}
}

package com.liviu.apps.shopreporter.data;

import com.liviu.apps.shopreporter.utils.Convertor;

public class MonthReportPeriod extends ReportPeriod{

	// Constants
	private final String TAG = "MonthReportPeriod";
	
	// Data
	private int mMonth;
	private int mYear;
	private String mFormattedDate;
	
	public MonthReportPeriod() {
		super();
		mMonth 			= 1;
		mYear  			= 2011;
		mFormattedDate 	= "";
	}
	
	public MonthReportPeriod addSession(Session pSession){
		getSessions().add(pSession);
		return this;
	}
	
	public int getTotalSessions(){
		return getSessions().size();
	}
	
	public Session getSessionAt(int pIndex){
		try{
			return getSessions().get(pIndex);
		}
		catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public MonthReportPeriod setMonth(int pMonth){
		mMonth = pMonth; 
		return this;
	}
	
	public int getMonth(){
		return mMonth;
	}
	
	public MonthReportPeriod setYear(int pYear){
		mYear = pYear;
		return this;
	}
	
	public int getYear(){
		return mYear;
	}
	
	public MonthReportPeriod setFormattedDate(String pFormattedDate){
		mFormattedDate = pFormattedDate;
		return this;
	}
	
	public String getFormattedDate(){
		return mFormattedDate;
	}
	
	@Override
	public String toString() {
		return Convertor.toString(this);
	}
}

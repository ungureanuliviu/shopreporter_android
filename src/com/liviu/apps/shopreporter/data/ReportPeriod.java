package com.liviu.apps.shopreporter.data;

import java.util.ArrayList;

public class ReportPeriod {

	// Constants
	private final String TAG = "ReportPeriod";
	
	// Data
	private ArrayList<Session> mSessions;
	
	public ReportPeriod() {
		mSessions = new ArrayList<Session>();
	}
	
	protected ArrayList<Session> getSessions(){
		return mSessions;
	}
}

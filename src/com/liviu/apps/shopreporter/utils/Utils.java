package com.liviu.apps.shopreporter.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

	public class Utils {

	public static String roundTwoDecimals(double d) {
    	DecimalFormat twoDForm = new DecimalFormat("0.00");
	
    	return twoDForm.format(d);
	}
	
	public static Long now() {
	    Date d = new Date();	    
	    return d.getTime();	        
	}	
	
	public static String formatDate(long pTimeStamp, String pattern){
		String 			 	defaultPattern	= "E, dd MMM yyyy HH:mm:ss";
		SimpleDateFormat	formatter		= null;
		
		try{
			formatter = new SimpleDateFormat(pattern);			
		}
		catch (Exception e) {
			e.printStackTrace();
			formatter = new SimpleDateFormat(defaultPattern);
		}
		
		return formatter.format(new Date(pTimeStamp));
		
	}	
	
	public static String formatTime(long pTime){
		String time = "";
        int seconds = (int) (pTime / 1000);
        int minutes = seconds / 60;
        int hours   = minutes / 60;
        
        if(hours > 0){
        	if(hours < 10)
        		time += "0";
        	time += Integer.toString(hours) + "h ";
        	
        	/*
        	if(hours == 1)
        		timeMessage += " hour ";        		        	
        	else
        		timeMessage += " hours ";
        	*/
        }
        
        if(minutes > 0){
        	if(minutes < 10)
        		time += "0";
        	
        	time += Integer.toString(minutes) + "min ";
        	
        	/*
        	if(minutes == 1)
        		timeMessage += " minute ";
        	else
        		timeMessage += " minutes ";        		
        	*/
        }
        
        seconds = seconds % 60;
        
        if(seconds > 0){
        	if(seconds < 10)
        		time += "0";
        	
        	time += Integer.toString(seconds) + "sec";
        	
        	/*
        	if(seconds == 1)
        		timeMessage += " second ";
        	else
        		timeMessage += " seconds ";
        	*/        	        
        }		
        
        return time;
	}
}

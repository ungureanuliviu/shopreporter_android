package com.liviu.apps.shopreporter.utils;

import java.text.DecimalFormat;
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
}

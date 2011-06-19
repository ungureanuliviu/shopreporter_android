package com.liviu.apps.shopreporter.utils;

import android.util.Log;

public class Console {
	private static boolean isDev = true;
	public static void debug(String TAG, String txt){
		if(isDev){
			String logDetails = "[line " + Thread.currentThread().getStackTrace()[2].getLineNumber() + "] ";
			Log.e(TAG, logDetails + txt);
		}
	}
}

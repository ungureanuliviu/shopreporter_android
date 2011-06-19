package com.liviu.apps.shopreporter.interfaces;

import java.util.List;

import android.location.Address;

public interface OnGeoCoderDataReceived {
	public void onGeoCoderDataReceived(boolean isSuccess, List<Address> addresses);
}

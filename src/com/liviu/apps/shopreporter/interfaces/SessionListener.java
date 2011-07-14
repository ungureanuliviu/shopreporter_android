package com.liviu.apps.shopreporter.interfaces;

import java.util.ArrayList;

import android.widget.AdapterView;

import com.liviu.apps.shopreporter.data.Product;
import com.liviu.apps.shopreporter.data.Session;

public interface SessionListener {
	public void onSessionCreated(boolean isSuccess, Session newSession);
	public void onProductAddedToSession(boolean isSuccess, Session session, Product addedProduct);
	public void onProductRemovedFromSession(boolean isSucces, Session session, Product removedProduct);
	public void onSessionLoaded(boolean isSuccess, Session pSession);
	public void onUserSessionsLoaded(boolean isSuccess, ArrayList<Session> pLoadedSessions);
	public void onCommonProductsLoaded(boolean isSuccess, ArrayList<Product> pCommonProducts);	
}

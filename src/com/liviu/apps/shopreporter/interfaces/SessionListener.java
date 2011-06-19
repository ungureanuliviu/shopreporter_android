package com.liviu.apps.shopreporter.interfaces;

import com.liviu.apps.shopreporter.data.Product;
import com.liviu.apps.shopreporter.data.Session;

public interface SessionListener {
	public void onSessionCreated(boolean isSuccess, Session newSession);
	public void onProductAddedToSession(boolean isSuccess, Session session, Product addedProduct);
	public void onProductRemovedFromSession(boolean isSucces, Session session, Product removedProduct);
}

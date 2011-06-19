package com.liviu.apps.shopreporter.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.os.Bundle;

import com.liviu.apps.shopreporter.data.Product;
import com.liviu.apps.shopreporter.data.Session;
import com.liviu.apps.shopreporter.interfaces.BundleAnnotation;
import com.liviu.apps.shopreporter.interfaces.LParam;
import com.liviu.apps.shopreporter.managers.ShoppingSessionManager;

public class Convertor {
	
	// Contants
	private static final String TAG = "Convertor";
	
	public Convertor() {}
	
	public static Bundle toBundle(Object obj){				
		Bundle b 		= new Bundle();		
		Class<?> c 		= obj.getClass();		
		Field[] fields 	= c.getDeclaredFields();
		
		for(Field f : fields){
			f.setAccessible(true);
			Annotation[] annotations = f.getAnnotations();
			for(Annotation a : annotations){
				if(a instanceof BundleAnnotation){
					BundleAnnotation annotation = (BundleAnnotation)a;	
					try {													
						if(f.getType().equals(String.class))
							b.putString(annotation.bundleKey(),((String)f.get(obj)));
						else if(f.getType().equals(int.class) || f.getType().equals(Integer.class))
							b.putInt(annotation.bundleKey(), f.getInt(obj));
						else if(f.getType().equals(long.class) || f.getType().equals(Long.class))
							b.putLong(annotation.bundleKey(), f.getLong(obj));
						else if(f.getType().equals(Double.class) || f.getType().equals(double.class))
							b.putDouble(annotation.bundleKey(), f.getDouble(obj));
						else if(f.getType().equals(Boolean.class) || f.getType().equals(boolean.class))
							b.putBoolean(annotation.bundleKey(), f.getBoolean(obj));						
					}
					catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
					catch (IllegalAccessException e) {
						e.printStackTrace();
					}						
				}
			}
		}
		
		return b;
	}
	public static String toString(Object obj){				
		String data		= "";
		Class<?> c 		= obj.getClass();				
		Field[] fields 	= c.getDeclaredFields();
		data += "====== " + c.getName() + " ========= \n";
		
		for(Field f : fields){
			f.setAccessible(true);
			Annotation[] annotations = f.getAnnotations();
			for(Annotation a : annotations){
				if(a instanceof BundleAnnotation){
					BundleAnnotation annotation = (BundleAnnotation)a;	
					try {													
						if(f.getType().equals(String.class))
							data += "\n" + f.getName() + ": " + ((String)f.get(obj));
						else if(f.getType().equals(int.class) || f.getType().equals(Integer.class))
							data += "\n" + f.getName() + ": " + f.getInt(obj);
						else if(f.getType().equals(long.class) || f.getType().equals(Long.class))
							data += "\n" + f.getName() + ": " + f.getLong(obj);
						else if(f.getType().equals(Double.class) || f.getType().equals(double.class))
							data += "\n" + f.getName() + ": " + f.getDouble(obj);
						else if(f.getType().equals(Boolean.class) || f.getType().equals(boolean.class))
							data += "\n" + f.getName() + ": " + f.getBoolean(obj);						
					}
					catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
					catch (IllegalAccessException e) {
						e.printStackTrace();
					}						
				}
			}
		}
		
		return data;
	}	
	
	/**
	 * How to call: Convertor.paramsToBunde(ShoppingSessionManager.this.getClass(), "removeProduct", new Object[]{cSession, cProductToRemove}, Session.class, Product.class);
	 * @param pClazz
	 * @param pMethodName
	 * @param pParamsValue
	 * @param pParams
	 * @return
	 */
	public static Bundle paramsToBunde(Class<?> pClazz, String pMethodName, Object[] pParamsValue, Class<?> ...pParams){
				
		try {			
			Method m = pClazz.getDeclaredMethod(pMethodName, pParams);
			Annotation[][] annotations = m.getParameterAnnotations();
			Console.debug(TAG, " Annotation length: " + annotations.length);
			
			for(int i = 0; i < m.getParameterTypes().length; i++){
				for(int j = 0; j < m.getParameterAnnotations()[i].length; j++){
				Console.debug(TAG, " " + annotations[i][j]);
					if(annotations[i][j] instanceof LParam){
						LParam lParamAnn = (LParam)annotations[i][j];
						Console.debug(TAG, "here: " + lParamAnn.paramName() + "=" + pParamsValue[i]);
						
					}
				}
			}				
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}	
		return null;
	}
}

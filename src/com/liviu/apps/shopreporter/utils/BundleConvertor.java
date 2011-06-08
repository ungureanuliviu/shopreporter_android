package com.liviu.apps.shopreporter.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import android.os.Bundle;

import com.liviu.apps.shopreporter.interfaces.BundleAnnotation;

public class BundleConvertor {
	
	public BundleConvertor(Object obj) {}
	
	public Bundle toBundle(Object obj){				
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
}

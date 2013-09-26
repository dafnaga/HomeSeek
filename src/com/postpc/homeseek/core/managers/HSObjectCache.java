package com.postpc.homeseek.core.managers;

import java.util.HashMap;
import java.util.Map;

import com.postpc.homeseek.core.hsobjects.HSObject;

public class HSObjectCache<T extends HSObject> {
	// Cache for objects. 
	protected Map<String, T> cacheMap = new HashMap<String, T>();
	
	// TODO set limit on the cache size
	
	public T get(String objectId){
		if (cacheMap.keySet().contains(objectId)){
			return cacheMap.get(objectId);			
		}
		
		return null;
	}
	
	public void remove(String objectId){
		if (cacheMap.keySet().contains(objectId)){
			cacheMap.remove(objectId);			
		}
	}
	
	public void add(T object){
		cacheMap.put(object.getId(), object);
	}
}

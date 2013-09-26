package com.postpc.homeseek.core.db;

public interface ParseObject {

	public String getObjectType();
	
	public void setField(String name, String value);
	
	public void setField(String name, int value);
	
	public String getStringField(String name);
	
	public int getIntField(String name);
	
	public boolean hasField(String name);
	
	public int getID();
	
	public void sync();
}

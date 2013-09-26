package com.postpc.homeseek.core.hsobjects;

import com.parse.ParseException;
import com.parse.ParseObject;

public class HSObject{
	private ParseObject dbObjects[];
	
	public HSObject(ParseObject dbObjects[])
	{
		this.dbObjects = dbObjects.clone();
	}
	
	public HSObject(ParseObject dbObject)
	{
		this(new ParseObject[]{dbObject});		 
	}
	
	public void save() throws ParseException
	{
		for (ParseObject dbObject : dbObjects){
			dbObject.save();
		}
	}
		
	public String getId()
	{
		return dbObjects[0].getObjectId(); 
	}
	
	protected void put(String name, String value) {
		ParseObject parseObj = findObjectWithField(name);
		if(parseObj != null){
			parseObj.put(name, value);
		}
	}	

	protected void put(String name, int value) {
		ParseObject parseObj = findObjectWithField(name);
		if(parseObj != null){
			parseObj.put(name, value);
		}
	}
	
	protected void put(String name, double value) {
		ParseObject parseObj = findObjectWithField(name);
		if(parseObj != null){
			parseObj.put(name, value);
		}
	}

	protected void put(String name, ParseObject value) {
		ParseObject parseObj = findObjectWithField(name);
		if(parseObj != null){
			parseObj.put(name, value);
		}
		//TODO - else?
	}
	
	protected static void putOptionalFileld(ParseObject parseObj, String field, Object value)
	{
		if(parseObj != null && value != null)
			parseObj.put(field, value);
	}
	
	protected Integer getIntField(String name) {
		ParseObject parseObj = findObjectWithField(name);
		if(parseObj != null){
			return parseObj.getInt(name);
		}
		return null;
	}
	
	protected Double getDoubleField(String name) {
		ParseObject parseObj = findObjectWithField(name);
		if(parseObj != null){
			return parseObj.getDouble(name);
		}
		return null;
	}

	public String getStringField(String name){
		ParseObject parseObj = findObjectWithField(name);
		if(parseObj != null){
			return parseObj.getString(name);
		}
		return null;
	}			
	
	public ParseObject getParseObjectField(String name) {
		ParseObject parseObj = findObjectWithField(name);
		if(parseObj != null){
			return parseObj.getParseObject(name);
		}
		return null;
	}
	
	private ParseObject findObjectWithField(String name) 
	{
		for (ParseObject dbObject : dbObjects){
			if (dbObject.containsKey(name)){
				return dbObject;
			}
		}
		return null;
	}

	public void delete() throws ParseException
	{
		for (ParseObject object : dbObjects){
			object.delete();
		}
	}	

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		final HSObject other = (HSObject) obj;
		if (this.dbObjects.length != other.dbObjects.length) {
			return false;
		}
		
		for (int i = 0; i < this.dbObjects.length; i++) {
			if (!this.dbObjects[i].getObjectId().equals(other.dbObjects[i].getObjectId())){
				return false;
			}	
		}
		return true;
	}
}

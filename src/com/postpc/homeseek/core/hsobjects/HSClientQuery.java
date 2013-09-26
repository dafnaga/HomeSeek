package com.postpc.homeseek.core.hsobjects;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.postpc.homeseek.MapCircle;
import com.postpc.homeseek.core.config.db.HSClientQueryDBConfig;
import com.postpc.homeseek.core.managers.HSApartmentQuery;

public class HSClientQuery extends HSObject{

	private ParseObject searchQueryDbObject;
	
	public static HSClientQuery create(String clientId, HSApartmentQuery searchQuery) throws ParseException
	{
		ParseObject dbObject = new ParseObject(HSClientQueryDBConfig.ClientQueryTable.NAME);
		HSClientQuery query = new HSClientQuery(dbObject);
		
		query.setClientId(clientId);
		query.setQuery(searchQuery);
		query.save();
		
		return query;
	}
	
	public HSClientQuery(ParseObject queryDbObject)
	{
		super(queryDbObject);
		this.searchQueryDbObject = queryDbObject;
	}

	
	public void setQuery(HSApartmentQuery query)
	{
		setCity(query.getCity());
		setStreet(query.getStreet());
		setMinRooms(query.getMinRooms());
		setMaxRooms(query.getMaxRooms());
		setMinPrice(query.getMinPrice());
		setMaxPrice(query.getMaxPrice());
		setMinArea(query.getMinArea());
		setMaxArea(query.getMaxArea());
		setSearchArea(query.getSearchArea());
	}
	
	public void setSearchArea(MapCircle searchArea) {
		if (searchArea == null){
			return;
		}
		
		LatLng point = searchArea.getCenter();		
		ParseGeoPoint parsePoint = new ParseGeoPoint(point.latitude, point.longitude);
		
		Double radius = searchArea.getRadius();
		
		searchQueryDbObject.put(HSClientQueryDBConfig.ClientQueryTable.Fields.MAP_AREA_CENTER, parsePoint);		
		searchQueryDbObject.put(HSClientQueryDBConfig.ClientQueryTable.Fields.MAP_AREA_RADIUS, radius);
	}

	public MapCircle getSearchArea(){
		ParseGeoPoint center = searchQueryDbObject.getParseGeoPoint(HSClientQueryDBConfig.ClientQueryTable.Fields.MAP_AREA_CENTER);
		if (center == null){
			return null;
		}
		
		LatLng point = new LatLng(center.getLatitude(), center.getLongitude()); 
		
		Double radius = searchQueryDbObject.getDouble(HSClientQueryDBConfig.ClientQueryTable.Fields.MAP_AREA_RADIUS);
		if (radius == null){
			return null;
		}
		
		return new MapCircle(point, radius);
	}
	
	public String getClientId()
	{
		return searchQueryDbObject.getString(HSClientQueryDBConfig.ClientQueryTable.Fields.CLIENT_ID);
	}
	
	public void setClientId(String clientId)
	{
		searchQueryDbObject.put(HSClientQueryDBConfig.ClientQueryTable.Fields.CLIENT_ID, clientId);		
	}
	
	public String getCity() 
	{
		return getStringField(HSClientQueryDBConfig.ClientQueryTable.Fields.CITY);
	}
	
	public void setCity(String city) 
	{
		putOptionalFileld(searchQueryDbObject, 
				HSClientQueryDBConfig.ClientQueryTable.Fields.CITY, city);
	}
	
	public String getStreet() 
	{
		return getStringField(HSClientQueryDBConfig.ClientQueryTable.Fields.STREET);
	}
	
	public void setStreet(String street) 
	{
		putOptionalFileld(searchQueryDbObject, 
				HSClientQueryDBConfig.ClientQueryTable.Fields.STREET, street);
	}
	
	public Double getMinRooms() 
	{
		return getDoubleField(HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_ROOMS);
	}
	
	public void setMinRooms(Double minRooms) 
	{
		putOptionalFileld(searchQueryDbObject, 
				HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_ROOMS, minRooms);
	}
	
	public Double getMaxRooms() 
	{
		return getDoubleField(HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_ROOMS);
	}
	
	public void setMaxRooms(Double maxRooms) 
	{
		putOptionalFileld(searchQueryDbObject, 
				HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_ROOMS, maxRooms);
	}
	
	public Integer getMinArea() 
	{
		return getIntField(HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_AREA);
	}
	
	public void setMinArea(Integer areaSize) 
	{
		putOptionalFileld(searchQueryDbObject,
				HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_AREA, areaSize);
	}
	
	public Integer getMaxArea() 
	{
		return getIntField(HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_AREA);
	}
	
	public void setMaxArea(Integer areaSize) 
	{
		putOptionalFileld(searchQueryDbObject,
				HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_AREA, areaSize);
	}
	
	public Integer getMinPrice() 
	{
		return getIntField(HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_PRICE);
	}
	
	public void setMinPrice(Integer minPrice) 
	{
		putOptionalFileld(searchQueryDbObject,
				HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_PRICE, minPrice);
	}
	
	public Integer getMaxPrice() 
	{
		return getIntField(HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_PRICE);
	}
	
	public void setMaxPrice(Integer maxPrice) 
	{
		putOptionalFileld(searchQueryDbObject,
				HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_PRICE, maxPrice);
	}

	public String getNeiborhood() {
		return getStringField(HSClientQueryDBConfig.ClientQueryTable.Fields.NEIGHBORHOOD);
	}
}

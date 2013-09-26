package com.postpc.homeseek.core.hsobjects;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.postpc.homeseek.core.config.db.HSApartmentDBConfig;
import com.postpc.homeseek.core.hsobjects.exceptions.IllegalApartmentParametersException;

public class HSApartment extends HSObject{

	protected ParseObject apartmentDbObject;

	public static HSApartment create(String city, String neighborhood, String street, String buildingNumber, 
			String apartmentNumber, Double rooms, Integer area, String ownerId, Integer price, LatLng mapLocation, String ownerComments) 
		    throws ParseException, IllegalApartmentParametersException
	{
		ParseObject apartmentObject = new ParseObject(HSApartmentDBConfig.ApartmentTable.NAME);
		
		if(city == null || street == null){
			throw new IllegalApartmentParametersException();
		}

		//Add non-optional fields
		apartmentObject.put(HSApartmentDBConfig.ApartmentTable.Fields.CITY, city);
		apartmentObject.put(HSApartmentDBConfig.ApartmentTable.Fields.STREET, street);
		apartmentObject.put(HSApartmentDBConfig.ApartmentTable.Fields.OWNER_ID, ownerId);
		
		//Add optional fields
		putOptionalFileld(apartmentObject, HSApartmentDBConfig.ApartmentTable.Fields.NEIGHBORHOOD, neighborhood);
		putOptionalFileld(apartmentObject, HSApartmentDBConfig.ApartmentTable.Fields.BUILDING_NUMBER, buildingNumber);
		putOptionalFileld(apartmentObject, HSApartmentDBConfig.ApartmentTable.Fields.APARTMENT_NUMBER, apartmentNumber);
		putOptionalFileld(apartmentObject, HSApartmentDBConfig.ApartmentTable.Fields.ROOMS, rooms);
		putOptionalFileld(apartmentObject, HSApartmentDBConfig.ApartmentTable.Fields.AREA, area);
		putOptionalFileld(apartmentObject, HSApartmentDBConfig.ApartmentTable.Fields.PRICE, price);
		putOptionalFileld(apartmentObject, HSApartmentDBConfig.ApartmentTable.Fields.OWNER_COMMENTS, ownerComments);
		
		if (mapLocation != null){
			ParseGeoPoint geoPoint = new ParseGeoPoint(mapLocation.latitude, mapLocation.longitude);
			apartmentObject.put(HSApartmentDBConfig.ApartmentTable.Fields.MAP_LOCATION, geoPoint);
		}
		
		apartmentObject.save();
		
		return new HSApartment(apartmentObject);
	}	
	
	public void update(String neighborhood, String buildingNumber, String apartmentNumber, Double rooms, Integer area, 
			Integer price, String ownerComments) throws ParseException
	{
		putOptionalFileld(this.apartmentDbObject,HSApartmentDBConfig.ApartmentTable.Fields.BUILDING_NUMBER, buildingNumber);
		putOptionalFileld(this.apartmentDbObject,HSApartmentDBConfig.ApartmentTable.Fields.APARTMENT_NUMBER, apartmentNumber);
		putOptionalFileld(this.apartmentDbObject,HSApartmentDBConfig.ApartmentTable.Fields.ROOMS, rooms);
		putOptionalFileld(this.apartmentDbObject,HSApartmentDBConfig.ApartmentTable.Fields.AREA, area);
		putOptionalFileld(this.apartmentDbObject,HSApartmentDBConfig.ApartmentTable.Fields.PRICE, price);
		putOptionalFileld(this.apartmentDbObject,HSApartmentDBConfig.ApartmentTable.Fields.NEIGHBORHOOD, neighborhood);
		putOptionalFileld(this.apartmentDbObject,HSApartmentDBConfig.ApartmentTable.Fields.OWNER_COMMENTS, ownerComments);
		
		this.apartmentDbObject.save();
	}
	
	public HSApartment(ParseObject apartmentDbObject)
	{
		super(apartmentDbObject);
		this.apartmentDbObject = apartmentDbObject;
	}

	public String getCity() 
	{
		return getStringField(HSApartmentDBConfig.ApartmentTable.Fields.CITY);
	}
	
	public void setCity(String city) 
	{
		put(HSApartmentDBConfig.ApartmentTable.Fields.CITY, city);
	}
	
	public String getNeighborhood() 
	{
		return getStringField(HSApartmentDBConfig.ApartmentTable.Fields.NEIGHBORHOOD);
	}
	
	public void setNeighborhood(String neighborhood) 
	{
		put(HSApartmentDBConfig.ApartmentTable.Fields.NEIGHBORHOOD, neighborhood);
	}
	
	public String getStreet() 
	{
		return getStringField(HSApartmentDBConfig.ApartmentTable.Fields.STREET);
	}
	
	public void setStreet(String street) 
	{
		put(HSApartmentDBConfig.ApartmentTable.Fields.STREET, street);
	}
	
	public String getBuildingNumber() 
	{
		return getStringField(HSApartmentDBConfig.ApartmentTable.Fields.BUILDING_NUMBER);
	}
	
	public void setBuildingNumber(String buildingNumber) 
	{
		put(HSApartmentDBConfig.ApartmentTable.Fields.BUILDING_NUMBER, buildingNumber);
	}
	
	public String getApartmentNumber() 
	{
		return getStringField(HSApartmentDBConfig.ApartmentTable.Fields.APARTMENT_NUMBER);
	}
	
	public void setApartmentNumber(String apartmentNumber) 
	{
		put(HSApartmentDBConfig.ApartmentTable.Fields.APARTMENT_NUMBER, apartmentNumber);
	}
	
	public Double getRooms() 
	{
		return getDoubleField(HSApartmentDBConfig.ApartmentTable.Fields.ROOMS);
	}
	
	public void setRooms(Double rooms) 
	{
		put(HSApartmentDBConfig.ApartmentTable.Fields.ROOMS, rooms);
	}
	
	public Integer getAreaSize() 
	{
		return getIntField(HSApartmentDBConfig.ApartmentTable.Fields.AREA);
	}
	
	public void setAreaSize(int areaSize) 
	{
		put(HSApartmentDBConfig.ApartmentTable.Fields.AREA, areaSize);
	}
	
	public String getOwnerId() 
	{
		return getStringField(HSApartmentDBConfig.ApartmentTable.Fields.OWNER_ID);
	}
	
	public void setOwnerId(String ownerId) 
	{
		put(HSApartmentDBConfig.ApartmentTable.Fields.OWNER_ID, ownerId);
	}

	public Integer getPrice() 
	{
		return getIntField(HSApartmentDBConfig.ApartmentTable.Fields.PRICE);
	}
	
	public void setPrice(int price) 
	{
		put(HSApartmentDBConfig.ApartmentTable.Fields.PRICE, price);
	}	
	
	public String getOwnerComments() 
	{
		return getStringField(HSApartmentDBConfig.ApartmentTable.Fields.OWNER_COMMENTS);
	}
	
	public void setOwnerComments(String comments) 
	{
		put(HSApartmentDBConfig.ApartmentTable.Fields.OWNER_COMMENTS, comments);
	}
	
	public LatLng getMapLocation(){
		ParseGeoPoint p = apartmentDbObject.getParseGeoPoint(HSApartmentDBConfig.ApartmentTable.Fields.MAP_LOCATION);
		if (p == null){
			return null;
		}
		
		return new LatLng(p.getLatitude(), p.getLongitude()); 
	}
	
	public void setMapLocation(LatLng location){
		ParseGeoPoint p = null;
		
		if (location != null){
			p = new ParseGeoPoint(location.latitude, location.longitude);
		}
		
		apartmentDbObject.put(HSApartmentDBConfig.ApartmentTable.Fields.MAP_LOCATION, p);		
	}
	
	public String getTitle() {
		return getApartemntTitle(getCity(),getStreet(),getBuildingNumber());
	}
	
	public static String getApartemntTitle(String city, String street, String buildingNumber) {
		String title = city +", "+ street;
		if (buildingNumber != null){
			title = title +" " + buildingNumber;
		}
		return title;
	}

	public String getAddress() {
		return "Israel, " + getTitle(); // TODO for now this will do
	}
	
	
	/* TODO other fields.. */

}

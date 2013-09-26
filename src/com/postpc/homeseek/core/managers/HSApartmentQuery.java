package com.postpc.homeseek.core.managers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.postpc.homeseek.MapCircle;
import com.postpc.homeseek.core.config.db.HSApartmentDBConfig;
import com.postpc.homeseek.core.config.db.HSClientQueryDBConfig;
import com.postpc.homeseek.core.hsobjects.HSApartment;

public class HSApartmentQuery implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1928691011745360331L;

	public enum ApartmentType {
	    RENT, BUY 
	}
	public static String BUY_TYPE = "Buy";
	public static String RENT_TYPE = "Rent";
	
	private ApartmentType type;
	protected String city;
	protected String neighborhood;
	protected String street;
	protected Integer minArea;
	protected Integer maxArea;
	protected Double minRooms;
	protected Double maxRooms;
	protected Integer minPrice;
	protected Integer maxPrice;
	private MapCircle searchArea;
	
	public HSApartmentQuery(){
		setApartmentType(RENT_TYPE);
		setCity(null);
		setStreet(null);
		setNeighborhood(null);
		setMaxArea(null);
		setMinArea(null);
		setMaxRooms(null);
		setMinRooms(null);
		setMinPrice(null);
		setMaxPrice(null);
	}
	
	public ApartmentType getApartmentType() {
		return type;
	}
	
	public void setApartmentType(String type) {
		this.type = (type.compareTo(RENT_TYPE) == 0)? ApartmentType.RENT: ApartmentType.BUY;
	}
	
	public String getApartmentTypeAsString() {
		return (this.type == ApartmentType.BUY)? BUY_TYPE: RENT_TYPE;
	}

	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}	

	public String getStreet() {
		return street;
	}
	
	public void setStreet(String street) {
		this.street = street;
	}
	
	public Integer getMinArea() {
		return minArea;
	}
	
	public void setMinArea(Integer minArea) {
		this.minArea = minArea;
	}
	
	public Integer getMaxArea() {
		return maxArea;
	}
	
	public void setMaxArea(Integer maxArea) {
		this.maxArea = maxArea;
	}
	
	public Double getMinRooms() {
		return minRooms;
	}
	
	public void setMinRooms(Double minRooms) {
		this.minRooms = minRooms;
	}
	
	public Double getMaxRooms() {
		return maxRooms;
	}
	
	public void setMaxRooms(Double maxRooms) {
		this.maxRooms = maxRooms;
	}
	
	public Integer getMinPrice() {
		return minPrice;
	}
	
	public void setMinPrice(Integer minPrice) {
		this.minPrice = minPrice;
	}
	
	public Integer getMaxPrice() {
		return maxPrice;
	}
	
	public void setMaxPrice(Integer maxPrice) {
		this.maxPrice = maxPrice;
	}
	
	public void setSearchArea(MapCircle area){
		this.searchArea = area;
	}
	
	public MapCircle getSearchArea(){
		return this.searchArea;
	}
	
	/**
	 * create a query to find all relevant apartments to the query (apartments that match at least one of the query fields)
	 * @return a permissive query 
	 */
	public ParseQuery<ParseObject> getPermissiveParseQuery() {
		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
		
		if (getCity() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSApartmentDBConfig.ApartmentTable.NAME);
			query.whereEqualTo(HSApartmentDBConfig.ApartmentTable.Fields.CITY, getCity());
			queries.add(query);
		}
		if (getNeighborhood() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSApartmentDBConfig.ApartmentTable.NAME);
			query.whereEqualTo(HSApartmentDBConfig.ApartmentTable.Fields.NEIGHBORHOOD, getNeighborhood());
			queries.add(query);
		}
		if (getStreet() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSApartmentDBConfig.ApartmentTable.NAME);
			query.whereEqualTo(HSApartmentDBConfig.ApartmentTable.Fields.STREET, getStreet());
			queries.add(query);
		}
		if (getMinPrice() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSApartmentDBConfig.ApartmentTable.NAME);
			query.whereGreaterThanOrEqualTo(HSApartmentDBConfig.ApartmentTable.Fields.PRICE, getMinPrice());
			queries.add(query);
		}
		if (getMaxPrice() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSApartmentDBConfig.ApartmentTable.NAME);
			query.whereLessThanOrEqualTo(HSApartmentDBConfig.ApartmentTable.Fields.PRICE, getMaxPrice());
			queries.add(query);
		}
		if (getMinRooms() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSApartmentDBConfig.ApartmentTable.NAME);
			query.whereGreaterThanOrEqualTo(HSApartmentDBConfig.ApartmentTable.Fields.ROOMS, getMinRooms());
			queries.add(query);
		}
		if (getMaxRooms() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSApartmentDBConfig.ApartmentTable.NAME);
			query.whereLessThanOrEqualTo(HSApartmentDBConfig.ApartmentTable.Fields.ROOMS, getMaxRooms());
			queries.add(query);
		}
		if (getMinArea() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSApartmentDBConfig.ApartmentTable.NAME);
			query.whereGreaterThanOrEqualTo(HSApartmentDBConfig.ApartmentTable.Fields.AREA, getMinArea());
			queries.add(query);
		}
		
		if (getMaxArea() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSApartmentDBConfig.ApartmentTable.NAME);
			query.whereLessThanOrEqualTo(HSApartmentDBConfig.ApartmentTable.Fields.AREA, getMaxArea());
			queries.add(query);
		}
		
		ParseQuery<ParseObject> finalQuery = null;
		
		if (queries.size() == 0) {
			finalQuery = ParseQuery.getQuery(HSApartmentDBConfig.ApartmentTable.NAME);
		} else {
			finalQuery = ParseQuery.or(queries);
		}
		if (searchArea != null){
			LatLng center = searchArea.getCenter();
			ParseGeoPoint p = new ParseGeoPoint(center.latitude, center.longitude);
			finalQuery.whereWithinKilometers(HSApartmentDBConfig.ApartmentTable.Fields.MAP_LOCATION, p, searchArea.getRadius());
		}
		return finalQuery;
	}
}

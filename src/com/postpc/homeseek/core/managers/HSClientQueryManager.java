package com.postpc.homeseek.core.managers;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.postpc.homeseek.core.config.db.HSClientQueryDBConfig;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.HSClientQuery;
import com.postpc.homeseek.core.hsobjects.HSUser;

public class HSClientQueryManager extends HSObjectManager {

	public static HSClientQuery newClientQuery(HSUser client, HSApartmentQuery query) throws ParseException
	{
		HSClientQuery hsquery = HSClientQuery.create(client.getId(), query);

		// TODO handle notifications

		return hsquery;
	}

	public static void setClientQuery(HSUser client, HSApartmentQuery query) throws ParseException {
		// TODO Auto-generated method stub
		List<HSClientQuery> queries = getClientQueries(client);
		if (queries.size() > 0){
			//remove old query
			assert(queries.size() == 1);
			queries.get(0).delete();
		} 
		newClientQuery(client,query);
	}

	public static List<HSClientQuery> getClientQueries(HSUser client) throws ParseException
	{
		ParseQuery<ParseObject> query  = getClientQueryTableParseQuery();

		query.whereEqualTo(HSClientQueryDBConfig.ClientQueryTable.Fields.CLIENT_ID, client.getId());

		List<ParseObject> records = query.find();				

		return recordsToHSClientQuerys(records);
	}

	protected static ParseQuery<ParseObject> getClientQueryTableParseQuery()
	{
		return ParseQuery.getQuery(HSClientQueryDBConfig.ClientQueryTable.NAME);
	}

	protected static List<HSClientQuery> recordsToHSClientQuerys(List<ParseObject> records) {
		List<HSClientQuery> queriesList = new ArrayList<HSClientQuery>();

		for (ParseObject record: records){
			queriesList.add(new HSClientQuery(record));
		}

		return queriesList;
	}


	/**
	 * Find all users that looks for this kind of apartment in order to send them a notification 
	 * @param apartment
	 * @return The IDs of clients who need to be informed about the new apartment
	 * @throws ParseException
	 */
	public static List<String> getClientsIdsOnNewApartment(HSApartment apartment) throws ParseException {
		
		ParseQuery<ParseObject> permissiveQuery = createPermissiveQuery(apartment);
		List<ParseObject> clientsQueries = permissiveQuery.find();
		List<String> clientIds = new ArrayList<String>();
		
		for (ParseObject clientQuery: clientsQueries){
			if (apartment.getMapLocation() != null && clientQuery.has(HSClientQueryDBConfig.ClientQueryTable.Fields.MAP_AREA_CENTER)){
				ParseGeoPoint p = clientQuery.getParseGeoPoint(HSClientQueryDBConfig.ClientQueryTable.Fields.MAP_AREA_CENTER);
				LatLng queryAreaCenter = new LatLng(p.getLatitude(), p.getLongitude());
				Double queryAreaRadius = clientQuery.getDouble(HSClientQueryDBConfig.ClientQueryTable.Fields.MAP_AREA_RADIUS);
				Double distance = getDistance(queryAreaCenter,apartment.getMapLocation());		

				if (isApartmentFitsQuery(clientQuery, apartment) && distance <= queryAreaRadius){
					clientIds.add(clientQuery.getString(HSClientQueryDBConfig.ClientQueryTable.Fields.CLIENT_ID));
				}
			} 
			else {
				if (isApartmentFitsQuery(clientQuery, apartment)){
					clientIds.add(clientQuery.getString(HSClientQueryDBConfig.ClientQueryTable.Fields.CLIENT_ID));
				}	
			}
		}
		return clientIds;		
	}

	/**
	 * Check if the given apartment fits the given client-query
	 * @param clientQuery
	 * @param apartment
	 * @return true if the apartment fits the given client-query, false otherwise
	 */
	private static boolean isApartmentFitsQuery(ParseObject clientQuery, HSApartment apartment) {
		String queryCity = clientQuery.getString(HSClientQueryDBConfig.ClientQueryTable.Fields.CITY);
		String queryNeiborhood = clientQuery.getString(HSClientQueryDBConfig.ClientQueryTable.Fields.NEIGHBORHOOD);
		String queryStreet = clientQuery.getString(HSClientQueryDBConfig.ClientQueryTable.Fields.STREET);
		Integer queryMinRooms = clientQuery.has(HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_ROOMS) ? clientQuery.getInt(HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_ROOMS) : null;
		Integer queryMaxRooms = clientQuery.has(HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_ROOMS) ? clientQuery.getInt(HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_ROOMS) : null;
		Integer queryMinPrice = clientQuery.has(HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_PRICE) ? clientQuery.getInt(HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_PRICE) : null;
		Integer queryMaxPrice = clientQuery.has(HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_PRICE) ? clientQuery.getInt(HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_PRICE) : null;
		Integer queryMinArea = clientQuery.has(HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_AREA) ? clientQuery.getInt(HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_AREA) : null;
		Integer queryMaxArea = clientQuery.has(HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_AREA) ? clientQuery.getInt(HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_AREA) : null;
		
		return ((queryCity == null || apartment.getCity() == null || queryCity.equalsIgnoreCase(apartment.getCity())) &&
				(queryNeiborhood == null || apartment.getNeighborhood() == null || queryNeiborhood.equalsIgnoreCase(apartment.getNeighborhood())) &&
				(queryStreet == null || apartment.getStreet() == null || queryStreet.equalsIgnoreCase(apartment.getStreet())) &&
				(queryMinRooms == null || apartment.getRooms() == null || queryMinRooms <= apartment.getRooms()) &&
				(queryMaxRooms == null || apartment.getRooms() == null || queryMaxRooms >= apartment.getRooms()) &&
				(queryMinPrice == null || apartment.getPrice() == null || queryMinPrice <= apartment.getPrice()) &&
				(queryMaxPrice == null || apartment.getPrice() == null || queryMaxPrice >= apartment.getPrice()) &&
				(queryMinArea == null || apartment.getAreaSize() == null || queryMinArea <= apartment.getAreaSize()) &&
				(queryMaxArea == null || apartment.getAreaSize() == null || queryMaxArea >= apartment.getAreaSize()));		
	}
	
	/**
	 * calculate the distance (in kilometers) between two map locations
	 * @param location1
	 * @param location2
	 * @return distance between location1 and location2
	 */
	private static Double getDistance(LatLng location1, LatLng location2) {
		double lat1 = location1.latitude;
		double lat2 = location2.latitude;
		double lon1 = location1.longitude;
		double lon2 = location2.longitude;
		// Approximate Equirectangular - works if (lat1,lon1) ~ (lat2,lon2)
		double x = (lon2 - lon1) * Math.cos((lat1 + lat2) / 2);
		double y = (lat2 - lat1);
		double d = Math.sqrt(x * x + y * y);
		return d;
	}


	/**
	 * create a query to find all relevant client-queries (queries that match at least one of the apartment fields)
	 * @param apartment
	 * @return a permissive query
	 */
	private static ParseQuery<ParseObject> createPermissiveQuery(HSApartment apartment) {
		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
		
		if (apartment.getCity() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSClientQueryDBConfig.ClientQueryTable.NAME);
			query.whereEqualTo(HSClientQueryDBConfig.ClientQueryTable.Fields.CITY, apartment.getCity());
			queries.add(query);
		}
		if (apartment.getNeighborhood() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSClientQueryDBConfig.ClientQueryTable.NAME);
			query.whereEqualTo(HSClientQueryDBConfig.ClientQueryTable.Fields.NEIGHBORHOOD, apartment.getNeighborhood());
			queries.add(query);
		}
		if (apartment.getStreet() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSClientQueryDBConfig.ClientQueryTable.NAME);
			query.whereEqualTo(HSClientQueryDBConfig.ClientQueryTable.Fields.STREET, apartment.getStreet());
			queries.add(query);
		}
		if (apartment.getPrice() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSClientQueryDBConfig.ClientQueryTable.NAME);
			query.whereLessThanOrEqualTo(HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_PRICE, apartment.getPrice());
			query.whereGreaterThanOrEqualTo(HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_PRICE, apartment.getPrice());
			queries.add(query);
		}
		if (apartment.getRooms() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSClientQueryDBConfig.ClientQueryTable.NAME);
			query.whereLessThanOrEqualTo(HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_ROOMS, apartment.getRooms());
			query.whereGreaterThanOrEqualTo(HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_ROOMS, apartment.getRooms());
			queries.add(query);
		}
		if (apartment.getAreaSize() != null){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(HSClientQueryDBConfig.ClientQueryTable.NAME);
			query.whereLessThanOrEqualTo(HSClientQueryDBConfig.ClientQueryTable.Fields.MIN_AREA, apartment.getAreaSize());
			query.whereGreaterThanOrEqualTo(HSClientQueryDBConfig.ClientQueryTable.Fields.MAX_AREA, apartment.getAreaSize());
			queries.add(query);
		}
		
		return ParseQuery.or(queries);
	}
}

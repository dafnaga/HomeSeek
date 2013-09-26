package com.postpc.homeseek.core.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.postpc.homeseek.core.config.db.HSApartmentDBConfig;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.HSApartmentPhoto;
import com.postpc.homeseek.core.hsobjects.HSMeetup;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.hsobjects.exceptions.IllegalApartmentParametersException;
import com.postpc.homeseek.core.managers.exceptions.NoSuchApartmentException;
import com.postpc.homeseek.core.managers.exceptions.NoSuchUserException;
import com.postpc.homeseek.core.session.HSSessionManager;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

public class HSApartmentsManager extends HSObjectManager {

	private static final int NO_LIMIT = -1;
	protected static HSObjectCache<HSApartment> cache = new HSObjectCache<HSApartment>();

	public static HSApartment createNewApartment(String city, String neighborhood, String street, String buildingNumber, String apartmentNumber, Double rooms, Integer area, Integer price, LatLng mapLocation, String ownerComments)
			throws ParseException, UserNotLoggedInException, IllegalApartmentParametersException
			{
		HSUser owner = HSSessionManager.getActiveSession().getUser();

		HSApartment apartment = createNewApartment(city, neighborhood, street, buildingNumber, apartmentNumber, rooms, area, owner, price, mapLocation, ownerComments);
		return apartment;
			}

	public static HSApartment createNewApartment(String city, String neighborhood, String street, String buildingNumber, String apartmentNumber, Double rooms, Integer area, HSUser owner, Integer price, LatLng mapLocation, String ownerComments)
			throws ParseException, IllegalApartmentParametersException
			{
		HSApartment newApartment = HSApartment.create(city, neighborhood, street, buildingNumber, apartmentNumber, rooms, area, owner.getId(), price, mapLocation, ownerComments);
		HSNotificationsManager.onNewApartment(newApartment);
		cache.add(newApartment);

		return newApartment;
			}

	public static void updateApartment(String apartmentId, String neighborhood, String buildingNumber, String apartmentNumber, Double rooms, Integer area, Integer price, String ownerComments)
			throws ParseException, UserNotLoggedInException, NoSuchApartmentException
			{
		HSApartment apartment = getApartmentById(apartmentId);

		apartment.update(neighborhood, buildingNumber, apartmentNumber, rooms, area, price, ownerComments);
		HSNotificationsManager.onApartmentChange(apartment);

			}

	public static void removeApartment(HSApartment apartment) throws ParseException{

		// Notify the clients - TODO - move to a different thread?
		List<String> clientsIds = getApartmentClientsIds(apartment.getId());
		HSNotificationsManager.onApartmentRemove(clientsIds,apartment.getTitle());

		// Remove apartment form client_apartment table
		removeFromClientApartments(apartment.getId());

		// Remove apartment's meetups
		List<HSMeetup> meetups = HSMeetupsManager.getApartmentMeetups(apartment);
		for (HSMeetup hsMeetup : meetups) {
			hsMeetup.delete();
		}

		// Remove apartment's photos 
		List<HSApartmentPhoto> photos = getApartmentPhotos(apartment);
		for (HSApartmentPhoto photo : photos) {
			photo.delete();
		}

		// Remove apartment record
		apartment.delete();
	}

	public static List<HSApartment> findApartments(HSApartmentQuery apartmentQuery) throws ParseException
	{
		ParseQuery<ParseObject> query = apartmentQuery.getPermissiveParseQuery();

		List<HSApartment> relevantApartments = recordsToHSApartments(query.find());

		List<HSApartment> apartments = new ArrayList<HSApartment>();

		for (HSApartment apartment: relevantApartments){
			if (isApartmentFitsQuery(apartmentQuery, apartment)){
				apartments.add(apartment);
			}
		}
		return apartments;	
	}

	/**
	 * Check if the given apartment fits the given client-query
	 * @param apartmentQuery
	 * @param apartment
	 * @return true if the apartment fits the given client-query, false otherwise
	 */
	private static boolean isApartmentFitsQuery(HSApartmentQuery apartmentQuery, HSApartment apartment) {
		String queryCity = apartmentQuery.getCity();
		String queryNeiborhood = apartmentQuery.getNeighborhood();
		String queryStreet = apartmentQuery.getStreet();
		Double queryMinRooms = apartmentQuery.getMinRooms();
		Double queryMaxRooms = apartmentQuery.getMaxRooms();
		Integer queryMinPrice = apartmentQuery.getMinPrice();
		Integer queryMaxPrice = apartmentQuery.getMaxPrice();
		Integer queryMinArea = apartmentQuery.getMinArea();
		Integer queryMaxArea = apartmentQuery.getMaxArea();
		
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

	public static HSApartment getApartmentById(String apartmentId) throws NoSuchApartmentException, ParseException
	{
		HSApartment hsApartment = cache.get(apartmentId);
		if (hsApartment != null){
			return hsApartment;
		}

		ParseQuery<ParseObject> query = getApartmentTableParseQuery();
		ParseObject apartment = query.get(apartmentId);		

		// At most one record should be returned
		if (apartment == null){
			System.err.println("Invalid apartmentId: " + apartmentId);
			throw new NoSuchApartmentException();
		}

		hsApartment = new HSApartment(apartment);
		cache.add(hsApartment);
		return hsApartment;
	}

	public static List<HSApartment> getOwnerApartments(HSUser owner) throws ParseException 
	{
		ParseQuery<ParseObject> query = getApartmentTableParseQuery();

		query.whereEqualTo(HSApartmentDBConfig.ApartmentTable.Fields.OWNER_ID, owner.getId());
		List<ParseObject> records = query.find();

		return recordsToHSApartments(records);
	}

	public static List<HSApartment> getClientApartments(HSUser client) throws ParseException, NoSuchApartmentException 
	{
		ParseQuery<ParseObject> apartmentsIdsQuery = getsClientApartmentLinkParseQuery();
		apartmentsIdsQuery.whereEqualTo(HSApartmentDBConfig.ClientApartmentLink.Fields.CLIENT_ID, client.getId());
		List<ParseObject> clientRecords = apartmentsIdsQuery.find();

		List<String> apartmentIds = new ArrayList<String>();
		for (ParseObject record: clientRecords){
			apartmentIds.add(record.getString(HSApartmentDBConfig.ClientApartmentLink.Fields.APARTMENT_ID));
		}

		ParseQuery<ParseObject> apartmentsObjectsQuery = getApartmentTableParseQuery();
		apartmentsObjectsQuery.whereContainedIn(HSApartmentDBConfig.ApartmentTable.Fields.OBJECT_ID, apartmentIds);

		List<ParseObject> apartmentsParseObjects = apartmentsObjectsQuery.find();
		List<HSApartment> apartments = new ArrayList<HSApartment>();
		for (ParseObject apartmentParseObj: apartmentsParseObjects){
			apartments.add(new HSApartment(apartmentParseObj));
		}
		return apartments;
	}

	protected static List<HSApartment> recordsToHSApartments(List<ParseObject> records) {
		ArrayList<HSApartment> apartmentList = new ArrayList<HSApartment>();

		// HACK - clear the cached photos on each search
		mainPhotoCache.clear();
		
		for (ParseObject record: records){
			HSApartment apartment;

			apartment = cache.get(record.getObjectId());
			if (apartment == null){
				apartment = new HSApartment(record);
				cache.add(apartment);
			}
			apartmentList.add(apartment);
		}

		return apartmentList;
	}

	protected static List<HSApartmentPhoto> recordsToHSApartmentPhotos(List<ParseObject> records) {
		ArrayList<HSApartmentPhoto> photoList = new ArrayList<HSApartmentPhoto>();

		for (ParseObject record: records){
			photoList.add(new HSApartmentPhoto(record));
		}

		return photoList;
	}

	protected static ParseQuery<ParseObject> getApartmentTableParseQuery()
	{
		return ParseQuery.getQuery(HSApartmentDBConfig.ApartmentTable.NAME);
	}

	protected static ParseQuery<ParseObject> getsClientApartmentLinkParseQuery()
	{
		return ParseQuery.getQuery(HSApartmentDBConfig.ClientApartmentLink.NAME);
	}

	protected static ParseQuery<ParseObject> getPhotoApartmentLinkParseQuery()
	{
		return ParseQuery.getQuery(HSApartmentDBConfig.PhotoApartmentLink.NAME);
	}

	public static HSApartmentPhoto addApartmentPhoto(HSApartment apartment, byte[] photoData, HSUser user, boolean isPublic) throws ParseException{

		return addApartmentPhoto(apartment.getId(), photoData, user.getId(), isPublic);
	}

	public static HSApartmentPhoto addApartmentPhoto(String apartmentId, byte[] photoData, String userId, boolean isPublic) throws ParseException{
		String photoId = UUID.randomUUID().toString();
		HSApartmentPhoto hsphoto = HSApartmentPhoto.create(apartmentId, userId, photoId, photoData, isPublic);
		if (isPublic){
			HSNotificationsManager.onApartmentChange(apartmentId);
		}
		mainPhotoCache.clear();
		return hsphoto;
	}

	public static void removeApartmentPhoto(HSApartmentPhoto photo) throws ParseException {

		photo.delete();
	}

	public static List<HSApartmentPhoto> getApartmentPhotos(HSApartment apartment, HSUser user) throws ParseException{
		return getApartmentPhotos(apartment.getId(), user.getId());
	}


	public static List<HSApartmentPhoto> getApartmentPhotos(String apartmentId, String userId) throws ParseException{
		return getApartmentPhotos(apartmentId, userId, NO_LIMIT); 
	}
	public static List<HSApartmentPhoto> getApartmentPhotos(String apartmentId, String userId, int limit) throws ParseException{
		ParseQuery<ParseObject> subQuery1 = getPhotoApartmentLinkParseQuery();
		ParseQuery<ParseObject> subQuery2 = getPhotoApartmentLinkParseQuery();

		subQuery1.whereEqualTo(HSApartmentDBConfig.PhotoApartmentLink.Fields.APARTMENT_ID, apartmentId);
		subQuery1.whereEqualTo(HSApartmentDBConfig.PhotoApartmentLink.Fields.VISIBILITY, HSApartmentPhoto.VISIBILITY_PUBLIC);

		subQuery2.whereEqualTo(HSApartmentDBConfig.PhotoApartmentLink.Fields.APARTMENT_ID, apartmentId);
		subQuery2.whereEqualTo(HSApartmentDBConfig.PhotoApartmentLink.Fields.VISIBILITY, HSApartmentPhoto.VISIBILITY_PRIVATE);
		subQuery2.whereEqualTo(HSApartmentDBConfig.PhotoApartmentLink.Fields.ALLOWED_USERS, userId);

		ParseQuery<ParseObject> finalQuery = ParseQuery.or(Arrays.asList(subQuery1, subQuery2));
		if (limit != NO_LIMIT) {
			finalQuery.orderByAscending(HSApartmentDBConfig.PhotoApartmentLink.Fields.CREATE_DATE);
			finalQuery.setLimit(limit);
		}

		List<ParseObject> records = finalQuery.find();
		return recordsToHSApartmentPhotos(records);

	}

	public static List<HSApartmentPhoto> getApartmentPhotos(HSApartment apartment) throws ParseException{
		return getApartmentPhotos(apartment.getId());
	}

	public static List<HSApartmentPhoto> getApartmentPhotos(String apartmentId) throws ParseException{
		String curUser = "";

		try {
			curUser = HSSessionManager.getActiveSession().getUser().getId();
		} catch (UserNotLoggedInException e) {
			// Show only the owner photos
		}

		return getApartmentPhotos(apartmentId, curUser); 
	}

	public static void saveClientComment(String clientId, String apartmentId, String comment) throws ParseException{
		List<ParseObject> records = getClientApartmentLinkRecords(clientId, apartmentId, NO_LIMIT);

		for (ParseObject record : records) {
			record.put(HSApartmentDBConfig.ClientApartmentLink.Fields.CLIENT_COMMENTS, comment);
			record.save();
		}
	}

	public static void addApartmentToClient(String clientId, String apartmentId) throws ParseException {
		//if the apartment is not already in the list, add it
		if (!isClientApartmentLinkRecords(clientId, apartmentId)){
			ParseObject clientLinkObject = new ParseObject(HSApartmentDBConfig.ClientApartmentLink.NAME);		
			clientLinkObject.put(HSApartmentDBConfig.ClientApartmentLink.Fields.CLIENT_ID, clientId);
			clientLinkObject.put(HSApartmentDBConfig.ClientApartmentLink.Fields.APARTMENT_ID, apartmentId);
			clientLinkObject.put(HSApartmentDBConfig.ClientApartmentLink.Fields.CLIENT_COMMENTS, "");
			clientLinkObject.save();
			HSNotificationsManager.subscribeToApartment(apartmentId);
		}
	}

	/**
	 * The function:
	 * 1. Removes the apartment from the given user apartments list
	 * 2. Removes the apartment from the client's channels
	 * 3. Removes the clien't scheduled meetups in the apartment
	 * 
	 * @param clientId
	 * @param apartmentId
	 * @throws ParseException
	 * @throws UserNotLoggedInException 
	 */
	public static void removeApartmentFromCurrentClient(String apartmentId) throws ParseException, UserNotLoggedInException {
		String clientId = HSSessionManager.getActiveSession().getUser().getId();
		// If the apartment is in the list, remove it
		List<ParseObject> records = getClientApartmentLinkRecords(clientId, apartmentId, NO_LIMIT);
		if (records.size() > 0){
			for (ParseObject record: records){
				record.delete();
			}
			HSNotificationsManager.unSubscribeApartment(apartmentId);
		}

		// Remove client from aparmenet's meetups
		List<HSMeetup> apartmentMeetups = HSMeetupsManager.getApartmentMeetups(apartmentId);
		for (HSMeetup hsMeetup : apartmentMeetups) {
			hsMeetup.unattendId(clientId);
		}
	}

	private static List<ParseObject> getClientApartmentLinkRecords(String clientId, String apartmentId, int limit) throws ParseException{
		ParseQuery<ParseObject> query = getsClientApartmentLinkParseQuery();

		query.whereEqualTo(HSApartmentDBConfig.ClientApartmentLink.Fields.CLIENT_ID, clientId);
		query.whereEqualTo(HSApartmentDBConfig.ClientApartmentLink.Fields.APARTMENT_ID, apartmentId);
		if (limit != NO_LIMIT && limit > 0) {
			query.setLimit(limit);
		}

		List<ParseObject> records = query.find();
		return records;
	}

	public static boolean isClientApartmentLinkRecords(String clientId, String apartmentId) throws ParseException {
		List<ParseObject> records = getClientApartmentLinkRecords(clientId, apartmentId, 1);
		return (records.size() > 0);
	}

	public static List<HSUser> getApartmentClients(String apartmentId) throws ParseException {
		ParseQuery<ParseObject> clientsIdsQuery = getsClientApartmentLinkParseQuery();
		clientsIdsQuery.whereEqualTo(HSApartmentDBConfig.ClientApartmentLink.Fields.APARTMENT_ID, apartmentId);
		List<ParseObject> clientRecords = clientsIdsQuery.find();

		List<HSUser> clients = new ArrayList<HSUser>();
		for (ParseObject record: clientRecords){
			try {
				clients.add(HSUsersManager.getUserById(record.getString(HSApartmentDBConfig.ClientApartmentLink.Fields.CLIENT_ID)));
			} catch (NoSuchUserException e) {
				// Skip
			}
		}

		return clients;			
	}	

	public static List<String> getApartmentClientsIds(String apartmentId) throws ParseException {
		ParseQuery<ParseObject> clientsIdsQuery = getsClientApartmentLinkParseQuery();
		clientsIdsQuery.whereEqualTo(HSApartmentDBConfig.ClientApartmentLink.Fields.APARTMENT_ID, apartmentId);
		List<ParseObject> clientRecords = clientsIdsQuery.find();

		List<String> clientsids = new ArrayList<String>();
		for (ParseObject record: clientRecords){
			clientsids.add(record.getString(HSApartmentDBConfig.ClientApartmentLink.Fields.CLIENT_ID));
		}

		return clientsids;			
	}	

	private static void removeFromClientApartments(String apartmentId) throws ParseException {
		ParseQuery<ParseObject> clientsIdsQuery = getsClientApartmentLinkParseQuery();
		clientsIdsQuery.whereEqualTo(HSApartmentDBConfig.ClientApartmentLink.Fields.APARTMENT_ID, apartmentId);
		List<ParseObject> clientRecords = clientsIdsQuery.find();

		for (ParseObject record: clientRecords){
			record.delete();
		}		
	}

	public static String getApartmentClientComments(HSApartment apartment, HSUser user) throws ParseException {
		ParseQuery<ParseObject> query = getsClientApartmentLinkParseQuery();
		query.whereEqualTo(HSApartmentDBConfig.ClientApartmentLink.Fields.APARTMENT_ID, apartment.getId());
		query.whereEqualTo(HSApartmentDBConfig.ClientApartmentLink.Fields.CLIENT_ID, user.getId());

		List<ParseObject> commentRecords = query.find();
		assert(commentRecords.size() <= 1);

		for (ParseObject record: commentRecords){
			return record.getString(HSApartmentDBConfig.ClientApartmentLink.Fields.CLIENT_COMMENTS);
		}
		return null;
	}

	public static void SendApartmentToUser(HSApartment apartment, HSUser fromUser, HSUser toUser) throws ParseException {
		SendApartmentToUser(apartment, fromUser, toUser.getId());
	}
	
	public static void SendApartmentToUser(HSApartment apartment, HSUser fromUser, String toUserId) throws ParseException {

		// Add toUserId to allowed_users of each of the apartment private photos
		List<HSApartmentPhoto> userPhotos = getApartmentUserPhotos(apartment, fromUser);
		for (HSApartmentPhoto photo : userPhotos) {
			photo.addAllowedUser(toUserId);
			photo.save();
		}

		// Send as instant message
		HSInstantMessagesManager.newInstantMessage(fromUser.getId(), toUserId, new Date(), apartment);
	}

	public static List<HSApartmentPhoto> getApartmentUserPhotos(HSApartment apartment, HSUser user) throws ParseException {

		ParseQuery<ParseObject> query = getPhotoApartmentLinkParseQuery();

		query.whereEqualTo(HSApartmentDBConfig.PhotoApartmentLink.Fields.APARTMENT_ID, apartment.getId());
		query.whereEqualTo(HSApartmentDBConfig.PhotoApartmentLink.Fields.VISIBILITY, HSApartmentPhoto.VISIBILITY_PRIVATE);
		query.whereEqualTo(HSApartmentDBConfig.PhotoApartmentLink.Fields.USER_ID, user.getId());

		List<ParseObject> records = query.find();
		return recordsToHSApartmentPhotos(records);
	}

	public static void sendApartmentPhotoToUser(HSApartmentPhoto photo, HSUser fromUser, HSUser toUser) throws ParseException{		
		// Allow user to see the photo
		photo.addAllowedUser(toUser.getId());
		photo.save();

		// Send as instant message
		HSInstantMessagesManager.newInstantMessage(fromUser.getId(), toUser.getId(), new Date(), photo);
	}

	public static HSApartmentPhoto getApartmentPhotoById(String photoId) throws NoSuchPhotoException, ParseException {
		ParseQuery<ParseObject> query = getPhotoApartmentLinkParseQuery();
		ParseObject photo = query.get(photoId);		

		// At most one record should be returned
		if (photo == null){
			System.err.println("Invalid photoId: " + photoId);
			throw new NoSuchPhotoException();
		}

		return new HSApartmentPhoto(photo);
	}

	static private Map<HSApartment, HSApartmentPhoto> mainPhotoCache = new HashMap<HSApartment, HSApartmentPhoto>();
	
	public static HSApartmentPhoto getApartmentMainPhoto(HSApartment apartment) throws ParseException {
		String userId = "";

		// Is it in the cache?
		if (mainPhotoCache.containsKey(apartment)){
			return mainPhotoCache.get(apartment);
		}
		
		try {
			userId = HSSessionManager.getActiveSession().getUser().getId();
		} catch (UserNotLoggedInException e) {
			// Show only the owner photos
		}
		List<HSApartmentPhoto> photos = getApartmentPhotos(apartment.getId(), userId, 1);
		HSApartmentPhoto photo; 		
		if (photos.size() == 0) {
			photo = null;
		} else {
			photo = photos.get(0);
		}

		// Add to cache
		mainPhotoCache.put(apartment, photo);
		return photo;
	}

}

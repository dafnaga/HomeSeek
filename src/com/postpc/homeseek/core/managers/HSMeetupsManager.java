package com.postpc.homeseek.core.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.postpc.homeseek.core.config.db.HSApartmentDBConfig;
import com.postpc.homeseek.core.config.db.HSMeetupDBConfig;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.HSMeetup;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.exceptions.NoSuchMeetupException;

public class HSMeetupsManager extends HSObjectManager {

	public static HSMeetup createMeetup(HSApartment forApartment, Date startDate, Date endDate) throws ParseException
	{
		return HSMeetup.create(forApartment.getId(), startDate, endDate);
	}
	
	public static HSMeetup getMeetupById(String meetupId) throws NoSuchMeetupException, ParseException
	{
		ParseQuery<ParseObject> query = getMeetupTableParseQuery();
		
		query.whereEqualTo(HSApartmentDBConfig.ApartmentTable.Fields.OBJECT_ID, meetupId);
		List<ParseObject> records = query.find();
		
		// At most one record should be returned
		if (records.size() != 1){
			throw new NoSuchMeetupException();
		}
		
		return new HSMeetup(records.get(0));		
	}
	
	public static void removeMeetup(HSMeetup meetup) throws ParseException
	{
		// notify attenders, etc..
		HSNotificationsManager.onMeetupRemoval(meetup);
		meetup.delete(); // must be done at the end
	}	

	public static List<HSMeetup> getApartmentMeetups(HSApartment apartment) throws ParseException
	{
		return getApartmentMeetups(apartment.getId());		
	}
	
	private static List<ParseObject> getApartmentMeetupsParseObjects(String apartmentId) throws ParseException{
		
		ParseQuery<ParseObject> query = getMeetupTableParseQuery();

		query.whereEqualTo(HSMeetupDBConfig.MeetupsTable.Fields.APARTMENT_ID, apartmentId);
		query.whereGreaterThanOrEqualTo(HSMeetupDBConfig.MeetupsTable.Fields.END_DATE, new Date());
		query.orderByAscending(HSMeetupDBConfig.MeetupsTable.Fields.START_DATE);

		return query.find();
	}
	
	public static List<HSMeetup> getApartmentMeetups(String apartmentId) throws ParseException
	{
		List<ParseObject> records = getApartmentMeetupsParseObjects(apartmentId);
		
		return recordsToHSMeetups(records);		
	}
	
	public static void removeApartmentMeetups(String apartmentId) throws ParseException {
		
		List<ParseObject> records = getApartmentMeetupsParseObjects(apartmentId);
		for (ParseObject parseObject : records) {
			parseObject.deleteEventually();
		}
	}

	public static List<HSMeetup> getClientMeetups(HSUser client) throws ParseException
	{
		ParseQuery<ParseObject> query = getMeetupTableParseQuery();
		
		query.whereEqualTo(HSMeetupDBConfig.MeetupsTable.Fields.ATTENDERS, client.getId());
		
		List<ParseObject> records = query.find();
		
		return recordsToHSMeetups(records);		
	}
	
	public static List<HSMeetup> getClientMeetups(HSUser client, HSApartment apartment) throws ParseException{
		return getClientMeetups(client.getId(), apartment.getId());
	}
	
	public static List<HSMeetup> getClientMeetups(String clientId, String apartmentId) throws ParseException
	{
		ParseQuery<ParseObject> query = getMeetupTableParseQuery();
		
		query.whereEqualTo(HSMeetupDBConfig.MeetupsTable.Fields.ATTENDERS, clientId);
		query.whereEqualTo(HSMeetupDBConfig.MeetupsTable.Fields.APARTMENT_ID, apartmentId);
		
		List<ParseObject> records = query.find();
		
		return recordsToHSMeetups(records);		
	}
	
	public static List<HSUser> getMeetupAttanders(HSMeetup meetup) throws ParseException
	{
		return meetup.getAttenders();
	}
	
	protected static ParseQuery<ParseObject> getMeetupTableParseQuery()
	{
		return ParseQuery.getQuery(HSMeetupDBConfig.MeetupsTable.NAME);
	}
	
	protected static List<HSMeetup> recordsToHSMeetups(List<ParseObject> records) {
		ArrayList<HSMeetup> meetupList = new ArrayList<HSMeetup>();
		
		for (ParseObject record: records){
			meetupList.add(new HSMeetup(record));
		}
		
		return meetupList;
	}

	public static String getMeetupTimeAsString(String meetupId) throws NoSuchMeetupException, ParseException {
		
		return getMeetupTimeAsString(getMeetupById(meetupId));
	}
	
	public static String getMeetupTimeAsString(HSMeetup meetup) throws NoSuchMeetupException, ParseException {
		
		return meetup.getMeetupTimeAsString();
	}

	public static List<String> getMeetupAttandersIds(HSMeetup meetup) {
		return meetup.getAttendersIds();
	}

	public static void setClientPresence(HSMeetup meetup, HSUser client, boolean isAttending) throws ParseException {

		// send the owner notification about the change
		HSNotificationsManager.onMeetupClientChange(meetup, client, isAttending);
		if (isAttending) {
			meetup.attendId(client.getId());
		} else {
			meetup.unattendId(client.getId());
		}
		
	}
}

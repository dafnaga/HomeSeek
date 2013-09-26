package com.postpc.homeseek.core.hsobjects;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.postpc.homeseek.core.config.db.HSMeetupDBConfig;
import com.postpc.homeseek.core.managers.HSUsersManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchUserException;

public class HSMeetup extends HSObject{

	private ParseObject meetupDbObject;

	public HSMeetup(ParseObject meetupDbObject)
	{
		super(meetupDbObject);
		this.meetupDbObject = meetupDbObject;
	}

	public static HSMeetup create(String apartmentId, Date startDate, Date endDate) throws ParseException
	{
		ParseObject dbObject = new ParseObject(HSMeetupDBConfig.MeetupsTable.NAME);
		dbObject.put(HSMeetupDBConfig.MeetupsTable.Fields.APARTMENT_ID, apartmentId);
		dbObject.put(HSMeetupDBConfig.MeetupsTable.Fields.START_DATE, startDate);
		dbObject.put(HSMeetupDBConfig.MeetupsTable.Fields.END_DATE, endDate);

		dbObject.save();
		
		return new HSMeetup(dbObject);
	}
	
	public void setStartDate(Date startDate)
	{
		meetupDbObject.put(HSMeetupDBConfig.MeetupsTable.Fields.START_DATE, startDate);
	}
	
	public Date getStartDate()
	{
		return meetupDbObject.getDate(HSMeetupDBConfig.MeetupsTable.Fields.START_DATE);
	}

	public void setEndDate(Date endDate)
	{
		meetupDbObject.put(HSMeetupDBConfig.MeetupsTable.Fields.END_DATE, endDate);
	}
	
	public Date getEndDate()
	{
		return meetupDbObject.getDate(HSMeetupDBConfig.MeetupsTable.Fields.END_DATE);
	}
		
	public void attendId(String userId) throws ParseException
	{
		meetupDbObject.addUnique(HSMeetupDBConfig.MeetupsTable.Fields.ATTENDERS, userId);
		meetupDbObject.save();
	}
	
	public String getApartmentId() {
		return getStringField(HSMeetupDBConfig.MeetupsTable.Fields.APARTMENT_ID);
	}
	
	public void unattendId(String userId) throws ParseException
	{
		meetupDbObject.removeAll(HSMeetupDBConfig.MeetupsTable.Fields.ATTENDERS, Arrays.asList(userId));
		meetupDbObject.save();
	}
	
	public List<String> getAttendersIds()
	{
		return meetupDbObject.getList(HSMeetupDBConfig.MeetupsTable.Fields.ATTENDERS);
	}

	public List<HSUser> getAttenders() throws ParseException
	{
		List<HSUser> users = new ArrayList<HSUser>();
		List<String> ids = meetupDbObject.getList(HSMeetupDBConfig.MeetupsTable.Fields.ATTENDERS);
		
		if (ids == null){
			return users; 
		}
		
		for (String id : ids){
			try {
				users.add(HSUsersManager.getUserById(id));
			} catch (NoSuchUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				// TODO should never happen
			}
		}
		
		return users;
	}

	public String getDateAsString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(getStartDate());
	}

	public Integer getNumOfVisitors() {
		List<String> ids = getAttendersIds();
		if (ids != null){
			return ids.size();
		} else {
			return 0;
		}
	}

	public String getHoursAsString() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(getStartDate()) + " - " + sdf.format(getEndDate());
	}

	public String getMeetupTimeAsString() {
		
		return getDateAsString() + ": " + getHoursAsString();
	}
	
}

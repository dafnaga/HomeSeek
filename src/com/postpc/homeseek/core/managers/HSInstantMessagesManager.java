package com.postpc.homeseek.core.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.postpc.homeseek.core.config.db.HSInstantMessageDBConfig;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.HSApartmentPhoto;
import com.postpc.homeseek.core.hsobjects.HSInstantMessage;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.exceptions.NoSuchUserException;

public class HSInstantMessagesManager extends HSObjectManager{

	private static final int DAY_IN_MILLISEC = 86400000;

	public static HSInstantMessage newInstantMessage(HSUser fromUser, HSUser toUser, Date sendDate, String messageText) throws ParseException
	{
		return newInstantMessage(fromUser.getId(), toUser.getId(), sendDate, messageText);
	}
	
	public static HSInstantMessage newInstantMessage(String fromUserId, String toUserId, Date sendDate, String messageText) throws ParseException
	{
		HSInstantMessage message = HSInstantMessage.create(fromUserId, toUserId, sendDate, HSInstantMessage.MESSAGE_TYPE.TYPE_TEXT, messageText);		
		HSNotificationsManager.onNewInstantMessage(message.getId(), fromUserId, toUserId);
		
		return message;
	}

	public static List<HSInstantMessage> getRecentConversation(HSUser firstUser, HSUser secondUser, int amount) throws ParseException
	{
		return getRecentConversation(firstUser.getId(), secondUser.getId(), amount);
	}
	
	public static List<HSInstantMessage> getRecentConversation(String firstUserId, String secondUserId, int amount) throws ParseException	
	{
		ParseQuery<ParseObject> queryTo = getInstantMessagesTableParseQuery();
		ParseQuery<ParseObject> queryFrom = getInstantMessagesTableParseQuery();
		
		queryTo.whereEqualTo(HSInstantMessageDBConfig.InstantMessagesTable.Fields.FROM_USER_ID, firstUserId);
		queryTo.whereEqualTo(HSInstantMessageDBConfig.InstantMessagesTable.Fields.TO_USER_ID, secondUserId);

		queryFrom.whereEqualTo(HSInstantMessageDBConfig.InstantMessagesTable.Fields.TO_USER_ID, firstUserId);
		queryFrom.whereEqualTo(HSInstantMessageDBConfig.InstantMessagesTable.Fields.FROM_USER_ID, secondUserId);
		
		ParseQuery<ParseObject> finalQuery  = ParseQuery.or(Arrays.asList(queryTo, queryFrom));
		finalQuery.addDescendingOrder(HSInstantMessageDBConfig.InstantMessagesTable.Fields.CREATE_DATE);
		finalQuery.setLimit(amount);
		
		List<ParseObject> records = finalQuery.find();				
		Collections.reverse(records);
		
		return recordsToHSInstantMessages(records);
	}
	
	public static List<HSUser> getClientRecentContacts(String userId, int numOfDays) throws ParseException {
		ParseQuery<ParseObject> subQuery1 = getInstantMessagesTableParseQuery();
		ParseQuery<ParseObject> subQuery2 = getInstantMessagesTableParseQuery();
		
		subQuery1.whereEqualTo(HSInstantMessageDBConfig.InstantMessagesTable.Fields.TO_USER_ID, userId);
		
		subQuery2.whereEqualTo(HSInstantMessageDBConfig.InstantMessagesTable.Fields.FROM_USER_ID, userId);
		
		ParseQuery<ParseObject> finalQuery  = ParseQuery.or(Arrays.asList(subQuery1, subQuery2));
		finalQuery.whereGreaterThanOrEqualTo(HSInstantMessageDBConfig.InstantMessagesTable.Fields.CREATE_DATE, 
											 new Date(System.currentTimeMillis() - numOfDays * DAY_IN_MILLISEC));
		finalQuery.addDescendingOrder(HSInstantMessageDBConfig.InstantMessagesTable.Fields.CREATE_DATE);
		
		List<ParseObject> records = finalQuery.find();	
		List<HSUser> contacts = new ArrayList<HSUser>();
		try {
			HSUser curUser = HSUsersManager.getUserById(userId);
			for (ParseObject record: records){

				HSUser user = HSUsersManager.getUserById(record.getString(HSInstantMessageDBConfig.InstantMessagesTable.Fields.FROM_USER_ID));
				if (!contacts.contains(user) && !user.equals(curUser)){
					contacts.add(user);
				}
				user = HSUsersManager.getUserById(record.getString(HSInstantMessageDBConfig.InstantMessagesTable.Fields.TO_USER_ID));
				if (!contacts.contains(user) && !user.equals(curUser)){
					contacts.add(user);
				}

			}
		} catch (NoSuchUserException e) {
			// Skip
		}
		return contacts;
	}

	protected static ParseQuery<ParseObject> getInstantMessagesTableParseQuery()
	{
		return ParseQuery.getQuery(HSInstantMessageDBConfig.InstantMessagesTable.NAME);
	}

	protected static List<HSInstantMessage> recordsToHSInstantMessages(List<ParseObject> records) {
		List<HSInstantMessage> instantMessagesList = new ArrayList<HSInstantMessage>();
		
		for (ParseObject record: records){
			instantMessagesList.add(new HSInstantMessage(record));
		}
		
		return instantMessagesList;
	}

	public static HSInstantMessage newInstantMessage(String fromUserId, String toUserId, Date sendDate, HSApartmentPhoto photo) throws ParseException {
		HSInstantMessage message = HSInstantMessage.create(fromUserId, toUserId, sendDate, HSInstantMessage.MESSAGE_TYPE.TYPE_PHOTO, photo.getId());		
		HSNotificationsManager.onNewInstantMessage(message.getId(), fromUserId, toUserId);
		
		return message;
	}

	public static HSInstantMessage newInstantMessage(String fromUserId, String toUserId, Date sendDate, HSApartment apartment) throws ParseException {
		HSInstantMessage message = HSInstantMessage.create(fromUserId, toUserId, sendDate, HSInstantMessage.MESSAGE_TYPE.TYPE_APARTMENT, apartment.getId());		
		HSNotificationsManager.onNewInstantMessage(message.getId(), fromUserId, toUserId);
		
		return message;
	}

}

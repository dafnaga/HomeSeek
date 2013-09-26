package com.postpc.homeseek.core.hsobjects;

import java.util.Date;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.postpc.homeseek.core.config.db.HSInstantMessageDBConfig;


public class HSInstantMessage extends HSObject{

	private ParseObject instantMessageDbObject;

	public static class MESSAGE_TYPE {
		public static final int TYPE_TEXT = 0;
		public static final int TYPE_PHOTO = 1;
		public static final int TYPE_APARTMENT = 2;
		// TODO what else?
	}
	
	public static HSInstantMessage create(String fromUserId, String toUserId, Date sendDate, int messageType, String messageData) throws ParseException
	{
		ParseObject dbObject = new ParseObject(HSInstantMessageDBConfig.InstantMessagesTable.NAME);
		
		dbObject.put(HSInstantMessageDBConfig.InstantMessagesTable.Fields.FROM_USER_ID, fromUserId);
		dbObject.put(HSInstantMessageDBConfig.InstantMessagesTable.Fields.TO_USER_ID, toUserId);
		dbObject.put(HSInstantMessageDBConfig.InstantMessagesTable.Fields.SEND_DATE, sendDate);
		dbObject.put(HSInstantMessageDBConfig.InstantMessagesTable.Fields.MESSAGE_TYPE, messageType);
		dbObject.put(HSInstantMessageDBConfig.InstantMessagesTable.Fields.MESSAGE_DATA, messageData);
		dbObject.save();
		
		return new HSInstantMessage(dbObject);
	}
	
	public HSInstantMessage(ParseObject instantMessageDbObject)
	{
		super(instantMessageDbObject);
		this.instantMessageDbObject = instantMessageDbObject;
	}

	public String getFromUserId()
	{
		return instantMessageDbObject.getString(HSInstantMessageDBConfig.InstantMessagesTable.Fields.FROM_USER_ID);
	}

	public void setFromUserId(String fromUserId)
	{
		instantMessageDbObject.put(HSInstantMessageDBConfig.InstantMessagesTable.Fields.FROM_USER_ID, fromUserId);
	}

	public String getToUserId()
	{
		return instantMessageDbObject.getString(HSInstantMessageDBConfig.InstantMessagesTable.Fields.FROM_USER_ID);
	}

	public void setToUserId(String toUserId)
	{
		instantMessageDbObject.put(HSInstantMessageDBConfig.InstantMessagesTable.Fields.TO_USER_ID, toUserId);
	}

	public Date getSendDate()
	{
		return instantMessageDbObject.getDate(HSInstantMessageDBConfig.InstantMessagesTable.Fields.SEND_DATE);
	}

	public void setSendDate(Date sendDate)
	{
		instantMessageDbObject.put(HSInstantMessageDBConfig.InstantMessagesTable.Fields.SEND_DATE, sendDate);
	}
	
	public int getMessageType()
	{
		return instantMessageDbObject.getInt(HSInstantMessageDBConfig.InstantMessagesTable.Fields.MESSAGE_TYPE);
	}

	public void setMessageType(int messageType)
	{
		instantMessageDbObject.put(HSInstantMessageDBConfig.InstantMessagesTable.Fields.MESSAGE_TYPE, messageType);
	}
	
	public String getMessageData()
	{
		return instantMessageDbObject.getString(HSInstantMessageDBConfig.InstantMessagesTable.Fields.MESSAGE_DATA);
	}

	public void setMessageData(String messageData)
	{
		instantMessageDbObject.put(HSInstantMessageDBConfig.InstantMessagesTable.Fields.MESSAGE_DATA, messageData);
	}	
	
}

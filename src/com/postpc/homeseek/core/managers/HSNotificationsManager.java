package com.postpc.homeseek.core.managers;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.postpc.homeseek.ClientApartmentInfoActivity;
import com.postpc.homeseek.HomeseekApplication;
import com.postpc.homeseek.core.config.db.HSInstallationDBConfig;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.HSMeetup;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.exceptions.NoSuchApartmentException;

public class HSNotificationsManager {

	private static final String TYPE_ACTIOM_HOMESEEK_NOTIFICATION = "com.postpc.homeseek.HOMESEEK_NOTIFICATION";

	public static final String ARG_NOTIFICATION_TYPE = "notification_type";

	public static final String TYPE_NEW_IM = "type_new_im";
	public static final String ARG_INSTANT_MSG_ID = "arg_instant_msg_id";
	public static final String ARG_FROM_USER_ID = "arg_from_user_id";
	
	public static final String TYPE_APARTMENT_UPDATED = "type_apartment_updated";
	public static final String ARG_APARTMENT_ID = "arg_apartment_id";
	
	public static final String TYPE_NEW_APARTMENT = "type_new_apartment";
	public static final String TYPE_APARTMENT_REMOVE = "type_apartment_removal";
	public static final String TYPE_MEETUP_REMOVE = "type_meetup_removal";
	
	public static final String ARG_ALERT = "msg";
	public static final String ARG_ACTION = "action";	
	
	public static final Object TYPE_MEETUP_ATTENDERS_CHANGE = "type_meetup_attenders_updated";
	public static final String ARG_MEETUP_ID = "arg_meetup_id";

	private static final String APARTMENT_PREFIX = "apartment_";

	public static void onApartmentChange(String apartmentId)
	{
		try {
			HSApartment apartment = HSApartmentsManager.getApartmentById(apartmentId);
			onApartmentChange(apartment);
		} catch (NoSuchApartmentException e) {
		} catch (ParseException e) {
		} 
	}
	
	public static void onApartmentChange(HSApartment apartment)
	{
		String msg = "Change in apartment!";
		msg = "The apartment at " + apartment.getStreet() + ", " + apartment.getCity() + " was updated!";
		
    	JSONObject data = new JSONObject();
    	try {
	        data.put(ARG_NOTIFICATION_TYPE, TYPE_APARTMENT_UPDATED);
	        data.put(ARG_APARTMENT_ID, apartment.getId());
	        data.put(ARG_ALERT, msg);
	        data.put(ARG_ACTION, TYPE_ACTIOM_HOMESEEK_NOTIFICATION);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		pushChannelNotification(data, getApartmentChannel(apartment.getId()));
	}
	
	public static void onApartmentRemove(Collection<String> userIds, String apartmentTitle){
		// Build the notification data
    	JSONObject data = new JSONObject();
    	try {
    		data.put(ARG_NOTIFICATION_TYPE, TYPE_APARTMENT_REMOVE);
	        data.put(ARG_ALERT, "Thr apartemnt at " + apartmentTitle + ", is no longer available");   
	        data.put(ARG_ACTION, TYPE_ACTIOM_HOMESEEK_NOTIFICATION);
    	} catch (JSONException e) {
    		e.printStackTrace();
    		return;
    	}
		
		pushMsgToUsers(userIds, data, null);
	}
	
	/**
	 * Update relevant clients on the new apartment. 
	 * The function creates other thread that handles the update.
	 * @param apartment The new apartment 
	 */
	public static void onNewApartment(final HSApartment apartment)
	{
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){

			@Override
			protected Void doInBackground(Void... params) {
				List<String> clientIds;
				try {
					clientIds = HSClientQueryManager.getClientsIdsOnNewApartment(apartment);
				} catch (ParseException e1) {
					System.out.println("HSNotificationsManager.onNewApartment: Failed to notify users");
					e1.printStackTrace();
					return null;
				}
				
				// Build the notification data
		    	JSONObject data = new JSONObject();
		    	try {
		    		data.put(ARG_NOTIFICATION_TYPE, TYPE_NEW_APARTMENT);
		    		data.put(ARG_APARTMENT_ID, apartment.getId());
			        data.put(ARG_ALERT, "Check out this new apartment!"); // TODO more details   
			        data.put(ARG_ACTION, TYPE_ACTIOM_HOMESEEK_NOTIFICATION);
		    	} catch (JSONException e) {
		    		e.printStackTrace();
		    		return null;
		    	}
				pushMsgToUsers(clientIds,data,null);
				return null;
			}
			
		};
		task.execute((Void)null);		
	}
	
	public static void onMeetupRemoval(HSMeetup meetup) 
	{
		List<String> clientIds = HSMeetupsManager.getMeetupAttandersIds(meetup);
		String apartmentId = meetup.getApartmentId();
		
		// Build the notification data
    	JSONObject data = new JSONObject();
    	try {
    		data.put(ARG_NOTIFICATION_TYPE, TYPE_MEETUP_REMOVE);
    		data.put(ARG_APARTMENT_ID, apartmentId);
	        data.put(ARG_ALERT, "A scheduled visit was canceled!");   
	        data.put(ARG_ACTION, TYPE_ACTIOM_HOMESEEK_NOTIFICATION);
    	} catch (JSONException e) {
    		e.printStackTrace();
    		return;
    	}
    	pushMsgToUsers(clientIds,data,getApartmentChannel(apartmentId));
	}
	
	public static void onMeetupClientChange(HSMeetup meetup, HSUser client,	boolean isAttending) throws ParseException {
		String apartmentId = meetup.getApartmentId();
		HSApartment apartment = null;
		try {
			apartment = HSApartmentsManager.getApartmentById(apartmentId);
		} catch (NoSuchApartmentException e1) {
			e1.printStackTrace();
			return;
		}
		
		// Build the notification data
    	JSONObject data = new JSONObject();
    	try {
    		data.put(ARG_NOTIFICATION_TYPE, TYPE_MEETUP_ATTENDERS_CHANGE);
    		data.put(ARG_MEETUP_ID, meetup.getId());
    		data.put(ARG_APARTMENT_ID, apartmentId);
	        data.put(ARG_ALERT, "Change in meetup attenders"); // TODO more details   
	        data.put(ARG_ACTION, TYPE_ACTIOM_HOMESEEK_NOTIFICATION); // TODO more details
    	} catch (JSONException e) {
    		e.printStackTrace();
    		return;
    	}
		
    	ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
    	pushQuery.whereEqualTo(HSInstallationDBConfig.InstallationTable.Fields.USER, apartment.getOwnerId());
    	
    	// Send push notification to query
    	ParsePush push = new ParsePush();
    	push.setQuery(pushQuery); 
    	push.setData(data);
    	push.sendInBackground();
	}
	
	public static void onNewInstantMessage(String instantMessageId, String fromUserId, String toUserId)
	{
		// Send notification to all user installations
		
		// Build the notification data
    	JSONObject data = new JSONObject();
    	try {
    		data.put(ARG_NOTIFICATION_TYPE, TYPE_NEW_IM);
    		data.put(ARG_INSTANT_MSG_ID, instantMessageId);
    		data.put(ARG_FROM_USER_ID, fromUserId);
	        data.put(ARG_ALERT, "New instant message!"); // TODO more details   
	        data.put(ARG_ACTION, TYPE_ACTIOM_HOMESEEK_NOTIFICATION); // TODO more details
    	} catch (JSONException e) {
    		e.printStackTrace();
    		return;
    	}
		
    	ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
    	pushQuery.whereEqualTo(HSInstallationDBConfig.InstallationTable.Fields.USER, toUserId);
    	
    	// Send push notification to query
    	ParsePush push = new ParsePush();
    	push.setQuery(pushQuery); 
    	push.setData(data);
    	try {
			push.send();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static void subscribeToApartment(String apartmentId)
	{
		PushService.subscribe(HomeseekApplication.getContext(), getApartmentChannel(apartmentId), ClientApartmentInfoActivity.class);  
	}
	
	public static void unSubscribeApartment(String apartmentId)
	{
		PushService.unsubscribe(HomeseekApplication.getContext(), getApartmentChannel(apartmentId));
	}
	
	/**
	 * This function subscribes the user to all the apartments in it's list.
	 * It should be called as part of the login process.
	 * @param userId
	 * @throws NoSuchApartmentException 
	 * @throws ParseException 
	 */
	public static void updateUserChannels(HSUser client) throws ParseException, NoSuchApartmentException
	{
		Set<String> temp_channels = PushService.getSubscriptions(HomeseekApplication.getContext());
		//since getSubscriptions is not blocking, make a copy of the set, and work with it (error may occure while trying to add/remove Items from it)
		Set<String> channels = new TreeSet<String>();
		channels.addAll(temp_channels);
		
		String curChannel;
		List<HSApartment> apartments = HSApartmentsManager.getClientApartments(client);
		for (HSApartment hsApartment : apartments) {
			curChannel = getApartmentChannel(hsApartment.getId());
			if(!channels.contains(curChannel)){
				subscribeToApartment(hsApartment.getId());
			} else {
				channels.remove(curChannel);
			}
		} 
		
		// Remove old channels 
		for (String channel : channels) {
			PushService.unsubscribe(HomeseekApplication.getContext(), channel);	
		}
		
	}
	
	private static void pushMsgToUsers(Collection<String> userIds, JSONObject data, String channel)
    {
    	System.out.println("Going to push message to number of users from: " + ParseUser.getCurrentUser().getUsername());
    	if(userIds == null){
    		return;
    	}
    	
    	ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
    	pushQuery.whereContainedIn(HSInstallationDBConfig.InstallationTable.Fields.USER, userIds);
    	if(channel != null)
    	{
    		pushQuery.whereEqualTo(HSInstallationDBConfig.InstallationTable.Fields.CHANNELS, channel);
    	}
    	
    	// Send push notification to query
    	ParsePush push = new ParsePush();
    	push.setQuery(pushQuery); // Set our Installation query
    	push.setData(data);
    	push.sendInBackground();
    }
    
    private static void pushChannelNotification(JSONObject data, String channel) {
    	System.out.println("Going to push message with Intent");
        
    	ParsePush push = new ParsePush();
   		push.setChannel(channel);
    	push.setData(data);
    	push.sendInBackground();
	}

	public static void init(ParseUser user) throws ParseException {
		// ParseInstallation allows users to subscribe to push notification service 
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put(HSInstallationDBConfig.InstallationTable.Fields.USER,user.getObjectId());
		// Save the user installation. Note: the installation is saved once on the first application launch. 
		installation.save();	
	}
	
	private static String getApartmentChannel(String apartmentId){
		return APARTMENT_PREFIX + apartmentId;
	}
}

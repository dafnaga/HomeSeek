package com.postpc.homeseek;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseException;
import com.postpc.homeseek.core.managers.HSApartmentsManager;
import com.postpc.homeseek.core.managers.HSNotificationsManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchApartmentException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyParseReceiver extends BroadcastReceiver {

    public static final String PARSE_EXTRA_DATA_KEY="com.parse.Data";
    public static final String PARSE_JSON_ALERT_KEY="alert";
    public static final String PARSE_JSON_CHANNELS_KEY="com.parse.Channel";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
//			String action = intent.getAction();
//			String channel = intent.getExtras().getString(PARSE_JSON_CHANNELS_KEY);			
			
			JSONObject json = new JSONObject(intent.getExtras().getString(PARSE_EXTRA_DATA_KEY));
			String notificationType = json.getString(HSNotificationsManager.ARG_NOTIFICATION_TYPE);
			
			Intent fireIntent;
			String msg = json.getString(HSNotificationsManager.ARG_ALERT);
			
			if (notificationType.equals(HSNotificationsManager.TYPE_NEW_IM)){
				// New IM notification
				fireIntent = handleNewIM(context, json);
				
			} else if (notificationType.equals(HSNotificationsManager.TYPE_APARTMENT_UPDATED)){
				// A relevant apartment was updated
				fireIntent = handleApartmentUpdated(context, json);
			
			} else if (notificationType.equals(HSNotificationsManager.TYPE_NEW_APARTMENT)){
				// A new relevant apartment was added
				fireIntent = handleApartmentUpdated(context, json);
				
			} else if (notificationType.equals(HSNotificationsManager.TYPE_APARTMENT_REMOVE)){
				fireIntent = handleApartmentRemoval(context, json);
				
			} else if (notificationType.equals(HSNotificationsManager.TYPE_MEETUP_REMOVE)){
				// A new relevant apartment was added
				fireIntent = handleMeetupChange(context, json);
				
			} else if (notificationType.equals(HSNotificationsManager.TYPE_MEETUP_ATTENDERS_CHANGE)){
				// A new relevant apartment was added
				fireIntent = handleMeetupAttendersChange(context, json);
				
			} else {
				// Unknown notification type
				return;
			}
			
			if (fireIntent == null){
				return;
			}
			
			// Fire the broadcast intent
			fireIntent.putExtra(EventHandlerReceiver.ARG_NOTIFICATION_TXT, msg);
			context.sendOrderedBroadcast(fireIntent, null);
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NoSuchApartmentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Intent handleApartmentUpdated(Context context, JSONObject data) throws JSONException {
		String apartmentId = data.getString(HSNotificationsManager.ARG_APARTMENT_ID);

		Intent intent = new Intent("com.postpc.homeseek.action.APARTMENT_UPDATED");
		intent.putExtra(ClientApartmentInfoActivity.ARG_APARTMENT_ID, apartmentId);

		return intent;
	}


	private Intent handleApartmentRemoval(Context context, JSONObject data) {
		Intent intent = new Intent("com.postpc.homeseek.action.APARTMENT_REMOVAL");
		return intent;
	}
	private Intent handleNewIM(Context context, JSONObject data) throws JSONException {
		String fromUserId = data.getString(HSNotificationsManager.ARG_FROM_USER_ID);
		
		Intent intent = new Intent("com.postpc.homeseek.action.NEW_IM");
		intent.putExtra(ChatActivity.ARG_WITH_USER_ID, fromUserId);
		
		return intent;		
	}

	private Intent handleMeetupChange(Context context, JSONObject data) throws NoSuchApartmentException, ParseException, JSONException {
		String apartmentId = data.getString(HSNotificationsManager.ARG_APARTMENT_ID);
		String apartmentTitle = HSApartmentsManager.getApartmentById(apartmentId).getTitle();
		
		Intent intent = new Intent("com.postpc.homeseek.action.MEETUP_CHANGE");
		intent.putExtra(ApartmentCalendarForClientActivity.ARG_APARTMENT_ID, apartmentId);
		intent.putExtra(ApartmentCalendarForClientActivity.ARG_APARTMENT_TITLE, apartmentTitle);
		
		return intent;
	}
	
	private Intent handleMeetupAttendersChange(Context context, JSONObject data) throws NoSuchApartmentException, ParseException, JSONException {
		String meetupId = data.getString(HSNotificationsManager.ARG_MEETUP_ID);
		String apartmentId = data.getString(HSNotificationsManager.ARG_APARTMENT_ID);
		String apartmentTitle = HSApartmentsManager.getApartmentById(apartmentId).getTitle();
		
		Intent intent = new Intent("com.postpc.homeseek.action.MEETUP_ATTENDERS_CHANGE");
		intent.putExtra(OwnerMeetupActivity.ARG_MEETUP_ID, meetupId);
		intent.putExtra(OwnerMeetupActivity.ARG_APARTMENT_TITLE, apartmentTitle);
		intent.putExtra(OwnerMeetupActivity.ARG_ALLOW_REMOVE, false);
		
		return intent;
	}
}

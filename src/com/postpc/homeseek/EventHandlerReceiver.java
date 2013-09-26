package com.postpc.homeseek;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class EventHandlerReceiver extends BroadcastReceiver{

	public static final String ARG_NOTIFICATION_TXT = "arg_notificaiton_txt";

	@Override
	public void onReceive(Context context, Intent intent) {
		String msg = getNotificaitonMsg(intent);
		
		Intent fireIntent = getFireIntent(context, intent);
		if (fireIntent == null){
			return;
		}
		
		fireNotification(context, msg, fireIntent);
	}
	
	private Intent getFireIntent(Context context, Intent fromIntent) {
		Intent intent = null;
		
		if (fromIntent.getAction().equals("com.postpc.homeseek.action.APARTMENT_UPDATED")){
			intent = onApartmentUpdated(context, fromIntent);
		}
		else if (fromIntent.getAction().equals("com.postpc.homeseek.action.APARTMENT_REMOVAL")){
			intent = onApartmentRemoval(context, fromIntent);
		}
		else if (fromIntent.getAction().equals("com.postpc.homeseek.action.NEW_IM")){
			intent = onNewIM(context, fromIntent);
		} 
		else if (fromIntent.getAction().equals("com.postpc.homeseek.action.MEETUP_CHANGE")){
			intent = onMeetupChange(context, fromIntent);
		}
		else if (fromIntent.getAction().equals("com.postpc.homeseek.action.MEETUP_ATTENDERS_CHANGE")){
			intent = onMeetupAttendersChange(context, fromIntent);
		}
		
		return intent;
	}

	private Intent onNewIM(Context context, Intent fromIntent) {
		Intent intent = new Intent(context, ChatActivity.class);
		intent.putExtra(ChatActivity.ARG_WITH_USER_ID, fromIntent.getStringExtra(ChatActivity.ARG_WITH_USER_ID));
		return intent;
	}

	private Intent onApartmentUpdated(Context context, Intent fromIntent) {
		Intent intent = new Intent(context, ClientApartmentInfoActivity.class);
		intent.putExtra(ClientApartmentInfoActivity.ARG_APARTMENT_ID, fromIntent.getStringExtra(ClientApartmentInfoActivity.ARG_APARTMENT_ID));
		return intent;
	}
	
	private Intent onApartmentRemoval(Context context, Intent fromIntent) {
		Intent intent = new Intent(context, ClientApartmentsActivity.class);
		return intent;
	}

	private Intent onMeetupChange(Context context, Intent fromIntent) {
		Intent intent = new Intent(context, ApartmentCalendarForClientActivity.class);
		intent.putExtra(ApartmentCalendarForClientActivity.ARG_APARTMENT_ID, fromIntent.getStringExtra(ApartmentCalendarForClientActivity.ARG_APARTMENT_ID));
		intent.putExtra(ApartmentCalendarForClientActivity.ARG_APARTMENT_TITLE, fromIntent.getStringExtra(ApartmentCalendarForClientActivity.ARG_APARTMENT_TITLE));
		return intent;
	}	
	
	private Intent onMeetupAttendersChange(Context context, Intent fromIntent) {
		Intent intent = new Intent(context, OwnerMeetupActivity.class);
		intent.putExtra(OwnerMeetupActivity.ARG_MEETUP_ID, fromIntent.getStringExtra(OwnerMeetupActivity.ARG_MEETUP_ID));
		intent.putExtra(OwnerMeetupActivity.ARG_APARTMENT_TITLE, fromIntent.getStringExtra(OwnerMeetupActivity.ARG_APARTMENT_TITLE));
		intent.putExtra(OwnerMeetupActivity.ARG_ALLOW_REMOVE, fromIntent.getBooleanExtra(OwnerMeetupActivity.ARG_ALLOW_REMOVE, false));
		return intent;
	}	
	
	private String getNotificaitonMsg(Intent intent) {
		return intent.getStringExtra(ARG_NOTIFICATION_TXT);
	}

	private void fireNotification(Context context, String msg, Intent fireIntent) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_homeseek)
		        .setContentTitle("HomeSeek")
		        .setContentText(msg);
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		//TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		//stackBuilder.addParentStack(fireIntent.resolveActivity (context.getPackageManager()).getClass());
		// Adds the Intent that starts the Activity to the top of the stack
		//stackBuilder.addNextIntent(fireIntent);
		//PendingIntent firePendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			
		PendingIntent firePendingIntent = PendingIntent.getActivity(context, 0, fireIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		mBuilder.setStyle(new NotificationCompat.InboxStyle());
		
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		mBuilder.setSound(alarmSound);
		
		mBuilder.setContentIntent(firePendingIntent);
		
		mBuilder.setAutoCancel(true);
		
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());	
	}

}

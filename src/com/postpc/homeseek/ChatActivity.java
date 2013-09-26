package com.postpc.homeseek;

import java.util.Date;
import java.util.List;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSInstantMessage;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSInstantMessagesManager;
import com.postpc.homeseek.core.managers.HSUsersManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchUserException;
import com.postpc.homeseek.core.session.HSSessionManager;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity {

	private static final int PAST_MESSAGES = 100;
	public static final String ARG_WITH_USER_ID = "with_user";
	
	private ChatAdapter chatAdapter;
	private HSUser currentUser;
	private HSUser secondUser;

	private IMEventsReceiver eventsReceiver;
	
	protected void registerEventReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.postpc.homeseek.action.NEW_IM");		
		filter.setPriority(10);
		
		eventsReceiver = new IMEventsReceiver();
		registerReceiver(eventsReceiver, filter);		
	}
	
	protected void unregisterEventReceiver(){
		unregisterReceiver(eventsReceiver);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		registerEventReceiver();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		unregisterEventReceiver();
	}
	
	protected class IMEventsReceiver extends BroadcastReceiver {				
				
		@Override
		public void onReceive(Context context, Intent intent) {
			refresh();
			abortBroadcast();
			
		    try {
		        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		        r.play();
		    } catch (Exception e) {}			
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		try {
			currentUser  = HSSessionManager.getActiveSession().getUser();
			
			String secondUserId  = getIntent().getStringExtra(ARG_WITH_USER_ID);
			if (secondUserId == null){
				throw new UserNotLoggedInException();
			}
			
			secondUser = HSUsersManager.getUserById(secondUserId);
			
			String title = "Chat with: " + secondUser.getFullName();
			HomeSeekViewUtils.setTxt(this, R.id.chat_title_txt, title);
			
			refresh();
		} catch (ParseException e) {
			LoginActivity.launch(this);
			finish();
			return;
		} catch (UserNotLoggedInException e) {
			// TODO Auto-generated catch block
			LoginActivity.launch(this);
			finish();
		} catch (NoSuchUserException e) {
			// TODO Auto-generated catch block
			LoginActivity.launch(this);
			finish();
		}
		
	}

	protected class ChatAdapter extends ArrayAdapter<HSInstantMessage>{
		private Context context;
		private HSInstantMessage[] ims;

		public ChatAdapter(Context context, HSInstantMessage[] ims) {
			super(context, 0, ims);
			
			this.context = context;		
			this.ims = ims;
		}
		
		@Override 
		public View getView(int position, View convertView, ViewGroup parent)
		{	
			if (convertView != null){
				String curImId = (String)convertView.getTag();
				String showImId = ims[position].getId();
				if (curImId.equals(showImId)){
					return convertView;
				}
			}
			
			// TODO don't get current user every time..						
			HSUser currentUser;
			try {
				currentUser = HSSessionManager.getActiveSession().getUser();
			} catch (UserNotLoggedInException e) {
				LoginActivity.launch((Activity)context);
				return null;
			}
			
			View row = InstantMessageViewFactory.createView(context, currentUser, ims[position], parent);
			row.setTag(ims[position].getId());
			return row;
		}
		
	}
	
	protected void refresh(){
		SimpleAsyncTask task = new SimpleAsyncTask(this) {
			
			boolean success;
			private List<HSInstantMessage> messages;
			
			@Override
			protected void preExecute() {
				
			}
			
			@Override
			protected void postExecute() {
				if (!success){
					LoginActivity.launch(activity);
					finish();
				}
				
				chatAdapter = new ChatAdapter(activity, messages.toArray(new HSInstantMessage[0]));
				ListView chatList = (ListView)findViewById(R.id.chat_container_list);
				chatList.setAdapter(chatAdapter);
				
				chatAdapter.notifyDataSetChanged();
				chatList.setSelection(messages.size());				
			}
		
			@Override
			protected Void doInBackground(Void... arg0) {
				// Get recent conversation
				try {
					messages = HSInstantMessagesManager.getRecentConversation(currentUser, secondUser, PAST_MESSAGES);
					success = true;
					return null;
				} catch (ParseException e) {					
					e.printStackTrace();
				}
				
				success = false;
				return null;
			}
		};
		
		task.execute((Void[])null);		
		
	}

	public void onSendClick(View view){
		SimpleAsyncTask task = new SimpleAsyncTask(this) {
			
			private String messageText;

			@Override
			protected void preExecute() {
				messageText = ((EditText)findViewById(R.id.chat_input_edt)).getText().toString();
			}
			
			@Override
			protected void postExecute() {
				// TODO Auto-generated method stub
				//refresh();
				activity.finish();
				activity.startActivity(activity.getIntent());
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					HSInstantMessagesManager.newInstantMessage(currentUser, secondUser, new Date(), messageText);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
		};
		
		task.execute((Void[])null);
	}

	public void onApartmentLinkClick (View view) {
		String apartmentId = (String) view.getTag();
		Intent intent = new Intent(this, ClientApartmentInfoActivity.class);
		intent.putExtra(ClientApartmentInfoActivity.ARG_APARTMENT_ID, apartmentId);
		startActivity(intent);
	}
	
}

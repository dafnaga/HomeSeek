package com.postpc.homeseek;

import java.util.List;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSInstantMessagesManager;
import com.postpc.homeseek.core.session.HSSessionManager;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class RecentConversationsActivity extends HSActivity {

	private static final int PAST_CONVERSATIONS_DAYS = 10;

	protected static final String ARG_USER_TYPE = "USER_TYPE";
	protected static final int MODE_CLIENT = 0;
	protected static final int MODE_OWNER = 1;
	private static final int REQ_PICK_CONTACT = 2;
	
	protected List<HSUser> userContacts;
	protected ChatContactsAdapter contactsAdapter;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view); 

//		SimpleAsyncTask task = new RecentConversationsAsyncTask(this);
//		task.execute((Void[])null);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SimpleAsyncTask task = new RecentConversationsAsyncTask(this);
		task.execute((Void[])null);
	}

	protected void setControlBar(int mode){
		//add top bar
		int layoutXmlId = (mode == MODE_CLIENT) ? R.layout.client_conversations_top_bar : R.layout.owner_conversations_top_bar;
		HomeSeekViewUtils.setExternalLayout(this, R.id.top_ruller_bar_layout, layoutXmlId);
	}

	@Override
	public void onRecentConversationsClick(View v) {
		// Do nothing
		
	}
	
	public void onChooseContactClick(View v) {
		Intent intent = new Intent(this, ChooseContactActivity.class);
		startActivityForResult(intent, REQ_PICK_CONTACT);			
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
	    // Check which request we're responding to
	    if (requestCode == REQ_PICK_CONTACT) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {    
	        	String contactId = data.getStringExtra(ChooseContactActivity.ARG_RETURN_USER_ID);
	        	Intent intent = new Intent(this, ChatActivity.class);

				intent.putExtra(ChatActivity.ARG_WITH_USER_ID, contactId);
				startActivity(intent);
	        }
	    }
	}
	
	private class RecentConversationsAsyncTask extends SimpleAsyncTask {

		public RecentConversationsAsyncTask(Activity activity) {
			super(activity);
		}


		boolean success = true;

		@Override
		protected void preExecute() {
			
			// Determine which control bar is required
			int barhMode = getIntent().getIntExtra(ARG_USER_TYPE, MODE_CLIENT);
			setControlBar(barhMode);
			
			// Set title
			HomeSeekViewUtils.setTxt(activity, R.id.activity_title_txt, activity.getResources().getString(R.string.recent_conversations));
			
			// Disable subtitle 
			activity.findViewById(R.id.activity_subtitle_txt).setVisibility(View.INVISIBLE);
			
			// Set buttons
			HomeSeekViewUtils.setExternalLayout(activity, R.id.btn_layout, R.layout.recent_conversations_btns);
		}

		@Override
		protected void postExecute() {
			// TODO Auto-generated method stub
			if (!success){
				// TODO handle error nicely, for now send to login
				LoginActivity.launch(activity);
			}

			// Build adapter and list
			contactsAdapter = new ChatContactsAdapter(activity, userContacts.toArray(new HSUser[0]));
			ListView clientsList = (ListView) activity.findViewById(R.id.activity_list);
			clientsList.setAdapter(contactsAdapter);

			contactsAdapter.notifyDataSetChanged();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				HSUser curUser = HSSessionManager.getActiveSession().getUser();
				userContacts = HSInstantMessagesManager.getClientRecentContacts(curUser.getId(), PAST_CONVERSATIONS_DAYS);
				success = true;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				success = false;
			} catch (UserNotLoggedInException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				success = false;
			}
			
			return null;
		}		
	}
}

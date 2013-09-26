package com.postpc.homeseek;

import java.util.List;
import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSMeetup;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSMeetupsManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchMeetupException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class OwnerMeetupActivity extends Activity {
	
	public static final String ARG_MEETUP_ID = "MEETUP_ID";
	public static final String ARG_APARTMENT_TITLE = "APARTMENT_TITLE";
	public static final String ARG_ALLOW_REMOVE = "ALLOW_REMOVE";
	protected static final int RESULT_REMOVED = 101;
	
	private HSMeetup meetup;
	private List<HSUser> attenders;
	private ChatContactsAdapter clientsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);

		
		SimpleAsyncTask task = new SimpleAsyncTask(this) {

			boolean success = true;

			@Override
			protected void preExecute() {
				Intent intent = getIntent();
				String meetupId = intent.getStringExtra(ARG_MEETUP_ID);
				String title = intent.getStringExtra(ARG_APARTMENT_TITLE);
				boolean allowRemove = intent.getBooleanExtra(ARG_ALLOW_REMOVE, false);
				String time = "";
				try {
					meetup = HSMeetupsManager.getMeetupById(meetupId);
					time = HSMeetupsManager.getMeetupTimeAsString(meetup);
				} catch (NoSuchMeetupException e1) {
					//should not get here
					success = false;
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					success = false;
				}

				// Set title
				HomeSeekViewUtils.setTxt(activity, R.id.activity_title_txt, title);
				HomeSeekViewUtils.setTxt(activity, R.id.activity_subtitle_txt, time);
				
				//set add_visit_btn text
				if(allowRemove){
					HomeSeekViewUtils.setExternalLayout(activity, R.id.btn_layout, R.layout.owner_meetup_btns);
				}
			}

			@Override
			protected void postExecute() {
				
				if(!success){
					// TODO: handle this
					finish();
				}
				
				// Build adapter and list
				clientsAdapter = new ChatContactsAdapter(activity, attenders.toArray(new HSUser[0]));
				ListView clientsList = (ListView) activity.findViewById(R.id.activity_list);
				View headerView = getLayoutInflater().inflate(R.layout.owner_meetup_list_header, clientsList,  false);
				clientsList.addHeaderView(headerView);
				clientsList.setAdapter(clientsAdapter);
				
				clientsAdapter.notifyDataSetChanged();
				
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				// Get attenders list
				try {
					attenders = HSMeetupsManager.getMeetupAttanders(meetup);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					success = false;
				}

				return null;
			}
		};

		task.execute((Void[])null);	
	}

	public void onRemoveMeetupBtnClick(View v) {
		
		try {
			//TODO - move to async task
			HSMeetupsManager.removeMeetup(meetup);
			setResult(RESULT_REMOVED);
			Toast.makeText(this, "Meetup was removed", Toast.LENGTH_LONG).show();//TODO - move to string.xml
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setResult(RESULT_CANCELED);
			Toast.makeText(this, "Faild to remove meetup", Toast.LENGTH_LONG).show();//TODO - move to string.xml 
		}
		finish();
		
	}
	
	public void onBackPressed() {
		//TODO - should a result be set? (OK or CANCEL)?
		super.onBackPressed();
	}
}

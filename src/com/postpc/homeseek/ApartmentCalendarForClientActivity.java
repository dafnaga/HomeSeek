package com.postpc.homeseek;

import java.util.List;
import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSMeetup;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSMeetupsManager;
import com.postpc.homeseek.core.session.HSSessionManager;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ApartmentCalendarForClientActivity extends Activity {

	public static final String ARG_APARTMENT_ID = "APARTMENT_ID";
	public static final String ARG_APARTMENT_TITLE = "APARTMENT_TITLE";

	private ClientMeetupsAdapter adapter;
	private ListView visitsList;
	private List<HSMeetup> apartemntMeetups;
	private List<HSMeetup> clientMeetups;
	private HSUser curClient;

	private String apartmentId;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);

		SimpleAsyncTask task = new SimpleAsyncTask(this) {
			private boolean success = false;
			String error = "";
			
			@Override
			protected void preExecute() {
				Intent intent = getIntent();
				apartmentId = intent.getStringExtra(ARG_APARTMENT_ID);

				//set title
				title = intent.getStringExtra(ARG_APARTMENT_TITLE);
				HomeSeekViewUtils.setTxt(activity, R.id.activity_title_txt, title);

				//set sub_title
				HomeSeekViewUtils.setTxt(activity, R.id.activity_subtitle_txt, getResources().getString(R.string.calendar_str));
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					apartemntMeetups = HSMeetupsManager.getApartmentMeetups(apartmentId);
					curClient = HSSessionManager.getActiveSession().getUser();
					clientMeetups = HSMeetupsManager.getClientMeetups(curClient.getId(), apartmentId);

					success = true;
					return null;
				} catch (ParseException e) {
					error = "Error connecting to server.";
					e.printStackTrace();
				} catch (UserNotLoggedInException e) {
					error = "Please login.";
					e.printStackTrace();
				}
				success  = false;
				
				return null;
			}

			@Override
			protected void postExecute() {

				if (!success){
					Toast toast = Toast.makeText(activity, error, Toast.LENGTH_SHORT);
					toast.show();
					LoginActivity.launch(activity);
					return;
				}

				// Build adapter and list
				adapter = new ClientMeetupsAdapter(activity, apartemntMeetups.toArray(new HSMeetup[0]));
				visitsList = (ListView)findViewById(R.id.activity_list);

				View headerView = getLayoutInflater().inflate(R.layout.client_meetups_list_header, visitsList,  false);
				visitsList.addHeaderView(headerView);
				visitsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
				visitsList.setAdapter(adapter);

				adapter.notifyDataSetChanged();
			}
		};

		task.execute((Void[])null);
	}

	private class ClientMeetupsAdapter extends ArrayAdapter<HSMeetup>{

		private final HSMeetup[] meetups;
		private final Context context;

		public ClientMeetupsAdapter(Context context, HSMeetup[] objects) {
			super(context, 0, objects);
			this.context = context;
			this.meetups = objects;
		}
		
		public View getView(int position, View row, ViewGroup parent)
		{
			if (row == null){
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(R.layout.client_meetup_row, parent, false);			
			}
			
			HSMeetup meetup = meetups[position];		
			HomeSeekViewUtils.fillViewWithMeetUpInfo(row, meetup);

			Button attendBtn = (Button)row.findViewById(R.id.attend_btn);
			attendBtn.setTag(meetup);
			
			if (clientMeetups.contains(meetup)) {
				attendBtn.setText(getResources().getString(R.string.unattend_txt));
			} else {
				attendBtn.setText(getResources().getString(R.string.attend_txt));
			}
			
			attendBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					final View view = v;
					SimpleAsyncTask task = new SimpleAsyncTask((Activity)v.getContext()) {
						
						private HSMeetup meetup;
						private boolean isAttendeing;
						private boolean success = false;
						private String error;
						
						@Override
						protected void preExecute() {
							meetup = (HSMeetup)view.getTag();
						}
						
						@Override
						protected void postExecute() {
							
							if (!success) {
								Toast toast = Toast.makeText(activity, error, Toast.LENGTH_SHORT);
								toast.show();
								activity.finish();
								return;
							}
							if (isAttendeing) {
								((Button)view).setText(getResources().getString(R.string.unattend_txt));
							} else {
								((Button)view).setText(getResources().getString(R.string.attend_txt));
							}
							
						}
						
						@Override
						protected Void doInBackground(Void... arg0) {
							int pos = clientMeetups.indexOf(meetup);
							try {
								if (pos >= 0) {
									//un-attend
									HSMeetupsManager.setClientPresence(meetup, curClient, false);
									clientMeetups.remove(pos);
									isAttendeing = false;

								} else {
									//attend
									HSMeetupsManager.setClientPresence(meetup, curClient, true);
									clientMeetups.add(meetup);
									isAttendeing = true;
								}
								success = true;
								return null;
							} catch (ParseException e) {
								error = "Error connecting server.";
								e.printStackTrace();
							}
							success = false;
							return null;
						}
					};
					task.execute((Void)null);
				}
			});

			return row;			
		}

	}
}
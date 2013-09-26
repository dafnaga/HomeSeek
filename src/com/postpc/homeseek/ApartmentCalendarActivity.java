package com.postpc.homeseek;

import java.util.ArrayList;
import java.util.List;
import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSMeetup;
import com.postpc.homeseek.core.managers.HSMeetupsManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchMeetupException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ApartmentCalendarActivity extends Activity {

	protected static final int ADD_MEETUP_REQ = 1;
	protected static final int VIEW_MEETUP_REQ = 2;
	
	public static final String ARG_MEETUP_ID = "MEETUP_ID";
	public static final String ARG_APARTMENT_ID = "APARTMENT_ID";
	public static final String ARG_APARTMENT_TITLE = "APARTMENT_TITLE";
	
	private ApartmentCalendarAdapter adapter;
	private List<HSMeetup> meetups;

	private String apartmentId;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);
		
		Intent intent = getIntent();
		apartmentId = intent.getStringExtra(ARG_APARTMENT_ID);

		//set title
		title = intent.getStringExtra(ARG_APARTMENT_TITLE);
		HomeSeekViewUtils.setTxt(this, R.id.activity_title_txt, title);
		
		//set subtitle
		HomeSeekViewUtils.setTxt(this, R.id.activity_subtitle_txt, getResources().getString(R.string.calendar_str));
		
		//set add_visit_btn text
		HomeSeekViewUtils.setExternalLayout(this, R.id.btn_layout, R.layout.apartment_calendar_btns);
		
		refresh(true);

	}

	public void onAddVisitBtnClick(View view){
		Intent intent = new Intent(this, AddMeetUpActivity.class);
		intent.putExtra(AddMeetUpActivity.ARG_APARTMENT_ID, apartmentId);
		startActivityForResult(intent, ADD_MEETUP_REQ);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ADD_MEETUP_REQ:
			if (resultCode == RESULT_OK) {
				//Update new details
				try {
					String meetupId = data.getStringExtra(ARG_MEETUP_ID);
					adapter.add(HSMeetupsManager.getMeetupById(meetupId));
					adapter.notifyDataSetChanged();
				} catch (ParseException e) {
					ErrorOccurredActivity.showConnectivityError(this);
				} catch (NoSuchMeetupException e) {
					e.printStackTrace();
				}
			}
			break;
		case VIEW_MEETUP_REQ:
			if (resultCode == OwnerMeetupActivity.RESULT_REMOVED) {
				refresh(false);				
			}
		default:
			break;
		}
		
	}

	private void refresh(final boolean addHeader) {
		
		SimpleAsyncTask task = new SimpleAsyncTask(this) {
			private boolean success = false;
			String error;
			
			@Override
			protected void preExecute() {
								
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					meetups = HSMeetupsManager.getApartmentMeetups(apartmentId);
					
					// Build adapter and list
					success = true;
				} catch (ParseException e) {
					error = "Error connecting to server.";
					success  = false;					
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void postExecute() {
				
				if (!success){
					ErrorOccurredActivity.show(activity, error);
					return;
				}
				
				adapter = new ApartmentCalendarAdapter(activity, new ArrayList<HSMeetup>(meetups));
				ListView visitsList = (ListView)findViewById(R.id.activity_list);
				if(addHeader){
					View headerView = getLayoutInflater().inflate(R.layout.owner_meetups_list_header, visitsList,  false);
					visitsList.addHeaderView(headerView);
				}
				visitsList.setAdapter(adapter);

				adapter.notifyDataSetChanged();

				visitsList.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> adptView, View view, int position, long id) {
						if (position == 0){
							// User clicked on list header, do nothing
							return;
						}
						position--;
						HSMeetup visit = adapter.getItem(position);

						Intent intent = new Intent(activity, OwnerMeetupActivity.class);
						intent.putExtra(OwnerMeetupActivity.ARG_MEETUP_ID, visit.getId());
						intent.putExtra(OwnerMeetupActivity.ARG_APARTMENT_TITLE, title);
						intent.putExtra(OwnerMeetupActivity.ARG_ALLOW_REMOVE, true);
						startActivityForResult(intent, VIEW_MEETUP_REQ);
					}
				});	
				

			}
		};
		
		task.execute((Void[])null);	
	}

	private class ApartmentCalendarAdapter extends ArrayAdapter<HSMeetup> {

		private final ArrayList<HSMeetup> visits;
		private final Context context;
		
		public ApartmentCalendarAdapter(Context context, ArrayList<HSMeetup> visits) {
			super(context, R.layout.owner_meetup_row, visits);
			
			this.context = context;		
			this.visits = visits;
		}
		
		@Override 
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			View row = convertView;
			if (row == null){
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(R.layout.owner_meetup_row, parent, false);		
			}
			
			HSMeetup visit = visits.get(position);		
			HomeSeekViewUtils.fillViewWithMeetUpInfo(row, visit);		
			return row;
		}
		
	}
}

package com.postpc.homeseek;

import java.util.List;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSApartmentsManager;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ApartmentClientsActivity extends Activity {

	public static final String ARG_APARTMENT_ID = "APARTMENT_ID";

	protected HSApartment apartment;
	protected List<HSUser> apartmentClients;
	protected ChatContactsAdapter clientsAdapter;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);

		final String apartmentId = getIntent().getStringExtra(ARG_APARTMENT_ID); 

		SimpleAsyncTask task = new SimpleAsyncTask(this) {

			boolean success = true;
			private String error;

			@Override
			protected void preExecute() {
				// Set title
				HomeSeekViewUtils.setTxt(activity, R.id.activity_title_txt, activity.getResources().getString(R.string.apartment_clients));
				
				// Disable subtitle 
				activity.findViewById(R.id.activity_subtitle_txt).setVisibility(View.INVISIBLE);
			}

			@Override
			protected void postExecute() {
				// TODO Auto-generated method stub
				if (!success){
					Toast toast = Toast.makeText(activity, error, Toast.LENGTH_SHORT);
					toast.show();
					activity.finish();
				}

				// Build adapter and list
				clientsAdapter = new ChatContactsAdapter(activity, apartmentClients.toArray(new HSUser[0]));
				ListView clientsList = (ListView) activity.findViewById(R.id.activity_list);
				clientsList.setAdapter(clientsAdapter);

				clientsAdapter.notifyDataSetChanged();
			}

			@Override
			protected Void doInBackground(Void... arg0) {

				try {
					apartmentClients = HSApartmentsManager.getApartmentClients(apartmentId);
					success = true;
				} catch (ParseException e) {
					error = "Error connecting server.";
					e.printStackTrace();					
					success = false;
				}

				return null;
			}
		};

		task.execute((Void[])null);

	}
}

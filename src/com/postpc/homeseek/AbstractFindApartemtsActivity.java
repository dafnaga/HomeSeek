package com.postpc.homeseek;

import java.util.List;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.managers.exceptions.NoSuchApartmentException;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public abstract class AbstractFindApartemtsActivity extends HSActivity {
	
	protected ApartmentListAdapter adapter;
	private ListView apartmentsList;
	protected AbstractFindApartemtsActivity activity;

	private int layoutResID;
	private int apartemntListResID;
	private Class<? /*TODO: extends AbstructApartmentInfoActivity*/> apartmentInfoActivityClass;

	protected List<HSApartment> apartments;
		
	protected AbstractFindApartemtsActivity(int layoutResID, int apartemntListResID, 
			Class<? /*extends AbstructApartmentInfoActivity*/> infoClass){
		this.layoutResID = layoutResID;
		this.apartemntListResID = apartemntListResID;
		this.apartmentInfoActivityClass = infoClass;
	}

	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layoutResID);

		activity = this;
		apartmentsList = (ListView)findViewById(apartemntListResID);
		apartmentsList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> adptView, View view, int position, long id) {
				
				HSApartment apartment = adapter.getItem(position);

				Intent intent = new Intent(activity, apartmentInfoActivityClass);
				intent.putExtra(AbstructApartmentInfoActivity.ARG_APARTMENT_ID, apartment.getId());
				startActivity(intent);
			}
		});

		SimpleAsyncTask task = new SearchApartmentsAsyncTask(this);
		task.execute((Void[])null);
	}

	@Override
	protected void onResume(){
		super.onResume();
	}
	
	protected abstract void setPreTaskExecute();

	protected abstract List<HSApartment> findApartments() throws ParseException, UserNotLoggedInException, NoSuchApartmentException;
	
	protected abstract ApartmentListAdapter generateApartmentsListAdapter(HSApartment[] hsApartments);

	protected class SearchApartmentsAsyncTask extends SimpleAsyncTask{

		public SearchApartmentsAsyncTask(Activity activity) {
			super(activity);
		}

		
		private boolean success = false;
		private String error = "Search error";

		@Override
		protected void preExecute() {
			setPreTaskExecute();
		}

		@Override
		protected void postExecute() {
			if (!success){
				Toast toast = Toast.makeText(activity, error, Toast.LENGTH_SHORT);
				toast.show();
				activity.finish();
				return;
			}

			adapter = generateApartmentsListAdapter(apartments.toArray(new HSApartment[0]));		        
			apartmentsList.setAdapter(adapter);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {						 
				// Get search results
				apartments = findApartments();
				success = true;
				return null;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// For now, send to login activity anyway
			} catch (UserNotLoggedInException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchApartmentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			success = false;
			return null;
		}
	}
}

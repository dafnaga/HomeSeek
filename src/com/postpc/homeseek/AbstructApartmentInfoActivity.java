package com.postpc.homeseek;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.managers.HSApartmentsManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchApartmentException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public abstract class AbstructApartmentInfoActivity extends Activity {

	protected static final String ARG_APARTMENT_ID = "APARTMENT_ID";
	private int layoutResID;
	protected String apartmentId;
	protected HSApartment apartment; 

	public AbstructApartmentInfoActivity(int layoutResID){
		this.layoutResID = layoutResID;
	}
	
	protected class ShowInfoAsyncTask extends SimpleAsyncTask{
		
		boolean success = false;
		String error = "General error occured";
		
		public ShowInfoAsyncTask(Activity activity) {
			super(activity);
		}
		
		@Override
		protected void preExecute() {
			apartmentId = getIntent().getStringExtra(ARG_APARTMENT_ID);
		}
		
		@Override
		protected void postExecute() {
			if (!success){
				Toast toast = Toast.makeText(activity, error, Toast.LENGTH_SHORT);
				toast.show();
				finish();
				return; 
			}
			HomeSeekViewUtils.fillViewWithApartmentInfo(activity.findViewById(android.R.id.content), apartment, R.drawable.add_photo_icon_big);				
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				apartment = HSApartmentsManager.getApartmentById(apartmentId);
				success = true;
				return null;
			} catch (NoSuchApartmentException e) {
				// TODO Auto-generated catch block
				error = "Apartment doesn't exist anymore.";
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				error = "Error connecting to server.";				
				e.printStackTrace();
			}
			success = false;
			return null;
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layoutResID);
		
		SimpleAsyncTask task = new ShowInfoAsyncTask(this);		
		
		task.execute((Void[])null);

	}
}

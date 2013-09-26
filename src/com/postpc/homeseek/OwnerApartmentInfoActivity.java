package com.postpc.homeseek;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.managers.HSApartmentsManager;
import com.postpc.homeseek.core.session.HSSessionManager;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

public class OwnerApartmentInfoActivity extends AbstructApartmentInfoActivity {

	private static final int UPDATE_REQ = 7412;
	
	public OwnerApartmentInfoActivity() {
		super(R.layout.activity_owner_apartment_info);
	}
	
	public void onAddPhotoClick(View view){
		String userId;

		try {
			userId = HSSessionManager.getActiveSession().getUser().getId();
		} catch (UserNotLoggedInException e) {
			LoginActivity.launch(this);
			return;
		}
		
		Intent intent = new Intent(this, AddPhotoActivity.class);
		intent.putExtra(AddPhotoActivity.ARG_APARTMENT_ID, apartmentId);
		intent.putExtra(AddPhotoActivity.ARG_USER_ID, userId);
		intent.putExtra(AddPhotoActivity.ARG_VISIBILITY, true);
		
		// TODO start for result
		startActivity(intent);
	}

	public void onShowPhotosClick(View view){
		Intent intent = new Intent(this, ApartmentPhotosActivity.class);
		intent.putExtra(ApartmentPhotosActivity.ARG_APARTMENT_ID, apartmentId);
		
		startActivity(intent);
	}
	
	public void onEditClick(View view){
		Intent intent = new Intent(this, UpdateApartmentActivity.class);
		intent.putExtra(UpdateApartmentActivity.ARG_APARTMENT_ID, apartmentId);
		intent.putExtra(UpdateApartmentActivity.ARG_CITY, 
				HomeSeekViewUtils.getTxtDataAsString(this, R.id.apartment_city_txt));
		intent.putExtra(UpdateApartmentActivity.ARG_NEIGHBORHOOD, 
				HomeSeekViewUtils.getTxtDataAsString(this, R.id.apartment_neighborhood_txt));
		intent.putExtra(UpdateApartmentActivity.ARG_STREET, 
				HomeSeekViewUtils.getTxtDataAsString(this, R.id.apartment_street_txt));
		intent.putExtra(UpdateApartmentActivity.ARG_BUILDING, 
				HomeSeekViewUtils.getTxtDataAsString(this, R.id.apartment_building_number_txt));
		intent.putExtra(UpdateApartmentActivity.ARG_APAPRTMENT_NUMBER, 
				HomeSeekViewUtils.getTxtDataAsString(this, R.id.apartment_ap_number_txt));
		intent.putExtra(UpdateApartmentActivity.ARG_ROOMS, 
				HomeSeekViewUtils.getTxtDataAsDouble(this, R.id.apartment_rooms_txt));
		intent.putExtra(UpdateApartmentActivity.ARG_AREA, 
				HomeSeekViewUtils.getTxtDataAsInteger(this, R.id.apartment_area_txt));
		intent.putExtra(UpdateApartmentActivity.ARG_PRICE, 
				HomeSeekViewUtils.getTxtDataAsInteger(this, R.id.apartment_price_txt));
		intent.putExtra(UpdateApartmentActivity.ARG_OWNER_COMMENTS, 
				HomeSeekViewUtils.getTxtDataAsString(this, R.id.apartment_owner_comments_txt));
		startActivityForResult(intent, UPDATE_REQ);
	}
	
	public void onCalendarClick(View view){
		Intent intent = new Intent(this, ApartmentCalendarActivity.class);
		intent.putExtra(ApartmentCalendarActivity.ARG_APARTMENT_ID, apartmentId);
		intent.putExtra(ApartmentCalendarActivity.ARG_APARTMENT_TITLE,
				HSApartment.getApartemntTitle(HomeSeekViewUtils.getTxtDataAsString(this, R.id.apartment_city_txt),
				HomeSeekViewUtils.getTxtDataAsString(this, R.id.apartment_street_txt),
				HomeSeekViewUtils.getTxtDataAsString(this, R.id.apartment_building_number_txt)));
		startActivity(intent);
	}
	
	public void onClientsListClick(View view){
		Intent intent = new Intent(this, ApartmentClientsActivity.class);
		intent.putExtra(ApartmentClientsActivity.ARG_APARTMENT_ID, apartmentId);
		
		startActivity(intent);
	}
	
	public void onRemoveClick(View view){
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					HSApartmentsManager.removeApartment(apartment);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};
		task.execute((Void)null);
		
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == UPDATE_REQ && resultCode == RESULT_OK) {
    		//Update new details
    		SimpleAsyncTask task = new ShowInfoAsyncTask(this);		
    		task.execute((Void[])null);
    	}
    }	
		
}

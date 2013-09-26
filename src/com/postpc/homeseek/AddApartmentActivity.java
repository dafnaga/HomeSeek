package com.postpc.homeseek;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.exceptions.IllegalApartmentParametersException;
import com.postpc.homeseek.core.managers.HSApartmentsManager;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddApartmentActivity extends HSActivity {

	LatLng apartmentMapLocation = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_apartment);
		
		//set control bar
		HomeSeekViewUtils.setExternalLayout(this, R.id.top_ruller_bar_layout, R.layout.owner_add_apartment_top_bar);
		
		//set buttons
		HomeSeekViewUtils.setExternalLayout(this, R.id.btn_layout, R.layout.owner_add_apartment_btns);
	}
	
	public void onAddApartmentConfirmClick(View v) throws ParseException {
		SimpleAsyncTask task = new SimpleAsyncTask(this) {
			
			private String status;
			private String city;
			private String street;
			private String buildingNumber;
			private String apartmentNumber;
			private String neighborhood;
			private Double rooms;
			private Integer area;
			private Integer price;
			private String ownerComments;
			private LatLng mapLocation;
			
			private HSApartment apartment;

			private boolean success = false;
			
			@Override
			protected void preExecute() {
				city = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.city_edt);
				neighborhood = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.neighborhood_edt);
				street = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.street_edt);
				buildingNumber = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.building_num_edt);
				apartmentNumber = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.apartment_num_edt);
				rooms = HomeSeekViewUtils.getEditableDataAsDouble(activity, R.id.rooms_edt);		
				area = HomeSeekViewUtils.getEditableDataAsInteger(activity, R.id.area_edt);
				price = HomeSeekViewUtils.getEditableDataAsInteger(activity, R.id.price_edt);
				ownerComments = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.owner_comments_edt);
				mapLocation = getApartmentMapLocation();
			}
			
			@Override
			protected void postExecute() {
				if (success) {
					Intent intent = new Intent(activity, OwnerApartmentInfoActivity.class);
					intent.putExtra(OwnerApartmentInfoActivity.ARG_APARTMENT_ID, apartment.getId());
					activity.startActivity(intent);
					
				} else {
					Intent intent = new Intent(activity, ErrorOccurredActivity.class);
					intent.putExtra(ErrorOccurredActivity.ARG_ERROR_STATUS_EXTRA, status);
					activity.startActivity(intent);
				}
				
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
								
				try {
					apartment = HSApartmentsManager.createNewApartment(city, neighborhood, street, buildingNumber, 
							apartmentNumber, rooms, area, price, mapLocation, ownerComments);
					status = "New apartment created successfully!";
					success = true;
					return null;
				} catch (UserNotLoggedInException e) {
					status = "User is not logged in";
				} catch (ParseException e) {
					status = "Failed to add new Apartment!";					
					e.printStackTrace();
				} catch (IllegalApartmentParametersException e) {
					status = "Illigal Apartment Paramenters!";
					e.printStackTrace();
				}				
				success = false;				
				return null;
			}
		};
		
		task.execute((Void[])null);
	}

	public void onShowMapLocationClick(View view){
		Intent intent = new Intent(this, ShowMapLocationActivity.class);
		
		// TODO pass on the intent the initial lat/lang to be extracted from the given address (if possible)
		
		LatLng locationSuggestion = getApartmentMapLocation();
		if (locationSuggestion == null){
			Toast toast = Toast.makeText(this, "Unable to resolve address", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}
		
		intent.putExtra(ShowMapLocationActivity.ARG_LAT, locationSuggestion.latitude);
		intent.putExtra(ShowMapLocationActivity.ARG_LNG, locationSuggestion.longitude);
		startActivity(intent);
	}
	
	private LatLng getApartmentMapLocation() {
		String city = ((EditText)findViewById(R.id.city_edt)).getText().toString();
		String neighborhood = ((EditText)findViewById(R.id.neighborhood_edt)).getText().toString();
		String street = ((EditText)findViewById(R.id.street_edt)).getText().toString();
		String buildingNumber = ((EditText)findViewById(R.id.building_num_edt)).getText().toString();
		
		String addressTxt = String.format("Israel, %s, %s, %s, %s", city, neighborhood, street, buildingNumber);
					
		LatLng location = HomeSeekMapUtils.getLocationFromAddress(this, addressTxt);
		return location;
	}

	@Override
	public void onRecentConversationsClick(View v) {
		Intent intent = new Intent(this, RecentConversationsActivity.class);
		intent.putExtra(RecentConversationsActivity.ARG_USER_TYPE, RecentConversationsActivity.MODE_OWNER);
		startActivity(intent);
	}
	
}

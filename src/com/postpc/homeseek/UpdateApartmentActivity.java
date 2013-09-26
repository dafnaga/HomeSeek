package com.postpc.homeseek;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.managers.HSApartmentsManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchApartmentException;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class UpdateApartmentActivity extends Activity {
	
	public static final String ARG_APARTMENT_ID = "APARTMENT_ID";
	public static final String ARG_CITY = "CITY";
	public static final String ARG_STREET = "STREET";
	public static final String ARG_BUILDING = "BUILDING_NUM";
	public static final String ARG_APAPRTMENT_NUMBER = "APARTMNET_NUM";
	public static final String ARG_ROOMS = "ROOMS";
	public static final String ARG_AREA = "AREA";
	public static final String ARG_PRICE = "PRICE";
	public static final String ARG_NEIGHBORHOOD = "NEIGHBORHOOD";
	public static final String ARG_OWNER_COMMENTS = "OWNER_COMMENTS";
	
	private String apartmentId;
	private String city;
	private String street;
	private String neighborhood;
	private String buildingNumber;
	private String apartmentNumber;
	private Double rooms;
	private Integer area;
	private Integer price;
	private String ownerCommennts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_apartment);
		
		Intent intent = getIntent();
		apartmentId = intent.getStringExtra(ARG_APARTMENT_ID);
		city = intent.getStringExtra(ARG_CITY);
		neighborhood = intent.getStringExtra(ARG_NEIGHBORHOOD);
		street = intent.getStringExtra(ARG_STREET);
		buildingNumber = intent.getStringExtra(ARG_BUILDING);
		apartmentNumber = intent.getStringExtra(ARG_APAPRTMENT_NUMBER);
		rooms = HomeSeekIntentUtils.getDoubleFromIntent(intent, ARG_ROOMS);		
		area = HomeSeekIntentUtils.getIntegerFromIntent(intent, ARG_AREA);
		price = HomeSeekIntentUtils.getIntegerFromIntent(intent, ARG_PRICE);
		ownerCommennts = intent.getStringExtra(ARG_OWNER_COMMENTS);
		
		//set view
		HomeSeekViewUtils.setTxt(this, R.id.activity_title_txt, HSApartment.getApartemntTitle(city,street,buildingNumber));
		HomeSeekViewUtils.setTxt(this, R.id.neighborhood_edt, neighborhood);
		HomeSeekViewUtils.setTxt(this, R.id.building_num_edt, buildingNumber);
		HomeSeekViewUtils.setTxt(this, R.id.apartment_num_edt, apartmentNumber);
		HomeSeekViewUtils.setDecimalNumberTxt(this, R.id.rooms_edt, rooms);
		HomeSeekViewUtils.setNumberTxt(this, R.id.area_edt, area);
		HomeSeekViewUtils.setNumberTxt(this, R.id.price_edt, price);
		HomeSeekViewUtils.setTxt(this, R.id.owner_comments_edt, ownerCommennts);
		
		//set buttons
		HomeSeekViewUtils.setExternalLayout(this, R.id.btn_layout, R.layout.owner_update_apartment_btns);

		//disable city & street layouts 
		View layout = findViewById(R.id.city_layout);
		layout.setVisibility(View.GONE);
		layout = findViewById(R.id.street_layout);
		layout.setVisibility(View.GONE);

	}
	
	public void onSaveClick(View v) throws ParseException {
		SimpleAsyncTask task = new SimpleAsyncTask(this) {
			
			private boolean success = false;
			
			@Override
			protected void preExecute() {
				neighborhood = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.neighborhood_edt);
				buildingNumber = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.building_num_edt);
				apartmentNumber = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.apartment_num_edt);
				rooms = HomeSeekViewUtils.getEditableDataAsDouble(activity, R.id.rooms_edt);		
				area = HomeSeekViewUtils.getEditableDataAsInteger(activity, R.id.area_edt);
				price = HomeSeekViewUtils.getEditableDataAsInteger(activity, R.id.price_edt);
				ownerCommennts = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.owner_comments_edt);
			}
			
			@Override
			protected void postExecute() {
				if (!success){
					setResult(RESULT_CANCELED);
				}else {
					setResult(RESULT_OK);
				}
				finish();
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {	
				try {
					//TODO - problem with deleting values. the function getEditableDataAsString returns null - so the value won't be updated 
					HSApartmentsManager.updateApartment(apartmentId, neighborhood, 
							buildingNumber, apartmentNumber, rooms, area, price, ownerCommennts);
					success = true;
				} catch (UserNotLoggedInException e) {
					success = false;
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					success = false;
				} catch (NoSuchApartmentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					success = false;
				}				
				return null;
			}
		};
		
		task.execute((Void[])null);
	}

	public void onCancelClick(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}
	
}

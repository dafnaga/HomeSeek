package com.postpc.homeseek;

import java.io.Serializable;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PickApartmentSearchAreaActivity extends MapActivity {

	public static final String ARG_INIT_AREA = "arg_init_area";
	public static final String RES_SEACH_AREA = "res_area";
	private static final Double RADIUS_UNIT = 0.1;	
	
	private MapCircle searchArea;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_apartment_search_area);
		
		initMap(R.id.map);
		
		map.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng point) {
				searchArea.setCenter(point);				
				refreshMap();
			}
		});
		
		Intent intent = getIntent();
		if (intent.hasExtra(ARG_INIT_AREA)){
			searchArea = (MapCircle)intent.getSerializableExtra(ARG_INIT_AREA);			
		} 
		
		if (searchArea == null){
			searchArea = new MapCircle();
		}
		
		refreshMap();		
	}
	
	protected void refreshMap() {
		map.clear();
		map.addMarker(new MarkerOptions()
        .position(searchArea.getCenter())
        .title("Center"));
		
		map.addCircle(new CircleOptions()
							.center(searchArea.getCenter())
							.radius(searchArea.getRadius()*1000)
							.fillColor(0x40ff0000)
							.strokeColor(0)
							.strokeWidth(5)
							.visible(true));

		moveToLocation(searchArea.getCenter());
	}
	
	public void onFindClick(View view){
		String addressTxt = ((EditText)findViewById(R.id.address_txt)).getText().toString();
		LatLng location = HomeSeekMapUtils.getLocationFromAddress(this, addressTxt);
		if (location == null){
			// Bad location, or some error occured. 
			Toast toast = Toast.makeText(this, "Error resolving address", Toast.LENGTH_SHORT);
			toast.show();
			return;
		}
		moveToLocation(location);
	}

	public void onMinusClick(View v){
		Double radius = searchArea.getRadius();
		
		if (radius > RADIUS_UNIT){
			radius -= RADIUS_UNIT;
		}
		
		searchArea.setRadius(radius);
		refreshMap();
	}

	public void onPlusClick(View v){
		Double radius = searchArea.getRadius();
		
		radius += RADIUS_UNIT;
		
		searchArea.setRadius(radius);
		refreshMap();
	}	

	public void onOkClick(View v){
		Intent intent = new Intent();
		
		intent.putExtra(RES_SEACH_AREA, (Serializable)searchArea);
		
		if (getParent() != null){
			getParent().setResult(RESULT_OK, intent);
		} else {
			setResult(RESULT_OK, intent);
		}
				
		finish();
	}
}

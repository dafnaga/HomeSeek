package com.postpc.homeseek;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ShowMapLocationActivity extends MapActivity {

	public static final String ARG_LAT = "arg_lat";
	public static final String ARG_LNG = "arg_lng";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_map_location);

		initMap(R.id.map);
		
		Intent intent = getIntent();
		double lat = intent.getDoubleExtra(ARG_LAT, 0); 
		double lng = intent.getDoubleExtra(ARG_LNG, 0);
		
		LatLng initLoc = new LatLng(lat, lng);
		moveToLocation(initLoc);		
	}
	
	protected void setLatLng(LatLng point) {
		map.clear();
		map.addMarker(new MarkerOptions()
        .position(point)
        .title("Your apartment"));
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

}

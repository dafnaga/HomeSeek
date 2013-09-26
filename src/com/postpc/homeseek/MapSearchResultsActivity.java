package com.postpc.homeseek;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.managers.HSApartmentQuery;
import com.postpc.homeseek.core.managers.HSApartmentsManager;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MapSearchResultsActivity extends MapActivity implements OnInfoWindowClickListener{

	public static final String ARG_SEARCH_QUERY = "arg_search_query";
	private List<HSApartment> apartments = new ArrayList<HSApartment>();
	private Map<Marker, HSApartment> markerToApartment = new HashMap<Marker, HSApartment>();
	private HSApartmentQuery searchQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_search_results);
		
		initMap(R.id.map);
		
		map.setInfoWindowAdapter(new InfoWindowAdapter() {
			
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}
			
			@Override
			public View getInfoContents(Marker marker) {
				View infoView = getLayoutInflater().inflate(R.layout.apartment_info_window_layout, null);
				HSApartment apartment = markerToApartment.get(marker);
				
				// Fill apartment details
				
				// Set title
				String title = apartment.getTitle();
				((TextView)infoView.findViewById(R.id.activity_title_txt)).setText(title);
				
				// Set price
				String priceTxt = getString(R.string.unspecified);
				Integer price = apartment.getPrice();
				if (price != null){
					priceTxt = price.toString();
				}
				((TextView)infoView.findViewById(R.id.priceTxt)).setText(priceTxt);
				
				// Set the tag for the button
				View detailsBtn = infoView.findViewById(R.id.details_btn);
				detailsBtn.setTag(apartment);
				
				return infoView;
			}
		});
		
		map.setOnInfoWindowClickListener(this);
		
		// Get the search query
		Intent intent = getIntent();
		searchQuery = (HSApartmentQuery)intent.getSerializableExtra(ARG_SEARCH_QUERY);

		// Obtain the results
		SimpleAsyncTask task = new SimpleAsyncTask(this) {

			boolean success;
			String error = "Seach error";
			
			@Override
			protected void preExecute() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void postExecute() {
				if (!success){
					Toast toast = Toast.makeText(activity, error, Toast.LENGTH_SHORT);
					toast.show();
					activity.finish();
					return;
				}
				
				refreshMap();
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
				
				try {
					apartments = HSApartmentsManager.findApartments(searchQuery);
					success = true;
					return null;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				success = false;					
				return null;
			}
		};
		
		task.execute();
	}

	protected void refreshMap() {
		map.clear();
		
		LatLngBounds.Builder builder = new LatLngBounds.Builder();		
		
		// Go over the results and place a marker for each apartment.
		for (HSApartment apartment : apartments){
			LatLng location = apartment.getMapLocation();
			
			if (location == null){
				continue;
			}
			
			builder.include(location);
			
			Marker marker = map.addMarker(new MarkerOptions()
								.position(location)
								.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_house_icon))
								.title(apartment.getTitle()));			
			markerToApartment.put(marker, apartment);			
		}
		
		// Make sure all markers are visible
		LatLngBounds bounds = builder.build();
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 25);
		map.animateCamera(cu);
	}

	public void onDetailsClick(View v){
		HSApartment apartment = (HSApartment)v.getTag();
		
		Intent intent = new Intent(this, ClientApartmentInfoActivity.class);
		intent.putExtra(ClientApartmentInfoActivity.ARG_APARTMENT_ID, apartment.getId());
		
		startActivity(intent);
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		HSApartment apartment = markerToApartment.get(marker);				
		
		Intent intent = new Intent(this, ClientApartmentInfoActivity.class);
		intent.putExtra(ClientApartmentInfoActivity.ARG_APARTMENT_ID, apartment.getId());
		
		startActivity(intent);				
	}	
}

package com.postpc.homeseek;

import android.app.Activity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends Activity {
	
	protected GoogleMap map;
	
	protected void initMap(int mapId){		
		map = ((MapFragment) getFragmentManager().findFragmentById(mapId)).getMap();
		setupMap(); //TODO need to be onResume() 
		map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);	
		map.setMyLocationEnabled(true); 

		GoogleMapOptions options = new GoogleMapOptions();
		options.mapType(GoogleMap.MAP_TYPE_TERRAIN)
	    .compassEnabled(false)
	    .rotateGesturesEnabled(false)
	    .tiltGesturesEnabled(false);
	}
	
	private void setupMap() {
	    // Do a null check to confirm that we have not already instantiated the map.
	    if (map == null) {
	        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	                            .getMap();
	        // Check if we were successful in obtaining the map.
	        if (map != null) {
	            // The Map is verified. It is now safe to manipulate the map.

	        }
	        
	        // TODO what if we can't get the map?
	    }
	}
	
	protected void moveToLocation(LatLng location){
		// Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
		CameraPosition cameraPosition = new CameraPosition.Builder()
		    .target(location)      // Sets the center of the map to Mountain View
		    .zoom(17)                   // Sets the zoom
		    .bearing(0)                
		    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
		    .build();                   // Creates a CameraPosition from the builder
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));		
	}	
}

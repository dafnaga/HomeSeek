package com.postpc.homeseek;

import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

public class HomeSeekMapUtils {

	public static LatLng getLocationFromAddress(Context context, String addressTxt){
		Geocoder coder = new Geocoder(context);
		List<Address> address;

		try {
		    address = coder.getFromLocationName(addressTxt,5);
		    if (address == null) {
		        return null;
		    }
		    
		    Address location = address.get(0);
		    location.getLatitude();
		    location.getLongitude();

		    LatLng p = new LatLng(location.getLatitude(), location.getLongitude());
		    return p;
		} 
		catch (Exception e) {
			return null;
		}
	}
	
}

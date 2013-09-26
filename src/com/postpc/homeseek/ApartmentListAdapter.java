package com.postpc.homeseek;

import java.util.HashMap;
import java.util.Map;

import com.postpc.homeseek.core.hsobjects.HSApartment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ApartmentListAdapter extends ArrayAdapter<HSApartment>{

	private int layoutResource;
	private HSApartment[] apartments;
	private final Context context;
	
	public ApartmentListAdapter(Context context, int resource,
			HSApartment[] apartments) {
		super(context, resource, apartments);
		
		this.context = context;		
		this.layoutResource = resource;
		this.apartments = apartments;
	}
	
	@Override 
	public View getView(int position, View convertView, ViewGroup parent)
	{		
		HSApartment apartment = apartments[position];
		View row = convertView;
		
		if (row != null){
			HSApartment curApartment = (HSApartment)row.getTag();
			if (curApartment.getId().equals(apartment.getId())){
				// Same apartment, no need to rebuild
				return row;
			}		
		}

		// Need to inflate/refill row
		
		if (row == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResource, parent, false);			
		}		
				
		HomeSeekViewUtils.fillViewWithApartmentInfo(row, apartment, R.drawable.default_photo_image);
		row.setTag(apartments[position]);
		return row;
	}

	public HSApartment[] getApartments() {
		return apartments;
	}

	public void setApartments(HSApartment[] apartments) {
		this.apartments = apartments;
		notifyDataSetChanged();
	}

}

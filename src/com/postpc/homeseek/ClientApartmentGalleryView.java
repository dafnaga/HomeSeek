package com.postpc.homeseek;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import com.postpc.homeseek.core.hsobjects.HSApartmentPhoto;

public class ClientApartmentGalleryView extends ApartmentPhotosGalleryView{

	public ClientApartmentGalleryView(Context context,
			List<HSApartmentPhoto> photos) {
		super(context, photos, R.layout.apartment_photos_gallery_layout, R.layout.apartment_gallery_img_layout);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onClick(View v) {
		final HSApartmentPhoto photo = (HSApartmentPhoto)v.getTag();
		
		final String choices[] = {"Send to friend"};
		
		AlertDialog.Builder builder = new AlertDialog.Builder((Activity)getContext());
		builder.setTitle("Gallery Photo");
		builder.setItems(choices, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				switch(which){
				case 0:
					handleSendToFriend(photo);
					break;

				default:
					break;					
				}
				
			}
		});
		
		builder.show();		
	}

	protected void handleSendToFriend(HSApartmentPhoto photo) {
		if (actionHandler != null){
			actionHandler.onSharePhotoWithFriend(photo);
		}
	}
	
}

package com.postpc.homeseek;

import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.HSApartmentPhoto;
import com.postpc.homeseek.core.hsobjects.HSInstantMessage;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSApartmentsManager;
import com.postpc.homeseek.core.managers.HSUsersManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PhotoInstantMessageViewCreator{


	public static View create(final Context context, final HSInstantMessage im, final HSUser currentUser, ViewGroup parent) {
		// TODO - for now same as text msg
		
		final View chatTxtViewWrap = ((Activity)context).getLayoutInflater().inflate(R.layout.im_photo_layout,null);		

		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void> () {
			
			HSUser fromUser;
			HSApartment apartment = null;
			HSApartmentPhoto photo;
			byte[] photoData;
			String txt, name;
			private ImageView imgView;
			private ProgressBar progressBar;
			private TextView chatTxtView;
			private TextView chatEmailView;

			@Override
			protected void onPreExecute() {
				chatTxtView = (TextView)chatTxtViewWrap.findViewById(R.id.chat_txt);
				chatEmailView = (TextView)chatTxtViewWrap.findViewById(R.id.chat_email);
				progressBar = (ProgressBar)chatTxtViewWrap.findViewById(R.id.img_progress_bar);
				imgView = (ImageView)chatTxtViewWrap.findViewById(R.id.chat_img);
				
				chatTxtView.setText(R.string.loading);
				chatEmailView.setText(R.string.loading);
				
				imgView.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				progressBar.animate();
			}
			
			@Override
			protected void onPostExecute(Void result) {				
				chatTxtView.setText(txt);
				chatEmailView.setText(name);
				if (photoData != null){
					Bitmap bmp = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);						
					imgView.setImageBitmap(bmp);
					
					imgView.setTag(apartment);
					
					imgView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							HSApartment apartment = (HSApartment)v.getTag();
							if (apartment == null){
								return;
							}
							
							Intent intent = new Intent(context, ClientApartmentInfoActivity.class);
							intent.putExtra(ClientApartmentInfoActivity.ARG_APARTMENT_ID, apartment.getId());
							context.startActivity(intent);
						}
					});
															
				} else {
					imgView.setImageResource(R.drawable.photo_deleted_image);
				}
				
				imgView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				
				chatTxtViewWrap.setVisibility(View.VISIBLE);
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
				txt = "";
				name = "Error: User doesn't exist";
				photoData = null;

				try {
					fromUser = HSUsersManager.getUserById(im.getFromUserId());
					name = fromUser.getFullName();					

					photo = HSApartmentsManager.getApartmentPhotoById(im.getMessageData());			
					photoData = photo.getPhotoData();					

					apartment = HSApartmentsManager.getApartmentById(photo.getApartmentId());
					txt = String.format("Photo for apartment: %s", apartment.getTitle());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
		};
		
		task.execute((Void[])null);	
		
		return chatTxtViewWrap;
	}
	
}

package com.postpc.homeseek;

import java.util.List;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.HSApartmentPhoto;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSApartmentsManager;
import com.postpc.homeseek.core.managers.HSUsersManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchApartmentException;
import com.postpc.homeseek.core.managers.exceptions.NoSuchUserException;
import com.postpc.homeseek.core.session.HSSessionManager;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ApartmentPhotosActivity extends Activity implements GalleryViewActionHandler {

	public static final String ARG_APARTMENT_ID = "APARTMENT_ID";
	public static final int REQ_PICK_CONTACT = 1;
	private HSApartmentPhoto selectedPhoto;
	private HSApartment apartment;
	private ApartmentPhotosGalleryView galleryView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apartment_photos);
	}
	
	@Override
	protected void onResume(){
		super.onResume();

		final String apartmentId = getIntent().getStringExtra(ARG_APARTMENT_ID);
		if (apartmentId == null){
			finish();
		}
		
		SimpleAsyncTask task = new SimpleAsyncTask(this) {

			private List<HSApartmentPhoto> photos;
			private String error;
			private boolean success;

			@Override
			protected void preExecute() {
				if (galleryView != null){
					LinearLayout container = (LinearLayout)findViewById(R.id.photo_gallery_container);
					container.removeViewInLayout(galleryView);
					galleryView = null;
				}
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					apartment = HSApartmentsManager.getApartmentById(apartmentId);
					photos = HSApartmentsManager.getApartmentPhotos(apartmentId);

					if (apartment.getOwnerId() == HSSessionManager.getActiveSession().getUser().getId()){
						// Owner
						galleryView = new OwnerApartmentGalleryView(activity, photos);
					} else {
						// Not owner
						galleryView = new ClientApartmentGalleryView(activity, photos);
					}
					success = true;
					return null;
				} catch (NoSuchApartmentException e) {
					error = "Apartment no longer exists!";
					e.printStackTrace();
				} catch (ParseException e) {
					error = "Error connecting to servers.";
					e.printStackTrace();
				} catch (UserNotLoggedInException e) {
					LoginActivity.launch(activity);
					activity.finish();
				}
				success = false;
				return null;
			}

			@Override
			protected void postExecute() {
				if (!success){
					Toast toast = Toast.makeText(activity, error, Toast.LENGTH_SHORT);
					toast.show();
					activity.finish();
				}
				galleryView.setActionHandler((ApartmentPhotosActivity)activity);
				LinearLayout container = (LinearLayout)findViewById(R.id.photo_gallery_container);
				container.addView(galleryView);
			}

		};

		task.execute((Void[])null);
	}

	@Override
	public void onSharePhotoWithFriend(HSApartmentPhoto photo) {
		this.selectedPhoto = photo;
		// Let the user pick a contact
		Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
		intent.setType(Email.CONTENT_TYPE);				
		startActivityForResult(intent, REQ_PICK_CONTACT);		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		// Check which request we're responding to
		if (requestCode == REQ_PICK_CONTACT) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				// The user picked a contact to send a picture to.
				// The Intent's data Uri identifies which contact was selected.

				SimpleAsyncTask task = new SimpleAsyncTask(this) {

					private boolean success;
					private String error;

					@Override
					protected void preExecute() {
						// Nothing to do
					}

					@Override
					protected void postExecute() {
						String txt = error;
						if (success){
							txt = "Photo sent successfuly";
						}

						Toast toast = Toast.makeText(activity, txt, Toast.LENGTH_SHORT);
						toast.show();

					}

					@Override
					protected Void doInBackground(Void... arg0) {
						// Get the URI that points to the selected contact
						Uri contactUri = data.getData();

						// We only need the ADDRESS column
						String[] projection = {Email.ADDRESS};

						// Perform the query on the contact to get the EMAIL column
						Cursor cursor = getContentResolver()
								.query(contactUri, projection, null, null, null);
						cursor.moveToFirst();

						// Retrieve the email address from the ADDRESS columns
						int column = cursor.getColumnIndex(Email.ADDRESS);
						String email = cursor.getString(column);

						// Got an email address, locate user and send

						HSUser toUser;
						try {
							toUser = HSUsersManager.getUserByEmail(email);
							HSUser currentUser = HSSessionManager.getActiveSession().getUser();
							HSApartmentsManager.sendApartmentPhotoToUser(selectedPhoto, currentUser, toUser);
							success = true;

							return null;
						} catch (ParseException e1) {
							error = "Error connecting to server.";
							e1.printStackTrace();
						} catch (NoSuchUserException e1) {
							error = "User isn't signed up to HomeSeek.";
							e1.printStackTrace();
						} catch (UserNotLoggedInException e) {
							error = "Please login first.";
							e.printStackTrace();
						}

						success = false;
						return null;
					}
				};
				task.execute((Void[])null);
			} else {
				// TODO notify the user
			}
		}
	}


	public void onAddPhotoClick (View v) {
		String userId;

		try {
			userId = HSSessionManager.getActiveSession().getUser().getId();
		} catch (UserNotLoggedInException e) {
			LoginActivity.launch(this);
			return;
		}

		Intent intent = new Intent(this, AddPhotoActivity.class);
		intent.putExtra(AddPhotoActivity.ARG_APARTMENT_ID, apartment.getId());
		intent.putExtra(AddPhotoActivity.ARG_USER_ID, userId);
		// TODO: ARG_VISIBILITY should be determined differently?.  
		intent.putExtra(AddPhotoActivity.ARG_VISIBILITY, userId.equals(apartment.getOwnerId()));

		startActivity(intent);
	}

	public void onSharePhotoClick (View v) {
		HSApartmentPhoto photo = galleryView.getCurrentlyDisplayedPhoto();
		if (photo != null){
			onSharePhotoWithFriend(photo);		
		}
	}

	public void onBackBtnClick(View v) {
		onBackPressed();
	}

}

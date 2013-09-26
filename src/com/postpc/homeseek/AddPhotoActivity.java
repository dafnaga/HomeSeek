package com.postpc.homeseek;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.postpc.homeseek.core.managers.HSApartmentsManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class AddPhotoActivity extends Activity {

	private static final int REQ_SELECT_PICTURE = 1;

	public static final String ARG_APARTMENT_ID = "APARTMENT_ID";
	public static final String ARG_USER_ID = "USER_ID";
	public static final String ARG_VISIBILITY = "VISIBILITY";

	private String userId;
	private String apartmentId;
	private boolean isPublic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_photo);
		
		Intent intent = getIntent();
		
		userId = intent.getStringExtra(ARG_USER_ID);
		apartmentId = intent.getStringExtra(ARG_APARTMENT_ID);
		isPublic = intent.getBooleanExtra(ARG_VISIBILITY, false);
		
		addPhoto();
	}
	

	private void addPhoto() {
		Intent pickIntent = new Intent();
		pickIntent.setType("image/*");
		pickIntent.setAction(Intent.ACTION_GET_CONTENT);

		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
		Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
		chooserIntent.putExtra
		(
		  Intent.EXTRA_INITIAL_INTENTS, 
		  new Intent[] { takePhotoIntent }
		);

		startActivityForResult(chooserIntent, REQ_SELECT_PICTURE);		
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	
	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	
	    return inSampleSize;
	}
	
	private int getPhotoOrientation(Uri photoUri) {
	    /* it's on the external media. */
	    Cursor cursor = this.getContentResolver().query(photoUri,
	            new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

	    if (cursor.getCount() != 1) {
	        return -1;
	    }

	    cursor.moveToFirst();
	    return cursor.getInt(0);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != REQ_SELECT_PICTURE){
			finish();
			return;
		}
		
		if (data == null || data.getData() == null){
			finish(); 
			return;
		}
		
        Uri imageUri = data.getData();

        // User had picked an image.
        
        // Get the orientation
        final int orientation = getPhotoOrientation(imageUri);

        Cursor cursor = getContentResolver().query(imageUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
        cursor.moveToFirst();

        //Link to the image
        final String imageFilePath = cursor.getString(0);
        cursor.close();
				
		SimpleAsyncTask task = new SimpleAsyncTask(this) {
			
			String error = "An error has occured";
			boolean success;
			
			final int height = 512;
			final int width = 384;
			
			@Override
			protected void preExecute() {
			}
			
			@Override
			protected void postExecute() {
				if (!success){
					Toast toast = Toast.makeText(activity, error, Toast.LENGTH_SHORT);
					toast.show();					
				}
				finish();
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {

				try {
					Matrix matrix = new Matrix();
			        File photoFile;			        
			        byte[] photoData;
			        
			        int reqWidth;
			        int reqHeight;
			        
			        photoFile = new File(imageFilePath);
			        InputStream is = new FileInputStream(photoFile);

			        // Decode the bounds of the image
			        BitmapFactory.Options opts = new BitmapFactory.Options();
			        opts.inJustDecodeBounds = true;
			        BitmapFactory.decodeStream(is, null, opts);
			        is.close();

			        // Rotate the image if needed
			        if (orientation != 0 && orientation != -1){
				        reqWidth = height;
				        reqHeight = width;
			        	matrix.postRotate(orientation);
			        } else {
				        reqWidth = width;
				        reqHeight = height;			        	
			        	matrix.postRotate(0);
			        }
			        
			        // Calculate the desired sample size			        			        
			        int sampleSize = calculateInSampleSize(opts, reqWidth, reqHeight);
			        opts = new BitmapFactory.Options();
			        opts.inSampleSize = sampleSize;
			        
			        // Finally decode the image
			        is = new FileInputStream(photoFile);
		        	Bitmap srcBmp = BitmapFactory.decodeStream(is, null, opts);
		        	is.close();
		        	
			        // Rotate it (TODO any better way?)
		        	Bitmap newBmp = Bitmap.createBitmap(srcBmp, 0, 0, srcBmp.getWidth(), srcBmp.getHeight(), matrix, true);
		        	
		        	// Now compress it
		        	ByteArrayOutputStream os = new ByteArrayOutputStream();
		        	
		        	boolean compressSuccessful = newBmp.compress(CompressFormat.JPEG, 80, os);
		        	if (!compressSuccessful){
		        		return null;
		        	}
		        	photoData = os.toByteArray();
			        
		        	// Upload it
					HSApartmentsManager.addApartmentPhoto(apartmentId, photoData, userId, isPublic);
					success = true;
					
					return null;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				success = false;
				return null;
			}
		};

		task.execute();
  	}
}

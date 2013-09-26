package com.postpc.homeseek;

import com.postpc.homeseek.core.hsobjects.HSApartment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

public class OwnerQRCodeActivity extends Activity{

	public static final String ARG_APARTMENT_ID = "APARTMENT_ID";
//	protected HSApartment apartment;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//
//		final String apartmentId = getIntent().getStringExtra(ARG_APARTMENT_ID); 
//
//		SimpleAsyncTask task = new SimpleAsyncTask(this) {
//
//			@Override
//			protected void preExecute() {
//
//			}
//
//			@Override
//			protected void postExecute() {
//				try {
//				    // generate a 150x150 QR code
//				    Bitmap bm = encodeAsBitmap(apartmentId, BarcodeFormat.QR_CODE, 150, 150);
//
//				    if(bm != null) {
//				        image_view.setImageBitmap(bm);
//				    }
//				} catch (WriterException e) {}
//			}
//
//			@Override
//			protected Void doInBackground(Void... arg0) {
//				return null;
//
//			}
//		};
//
//		task.execute((Void[])null);
//	}
}

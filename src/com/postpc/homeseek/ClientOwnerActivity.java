package com.postpc.homeseek;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class ClientOwnerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client_owner);
	}

	public void onSearchApartmentBtnClick(View v) {
        // Perform action on click
		Intent intent = new Intent(this, SearchApartmentsActivity.class);
		intent.putExtra(SearchApartmentsActivity.ARG_SEARCH_TYPE, SearchApartmentsActivity.SEARCH_MODE_ADDRESS);		
		startActivity(intent);
    }
	
	public void onPublishApartmentBtnClick(View v) {
        // Perform action on click
		Intent intent = new Intent(this, OwnerActivity.class);		
		startActivity(intent);
    }
	
}

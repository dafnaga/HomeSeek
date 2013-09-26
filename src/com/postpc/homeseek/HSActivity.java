package com.postpc.homeseek;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public abstract class HSActivity extends Activity {

	
	abstract public void onRecentConversationsClick(View v);
	
	public void onBackBtnClick(View v) {
		onBackPressed();
	}
	
	// Client Clicks
	public void onSearchApartmentsBtnClick(View v) {
		Intent intent = new Intent(this, SearchApartmentsActivity.class);
		intent.putExtra(SearchApartmentsActivity.ARG_SEARCH_TYPE, 
						SearchApartmentsActivity.SEARCH_MODE_ADDRESS);
		startActivity(intent);		
	}
  
	public void onMapSearchBtnClick(View v) {
		Intent intent = new Intent(this, SearchApartmentsActivity.class);
		intent.putExtra(SearchApartmentsActivity.ARG_SEARCH_TYPE, 
						SearchApartmentsActivity.SEARCH_MODE_MAP);
		startActivity(intent);
    }
	
	public void onClientApartmentsBtnClick(View v) {
		Intent intent = new Intent(this, ClientApartmentsActivity.class);		
		startActivity(intent);		
	}

	// Owner Clicks
	public void onNewApartmentBtnClick(View v) {
		Intent intent = new Intent(this, AddApartmentActivity.class);		
		startActivity(intent);
	}
	
	public void onOwnerApartmentsBtnClick(View v) {
		Intent intent = new Intent(this, OwnerActivity.class);		
		startActivity(intent);		
	}
	
}

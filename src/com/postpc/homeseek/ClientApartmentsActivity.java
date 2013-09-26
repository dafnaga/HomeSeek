package com.postpc.homeseek;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSApartmentsManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchApartmentException;
import com.postpc.homeseek.core.session.HSSessionManager;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

public class ClientApartmentsActivity extends AbstractFindApartemtsActivity {

    
	public ClientApartmentsActivity() {
		super(R.layout.activity_list_view, R.id.activity_list, ClientApartmentInfoActivity.class/*TODO make button invisible*/);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set Title
		HomeSeekViewUtils.setTxt(this, R.id.activity_title_txt, getResources().getString(R.string.my_apartments_str));
		
		// Disable subtitle 
		findViewById(R.id.activity_subtitle_txt).setVisibility(View.INVISIBLE);
		
		//add top bar
		HomeSeekViewUtils.setExternalLayout(this, R.id.top_ruller_bar_layout, R.layout.client_apartments_top_bar);
	}
	
	@Override
	protected void setPreTaskExecute() {
		//do nothing
	}

	@Override
	protected List<HSApartment> findApartments() throws ParseException, UserNotLoggedInException, NoSuchApartmentException {
		HSUser client = HSSessionManager.getActiveSession().getUser();
		return HSApartmentsManager.getClientApartments(client);
	}

	@Override
	protected ApartmentListAdapter generateApartmentsListAdapter(HSApartment[] hsApartments) {
		return new ApartmentListAdapter(this, R.layout.apartment_list_row, hsApartments);
		
	}

	@Override
	public void onRecentConversationsClick(View v) {
		Intent intent = new Intent(this, RecentConversationsActivity.class);
		intent.putExtra(RecentConversationsActivity.ARG_USER_TYPE, RecentConversationsActivity.MODE_CLIENT);
		startActivity(intent);
		
	}
    
}

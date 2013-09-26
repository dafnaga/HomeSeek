package com.postpc.homeseek;

import java.util.List;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSApartmentsManager;
import com.postpc.homeseek.core.session.HSSessionManager;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

import android.content.Intent;
import android.view.View;

public class OwnerActivity extends AbstractFindApartemtsActivity {

    
	public OwnerActivity() {
		super(R.layout.activity_list_view, R.id.activity_list, OwnerApartmentInfoActivity.class);
	}
	
	@Override
	protected void setPreTaskExecute() {
		
		// Set title
		HomeSeekViewUtils.setTxt(activity, R.id.activity_title_txt, activity.getResources().getString(R.string.your_apartments_str));
		
		// Disable subtitle 
		activity.findViewById(R.id.activity_subtitle_txt).setVisibility(View.INVISIBLE);
		
		//set control bar
		HomeSeekViewUtils.setExternalLayout(activity, R.id.top_ruller_bar_layout, R.layout.owner_apartments_top_bar);
		
		//enable add apartment button
		HomeSeekViewUtils.setExternalLayout(activity, R.id.btn_layout, R.layout.owner_apartments_btns);
	}

	@Override
	protected List<HSApartment> findApartments() throws ParseException, UserNotLoggedInException {
		HSUser owner = HSSessionManager.getActiveSession().getUser();
		return HSApartmentsManager.getOwnerApartments(owner);
	}

	@Override
	protected ApartmentListAdapter generateApartmentsListAdapter(HSApartment[] hsApartments) {
		return new ApartmentListAdapter(this, R.layout.apartment_list_row, hsApartments);		
	}

	@Override
	public void onRecentConversationsClick(View v) {
		Intent intent = new Intent(this, RecentConversationsActivity.class);
		intent.putExtra(RecentConversationsActivity.ARG_USER_TYPE, RecentConversationsActivity.MODE_OWNER);
		startActivity(intent);
	}
	
}

package com.postpc.homeseek;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;

public class ClientActivity extends HSActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);
	}
	
	public void onRecentConversationsClick(View v) {
		Intent intent = new Intent(this, RecentConversationsActivity.class);
		intent.putExtra(RecentConversationsActivity.ARG_USER_TYPE, RecentConversationsActivity.MODE_CLIENT);
		startActivity(intent);
	}

}

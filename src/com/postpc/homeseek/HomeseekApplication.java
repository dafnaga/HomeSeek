package com.postpc.homeseek;

import com.parse.Parse;
import com.parse.PushService;

import android.app.Application;
import android.content.Context;

public class HomeseekApplication extends Application {

	private static Context context;

	public void onCreate() {
		super.onCreate();
		context = this.getApplicationContext();
		Parse.initialize(HomeseekApplication.getContext(), getString(R.string.parse_app_id), getString(R.string.parse_client_id));
		PushService.setDefaultPushCallback(context, MainActivity.class);
	}
	
	public static Context getContext() {
		return context;
	}
	
}

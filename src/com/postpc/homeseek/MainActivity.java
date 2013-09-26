package com.postpc.homeseek;

import com.postpc.homeseek.core.session.HSSession;
import com.postpc.homeseek.core.session.HSSessionManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		SimpleAsyncTask task = new SimpleAsyncTask(this) {			
			private HSSession session;

			@Override
			protected void preExecute() {
			}
			
			@Override
			protected void postExecute() {
				if (!session.isAnonymousSession()){
					Intent intent = new Intent(activity, ClientOwnerActivity.class);
					startActivity(intent);
				}
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
				session = HSSessionManager.getActiveSession();				
				return null;
			}
		};

		task.execute((Void[])null);
	}
	
	public void onSignUpBtnClick(View v) {
        // Perform action on click
		Intent intent = new Intent(this, SignUpActivity.class);		
		startActivity(intent);
    }
	
	public void onLoginBtnClick(View v) {
        // Perform action on click
		Intent intent = new Intent(this, LoginActivity.class);		
		startActivity(intent);
    }
	
	
}

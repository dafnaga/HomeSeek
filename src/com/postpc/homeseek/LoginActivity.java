package com.postpc.homeseek;

import com.parse.ParseException;
import com.postpc.homeseek.core.session.Credentials;
import com.postpc.homeseek.core.session.HSSessionManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}
	
	public void onLoginConfirmClick(View v) {

		SimpleAsyncTask task = new SimpleAsyncTask(this) {
			
			private String status;
			private boolean loggedIn;
			private String email;
			private String password;
			
			@Override
			protected Void doInBackground(Void... arg0) {
				loggedIn = false;
				
				try {
					HSSessionManager.login(new Credentials(email, password));
					
					status = "Logged in successfully!";
					loggedIn = true;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					status = e.getMessage();
					
					// TODO handle exception
				}

				
				return null;
			}

			@Override 
			protected void postExecute(){
				
				if (loggedIn){
					((TextView)activity.findViewById(R.id.status_txt)).setText(status);
					Intent intent = new Intent(activity, ClientOwnerActivity.class);
					activity.startActivity(intent);
				}
				else{
					Intent intent = new Intent(activity, ErrorOccurredActivity.class);
					intent.putExtra(ErrorOccurredActivity.ARG_ERROR_STATUS_EXTRA, status);
					activity.startActivity(intent);
				}
			}

			@Override
			protected void preExecute() {
				email = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.email_edt);
				password = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.password_edt);
			}

		};
		
		task.execute((Void[])null);		
	}

	public static void launch(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		
		context.startActivity(intent);
	}
	
}

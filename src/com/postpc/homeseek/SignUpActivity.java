package com.postpc.homeseek;

import com.parse.ParseException;
import com.postpc.homeseek.core.managers.HSUsersManager;
import com.postpc.homeseek.core.session.Credentials;
import com.postpc.homeseek.core.session.HSSessionManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class SignUpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
	}

	public void onSignUpConfirmClick(View v) {

		SimpleAsyncTask task = new SimpleAsyncTask(this) {


			private String status;
			private String lastName;
			private String firstName;
			private String password;
			private String email;
			private String confirmPassword;
			
			boolean signedUp = false;
			

			@Override
			protected Void doInBackground(Void... arg0) {				
				
				if (firstName == null || lastName == null || email == null || 
					password == null || confirmPassword == null) {
					status = "Fill in all the fields!";
					return null;
				}
				
				if (!password.equals(confirmPassword)){
					status = "Invalid credentials!";
					return null;
				}

				try {
					HSUsersManager.createNewUser(email, password, firstName, lastName);

					HSSessionManager.login(new Credentials(email, password));
					status = "Signed up successfully!";
					signedUp = true;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					status = e.getMessage();

					// TODO handle exception
				}

				return null;
			}

			@Override
			protected void postExecute() {

				if (signedUp){
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
				confirmPassword = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.password_confirm_edt);
				firstName = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.first_name_edt);
				lastName = 	HomeSeekViewUtils.getEditableDataAsString(activity, R.id.last_name_edt);
			}						
		};

		task.execute((Void[])null);
	}
}

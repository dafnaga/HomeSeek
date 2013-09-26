package com.postpc.homeseek;

import com.parse.ParseException;
import com.postpc.homeseek.core.managers.HSUsersManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchUserException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.view.View;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;

public class ChooseContactActivity extends Activity {

	public final static String ARG_RETURN_USER_ID = "ARG_RETURN_USER_ID";
	private static final int REQ_PICK_CONTACT = 1;
	
	private String toEmail;
	private String toUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_contact);
	}
	
	public void onCancelClick(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}
	
	public void onOkClick(View v) {
		
		SimpleAsyncTask task = new SimpleAsyncTask(this) {
			
			private boolean success = true; 
			
			@Override
			protected void preExecute() {
				toEmail = HomeSeekViewUtils.getEditableDataAsString(activity, R.id.email_edt);	
			}
			
			@Override
			protected void postExecute() {
				if (!success) {
					setResult(RESULT_CANCELED);
					Toast.makeText(activity, R.string.error_has_occurred_txt, Toast.LENGTH_LONG).show();
				} else {
					Intent resultIntent = new Intent();
					resultIntent.putExtra(ARG_RETURN_USER_ID, toUser);
					setResult(RESULT_OK, resultIntent );
				}
				finish();

			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					toUser = HSUsersManager.getUserByEmail(toEmail).getId();
							
				} catch (ParseException e) {
					e.printStackTrace();
					success = false;
				} catch (NoSuchUserException e) {
					// TODO Send email?
					e.printStackTrace();
					success = false;
				}
				return null;
			}
		};
		
		task.execute((Void) null);
		
	}
	
	public void onChooseContactClick (View view) {
		// Let the user pick a contact
		Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
		intent.setType(Email.CONTENT_TYPE);				
		startActivityForResult(intent, REQ_PICK_CONTACT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
	    // Check which request we're responding to
	    if (requestCode == REQ_PICK_CONTACT) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	            // The user picked a contact to send
	            // The Intent's data Uri identifies which contact was selected.

	        	SimpleAsyncTask task = new SimpleAsyncTask(this) {

					@Override
					protected void preExecute() {
						// Nothing to do
					}
					
					@Override
					protected void postExecute() {
						HomeSeekViewUtils.setTxt(activity, R.id.email_edt, toEmail); 

					}
					
					@Override
					protected Void doInBackground(Void... arg0) {
						
			            // Get the URI that points to the selected contact
			            Uri contactUri = data.getData();
			            
			            // We only need the ADDRESS column
			            String[] projection = {Email.ADDRESS};

			            // Perform the query on the contact to get the EMAIL column
			            Cursor cursor = getContentResolver()
			                    .query(contactUri, projection, null, null, null);
			            if(!cursor.moveToFirst()){
			            	toEmail = "Enter manually";
			            } else {
			            	// Retrieve the phone number from the NUMBER column
			            	int column = cursor.getColumnIndex(Email.ADDRESS);
			            	toEmail = cursor.getString(column);
			            }
						return null;
					}
				};
				task.execute((Void[])null);
	        } else {
	        	Toast.makeText(this, "Faild to get contact"/*TODO*/, Toast.LENGTH_LONG).show();
	        }
	    }
	}
}

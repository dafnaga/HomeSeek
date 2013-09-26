package com.postpc.homeseek;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSApartmentsManager;
import com.postpc.homeseek.core.session.HSSessionManager;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ClientApartmentInfoActivity extends AbstructApartmentInfoActivity {

	private static final int REQ_PICK_CONTACT = 0;

	private String clientComment;

	public ClientApartmentInfoActivity() {
		super(R.layout.activity_client_apartment_info);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		boolean isApartmentInClientList = false;

		try {
			String userId = HSSessionManager.getActiveSession().getUser().getId();
			isApartmentInClientList = HSApartmentsManager
					.isClientApartmentLinkRecords(userId, apartmentId);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UserNotLoggedInException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		enableRegisterdClientViews(isApartmentInClientList);
	}

	public void onUnsubscribeBtnClick(View v) {
		SimpleAsyncTask task = new SimpleAsyncTask(this) {

			private boolean success = false;

			@Override
			protected void preExecute() {
			}

			@Override
			protected void postExecute() {
				if (!success) {
					LoginActivity.launch(activity);
					return;
				}
				Toast.makeText(getApplicationContext(),
						"The apartment was removed from your list",
						Toast.LENGTH_SHORT).show();
				enableRegisterdClientViews(false);
			}

			@Override
			protected Void doInBackground(Void... arg0) {

				try {
					HSApartmentsManager
					.removeApartmentFromCurrentClient(apartmentId);
					success = true;
				} catch (UserNotLoggedInException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};

		task.execute((Void[]) null);
	}

	public void onSubscribeBtnClick(View v) {

		SimpleAsyncTask task = new SimpleAsyncTask(this) {

			private boolean success = false;

			@Override
			protected void preExecute() {
			}

			@Override
			protected void postExecute() {
				if (!success) {
					LoginActivity.launch(activity);
					return;
				}
				Toast.makeText(getApplicationContext(),
						"The apartment was added to your list",
						Toast.LENGTH_SHORT).show();
				enableRegisterdClientViews(true);
			}

			@Override
			protected Void doInBackground(Void... arg0) {

				try {
					HSUser curClient = HSSessionManager.getActiveSession()
							.getUser();
					HSApartmentsManager.addApartmentToClient(curClient.getId(),
							apartmentId);
					success = true;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UserNotLoggedInException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};

		task.execute((Void[]) null);
	}

	private void enableRegisterdClientViews(boolean isRegisterd) {
		Button addButton = (Button) findViewById(R.id.addButton);
		View registerdBtnsLayout = findViewById(R.id.client_btns_layout);

		if (isRegisterd) {
			addButton.setVisibility(View.GONE);
			registerdBtnsLayout.setVisibility(View.VISIBLE);
		} else {
			addButton.setVisibility(View.VISIBLE);
			registerdBtnsLayout.setVisibility(View.GONE);
		}
		enableView(R.id.client_comments_layout, isRegisterd);
	}

	private void enableView(int id, boolean visible) {
		View view = findViewById(id);
		if (view == null) {
			return;
		}
		if (visible) {
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}

	}

	//	public void onAddPhotoClick(View view) {
	//		String userId;
	//
	//		try {
	//			userId = HSSessionManager.getActiveSession().getUser().getId();
	//		} catch (UserNotLoggedInException e) {
	//			LoginActivity.launchLoginActivity(this);
	//			return;
	//		}
	//
	//		Intent intent = new Intent(this, AddPhotoActivity.class);
	//		intent.putExtra(AddPhotoActivity.ARG_APARTMENT_ID, apartmentId);
	//		intent.putExtra(AddPhotoActivity.ARG_USER_ID, userId);
	//		intent.putExtra(AddPhotoActivity.ARG_VISIBILITY, false);
	//
	//		startActivity(intent);
	//	}

	public void onShowPhotosClick(View view) {
		// TODO: The same function is in OwnerApartmentInfoActivity.
		// move to AbstructApartmentInfoActivity?
		Intent intent = new Intent(this, ApartmentPhotosActivity.class);
		intent.putExtra(ApartmentPhotosActivity.ARG_APARTMENT_ID, apartmentId);

		startActivity(intent);
	}

	public void onChatWithOwnerClick(View view) {
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra(ChatActivity.ARG_WITH_USER_ID, apartment.getOwnerId());

		startActivity(intent);
	}

	public void onCalendarBtnClick(View view) {

		Intent intent = new Intent(this,
				ApartmentCalendarForClientActivity.class);
		intent.putExtra(ApartmentCalendarForClientActivity.ARG_APARTMENT_ID,
				apartment.getId());
		intent.putExtra(ApartmentCalendarForClientActivity.ARG_APARTMENT_TITLE,
				apartment.getTitle());

		startActivity(intent);
	}

	public void onSaveCommentClick(View view) {

		final Activity activity = this;

		clientComment = HomeSeekViewUtils.getTxtDataAsString(this, R.id.apartment_client_comments_txt);
		clientComment = (clientComment == null) ? "" : clientComment;

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Set Comment");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setText(clientComment);
		alert.setView(input);

		alert.setPositiveButton("Set", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				SimpleAsyncTask task = new SimpleAsyncTask(activity) {
					private boolean success = true;
					private String clientId;

					@Override
					protected void preExecute() {
						clientComment = input.getText().toString();
					}

					@Override
					protected void postExecute() {
						if (!success) {
							Toast.makeText(activity, "Failed to save comment", Toast.LENGTH_LONG)
							.show();
							return;
						}
						HomeSeekViewUtils.setTxt(activity, R.id.apartment_client_comments_txt, clientComment);

					}

					@Override
					protected Void doInBackground(Void... arg0) {
						try {
							clientId = HSSessionManager.getActiveSession().getUser().getId();
							HSApartmentsManager.saveClientComment(clientId, apartmentId, clientComment);
						} catch (ParseException e) {
							success = false;
						} catch (UserNotLoggedInException e) {
							success = false;
						}
						return null;
					}
				};

				task.execute((Void) null);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();


	}

	public void onNavigateClick(View view){
		String uriTxt = "google.navigation:q=" + apartment.getAddress().replace(' ','+');
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriTxt));
		startActivity(intent);
	}

	public void onShareBtnClick(View v) {
		Intent intent = new Intent(this, ChooseContactActivity.class);
		startActivityForResult(intent, REQ_PICK_CONTACT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		// Check which request we're responding to
		if (requestCode == REQ_PICK_CONTACT) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {

				SimpleAsyncTask task = new SimpleAsyncTask(this) {

					private String toUser;
					private boolean success = false;

					@Override
					protected void preExecute() {
						toUser = data.getStringExtra(ChooseContactActivity.ARG_RETURN_USER_ID);
					}

					@Override
					protected void postExecute() {
						if (success) {
							Toast.makeText(activity, "The apartment was sent successfully"/*TODO*/, Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(activity, R.string.faild_to_share_apartment_str, Toast.LENGTH_LONG).show();
						}

					}

					@Override
					protected Void doInBackground(Void... arg0) {

						try {
							HSUser fromUser = HSSessionManager.getActiveSession().getUser();
							HSApartmentsManager.SendApartmentToUser(apartment, fromUser, toUser);
							success = true;
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UserNotLoggedInException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						return null;
					}
				};
				task.execute((Void[])null);
			}
		}
	}
}

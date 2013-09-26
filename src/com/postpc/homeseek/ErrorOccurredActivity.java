package com.postpc.homeseek;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class ErrorOccurredActivity extends Activity {

	public static final String ARG_ERROR_STATUS_EXTRA = "Error Occurred";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error_occurred);
		Intent intent = getIntent();
		String errorTxt = intent.getStringExtra(ARG_ERROR_STATUS_EXTRA);
		TextView errorOccurred = (TextView) findViewById(R.id.error_occurred);

		errorOccurred.setText(errorTxt);

	}

	public void onOkClick(View v) {
		finish();
	}

	public static void showConnectivityError(
			Context context) {
		show(context, "Error connecting to server.");
	}

	public static void show(Context context, String error) {
		Intent intent = new Intent(context, ErrorOccurredActivity.class);
		intent.putExtra(ARG_ERROR_STATUS_EXTRA, error);
		context.startActivity(intent);		
	}


}

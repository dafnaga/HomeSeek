package com.postpc.homeseek;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.HSMeetup;
import com.postpc.homeseek.core.managers.HSApartmentsManager;
import com.postpc.homeseek.core.managers.HSMeetupsManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchApartmentException;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddMeetUpActivity extends Activity {

	private static final String DATE_FORMAT = "dd/MM/yyyy";
	private static final String HOUR_FORMAT = "HH:mm";
	private static final String FULL_DATE_FORMAT = DATE_FORMAT + " " + HOUR_FORMAT;
	
	public static final String ARG_APARTMENT_ID = "APARTMENT_ID";
	
	private String apartmentId;
	private HSMeetup meetup;
	
	private Button pickDate;
	private Button startPickHour;
	private Button endPickHour;
	private Calendar startCalendarDate;
	private Calendar endCalendarDate;
	
	private Date startDate;
	private Date endDate;

	private static final int TYPE_DATE = 0;
	private static final int TYPE_HOUR = 1;

	private Button activeDateBtnDisplay;
	private Calendar activeDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_meetup);
		Intent intent = getIntent();
		apartmentId = intent.getStringExtra(ARG_APARTMENT_ID);
		
		/*  capture our View elements for the start date function   */
		pickDate = (Button) findViewById(R.id.date_btn);
		startPickHour = (Button) findViewById(R.id.start_hour_btn);

		/* get the current date */
		startCalendarDate = Calendar.getInstance();

		/* add a click listener to the button   */
		pickDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDateDialog(pickDate, startCalendarDate);
			}
		});

		/* add a click listener to the button   */
		startPickHour.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showHourDialog(startPickHour, startCalendarDate);

			}
		});

		/* capture our View elements for the end date function */
		endPickHour = (Button) findViewById(R.id.end_hour_btn);

		/* get the current date */
		endCalendarDate = Calendar.getInstance();

		/* add a click listener to the button   */
		endPickHour.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showHourDialog(endPickHour, endCalendarDate);

			}
		});

		/* display the current date (this method is below)  */
		updateDisplay(pickDate, startCalendarDate, TYPE_DATE);
		updateDisplay(startPickHour, startCalendarDate, TYPE_HOUR);
		updateDisplay(endPickHour, endCalendarDate, TYPE_HOUR);
	}
	private void updateDisplay(Button dateDisplay, Calendar date, int type) {
		
		String strdate = null;

		SimpleDateFormat sdf = null;
		switch (type) {
		case TYPE_DATE:
			sdf = new SimpleDateFormat(DATE_FORMAT);
			break;
		case TYPE_HOUR:
			sdf = new SimpleDateFormat(HOUR_FORMAT);
			break;
		default:
			break;
		}
		if (date != null && sdf != null) {
			strdate = sdf.format(date.getTime());
			dateDisplay.setText(strdate);
		}
	}

	public void showDateDialog(Button dateBtn, Calendar date) {
		activeDateBtnDisplay = dateBtn;
		activeDate = date;
		showDialog(TYPE_DATE);
	}

	private void showHourDialog(Button hourBtn, Calendar date) {
		activeDateBtnDisplay = hourBtn;
		activeDate = date;
		showDialog(TYPE_HOUR);

	}

	private OnDateSetListener dateSetListener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			activeDate.set(Calendar.YEAR, year);
			activeDate.set(Calendar.MONTH, monthOfYear);
			activeDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateDisplay(activeDateBtnDisplay, activeDate, TYPE_DATE);
			unregisterDateDisplay();
		}
	};

	private OnTimeSetListener timeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hour, int min) {
			activeDate.set(Calendar.HOUR_OF_DAY, hour);
			activeDate.set(Calendar.MINUTE, min);
			updateDisplay(activeDateBtnDisplay, activeDate, TYPE_HOUR);
			unregisterDateDisplay();
			
		}	
	};
	
	private void unregisterDateDisplay() {
		activeDateBtnDisplay = null;
		activeDate = null;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TYPE_DATE:
			return new DatePickerDialog(this, dateSetListener, activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
		case TYPE_HOUR:
			return new TimePickerDialog(this, timeSetListener, activeDate.get(Calendar.HOUR_OF_DAY), activeDate.get(Calendar.MINUTE), true);
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case TYPE_DATE:
			((DatePickerDialog) dialog).updateDate(activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
			break;
		case TYPE_HOUR:
			((TimePickerDialog) dialog).updateTime(activeDate.get(Calendar.HOUR_OF_DAY), activeDate.get(Calendar.MINUTE));
			break;
		}
	}


	public void onSaveClick(View v) throws ParseException {
		SimpleAsyncTask task = new SimpleAsyncTask(this) {

			private boolean success = false;

			@Override
			protected void preExecute() {
				
				// Set start and end date from display
				String dateStr = (String) ((Button)findViewById(R.id.date_btn)).getText();
				String startHourStr = (String) ((Button)findViewById(R.id.start_hour_btn)).getText();
				String endtHourStr = (String) ((Button)findViewById(R.id.end_hour_btn)).getText();
				
				startDate = getDateFromString(dateStr, startHourStr);
				endDate = getDateFromString(dateStr, endtHourStr);
			}

			@Override
			protected void postExecute() {
				if (!success){
					Toast toast = Toast.makeText(activity, "Error creating meetup", Toast.LENGTH_SHORT);
					setResult(RESULT_CANCELED);
				}else {
					Intent resultIntent = new Intent();
					resultIntent.putExtra(ApartmentCalendarActivity.ARG_MEETUP_ID, meetup.getId());
					setResult(RESULT_OK,resultIntent);
				}
				finish();
			}

			@Override
			protected Void doInBackground(Void... arg0) {	
				try {
					if(startDate.getTime() > endDate.getTime()){
						success = false;
					} else {
						HSApartment forApartment = HSApartmentsManager.getApartmentById(apartmentId);
						meetup = HSMeetupsManager.createMeetup(forApartment, startDate, endDate);
						success = true;	
					}
				} catch (ParseException e) {
					e.printStackTrace();
					success = false;
				} catch (NoSuchApartmentException e) {
					e.printStackTrace();
					success = false;
				}				
				return null;
			}
		};

		task.execute((Void[])null);
	}

	protected Date getDateFromString(String dateStr, String hourStr) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(FULL_DATE_FORMAT);
		try {
			cal.setTime(sdf.parse(dateStr + " " + hourStr));
		} catch (java.text.ParseException e) {
			//should not get here
			assert(false);
		}
		return cal.getTime();
		
	}
	
	public void onCancelClick(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}
}

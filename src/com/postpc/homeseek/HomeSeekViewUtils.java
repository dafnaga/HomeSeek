
package com.postpc.homeseek;
import com.parse.ParseException;
import com.postpc.homeseek.R;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.hsobjects.HSApartmentPhoto;
import com.postpc.homeseek.core.hsobjects.HSMeetup;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSApartmentsManager;
import com.postpc.homeseek.core.session.HSSessionManager;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class HomeSeekViewUtils {
	
	private final static int NO_IMAGE = -1;
	
	public static void fillViewWithApartmentInfo(View view, HSApartment apartment, int defaultImageId){
		setNumberTxt(view, R.id.apartment_area_txt, apartment.getAreaSize());
		setTxt(view, R.id.activity_title_txt, apartment.getTitle());
		setTxt(view, R.id.apartment_title_txt, apartment.getTitle());
		setTxt(view, R.id.apartment_city_txt, apartment.getCity());
		setTxt(view, R.id.apartment_building_number_txt, apartment.getBuildingNumber());
		setTxt(view, R.id.apartment_ap_number_txt, apartment.getApartmentNumber());
		setNumberTxt(view, R.id.apartment_price_txt, apartment.getPrice());
		setDecimalNumberTxt(view, R.id.apartment_rooms_txt, apartment.getRooms());
		setTxt(view, R.id.apartment_street_txt, apartment.getStreet());
		setTxt(view, R.id.apartment_owner_comments_txt, apartment.getOwnerComments());
		setTxt(view, R.id.apartment_neighborhood_txt, apartment.getNeighborhood());
		
		setPriceDescription(view, apartment.getPrice());
		setRoomsDescription(view, apartment.getRooms());
		
		setClientComment(view, R.id.apartment_client_comments_txt, apartment);
		
		// Get the first image in the list and show it
		if (defaultImageId != NO_IMAGE) {
			setImage(view, R.id.apartment_photo_img, apartment, defaultImageId);
		}
	}
	
	public static void fillViewWithApartmentInfo(View view, HSApartment apartment){
		fillViewWithApartmentInfo(view, apartment, NO_IMAGE);	
	}
	
	private static void setPriceDescription(View view, Integer price) {
		TextView txtView = (TextView)view.findViewById(R.id.apartment_price_description);
		if (txtView != null){
			if (price != null){
				txtView.setText(price.toString() + " NIS");
			} else {
				txtView.setText(HomeseekApplication.getContext().getResources().getString(R.string.price_txt) +   
								HomeseekApplication.getContext().getResources().getString(R.string.unspecified));
			}
		}
	}
	
	private static void setRoomsDescription(View view, Double rooms) {
		TextView txtView = (TextView)view.findViewById(R.id.apartment_rooms_description);
		if (txtView != null){
			if (rooms != null){
				txtView.setText(rooms.toString() + " " + HomeseekApplication.getContext().getResources().getString(R.string.rooms_str));
			} else {
				txtView.setText(HomeseekApplication.getContext().getResources().getString(R.string.rooms_num_txt) +  
								HomeseekApplication.getContext().getResources().getString(R.string.unspecified));
			}
		}
	}

	public static void fillViewWithMeetUpInfo(View view, HSMeetup visit) {
		setTxt(view, R.id.meetup_date_txt, visit.getDateAsString());
		setNumberTxt(view, R.id.meetup_visitors_txt, visit.getNumOfVisitors());
		setTxt(view, R.id.meetup_hours_txt, visit.getHoursAsString());
	}
	
	private	static void setTxt(View view, int id, String txt){
		TextView txtView = (TextView)view.findViewById(id);
		if (txtView != null){
			if (txt != null){
				txtView.setText(txt);
			}else{
				txtView.setText("");
			}
		}
	}
	
	private	static void setDecimalNumberTxt(View view, int id, Double txt){
		TextView txtView = (TextView)view.findViewById(id);
		if (txtView != null){
			if (txt != null){
				txtView.setText(txt.toString());
			}else {
				txtView.setText("");
			}
			
		}
	}
	
	private	static void setNumberTxt(View view, int id, Integer txt){
		TextView txtView = (TextView)view.findViewById(id);
		if (txtView != null){
			if (txt != null){
				txtView.setText(txt.toString());	
			}else {
				txtView.setText("");
			}
			
		}
	}
	
	private	static void setImage(View view, int id, final HSApartment apartment, final int defaultImageId){
		final ImageView imgView = (ImageView)view.findViewById(id);
		final ProgressBar pb = (ProgressBar)view.findViewById(R.id.apartment_photo_img_progress_bar);
		
		if (imgView == null){
			return;
		}
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void,Void,Void>() {
			HSApartmentPhoto photo;			
			byte[] imageData;
			boolean success;
			
			@Override
			protected void onPreExecute() {
				// Show the progress bar, hide the photo
				System.out.println("Getting image for view " + imgView.getId() + " apartment " + apartment.getId());
				imgView.setTag(apartment);
				if (pb != null) {					
					imgView.setVisibility(View.GONE);
					pb.setVisibility(View.VISIBLE);
					pb.animate();
				}
			}
			
			@Override
			protected void onPostExecute(Void result) {
				HSApartment curApr = (HSApartment)imgView.getTag();
				if (!curApr.getId().equals(apartment.getId())){
					// View has changed
					return;
				}
				
				if (pb != null) {
					imgView.setVisibility(View.VISIBLE);
					pb.setVisibility(View.GONE);
				}
				
				if (!success){
					imgView.setImageResource(defaultImageId);
					return;
				}
				
				Bitmap imgBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
				imgView.setImageBitmap(imgBitmap);
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
				success = false;
				
				try {
					photo = HSApartmentsManager.getApartmentMainPhoto(apartment);
					if (photo == null){
						success = false;
						return null;
					}
					imageData = photo.getPhotoData();
					success = true;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					success = false;
				}
				return null;
			}
		};
		
		task.execute((Void[])null);
	}
	
	
	private static void setClientComment(final View view, final int id, final HSApartment apartment) {
		final TextView txtView = (TextView) view.findViewById(id);
		
		if (txtView == null){
			return;
		}
		
		SimpleAsyncTask task = new SimpleAsyncTask((Activity)view.getContext()) {
			
			String comment;
			boolean success = true;
			
			@Override
			protected void preExecute() {
			}
			
			@Override
			protected void postExecute() {
				if (success){
					setTxt(view, id, comment);
				}
				
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					HSUser curUser = HSSessionManager.getActiveSession().getUser();
					comment = HSApartmentsManager.getApartmentClientComments(apartment, curUser);
				} catch (UserNotLoggedInException e) {
					success = false;
				} catch (ParseException e) {
					success = false;
				}
				return null;
			}
		};
		
		task.execute((Void) null);
	}
	
	public static String getRadioDataAsString(Activity activity, int id) {
		RadioGroup typeGroup = (RadioGroup)activity.findViewById(id);
		RadioButton checkedBtn = (RadioButton)activity.findViewById(typeGroup.getCheckedRadioButtonId());
		return checkedBtn.getText().toString();
	}
	
	public static String getEditableDataAsString(Activity activity, int id){
		Editable val = ((EditText)activity.findViewById(id)).getText();
		if(val != null && val.length() > 0){
			return val.toString();
		}
		return null;
	}
	
	public static Integer getEditableDataAsInteger(Activity activity, int id) {
		Editable val = ((EditText) activity.findViewById(id)).getText();
		if(val != null && val.length() > 0){
			return Integer.parseInt(val.toString());
		}
		return null;
	}
	
	public static Double getEditableDataAsDouble(Activity activity, int id){
		Editable val = ((EditText)activity.findViewById(id)).getText();
		if(val != null && val.length() > 0){
			return Double.parseDouble(val.toString());
		}
		return null;
	}
	
	public static String getTxtDataAsString(Activity activity, int id) {
		TextView txtView = ((TextView)activity.findViewById(id));
		if (txtView != null){
			return txtView.getText().toString();
		}
		return null;
	}
	
	public static Integer getTxtDataAsInteger(Activity activity, int id) {
		TextView txtView = ((TextView)activity.findViewById(id));
		if (txtView != null){
			String txt = txtView.getText().toString();
			if (txt.length() != 0){
				return Integer.parseInt(txt);
			}
		}
		return null;
	}
	
	public static Double getTxtDataAsDouble(Activity activity, int id) {
		TextView txtView = ((TextView)activity.findViewById(id));
		if (txtView != null){
			String txt = txtView.getText().toString();
			if (txt.length() != 0){
				return Double.parseDouble(txt);
			}
		}
		return null;
	}
	
	
	//TODO - the next 3 functions are the same as the private functions with the View argument. handle this! 
	public	static void setTxt(Activity activity, int id, String txt){
		TextView txtView = (TextView)activity.findViewById(id);
		if (txtView != null && txt != null){
			txtView.setText(txt);
		}
	}
	
	public	static void setDecimalNumberTxt(Activity activity, int id, Double txt){
		TextView txtView = (TextView)activity.findViewById(id);
		if (txtView != null && txt != null){
			txtView.setText(txt.toString());
		}
	}
	
	public	static void setNumberTxt(Activity activity, int id, Integer txt){
		TextView txtView = (TextView)activity.findViewById(id);
		if (txtView != null && txt != null){
			txtView.setText(txt.toString());
		}
	}

	public static void setExternalLayout(Activity activity, int viewId, int layoutXmlId) {
		ViewGroup layout = (ViewGroup) activity.findViewById(viewId);
		if (layout == null) {
			return;
		}
		layout.setVisibility(View.VISIBLE);

		if (layout.getChildCount() > 0) {
			return;
		}

		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View childLayout = inflater.inflate(layoutXmlId, null);
		layout.addView(childLayout);
		
	}
	
}
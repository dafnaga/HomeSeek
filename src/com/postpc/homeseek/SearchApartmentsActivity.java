package com.postpc.homeseek;

import java.io.Serializable;
import java.util.List;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSClientQuery;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSApartmentQuery;
import com.postpc.homeseek.core.managers.HSClientQueryManager;
import com.postpc.homeseek.core.session.HSSessionManager;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SearchApartmentsActivity extends HSActivity {

	public static final String ARG_SEARCH_TYPE = "arg_search_type";

	private static final int REQ_SEARCH_AREA = 1;
	public static final int RES_SUCCESS = 0;
	private MapCircle searchArea;

	public static final int SEARCH_MODE_MAP = 1;
	public static final int SEARCH_MODE_ADDRESS = 2;

	private int searchMode;	

	protected void setSearchMode(int searchMode){
		this.searchMode = searchMode;
		View show, hide;
		View showBtn, hideBtn;
		View showSelBtn, hideSelBtn;
		
		if (searchMode == SEARCH_MODE_MAP){
			hide = findViewById(R.id.address_fields_wrap);
			show = findViewById(R.id.map_fields_wrap);
			showBtn = findViewById(R.id.top_ruler_search_icon);
			hideBtn = findViewById(R.id.top_ruler_map_icon);
			showSelBtn = findViewById(R.id.switch_to_address_btn);
			hideSelBtn = findViewById(R.id.switch_to_map_btn);
		} else {
			show = findViewById(R.id.address_fields_wrap);
			hide = findViewById(R.id.map_fields_wrap);
			hideBtn = findViewById(R.id.top_ruler_search_icon);
			showBtn = findViewById(R.id.top_ruler_map_icon);
			hideSelBtn = findViewById(R.id.switch_to_address_btn);
			showSelBtn = findViewById(R.id.switch_to_map_btn);			
		}

		show.setVisibility(View.VISIBLE);
		hide.setVisibility(View.GONE);
		showBtn.setVisibility(View.VISIBLE);
		hideBtn.setVisibility(View.GONE);
		hideSelBtn.setVisibility(View.GONE);
		showSelBtn.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_apartments);

		// Determine which search type is required (map or address)
		int searchMode = getIntent().getIntExtra(ARG_SEARCH_TYPE, SEARCH_MODE_ADDRESS);
		setSearchMode(searchMode);

		// If search mode is map search, get the map search area from another activity
		if (searchMode == SEARCH_MODE_MAP){
			Intent intent = new Intent(this, PickApartmentSearchAreaActivity.class);
			startActivityForResult(intent, REQ_SEARCH_AREA);
		}

	}

	public void onSetSearchAreaClick(View view){
		Intent intent = new Intent(this, PickApartmentSearchAreaActivity.class);
		intent.putExtra(PickApartmentSearchAreaActivity.ARG_INIT_AREA, (Serializable)searchArea);
		startActivityForResult(intent, REQ_SEARCH_AREA);		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK){			
			// TODO alert the user
			return;
		}

		if (requestCode == REQ_SEARCH_AREA){
			searchArea = (MapCircle)data.getSerializableExtra(PickApartmentSearchAreaActivity.RES_SEACH_AREA);
			// TODO alert the user
			if (searchArea != null){
				Toast toast = Toast.makeText(this, "Search area set successfuly", Toast.LENGTH_SHORT);
				toast.show();
			} else {
				// TODO notify user
			}

			// Update the layout
			setSearchMode(SEARCH_MODE_MAP);
			return;
		}
	}

	/**
	 * The function handles click on the "search" button.
	 * @param v
	 * @throws ParseException
	 */
	public void onSearchClick(View v) {
		Intent intent = new Intent(this, SearchResultsActivity.class);

		HSApartmentQuery query = prepareQuery();

		intent.putExtra(SearchResultsActivity.ARG_SEARCH_QUERY, (Serializable)query);

		startActivity(intent);
	}

	public void onRecentConversationsClick(View v) {
		Intent intent = new Intent(this, RecentConversationsActivity.class);
		intent.putExtra(RecentConversationsActivity.ARG_USER_TYPE, RecentConversationsActivity.MODE_CLIENT);
		startActivity(intent);
	}

	protected HSApartmentQuery prepareQuery(){
		HSApartmentQuery query = new HSApartmentQuery();
		query.setApartmentType(HomeSeekViewUtils.getRadioDataAsString(this, R.id.type_radio_group));
		query.setMaxRooms(HomeSeekViewUtils.getEditableDataAsDouble(this, R.id.max_rooms_edt));
		query.setMinRooms(HomeSeekViewUtils.getEditableDataAsDouble(this, R.id.min_rooms_edt));
		query.setMinPrice(HomeSeekViewUtils.getEditableDataAsInteger(this, R.id.min_price_edt));
		query.setMaxPrice(HomeSeekViewUtils.getEditableDataAsInteger(this, R.id.max_price_edt));
		query.setMinArea(HomeSeekViewUtils.getEditableDataAsInteger(this, R.id.min_area_edt));
		query.setMaxArea(HomeSeekViewUtils.getEditableDataAsInteger(this, R.id.max_area_edt));
		if (searchMode == SEARCH_MODE_MAP){
			query.setSearchArea(searchArea);
		} else {
			query.setCity(HomeSeekViewUtils.getEditableDataAsString(this, R.id.city_edt));
			query.setNeighborhood(HomeSeekViewUtils.getEditableDataAsString(this, R.id.neighborhood_edt));
			query.setStreet(HomeSeekViewUtils.getEditableDataAsString(this, R.id.street_edt));			
		}

		return query;
	}

	public void onMapSearchBtnClick(View v) {
		setSearchMode(SEARCH_MODE_MAP);
		onSetSearchAreaClick(v);
	}

	public void onSearchApartmentsBtnClick(View v) {
		setSearchMode(SEARCH_MODE_ADDRESS);
	}

	/**
	 * The function handles click on the "save my search" button.
	 * @param v
	 * @throws ParseException
	 */
	public void onSaveSearchClick(final View v) throws ParseException {

		SimpleAsyncTask task = new SimpleAsyncTask(this) {

			private HSApartmentQuery query;
			private boolean success = true;

			@Override
			protected void preExecute() {
				query = prepareQuery();
			}

			@Override
			protected void postExecute() {
				if(success){
					Toast.makeText(getApplicationContext(), R.string.success_message_search_saved, Toast.LENGTH_SHORT).show();
					onSearchClick(v);
				} else {
					Toast.makeText(getApplicationContext(), R.string.error_message_search_saving_failed, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			protected Void doInBackground(Void... arg0) {

				HSUser client;
				try {
					client = HSSessionManager.getActiveSession().getUser();
					HSClientQueryManager.setClientQuery(client, query);
				} catch (UserNotLoggedInException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					success = false;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					success = false;
				}
				return null;
			}
		};

		task.execute((Void)null);
	}

	/**
	 * The function handle click on the "load my search" button.
	 * If the user has no saved search, no search will be loaded and relevant message will be displayed.
	 * @param v
	 * @throws ParseException
	 */
	public void onLoadSearchClick(final View v) throws ParseException {

		SimpleAsyncTask task = new SimpleAsyncTask(this) {

			private List<HSClientQuery> queries;
			private HSClientQuery query;
			private boolean hasQuery = false;
			private HSUser client;

			@Override
			protected void preExecute() {
				try {
					client = HSSessionManager.getActiveSession().getUser();
				} catch (UserNotLoggedInException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			protected void postExecute() {
				/**
				 * If a search query was found, fill the search fields by it's values.
				 */
				if (hasQuery){
					if (query.getCity() != null){
						HomeSeekViewUtils.setTxt(activity, R.id.city_edt, query.getCity());
					}
					if (query.getMaxArea() != null){
						HomeSeekViewUtils.setNumberTxt(activity, R.id.max_area_edt, query.getMaxArea());
					}
					if (query.getMinArea() != null){
						HomeSeekViewUtils.setNumberTxt(activity, R.id.min_area_edt, query.getMinArea());
					}
					if (query.getMaxPrice() != null){
						HomeSeekViewUtils.setNumberTxt(activity, R.id.max_price_edt, query.getMaxPrice());
					}
					if (query.getMinPrice() != null){
						HomeSeekViewUtils.setNumberTxt(activity, R.id.min_price_edt, query.getMinPrice());
					}
					if (query.getMaxRooms() != null){
						HomeSeekViewUtils.setDecimalNumberTxt(activity, R.id.max_rooms_edt, query.getMaxRooms());
					}
					if (query.getMinRooms() != null){
						HomeSeekViewUtils.setDecimalNumberTxt(activity, R.id.min_rooms_edt, query.getMinRooms());
					}
					if (query.getStreet() != null){
						HomeSeekViewUtils.setTxt(activity, R.id.street_edt, query.getStreet());
					}
					if (query.getNeiborhood() != null){
						HomeSeekViewUtils.setTxt(activity, R.id.neighborhood_edt, query.getNeiborhood());
					}

					searchArea = query.getSearchArea();
					if (query.getSearchArea() != null){
						setSearchMode(SEARCH_MODE_MAP);
					} else {
						setSearchMode(SEARCH_MODE_ADDRESS);
					}
				} else {
					Toast.makeText(getApplicationContext(), R.string.error_message_no_search_saved_yet, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					queries = HSClientQueryManager.getClientQueries(client);
					if (queries.size() > 0){
						query = queries.get(0);
						hasQuery = true;
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;

			}
		};

		task.execute((Void)null);
	}

}

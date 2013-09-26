package com.postpc.homeseek;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.parse.ParseException;
import com.postpc.homeseek.core.config.db.HSApartmentDBConfig;
import com.postpc.homeseek.core.hsobjects.HSApartment;
import com.postpc.homeseek.core.managers.HSApartmentQuery;
import com.postpc.homeseek.core.managers.HSApartmentsManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

public class SearchResultsActivity extends AbstractFindApartemtsActivity {
	public static final String ARG_SEARCH_QUERY = "arg_search_query";	
	private Intent intent;
	private HSApartmentQuery searchQuery;

	public SearchResultsActivity() {
		super(R.layout.activity_list_view, R.id.activity_list,
				ClientApartmentInfoActivity.class);
	}

	protected void setPreTaskExecute() {
		intent = getIntent();
		
		// Set title
		HomeSeekViewUtils.setTxt(activity, R.id.activity_title_txt, activity.getResources().getString(R.string.search_results_str));

		// Disable subtitle 
		activity.findViewById(R.id.activity_subtitle_txt).setVisibility(View.INVISIBLE);

		// enable add apartment button
		HomeSeekViewUtils.setExternalLayout(activity, R.id.btn_layout, R.layout.search_results_btns);
	}

	protected List<HSApartment> findApartments() throws ParseException {
		searchQuery = (HSApartmentQuery)intent.getSerializableExtra(ARG_SEARCH_QUERY);
		return HSApartmentsManager.findApartments(searchQuery);
	}

	@Override
	protected ApartmentListAdapter generateApartmentsListAdapter(
			HSApartment[] hsApartments) {
		return new ApartmentListAdapter(this, R.layout.apartment_list_row, hsApartments);
	}
	
	
	public void onShowOnMapClick(View v){
		Intent intent = new Intent(this, MapSearchResultsActivity.class);
		
		intent.putExtra(MapSearchResultsActivity.ARG_SEARCH_QUERY, (Serializable)searchQuery);
		
		startActivity(intent);
	}
	
	/**
	 * The function handles a click on "sort my results" button.
	 * It opens a dialog, for choosing the column to sort the results by.
	 * @param v
	 */
	public void onSortClick(View v){
		final Dialog dialog = createDialog();
		dialog.show();	
	}
	
	public Dialog createDialog() {
		final List<CharSequence> columns = new ArrayList<CharSequence>();
		columns.add((CharSequence)HSApartmentDBConfig.ApartmentTable.Fields.CITY);
		columns.add((CharSequence)HSApartmentDBConfig.ApartmentTable.Fields.NEIGHBORHOOD);
		columns.add((CharSequence)HSApartmentDBConfig.ApartmentTable.Fields.STREET);
		columns.add((CharSequence)HSApartmentDBConfig.ApartmentTable.Fields.PRICE);
		columns.add((CharSequence)HSApartmentDBConfig.ApartmentTable.Fields.AREA);
		columns.add((CharSequence)HSApartmentDBConfig.ApartmentTable.Fields.ROOMS);
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.pick_sort_column);
	    builder.setItems(columns.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
	    	
	    	public void onClick(DialogInterface dialog, int which) { // The 'which' argument is the index of the selected item
	    		HSApartment[] sortedApartments = sortApartments(adapter.getApartments(), columns.get(which));
	    		adapter.setApartments(sortedApartments);
	        }

	    	/**
	    	 * The function gets array of apartments and the field chosen for sort, and sorts the array by it.
	    	 * @param apartments
	    	 * @param field
	    	 * @return
	    	 */
			private HSApartment[] sortApartments(HSApartment[] apartments, final CharSequence field) {
				Arrays.sort(apartments, new Comparator<HSApartment>() {

					@Override
					public int compare(HSApartment apartment1, HSApartment apartment2) {
						if (HSApartmentDBConfig.ApartmentTable.Fields.CITY.equals(field)){
							return compareValues(apartment1.getCity(),apartment2.getCity());
						}
						if (HSApartmentDBConfig.ApartmentTable.Fields.STREET.equals(field)){
							return compareValues(apartment1.getStreet(),apartment2.getStreet());
						}
						if (HSApartmentDBConfig.ApartmentTable.Fields.NEIGHBORHOOD.equals(field)){
							return compareValues(apartment1.getNeighborhood(),apartment2.getNeighborhood());
						}
						if (HSApartmentDBConfig.ApartmentTable.Fields.PRICE.equals(field)){
							return compareValues(apartment1.getPrice(),apartment2.getPrice());
						}
						if (HSApartmentDBConfig.ApartmentTable.Fields.AREA.equals(field)){
							return compareValues(apartment1.getAreaSize(),apartment2.getAreaSize());
						}
						if (HSApartmentDBConfig.ApartmentTable.Fields.ROOMS.equals(field)){
							return compareValues(apartment1.getRooms(),apartment2.getRooms());
						}
						return 0;
					}

					private int compareValues(String value1, String value2) {
						if (value1 == null || value2 == null){
							return value1 == null ? 1 : -1; 
						}
						return value1.compareTo(value2);
					}
					private int compareValues(Integer value1, Integer value2) {
						if (value1 == null || value2 == null){
							return value1 == null ? 1 : -1; 
						}
						return value1.compareTo(value2);
					}
					private int compareValues(Double value1, Double value2) {
						if (value1 == null || value2 == null){
							return value1 == null ? 1 : -1; 
						}
						return value1.compareTo(value2);
					}});
				return apartments;
			}
	    });
	    return builder.create();
	}
	
	@Override
	public void onRecentConversationsClick(View v) {
		// Do nothing
	}
}

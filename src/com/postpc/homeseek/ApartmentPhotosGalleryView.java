package com.postpc.homeseek;
import java.util.List;
import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSApartmentPhoto;
import com.postpc.homeseek.core.managers.HSApartmentsManager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

public abstract class ApartmentPhotosGalleryView extends FrameLayout implements OnClickListener {
	
	protected ViewPager pager;
	protected GalleryPagerAdapter pagerAdapter;
	private int galleryLayoutRes;
	private int galleryImageLayoutRes;
	protected OnClickListener thisViewAsListener = this;
	protected GalleryViewActionHandler actionHandler = null;

	public ApartmentPhotosGalleryView(Context context, AttributeSet attrs, int defStyle, List<HSApartmentPhoto> photos, int galleryLayoutRes, int galleryImageLayoutRes) {
        super(context, attrs, defStyle);
        initView(context, photos, galleryLayoutRes, galleryImageLayoutRes);
    }

    public ApartmentPhotosGalleryView(Context context, AttributeSet attrs, List<HSApartmentPhoto> photos, int galleryLayoutRes, int galleryImageLayoutRes) {
        super(context, attrs);
        initView(context, photos, galleryLayoutRes, galleryImageLayoutRes);
   }

    public ApartmentPhotosGalleryView(Context context, List<HSApartmentPhoto> photos, int galleryLayoutRes, int galleryImageLayoutRes) {
        super(context);
        initView(context, photos, galleryLayoutRes, galleryImageLayoutRes);
    }

    private void initView(final Context context, final List<HSApartmentPhoto> photos, final int galleryLayoutRes, final int galleryImageLayoutRes) {
    	this.galleryLayoutRes = galleryLayoutRes;
		this.galleryImageLayoutRes = galleryImageLayoutRes;
		pager =  new ViewPager(context);

    	pagerAdapter = new GalleryPagerAdapter(photos);				
		pager.setAdapter(pagerAdapter);
		pager.setOnClickListener(this);
    	    	
    	addView(pager);    	    
    }
    
    private class GalleryPagerAdapter extends PagerAdapter {
    	
    	private final List<HSApartmentPhoto> photos;

		public GalleryPagerAdapter(List<HSApartmentPhoto> photos){
			super();
			this.photos = photos;
    	}
    	
		@Override
		public boolean isViewFromObject(View view, Object object) {						
			return view == object;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return photos.size();
		}
		
		@Override
		public Object instantiateItem (final ViewGroup container, final int position){
        	final View imgWrapView = ((Activity)container.getContext()).getLayoutInflater().inflate(galleryImageLayoutRes, null, false);
        	
	    	AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
				
				private byte[] imgData = null;
				private ImageView img;
				private ProgressBar pb;

				@Override
				protected void onPreExecute() {
		        	img = (ImageView)imgWrapView.findViewById(R.id.img);
		        	pb = (ProgressBar)imgWrapView.findViewById(R.id.img_progress_bar);
		        	
		        	img.setVisibility(View.GONE);
		        	pb.setVisibility(View.VISIBLE);
		        	pb.animate();
				}
				
				@Override
				protected void onPostExecute(Void result) {
					if (imgData != null){
			        	img.setTag(photos.get(position));
			        	img.setOnClickListener(thisViewAsListener);
			        			        	
						Bitmap bmp = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
						img.setImageBitmap(bmp);						
					} else {
						img.setImageResource(R.drawable.photo_deleted_image);
					}
					
		        	pb.setVisibility(View.GONE);
		        	img.setVisibility(View.VISIBLE);					
				}
				
				@Override
				protected Void doInBackground(Void... arg0) {
					try {
						imgData = photos.get(position).getPhotoData();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			        return null;
				}
			};
	    	
			task.execute((Void[])null);
			
			container.addView(imgWrapView);
			return imgWrapView;
		}
		
		@Override
		public void destroyItem (ViewGroup container, int position, Object object){
			View view = (View)object;
			container.removeView(view);	
		}
		
		public void removePhoto(HSApartmentPhoto photo){
			photos.remove(photo);
		}
		
		public HSApartmentPhoto getPhotoAt(int index){
			if (photos.size() <= index){
				return null;
			}
			
			return photos.get(index);
		}
    }
    
	protected void deletePhoto(final HSApartmentPhoto photo) {
		SimpleAsyncTask task = new SimpleAsyncTask((Activity)getContext()) {
			
			boolean success = false;
			
			@Override
			protected void preExecute() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			protected void postExecute() {
				// If deleted, update the view
				if (!success){
					return;
				}
				
				pagerAdapter.removePhoto(photo);
				pager.setAdapter(null);
				pager.setAdapter(pagerAdapter);
				pager.setCurrentItem(0);
			}
			
			@Override
			protected Void doInBackground(Void... arg0) {
				try {
					HSApartmentsManager.removeApartmentPhoto(photo);
					success = true;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};
		
		task.execute((Void[])null);
	}	

	public void setActionHandler(GalleryViewActionHandler handler){
		this.actionHandler = handler;		
	}
	
	public HSApartmentPhoto getCurrentlyDisplayedPhoto(){		
		return pagerAdapter.getPhotoAt(pager.getCurrentItem());
	}
	
}

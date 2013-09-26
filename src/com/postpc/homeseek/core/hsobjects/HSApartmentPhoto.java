package com.postpc.homeseek.core.hsobjects;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.postpc.homeseek.core.config.db.HSApartmentDBConfig;
import com.postpc.homeseek.core.config.db.HSMeetupDBConfig;

public class HSApartmentPhoto extends HSObject{

	protected ParseObject apartmentDbObject;

	public static final String VISIBILITY_PUBLIC = "public";
	public static final String VISIBILITY_PRIVATE = "private";	
		
	public static HSApartmentPhoto create(String apartmentId, String userId, String  photoId, byte[] photoData, boolean isPublic) throws ParseException 
	{
		ParseObject apartmentPhotoObject = new ParseObject(HSApartmentDBConfig.PhotoApartmentLink.NAME);
		String visibility;
		
		if (isPublic){
			visibility = VISIBILITY_PUBLIC;
		} else {
			visibility = VISIBILITY_PRIVATE;
		}
		
		ParseFile photoFile = new ParseFile(photoId, photoData);
		photoFile.save();
		
		apartmentPhotoObject.put(HSApartmentDBConfig.PhotoApartmentLink.Fields.APARTMENT_ID, apartmentId);
		apartmentPhotoObject.put(HSApartmentDBConfig.PhotoApartmentLink.Fields.PHOTO_ID, photoId);
		apartmentPhotoObject.put(HSApartmentDBConfig.PhotoApartmentLink.Fields.PHOTO_DATA, photoFile);
		apartmentPhotoObject.put(HSApartmentDBConfig.PhotoApartmentLink.Fields.USER_ID,userId); 
		apartmentPhotoObject.put(HSApartmentDBConfig.PhotoApartmentLink.Fields.VISIBILITY,visibility);
		apartmentPhotoObject.addUnique(HSApartmentDBConfig.PhotoApartmentLink.Fields.ALLOWED_USERS, userId); 
		
		apartmentPhotoObject.save();
		
		return new HSApartmentPhoto(apartmentPhotoObject);
	}	
	
	public HSApartmentPhoto(ParseObject apartmentDbObject)
	{
		super(apartmentDbObject);
		this.apartmentDbObject = apartmentDbObject;
	}

	public String getUserId() 
	{
		return getStringField(HSApartmentDBConfig.PhotoApartmentLink.Fields.USER_ID);
	}
	
	public void setUserId(String userId) 
	{
		put(HSApartmentDBConfig.PhotoApartmentLink.Fields.USER_ID, userId);
	}
	
	public String getPhotoId() 
	{
		return getStringField(HSApartmentDBConfig.PhotoApartmentLink.Fields.PHOTO_ID);
	}
	
	public void setPhotoId(String street) 
	{
		put(HSApartmentDBConfig.PhotoApartmentLink.Fields.PHOTO_ID, street);
	}

	public String getApartmentId() 
	{
		return getStringField(HSApartmentDBConfig.PhotoApartmentLink.Fields.APARTMENT_ID);
	}
	
	public void setApartmentId(String street) 
	{
		put(HSApartmentDBConfig.PhotoApartmentLink.Fields.APARTMENT_ID, street);
	}
	
	public byte[] getPhotoData() throws ParseException 
	{
		return apartmentDbObject.getParseFile(HSApartmentDBConfig.PhotoApartmentLink.Fields.PHOTO_DATA).getData();
	}
		
	public String getPhotoUrl() 
	{
		return apartmentDbObject.getParseFile(HSApartmentDBConfig.PhotoApartmentLink.Fields.PHOTO_DATA).getUrl();
	}	
	
	private String getVisibility()
	{
		return getStringField(HSApartmentDBConfig.PhotoApartmentLink.Fields.VISIBILITY);
	}

	public void setVisibility(String visibility) 
	{
		put(HSApartmentDBConfig.PhotoApartmentLink.Fields.VISIBILITY, visibility);
	}	
	
	public boolean isPublic(){
		String visibility = getVisibility();
		if (visibility.equals(VISIBILITY_PUBLIC)){
			return true;
		}
		
		return false;
	}
	
	public void setPublic(boolean isPublic){
		String visibility;
		
		if (isPublic){
			visibility = VISIBILITY_PUBLIC;
		} else {
			visibility = VISIBILITY_PRIVATE;
		}
		
		setVisibility(visibility);
	}

	public void addAllowedUser(String userId) {
		apartmentDbObject.addUnique(HSApartmentDBConfig.PhotoApartmentLink.Fields.ALLOWED_USERS, userId);		
	}
}

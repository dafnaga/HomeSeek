package com.postpc.homeseek.core.config.db;

public class HSApartmentDBConfig extends DBConfig{
	public class ApartmentTable extends DBConfig.Table {
		public static final String NAME = "apartments";
		
		public class Fields extends DBConfig.Table.Fields {
			public static final String CITY = "city";
			public static final String STREET = "street";
			public static final String BUILDING_NUMBER = "building_number";
			public static final String APARTMENT_NUMBER = "apartment_number";
			public static final String ROOMS = "rooms";
			public static final String AREA = "area";
			public static final String OWNER_ID = "owner_id";	
			public static final String PRICE = "price";
			public static final String TYPE = "type";
			public static final String NEIGHBORHOOD = "neighborhood";
			public static final String MAP_LOCATION = "map_location";			
			public static final String OWNER_COMMENTS = "owner_comments";
 
			//TODO- add floor
		}
	}
	
	public class ClientApartmentLink extends DBConfig.Table {
		public static final String NAME = "client_apartment";
		
		public class Fields extends DBConfig.Table.Fields {
			public static final String APARTMENT_ID = "apartment_id";
			public static final String CLIENT_ID = "client_id";
			public static final String CLIENT_COMMENTS = "client_comments";
		}
	}
	
	public class PhotoApartmentLink extends DBConfig.Table {
		public static final String NAME = "photo_apartment";
		
		public class Fields extends DBConfig.Table.Fields {
			public static final String APARTMENT_ID = "apartment_id";
			public static final String USER_ID	= "user_id";
			public static final String PHOTO_ID = "photo_id";				
			public static final String PHOTO_DATA = "photo_data";
			public static final String VISIBILITY = "visibilty";
			public static final String ALLOWED_USERS = "allowed_users";
		}
	}	
}

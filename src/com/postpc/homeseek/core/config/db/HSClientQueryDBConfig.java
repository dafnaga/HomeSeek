package com.postpc.homeseek.core.config.db;

public class HSClientQueryDBConfig extends DBConfig{
	public class ClientQueryTable extends DBConfig.Table {
		public static final String NAME = "client_queries";
		
		public class Fields extends DBConfig.Table.Fields {
			public static final String CLIENT_ID = "client_id";
			public static final String CITY = "city";
			public static final String STREET= "street";
			public static final String MIN_AREA = "min_area";
			public static final String MAX_AREA = "max_area";
			public static final String MIN_ROOMS = "min_rooms";
			public static final String MAX_ROOMS = "max_rooms";
			public static final String MIN_PRICE = "min_price";
			public static final String MAX_PRICE = "max_price";
			public static final String NEIGHBORHOOD = "neighborhood";						
			public static final String MAP_AREA_CENTER = "map_area_center";
			public static final String MAP_AREA_RADIUS = "map_area_radius";
		}
	}
}

package com.postpc.homeseek.core.config.db;

public class HSMeetupDBConfig extends DBConfig{
	public class MeetupsTable extends DBConfig.Table {
		public static final String NAME = "meetups";
		
		public class Fields extends DBConfig.Table.Fields {
			public static final String START_DATE = "start_date";
			public static final String END_DATE = "end_date";
			public static final String APARTMENT_ID = "apartment_id";
			public static final String ATTENDERS = "attenders";
		}
	}
}

package com.postpc.homeseek.core.config.db;

public class HSInstantMessageDBConfig extends DBConfig{
	public class InstantMessagesTable extends DBConfig.Table {
		public static final String NAME = "instant_messages";
		
		public class Fields extends DBConfig.Table.Fields {
			public static final String SEND_DATE = "send_date";
			public static final String FROM_USER_ID = "from_user_id";
			public static final String TO_USER_ID = "to_user_id";
			public static final String MESSAGE_TYPE = "message_type";
			public static final String MESSAGE_DATA = "message_text";
		}
	}
}

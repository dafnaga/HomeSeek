package com.postpc.homeseek.core.config.db;

public class HSUserDBConfig extends DBConfig{
	public class UsersTable extends DBConfig.Table {
		public static final String NAME = "User";
		
		public class Fields extends DBConfig.Table.Fields {
			public static final String EMAIL = "email";
			public static final String PASSWORD = "password";
			public static final String FIRST_NAME = "first_name";
			public static final String LAST_NAME = "last_name";
		}
	}
}

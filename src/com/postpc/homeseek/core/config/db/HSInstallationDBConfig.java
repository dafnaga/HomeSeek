package com.postpc.homeseek.core.config.db;

public class HSInstallationDBConfig extends DBConfig{
	public class InstallationTable extends DBConfig.Table {
		public static final String NAME = "Installation";
		
		public class Fields extends DBConfig.Table.Fields {
			public static final String USER = "user_id";
			public static final String CHANNELS = "channels";
		}
	}
}

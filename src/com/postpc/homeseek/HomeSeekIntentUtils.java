package com.postpc.homeseek;

import android.content.Intent;

public class HomeSeekIntentUtils {
	
	private static final int DEFAULT_VALUE = Integer.MAX_VALUE;
	
	public static Double getDoubleFromIntent(Intent intent, String key)
	{
		Double temp = intent.getDoubleExtra(key, DEFAULT_VALUE);
		return (temp == DEFAULT_VALUE) ? null: temp;
	}
	
	public static Integer getIntegerFromIntent(Intent intent, String key)
	{
		Integer temp = intent.getIntExtra(key, DEFAULT_VALUE);
		return (temp == DEFAULT_VALUE) ? null: temp;
	}
	

}

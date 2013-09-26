package com.postpc.homeseek.core.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.postpc.homeseek.HomeseekApplication;
import com.postpc.homeseek.core.managers.HSNotificationsManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchApartmentException;
import com.postpc.homeseek.core.managers.exceptions.NoSuchUserException;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

public class HSSessionManager {

	static HSSession activeSession = null;	
	
	static public HSSession login(Credentials credentials) throws ParseException
	{
		if (isLoggedIn()){
			activeSession.close();
		}		
		
		if (isOtherLoggedIn(credentials)) {
			activeSession.closeOtherUser();	
		}
	
		ParseUser user = ParseUser.logIn(credentials.getEmail(), credentials.getPassword()); // TODO distinguish between general error and authentication error
		if (user == null){
			return null;
		}
		
		createCookie(credentials);
		
		try {
			activeSession = new HSSession(credentials.getEmail());
		} catch (NoSuchUserException e) {
			e.printStackTrace();
			return null;
		}
		
		HSNotificationsManager.init(user);
		
		 try {
			HSNotificationsManager.updateUserChannels(getActiveSession().getUser());
		} catch (NoSuchApartmentException e) {
			e.printStackTrace();
		} catch (UserNotLoggedInException e) {
			e.printStackTrace();
		}
		
		return activeSession;
	}
	
		
	static public HSSession getActiveSession()
	{
		if (activeSession == null){
			setupSession();
		}
		
		return activeSession;
	}
	
	static public void logout()
	{
		activeSession.close();
		
		setupSession();
	}
	
		
		// Check for a cookie
	private static void setupSession() {
		Credentials cred;
		
		// Fallback to an anonymous session
		activeSession = new HSSession();
		
		cred = getCookie();
		if(cred == null){
			return;
		}
		
		// Login with given credentials
		try {					
			login(cred);
		} catch (Exception e) {
			// On error, stay with the anonymous session			
		}	
	}


	public static boolean isLoggedIn() {
		return activeSession.isAnonymousSession(); 
	}
	
	public static boolean isOtherLoggedIn(Credentials credentials) {
		return activeSession.isOtherUserSession(credentials);
	}
	
	private static void createCookie(Credentials credentials) {
		SharedPreferences credCookie = HomeseekApplication.getContext().getSharedPreferences(Credentials.cookieName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = credCookie.edit();
		
		editor.putString(Credentials.emailField, credentials.getEmail());
		editor.putString(Credentials.passwordField, credentials.getPassword());
		editor.commit();
	}
	
	private static Credentials getCookie() {
		SharedPreferences credCookie = HomeseekApplication.getContext().getSharedPreferences(Credentials.cookieName, Context.MODE_PRIVATE);
		
		String email = credCookie.getString(Credentials.emailField, "");
		String password = credCookie.getString(Credentials.passwordField, "");
		
		if (email == ""){
			// No cookie
			return null;
		}
		
		return new Credentials(email, password);
	}
}
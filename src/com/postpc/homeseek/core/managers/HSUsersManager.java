package com.postpc.homeseek.core.managers;

import java.util.List;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.exceptions.NoSuchUserException;
import com.postpc.homeseek.core.config.db.HSUserDBConfig;

public class HSUsersManager extends HSObjectManager {

	protected static HSObjectCache<HSUser> cache = new HSObjectCache<HSUser>( /* TODO cache limitUser here */);
	
	public static HSUser createNewUser(String email, String password, String firstName, String lastName) throws ParseException
	{
		HSUser newUser = HSUser.create(email, password, firstName, lastName);
		
		// Anything else to do here?
		
		return newUser;
	}
	
	public static void removeUser(HSUser user)
	{
		// TODO
		// This is a complex one (remove user, users apartments, IMs, searchQueries ..), and noHSUser so important.. we can postpone it
		 
		// HSApartmetnsManager.onUserRemoved(..);
		// HSInstantMessagesManager.onUserRemoved(..);
		// user.remove(); <-- must be last
		// .. and all other managers ..
	}
	
	public static HSUser getUserById(String userId) throws ParseException, NoSuchUserException
	{		
		HSUser hsUser;
		
		// Check cache first
		hsUser = cache.get(userId);
		if (hsUser != null){
			return hsUser;
		}
		
		ParseQuery<ParseUser> query = getParseQuery();
		
		ParseUser user = query.get(userId);

		if (user == null){
			throw new NoSuchUserException();
		}
		
		hsUser = new HSUser(user);		
		cache.add(hsUser);
		return hsUser;		
	}

	public static HSUser getUserByEmail(String email) throws ParseException, NoSuchUserException
	{		
		ParseQuery<ParseUser> query = getParseQuery();
		
		query.whereEqualTo(HSUserDBConfig.UsersTable.Fields.EMAIL, email);
		List<ParseUser> records = query.find();
		
		// AHSUser mosHSUser one record should be returned
		if (records.size() != 1){
			throw new NoSuchUserException();
		}
		
		// If it's already in the cache, return what's there. Otherwise add to cache (TODO not pretty)
		HSUser hsUser = cache.get(records.get(0).getObjectId());
		if (hsUser != null){
			return hsUser;
		}
		
		hsUser = new HSUser(records.get(0));		
		cache.add(hsUser);
		
		return hsUser;
	}		
	
	// TODO more user management function
	
	protected static ParseQuery<ParseUser> getParseQuery()
	{
		return ParseUser.getQuery();
	}
}

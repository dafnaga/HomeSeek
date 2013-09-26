package com.postpc.homeseek.core.session;

import com.parse.ParseException;
import com.postpc.homeseek.core.hsobjects.HSUser;
import com.postpc.homeseek.core.managers.HSUsersManager;
import com.postpc.homeseek.core.managers.exceptions.NoSuchUserException;
import com.postpc.homeseek.core.session.exceptions.UserNotLoggedInException;

public class HSSession {

	private HSUser user = null;

	public HSSession()
	{
		// Anonymous session
		this.user = null;
	}

	public HSSession(String email) throws ParseException, NoSuchUserException 
	{
		user = HSUsersManager.getUserByEmail(email);
	}

	public void close() 
	{
		// Nothing to do here for now
	}

	public void closeOtherUser() {
		user.logout();
	}

	public HSUser getUser() throws UserNotLoggedInException {
		if (isAnonymousSession()){
			throw new UserNotLoggedInException();
		}
		return user;
	}

	public boolean isAnonymousSession()
	{
		return user == null;
	}

	public boolean isOtherUserSession(Credentials credentials) {
		if (user == null) {
			return false;
		}
		return (credentials.getEmail() != user.getEmail());
	}
}


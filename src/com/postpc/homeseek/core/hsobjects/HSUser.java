package com.postpc.homeseek.core.hsobjects;


import com.parse.ParseException;
import com.parse.ParseUser;
import com.postpc.homeseek.core.config.db.HSUserDBConfig;

public class HSUser extends HSObject{

	protected ParseUser userDbObject;

	public static HSUser create(String email, String password, String firstName, String lastName) throws ParseException
	{
		ParseUser userObject = new ParseUser();

		userObject.setUsername(email);
		userObject.setEmail(email);
		userObject.setPassword(password);

		userObject.put(HSUserDBConfig.UsersTable.Fields.FIRST_NAME, firstName);
		userObject.put(HSUserDBConfig.UsersTable.Fields.LAST_NAME, lastName);
		userObject.signUp();

		return new HSUser(userObject);
	}

	public void logout()
	{
		ParseUser.logOut();
	}

	public HSUser(ParseUser userDbObject)
	{
		super(userDbObject);
		this.userDbObject = userDbObject;
	}

	public String getEmail()
	{
		return userDbObject.getEmail();
	}

	public void setEmail(String email)
	{
		userDbObject.setEmail(email);
	}

	public String getFirstName()
	{
		return getStringField(HSUserDBConfig.UsersTable.Fields.FIRST_NAME);
	}

	public void setFirstName(String firstName)
	{
		put(HSUserDBConfig.UsersTable.Fields.FIRST_NAME, firstName);
	}

	public String getLastName()
	{
		return getStringField(HSUserDBConfig.UsersTable.Fields.LAST_NAME);
	}

	public void setLastName(String lastName)
	{
		put(HSUserDBConfig.UsersTable.Fields.LAST_NAME, lastName);
	}

	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

	/* TODO other fields.. */
}

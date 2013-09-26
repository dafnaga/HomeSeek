package com.postpc.homeseek.core.db;

public interface ParseUser extends ParseObject{

	public void setEmail(String email);
	
	public String getEmail();
	
	public void setPassword(String password);
	
	public String getPassword();
}


package com.postpc.homeseek.core.session;

public class Credentials {

	private String email;
	private String password;
	
	static final String cookieName = "CREDENTIALS";
	public static final String emailField = "EMAIL";
	public static final String passwordField = "PASSWORD";
	
	public Credentials(String email, String password) {
		setEmail(email);
		setPassword(password);
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String username) {
		this.email = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}

package com.musicfever.music_fever.config.jwt;

public class LoginRequest {

	private String username;
	private String email;
	private String password;

	public LoginRequest() {
	}

	public LoginRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public LoginRequest(String email, String username, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		return "LoginRequest [username=" + username + ", password=" + password + "]";
	}
}
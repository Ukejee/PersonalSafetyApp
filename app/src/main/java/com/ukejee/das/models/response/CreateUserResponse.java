package com.ukejee.das.models.response;

import com.ukejee.das.models.User;

import java.io.Serializable;

public class CreateUserResponse implements Serializable {
	private String message;
	private User user;
	private String token;

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setUser(User user){
		this.user = user;
	}

	public User getUser(){
		return user;
	}

	public void setToken(String token){
		this.token = token;
	}

	public String getToken(){
		return token;
	}
}

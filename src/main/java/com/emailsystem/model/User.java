package main.java.com.emailsystem.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import main.java.com.emailsystem.enumpackage.FolderType;

public class User {
	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName + ", emailAddress=" + emailAddress
				+ ", passwordHash=" + passwordHash + "]";
	}

	private int userId;
	private String userName;
	private String emailAddress;
	private String passwordHash;

	public User(int userId, String userName, String emailAddress, String passwordHash) {
		this.userId = userId;
		this.userName = userName;
		this.emailAddress = emailAddress;
		this.passwordHash = passwordHash;
	}
	
	public User(String userName, String emailAddress, String passwordHash) {
		this.userName = userName;
		this.emailAddress = emailAddress;
		this.passwordHash = passwordHash;
	}

	public String getEmailAddress() {
		return this.emailAddress;
	}

	public int getUserId() {
		return this.userId;
	}

	public String getUserName() {
		return this.userName;
	}
	
	public String getPassword() {
		return this.passwordHash;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setPassword(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Boolean checkPassword(String password) {
		if (this.passwordHash.equals(password)) {
			return true;
		}
		return false;
	}
}

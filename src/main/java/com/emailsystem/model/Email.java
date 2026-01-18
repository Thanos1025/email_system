package main.java.com.emailsystem.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Email {
	public void setSubject(String subject) {
		this.subject = subject;
	}



	public void setBody(String body) {
		this.body = body;
	}



	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}



	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	private int emailId;
	private String subject;
	private String body;
	private LocalDateTime createDate;
	private LocalDateTime sentDate;
	private int senderId;

	public Email(String subject, String body, int senderId, LocalDateTime createDate) {
		this.subject = subject;
		this.body = body;
		this.senderId = senderId;
		this.createDate = createDate;
	}
	
	

	public Email(int emailId, String subject, String body, LocalDateTime createdDate, LocalDateTime sentDate, int senderId) {
		super();
		this.emailId = emailId;
		this.subject = subject;
		this.body = body;
		this.createDate = createdDate;
		this.sentDate = sentDate;
		this.senderId = senderId;
	}

	public int getEmailId() {
		return this.emailId;
	}

	public String getSubject() {
		return this.subject;
	}

	public String getBody() {
		return this.body;
	}

	public LocalDateTime getSentDate() {
		return this.sentDate;
	}

	public LocalDateTime getCreatedDate() {
		return this.createDate;
	}

	public int getSenderId() {
		return this.senderId;
	}
	

	public void setEmailId(int emailId) {
		this.emailId = emailId;
	}
	
	public void setCreatedDate(LocalDateTime date) {
		this.createDate = date;
	}

	public void setSentDate(LocalDateTime date) {
		this.sentDate = date;
	}
}

package main.java.com.emailsystem.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Email {
	private int emailId;
	private String subject;
	private String body;
	private LocalDateTime createdDate;
	private LocalDateTime sentDate;
	private Boolean isRead = false;
	private int senderId;
	private List<Integer> recipientIds;
	private List<Integer> subEmails = new LinkedList<>();

	public Email(int emailId, String subject, String body, int senderId, List<Integer> recipientIds) {
		this.emailId = emailId;
		this.subject = subject;
		this.body = body;
		this.senderId = senderId;
		this.recipientIds = recipientIds;
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
		return this.createdDate;
	}

	public String getIsRead() {
		if(this.isRead) {
			return "Read";
		}else {
			return "Unread";
		}
	}

	public int getSenderId() {
		return this.senderId;
	}

	public List<Integer> getRecipientIds() {
		return this.recipientIds;
	}

	public List getReplyMailIds() {
		return this.subEmails;
	}

	public void setCreatedDate(LocalDateTime date) {
		this.createdDate = date;
	}

	public void setSentDate(LocalDateTime date) {
		this.sentDate = date;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public void sendReplyEmail(int repliedEmailId) {
		this.subEmails.add(repliedEmailId);
	}

	public void sendFowardEmail(int forwardEmailId) {
		this.subEmails.add(forwardEmailId);
	}
}

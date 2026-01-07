package main.java.com.emailsystem.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import main.java.com.emailsystem.controller.ServerAndClientHandler;
import main.java.com.emailsystem.enumpackage.FolderType;
import main.java.com.emailsystem.exception.InvalidUserException;
import main.java.com.emailsystem.exception.NoEmailAddressExistException;
import main.java.com.emailsystem.exception.NoUserException;
import main.java.com.emailsystem.exception.NothingInTheFolderException;
import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.Folder;
import main.java.com.emailsystem.model.User;
import main.java.com.emailsystem.server.MailServer;

public class MailClient {
	private int clientId;
	private String name;
	private int connectedServerId;
	private int loggedInUserId;
	private MailServer server;

	public MailClient(String name, int connectedServerId) {
		this.clientId = ServerAndClientHandler.clients.size();
		this.name = name;
		this.connectedServerId = connectedServerId;
		this.server = ServerAndClientHandler.getServer(connectedServerId);
	}

	// ---------------- MAILCLIENT GETTER METHODS ---------------- //
	
	public int getClientId() {
		return this.clientId;
	}

	public String getClientName() {
		return this.name;
	}

	public int getConnectedServerId() {
		return this.connectedServerId;
	}

	public int getLoggedInUserId() {
		return this.loggedInUserId;
	}

	public String getLoggedInUserUserName() {
		User user = server.getUser(loggedInUserId);
		return user.getUserName();
	}
	
	// ---------------- Other getter METHODS ---------------- //

	public List<Integer> getFolders() {
		return this.server.getSpecificUserFolders(loggedInUserId);
	}


	// ---------------- AUTHENTICATION METHODS ---------------- //
	
	public Boolean emailExist(String emailAddress) throws NoEmailAddressExistException {
		return server.emailExist(emailAddress);
	}

	public int addUser(String userName, String emailAddress, String password) {
		return server.addUser(userName, emailAddress, password);
	}

	public int login(String emailAddress, String password) throws InvalidUserException {
		this.loggedInUserId = server.authenticate(emailAddress, password);
		return this.loggedInUserId;
	}

	// ---------------- EMAIL METHODS ---------------- //
	public List<Integer> getAllRecipientIdsWithEmailAddresses(List<String> recipientEmailAddresses) throws NoUserException{
		List<Integer> recipientIds = new LinkedList<>();
		for(String recipientEmailAddress : recipientEmailAddresses) {
			recipientIds.add(server.getUserId(recipientEmailAddress));
		}
		return recipientIds;
	}
	
	public int composeEmail(List<Integer> recipientIds, String subject, String body) {
		int emailId = server.addEmail(subject, body, loggedInUserId, recipientIds);

		return emailId;
	}

	public void sendEmail(int emailId) {
		Email email = server.getEmail(emailId);
		server.sendEmail(emailId, loggedInUserId);
		for(int recipientId : email.getRecipientIds()) {
			server.receiveEmail(emailId, recipientId);
		}
	}

	public void sendEmail(int emailId, List<Integer> recipientIds) {
		server.sendEmail(emailId, loggedInUserId);
		for(int recipientId : recipientIds) {
			server.receiveEmail(emailId, recipientId);
		}
	}

	public void sendDraftEmail(int emailId) {
		sendEmail(emailId);
		server.removeEmailFromAFolder(emailId, loggedInUserId, FolderType.DRAFT.getIndex());
	}

	public void draftEmail(int emailId) {
		server.draftEmail(emailId, loggedInUserId);
	}

	public void trashEmail(int emailId, int previousFolderId) {
		server.trashEmail(emailId, loggedInUserId, previousFolderId);
	}

	public void restoreEmail(int emailId) {
		server.restoreEmail(emailId, loggedInUserId);
	}

	public void deleteEmailwhileCompose(int emailId) {
		server.deleteEmail(emailId);
	}

	public void replyEmail(int emailId,List<Integer> recipientEmailIds, String subject, String body) {
		int repliedEmailId = server.addEmail(subject, body, loggedInUserId, recipientEmailIds);
		server.replyEmail(emailId, repliedEmailId);
	}

	public void forwardEmail(int emailId, List<Integer> recipientEmailIds) throws NoUserException {
		Email email = server.getEmail(emailId);
		User emailSender = server.getUser(email.getSenderId());
		List<User> emailRecipients = server.getUsers(email.getRecipientIds());
		String emailRecipientsString = "";
		for(User emailRecipient: emailRecipients) {
			emailRecipientsString = emailRecipient.getUserName()+" ";
		}
		String body = "-------- Forwarded message --------"+"\nFrom: "+emailSender.getUserName()+"\nSubject: "+emailRecipientsString+"/n"+email.getBody();//see gmail forwarded message for reference dont forget
		String subject = email.getSubject();
		int forwardedEmailId = composeEmail(recipientEmailIds, subject, body);
		sendEmail(forwardedEmailId, recipientEmailIds);
	}

	// ---------------- SYNCS METHODS ---------------- //

	public void syncFolders() {
		String[] folders = server.fetchFolder(loggedInUserId);
		for (String folder : folders) {
			System.out.println(folder);
		}
	}

	public List syncEmails(int folderId) throws NothingInTheFolderException {
		String[] emails = server.fetchEmails(loggedInUserId, folderId);
		for (String email : emails) {
			System.out.println(email);
		}
		return server.getFolderEmailIds(loggedInUserId, folderId);
	}

	// ---------------- OPEN MAIL METHODS ---------------- //

	public void showEmail(int emailId, int folderId) {
		Email email = server.getEmail(emailId);
		email.setIsRead(true);
		int senderId = email.getSenderId();
		List<Integer> recipientIds = email.getRecipientIds();
		String recipientEmailAddress = "";
		for(int recipientId: recipientIds) {
			if(recipientId == loggedInUserId) {
				recipientEmailAddress += "you ";
				continue;
			}
			recipientEmailAddress += server.getUser(recipientId).getEmailAddress()+" ";
		}
		System.out.println();
		System.out.println("Sender: " + server.getUser(senderId).getEmailAddress());
		System.out.println("Recipient: " + recipientEmailAddress);
		if(folderId == 2) {
			System.out.println("Date: " + server.getShortDateFormat(email.getCreatedDate()));
		}else if(folderId == 0 || folderId == 1) {
			System.out.println("Date: " + server.getShortDateFormat(email.getSentDate()));
		}
		System.out.println("Subject: " + email.getSubject());
		System.out.println("Body: " + email.getBody());
		System.out.println("Readed Status: "+ email.getIsRead());
		List<Integer> repliedEmailIds = email.getReplyMailIds();//look after this later, I replace the replied email with the subemails
		int indexOfRepliedEmail = 1;
		if (!repliedEmailIds.isEmpty()) {
			for (int repliedEmailId : repliedEmailIds) {
				Email repliedEmail = server.getEmail(repliedEmailId);
				int repliedEmailSenderId = repliedEmail.getSenderId();
				System.out.printf("%-5s %-4s %-20s %-4s %-20s", "", indexOfRepliedEmail, server.getUser(repliedEmailSenderId).getUserName(), "", repliedEmail.getSubject());
			}
		}
	}
}

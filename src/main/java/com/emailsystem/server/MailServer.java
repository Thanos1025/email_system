package main.java.com.emailsystem.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
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

public class MailServer {
	private int serverId;
	private String name;
	private String domain;
	private Map<Integer, User> users = new LinkedHashMap<>();
	private Map<Integer, Email> emails = new LinkedHashMap<>();
	private Map<Integer, Folder> folders = new LinkedHashMap<>();

	public MailServer(String name, String domain) {
		this.serverId = ServerAndClientHandler.servers.size();
		this.name = name;
		this.domain = domain;
	}
	
	// ---------------- DATE FORMATTER METHODS ---------------- //
	
	public String getShortDateFormat(LocalDateTime date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d");
		return date.format(formatter);
	}

	// ---------------- SERVER GETTER METHODS ---------------- //

	
	public Map<Integer, User> getUsers(){
		return this.users;
	}
	
	public Map<Integer, Email> getEmails(){
		return this.emails;
	}
	
	public Map<Integer, Folder> getFolders(){
		return this.folders;
	}

	public String getDomain() {
		return this.domain;
	}

	public int getServerId() {
		return this.serverId;
	}
	
	// ---------------- USER METHODS ---------------- //


	public int importUser(int userId,String name, String emailAddress, String password) {
		this.users.put(userId, new User(userId, name, emailAddress, password));
		return userId;
	}
	
	public int addUser(String name, String emailAddress, String password) {
		int userId = users.size();
		this.users.put(userId, new User(users.size(), name, emailAddress, password));
		User user = users.get(userId);
		int inbox = addFolder("Inbox", true);
		int sent = addFolder("Sent", true);
		int draft = addFolder("Draft", true);
		int trash = addFolder("Trash", true);
		user.addFolder(inbox);
		user.addFolder(sent);
		user.addFolder(draft);
		user.addFolder(trash);
		return userId;
	}

	public User getUser(int userId) {
		return this.users.get(userId);
	}

	public List<User> getUsers(List<Integer> userIds){
		List<User> users = new LinkedList<>();
		for(int userId: userIds) {
			users.add(this.users.get(userId));
		}
		return users;
	}
	public int getUserId(String emailAddress) throws NoUserException {
		for (Map.Entry<Integer, User> entry : users.entrySet()) {
			User user = entry.getValue();
			if (user.getEmailAddress().equals(emailAddress)) {
				return user.getUserId();
			}
		}
		throw new NoUserException("There is no user with this email Address");
	}

	public void deleteUser(int userId) {
		this.users.remove(userId);
	}

	// ---------------- AUTHENTICATION METHODS ---------------- //

	public Boolean emailExist(String emailAddress) throws NoEmailAddressExistException {
		if (users.size() == 0) {
			throw new NoEmailAddressExistException("No email have been created in this server");
		}
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			if (user.getEmailAddress().equals(emailAddress)) {
				return true;
			}
		}
		return false;
	}

	public int authenticate(String emailAddress, String password) throws InvalidUserException {
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			if (user.getEmailAddress().equals(emailAddress) && user.checkPassword(password)) {
				return user.getUserId();
			}
		}
		throw new InvalidUserException("Invalid Username or Password");
	}

	// ---------------- FOLDER METHODS ---------------- //

	public List<Integer> getSpecificUserFolders(int userId) {
		return users.get(userId).getFolderIds();
	}
	
	public void importFolder(int folderId, String folderName, Boolean isDefaultFolder) {
		folders.put(folderId, new Folder(folderId, folderName, isDefaultFolder));
	}
	
	public int addFolder(String folderName, Boolean isDefaultFolder) {
		int folderId = folders.size();
		folders.put(folderId, new Folder(folderId, folderName, isDefaultFolder));
		return folderId;
	}
	
	public Boolean deleteFolder(int folderId) {
		Folder folder = folders.get(folderId);
		if(folder.getIsDefaultFolder()) {
			return false;
		}
		else {
			folders.remove(folderId);
			return true;
		}
	}
	
	public Folder getFolder(int folderId) {
		return this.folders.get(folderId);
	}

	// ---------------- EMAIL METHODS ---------------- //
	
	public void importEmail(int emailId, String subject, String body, LocalDateTime createdDate, LocalDateTime sentDate,
		Boolean isRead, int senderId) {
		emails.put(emailId, new Email(emailId, subject, body, createdDate, sentDate, isRead, senderId) );
	}
	
	public void mapEmailwithRecipient(int emailId, int recipientId) {
		Email email = getEmail(emailId);
		List<Integer> recipientIds = email.getRecipientIds();
		recipientIds.add(recipientId);
	}
	
	public int addEmail(String subject, String body, int senderId, List<Integer> recipientIds) {
		int emailId = emails.size();
		emails.put(emailId, new Email(emails.size(), subject, body, senderId, recipientIds));
		Email email = getEmail(emailId);
		email.setCreatedDate(LocalDateTime.now());
		return emailId;
	}
	
	public void deleteEmail(int emailId) {
		emails.remove(emailId);
	}

	public Email getEmail(int emailId) {
		return this.emails.get(emailId);
	}

	public void receiveEmail(int emailId, int userId) {
		Email email = getEmail(emailId);
		User user = getUser(userId);
		int folderId = user.getFolderId(FolderType.INBOX.getIndex());
		Folder folder = folders.get(folderId);
		user.putEmail(emailId, folder);
	}

	public void sendEmail(int emailId, int userId) {
		Email email = getEmail(emailId);
		email.setSentDate(LocalDateTime.now());
		User user = getUser(userId);
		int folderId = user.getFolderId(FolderType.SENT.getIndex());
		Folder folder = folders.get(folderId);
		user.putEmail(emailId, folder);
	}

	public void draftEmail(int emailId, int userId) {
		User user = getUser(userId);
		int folderId = user.getFolderId(FolderType.DRAFT.getIndex());
		Folder folder = folders.get(folderId);
		user.putEmail(emailId, folder);
	}

	public void trashEmail(int emailId, int userId, int folderId) {
		User user = getUser(userId);
		int currentFolderId = user.getFolderId(folderId);
		Folder currentFolder = folders.get(currentFolderId);
		int trashFolderId = user.getFolderId(FolderType.TRASH.getIndex());
		Folder trashFolder = folders.get(trashFolderId);
		if(folderId != FolderType.TRASH.getIndex()) {
			user.deleteEmail(emailId, currentFolderId, currentFolder, trashFolder);
		}else {
			user.permanentlyDeleteEmail(emailId, trashFolder);
		}
	}
	
	public void restoreEmail(int emailId, int userId) {
		User user = getUser(userId);
		int trashFolderId = user.getFolderId(FolderType.TRASH.getIndex());
		Folder trashFolder = folders.get(trashFolderId);
		int emailIndex = trashFolder.getEmailIdIndex(emailId);
		List<Integer> previousFolderIds = trashFolder.getPreviousFolderIds();
		Folder previousFolder = folders.get(previousFolderIds.get(emailIndex));
		user.moveEmail(emailId, previousFolder, trashFolder);
	}
	
	public void removeEmailFromAFolder(int emailId, int userId, int folderId) {
		User user = getUser(userId);
		Folder folder = folders.get(user.getFolderId(folderId));
		folder.removeEmail(emailId);
	}
	
	public int getSenderId(int emailId) {
		Email email = getEmail(emailId);
		return email.getSenderId();
	}
	
	public void mapEmailWithSubEmail(int emailId, int subEmailId) {
		Email email = getEmail(emailId);
		email.setSentDate(LocalDateTime.now());
		email.sendReplyEmail(subEmailId);
	}
	
	// ---------------- FETCH METHODS ---------------- //

	public String[] fetchFolder(int userId) {
		User user = getUser(userId);
		List<Integer> folderIds = user.getFolderIds();
		String[] foldersString = new String[folderIds.size() + 1];
		int length = 1;
		foldersString[0] = "\nFolders";
		for (int folderId : folderIds) {
			Folder folder = folders.get(folderId);
			foldersString[length] = (length) + "." + folder.getFolderName();
			length++;
		}
		return foldersString;
	}

	public String[] fetchEmails(int userId, int folderId) throws NothingInTheFolderException {
		User user = getUser(userId);
		Folder folder = folders.get(user.getFolderId(folderId));
		List<Integer> emailIds = folder.getEmailIds();
		String[] emailsString = new String[emailIds.size() + 1];
		
		emailsString[0] = "\n" + folder.getFolderName();
		int noOfEmails = 1;
		
		for (int emailId : emailIds) {
			Email email = emails.get(emailId);	
			if(folderId==1 || folderId == 2) {
				String recipientEmailAddresses = "";
				List<Integer> recipientIds = email.getRecipientIds();
				for(int recipientId : recipientIds) {	
						if(recipientId != userId) {
							recipientEmailAddresses+=(users.get(recipientId)).getEmailAddress()+" ";
						}else {
							recipientEmailAddresses+="you ";
						}
				}
				if(folderId ==1) {
					emailsString[noOfEmails] = String.format("%-5s %-20s %-5s %-20s %-5s %-5s", (noOfEmails) + ".","To: "+recipientEmailAddresses, "", email.getSubject(), "", getShortDateFormat(email.getSentDate()));
				}else {
					emailsString[noOfEmails] = String.format("%-5s %-20s %-5s %-20s %-5s %-5s", (noOfEmails) + ".","To: "+recipientEmailAddresses, "", email.getSubject(), "", getShortDateFormat(email.getCreatedDate()));
				}
			}else {
				String emailAddress = users.get(email.getSenderId()).getEmailAddress();
				emailsString[noOfEmails] = String.format("%-5s %-20s %-5s %-20s %-5s %-5s %-5s %-5s ", (noOfEmails) + ".",emailAddress, "", email.getSubject(), "", email.getIsRead(), "", getShortDateFormat(email.getSentDate()));
			}
			noOfEmails++;
		}
		
		if (emailIds.isEmpty()) {
			throw new NothingInTheFolderException(folder.getFolderName()+"\nNothing inside the "+folder.getFolderName());
		}
		return emailsString;
	}
	
	public List getFolderEmailIds(int userId, int folderId) {
		User user = getUser(userId);
		Folder folder = folders.get(user.getFolderId(folderId));
		return folder.getEmailIds();
	}
}

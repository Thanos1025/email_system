package main.java.com.emailsystem.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import main.java.com.emailsystem.enumpackage.FolderType;

public class User {
	private int userId;
	private String userName;
	private String emailAddress;
	private String passwordHash;
	private List<Integer> folderIds = new LinkedList<>();

	public User(int userId, String userName, String emailAddress, String passwordHash) {
		this.userId = userId;
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

	public int getFolderId(int folderId) {
		return folderIds.get(folderId);
	}

	public List<Integer> getFolderIds() {
		return this.folderIds;
	}

	public Boolean checkPassword(String password) {
		if (this.passwordHash.equals(password)) {
			return true;
		}
		return false;
	}

	public void putEmail(int emailId, Folder folder) {
		folder.addEmail(emailId);
	}
	
	public void putEmail(int emailId, Folder folder, int previousFolderId) {
		folder.addEmail(emailId, previousFolderId);
	}

	public Boolean getEmail(int emailId, Folder folder) {
		return folder.checkEmail(emailId);
	}

	public void deleteEmail(int emailId, int currentFolderId,Folder currentFolder, Folder trashFolder ) {
		currentFolder.removeEmail(emailId);
		this.putEmail(emailId, trashFolder, currentFolderId);
	}
	
	public void permanentlyDeleteEmail(int emailId, Folder folder) {//remember the parameter of the folder is trash folder
		folder.removeEmail(emailId);
	}

	public void copyEmail(int emailId, int sourceFolderId, Folder copytoFolderId) {
		this.putEmail(emailId, copytoFolderId, sourceFolderId);
	}

	public void moveEmail(int emailId, Folder movetoFolder, Folder sourceFolder) {
		int sourceFolderId = sourceFolder.getFolderId();
		sourceFolder.removeEmail(emailId);
		this.putEmail(emailId, movetoFolder, sourceFolderId);
	}

	public void addFolder(int folderId) {
		folderIds.add(folderId);
	}

	public void deleteFolder(int folderIdIndex) {
		folderIds.remove(folderIdIndex);
	}
}

package main.java.com.emailsystem.model;

import java.util.LinkedList;
import java.util.List;

public class Folder {
	private int folderId;
	private String folderName;
	private Boolean isDefaultFolder = false;
	private List<Integer> emailIds = new LinkedList<>();
	private List<Integer> previousFolderIds = new LinkedList<>();

	public Folder(int folderId, String folderName, Boolean isDefaultFolder) {
		this.folderId = folderId;
		this.folderName = folderName;
		this.isDefaultFolder = isDefaultFolder;
	}

	public int getFolderId() {
		return this.folderId;
	}

	public String getFolderName() {
		return this.folderName;
	}

	public Boolean getIsDefaultFolder() {
		return this.isDefaultFolder;
	}
	public List<Integer> getEmailIds() {
		return emailIds;
	}
	
	public List<Integer> getPreviousFolderIds(){
		return previousFolderIds;
	}
	
	public int getPreviousFolderId(int index){
		return previousFolderIds.get(index);
	}
	
	public int getEmailIdIndex(int emailId) {
		return emailIds.indexOf(emailId);
	}
	
	public void addEmail(int emailId, int folderId) {
		getEmailIds().add(emailId);
		getPreviousFolderIds().add(folderId);
	}
	
	public void addEmail(int emailId) {
		addEmail(emailId, -1);
	}

	public void removeEmail(int emailId) {
		int index = getEmailIds().indexOf(emailId);
		if(index>=0) {
			getEmailIds().remove(index);
			getPreviousFolderIds().remove(index);
		}
	}

	public Boolean checkEmail(int emailId) {
		for (int email : getEmailIds()) {
			if (emailId == email) {
				return true;
			}
		}
		return false;
	}
}

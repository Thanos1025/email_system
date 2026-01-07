package main.java.com.emailsystem.model;

import java.util.LinkedList;
import java.util.List;

public class Folder {
	private int folderId;
	private String folderName;
	private Boolean isDefaultFolder = false;

	public Folder(int folderId, String folderName, Boolean isDefaultFolder) {
		this.folderId = folderId;
		this.folderName = folderName;
		this.isDefaultFolder = isDefaultFolder;
	}
	
	public Folder(String folderName, Boolean isDefaultFolder) {
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
		
	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}
}

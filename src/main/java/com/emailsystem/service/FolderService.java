package main.java.com.emailsystem.service;

import java.sql.SQLException;
import java.util.List;

import main.java.com.emailsystem.dao.*;
import main.java.com.emailsystem.exception.CantDeleteDefaultFolderException;
import main.java.com.emailsystem.exception.EmailAddressAlreadyExistException;
import main.java.com.emailsystem.exception.NothingInTheFolderException;
import main.java.com.emailsystem.exception.UserFolderRelationNotCreatedException;
import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.Folder;
import main.java.com.emailsystem.model.User;

public class FolderService {
	public FoldersDAO foldersDAO = new FoldersDAOImpl();
	public UserFolderRelationsDAO userFolderRelationsDAO = new UserFolderRelationsDAOImpl();
	public FolderEmailRelationsDAO folderEmailRelationsDAO = new FolderEmailRelationsDAOImpl();
	
	public Folder createFolder(Folder folder, int userId) throws SQLException, UserFolderRelationNotCreatedException, EmailAddressAlreadyExistException {
		folder = foldersDAO.save(folder);
		if(userFolderRelationsDAO.createRelation(userId, folder.getFolderId())) {
			return folder;
		}else {
			throw new UserFolderRelationNotCreatedException("User(userId-"+userId+") and Folder(folderId-"+folder.getFolderId()+") relation is not created");
		}
	}
	
	public int updateFolder(Folder folder, int folderId) throws SQLException {
		return foldersDAO.update(folder, folderId);
	}
	
	public Boolean getIsRead(Folder folder, Email email) throws SQLException{
		return folderEmailRelationsDAO.getIsRead(folder.getFolderId(), email.getEmailId());
	}
	
	public String getStringIsRead(Folder folder, Email email) throws SQLException{
		return folderEmailRelationsDAO.getIsRead(folder.getFolderId(), email.getEmailId())? "Read": "Unread";
	}
	
	public int updateIsRead(Folder folder, Email email , Boolean isRead) throws SQLException {
		return folderEmailRelationsDAO.updateIsRead(folder.getFolderId(), email.getEmailId(), isRead);
	}
	
	public boolean deleteFolder(Folder folder) throws SQLException, CantDeleteDefaultFolderException{
		if(folder.getIsDefaultFolder()) {
			throw new CantDeleteDefaultFolderException("You can't delete default folder!!");
		}else {
			userFolderRelationsDAO.deleteUserFolderRelation(folder.getFolderId());
			return foldersDAO.delete(folder.getFolderId());
		}
	}
	public List<Email> getEmailsByFolderId(Folder folder) throws SQLException, NothingInTheFolderException{
		List<Email> emails = folderEmailRelationsDAO.getRelationObjects(folder.getFolderId());
		if(emails.isEmpty()) {
			throw new NothingInTheFolderException("There is nothing inside the folder "+folder.getFolderName());
		}else {
			return emails;
		}
	}
	
	public Folder getFolderByFolderNameAndUserId(String folderName, int userId) throws SQLException {
		return foldersDAO.getFolderByFolderNameAndUserId(folderName, userId);
	}
	

	//create the default folders
	public void createDefaultFolders(User user) throws SQLException, UserFolderRelationNotCreatedException, EmailAddressAlreadyExistException {
		Folder inbox = new Folder("Inbox", true);
		Folder sent = new Folder("Sent", true);
		Folder draft = new Folder("Draft", true);
		Folder trash = new Folder("Trash", true);
		Folder newInbox = createFolder(inbox, user.getUserId());
		Folder newSent = createFolder(sent, user.getUserId());
		Folder newDraft = createFolder(draft, user.getUserId());
		Folder newTrash = createFolder(trash, user.getUserId());
	}
}

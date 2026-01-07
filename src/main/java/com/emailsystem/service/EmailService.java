package main.java.com.emailsystem.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import main.java.com.emailsystem.dao.*;
import main.java.com.emailsystem.exception.EmailAddressAlreadyExistException;
import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.Folder;
import main.java.com.emailsystem.model.User;

public class EmailService {
	public EmailsDAO emailsDAO = new EmailsDAOImpl();
	public FoldersDAO foldersDAO = new FoldersDAOImpl();
	public EmailRecipientRelationsDAO emailRecipientRelationsDAO = new EmailRecipientRelationsDAOImpl();
	public FolderEmailRelationsDAO folderEmailRelationsDAO = new FolderEmailRelationsDAOImpl();
	public FilterService filterService = new FilterService();
	
	//creating an email
	public Email createEmail(Email email, List<User> recipients) throws SQLException, EmailAddressAlreadyExistException {
		Email newEmail = emailsDAO.save(email);
		for(User recipient: recipients) {
			emailRecipientRelationsDAO.createRelation(newEmail.getEmailId(), recipient.getUserId());
		}
		return email;
	}
	
	//permanently deleting an email
	public boolean permanentlyDeleteEmail(Folder folder, Email email) throws SQLException {
		return folderEmailRelationsDAO.deleteFolderEmailRelationByEmailIdAndFolderId(folder.getFolderId(), email.getEmailId());
	}
	
	public void restoreEmail(Folder folder, Email email) throws SQLException{
		Folder previousFolder = folderEmailRelationsDAO.getPreviouseFolder(folder.getFolderId(), email.getEmailId());
		folderEmailRelationsDAO.updateFolderEmailRelation(previousFolder.getFolderId(), email.getEmailId(), folder.getFolderId());
	}
	
	//get an email object by emailId
	public Email getEmail(int emailId) throws SQLException {
		return emailsDAO.get(emailId);
	}
	
	//update an email 
	public void updateEmail(Email email, int emailId) throws SQLException {
		emailsDAO.update(email, emailId);
	}
	
	//send Email to all the recipients(Map the email to the recipients Inbox folder Id and to the sender Sent folder Id)
	public void sendEmail(Email email, List<User> recipients) throws SQLException {
		email.setSentDate(LocalDateTime.now());
		emailsDAO.update(email, email.getEmailId());
		
		Folder sent = foldersDAO.getFolderByFolderNameAndUserId("Sent", email.getSenderId());
		folderEmailRelationsDAO.createRelation(sent.getFolderId(), email.getEmailId());
		
		for(User recipient : recipients) {
			Folder folder = filterService.applyFilter(email, recipient);
			folderEmailRelationsDAO.createRelation(folder.getFolderId(), email.getEmailId());
		}
	}
	
	public void moveEmail(Folder srcFolder, Folder desFolder, List<Email> emails) throws SQLException {
		for(Email email: emails) {
			folderEmailRelationsDAO.updateFolderEmailRelation(desFolder.getFolderId(), email.getEmailId(), srcFolder.getFolderId());
		}
	}
	
	public void sendDraftEmail(Folder draft, Email email, List<User> recipients) throws SQLException {
		folderEmailRelationsDAO.deleteFolderEmailRelationByEmailIdAndFolderId(draft.getFolderId(), email.getEmailId());
		sendEmail(email, recipients);
	}
	
	//draft an email(Map the email to the draft folder id)
	public void draftEmail(Email email) throws SQLException {
		Folder draft = foldersDAO.getFolderByFolderNameAndUserId("Draft", email.getSenderId());
		folderEmailRelationsDAO.createRelation(draft.getFolderId(), email.getEmailId());
	}
	
	//trash an email(Map the email to the trash folder id and unmap the email to the previous folder Id)
	public void trashEmail(User user,Folder folder, Email email) throws SQLException {
		Folder trash = foldersDAO.getFolderByFolderNameAndUserId("Trash", user.getUserId());
		folderEmailRelationsDAO.updateFolderEmailRelation(trash.getFolderId(), email.getEmailId(), folder.getFolderId());
	}
	
	public void trashMultipleEmails(User user, Folder folder, List<Email> emails) throws SQLException {
		for(Email email: emails) {
			trashEmail(user, folder, email);
		}
	}
	//
	public List<User> getEmailRecipients(Email email) throws SQLException{
		return emailRecipientRelationsDAO.getRelationObjects(email.getEmailId());
	}
	
	public String getFolderNameByUserAndEmail(User user, Email email) throws SQLException {
		Folder folder = folderEmailRelationsDAO.getFolderByEmailIdAndUserId(user.getUserId(), email.getEmailId());
		return folder.getFolderName();
	}
}

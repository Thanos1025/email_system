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
	public EmailCarbonCopyRelationsDAO emailCarbonCopyRelationsDAO = new EmailCarbonCopyRelationsDAOImpl();
	public EmailSubEmailRelationsDAO emailSubEmailRelationsDAO = new EmailSubEmailRelationsDAOImpl();
	public FolderEmailRelationsDAO folderEmailRelationsDAO = new FolderEmailRelationsDAOImpl();
	public FilterService filterService = new FilterService();
	
	//creating an email
	public Email createEmail(Email email, List<User> recipients, List<User> ccUsers) throws SQLException, EmailAddressAlreadyExistException {
		Email newEmail = emailsDAO.save(email);
		for(User recipient: recipients) {
			emailRecipientRelationsDAO.createRelation(newEmail.getEmailId(), recipient.getUserId());
		}
		for(User cc: ccUsers) {
			emailCarbonCopyRelationsDAO.createRelation(newEmail.getEmailId(), cc.getUserId());
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
	public void sendEmail(Email email, List<User> recipients, List<User> ccs) throws SQLException {
		email.setSentDate(LocalDateTime.now());
		emailsDAO.update(email, email.getEmailId());
		
		Folder sent = foldersDAO.getFolderByFolderNameAndUserId("Sent", email.getSenderId());
		folderEmailRelationsDAO.createRelation(sent.getFolderId(), email.getEmailId());
		
		for(User recipient : recipients) {
			Folder folder = filterService.applyFilter(email, recipient);
			folderEmailRelationsDAO.createRelation(folder.getFolderId(), email.getEmailId());
		}
		
		for(User cc: ccs) {
			Folder folder = filterService.applyFilter(email, cc);
			folderEmailRelationsDAO.createRelation(folder.getFolderId(), email.getEmailId());
		}
	}
	
	public void sendSubEmail(Email parentEmail, Email subEmail, List<User> recipients, List<User> ccs) throws SQLException {
		subEmail.setSentDate(LocalDateTime.now());
		emailsDAO.update(subEmail, subEmail.getEmailId());
		emailSubEmailRelationsDAO.createRelation(parentEmail.getEmailId(), subEmail.getEmailId(), subEmail.getSenderId());
		List<User> parentEmailRecipients = getEmailRecipients(parentEmail);
		List<User> parentEmailCCs = getEmailCCs(parentEmail);
		for(User recipient : recipients) {
			if(parentEmailRecipients.contains(recipient) || parentEmailCCs.contains(recipient)) {
				emailSubEmailRelationsDAO.createRelation(parentEmail.getEmailId(), subEmail.getEmailId(), recipient.getUserId());
			}else {
				Folder folder = filterService.applyFilter(subEmail, recipient);
				folderEmailRelationsDAO.createRelation(folder.getFolderId(), subEmail.getEmailId());
			}
		}
		for(User cc: ccs) {
			if(parentEmailRecipients.contains(cc) || parentEmailCCs.contains(cc)) {
				emailSubEmailRelationsDAO.createRelation(parentEmail.getEmailId(), subEmail.getEmailId(), cc.getUserId());
			}else {
				Folder folder = filterService.applyFilter(subEmail, cc);
				folderEmailRelationsDAO.createRelation(folder.getFolderId(), subEmail.getEmailId());
			}
		}
	}
	
	public void moveEmail(Folder srcFolder, Folder desFolder, List<Email> emails) throws SQLException {
		for(Email email: emails) {
			folderEmailRelationsDAO.updateFolderEmailRelation(desFolder.getFolderId(), email.getEmailId(), srcFolder.getFolderId());
		}
	}
	
	public void sendDraftEmail(Folder draft, Email email, List<User> recipients, List<User> ccs) throws SQLException {
		folderEmailRelationsDAO.deleteFolderEmailRelationByEmailIdAndFolderId(draft.getFolderId(), email.getEmailId());
		sendEmail(email, recipients, ccs);
	}
	
	//draft an email(Map the email to the draft folder id)
	public void draftEmail(Email email) throws SQLException {
		Folder draft = foldersDAO.getFolderByFolderNameAndUserId("Draft", email.getSenderId());
		folderEmailRelationsDAO.createRelation(draft.getFolderId(), email.getEmailId());
	}
	
	public void draftSubEmail(Email parentEmail, Email subEmail, User user) throws SQLException {
		emailSubEmailRelationsDAO.createRelation(parentEmail.getEmailId(), subEmail.getEmailId(), user.getUserId());
	}
	
	//trash an email(Map the email to the trash folder id and unmap the email to the previous folder Id)
	public void trashEmail(User user,Folder folder, Email email) throws SQLException {
		Folder trash = foldersDAO.getFolderByFolderNameAndUserId("Trash", user.getUserId());
		folderEmailRelationsDAO.updateFolderEmailRelation(trash.getFolderId(), email.getEmailId(), folder.getFolderId());
	}
	
	public boolean deleteSubEmail(Email parentEmail, Email subEmail, User user) throws SQLException {
		return emailSubEmailRelationsDAO.deleteRelation(parentEmail.getEmailId(), subEmail.getEmailId(), user.getUserId());
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
	
	public List<User> getEmailCCs(Email email) throws SQLException{
		return emailCarbonCopyRelationsDAO.getRelationObjects(email.getEmailId());
	}
	
	public List<Email> getSubEmailsByEmail(Email email, User user) throws SQLException{
		return emailSubEmailRelationsDAO.getRelationObjects(email.getEmailId(), user.getUserId());
	}
	
	public String getFolderNameByUserAndEmail(User user, Email email) throws SQLException {
		Folder folder = folderEmailRelationsDAO.getFolderByEmailIdAndUserId(user.getUserId(), email.getEmailId());
		return folder.getFolderName();
	}
}

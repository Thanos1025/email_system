package main.java.com.emailsystem.fileSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import main.java.com.emailsystem.controller.ServerAndClientHandler;
import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.Folder;
import main.java.com.emailsystem.model.User;
import main.java.com.emailsystem.server.MailServer;

public class Writer {
	public static File usersFile = new File("../database/users.csv");
	public static File emailsFile = new File("../database/emails.csv");
	public static File usersFoldersRelaionFile = new File("../database/usersFolders.csv");
	public static File foldersFile = new File("../database/folders.csv");
	public static File serverFile = new File("../database/server.csv");
	
	public static void writeFiles() {
		try {
    		if(usersFile.createNewFile()) {
    			System.out.println("Users File Created");
    		}
    		else {
    			System.out.println("Users File already exist");
    		}
    		
    		if(emailsFile.createNewFile()) {
    			System.out.println("Emails File Created");
    		}
    		else {
    			System.out.println("Emails File already exist");
    		}
    		
    		if(foldersFile.createNewFile()) {
    			System.out.println("Folders File Created");
    		}
    		else {
    			System.out.println("Folders File already exist");
    		}

    		if(usersFoldersRelaionFile.createNewFile()) {
    			System.out.println("Folders File Created");
    		}
    		else {
    			System.out.println("Folders File already exist");
    		}

			FileWriter usersFileWriter = new FileWriter(usersFile); 
			FileWriter emailsFileWriter = new FileWriter(emailsFile); 
			FileWriter foldersFileWriter = new FileWriter(foldersFile);
			FileWriter usersFoldersRelaionWriter = new FileWriter(usersFoldersRelaionFile);
    		
    		for(MailServer server: ServerAndClientHandler.servers.values()) {
    			usersFileWriter.write("userId, Name, EmailAddress, passwordHash");
    			usersFoldersRelaionWriter.write("folderId, userId");
    			foldersFileWriter.write("folderId, userId, folderName, isDefaultFolder, emailId, previousFolderId");
    			emailsFileWriter.write("emailId, subject, body, senderId, recipientId, repliedEmailId");
    			for(User user : server.getUsers().values()) {
    				usersFileWriter.write("\n"+user.getUserId()+", "+user.getUserName()+", "+user.getEmailAddress()+", "+user.getPassword());
    				List<Integer> folderIds = user.getFolderIds();
    				for(int folderId: folderIds) {
    					usersFoldersRelaionWriter.write("\n"+user.getUserId()+", "+folderId);
    				}
    			}
    			for(Folder folder: server.getFolders().values()) {
    				for(int emailId: folder.getEmailIds()) {
    					int index = folder.getEmailIdIndex(emailId);
    					foldersFileWriter.write("\n"+folder.getFolderId()+", "+folder.getUserId()+", "+folder.getFolderName()+", "+folder.getIsDefaultFolder()+", "+emailId+", "+folder.getPreviousFolderId(index));
    				}
    			}
    			for(Email email: server.getEmails().values()) {
    				
    			}
    		}
    		usersFileWriter.close();
    		emailsFileWriter.close();
    		foldersFileWriter.close();
    		usersFoldersRelaionWriter.close();
		}catch(IOException e) {
			System.out.println("Error ");
			e.printStackTrace();
		
		}
	}
}

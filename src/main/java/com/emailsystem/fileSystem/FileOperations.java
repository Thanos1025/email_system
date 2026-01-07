package main.java.com.emailsystem.fileSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import com.opencsv.CSVWriter;

import main.java.com.emailsystem.controller.ServerAndClientHandler;
import main.java.com.emailsystem.enumpackage.FileType;
import main.java.com.emailsystem.enumpackage.Server;
import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.Folder;
import main.java.com.emailsystem.model.User;
import main.java.com.emailsystem.server.MailServer;

public class FileOperations {

	public static File usersFile = new File(FileType.USERS.getPath());
	public static File usersFoldersRelaionFile = new File(FileType.USERFOLSERRELATION.getPath());
	public static File foldersFile = new File(FileType.FOLDERS.getPath());
	public static File foldersEmailsRelationFile = new File(FileType.FOLDEREMAILRELATION.getPath());
	public static File emailsFile = new File(FileType.EMAILS.getPath());
	public static File emailsSubEmailsRelationFile = new File(FileType.EMAILSSUBEMAILSRELATION.getPath());
	public static File emailsRecipientsRelationFile = new File(FileType.EMAILSRECIPIENTSRELATION.getPath());

	public static void createFiles() {
		try {
			if(usersFile.createNewFile()) {
				System.out.println("UsersFile is created");
			}
			if(emailsFile.createNewFile()) {
				System.out.println("EmailsFile is created");
			}
			if(foldersFile.createNewFile()) {
				System.out.println("FoldersFile is created");
			}
			if(usersFoldersRelaionFile.createNewFile()) {
				System.out.println("usersFoldersRelaionFile is created");
			}
			if(emailsSubEmailsRelationFile.createNewFile()) {
				System.out.println("emailsSubEmailsRelationFile is created");
			}
			if (foldersEmailsRelationFile.createNewFile()) {
				System.out.println("foldersEmailsRelationFile is created");
			}
			if (emailsRecipientsRelationFile.createNewFile()) {
				System.out.println("emailsRecipientsRelationFile is created");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeAll() {
		UserFileOperations.writeUserFiles();
		FolderFileOperations.writeFolderFiles();
		EmailFileOperations.writeEmailFiles();
	}
	
	public static void readAll() {
		UserFileOperations.readUserFiles();
		FolderFileOperations.readFolderFiles();
		EmailFileOperations.readEmailFiles();
	}
}

package main.java.com.emailsystem.fileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import main.java.com.emailsystem.enumpackage.FileType;
import main.java.com.emailsystem.enumpackage.Server;
import main.java.com.emailsystem.model.User;

public class UserFileOperations {
	public static File usersFile = new File(FileType.USERS.getPath());
	public static File usersFoldersRelaionFile = new File(FileType.USERFOLSERRELATION.getPath());

	public static void writeUserFiles() {
		writeUsersFile();
		writeUserFolderRelationsFile();
	}
	
	public static void readUserFiles() {
		readUsersFile();
		readUserFolderRelationsFile();
	}
	
	public static void readUsersFile() {
		try {
			CSVReader userFileReader = new CSVReaderBuilder(new FileReader(usersFile)).withSkipLines(1).build();
			List<String[]> users = userFileReader.readAll();
			for(String[] user: users ) {
				Server.gmailServer.importUser(Integer.parseInt(user[0]), user[1], user[2], user[3]);
			}
		} catch (IOException | CsvException e) {
			e.printStackTrace();
		}
	}
	
	public static void readUserFolderRelationsFile() {
		try {
			CSVReader userFolderRelationFileReader = new CSVReaderBuilder(new FileReader(usersFoldersRelaionFile)).withSkipLines(1).build();
			List<String[]> relations = userFolderRelationFileReader.readAll();
			for(String[] relation: relations ) {
				int folderId = Integer.parseInt(relation[0]);
				int userId = Integer.parseInt(relation[1]);
				int folderIdInUserId = Integer.parseInt(relation[2]);
				User user = Server.gmailServer.getUser(userId);
				user.addFolder(folderId);
			}
		} catch (IOException | CsvException e) {
			e.printStackTrace();
		}
	}

	public static void writeUsersFile() {
		try {
			// --------------------------- CSVWriter --------------------------- //

			CSVWriter usersFileWriter = new CSVWriter(new FileWriter(usersFile));

			// --------------------------- COLUMNNAME --------------------------- //

			String[] users = { "userId", "userName", "emailAddress", "passwordHash" };

			usersFileWriter.writeNext(users);

			// --------------------------- ROWS --------------------------- //

			for (User user : Server.gmailServer.getUsers().values()) {
				String[] fileValuesinUser = { String.valueOf(user.getUserId()), user.getUserName(),
						user.getEmailAddress(), user.getPassword() };
				usersFileWriter.writeNext(fileValuesinUser, true);
			}
			usersFileWriter.close();
		} catch (IOException e) {
			System.out.println("Error ");
			e.printStackTrace();
		}
	}

	public static void writeUserFolderRelationsFile() {
		try {
			// --------------------------- CSVWriter --------------------------- //

			CSVWriter usersFoldersRelaionWriter = new CSVWriter(new FileWriter(usersFoldersRelaionFile));

			// --------------------------- COLUMNNAME --------------------------- //

			String[] userFolderRelation = { "folderId", "userId", "folderIdInUserId" };

			usersFoldersRelaionWriter.writeNext(userFolderRelation);

			// --------------------------- ROWS --------------------------- //

			for (User user : Server.gmailServer.getUsers().values()) {
				for (int folderId : user.getFolderIds()) {
					String[] fileValuesinUserFolderRelation = { String.valueOf(folderId),
							String.valueOf(user.getUserId()), String.valueOf(user.getFolderIds().indexOf(folderId)) };
					usersFoldersRelaionWriter.writeNext(fileValuesinUserFolderRelation, true);
				}

			}
			usersFoldersRelaionWriter.close();
		} catch (IOException e) {
			System.out.println("Error ");
			e.printStackTrace();

		}
	}
}

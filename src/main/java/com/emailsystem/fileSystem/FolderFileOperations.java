package main.java.com.emailsystem.fileSystem;

import java.io.File;
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
import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.Folder;

public class FolderFileOperations {
	public static File foldersFile = new File(FileType.FOLDERS.getPath());
	public static File foldersEmailsRelationFile = new File(FileType.FOLDEREMAILRELATION.getPath());
	
	public static void writeFolderFiles() {
		writeFoldersFile();
		writeFolderEmailRelationFile();
	}
	
	public static void readFolderFiles() {
		readFoldersFile();
		readFoldersEmailsRelationFile();
	}
	
	public static void readFoldersFile() {
		try {
			CSVReader foldersFileReader = new CSVReaderBuilder(new FileReader(foldersFile)).withSkipLines(1).build();
			List<String[]> folders = foldersFileReader.readAll();
			for(String[] folder: folders ) {
				Server.gmailServer.importFolder(Integer.parseInt(folder[0]), folder[1], Boolean.parseBoolean(folder[2]));
			}
		} catch (IOException | CsvException e) {
			e.printStackTrace();
		}
	}
	
	public static void readFoldersEmailsRelationFile() {
		try {
			CSVReader foldersEmailsRelationFileReader = new CSVReaderBuilder(new FileReader(foldersEmailsRelationFile)).withSkipLines(1).build();
			List<String[]> foldersEmails = foldersEmailsRelationFileReader.readAll();
			for(String[] folderEmail: foldersEmails ) {
				Folder folder = Server.gmailServer.getFolder(Integer.parseInt(folderEmail[0]));
				folder.addEmail(Integer.parseInt(folderEmail[1]), Integer.parseInt(folderEmail[2]));
			}
		} catch (IOException | CsvException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeFoldersFile() {
		try {			
			
			// --------------------------- CSVWriter --------------------------- //

			CSVWriter foldersFileWriter = new CSVWriter(new FileWriter(foldersFile));
			
			// --------------------------- COLUMNNAME --------------------------- //

			String[] folders = { "folderId", "folderName", "isDefaultFolder" };
			foldersFileWriter.writeNext(folders);
			
			// --------------------------- ROWS --------------------------- //

			for (Folder folder : Server.gmailServer.getFolders().values()) {
				String[] fileValuesinFolder = { String.valueOf(folder.getFolderId()), folder.getFolderName(),
						String.valueOf(folder.getIsDefaultFolder()) };
				foldersFileWriter.writeNext(fileValuesinFolder, true);
			}
			
			// --------------------------- CLOSE --------------------------- //

			foldersFileWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeFolderEmailRelationFile() {
		try {			
			
			// --------------------------- CSVWriter --------------------------- //

			CSVWriter foldersEmailsRelationWriter = new CSVWriter(new FileWriter(foldersEmailsRelationFile));
			
			// --------------------------- COLUMNNAME --------------------------- //

			String[] foldersEmails = { "folderId", "emailId", "previousFolderId" };
			foldersEmailsRelationWriter.writeNext(foldersEmails);
			
			// --------------------------- ROWS --------------------------- //

			for (Folder folder : Server.gmailServer.getFolders().values()) {
				for(int i=0; i<folder.getEmailIds().size(); i++) {
					String[] fileValuesinfolderEmailRelation = { String.valueOf(folder.getFolderId()) , String.valueOf(folder.getEmailIds().get(i)), String.valueOf(folder.getPreviousFolderId(i))};
					foldersEmailsRelationWriter.writeNext(fileValuesinfolderEmailRelation, true);
				}

			}
			// --------------------------- CLOSE --------------------------- //

			foldersEmailsRelationWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

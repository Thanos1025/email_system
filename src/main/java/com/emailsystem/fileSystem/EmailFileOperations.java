package main.java.com.emailsystem.fileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import main.java.com.emailsystem.enumpackage.FileType;
import main.java.com.emailsystem.enumpackage.Server;
import main.java.com.emailsystem.model.Email;

public class EmailFileOperations {
	public static File emailsFile = new File(FileType.EMAILS.getPath());
	public static File emailsSubEmailsRelationFile = new File(FileType.EMAILSSUBEMAILSRELATION.getPath());
	public static File emailsRecipientsRelationFile = new File(FileType.EMAILSRECIPIENTSRELATION.getPath());

	
	public static void writeEmailFiles() {
		writeEmailsFile();
		writeEmailsSubEmailsRelationFile();
		writeEmailsRecipientsRelationFile();
	}
	
	public static void readEmailFiles() {
		readEmailsFile();
		readEmailsSubEmailsRelationFile();
		readEmailsRecipientsRelationFile();
	}
	
	public static void readEmailsFile() {
		try {
			CSVReader emailsFileReader = new CSVReaderBuilder(new FileReader(emailsFile)).withSkipLines(1).build();
			List<String[]> emails = emailsFileReader.readAll();
			if(emails.isEmpty()) {
				return ;
			}
			for(String[] email: emails) {
				try {
					Server.gmailServer.importEmail(Integer.parseInt(email[0]), email[1], email[2], LocalDateTime.parse(email[3]), LocalDateTime.parse(email[4]), Boolean.parseBoolean(email[5]), Integer.parseInt(email[6]));
				}catch(NumberFormatException e) {
					
				}
			}
		} catch (IOException | CsvException e) {
			e.printStackTrace();
		}
	}
	
	public static void readEmailsSubEmailsRelationFile() {
		try {
			CSVReader emailsSubEmailsRelationReader = new CSVReaderBuilder(new FileReader(emailsSubEmailsRelationFile)).withSkipLines(1).build();
			List<String[]> emailsSubEmailsRelations = emailsSubEmailsRelationReader.readAll();
			if(emailsSubEmailsRelations.isEmpty()) {
				return ;
			}
			for(String[] emailsSubEmailsRelation: emailsSubEmailsRelations) {
				Server.gmailServer.mapEmailWithSubEmail(Integer.parseInt(emailsSubEmailsRelation[0]), Integer.parseInt(emailsSubEmailsRelation[1]));
			}
		} catch (IOException | CsvException e) {
			e.printStackTrace();
		}
	}
	
	public static void readEmailsRecipientsRelationFile() {
		try {
			CSVReader emailsRecipientsRelationReader = new CSVReaderBuilder(new FileReader(emailsRecipientsRelationFile)).withSkipLines(1).build();
			List<String[]> emailsRecipientsRelations = emailsRecipientsRelationReader.readAll();
			for(String[] emailsRecipientsRelation: emailsRecipientsRelations) {
				try {
					Server.gmailServer.mapEmailwithRecipient(Integer.parseInt(emailsRecipientsRelation[0]), Integer.parseInt(emailsRecipientsRelation[1]));
				}catch(NumberFormatException e) {
					
				}
			}
		} catch (IOException | CsvException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeEmailsFile() {
		try {			
			
			// --------------------------- CSVWriter --------------------------- //

			CSVWriter emailsFileWriter = new CSVWriter(new FileWriter(emailsFile));
			
			// --------------------------- COLUMNNAME --------------------------- //

			String[] emails = { "emailId", "subject", "body", "createDate", "sentDate", "isRead", "senderId" };
			emailsFileWriter.writeNext(emails);
			
			// --------------------------- ROWS --------------------------- //

			for (Email email : Server.gmailServer.getEmails().values()) {
				String[] fileValuesinEmail = { String.valueOf(email.getEmailId()), email.getSubject(), email.getBody(),
						email.getCreatedDate().toString(), email.getSentDate() == null ? "": email.getSentDate().toString(), email.getIsRead(),
						String.valueOf(email.getSenderId()) };
				emailsFileWriter.writeNext(fileValuesinEmail, true);
			}
			
			// --------------------------- CLOSE --------------------------- //

			emailsFileWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeEmailsSubEmailsRelationFile() {
		try {			
			
			// --------------------------- CSVWriter --------------------------- //

			CSVWriter emailsSubEmailsRelationWriter = new CSVWriter(new FileWriter(emailsSubEmailsRelationFile));
			
			// --------------------------- COLUMNNAME --------------------------- //

			String[] emailSubemail = { "emailId", "subEmailId" };
			emailsSubEmailsRelationWriter.writeNext(emailSubemail);
			
			// --------------------------- ROWS --------------------------- //

			for (Email email : Server.gmailServer.getEmails().values()) {
				for (int subEmailId : email.getSubEmailIds()) {
					String[] fileValuesinEmailSubEmailRelation = {String.valueOf(email.getEmailId()), String.valueOf(subEmailId)};
					emailsSubEmailsRelationWriter.writeNext(fileValuesinEmailSubEmailRelation, true);
				}
			}

			
			// --------------------------- CLOSE --------------------------- //

			emailsSubEmailsRelationWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeEmailsRecipientsRelationFile() {
		try {			
					
		// --------------------------- CSVWriter --------------------------- //
		
		CSVWriter emailsRecipientsRelationWriter = new CSVWriter(new FileWriter(emailsRecipientsRelationFile));
			
		// --------------------------- COLUMNNAME --------------------------- //
		
		String[] emailsRecipients = { "emailId", "recipientId" };
		emailsRecipientsRelationWriter.writeNext(emailsRecipients);
			
		// --------------------------- ROWS --------------------------- //
		
		for (Email email : Server.gmailServer.getEmails().values()) {
			for (int recipientId : email.getRecipientIds()) {
				String[] fileValuesinEmailRecipientRelation = { String.valueOf(email.getEmailId()),
						String.valueOf(recipientId) };
				emailsRecipientsRelationWriter.writeNext(fileValuesinEmailRecipientRelation, true);
			}
		}
		// --------------------------- CLOSE --------------------------- //
		
		emailsRecipientsRelationWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

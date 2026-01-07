package main.java.com.emailsystem.view;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.User;
import main.java.com.emailsystem.server.MailServer;
import main.java.com.emailsystem.client.MailClient;
import main.java.com.emailsystem.controller.ServerAndClientHandler;
import main.java.com.emailsystem.enumpackage.FolderType;
import main.java.com.emailsystem.enumpackage.Server;
import main.java.com.emailsystem.exception.InvalidUserException;
import main.java.com.emailsystem.exception.NoEmailAddressExistException;
import main.java.com.emailsystem.exception.NoEmailExistException;
import main.java.com.emailsystem.exception.NoUserException;
import main.java.com.emailsystem.exception.NothingInTheFolderException;
import main.java.com.emailsystem.fileSystem.Writer;
import main.java.com.emailsystem.util.Input;

public class ClientApp {
	static Input sc = new Input();
	static MailClient client;

	public static void signInOrsignUp(int serverId) {
		while (true) {
			client = ServerAndClientHandler.getClient(ServerAndClientHandler.createClients("client1", serverId));
			int userId;
			System.out.print("\nWould you want to \n1.sign in \n2.sign up \n3.exit");// this is going to the first step
																						// as
																						// we ask to sign in or sign up
			byte signChoice = sc.nextByte();

			switch (signChoice) {
			case 1:
				userId = signIn();// we catch the signed in user Id
				selectFolder(FolderType.INBOX.getIndex()+1);// this is our fourth step after login view
				break;
			case 2:
				userId = signUp();// we catch the signed up user Id
				selectFolder(FolderType.INBOX.getIndex()+1);// this is our fourth step after login view
				break;
			case 3:
				System.out.println("Exiting...");
//				return;
			default:
				System.out.println("Invalid choice");
				break;
			}
		}
	}

	public static int signUp() {// I used MailServer directly for sign up and this is the second step
		System.out.print("\nEnter your Name: ");
		String signUpName = sc.nextLine();
		System.out.print("Enter the email address: ");
		String emailAddress = sc.nextLine();
		try {
			if (client.emailExist(emailAddress)) {
				System.out.println("This user already exist");
				signUp();
			}
		} catch (NoEmailAddressExistException e) {

		}
		System.out.println("Enter your Password: ");
		String password = sc.nextLine();
		return client.addUser(signUpName, emailAddress, password);// this return signed up user Id
	}

	public static int signIn() {// I used MailServer directly for sign in and this is the second step
		System.out.print("\nEnter your email address: ");
		String emailAddress = sc.nextLine();
		System.out.print("Enter your Password: ");
		String password = sc.nextLine();
		int userId = 0;
		try {
			userId = client.login(emailAddress, password);
		} catch (InvalidUserException e) {
			System.out.println(e.getMessage());
			signIn();
		}
		return userId;// this return signed in user Id
	}
	
	public static void selectFolder(int folderId) {// In the afterLoggin view we are going ask would you want to
		// compose email or show email
		folderId--;
		while(true) {
			int userId = client.getLoggedInUserId();
			List <Integer> emailIds = new LinkedList<>();
			try {
				emailIds = client.syncEmails(folderId);
			} catch (NothingInTheFolderException e) {
				System.out.println(e.getMessage());
			}
			System.out.println("\nWould you want to \n1.Compose Email \n2.Change Folder \n3.Open Email \n4.Select Emails \n5.Exit");
			int choice = sc.nextInt();
			switch(choice) {
			case 1:
				composeEmail(userId);
				break;
			case 2:
				changeFolder();
				return;
			case 3:
				if(!emailIds.isEmpty()) {
					try {
						switch(folderId) {
						case 2->{
							int emailId = openEmail(emailIds, folderId);
							optionsWithDraftEmail(emailId, folderId);
							break;
						}
						case 3->{
							int emailId = openEmail(emailIds, folderId);
							optionsWithTrashEmail(emailId, folderId);
							break;
						}
						default->{
							int emailId = openEmail(emailIds, folderId);
							optionsWithEmail(emailId, folderId);
							break;
						}
					}
					} catch (NoEmailExistException e) {
						System.out.println(e.getMessage());
						break;
					}
				}
				break;
			case 4:
				break;
			case 5:
				Writer.writeFiles();
				return;
			}
		}
	}

	public static void composeEmail(int userId) {// In the composeEmail method we collect recipient email address,
													// subject and email which are necessary for email. user input
		List<String> recipientEmailAddresses = gettingRecipientEmailAddresses();
		if(recipientEmailAddresses == null) {
			return;
		}
		List<Integer> recipientEmailIds = new LinkedList<>();
		System.out.print("Enter the subject: ");
		String subject = sc.nextLine();
		System.out.print("Enter the body of the email: ");
		String body = sc.nextLine();
		int emailId = 0;
		try {
			recipientEmailIds = client.getAllRecipientIdsWithEmailAddresses(recipientEmailAddresses);
			emailId = client.composeEmail(recipientEmailIds, subject, body);// we create the mail temporarily
		} catch (NoUserException e) {
			System.out.println(e.getMessage());
			return;
		}
		sendOrdraftcheck(recipientEmailIds, emailId);// check whether we want to send or draft it
	}

	public static void sendOrdraftcheck(List<Integer> recipientIds, int emailId) {
		System.out.println("\nWould you want to \n1.Sent the email \n2.Draft the email \n3.Exit");
		int choice = sc.nextInt();
		switch (choice) {
		case 1:
			client.sendEmail(emailId, recipientIds);// sending the email to the recipient with the given recipientId
			break;
		case 2:
			client.draftEmail(emailId);// drafting the email
			break;
		case 3:
			client.deleteEmailwhileCompose(emailId);// delete the temporary email if the don't want to send or draft it
			return;
		default:
			System.out.println("Invalid Choice");
			sendOrdraftcheck(recipientIds, emailId);
			break;
		}
	}

	public static void changeFolder() {
		client.syncFolders();
		System.out.println("Enter the index of the folder to be open: ");
		int folderIndex = sc.nextInt();
		if(folderIndex>client.getFolders().size()) {
			System.out.println("No Such Folder exist");
		}
		selectFolder(folderIndex);
	}

	public static int openEmail(List<Integer> emailIds, int folderId) throws NoEmailExistException {
		System.out.println("Enter the index of the email to be open: ");
		int userEmailIndex = sc.nextInt();
		int emailIndex = 0 ;
		if(userEmailIndex<emailIds.size()+1 && userEmailIndex>0) {
			emailIndex = emailIds.get(userEmailIndex-1);
			
		}
		else {
			throw new NoEmailExistException("No such email exist");
		}
		client.showEmail(emailIndex, folderId);
		return emailIndex;
	}
	
	public static void optionsWithTrashEmail(int emailId,int folderId) {
		while(true) {
			System.out.println("\nWould you want to \n1.Restore Email \n2.Delete Email \n3.Exit");
			int choice = sc.nextInt();
			switch(choice) {
			case 1:
				client.restoreEmail(emailId);
				return;
			case 2:
				client.trashEmail(emailId, FolderType.TRASH.getIndex());
				return;
			case 3:
				return;
			default:
				System.out.println("Invalid Choice");
				break;
			}
		}
	}
	
	public static List<String> gettingRecipientEmailAddresses(List<String> autoEmailAddresses){
		Boolean continueEnterRecipientEmailAddress = true;
		List<String> recipientEmailAddresses = new LinkedList<>();
		if(autoEmailAddresses !=null) {
			for(String autoEmailAddress : autoEmailAddresses) {
				recipientEmailAddresses.add(autoEmailAddress);
			}
		}
		while(continueEnterRecipientEmailAddress) {
			System.out.print("\nEnter the recipient email address(If you want to exit type (exit) and to cancle type (cancel)): ");
			String emailAddress = sc.nextLine();
			if(emailAddress.equals("cancel")) {
				return null;
			}
			if(emailAddress.equals("exit")&& recipientEmailAddresses.size() == 0) {
				System.out.println("No recipient has been selected");
				continue;
			}
			if(emailAddress.equals("exit")) {
				continueEnterRecipientEmailAddress = false;
				continue;
			}
			try {
				if(!client.emailExist(emailAddress)) {
					System.out.println("No User with the email Address "+emailAddress+" exists");
					continue;
				}
			}catch(NoEmailAddressExistException e) {
				System.out.println(e.getMessage());
				continue;
			}
			recipientEmailAddresses.add(emailAddress);
		}
		return recipientEmailAddresses;
	}
	
	public static List<String> gettingRecipientEmailAddresses(){
		return gettingRecipientEmailAddresses(null);
	}
	
	public static void optionsWithEmail(int emailId, int folderId) {
		
		while(true) {
			System.out.println("\nWould you want to \n1.Reply \n2.Forward \n3.Delete \n4.Mark as Unread \n5.Exit");
			int choice = sc.nextInt();
			switch(choice) {
			case 1->{
				List<String> recipientEmailAddresses = gettingRecipientEmailAddresses();
				if(recipientEmailAddresses == null) {
					return;
				}
				List<Integer> recipientEmailIds = new LinkedList<>();
				System.out.print("Enter the subject: ");
				String subject = sc.nextLine();
				System.out.print("Enter the body of the email: ");
				String body = sc.nextLine();
				try {
					recipientEmailIds = client.getAllRecipientIdsWithEmailAddresses(recipientEmailAddresses);
					client.replyEmail(emailId,recipientEmailIds, subject, body);
				} catch (NoUserException e) {
					System.out.println(e.getMessage());
					return;
				}
				break;
			}
			case 2->{
				List<String> recipientEmailAddresses = gettingRecipientEmailAddresses();
				if(recipientEmailAddresses == null) {
					return;
				}
				List<Integer> recipientEmailIds = new LinkedList<>();
				try {
					recipientEmailIds = client.getAllRecipientIdsWithEmailAddresses(recipientEmailAddresses);
					client.forwardEmail(emailId, recipientEmailIds);
				} catch (NoUserException e) {
					System.out.println(e.getMessage());
					break;
				}
				break;
			}
			case 3->{
				System.out.println("Would you confirmly delete the email(y/n): ");
				String option = sc.nextLine();
				switch(option.toLowerCase()) {
				case "y":
					System.out.println("The email is deleted and moved to the Trash");
					client.trashEmail(emailId, folderId);
					return;
				case "n":
					System.out.println("The email is not deleted.");
					break;
				}
				break;
			}
			case 4->{
				break;
			}
			case 5->{
				return;
			}
			}
		}
	}
	
	public static void optionsWithDraftEmail(int emailId, int folderId) {
		while(true) {
			System.out.println("\nWould you want to \n1.Send \n2.Delete \n3.Exit");
			int choice = sc.nextInt();
			switch(choice) {
			case 1:
				client.sendDraftEmail(emailId);
				return;
			case 2:
				System.out.println("Would you confirmly delete the email(y/n): ");
				String option = sc.nextLine();
				switch(option.toLowerCase()) {
				case "y":
					System.out.println("The email is deleted and moved to the Trash");
					client.trashEmail(emailId, folderId);
					return;
				case "n":
					System.out.println("The email is not deleted.");
					break;
				}
				break;
			case 3:
				return;
			}
		}
	}

	public static void main(String[] args) throws InvalidUserException {
		int userId = Server.gmailServer.addUser("yogi", "yogi458@gmail.com", "mponyogi");
		Server.gmailServer.addUser("balaji", "calmstorm@gmail.com", "passwordBala");
		client = ServerAndClientHandler.getClient(ServerAndClientHandler.createClients("client1", Server.gmailServerId));
		signInOrsignUp(Server.gmailServerId);
//		while (true) {
//			System.out.println("Which domain would you want to use \n1.@gmail.com \n2.@zohocorp.com");
//			int domain = sc.nextInt();
//			switch (domain) {
//			case 1 -> {
//				signInOrsignUp(Server.gmailServerId);
//				break;
//			}
//			case 2 -> {
//				signInOrsignUp(Server.zohocorpServerId);
//				break;
//			}
//			default -> {
//				System.out.println("Invalid choice");
//			}
//			}
//		}
	}
}

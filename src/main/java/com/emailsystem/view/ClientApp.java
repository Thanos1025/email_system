package main.java.com.emailsystem.view;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import main.java.com.emailsystem.util.DateFormatter;
import main.java.com.emailsystem.enumpackage.ConditionOperator;
import main.java.com.emailsystem.enumpackage.ConditionPoint;
import main.java.com.emailsystem.enumpackage.ConditionType;
import main.java.com.emailsystem.exception.CantCreateFilterException;
import main.java.com.emailsystem.exception.CantDeleteDefaultFolderException;
import main.java.com.emailsystem.exception.EmailAddressAlreadyExistException;
import main.java.com.emailsystem.exception.EmailAlreadySelectedException;
import main.java.com.emailsystem.exception.InvalidUserException;
import main.java.com.emailsystem.exception.NoEmailAddressExistException;
import main.java.com.emailsystem.exception.NoEmailExistException;
import main.java.com.emailsystem.exception.NoUserException;
import main.java.com.emailsystem.exception.NothingInTheFolderException;
import main.java.com.emailsystem.exception.UserFolderRelationNotCreatedException;
import main.java.com.emailsystem.model.Condition;
import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.Filter;
import main.java.com.emailsystem.model.Folder;
import main.java.com.emailsystem.model.User;
import main.java.com.emailsystem.service.ConditionService;
import main.java.com.emailsystem.service.EmailService;
import main.java.com.emailsystem.service.FilterService;
import main.java.com.emailsystem.service.FolderService;
import main.java.com.emailsystem.service.UserService;
import main.java.com.emailsystem.util.Input;
import main.java.com.emailsystem.util.PrintTable;

public class ClientApp {
	static String separator = "|!()";
	static Input input = new Input();
	static PrintTable printTable = new PrintTable();
	static UserService userService = new UserService();
	static EmailService emailService = new EmailService();
	static FolderService folderService = new FolderService();
	static FilterService filterService = new FilterService();
	static ConditionService conditionService = new ConditionService();
	static DateFormatter dateformatter = new DateFormatter();

	static void signInOrSignUpPage() throws InvalidUserException {
		User user = null;
		List<String> headers = new LinkedList<>();
		List<String> rows = new LinkedList<>();
		rows.add("1. Signin");
		rows.add("2. Signup");
		rows.add("3. Login to the yogi458@gamil.com");
		printTable.printBox(headers, rows);
		int choice = input.nextInt("Enter your choice: ");
		switch (choice) {
		case 1 -> {
			String emailAddress = input.nextLine("Enter your email Address: ");
			String password = input.nextLine("Enter your password: ");
			try {
				if (userService.authenticateUser(emailAddress, password)) {
					user = userService.getUserByEmailAddress(emailAddress);
				} else {
					throw new InvalidUserException("Invalid Username or Password");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NoUserException e) {
				throw new InvalidUserException("Invalid Username or Password");
			}
			homepage(user);
		}
		case 2 -> {
			String userName = input.nextLine("Enter user name: ");
			String emailAddress = input.nextLine("Enter your email address(like example@gmail.com): ");
			while (!userService.emailAddressValidation(emailAddress)) {
				System.out.println("Please enter a valid email address");
				emailAddress = input.nextLine("Enter your email address: ");
			}
			String password = input.nextLine("Enter your password: ");
			try {
				user = userService.addUser(userName, emailAddress, password);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (EmailAddressAlreadyExistException e) {
				System.err.println(e.getMessage());
				return;
			}
			try {
				folderService.createDefaultFolders(user);
			} catch (SQLException | UserFolderRelationNotCreatedException e) {
				e.printStackTrace();
			} catch (EmailAddressAlreadyExistException e) {
				System.err.println(e.getMessage());
			}
			homepage(user);
		}
		case 3 -> {
			user = new User(1, "Ponyogi M", "yogi458@gmail.com", "mponyogi458");
			homepage(user);
		}
		default -> {
			System.out.println("Invalid Option...");
		}
		}
	}

	static void homepage(User user) {
		try {
			Folder inbox = folderService.getFolderByFolderNameAndUserId("Inbox", user.getUserId());
			homepage(user, inbox);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static void homepage(User user, Folder folder) {
//		Would you want to 
//		1.Compose Email 
//		2.Change Folder 
//		3.Open Email 
//		4.Select Emails 
//		5.Exit

//		"\nWould you want to \n1.Send \n2.Delete \n3.Exit"
//		"\nWould you want to \n1.Restore Email \n2.Delete Email \n3.Exit"
		List<String> welcomeRows = new LinkedList<>();
		welcomeRows.add("Welcome " + user.getUserName());
		printTable.printBox(new LinkedList<>(), welcomeRows);
		boolean continueLoop = true;
		while (continueLoop) {

			List<Email> emails = printEmailsByFolder(user, folder);

			List<String> headers = new LinkedList<>();
			List<String> rows = new LinkedList<>();

			headers.add("Options");
			rows.add("1. Compose Email");
			rows.add("2. Change Folder");
			rows.add("3. Open Email");
			rows.add("4. Select Emails");
			rows.add("5. Show Filters");
			rows.add("6. Create a Folder");
			rows.add("7. Apply a Filter");
			rows.add("8. Empty the Folder");
			rows.add("9. Delete the Folder");
			rows.add("10. My Profile");
			printTable.printBox(headers, rows);
			int option = input.nextInt("Enter your options: ");
			switch (option) {
			case 1 -> {
				List<User> recipients = getRecipients();
				List<User> ccUsers = getCCUsers();
				Email email = getEmailObject(user);
				try {
					email = emailService.createEmail(email, recipients, ccUsers);
				} catch (SQLException | EmailAddressAlreadyExistException e) {
					e.printStackTrace();
				}
				sendOrdraftEmailpage(user, folder, email, recipients, ccUsers);
			}
			case 2 -> {
				changeFolder(user);
				return;
			}
			case 3 -> {
				int emailIndex = input.nextInt("Enter the index of the email to be open: ");
				Email email = null;
				try {
					email = emails.get(emailIndex - 1);
				} catch (IndexOutOfBoundsException e) {
					System.err.println("The index is not inside your emails range!!");
					break;
				}
				switch (folder.getFolderName()) {
				case ("Draft") -> {
					optionsWithDraftEmail(user, folder, email);
				}
				case ("Trash") -> {
					optionsWithTrashEmail(user, folder, email);
				}
				default -> {
					optionsWithEmail(user, folder, email);
				}
				}
			}
			case 4 -> {
				List<Email> selectedEmails = new LinkedList<>();
				boolean selectingEmail = true;
				while (selectingEmail) {
					try {
						int emailId = input
								.nextInt("Enter the index of the email to be selected(type -1 for stop selecting): ");
						if (emailId != -1) {
							if (!selectedEmails.contains(emails.get(emailId - 1))) {
								selectedEmails.add(emails.get(emailId - 1));
							} else {
								throw new EmailAlreadySelectedException("The email is already selected!!");
							}
						} else {
							if (selectedEmails.isEmpty()) {
								throw new NoEmailExistException("No email is Selected yet!!");
							} else {
								selectingEmail = false;
							}
						}
					} catch (EmailAlreadySelectedException e) {
						System.err.println(e.getMessage());
					} catch (NoEmailExistException e) {
						System.err.println(e.getMessage());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				optionsWithMultipleEmails(user, folder, selectedEmails);
			}
			case 5 -> {
				showFilters(user);
			}
			case 6 -> {
				String folderName = input.nextLine("Enter the Folder name: ");
				Folder newFolder = new Folder(folderName, false);
				try {
					folderService.createFolder(newFolder, user.getUserId());
				} catch (SQLException | UserFolderRelationNotCreatedException | EmailAddressAlreadyExistException e) {
					// TODO Auto-generrated catch block
					e.printStackTrace();
				}
				homepage(user, newFolder);
				return;
			}
			case 7 -> {
				Filter filter = null;
				try {
					filter = createFilter(user);
				} catch (CantCreateFilterException e) {
					System.err.println(e.getMessage());
					break;
				}
				if (filter.getConditionType() == ConditionType.NOT.getIndex()) {
					break;
				}
				List<Condition> conditions = new LinkedList<>();
				conditions.add(createConditionsForFilter(filter));
				boolean addCondition = true;
				while (addCondition) {
					String addConditionOption = input
							.nextLine("Would you want to add more conditions to the filter(y/n): ");
					if (addConditionOption.toLowerCase().equals("y")) {
						conditions.add(createConditionsForFilter(filter));
					} else if (addConditionOption.toLowerCase().equals("n")) {
						addCondition = false;
					} else {
						System.err.println("Please enter a valid input!!");
					}
				}
			}
			case 8 -> {
				String deleteFolderOption = input
						.nextLine("Empty the " + folder.getFolderName() + " folder will delete all its email(y/n): ");
				if (deleteFolderOption.equals("y")) {
					try {
						List<Email> trashingEmails = folderService.getEmailsByFolderId(folder);
						for (Email trashingEmail : trashingEmails) {
							emailService.trashEmail(user, folder, trashingEmail);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (NothingInTheFolderException e) {
						System.out.println(e.getMessage());
					}
				}
				System.out.println("All emails from the " + folder.getFolderName() + " folder is delted");
			}
			case 9 -> {
				if (folder.getIsDefaultFolder()) {
					System.err.println("You can't delete default folder!!");
					break;
				}
				String deleteFolderOption = input
						.nextLine("Deleting " + folder.getFolderName() + " folder will delete all its email(y/n): ");
				if (deleteFolderOption.equals("y")) {
					try {
						List<Email> trashingEmails = folderService.getEmailsByFolderId(folder);
						for (Email trashingEmail : trashingEmails) {
							emailService.trashEmail(user, folder, trashingEmail);
						}
						folderService.deleteFolder(folder);
						Filter trashFolderfilter = filterService.getFilterByFolder(folder);
						filterService.deleteFilter(trashFolderfilter.getFilterId());
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (CantDeleteDefaultFolderException e) {
						System.err.println(e.getMessage());
						break;
					} catch (NothingInTheFolderException e) {
						System.err.println(e.getMessage());
					}
				}
				System.out.println(folder.getFolderName() + " folder is deleted...");
				homepage(user);
				return;
			}
			case 10 -> {
				if (myProfile(user)) {
					return;
				}
			}
			default -> {
				System.err.println("Invalid input");
			}
			}
		}
	}

	static boolean myProfile(User user) {
		while (true) {
			List<String> profileinfo = new LinkedList<>();
			profileinfo.add("UserName" + separator + user.getUserName());
			profileinfo.add("Email address" + separator + user.getEmailAddress());
			printTable.printBox(List.of("Profile"), profileinfo);

			List<String> headers = new LinkedList<>();
			List<String> rows = new LinkedList<>();
			headers.add("Options");
			rows.add("1. Change Username");
			rows.add("2. Change Password");
			rows.add("3. Log Out");
			rows.add("4. Exit Profile");
			printTable.printBox(headers, rows);

			int option = input.nextInt("Enter your option: ");
			switch (option) {
			case 1 -> {
				String userName = input.nextLine("Enter your new username: ");
				user.setUserName(userName);
				try {
					userService.updateUser(user, user.getUserId());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			case 2 -> {
				String oldpasswordHash = input.nextLine("Enter your old password: ");
				try {
					if (userService.authenticateUser(user.getEmailAddress(), oldpasswordHash)) {
						String newpasswordHash = input.nextLine("Enter your new password: ");
						user.setPassword(newpasswordHash);
						userService.updateUser(user, user.getUserId());
					} else {
						throw new InvalidUserException("Invalid Password");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (NoUserException e) {
					System.err.println(e.getMessage());
				} catch (InvalidUserException e) {
					e.printStackTrace();
				}
			}
			case 3 -> {
				return true;
			}
			case 4 -> {
				System.out.println("Exiting Profile...");
				return false;
			}
			default -> {
				System.out.println("Invalid input");
			}
			}
		}
	}

	static void showFilters(User user) {
		try {
			while (true) {
				List<Filter> filters = filterService.getFilterByUser(user);
				List<String> filterHeaders = new LinkedList<>();
				List<String> filterRows = new LinkedList<>();
				filterHeaders.add("Filters" + separator + "Filter applied Folder");
				int filterSize = 1;
				for (Filter filter : filters) {
					filterRows.add(filterSize++ + separator
							+ folderService.getFolder(filter.getFolderId()).getFolderName() + separator
							+ filter.getFilterName() + separator
							+ ConditionType.messageFromIndex(filter.getConditionType()) + separator
							+ "Condition_Count: " + conditionService.getConditionCountByFilterId(filter.getFilterId()));
				}
				if (filters.isEmpty()) {
					filterRows.add("There are no filters");
				}
				printTable.printBox(filterHeaders, filterRows);

				List<String> headers = new LinkedList<>();
				List<String> rows = new LinkedList<>();
				headers.add("Options with filter ");
				rows.add("1. Create a filter");
				rows.add("2. Open a filter");
				rows.add("3. Deleter a filter");
				rows.add("4. Exit");
				printTable.printBox(headers, rows);
				int option = input.nextInt("Enter your option: ");
				switch (option) {
				case 1 -> {
					Filter filter = null;
					try {
						filter = createFilter(user);
					} catch (CantCreateFilterException e) {
						System.err.println(e.getMessage());
						break;
					}
					List<Condition> conditions = new LinkedList<>();
					conditions.add(createConditionsForFilter(filter));
					boolean addCondition = true;
					while (addCondition) {
						String addConditionOption = input
								.nextLine("Would you want to add more conditions to the filter(y/n): ");
						if (addConditionOption.toLowerCase().equals("y")) {
							conditions.add(createConditionsForFilter(filter));
						} else if (addConditionOption.toLowerCase().equals("n")) {
							addCondition = false;
						} else {
							System.err.println("Please enter a valid input!!");
						}
					}
				}
				case 2 -> {
					if (!filters.isEmpty()) {
						int filterIndex = input.nextInt("Enter the index of the filter to be open: ");
						while (filterIndex > filters.size()) {
							System.err.println("Please enter the valid index!!");
							filterIndex = input.nextInt("Enter the index of the filter to be open: ");
						}
						Filter filter = filters.get(filterIndex - 1);
						openFilter(filter, user);
					} else {
						System.out.println("There is no filter");
					}
				}
				case 3 -> {
					if (!filters.isEmpty()) {
						int filterIndex = input.nextInt("Enter the index of the filter to be delete: ");
						Filter filter = filters.get(filterIndex);
						filterService.deleteFilter(filter.getFilterId());
						System.out.println(filter.getFilterName() + " filter is deleted...");
					} else {
						System.out.println("There is no filter");
					}
				}
				default -> {
					System.out.println("Exiting filter option...");
					return;
				}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void openFilter(Filter filter, User user) {
		try {
			while (true) {
				List<Condition> conditions = conditionService.getConditionByFilter(filter);
				List<String> conditionHeaders = new LinkedList<>();
				List<String> conditionRows = new LinkedList<>();
				conditionHeaders.add("Sno" + separator + "Conditons in the filter " + filter.getFilterName() + separator
						+ ConditionType.messageFromIndex(filter.getConditionType()) + separator + "Folder Name-"
						+ folderService.getFolder(filter.getFolderId()).getFolderName());
				int conditionSize = 1;
				if (!conditions.isEmpty()) {
					for (Condition condition : conditions) {
						conditionRows.add(conditionSize++ + separator
								+ ConditionPoint.messageFromIndex(condition.getConditionPoint()) + separator
								+ ConditionOperator.messageFromIndex(condition.getConditionOperator()) + separator
								+ condition.getConditionString());
					}
				} else {
					conditionRows.add("There are no condions in the " + filter.getFilterName() + " filter");
				}
				printTable.printBox(conditionHeaders, conditionRows);
				List<String> headers = new LinkedList<>();
				List<String> rows = new LinkedList<>();
				if (filter.getConditionType() != ConditionType.NOT.getIndex()) {
					headers.add("Filter-" + filter.getFilterName());
					rows.add("1. Edit filter Name");
					rows.add("2. Edit filter condition Type");
					rows.add("3. Edit filter applied folder: ");
					rows.add("4. Add a Condition");
					rows.add("5. Delete a Condition");
					rows.add("6. Edit a Condition");
					rows.add("7. Exit");
					printTable.printBox(headers, rows);
					int option = input.nextInt("Enter your option: ");
					switch (option) {
					case 1 -> {
						String filterName = input.nextLine("Enter new filter name: ");
						filter.setFilterName(filterName);
						filterService.updateFilter(filter, filter.getFilterId());
						System.out.println("Filter Name updated...");
					}
					case 2 -> {
						List<String> conditionTypeHeaders = new LinkedList<>();
						List<String> conditionTypeRows = new LinkedList<>();
						conditionTypeHeaders.add("Condtion Type");
						List<ConditionType> conditionTypes = ConditionType.getAll();
						for (ConditionType conditionType : conditionTypes) {
							conditionTypeRows.add(conditionType.getIndex() + 1 + ". " + conditionType.getMessage());
						}
						printTable.printBox(conditionTypeHeaders, conditionTypeRows);
						int conditionType = input.nextInt("Enter the index of condition type of the filer: ");
						while (conditionType < 0 && conditionType > conditionTypes.size()) {
							System.err.println("Please enter the valid type!!");
							conditionType = input.nextInt("Enter the index of condition type of the filer: ");
						}
						filter.setConditionType(conditionType - 1);
						filterService.updateFilter(filter, filter.getFilterId());
						System.out.println("Filter Condition type is updated...");
					}
					case 3 -> {
						List<String> folderheaders = new LinkedList<>();
						List<String> folderrows = new LinkedList<>();
						List<Folder> allfolders = userService.getFoldersByUserId(user.getUserId());
						List<Folder> folders = new LinkedList<>();
						for (Folder folder : allfolders) {
							if (!folder.getIsDefaultFolder()) {
								folders.add(folder);
							}
						}
						folderheaders.add("Folders");
						int count = 1;
						for (Folder folder : folders) {
							folderrows.add((count++) + ". " + folder.getFolderName());
						}
						printTable.printBox(folderheaders, folderrows);
						int folderIndex = input
								.nextInt("Enter the index of the folder to whicht the filter is apply: ");

						while (folderIndex > folders.size() || folderIndex <= 0) {
							System.err.println("Please enter a valid folder index ");
							folderIndex = input
									.nextInt("Enter the index of the foldere to whicht the filter is apply: ");
						}
						int folderId = folders.get(folderIndex - 1).getFolderId();
						filter.setFolderId(folderId);
						filterService.updateFilter(filter, filter.getFilterId());
						System.out.println("Filter's folder is updated...");
					}
					case 4 -> {
						conditions.add(createConditionsForFilter(filter));
					}
					case 5 -> {
						int deleteConditionIndex = input.nextInt("Enter the index of the condtion to be delete: ");
						conditionService.deleteCondition(conditions.get(deleteConditionIndex - 1).getConditionId());
					}
					case 6 -> {
						int editConditionIndex = input.nextInt("Enter the index of the condition to be edited: ");
						Condition condition = updateCondition(filter);
						conditionService.updateCondition(condition,
								conditions.get(editConditionIndex - 1).getConditionId());
						System.out.println("Condition is updated successfully...");
					}
					case 7 -> {
						System.out.println("Exiting " + filter.getFilterName() + " filter...");
						return;
					}
					default -> {
						System.out.println("Invalid input!!");
					}
					}
				} else {
					headers.add("Filter-" + filter.getFilterName());
					rows.add("1. Edit filter Name");
					rows.add("2. Edit filter condition Type");
					rows.add("3. Edit filter applied folder");
					rows.add("4. Exit");
					printTable.printBox(headers, rows);
					int option = input.nextInt("Enter your option: ");
					switch (option) {
					case 1 -> {
						String filterName = input.nextLine("Enter new filter name: ");
						filter.setFilterName(filterName);
						filterService.updateFilter(filter, filter.getFilterId());
						System.out.println("Filter Name updated...");
					}
					case 2 -> {
						List<String> conditionTypeHeaders = new LinkedList<>();
						List<String> conditionTypeRows = new LinkedList<>();
						conditionTypeHeaders.add("Condtion Type");
						List<ConditionType> conditionTypes = ConditionType.getAll();
						for (ConditionType conditionType : conditionTypes) {
							conditionTypeRows.add(conditionType.getIndex() + 1 + ". " + conditionType.getMessage());
						}
						printTable.printBox(conditionTypeHeaders, conditionTypeRows);
						int conditionType = input.nextInt("Enter the index of condition type of the filer: ");
						while (conditionType < 0 && conditionType > conditionTypes.size()) {
							System.err.println("Please enter the valid type!!");
							conditionType = input.nextInt("Enter the index of condition type of the filer: ");
						}
						filter.setConditionType(conditionType - 1);
						filterService.updateFilter(filter, filter.getFilterId());
						System.out.println("Filter Condition type is updated...");
					}
					case 3 -> {
						List<String> folderheaders = new LinkedList<>();
						List<String> folderrows = new LinkedList<>();
						List<Folder> allfolders = userService.getFoldersByUserId(user.getUserId());
						List<Folder> folders = new LinkedList<>();
						for (Folder folder : allfolders) {
							if (!folder.getIsDefaultFolder()) {
								folders.add(folder);
							}
						}
						folderheaders.add("Folders");
						int count = 1;
						for (Folder folder : folders) {
							folderrows.add((count++) + ". " + folder.getFolderName());
						}
						printTable.printBox(folderheaders, folderrows);
						int folderIndex = input
								.nextInt("Enter the index of the folder to whicht the filter is apply: ");
						while (folderIndex > folders.size() || folderIndex <= 0) {
							System.err.println("Please enter a valid folder index ");
							folderIndex = input
									.nextInt("Enter the index of the foldere to whicht the filter is apply: ");
						}
						int folderId = folders.get(folderIndex - 1).getFolderId();
						filter.setFolderId(folderId);
						filterService.updateFilter(filter, filter.getFilterId());
						System.out.println("Filter's folder is updated...");
					}
					case 4 -> {
						System.out.println("Exiting " + filter.getFilterName() + " filter...");
						return;
					}
					default -> {
						System.out.println("Invalid input!!");
					}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Options with the folder
	static void folderOptions(User user, Folder folder) {
		while (true) {
			List<String> headers = new LinkedList<>();
			List<String> rows = new LinkedList<>();
			headers.add("Folder Options");
			rows.add("1. Create a Filter");
			rows.add("2. Delete the Folder");
			rows.add("3. Empty the Folder");
			rows.add("4. Exit folder options");
			printTable.printBox(headers, rows);
			int option = input.nextInt("Enter your Option: ");
			switch (option) {
			case 1 -> {
				Filter filter = null;
				try {
					filter = createFilter(user);
				} catch (CantCreateFilterException e) {
					System.err.println(e.getMessage());
					break;
				}
				if (filter.getConditionType() == ConditionType.NOT.getIndex()) {
					break;
				}
				List<Condition> conditions = new LinkedList<>();
				conditions.add(createConditionsForFilter(filter));
				boolean addCondition = true;
				while (addCondition) {
					String addConditionOption = input
							.nextLine("Would you want to add more conditions to the filter(y/n): ");
					if (addConditionOption.toLowerCase().equals("y")) {
						conditions.add(createConditionsForFilter(filter));
					} else if (addConditionOption.toLowerCase().equals("n")) {
						addCondition = false;
					} else {
						System.err.println("Please enter a valid input!!");
					}
				}
			}
			case 2 -> {
				if (folder.getIsDefaultFolder()) {
					System.err.println("You can't delete default folder!!");
					break;
				}
				String deleteFolderOption = input
						.nextLine("Deleting " + folder.getFolderName() + " folder will delete all its email(y/n): ");
				if (deleteFolderOption.equals("y")) {
					try {
						List<Email> trashingEmails = folderService.getEmailsByFolderId(folder);
						for (Email trashingEmail : trashingEmails) {
							emailService.trashEmail(user, folder, trashingEmail);
						}
						folderService.deleteFolder(folder);
						Filter trashFolderfilter = filterService.getFilterByFolder(folder);
						filterService.deleteFilter(trashFolderfilter.getFilterId());
					} catch (SQLException | NothingInTheFolderException | CantDeleteDefaultFolderException e) {
						e.printStackTrace();
					}
				}
				System.out.println(folder.getFolderName() + " folder is deleted...");
				return;
			}
			case 3 -> {
				String deleteFolderOption = input
						.nextLine("Empty the " + folder.getFolderName() + " folder will delete all its email(y/n): ");
				if (deleteFolderOption.equals("y")) {
					try {
						folderService.deleteFolder(folder);
						List<Email> trashingEmails = folderService.getEmailsByFolderId(folder);
						for (Email trashingEmail : trashingEmails) {
							emailService.trashEmail(user, folder, trashingEmail);
						}
					} catch (SQLException | NothingInTheFolderException e) {
						e.printStackTrace();
					} catch (CantDeleteDefaultFolderException e) {
						System.err.println(e.getMessage());
					}
				}
				System.out.println("All emails from the " + folder.getFolderName() + " folder is delted");
			}
			case 4 -> {
				System.out.println("Exiting folder Option");
				return;
			}
			default -> {
				System.out.println("Invalid input!!");
			}
			}
		}
	}

	static Condition updateCondition(Filter filter) {
		List<ConditionPoint> conditionPoints = ConditionPoint.getAll();
		List<ConditionOperator> conditionOperators = ConditionOperator.getAll();

		List<String> conditionPointHeaders = new LinkedList<>();
		List<String> conditionPointRows = new LinkedList<>();
		conditionPointHeaders.add("Condition Point");
		for (ConditionPoint obj : conditionPoints) {
			conditionPointRows.add((obj.getIndex() + 1) + ". " + obj.getMessage());
		}
		printTable.printBox(conditionPointHeaders, conditionPointRows);
		int conditionPoint = input.nextInt("Enter your condition's point: ");
		while (conditionPoint > conditionPoints.size() && conditionPoint <= 0) {
			System.err.println("Please enter a valid point!!");
			conditionPoint = input.nextInt("Enter your condition's point: ");
		}
		conditionPoint--;

		List<String> conditionOperatorHeaders = new LinkedList<>();
		List<String> conditionOperatorRows = new LinkedList<>();
		conditionOperatorHeaders.add("Condition Operator");
		for (ConditionOperator obj : conditionOperators) {
			conditionOperatorRows.add((obj.getIndex() + 1) + ". " + obj.getMessage());
		}
		printTable.printBox(conditionOperatorHeaders, conditionOperatorRows);
		int conditionOperator = input.nextInt("Enter your condition's operator: ");
		while (conditionOperator > conditionOperators.size() && conditionOperator <= 0) {
			System.err.println("Please enter a valid operator!!");
			conditionOperator = input.nextInt("Enter your condition's operator: ");
		}
		conditionOperator--;
		String conditionString = input.nextLine("Enter your condition's string: ");
		Condition condition = new Condition(conditionString, conditionOperator, conditionPoint, filter.getFilterId());
		return condition;
	}

	static Condition createConditionsForFilter(Filter filter) {
		Condition condition = updateCondition(filter);
		try {
			condition = conditionService.createCondition(condition);
		} catch (SQLException | EmailAddressAlreadyExistException e) {
			e.printStackTrace();
		}
		return condition;
	}

	static Filter createFilter(User user) throws CantCreateFilterException {
		try {
			List<Folder> allfolders = userService.getFoldersByUserId(user.getUserId());
			List<Folder> folders = new LinkedList<>();
			for (Folder folder : allfolders) {
				if (!folder.getIsDefaultFolder()) {
					folders.add(folder);
				}
			}
			if (folders.isEmpty()) {
				throw new CantCreateFilterException("You can't create filter before creating a new folder...");
			}
			List<Integer> conditionTypeIndexes = new LinkedList<>();
			String filterName = input.nextLine("Enter the filter name: ");
			List<String> conditionTypeHeaders = new LinkedList<>();
			List<String> conditionTypeRows = new LinkedList<>();
			conditionTypeHeaders.add("Condition Type");
			for (ConditionType obj : ConditionType.getAll()) {
				conditionTypeRows.add((obj.getIndex() + 1) + ". " + obj.getMessage());
				conditionTypeIndexes.add(obj.getIndex());
			}
			printTable.printBox(conditionTypeHeaders, conditionTypeRows);
			int conditionType = input.nextInt("Enter your filter's condition type: ");
			conditionType--;
			System.out.println();
			while (!conditionTypeIndexes.contains(conditionType)) {
				System.out.println("Please enter a valid type!!");
				conditionType = input.nextInt("Enter your filter's condition type: ");
				conditionType--;
			}
			List<String> folderheaders = new LinkedList<>();
			List<String> folderrows = new LinkedList<>();
			folderheaders.add("Folders");
			int count = 1;
			for (Folder folder : folders) {
				folderrows.add((count++) + ". " + folder.getFolderName());
			}
			printTable.printBox(folderheaders, folderrows);
			int folderIndex = input.nextInt("Enter the index of the folder to whicht the filter is apply: ");
			while (folderIndex > folders.size() || folderIndex <= 0) {
				System.err.println("Please enter a valid folder index ");
				folderIndex = input.nextInt("Enter the index of the foldere to whicht the filter is apply: ");
			}
			int folderId = folders.get(folderIndex - 1).getFolderId();
			Filter filter = new Filter(filterName, conditionType, folderId);
			filter = filterService.createFilter(filter);
			return filter;
		} catch (SQLException | EmailAddressAlreadyExistException e) {
			e.printStackTrace();
		}
		return null;
	}

	// change the folder
	static void changeFolder(User user) {
		try {
			List<String> headers = new LinkedList<>();
			List<String> rows = new LinkedList<>();
			List<Folder> folders = userService.getFoldersByUserId(user.getUserId());
			headers.add("Folders");
			int count = 1;
			for (Folder folder : folders) {
				rows.add((count++) + ". " + folder.getFolderName());
			}
			printTable.printBox(headers, rows);
			int folderIndex = input.nextInt("Enter the index of the folder to be selected: ");
			while (folderIndex > folders.size()) {
				System.err.println("Please enter a valid folder index ");
				folderIndex = input.nextInt("Enter the index of the foldere to be selected: ");
			}
			homepage(user, folders.get(folderIndex - 1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static List<User> getRecipients() {
		List<User> recipients = new LinkedList<>();
		boolean askRecipientEmailAddress = true;
		while (askRecipientEmailAddress) {
			String recipientEmailAddress = input
					.nextLine("Enter the recipient email address(type 'quit' to complete selection): ");
			if (recipientEmailAddress.equals("quit")) {
				if (recipients.isEmpty()) {
					System.err.println("No recipient is selected yet!!");
				} else {
					askRecipientEmailAddress = false;
				}
			} else if (!recipientEmailAddress.isEmpty()) {
				try {
					User recipient = userService.getUserByEmailAddress(recipientEmailAddress);
					for (User recipient1 : recipients) {
						if (recipient1.getUserId() == recipient.getUserId()) {
							throw new EmailAddressAlreadyExistException(
									recipient.getEmailAddress() + " is already Selected !!");
						}
					}
					recipients.add(recipient);
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (NoUserException e) {
					System.err.println(e.getMessage());
				} catch (EmailAddressAlreadyExistException e) {
					System.err.println(e.getMessage());
				}
			}
		}
		return recipients;
	}

	static List<User> getCCUsers() {
		List<User> ccUsers = new LinkedList<>();
		boolean askCCUserEmailAddress = true;
		while (askCCUserEmailAddress) {
			String ccUserEmailAddress = input
					.nextLine("Enter the cc email address(type 'quit' to complete selection): ");
			if (ccUserEmailAddress.equals("quit")) {
				System.out.println("There are no cc is selected");
				askCCUserEmailAddress = false;
			} else if (!ccUserEmailAddress.isEmpty()) {
				try {
					User cc = userService.getUserByEmailAddress(ccUserEmailAddress);
					for (User user : ccUsers) {
						if (user.getUserId() == cc.getUserId()) {
							throw new EmailAddressAlreadyExistException(
									cc.getEmailAddress() + " is already Selected !!");
						}
					}
					ccUsers.add(cc);
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (NoUserException e) {
					System.err.println(e.getMessage());
				} catch (EmailAddressAlreadyExistException e) {
					System.err.println(e.getMessage());
				}
			}
		}
		return ccUsers;
	}

	static Email getEmailObject(User user) {
		String subject = input.nextLine("Enter the subject of your email: ");
		String body = input.nextLine("Enter the body of your email: ");
		Email createdEmail = new Email(subject, body, user.getUserId(), LocalDateTime.now());
		return createdEmail;
	}

	// options after composing the email
	static void sendOrdraftEmailpage(User user, Folder folder, Email email, List<User> recipients, List<User> ccs) {
		while (true) {
			List<String> headers = new LinkedList<>();
			List<String> rows = new LinkedList<>();

			headers.add("Options");
			rows.add("1. Send");
			rows.add("2. Draft");
			rows.add("3. Discard");

			printTable.printBox(headers, rows);

			int option = input.nextInt("Enter your option to continue: ");

			switch (option) {
			case 1 -> {
				try {
					emailService.sendEmail(email, recipients, ccs);
					System.out.println("Email is sented successfully...");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			case 2 -> {
				try {
					emailService.draftEmail(email);
					System.out.println("Email is drafted successfully...");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			case 3 -> {
				try {
					emailService.trashEmail(user, folder, email);
					System.out.println("Email is deleted...");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			default -> {
				System.out.println("Invalid Input");
				continue;
			}
			}
			return;
		}
	}

	static void sendOrdraftSubEmailpage(Email parentEmail, Email subEmail, User user, List<User> recipients, List<User> ccs) {
		while (true) {
			List<String> headers = new LinkedList<>();
			List<String> rows = new LinkedList<>();

			headers.add("Options");
			rows.add("1. Send");
			rows.add("2. Draft");
			rows.add("3. Discard");

			printTable.printBox(headers, rows);

			int option = input.nextInt("Enter your option to continue: ");

			switch (option) {
			case 1 -> {
				try {
					emailService.sendSubEmail(parentEmail, subEmail, recipients, ccs);
					System.out.println("Email is sented successfully...");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			case 2 -> {
				try {
					emailService.draftSubEmail(parentEmail, subEmail, user);
					System.out.println("Email is drafted successfully...");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			case 3 -> {
				System.out.println("Email is deleted...");
			}
			default -> {
				System.out.println("Invalid Input");
				continue;
			}
			}
			return;
		}
	}

	static void printEmail(Folder folder, Email email) throws SQLException {
		User sender = userService.getUserByUserId(email.getSenderId());
		String recipientsStr = getRecipientsStringByEmail(email);
		String ccsStr = getCCsStringByEmail(email);
		List<String> headers = new LinkedList<>();
		List<String> rows = new LinkedList<>();
		boolean isSentDateExist = email.getSentDate() != null;
		headers.add(isSentDateExist ? "Email" : "Draft Email" + separator + " ");
		rows.add("From: " + separator + sender.getUserName());
		rows.add("To: " + separator + recipientsStr);
		rows.add("CC: "+separator+ccsStr);
		rows.add("Subject: " + separator + email.getSubject());
		rows.add("Sent Date: " + separator
				+ (isSentDateExist ? (dateformatter.getLongDateFormat(email.getSentDate())) : "yet to sent"));
		rows.add("Is Read: " + separator + folderService.getStringIsRead(folder, email));

		printTable.printBox(headers, rows);

		List<String> bodyheaders = new LinkedList<>();
		List<String> bodyrows = new LinkedList<>();

		bodyheaders.add("Body");
		bodyrows.add(email.getBody());

		printTable.printBox(bodyheaders, bodyrows, true);
	}

	static String getRecipientsStringByEmail(Email email) throws SQLException {
		List<User> recipients = emailService.getEmailRecipients(email);
		List<String> recipientsName = new LinkedList<>();
		for (User recipient : recipients) {
			recipientsName.add(recipient.getUserName());
		}
		return String.join(",", recipientsName);
	}
	
	static String getCCsStringByEmail(Email email) throws SQLException {
		List<User> ccs = emailService.getEmailCCs(email);
		List<String> ccsName = new LinkedList<>();
		if(!ccs.isEmpty()) {
			for(User cc: ccs) {
				ccsName.add(cc.getUserName());
			}
		}else {
			ccsName.add("There are no ccs");
		}
		return String.join(",", ccsName);
	}

	// Options with opening the normalEmail
	static void optionsWithEmail(User user, Folder folder, Email email) {
		try {
			folderService.updateIsRead(folder, email, true);
			emailService.updateEmail(email, email.getEmailId());

			User sender = userService.getUserByUserId(email.getSenderId());
			String recipientsStr = getRecipientsStringByEmail(email);
			printEmail(folder, email);

			List<String> subEmailHeaders = new LinkedList<>();
			List<String> subEmailRows = new LinkedList<>();

			subEmailHeaders.add("SNo" + separator + "Sub Emails");
			List<Email> subEmails = emailService.getSubEmailsByEmail(email, user);
			Email lastSubEmail = email;
			if (!subEmails.isEmpty()) {
				for (Email subEmail : subEmails) {
					
					String subBody = subEmail.getSubject() + "-" + subEmail.getBody();
					subBody = subBody.length() > 40 ? subBody.substring(0, 40) + "..." : subBody;
					boolean isSentDateExist = subEmail.getSentDate() != null;

					// Sent Email
					if ((subEmail.getSenderId() == user.getUserId()) && isSentDateExist) {
						subEmailRows.add((subEmails.indexOf(subEmail) + 1) + separator + "To: "
								+getRecipientsStringByEmail(subEmail) + separator + subEmail.getSubject() + " - "
								+ subBody + separator + dateformatter.getShortDateFormat(subEmail.getSentDate())
								+ separator + folderService.getStringIsRead(folder, subEmail));
					}

					// Draft Email
					else if (email.getSenderId() == user.getUserId() && subEmail.getSentDate() == null) {
						subEmailRows.add((subEmails.indexOf(subEmail) + 1) + separator + "Draft" + separator
								+ email.getSubject() + " - " + subBody + separator + "yet to sent");
					}

					// Email
					else {
						User subEmailSender = userService.getUserByUserId(subEmail.getSenderId());
						subEmailRows.add((subEmails.indexOf(subEmail) + 1) + separator + subEmailSender.getUserName()
								+ separator + subEmail.getSubject() + " - " + subBody + separator
								+ dateformatter.getShortDateFormat(subEmail.getSentDate()) + separator
								+ folderService.getStringIsRead(folder, subEmail));
					}
				}
				printTable.printBox(subEmailHeaders, subEmailRows);
				if(!subEmails.isEmpty()) {
					lastSubEmail = subEmails.get(subEmails.size() - 1);
				}
			}
			

//		System.out.println("Body: \n\n"+email.getBody());
//		"\nWould you want to \n1.Reply \n2.Forward \n3.Delete \n4.Mark as Unread \n5.Exit"
			List<String> emailOptionHeaders = new LinkedList<>();
			List<String> emailOptionRows = new LinkedList<>();
			emailOptionHeaders.add("Options with Email");
			emailOptionRows.add("1. Reply");
			emailOptionRows.add("2. Forward");
			emailOptionRows.add("3. Delete");
			emailOptionRows.add(folderService.getIsRead(folder, email) ? "4. Mark as Unread" : "4. Mark as Read");
			emailOptionRows.add("5. Exit");

			printTable.printBox(emailOptionHeaders, emailOptionRows);
			int option = input.nextInt("Enter your option: ");
			switch (option) {
			case 1 -> {
				if (lastSubEmail.getSentDate() == null) {
					int sentOrDiscard = input.nextInt("Do you want to \n 1.Sent Or 2.Discard the last draft email");
					switch(sentOrDiscard) {
					case 1->{
						emailService.sendSubEmail(email, lastSubEmail, emailService.getEmailRecipients(lastSubEmail), emailService.getEmailCCs(lastSubEmail));
						System.out.println("The last email is sented successfully...");
					}
					case 2->{
						emailService.deleteSubEmail(email, lastSubEmail, user);
						System.out.println("Email is deleted...");
					}
					}
				}
				String replyEmailBody = "\n================ Reply Message ================\n" + "---- On, "
						+ dateformatter.getLongDateFormatwithOutTimeAgo(lastSubEmail.getSentDate()) + " "
						+ sender.getEmailAddress() + " wrote ----\n" + lastSubEmail.getBody()
						+ "\n================= Reply Message =================\n";
				// composeEmailpage(user, folder, "Re: " + email.getSubject(), replyEmailBody);
				List<User> recipients = getRecipients();
				List<User> ccs = getCCUsers();
				Email replyEmail = getEmailObject(user);
				replyEmail.setSubject("Re: " + replyEmail.getSubject()+" To "+lastSubEmail.getSubject());
				replyEmail.setBody(replyEmail.getBody() + "\n" + replyEmailBody);
				try {
					replyEmail = emailService.createEmail(replyEmail, recipients, ccs);
				} catch (EmailAddressAlreadyExistException e) {
					e.printStackTrace();
				}
				sendOrdraftSubEmailpage(email, replyEmail, user, recipients, ccs);
			}
			case 2 -> {
				if (lastSubEmail.getSentDate() == null) {
					int sentOrDiscard = input.nextInt("Do you want to \n 1.Sent Or 2.Discard the last draft email");
					switch(sentOrDiscard) {
					case 1->{
						emailService.sendSubEmail(email, lastSubEmail, emailService.getEmailRecipients(lastSubEmail), emailService.getEmailCCs(lastSubEmail));
						System.out.println("The last email is sented successfully...");
					}
					case 2->{
						emailService.deleteSubEmail(email, lastSubEmail, user);
						System.out.println("Email is deleted...");
					}
					}
				}
				String forwardEmailBody = "\n============== Forwarded Message ==============\nFrom: "
						+ sender.getEmailAddress() + "\nTo: " + recipientsStr + "\nDate: "
						+ dateformatter.getLongDateFormatwithOutTimeAgo(lastSubEmail.getSentDate()) + "\nBody: \n"
						+ lastSubEmail.getBody() + "\n============== Forwarded Message ==============";
				List<User> recipients = getRecipients();
				List<User> ccs = getCCUsers();
				Email forwardEmail = getEmailObject(user);
				forwardEmail.setSubject("Fwd: "+forwardEmail.getSubject()+" of "+ lastSubEmail.getSubject());
				forwardEmail.setBody(forwardEmail.getBody() + "\n" + forwardEmailBody);
				try {
					forwardEmail = emailService.createEmail(forwardEmail, recipients, ccs);
				} catch (EmailAddressAlreadyExistException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sendOrdraftSubEmailpage(email, forwardEmail, user, recipients, ccs);
			}
			case 3 -> {
				String yesOrno = input.nextLine("Are you sure you want to move this email to Trash?(y/n)");
				switch (yesOrno.toLowerCase()) {
				case "y" -> {
					emailService.trashEmail(user, folder, email);
					System.out.println("Email is moved to Trash...");
				}
				case "n" -> {
					System.out.println("Email is not deleted...");
				}
				default -> {
					System.out.println("Invalid input...");
				}
				}
			}
			case 4 -> {
				folderService.updateIsRead(folder, email, (!folderService.getIsRead(folder, email)));
				emailService.updateEmail(email, email.getEmailId());
				System.out.println(folderService.getIsRead(folder, email) ? "Marked as Unread" : "Marked as Read");
			}
			case 5 -> {
				System.out.println("Exited from the email...");
			}
			default -> {
				System.out.println("Invalid Option...");
			}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Options with opening the DraftEmail
	static void optionsWithDraftEmail(User user, Folder folder, Email email) {
		try {
			while (true) {
				folderService.updateIsRead(folder, email, true);
				emailService.updateEmail(email, email.getEmailId());

				printEmail(folder, email);

				List<User> recipients = emailService.getEmailRecipients(email);
				List<User> ccs = emailService.getEmailCCs(email);
				List<String> emailOptionHeaders = new LinkedList<>();
				List<String> emailOptionRows = new LinkedList<>();
				emailOptionHeaders.add("Options with Draft email");
				emailOptionRows.add("1. Sent email");
				emailOptionRows.add("2. Discard email");
				emailOptionRows.add("3. Exit");

				printTable.printBox(emailOptionHeaders, emailOptionRows);

				int option = input.nextInt("Enter your option: ");
				switch (option) {
				case 1 -> {
					emailService.sendDraftEmail(folder, email, recipients, ccs);
					System.out.println("Email is sented successfully...");
					return;
				}
				case 2 -> {
					String confirmTrashEmail = input.nextLine("Delete the Email(y/n): ");
					if (confirmTrashEmail.toLowerCase().equals("y")) {
						emailService.trashEmail(user, folder, email);
						System.out.println("Email is deleted...");
						return;
					} else {
						System.out.println("Email is not deleted...");
					}
				}
				case 3 -> {
					System.out.println("Exiting draft Email...");
					return;
				}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Options with opening the TrashEmail
	static void optionsWithTrashEmail(User user, Folder folder, Email email) {
		try {
			while (true) {
				folderService.updateIsRead(folder, email, true);
				emailService.updateEmail(email, email.getEmailId());

				printEmail(folder, email);

				List<User> recipients = emailService.getEmailRecipients(email);
				List<String> emailOptionHeaders = new LinkedList<>();
				List<String> emailOptionRows = new LinkedList<>();
				emailOptionHeaders.add("Options with Draft email");
				emailOptionRows.add("1. Restore email");
				emailOptionRows.add("2. Delete email");
				emailOptionRows.add("3. Exit");

				printTable.printBox(emailOptionHeaders, emailOptionRows);

				int option = input.nextInt("Enter your option: ");
				switch (option) {
				case 1 -> {
					emailService.restoreEmail(folder, email);
					System.out.println("Email is restored successfully...");
					return;
				}
				case 2 -> {
					String confirmDeleteEmail = input.nextLine("Permanently Delete the Email(y/n): ");
					if (confirmDeleteEmail.toLowerCase().equals("y")) {
						emailService.permanentlyDeleteEmail(folder, email);
						System.out.println("Email is deleted permanently...");
						return;
					} else {
						System.out.println("Email is not deleted...");
					}
				}
				case 3 -> {
					System.out.println("Exiting draft Email...");
					return;
				}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static void optionsWithMultipleEmails(User user, Folder folder, List<Email> selectedEmails) {
		try {
			List<String> headers = new LinkedList<>();
			List<String> rows = new LinkedList<>();
			headers.add("Options");
			rows.add("1. Delete");
			rows.add("2. Move");
			rows.add("3. Mark as Unread");
			rows.add("4. Exit");

			printTable.printBox(headers, rows);

			int option = input.nextInt("Enter your option: ");
			switch (option) {
			case 1 -> {
				String confirmDelete = input.nextLine("Are you sure you want to move these emails to Trash?(y/n): ");
				if (confirmDelete.toLowerCase().equals("y")) {
					emailService.trashMultipleEmails(user, folder, selectedEmails);
					System.out.println("All emails are move to trash...");
				} else {
					System.out.println("All emails are not deleted..");
				}
			}
			case 2 -> {
				List<String> folderheaders = new LinkedList<>();
				List<String> folderrows = new LinkedList<>();
				List<Folder> folders = userService.getFoldersByUserId(user.getUserId());
				int count = 1;
				for (Folder selectingfolder : folders) {
					folderrows.add((count++) + ". " + selectingfolder.getFolderName());
				}
				printTable.printBox(folderheaders, folderrows);
				int folderIndex = input.nextInt("Enter the index of the folder: ");
				while (folderIndex > folders.size() || folderIndex <= 0) {
					System.err.println("Please enter a valid folder index ");
					folderIndex = input.nextInt("Enter the index of the folder: ");
				}
				Folder selectedfolder = folders.get(folderIndex - 1);
				emailService.moveEmail(folder, selectedfolder, selectedEmails);
				System.out.println("All the emails are moved to the " + selectedfolder.getFolderName() + " folder");
			}
			case 3 -> {
				for (Email email : selectedEmails) {
					folderService.updateIsRead(folder, email, false);
				}
				System.out.println("All emails are marked as unread");
			}
			case 4 -> {
				System.out.println("Exiting options...");
				return;
			}
			default -> {

			}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// print the emails inside the folder
	static List<Email> printEmailsByFolder(User user, Folder folder) {
		List<String> folderHeaders = new LinkedList<>();
		List<String> folderRows = new LinkedList<>();

		folderHeaders.add(folder.getFolderName());
		List<Email> emails = new LinkedList<>();
		try {
			emails = folderService.getEmailsByFolderId(folder);
			if (!emails.isEmpty()) {
				for (Email email : emails) {
					String recipientsStr = "";
					List<User> recipients = emailService.getEmailRecipients(email);
					for (User recipient : recipients) {
						if (recipients.indexOf(recipient) != recipients.size() - 1) {
							recipientsStr += recipient.getUserName() + ", ";
						} else {
							recipientsStr += recipient.getUserName();
						}
					}
					String subBody = email.getSubject() + "-" + email.getBody();
					subBody = subBody.length() > 40 ? subBody.substring(0, 40) + "..." : subBody;
					boolean isSentDateExist = email.getSentDate() != null;

					// Sent Email
					if ((email.getSenderId() == user.getUserId()) && isSentDateExist) {
						folderRows.add((emails.indexOf(email) + 1) + separator + "To: " + recipientsStr + separator
								+ email.getSubject() + " - " + subBody + separator
								+ dateformatter.getShortDateFormat(email.getSentDate()) + separator
								+ folderService.getStringIsRead(folder, email));
					}

					// Draft Email
					else if (email.getSenderId() == user.getUserId() && email.getSentDate() == null) {
						folderRows.add((emails.indexOf(email) + 1) + separator + "Draft" + separator
								+ email.getSubject() + " - " + subBody + separator + "yet to sent");
					}

					// Email
					else {
						User sender = userService.getUserByUserId(email.getSenderId());
						folderRows.add((emails.indexOf(email) + 1) + separator + sender.getUserName() + separator
								+ email.getSubject() + " - " + subBody + separator
								+ dateformatter.getShortDateFormat(email.getSentDate()) + separator
								+ folderService.getStringIsRead(folder, email));
					}
				}
//				switch (folder.getFolderName()) {
//				case ("Sent") -> {
//					for (Email email : emails) {
//						String recipientsStr = "";
//						List<User> recipients = emailService.getEmailRecipients(email);
//						for (User recipient : recipients) {
//							if (recipients.indexOf(recipient) != recipients.size() - 1) {
//								recipientsStr += recipient.getUserName() + ", ";
//							} else {
//								recipientsStr += recipient.getUserName();
//							}
//						}
//						String subBody = email.getSubject() + "-" + email.getBody();
//						subBody = subBody.length() > 40 ? subBody.substring(0, 40) + "..." : subBody;
//						folderRows.add((emails.indexOf(email) + 1) + separator + "To: " + recipientsStr + separator
//								+ email.getSubject() + " - " + subBody + separator
//								+ dateformatter.getShortDateFormat(email.getSentDate()));
//					}
//				}
//				case ("Draft") -> {
//					for (Email email : emails) {
//						String subBody = email.getSubject() + "-" + email.getBody();
//						subBody = subBody.length() > 40 ? subBody.substring(0, 40) + "..." : subBody;
//						folderRows.add((emails.indexOf(email) + 1) + separator + "Draft" + separator
//								+ email.getSubject() + " - " + subBody + separator
//								+ dateformatter.getShortDateFormat(email.getSentDate()));
//					}
//				}
//				case ("Trash")->{
//					
//				}
//				default -> {
//					for (Email email : emails) {
//						User sender = userService.getUserByUserId(email.getSenderId());
//						String subBody = email.getSubject() + "-" + email.getBody();
//						subBody = subBody.length() > 40 ? subBody.substring(0, 40) + "..." : subBody;
//						folderRows.add((emails.indexOf(email) + 1) + separator + sender.getUserName() + separator
//								+ email.getSubject() + " - " + subBody + separator
//								+ dateformatter.getShortDateFormat(email.getSentDate()) + separator
//								+ folderService.getStringIsRead(folder, email));
//					}
//				}
//				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NothingInTheFolderException e) {
			folderRows.add(e.getMessage());
		}
		printTable.printBox(folderHeaders, folderRows);
		return emails;
	}

	public static void main(String[] args) {
		while (true) {
			try {
				signInOrSignUpPage();
			} catch (InvalidUserException e) {
				System.err.println(e.getMessage() + '\n');
			}

//			System.out.println(dateformatter.getLongDateFormat(LocalDateTime.now()));
		}
	}
}

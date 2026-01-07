package main.java.com.emailsystem.service;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.com.emailsystem.dao.*;
import main.java.com.emailsystem.exception.EmailAddressAlreadyExistException;
import main.java.com.emailsystem.exception.NoEmailAddressExistException;
import main.java.com.emailsystem.exception.NoUserException;
import main.java.com.emailsystem.model.Folder;
import main.java.com.emailsystem.model.User;

public class UserService {
	public UsersDAO usersDAO = new UsersDAOImpl();
	public UserFolderRelationsDAO userFolderRelationsDAO = new UserFolderRelationsDAOImpl();
	
	//add a new User to the db and return the User object
	public User addUser(String userName, String emailAddress, String passwordHash) throws SQLException, EmailAddressAlreadyExistException {
		if(usersDAO.isEmailAddressExist(emailAddress)) {
			throw new EmailAddressAlreadyExistException("Emailaddress already exist");
		}
		User user = new User( userName, emailAddress, passwordHash);
		user = usersDAO.save(user);
		return user;
	}
	
	//get the user object by emailAddress from the db
	public User getUserByEmailAddress(String emailAddress) throws SQLException, NoUserException {
		return usersDAO.getUserByEmailAddress(emailAddress);
	}
	
	//get the user object by userId from the db
	public User getUserByUserId(int userId) throws SQLException {
		return usersDAO.get(userId);
	}
	
	public int updateUser(User user, int userId) throws SQLException {
		return usersDAO.update(user, userId);
	}
	
	//authenticate the user and return boolean
	public boolean authenticateUser(String emailAddress, String password) throws SQLException, NoUserException {
		User user = usersDAO.getUserByEmailAddress(emailAddress);
		if(user.getEmailAddress().equals(emailAddress) && user.getPassword().equals(password)) {
			return true;
		}else {
			return false;
		}
	}
	
	//check the emailaddress is in this syntax
	public boolean emailAddressValidation(String emailAddress) {
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@gmail\\.com$");
		Matcher matcher = pattern.matcher(emailAddress);
		return matcher.matches();
	}
	
	//get the List of folders in a specific user
	public List<Folder> getFoldersByUserId(int userId) throws SQLException {
		List<Folder> folders = userFolderRelationsDAO.getRelationObjects(userId);
		return folders;
	}
}

package main.java.com.emailsystem.dao;

import java.sql.SQLException;
import java.util.List;

import main.java.com.emailsystem.exception.EmailAddressAlreadyExistException;
import main.java.com.emailsystem.exception.NoUserException;
import main.java.com.emailsystem.model.User;

public interface UsersDAO extends CommonDAO<User>{
	List<User> getUsers() throws SQLException;
	boolean isEmailAddressExist(String emailAddress) throws SQLException;
	User getUserByEmailAddress(String finduserEmailAddress) throws SQLException, NoUserException;
	int getNoOfFolders(int userId) throws SQLException;
}

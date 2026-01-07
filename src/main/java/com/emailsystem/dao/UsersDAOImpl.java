package main.java.com.emailsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import main.java.com.emailsystem.exception.EmailAddressAlreadyExistException;
import main.java.com.emailsystem.exception.NoUserException;
import main.java.com.emailsystem.model.User;
import main.java.com.emailsystem.util.MySqlDbUtil;

public class UsersDAOImpl implements UsersDAO{

	@Override
	public User save(User user) throws SQLException, EmailAddressAlreadyExistException {
		if(isEmailAddressExist(user.getEmailAddress())) {
			throw new EmailAddressAlreadyExistException("Emailaddress already exist");
		}
		Connection connection = MySqlDbUtil.getConnection();
		String query = "INSERT INTO users (user_name, email_address, password_hash) VALUES (?, ?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
		pstmt.setString(1, user.getUserName());
		pstmt.setString(2, user.getEmailAddress());
		pstmt.setString(3, user.getPassword());
		
		int affectedRows = pstmt.executeUpdate();
		ResultSet rs = pstmt.getGeneratedKeys();
		int userId = 0;
		if(rs.next()) {
			userId = rs.getInt(1);
		}
		user.setUserId(userId);
		connection.close();
		return user;
	}

	@Override
	public int update(User user, int userId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "Update users set user_name = ?, email_address = ?, password_hash = ? WHERE user_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, user.getUserName());
		pstmt.setString(2, user.getEmailAddress());
		pstmt.setString(3, user.getPassword());
		pstmt.setInt(4, userId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows;
	}

	@Override
	public boolean delete(int userId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "DELECT FORM users WHERE user_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, userId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows > 0;
	}

	@Override
	public User get(int finduserId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM users WHERE user_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, finduserId);
		ResultSet rs = pstmt.executeQuery();
		User user = null;
		while(rs.next()) {
			int userId = rs.getInt("user_id");
			String userName = rs.getString("user_name");
			String emailAddress = rs.getString("email_address");
			String passwordHash = rs.getString("password_hash");
			user = new User(userId, userName, emailAddress, passwordHash);
		}
		connection.close();
		return user;
	}
	
	@Override
	public List<User> getUsers() throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query ="SELECT * FROM users";
		PreparedStatement pstmt = connection.prepareStatement(query);
		ResultSet rs = pstmt.executeQuery();
		List<User> users = new LinkedList<>();
		while(rs.next()){
			int userId = rs.getInt("user_id");
			String userName = rs.getString("user_name");
			String emailAddress = rs.getString("email_address");
			String passwordHash = rs.getString("password_hash");
			User user = new User(userId, userName, emailAddress, passwordHash);
			users.add(user);
		}
		connection.close();
		return users;
	}
	
	@Override
	public boolean isEmailAddressExist(String emailAddress) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM users WHERE user_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, emailAddress);
		ResultSet rs = pstmt.executeQuery();
		int count = 0;
		while(rs.next()) {
			count++;
		}
		return count>0;
	}
	
	@Override
	public User getUserByEmailAddress(String finduserEmailAddress) throws SQLException, NoUserException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM users WHERE email_address = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, finduserEmailAddress);
		ResultSet rs = pstmt.executeQuery();
		User user = null;
		while(rs.next()) {
			int userId = rs.getInt("user_id");
			String userName = rs.getString("user_name");
			String emailAddress = rs.getString("email_address");
			String passwordHash = rs.getString("password_hash");
			user = new User(userId, userName, emailAddress, passwordHash);
		}
		if(user == null) {
			throw new NoUserException("No such email address exist !!");
		}
		return user;
	}

	@Override
	public int getNoOfFolders(int userId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT COUNT(*) FROM folders GROUP BY user_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, userId);
		ResultSet rs = pstmt.executeQuery();
		int noOfFolders = 0;
		while(rs.next()) {
			noOfFolders = rs.getInt(1);
		}
		connection.close();
		return noOfFolders;
	}

}

package main.java.com.emailsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import main.java.com.emailsystem.model.User;
import main.java.com.emailsystem.util.MySqlDbUtil;

public class EmailRecipientRelationsDAOImpl implements EmailRecipientRelationsDAO{

	@Override
	public boolean createRelation(int emailId, int recipientId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "INSERT INTO email_recipient_relations (email_id, recipient_id) VALUES (?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, emailId);
		pstmt.setInt(2, recipientId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows>0;
	}

	@Override
	public List<User> getRelationObjects(int emailId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * from users WHERE user_id in (SELECT recipient_id FROM email_recipient_relations WHERE email_id = ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, emailId);
		ResultSet rs = pstmt.executeQuery();
		List<User> recipients = new LinkedList<>();
		while(rs.next()) {
			int recipientId = rs.getInt("user_id");
			String recipientName = rs.getString("user_name");
			String recipientEmailAddress = rs.getString("email_address");
			String recipientPasswordHash = rs.getString("password_hash");
			User recipient = new User(recipientId, recipientName, recipientEmailAddress, recipientPasswordHash);
			recipients.add(recipient);
		}
		connection.close();
		return recipients;
	}
	 
	@Override
	public boolean deleteEmailRecipientRelation(int emailId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "DELETE from email_recipient_relations WHERE email_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, emailId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows > 0;
	}

}

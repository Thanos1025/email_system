package main.java.com.emailsystem.dao;

import java.sql.*;
import java.time.LocalDateTime;

import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.util.LocalDateTimeAndDateUtil;
import main.java.com.emailsystem.util.MySqlDbUtil;

public class EmailsDAOImpl implements EmailsDAO{

	@Override
	public Email save(Email email) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "INSERT INTO emails (subject, body, sender_id, create_date) VALUES (?, ?, ?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
		pstmt.setString(1, email.getSubject());
		pstmt.setString(2, email.getBody());
		pstmt.setInt(3, email.getSenderId());
		pstmt.setTimestamp(4, LocalDateTimeAndDateUtil.convertLocalDateTimeToTimestamp(email.getCreatedDate()));
		int affectedRows = pstmt.executeUpdate();
		ResultSet rs = pstmt.getGeneratedKeys();
		int emailId = 0;
		if(rs.next()) {
			emailId = rs.getInt(1);
		}
		email.setEmailId(emailId);
		connection.close();
		return email;
	}

	@Override
	public int update(Email email, int updateEmailId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "UPDATE emails SET subject = ?, body = ?, create_date = ?, sent_date = ?, sender_id =? WHERE email_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, email.getSubject());
		pstmt.setString(2, email.getBody());
		pstmt.setTimestamp(3, LocalDateTimeAndDateUtil.convertLocalDateTimeToTimestamp(email.getCreatedDate()));
		pstmt.setTimestamp(4, LocalDateTimeAndDateUtil.convertLocalDateTimeToTimestamp(email.getSentDate()));
		pstmt.setInt(5, email.getSenderId());
		pstmt.setInt(6, updateEmailId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows;
	}

	@Override
	public boolean delete(int emailId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "DELETE FROM emails WHERE email_id =?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, emailId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows>0;
	}

	@Override
	public Email get(int findEmailId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM emails WHERE email_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, findEmailId);
		ResultSet rs = pstmt.executeQuery();
		Email email = null;
		while(rs.next()) {
			int emailId = rs.getInt("email_id");
			String subject = rs.getString("subject");
			String body = rs.getString("body");
			LocalDateTime createDate = LocalDateTimeAndDateUtil.convertTimestampToLocalDateTime(rs.getTimestamp("create_date"));
			LocalDateTime sentDate = LocalDateTimeAndDateUtil.convertTimestampToLocalDateTime(rs.getTimestamp("sent_date"));
			int senderId = rs.getInt("sender_id");
			email = new Email(emailId, subject, body, createDate, sentDate, senderId);
		}
		connection.close();
		return email;
	}

}

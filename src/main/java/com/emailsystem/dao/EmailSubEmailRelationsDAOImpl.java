package main.java.com.emailsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.util.LocalDateTimeAndDateUtil;
import main.java.com.emailsystem.util.MySqlDbUtil;

public class EmailSubEmailRelationsDAOImpl implements EmailSubEmailRelationsDAO {

	@Override
	public boolean createRelation(int emailId, int subEmailId, int userId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "INSERT INTO email_subemail_relations (email_id, sub_email_id, user_id) VALUES (?, ?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, emailId);
		pstmt.setInt(2, subEmailId);
		pstmt.setInt(3, userId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows>0;
	}

	@Override
	public List<Email> getRelationObjects(int emailId, int userId) throws SQLException {
		List<Email> subEmails = new LinkedList<>();
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM emails WHERE email_id IN (SELECT sub_email_id FROM email_subemail_relations WHERE email_id = ? AND user_id = ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, emailId);
		pstmt.setInt(2, userId);
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()) {
			int subEmailId = rs.getInt("email_id");
			String subject = rs.getString("subject");
			String body = rs.getString("body");
			LocalDateTime createDate = LocalDateTimeAndDateUtil.convertTimestampToLocalDateTime(rs.getTimestamp("create_date"));
			LocalDateTime sendDate = LocalDateTimeAndDateUtil.convertTimestampToLocalDateTime(rs.getTimestamp("sent_date"));
			int senderId = rs.getInt("sender_id");
			Email subEmail = new Email(subEmailId, subject, body, createDate, sendDate, senderId);
			subEmails.add(subEmail);
		}
		return subEmails;
	}

	@Override
	public boolean deleteRelation(int emailId, int subEmailId, int userId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "DELETE FROM email_subemail_relations WHERE email_id = ? AND sub_email_id = ? AND user_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, emailId);
		pstmt.setInt(2, subEmailId);
		pstmt.setInt(3, userId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows>0;
	}

}

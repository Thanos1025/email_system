package main.java.com.emailsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.Folder;
import main.java.com.emailsystem.util.LocalDateTimeAndDateUtil;
import main.java.com.emailsystem.util.MySqlDbUtil;

public class FolderEmailRelationsDAOImpl implements FolderEmailRelationsDAO{

	@Override
	public boolean createRelation(int folderId, int emailId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "INSERT INTO folder_email_relations (folder_id, email_id, is_read) values (?, ?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, folderId);
		pstmt.setInt(2, emailId);
		pstmt.setBoolean(3, false);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows >0;
	}

	@Override
	public List<Email> getRelationObjects(int folderId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM emails WHERE email_id IN (SELECT email_id from folder_email_relations where folder_id = ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, folderId);
		ResultSet rs = pstmt.executeQuery();
		List<Email> emails = new LinkedList<>();
		while(rs.next()) {
			int emailId = rs.getInt("email_id");
			String subject = rs.getString("subject");
			String body = rs.getString("body");
			LocalDateTime createDate = LocalDateTimeAndDateUtil.convertTimestampToLocalDateTime(rs.getTimestamp("create_date"));
			LocalDateTime sendDate = LocalDateTimeAndDateUtil.convertTimestampToLocalDateTime(rs.getTimestamp("sent_date"));
			int senderId = rs.getInt("sender_id");
			Email email = new Email(emailId, subject, body, createDate, sendDate, senderId);
			emails.add(email);
		}
		connection.close();
		return emails;
	}
	
	@Override
	public Folder getFolderByEmailIdAndUserId(int userId, int emailId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM folders WHERE folder_id in (SELECT folder_id from user_folder_relations where user_id = ?) AND folder_id in (SELECT folder_id from folder_email_relations where email_id = ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, userId);
		pstmt.setInt(2, emailId);
		ResultSet rs = pstmt.executeQuery();
		Folder folder = null;
		while(rs.next()) {
			int folderId = rs.getInt("folder_id");
			String folderName = rs.getString("folder_name");
			Boolean isDefaultFolder = rs.getBoolean("is_default_folder");
			folder = new Folder(folderId, folderName, isDefaultFolder);
		}
		connection.close();
		return folder;
	}

	public boolean getIsRead(int folderId, int emailId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT is_read FROM folder_email_relations WHERE folder_id = ? AND email_id =?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, folderId);
		pstmt.setInt(2, emailId);
		ResultSet rs = pstmt.executeQuery();
		boolean isRead = false;
		if(rs.next()) {
			isRead = rs.getBoolean("is_read");
		}
		connection.close();
		return isRead;
	}
	@Override
	public int updateFolderEmailRelation(int newfolderId, int emailId, int previousFolderId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "UPDATE folder_email_relations SET folder_id = ?, previous_folder_id = ? WHERE email_id = ? AND folder_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, newfolderId);
		pstmt.setInt(2, previousFolderId);
		pstmt.setInt(3, emailId);
		pstmt.setInt(4, previousFolderId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows;
	}

	@Override
	public boolean deleteFolderEmailRelationByEmailIdAndFolderId(int folderId, int emailId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "DELETE from folder_email_relations WHERE email_id = ? AND folder_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, emailId);
		pstmt.setInt(2, folderId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows > 0;
	}

	@Override
	public int updateIsRead(int folderId, int emailId, boolean isRead) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "UPDATE folder_email_relations SET is_read = ? WHERE email_id = ? AND folder_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setBoolean(1, isRead);
		pstmt.setInt(2, emailId);
		pstmt.setInt(3, folderId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows;
	}

	@Override
	public Folder getPreviouseFolder(int currentFolderId, int emailId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM folders WHERE folder_id = (SELECT previous_folder_id FROM folder_email_relations WHERE folder_id = ? AND email_id = ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, currentFolderId);
		pstmt.setInt(2, emailId);
		ResultSet rs = pstmt.executeQuery();
		Folder folder = null;
		while(rs.next()) {
			int folderId = rs.getInt("folder_id");
			String folderName = rs.getString("folder_name");
			Boolean isDefaultFolder = rs.getBoolean("is_default_folder");
			folder = new Folder(folderId, folderName, isDefaultFolder);
		}
		connection.close();
		return folder;
	}

}

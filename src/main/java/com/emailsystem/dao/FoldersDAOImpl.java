package main.java.com.emailsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.Folder;
import main.java.com.emailsystem.util.MySqlDbUtil;

public class FoldersDAOImpl implements FoldersDAO{

	@Override
	public Folder save(Folder folder) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "INSERT INTO folders (folder_name, is_default_folder) VALUES (?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
		pstmt.setString(1, folder.getFolderName());
		pstmt.setBoolean(2, folder.getIsDefaultFolder());
		int affectedRows = pstmt.executeUpdate();
		ResultSet rs = pstmt.getGeneratedKeys();
		int folderId = 0;
		if(rs.next()) {
			folderId = rs.getInt(1);
		}
		folder.setFolderId(folderId);
		connection.close();
		return folder;
	}

	@Override
	public Folder get(int findfolderId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM folders WHERE folder_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, findfolderId);
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

	@Override
	public boolean delete(int folderId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "DELETE FROM folders WHERE folder_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, folderId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows>0;
	}

	@Override
	public int update(Folder folder, int folderId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "UPDATE folders SET folder_name = ?, is_default_folder = ? WHERE folder_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, folder.getFolderName());
		pstmt.setBoolean(2, folder.getIsDefaultFolder());
		pstmt.setInt(3, folderId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows;
	}
	
	@Override
	public Folder getFolderByFolderNameAndUserId(String findFolderName, int userId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM folders WHERE folder_name = ? AND folder_id IN (SELECT folder_id from user_folder_relations WHERE user_id = ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, findFolderName);
		pstmt.setInt(2, userId);
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

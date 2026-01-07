package main.java.com.emailsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import main.java.com.emailsystem.model.Folder;
import main.java.com.emailsystem.util.MySqlDbUtil;

public class UserFolderRelationsDAOImpl implements UserFolderRelationsDAO{

	@Override
	public List<Folder> getRelationObjects(int userId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM folders WHERE folder_id IN (SELECT folder_id FROM user_folder_relations WHERE user_id = ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, userId);
		ResultSet rs = pstmt.executeQuery();
		List<Folder> folders = new LinkedList<>();
		while(rs.next()) {
			int folderId= rs.getInt("folder_id");
			String folderName = rs.getString("folder_name");
			Boolean isDefaultFolder = rs.getBoolean("is_default_folder");
			Folder folder = new Folder(folderId, folderName, isDefaultFolder);
			folders.add(folder);
		}
		connection.close();
		return folders;
	}

	@Override
	public boolean createRelation(int userId, int folderId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "INSERT INTO user_folder_relations (folder_id, user_id) VALUES (?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, folderId);
		pstmt.setInt(2, userId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows > 0;
	}

	@Override
	public boolean deleteUserFolderRelation(int folderId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "DELELTE FROM user_folder_relations WHERE folder_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, folderId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows > 0;
	}

}

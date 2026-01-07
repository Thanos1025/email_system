package main.java.com.emailsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import main.java.com.emailsystem.exception.EmailAddressAlreadyExistException;
import main.java.com.emailsystem.model.Filter;
import main.java.com.emailsystem.model.Folder;
import main.java.com.emailsystem.util.MySqlDbUtil;

public class FiltersDAOImpl implements FiltersDAO{

	@Override
	public Filter save(Filter filter) throws SQLException, EmailAddressAlreadyExistException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "INSERT INTO filters (filter_name, condition_type, folder_id) VALUES (?, ?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
		pstmt.setString(1, filter.getFilterName());
		pstmt.setInt(2, filter.getConditionType());
		pstmt.setInt(3, filter.getFolderId());
		int affectedRows = pstmt.executeUpdate();
		ResultSet rs = pstmt.getGeneratedKeys();
		if(rs.next()) {
			int filterId = rs.getInt(1);
			filter.setFilterId(filterId);
		}
		connection.close();
		return filter;
	}

	@Override
	public int update(Filter filter, int filterId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "UPDATE filters SET filter_name = ?, condition_type = ?, folder_id = ? WHERE filter_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, filter.getFilterName());
		pstmt.setInt(2, filter.getConditionType());
		pstmt.setInt(3, filter.getFolderId());
		pstmt.setInt(4, filterId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows;
	}

	@Override
	public boolean delete(int filterId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "DELETE FROM filters WHERE fiter_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, filterId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows>0;
	}

	@Override
	public Filter get(int findFilterId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM filters WHERE fiter_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, findFilterId);
		ResultSet rs = pstmt.executeQuery();
		Filter filter = null;
		while(rs.next()) {
			int filterId = rs.getInt("filter_id");
			String filterName = rs.getString("filter_name");
			int conditionType = rs.getInt("condition_type");
			int folderId = rs.getInt("folder_id");
			filter = new Filter(filterId, filterName, conditionType, folderId);
		}
		connection.close();
		return filter;
	}

	@Override
	public List<Filter> getFilterByUserId(int userId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM filters WHERE folder_id IN (SELECT folder_id from user_folder_relations WHERE user_id = ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, userId);
		ResultSet rs = pstmt.executeQuery();
		List<Filter> filters = new LinkedList<>();
		while(rs.next()) {
			int filterId = rs.getInt("filter_id");
			String filterName = rs.getString("filter_name");
			int conditionType = rs.getInt("condition_type");
			int folderId = rs.getInt("folder_id");
			Filter filter = new Filter(filterId, filterName, conditionType, folderId);
			filters.add(filter);
		}
		connection.close();
		return filters;
	}

	@Override
	public Filter getFilterByFolderId(int findFolderId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM filters WHERE folder_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, findFolderId);
		ResultSet rs = pstmt.executeQuery();
		Filter filter = null;
		if(rs.next()) {
			int filterId = rs.getInt("filter_id");
			String filterName = rs.getString("filter_name");
			int conditionType = rs.getInt("condition_type");
			int folderId = rs.getInt("folder_id");
			filter = new Filter(filterId, filterName, conditionType, folderId);
		}
		connection.close();
		return filter;
	}

	@Override
	public Folder getFolderByFilterId(int filterId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM folders WHERE folder_id = (SELECT folder_id from filters WHERE filter_id = ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, filterId);
		ResultSet rs = pstmt.executeQuery();
		Folder folder = null;
		if(rs.next()) {
			int folderId = rs.getInt("folder_id");
			String folderName = rs.getString("folder_name");
			Boolean isDefaultFolder = rs.getBoolean("is_default_folder");
			folder = new Folder(folderId, folderName, isDefaultFolder);
		}
		connection.close();
		return folder;
	}
}

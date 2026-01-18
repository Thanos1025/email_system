package main.java.com.emailsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import main.java.com.emailsystem.model.User;
import main.java.com.emailsystem.util.MySqlDbUtil;

public class EmailCarbonCopyRelationsDAOImpl implements EmailCarbonCopyRelationsDAO {

	@Override
	public boolean createRelation(int emailId, int ccUserId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "INSERT INTO email_carbon_copy_relations (email_id, carbon_copy_user_id) VALUES (?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, emailId);
		pstmt.setInt(2, ccUserId);
		int affectedRows = pstmt.executeUpdate();
		connection.close();
		return affectedRows>0;
	}

	@Override
	public List<User> getRelationObjects(int emailId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * from users WHERE user_id in (SELECT carbon_copy_user_id FROM email_carbon_copy_relations WHERE email_id = ?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, emailId);
		ResultSet rs = pstmt.executeQuery();
		List<User> ccs = new LinkedList<>();
		while(rs.next()) {
			int ccUserId = rs.getInt("user_id");
			String ccUserName = rs.getString("user_name");
			String ccEmailAddress = rs.getString("email_address");
			String ccPasswordHash = rs.getString("password_hash");
			User cc = new User(ccUserId, ccUserName, ccEmailAddress, ccPasswordHash);
			ccs.add(cc);
		}
		connection.close();
		return ccs;
	}

}

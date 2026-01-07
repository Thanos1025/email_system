package main.java.com.emailsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import main.java.com.emailsystem.exception.EmailAddressAlreadyExistException;
import main.java.com.emailsystem.model.Condition;
import main.java.com.emailsystem.util.MySqlDbUtil;

public class ConditionsDAOImpl implements ConditionsDAO {

	@Override
	public Condition save(Condition condition) throws SQLException, EmailAddressAlreadyExistException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "INSERT INTO conditions (condition_string, condition_operator, condition_point, filter_id) VALUES (?, ?, ?, ?)";
		PreparedStatement pstmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
		pstmt.setString(1, condition.getConditionString());
		pstmt.setInt(2, condition.getConditionOperator());
		pstmt.setInt(3, condition.getConditionPoint());
		pstmt.setInt(4, condition.getFilterId());
		int affectedRows = pstmt.executeUpdate();
		ResultSet rs = pstmt.getGeneratedKeys();
		if(rs.next()) {
			condition.setConditionId(rs.getInt(1));
		}
		connection.close();
		return condition;
	}

	@Override
	public int update(Condition condition, int conditionId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "UPDATE conditions SET condition_string = ?, condition_operator =?, condition_point = ?, filter_id = ? WHERE condition_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, condition.getConditionString());
		pstmt.setInt(2, condition.getConditionOperator());
		pstmt.setInt(3, condition.getConditionPoint());
		pstmt.setInt(4, condition.getFilterId());
		pstmt.setInt(5, conditionId);
		int affectRows = pstmt.executeUpdate();
		connection.close();
		return affectRows;
	}

	@Override
	public boolean delete(int conditionId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "DELETE FROM conditions WHERE condition_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, conditionId);
		int affectedRows = pstmt.executeUpdate();
		return affectedRows>0;
	}

	@Override
	public Condition get(int findConditionId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM conditions WHERE condition_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, findConditionId);
		ResultSet rs = pstmt.executeQuery();
		Condition condition = null;
		while(rs.next()) {
			int conditionId = rs.getInt("condition_id");
			String conditionString = rs.getString("condition_string");
			int conditionOperator =rs.getInt("condition_operator");
			int conditionAppliedUserId = rs.getInt("condition_point");
			int filterId = rs.getInt("filter_id");
			condition = new Condition(conditionId, conditionString, conditionOperator, conditionAppliedUserId, filterId);
		}
		return condition;
	}

	@Override
	public List<Condition> getConditionByFilterId(int findFilterId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT * FROM conditions WHERE filter_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, findFilterId);
		ResultSet rs = pstmt.executeQuery();
		List<Condition> conditions = new LinkedList<>();
		while(rs.next()) {
			int conditionId = rs.getInt("condition_id");
			String conditionString = rs.getString("condition_string");
			int conditionOperator =rs.getInt("condition_operator");
			int conditionPoint = rs.getInt("condition_point");
			int filterId = rs.getInt("filter_id");
			Condition condition = new Condition(conditionId, conditionString, conditionOperator, conditionPoint, filterId);
			conditions.add(condition);
		}
		return conditions;
	}

	@Override
	public boolean deleteConditionByFilterId(int filterId) throws SQLException {
		Connection connection = MySqlDbUtil.getConnection();
		String query = "DELETE FROM conditions WHERE filter_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, filterId);
		int affectedRows = pstmt.executeUpdate();
		return affectedRows>0;
	}
	
	@Override
	public int getConditionCountByFilterId(int filterId) throws SQLException{
		Connection connection = MySqlDbUtil.getConnection();
		String query = "SELECT COUNT(*) FROM conditions WHERE filter_id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, filterId);
		ResultSet rs = pstmt.executeQuery();
		int conditionCount = 0;
		if(rs.next()) {
			conditionCount = rs.getInt(1);
		}
		return conditionCount;
	}
}

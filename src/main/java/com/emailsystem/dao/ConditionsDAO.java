package main.java.com.emailsystem.dao;

import java.sql.SQLException;
import java.util.List;

import main.java.com.emailsystem.model.Condition;

public interface ConditionsDAO extends CommonDAO<Condition>{
	List<Condition> getConditionByFilterId(int filterId) throws SQLException;
	boolean deleteConditionByFilterId(int filterId) throws SQLException;
	int getConditionCountByFilterId(int filterId) throws SQLException;
}

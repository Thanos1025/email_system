package main.java.com.emailsystem.service;

import java.sql.SQLException;
import java.util.List;

import main.java.com.emailsystem.dao.ConditionsDAO;
import main.java.com.emailsystem.dao.ConditionsDAOImpl;
import main.java.com.emailsystem.dao.UsersDAO;
import main.java.com.emailsystem.dao.UsersDAOImpl;
import main.java.com.emailsystem.enumpackage.ConditionOperator;
import main.java.com.emailsystem.enumpackage.ConditionPoint;
import main.java.com.emailsystem.exception.EmailAddressAlreadyExistException;
import main.java.com.emailsystem.model.Condition;
import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.Filter;
import main.java.com.emailsystem.model.User;

public class ConditionService {
	public ConditionsDAO conditionDAO = new ConditionsDAOImpl();
	public UsersDAO userDAO = new UsersDAOImpl();

	public Condition createCondition(Condition condition) throws SQLException, EmailAddressAlreadyExistException {
		return conditionDAO.save(condition);
	}

	public int updateCondition(Condition condition, int conditionId) throws SQLException {
		return conditionDAO.update(condition, conditionId);
	}

	public boolean deleteCondition(int conditionId) throws SQLException {
		return conditionDAO.delete(conditionId);
	}
	
	public boolean deleteConditionByFilterId(int filterId) throws SQLException {
		return conditionDAO.deleteConditionByFilterId(filterId);
	}

	public Condition getCondition(int conditionId) throws SQLException {
		return conditionDAO.get(conditionId);
	}

	public List<Condition> getConditionByFilter(Filter filter) throws SQLException {
		return conditionDAO.getConditionByFilterId(filter.getFilterId());
	}
	
	public int getConditionCountByFilterId(int filterId) throws SQLException {
		return conditionDAO.getConditionCountByFilterId(filterId);
	}

	public boolean checkCondition(Email email, Condition condition) throws SQLException {
		String emailItem = "";
		switch (ConditionPoint.fromIndex(condition.getConditionPoint())) {
		case ConditionPoint.FROM -> {
			User sender = userDAO.get(email.getSenderId());
			emailItem = sender.getEmailAddress();
		}
		case ConditionPoint.SUBJECT -> {
			emailItem = email.getSubject();
		}
		case ConditionPoint.BODY -> {
			emailItem = email.getBody();
		}
		}
		switch (ConditionOperator.fromIndex(condition.getConditionOperator())) {
		case ConditionOperator.CONTAINS:
			return emailItem.contains(condition.getConditionString());
		case ConditionOperator.DOESNOTCONTAIN:
			return !emailItem.contains(condition.getConditionString());
		case ConditionOperator.BEGINSWITH:
			return emailItem.startsWith(condition.getConditionString());
		case ConditionOperator.ENDSWITH:
			return emailItem.endsWith(condition.getConditionString());
		case ConditionOperator.IS:
			return emailItem.equals(condition.getConditionString());
		case ConditionOperator.ISNOT:
			return !emailItem.equals(condition.getConditionString());
		default:
			return false;
		}
	}
}

package main.java.com.emailsystem.service;

import main.java.com.emailsystem.dao.FiltersDAOImpl;
import main.java.com.emailsystem.dao.FoldersDAOImpl;
import main.java.com.emailsystem.dao.ConditionsDAOImpl;
import main.java.com.emailsystem.enumpackage.ConditionType;
import main.java.com.emailsystem.exception.EmailAddressAlreadyExistException;
import main.java.com.emailsystem.model.Condition;
import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.Filter;
import main.java.com.emailsystem.model.Folder;
import main.java.com.emailsystem.model.User;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import main.java.com.emailsystem.dao.FiltersDAO;
import main.java.com.emailsystem.dao.FoldersDAO;
import main.java.com.emailsystem.dao.ConditionsDAO;

public class FilterService {
	public FiltersDAO filterDAO = new FiltersDAOImpl();
	public FoldersDAO folderDAO = new FoldersDAOImpl();
	public ConditionService conditionService = new ConditionService();

	public Filter createFilter(Filter filter) throws SQLException, EmailAddressAlreadyExistException {
		filter = filterDAO.save(filter);
		return filter;
	}

	public int updateFilter(Filter filter, int filterId) throws SQLException {
		return filterDAO.update(filter, filterId);
	}

	public boolean deleteFilter(int filterId) throws SQLException {
		conditionService.deleteConditionByFilterId(filterId);
		return filterDAO.delete(filterId);
	}
	
	public Filter getFitler(int filterId) throws SQLException {
		return filterDAO.get(filterId);
	}
	
	public List<Filter> getFilterByUser(User user) throws SQLException{
		return filterDAO.getFilterByUserId(user.getUserId());
	}
	
	public Filter getFilterByFolder(Folder folder ) throws SQLException {
		return filterDAO.getFilterByFolderId(folder.getFolderId());
	}

	public Folder applyFilter(Email email, User user) throws SQLException {
		Folder inbox = folderDAO.getFolderByFolderNameAndUserId("Inbox", user.getUserId());
		List<Filter> filters = filterDAO.getFilterByUserId(user.getUserId());
		
		filterLoop:
		for (Filter filter : filters) {
			List<Condition> conditions = conditionService.getConditionByFilter(filter);
			Folder folder = filterDAO.getFolderByFilterId(filter.getFilterId());
			switch (filter.getConditionType()) {
			case 0 -> {
				boolean passFilter = false;
				for (Condition condition : conditions) {
					if (passFilter) {
						return folder;
					}
					if (conditionService.checkCondition(email, condition)) {
						passFilter = true;
					}
				}
			}
			case 1 -> {
				boolean passFilter = false;
				for (Condition condition : conditions) {
					if (!conditionService.checkCondition(email, condition)) {
						break filterLoop;
					}
				}
				return folder;
			}
			case 2 -> {
				return folder;
			}
			}
		}
		
		return inbox;
	}
}

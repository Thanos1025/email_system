package main.java.com.emailsystem.dao;

import java.sql.SQLException;
import java.util.List;

import main.java.com.emailsystem.model.Filter;
import main.java.com.emailsystem.model.Folder;

public interface FiltersDAO extends CommonDAO<Filter>{
	List<Filter> getFilterByUserId(int userId) throws SQLException;
	Filter getFilterByFolderId(int folderId) throws SQLException;
	Folder getFolderByFilterId(int filterId) throws SQLException;
}

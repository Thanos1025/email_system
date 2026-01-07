package main.java.com.emailsystem.dao;

import java.sql.SQLException;
import java.util.List;

public interface CommonRelationDAO<T> {
	boolean createRelation(int id1, int id2) throws SQLException;
	List<T> getRelationObjects(int id1) throws SQLException;
}

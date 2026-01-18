package main.java.com.emailsystem.dao;

import java.sql.SQLException;
import java.util.List;

import main.java.com.emailsystem.model.Email;

public interface EmailSubEmailRelationsDAO {
	boolean createRelation(int emailId, int subEmailId, int userId) throws SQLException;
	List<Email> getRelationObjects(int emailId, int userId) throws SQLException;
	boolean deleteRelation(int emailId, int subEmailId, int userId) throws SQLException;
}

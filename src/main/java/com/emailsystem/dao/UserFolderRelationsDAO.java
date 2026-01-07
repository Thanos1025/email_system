package main.java.com.emailsystem.dao;

import java.sql.SQLException;
import java.util.List;

import main.java.com.emailsystem.model.Folder;
import main.java.com.emailsystem.model.User;

public interface UserFolderRelationsDAO extends CommonRelationDAO<Folder>{
	boolean deleteUserFolderRelation(int folderId) throws SQLException;
}

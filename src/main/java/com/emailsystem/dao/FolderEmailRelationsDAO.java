package main.java.com.emailsystem.dao;

import java.sql.SQLException;
import java.util.List;

import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.Folder;

public interface FolderEmailRelationsDAO extends CommonRelationDAO<Email>{
	boolean deleteFolderEmailRelationByEmailIdAndFolderId(int folderId, int emailId) throws SQLException;
	Folder getFolderByEmailIdAndUserId(int userId, int emailId) throws SQLException;
	int updateFolderEmailRelation(int folderId, int emailId, int previousFolderId) throws SQLException;
	boolean getIsRead(int folderId, int emailId) throws SQLException;
	int updateIsRead(int folderId, int emailId, boolean isRead) throws SQLException;
	Folder getPreviouseFolder(int currentFolderId, int emailId) throws SQLException;
}

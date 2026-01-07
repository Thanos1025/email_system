package main.java.com.emailsystem.dao;

import java.sql.SQLException;
import java.util.List;

import main.java.com.emailsystem.model.Email;
import main.java.com.emailsystem.model.Folder;

public interface FoldersDAO extends CommonDAO<Folder>{
	Folder getFolderByFolderNameAndUserId(String folderName, int userId) throws SQLException;
}

package main.java.com.emailsystem.dao;

import java.sql.SQLException;

import main.java.com.emailsystem.exception.EmailAddressAlreadyExistException;

public interface CommonDAO<T> {
	T save(T object) throws SQLException, EmailAddressAlreadyExistException;

	int update(T object, int objectId) throws SQLException;

	boolean delete(int objectId) throws SQLException;

	T get(int objectId) throws SQLException;
}

package main.java.com.emailsystem.exception;

public class CantDeleteDefaultFolderException extends Exception{
	public CantDeleteDefaultFolderException() {
		super();
	}
	public CantDeleteDefaultFolderException(String message) {
		super(message);
	}
}

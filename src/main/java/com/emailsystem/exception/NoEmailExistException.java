package main.java.com.emailsystem.exception;

public class NoEmailExistException extends Exception {
	public NoEmailExistException() {
		
	}
	public NoEmailExistException(String message) {
		super(message);
	}
}

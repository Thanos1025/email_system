package main.java.com.emailsystem.exception;

public class InvalidUserException extends Exception{
	public InvalidUserException() {
		
	}
	public InvalidUserException(String message) {
		super(message);
	}
}

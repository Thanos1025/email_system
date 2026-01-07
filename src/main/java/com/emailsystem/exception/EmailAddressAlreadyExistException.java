package main.java.com.emailsystem.exception;

public class EmailAddressAlreadyExistException extends Exception{
	public EmailAddressAlreadyExistException() {
		
	}
	public EmailAddressAlreadyExistException(String message) {
		super(message);
	}
}

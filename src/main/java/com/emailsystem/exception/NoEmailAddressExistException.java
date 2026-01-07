package main.java.com.emailsystem.exception;

public class NoEmailAddressExistException extends Exception{
	public NoEmailAddressExistException() {
		
	}
	public NoEmailAddressExistException(String message) {
		super(message);
	}
}

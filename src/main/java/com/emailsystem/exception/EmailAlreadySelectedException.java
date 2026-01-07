package main.java.com.emailsystem.exception;

public class EmailAlreadySelectedException extends Exception{
	public EmailAlreadySelectedException() {
		
	}
	public EmailAlreadySelectedException(String message) {
		super(message);
	}
}

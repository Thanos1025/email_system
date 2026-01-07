package main.java.com.emailsystem.enumpackage;

import java.util.Arrays;
import java.util.List;

public enum ConditionPoint {
	FROM("From", 0),
	SUBJECT("Subject", 1),
	BODY("Body", 2);
	private final int index;
	private final String message;
	private ConditionPoint(String message, int index) {
		this.index = index;
		this.message = message;
	}
	public int getIndex() {
		return this.index;
	}
	public String getMessage() {
		return this.message;
	}
	public static ConditionPoint fromIndex(int index) {
		for(ConditionPoint point: values()) {
			if(point.index == index) {
				return point;
			}
		}
		throw new IllegalArgumentException("Unknown index: " + index);
	}
	
	public static String messageFromIndex(int index) {
		for(ConditionPoint point: values()) {
			if(point.index == index) {
				return point.getMessage();
			}
		}
		throw new IllegalArgumentException("Unknown index: " + index);
	}
	public static List<ConditionPoint> getAll(){
		return Arrays.asList(values());
	}
}

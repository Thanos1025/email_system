package main.java.com.emailsystem.enumpackage;

import java.util.Arrays;
import java.util.List;

public enum ConditionOperator {
	CONTAINS("contains", 0),
	DOESNOTCONTAIN("does not contains", 1),
	BEGINSWITH("begins with", 2),
	ENDSWITH("ends with", 3),
	IS("is", 4),
	ISNOT("is not", 5);
	
	private final int index;
	private final String message;
	private ConditionOperator(String message, int index) {
		this.index = index;
		this.message =  message;
	}
	public int getIndex() {
		return this.index;
	}
	public String getMessage() {
		return this.message;
	}
	public static ConditionOperator fromIndex(int index) {
		for(ConditionOperator operator: values()) {
			if(operator.index == index) {
				return operator;
			}
		}
		throw new IllegalArgumentException("Unknown index: " + index);
	}
	
	public static String messageFromIndex(int index) {
		for(ConditionOperator operator: values()) {
			if(operator.index == index) {
				return operator.getMessage();
			}
		}
		throw new IllegalArgumentException("Unknown index: " + index);
	}
	
	public static List<ConditionOperator> getAll() {
		return Arrays.asList(values());
	}
}

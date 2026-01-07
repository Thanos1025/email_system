package main.java.com.emailsystem.enumpackage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public enum ConditionType {
	OR("Any of these conditions (OR)", 0),
	AND("All the conditions (AND)", 1),
	NOT("No conditions, All incoming emails", 2);

	private final String message;
	private final int index;
	ConditionType(String message, int index){
		this.message = message;
		this.index = index;
	}
	
	public String getMessage() {
		return this.message;
	}
	public static List<ConditionType> getAll(){
		return Arrays.asList(values());
	}
	public static String messageFromIndex(int index) {
		for(ConditionType type: values()) {
			if(type.index == index) {
				return type.getMessage();
			}
		}
		throw new IllegalArgumentException("Unknown index: " + index);
	}
	public int getIndex() {
		return this.index;
	}
}

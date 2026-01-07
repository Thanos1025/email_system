package main.java.com.emailsystem.model;

public class Condition {
	@Override
	public String toString() {
		return "Condition [conditionId=" + conditionId + ", conditionString=" + conditionString + ", conditionOperator="
				+ conditionOperator + ", conditionPoint=" + conditionPoint + ", filterId=" + filterId + "]";
	}
	private int conditionId;
	private String conditionString;
	private int conditionOperator;
	private int conditionPoint;
	private int filterId;
	
	public Condition(int conditionId, String conditionString, int conditionOperator,
			int conditionPoint, int filterId) {
		super();
		this.conditionId = conditionId;
		this.conditionString = conditionString;
		this.conditionOperator = conditionOperator;
		this.conditionPoint = conditionPoint;
		this.filterId = filterId;
	}
	
	public Condition(String conditionString, int conditionOperator,
			int conditionPoint, int filterId) {
		super();
		this.conditionString = conditionString;
		this.conditionOperator = conditionOperator;
		this.conditionPoint = conditionPoint;
		this.filterId = filterId;
	}
	
	public int getFilterId() {
		return filterId;
	}

	public void setFilterId(int filterId) {
		this.filterId = filterId;
	}

	public int getConditionId() {
		return conditionId;
	}
	public void setConditionId(int conditionId) {
		this.conditionId = conditionId;
	}
	public String getConditionString() {
		return conditionString;
	}
	public void setConditionString(String conditionString) {
		this.conditionString = conditionString;
	}
	public int getConditionOperator() {
		return conditionOperator;
	}
	public void setConditionOperator(int conditionOperator) {
		this.conditionOperator = conditionOperator;
	}
	public int getConditionPoint() {
		return conditionPoint;
	}
	public void setConditionPoint(int conditionPoint) {
		this.conditionPoint = conditionPoint;
	}
}

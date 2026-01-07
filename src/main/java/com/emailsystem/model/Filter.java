package main.java.com.emailsystem.model;

import java.util.List;

public class Filter {
	private int filterId;
	private String filterName;
	private  int conditionType;
	private int folderId;
	
	@Override
	public String toString() {
		return "Filter [filterId=" + filterId + ", filterName=" + filterName + ", conditionType=" + conditionType
				+ ", folderId=" + folderId + "]";
	}
	
	public Filter(int filterId, String filterName, int conditionType, int folderId) {
		super();
		this.filterId = filterId;
		this.filterName = filterName;
		this.conditionType = conditionType;
		this.folderId = folderId;
	}
	public Filter( String filterName, int conditionType, int folderId) {
		super();
		this.filterName = filterName;
		this.conditionType = conditionType;
		this.folderId = folderId;
	}
	public int getFolderId() {
		return folderId;
	}
	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}
	public int getFilterId() {
		return filterId;
	}
	public void setFilterId(int filterId) {
		this.filterId = filterId;
	}
	public String getFilterName() {
		return filterName;
	}
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	public int getConditionType() {
		return conditionType;
	}
	public void setConditionType(int conditionType) {
		this.conditionType = conditionType;
	}
}
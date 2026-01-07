package main.java.com.emailsystem.enumpackage;

public enum FolderType {
	INBOX(0),
	SENT(1),
	DRAFT(2),
	TRASH(3);
	
	private final int index;
	FolderType(int index){
		this.index = index;
	}
	
	public int getIndex() {
		return this.index;
	}
}

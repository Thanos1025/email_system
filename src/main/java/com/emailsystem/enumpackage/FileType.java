package main.java.com.emailsystem.enumpackage;

public enum FileType {
	USERS("/home/yogi-zstth008/Java_Eclipse/java_project_email_system_using_file_system/src/main/java/com/emailsystem/database/users.csv"),
	EMAILS("/home/yogi-zstth008/Java_Eclipse/java_project_email_system_using_file_system/src/main/java/com/emailsystem/database/emails.csv"),
	FOLDERS("/home/yogi-zstth008/Java_Eclipse/java_project_email_system_using_file_system/src/main/java/com/emailsystem/database/folders.csv"),
	USERFOLSERRELATION("/home/yogi-zstth008/Java_Eclipse/java_project_email_system_using_file_system/src/main/java/com/emailsystem/database/userFolderRealtions.csv"),
	FOLDEREMAILRELATION("/home/yogi-zstth008/Java_Eclipse/java_project_email_system_using_file_system/src/main/java/com/emailsystem/database/folderEmailRelation.csv"),
	EMAILSRECIPIENTSRELATION("/home/yogi-zstth008/Java_Eclipse/java_project_email_system_using_file_system/src/main/java/com/emailsystem/database/emailRecipientRelation.csv"),
	EMAILSSUBEMAILSRELATION("/home/yogi-zstth008/Java_Eclipse/java_project_email_system_using_file_system/src/main/java/com/emailsystem/database/emailsSubEmailsRelation.csv");
	
	private final String path ;
	private FileType(String path) {
		this.path = path;
	}
	public String getPath() {
		return this.path;
	}
}

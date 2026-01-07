package main.java.com.emailsystem.util;
import java.sql.*;

public class MySqlDbUtil {
	private static final String URL = "jdbc:mysql://localhost:3306/email_system?user=root&&password=";
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL);
	}
}

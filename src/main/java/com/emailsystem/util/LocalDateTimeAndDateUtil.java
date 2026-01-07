package main.java.com.emailsystem.util;

import java.time.*; 
import java.time.ZoneId;
import java.sql.*;

public class LocalDateTimeAndDateUtil {
	public static Timestamp convertLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
		return localDateTime!=null?Timestamp.valueOf(localDateTime):null;
	}
	
	public static LocalDateTime convertTimestampToLocalDateTime(Timestamp timeStamp) {
		return timeStamp!=null?timeStamp.toLocalDateTime():null;
	}
}
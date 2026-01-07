package main.java.com.emailsystem.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
	public String getShortDateFormat(LocalDateTime date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d");
		return date!=null?date.format(formatter):null;
	}
	public String getLongDateFormat(LocalDateTime date) {
		LocalDateTime now = LocalDateTime.now();
		Duration duration = Duration.between(date, now);
		
		String timeAgo;
		long hours = duration.toHours();
		long minutes = duration.toMinutes();
		
		if(hours > 24) {
			long days = duration.toDays();
			timeAgo = days + " days ago";
		}else if(hours >=1) {
			timeAgo = hours + " hours ago";
		}else {
			timeAgo = minutes + " minutes ago";
		}
		
		return getLongDateFormatwithOutTimeAgo(date)+" ("+timeAgo+")";
	}
	
	public String getLongDateFormatwithOutTimeAgo(LocalDateTime date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm a");
		return date.format(formatter);
	}
}

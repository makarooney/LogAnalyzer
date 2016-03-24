package loganalyzer;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Represents a single line from a log data file.
 */
public final class LogDataEntry implements LogData{
	
	//assume this date format is supported. Assume 24-hour format (HH is 0-23)
	private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
	private SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy/MM/dd");
	
	private Date dateTime;
	private String userIp;
	private String action;
	private String domain;
	
	public LogDataEntry(String logLine) throws  ParseException {
		tokenizeLogEntry(logLine);
	}
	
	/**
	 * Internal helper method for tokenizing a log entry (i.e. a single line from a log file).
	 * @param logLine Will be of the format [Timestamp] [IP] [GET/POST action] [domain]
	 * @throws ParseException is thrown if date and time format is not the expected "yyyy/MM/dd HH:mm:ss"
	 */
	private void tokenizeLogEntry(String logLine) throws ParseException {
		String [] logData = logLine.split(",");
		if (logData.length != 4) {
			throw new IllegalArgumentException("Incorrect log format. Entry must contain 4 columns.");
		} else {
			dateTime = parseDateToExpectedFormat(logData[0], dateTimeFormat);
			userIp = logData[1].trim();
			action = logData[2].trim();
			domain = logData[3].trim();
		}
	}
	
	/**
	 * Parser will not automatically throw exception if invalid format detected. Instead, it will
	 * merely stop parsing before the end of the string. 
	 * 
	 * This helper method checks to see if parsing was successful by making sure the parser index
	 * reaches the end of the string to parse.
	 */
	private Date parseDateToExpectedFormat(String dateToBeFormatted, SimpleDateFormat expectedFormat) throws ParseException {
		ParsePosition p = new ParsePosition(0);
		expectedFormat.setLenient(false);
		dateTime = expectedFormat.parse(dateToBeFormatted, p);
		
		// Check to make sure parsed properly since parsing may not use the whole parsed string.
		// Force a ParseException to be thrown in this case.
		if (p.getIndex() < dateToBeFormatted.length()) {
			throw new ParseException(dateToBeFormatted, p.getIndex());
		}
		
		return dateTime;
	}
	
	@Override

	/**
	 * Expect data in format "IP Timestamp Action Domain"
	 * The Timestamp will have the minute and seconds set to "00" as per requirements to
	 * group items in hourly buckets.
	 */
	public String userData() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(dateTime);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		String newTimeStamp = dateTimeFormat.format(calendar.getTime()).trim();
		
		return userIp + " " + newTimeStamp + " " + action;
	}

	public String domain() {
		return domain;
	}
	
	@Override
	/**
	 * Helper method that simply returns a string with [Date] [Domain]. 
	 */
	public String domainData() {
		String dateOnly = entryDate();
		return dateOnly + " " + domain;
	}
	
	@Override
	/**
	 * Helper method that simply returns the date portion of the Timestamp from log file entry.
	 */
	public String entryDate() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(dateTime);
		String dateOnly = dateOnlyFormat.format(calendar.getTime()).trim();
		
		return dateOnly;
	}

}

package loganalyzer;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Test;

public final class LogDataEntryTest {
	private String sampleLogEntry = "2014/11/24 09:01:01, 10.0.0.1, GET, google.com";
	private String logEntry_InvalidDateFormat = "11/24/2014 09:01:01, 10.0.0.1, GET, google.com";
	private String logEntry_tooFewArguments = "2014/11/24 09:01:01, 10.0.0.1, GET";
	private LogDataEntry logEntry;
	
	@Test(expected=ParseException.class)
	public void LogDataEntryTest_invalidDateFormat() throws  ParseException{
		logEntry = new LogDataEntry(logEntry_InvalidDateFormat);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void LogDataEntryTest_invalidNumberOfDataColumns() throws ParseException {
		logEntry = new LogDataEntry(logEntry_tooFewArguments);
	}
	
	@Test
	public void userDataTest() throws  ParseException {
		String expectedResult = "10.0.0.1 2014/11/24 09:00:00 GET";
		logEntry = new LogDataEntry(sampleLogEntry);
		try {
			assertTrue(logEntry.userData().equals(expectedResult));
		} catch (AssertionError testFail) {
			System.out.println("Expected: "+ expectedResult);
			System.out.println("Actual result: "+ logEntry.userData());
			fail();
		}		
	}
	
	@Test
	public void domainDataTest() throws  ParseException {
		String expectedResult = "2014/11/24 google.com";
		logEntry = new LogDataEntry(sampleLogEntry);
		
		try {
			assertTrue(logEntry.domainData().equals(expectedResult));
		} catch (AssertionError testFail) {
			System.out.println("Expected: " + expectedResult);
			System.out.println("Actual result: " + logEntry.domainData());
			fail();
		}
	}
	
	@Test
	public void logDateTest() throws ParseException {
		logEntry = new LogDataEntry(sampleLogEntry);
		String expectedResult = "2014/11/24";
		
		try {
			assertTrue(logEntry.entryDate().equals(expectedResult));
		} catch (AssertionError testFail) {
			System.out.println("Expected: " + expectedResult);
			System.out.println("Actual result: " + logEntry.entryDate());
			fail();
		}
	}
	
	
}

package loganalyzer;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public final class LogUsageDataTest {

	//need to capture System.out.println output
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	
//	@Before
//	public void setupStreams() {
//		System.setOut(new PrintStream(outContent));
//		System.setErr(new PrintStream(errContent));
//	}
//
//	@After
//	public void cleaupStreams() {
//		System.setOut(null);
//		System.setErr(null);
//	}
	@Test
	public void displayUserLogData() throws FileNotFoundException, UnsupportedEncodingException {
		createSimpleDataFile("test-usage.txt");
		LargeLogFile logFile = new LargeLogFile("test-usage.txt");
		
		LogUsageData displayDailyLogs = new LogUsageData(logFile);
		
		displayDailyLogs.displayAllData();
		//fail("Not yet implemented");
	}
	
	private void createSimpleDataFile(String fileName) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		writer.println("2014/11/24 09:01:01, 10.0.0.1, GET, google.com");
		writer.println("2014/11/24 09:11:01, 10.0.0.1, GET, amazon.com");
		writer.println("2014/11/24 09:01:01, 10.0.0.2, GET, amazon.com");
		
		writer.println("2014/11/25 23:01:01, 10.0.0.1, POST, github.com");
		writer.println("2014/11/25 13:01:01, 10.0.0.1, GET, theonion.com");
		writer.println("2014/11/25 14:11:01, 10.0.0.1, GET, theonion.com");
		
		writer.close();
	}
}

package loganalyzer;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public final class LargeLogFileTest {
	private String sampleLogEntry = "2014/11/24 09:01:01, 10.0.0.1, GET, google.com";
	
	@Test(expected=FileNotFoundException.class)
	public void largeLogFileTest_InvalidFilePath() throws FileNotFoundException{
		@SuppressWarnings("unused")
		LargeLogFile invalidLogFile = new LargeLogFile("");
	}
	
	@Test
	public void readLineTest_OpenSimipleFile() throws IOException {
		createSimpleDataFile("test.txt");
		LargeLogFile simpleLogFile = new LargeLogFile("test.txt");
		
		// First line should be empty
		assertTrue(simpleLogFile.readLine().equals(""));
		// Second line expected to be known log entry
		assertTrue(simpleLogFile.readLine().equals(sampleLogEntry));
		// File ends here, so expect ""
		assertTrue(simpleLogFile.readLine().equals(""));
		// File already ended, so this should be a no-op
		assertTrue(simpleLogFile.readLine().equals(""));
	}
	
	private void createSimpleDataFile(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		writer.println("");
		writer.println(sampleLogEntry);
		writer.close();
	}
}

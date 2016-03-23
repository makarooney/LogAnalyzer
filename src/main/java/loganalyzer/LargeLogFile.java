package loganalyzer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Wrapper for effectively reading a large file line by line. 
 */
public final class LargeLogFile implements LargeFile{
	private FileInputStream largeFileStream;
	private Scanner largeFileScanner;
	
	/**
	 * 
	 * @param filePath
	 * @throws FileNotFoundException
	 */
	public LargeLogFile(String filePath) throws FileNotFoundException {
		largeFileStream = new FileInputStream(filePath);
		largeFileScanner = new Scanner(largeFileStream, "UTF-8");
	}

	@Override
	public String readLine() throws IOException {
		String logLine = "";
		try {
			if (largeFileScanner.hasNextLine()) {
				logLine = largeFileScanner.nextLine();
			} else {
				closeStreams();
			}
		} catch (IllegalStateException ex){
			if (ex.getMessage().equals("Scanner closed"))
			{
				// User tried to keep reading even after end of file reached
				// and scanner was closed, so treat this as a no-op and do nothing.
			} else {
				throw ex; 
			}
		}
		
		return logLine;
	}
	
	private void closeStreams() throws IOException {
		if(largeFileStream != null) {
			largeFileStream.close();
		}
		if (largeFileScanner != null) {
			largeFileScanner.close();
		}
	}

}

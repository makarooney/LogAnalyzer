package loganalyzer;

import java.io.IOException;

/**
 * Interface for reading a large file without fully loading in memory.
 */
public interface LargeFile {
	
	public String readLine() throws IOException;
	
}

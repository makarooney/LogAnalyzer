package loganalyzer;

/**
 * Interface for providing relevant info from log file.
 */
public interface LogData {
	public String userData();
	public String domainData();
	public String entryDate();
}

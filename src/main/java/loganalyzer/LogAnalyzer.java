package loganalyzer;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

/**
 * Entry point of LogAnalyzer class. Assumes user will pass in a path to a log file.
 */
public class LogAnalyzer {
	
	public static void main( String[] args )
    {		
		if(args.length != 1){
			displayUsage();
		}

		try {
			System.out.println("Trying to open file: " + args[0]);
			LargeLogFile logFile = new LargeLogFile(args[0]);	
			LogUsageData usageData = new LogUsageData(logFile);
			
			Long startTime = System.currentTimeMillis();
			
			usageData.displayAllData();
			
			Long endTime = System.currentTimeMillis();
			Long processingTime = endTime - startTime;	
			
			displayElapsedTime(processingTime);
			
		} catch (FileNotFoundException e) {
			System.out.println("You entered the following invalid path: " + args[0]);
			displayUsage();
		} catch( Exception e)
		{
			e.printStackTrace(System.out);
		}
    	
    }
	
	private static void displayElapsedTime(Long elapsedTime) {
		long hours = TimeUnit.MILLISECONDS.toHours(elapsedTime);
		
		long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime - TimeUnit.HOURS.toMinutes(hours));
		
		long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime - TimeUnit.HOURS.toMillis(hours) 
													- TimeUnit.MINUTES.toMillis(minutes));
		long ms = TimeUnit.MILLISECONDS.toMillis(elapsedTime - TimeUnit.HOURS.toMillis(hours) 
												- TimeUnit.MINUTES.toMillis(minutes) 
												- TimeUnit.SECONDS.toMillis(seconds));
		
		System.out.println("Elapsed time is: " + hours + " hrs, " + minutes + " mins, " + seconds + " sec, " + ms + " ms.");
	}
	
	private static void displayUsage() {
		System.out.println("Usage: LogAnayzer \"<log path>\"");
	}
}

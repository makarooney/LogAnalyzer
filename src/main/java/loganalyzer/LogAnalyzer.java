package loganalyzer;

import java.io.FileNotFoundException;

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
			//Double processingTimeSeconds = (double) (processingTime/1000);
			
			System.out.println("Processing time: " + processingTime + "ms");
			
		} catch (FileNotFoundException e) {
			System.out.println("You entered the following invalid path: " + args[0]);
			displayUsage();
		} catch( Exception e)
		{
			//suppress all other exceptions or log them.
		}
    	
    }
	
	private static void displayUsage() {
		System.out.println("Usage: LogAnayzer \"<log path>\"");
	}
}

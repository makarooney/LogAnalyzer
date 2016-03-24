package loganalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Class that only displays log usage data in desired format, as per requirements.
 */
public final class LogUsageData implements UsageData{
	
	private LargeLogFile logDataFile;
	
	// Hash map used for storing IP usage and later for display purposes
	private Map<String, Integer> userDataHashMap;
	
	// Data structures needed for displaying second most popular domain
	private Map<String, PopularDomain> domainHashMap;
	private PriorityQueue<PopularDomain> popularDomainsMinHeap;
	private final int HEAP_LIMIT = 2;
	private List<PopularDomain> allSecondPopularDomains;
	
	public LogUsageData(LargeLogFile logFile) {
		this.logDataFile = logFile;
		userDataHashMap = new HashMap<String, Integer>();
		domainHashMap = new HashMap<String, PopularDomain>();
		// Heap ordering Comparator is set to increasing order the "count" field in the stored objects
		popularDomainsMinHeap = new PriorityQueue<PopularDomain>((a, b) -> a.count - b.count);
		allSecondPopularDomains = new ArrayList<PopularDomain>();
	}
	
	public void displayAllData(){
		try {
			String currentLine = logDataFile.readLine();
			LogDataEntry logEntry = new LogDataEntry(currentLine);
			
			while (! currentLine.equals("")) {
				// Update current entry info
				logEntry = new LogDataEntry(currentLine);

				// Update counts in hash structures
				updateUserDataHashMap(logEntry);
				updateDomainHashMap(logEntry);
				
				currentLine = logDataFile.readLine();
			}
			
			// As per requirements, first show all usage counts for IPs based on Timestamp using hourly buckets
			displayUserLogData();
			// Find all second most popular domains and store them in ordered list sorted by date
			createPopularDomainsList();
			// Display second most popular domain
			displaySecondPopularDomainData();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	

	/**
	 * Method for creating list of second most popular daily domains.
	 * 
	 * Sort domain entries from hashmap using natural ordering of String keys which start by Date. 
	 * This means that the entries will be listed as {day1 day1 day1 day2 day2 day3 ...}
	 * I.e. whenever the day changes, this is an inflection point. 
	 * 
	 * For each day, a min heap of size 2 of popular domains is maintained. At the end of the day when inflection
	 * point is detected), the first item of the min heap will be what we're looking for: the second most popular 
	 * domain of that day. 
	 * 
	 * This item is them stored in the ArrayList to be displayed later. 
	 * 
	 * The MinHeap data structure is reset for the next day.
	 */
	private void createPopularDomainsList() {

		// Sort domain hash keys according to their natural ordering
		List<String> sortedKeys = new ArrayList<String>(domainHashMap.keySet());
		Collections.sort(sortedKeys);
				
		// Get the first item first just to set the currentDate
		String currentDate = "";
		if (sortedKeys.size() > 0) {
			currentDate = domainHashMap.get(sortedKeys.get(0)).date;
			updateDomainHeapIfApplicable(sortedKeys.get(0));
		}
		
		String nextDate = "";
		for (int i = 1; i < sortedKeys.size(); i++) {
			
			String key = sortedKeys.get(i);
			nextDate = domainHashMap.get(key).date;
			
			// For each domain used this day, see if its count is higher than the 
			// first element of the min heap and replace if that's the case.
			if(nextDate.equals(currentDate)) {
				updateDomainHeapIfApplicable(key);
			} 
			else {
				// Inflection point detected: New day is foud so must find 2nd most popular domain for current date
				allSecondPopularDomains.add(popularDomainsMinHeap.peek());
				//reset heap
				popularDomainsMinHeap = new PriorityQueue<PopularDomain>((a, b) -> a.count - b.count);
				
				currentDate = nextDate;
				updateDomainHeapIfApplicable(key);
			}
		}
		
		allSecondPopularDomains.add(popularDomainsMinHeap.peek());
	}

	/**
	 * Increment user IP count
	 */
	private void updateUserDataHashMap(LogDataEntry logEntry) {
		String userDataKey = logEntry.userData();
		Integer currentItemCount = userDataHashMap.get(userDataKey);
		
		if (currentItemCount == null) {
			userDataHashMap.put(userDataKey, 1);
		} 
		else {
			userDataHashMap.put(userDataKey, currentItemCount+1);
		}
	}
	
	/**
	 * Increment domain counts. 
	 */
	public void updateDomainHashMap(LogDataEntry logEntry){
		String domainDataKey = logEntry.domainData();
		PopularDomain domain = domainHashMap.get(domainDataKey);
		
		// If Domain object already in hash map, increment count. Otherwise, add with count set to 1.
		if (domain == null) {
			domainHashMap.put(domainDataKey, new PopularDomain(logEntry.domain(), logEntry.entryDate(), 1));
		}
		else {
			//increment count since it's a private class and have access to internal members
			domainHashMap.get(domainDataKey).count +=1;
		}
	}
	
	/**
	 * Method that maintains a min heap of limited size (in this case 2). 
	 * 
	 * If time permits, this should be extracted into its own data structure class that implements
	 * the PriorityQueue interface but provides more APIs for functions needed here, such as 
	 * updating a heap element with automatic rebalancing without needing to pop() first.
	 */
	public void updateDomainHeapIfApplicable(String domainDataKey){
		//check min heap. If first two entries of the day, add them to heap automatically
		if (popularDomainsMinHeap.size() < HEAP_LIMIT) {
			PopularDomain addToHeap = domainHashMap.get(domainDataKey);
			PopularDomain popularDomain = new PopularDomain(addToHeap.domain, addToHeap.date, addToHeap.count);
			
			// If item with this key has already been added, remove it and add it again. Otherwise, just add new item.
			if(popularDomainsMinHeap.peek() != null && popularDomain.getKey().equals(popularDomainsMinHeap.peek().getKey()) ) {
				// Note that it must be removed first before the update since the Java PriorityQueue implementation
				// doesn't automatically rebalance heap after an update. 
				popularDomainsMinHeap.poll();
				popularDomainsMinHeap.add(popularDomain);
			} 
			else {
				popularDomainsMinHeap.add(popularDomain);
			}
			return;
		}
		
		// If heap already contains 2 items, check to see if one of them needs updating and do so.
		PopularDomain toUpdate = null;
		//Check if this item is already in queue. If so, update count.
		for (PopularDomain p : popularDomainsMinHeap) {
			if (domainDataKey.equals((p.getKey()))) {
				toUpdate = p;
				break;
			}
		}
		
		if (toUpdate != null) {
			popularDomainsMinHeap.remove(toUpdate);
			popularDomainsMinHeap.add(new PopularDomain(toUpdate.domain, toUpdate.date, toUpdate.count));
			return;
		}

		// For any update to the Domain Hash Map, check if it's bigger than the first heap entry and that keys are different.
		// If so, this would mean a new domain that's either the most popular or second most popular.
		// Remove old entry and and add the new one. 
		if (domainHashMap.get(domainDataKey).count > popularDomainsMinHeap.peek().count && 
				!domainDataKey.equals(popularDomainsMinHeap.peek().getKey())) {
			
			PopularDomain newPopularDomain = new PopularDomain(domainHashMap.get(domainDataKey).domain, 
					domainHashMap.get(domainDataKey).date, 
					domainHashMap.get(domainDataKey).count);
			
			popularDomainsMinHeap.poll();
			popularDomainsMinHeap.add(newPopularDomain);
		}
	}

	/**
	 * Displaying all data in user IP hash map using the natural String ordering of the keys.
	 */
	private void displayUserLogData() {
		List<String> sortedKeys = new ArrayList<String>(userDataHashMap.keySet());
		Collections.sort(sortedKeys);
		sortedKeys.forEach(key -> System.out.println(key + " " + userDataHashMap.get(key)));
	}

	/**
	 * Display the second most popular domain for each day. Entries should already be sorted in increasing order by Date.
	 */
	private void displaySecondPopularDomainData() {
		allSecondPopularDomains.forEach(popDomainItem -> System.out.println(popDomainItem.date + " " + popDomainItem.domain + " " + popDomainItem.count));
	}
	

	/**
	 * Internal helper class to LogUsageData for storing Domain data.
	 */
	private final class PopularDomain  {
		private String domain;
		private String date;
		private Integer count;
		
		public PopularDomain(String domain, String date, Integer count){
			this.domain = domain;
			this.date = date;
			this.count = count;
		}
		
		public String getKey() {
			return date + " " + domain;
		}
	}
}


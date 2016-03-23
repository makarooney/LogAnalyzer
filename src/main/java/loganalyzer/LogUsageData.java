package loganalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public final class LogUsageData implements UsageData{
	
	private LargeLogFile logDataFile;
	
	private Map<String, Integer> userDataHashMap;
	
	private Map<String, PopularDomain> domainHashMap;
	private PriorityQueue<PopularDomain> popularDomainsMinHeap;
	private final int HEAP_LIMIT = 2;
	private List<PopularDomain> allSecondPopularDomains;
	
	public LogUsageData(LargeLogFile logFile) {

		this.logDataFile = logFile;
		
		userDataHashMap = new HashMap<String, Integer>();
		domainHashMap = new HashMap<String, PopularDomain>();
		popularDomainsMinHeap = new PriorityQueue<PopularDomain>((a, b) -> a.count - b.count);
		
		allSecondPopularDomains = new ArrayList<PopularDomain>();
	}
	
	public void displayAllData(){
		try {
			String currentLine = logDataFile.readLine();
			LogDataEntry logEntry = new LogDataEntry(currentLine);
			
			while (! currentLine.equals("")) {
				// update current entry info
				logEntry = new LogDataEntry(currentLine);

				updateUserDataHashMap(logEntry);
				updateDomainHashMap(logEntry);
				
				currentLine = logDataFile.readLine();
			}
			
			//sort DomainHash by keyset into a sorted list
			//Loop through list, and keep track of 
			displayUserLogData();
			updateMinHeap();
			displaySecondPopularDomainData();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	
	private void updateMinHeap() {
		List<String> sortedKeys = new ArrayList<String>(domainHashMap.keySet());
		Collections.sort(sortedKeys);
				
		String currentDate = "";
		if (sortedKeys.size() > 0) {
			currentDate = domainHashMap.get(sortedKeys.get(0)).date;
			updateDomainHeapIfApplicable(sortedKeys.get(0));
		}
		
		String nextDate = "";
		for (int i = 1; i < sortedKeys.size(); i++) {
			
			String key = sortedKeys.get(i);
			nextDate = domainHashMap.get(key).date;
			 
			if(nextDate.equals(currentDate)) {
				updateDomainHeapIfApplicable(key);
			} 
			else {
				allSecondPopularDomains.add(popularDomainsMinHeap.peek());
				//reset heap
				popularDomainsMinHeap = new PriorityQueue<PopularDomain>((a, b) -> a.count - b.count);
				currentDate = nextDate;
				updateDomainHeapIfApplicable(key);
			}
		}
		
		allSecondPopularDomains.add(popularDomainsMinHeap.peek());
	}

	private void updateUserDataHashMap(LogDataEntry logEntry) {
		//Increment Part1
		String userDataKey = logEntry.userData();
		Integer currentItemCount = userDataHashMap.get(userDataKey);
		if (currentItemCount == null) {
			userDataHashMap.put(userDataKey, 1);
		} 
		else {
			userDataHashMap.put(userDataKey, currentItemCount+1);
		}
	}
		
	public void updateDomainHashMap(LogDataEntry logEntry){
		//Increment Part2
		String domainDataKey = logEntry.domainData();
		PopularDomain domain = domainHashMap.get(domainDataKey);
		if (domain == null) {
			domainHashMap.put(domainDataKey, new PopularDomain(logEntry.domain(), logEntry.entryDate(), 1));
		}
		else {
			//domainHashMap.put(domainDataKey,currentDomainCount+ 1);
			domainHashMap.get(domainDataKey).count +=1;//increment count since it's a private class and have access to internal members
		}
		
		//updateDomainHeapIfApplicable(domainDataKey);
	}
	
	public void updateDomainHeapIfApplicable(String domainDataKey){
		//check min heap. If first two entries of the day, add them to heap automatically
		if (popularDomainsMinHeap.size() < HEAP_LIMIT) {
			PopularDomain addToHeap = domainHashMap.get(domainDataKey);
			PopularDomain popularDomain = new PopularDomain(domainDataKey, addToHeap.date, addToHeap.count);
			
			//If item with this key has already been added, remove it and add it again
			if(popularDomainsMinHeap.peek() != null && popularDomain.getKey().equals(popularDomainsMinHeap.peek().getKey()) ) {
				popularDomainsMinHeap.poll();
				popularDomainsMinHeap.add(popularDomain);
			} 
			else {
				popularDomainsMinHeap.add(popularDomain);
			}
			return;
		}
		
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
		// If so, remove that and add the new one. 
		if (domainHashMap.get(domainDataKey).count > popularDomainsMinHeap.peek().count && 
				!domainDataKey.equals(popularDomainsMinHeap.peek().getKey())) {
			
			PopularDomain newPopularDomain = new PopularDomain(domainHashMap.get(domainDataKey).domain, 
					domainHashMap.get(domainDataKey).date, 
					domainHashMap.get(domainDataKey).count);
			
			popularDomainsMinHeap.poll();
			popularDomainsMinHeap.add(newPopularDomain);
		}
	}

	
	private void displayUserLogData() {
		List<String> sortedKeys = new ArrayList<String>(userDataHashMap.keySet());
		Collections.sort(sortedKeys);
		sortedKeys.forEach(key -> System.out.println(key + " " + userDataHashMap.get(key)));
	}

	private void displaySecondPopularDomainData() {
		allSecondPopularDomains.forEach(popDomainItem -> System.out.println(popDomainItem.domain + " " + popDomainItem.count));
	}
	

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


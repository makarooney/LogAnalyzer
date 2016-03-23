package loganalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public final class LogUsageData implements UsageData{
	
	private LargeLogFile logDataFile;
	
	private Map<String, Integer> UserDataHashMap;
	
	private Map<String, Integer> DomainHitHashMap;
	private PriorityQueue<PopularDomain> PopularDomainsMinHeap;
	private final int HEAP_LIMIT = 2;
	private List<PopularDomain> AllSecondPopularDomains;
	
	public LogUsageData(LargeLogFile logFile) {

		this.logDataFile = logFile;
		
		UserDataHashMap = new HashMap<String, Integer>();
		DomainHitHashMap = new HashMap<String, Integer>();
		PopularDomainsMinHeap = new PriorityQueue<PopularDomain>((a, b) -> a.count() - b.count());
		
		AllSecondPopularDomains = new ArrayList<PopularDomain>();
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
			AllSecondPopularDomains.add(PopularDomainsMinHeap.peek());
			displaySecondPopularDomainData();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	
	private void updateUserDataHashMap(LogDataEntry logEntry) {
		//Increment Part1
		String userDataKey = logEntry.userData();
		Integer currentItemCount = UserDataHashMap.get(userDataKey);
		if (currentItemCount == null) {
			UserDataHashMap.put(userDataKey, 1);
		} 
		else {
			UserDataHashMap.put(userDataKey, currentItemCount+1);
		}
	}
		
	public void updateDomainHashMap(LogDataEntry logEntry){
		//Increment Part2
		String domainDataKey = logEntry.domainData();
		Integer currentDomainCount = DomainHitHashMap.get(domainDataKey);
		if (currentDomainCount == null) {
			DomainHitHashMap.put(domainDataKey, 1);
		}
		else {
			DomainHitHashMap.put(domainDataKey,currentDomainCount+ 1);
		}
		
		updateDomainHeapIfApplicable(domainDataKey);
	}
	
	public void updateDomainHeapIfApplicable(String domainDataKey){
		//check min heap. If first two entries of the day, add them to heap automatically
		if (PopularDomainsMinHeap.size() < HEAP_LIMIT) {
			PopularDomain popularDomain = new PopularDomain(domainDataKey, DomainHitHashMap.get(domainDataKey));
			
			//If item with this key has already been added, remove it and add it again
			if(PopularDomainsMinHeap.peek() != null && popularDomain.getKey().equals(PopularDomainsMinHeap.peek().getKey()) ) {
				PopularDomainsMinHeap.poll();
				PopularDomainsMinHeap.add(popularDomain);
			} 
			else {
				PopularDomainsMinHeap.add(popularDomain);
			}
			return;
		}
		
		PopularDomain toUpdate = null;
		//Check if this item is already in queue. If so, update count.
		for (PopularDomain p : PopularDomainsMinHeap) {
			if (p.getKey().equals(domainDataKey)) {
				toUpdate = p;
				break;
			}
		}
		
		if (toUpdate != null) {
			PopularDomainsMinHeap.remove(toUpdate);
			PopularDomainsMinHeap.add(new PopularDomain(toUpdate.getKey(), DomainHitHashMap.get(toUpdate.getKey())));
			return;
		}

		// For any update to the Domain Hash Map, check if it's bigger than the first heap entry and that keys are different.
		// If so, remove that and add the new one. 
		if (DomainHitHashMap.get(domainDataKey) > PopularDomainsMinHeap.peek().count() && 
				!domainDataKey.equals(PopularDomainsMinHeap.peek().getKey())) {
			
			PopularDomain newPopularDomain = new PopularDomain(domainDataKey, DomainHitHashMap.get(domainDataKey));
			PopularDomainsMinHeap.poll();
			PopularDomainsMinHeap.add(newPopularDomain);
		}
	}

	
	private void displayUserLogData() {
		List<String> sortedKeys = new ArrayList<String>(UserDataHashMap.keySet());
		Collections.sort(sortedKeys);
		sortedKeys.forEach(key -> System.out.println(key + " " + UserDataHashMap.get(key)));
	}

	private void displaySecondPopularDomainData() {
		AllSecondPopularDomains.forEach(popDomainItem -> System.out.println(popDomainItem.getKey() + " " + popDomainItem.count()));
	}
	
	/**
	 * Private helper class for Second popular domain Min heap trick
	 *
	 */
	private final class PopularDomain  {
		private String domainKey;
		private Integer domainCount;
		
		public PopularDomain(String domainKey, Integer domainCount){
			this.domainKey = domainKey;
			this.domainCount = domainCount;
		}
		
		public String getKey() {
			return domainKey;
		}

		public Integer count() {
			return domainCount;
		}
	}
}


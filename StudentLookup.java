/**
 * Your implementation of the LookupInterface.  The only public methods
 * in this class should be the ones that implement the interface.  You
 * should write as many other private methods as needed.  Of course, you
 * should also have a public constructor.
 * 
 * @author Zachary Zampa
 */
  



 
public class StudentLookup implements LookupInterface {
	private Dictionary<DataWrapper, Integer> dict;
	private StorageArrayUtil<DataWrapper> store;

	
	
	/**
	 * Constructor for StudentLookup
	 */
	public StudentLookup() {
		dict = new Dictionary<>();
		store = new StorageArrayUtil<>();
	}

	@Override
	public void addString(int amount, String s) {
		DataWrapper newEntry = new DataWrapper(s, amount);  // wrap the data so it can be worked on
		
		if (dict.getValue(newEntry) != null) {
			int count = dict.getValue(newEntry) + amount;
			// dictionary already contains value -- re-add value but replace amount
			dict.add(newEntry, count);
			store.updateEntry(new DataWrapper(s, count));
		} else {
			// dictionary does not contain value already  -- add original value
			dict.add(newEntry, amount);  // add just new amount
			store.add(newEntry);
		}
	}

	@Override
	public int lookupCount(String s) {
		return lookupCount(new DataWrapper(s));
	}
	
	/**
	 * Performs same duty as standard lookupCount but with a DataWrapper passed through
	 * @param entry DataWrapper to look up
	 * @return how often it occurs
	 */
	private int lookupCount(DataWrapper entry) {
		if (dict.getValue(entry) == null) {
			// value does not exist
			return 0;
		}
		
		return dict.getValue(entry);
	}
	
	@Override
	public String lookupPopularity(int n) {
		return store.getNMax(n).getData();
	}

	@Override
	public int numEntries() {
		return dict.getSize();
	}
    
	
}




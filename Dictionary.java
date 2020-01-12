/**
 * A hashed, linear probing dictionary
 * Quadratic probing methods are available and commented out
 * For the capacity of the default dictionary, linear was faster
 * @author Zachary Zampa
 * @since 2019/05/01
 *
 * @param <K>
 * @param <V>
 */


public class Dictionary<K, V> {

	// Dictionary Properties
	private int numberOfEntries;  // number of entries in the dictionary
	private static final int DEFAULT_CAPACITY = 1000000;  // default capacity -- must be prime
	private static final int MAX_CAPACITY = 10000000;  // max capacity = 10^7
	private TableEntry<K, V>[] dictionary;
	@SuppressWarnings("unused")
	private int tableSize;  // must be prime
	private static final int MAX_SIZE = 2 * MAX_CAPACITY;  // max capacity of hash table
	private boolean integrityFlag = false;   // checks the integrity of the table
	private static final double MAX_LOAD = 0.5;  // How much of the table can be filled


	/**
	 * Empty Constructor
	 */
	public Dictionary() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * Size based Constructor
	 * @param size size to make the dictionary
	 */
	public Dictionary(int size) {
		// check if size exceeds max Capacity
		capacityCheck(size);
		numberOfEntries = 0;

		// ensure tableSize is a prime number, and one under the Max size
		int tableSize = primeGenerator(size);
		sizeCheck(tableSize);

		// Cast new array
		@SuppressWarnings("unchecked")
		TableEntry<K, V>[] tmp = (TableEntry<K, V>[]) new TableEntry[tableSize];
		dictionary = tmp;
		integrityFlag = true;
	}

	/**
	 * Checks if the capacity exceeds the maximum capacity allowed
	 * @param capacity size of dictionary
	 */
	private void capacityCheck(int capacity) {
		if (capacity > MAX_CAPACITY) {
			// size exceeds max capacity
			System.out.printf("ERROR: Dictionary capacity [%d] exceeds limits", capacity);
			System.exit(1);  // end program with error code
		}
	}

	/**
	 * Check if the size exceeds the maximum size allowed
	 * @param size size of table
	 */
	private void sizeCheck(int size) {
		if (size > MAX_SIZE) {
			// size exceeds max capacity
			System.out.printf("ERROR: Dictionary size [%d] exceeds limits", size);
			System.exit(1);  // end program with error code
		}
	}

	/**
	 * Generate the next prime number after / = the number
	 * @param num integer
	 * @return next prime number
	 */
	private int primeGenerator(int num) {
		if (num % 2 == 0) {
			// even so make odd
			num++;
		}
		
		while (!checkPrime(num)) {
			// go odd to odd until prime
			num += 2;  
		}

		return num;
	}

	/**
	 * check if the number is prime
	 * @param num number to check
	 * @return true if prime
	 */
	private boolean checkPrime(int num) {
		boolean result;
		boolean finished = false;

		// check if 1 or even  -- not prime
		if (num == 1 || num % 2 == 0) {
			// not prime
			result = false; 
		} else if (num == 3 || num == 2) {
			// a prime
			result = true;
		} else {
			result = true; // assumes prime number
			for (int i = 3; !finished && (i * i <= num); i = i + 2) {
				if (num % i == 0) {
					// divisible; not prime
					result = false; 
					finished = true;
				}
			}
		}

		return result;
	}

	/**
	 * Check the initialization of the dictionary
	 */
	private void checkInit(){
		// check if dictionary is valid
		if (!integrityFlag) {
			// integrity flag is false
			System.out.println("The dictionary is corrupt");
			System.exit(1);  // exit with error code
		}
	}

	/**
	 * Add a key / value to the dictionary
	 * @param key to add
	 * @param value to add
	 * @return null if empty; else old value
	 */
	public V add(K key, V value) {
		checkInit();  // check initialization
		
		// check for potential null values
		if (key == null || value == null) {
			// null value attempted
			System.out.println("ERROR: Cannot add a null value");
			System.exit(1); // exit with an error code
		}

		V oldV;  // old value to return
		int index = collisionCheck(getHashIndex(key), key);  // figure out where the key should be

		if (dictionary[index] == null || dictionary[index].wasRemoved()) {
			// key not found -- insert
			dictionary[index] = new TableEntry<>(key, value);
			numberOfEntries++;
			oldV = null;  // null since no old value
		} else {
			// key was found -- replace
			oldV = dictionary[index].getValue();  // get old value
			dictionary[index].setValue(value);  // add new value
		}

		// check if dictionary can take more additions
		if (numberOfEntries > MAX_LOAD * dictionary.length) {
			enlargeDic();
		}

		return oldV;
	}

	/**
	 * Enlarge the size of the dictionary array
	 */
	private void enlargeDic() {
		TableEntry<K, V>[] oldDic = dictionary;
		int oldSize = dictionary.length;
		int newSize = primeGenerator(oldSize + oldSize);
		sizeCheck(newSize);

		@SuppressWarnings("unchecked")
		TableEntry<K, V>[] tmpDic = (TableEntry<K, V>[]) new TableEntry[newSize];
		dictionary = tmpDic;
		numberOfEntries = 0; // Reset to 0; since re-adding will increase this to correct number

		// Re-add all non-null and non-empty positions
		for (int i = 0; i < oldSize; i++) {
			if ((oldDic[i] != null) && oldDic[i].isHere()) {
				// not null or nonempty so add
				add(oldDic[i].getKey(), oldDic[i].getValue());	
			}
		} 
	}

	/**
	 * Remove the value that corresponds to the specified key
	 * @param key to look for
	 * @return the removed value
	 */
	public V remove(K key) {
		checkInit();
		V removedV = null;  // initially nothing is removed

		int index = getHashIndex(key);  // get hash index
		index = search(index, key);  // search and return true index

		if (index != -1) {
			// key is found -- remove
			removedV = dictionary[index].getValue();  // get value so it can be returned
			dictionary[index].setRemoved();  // set index to removed not null
			numberOfEntries--;  // lower number of entries in dictionary
		}

		return removedV;
	}

	/**
	 * Loop through removing every entry;
	 */
	public final void clear() {
		checkInit();

		for (int i = 0; i < dictionary.length; i++ ) {
			dictionary[i] = null;  // set each to null
		}

		numberOfEntries = 0;
	}

	/**
	 * Check if their are collisions and if so look for empty spot
	 * @param index index to add to
	 * @param key key to add
	 * @return free index or index containing same value as key
	 */
	private int collisionCheck(int index, K key) {
		int freeIndex = -1;
		boolean isSame = false;
//		int increment = 1;  // used with quadratic probing

		// search for an empty spot and if there is already the value
		while (!isSame && dictionary[index] != null) {
			if (dictionary[index].isHere()) {
				// an entry is here
				if (key.equals(dictionary[index].getKey())) {
					// key found and same value
					isSame = true;  
				} else {
					index = (index + 1) % dictionary.length;  // conduct linear probing
					// conduct quadratic probing
//					index = (index + increment) % dictionary.length;
//					increment = increment + 2;
				}
			} else {
				// an entry was removed from here; carry on, but save position
				if (freeIndex == -1) {
					// a free index has not been found yet
					freeIndex = index;
				}
				index = (index + 1) % dictionary.length; // conduct linear probing
				// conduct quadratic probing
//				index = (index + increment) % dictionary.length;
//				increment = increment + 2;
			}
		}

		if (freeIndex == -1 || isSame) {
			// no free indexes, or already contained in dictionary
			return index;
		} else {
			// not contained yet; and room exists
			return freeIndex;
		}
	}

	/**
	 * Search for the key in the dictionary
	 * @param index index to look for
	 * @param key key to look for
	 * @return true index of key or -1
	 */
	private int search(int index, K key) {
		boolean isHere = false;
//		int increment = 1;  // for quadratic probing
		int result = -1;

		while (!isHere && dictionary[index] != null) {
			if (dictionary[index].isHere() && dictionary[index].getKey().equals(key)) {
				// key was found in dictionary
				isHere = true; 
				result = index;  // set result to index
			} else {
				index = (index + 1) % dictionary.length;  // conduct linear probing
				// perform quadratic probing
//				index = (index + increment) % dictionary.length;
//				increment = increment + 2;
			}
		}

		return result;
	}

	/**
	 * Get the value of the desired key
	 * @param key key that corresponds to the value
	 * @return value from key
	 */
	public V getValue(K key) {
		checkInit();

		V result = null;
		int index = getHashIndex(key);
		index = search(index, key);

		if (index != -1) {
			// key found; get value
			result = dictionary[index].getValue();
		}

		return result;
	}

	/**
	 * Return if dictionary contains key
	 * @param key key too look for
	 * @return true if contained
	 */
	public boolean contains(K key) {
		return getValue(key) != null;  // if null then key was never found
	}

	/**
	 * Check if dictionary is empty
	 * @return true if empty
	 */
	public boolean isEmpty() {
		return numberOfEntries == 0;
	}

	/**
	 * Get the number of entries in dictionary
	 * @return size
	 */
	public int getSize() {
		return numberOfEntries;
	}
	

	/**
	 * Find the index the item belongs; uses hashing
	 * @param key item to look for
	 * @return hashed index
	 */
	private int getHashIndex(K key) {
		int index = key.hashCode() % dictionary.length;

		if (index < 0) {
			// out of bounds; add length
			index = index + dictionary.length;
		}

		return index;
	}

	

	private static class TableEntry<Ky, Va> {
		private Ky key;
		private Va value;
		private Status stat;
		private enum Status {CURRENT, REMOVED} // Possible status

		/**
		 * Table constructor
		 * @param key key to search for
		 * @param value value to search for
		 */
		private TableEntry(Ky key, Va value) {
			this.key = key;
			this.value = value;
			stat = Status.CURRENT;
		}

		/**
		 * Was removed from the dictionary
		 * @return sets spot to removed
		 */
		public boolean wasRemoved() {
			return stat == Status.REMOVED;
		}

		// getters and setters
		// set value
		private void setValue(Va newVal) {
			value = newVal;
		}

		// set state of the status of entry position to removed
		private void setRemoved()
		{
			key = null;
			value = null;
			stat = Status.REMOVED; // Entry is deleted from table
		} 

		/**
		 * Get the key
		 * @return
		 */
		private Ky getKey()
		{
			return key;
		} 

		/**
		 * Get the value
		 * @return
		 */
		private Va getValue()
		{
			return value;
		} 

		/**
		 * Return true if entry exists in the hash table
		 * @return
		 */
		private boolean isHere()
		{
			return stat == Status.CURRENT;
		} 


	}


}

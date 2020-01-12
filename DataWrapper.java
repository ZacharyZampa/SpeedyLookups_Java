/**
 * A wrapper to wrap the objects that are being used 
 * with an ADT that does not allow duplicates
 * @author Zachary Zampa
 * @since 2019/04/29
 *
 */
public class DataWrapper implements Comparable<DataWrapper>{

	private String data;  // data to store
	private int count;  // how many of this object are contained in the ADT entry
	
	/**
	 * Default Constructor - count to 0
	 * @param data data to add
	 */
	public DataWrapper(String data) {
		this(data, 0);  
	}
	
	/**
	 * Workhorse constructor
	 * @param data
	 * @param count
	 */
	public DataWrapper(String data, int count) {
		this.data = data;
		this.count = count;
	}

	@Override
	public String toString() {
		return data;
	}
	
	@Override
	public int hashCode() {
//		return data.hashCode();  // 1:56 runtime
		
		
		// sdbm hash   1:51 runtime
		int hash = 0;

		for(int i = 0; i < data.length(); i++) {
			hash = data.charAt(i) + (hash << 6) + (hash << 16) - hash;
		}

		return hash;
	}
	
	/**
	 * Check if two objects are equal
	 * @return true if the data equals each other
	 */
	public boolean equals(Object other) {
	      boolean result;
	      
	      if ((other == null) || (getClass() != other.getClass())) {
	         result = false;
	      } else {
	         result = data.equals(((DataWrapper)other).getData());
	      }
	      
	      return result;
	   }
	
	/**
	 * Check if object is equal to string data
	 * @return true if the data equals each other
	 */
	public boolean equals(String otherData) {
		return data.equals(otherData);
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
	
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int compareTo(DataWrapper other) {
		int result = Integer.compare(count, other.count);  // compares opposite of data to descend

		// If counts are equal, check data
		if (result == 0) {
			result = other.data.compareTo(data);
		}

		return result;
	}
	
	
	
	
	
}

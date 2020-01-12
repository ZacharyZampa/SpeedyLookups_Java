import java.util.Arrays;

/**
 * @author Zachary Zampa
 *
 */
public class MaxHeap<T extends Comparable<? super T>> {
	private T[] heap;
	private static final int DEFAULT_CAPACITY = 10000;  // default capacity
	private static final int MAX_CAPACITY = 10000000;  // max capacity = 10^7
	private int lastIndex;  // the index of the last element
	private boolean integrityFlag = false;   // checks the integrity of the heap
	private static final double MAX_LOAD = 0.75;  // How much of the table can be filled
	
	/**
	 * Default Constructor for Empty Maxheap
	 */
	public MaxHeap() {
		this(DEFAULT_CAPACITY);
	}
	
	/**
	 * Constructor for Max Heap with a given capacity
	 * @param capacity
	 */
	public MaxHeap(int capacity) {
		capacityCheck(capacity);
		
		// Cast new array
		@SuppressWarnings("unchecked")
		T[] tmp = (T[]) new Comparable[capacity + 1];
		heap = tmp;
		lastIndex = 0;  
		integrityFlag = true;  // everything is initialized; set flag to true
	}
	
	/**
	 * Checks if the capacity exceeds the maximum capacity allowed
	 * @param capacity size of heap
	 */
	private void capacityCheck(int capacity) {
		if (capacity > MAX_CAPACITY) {
			// size exceeds max capacity
			System.out.printf("ERROR: Heap capacity [%d] exceeds limits", capacity);
			System.exit(1);  // end program with error code
		}
	}

	
	public MaxHeap(MaxHeap<T> other) {
		T[] tmp = (T[]) Arrays.copyOf(other.heap, other.heap.length);
		heap = tmp;
		lastIndex = other.lastIndex;
		integrityFlag = other.integrityFlag;
	}

	/**
	 * CheckLoad of the heap array
	 */
	private void checkLoad() {
		// check if heap array can take more additions
		if (lastIndex < MAX_LOAD * heap.length) {
			return;
		}
		
		// Needs more room
		T[] tmpHeap = heap;
		int oldSize = heap.length;
		int newSize = oldSize * 2;
		sizeCheck(newSize);  // check if new size is under capacity
		heap = Arrays.copyOf(tmpHeap, newSize);
	}

	/**
	 * Check what size the heap is
	 * @param newSize
	 */
	private void sizeCheck(int newSize) {
		if (newSize > MAX_CAPACITY) {
			// size exceeds max capacity
			System.out.printf("ERROR: Heap capacity [%d] exceeds limits", newSize);
			System.exit(1);  // end program with error code
		}
	}

	/**
	 * Check the initialization of the dictionary
	 */
	private void checkInit(){
		// check if dictionary is valid
		if (!integrityFlag) {
			// integrity flag is false
			System.out.println("The Heap is corrupt");
			System.exit(1);  // exit with error code
		}
	}
	
	/**
	 * Add a new entry to the max heap
	 * @param newEntry entry to add
	 */
	public void add(T newEntry) {
		checkInit();
		int nextIndex = lastIndex + 1;
		int pIndex = parent(nextIndex);
		
		// compare going up tree while newentry is greater than the parent
		while (pIndex > 0 && newEntry.compareTo(heap[pIndex]) > 0) {
			heap[nextIndex] = heap[pIndex];
			nextIndex = pIndex;
			pIndex = parent(nextIndex);
		}
		
		heap[nextIndex] = newEntry;
		lastIndex++;
		checkLoad();
	}
	
	/**
	 * Update an existing entry
	 * Assumes that it does exist in max heap; works in conjunction with a dictionary
	 * @param newEntry
	 */
	public void updateEntry(T newEntry) {
		checkInit();
		
		
		// perform linear search through maxheap
		for(int i = 1; i <= lastIndex; i++) {
			if(heap[i].equals(newEntry)) {
				heap[i] = newEntry;
				reheap(1);
				break;
			}
		}
		
		
//		// perform linear search where left Child is 2*i and right child is 2*i+1
//		for (int i = 1; i <= lastIndex; i = 2*i) {
//			if (heap[i].equals(newEntry)) {
//				heap[i] = newEntry;
//				reheap(1);
//				return;
//			} 
//			// This is cuts the search short if it is taking too long and cannot be found
//			if (heap[i].compareTo(newEntry) > 0) {
//				break;
//			}
//		}
//		
//		for (int i = 1; i <= lastIndex; i = 2*i + 1) {
//			if (heap[i].equals(newEntry)) {
//				heap[i] = newEntry;
//				reheap(1);
//				return;
//			}
//		}
		
		
		
		
	}
		
	
	/**
	 * Transform a semi-heap into a max heap
	 * @param rootIndex index the root is in
	 */
	public void reheap(int rootIndex) {
		boolean finished = false;
		T lost = heap[rootIndex];
		int leftCIndex = leftChild(rootIndex);  // get left child node
		
		while (!finished && leftCIndex <= lastIndex) {
			int largeCIndex = leftCIndex;  // assume it is larger
			int rightCIndex = leftCIndex + 1;  // get right child node
			if (rightCIndex <= lastIndex && heap[rightCIndex].compareTo(heap[largeCIndex]) > 0) {
				largeCIndex = rightCIndex;
			}
			
			if (lost.compareTo(heap[largeCIndex]) < 0) {
				// lost node is less than node in largeCIndex
				heap[rootIndex] = heap[largeCIndex];  // set this into the root index
				rootIndex = largeCIndex;  // tick down to next index
				leftCIndex = leftChild(rootIndex);
			} else {
				// none are larger here, stop; lost node is larger
				finished = true;
			}
		}
		
		heap[rootIndex] = lost;  // this node is then the max
	}
	
	/**
	 * Clear all entries in the heap
	 */
	public void clear() {
		checkInit();
		
		while (lastIndex > -1) {
			heap[lastIndex] = null;
			lastIndex--;
		}
		
		lastIndex = 0;  // reset index to 0
	}
	
	/**
	 * Remove the maximum value from the heap
	 * @return Max Value in Heap
	 */
	public T removeMax() {
		checkInit();
		T root = null;
		
		if (!isEmpty()) {
			root = heap[1];  // store the max (top) value
			heap[1] = heap[lastIndex];  // replace max value with last leaf
			lastIndex--; 
			reheap(1);  // reheap to ensure 
		}
		
		return root;
	}
	
	/**
	 * Get the maximum value from the heap
	 * @return Max Value in Heap
	 */
	public T getMax() {
		checkInit();
		T root = null;
		
		if (!isEmpty()) {
			// not empty
			root = heap[1];
		}
		
		return root;
	}
	
	/**
	 * Get the nth most max item from the max heap
	 * @param n which most max to get
	 * @return N most max in heap
	 */
	public T getNMax(int n) {
		
		
		
		
		
		
		
		
		
		
		return null;  // TODO add real return statement
	}
	
	
	
	
	
	/**
	 * Get the size of the heap
	 * @return size of heap
	 */
	public int getSize() {
		return lastIndex;
	}
	
	/**
	 * Check if heap is empty
	 * @return true if empty
	 */
	public boolean isEmpty() {
		return lastIndex < 1;
	}

	/**
	 * Return parent of current index
	 * @param index current index
	 * @return parent's index
	 */
	private int parent(int index) {
		return index / 2;
	}

	/**
	 * Return the left child of current index
	 * @param index current index
	 * @return left child
	 */
	private int leftChild(int index) {
		return 2 * index; 
	}
	
	
	
	
	
}

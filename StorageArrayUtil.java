import java.util.Arrays;
import java.util.Collections;

/**
 * Array based storage
 * @author Zachary Zampa
 * @since 2019/05/03
 *
 * @param <T>
 */

// TODO try collections sort or Quicksort rather than heap and insertion


public class StorageArrayUtil<T extends Comparable<? super T>> {

	// Storage Properties
	private T[] storage;
	private int numberOfEntries;  // number of entries
	private static final int DEFAULT_CAPACITY = 100000;  // default capacity
	private static final int MAX_CAPACITY = 10000000;  // max capacity = 10^7
	private int sortTracker;   // number of additions since last sort
	private boolean everSort;  // ensure it is sorted at least once
	private static final double SORT_RATIO = 0.3;  // how much must be sorted to use insertion sort -- 90% sorted
	private static final double LOAD_RATIO = 0.9;  // how full the array can be before it must be resized -- 90% full



	/**
	 * Empty Constructor
	 */
	public StorageArrayUtil() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * Size based Constructor
	 * @param size size to make the storage
	 */
	public StorageArrayUtil(int size) {
		// check if size exceeds max Capacity
		capacityCheck(size);
		numberOfEntries = 0;

		// Cast new array
		@SuppressWarnings("unchecked")
		T[] tmp = (T[]) new Comparable[size];
		storage = tmp;

		sortTracker = 0;
		everSort = false;
	}

	/**
	 * Checks if the capacity exceeds the maximum capacity allowed
	 * @param capacity size of storage
	 */
	private void capacityCheck(int capacity) {
		if (capacity > MAX_CAPACITY) {
			// size exceeds max capacity
			System.out.printf("ERROR: Storage capacity [%d] exceeds limits", capacity);
			System.exit(1);  // end program with error code
		}
	}


	/** 
	 * Works in conjunction with dictionary to check if item already exists in storage
	 * @param item
	 */
	public void add(T item) {
		storage[numberOfEntries] = item;  // add to next unused index in storage
		numberOfEntries++;
		sortTracker++;
		loadCheck();
	}

	/**
	 * Checks to see if the storage array is exceeding the load ratio
	 * If it exceeds the load ratio it must be expanded
	 */
	private void loadCheck() {
		if (numberOfEntries / storage.length > LOAD_RATIO) {
			// this is too full -- expand array
			enlargeStorage();
		}

	}

	/**
	 * Enlarge the size of the storage array
	 * The size is doubled
	 */
	private void enlargeStorage() {
		T[] oldStore = storage;
		int oldSize = storage.length;
		int oldNum = numberOfEntries;
		int newSize = oldSize * 2;
		capacityCheck(newSize);  // ensure still within limits

		@SuppressWarnings("unchecked")
		T[] tmp = (T[]) new Comparable[newSize];
		storage = tmp;
		numberOfEntries = 0; // Reset to 0; since re-adding will increase this to correct number

		// Re-add all positions
		for (int i = 0; i < oldNum; i++) {
			add(oldStore[i]);	
		} 
	}

	/** 
	 * Works in conjunction with dictionary to check if item already exists in storage
	 * @param item
	 */
	public void updateEntry(T item) {
		// check if array is sorted
		if (sortTracker == 0) {
			// sorted -- binary search
			binarySearch(item);

		} else {
			// not sorted -- linear search
			linearSearch(item);
		}

		sortTracker++;
	}


	private void linearSearch(T item) {
		for (int i = 0; i < numberOfEntries; i++) {
			if (storage[i].equals(item)) {
				storage[i] = item;  // add to next unused index in storage
				break;
			}
		}

	}

	private void binarySearch(T item) {
		int lower = 0;
		int upper = numberOfEntries - 1; 
		while (lower <= upper) { 
			int mid = (lower + upper) / 2; 

			int comp = storage[mid].compareTo(item);
			// Check if the item is at mid
			if (comp == 0) {
				storage[mid] = item; 
			}

			// If item is greater, ignore left half
			if (comp < 0) {
				lower = mid + 1; 
			} else {
				// Item is smaller, ignore right half 
				upper = mid - 1; 
			}
		}
	}

	/**
	 * Get the n'th most max
	 * @param n rank
	 * @return item
	 */
	public T getNMax(int n) {
		// check if sorted
		if (everSort) {
			if (sortTracker == 0) {
				// sorted -- pull value
				return storage[n];
			}
		}


		// else not sorted -- see which sort to do
		Arrays.sort(storage, 0, numberOfEntries, Collections.reverseOrder()); 

		return storage[n];


	}

	/**
	 * Sort the array by treating it as a heap -- this sorts quickly; good on randomly ordered data
	 * Sorts in descending order
	 */
	private void heapSort() {
		// create the first heap
		for (int rootIndex = numberOfEntries / 2 - 1; rootIndex >= 0; rootIndex--) {
			reheap(storage, rootIndex, numberOfEntries - 1);
		}

		swap(0, numberOfEntries - 1);  // swap the root node with the last node

		// start operations on reduced heap
		for (int lastIndex = numberOfEntries - 2; lastIndex > 0; lastIndex--) {
			reheap(storage, 0, lastIndex);
			swap(0, lastIndex);
		}

	}


	/**
	 * Transform a semi-heap into a max heap
	 * @param rootIndex index the root is in
	 * @param lastIndex index at end
	 */
	private void reheap(T[] heap, int rootIndex, int lastIndex) {
		boolean finished = false;
		int leftCIndex = 2 * rootIndex + 1;  
		T lost = heap[rootIndex];
		

		while (!finished && leftCIndex <= lastIndex) {
			int largeCIndex = leftCIndex; // assume it is larger
			int rightCIndex = leftCIndex + 1; // right child is next to left

			if (rightCIndex <= lastIndex && heap[rightCIndex].compareTo(heap[largeCIndex]) < 0) {
				largeCIndex = rightCIndex;
			}

			if (lost.compareTo(heap[largeCIndex]) > 0) {
				// lost node is greater than node in largeCIndex
				heap[rootIndex] = heap[largeCIndex]; // set this into the root index
				rootIndex = largeCIndex;   // tick down to next index
				leftCIndex = 2 * rootIndex + 1; 
			} else {
				// none are larger here, stop
				finished = true;
			}
		}

		heap[rootIndex] = lost;	
	}


	/**
	 * Sorts an array with the insertion algorithm -- good on semi-sorted data
	 */
	private void insertionSort() {
		int j;
		T next;
		for(int i = 0; i < numberOfEntries; i++) {
			next = storage[i];
			j = i;
			while(j > 0 && storage[j - 1].compareTo(next) < 0) {
				storage[j] = storage[--j];
			}
			storage[j] = next;
		}
	}

	/**
	 * Swap two items in an array
	 * @param p1 position 1
	 * @param p2 position 2
	 */
	private void swap(int p1, int p2) {
		T tmp = storage[p1];
		storage[p1] = storage[p2];
		storage[p2] = tmp;
	}

	
//	/**
//	 * Sorts an array with the quicksort algorithm -- good on unsorted data
//	 * @param low
//	 * @param high
//	 */
//	private void quickSort(int low, int high) {
//		if (low >= high) {
//			return;
//		}
//		T pivot = storage[(low + high) / 2];
//		int i = low;
//		int j = high;
//
//		while (i <= j) {
//			while (storage[i].compareTo(pivot) > 0) {
//				i++;
//			}
//			while (storage[j].compareTo(pivot) < 0) {
//				j--;
//			}
//			if (i <= j) {
//				swap(i++, j--);
//			}
//		}
//
//		if (low < j) {
//			quickSort(low, j);
//		}
//		if (high > i) {
//			quickSort(i, high);
//		}
//
//	}

}

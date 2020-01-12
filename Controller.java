import java.io.*;
import java.util.*;

/**
 *
 * @author skonlc brinkmwj and karroje and krumpenj
 * @Date : 2009-10-04, 2011-03-10, 2015-09-27, 2017-04-22 Sources : All code is
 *       original Purpose : The purpose of this file is to do some VERY
 *       rudimentary timing of your increaseCount method. For the QUALITY
 *       measures I am also going to test getCount and getNthMostPopular, so you
 *       might want to design some way to test out the running time of your
 *       getCount as well.
 */
public class Controller {

	/*
	 * The only purpose of main() is to call processFile with increasingly
	 * larger and larger files. A larger file will give a more accurate sense of
	 * how fast addToTrends is, but at some point it may take so long to do the
	 * getNthMostPopular, that we aren't willing to wait for it to finish.
	 */

	static Random rng;

	public static void main(String[] args) {

		/*
		 * These files are from books project Gutenberg. I have provided the
		 * inputs, as well as my outputs in the starter files
		 */

		/*
		 * NOTE: You may want to comment some of these out! Unless your program
		 * is very speedy on all operations some of these will never finish.
		 */
		long start = System.nanoTime();
		rng = new Random(42);

		System.out.println("file 1");
		timeTests("1.txt");
		System.out.println("file 2");
		timeTests("2.txt");
		System.out.println("file 3");
		timeTests("3.txt");
		System.out.println("file 4");
		timeTests("4.txt");
		System.out.println("file 5");
		timeTests("5.txt");
		System.out.println("file 6");
		timeTests("6.txt");
		System.out.println("file 7");
		timeTests("7.txt");
		long endTime = System.nanoTime();
		double test = (endTime - start) / 1000000000.0;
		System.out.println("Test: " + test + " seconds (total)");
		

	}

	public static ArrayList<String> readFile(String fileName) {
		ArrayList<String> wordList = new ArrayList<>();
		String line;
		try {
			FileReader fileReader = new FileReader(fileName);
			try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
				while ((line = bufferedReader.readLine()) != null) {
					line = line.replaceAll("[^A-Za-z0-9 ]", ""); // Remove all
																	// non-alphanumeric/whitespace
																	// characters
					for (String word : line.split(" "))
						if (!word.isEmpty())
							wordList.add(word.trim());
				}
			}
		} catch (FileNotFoundException ex) {
			System.err.println("Unable to open file '" + fileName + "'");
			System.exit(1);
		} catch (IOException ex) {
			System.err.println("Error reading file '" + fileName + "'");
			System.exit(1);
		}
		return wordList;
	}

	/**
	 * Find the total time to insert the words into a list.
	 * 
	 * @param wordList
	 * @return average time per insert
	 */
	public static void timeTests(String file) {
		ArrayList<String> wordList = readFile(file);

		// Average time for insert
		long startTime = System.nanoTime();
		LookupInterface tr = new StudentLookup();
		for (String w : wordList)
			tr.addString(1, w);
		long endTime = System.nanoTime();
		double test1 = (double) ((endTime - startTime) / 1000000.0) / (double) wordList.size();
		System.out.println("Test 1: " + test1 + " milliseconds / insert");
//		System.out.println(tr.numEntries());

//		Set<DataWrapper> st = new HashSet<DataWrapper>();  // test print
		// Average time for getCount
		startTime = System.nanoTime();
		for (String w : wordList) {
			tr.lookupCount(w);
//			st.add(new DataWrapper(w, tr.lookupCount(w)));  // test print
		}
//		Set<DataWrapper> sst = new TreeSet<DataWrapper>(st); // test print
//		for (DataWrapper w : sst) {   // test print
//			System.out.println(tr.lookupCount(w.getData()) + " " + w.getData());  // test print
//		}   // test print
		endTime = System.nanoTime();
		double test2 = (double) ((endTime - startTime) / 1000000.0) / (double) wordList.size();
		System.out.println("Test 2: " + test2 + " milliseconds / getCount");

		// Average time for getNthMostPopular();
		int n = tr.numEntries();
		startTime = System.nanoTime();
		for (int i = 0; i < n; i++)
			tr.lookupPopularity(i);
		endTime = System.nanoTime();
		double test3 = (double) ((endTime - startTime) / 1000000.0) / (double) n;
		System.out.println("Test 3: " + test3 + " milliseconds / getNthMostPopular");

		// Average time per operation when mixing operations
		tr = new StudentLookup();
		startTime = System.nanoTime();
		n = 0;
		for (int i = 0; i < wordList.size(); i++) {
			tr.addString(1, wordList.get(i));
			if (rng.nextDouble() < 0.2)
				n = tr.numEntries();
			if (rng.nextDouble() < 0.2) {
				tr.lookupPopularity(rng.nextInt(Math.max(n, 1)));
			}
		}
		endTime = System.nanoTime();
		double test4 = (endTime - startTime) / 1000000000.0;
		System.out.println("Test 4: " + test4 + " seconds (total)");		
	}

}

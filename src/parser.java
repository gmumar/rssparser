/*---------------------------
 * Please make sure internet
 * is connected before running 
 * app
 * 
 * Author: Ghulam Umar
 * Date: 13/03/2012
 * 
 * Output: out.txt
 * 
 * RssParser: Read Rss feed
 * get links and counts words
 * from the articles.
 *
 * Library used: rssutils.jar
 * link:http://java.sun.com/developer/technicalArticles/javaserverpages/rss_utilities/
 * 
 * -----------------------------
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.cnpi.rss.elements.Item;
import com.sun.cnpi.rss.elements.Link;
import com.sun.cnpi.rss.elements.Rss;
import com.sun.cnpi.rss.parser.RssParser;
import com.sun.cnpi.rss.parser.RssParserException;
import com.sun.cnpi.rss.parser.RssParserFactory;

public class parser {
	// MY CODE STARTS--->

	static LinkedList<String> list = new LinkedList<String>();
	static HashMap<String, Integer> contain = new HashMap<String, Integer>();
	static SortedMap<String, Integer> printer = new TreeMap<String, Integer>();
	static LinkedList<Integer> count = new LinkedList<Integer>();
	static PrintStream fout;
	static FileOutputStream out;
	static URL rss_url;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		try {
			parse();
		} catch (NullPointerException e) {
			System.out.println("Error:Failed to Parse.");
		}
	}

	public static void parse() throws Exception {
		RssParser parser = RssParserFactory.createDefault();
		Rss rss = null;
		// RSS url
		rss_url = new URL("http://rss.cbc.ca/lineup/topstories.xml");

		// Exception handling for RSS parser
		try {
			rss = parser.parse(rss_url);
		} catch (FileNotFoundException e) {
			System.out.println("Error:Cannot Open Url.");
		} catch (UnknownHostException e) {
			System.out
					.println("Error:Cannot Open Url - Check your connection.");
		} catch (RssParserException e) {
			System.out.println("Error:Rss Parser Error - Please try again.");
		}

		// Reading iteams from RSS parser
		Collection<?> items = rss.getChannel().getItems();
		if (items != null && !items.isEmpty()) {
			for (Iterator<?> i = items.iterator(); i.hasNext();) {
				Item item = (Item) i.next();
				Link link = item.getLink();

				URL url = new URL(link.toString());
				URLConnection conn = null;
				BufferedReader read = null;

				// Exception handling for Opening urls in feed
				try {
					conn = url.openConnection();
				} catch (IOException e) {
					System.out.println("Error:Cannot Read from Url");
				} finally {

					// Exception handling for reading from url
					try {
						read = new BufferedReader(new InputStreamReader(
								conn.getInputStream()));
					} catch (IOException e) {
						System.out.println("Error:Cannot Read from Url");
					}
				}

				
				System.out.println(item.getLink() + " ... read successfull");
				String line;
				String[] words;
				int i1, num;

				while ((line = read.readLine()) != null) {

					// pick up line
					if ((line.contains("<p>") || line.contains("<li>"))) {
						// Scrub the line
						line = line.replaceAll("<[^>]*>", "");
						line = line.replaceAll("[^a-zA-Z ]+", "");
						// break line into words
						words = line.split(" ");

						for (i1 = 0; i1 < words.length; i1++) {
							// Go through each word and compare to see if it in
							// the list
							words[i1] = words[i1].replaceAll("[ \t\n]+", "");
							if (list.contains(words[i1])) {
								// in list already
								if (contain.containsKey(words[i1])) {
									num = contain.get(words[i1]);
									num++;
									contain.put(words[i1], num);
								} else {
									contain.put(words[i1], 1);
								}
							} else {
								// not is list
								if ((words[i1].length() > 1))
									list.add(words[i1]);
							}
						}
					}
				}// while ends
			}
			list.clear();
			// Sort list to print top 50
			printer = sortByValues(contain);

			boolean flag = false;
			try {
				out = new FileOutputStream("out.txt");
				fout = new PrintStream(out);
			} catch (FileNotFoundException e) {
				System.out
						.println("Error:File write fail. Outputing to console");
				fout = System.out;
				flag = true;
			}

			int print_s = 0;
			// Print top 50
			for (String key : printer.keySet()) {
				print_s++;
				if (print_s > 50)
					break;
				fout.println(print_s + ". " + contain.get(key) + " " + key);
			}
			if (!flag)
				System.out
						.println("Complete.\nPlease open out.txt to see the output.");
			else
				System.out.println("Complete.\nOutput:\n");
		}
	}

	// <--- MY CODE ENDS

	public static <K, V extends Comparable<V>> SortedMap<K, V> sortByValues(
			final HashMap<K, V> map) {
		Comparator<K> valueComparator = new Comparator<K>() {
			public int compare(K k1, K k2) {
				int compare = map.get(k2).compareTo(map.get(k1));
				if (compare == 0)
					return 1;
				else
					return compare;
			}
		};
		Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return (SortedMap<K, V>) sortedByValues;

	}
}

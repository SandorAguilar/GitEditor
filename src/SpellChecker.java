
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;


/**
 * Spell check class 
 * 
 * @author Eitan
 *
 */
public class SpellChecker {
	
	private Path dict_path = Paths.get("/Users/Eitan/Desktop/upenn/cit594/hw6/words.txt");
	private Set<String> dict;
	private int numSuggestions = 4;
	
//	public static void main(String[] args){
//		SpellChecker sc = new SpellChecker();
//		String x = "So this cd is a HUGE paragrph with .all sorts abb of valid and glasses invalid words. \"SO exciting\" one may, or may not, say.";
//		System.out.println(sc.checkDocument(x));
//		for (String s : sc.checkDocument(x)){
//			System.out.println(s + ": " + sc.recommendedWords(s));
//			
//		}
//		sc.recommendedWords("aria");
//	}
	
	/**
	 * Constructor
	 */
	public SpellChecker(){
		dict = new HashSet<String>();
		readDict();
	}
	
	
	/**
	 * Returns the top numSuggestions closest words to the wrong word 
	 * @param wrongWord
	 * @return
	 */
	public List<String> recommendedWords(String wrongWord){
//		For each word in dict, compute edit-distance value from wrongWord
//		Store in heap the top k elements
//		Return those elements
		List<String> suggestions = new ArrayList<String>();
		PriorityQueue<Tuple> minHeap = new PriorityQueue<Tuple>(numSuggestions);
		
		minHeap.add(new Tuple("blank", Integer.MAX_VALUE));
		
		for (String w : dict){
			
			if (minHeap.peek().getValue() == 1 && minHeap.size() == numSuggestions) break;
			
			int val = EditDistance.calcDistance(wrongWord, w);
			
			if (minHeap.size() < numSuggestions){
				minHeap.add(new Tuple(w, val));
				continue;
			}
			
			if (val < minHeap.peek().getValue()){
				minHeap.remove();
				minHeap.add(new Tuple(w, val));
			}
		}
		
		System.out.println(minHeap);
		for (Tuple t : minHeap){
			suggestions.add(t.getStr());
		}
		return suggestions;
	}
	
	
	
	
	/**
	 * Inputs string of whole document
	 * Outputs the misspelled words
	 * @param doc
	 * @return
	 */
	public List<String> checkDocument(String doc){
		List<String> wrong_words = new ArrayList<String>();
		doc = doc.trim();
		String[] words = doc.split("\\s+");
		for (String w : words){
			if (!checkWord(w))
				wrong_words.add(w);
		}
		return wrong_words;
	}
	
	/**
	 * Checks if word is in dictionary
	 * Strips any punctuation
	 * @param word
	 * @return
	 */
	public boolean checkWord(String word){
//		strip punctuation from beginning of string
		word = word.replaceFirst("^[^a-zA-Z]+", "");
//		strip punctuation from end of string
		word = word.replaceAll("[^a-zA-Z]+$", "");
		if (dict.contains(word) || dict.contains(word.toLowerCase())) return true;
		int L = word.length();
		if (word.charAt(L - 1) == 's'){
			word = word.substring(0, L - 1);
			if (dict.contains(word) || dict.contains(word.toLowerCase())) return true;
			L = word.length();
			if (word.charAt(L - 1) == '\''){
				word = word.substring(0, L - 1);
				return (dict.contains(word) || dict.contains(word.toLowerCase()));
			}
		}
		return false;
	}
	
	/**
	 * Reads in dictionary from specified path
	 */
	private void readDict(){
		Path p1 = dict_path;
		try (
				InputStream in = Files.newInputStream(p1);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))
			) {
				String newWord = null;
				while ((newWord = reader.readLine()) != null) {
					dict.add(newWord);				
				}
			} catch (IOException x) {
			    System.err.println(x);
			}
	}	
}
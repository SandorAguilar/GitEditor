package hw6;


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
import java.util.Set;

/**
 * Spell check class 
 * @author Eitan
 *
 */
public class SpellChecker {

//	Store the dictionary
//	Two purposes: 
//			1) Check if word exists in dictionary
//			2) Come up with suggestions for words
	
//	Option 1: store in array sorted, do binary search
	
	private Path dict_path = Paths.get("/Users/Eitan/Desktop/upenn/cit594/hw6/words.txt");
	private Set<String> dict;
	
	public static void main(String[] args){
		SpellChecker sc = new SpellChecker();
		String x = "So this is a HUGE paragrph with .all sorts abb of valid and glasses invalid words. \"SO exciting\" one may, or may not, say.";
		System.out.println(sc.checkDocument(x));
	}
	
	/**
	 * Constructor
	 */
	public SpellChecker(){
		dict = new HashSet<String>();
		readDict();
	}
	
	public List<String> recommendedWords(String wrongWord){
		return null;
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
//		TODO strips s at end (words => word)
		
//		TODO strip 's at end 
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
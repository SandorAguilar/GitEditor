import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Interface for spellchecker 
 * @author Eitan
 *
 */
public interface SpellCheckInterface{
	
	/**
	 * Returns the top numSuggestions closest words to the wrong word 
	 * @param wrongWord
	 * @return
	 */
	public List<String> recommendedWords(String wrongWord);
	
	/**
	 * Inputs string of whole document
	 * Outputs the misspelled words
	 * @param doc
	 * @return
	 */
	public List<String> checkDocument(String doc);
	
	/**
	 * Returns list of mappings of wrong words to a list of their suggested words 
	 * @param docText
	 * @return suggestions
	 */
	public List<Map<String, List<String>>> wrongWordsSuggestions(String docText);

}
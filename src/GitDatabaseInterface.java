import java.util.ArrayList;
/**
 * This is the interface for GitDatabase to implement all the methods related to the database.
 * @author fanglinlu
 *
 */
public interface GitDatabaseInterface {
	
	/**
	 * This is a method to create a new file by providing the fileName and content of the file.
	 * @param fileName
	 * @param content
	 * @return a boolean variable indicating whether the file has been created or not. 
	 */
	public boolean createNewFile (String fileName, String content);
	
	/**
	 * This is a method that is called when saving the file.
	 * @param content
	 * @param commitMessage
	 */
	public void save (String content, String commitMessage);
	
	/**
	 * This is a method to retrieve file records from the database. This is usually called when you open a file.
	 * @param fileName
	 * @param content
	 * @return
	 */
	public boolean retrieveFileRecordsFromDatabase (String fileName, String content);
	
	/**
	 * This is a method used to retrieve all the previous saved versions for the current file.
	 * @return
	 */
	public ArrayList<Commit> retrieve ();
	
	/**
	 * This method is called to open the retrieved version
	 * @param storedFileTime
	 * @param previousFileContent
	 * @return
	 */
	public String openRetrievedVersion (String storedFileTime, String previousFileContent);
	
	/**
	 * This method is called to close the database and save newly updated things in the database.
	 */
	public void closeDatabase();
	
}

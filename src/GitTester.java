
public class GitTester {
	
	public static void main (String[] args) {
		
		GitDatabase database = GitDatabase.getInstance();
		
		database.createNewFile("file1", "This is the first file for testing.");
		database.createNewFile("file2", "This is the secondfile for testing.");
		database.closeDatabase();
	}
}

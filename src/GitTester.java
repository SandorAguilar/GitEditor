import java.util.ArrayList;

public class GitTester {
	
	public static void main (String[] args) {
		
		GitDatabase database = GitDatabase.getInstance();
		
		database.createNewFile("file1", "This is the first file for testing.");
		
		database.save ("This is the first file for testing. Added new line", "Add a new line");
		
		ArrayList<Commit> retrieves = database.retrieve();
		
		for (Commit commit: retrieves) {
			System.out.println(commit.getCommitMessage());
			System.out.println(commit.getCommitTime());
		}

		
		database.closeDatabase();
		

	}
}

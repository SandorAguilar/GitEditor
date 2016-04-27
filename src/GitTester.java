import java.util.ArrayList;

public class GitTester {
	
	public static void main (String[] args) {
		
		GitDatabase database = GitDatabase.getInstance();
		
		database.createNewFile("file1", "This is the eighth file for testing.");
		
		database.save ("This is the first file for testing. Added new line", "Add a new line");
//		
//		database.save ("This is the Second file for testing. Added another new line", "Add another new line");
//		
		ArrayList<Commit> retrieves = database.retrieve();
		long lastCommitTime = 0;
		
		for (Commit commit: retrieves) {
			System.out.println(commit.getCommitMessage());
			lastCommitTime = commit.getCommitTime();
			System.out.println(lastCommitTime);
		}

		System.out.println(database.openRetrievedVersion(lastCommitTime + "", "This is the Second file for testing. Added another new line"));
		
		database.closeDatabase();
		

	}
}

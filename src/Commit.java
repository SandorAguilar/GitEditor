
/**
 * This is a wrapper class that wraps the commitTime and commitMessage for a file.
 * @author fanglinlu
 *
 */
public class Commit {
	
	private long commitTime;
	private String commitMessage;
	
	public Commit (long commitTime, String commitMessage) {
		this.commitTime = commitTime;
		this.commitMessage = commitMessage;
	}

	public long getCommitTime() {
		return commitTime;
	}

	public void setCommitTime(long commitTime) {
		this.commitTime = commitTime;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}
	
	
}

import java.io.File;
import java.util.ArrayList;
/**
 * This is a FileNade class that represents each saved file in the database.
 * @author fanglinlu
 *
 */
public class FileNode {
	private File file;
	private FileNode parent;
	private ArrayList<FileNode> children;
	private String commitMessage;
	
	public FileNode (File file) {
		this.file = file;
		children = new ArrayList<FileNode> ();
	}
	
	public File getFile() {
		return file;
	}

	
	public String getCommitMessage() {
		return commitMessage;
	}
	
	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}
	
	public FileNode getParent() {
		return parent;
	}

	public void setParent(FileNode parent) {
		this.parent = parent;
	}

	public ArrayList<FileNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<FileNode> children) {
		this.children = children;
	}
	
	public void addChildren (FileNode child) {
		children.add(child);
	}
	
	
	
}

/**
 * This is a class implementing the relationship between different versions saved in the database in a form of fileTree. 
 * @author fanglinlu
 *
 */
public class MyFileTree {
	
	private FileNode head;
	private String fileName;
	private FileNode currentNode;
	
	public MyFileTree (String fileName) {
		head = null;
		this.fileName = fileName;
	}
	
	public void setHeadNode (FileNode fileNode) {
		head = fileNode;
	}
	
	public FileNode getHeadNode () {
		return head;
	}
	
	public String getFileName () {
		return fileName;
	}
	
	public FileNode getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(FileNode currentNode) {
		this.currentNode = currentNode;
	}
	
	/**
	 * This is a recursive method that is called to find the node in the tree.
	 * @param filePath
	 * @param startNode
	 * @return
	 */
	public FileNode findNode (String filePath, FileNode startNode) {
		if (startNode == null) {
			return null;
		}

		if (startNode.getFile().getPath().equals(filePath)) {
		
			return startNode;
		}
		for (FileNode fileNode: startNode.getChildren()) {

			FileNode childrenNode = findNode(filePath, fileNode);
			if (childrenNode != null) {
				return childrenNode;
			}
			
		}
		
		return null;
	}
	
	public FileNode findNode (String filePath) {
		return findNode (filePath, head);
	}
}

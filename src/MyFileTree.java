
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

	public FileNode findNode (String filePath, FileNode startNode) {
		if (startNode == null) {
			return null;
		}
		
		if (startNode.getFile().getPath().equals(filePath)) {
			return startNode;
		}
		for (FileNode fileNode: startNode.getChildren()) {
			if (fileNode.getFile().getPath().equals(filePath)) {
//				System.out.println("children's path:" + fileNode.getFile().getPath());
				return fileNode;
			}
		}
		return null;
	}
	
	public FileNode findNode (String filePath) {
		return findNode (filePath, head);
	}
}

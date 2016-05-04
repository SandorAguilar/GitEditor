
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
		System.out.println("Check returning startNode.");
		System.out.println("start node path is:" + startNode.getFile().getPath());
		System.out.println("File Path is:" + filePath);
		
		if (startNode.getFile().getPath().equals(filePath)) {
		
			return startNode;
		}
		for (FileNode fileNode: startNode.getChildren()) {
			System.out.println("Children's path" + fileNode.getFile().getPath());
			System.out.println("File path is:" + filePath);
//			if (fileNode.getFile().getPath().equals(filePath)) {
//				System.out.println("children's path:" + fileNode.getFile().getPath());
//				return fileNode;
//			} else {
//				FileNode childrenNode = findNode (filePath, fileNode) ;
//				if (childrenNode != null) {
//					return childrenNode;
//				}
//			}
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

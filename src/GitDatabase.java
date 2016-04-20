import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class GitDatabase {
	
	private GitDatabase database = null;
	private ArrayList<MyFileTree> fileTreeData;
	private String gitDatabasePath = ".gitDatabase";
	private FileNode currentNode;
	private MyFileTree currentTree;
	

	/**
	 * This instance variable is used to implement the priority queue.
	 */
	private Comparator <Commit> commitComparator = new Comparator <Commit> () {
		public int compare (Commit commit1, Commit commit2) {

			if (commit1.getCommitTime() - commit2.getCommitTime() < 0) {
				return 1;
			} else if (commit1.getCommitTime() - commit2.getCommitTime() > 0) {
				return -1;
			} else {
				return 0;
			}
			
		}
	};
	
	private GitDatabase () {
		
	}
	
	public GitDatabase getInstance () {
		if (database == null) {
			database.init(".gitDatabase");
		} 
		
		return database;
	}
	
	public void createNewFile (String fileName, String content) {
		
		MyFileTree newFileTree = new MyFileTree (fileName);
		String fileStoredName = System.currentTimeMillis() + "";

		String newFileStoredPath = gitDatabasePath + "/" + fileStoredName;
		File newHeadFile = new File (newFileStoredPath);
		FileNode newFileHeadNode = new FileNode (newHeadFile);
		
		newFileHeadNode.setCommitMessage("Created");
		newFileTree.setHeadNode(newFileHeadNode);
		newFileTree.setCurrentNode(newFileHeadNode);
		
		try {
			
			FileWriter fileWriter = new FileWriter (newFileStoredPath);
			BufferedWriter bw = new BufferedWriter (fileWriter);
			bw.write(content);
			bw.flush();
			bw.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		fileTreeData.add(newFileTree);
		writeFileNameToDirectory (fileName, fileStoredName);
		
		currentNode = newFileHeadNode;
		currentTree = newFileTree;
		
	}
	
	public void save (String content, String commitMessage) {
		
		String fileStoredName = System.currentTimeMillis() + "";

		String newFileStoredPath = gitDatabasePath + fileStoredName;
		File newFile = new File (newFileStoredPath);
		FileNode newFileNode = new FileNode (newFile);
		
		newFileNode.setParent(currentNode);
		newFileNode.setCommitMessage(commitMessage);
		
		currentNode.addChildren(newFileNode);
		
		newFileNode = currentNode;
		currentTree.setCurrentNode(currentNode);
		
		try {
			
			FileWriter fileWriter = new FileWriter (newFileStoredPath);
			BufferedWriter bw = new BufferedWriter (fileWriter);
			bw.write(content);
			bw.flush();
			bw.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	/**
	 * This methods retrieve the file records from the database. If the file's content is not the same with
	 * what is store in the database, a new version is saved. 
	 * @return a boolean variable indicating whether there are file records or not. 
	 */
	public boolean retrieveFileRecordsFromDatabase (String fileName, String content) {
		for (MyFileTree mft: fileTreeData) {
			if (mft.getFileName().equals(fileName)) {
				currentTree = mft;
				FileNode fileCurrentNode = mft.getCurrentNode();
				currentNode = fileCurrentNode;
				
				File currentVersionFile = fileCurrentNode.getFile();
				String currentStoredContent = getFileContent (currentVersionFile);
				if (!currentStoredContent.equals(content)) {
					save (content, "Reopened after modifying");
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method returns an arrayList of reversely sorted commits based on the commit time.
	 * @return
	 */
	public ArrayList<Commit> retrieve () {
		
		PriorityQueue<Commit> pq = new PriorityQueue<Commit> (10, commitComparator);
		addTreeToQueue (pq, currentTree.getHeadNode());
		
		ArrayList<Commit> result = new ArrayList<Commit> ();
		result.add(pq.poll());
		return result;
		
	}
	
	/**
	 * This method is used to open the retrieved version of files.
	 * @param fileName
	 */
	public String openRetrievedVersion (String fileName) {
		File retrievedFile = new File (gitDatabasePath + "/" + fileName);
		FileNode retrievedNode = currentTree.findNode(retrievedFile.getPath());
		currentNode = retrievedNode;
		return getFileContent (retrievedFile);
		
	}
	
	private void addTreeToQueue (PriorityQueue<Commit> pq, FileNode node) {
		if (node == null) {
			return;
		}
		
		Commit commit = new Commit (Long.parseLong(node.getFile().getName()), node.getCommitMessage());
		pq.add(commit);
		
		for (FileNode childNode: node.getChildren()) {
			addTreeToQueue (pq,childNode);
		}
		
	}
	
	private String getFileContent (File file) {
		String content = "";
		try {
			FileReader fileReader = new FileReader (file.getPath());
			BufferedReader br1 = new BufferedReader (fileReader);
			
			String line = null;
			while ((line = br1.readLine()) != null) {
				content = content + line + "\n";
			}
			
			br1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return content;
	}
	
	private void writeFileNameToDirectory (String fileName, String fileStoredName) {
		try {
			
			FileWriter fileWriter = new FileWriter (gitDatabasePath + "/fileDirectory.txt", true);
			BufferedWriter bw = new BufferedWriter (fileWriter);
			bw.write(fileName + ":" + fileStoredName);
			bw.flush();
			bw.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void init (String gitDatabasePath) {
		
		File homeDirectory = new File (gitDatabasePath);
		fileTreeData = new ArrayList<MyFileTree> ();
		if (!homeDirectory.exists()) {
			homeDirectory.mkdirs();
		} 

		File filesDatabase = new File (gitDatabasePath + "/fileDirectory.txt");
		
		if (!filesDatabase.exists()) {
			try {
				filesDatabase.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			populateFiles (".gitDatabase/fileDirectory.txt");
		}
	}
	
	private void populateFiles (String directoryPath) {
		
		ArrayList<String> storedFiles = new ArrayList<String> ();
		try {
			FileReader fileReader = new FileReader (directoryPath);
			BufferedReader br1 = new BufferedReader (fileReader);
			
			String line = null;
			while ((line = br1.readLine()) != null) {
				storedFiles.add(line);
			}
			
			br1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (String storedFile: storedFiles) {
			String[] storedFileInfo = storedFile.split(":");
			String path = ".gitDatabase/" + storedFileInfo[1];
			MyFileTree mft = new MyFileTree(storedFileInfo[0]);
			populateData (path, mft);
			fileTreeData.add(mft);
		}	
		
	}
	
	private void populateData (String filePath, MyFileTree mft) {
		try {
			FileReader fileReader = new FileReader (filePath);
			BufferedReader br1 = new BufferedReader (fileReader);
			
			String line = null;
			while ((line = br1.readLine()) != null) {
				String [] lineInfo = line.split(";");
				File file = new File (lineInfo[0]);
//				FileNode fileNode = new FileNode (file);
				FileNode fileNode = mft.findNode(file.getPath());
				if (fileNode == null) {
					fileNode = new FileNode (file);
				}
				
				if (lineInfo[1].length() > 0) {
					File temp = new File (lineInfo[1]);
					FileNode parentNode = mft.findNode(temp.getPath());
					fileNode.setParent(parentNode);
				}
				
				if (lineInfo[2].length() > 0) {
					String[] childrenFilePaths = lineInfo[2].split(",");
					for (String childFilePath: childrenFilePaths) {
						File childFile = new File (childFilePath);
						FileNode childNode = new FileNode (childFile);
						fileNode.addChildren(childNode);
					}
				}
				
				if (lineInfo[3].length() > 0) {
					fileNode.setCommitMessage(lineInfo[3]);
				}
				if (lineInfo[4].equals("T")) {
					mft.setCurrentNode(fileNode);
				}
 			}
			
			br1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

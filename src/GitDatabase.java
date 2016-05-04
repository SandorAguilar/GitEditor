import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
/**
 * This is the GitDatabase method that manages all the database related functions
 * @author fanglinlu
 *
 */
public class GitDatabase implements GitDatabaseInterface{
	
	private static GitDatabase database = null;
	private static ArrayList<MyFileTree> fileTreeData;
	private static String gitDatabasePath = ".gitDatabase";
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
	
	/**
	 * This is the contructor of the database.
	 */
	private GitDatabase () {
		
	}
	
	/**
	 * This method used Singleton desing pattern to get an instance of the GitDatabase.
	 * @return
	 */
	public synchronized static GitDatabase getInstance () {
		if (database == null) {
			database = new GitDatabase();
			init(gitDatabasePath);
		} 
		
		return database;
	}
	
	/**
	 * This method is used to create a new file in the database.
	 */
	public boolean createNewFile (String fileName, String content) {
		
		if (!checkFileName(fileName)) {
			return false;
		}
		
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
		writeFileNameToDirectory (fileName);

		
		currentNode = newFileHeadNode;
		currentTree = newFileTree;
		
		updateTreeRecords (fileName);
		
		return true;
		
	}
	
	/**
	 * This method is used to check whether the file name is stored in the database.
	 * @param fileName
	 * @return
	 */
	private boolean checkFileName (String fileName) {
		for (MyFileTree mft: fileTreeData) {
			if (mft.getFileName().equals(fileName)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * This method is used to update tree records in the database.
	 * @param fileName
	 */
	private void updateTreeRecords (String fileName) {
		String fileStoredPath = gitDatabasePath + "/" + fileName;
		try {
			
			FileWriter fileWriter = new FileWriter (fileStoredPath);
			BufferedWriter bw = new BufferedWriter (fileWriter);
			writeNodeRecords (bw, currentTree.getCurrentNode());
			bw.flush();
			bw.close();
			
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * This method is used to write the file node records in the database.
	 * @param bw
	 * @param fileNode
	 * @throws Exception
	 */
	private void writeNodeRecords (BufferedWriter bw, FileNode fileNode) throws Exception{
		
		bw.write(fileNode.getFile().getPath() + ";");
		if (fileNode.getParent() != null) {
			bw.write(fileNode.getParent().getFile().getPath());
		} else {
			bw.write(";");
		}
		
		if (fileNode.getChildren() != null) {
			for (FileNode child: fileNode.getChildren()) {
				
			}
		}
		
	}
	
	/**
	 * This method is called when you want to save the file.
	 */
	public void save (String content, String commitMessage) {
		
		String fileStoredName = System.currentTimeMillis() + "";

		String newFileStoredPath = gitDatabasePath + "/" + fileStoredName;
		File newFile = new File (newFileStoredPath);
		FileNode newFileNode = new FileNode (newFile);
		
		newFileNode.setParent(currentNode);
		newFileNode.setCommitMessage(commitMessage);
//		System.out.println("Set commit message:" + commitMessage);
		
		currentNode.addChildren(newFileNode);
		
		currentNode = newFileNode;
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
		if (currentTree != null) {
			closeDatabase();
		} 
		for (MyFileTree mft: fileTreeData) {
			if (mft.getFileName().equals(fileName)) {
				currentTree = mft;
				FileNode fileCurrentNode = mft.getCurrentNode();
				currentNode = fileCurrentNode;
				
				File currentVersionFile = fileCurrentNode.getFile();
				String currentStoredContent = getFileContent (currentVersionFile);
				if (!currentStoredContent.equals(content)) {
					save (content, "Reopeneding");
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
		 
		while (!pq.isEmpty() && result.size() < 10) {
			result.add(pq.poll());
		}
		
		return result;
		
	}
	
	/**
	 * This method is used to open the retrieved version of files.
	 * @param fileName
	 */
	public String openRetrievedVersion (String storedFileTime, String previousFileContent) {

		File retrievedFile = new File (gitDatabasePath + "/" + storedFileTime);

		FileNode retrievedNode = currentTree.findNode(retrievedFile.getPath());
		currentNode = retrievedNode;
		currentTree.setCurrentNode(currentNode);
		
		return getFileContent (retrievedFile);
		
	}
	
	/**
	 * This method is used to add the fileNode to the queue to help with retrireving the most recent commits. 
	 * @param pq
	 * @param node
	 */
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
	
	/**
	 * This method is used to get the file content.
	 * @param file
	 * @return
	 */
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
	
	/**
	 * This method is used to write the file name to the directory stored in the database.
	 * @param fileName
	 */
	private void writeFileNameToDirectory (String fileName) {
		try {
			
			FileWriter fileWriter = new FileWriter (gitDatabasePath + "/fileDirectory.txt", true);
			BufferedWriter bw = new BufferedWriter (fileWriter);
			bw.write(fileName + "\n");
			bw.flush();
			bw.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This method is called to init the database if it does not exist before. 
	 * @param gitDatabasePath
	 */
	private static void init (String gitDatabasePath) {
		
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
	
	/**
	 * This method is called to populate files to the database.
	 * @param directoryPath
	 */
	private static void populateFiles (String directoryPath) {
		
		ArrayList<String> storedFiles = new ArrayList<String> ();
		try {
			FileReader fileReader = new FileReader (directoryPath);
			BufferedReader br1 = new BufferedReader (fileReader);
			
			String line = null;
			while ((line = br1.readLine()) != null) {
				if (line.length () > 0)
				storedFiles.add(line);
			}
			
			br1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (String storedFile: storedFiles) {
			
			String path = gitDatabasePath + "/" + storedFile;
			MyFileTree mft = new MyFileTree(storedFile);
			populateData (path, mft);
			fileTreeData.add(mft);
		}	
		
	}
	/**
	 * This method is called to populate detailed file records to the database.
	 * @param filePath
	 * @param mft
	 */
	private static void populateData (String filePath, MyFileTree mft) {
		try {
			FileReader fileReader = new FileReader (filePath);
			BufferedReader br1 = new BufferedReader (fileReader);
			
			String line = null;
			boolean headNodeSet = false;
			while ((line = br1.readLine()) != null && line.length() > 0) {
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
//					System.out.println("SetCommitMessage:" + lineInfo[3]);
				}
				if (lineInfo[4].equals("T")) {
					mft.setCurrentNode(fileNode);
				}
				
				if (headNodeSet == false) {
					mft.setHeadNode(fileNode);
					headNodeSet = true;
				} 
 			}
			
			br1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is called to close the database and stored all the related information to the database.
	 */
	public void closeDatabase() {

		if (currentTree == null) {
			return;
		}
		
		try {
			
			FileWriter fileWriter = new FileWriter (gitDatabasePath + "/" + currentTree.getFileName(), false);
			BufferedWriter bw = new BufferedWriter (fileWriter);
//				bw.write(mft.getFileName() + "\n");
			writeNodeToTheFile (bw, currentTree.getHeadNode());
			bw.flush();
			bw.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is called to write the file node information to the file.
	 * @param bw
	 * @param node
	 * @throws Exception
	 */
	private void writeNodeToTheFile (BufferedWriter bw, FileNode node) throws Exception{
		if (node == null) {
			return;
		}
		
		String writeString = node.getFile().getPath() + ";";
		
		if (node.getParent() != null) {
			writeString += node.getParent().getFile().getPath();
		}
		
		writeString += ";";
		
		if (node.getChildren() != null) {
			for (FileNode child: node.getChildren()) {
				writeString += child.getFile().getPath() + ",";
			}
			
			if (writeString.endsWith(",")) {
				writeString = writeString.substring(0, writeString.length() - 1);
			}
		}
		
		writeString += ";";
		
		if (node.getCommitMessage()!= null) {
			writeString += node.getCommitMessage();
		}
		
		writeString += ";";
		
		if (node == currentNode) {
			writeString += "T";
		} else {
			writeString += "F";
		}
		
		writeString += "\n";
		bw.write(writeString);
		
		if (node.getChildren() != null) {
			for (FileNode child: node.getChildren()) {
				writeNodeToTheFile (bw, child);
			}
		}
	}
}

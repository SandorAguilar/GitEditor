import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This class controls what's displayed on the screen
 * and handle the action events for each available input.
 * @author sandor
 *
 */
public class Controller {

	private File workingFile;
	private File gitFile;

	private JFrame frame;
	private JPanel menuPanel;
	private JPanel statPanel;
	private JPanel spellCheckPanel;
	private JPanel spellCheckButtonPanel;
	private JPanel retrievePanel;

	private JButton openButton;
	private JButton saveButton;
	private JButton retrieveButton;
	private JButton spellCheckButton;
	private JButton updateButton;     

	private JFileChooser fileChooser;
	
	private JLabel blank1;
	private JLabel blank2;
	
	private JLabel retrieveLabel;

	private JTextPane mainTextPane;
	private JTextPane spellCheckPane;
	private JScrollPane mainTextPaneScroll;
	private JScrollPane spellCheckPaneScroll;

	private boolean fileAlreadySaved; // if the file has already been saved.
	private String fileName; // the name of the file that's currently being worked on.
	private String commitMessage;
	private Long commitTime;
	private String previousSave;
	private String homeDir;
	private String gitPath;
	private String gitFileString;
	private String[] toDisplay;
	
	private JComboBox gitCommitList;
	
	GitDatabase database;


	/**
	 * Starts the application.
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Controller c = new Controller();
				c.init();
				c.display();  
				c.fileName = "";
				c.fileAlreadySaved = false;
			}
		}); 
	}

	/**
	 * Initializes the code and creates the necessary 
	 * folder if its not already there.
	 */
	public void init() {
		//view = new View();    
		homeDir = System.getProperty("user.home");
		File file = new File(homeDir + "//.gitEditor");
		database = GitDatabase.getInstance();
		gitPath = file.getAbsolutePath();
		if (!file.exists()) {
			file.mkdir();     
        }
	}

	/**
	 * Displays the gui with given parameters. 
	 */
	public void display() {
		layOutComponents();
		attachListenersToComponents();
		frame.setSize(900, 700);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	/**
	 * Details all components and how they are attached
	 * to the JFrame.
	 */
	private void layOutComponents() {

		frame = new JFrame("GitEditor");
		menuPanel = new JPanel();
		statPanel = new JPanel();
		spellCheckPanel = new JPanel();
		spellCheckButtonPanel = new JPanel();
		retrievePanel = new JPanel();
		menuPanel.setLayout(new GridLayout(1,7));
		statPanel.setLayout(new GridLayout(1,5));
		spellCheckButtonPanel.setLayout(new GridLayout(1,3));
		spellCheckPanel.setLayout(new BorderLayout());
		retrievePanel.setLayout(new BorderLayout());
		

		fileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
		fileChooser.setFileFilter(filter);
		
		blank1 = new JLabel("");
		blank2 = new JLabel("");
		
		retrieveLabel = new JLabel("pick previous commit", SwingConstants.CENTER);

		openButton = new JButton("open");
		saveButton = new JButton("save");
		retrieveButton = new JButton("retrieve");
		updateButton = new JButton("update");
		spellCheckButton = new JButton("spell check");
		
		spellCheckButtonPanel.add(blank1);
		spellCheckButtonPanel.add(spellCheckButton);
		spellCheckButtonPanel.add(blank2);
		
		ArrayList<String> commentList = new ArrayList<>();
		ArrayList<Long> timeList = new ArrayList<>();
		
		commentList.add("initial commit");
		commentList.add("implemented gui");
		commentList.add("refactored the main class");
		
		timeList.add(System.currentTimeMillis());
		timeList.add(System.currentTimeMillis());
		timeList.add(System.currentTimeMillis());
		
		updateGitList(commentList, timeList);

		spellCheckButton.setPreferredSize(new Dimension(10,30));


		menuPanel.add(openButton);
		menuPanel.add(saveButton);
		menuPanel.add(retrievePanel);
		
		mainTextPane = new JTextPane();
		spellCheckPane = new JTextPane();

		spellCheckPane.setPreferredSize(new Dimension(100,100));

		mainTextPane.setEditable(true);

		mainTextPaneScroll = new JScrollPane(mainTextPane);
		spellCheckPaneScroll = new JScrollPane(spellCheckPane);
		spellCheckPaneScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		mainTextPaneScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		
		retrievePanel.add(BorderLayout.NORTH, retrieveLabel);
		retrievePanel.add(BorderLayout.CENTER, gitCommitList);

		spellCheckPanel.add(BorderLayout.NORTH, spellCheckButtonPanel);
		spellCheckPanel.add(BorderLayout.CENTER, spellCheckPaneScroll);

		frame.add(BorderLayout.NORTH, menuPanel);
		frame.add(BorderLayout.SOUTH, spellCheckPanel);
		frame.add(BorderLayout.CENTER, mainTextPaneScroll);
	}

	/**
	 * Attaches the action listeners.
	 */
	private void attachListenersToComponents() {
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
            public void windowClosing(WindowEvent e)
            {
				if ((isFileAlreadySaved() && !mainTextPane.getText().equals(previousSave))
						|| (!isFileAlreadySaved() && mainTextPane.getText().length() > 0)) {
					int returnVal = JOptionPane.showConfirmDialog(frame, "save message before closing?");
					//System.out.println("returnVal: " + returnVal);
					if (returnVal == 0) {
						saveButton.doClick();
						database.closeDatabase(); // closing the database
						e.getWindow().dispose();
					}
					else if (returnVal == 1) {
						database.closeDatabase(); // closing the database
						e.getWindow().dispose();
					} 
				} else {
					e.getWindow().dispose();
				}
            }
		});

		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int returnVal = -1;
				File file = null;
				try {
					returnVal = fileChooser.showOpenDialog(frame);
					if (returnVal == 0) {
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							file = fileChooser.getSelectedFile();
							frame.setTitle("GitEditor - " + file.getName());
						}
						readFile(file);
						setFileAlreadySaved(true);
						workingFile = file.getAbsoluteFile();
						gitFile = new File(gitPath + "//" + fileChooser.getSelectedFile().getName());
						previousSave = mainTextPane.getText();
					}
				} catch (Exception e) {
					
				}
			}
		});

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (!isFileAlreadySaved()) {
					if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
						
						String filePath = fileChooser.getSelectedFile().getAbsolutePath();
						
						File savedFile = new File(filePath + ".txt");
						File gitSavedFile = new File(gitPath + "//" + fileChooser.getSelectedFile().getName() + ".txt");

						if (!database.createNewFile(fileChooser.getSelectedFile().getName(), mainTextPane.getText())) {
							System.out.println("file already in db");
						}
						
						try {
							savedFile.createNewFile();							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						setFileAlreadySaved(true);

						FileWriter writer;
						FileWriter gitWriter;
						try {
							writer = new FileWriter(savedFile);
							writer.write(mainTextPane.getText());
							writer.close();
							
							gitWriter = new FileWriter(gitSavedFile);
							gitWriter.write(mainTextPane.getText());
							gitWriter.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						workingFile = savedFile;
						gitFile = gitSavedFile;
						previousSave = mainTextPane.getText();
						frame.setTitle("GitEditor - " + workingFile.getName());
					}
				} else {
					if (!previousSave.equals(mainTextPane.getText())) {
						FileWriter writer;
						FileWriter gitWriter;
						try {
							writer = new FileWriter(workingFile);
							writer.write(mainTextPane.getText());
							writer.close();
							
							gitWriter = new FileWriter(gitFile);
							gitWriter.write(mainTextPane.getText());
							gitWriter.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						commitMessage = JOptionPane.showInputDialog(frame, "commit message:", null);
						//System.out.print("commitMessage: " + commitMessage);
						previousSave = mainTextPane.getText();
					}
				}
			}
		});

		//retrieveButton.addActionListener(new ActionListener() {
		gitCommitList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String commit = (String)gitCommitList.getSelectedItem();
				System.out.println(commit);
			}
		});

		spellCheckButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// TODO
			}
		});
	}

	/**
	 * Updates the array of items that's used to populate the
	 * drop down on which previous commit to return.
	 * @param commit
	 * @param time
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateGitList(ArrayList<String> commit, ArrayList<Long> time) {
		if (gitCommitList != null) {
			gitCommitList = null;
		}
			
		toDisplay = new String[commit.size() + 1];
		
		toDisplay[0] = "";
		
		for (int i = 0; i < commit.size(); i++) {
			String message = commit.get(i) + " - " + convertLongToDate(time.get(i));
			toDisplay[i+1] = message;
			//System.out.println(toDisplay[i]);
		}
		
		gitCommitList = new JComboBox(toDisplay);
		
		//JComboBox list = new JComboBox(toDisplay);	
	}

	/**
	 * Reads in the selected file and displays it into the main text area.
	 * @param file - the file we're reading in
	 */
	public void readFile(File file) {
		FileReader fr = null;
		try {

			fr = new FileReader(file);
			try {
				mainTextPane.read(fr, null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}             
	}
	
	/**
	 * Converts a time in long to a readable string format.
	 * @param time - the long we want to convert.
	 * @return - the time as a string so its in a readable format.
	 */
	public String convertLongToDate(Long time) {
		Date date = new Date(time);
		Format format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	    //Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
		//System.out.println(format.format(date));
	    return format.format(date);
	}
	
	/**
	 * Getter method for private variable.
	 * @return - if the current file has been saved already.
	 */
	public boolean isFileAlreadySaved() {
		return fileAlreadySaved;
	}

	/**
	 * Setter method for private variable. 
	 * @param fileAlreadySaved - setting if the file has already
	 * been saved. 
	 */
	public void setFileAlreadySaved(boolean fileAlreadySaved) {
		this.fileAlreadySaved = fileAlreadySaved;
	}
}

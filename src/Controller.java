import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
 
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
 
public class Controller {
     
       private View view;
       private Timer timer;
       private TimerTask task;
     
       private JFrame frame;
       private JPanel menuPanel;
       private JPanel statPanel;
     
       private JButton openButton;
       private JButton saveButton;
       private JButton retrieveButton;
     
       
       private JButton updateButton;     
      
       private JFileChooser fileChooser;
     
       private JLabel wordCountLabel;
       private JLabel totalCommitsLabel;
     
       private JLabel wordCountResultLabel;
       private JLabel totalCommitResultLabel;
     
       private JTextPane mainTextPane;
       private JScrollPane mainTextPaneScroll;
       
       private boolean fileAlreadySaved; // if the file has already been saved.
       private String fileName; // the name of the file that's currently being worked on.
     
      
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
     
       public void init() {
             view = new View();
            
       }
     
       public void display() {
    	   		layOutComponents();
    	   		attachListenersToComponents();
            frame.setSize(900, 700);
            frame.setVisible(true);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //timer = new Timer();
            // timer.schedule(new Task(), 0, 5000);
       }
     
       private void layOutComponents() {
           
             frame = new JFrame("GitEditor");
             menuPanel = new JPanel();
             statPanel = new JPanel();
             menuPanel.setLayout(new GridLayout(1,7));
             statPanel.setLayout(new GridLayout(1,5));
            
             fileChooser = new JFileChooser();
             FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
             fileChooser.setFileFilter(filter);
             
            
             openButton = new JButton("open");
             saveButton = new JButton("save");
             retrieveButton = new JButton("retrieve");
             updateButton = new JButton("update");
 
           
            menuPanel.add(openButton);
            menuPanel.add(saveButton);
 
             menuPanel.add(retrieveButton);
 
             mainTextPane = new JTextPane() ;
 
                       
             mainTextPane.setEditable(true);
 
      
             mainTextPaneScroll = new JScrollPane(mainTextPane);
             mainTextPaneScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
           
             wordCountLabel = new JLabel("  Word Count:  ", SwingConstants.RIGHT);
             totalCommitsLabel = new JLabel("  Total Commits:  ", SwingConstants.RIGHT);
 
           
             wordCountResultLabel = new JLabel("", SwingConstants.LEFT);
             totalCommitResultLabel = new JLabel("", SwingConstants.LEFT);
           
             statPanel.add(wordCountLabel);
             statPanel.add(wordCountResultLabel);
             statPanel.add(totalCommitsLabel);
             statPanel.add(totalCommitResultLabel);
 
             statPanel.add(updateButton);
            
             frame.add(BorderLayout.EAST, mainTextPaneScroll);
             frame.add(BorderLayout.NORTH, menuPanel);
             frame.add(BorderLayout.SOUTH, statPanel);
             frame.add(BorderLayout.CENTER, mainTextPaneScroll);
       }
       
       /**
        * Attaches the listeners.
        */
       private void attachListenersToComponents() {
               updateButton.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent event) {
                  String wordCount = Integer.toString(getWordCount());
                   wordCountResultLabel.setText(wordCount);
                  
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
			                   }
			                  readFile(file);
		                  }
					} catch (Exception e) {
						//System.out.println("doh");
					}
	                  //System.out.println("returnVal: " + returnVal);
	                  
	               }
               });
               
               saveButton.addActionListener(new ActionListener() {
	               @Override
	               public void actionPerformed(ActionEvent event) {

	               }
               });
               
               retrieveButton.addActionListener(new ActionListener() {
	               @Override
	               public void actionPerformed(ActionEvent event) {

	               }
               });
       }
      
       /**
        * Counts the number of words that are currently in the main text area.
        * @return - the number of words.
        */
       public int getWordCount() {
                  int count = 0;
                  
                  String text = mainTextPane.getText();
                  
                  //System.out.println("textLength: " + text.length());
                  
                  if (text.length() == 0 || text == null) {
                                 return 0;
                  }
                                 
                  for (int i = 0; i < text.length() - 1; i++) {
 
                	  		char s1 = text.charAt(i);
                	  		char s2 = text.charAt(i+1);
                                 
                                 //System.out.println(i + " -> 1." +  Character.toString(s1) + ":" + (int)s1 +
                                             //               " 2." +  Character.toString(s2) + ":" + (int)s2);
                                 
                        if (((int)s1 != 13 || s1 != ' ') && (s2 == ' ' || i+2 == text.length() || (int)s2 == 13)) {
                                  count++;
                        }
                                 
                        if ((int)s2 == 13 && s1 != ' ') {
                                  count++;
                        }
  
                        else  if (s1 == 13 && s2 == 10) {
                                  count--;
                       }
                  }
                  //System.out.println("count: " + count);
                  //System.out.println("----------------------");
            String wordCount = Integer.toString(count);
            	wordCountResultLabel.setText(wordCount);
            return count;
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
        * 
        * @return - if the current file has been saved already.
        */
       public boolean isFileAlreadySaved() {
        		return fileAlreadySaved;
       }

       /**
        * 
        * @param fileAlreadySaved - setting if the file has already
        * been saved. 
        */
       public void setFileAlreadySaved(boolean fileAlreadySaved) {
    	   		this.fileAlreadySaved = fileAlreadySaved;
	}

			class Task extends TimerTask {
            	   		@Override
                    public void run() {
            	   			// TODO Auto-generated method stub
                        if (mainTextPane.getText().length() > 0) {
                            getWordCount();
                      }
                  }             
               }
}
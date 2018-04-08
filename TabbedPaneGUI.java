import java.awt.*;
import javax.swing.*;

/**
 * A tabbed pane GUI to display different data structures
 * @author Ha Cao and Sarah Abowitz
 * @version Apr 7th, 2018
 */

public class TabbedPaneGUI extends JPanel {	
	public TabbedPaneGUI() {
		super(new GridLayout(1, 1));
		
		// tabbedPane contains all tabs
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setLayout(new FlowLayout());
		
		// Tab for graph
		JComponent panel1 = new JPanel();
		GraphPanel graphPanel = new GraphPanel(panel1);
		tabbedPane.addTab("Graph", panel1);
		
		// Tab for array
		JComponent panel2 = new JPanel();
		// ArrayPanel arrayPanel = new ArrayPanel(panel2);
		tabbedPane.addTab("Array", panel2);
		
		// Add the tabbed pane to this panel
		add(tabbedPane);
		
		// The following line enables to use scrolling tabs
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);	
	}
	
	/** 
	 * Create the GUI and show it. For thread safety,
	 * this method should be invoked from 
	 * the event dispatch thread
	 */
	private static void createAndShowGUI() {
		// Create and set up the window
		JFrame frame = new JFrame("Data Structure Visualizer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Add content to the window
		frame.add(new TabbedPaneGUI(), BorderLayout.CENTER);
		
		// Display the window
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread
		// creating and showing this application's GUI
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		createAndShowGUI();
            }
        });
	}
}

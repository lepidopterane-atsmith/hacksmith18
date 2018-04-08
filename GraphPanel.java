import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.PrintStream;
import java.util.*;
import java.util.Queue;
import javax.swing.*;        

/**
 * A panel to display graph
 * @author Ha Cao and Sarah Abowitz
 * @version Apr 7th, 2018
 */
public class GraphPanel {
	/** The graph to be displayed */
	private static GraphCanvas canvas;

	/** Label for the input mode instructions */
	private JLabel instr;

	/** The input mode */
	private InputMode mode = InputMode.ADD_NODES;

	/** Remember point where last mouse-down event occurred */
	private Point pointUnderMouse;

	/** Remember point where second-last mouse-down event occurred */
	private Point previousPoint;

	/** The number of nodes that have been clicked */
	private int twoNodeClick = 0;

	/** Graph display fields */
	private JPanel panel1;
	private GraphMouseListener gml;

	/** Control field */
	private JPanel panel2;
	
	/** Create an instance of javax.swing.JTextArea control */
	JTextArea txtConsole;

	/** Constructor */
	public GraphPanel(JComponent panel) {
		// Initialize the graph display and control fields
		canvas = new GraphCanvas();
		panel1 = new JPanel();
		gml = new GraphMouseListener();
		instr = new JLabel("Click to add new nodes; drag to move.");
		panel2 = new JPanel();
		txtConsole = new JTextArea();

		// Now create a new TextAreaOutputStream to write to our JTextArea control and wrap a
		// PrintStream around it to support the println/printf methods.
		PrintStream out = new PrintStream(new TextAreaOutputStream(txtConsole));

		// redirect standard output stream to the TextAreaOutputStream
		System.setOut(out);

		// redirect standard error stream to the TextAreaOutputStream
		System.setErr(out);		

		createComponents(panel);
	}

	/** 
	 * A method to find index in the list of point under mouse 
	 * @param pointUnderMouse The point where the last mouse event occurred
	 * @return The index of point under mouse in the list of points
	 */
	public static int getIndex(Point pointUnderMouse) {
		int index = -1;
		for (int i = 0; i < canvas.points.size(); i++) {
			if (canvas.points.get(i).equals(pointUnderMouse)) {
				index = i;
			}
		}
		return index;
	}

	/** 
	 * A method to find index in the list of a particular node ID
	 * @param id The ID of a particular node
	 * @return The index of the ID in the list of IDs
	 */
	public static int getIndex(Integer id) {
		int index = -1;
		for (int i = 0; i < canvas.ids.size(); i++) {
			if (canvas.ids.get(i).equals(id)) {
				index = i;
			}
		}
		return index;
	}

	/** 
	 * A method to find index in the list of the nodes of a particular node 
	 * @param node A particular node
	 * @return The index of the node in the list of nodes
	 */
	public static int getIndex(Graph<String,Integer>.Node node) {
		int index = -1;
		for (int i = 0; i < canvas.graph.nodes.size(); i++) {
			if (canvas.graph.getNode(i).equals(node)) {
				index = i;
			}
		}
		return index;
	}
	
	/**
	 * A method to default the colors of the nodes, the point under mouse, previous point and twoNodeClick boolean,
	 * to return to the original display condition when changing modes
	 */
	public void defaultVar(GraphCanvas canvas) {
		// Paint the nodes as red again just in case the user changes modes while a node is waiting (in yellow).
		// in order to avoid leaving the node still yellow when moving to a new mode
		for (int i = 0; i < canvas.colors.size(); i++) {
			canvas.setNodeColor(i, Color.RED);
		}
		// Default these values to so that new modes can begin from scratch
		pointUnderMouse = new Point();
		previousPoint = new Point();
		twoNodeClick = 0;
		canvas.repaint();
	}

	/** Puts content in the GUI window */
	public void createComponents(JComponent panel) {
		// Graph display
		panel.setLayout(new FlowLayout());
		panel1.setLayout(new BorderLayout());
		canvas.addMouseListener(gml);
		canvas.addMouseMotionListener(gml);
		panel1.add(canvas);
		panel1.add(instr, BorderLayout.NORTH);		

		panel.add(panel1);
		
		// Controls
		panel2.setLayout(new GridLayout(10, 1));

		JButton addPointButton = new JButton("Add/Move Nodes");
		panel2.add(addPointButton);
		addPointButton.addActionListener(new AddNodeListener());

		JButton rmvPointButton = new JButton("Remove Nodes");
		panel2.add(rmvPointButton);
		rmvPointButton.addActionListener(new RmvNodeListener());

		JButton addEdgeButton = new JButton("Add Edges");
		panel2.add(addEdgeButton);
		addEdgeButton.addActionListener(new AddEdgeListener());

		JButton rmvEdgeButton = new JButton("Remove Edges");
		panel2.add(rmvEdgeButton);
		rmvEdgeButton.addActionListener(new RmvEdgeListener());	

		JButton BFTButton = new JButton("Breadth-First Traversal");
		panel2.add(BFTButton);
		BFTButton.addActionListener(new BFTListener());	

		JButton DFTButton = new JButton("Depth-First Traversal");
		panel2.add(DFTButton);
		DFTButton.addActionListener(new DFTListener());	

		JButton shortestPathButton1 = new JButton("Shortest Path to All Nodes");
		panel2.add(shortestPathButton1);
		shortestPathButton1.addActionListener(new ShortestPath1Listener());	

		JButton shortestPathButton2 = new JButton("Shortest Path to One Node");
		panel2.add(shortestPathButton2);
		shortestPathButton2.addActionListener(new ShortestPath2Listener());	

		JButton clearButton = new JButton("Clear Previous Content");
		panel2.add(clearButton);
		clearButton.addActionListener(new ClearListener());	
		
		panel2.add(txtConsole);
		panel2.add(new JScrollPane(txtConsole));
		
		panel.add(panel2);	
	}

	/** 
	 *  Return a point found within the drawing radius of the given location, 
	 *  or null if none
	 *
	 *  @param x  The x coordinate of the location
	 *  @param y  The y coordinate of the location
	 *  @return  A point from the canvas if there is one covering this location, 
	 *  or a null reference if not
	 */
	public Point findNearbyPoint(int x, int y) {
		// Loop over all points in the canvas and see if any of them
		// overlap with the location specified
		int xNear = 0;
		int yNear = 0;
		boolean exist = false;
		Point point = new Point();
		if (canvas.points.size()==0) {
			exist = false;
		} else {
			for (int i=0; i<canvas.points.size(); i++) {
				if (Point2D.distance(x, y, canvas.points.get(i).getX(), canvas.points.get(i).getY()) <= 20) {
					xNear = (int) canvas.points.get(i).getX();
					yNear = (int) canvas.points.get(i).getY();
					exist = true;
				}
			}
		}
		if (exist) {
			for (int i=0; i<canvas.points.size(); i++) {
				if (canvas.points.get(i).equals(new Point(xNear, yNear))) {
					point = canvas.points.get(i);
				}
			}
		} else {
			point = null;
		}
		return point;		
	}

	/** Constants for recording the input mode */
	enum InputMode {
		ADD_NODES, RMV_NODES, ADD_EDGES, RMV_EDGES, BFT, DFT, SHORTEST_PATH_TO_ALL, SHORTEST_PATH_TO_ONE, CLEAR_CONTENT
	}

	/** Listener for AddNode button */
	private class AddNodeListener implements ActionListener {
		/** Event handler for AddPoint button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.ADD_NODES;
			instr.setText("Click to add new nodes or change their location.");
			defaultVar(canvas);
		}
	}

	/** Listener for RmvNode button */
	private class RmvNodeListener implements ActionListener {
		/** Event handler for RmvNode button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.RMV_NODES;
			instr.setText("Click to remove existing nodes.");
			defaultVar(canvas);
		}
	}

	/** Listener for AddEdge button */
	private class AddEdgeListener implements ActionListener {
		/** Event handler for AddEdge button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.ADD_EDGES;
			instr.setText("Click head and tail respectively to add edge.");
			defaultVar(canvas);
		}
	}

	/** Listener for RmvEdge button */
	private class RmvEdgeListener implements ActionListener {
		/** Event handler for RmvEdge button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.RMV_EDGES;
			instr.setText("Click tail and head respectively to remove edge.");
			defaultVar(canvas);
		}
	}

	/** Listener for BFT button */
	private class BFTListener implements ActionListener {
		/** Event handler for BFT button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.BFT;
			instr.setText("Click a node to broad-traverse the graph.");
			defaultVar(canvas);
		}
	}

	/** Listener for DFT button */
	private class DFTListener implements ActionListener {
		/** Event handler for DFT button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.DFT;
			instr.setText("Click a node to deep-traverse the graph.");
			defaultVar(canvas);
		}
	}

	/** Listener for Shortest Paths to All Nodes button */
	private class ShortestPath1Listener implements ActionListener {
		/** Event handler for ShortestPath1 button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.SHORTEST_PATH_TO_ALL;
			instr.setText("Click a node to find shortest paths to other nodes.");
			defaultVar(canvas);
		}
	}

	/** Listener for Shortest Path to One Node button */
	private class ShortestPath2Listener implements ActionListener {
		/** Event handler for ShortestPath2 button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.SHORTEST_PATH_TO_ONE;
			instr.setText("Click two nodes to find shortest path.");
			defaultVar(canvas);
		}
	}

	/** Listener for Clear button */
	private class ClearListener implements ActionListener {
		/** Event handler for Clear button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.CLEAR_CONTENT;
			instr.setText("Previous content is removed");
			// Remove all nodes and edges in canvas and repaint it
			canvas.graph.removeAll();
			canvas.repaint();
			txtConsole.setText(null);
		}
	}

	/** Mouse listener for GraphCanvas element */
	private class GraphMouseListener extends MouseAdapter
	implements MouseMotionListener {

		/** Responds to click event depending on mode */
		public void mouseClicked(MouseEvent e) {
			switch (mode) {
			case ADD_NODES:
				// If the click is not on top of an existing node, create a new node and add it to the canvas.
				// Otherwise, emit a beep, as shown below:
				pointUnderMouse = findNearbyPoint((int) e.getX(), (int) e.getY());
				Point pointToCreate = new Point((int) e.getX(), (int) e.getY());
				if (pointUnderMouse == null) {
					JFrame frame = new JFrame("User's input data of the nodes");
					// Prompt the user to enter the input data and IDs of the nodes 
					String dataNode = JOptionPane.showInputDialog(frame, "What's the data of this node?");
					String idNode = JOptionPane.showInputDialog(frame, "What's the ID of this node?");
					canvas.graph.addNode(dataNode);
					canvas.ids.add(Integer.valueOf(idNode));
					canvas.points.add(pointToCreate);
					canvas.colors.add(Color.RED);
				} else {
					Toolkit.getDefaultToolkit().beep();
					JFrame frame = new JFrame("");
					// Warning
					JOptionPane.showMessageDialog(frame,
							"Failed click on empty space. Start adding nodes again.",
							"Click Warning",
							JOptionPane.WARNING_MESSAGE);
				}
				canvas.repaint();
				break;
			case RMV_NODES:
				// If the click is on top of an existing node, remove it from the canvas's graph.
				// Otherwise, emit a beep.
				pointUnderMouse = findNearbyPoint((int) e.getX(), (int) e.getY());
				if (pointUnderMouse != null) {
					for (int i=0; i<canvas.points.size(); i++) {
						if (canvas.points.get(i).equals(pointUnderMouse)) {
							canvas.graph.removeNode(canvas.graph.getNode(i));
							canvas.points.remove(canvas.points.get(i));
							canvas.ids.remove(canvas.ids.get(i));
							canvas.colors.remove(canvas.colors.get(i));
						}
					}					
				} else {
					Toolkit.getDefaultToolkit().beep();		
					JFrame frame = new JFrame("");
					// Warning
					JOptionPane.showMessageDialog(frame,
							"Failed click on nodes. Start removing nodes again.",
							"Click Warning",
							JOptionPane.WARNING_MESSAGE);
				}
				canvas.repaint();
				break;		
			case ADD_EDGES:
				// If the click is not on top of an existing node, emit a beep, as shown below.
				// Otherwise, check how many nodes have been clicked;
				// If only 1, save the node (which is supposed to be the head).
				// If already 2, create an edge between the two nodes. 

				pointUnderMouse = findNearbyPoint((int) e.getX(), (int) e.getY());
				if (pointUnderMouse != null) {	
					twoNodeClick++;
					if (twoNodeClick == 2) {
						canvas.graph.addEdge((int) Point2D.distance(previousPoint.getX(), previousPoint.getY(), pointUnderMouse.getX(), pointUnderMouse.getY()),
								canvas.graph.getNode(getIndex(previousPoint)), canvas.graph.getNode(getIndex(pointUnderMouse)));
						twoNodeClick = 0;
						canvas.setNodeColor(getIndex(previousPoint), Color.RED);	
					} else {
						previousPoint = findNearbyPoint((int) e.getX(), (int) e.getY());
						// Change color of the waiting node
						canvas.setNodeColor(getIndex(previousPoint), Color.YELLOW);						
					}
				} else {
					Toolkit.getDefaultToolkit().beep();		
					twoNodeClick = 0;
					for (int i = 0; i<canvas.colors.size(); i++) {
						canvas.setNodeColor(i, Color.RED);
					}					
					JFrame frame = new JFrame("");
					// Warning
					JOptionPane.showMessageDialog(frame,
							"Failed click on nodes. Start adding edges again.",
							"Click Warning",
							JOptionPane.WARNING_MESSAGE);
				}
				canvas.repaint();
				break ;		
			case RMV_EDGES:
				// If the click is not on top of an existing node, emit a beep, as shown below.
				// Otherwise, check how many nodes have been clicked;
				// If only 1, save the node (which is supposed to be the head).
				// If already 2, remove the edge between the two nodes. 
				pointUnderMouse = findNearbyPoint((int) e.getX(), (int) e.getY());
				if (pointUnderMouse != null) {
					twoNodeClick++;
					if (twoNodeClick == 2) {
						Graph<String,Integer>.Edge edge = canvas.graph.getEdgeRef(canvas.graph.getNode(getIndex(previousPoint)), 
								canvas.graph.getNode(getIndex(pointUnderMouse)));
						if (edge != null) {
							for (int i = 0; i < canvas.graph.edges.size(); i++) {
								if (canvas.graph.getEdge(i).equals(edge)) {
									canvas.graph.removeEdge(canvas.graph.getEdge(i));
								}
							}	
							twoNodeClick = 0;
							canvas.setNodeColor(getIndex(previousPoint), Color.RED);
						} else {
							Toolkit.getDefaultToolkit().beep();		
							twoNodeClick = 0;
							canvas.setNodeColor(getIndex(previousPoint), Color.RED);
							JFrame frame = new JFrame("");
							// Warning
							JOptionPane.showMessageDialog(frame,
									"Edge doesn't exist. Start removing edge again.",
									"Click Warning",
									JOptionPane.WARNING_MESSAGE);
						} 
					} else {
						previousPoint = findNearbyPoint((int) e.getX(), (int) e.getY());
						// Change color of the waiting node
						canvas.setNodeColor(getIndex(previousPoint), Color.YELLOW);	
					}
				} else {
					Toolkit.getDefaultToolkit().beep();		
					twoNodeClick = 0;
					for (int i = 0; i < canvas.colors.size(); i++) {
						canvas.setNodeColor(i, Color.RED);
					}	
					JFrame frame = new JFrame("");
					// Warning
					JOptionPane.showMessageDialog(frame,
							"Failed click on nodes. Start removing edges again.",
							"Click Warning",
							JOptionPane.WARNING_MESSAGE);
				}
				canvas.repaint();
				break;	
			case BFT:
				// If the click is on top of an existing node, perform a breadth-first traversal of the graph from this node.
				// Otherwise, emit a beep.
				pointUnderMouse = findNearbyPoint((int) e.getX(), (int) e.getY());
				if (pointUnderMouse!=null) {
					Queue<Graph<String, Integer>.Node> broadPath = canvas.graph.BFT(canvas.graph.getNode(getIndex(pointUnderMouse)));
					// Result is printed out in the console
					System.out.println("\nBreadth first traversal of the graph from the node "+canvas.graph.getNode(getIndex(pointUnderMouse)).getData()+":");
					for (int i=0; i<canvas.graph.nodes.size(); i++) {
						if (!broadPath.contains(canvas.graph.getNode(i))) {
							System.out.println("("+canvas.graph.getNode(i).getData()+" is unreachable from "+canvas.graph.getNode(getIndex(pointUnderMouse)).getData()+")");
						}
					}

					// Print out the result in the console
					while (!broadPath.isEmpty()) {
						if (broadPath.size()>1) {
							System.out.print(broadPath.remove().getData()+" ---> ");
						} else {
							System.out.print(broadPath.remove().getData()+" ");
						}
					}
					System.out.println("");

				} else {
					Toolkit.getDefaultToolkit().beep();	
					JFrame frame = new JFrame("");
					// Warning
					JOptionPane.showMessageDialog(frame,
							"Failed click on nodes. Start BFT again.",
							"Click Warning",
							JOptionPane.WARNING_MESSAGE);
				}
				canvas.repaint();
				break;		
			case DFT:
				// If the click is on top of an existing node, perform a depth-first traversal of the graph from this node.
				// Otherwise, emit a beep.
				pointUnderMouse = findNearbyPoint((int) e.getX(), (int) e.getY());
				if (pointUnderMouse!=null) {
					Stack<Graph<String, Integer>.Node> deepPath = canvas.graph.DFT(canvas.graph.getNode(getIndex(pointUnderMouse)));
					// Result is printed out in the console
					System.out.println("\nDepth first traversal of the graph from the node "+canvas.graph.getNode(getIndex(pointUnderMouse)).getData()+":");
					for (int i=0; i<canvas.graph.nodes.size(); i++) {
						if (!deepPath.contains(canvas.graph.getNode(i))) {
							System.out.println("("+canvas.graph.getNode(i).getData()+" is unreachable from "+canvas.graph.getNode(getIndex(pointUnderMouse)).getData()+")");
						}
					}

					// Print out the result in the console
					while (!deepPath.isEmpty()) {
						if (deepPath.size()>1) {
							System.out.print(deepPath.pop().getData()+" ---> ");
						} else {
							System.out.print(deepPath.pop().getData()+" ");
						}
					}
					System.out.println("");

				} else {
					Toolkit.getDefaultToolkit().beep();		
					JFrame frame = new JFrame("");
					// Warning
					JOptionPane.showMessageDialog(frame,
							"Failed click on nodes. Start DFT again.",
							"Click Warning",
							JOptionPane.WARNING_MESSAGE);
				}
				canvas.repaint();
				break;	
			case SHORTEST_PATH_TO_ALL:
				// If the click is on top of an existing node, find the shortest paths from that node to all other nodes in the graph.
				// Otherwise, emit a beep.
				pointUnderMouse = findNearbyPoint((int) e.getX(), (int) e.getY());
				if (pointUnderMouse!=null) {
					// Result is printed out in the console
					canvas.graph.shortestPath(canvas.graph.getNode(getIndex(pointUnderMouse)), canvas.graph);
					System.out.println("");

				} else {
					Toolkit.getDefaultToolkit().beep();	
					JFrame frame = new JFrame("");
					// Warning
					JOptionPane.showMessageDialog(frame,
							"Failed click on nodes. Start finding shortest paths to all other nodes again.",
							"Click Warning",
							JOptionPane.WARNING_MESSAGE);
				}
				canvas.repaint();
				break;	
			case SHORTEST_PATH_TO_ONE:
				// If the click is not on top of an existing node, emit a beep, as shown below.
				// Otherwise, check how many nodes have been clicked;
				// If only 1, save the node (which is supposed to be the head).
				// If already 2, find the shortest path from the 1st clicked node to the 2nd clicked node
				pointUnderMouse = findNearbyPoint((int) e.getX(), (int) e.getY());
				if (pointUnderMouse!=null) {
					twoNodeClick++;
					if (twoNodeClick==2) {
						// Result is printed out in the console
						canvas.graph.shortestPath(canvas.graph.getNode(getIndex(previousPoint)), canvas.graph.getNode(getIndex(pointUnderMouse)), canvas.graph);
						twoNodeClick = 0;
						canvas.setNodeColor(getIndex(previousPoint), Color.RED);
					} else {
						previousPoint = findNearbyPoint((int) e.getX(), (int) e.getY());
						// Change color of the waiting node
						canvas.setNodeColor(getIndex(previousPoint), Color.YELLOW);	
					}
				} else {
					Toolkit.getDefaultToolkit().beep();		
					for (int i=0; i<canvas.colors.size(); i++) {
						canvas.setNodeColor(i, Color.RED);
					}	
					twoNodeClick = 0;JFrame frame = new JFrame("");
					// Warning
					JOptionPane.showMessageDialog(frame,
							"Failed click on nodes. Start choosing nodes again.",
							"Click Warning",
							JOptionPane.WARNING_MESSAGE);
				}
				canvas.repaint();
				break;	
			case CLEAR_CONTENT:
				break;
			}
		}

		/** Records point under mouse-down event in anticipation of possible drag */
		public void mousePressed(MouseEvent e) {
			// Record point under mouse, if any
			pointUnderMouse = findNearbyPoint((int) e.getX(), (int) e.getY());
		}

		/** Responds to mouse-up event */
		public void mouseReleased(MouseEvent e) {
			// Clear record of point under mouse, if any
			pointUnderMouse = null;
		}

		/** Responds to mouse drag event */
		public void mouseDragged(MouseEvent e) {
			// If mode allows node motion, and there is a point under the mouse, 
			// then change its coordinates to the current mouse coordinates & update display
			if ((mode == InputMode.ADD_NODES) && (pointUnderMouse != null)) {
				pointUnderMouse.setLocation((int) e.getX(), (int) e.getY());
				// Loop through the edge list of this particular node to update the distances
				for (int i = 0; i < canvas.graph.getNode(getIndex(pointUnderMouse)).getMyEdges().size(); i++) {
					Graph<String,Integer>.Edge edge = canvas.graph.getNode(getIndex(pointUnderMouse)).getMyEdges().get(i);
					Point headPoint = canvas.points.get(getIndex(edge.getHead()));
					Point tailPoint = canvas.points.get(getIndex(edge.getTail()));
					edge.setData((int) Point2D.distance((int) headPoint.getX(), (int) headPoint.getY(), (int) tailPoint.getX(), (int) tailPoint.getY()));
				}
			canvas.repaint();
		}
	}

	// Empty but necessary to comply with MouseMotionListener interface
	public void mouseMoved(MouseEvent e) {}
}
} // end of GraphGUI
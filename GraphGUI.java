import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.Queue;
import java.io.*;
import javax.swing.*;        

/**
 *  A class to implement a GUI that combines GUI interface and text-based input,
 * 	to display a graph of nodes (containing strings) and edges (containing weights/distances)
 *  @author  Ha Cao (modded for Array support by Sarah Abowitz)
 *  @version CSC 112, May 1st 2017
 */
public class GraphGUI {
	
	boolean arrayMode = true;
	
	// I need a mock array if I'm gonna do this 
	// {Alice, Bob, Carol, Dave, Elise, Fred, Grandpa, Ha, Innes, Jessica}
	
	private String addPointStr, rmvPointStr, addEdgeStr, rmvEdgeStr;
	private String addPtInstr, rmvPtInstr, addEdgeInstr, rmvEdgeInstr;
	
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

	/** The graph frame */
	private JFrame graphFrame;

	/** Graph display fields */
	private Container pane;
	private JPanel panel1;
	private GraphMouseListener gml;

	/** Control field */
	private JPanel panel2;

	/** Constructor */
	public GraphGUI() {
		// Initialize the graph display and control fields
		graphFrame = new JFrame("Graph GUI");
		pane = graphFrame.getContentPane();
		canvas = new GraphCanvas();
		panel1 = new JPanel();
		gml = new GraphMouseListener();
		instr = new JLabel("Click to add new nodes; drag to move.");
		panel2 = new JPanel();
	}

	/** 
	 * A method to find index in the list of point under mouse 
	 * @param pointUnderMouse The point where the last mouse event occurred
	 * @return The index of point under mouse in the list of points
	 */
	public static int getIndex(Point pointUnderMouse) {
		int index = -1;
		for (int i=0; i<canvas.points.size(); i++) {
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
		for (int i=0; i<canvas.ids.size(); i++) {
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
		for (int i=0; i<canvas.graph.nodes.size(); i++) {
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
		for (int i=0; i<canvas.colors.size(); i++) {
			canvas.setNodeColor(i, Color.RED);
		}
		// Default these values to so that new modes can begin from scratch
		pointUnderMouse = new Point();
		previousPoint = new Point();
		twoNodeClick = 0;
		canvas.repaint();
	}

	/**
	 *  Schedule a job for the event-dispatching thread,
	 *  creating and showing this application's GUI
	 */
	public static void main(String[] args) {
		final GraphGUI GUI = new GraphGUI();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GUI.createAndShowGUI();
			}
		});

		// If the user input a file 
		if (args.length != 0) {
			// Read the input problem file
			BufferedReader file = null;		
			try {
				file = new BufferedReader(new FileReader(args[0]));
				String thisLine;
				while ((thisLine = file.readLine()) != null) {
					// Real index in the lists of nodes/edges
					// If this line is about a node
					if (thisLine.charAt(0) == 'n') {
						// First white space
						int w1 = thisLine.indexOf(" ");
						// Second white space
						int w2 = thisLine.indexOf(" ", w1+1);
						// Third white space
						int w3 = thisLine.indexOf(" ", w2+1);
						// Fourth white space
						int w4 = thisLine.indexOf(" ", w3+1);
						// ID of the node
						canvas.ids.add(Integer.valueOf(thisLine.substring(w1+1, w2).replaceAll(" ", "")));
						// x-coordinate of the point representing this node
						int x = Integer.valueOf(thisLine.substring(w2+1, w3).replaceAll(" ", ""));
						// y-coordinate of the point representing this node
						int y = Integer.valueOf(thisLine.substring(w3+1, w4).replaceAll(" ", ""));
						// Add the point of this node
						canvas.points.add(new Point(x, y));
						// Add this node to the graph
						canvas.graph.addNode(thisLine.substring(w4+1, thisLine.length()));
						// Add the color of this node
						canvas.colors.add(Color.RED);
						canvas.repaint();
					} else if (thisLine.charAt(0) == 'e') { // If this line is about an edge
						// First white space
						int w1 = thisLine.indexOf(" ");
						// Second white space
						int w2 = thisLine.indexOf(" ", w1+1);
						// Third white space
						int w3 = thisLine.indexOf(" ", w2+1);
						// ID of head of this edge
						int headID = Integer.valueOf(thisLine.substring(w1+1, w2).replaceAll(" ", ""));
						// ID of tail of this edge
						int tailID = Integer.valueOf(thisLine.substring(w2+1, w3).replaceAll(" ", ""));
						// Add this edge to the graph
						canvas.graph.addEdge((int) Point2D.distance(canvas.points.get(getIndex(headID)).getX(), canvas.points.get(getIndex(headID)).getY(), 
								canvas.points.get(getIndex(tailID)).getX(), canvas.points.get(getIndex(tailID)).getY()),
								canvas.graph.getNode(getIndex(headID)), canvas.graph.getNode(getIndex(tailID)));
						canvas.repaint();
					}					
				}
			} catch (IOException e) {
				System.err.println("Problem reading file "+file);
				System.exit(-1);
			}
		}
	}

	/** Set up the GUI window */
	public void createAndShowGUI() {
		// Make sure we have nice window decorations
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window
		graphFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add components
		createComponents(graphFrame);

		// Display the window.
		graphFrame.pack();
		graphFrame.setVisible(true);
	}

	/** Puts content in the GUI window */
	public void createComponents(JFrame frame) {
		// Graph display
		pane.setLayout(new FlowLayout());
		panel1.setLayout(new BorderLayout());
		canvas.addMouseListener(gml);
		canvas.addMouseMotionListener(gml);
		panel1.add(canvas);
		panel1.add(instr,BorderLayout.NORTH);
		pane.add(panel1);

		// Controls
		panel2.setLayout(new GridLayout(5,2));

		if (arrayMode){
			addPointStr = "Add Entry";
			rmvPointStr = "Remove Entry";
			addEdgeStr = "Access Entry"; // simulation will simulate getting that entry
			rmvEdgeStr = "Search Array";
		}else{
			addPointStr = "Add/Move Nodes";
			rmvPointStr = "Remove Nodes";
			addEdgeStr = "Add Edges";
			rmvEdgeStr = "Remove Edges";
		}
		
		JButton addPointButton = new JButton(addPointStr);
		panel2.add(addPointButton);
		addPointButton.addActionListener(new AddNodeListener());

		JButton rmvPointButton = new JButton(rmvPointStr);
		panel2.add(rmvPointButton);
		rmvPointButton.addActionListener(new RmvNodeListener());

		JButton addEdgeButton = new JButton(addEdgeStr);
		panel2.add(addEdgeButton);
		addEdgeButton.addActionListener(new AddEdgeListener());

		JButton rmvEdgeButton = new JButton(rmvEdgeStr);
		panel2.add(rmvEdgeButton);
		rmvEdgeButton.addActionListener(new RmvEdgeListener());	

		if (!arrayMode){
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
	
			JButton outputButton = new JButton("Print a New Graph File");
			panel2.add(outputButton);
			outputButton.addActionListener(new OutputListener());	
		}
		
		pane.add(panel2);	
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
		ADD_NODES, RMV_NODES, ADD_EDGES, RMV_EDGES, BFT, DFT, SHORTEST_PATH_TO_ALL, SHORTEST_PATH_TO_ONE, OUT_PUT
	}

	/** Listener for AddNode button */
	private class AddNodeListener implements ActionListener {
		/** Event handler for AddPoint button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.ADD_NODES;
			if (arrayMode){
				addPtInstr = "Click an empty array slot to add an element.";
			} else {
				addPtInstr = "Click to add new nodes or change their location.";
			}
			instr.setText(addPtInstr);
			defaultVar(canvas);
		}
	}

	/** Listener for RmvNode button */
	private class RmvNodeListener implements ActionListener {
		/** Event handler for RmvNode button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.RMV_NODES;
			if (arrayMode){
				rmvPtInstr = "Click an array element to remove it.";
			} else {
				rmvPtInstr = "Click to remove existing nodes.";
			}
			instr.setText(rmvPtInstr);
			defaultVar(canvas);
		}
	}
	
	/** Listener for AddEdge button */
	private class AddEdgeListener implements ActionListener {
		/** Event handler for AddEdge button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.ADD_EDGES;
			if (arrayMode){
				addEdgeInstr = "Click an array element to access it.";
			} else {
				addEdgeInstr = "Click head and tail respectively to add edge.";
			}
			instr.setText(addEdgeInstr);
			defaultVar(canvas);
		}
	}

	/** Listener for RmvEdge button */
	private class RmvEdgeListener implements ActionListener {
		/** Event handler for RmvEdge button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.RMV_EDGES;
			if (arrayMode){
				rmvEdgeInstr = "Click an array element to search for it.";
			} else {
				rmvEdgeInstr = "Click tail and head respectively to remove edge.";
			}
			instr.setText(rmvEdgeInstr);
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

	/** Listener for Output button */
	private class OutputListener implements ActionListener {
		/** Event handler for Output button */
		public void actionPerformed(ActionEvent e) {
			mode = InputMode.OUT_PUT;
			instr.setText("New graph file is printed.");
			defaultVar(canvas);

			// Print the output in the file
			PrintWriter out = null;
			try {
				out = new PrintWriter(new FileWriter("graph_output.txt"));
			} catch (IOException ex) {
				System.err.println("Error with output file.");
			}
			// Print the problem statement line
			out.println("p "+canvas.graph.numNodes()+" "+canvas.graph.numEdges());
			// Print the nodes
			for (int i=0; i<canvas.graph.nodes.size(); i++) {
				out.println("n "+canvas.ids.get(i)+" "+ (int) canvas.points.get(i).getX()+" "+
						(int) canvas.points.get(i).getY()+" "+ canvas.graph.nodes.get(i).getData());
			}
			// Print the edges
			for (int i=0; i<canvas.graph.edges.size(); i++) {
				out.println("e "+ canvas.ids.get(getIndex(canvas.graph.edges.get(i).getHead()))+" "+
						canvas.ids.get(getIndex(canvas.graph.edges.get(i).getTail())) +" "+
						canvas.graph.getEdge(i).getData());
			}
			out.close();
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
				if (pointUnderMouse==null) {
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
				if (pointUnderMouse!=null) {
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
				if (pointUnderMouse!=null) {	
					twoNodeClick++;
					if (twoNodeClick==2) {
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
					for (int i=0; i<canvas.colors.size(); i++) {
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
				if (pointUnderMouse!=null) {
					twoNodeClick++;
					if (twoNodeClick==2) {
						Graph<String,Integer>.Edge edge = canvas.graph.getEdgeRef(canvas.graph.getNode(getIndex(previousPoint)), 
								canvas.graph.getNode(getIndex(pointUnderMouse)));
						if (edge!=null) {
							for (int i=0; i<canvas.graph.edges.size(); i++) {
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
					for (int i=0; i<canvas.colors.size(); i++) {
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
			case OUT_PUT:
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
			if ((mode == InputMode.ADD_NODES) && (pointUnderMouse!=null)) {
				pointUnderMouse.setLocation((int) e.getX(), (int) e.getY());
				// Loop through the edge list of this particular node to update the distances
				for (int i=0; i<canvas.graph.getNode(getIndex(pointUnderMouse)).getMyEdges().size(); i++) {
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
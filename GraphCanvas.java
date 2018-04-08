import java.util.*;
import java.util.Timer;
import java.awt.*;
import javax.swing.*;  

/**
 *  Implement a graphical canvas that displays a graph of nodes (represented by points) and edges (represented by lines)
 *
 *  @author  Ha Cao (modded by Sarah Abowitz)
 *  @version CSC 112, May 1st 2017
 */
class GraphCanvas extends JComponent {	
	private static final long serialVersionUID = 1L;
	/** The graph */
	protected Graph<String, Integer> graph;
	/** The points that represent the nodes */
	protected LinkedList<Point> points;
	/** The list of temporary IDs of the nodes */
	protected LinkedList<Integer> ids;
	/** The list of colors of all the nodes */
	protected LinkedList<Color> colors;
	private int mode = 1;
	// Graph = 0, Array = 1, more coming ;)
	private int[] arr = {211, 222, 233, 244, 255, 266, 277, 288, 299};
	private ArrayList<String> annotations = new ArrayList<String>();

	/** Constructor */
	public GraphCanvas() {
		graph = new Graph<String, Integer>();
		points = new LinkedList<Point>();
		colors = new LinkedList<Color>();
		ids = new LinkedList<Integer>();
		setMinimumSize(new Dimension(500,700));
		setPreferredSize(new Dimension(500,700));
	}
	
	/**
     * Draw an arrow line between two points 
     * 
     * @param g The graphic component
     * @param x1 x-coordinate of start point
     * @param y1 y-coordinate of start point
     * @param x2 x-coordinate of end point
     * @param y2 y-coordinate of end point
     */
    public static void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2){
       int dx = x2 - x1, dy = y2 - y1;
       double D = Math.sqrt(dx*dx + dy*dy);
       double xm = D - 5, xn = xm, ym = 5, yn = -5, x;
       double sin = dy/D, cos = dx/D;

       x = xm*cos - ym*sin + x1;
       ym = xm*sin + ym*cos + y1;
       xm = x;

       x = xn*cos - yn*sin + x1;
       yn = xn*sin + yn*cos + y1;
       xn = x;
       
       // Create x-points and y-points arrays for the Polygon
       int[] xpoints = {x2, (int) xm, (int) xn};
       int[] ypoints = {y2, (int) ym, (int) yn};

       // Draw the line
       g.drawLine(x1, y1, x2, y2);
       // Draw the arrow part
       g.fillPolygon(xpoints, ypoints, 3);
    }
    
    /**
     * A method to get the color of a particular node given its index
     * 
     * @param i The index of the node
     * @return The color of the node
     */
    public Color getColor(int i) {
		return colors.get(i);
	}
    
    /**
     * Change color of a particular node given its index
     * 
     * @param i The index of the node
     * @param c The new color
     */
    public void setNodeColor(int i, Color c) {
    	colors.set(i, c);
    }

    public void setArr(int[] array){
    	for(int i=0; i<array.length; i++){
    		arr[i] = array[i];
    	}
    }
    
    public int[] getArr(){
    	return arr;
    }
    
    public void setMode(int i){
    	mode = i;
    }
    
    public int getMode(){
    	return mode;
    }
    
    public void arrRemoval(int index){
    	Timer timer = new Timer();
    	int bound = arr.length;
    	long delay = 1000;
    	for (int j = 0; j < arr.length; j++){
    		final int i = j;
    		timer.schedule(new TimerTask(){
    			@Override 
    			public void run(){
    				if (i >= index && i < arr.length-1){
    	    			annotations.add("array["+i+"] = "+arr[i+1]);
    	    		} else if (i < arr.length-1){
    	    			annotations.add("Remains the same");
    	    		} else {
    	    			annotations.add("This slot does not exist in the new array");
    	    		}
    				GraphCanvas.this.repaint();
    			}
    			}, (2500*j));
    		
    	}
    	
    	timer.schedule(new TimerTask(){
    		@Override
    		public void run(){int[] temp = new int[bound-1];
        	for(int i = 0; i < bound-1; i++){
        		if (i>= index && i < bound-1){
        			temp[i]=arr[i+1];
        		} else {
        			temp[i]=arr[i];
        		}
        	}
        	
        	arr = new int[bound-1];
        	
        	for(int i=0; i<arr.length; i++){
        		arr[i] = temp[i];
        	}
        	
        	annotations.clear();
        	GraphCanvas.this.repaint();
        	
        	}}, 2500*bound);
    	
    	// for ()
    	// redo the array, bye bye annotations, repaint
    }
    
	/**
	 *  Paint a red circle 20 pixels in diameter at each point to represent a node,
	 *  a blue line to represent an edge, and a string label to represent the data of a node
	 *
	 *  @param g The graphics object to draw with
	 */
	public void paintComponent(Graphics g) {
		
		if (mode == 0){
			// Paint the nodes
			for (int i=0; i<graph.nodes.size(); i++) {
				g.setColor(colors.get(i));
				g.fillOval((int) points.get(i).getX(), (int) points.get(i).getY(), 20, 20);	
				// Paint the data of the nodes
				g.setColor(Color.BLACK);
				//g.drawString(graph.getNode(i).getData(), (int) points.get(i).getX()+30, (int) points.get(i).getY()+30);
				
				String tester = "X: "+points.get(i).getX()+" Y: "+points.get(i).getY();
				g.drawString(tester, (int) points.get(i).getX()+30, (int) points.get(i).getY()+30);
			}
			
			// Paint the edges
			for (int i=0; i<graph.edges.size(); i++) {
				g.setColor(Color.BLUE);
				drawArrowLine(g, ((int) points.get(graph.getEdge(i).getHead().getIndex()).getX())+10, ((int) points.get(graph.getEdge(i).getHead().getIndex()).getY())+10, 
						((int) points.get(graph.getEdge(i).getTail().getIndex()).getX())+10, ((int) points.get(graph.getEdge(i).getTail().getIndex()).getY())+10);
			}
		} else if(mode == 1){
			for (int i=0; i < arr.length; i++){
				g.setColor(Color.GREEN);
				g.fillRect(22, 26+(60*i), 400, 50);
				g.setColor(Color.BLACK);
				g.drawString("array["+i+"] = "+arr[i], 30, 50+(60*i));
				if (annotations.size()>i && annotations.size()>0){
					g.drawString(annotations.get(i), 150, 50+(60*i));
					
				}
			}
			
			
		}
		
	}
} // end of GraphCanvas
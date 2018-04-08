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

public class ArrayCanvas extends JComponent{
	private int[] arr = {211, 222, 233, 244, 255, 266, 277, 288, 299};
	private ArrayList<String> annotations = new ArrayList<String>();
	boolean addingToArr = false;
	private int access = 10;

	/** Constructor */
	public ArrayCanvas() {
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
    
    public void setArr(int[] array){
    	for(int i=0; i<array.length; i++){
    		arr[i] = array[i];
    	}
    }
    
    public int[] getArr(){
    	return arr;
    }
    
    public void arrSearch(int match){
    	// while this thing isn't found or we still have array in front of  us
    	int j = 0;
    	Timer timer = new Timer();
    	long delay = 2500;
    	boolean found = false;
    	while (!found && j <= arr.length){
    		final int i = j;
    		timer.schedule(new TimerTask(){
    			@Override
    			public void run(){
    				if (i > arr.length){
    					
    				} else if (arr[i] == match){
    					annotations.add("Query found!");
    					// DO SOMETHING
    				} else {
    					annotations.add("Still searching...");
    				}
    				ArrayCanvas.this.repaint();
    			}
    		}, delay*j);  
    		
    	}
    }
    
    public void arrAccess(int index){
    	access = index;
    	ArrayCanvas.this.repaint();
    }
    
    public void arrAddition(int index,int num){
    	addingToArr = true;
    	Timer timer = new Timer();
    	int bound = arr.length;
    	long delay = 1000;
    	int j;
    	for (j = 0; j <= arr.length; j++){
    		final int i = j;
    		timer.schedule(new TimerTask(){
    			@Override 
    			public void run(){
    				if (i > index){
    	    			annotations.add("array["+i+"] = "+arr[i-1]);
    	    		} else if (i < index){
    	    			annotations.add("Remains the same");
    	    		} else {
    	    			annotations.add("array["+i+"] = "+num);
    	    		}
    				ArrayCanvas.this.repaint();
    			}
    			}, (2500*j));
    		
    	}
    	final int k = j+1;
    	timer.schedule(new TimerTask(){
    		@Override
    		public void run(){
    		try{
    				annotations.add("array["+k+"] = "+arr[-1]);
    		} catch (ArrayIndexOutOfBoundsException a){
    			a.printStackTrace();
    		}
    		ArrayCanvas.this.repaint();
    		
    		int[] temp = new int[bound+1];
        	for(int i = 0; i < bound+1; i++){
        		if (i> index){
        			temp[i]=arr[i-1];
        		} else if (i == index){
        			temp[i]=num; 
        		} else {
        			temp[i]=arr[i];
        		}
        	}
        	
        	arr = new int[bound+1];
        	
        	for(int i=0; i<arr.length; i++){
        		arr[i] = temp[i];
        	}
        	
        	annotations.clear();
        	ArrayCanvas.this.repaint();
        	
        	}}, 2500*bound);
    }
    
    public void arrRemoval(int index){
    	addingToArr=false;
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
    				ArrayCanvas.this.repaint();
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
        	ArrayCanvas.this.repaint();
        	
        	}}, 2500*bound);
    }
    
	/**
	 *  Paint a red circle 20 pixels in diameter at each point to represent a node,
	 *  a blue line to represent an edge, and a string label to represent the data of a node
	 *
	 *  @param g The graphics object to draw with
	 */
	public void paintComponent(Graphics g) {
			Color c = Color.GREEN;
			for (int i=0; i < arr.length; i++){
				if (i == access){
					c = Color.CYAN;
				} else {
					c = Color.GREEN;
				}
				// System.out.println("i: "+i+" access: "+access);
				g.setColor(c);
				g.fillRect(22, 26+(60*i), 400, 50);
				g.setColor(Color.BLACK);
				g.drawString("array["+i+"] = "+arr[i], 30, 50+(60*i));
				if (annotations.size()>i && annotations.size()>0){
					g.drawString(annotations.get(i), 150, 50+(60*i));
					
				}
				if (addingToArr && arr.length < annotations.size()){
					g.drawString(annotations.get(annotations.size()-1), 150, 50+(60*arr.length));
				}
			}
			
			access = 10;
		
	}
}

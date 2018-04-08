import java.lang.Object;
import java.util.*;

/**
 * A class to implement directed graph data structure
 * @author Ha Cao
 * @version CSC 212, April 27, 2017
 */
public class Graph<V,E>{
	/** The list of edges */
	protected LinkedList<Edge> edges;
	/** The list of nodes */
	protected LinkedList<Node> nodes;

	/** Constructor for class */
	public Graph() {
		edges = new LinkedList<Edge>();
		nodes = new LinkedList<Node>();
	}

	/** Add an edge */
	public void addEdge(E data, Node head, Node tail) {
		Edge edge = new Edge(data, head, tail);
		head.addEdgeRef(edge);
		tail.addEdgeRef(edge);
		edges.add(edge);
	}

	/** Add a node */
	public void addNode(V data) {
		Node node = new Node(data);
		nodes.add(node);
	}

	/** Check the consistency of the graph */
	public void check() {
		// For every edge on the central list
		for (int i=0; i<edges.size(); i++) {
			if (!edges.get(i).head.myEdges.contains(edges.get(i))) {
				System.out.println("This "+i+" edge's head does not include it in its edge list.");
			}
			if (!edges.get(i).tail.myEdges.contains(edges.get(i))) {
				System.out.println("This "+i+" edge's tail does not include it in its edge list.");
			}
			if (!nodes.contains(edges.get(i).head)) {
				System.out.println("This "+i+" edge's head doesn't appear on the central node list for the graph.");
			}
			if (!nodes.contains(edges.get(i).tail)) {
				System.out.println("This "+i+" edge's tail doesn't appear on the central node list for the graph.");
			}
		}

		// For every node on the central list
		for (int i=0; i<nodes.size(); i++) {
			for (int j=0; j<nodes.get(i).myEdges.size(); j++) {
				if ((!nodes.get(i).myEdges.get(j).head.equals(nodes.get(i))) && (!nodes.get(i).myEdges.get(j).tail.equals(nodes.get(i)))) {
					System.out.println("The "+j+" edge on the "+i+" node's edge list doesn't specify the node as either head or tail.");
				}
				if (!edges.contains(nodes.get(i).myEdges.get(j))) {
					System.out.println("The "+j+" edge on the "+i+" node's edge list doesn't appear in the central edge list for the graph.");
				}
			}
		}
	}

	/** Accessor for edges */
	public Edge getEdge(int i) {
		return edges.get(i);
	}

	/** Accessor for specific edge */
	public Edge getEdgeRef(Node head, Node tail) {
		return head.edgeTo(tail);
	}

	/** Accessor for nodes */
	public Node getNode(int i) {
		return nodes.get(i);
	}

	/** Accessor for number of edges */
	public int numEdges() {
		return edges.size();
	}

	/** Accessor for number of nodes */
	public int numNodes() {
		return nodes.size();
	}

	/** Return nodes not on a given list */
	public LinkedList<Node> otherNodes(LinkedList<Node> group) {
		// A hash set of nodes that are not on a given list
		LinkedList<Node> otherNodes = new LinkedList<Node>();
		// Loop through the list of nodes to check which nodes are not on the given hash set
		for (int i=0; i<nodes.size(); i++) {
			if (!group.contains(nodes.get(i))) {
				otherNodes.add(nodes.get(i));
			}
		}
		return otherNodes;
	}

	/** Print a representation of the graph */
	public void print() {
		// For every node in the central node list, print its neighbors
		for (int i=0; i<nodes.size(); i++) {
			System.out.println("Node: "+nodes.get(i).getData());
			System.out.print(" Neighbors: ");
			for (int j=0; j<nodes.get(i).getNeighbors().size(); j++) {
				System.out.print(nodes.get(i).getNeighbors().get(j).getData()+" ");
			}
			System.out.println("");
		}

		// For every edge in the central edge list, print how it connects nodes
		for (int i=0; i<edges.size(); i++) {
			System.out.println("Edge: "+edges.get(i).getData());
			System.out.println(" Connects "+edges.get(i).head.getData()+" to "+edges.get(i).tail.getData());
		}
	}

	/** Remove an edge */
	public void removeEdge(Edge edge) {
		boolean remove = false;
		// Loop through the central edge list to find the same edge to prevent the program from crashing
		// when the user passes in an edge that doesn't belong to this graph
		for (int i=0; i<edges.size(); i++) {
			if (edges.get(i).equals(edge)) {
				edges.remove(i);
				remove = true;
			}
		}
		if (!remove) {
			// Which means the user passes in an edge that doesn't belong to this graph
			System.out.println("This edge doesn't belong to this graph!");
		}
	}

	/** Remove an edge knowing its head and tail */
	public void removeEdge(Node head, Node tail) {
		boolean remove = false;
		// Loop through the central edge list to find the edge that has the same head and tail to prevent the program from crashing
		// when the user passes in a head or a tail that doesn't belong to this graph
		for (int i=0; i<edges.size(); i++) {
			if ((head.equals(edges.get(i).head)) && (tail.equals(edges.get(i).tail))) {
				edges.remove(i);
				remove = true;
			}
		}
		if (!remove) {
			// Which means the user passes in a head or a tail that represents an edge that doesn't belong to this graph
			System.out.println("This edge doesn't belong to this graph!");
		}
	}

	/** Remove a node */
	public void removeNode(Node node) {
		boolean remove = false;
		// Remove all edges linked to this node
		// Loop through the central node list to find the same node to prevent the program from crashing
		// when the user passes in a node that doesn't belong to this graph
		for (int i=0; i<nodes.size(); i++) {
			if (nodes.get(i).equals(node)) {
				for (int j=0; j<nodes.get(i).myEdges.size(); j++) {
					edges.remove(nodes.get(i).myEdges.get(j));
				}
			}
		}
		// Remove this node
		for (int i=0; i<nodes.size(); i++) {
			if (nodes.get(i).equals(node)) {
				nodes.remove(i);
				remove = true;
			}
		}
		if (!remove) {
			// Which means the user passes in a node that doesn't belong to this graph
			System.out.println("This node doesn't belong to this graph!");
		}		
	}

	/** A method to reset the graph to an empty graph */
	public void removeAll() {
		edges = new LinkedList<Edge>();
		nodes = new LinkedList<Node>();
	}
	
	/** A method to obtain the nodes that are end-points to a given list of edges */
	public LinkedList<Node> endpoints(LinkedList<Edge> givenEdges) {
		LinkedList<Node> endpoints = new LinkedList<Node>();
		for (int i=0; i<givenEdges.size(); i++) {
			for (int j=0; j<edges.size(); j++) {
				if (givenEdges.get(i).equals(edges.get(j))) {
					endpoints.add(edges.get(j).getHead());
					endpoints.add(edges.get(j).getTail());
				}
			}
		}
		return endpoints;
	}

	/** 
	 * Get unvisited neighbor 
	 * 
	 * @param node The node neighbors of which the user wants to find
	 * @param vistied The list of booleans that show whether the corresponding nodes have been visited or not
	 * @return An unvisited neighbor of the given node, or null if all the neighbors have been visited or the node has no neighbors
	 */
	public Node getUnvisitedOutChild(Node node, boolean[] visited) {
		int index = node.getIndex();
		LinkedList<Node> neighbors = nodes.get(index).outNeighbors();
		for (int i=0; i<neighbors.size(); i++) {
			if (!visited[neighbors.get(i).getIndex()]) {
				return neighbors.get(i);
			}
		}
		return null;
	}

	/**
	 * A method that performs breadth-first traversal of the graph,
	 * using queue data structure - FIFO
	 *
	 * @param start The node to start traversing from
	 * @return The queue of nodes that have been traversed in order
	 */
	public Queue<Node> BFT(Node start) {
		// Mark all the nodes as not visited 
		// (by default set as false)
		boolean visited[] = new boolean[nodes.size()];
		for (int i=0; i<visited.length; i++) {
			visited[i] = false;
		}

		// Create a queue for BFT
		Queue<Node> queue = new LinkedList<Node>();

		// Create a queue to save the traversed path
		Queue<Node> path = new LinkedList<Node>();

		// Mark the current node as visited and enqueue it
		visited[start.getIndex()] = true;
		queue.add(start);

		while (!queue.isEmpty()) {
			// Dequeue a node from queue
			Node node = queue.remove();
			path.add(node);
			Node child = null;
			// Get all the unvisited children of this node
			while((child = getUnvisitedOutChild(node, visited)) != null) {
				visited[child.getIndex()] = true;
				queue.add(child);
			}
		}

		// Clear visited property of nodes
		for (int i=0; i<visited.length; i++) {
			visited[i] = false;
		}

		return path;
	}

	/** 
	 * A method that performs depth-first traversal of the graph,
	 * using stack data structure - LIFO
	 *
	 * @param start The node to start traversing from
	 * @return The stack of nodes that have been traversed in reversed order (for printing purpose)
	 */
	public Stack<Node> DFT(Node start) {
		// Mark all the nodes as not visited 
		// (by default set as false)
		boolean visited[] = new boolean[nodes.size()];
		for (int i=0; i<visited.length; i++) {
			visited[i] = false;
		}

		// Create a stack for DFT
		Stack<Node> s = new Stack<Node>();
		// Create a stack to save the path
		Stack<Node> path = new Stack<Node>();

		// Mark the current node as visited and enqueue it
		visited[start.getIndex()] = true;
		s.push(start);

		while (!s.isEmpty()) {
			// Look at the top of the stack 
			Node node = s.peek();
			Node child = getUnvisitedOutChild(node, visited);	
			// If this node has unvisited child, visit its child
			if (child != null) {
				visited[child.getIndex()] = true;
				s.push(child);
			} else { 
				// If this node has no more unvisited child, remove this node
				// which means going back to this node's parent
				s.pop();
				path.push(node);
			}
		}

		// Clear visited property of nodes
		for (int i=0; i<visited.length; i++) {
			visited[i] = false;
		}

		return path;
	}	

	/**
	 * A method that uses the Dijkstra's Algorithm to find the shortest distance between one node and all other nodes in the graph
	 * 
	 * @param start The starting node to find the shortest paths to all other nodes
	 * @param graph The graph that contains all the nodes
	 */
	public <S,T extends Number> void shortestPathAlgorithm(Node start, Graph<V,E> graph) {
		// Default all the minimum distances with the biggest number possible
		for (Node node: nodes) {
			node.min = Integer.MAX_VALUE;
			node.previous = null;
		}

		// Distance from the start node to itself is zero
		start.min = 0;
		DistanceComparator comparator = new DistanceComparator();
		// This priority queue helps sort the queue in terms of distance to the node in ascending order
		PriorityQueue<Node> queue = new PriorityQueue<Node>(nodes.size(), comparator);

		// Add all the nodes to the queue
		for (int i=0; i<nodes.size(); i++) {
			queue.add(nodes.get(i));
		}		

		while(!queue.isEmpty()) {
			// Remove the node with the shortest distance from the queue
			Node currentPos = queue.poll();

			// Visit each outgoing edge from the current node
			for (Edge e: currentPos.outEdges()) {
				Node currentTail = e.tail;
				int weight = ((Number) e.getData()).intValue();
				int tempDistance = currentPos.min + weight;
				if (tempDistance < currentTail.min) {
					queue.remove(currentTail); // Remove to re-add it in the queue with the new distance
					currentTail.min = tempDistance;
					currentTail.previous = currentPos;
					queue.add(currentTail);
				}	
			}
		}
	}

	/**
	 * A method that prints out the shortest distance between one node and another node in the graph
	 * 
	 * @param start The starting node to find the shortest path
	 * @param end The destination 
	 * @param graph The graph that contains all the nodes
	 */
	public <S,T extends Number> void shortestPath(Node start, Node end, Graph<V,E> graph) {
		shortestPathAlgorithm(start, graph);
		if (end.getPrev() == null && !end.equals(start)) {
			System.out.println("No way to go from "+start.getData()+" to "+end.getData());
		} else {
			Stack<Node> printStack = new Stack<Node>();
			end.printPath(start, printStack);
		}
	}

	/**
	 * A method that computes the shortest distance between one node and all other nodes in the graph
	 * 
	 * @param start The starting node to find the shortest paths to all other nodes
	 * @param graph The graph that contains all the nodes
	 */
	public <S,T extends Number> void shortestPath(Node start, Graph<V,E> graph) {
		for (Node n: nodes) {
			shortestPath(start, n, graph);
		}
	}

	/** Utility method for printing the node's data */
	public void printNode(Node node) {
		System.out.print(node.data+" ");
	}

	// Nested Classes
	/** A class to create a comparator between 2 nodes in terms of minimum distance */
	public class DistanceComparator implements Comparator<Node> {
		@Override
		public int compare(Node x, Node y) {
			if (x.min < y.min) {
				return -1;
			}
			if (x.min > y.min) {
				return 1;
			}
			return 0;
		}
	}

	/** A class to implement edges */
	public class Edge{
		/** The data */
		private E data;
		/** The head node */
		private Node head;
		/** The tail node */
		private Node tail;

		/** Constructor for a new edge */
		public Edge (E data, Node head, Node tail) {
			this.data = data;
			this.head = head;
			this.tail = tail;
		}

		/**
		 * A method to override .equals to compare two edges;
		 * two edges are equal if they connect the same end-points 
		 * regardless of the data they carry;
		 * as this class implements directed graphs, head must be equal to head and tail to tail
		 * 
		 * @param o The object that is to compared to this edge
		 * @return T/F if that object is equal to this edge
		 */
		@Override
		public boolean equals(Object o) {
			if (o == this) return true;
			if (!(o instanceof Graph.Edge)) {
				return false;
			}
			@SuppressWarnings("unchecked")
			Edge object = (Edge) o;
			return object.head.equals(head) &&
					object.tail.equals(tail);
		}
		
		// Override hashCode()
		//Idea from effective Java: Item 9
		@Override
		public int hashCode() {
			int result = 17;
			result = 31 * result + head.hashCode();
			result = 31 * result + tail.hashCode();
			return result;
		}

		/** Accessor for data of the edge */
		public E getData() {
			return this.data;
		}

		/** Accessor for end-point #1 */
		public Node getHead() {
			return this.head;
		}

		/** Accessor for end-point #2 */
		public Node getTail() {
			return this.tail;
		}

		/** Accessor for opposite node */
		public Node oppositeTo(Node node) {
			if (node.equals(head)) {
				return tail;
			} else if (node.equals(tail)) {
				return head;
			} else {
				return null;
			}			
		}

		/** Manipulator for data */
		public void setData(E data) {
			this.data = data;
		}
	}

	/** A class to implement nodes */
	public class Node {
		/** The data */
		private V data;
		/** The edge list of this node */
		private LinkedList<Edge> myEdges = new LinkedList<Edge>();
		/** Minimum distance to a node */
		private int min = Integer.MAX_VALUE;
		/** Previous node that needs to pass by to reach this node in traversals and Dijkstra's Algorithm */
		private Node previous;

		/** Constructor for a disconnected node */
		public Node (V data) {
			this.data = data;
		}

		/** Add an edge to the edge list */
		protected void addEdgeRef(Edge edge) {
			myEdges.add(edge);
		}

		/** Return the edge to a specified node, or null if there is none */
		public Edge edgeTo(Node neighbor) {
			Edge edge = null;
			for (int i=0; i<edges.size(); i++) {
				if ((edges.get(i).head.equals(this)) && (edges.get(i).tail.equals(neighbor))) {
					edge = edges.get(i);
				}
			}
			return edge;
		}

		/** Return a list of outgoing edges from a given nodes */
		public LinkedList<Edge> outEdges() {
			LinkedList<Edge> outEdges = new LinkedList<Edge>();
			for (int i=0; i<myEdges.size(); i++) {
				if (this.equals(myEdges.get(i).head)) {
					outEdges.add(myEdges.get(i));
				}
			}
			return outEdges;
		}

		/** Accessor for data of the node */
		public V getData() {
			return this.data;
		}
		
		/** Accessor for data of the node */
		public LinkedList<Edge> getMyEdges() {
			return this.myEdges;
		}

		/** Accessor for minimum distance of the node */
		public int getMin() {
			return this.min;
		}

		/** Accessor for previous node of this node */
		public Node getPrev() {
			return this.previous;
		}

		/** Return a list of neighbors */
		public LinkedList<Node> getNeighbors() {
			LinkedList<Node> neighbors = new LinkedList<Node>();
			for (int i=0; i<nodes.size(); i++) {
				if (nodes.get(i).isNeighbor(this)) {
					neighbors.add(nodes.get(i));
				}
			}
			return neighbors;
		}

		/** Return a list of neighbors that can be reached directly by outgoing edges */
		public LinkedList<Node> outNeighbors() {
			LinkedList<Node> outNeighbors = new LinkedList<Node>();
			for (int i=0; i<nodes.size(); i++) {
				if (nodes.get(i).isNeighbor(this)) {
					if(this.edgeTo(nodes.get(i)) != null) {
						outNeighbors.add(nodes.get(i));
					}
				}
			}
			return outNeighbors;
		}

		/** Return true the given node is a neighbor of this node */
		public boolean isNeighbor(Node node) {
			boolean isNeighbor = false;
			for (int i=0; i<edges.size(); i++) {
				if (edges.get(i).head.equals(this)) {
					if (node.equals(edges.get(i).tail)) {
						isNeighbor = true;
					}
				} else if (edges.get(i).tail.equals(this)) {
					if (node.equals(edges.get(i).head)) {
						isNeighbor = true;
					}
				}
			}
			return isNeighbor;
		}

		/** Remove an edge from the edge list */
		protected void removeEdgeRef(Edge edge) {
			myEdges.remove(edge);
		}

		/** Manipulator for data */
		public void setData(V data) {
			this.data = data;
		}

		/** Method to get the index in the central node list of a particular node */
		public int getIndex() {
			int index = -1;
			for (int i=0; i<nodes.size(); i++) {
				if (nodes.get(i).equals(this)) {
					index = i;
				}
			}
			return index;
		}

		/** Utility method to print the shortest paths from a given node */
		public void printPath(Node start, Stack<Node> printStack) {
			Node destination = this;
			Node n = this;
			System.out.println("***Shortest distance from "+start.getData()+" to "+n.getData()+" is "+n.getMin());
			System.out.println("\tPath from "+start.getData()+" to "+n.getData()+":");
			System.out.print("\t");
			while (n.getPrev() != null) {
				printStack.push(n.getPrev());
				n = n.getPrev();
			}
			while (!printStack.isEmpty()) {
				System.out.print(printStack.pop().getData()+" ---> ");			
			}
			System.out.print(destination.getData());
			System.out.println("");
		}
	}
} // end of Graph
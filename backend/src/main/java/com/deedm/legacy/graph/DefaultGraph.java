/**
 * 
 */
package com.deedm.legacy.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.deedm.legacy.setrelfun.Matrix;
import com.deedm.legacy.setrelfun.Relation;
import com.deedm.legacy.util.Stack;

/**
 * @author user
 *
 */
public class DefaultGraph extends AbstractGraph {
	private static List<TravelStepRecord> travelStepRecords = null;

	String id = null;
	
	public DefaultGraph(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isSimpleGraph() {
		for (GraphEdge edge : edges) {
			if (edge.getStartNode() == edge.getEndNode()) return false;
			for (GraphEdge otherEdge : edges) {
				if (edge != otherEdge) {
					if (edge.isDirected() || otherEdge.isDirected()) {
						if (edge.getStartNode().equals(otherEdge.getStartNode()) && edge.getEndNode().equals(otherEdge.getEndNode())) return false;
					} else {
						if (edge.getStartNode().equals(otherEdge.getStartNode()) && edge.getEndNode().equals(otherEdge.getEndNode())) return false;
						if (edge.getStartNode().equals(otherEdge.getEndNode()) && edge.getEndNode().equals(otherEdge.getStartNode())) return false;
					}
				}
			}
		}
		return true;
	}
	
	public int getDegree(GraphNode node) {
		int result = 0;
		for (GraphEdge edge : edges) {
			if (edge.getStartNode().equals(node)) result++;
			if (edge.getEndNode().equals(node)) result++;
		}
		return result;
	}
	
	public int getInDegree(GraphNode node) {
		int result = 0;
		for (GraphEdge edge : edges) {
			if (edge.getEndNode().equals(node)) result++;
		}
		return result;
	}
	
	public int getOutDegree(GraphNode node) {
		int result = 0;
		for (GraphEdge edge : edges) {
			if (edge.getStartNode().equals(node)) result++;
		}
		return result;
	}
	
	public Matrix getIncidenceMatrix() {
		int rows = nodes.size();
		int cols = edges.size();
		Matrix result = new Matrix(rows, cols);
		for (int j = 0; j < edges.size(); j++) {
			GraphEdge edge = edges.get(j);
			GraphNode startNode = edge.getStartNode();
			GraphNode endNode = edge.getEndNode();
			int row = nodes.indexOf(startNode);
			if (row >= 0 && row < nodes.size()) result.set(row, j, 1);
			int value = 1;
			if (edge.isDirected()) value = -1;
			row = nodes.indexOf(endNode);
			if (row >= 0 && row < nodes.size()) result.set(row, j, value);
		}
		return result;
	}
	
	public Matrix getAdjacencyMatrix() {
		int rows = nodes.size();
		int cols = rows;

		Matrix result = new Matrix(rows, cols);
		for (int j = 0; j < edges.size(); j++) {
			GraphEdge edge = edges.get(j);
			GraphNode startNode = edge.getStartNode();
			GraphNode endNode = edge.getEndNode();
			int row = nodes.indexOf(startNode);
			int col = nodes.indexOf(endNode);
			if (row < 0 || row >= nodes.size()) continue;
			if (col < 0 || col >= nodes.size()) continue;
			int value = result.get(row, col)+1;
			result.set(row, col, value);
			if (!edge.isDirected()) {
				value = result.get(col, row)+1;
				result.set(col, row, value);
			}
		}
		return result;
	}
	
	public Matrix[] calculatePathNumberByUsingAdjacencyMatrix() {
		int n = nodes.size();
		Matrix matrix = this.getAdjacencyMatrix();
		Matrix powerMatrix = new Matrix(matrix);
		
		Matrix[] result = new Matrix[n];
		result[0] = new Matrix(powerMatrix);
		for (int k = 2; k <= n; k++) {
			powerMatrix = powerMatrix.product(matrix);
			result[k-1] = new Matrix(powerMatrix);
		}
		return result;
	}

	
	public static class TravelStepRecord {
		int step = 0;
		List<GraphNode> visitedNodeList = null;
		List<GraphNode> auxNodeList = null;
		
		public TravelStepRecord(int step, List<GraphNode> visitedNodeList, List<GraphNode> auxNodeList) {
			this.step = step;
			this.visitedNodeList = new ArrayList<GraphNode>();
			this.visitedNodeList.addAll(visitedNodeList);
			this.auxNodeList = auxNodeList;
		}

		public int getStep() {
			return step;
		}

		public List<GraphNode> getVisitedNodeList() {
			return visitedNodeList;
		}

		public List<GraphNode> getAuxNodeList() {
			return auxNodeList;
		}
	}
	
	public static List<TravelStepRecord> getTravelStepRecords() {
		return travelStepRecords;
	}
	
	private TravelStepRecord createDFSStepRecord(int step, List<GraphNode> visitedNodeList, Stack<Integer> stack) {
		List<Integer> stackData = stack.getAllData();
		List<GraphNode> auxNodeList = new ArrayList<GraphNode>();
		for (int i = 0; i < stackData.size(); i++) {
			int index = stackData.get(i);
			auxNodeList.add(nodes.get(index));
		}
		return new TravelStepRecord(step, visitedNodeList, auxNodeList);
	}
	
	public List<GraphNode> getDFSNodeList() {
		List<GraphNode> result = new ArrayList<GraphNode>();
		Matrix amatrix = getAdjacencyMatrix();
		boolean[] visited = new boolean[nodes.size()];
		Stack<Integer> stack = new Stack<Integer>();
		travelStepRecords = new ArrayList<TravelStepRecord>();
		
		for (int i = 0; i < visited.length; i++) visited[i] = false;
		int startIndex = 0;
		int step = 1;
		
		while (startIndex < nodes.size()) {
			// Visited the selected node, and push its index to the stack 
			GraphNode startNode = nodes.get(startIndex);
			visited[startIndex] = true;
			result.add(startNode);
			stack.push(new Integer(startIndex));
			startIndex = startIndex+1;
			
			TravelStepRecord record = createDFSStepRecord(step, result, stack);
			travelStepRecords.add(record);
			step = step + 1;
			
			while (!stack.isEmpty()) {
				// Get the index of node at the top of the stack
				int currentNodeIndex = stack.getTop().intValue();
				// Find the index j of a node which is the adjacency node (amatrix.getCurrentNodeIndex, j) > 0) of the node at the top of the stack and 
				// has not been visited (visited[j] == false) 
				int nextNodeIndex = -1;
				for (int j = 0; j < nodes.size(); j++) {
					if (amatrix.get(currentNodeIndex, j) > 0 && visited[j] == false) {
						nextNodeIndex = j;
						break;
					}
				}
				if (nextNodeIndex >= 0) {
					// If has such node, the index of such node will be nextNodeIndex and it is greater than 0
					visited[nextNodeIndex] = true;
					GraphNode nextNode = nodes.get(nextNodeIndex);
					result.add(nextNode);
					stack.push(new Integer(nextNodeIndex));
				} else {
					stack.pop();
				}
				record = createDFSStepRecord(step, result, stack);
				travelStepRecords.add(record);
				step = step + 1;
			}
			
			// Select the next non-visited node to visit
			while (startIndex < nodes.size()) {
				if (visited[startIndex] == false) break;
				startIndex++;
			}
		}
		
		return result;
	}

	private TravelStepRecord createBFSStepRecord(int step, List<GraphNode> visitedNodeList, LinkedList<Integer> queue) {
		List<GraphNode> auxNodeList = new ArrayList<GraphNode>();
		for (int i = 0; i < queue.size(); i++) {
			int index = queue.get(i);
			auxNodeList.add(nodes.get(index));
		}
		return new TravelStepRecord(step, visitedNodeList, auxNodeList);
	}
	
	public List<GraphNode> getBFSNodeList() {
		List<GraphNode> result = new ArrayList<GraphNode>();
		Matrix amatrix = getAdjacencyMatrix();
		boolean[] visited = new boolean[nodes.size()];
		LinkedList<Integer> queue = new LinkedList<Integer>();
		travelStepRecords = new ArrayList<TravelStepRecord>();

		for (int i = 0; i < visited.length; i++) visited[i] = false;
		int startIndex = 0;
		int step = 1;
		while (startIndex < nodes.size()) {
			// Visited the selected node, and add its index to the tail of the queue 
			GraphNode startNode = nodes.get(startIndex);
			visited[startIndex] = true;
			result.add(startNode);
			queue.addLast(new Integer(startIndex));
			startIndex = startIndex+1;

			TravelStepRecord record = createBFSStepRecord(step, result, queue);
			travelStepRecords.add(record);
			step = step + 1;
			
			while (!queue.isEmpty()) {
				// Get the index of node at the head of the queue
				int currentNodeIndex = queue.getFirst().intValue();
				queue.removeFirst();
				// Add all adjacency nodes which has not been visited to result list and the queue   
				for (int j = 0; j < nodes.size(); j++) {
					if (amatrix.get(currentNodeIndex, j) > 0 && visited[j] == false) {
						GraphNode node = nodes.get(j);
						visited[j] = true;
						result.add(node);
						queue.addLast(new Integer(j));
					}
				}

				record = createBFSStepRecord(step, result, queue);
				travelStepRecords.add(record);
				step = step + 1;
			}
			
			// Select the next non-visited node to visit
			while (startIndex < nodes.size()) {
				if (visited[startIndex] == false) break;
				startIndex++;
			}
		}
		
		return result;
	}
	
	public static class GraphComponent {
		public static final int NORMAL_COMPONENT = 0;
		public static final int NEW_COMPONENT = 1;
		public static final int COMBINED_COMPONENT = 2;
		public static final int CIRCLED_COMPONENT = 3;
		
		protected int type = NEW_COMPONENT;
		protected List<GraphNode> subNodeList = null;

		public GraphComponent() {
			subNodeList = new ArrayList<GraphNode>();
		}

		public int changeTypeTo(int type) {
			int oldType = this.type;
			this.type = type;
			return oldType;
		}
		
		public int getType() {
			return type;
		}
		
		public boolean add(GraphNode node) {
			if (subNodeList.contains(node)) return false;
			return subNodeList.add(node);
		}
		
		public boolean contains(GraphNode node) {
			return subNodeList.contains(node);
		}
		
		public List<GraphNode> getNodeList() {
			return subNodeList;
		}
	}
	
	/**
	 * Add a edge (newEdge) to the componentList:
	 * <p>(1) If the two end nodes of the edge are not in any component in the componentList, then we create a new component which only contains these two 
	 *     end nodes, and return the new component, which type is set to be NEW_COMPONENT.
	 * <p>(2) If there is an end node of the edge in a component in the componentList, and another end node is not in any component in componentList, then
	 *     the other end node will be added to this component, and return this component, which type is not changed. 
	 * <p>(3) If there is an end node of the edge in a component in the componentList, and another end node is in another component in componentList, then
	 *     these two component will be combined to a component, and return the combined component, which type is changed to be COMBINED_COMPONENT.  
	 * <p>(4) If there is a component contains two end nodes of the edge, then we find a circle in this component, return this component, which type is 
	 *     changed to be CIRCLED_COMPONENT.   
	 */
	protected static GraphComponent addEdgeToComponentList(List<GraphComponent> componentList, GraphEdge newEdge) {
		GraphComponent result = null;
		
		GraphNode startNode = newEdge.getStartNode();
		GraphNode endNode = newEdge.getEndNode();
		GraphComponent startComponent = null;
		GraphComponent endComponent = null;
		for (GraphComponent component : componentList) {
			if (component.contains(startNode)) startComponent = component;
			if (component.contains(endNode)) endComponent = component;
		}
		if (startComponent == null && endComponent == null) {
			result = new GraphComponent();
			result.add(startNode);
			if (!startNode.equals(endNode)) result.add(endNode);
			componentList.add(result);
		} else if (startComponent != null && endComponent == null) {
			result = startComponent;
			if (!startNode.equals(endNode)) result.add(endNode);
		} else if (startComponent == null && endComponent != null) {
			result = endComponent;
			if (!startNode.equals(endNode)) result.add(startNode);
		} else if (startComponent != endComponent) {
			result = startComponent;
			List<GraphNode> endComponentNodeList = endComponent.getNodeList();
			for (GraphNode node : endComponentNodeList) result.add(node);
			result.changeTypeTo(GraphComponent.COMBINED_COMPONENT);
			componentList.remove(endComponent);
		} else {
			result = startComponent;
			result.changeTypeTo(GraphComponent.CIRCLED_COMPONENT);
		}
		return result; 
	}
	
	public DefaultGraph getUndirectSpanningForest () {
		List<GraphComponent> componentList = new ArrayList<GraphComponent>();
		List<GraphEdge> selectEdge = new ArrayList<GraphEdge>();
		for (GraphEdge edge : edges) {
			if (edge.getStartNode().equals(edge.getEndNode())) continue;
			GraphComponent component = addEdgeToComponentList(componentList, edge);
			if (component.getType() != GraphComponent.CIRCLED_COMPONENT) {
				selectEdge.add(edge);
			} else component.changeTypeTo(GraphComponent.NORMAL_COMPONENT);
		}
		
		DefaultGraph result = new DefaultGraph("USFof" + id);
		result.setNodes(nodes);
		result.setEdges(selectEdge);
		return result;
	}
	
	public boolean isUndirectTree() {
		List<GraphComponent> componentList = new ArrayList<GraphComponent>();
		for (GraphEdge edge : edges) {
			if (edge.getStartNode().equals(edge.getEndNode())) return false;
			GraphComponent component = addEdgeToComponentList(componentList, edge);
			if (component.getType() == GraphComponent.CIRCLED_COMPONENT) return false; 
		}
		if (componentList.size() > 1) return false;
		return true;
	}
}

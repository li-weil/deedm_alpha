package com.deedm.legacy.graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * The abstract class of the graph. It implements some common methods for graph. The class select the 
 * 		data structures for storing a graph, and all implementations depend on these data structures,
 * @author Zhou Xiaocong
 * @since 2012/12/26
 * @version 1.1
 * @update 2013/03/29 Zhou Xiaocong
 * 		Add methods getAllNodes(), getAllEdegs() 
 * @update 2013/06/12 Zhou Xiaocong
 * 		Add methods setNodes(), setEdges(), simplyWriteToDotFile(), getLegalToken()
 * @update 2013/09/13 Zhou Xiaocong
 *      (1) Add method boolean hasEdges(GraphNode, GraphNode);
 * 		(2) Correct many bugs in the methods which can not correctly deal with the case when the fields nodes or edges is null!
 *      (3) Add method List<GraphNode> adjacentNodes(GraphNode); 
 * 
 */
public abstract class AbstractGraph {
	protected List<GraphNode> nodes = null;
	protected List<GraphEdge> edges = null;
	
	public abstract String getId();
	
	/**
	 * Add a node to the graph. if the node has been in the graph yet, the method dose not add it to the graph.
	 * That is, if there is a node(graphNode) in the graph such that graphNode.equals(node) == true, the node 
	 * has been in the node.  
	 */
	public void addNode(GraphNode node) {
		if (nodes == null) nodes = new ArrayList<GraphNode>(10);
		if (nodes.contains(node)) return;
		nodes.add(node);
	}

	/**
	 * Add an edge to the graph. The method do not check if the edge has been in the graph.
	 */
	public void addEdge(GraphEdge edge) {
		if (edges == null) edges = new ArrayList<GraphEdge>(10);
		edges.add(edge);
	}
	
	/**
	 * Remove an edge from the graph. Use the method equals() to check two nodes are identity or not.
	 */
	public void removeNode(GraphNode node) {
		if (nodes == null) return;
		nodes.remove(node);
	}

	/**
	 * Remove an edge from the graph. Use the method equals() to check two edges are identity or not.
	 */
	public void removeEdge(GraphEdge edge) {
		if (edges == null) return;
		edges.remove(edge);
	}
	
	/** 
	 * Check if the node is in the graph. Use the method equals() to check two nodes are identity or not.
	 */
	public boolean hasNode(GraphNode node) {
		if (nodes == null) return false;
		if (nodes.contains(node)) return true;
		else return false;
	}

	/** 
	 * Check if the edge is in the graph. Use the method equals() to check two nodes are identity or not.
	 */
	public boolean hasEdge(GraphEdge edge) {
		if (edges == null) return false;
		if (edges.contains(edge)) return true;
		else return false;
	}
	
	/**
	 * Check if the edge between two nodes is in the graph
	 */
	public boolean hasEdge(GraphNode from, GraphNode to) {
		if (edges == null) return false;
		for (GraphEdge edge : edges) {
			if (edge.getStartNode().equals(from) && edge.getEndNode().equals(to)) return true;
		}
		return false;
	}
	
	/**
	 * Find a node by its id
	 */
	public GraphNode findById(String id) {
		if (nodes == null) return null;
		for (GraphNode node: nodes) {
			if (id.equals(node.getId())) return node;
		}
		return null;
	}

	/**
	 * Find a node by its label
	 */
	public GraphNode findByLabel(String label) {
		if (nodes == null) return null;
		for (GraphNode node: nodes) {
			if (label.equals(node.getLabel())) return node;
		}
		return null;
	}
	
	/**
	 * @param node: a fix node may in the graph
	 * @return All nodes that are adjacent to the node, i.e. there is an edge from it to the given node. 
	 */
	public List<GraphNode> adjacentToNode(GraphNode node) {
		ArrayList<GraphNode> result = new ArrayList<GraphNode>();
		if (edges == null) return result;
		for (GraphEdge edge: edges) {
			if (edge.getEndNode().equals(node)) result.add(edge.getStartNode());
		}
		return result;
	}
	
	/**
	 * @param node: a fix node may in the graph
	 * @return All nodes that are adjacent from the node, i.e. there is an edge from the given node to it. 
	 */
	public List<GraphNode> adjacentFromNode(GraphNode node) {
		ArrayList<GraphNode> result = new ArrayList<GraphNode>();
		if (edges == null) return result;
		for (GraphEdge edge: edges) {
			if (edge.getStartNode().equals(node)) result.add(edge.getEndNode());
		}
		return result;
	}
	
	/**
	 * @param node: a fix node may in the graph
	 * @return All nodes that are adjacent from the node or to the node, i.e. there is an edge from or to the given node. 
	 */
	public List<GraphNode> adjacentNodes(GraphNode node) {
		ArrayList<GraphNode> result = new ArrayList<GraphNode>();
		if (edges == null) return result;
		for (GraphEdge edge: edges) {
			if (edge.getStartNode().equals(node)) result.add(edge.getEndNode());
			if (edge.getEndNode().equals(node)) result.add(edge.getStartNode());
		}
		return result;
	}
	
	public boolean isUndirectedGraph() {
		for (GraphEdge edge : edges) 
			if (edge.isDirected()) return false;
		return true;
	}
	
	public boolean isDirectedGraph() {
		for (GraphEdge edge : edges) 
			if (!edge.isDirected()) return false;
		return true;
	}
	
	/**
	 * Print the detailed information (especially for node information) of the graph to a string. 
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Nodes:\n");
		boolean isFirstNode = true;
		for (GraphNode node: nodes) {
			if (isFirstNode) {
				buffer.append(node.toString());
				isFirstNode = false;
			}
			else buffer.append(", " + node.toString());
		}
		
		if (edges == null) return buffer.toString();
		
		for (GraphEdge edge: edges) {
			buffer.append(edge.toString()+"\n");
		}
		buffer.append("\n");
		return buffer.toString();
	}

	/**
	 * Print the detailed information (especially for node information) of the graph to a string. 
	 */
	public String toFullString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Nodes:\n");
		for (GraphNode node: nodes) {
			buffer.append(node.toFullString()+"\n");
		}
		buffer.append("\nEdges: \n");
		for (GraphEdge edge: edges) {
			buffer.append(edge.toString()+"\n");
		}
		buffer.append("\n");
		return buffer.toString();
	}
	
	/**
	 * Return all nodes of the graph
	 */
	public List<GraphNode> getNodes() {
		return nodes;
	}

	/**
	 * Set all nodes of the graph
	 */
	public void setNodes(List<GraphNode> nodes) {
		this.nodes = nodes;
	}

	/**
	 * Return all edges of the graph
	 */
	public List<GraphEdge> getEdges() {
		return edges;
	}

	/**
	 * Set all edges of the graph
	 */
	public void setEdges(List<GraphEdge> edges) {
		this.edges = edges;
	}
	
	/**
	 * Write the (directed) graph to a text file, which can be regarded as the description of the graph 
	 * in dot language, and can be used to visualized the graph use Graphviz tools.
	 * @param out : the output text file, which should be opened
	 */
	public void simplyWriteToDotFile(PrintWriter output) throws IOException {
		if (nodes == null) return;

		String id = getLegalToken(getId());
		if (isDirectedGraph()) output.println("digraph " + id + " {");
		else output.println("graph " + id + " {");
		for (GraphNode node : nodes) {
			String label = node.getImageFileName();
			if (label == null || label.trim().equals("")) label = node.getLabel();
			id = "node" + getLegalToken(node.getId());
			output.println("    " + id + "[label = \"" + label + "\"];");
		}

		if (edges != null) {
			for (GraphEdge edge : edges) {
				String label = edge.getLabel();
				GraphNode start = edge.getStartNode();
				GraphNode end = edge.getEndNode();
				String startNodeId = "node" + getLegalToken(start.getId());
				String endNodeId = "node" + getLegalToken(end.getId());

				String edgeSharp = "--";
				if (edge.isDirected()) edgeSharp = "->";
				if (label != null) {
					output.println("    " + startNodeId + edgeSharp + endNodeId + "[label = \"" + label + "\"];");
				} else {
					output.println("    " + startNodeId + edgeSharp + endNodeId + ";");
				}
			}
		}

		output.println("}");
		output.println();
		output.flush();
	}
	
	/**
	 * Get a legal identifier (in dot language) from a identifier of graph, node, or edge 
	 */
	protected String getLegalToken(String id) {
		StringBuilder token = new StringBuilder("");
		for (int index = 0; index < id.length(); index++) {
			char ch = id.charAt(index);
			if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ('0' <= ch && ch <= '9')) token.append(ch);
			else token.append('_');
		}
		return token.toString();
	}
	
}


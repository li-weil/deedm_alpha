/**
 * 
 */
package com.deedm.legacy.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author user
 *
 */
public class GraphPath {
	protected String id = null;
	protected LinkedList<GraphEdge> edges = null;
	
	public GraphPath() {
		edges = new LinkedList<GraphEdge>();
	}
	
	public GraphPath(String id) {
		edges = new LinkedList<GraphEdge>();
		this.id = id;
	}

	public void setEdges(LinkedList<GraphEdge> edges) {
		this.edges = edges;
	}
	
	public void addEdgeFirst(GraphEdge edge) {
		edges.addFirst(edge);
	}
	
	public void addEdgeLast(GraphEdge edge) {
		edges.addLast(edge);
	}

	public List<GraphEdge> getEdges() {
		return edges;
	}
	
	public List<GraphNode> getNodes() {
		List<GraphNode> result = new ArrayList<GraphNode>();
		GraphNode start = edges.get(0).getStartNode();
		result.add(start);
		for (int i = 0; i < edges.size(); i++) result.add(edges.get(i).getEndNode());
		return result;
	}
	
	public GraphEdge getStartEdge() {
		return edges.get(0);
	}
	
	public GraphEdge getEndEdge() {
		return edges.get(edges.size()-1);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(edges.get(0));
		for (int i = 1; i < edges.size(); i++) {
			GraphEdge edge = edges.get(i);
			if (edge.isDirected()) buffer.append("-->" + edge);
			else buffer.append("---" + edge);
		}
		return buffer.toString();		
	}
	
	public String toFullString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(edges.get(0).toFullString());
		for (int i = 1; i < edges.size(); i++) {
			GraphEdge edge = edges.get(i);
			if (edge.isDirected()) buffer.append("-->" + edge.toFullString());
			else buffer.append("---" + edge.toFullString());
		}
		return buffer.toString();		
	}
}

/**
 * 
 */
package com.deedm.legacy.graph;

import java.util.LinkedList;

/**
 * @author user
 *
 */
public class WeightedGraphPath extends GraphPath {
	protected double weight = 0;
	
	public WeightedGraphPath() {
	}

	public WeightedGraphPath(String id) {
		super(id);
	}

	public void setEdges(LinkedList<GraphEdge> edges) {
		this.edges = edges;
		weight = 0;
		for (GraphEdge edge : edges) {
			WeightedGraphEdge wedge = (WeightedGraphEdge)edge;
			weight = weight + wedge.weight;
		}
	}
	
	public boolean addEdge(GraphEdge edge) {
		boolean success = edges.add(edge);
		if (success == false) return false;
		WeightedGraphEdge wedge = (WeightedGraphEdge)edge;
		weight = weight + wedge.weight;
		return success;
	}

	public void addEdgeFirst(GraphEdge edge) {
		edges.addFirst(edge);
		WeightedGraphEdge wedge = (WeightedGraphEdge)edge;
		weight = weight + wedge.weight;
	}
	
	public void addEdgeLast(GraphEdge edge) {
		edges.addLast(edge);
		WeightedGraphEdge wedge = (WeightedGraphEdge)edge;
		weight = weight + wedge.weight;
	}
	
	public double getWeight() {
		return weight;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(weight + " : ");
		buffer.append(edges.get(0));
		for (int i = 1; i < edges.size(); i++) {
			GraphEdge edge = edges.get(i);
			if (edge.isDirected()) buffer.append("-->" + edge);
			else buffer.append("---" + edge);
		}
		return buffer.toString();		
	}
	
	public String toLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(weight + " : ");
		buffer.append(edges.get(0).toLaTeXString());
		for (int i = 1; i < edges.size(); i++) {
			GraphEdge edge = edges.get(i);
			if (edge.isDirected()) buffer.append("\\longrightarrow " + edge.toLaTeXString());
			else buffer.append("---" + edge.toLaTeXString());
		}
		return buffer.toString();		
	}

	public String toLaTeXString(GraphNode startNode) {
		StringBuffer buffer = new StringBuffer();
		WeightedGraphEdge edge = (WeightedGraphEdge)edges.get(0);
		GraphNode lastNode = startNode;
		GraphNode otherNode = null;
		if (edge.getStartNode().equals(lastNode)) otherNode = edge.getEndNode();
		else otherNode = edge.getStartNode();
		
		buffer.append(weight + " : " + lastNode.getLabel() + "\\xrightarrow{" + edge.getLabel() + "}" + otherNode.getLabel());
		lastNode = otherNode;
		for (int i = 1; i < edges.size(); i++) {
			edge = (WeightedGraphEdge)edges.get(i);
			if (edge.getStartNode().equals(lastNode)) otherNode = edge.getEndNode();
			else otherNode = edge.getStartNode();
			buffer.append("\\xrightarrow{" + edge.getLabel() + "}" + otherNode.getLabel());
			lastNode = otherNode;
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
		buffer.append(" : " + weight);
		return buffer.toString();		
	}
}

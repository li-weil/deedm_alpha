/**
 * 
 */
package com.deedm.legacy.graph;

/**
 * @author user
 *
 */
public class WeightedGraphEdge extends DefaultGraphEdge {
	protected double weight = 0;
	
	public WeightedGraphEdge(GraphNode start, GraphNode end, double weight) {
		super(start, end);
		this.weight = weight;
	}

	public WeightedGraphEdge(GraphNode start, GraphNode end, boolean directed, double weight) {
		super(start, end, null, directed);
		this.weight = weight;
	}
	
	public WeightedGraphEdge(GraphNode start, GraphNode end, String label, double weight) {
		super(start, end, label);
		this.weight = weight;
	}

	public WeightedGraphEdge(GraphNode start, GraphNode end, String label, boolean directed, double weight) {
		super(start, end, label, directed);
		this.weight = weight;
	}

	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	@Override
	public String getLabel() {
		if (label == null) return "[" + weight + "]";
		else return label+"["+weight+"]";
	}

	@Override
	public String getDescription() {
		StringBuffer buffer = new StringBuffer();
		if (label != null) buffer.append(label + "[" + weight + "]" + " = ");
		if (directed) buffer.append("<");
		else buffer.append("(");
		buffer.append(start.getLabel() + ", " + end.getLabel());
		if (directed) buffer.append(">");
		else buffer.append(")");
		return buffer.toString();
	}
	
	public String toLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		if (label != null) buffer.append(label + "[" + weight + "]" + " = ");
		else buffer.append("[" + weight + "]" + " = ");
		if (directed) buffer.append("\\langle ");
		else buffer.append("(");
		buffer.append(start.getLabel() + ", " + end.getLabel());
		if (directed) buffer.append("\\rangle ");
		else buffer.append(")");
		return buffer.toString();
	}
}

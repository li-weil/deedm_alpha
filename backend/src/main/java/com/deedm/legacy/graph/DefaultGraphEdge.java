package com.deedm.legacy.graph;

public class DefaultGraphEdge implements GraphEdge {
	protected GraphNode start = null;
	protected GraphNode end = null;
	protected boolean directed = false;
	protected String label = null;

	public DefaultGraphEdge(GraphNode start, GraphNode end) {
		this.start = start;
		this.end = end;
	}
	
	public DefaultGraphEdge(GraphNode start, GraphNode end, String label) {
		this.start = start;
		this.end = end;
		this.label = label;
	}
	
	public DefaultGraphEdge(GraphNode start, GraphNode end, String label, boolean directed) {
		this.start = start;
		this.end = end;
		this.label = label;
		this.directed = directed;
	}

	@Override
	public boolean isDirected() {
		return directed;
	}

	@Override
	public GraphNode getStartNode() {
		return start;
	}

	@Override
	public GraphNode getEndNode() {
		return end;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getDescription() {
		StringBuffer buffer = new StringBuffer();
		if (label != null) buffer.append(label + " = ");
		if (directed) buffer.append("<");
		else buffer.append("(");
		buffer.append(start.getLabel() + ", " + end.getLabel());
		if (directed) buffer.append(">");
		else buffer.append(")");
		return buffer.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + start.hashCode();
		result = prime * result + end.hashCode();
		if (label != null) result = prime * result + label.hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof DefaultGraphEdge)) return false;
		
		DefaultGraphEdge otherEdge = (DefaultGraphEdge)obj;
		if (label != null) {
			if (otherEdge.label == null) return false;
			if (!label.equals(otherEdge.label)) return false;
		}
		if (directed || otherEdge.directed) {
			if (!start.equals(otherEdge.start) || !end.equals(otherEdge.end)) return false;
		} else {
			if (!start.equals(otherEdge.start) && !start.equals(otherEdge.end)) return false;
			if (start.equals(otherEdge.start)) {
				if (!end.equals(otherEdge.end)) return false;
			} else if (!end.equals(otherEdge.start)) return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		if (label != null) return label;
		StringBuffer buffer = new StringBuffer();
		if (directed) buffer.append("<");
		else buffer.append("(");
		buffer.append(start.getLabel() + ", " + end.getLabel());
		if (directed) buffer.append(">");
		else buffer.append(")");
		return buffer.toString();
	}
	
	@Override
	public String toFullString() {
		return getDescription();
	}
	
	public String toLaTeXString() {
		StringBuffer buffer = new StringBuffer();
		if (label != null) buffer.append(label + " = ");
		if (directed) buffer.append("\\langle ");
		else buffer.append("(");
		buffer.append(start.getLabel() + ", " + end.getLabel());
		if (directed) buffer.append("\\rangle ");
		else buffer.append(")");
		return buffer.toString();
	}
	

}

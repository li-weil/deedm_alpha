package com.deedm.legacy.graph;

public class DefaultGraphNode implements GraphNode {
	String id = null;
	String label = null;
	String description = null;
	
	public DefaultGraphNode(String id) {
		this.id = id;
		this.label = id;
		this.description = id;
	}
	public DefaultGraphNode(String id, String label) {
		this.id = id;
		this.label = label;
		this.description = label;
	}
	public DefaultGraphNode(String id, String label, String description) {
		this.id = id;
		this.label = label;
		this.description = description;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getImageFileName() {
		return description;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id.hashCode();
		result = prime * result + label.hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof DefaultGraphNode)) return false;
		
		DefaultGraphNode otherNode = (DefaultGraphNode)obj;
		return id.equals(otherNode.id) && label.equals(otherNode.label);
	}

	@Override
	public String toString() {
		if (label.equals(id)) return id;
		else return id + "[" + label + "]";
	}
	
	@Override
	public String toFullString() {
		if (label.equals(id)) return id;
		else return id + "[" + label + "]";
	}
}

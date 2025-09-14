/**
 * 
 */
package com.deedm.legacy.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * @author user
 *
 */
public class RootedTreeNode implements GraphNode {
	String id = null;
	String label = null;
	String description = null;
	List<RootedTreeNode> childList = null;
	
	public RootedTreeNode(String id) {
		this.id = id;
		this.label = id;
		this.description = id;
	}
	public RootedTreeNode(String id, String label) {
		this.id = id;
		this.label = label;
		this.description = label;
	}
	public RootedTreeNode(String id, String label, String description) {
		this.id = id;
		this.label = label;
		this.description = description;
	}

	public List<RootedTreeNode> getChildList() {
		return childList;
	}
	
	public boolean containsChildNode(RootedTreeNode node) {
		if (childList == null) return false;
		return childList.contains(node);
	}
	
	public void setChildList(List<RootedTreeNode> childList) {
		this.childList = childList;
	}
	
	public boolean addChildNode(RootedTreeNode node) {
		if (childList == null) childList = new ArrayList<RootedTreeNode>();
		if (!childList.contains(node)) return childList.add(node);
		else return false;
	}
	
	public static RootedTreeNode createRootedTreeNodeFromDefaultGraphNode(DefaultGraphNode node) {
		RootedTreeNode treeNode = new RootedTreeNode(node.getId(), node.getLabel(), node.getImageFileName());
		return treeNode;
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

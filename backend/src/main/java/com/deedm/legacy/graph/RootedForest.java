/**
 * 
 */
package com.deedm.legacy.graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author user
 *
 */
public class RootedForest {
	String id = null;
	RootedTreeNode[] roots = null;
	
	public RootedForest(String id, RootedTreeNode[] roots) {
		this.id = id;
		this.roots = roots;
	}
	
	public String getId() {
		return id;
	}
	
	public RootedTreeNode[] getRoots() {
		return roots;
	}
	
	/**
	 * Converted the rooted tree to a default graph.  
	 */
	public DefaultGraph convertToDefaultGraph(boolean directed, boolean labelEdge) {
		DefaultGraph result = new DefaultGraph("Tree" + id);
		List<GraphNode> nodes = new ArrayList<GraphNode>();
		List<GraphEdge> edges = new ArrayList<GraphEdge>();
		
		for (int i = 0; i < roots.length; i++) {
			nodes.add(roots[i]);
			convertToDefaultGraph(roots[i], nodes, edges, directed, labelEdge);
		}
		
		result.setNodes(nodes);
		result.setEdges(edges);
		return result;
	}
	
	/**
	 * Write the tree to a text file, which can be regarded as the description of the graph 
	 * in dot language, and can be used to visualized the graph use Graphviz tools.
	 * @param out : the output text file, which should be opened
	 */
	public void simplyWriteToDotFile(PrintWriter output, boolean directed, boolean labelEdge) throws IOException {
		List<GraphNode> nodes = new ArrayList<GraphNode>();
		List<GraphEdge> edges = new ArrayList<GraphEdge>();
		for (int i = 0; i < roots.length; i++) {
			nodes.add(roots[i]);
			convertToDefaultGraph(roots[i], nodes, edges, directed, labelEdge);
		}

		String id = getLegalToken(getId());
		if (directed) output.println("digraph " + id + " {");
		else output.println("graph " + id + " {");
		for (GraphNode node : nodes) {
			boolean isChild = false;
			if (node instanceof RootedTreeNode) {
				RootedTreeNode treeNode = (RootedTreeNode)node;
				if (treeNode.childList == null) isChild = true;
				else if (treeNode.childList.size() == 0) isChild = true;
			}
			String label = node.getImageFileName();
			if (label == null || label.trim().equals("")) label = node.getLabel();
			id = "node" + getLegalToken(node.getId());
			if (isChild) output.println("    " + id + "[label = \"" + label + "\", shape = box]");
			else output.println("    " + id + "[label = \"" + label + "\"]");
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
					output.println("    " + startNodeId + edgeSharp + endNodeId + "[label = \"" + label + "\"]");
				} else {
					output.println("    " + startNodeId + edgeSharp + endNodeId);
				}
			}
		}

		output.println("};");
		output.println();
		output.flush();
	}
	
	/**
	 * Get a legal identifier (in dot language) from a identifier of graph, node, or edge 
	 */
	private String getLegalToken(String id) {
		StringBuilder token = new StringBuilder("");
		for (int index = 0; index < id.length(); index++) {
			char ch = id.charAt(index);
			if (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ('0' <= ch && ch <= '9')) token.append(ch);
			else token.append('_');
		}
		return token.toString();
	}
	
	/**
	 * Convert a sub-tree to a directed or undirected graph. The nodes of the sub-tree should be added into the parameter nodes and the edges 
	 * should be added into the parameter edges. Note that the node root has been added into nodes, but its children has not been added into nodes.  
	 */
	private void convertToDefaultGraph(RootedTreeNode root, List<GraphNode> nodes, List<GraphEdge> edges, boolean directed, boolean labelEdge) {
		List<RootedTreeNode> childList = root.getChildList();
		if (childList == null) return;
		int i = 0;
		for (RootedTreeNode childNode : childList) {
			nodes.add(childNode);
			String label = null;
			if (labelEdge) label = "" + i;
			i = i + 1;
			DefaultGraphEdge edge = new DefaultGraphEdge(root, childNode, label, directed);
			edges.add(edge);
			// Recursively convert the sub-tree rooted by this childNode to graph.
			convertToDefaultGraph(childNode, nodes, edges, directed, labelEdge);
		}
	}

}

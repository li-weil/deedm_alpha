package com.deedm.legacy.graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author user
 *
 */
public class RootedTree {
	String id = null;
	RootedTreeNode root = null;

	public RootedTree(String id, RootedTreeNode root) {
		this.id = id;
		this.root = root;
	}

	public String getId() {
		return id;
	}
	
	public RootedTreeNode getRoot() {
		return root;
	}
	
	/**
	 * Given a graph and a node as the root node, return a rooted tree, whose root is the given root node, and its children are the adjacency 
	 * node of the root node, etc. If do not give the root node, we will chose a node in the graph as the root node.
	 */
	public static RootedTree getAnRootedTree(DefaultGraph graph, DefaultGraphNode rootNode, boolean directedGraph) {
		if (rootNode == null) rootNode = (DefaultGraphNode)graph.getNodes().get(0);
		RootedTreeNode root = RootedTreeNode.createRootedTreeNodeFromDefaultGraphNode(rootNode);
		List<DefaultGraphNode> addedNodeList = new ArrayList<DefaultGraphNode>();
		addedNodeList.add((DefaultGraphNode)rootNode);

		getAnSubRootedTree(graph, root, rootNode, addedNodeList, directedGraph);
		RootedTree result = new RootedTree(graph.getId(), root);
		return result;
	}
	
	/**
	 * Converted the rooted tree to a default graph.  
	 */
	public DefaultGraph convertToDefaultGraph(boolean directed, boolean labelEdge) {
		DefaultGraph result = new DefaultGraph("Tree" + id);
		List<GraphNode> nodes = new ArrayList<GraphNode>();
		List<GraphEdge> edges = new ArrayList<GraphEdge>();
		
		nodes.add(root);
		convertToDefaultGraph(root, nodes, edges, directed, labelEdge);
		
		result.setNodes(nodes);
		result.setEdges(edges);
		return result;
	}
	
	public List<RootedTreeNode> getPreorderNodeList() {
		return getPreorderNodeList(root);
	}
	
	public List<RootedTreeNode> getInorderNodeList() {
		return getInorderNodeList(root);
	}

	public List<RootedTreeNode> getPostorderNodeList() {
		return getPostorderNodeList(root);
	}

	@Override
	public String toString() {
		return id+"<Root=" + root + ">";
	}
	
	/**
	 * Write the tree to a text file, which can be regarded as the description of the graph 
	 * in dot language, and can be used to visualized the graph use Graphviz tools.
	 * @param out : the output text file, which should be opened
	 */
	public void simplyWriteToDotFile(PrintWriter output, boolean directed, boolean labelEdge) throws IOException {
		List<GraphNode> nodes = new ArrayList<GraphNode>();
		List<GraphEdge> edges = new ArrayList<GraphEdge>();

		nodes.add(root);
		convertToDefaultGraph(root, nodes, edges, directed, labelEdge);

		String graphId = getLegalToken(getId());
		if (directed) {
			output.println("digraph " + graphId + " {");
		} else {
			output.println("graph " + graphId + " {");
		}

		output.println("  rankdir=TB;");
		output.println("  node [shape=circle, style=filled, fillcolor=lightblue];");

		// 写入节点
		for (GraphNode node : nodes) {
			boolean isLeaf = false;
			if (node instanceof RootedTreeNode) {
				RootedTreeNode treeNode = (RootedTreeNode)node;
				if (treeNode.childList == null || treeNode.childList.size() == 0) {
					isLeaf = true;
				}
			}

			String label = node.getImageFileName();
			if (label == null || label.trim().equals("")) {
				label = node.getLabel();
			}

			String nodeId = "node" + getLegalToken(node.getId());
			// 转义标签中的特殊字符
			String escapedLabel = label.replace("\"", "\\\"").replace("\n", "\\n");

			if (isLeaf) {
				output.println("  \"" + nodeId + "\" [label=\"" + escapedLabel + "\", shape=box, style=filled, fillcolor=lightgreen];");
			} else {
				output.println("  \"" + nodeId + "\" [label=\"" + escapedLabel + "\"];");
			}
		}

		// 写入边
		if (edges != null) {
			for (GraphEdge edge : edges) {
				GraphNode start = edge.getStartNode();
				GraphNode end = edge.getEndNode();
				String startNodeId = "node" + getLegalToken(start.getId());
				String endNodeId = "node" + getLegalToken(end.getId());

				if (directed) {
					output.println("  \"" + startNodeId + "\" -> \"" + endNodeId + "\";");
				} else {
					output.println("  \"" + startNodeId + "\" -- \"" + endNodeId + "\";");
				}
			}
		}

		output.println("}");
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
	
	/**
	 * Get a tree from the given graph. The root of the tree is the parameter root, which corresponding to the graph's node rootNode. The children of the 
	 * root will be the adjacent node of rootNode, and so on. The addNodeList given those graph node which have been added into the rooted tree so that we 
	 * can only select those adjacent nodes have not been added into the rooted tree as the children.  
	 */
	private static void getAnSubRootedTree(DefaultGraph graph, RootedTreeNode root, DefaultGraphNode rootNode, List<DefaultGraphNode> addedNodeList, boolean directedGraph) {
		List<GraphNode> adjacentNodeList = null;
		if (directedGraph) adjacentNodeList = graph.adjacentFromNode(rootNode);
		else adjacentNodeList = graph.adjacentNodes(rootNode);
		int size = adjacentNodeList.size();
		if (size == 0) return;
		
		DefaultGraphNode[] childGraphNodeList = new DefaultGraphNode[size];
		RootedTreeNode[] childTreeNodeList = new RootedTreeNode[size];
		int counter = 0;
		for (GraphNode adjacentNode : adjacentNodeList) {
			DefaultGraphNode aNode = (DefaultGraphNode)adjacentNode;
			if (!addedNodeList.contains(aNode)) {
				addedNodeList.add(aNode);
				RootedTreeNode treeNode = RootedTreeNode.createRootedTreeNodeFromDefaultGraphNode(aNode);
				root.addChildNode(treeNode);
				
				childGraphNodeList[counter] = aNode;
				childTreeNodeList[counter] = treeNode;
				counter = counter+1;
			}
		}
		for (int i = 0; i < counter; i++) {
			getAnSubRootedTree(graph, childTreeNodeList[i], childGraphNodeList[i], addedNodeList, directedGraph);
		}
	}
	
	private List<RootedTreeNode> getPreorderNodeList(RootedTreeNode startNode) {
		List<RootedTreeNode> result = new ArrayList<RootedTreeNode>();
		result.add(startNode);
		List<RootedTreeNode> childList = startNode.getChildList();
		if (childList == null) return result;
		for (RootedTreeNode child : childList) {
			List<RootedTreeNode> childResult = getPreorderNodeList(child);
			result.addAll(childResult);
		}
		return result;
	}

	private List<RootedTreeNode> getInorderNodeList(RootedTreeNode startNode) {
		List<RootedTreeNode> result = new ArrayList<RootedTreeNode>();
		List<RootedTreeNode> childList = startNode.getChildList();
		if (childList == null) {
			result.add(startNode);
			return result;
		}
		RootedTreeNode firstChild = childList.get(0);
		List<RootedTreeNode> childResult = getInorderNodeList(firstChild);
		result.addAll(childResult);
		result.add(startNode);
		for (int i = 1; i < childList.size(); i++) {
			RootedTreeNode otherChild = childList.get(i);
			childResult = getInorderNodeList(otherChild);
			result.addAll(childResult);
		}
		return result;
	}
	
	private List<RootedTreeNode> getPostorderNodeList(RootedTreeNode startNode) {
		List<RootedTreeNode> result = new ArrayList<RootedTreeNode>();
		List<RootedTreeNode> childList = startNode.getChildList();
		if (childList != null) {
			for (RootedTreeNode child : childList) {
				List<RootedTreeNode> childResult = getPostorderNodeList(child);
				result.addAll(childResult);
			}
		}
		result.add(startNode);
		return result;
	}
}

package com.deedm.legacy.graph;

import java.util.ArrayList;
import java.util.List;

import com.deedm.legacy.setrelfun.Matrix;

public class GraphUtil {
	private static String errorMessage = null;
	
	public static String getErrorMessage() {
		return errorMessage;
	}

	
	public static DefaultGraph createGraphUsingAdjacencyMatrix(Matrix matrix, ArrayList<GraphNode> nodes, String graphId, boolean directed, boolean labeledEdge) {
		ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
		int n = nodes.size();
		int edgeCounter = 1;
		for (int i = 0; i < n; i++) {
			int j = 0;
			if (!directed) j = i;  // If create an undirected graph, we only use the upper triangle of the matrix to create edges.
			for (; j < n; j++) {
				int value = 0;
				if (i < matrix.getRowNumber() && j < matrix.getColNumber()) value = matrix.get(i, j);
				for (int k = 1; k <= value; k++) {
					GraphNode startNode = nodes.get(i);
					GraphNode endNode = nodes.get(j);
					String label = null;
					if (labeledEdge) label = "e_{" + edgeCounter +"}";
					edgeCounter++;
					DefaultGraphEdge edge = new DefaultGraphEdge(startNode, endNode, label, directed);
					edges.add(edge);
				}
			}
		}

		DefaultGraph graph = new DefaultGraph(graphId);
		graph.setNodes(nodes);
		graph.setEdges(edges);
		return graph;
	}
	
	public static DefaultGraph createCompleteGraph(int n) {
		Matrix matrix = new Matrix(n, n);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) matrix.set(i, j, 1);
			}
		}
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		for (int i = 0; i < n; i++) {
			DefaultGraphNode node = new DefaultGraphNode("v"+i, "v_"+i);
			nodes.add(node);
		}
		return createGraphUsingAdjacencyMatrix(matrix, nodes, "K"+n, false, false);
	}

	public static DefaultGraph createCompleteBiGraph(int n, int m) {
		Matrix matrix = new Matrix(n+m, n+m);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				matrix.set(i, j+n, 1);
			}
		}
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		for (int i = 0; i < n+m; i++) {
			DefaultGraphNode node = new DefaultGraphNode("v"+i, "v_" + i);
			nodes.add(node);
		}
		return createGraphUsingAdjacencyMatrix(matrix, nodes, "K"+n+","+m, false, false);
	}
	
	public static DefaultGraph createCycleGraph(int n) {
		Matrix matrix = new Matrix(n, n);
		for (int i = 0; i < n-1; i++) matrix.set(i,  i+1, 1);
		matrix.set(0, n-1, 1);
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		for (int i = 0; i < n; i++) {
			DefaultGraphNode node = new DefaultGraphNode("v"+i, "v_"+i);
			nodes.add(node);
		}
		return createGraphUsingAdjacencyMatrix(matrix, nodes, "C"+n, false, false);
	}

	public static DefaultGraph createWheelGraph(int n) {
		Matrix matrix = new Matrix(n+1, n+1);
		for (int i = 0; i < n-1; i++) matrix.set(i,  i+1, 1);
		matrix.set(0, n-1, 1);
		for (int i = 0; i < n; i++) matrix.set(i, n, 1);
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		for (int i = 0; i <= n; i++) {
			DefaultGraphNode node = new DefaultGraphNode("v"+i, "v_" + i);
			nodes.add(node);
		}
		return createGraphUsingAdjacencyMatrix(matrix, nodes, "W"+n, false, false);
	}
	
	public static DefaultGraph createHyperCubeGraph(int n) {
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		char[] labelCharArray = new char[n];
		for (int i = 0; i < n; i++) labelCharArray[i] = '0';
		int nodeCounter = 0;
		do {
			String label = new String(labelCharArray);
			DefaultGraphNode node = new DefaultGraphNode("v"+nodeCounter, label);
			nodeCounter++;
			nodes.add(node);

			boolean isLast = true;
			for (int i = 0; i < n; i++) {
				if (labelCharArray[i] == '0') {
					isLast = false;
					break;
				}
			}
			if (isLast) break;
			
			for (int i = n-1; i >= 0; i--) {
				if (labelCharArray[i] == '1') labelCharArray[i] = '0';
				else {
					labelCharArray[i] = '1';
					break;
				}
			}
		} while(true);
		
		ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
		for (int i = 0; i < nodes.size(); i++) {
			GraphNode start = nodes.get(i);
			char[] startLabelArray = start.getLabel().toCharArray();
			for (int j = i+1; j < nodes.size(); j++) {
				GraphNode end = nodes.get(j);
				char[] endLabelArray = end.getLabel().toCharArray();
				int counter = 0;
				for (int k = 0; k < startLabelArray.length; k++) {
					if (startLabelArray[k] != endLabelArray[k]) counter++;
					if (counter > 1) break;
				}
				if (counter == 1) {
					DefaultGraphEdge edge = new DefaultGraphEdge(start, end);
					edges.add(edge);
				}
			}
		}
		
		DefaultGraph graph = new DefaultGraph("Q"+n);
		graph.setNodes(nodes);
		graph.setEdges(edges);
		return graph;
	}
	
	public static DefaultGraph randomGenerateGraph(int nodeNumber, int edgeNumber, boolean directed, boolean labeledEdge) {
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		for (int i = 0; i < nodeNumber; i++) {
			DefaultGraphNode node = new DefaultGraphNode("v" + i, "v_" + i);
			nodes.add(node);
		}
		ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
		for (int k = 0; k < edgeNumber; k++) {
			int i = (int)(Math.random() * nodeNumber);
			if (i >= nodeNumber) i = nodeNumber-1;
			GraphNode startNode = nodes.get(i);
			i = (int)(Math.random() * nodeNumber);
			if (i >= nodeNumber) i = nodeNumber-1;
			GraphNode endNode = nodes.get(i);
			String label = null;
			if (labeledEdge) label = "e_{"+k+"}";
			DefaultGraphEdge edge = new DefaultGraphEdge(startNode, endNode, label, directed);
			edges.add(edge);
		}

		String id = "RandomGraph";
		if (directed) id = "RandomDigraph";
		id = id + nodeNumber;
		DefaultGraph graph = new DefaultGraph(id);
		graph.setNodes(nodes);
		graph.setEdges(edges);
		return graph;
	}

	public static DefaultGraph randomGenerateSimpleGraph(int nodeNumber, int maxEdgeNumber, boolean directed, boolean labeledEdge) {
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		for (int i = 0; i < nodeNumber; i++) {
			DefaultGraphNode node = new DefaultGraphNode("v" + i, "v_" + i);
			nodes.add(node);
		}
		ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
		for (int k = 0; k < maxEdgeNumber; k++) {
			int i = (int)(Math.random() * nodeNumber);
			if (i >= nodeNumber) i = nodeNumber-1;
			int j = (int)(Math.random() * nodeNumber);
			if (j >= nodeNumber) j = nodeNumber-1;
			if (i == j) continue;
			
			GraphNode startNode = nodes.get(i);
			GraphNode endNode = nodes.get(j);
			String label = null;
			if (labeledEdge) label = "e_{"+k+"}";
			DefaultGraphEdge edge = new DefaultGraphEdge(startNode, endNode, label, directed);
			if (!edges.contains(edge)) edges.add(edge);
		}

		String id = "RandomSimpleGraph";
		if (directed) id = "RandomSimpleDigraph";
		id = id + nodeNumber;
		DefaultGraph graph = new DefaultGraph(id);
		graph.setNodes(nodes);
		graph.setEdges(edges);
		return graph;
	}
	
	public static WeightedGraph randomGenerateWeightedGraph(int nodeNumber, int edgeNumber, boolean labeledEdge, boolean directed, double maxWeight) {
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		for (int i = 0; i < nodeNumber; i++) {
			DefaultGraphNode node = new DefaultGraphNode("v" + i, "v_"+i);
			nodes.add(node);
		}
		ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
		for (int k = 0; k < edgeNumber; k++) {
			int i = (int)(Math.random() * nodeNumber);
			if (i >= nodeNumber) i = nodeNumber-1;
			GraphNode startNode = nodes.get(i);
			i = (int)(Math.random() * nodeNumber);
			if (i >= nodeNumber) i = nodeNumber-1;
			GraphNode endNode = nodes.get(i);
			if (startNode.equals(endNode)) continue;
			
			String label = null;
			if (labeledEdge) label = "e_{"+k+"}";
			double weight = (int)(Math.random() * maxWeight)+1;
			WeightedGraphEdge edge = new WeightedGraphEdge(startNode, endNode, label, directed, weight);
			if (!edges.contains(edge)) edges.add(edge);
		}

		String id = "RandomWeightedGraph";
		id = id + nodeNumber;
		WeightedGraph graph = new WeightedGraph(id);
		graph.setNodes(nodes);
		graph.setEdges(edges);
		return graph;
	}
	
	public static DefaultGraph randomGenerateUndirectedTree(int nodeNumber, boolean labeledEdge) {
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		ArrayList<DefaultGraph.GraphComponent> componentList = new ArrayList<DefaultGraph.GraphComponent>();
		for (int i = 0; i < nodeNumber; i++) {
			DefaultGraphNode node = new DefaultGraphNode("v" + i, "v_" + i);
			nodes.add(node);
			DefaultGraph.GraphComponent component = new DefaultGraph.GraphComponent();
			component.add(node);
			componentList.add(component);
		}
		
		ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
		int k = 0;
		while (componentList.size() > 1) {
			int i = (int)(Math.random() * nodeNumber);
			if (i >= nodeNumber) i = nodeNumber-1;
			int j = (int)(Math.random() * nodeNumber);
			if (j >= nodeNumber) j = nodeNumber-1;
			if (i == j) continue;
			
			GraphNode startNode = nodes.get(i);
			GraphNode endNode = nodes.get(j);
			String label = null;
			if (labeledEdge) label = "e_{"+k+"}";
			k++;
			DefaultGraphEdge edge = new DefaultGraphEdge(startNode, endNode, label, false);
			if (edges.contains(edge)) continue;
			
			DefaultGraph.GraphComponent returnedComponent = DefaultGraph.addEdgeToComponentList(componentList, edge);
			if (returnedComponent.getType() != DefaultGraph.GraphComponent.CIRCLED_COMPONENT) edges.add(edge);  
			else returnedComponent.changeTypeTo(DefaultGraph.GraphComponent.NORMAL_COMPONENT);
		}

		String id = "RandomTree";
		id = id + nodeNumber;
		DefaultGraph graph = new DefaultGraph(id);
		graph.setNodes(nodes);
		graph.setEdges(edges);
		return graph;
	}
	
	public static String getNodeLaTeXString(List<GraphNode> nodes) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\\{ ");
		boolean isFirst = true;
		for (GraphNode node : nodes) {
			if (isFirst) {
				buffer.append(node.getLabel()); 
				isFirst = false;
			} else {
				buffer.append(", " + node.getLabel());
			}
		}
		buffer.append(" \\}");
		return buffer.toString();
	}

	public static String getFormatedNodeString(List<GraphNode> nodes) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{ ");
		boolean isFirst = true;
		for (GraphNode node : nodes) {
			if (isFirst) {
				buffer.append(node.getId()); 
				if (!node.getId().equals(node.getLabel())) buffer.append("(" + node.getLabel() + ")");
				isFirst = false;
			} else {
				buffer.append(", " + node.getId());
				if (!node.getId().equals(node.getLabel())) buffer.append("(" + node.getLabel() + ")");
			}
		}
		buffer.append(" }");
		return buffer.toString();
	}
	
	public static String getEdgeLaTeXString(List<GraphEdge> edges) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\\{ ");
		boolean isFirst = true;
		for (GraphEdge edge : edges) {
			if (isFirst) {
				isFirst = false;
			} else {
				buffer.append(", ");
			}
			String label = edge.getLabel();
			boolean directed = edge.isDirected();
			if (label != null) buffer.append(label + " = ");
			if (directed) buffer.append("\\langle ");
			else buffer.append("(");
			buffer.append(edge.getStartNode().getLabel() + ", " + edge.getEndNode().getLabel());
			if (directed) buffer.append("\\rangle ");
			else buffer.append(")");
		}
		buffer.append(" \\}");
		return buffer.toString();
	}

	public static String getWeightedEdgeLaTeXString(List<GraphEdge> edges) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\\{ ");
		boolean isFirst = true;
		for (GraphEdge edge : edges) {
			WeightedGraphEdge weightedEdge = (WeightedGraphEdge)edge;
			if (isFirst) {
				isFirst = false;
			} else {
				buffer.append(", ");
			}
			String label = weightedEdge.getLabel();
			boolean directed = weightedEdge.isDirected();
			buffer.append(label + " = ");
			if (directed) buffer.append("\\langle ");
			else buffer.append("(");
			buffer.append(weightedEdge.getStartNode().getLabel() + ", " + weightedEdge.getEndNode().getLabel());
			if (directed) buffer.append("\\rangle ");
			else buffer.append(")");
		}
		buffer.append(" \\}");
		return buffer.toString();
	}
	
	public static String getFormatedEdgeString(List<GraphEdge> edges) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{ ");
		boolean isFirst = true;
		for (GraphEdge edge : edges) {
			if (isFirst) {
				isFirst = false;
			} else {
				buffer.append(", ");
			}
			String label = edge.getLabel();
			boolean directed = edge.isDirected();
			if (label != null) buffer.append(label + " = ");
			if (directed) buffer.append("<");
			else buffer.append("(");
			buffer.append(edge.getStartNode().getId() + ", " + edge.getEndNode().getId());
			if (directed) buffer.append(">");
			else buffer.append(")");
		}
		buffer.append(" }");
		return buffer.toString();
	}

	public static String getFormatedWeightedEdgeString(List<GraphEdge> edges) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{ ");
		boolean isFirst = true;
		for (GraphEdge edge : edges) {
			WeightedGraphEdge weightedEdge = (WeightedGraphEdge)edge;
			if (isFirst) {
				isFirst = false;
			} else {
				buffer.append(", ");
			}
			String label = weightedEdge.getLabel();
			boolean directed = weightedEdge.isDirected();
			buffer.append(label + " = ");
			if (directed) buffer.append("<");
			else buffer.append("(");
			buffer.append(weightedEdge.getStartNode().getId() + ", " + weightedEdge.getEndNode().getId());
			if (directed) buffer.append(">");
			else buffer.append(")");
		}
		buffer.append(" }");
		return buffer.toString();
	}
	
	/**
	 * Use formated strings, which often are inputed by user to create a graph. The nodeString, given the nodes of the graph, should be formated as "{nodeId(label), 
	 * nodeId(label), ..., nodeId(label)}", where both "nodeId" and "label" are string. Note the label of a node can be omitted. The edgesString, given the edges of 
	 * the graph, should be formated as "{edgeId = (nodeId, nodeId), ...}", if it is an undirected graph, or as "{edgeId = <nodeId, nodeId>, ...}", if it is a 
	 * directed graph. All nodeId in the edgeString should be a nodeId in nodeString, and some edgeIds can be omitted.
	 */
	public static DefaultGraph createGraphUsingFormatedString(String graphId, String nodesString, String edgesString, boolean directed) {
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		if (extractNodesFromFormatedString(nodesString, nodes) == false) return null;
		
		ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
		if (extractEdgesFromFormatedString(nodes, edgesString, edges, directed) == false) return null;
		
		DefaultGraph graph = new DefaultGraph(graphId);
		graph.setNodes(nodes);
		graph.setEdges(edges);
		return graph;
	}
	
	public static boolean extractNodesFromFormatedString(String nodesString, List<GraphNode> nodes) {
		boolean hasAddedNode = false;		
		int index = 0;
		StringBuffer nodeIdBuffer = new StringBuffer();
		StringBuffer nodeLabelBuffer = null;
		boolean inNodeString = false;
		boolean inLabelString = false;
		while (index < nodesString.length()) {
			char ch = nodesString.charAt(index);
			index = index + 1;
			
			if (ch == '{' || ch == '}' || ch == '\n' || ch == '\r') continue;
			if (inNodeString == false && inLabelString == false && ch == ' ') continue;
			if (ch == ',') {
				if (inNodeString == false) {
					errorMessage = "Meet illegal , in node string " + nodesString;
					return false;
				}
				String nodeId = nodeIdBuffer.toString().trim();
				String nodeLabel = nodeId;
				if (nodeLabelBuffer != null) nodeLabel = nodeLabelBuffer.toString().trim();
				DefaultGraphNode node = new DefaultGraphNode(nodeId, nodeLabel);
				nodes.add(node);
				nodeIdBuffer = new StringBuffer();
				nodeLabelBuffer = null;
				inLabelString = false;
				inNodeString = false;
				hasAddedNode = true;
			} else if (ch == '(') {
				inLabelString = true;
				nodeLabelBuffer = new StringBuffer();
			} else if (ch == ')') {
				inLabelString = false;
			} else {
				inNodeString = true;
				if (inLabelString) nodeLabelBuffer.append(ch);
				else nodeIdBuffer.append(ch);
			}
		}
		String nodeId = nodeIdBuffer.toString().trim();
		if (!nodeId.contentEquals("")) {
			String nodeLabel = nodeId;
			if (nodeLabelBuffer != null) nodeLabel = nodeLabelBuffer.toString().trim();
			DefaultGraphNode node = new DefaultGraphNode(nodeId, nodeLabel);
			nodes.add(node);
			hasAddedNode = true;
		}
		return hasAddedNode;
	}
	
	public static boolean extractEdgesFromFormatedString(List<GraphNode> nodes, String edgesString, List<GraphEdge> edges, boolean directed) {
		int index = 0;
		StringBuffer idBuffer = new StringBuffer();
		GraphNode startNode = null;
		String edgeLabel = null;
		boolean inIdString = false;
		boolean inEdge = false;
		while (index < edgesString.length()) {
			char ch = edgesString.charAt(index);
			index = index + 1;

			if (ch == '{' || ch == '}' || ch == '\n' || ch == '\r') continue;
			if (inIdString == false && ch == ' ') continue;
			
			if (ch == '=') {
				if (inEdge == true || inIdString == false) {
					errorMessage = "Meet illegal '=' in edge string " + edgesString;
					return false;
				}
				edgeLabel = idBuffer.toString().trim();
				idBuffer = new StringBuffer();
				inIdString = false;
			} else if (ch == ',') {
				if (inEdge == true && inIdString == false) {
					errorMessage = "Meet illegal ',' in edge string " + edgesString;
					return false;
				}
				if (inEdge) {
					String nodeId = idBuffer.toString().trim();
					startNode = findNodeById(nodes, nodeId);
					if (startNode == null) {
						errorMessage = "Can not find node id = [" + nodeId + "] for the start node in the given node list!";
						return false;
					}
					idBuffer = new StringBuffer();
				}
				inIdString = false;
			} else if (ch == '(' || ch == '<') {
				if (inIdString == true || inEdge == true) {
					errorMessage = "Meet illegal '(' or '<' (index = " + index + ", inEdge = " + inEdge + ", inIdString = " + inIdString + ") in edge string " + edgesString;
					return false;
				}
				inEdge = true;
			} else if (ch == ')' || ch == '>') {
				if (inEdge == false || inIdString == false) {
					errorMessage = "Meet illegal ')' or '>' (index = " + index + ", inEdge = " + inEdge + ", inIdString = " + inIdString + ") in edge string " + edgesString;
					return false;
				}
				String nodeId = idBuffer.toString().trim();
				GraphNode endNode = findNodeById(nodes, nodeId);
				if (endNode == null) {
					errorMessage = "Can not find node id = [" + nodeId + "] for the end node in the given node list!";
					return false;
				}
				if (startNode == null) {
					errorMessage = "Do not give the start node before ')' or '>'!";
					return false;
				}
				GraphEdge edge = new DefaultGraphEdge(startNode, endNode, edgeLabel, directed);
				edges.add(edge);
				
				idBuffer = new StringBuffer();
				inEdge = false;
				inIdString = false;
				startNode = null;
			} else {
				inIdString = true;
				idBuffer.append(ch);
			}
		}
		
		return true;
	}
	
	/**
	 * Use formated strings, which often are inputed by user to create a graph. The nodeString, given the nodes of the graph, should be formated as "{nodeId(label), 
	 * nodeId(label), ..., nodeId(label)}", where both "nodeId" and "label" are string. Note the label of a node can be omitted. The edgesString, given the weighted 
	 * edges of the graph, should be formated as "{edgeId[weight] = (nodeId, nodeId), ...}", if it is an undirected graph, or as "{edgeId[weight] = <nodeId,  
	 * nodeId>, ...}", if it is a directed graph. All nodeId in the edgeString should be a nodeId in nodeString, and some edgeIds can be omitted.
	 */
	public static WeightedGraph createWeightedGraphUsingFormatedString(String graphId, String nodesString, String edgesString, boolean directed) {
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		if (extractNodesFromFormatedString(nodesString, nodes) == false) return null;
		
		ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
		if (extractWeightedEdgesFromFormatedString(nodes, edgesString, edges, directed) == false) return null;
		
		WeightedGraph graph = new WeightedGraph(graphId);
		graph.setNodes(nodes);
		graph.setEdges(edges);
		return graph;
	}
	
	public static boolean extractWeightedEdgesFromFormatedString(List<GraphNode> nodes, String edgesString, List<GraphEdge> edges, boolean directed) {
		int index = 0;
		StringBuffer idBuffer = new StringBuffer();
		GraphNode startNode = null;
		String edgeLabel = null;
		boolean inIdString = false;
		boolean inEdge = false;
		boolean inWeight = false;
		double weight = 0.0;
		while (index < edgesString.length()) {
			char ch = edgesString.charAt(index);
			index = index + 1;

			if (ch == '{' || ch == '}' || ch == '\n' || ch == '\r') continue;
			if (inIdString == false && ch == ' ') continue;
			
			if (ch == '=') {
				if (inEdge == true) {
					errorMessage = "Meet illegal '=' in edge string " + edgesString;
					return false;
				}
				if (inIdString == true) {
					edgeLabel = idBuffer.toString().trim();
					idBuffer = new StringBuffer();
					inIdString = false;
				}
			} else if (ch == ',') {
				if (inEdge == true && inIdString == false) {
					errorMessage = "Meet illegal ',' in edge string " + edgesString;
					return false;
				}
				if (inEdge) {
					String nodeId = idBuffer.toString().trim();
					startNode = findNodeById(nodes, nodeId);
					if (startNode == null) {
						errorMessage = "Can not find node id = [" + nodeId + "] for the start node in the given node list!";
						return false;
					}
					idBuffer = new StringBuffer();
				}
				inWeight = false;
				inIdString = false;
			} else if (ch == '(' || ch == '<') {
				if (inIdString == true || inEdge == true) {
					errorMessage = "Meet illegal '(' or '<' (index = " + index + ", inEdge = " + inEdge + ", inIdString = " + inIdString + ") in edge string " + edgesString;
					return false;
				}
				inEdge = true;
			} else if (ch == '[') {
				if (inEdge == true) {
					errorMessage = "Meet illegal '[' (index = " + index + ", inEdge = " + inEdge + ") in edge string " + edgesString;
					return false;
				}
				if (inIdString == true) {
					edgeLabel = idBuffer.toString().trim();
					idBuffer = new StringBuffer();
					inIdString = false;
				}
				inWeight = true;
			} else if (ch == ']') {
				if (inWeight == false) {
					errorMessage = "Meet illegal ']' (index = " + index + ", inWeight = " + inWeight + ") in edge string " + edgesString;
					return false;
				}
				String weightString = idBuffer.toString().trim();
				weight = Double.parseDouble(weightString);
				idBuffer = new StringBuffer();
				inWeight = false;
			} else if (ch == ')' || ch == '>') {
				if (inEdge == false || inIdString == false) {
					errorMessage = "Meet illegal ')' or '>' (index = " + index + ", inEdge = " + inEdge + ", inIdString = " + inIdString + ") in edge string " + edgesString;
					return false;
				}
				String nodeId = idBuffer.toString().trim();
				GraphNode endNode = findNodeById(nodes, nodeId);
				if (endNode == null) {
					errorMessage = "Can not find node id = [" + nodeId + "] for the end node in the given node list!";
					return false;
				}
				if (startNode == null) {
					errorMessage = "Do not give the start node before ')' or '>'!";
					return false;
				}
				GraphEdge edge = new WeightedGraphEdge(startNode, endNode, edgeLabel, directed, weight);
				edges.add(edge);
				
				idBuffer = new StringBuffer();
				inEdge = false;
				inIdString = false;
				startNode = null;
			} else {
				if (inWeight == false) inIdString = true;
				idBuffer.append(ch);
			}
		}
		
		return true;
	}

	private static GraphNode findNodeById(List<GraphNode> nodes, String nodeId) {
		for (GraphNode node : nodes) {
			if (node.getId().equals(nodeId)) return node;
		}
		return null;
	}
}

package com.deedm.legacy.graph;

import java.util.ArrayList;
import java.util.List;

public class WeightedGraph extends DefaultGraph {

	public WeightedGraph(String id) {
		super(id);
	}

	public DoubleMatrix getWeightAdjacencyMatrix() {
		int rows = nodes.size();
		int cols = rows;

		DoubleMatrix result = new DoubleMatrix(rows, cols);
		for (int i = 0; i < rows; i++) 
			for (int j = 0; j < cols; j++) result.set(i, j, Double.MAX_VALUE);
				
		for (int j = 0; j < edges.size(); j++) {
			WeightedGraphEdge edge = (WeightedGraphEdge)edges.get(j);
			GraphNode startNode = edge.getStartNode();
			GraphNode endNode = edge.getEndNode();
			int row = nodes.indexOf(startNode);
			int col = nodes.indexOf(endNode);
			if (row < 0 || row >= nodes.size()) continue;
			if (col < 0 || col >= nodes.size()) continue;
			double value = result.get(row, col);
			if (edge.weight < value) result.set(row, col, edge.weight);
			if (!edge.isDirected()) {
				value = result.get(col, row);
				if (edge.weight < value) result.set(col, row, edge.weight);
			}
		}
		return result;
	}
	
	public double getTotalWeight() {
		double sum = 0;
		for (GraphEdge edge : edges) {
			WeightedGraphEdge wedge = (WeightedGraphEdge)edge;
			sum = sum + wedge.weight;
		}
		return sum;
	}
	
	public static class KruskalStepRecorder {
		private int step = 0;
		private WeightedGraphEdge edge = null;
		private boolean selected = false;
		
		public KruskalStepRecorder(int step, WeightedGraphEdge edge, boolean selected) {
			this.step = step;
			this.edge = edge;
			this.selected = selected;
		}
		public int getStep() {
			return step;
		}
		public WeightedGraphEdge getEdge() {
			return edge;
		}
		public boolean isSelected() {
			return selected;
		}
	}
	
	public static class KruskalRecorder {
		private List<KruskalStepRecorder> stepList = null;
		
		public KruskalRecorder() {
			stepList = new ArrayList<KruskalStepRecorder>();
		}
		public boolean addStepRecorder(KruskalStepRecorder recorder) {
			return stepList.add(recorder);
		}
		public boolean addStepRecorder(int step, WeightedGraphEdge edge, boolean selected) {
			return stepList.add(new KruskalStepRecorder(step, edge, selected));
		}
		
		public String toLaTeXString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("\\begin{tabular}{c|c|c}\n");
			buffer.append("\\hline\\textrm{Step} & \\textrm{Consider Edge} & \\textrm{Selected or not} \\\\ \n");
			for (KruskalStepRecorder recorder : stepList) {
				WeightedGraphEdge edge = (WeightedGraphEdge)recorder.getEdge();
				if (recorder.isSelected()) buffer.append("\\hline \\textrm{Step}\\quad " + recorder.getStep() + " & " + edge.toLaTeXString() + " & \\checkmark \\\\ \n"); 
				else buffer.append("\\hline \\textrm{Step}\\quad " + recorder.getStep() + " & " + edge.toLaTeXString() + " & \\times \\\\ \n");
			}
			buffer.append("\\hline\n");
			buffer.append("\\end{tabular}\n");
			buffer.trimToSize();
			return buffer.toString();
		}
	}
	
	private static KruskalRecorder kruskalRecorder = null;
	public static KruskalRecorder getKruskalRecorder() {
		return kruskalRecorder;
	}
	
	public WeightedGraph kruskal () {
		List<GraphComponent> componentList = new ArrayList<GraphComponent>();
		List<GraphEdge> selectEdgeList = new ArrayList<GraphEdge>();
		WeightedGraphEdge[] sortedEdges = sortEdgesByWeight(); 
		int totalNumber = nodes.size();
		
		int stepCounter = 1;
		int selectedCounter = 0;
		kruskalRecorder = new KruskalRecorder();
		for (int i = 0; i < sortedEdges.length && selectedCounter < totalNumber-1; i++) {
			WeightedGraphEdge edge = sortedEdges[i];
			boolean selected = false;
			
			if (edge.getStartNode().equals(edge.getEndNode())) continue;
			GraphComponent component = addEdgeToComponentList(componentList, edge);
			if (component.getType() != GraphComponent.CIRCLED_COMPONENT) {
				selectEdgeList.add(edge);
				selectedCounter++;
				selected = true;
			} else component.changeTypeTo(GraphComponent.NORMAL_COMPONENT);
			
			kruskalRecorder.addStepRecorder(stepCounter, edge, selected);
			stepCounter++;
		}
		
		WeightedGraph result = new WeightedGraph("MSTof" + id);
		result.setNodes(nodes);
		result.setEdges(selectEdgeList);
		return result;
	}
	
	protected WeightedGraphEdge[] sortEdgesByWeight() {
		WeightedGraphEdge[] result = new WeightedGraphEdge[edges.size()];
		result[0] = (WeightedGraphEdge)edges.get(0);
		int currentLength = 1;

		for (int i = 1; i < edges.size(); i++) {
			WeightedGraphEdge currentEdge = (WeightedGraphEdge)edges.get(i);
			int insertIndex = 0;
			while (insertIndex < currentLength) {
				if (result[insertIndex].weight <= currentEdge.weight) insertIndex++;
				else break;
			}
			for (int moveIndex = currentLength; moveIndex > insertIndex; moveIndex--) result[moveIndex] = result[moveIndex-1];
			result[insertIndex] = currentEdge;
			currentLength = currentLength+1;
		}
		return result;
	}
	
	public static class PrimStepRecorder {
		private int step = 0;
		private List<GraphEdge> considerEdgeList = null;
		private List<GraphNode> treeNodeList = null;
		private List<GraphNode> otherNodeList = null;
		private GraphEdge selectedEdge = null;
		private GraphNode selectedNode = null;
		
		public PrimStepRecorder(int step, List<GraphEdge> considerEdgeList, List<GraphNode> treeNodeList, List<GraphNode> otherNodeList, GraphEdge selectedEdge, GraphNode selectedNode) {
			this.step = step;
			this.considerEdgeList = new ArrayList<GraphEdge>();
			this.considerEdgeList.addAll(considerEdgeList);
			
			this.treeNodeList = new ArrayList<GraphNode>();
			this.treeNodeList.addAll(treeNodeList);
			
			this.otherNodeList = new ArrayList<GraphNode>();
			this.otherNodeList.addAll(otherNodeList);
			
			this.selectedEdge = selectedEdge;
			this.selectedNode = selectedNode;
		}
		
		public int getStep() {
			return step;
		}
		public List<GraphEdge> getConsiderEdgeList() {
			return considerEdgeList;
		}
		public List<GraphNode> getTreeNodeList() {
			return treeNodeList;
		}
		public List<GraphNode> getOtherNodeList() {
			return otherNodeList;
		}
		public GraphEdge getSelectedEdge() {
			return selectedEdge;
		}
		public GraphNode getSelectedNode() {
			return selectedNode;
		}
	}

	public static class PrimRecorder {
		private List<PrimStepRecorder> stepList = null;
		
		public PrimRecorder() {
			stepList = new ArrayList<PrimStepRecorder>();
		}
		public boolean addStepRecorder(PrimStepRecorder recorder) {
			return stepList.add(recorder);
		}
		public boolean addStepRecorder(int step, List<GraphEdge> considerEdgeList, List<GraphNode> treeNodeList, List<GraphNode> otherNodeList, GraphEdge selectedEdge, GraphNode selectedNode) {
			return stepList.add(new PrimStepRecorder(step, considerEdgeList, treeNodeList, otherNodeList, selectedEdge, selectedNode));
		}
		public String toLaTeXString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("\\begin{tabular}{c|c|c|c|c|c}\n");
			buffer.append("\\hline\\textrm{Step} & \\textrm{Nodes in Tree} & \\textrm{Nodes Not in Tree} & \\textrm{Consider Edges} & \\textrm{Select Edge} & \\textrm{Select Node} \\\\ \n");
			for (PrimStepRecorder recorder : stepList) {
				buffer.append("\\hline \\textrm{Step}\\quad " + recorder.getStep() + " & ");
				StringBuffer message = new StringBuffer();
				boolean isFirst = true;
				for (GraphNode node : recorder.getTreeNodeList()) {
					if (isFirst) {
						message.append(node.getLabel());
						isFirst = false;
					} else message.append(", " + node.getLabel());
				}
				message.trimToSize();
				buffer.append(message + " & ");
				
				message = new StringBuffer();
				isFirst = true;
				for (GraphNode node : recorder.getOtherNodeList()) {
					if (isFirst) {
						message.append(node.getLabel());
						isFirst = false;
					} else message.append(", " + node.getLabel());
				}
				message.trimToSize();
				buffer.append(message + " & ");
				
				message = new StringBuffer();
				isFirst = true;
				for (GraphEdge edge : recorder.getConsiderEdgeList()) {
					WeightedGraphEdge wedge = (WeightedGraphEdge)edge;
					if (isFirst) {
						message.append(wedge.toLaTeXString());
						isFirst = false;
					} else message.append(", " + wedge.toLaTeXString());
				}
				message.trimToSize();
				WeightedGraphEdge swedge = (WeightedGraphEdge)recorder.getSelectedEdge();
				buffer.append(message + " & " + swedge.toLaTeXString() + " & " + recorder.getSelectedNode().getLabel() + "\\\\ \n");
			}
			buffer.append("\\hline\n");
			buffer.append("\\end{tabular}\n");
			buffer.trimToSize();
			return buffer.toString();
		}
	}
	
	private static PrimRecorder primRecorder = null;
	public static PrimRecorder getPrimRecorder() {
		return primRecorder;
	}
	
	public WeightedGraph prim () {
		List<GraphEdge> selectEdgeList = new ArrayList<GraphEdge>();
		List<GraphNode> treeNodeList = new ArrayList<GraphNode>();
		List<GraphNode> otherNodeList = new ArrayList<GraphNode>();
		primRecorder = new PrimRecorder();
		
		treeNodeList.add(nodes.get(0));
		for (int i = 1; i < nodes.size(); i++) otherNodeList.add(nodes.get(i));
		int stepCounter = 1;
		while (!otherNodeList.isEmpty()) {
			List<GraphEdge> considerEdgeList = new ArrayList<GraphEdge>();
			WeightedGraphEdge selectEdge = null;
			GraphNode otherNode = null;
			for (GraphEdge edge : edges) {
				GraphNode start = edge.getStartNode();
				GraphNode end = edge.getEndNode();
				WeightedGraphEdge weightedEdge = (WeightedGraphEdge)edge;
				if (treeNodeList.contains(start) && otherNodeList.contains(end)) {
					if (selectEdge == null) {
						selectEdge = weightedEdge;
						otherNode = end;
					} else if (weightedEdge.weight < selectEdge.weight) {
						selectEdge = weightedEdge;
						otherNode = end;
					}
					considerEdgeList.add(weightedEdge);
				} else if (treeNodeList.contains(end) && otherNodeList.contains(start)) {
					if (selectEdge == null) {
						selectEdge = weightedEdge;
						otherNode = start;
					} else if (weightedEdge.weight < selectEdge.weight) {
						selectEdge = weightedEdge;
						otherNode = start;
					}
					considerEdgeList.add(weightedEdge);
				}
			}
			if (selectEdge != null) {
				primRecorder.addStepRecorder(stepCounter, considerEdgeList, treeNodeList, otherNodeList, selectEdge, otherNode);
				stepCounter++;
				
				selectEdgeList.add(selectEdge);
				treeNodeList.add(otherNode);
				otherNodeList.remove(otherNode);
			} else {
				// If selectEdge == null, then the nodes in treeNodeList and the nodes in otherNodeList are in different graph connected component. Then
				// we should clear treeNodeList and select a node in otherNodeList to the treeNodeList to find another tree. 
				treeNodeList.clear();
				GraphNode first = otherNodeList.get(0);
				otherNodeList.remove(0);
				treeNodeList.add(first);
			}
		}
		
		WeightedGraph result = new WeightedGraph("MSTof" + id);
		result.setNodes(nodes);
		result.setEdges(selectEdgeList);
		return result;
	}
	
	public static class DijkstraStepRecorder {
		private int step = 0;
		private int selectIndex = 0;
		private boolean[] selectedArray = null;
		private double[] distanceArray = null;
		private int[] lastNodeIndexArray = null;

		public DijkstraStepRecorder(int step, int selectIndex, boolean[] selectedArray, double[] distanceArray, int[] lastNodeIndexArray) {
			this.step = step;
			this.selectIndex = selectIndex;
			this.selectedArray = new boolean[selectedArray.length];
			for (int i = 0; i < selectedArray.length; i++) this.selectedArray[i] = selectedArray[i];
			this.distanceArray = new double[distanceArray.length];
			for (int i = 0; i < distanceArray.length; i++) this.distanceArray[i] = distanceArray[i];
			this.lastNodeIndexArray = new int[lastNodeIndexArray.length];
			for (int i = 0; i < lastNodeIndexArray.length; i++) this.lastNodeIndexArray[i] = lastNodeIndexArray[i];
		}
		
		public int getStep() {
			return step;
		}
		public int getSelectIndex() {
			return selectIndex;
		}
		public boolean[] getSelectedArray() {
			return selectedArray;
		}
		public double[] getDistanceArray() {
			return distanceArray;
		}
		public int[] getLastNodeIndexArray() {
			return lastNodeIndexArray;
		}
	}
	
	public static class DijkstraRecorder {
		GraphNode[] nodeArray = null;
		List<DijkstraStepRecorder> stepList = null;
		
		public DijkstraRecorder(GraphNode[] nodeArray) {
			this.nodeArray = new GraphNode[nodeArray.length];
			for (int i = 0; i < nodeArray.length; i++) this.nodeArray[i] = nodeArray[i];
			stepList = new ArrayList<DijkstraStepRecorder>();
		}
		
		public boolean addStepRecord(DijkstraStepRecorder recorder) {
			return stepList.add(recorder); 
		}
		
		public boolean addStepRecord(int step, int selectIndex, boolean[] selectedArray, double[] distanceArray, int[] lastNodeIndexArray) {
			return stepList.add(new DijkstraStepRecorder(step, selectIndex, selectedArray, distanceArray, lastNodeIndexArray));
		}
		
		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("   \t"); 
			for (int i = 0; i < nodeArray.length; i++) {
				buffer.append(nodeArray[i].getLabel() + "\t");
			}
			buffer.append("\n");

			for (DijkstraStepRecorder recorder : stepList) {
				buffer.append("Step " + recorder.getStep() + "\t"); 
				for (int i = 0; i < nodeArray.length; i++) {
					if (i != recorder.getSelectIndex() && recorder.getSelectedArray()[i] == true) buffer.append("  \t");
					else {
						double[] distanceArray = recorder.getDistanceArray();
						int[] lastNodeIndex = recorder.getLastNodeIndexArray(); 
						if (distanceArray[i] < Double.MAX_VALUE) {
							buffer.append(distanceArray[i]);
							if (i == recorder.getSelectIndex()) buffer.append("*");
						} else buffer.append("-");
						if (lastNodeIndex[i] < 0) buffer.append("/-\t");
						else buffer.append("/" + nodeArray[lastNodeIndex[i]].getLabel() + "\t");
					}
				}
				buffer.append("\n");
			}
			return buffer.toString();
		}

		public String toLaTeXString() {
			StringBuffer buffer = new StringBuffer();
			
			buffer.append("\\begin{tabular}{c");
			for (int i = 0; i < nodeArray.length; i++) buffer.append("|c");
			buffer.append("}\n");

			buffer.append("\\hline\\quad & ");
			for (int i = 0; i < nodeArray.length; i++) {
				if (i == 0) buffer.append("$" + nodeArray[i].getLabel() + "$ ");
				else buffer.append("& $" + nodeArray[i].getLabel() + "$ ");
			}
			buffer.append("\\\\\n");

			for (DijkstraStepRecorder recorder : stepList) {
				buffer.append("\\hline \\textrm{Step}\\quad " + recorder.getStep() + " & "); 
				for (int i = 0; i < nodeArray.length; i++) {
					if (i != recorder.getSelectIndex() && recorder.getSelectedArray()[i] == true) {
						if (i == 0) buffer.append(" \\quad ");
						else buffer.append(" & \\quad ");
					}
					else {
						if (i > 0) buffer.append(" & ");
						double[] distanceArray = recorder.getDistanceArray();
						int[] lastNodeIndex = recorder.getLastNodeIndexArray(); 
						if (distanceArray[i] < Double.MAX_VALUE) {
							if (i == recorder.getSelectIndex()) buffer.append("$\\fbox{" + distanceArray[i] + "}");
							else buffer.append("$" + distanceArray[i]); 
						} else buffer.append("$\\infty ");
						if (lastNodeIndex[i] < 0) buffer.append("/-$ ");
						else buffer.append("/" + nodeArray[lastNodeIndex[i]].getLabel() + "$ ");
					}
				}
				buffer.append("\\\\\n");
			}
			buffer.append("\\hline\n");
			buffer.append("\\end{tabular}\n");
			buffer.trimToSize();
			return buffer.toString();
		}
	}

	private static DijkstraRecorder dijkstraRecorder = null;
	public static DijkstraRecorder getDijkstraRecorder() {
		return dijkstraRecorder;
	}
	
	public List<WeightedGraphPath> dijkstra(GraphNode source, GraphNode dest) {
		List<WeightedGraphPath> result = new ArrayList<WeightedGraphPath>();
		int sourceIndex = nodes.indexOf(source);
		if (sourceIndex < 0 || sourceIndex > nodes.size()) return result;

		GraphNode[] nodeArray = new GraphNode[nodes.size()];
		nodeArray[0] = source;
		int index = 1;
		for (GraphNode node : nodes) {
			if (!node.equals(source)) {
				nodeArray[index] = node;
				index++;
			}
		}
		DoubleMatrix matrix = getWeightAdjacencyMatrix();
		double[] distanceArray = new double[nodeArray.length];
		int[] lastNodeIndex = new int[nodeArray.length];

		boolean[] selectedArray = new boolean[nodeArray.length];
		selectedArray[0] = true;
		distanceArray[0] = 0;
		lastNodeIndex[0] = -1;
		for (int i = 1; i < nodeArray.length; i++) {
			selectedArray[i] = false;
			index = nodes.indexOf(nodeArray[i]);
			if (index < 0 || index > nodes.size()) {
				distanceArray[i] = Double.MAX_VALUE;
				lastNodeIndex[i] = -1;		// There is no path from source to this node
			} else {
				double distance = matrix.get(sourceIndex, index);
				if (distance >= Double.MAX_VALUE) {
					distanceArray[i] = Double.MAX_VALUE;
					lastNodeIndex[i] = -1; // There is no path from source to this node
				} else {
					distanceArray[i] = distance;
					lastNodeIndex[i] = 0; // There is an edge from source to this node
				}
			}
		}
		
		dijkstraRecorder = new DijkstraRecorder(nodeArray);
		int stepCounter = 0;
		dijkstraRecorder.addStepRecord(stepCounter, 0, selectedArray, distanceArray, lastNodeIndex);
		
		while (true) {
			boolean hasNode = false;
			for (int i = 0; i < selectedArray.length; i++) {
				if (selectedArray[i] == false) {
					hasNode = true;
					break;
				}
			}
			if (hasNode == false) break;
			
			int minIndex = -1;
			for (int i = 0; i < distanceArray.length; i++) {
				if (selectedArray[i] == false) {
					if (minIndex == -1) minIndex = i;
					else if (distanceArray[i] < distanceArray[minIndex]) minIndex = i;
				}
			}
			selectedArray[minIndex] = true;
			for (int i = 0; i < nodeArray.length; i++) {
				if (selectedArray[i] == false) {
					int matrixRow = nodes.indexOf(nodeArray[minIndex]);
					int matrixCol = nodes.indexOf(nodeArray[i]);
					double newDistance = distanceArray[minIndex]+matrix.get(matrixRow, matrixCol);
					if (distanceArray[i] > newDistance) {
						distanceArray[i] = newDistance;
						lastNodeIndex[i] = minIndex;
					}
				}
			}
			stepCounter++;
			dijkstraRecorder.addStepRecord(stepCounter, minIndex, selectedArray, distanceArray, lastNodeIndex);
			
			if (dest != null) {
				if (nodeArray[minIndex].equals(dest)) break;
			}
		}
		
		for (int i = 0; i < nodeArray.length; i++) {
			index = i;
			WeightedGraphPath path = null;
			while (lastNodeIndex[index] >= 0 && lastNodeIndex[index] < nodeArray.length) {
				GraphNode start = nodeArray[lastNodeIndex[index]];
				GraphNode end = nodeArray[index];
				WeightedGraphEdge edge = findShortestEdgeBetweenTwoNodes(start, end);
				if (edge != null) {
					if (path == null) path = new WeightedGraphPath();
					path.addEdgeFirst(edge);
				}
				index = lastNodeIndex[index];
			}
			if (path != null) result.add(path);
		}
		return result;
	}
	
	private WeightedGraphEdge findShortestEdgeBetweenTwoNodes(GraphNode startNode, GraphNode endNode) {
		WeightedGraphEdge selectedEdge = null;
		for (GraphEdge edge : edges) {
			WeightedGraphEdge wedge = (WeightedGraphEdge)edge;
			if (wedge.getStartNode().equals(startNode) && wedge.getEndNode().equals(endNode)) {
				if (selectedEdge == null) selectedEdge = wedge;
				else if (wedge.weight < selectedEdge.weight) selectedEdge = wedge;
			} else if (!wedge.isDirected() && wedge.getStartNode().equals(endNode) && wedge.getEndNode().equals(startNode)) {
				if (selectedEdge == null) selectedEdge = wedge;
				else if (wedge.weight < selectedEdge.weight) selectedEdge = wedge;
			}
		}
		return selectedEdge;
	}

}

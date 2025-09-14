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
public class HuffmanTree extends RootedTree {
	private static String errorMessage = null;
	
	/**
	 * The nodeString, given the nodes of the graph, should be formated as "{nodeId(label)[weight], nodeId(label)[weight], ..., 
	 * nodeId(label)[weight]}", where both "nodeId" and "label" are string. Note the label of a node can be omitted.  
	 */
	public static boolean extractWeightedLeafsFromFormatedString(String nodesString, List<GraphNode> nodes) {
		boolean hasAddedNode = false;		
		int index = 0;
		StringBuffer nodeIdBuffer = new StringBuffer();
		StringBuffer nodeLabelBuffer = null;
		StringBuffer weightBuffer = null;
		boolean inNodeString = false;
		boolean inLabelString = false;
		boolean inWeightString = false;
		while (index < nodesString.length()) {
			char ch = nodesString.charAt(index);
			index = index + 1;
			
			if (ch == '{' || ch == '}' || ch == '\n' || ch == '\r') continue;
			if (ch == ' ') {
				if (inWeightString == true) {
					errorMessage = "Meet illegal space character in the weight of the node string " + nodesString;
					return false;
				}
				if (inNodeString == false && inLabelString == false) continue;
			}
			if (ch == ',') {
				if (inNodeString == false) {
					errorMessage = "Meet illegal , in node string " + nodesString;
					return false;
				}
				String nodeId = nodeIdBuffer.toString().trim();
				String nodeLabel = nodeId;
				if (nodeLabelBuffer != null) nodeLabel = nodeLabelBuffer.toString().trim();
				if (weightBuffer == null) {
					errorMessage = "Does not give the weight of the leaf node " + nodeId + "!";
					return false;
				}
				double weight = Double.parseDouble(weightBuffer.toString().trim());
				WeightedTreeNode node = new WeightedTreeNode(nodeId, nodeLabel, weight);
				nodes.add(node);
				nodeIdBuffer = new StringBuffer();
				nodeLabelBuffer = null;
				weightBuffer = null;
				inLabelString = false;
				inNodeString = false;
				inWeightString = false;
				hasAddedNode = true;
			} else if (ch == '(') {
				inLabelString = true;
				nodeLabelBuffer = new StringBuffer();
			} else if (ch == ')') {
				inLabelString = false;
			} else if (ch == '[') {
				inWeightString = true;
				weightBuffer = new StringBuffer();
			} else if (ch == ']') {
				inWeightString = false;
			} else {
				inNodeString = true;
				if (inLabelString) nodeLabelBuffer.append(ch);
				else if (inWeightString) weightBuffer.append(ch);
				else nodeIdBuffer.append(ch);
			}
		}
		String nodeId = nodeIdBuffer.toString().trim();
		if (!nodeId.contentEquals("")) {
			String nodeLabel = nodeId;
			if (nodeLabelBuffer != null) nodeLabel = nodeLabelBuffer.toString().trim();
			if (weightBuffer == null) {
				errorMessage = "Does not give the weight of the leaf node " + nodeId + "!";
				return false;
			}
			double weight = Double.parseDouble(weightBuffer.toString().trim());
			WeightedTreeNode node = new WeightedTreeNode(nodeId, nodeLabel, weight);
			nodes.add(node);
			hasAddedNode = true;
		}
		return hasAddedNode;
	}
	
	public static String getWeightedLeafLaTeXString(List<GraphNode> weightedLeafList) {
		StringBuffer buffer = new StringBuffer();
		boolean isFirst = true;
		buffer.append("\\{");
		for (GraphNode node : weightedLeafList) {
			WeightedTreeNode treeNode = (WeightedTreeNode)node;
			if (isFirst == true) isFirst = false;
			else buffer.append(", ");
			buffer.append(treeNode.getLabel() + "[" + treeNode.getWeight() + "]");
		}
		buffer.append("\\}");
		return buffer.toString();
	}
	
	public static String getWeightedLeafFormatedString(List<GraphNode> weightedLeafList) {
		StringBuffer buffer = new StringBuffer();
		boolean isFirst = true;
		buffer.append("{ ");
		for (GraphNode node : weightedLeafList) {
			WeightedTreeNode treeNode = (WeightedTreeNode)node;
			if (isFirst == true) isFirst = false;
			else buffer.append(", ");
			buffer.append(treeNode.getId());
			if (!treeNode.getLabel().contentEquals(treeNode.getId())) buffer.append("(" + treeNode.getLabel() + ")");
			buffer.append("[" + treeNode.getWeight() + "]");
		}
		buffer.append(" }");
		return buffer.toString();
	}
	
	public static String getErrorMessage( ) {
		return errorMessage;
	}
	
	public static List<GraphNode> randomGenerateWeightedLeafNodes(int nodeNumber, int maxWeight) {
		List<GraphNode> result = new ArrayList<GraphNode>();
		for (int i = 0; i < nodeNumber; i++) {
			int weight = (int)(Math.random() * maxWeight)+1;
			WeightedTreeNode node = new WeightedTreeNode("v"+i, weight);
			result.add(node);
		}
		return result;
	}
	
	public static class HuffmanStepRecorder {
		private int step = 0;
		private WeightedTreeNode[] nodeArray = null;
		public HuffmanStepRecorder(int step, WeightedTreeNode[] nodeArray, int fromIndex, int toIndex) {
			this.step = step;
			this.nodeArray = new WeightedTreeNode[toIndex-fromIndex];
			for (int i = fromIndex; i < toIndex; i++) this.nodeArray[i-fromIndex] = nodeArray[i];
		}
		public int getStep() {
			return step;
		}
		public WeightedTreeNode[] getNodeArray() {
			return nodeArray;
		}
		public String getLaTeXStringOfNodeArray() {
			StringBuffer buffer = new StringBuffer();
			boolean isFirst = true;
			for (int i = 0; i < nodeArray.length; i++) {
				if (isFirst) {
					buffer.append(nodeArray[i].getLabel() + "[" + nodeArray[i].getWeight() + "] ");
					isFirst = false;
				}
				else buffer.append("\\quad\\quad\\quad\\quad " + nodeArray[i].getLabel() + "[" + nodeArray[i].getWeight() + "] ");
			}
			return buffer.toString();
		}
		public RootedForest getRootedForest() {
			RootedForest result = new RootedForest("Huffman" + step, nodeArray);
			return result;
		}
	}
	
	public static class HuffmanRecorder {
		private List<HuffmanStepRecorder> stepList = null;
		
		public HuffmanRecorder() {
			stepList = new ArrayList<HuffmanStepRecorder>();
		}
		public boolean addStepRecorder(int step, WeightedTreeNode[] nodeArray, int fromIndex, int toIndex) {
			return stepList.add(new HuffmanStepRecorder(step, nodeArray, fromIndex, toIndex));
		}
		public List<HuffmanStepRecorder> getStepList() {
			return stepList;
		}
	}
	
	private static HuffmanRecorder huffmanRecorder = null;
	public static HuffmanRecorder getHuffmanRecorder() {
		return huffmanRecorder;
	}
	
	public HuffmanTree(String id, WeightedTreeNode root) {
		super(id, root);
	}
	
	public static HuffmanTree createHuffmanTree(WeightedTreeNode[] leafs) {
		WeightedTreeNode[] nodeArray = new WeightedTreeNode[leafs.length];
		boolean[] selected = new boolean[leafs.length];
		for (int i = 0; i < selected.length; i++) selected[i] = false;
		
		// Copy leaf nodes (i.e. nodes in the parameter leafs) to nodeArray and sorted by its weight using selection sorting algorithm   
		for (int i = 0; i < nodeArray.length; i++) {
			int selectIndex = -1;
			for (int j = 0; j < leafs.length; j++) {
				if (selected[j] == true) continue;
				if (selectIndex < 0) selectIndex = j;
				else {
					if (leafs[j].getWeight() < leafs[selectIndex].getWeight()) selectIndex = j;
				}
			}
			nodeArray[i] = leafs[selectIndex];
			selected[selectIndex] = true;
		}

		huffmanRecorder = new HuffmanRecorder();
		int startIndex = 0;		// The index of the current least weight node
		huffmanRecorder.addStepRecorder(startIndex, nodeArray, startIndex, nodeArray.length);
		while (startIndex < nodeArray.length-1) {
			// Use first two least weight nodes to create a new node
			WeightedTreeNode leftChild = nodeArray[startIndex];
			WeightedTreeNode rightChild = nodeArray[startIndex+1];
			double weight = leftChild.getWeight() + rightChild.getWeight();
			WeightedTreeNode newNode = new WeightedTreeNode("t"+startIndex, weight);
			newNode.addChildNode(leftChild);
			newNode.addChildNode(rightChild);

			// Insert the new node to nodeArray with preserving the weight order of nodes
			int insertIndex = startIndex+1;
			while (insertIndex < nodeArray.length-1) {
				if (nodeArray[insertIndex+1].getWeight() < weight) {
					nodeArray[insertIndex] = nodeArray[insertIndex+1];
					insertIndex = insertIndex+1;
				} else break;
			}
			nodeArray[insertIndex] = newNode;
			// Now the current least weight node is in startIndex+1, so increase startIndex.
			startIndex = startIndex + 1;
			huffmanRecorder.addStepRecorder(startIndex, nodeArray, startIndex, nodeArray.length);
		}
		
		// Finally, the root of the Huffman tree is in nodeArray[startIndex]
		String id = "Huffman" + leafs.length;
		WeightedTreeNode root = nodeArray[startIndex];
		HuffmanTree result = new HuffmanTree(id, root);
		return result;
	}
	
	public String[] getCodeOfLeafNodes(WeightedTreeNode[] leafs) {
		String[] result = new String[leafs.length];
		codingLeafNode((WeightedTreeNode)root, "", leafs, result);
		return result;
	}
	
	public double getTotalWeight() {
		weightCalLaTeXString = new StringBuffer();
		isFirstLeaf = true;
		return calculateTotalWeightOfSubTree((WeightedTreeNode)root, 0);
	}

	public String getTotalWeightCalculateLaTeXString() {
		return weightCalLaTeXString.toString();
	}
	
	private StringBuffer weightCalLaTeXString = null;
	private boolean isFirstLeaf = true;
	
	private void codingLeafNode(WeightedTreeNode subtreeRoot, String currentCode, WeightedTreeNode[] leafs, String[] codes) {
		List<RootedTreeNode> childList = subtreeRoot.getChildList();
		if (childList == null) {
			// The subtreeRoot is a leaf indeed. Find it in the given WeightedTreeNode array leafs. If it is a node in leafs, then 
			// set the code of this leaf to be currentCode.
			for (int i = 0; i < leafs.length; i++) {
				if (leafs[i].equals(subtreeRoot)) {
					codes[i] = currentCode;
					break;
				}
			}
		} else {
			// Goto the left and right child to code the leaf node in the sub-tree.
			WeightedTreeNode left = (WeightedTreeNode)childList.get(0);
			WeightedTreeNode right = (WeightedTreeNode)childList.get(1);
			codingLeafNode(left, currentCode+"0", leafs, codes);
			codingLeafNode(right, currentCode+"1", leafs, codes);
		}
	}
	
	/**
	 * Calculate the total weight of the leafs in the sub-tree rooted by subtreeRoot. The parameter level gives the level of this root in the whole tree.
	 * This level information will be passed to the leaf, and then we know the level of all leafs in this tree.  
	 */
	private double calculateTotalWeightOfSubTree(WeightedTreeNode subtreeRoot, int level) {
		double result = 0; 
		List<RootedTreeNode> childList = subtreeRoot.getChildList();
		if (childList == null) {
			// The subtreeRoot is a leaf node indeed
			result = subtreeRoot.getWeight() * level;
			if (isFirstLeaf) isFirstLeaf = false;
			else weightCalLaTeXString.append(" + ");
			weightCalLaTeXString.append(subtreeRoot.getWeight() + "\\times " + level);
		} else {
			// The total weight of this sub-tree is the sum of weight of the leaf nodes, that is the sum of total weight of the sub-tree rooted by 
			// the children of subtreeRoot. 
			for (RootedTreeNode treeNode : childList) {
				WeightedTreeNode node = (WeightedTreeNode)treeNode;
				result = result + calculateTotalWeightOfSubTree(node, level+1);
			}
		}
		return result;
	}

}

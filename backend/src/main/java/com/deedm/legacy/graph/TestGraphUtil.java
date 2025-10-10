/**
 * 
 */
package com.deedm.legacy.graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.deedm.legacy.setrelfun.Matrix;

/**
 * @author user
 *
 */
public class TestGraphUtil {

	public static void main(String[] args) {
//		testGraphCreation();
//		testWeightedGraphCreation();
//		testShortestPath();
		
//		testRootedTree();
		testHuffmanTree();
	}
	
	public static void testHuffmanTree() {
		String rootPath = "E:\\";
		String path = rootPath + "ZxcWork\\DiscreteTools\\data\\";
		String fileName = "result.txt";

		try {
			PrintWriter writer = new PrintWriter(path + fileName);
			int n = 8;
			WeightedTreeNode[] leafs = new WeightedTreeNode[n];
			for (int i = 0; i < leafs.length; i++) {
				double weight = (int)(Math.random()*100)+1;
				leafs[i] = new WeightedTreeNode("v"+i, weight);
			}
			
			System.out.print("Weight: ");
			for (int i = 0; i < leafs.length; i++) System.out.print(leafs[i].getWeight() + " ");
			System.out.println();
			
			HuffmanTree huffman = HuffmanTree.createHuffmanTree(leafs);
			System.out.println("Total weight of the tree is " + huffman.getTotalWeight());
			
			String[] codes = huffman.getCodeOfLeafNodes(leafs);
			for (int i = 0; i < leafs.length; i++) {
				System.out.println("\tThe code of leaf " + leafs[i].getLabel() + " is " + codes[i]);
			}
			
			DefaultGraph graph = huffman.convertToDefaultGraph(false, true);
			graph.simplyWriteToDotFile(writer);
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void testRootedTree() {
		String rootPath = "E:\\";
		String path = rootPath + "ZxcWork\\DiscreteTools\\data\\";
		String fileName = "result.txt";

		try {
			PrintWriter writer = new PrintWriter(path + fileName);
			int n = 16;
//			int m = 20;
			boolean directed = false;
			DefaultGraph graph = GraphUtil.randomGenerateUndirectedTree(n, false);
			graph.simplyWriteToDotFile(writer);

			List<GraphNode> graphNodeList = graph.getNodes();
			GraphNode rootNode = null;
			for (int i = 0; i < graphNodeList.size(); i++) {
				rootNode = graphNodeList.get(i);
				if (graph.getDegree(rootNode) > 1) break;
			}
			
			RootedTree tree = RootedTree.getAnRootedTree(graph, (DefaultGraphNode)rootNode, directed);
			List<RootedTreeNode> nodeList = tree.getPreorderNodeList();
			System.out.println("Preorder node list: ");
			for (RootedTreeNode node : nodeList) System.out.print(node + " ");
			System.out.println();

			nodeList = tree.getInorderNodeList();
			System.out.println("Inorder node list: ");
			for (RootedTreeNode node : nodeList) System.out.print(node + " ");
			System.out.println();
			
			nodeList = tree.getPostorderNodeList();
			System.out.println("Postorder node list: ");
			for (RootedTreeNode node : nodeList) System.out.print(node + " ");
			System.out.println();

			graph = tree.convertToDefaultGraph(false, false);
			graph.simplyWriteToDotFile(writer);
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void testGraphCreation() {
		String rootPath = "E:\\";
		String path = rootPath + "ZxcWork\\DiscreteTools\\data\\";
		String fileName = "result.txt";

		try {
			PrintWriter writer = new PrintWriter(path + fileName);
			int n = 6;
			int m = 20;
//			AbstractGraph graph = GraphUtil.createHyperCubeGraph(n);
//			graph.simplyWriteToDotFile(writer);
//			graph = GraphUtil.createCompleteGraph(n);
//			graph.simplyWriteToDotFile(writer);
//			graph = GraphUtil.createCycleGraph(n);
//			graph.simplyWriteToDotFile(writer);
//			graph = GraphUtil.createWheelGraph(n);
//			graph.simplyWriteToDotFile(writer);
//			graph = GraphUtil.createCompleteBiGraph(n, m);
//			graph.simplyWriteToDotFile(writer);
			DefaultGraph graph = GraphUtil.randomGenerateGraph(n, m, false, false);
			graph.simplyWriteToDotFile(writer);
			DefaultGraph simpleGraph = GraphUtil.randomGenerateSimpleGraph(n, m, false, false);
			simpleGraph.simplyWriteToDotFile(writer);
			DefaultGraph digraph = GraphUtil.randomGenerateSimpleGraph(n, m, true, false);
			digraph.simplyWriteToDotFile(writer);
			
			List<GraphNode> nodes = graph.getNodes();
			for (GraphNode node : nodes) {
				System.out.println("Degree of node " + node + " is " + graph.getDegree(node));
			}
			Matrix imatrix = graph.getIncidenceMatrix();
			System.out.println("The incidence matrix of graph " + graph.getId() + " is ");
			System.out.println(imatrix);
			Matrix amatrix = graph.getAdjacencyMatrix();
			System.out.println("The adjacency matrix of graph " + graph.getId() + " is ");
			System.out.println(amatrix);
			
			nodes = digraph.getNodes();
			for (GraphNode node : nodes) {
				System.out.println("In-degree of node " + node + " is " + digraph.getInDegree(node));
				System.out.println("Out-Degree of node " + node + " is " + digraph.getOutDegree(node));
			}
			imatrix = digraph.getIncidenceMatrix();
			System.out.println("The incidence matrix of graph " + digraph.getId() + " is ");
			System.out.println(imatrix);
			amatrix = digraph.getAdjacencyMatrix();
			System.out.println("The adjacency matrix of graph " + digraph.getId() + " is ");
			System.out.println(amatrix);
			
			List<GraphNode> nodeDFSList = simpleGraph.getDFSNodeList();
			System.out.println("DFS node list: ");
			for (GraphNode node : nodeDFSList) System.out.print(node + " ");
			
			List<GraphNode> nodeBFSList = simpleGraph.getBFSNodeList();
			System.out.println();
			System.out.println("BFS node list: ");
			for (GraphNode node : nodeBFSList) System.out.print(node + " ");
			System.out.println();
			
			DefaultGraph spanningTree = simpleGraph.getUndirectSpanningForest();
			spanningTree.simplyWriteToDotFile(writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void testWeightedGraphCreation() {
		String rootPath = "E:\\";
		String path = rootPath + "ZxcWork\\DiscreteTools\\data\\";
		String fileName = "result.txt";

		try {
			PrintWriter writer = new PrintWriter(path + fileName);
			int n = 8;
			int m = 15;
			int maxWeight = 10;
			WeightedGraph graph = GraphUtil.randomGenerateWeightedGraph(n, m, true, false, maxWeight);
			graph.simplyWriteToDotFile(writer);
			
			WeightedGraph spanningTree = graph.kruskal();
			spanningTree.setId("Kruskal");
			System.out.println("Kruskal spanning tree's weight: " + spanningTree.getTotalWeight());
			spanningTree.simplyWriteToDotFile(writer);

			spanningTree = graph.prim();
			spanningTree.setId("Prim");
			System.out.println("Prim spanning tree's weight: " + spanningTree.getTotalWeight());
			spanningTree.simplyWriteToDotFile(writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void testShortestPath() {
		String rootPath = "E:\\";
		String path = rootPath + "ZxcWork\\DiscreteTools\\data\\";
		String fileName = "result.txt";

		try {
			PrintWriter writer = new PrintWriter(path + fileName);
			int n = 8;
			int m = 16;
			int maxWeight = 10;
			WeightedGraph graph = GraphUtil.randomGenerateWeightedGraph(n, m, false, false, maxWeight);
			graph.simplyWriteToDotFile(writer);
			GraphNode source = graph.getNodes().get(0);
			
			System.out.println("Source node " + source);
			List<WeightedGraphPath> pathList = graph.dijkstra(source, null);
			for (WeightedGraphPath shortestPath : pathList) {
				System.out.println(shortestPath);
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}

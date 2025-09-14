/**
 * 
 */
package com.deedm.legacy.graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import setrelfun.Matrix;

/**
 * @author user
 *
 */
public class GraphExampleSolver {
	
	public static void Example9_31() {
		String rootPath = "E:\\";
		String path = rootPath + "ZxcWork\\DiscreteTools\\data\\";
		String fileName = "result.txt";

		try {
			PrintWriter writer = new PrintWriter(path + fileName);
			String[] ids = {"a", "b", "c", "d", "e", "f", "g", "h"};
			double[] weights = {0.15, 0.12, 0.25, 0.08, 0.20, 0.06, 0.08, 0.06};
			WeightedTreeNode[] leafs = new WeightedTreeNode[ids.length];
			for (int i = 0; i < leafs.length; i++) {
				leafs[i] = new WeightedTreeNode(ids[i], weights[i]);
			}
			
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
	
	
	public static void Example9_28() {
		String rootPath = "E:\\";
		String path = rootPath + "ZxcWork\\DiscreteTools\\data\\";
		String fileName = "result.txt";

		try {
			PrintWriter writer = new PrintWriter(path + fileName);
			
			WeightedGraph graph = new WeightedGraph("Example9_28");
			DefaultGraphNode v1 = new DefaultGraphNode("v1");  graph.addNode(v1);
			DefaultGraphNode v2 = new DefaultGraphNode("v2");  graph.addNode(v2);
			DefaultGraphNode v3 = new DefaultGraphNode("v3");  graph.addNode(v3);
			DefaultGraphNode v4 = new DefaultGraphNode("v4");  graph.addNode(v4);
			DefaultGraphNode v5 = new DefaultGraphNode("v5");  graph.addNode(v5);
			DefaultGraphNode v6 = new DefaultGraphNode("v6");  graph.addNode(v6);
			WeightedGraphEdge e1 = new WeightedGraphEdge(v1, v2, true, 7);  graph.addEdge(e1);
			WeightedGraphEdge e2 = new WeightedGraphEdge(v1, v3, true, 1);  graph.addEdge(e2);
			WeightedGraphEdge e3 = new WeightedGraphEdge(v2, v4, true, 4);  graph.addEdge(e3);
			WeightedGraphEdge e4 = new WeightedGraphEdge(v2, v6, true, 1);  graph.addEdge(e4);
			WeightedGraphEdge e5 = new WeightedGraphEdge(v3, v2, true, 6);  graph.addEdge(e5);
			WeightedGraphEdge e6 = new WeightedGraphEdge(v3, v6, true, 7);  graph.addEdge(e6);
			WeightedGraphEdge e7 = new WeightedGraphEdge(v3, v5, true, 2);  graph.addEdge(e7);
			WeightedGraphEdge e8 = new WeightedGraphEdge(v5, v2, true, 3);  graph.addEdge(e8);
			WeightedGraphEdge e9 = new WeightedGraphEdge(v5, v4, true, 5);  graph.addEdge(e9);
			WeightedGraphEdge e10 = new WeightedGraphEdge(v6, v5, true, 3);  graph.addEdge(e10);
			graph.simplyWriteToDotFile(writer);

			List<WeightedGraphPath> pathList = graph.dijkstra(v1, null);
			for (WeightedGraphPath shortestPath : pathList) {
				System.out.println(shortestPath);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void Example9_29() {
		String rootPath = "E:\\";
		String path = rootPath + "ZxcWork\\DiscreteTools\\data\\";
		String fileName = "result.txt";

		try {
			PrintWriter writer = new PrintWriter(path + fileName);
			
			WeightedGraph graph = new WeightedGraph("Example9_29");
			DefaultGraphNode v1 = new DefaultGraphNode("v1");  graph.addNode(v1);
			DefaultGraphNode v2 = new DefaultGraphNode("v2");  graph.addNode(v2);
			DefaultGraphNode v3 = new DefaultGraphNode("v3");  graph.addNode(v3);
			DefaultGraphNode v4 = new DefaultGraphNode("v4");  graph.addNode(v4);
			DefaultGraphNode v5 = new DefaultGraphNode("v5");  graph.addNode(v5);
			WeightedGraphEdge e1 = new WeightedGraphEdge(v1, v2, 15);  graph.addEdge(e1);
			WeightedGraphEdge e2 = new WeightedGraphEdge(v1, v3, 20);  graph.addEdge(e2);
			WeightedGraphEdge e3 = new WeightedGraphEdge(v1, v4, 40);  graph.addEdge(e3);
			WeightedGraphEdge e4 = new WeightedGraphEdge(v1, v5, 25);  graph.addEdge(e4);
			WeightedGraphEdge e5 = new WeightedGraphEdge(v2, v3, 30);  graph.addEdge(e5);
			WeightedGraphEdge e6 = new WeightedGraphEdge(v2, v4, 20);  graph.addEdge(e6);
			WeightedGraphEdge e7 = new WeightedGraphEdge(v2, v5, 30);  graph.addEdge(e7);
			WeightedGraphEdge e8 = new WeightedGraphEdge(v3, v4, 10);  graph.addEdge(e8);
			WeightedGraphEdge e9 = new WeightedGraphEdge(v3, v5, 15);  graph.addEdge(e9);
			WeightedGraphEdge e10 = new WeightedGraphEdge(v4, v5, 10);  graph.addEdge(e10);
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

	public static void Example9_26() {
		String rootPath = "E:\\";
		String path = rootPath + "ZxcWork\\DiscreteTools\\data\\";
		String fileName = "result.txt";

		try {
			PrintWriter writer = new PrintWriter(path + fileName);
			RootedTreeNode a = new RootedTreeNode("a");
			RootedTreeNode b = new RootedTreeNode("b");
			RootedTreeNode c = new RootedTreeNode("c");
			RootedTreeNode d = new RootedTreeNode("d");
			RootedTreeNode e = new RootedTreeNode("e");
			RootedTreeNode f = new RootedTreeNode("f");
			RootedTreeNode g = new RootedTreeNode("g");
			RootedTreeNode h = new RootedTreeNode("h");
			RootedTreeNode i = new RootedTreeNode("i");
			RootedTreeNode j = new RootedTreeNode("j");
			RootedTreeNode k = new RootedTreeNode("k");
			RootedTreeNode p = new RootedTreeNode("p");
			a.addChildNode(b);  a.addChildNode(j);  a.addChildNode(h);
			b.addChildNode(c);  b.addChildNode(k);  b.addChildNode(e);
			e.addChildNode(d);  e.addChildNode(f);
			h.addChildNode(g);  h.addChildNode(p);  h.addChildNode(i);
			RootedTree tree = new RootedTree("Example9_26", a);
			
			DefaultGraph graph = tree.convertToDefaultGraph(false, false);
			graph.simplyWriteToDotFile(writer);
			
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
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void Example9_20() {
		String rootPath = "E:\\";
		String path = rootPath + "ZxcWork\\DiscreteTools\\data\\";
		String fileName = "result.txt";

		try {
			PrintWriter writer = new PrintWriter(path + fileName);
			
			DefaultGraph graph = new DefaultGraph("Example9_20");
			DefaultGraphNode v1 = new DefaultGraphNode("v1");  graph.addNode(v1);
			DefaultGraphNode v2 = new DefaultGraphNode("v2");  graph.addNode(v2);
			DefaultGraphNode v3 = new DefaultGraphNode("v3");  graph.addNode(v3);
			DefaultGraphNode v4 = new DefaultGraphNode("v4");  graph.addNode(v4);
			DefaultGraphNode v5 = new DefaultGraphNode("v5");  graph.addNode(v5);
			DefaultGraphNode v6 = new DefaultGraphNode("v6");  graph.addNode(v6);
			DefaultGraphNode v7 = new DefaultGraphNode("v7");  graph.addNode(v7);
			DefaultGraphEdge e1 = new DefaultGraphEdge(v1, v2);  graph.addEdge(e1);
			DefaultGraphEdge e2 = new DefaultGraphEdge(v1, v3);  graph.addEdge(e2);
			DefaultGraphEdge e3 = new DefaultGraphEdge(v2, v3);  graph.addEdge(e3);
			DefaultGraphEdge e4 = new DefaultGraphEdge(v3, v7);  graph.addEdge(e4);
			DefaultGraphEdge e5 = new DefaultGraphEdge(v2, v4);  graph.addEdge(e5);
			DefaultGraphEdge e6 = new DefaultGraphEdge(v2, v6);  graph.addEdge(e6);
			DefaultGraphEdge e7 = new DefaultGraphEdge(v4, v5);  graph.addEdge(e7);
			DefaultGraphEdge e8 = new DefaultGraphEdge(v5, v6);  graph.addEdge(e8);
			graph.simplyWriteToDotFile(writer);
			
			List<GraphNode> nodeDFSList = graph.getDFSNodeList();
			System.out.println("DFS node list: ");
			for (GraphNode node : nodeDFSList) System.out.print(node + " ");
			
			List<GraphNode> nodeBFSList = graph.getBFSNodeList();
			System.out.println();
			System.out.println("BFS node list: ");
			for (GraphNode node : nodeBFSList) System.out.print(node + " ");
			System.out.println();
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void Example9_16() {
		String rootPath = "E:\\";
		String path = rootPath + "ZxcWork\\DiscreteTools\\data\\";
		String fileName = "result.txt";

		try {
			PrintWriter writer = new PrintWriter(path + fileName);
			
			DefaultGraph directedGraph = new DefaultGraph("Example9_16_dg");
			DefaultGraphNode v1 = new DefaultGraphNode("v1");  directedGraph.addNode(v1);
			DefaultGraphNode v2 = new DefaultGraphNode("v2");  directedGraph.addNode(v2);
			DefaultGraphNode v3 = new DefaultGraphNode("v3");  directedGraph.addNode(v3);
			DefaultGraphNode v4 = new DefaultGraphNode("v4");  directedGraph.addNode(v4);
			DefaultGraphNode v5 = new DefaultGraphNode("v5");  directedGraph.addNode(v5);
			DefaultGraphEdge e1 = new DefaultGraphEdge(v1, v2, "e1", true);  directedGraph.addEdge(e1);
			DefaultGraphEdge e2 = new DefaultGraphEdge(v3, v1, "e2", true);  directedGraph.addEdge(e2);
			DefaultGraphEdge e3 = new DefaultGraphEdge(v2, v3, "e3", true);  directedGraph.addEdge(e3);
			DefaultGraphEdge e4 = new DefaultGraphEdge(v4, v2, "e4", true);  directedGraph.addEdge(e4);
			DefaultGraphEdge e5 = new DefaultGraphEdge(v4, v1, "e5", true);  directedGraph.addEdge(e5);
			DefaultGraphEdge e6 = new DefaultGraphEdge(v4, v5, "e6", true);  directedGraph.addEdge(e6);
			DefaultGraphEdge e7 = new DefaultGraphEdge(v3, v5, "e7", true);  directedGraph.addEdge(e7);
			DefaultGraphEdge e8 = new DefaultGraphEdge(v5, v3, "e8", true);  directedGraph.addEdge(e8);
			DefaultGraphEdge e9 = new DefaultGraphEdge(v3, v5, "e9", true);  directedGraph.addEdge(e9);
			directedGraph.simplyWriteToDotFile(writer);
			
			DefaultGraph undirectedGraph = new DefaultGraph("Example9_16_ug");
			v1 = new DefaultGraphNode("v1");  undirectedGraph.addNode(v1);
			v2 = new DefaultGraphNode("v2");  undirectedGraph.addNode(v2);
			v3 = new DefaultGraphNode("v3");  undirectedGraph.addNode(v3);
			v4 = new DefaultGraphNode("v4");  undirectedGraph.addNode(v4);
			v5 = new DefaultGraphNode("v5");  undirectedGraph.addNode(v5);
			e1 = new DefaultGraphEdge(v1, v2, "e1");  undirectedGraph.addEdge(e1);
			e2 = new DefaultGraphEdge(v3, v1, "e2");  undirectedGraph.addEdge(e2);
			e3 = new DefaultGraphEdge(v2, v3, "e3");  undirectedGraph.addEdge(e3);
			e4 = new DefaultGraphEdge(v4, v2, "e4");  undirectedGraph.addEdge(e4);
			e5 = new DefaultGraphEdge(v4, v1, "e5");  undirectedGraph.addEdge(e5);
			e6 = new DefaultGraphEdge(v4, v5, "e6");  undirectedGraph.addEdge(e6);
			e7 = new DefaultGraphEdge(v3, v5, "e7");  undirectedGraph.addEdge(e7);
			e8 = new DefaultGraphEdge(v5, v3, "e8");  undirectedGraph.addEdge(e8);
			undirectedGraph.simplyWriteToDotFile(writer);
			
			Matrix dgimatrix = directedGraph.getIncidenceMatrix();
			Matrix dgamatrix = directedGraph.getAdjacencyMatrix();
			Matrix ugimatrix = undirectedGraph.getIncidenceMatrix();
			Matrix ugamatrix = undirectedGraph.getAdjacencyMatrix();
			
			System.out.println("Incidence Matrix of directed graph: ");
			System.out.println(dgimatrix);
			System.out.println("Incidence Matrix of undirected graph: ");
			System.out.println(ugimatrix);
			
			System.out.println("Adjacency Matrix of directed graph: ");
			System.out.println(dgamatrix);
			System.out.println("Adjacency Matrix of undirected graph: ");
			System.out.println(ugamatrix);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
//		Example9_31();
//		Example9_28();
//		Example9_29();
//		Example9_26();
//		Example9_20();
//		Example9_16();
	}

}

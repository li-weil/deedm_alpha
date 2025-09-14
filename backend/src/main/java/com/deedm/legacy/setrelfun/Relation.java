package com.deedm.legacy.setrelfun;

import java.util.ArrayList;
import java.util.List;

import graph.AbstractGraph;
import graph.DefaultGraph;
import graph.DefaultGraphEdge;
import graph.DefaultGraphNode;
import graph.GraphEdge;
import graph.GraphNode;

/**
 * @author zxc
 *
 */
public class Relation {
	private static OrderedPair exampleOne = null;		// 用于解释关系性质为什么不成立的有序对例子一，在调用性质判断的方法之后，马上获取这些例子，可进一步说明关系的性质
	private static OrderedPair exampleTwo = null;       // 用于解释关系性质为什么不成立的有序对例子二，像传递性需要两个有序对例子
	private static Matrix[] closureMatrixes = null; 		// 用于存放计算传递闭包时的中间结果矩阵
	
	protected Set from = null;
	protected Set to = null;
	protected String name = null;
	
	protected List<OrderedPair> pairs = null;
	
	public Relation(Set from, Set to) {
		this.from = from;
		this.to = to;
		pairs = new ArrayList<OrderedPair>();
	}
	
	public Relation(Set set) {
		this.from = set;
		this.to = set;
		pairs = new ArrayList<OrderedPair>();
	}
	
	public Relation(Relation other) {
		this.from = other.from;
		this.to = other.to;
		pairs = new ArrayList<OrderedPair>();
		
		for (OrderedPair pair : other.pairs) pairs.add(pair);
	}
	
	public Set getFromSet() {
		return from;
	}
	
	public Set getToSet() {
		return to;
	}
	
	public boolean setPairs(Matrix matrix) {
		int rows = matrix.getRowNumber();
		int cols = matrix.getColNumber();
		if (from.length() != rows) return false;
		if (to.length() != cols) return false;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (matrix.get(i, j) == 1) {
					pairs.add(new OrderedPair(from.get(i), to.get(j)));
				}
			}
		}
		return true;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Matrix getMatrix() {
		Matrix result = new Matrix(from.length(), to.length());
		
		for (OrderedPair pair : pairs) {
			int row = from.getIndex(pair.getFirst());
			int col = to.getIndex(pair.getSecond());
			
			if (row != -1 && col != -1) result.set(row, col, 1);
		}
		return result;
	}
	
	public boolean addPair(OrderedPair pair) {
		if (from.inSet(pair.getFirst()) != true || to.inSet(pair.getSecond()) != true) return false;
		if (pairs.contains(pair)) return false;
		return pairs.add(pair);
	}
	
	public boolean addPair(List<OrderedPair> pairs) {
		boolean success = true;
		for (OrderedPair pair : pairs) {
			if (!addPair(pair)) success = false;
		}
		return success;
	}
	
	public boolean addPair(OrderedPair[] pairs) {
		boolean success = true;
		for (OrderedPair pair : pairs) {
			if (!addPair(pair)) success = false;
		}
		return success;
	}
	
	public OrderedPair[] getPairs() {
		if (pairs.size() == 0) return null;
		OrderedPair[] result = new OrderedPair[pairs.size()];
		int index = 0;
		for (OrderedPair pair : pairs) {
			result[index] = new OrderedPair(pair.getFirst(), pair.getSecond());
			index = index + 1;
		}
		return result;
	}
	
	public boolean isEmpty() {
		return pairs.size() == 0;
	}
	
	public Relation union(Relation other) {
		if (!from.equalsTo(other.from) || !to.equalsTo(other.to)) return null;
		Relation result = new Relation(from, to);
		result.addPair(pairs);
		result.addPair(other.pairs);
		return result;
	}
	
	public Relation intersection(Relation other) {
		if (!from.equalsTo(other.from) || !to.equalsTo(other.to)) return null;
		Relation result = new Relation(from, to);
		
		for (OrderedPair thisPair : pairs) {
			if (other.pairs.contains(thisPair)) result.addPair(thisPair);
		}
		return result;
	}
	
	public Relation subtract(Relation other) {
		if (!from.equalsTo(other.from) || !to.equalsTo(other.to)) return null;
		Relation result = new Relation(from, to);
		
		for (OrderedPair thisPair : pairs) {
			if (!other.pairs.contains(thisPair)) result.addPair(thisPair);
		}
		return result;
	}
	
	public boolean isSubRelationOf(Relation other) {
		if (!from.equalsTo(other.from) || !to.equalsTo(other.to)) return false;
		for (OrderedPair thisPair : pairs) {
			if (!other.pairs.contains(thisPair)) return false;
		}
		return true;
	}
	
	public boolean isFunction() {
		for (int i = 0; i < from.length(); i++) {
			char element = from.get(i);
			boolean found = false;
			for (OrderedPair pair : pairs) {
				if (pair.getFirst() == element) {
					if (found == false) found = true;
					else return false;
				}
			}
			if (found == false) return false;
		}
		return true;
	}
	
	public Relation inverse() {
		Relation result = new Relation(to, from);
		
		for (OrderedPair pair : pairs) {
			result.addPair(new OrderedPair(pair.getSecond(), pair.getFirst()));
		}
		return result;
	}
	
	/**
	 * @return other \circ this
	 */
	public Relation composite(Relation other) {
		if (!other.from.equalsTo(to)) return null;
		Relation result = new Relation(from, other.to);
		for (OrderedPair pair : pairs) {
			for (OrderedPair otherPair : other.pairs) {
				if (pair.getSecond() == otherPair.getFirst()) {
					result.addPair(new OrderedPair(pair.getFirst(), otherPair.getSecond()));
				}
			}
		}
		return result;
	}
	
	public boolean isReflexive() {
		for (int i = 0; i < from.length(); i++) {
			char element = from.get(i);
			OrderedPair pair = new OrderedPair(element, element);
			if (!pairs.contains(pair)) {
				exampleOne = pair;
				return false; 
			}
		}
		return true;
	}
	
	public boolean isIrreflexive() {
		for (int i = 0; i < from.length(); i++) {
			char element = from.get(i);
			OrderedPair pair = new OrderedPair(element, element);
			if (pairs.contains(pair)) {
				exampleOne = pair;
				return false; 
			}
		}
		return true;
	}
	
	public boolean isSymmetric() {
		for (OrderedPair pair : pairs) {
			char first = pair.getFirst();
			char second = pair.getSecond();
			if (first == second) continue;
			
			boolean found = false;
			for (OrderedPair checkPair : pairs) {
				if (checkPair.getFirst() == second && checkPair.getSecond() == first) {
					found = true;
					break;
				}
			}
			if (!found) {
				exampleOne = pair;
				exampleTwo = new OrderedPair(second, first);
				return false;
			}
		}
		return true;
	}
	
	public boolean isAntisymmetric() {
		for (OrderedPair pair : pairs) {
			char first = pair.getFirst();
			char second = pair.getSecond();
			if (first == second) continue;
			
			for (OrderedPair checkPair : pairs) {
				if (checkPair.getFirst() == second && checkPair.getSecond() == first) {
					exampleOne = pair;
					exampleTwo = checkPair;
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean isTransitive() {
		for (OrderedPair firstPair : pairs) {
			for (OrderedPair secondPair : pairs) {
				if (firstPair.getSecond() == secondPair.getFirst()) {
					char first = firstPair.getFirst();
					char second = secondPair.getSecond();
					boolean found = false;
					for (OrderedPair pair : pairs) {
						if (pair.getFirst() == first && pair.getSecond() == second) {
							found = true;
							break;
						}
					}
					if (found == false) {
						exampleOne = firstPair;
						exampleTwo = secondPair;
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static OrderedPair getExampleOne() {
		return exampleOne;
	}
	
	public static OrderedPair getExampleTwo() {
		return exampleTwo;
	}

	public Relation reflexiveClosure() {
		Relation result = new Relation(from);
		result.pairs.addAll(pairs);
		for (int i = 0; i < from.length(); i++) {
			char element = from.get(i);
			OrderedPair pair = new OrderedPair(element, element);
			if (!pairs.contains(pair)) result.pairs.add(pair); 
		}
		
		return result;
	}

	public Relation symmetricClosure() {
		Relation result = new Relation(from);
		result.pairs.addAll(pairs);
		for (OrderedPair originPair : pairs) {
			OrderedPair pair = new OrderedPair(originPair.getSecond(), originPair.getFirst());
			if (!pairs.contains(pair)) result.pairs.add(pair); 
		}
		
		return result;
	}
	
	public Relation transitiveClosureByComposition() {
		int n = from.length();
		Matrix matrix = this.getMatrix();
		Matrix closureMatrix = new Matrix(matrix);
		Matrix powerMatrix = new Matrix(matrix);
		
		closureMatrixes = new Matrix[n];
		closureMatrixes[0] = new Matrix(powerMatrix);
		for (int k = 2; k <= n; k++) {
			powerMatrix = powerMatrix.logicProduct(matrix);
			closureMatrixes[k-1] = new Matrix(powerMatrix);
			closureMatrix = closureMatrix.logicAdd(powerMatrix);
		}
		Relation result = new Relation(from);
		result.setPairs(closureMatrix);
		return result;
	}

	public Relation transitiveClosureByWarshallAlgorithm() {
		int n = from.length();
		Matrix matrix = this.getMatrix();
		Matrix WMatrix = new Matrix(matrix);

		closureMatrixes = new Matrix[n+1];
		closureMatrixes[0] = new Matrix(WMatrix);
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					int newValue = WMatrix.get(i, j);
					if (newValue == 0 && WMatrix.get(i, k) == 1 && WMatrix.get(k, j) == 1) newValue = 1;
					WMatrix.set(i, j, newValue);
				}
			}
			closureMatrixes[k+1] = new Matrix(WMatrix);
		}
		Relation result = new Relation(from);
		result.setPairs(WMatrix);
		return result;
	}
	
	public static Matrix[] getTransitiveClosureMatrixes() {
		return closureMatrixes;
	}
	
	public AbstractGraph getRelationGraph(boolean isIntElement) {
		DefaultGraphNode startNodes[] = new DefaultGraphNode[from.length()];
		DefaultGraphNode endNodes[] = new DefaultGraphNode[to.length()];
		
		if (from.equalsTo(to)) {
			for (int i = 0; i < from.length(); i++) {
				char idChar = from.get(i);
				String id = Set.getElementLabel(idChar, isIntElement);
				DefaultGraphNode node = new DefaultGraphNode(id);
				startNodes[i] = node;
				endNodes[i] = node;
			}
		} else {
			for (int i = 0; i < from.length(); i++) {
				char idChar = from.get(i);
				String label = Set.getElementLabel(idChar, isIntElement);
				String id = "Start_" + label;
				DefaultGraphNode node = new DefaultGraphNode(id, label);
				startNodes[i] = node;
			}
			for (int i = 0; i < to.length(); i++) {
				char idChar = to.get(i);
				String label = Set.getElementLabel(idChar, isIntElement);
				String id = "End_" + label;
				DefaultGraphNode node = new DefaultGraphNode(id, label);
				endNodes[i] = node;
			}
		}
		
		List<GraphEdge> edgeList = new ArrayList<GraphEdge>();
		for (OrderedPair pair : pairs) {
			int firstIndex = from.getIndex(pair.getFirst());
			int secondIndex = to.getIndex(pair.getSecond());
			DefaultGraphEdge  edge = new DefaultGraphEdge(startNodes[firstIndex], endNodes[secondIndex], null, true);
			edgeList.add(edge);
		}
		List<GraphNode> nodeList = new ArrayList<GraphNode>();
		for (int i = 0; i < from.length(); i++) nodeList.add(startNodes[i]);
		if (!from.equalsTo(to)) {
			for (int i = 0; i < to.length(); i++) nodeList.add(endNodes[i]);
		}
		String graphName = "Relation";
		if (name != null) graphName = name;
		AbstractGraph graph = new DefaultGraph(graphName);
		graph.setNodes(nodeList);
		graph.setEdges(edgeList);
		return graph;
	}
	
	public boolean equalsTo(Relation other) {
		if (!from.equalsTo(other.from) || !to.equalsTo(other.to)) return false;
		
		for (OrderedPair pair : pairs) {
			if (!other.pairs.contains(pair)) return false; 
		}
		for (OrderedPair pair : other.pairs) {
			if (!pairs.contains(pair)) return false;
		}
		return true;
	}
	
	public String toString() {
		String result = "{ ";
		if (name != null) result = name + " = {";
		if (pairs.size() > 0) {
			result = result + pairs.get(0);
			for (int i = 1; i < pairs.size(); i++) result = result + ", " + pairs.get(i); 
		}
		result = result + " }";
		return result;
	}
	
	public String toLaTeXString() {
		if (isEmpty()) return "\\varnothing ";
		String result = "\\{ ";
		if (name != null) result = name + " = \\{";
		if (pairs.size() > 0) {
			result = result + pairs.get(0).toLaTeXString();
			for (int i = 1; i < pairs.size(); i++) result = result + ", " + pairs.get(i).toLaTeXString(); 
		}
		result = result + " \\}";
		return result;
	}
	
	public String toLaTeXString(boolean isIntElement) {
		if (isEmpty()) return "\\varnothing ";
		String result = "\\{ ";
		if (name != null) result = name + " = \\{";
		if (pairs.size() > 0) {
			result = result + pairs.get(0).toLaTeXString(isIntElement);
			for (int i = 1; i < pairs.size(); i++) result = result + ", " + pairs.get(i).toLaTeXString(isIntElement); 
		}
		result = result + " \\}";
		return result;
	}
	
	public static Relation getIdentity(Set from) {
		Relation result = new Relation(from, from);
		for (int i = 0; i < from.length(); i++) {
			char element = from.get(i);
			OrderedPair pair = new OrderedPair(element, element);
			result.addPair(pair);
		}
		return result;
	}
	
	public static Relation getCartesianProduct(Set from, Set to) {
		Relation result = new Relation(from, to);
		for (int i = 0; i < from.length(); i++) {
			char first = from.get(i);
			for (int j = 0; j < to.length(); j++) {
				char second = to.get(j);
				OrderedPair pair = new OrderedPair(first, second);
				result.addPair(pair);
			}
		}
		return result;
	}
	
	public static Relation randomGenerate(Set from, Set to, int maxPairNumber) {
		Relation result = new Relation(from, to);
		
		for (int i = 0; i < maxPairNumber; i++) {
			int firstIndex = (int)(from.length() * Math.random());
			int secondIndex = (int)(to.length() * Math.random());
			
			result.addPair(new OrderedPair(from.get(firstIndex), to.get(secondIndex)));
		}
		return result;
	}

}

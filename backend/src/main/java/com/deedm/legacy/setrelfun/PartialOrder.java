package com.deedm.legacy.setrelfun;

import java.util.ArrayList;
import java.util.List;

import com.deedm.legacy.graph.AbstractGraph;
import com.deedm.legacy.graph.DefaultGraph;
import com.deedm.legacy.graph.DefaultGraphEdge;
import com.deedm.legacy.graph.DefaultGraphNode;
import com.deedm.legacy.graph.GraphEdge;
import com.deedm.legacy.graph.GraphNode;

public class PartialOrder extends Relation {
	public static final int WITHOUT_LUB = 0;
	public static final int WITHOUT_GLB = 1;
	protected static int reasonType = WITHOUT_LUB;
	protected static char exampleOne = 0;
	protected static char exampleTwo = 0;

	public PartialOrder(Set set) {
		super(set);
	}
	
	public PartialOrder(PartialOrder relation) {
		super(relation);
	}

	public PartialOrder(Relation relation) {
		super(relation.from);
		pairs.addAll(relation.pairs);
	}
	
	public static PartialOrder createFactorDivisionOrder(int n) {
		char[] factors = new char[n];
		int counter = 0;
		for (int i = 1; i <= n; i++) {
			if (n % i == 0) {
				factors[counter] = (char)i;
				counter++;
			}
		}
		char[] base = new char[counter];
		for (int i = 0; i < counter; i++) base[i] = factors[i];
		Set baseSet = new Set(base);
		PartialOrder result = new PartialOrder(baseSet);
		for (int i = 0; i < counter; i++) {
			for (int j = 0; j < counter; j++) {
				if (base[j] % base[i] == 0) {
					OrderedPair pair = new OrderedPair(base[i], base[j]);
					result.addPair(pair);
				}
			}
		}
		return result;
	}
	
	public static PartialOrder createDivisionOrder(int[] numbers) {
		char[] base = new char[numbers.length];
		for (int i = 0; i < numbers.length; i++) base[i] = (char)numbers[i];
		Set baseSet = new Set(base);
		PartialOrder result = new PartialOrder(baseSet);
		for (int i = 0; i < numbers.length; i++) {
			for (int j = 0; j < numbers.length; j++) {
				if (base[j] % base[i] == 0) {
					OrderedPair pair = new OrderedPair(base[i], base[j]);
					result.addPair(pair);
				}
			}
		}
		return result;
	}

	public static PartialOrder createDivisionOrder(Set intSet) {
		PartialOrder result = new PartialOrder(intSet);
		for (int i = 0; i < intSet.length(); i++) {
			if (intSet.get(i) == 0) continue;
			for (int j = 0; j < intSet.length(); j++) {
				if (intSet.get(j) % intSet.get(i) == 0) {
					OrderedPair pair = new OrderedPair(intSet.get(i), intSet.get(j));
					result.addPair(pair);
				}
			}
		}
		return result;
	}
	
	public static PartialOrder randomGeneratePartialOrder(Set set) {
		int[] seeds = {1, 2, 3, 4, 6, 8, 12, 15, 18, 21, 24, 30, 32, 36, 40, 45, 48, 54, 60, 64, 72};
		if (set.length() > seeds.length) return null;
		char[] seedsSetElements = new char[seeds.length];
		for (int i = 0; i < seedsSetElements.length; i++) seedsSetElements[i] = (char)seeds[i];
		Set seedsSet = new Set(seedsSetElements);
		Set seedsSubSet = Set.randomGenerateSubset(seedsSet, set.length());
		int[] usedSeeds = new int[seedsSubSet.length()];
		for (int i = 0; i < usedSeeds.length; i++) usedSeeds[i] = (int)seedsSubSet.get(i);
		PartialOrder seedOrder = createDivisionOrder(usedSeeds);
		PartialOrder result = new PartialOrder(set);
		for (OrderedPair pair : seedOrder.pairs) {
			int first = (int)pair.getFirst();
			int second = (int)pair.getSecond();
			char firstChar = 0;
			char secondChar = 0;
			for (int i = 0; i < usedSeeds.length; i++) {
				if (usedSeeds[i] == first) firstChar = set.get(i);
				if (usedSeeds[i] == second) secondChar = set.get(i);
			}
			result.addPair(new OrderedPair(firstChar, secondChar));
		}
		return result;
	}
	
	public boolean isComparable(char elementOne, char elementTwo) {
		for (OrderedPair pair : pairs) {
			if (pair.getFirst() == elementOne && pair.getSecond() == elementTwo) return true;
			if (pair.getFirst() == elementTwo && pair.getSecond() == elementOne) return true;
		}
		return false;
	}
	
	/**
	 * Test if the element covered is covered by the element coveree
	 */
	public boolean isCover(char covered, char coveree) {
		if (covered == coveree) return false;
		
		// This flag array sets coveredFlag[i] to be true if the element in from.get(i) is greater than the element covered.
		boolean[] coveredFlag = new boolean[from.length()];  
		for (int i = 0; i < from.length(); i++) {
			coveredFlag[i] = false;
			char covereeElement = from.get(i);
			if (covered == covereeElement) continue;  // The element itself does not cover itself.
			for (OrderedPair pair : pairs) {
				if (pair.getFirst() == covered && pair.getSecond() == covereeElement) {
					coveredFlag[i] = true;
					break;
				}
			}
			if (covereeElement == coveree && coveredFlag[i] == false) {
//				System.out.println("CovereeElement = " + (int)covereeElement + ", coveree = " + (int)coveree + ", covered flag = " + coveredFlag[i]);
				return false; // The element coveree is not greater than the element covered.
			}
		}
		// The following loop test if there an element which is greater than the element covered and is less than coveree.
		for (int i = 0; i < from.length(); i++) {
			if (coveredFlag[i] == false) continue;
			char covereeElement = from.get(i);
			if (covereeElement == coveree) continue;
			for (OrderedPair pair : pairs) {
				if (pair.getFirst() == covereeElement && pair.getSecond() == coveree) {
//					System.out.println("covered(" + Set.getElementLabel(covered) + ") <= covereeElement(" + Set.getElementLabel(covereeElement) + ") <= coveree(" + Set.getElementLabel(coveree) + ")");
					return false;  // We have covered < covereeElement < coveree!
				}
			}
		}
		return true;
	}
	
	/**
	 * Get all elements which cover the given element. 
	 */
	public char[] getAllCoverElements(char element) {
		char[] result = new char[from.length()];
		int counter = 0;
		boolean[] covered = new boolean[from.length()];
		for (int i = 0; i < from.length(); i++) {
			covered[i] = false;
			char coverElement = from.get(i);
			if (element == coverElement) continue;  // The element itself does not cover itself.
			for (OrderedPair pair : pairs) {
				if (pair.getFirst() == element && pair.getSecond() == coverElement) {
					covered[i] = true;
					break;
				}
			}
		}
		for (int i = 0; i < from.length(); i++) {
			if (covered[i] == false) continue;
			char coverElement = from.get(i);
			boolean hasMiddleElement = false;
			for (int j = 0; j < from.length(); j++) {
				if (i == j || covered[j] == false) continue;
				char otherElement = from.get(j);
				
				for (OrderedPair pair : pairs) {
					if (pair.getFirst() == otherElement && pair.getSecond() == coverElement) {
						hasMiddleElement = true;
						break;
					}
				}
				if (hasMiddleElement == true) break;
			}
			if (hasMiddleElement == false) {
				result[counter] = coverElement;
				counter++;
			}
		}

		if (counter == 0) return null;
		char[] trimedResult = new char[counter];
		for (int i = 0; i < counter; i++) trimedResult[i] = result[i];
		return trimedResult;
	}

	public AbstractGraph getHasseDigram(boolean isIntElement) {
		DefaultGraphNode[] nodes = new DefaultGraphNode[from.length()];
		for (int i = 0; i < from.length(); i++) {
			char idChar = from.get(i);
			String id = Set.getElementLabel(idChar, isIntElement);
			nodes[i] = new DefaultGraphNode(id);
		}
		
		List<GraphEdge> edgeList = new ArrayList<GraphEdge>();
		for (int i = 0; i < from.length(); i++) {
			char first = from.get(i);
			for (int j = 0; j < from.length(); j++) {
				char second = from.get(j);
				if (isCover(first, second)) {
					DefaultGraphEdge edge = new DefaultGraphEdge(nodes[i], nodes[j]);
					edgeList.add(edge);
				}
			}
		}
		List<GraphNode> nodeList = new ArrayList<GraphNode>();
		for (int i = 0; i < from.length(); i++) nodeList.add(nodes[i]);
		
		String graphName = "HasseD";
		if (name != null) graphName = name;
		DefaultGraph graph = new DefaultGraph(graphName);
		graph.setNodes(nodeList);
		graph.setEdges(edgeList);
		return graph;
	}

	/**
	 * Find the maximal elements of a subset of the partial order. If there are maximal elements, then 
	 * all maximal elements will be set into the array maxElements which should be able to store at least
	 * subset.length() characters. This method will return the number of maximal elements. Those maximal 
	 * elements will be set into from maxElements[0] to maxElements[counter-1].   
	 */
	public int getMaximalElement(Set subset, char[] maxElements) {
		int counter = 0;
		for (int i = 0; i < subset.length(); i++) {
			boolean hasGreaterElement = false;
			for (OrderedPair pair : pairs) {
				char element = subset.get(i);
				char first = pair.getFirst();
				char second = pair.getSecond();
				if (first == element && second != element) {
					int index = subset.getIndex(second);
//					System.out.println("\tThe element subset[" + i + "] = " + Set.getElementLabel(element, true) + ", the greater element " + Set.getElementLabel(second, true) + ", at index of subset " + index);

					if (index >= 0 && index < subset.length()) {
						hasGreaterElement = true;
						break;
					}
				}
			}
			if (hasGreaterElement == false) {
				maxElements[counter] = subset.get(i);
				counter = counter + 1;
			}
		}
		return counter;
	}

	/**
	 * Find the minimal elements of a subset of the partial order. If there are minimal elements, then 
	 * all minimal elements will be set into the array minElements which should be able to store at least
	 * subset.length() characters. This method will return the number of minimal elements. Those minimal 
	 * elements will be set into from minElements[0] to minElements[counter-1].   
	 */
	public int getMinimalElement(Set subset, char[] minElements) {
		int counter = 0;
		for (int i = 0; i < subset.length(); i++) {
			boolean hasGreaterElement = false;
			for (OrderedPair pair : pairs) {
				char element = subset.get(i);
				char first = pair.getFirst();
				char second = pair.getSecond();
				if (second == element && first != element) {
					int index = subset.getIndex(first);
					if (index >= 0 && index < subset.length()) {
						hasGreaterElement = true;
						break;
					}
				}
			}
			if (hasGreaterElement == false) {
				minElements[counter] = subset.get(i);
				counter = counter + 1;
			}
		}
		return counter;
	}
	
	public boolean hasGreatestElement(Set subset) {
		boolean[] comparable = new boolean[subset.length()];  // comparable[i] show that if the current element is greater than subset.get(i)
		for (int i = 0; i < subset.length(); i++) {
			for (int j = 0; j < comparable.length; j++) comparable[j] = false;
			for (OrderedPair pair : pairs) {
				if (pair.getSecond() == subset.get(i)) {
					int index = subset.getIndex(pair.getFirst());
					if (index >= 0 && index < subset.length()) comparable[index] = true;
				}
			}
			boolean isGreatestElement = true;
			for (int j = 0; j < comparable.length; j++) {
				if (comparable[j] == false) {
					isGreatestElement = false;
					break;
				}
			}
			if (isGreatestElement) return true; 
		}
		return false;
	}
	
	public char getGreatestElement(Set subset) {
		boolean[] comparable = new boolean[subset.length()];  // comparable[i] show that if the current element is greater than subset.get(i)
		for (int i = 0; i < subset.length(); i++) {
			for (int j = 0; j < comparable.length; j++) comparable[j] = false;
			for (OrderedPair pair : pairs) {
				if (pair.getSecond() == subset.get(i)) {
					int index = subset.getIndex(pair.getFirst());
					if (index >= 0 && index < subset.length()) comparable[index] = true;
				}
			}
			boolean isGreatestElement = true;
			for (int j = 0; j < comparable.length; j++) {
				if (comparable[j] == false) {
					isGreatestElement = false;
					break;
				}
			}
			if (isGreatestElement) return subset.get(i); 
		}
		return 0;
	}
	
	public boolean hasLeastElement(Set subset) {
		boolean[] comparable = new boolean[subset.length()];  // comparable[i] show that if the current element is less than subset.get(i)
		for (int i = 0; i < subset.length(); i++) {
			for (int j = 0; j < comparable.length; j++) comparable[j] = false;
			for (OrderedPair pair : pairs) {
				if (pair.getFirst() == subset.get(i)) {
					int index = subset.getIndex(pair.getSecond());
					if (index >= 0 && index < subset.length()) comparable[index] = true;
				}
			}
			boolean isGreatestElement = true;
			for (int j = 0; j < comparable.length; j++) {
				if (comparable[j] == false) {
					isGreatestElement = false;
					break;
				}
			}
			if (isGreatestElement) return true; 
		}
		return false;
	}
	
	public char getLeastElement(Set subset) {
		boolean[] comparable = new boolean[subset.length()];  // comparable[i] show that if the current element is greater than subset.get(i)
		for (int i = 0; i < subset.length(); i++) {
			for (int j = 0; j < comparable.length; j++) comparable[j] = false;
			for (OrderedPair pair : pairs) {
				if (pair.getFirst() == subset.get(i)) {
					int index = subset.getIndex(pair.getSecond());
					if (index >= 0 && index < subset.length()) comparable[index] = true;
				}
			}
			boolean isGreatestElement = true;
			for (int j = 0; j < comparable.length; j++) {
				if (comparable[j] == false) {
					isGreatestElement = false;
					break;
				}
			}
			if (isGreatestElement) return subset.get(i); 
		}
		return 0;
	}
	
	/**
	 * Find the lower bound of a subset of the partial order. If there are lower bounds, then 
	 * all lower bound will be set into the array lwerBounds which should be able to store at least
	 * from.length() characters. This method will return the number of lower bounds. Those lower bounds 
	 * will be set into from lowerBounds[0] to lowerBounds[counter-1].   
	 */
	public int getLowerBound(Set subset, char[] lowerBounds) {
		boolean[] comparable = new boolean[subset.length()];
		int counter = 0;
		for (int i = 0; i < from.length(); i++) {
			for (int j = 0; j < comparable.length; j++) comparable[j] = false;
			for (OrderedPair pair : pairs) {
				if (pair.getFirst() == from.get(i)) {
					int index = subset.getIndex(pair.getSecond());
					if (index >= 0 && index < subset.length()) comparable[index] = true;
				}
			}
			boolean isLowerBound = true;
			for (int j = 0; j < comparable.length; j++) {
				if (comparable[j] == false) {
					isLowerBound = false;
					break;
				}
			}
			if (isLowerBound) {
				lowerBounds[counter] = from.get(i);
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Find the upper bound of a subset of the partial order. If there are upper bounds, then 
	 * all upper bound will be set into the array lwerBounds which should be able to store at least
	 * from.length() characters. This method will return the number of upper bounds. Those upper bounds 
	 * will be set into from upperBounds[0] to upperBounds[counter-1].   
	 */
	public int getUpperBound(Set subset, char[] upperBounds) {
		boolean[] comparable = new boolean[subset.length()];
		int counter = 0;
		for (int i = 0; i < from.length(); i++) {
			for (int j = 0; j < comparable.length; j++) comparable[j] = false;
			for (OrderedPair pair : pairs) {
				if (pair.getSecond() == from.get(i)) {
					int index = subset.getIndex(pair.getFirst());
					if (index >= 0 && index < subset.length()) comparable[index] = true;
				}
			}
			boolean isUpperBound = true;
			for (int j = 0; j < comparable.length; j++) {
				if (comparable[j] == false) {
					isUpperBound = false;
					break;
				}
			}
			if (isUpperBound) {
				upperBounds[counter] = from.get(i);
				counter++;
			}
		}
		return counter;
	}
	
	public boolean hasLeastUpperBound(Set subset) {
		char[] upperBounds = new char[from.length()];
		int counter = getUpperBound(subset, upperBounds);
		if (counter == 0) return false;
		char[] trimedUpperBounds = new char[counter];
		for (int i = 0; i < counter; i++) trimedUpperBounds[i] = upperBounds[i];
		Set upperBoundSet = new Set(trimedUpperBounds);
		return hasLeastElement(upperBoundSet);
	}
	
	public char getLeastUpperBound(Set subset) {
		char[] upperBounds = new char[from.length()];
		int counter = getUpperBound(subset, upperBounds);
		if (counter == 0) return 0;
		char[] trimedUpperBounds = new char[counter];
		for (int i = 0; i < counter; i++) trimedUpperBounds[i] = upperBounds[i];
		Set upperBoundSet = new Set(trimedUpperBounds);
		return getLeastElement(upperBoundSet);
	}
	
	public boolean hasGreatestLowerBound(Set subset) {
		char[] lowerBounds = new char[from.length()];
		int counter = getLowerBound(subset, lowerBounds);
		if (counter == 0) return false;
		char[] trimedLowerBounds = new char[counter];
		for (int i = 0; i < counter; i++) trimedLowerBounds[i] = lowerBounds[i];
		Set lowerBoundSet = new Set(trimedLowerBounds);
		return hasGreatestElement(lowerBoundSet);
	}
	
	public char getGreatestLowerBound(Set subset) {
		char[] lowerBounds = new char[from.length()];
		int counter = getLowerBound(subset, lowerBounds);
		if (counter == 0) return 0;
		char[] trimedLowerBounds = new char[counter];
		for (int i = 0; i < counter; i++) trimedLowerBounds[i] = lowerBounds[i];
		Set lowerBoundSet = new Set(trimedLowerBounds);
		return getGreatestElement(lowerBoundSet);
	}
	
	public boolean hasGreatestLowerBound(char one, char two) {
		if (one == two) return true;
		char[] subset = new char[2];
		subset[0] = one;
		subset[1] = two;
		Set elementSet = new Set(subset);
		return hasGreatestLowerBound(elementSet);
	}

	public char getGreatestLowerBound(char one, char two) {
		if (one == two) return one;
		char[] subset = new char[2];
		subset[0] = one;
		subset[1] = two;
		Set elementSet = new Set(subset);
		return getGreatestLowerBound(elementSet);
	}
	
	public boolean hasLeastUpperBound(char one, char two) {
		if (one == two) return true;
		char[] subset = new char[2];
		subset[0] = one;
		subset[1] = two;
		Set elementSet = new Set(subset);
		return hasLeastUpperBound(elementSet);
	}
	
	public char getLeastUpperBound(char one, char two) {
		if (one == two) return one;
		char[] subset = new char[2];
		subset[0] = one;
		subset[1] = two;
		Set elementSet = new Set(subset);
		return getLeastUpperBound(elementSet);
	}
	
	public boolean isLattice() {
		for (int i = 0; i < from.length(); i++) {
			char one = from.get(i);
			for (int j = i+1; j < from.length(); j++) {
				char two = from.get(j);
				if (!hasGreatestLowerBound(one, two)) {
					exampleOne = one;
					exampleTwo = two;
					reasonType = WITHOUT_GLB;
					return false;
				}
				if (!hasLeastUpperBound(one, two)) {
					exampleOne = one;
					exampleTwo = two;
					reasonType = WITHOUT_LUB;
					return false;
				}
			}
		}
		return true;
	}
	
	public static char getElementOne() {
		return exampleOne;
	}
	
	public static char getElementTwo() {
		return exampleTwo;
	}
	
	public static int getReasonType() {
		return reasonType;
	}
}

/**
 * 
 */
package com.deedm.legacy.graph;

/**
 * @author user
 *
 */
public class WeightedTreeNode extends RootedTreeNode {
	double weight = 0;
	
	public WeightedTreeNode(String id, double weight) {
		super(id);
		this.weight = weight;
	}

	public WeightedTreeNode(String id, String label, double weight) {
		super(id, label);
		this.weight = weight;
	}

	public WeightedTreeNode(String id, String label, String description, double weight) {
		super(id, label, description);
		this.weight = weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public double getWeight() {
		return weight;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getImageFileName() {
		return description+"[" + weight + "]";
	}
	
}

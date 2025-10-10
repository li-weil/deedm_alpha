/**
 * 
 */
package com.deedm.legacy.proplogic.formula.ASTGraph;

import com.deedm.legacy.graph.GraphNode;

/**
 * @author zxc
 *
 */
public class FormulaASTNode implements GraphNode {
	private String id = "";
	private char operator = 'p';
	private String imageFileName = "";
	private boolean isVariable = false;
	
	public FormulaASTNode(String id, char operator, boolean isVariable) {
		this.id = id;
		this.operator = operator;
		this.isVariable = isVariable;
	}

	public FormulaASTNode(String id, char operator, String imageFileName, boolean isVariable) {
		this.id = id;
		this.operator = operator;
		this.imageFileName = imageFileName;
		this.isVariable = isVariable;
	}

	public boolean isVariable() {
		return isVariable;
	}
	
	/* (non-Javadoc)
	 * @see basic.GraphNode#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see basic.GraphNode#getLabel()
	 */
	@Override
	public String getLabel() {
		return "" + operator;
	}

	/* (non-Javadoc)
	 * @see basic.GraphNode#getDescription()
	 */
	@Override
	public String getImageFileName() {
		return imageFileName;
	}

	/* (non-Javadoc)
	 * @see basic.GraphNode#toFullString()
	 */
	@Override
	public String toFullString() {
		return id + ":" + operator;
	}
}

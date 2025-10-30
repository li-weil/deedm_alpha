/**
 * 
 */
package com.deedm.legacy.proplogic.formula.ASTGraph;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.deedm.legacy.graph.AbstractGraph;
import com.deedm.legacy.graph.GraphEdge;
import com.deedm.legacy.graph.GraphNode;
import com.deedm.legacy.proplogic.formula.Formula;
import com.deedm.legacy.proplogic.formula.Symbol;

/**
 * @author zxc
 *
 */
public class FormulaASTGraph extends AbstractGraph {
	static int AST_NODE_COUNTER = 0;

	private String formula = null;
	
	public FormulaASTGraph(String formula) {
		this.formula = formula;
	}

	@Override
	public String getId() {
		return formula;
	}

	public void simplyWriteToDotFile(PrintWriter output) throws IOException {
		if (nodes == null) return;
		
		String id = getLegalToken(getId());
		output.println("graph " + id + " {");
		for (GraphNode node : nodes) {
			String label = node.getLabel();
			id = "node" + getLegalToken(node.getId());
			String nodeShape = "ellipse";
			FormulaASTNode astNode = (FormulaASTNode)node;
			if (astNode.isVariable()) {
				nodeShape = "box";
				output.println("    " + id + "[label = \"" + label + "\", shape = " + nodeShape + "]");
			} else {
				String imageFileName = astNode.getImageFileName();
				File file = new File(imageFileName);
				if (file.exists()) {
					output.println("    " + id + "[label = \"\", image = \"" + imageFileName + "\", shape = " + nodeShape + "]");
				} else {
					output.println("    " + id + "[label = \"" + label + "\", shape = " + nodeShape + "]");
				}
			}
		}
		
		if (edges != null) {
			for (GraphEdge edge : edges) {
				String label = edge.getLabel();
				String startNodeId = "node" + getLegalToken(edge.getStartNode().getId());
				String endNodeId = "node" + getLegalToken(edge.getEndNode().getId());
				
				if (label != null) {
					output.println("    " + startNodeId + "--" + endNodeId + "[label = \"" + label + "\"]");
				} else {
					output.println("    " + startNodeId + "--" + endNodeId);
				}
			}
		}

		output.println("}");
		output.flush();
	}

	private static String getASTNodeId() {
		String id = "ASTNODE" + AST_NODE_COUNTER;
		AST_NODE_COUNTER++;
		return id;
	}

	public static FormulaASTGraph createASTGraph(Formula formula, String id) {
		FormulaASTGraph result = new FormulaASTGraph(id);
	
		AST_NODE_COUNTER = 0;
		createASTNode(formula, result);
		return result;
	}

	private static FormulaASTNode createASTNode(Formula formula, FormulaASTGraph resultGraph) {
		FormulaASTNode root = null;
		
		if (formula.isNegFormula()) {
			FormulaASTNode leftNode = createASTNode(formula.getRight(), resultGraph);
			root = new FormulaASTNode(getASTNodeId(), Symbol.OPERATOR_NOT, Symbol.getOperatorImageFileName(Symbol.OPERATOR_NOT), false);
			resultGraph.addNode(root);
			FormulaASTEdge edge = new FormulaASTEdge(root, leftNode); 
			resultGraph.addEdge(edge);
		} else if (formula.isAtomicFormula()) {
			root = new FormulaASTNode(getASTNodeId(), formula.getOperator(), true);
			resultGraph.addNode(root);
		} else {
			FormulaASTNode leftNode = createASTNode(formula.getLeft(), resultGraph);
			FormulaASTNode rightNode = createASTNode(formula.getRight(), resultGraph);
			root = new FormulaASTNode(getASTNodeId(), formula.getOperator(), Symbol.getOperatorImageFileName(formula.getOperator()), false);
			resultGraph.addNode(root);
			FormulaASTEdge edge = new FormulaASTEdge(root, leftNode); 
			resultGraph.addEdge(edge);
			edge = new FormulaASTEdge(root, rightNode);
			resultGraph.addEdge(edge);
		}
		return root;
	}
	
}

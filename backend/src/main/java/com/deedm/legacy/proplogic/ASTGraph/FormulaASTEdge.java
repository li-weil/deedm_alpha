package proplogic.formula.ASTGraph;

import graph.GraphEdge;
import graph.GraphNode;

public class FormulaASTEdge implements GraphEdge {
	private FormulaASTNode start = null;
	private FormulaASTNode end = null;
	
	public FormulaASTEdge(FormulaASTNode start, FormulaASTNode end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public boolean isDirected() {
		return true;
	}

	@Override
	public GraphNode getStartNode() {
		return start;
	}

	@Override
	public GraphNode getEndNode() {
		// TODO Auto-generated method stub
		return end;
	}

	@Override
	public String getLabel() {
		return "";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String toFullString() {
		return "";
	}

	@Override
	public String toLaTeXString() {
		return "";
	}
}

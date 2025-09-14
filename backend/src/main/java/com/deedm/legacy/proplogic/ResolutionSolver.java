/**
 * 
 */
package proplogic.normalFormula;

import java.util.ArrayList;
import java.util.List;

/**
 * @author user
 *
 */
public class ResolutionSolver {
	private List<ResolventAndLayer> resultList = null;
	private List<SimpleDisjunctiveFormula> resultClauseList = null;
	
	public ResolutionSolver() {
		resultList = new ArrayList<ResolventAndLayer>();
		resultClauseList = new ArrayList<SimpleDisjunctiveFormula>();
	}

	public boolean tryResolution(ConjunctiveNormalFormula formula, boolean tryAll) {
		if (formula.isTautology()) return false;
		
		for (SimpleDisjunctiveFormula disjform : formula.disjformList) {
			if (disjform.isContradiction()) return true;
			if (disjform.isTautology() == false) {
				resultClauseList.add(disjform.simplify());
			}
		}
		if (resultClauseList.size() < 2) return false;
		
		boolean hasEmpty = false;
		List<Literal> avoidOneList = new ArrayList<Literal>();
		List<Literal> avoidTwoList = new ArrayList<Literal>();
		int clauseOneIndex = 0;
		int clauseTwoIndex = 1;
		int layerEndIndex = resultClauseList.size();
		int layer = 1;
		SimpleDisjunctiveFormula clauseOne = resultClauseList.get(clauseOneIndex);
		SimpleDisjunctiveFormula clauseTwo = resultClauseList.get(clauseTwoIndex);
		while (clauseTwoIndex < resultClauseList.size()) {
			Resolvent result = Resolvent.tryResolution(clauseOne, clauseTwo, avoidOneList, avoidTwoList);
			if (result != null) {
				resultList.add(new ResolventAndLayer(result, layer));
				if (result.resultClause.isContradiction()) {
					if (!tryAll) return true;
					hasEmpty = true;
				}
				boolean found = false;
				for (SimpleDisjunctiveFormula clause : resultClauseList) {
					if (clause.equals(result.resultClause)) {
						found = true;
						break;
					}
				}
				if (found == false) {
					resultClauseList.add(result.resultClause); 
				}
				avoidOneList.add(result.removedOne);
				avoidTwoList.add(result.removedTwo);
			} else {
				clauseOneIndex = clauseOneIndex + 1;
				if (clauseOneIndex >= clauseTwoIndex) {
					clauseTwoIndex = clauseTwoIndex + 1;
					if (clauseTwoIndex < resultClauseList.size()) {
						clauseOneIndex = 0;
						avoidOneList.clear();
						avoidTwoList.clear();
						clauseOne = resultClauseList.get(clauseOneIndex);
						clauseTwo = resultClauseList.get(clauseTwoIndex);
					}
					if (clauseTwoIndex >= layerEndIndex) {
						layerEndIndex = resultClauseList.size();
						layer++;
					}
				} else {
					avoidOneList.clear();
					avoidTwoList.clear();
					clauseOne = resultClauseList.get(clauseOneIndex);
				}
			}
		}
		
		return hasEmpty;
	}
	
	public String resolventString(String prefix) {
		if (resultList == null) return prefix + "{ }";
		if (resultList.size() == 0) return prefix + "{ }";
		StringBuffer result = new StringBuffer();
		result.append(prefix + "{ \n");
		for (ResolventAndLayer resultResolvent : resultList) {
			result.append(prefix + resultResolvent.layer + ":" + resultResolvent.resolvent.toString() + "\n");
		}
		result.append(prefix + " }\n");
		return result.toString();
	}

	public String resolventLaTeXString(String prefix) {
		if (resultList == null) return prefix + "{ }";
		if (resultList.size() == 0) return prefix + "{ }";
		StringBuffer result = new StringBuffer();
		result.append(prefix + "{ \n");
		for (ResolventAndLayer resultResolvent : resultList) {
			result.append(prefix + resultResolvent.layer + ":" + resultResolvent.resolvent.toLaTeXString() + "\n");
		}
		result.append(prefix + "}");
		return result.toString();
	}
	
	public String resultClausesString() {
		if (resultClauseList == null) return "{ }";
		if (resultClauseList.size() == 0) return "{ }";
		StringBuffer result = new StringBuffer();
		result.append("{ ");
		boolean first = true;
		for (SimpleDisjunctiveFormula clause : resultClauseList) {
			String clauseString = null;
			if (clause.isContradiction()) clauseString = "0";
			else {
				clause.sortLiterals();
				clauseString = clause.toFormula().toString();
			}
			if (first) {
				first = false;
				result.append(clauseString);
			} else result.append(", " + clauseString);
		}
		result.append(" }");
		return result.toString();
	}

	public String resultClausesLaTeXString() {
		if (resultClauseList == null) return "{ }";
		if (resultClauseList.size() == 0) return "{ }";
		StringBuffer result = new StringBuffer();
		result.append("{ ");
		boolean first = true;
		for (SimpleDisjunctiveFormula clause : resultClauseList) {
			String clauseString = null;
			if (clause.isContradiction()) clauseString = "0";
			else {
				clause.sortLiterals();
				clauseString = clause.toFormula().toSimpleLaTeXString();
			}
			if (first) {
				first = false;
				result.append(clauseString);
			} else result.append(", " + clauseString);
		}
		result.append(" }");
		return result.toString();
	}
}

class ResolventAndLayer {
	Resolvent resolvent = null;
	int layer = 1;
	
	public ResolventAndLayer(Resolvent resolvent, int layer) {
		this.resolvent = resolvent;
		this.layer = layer;
	}
}

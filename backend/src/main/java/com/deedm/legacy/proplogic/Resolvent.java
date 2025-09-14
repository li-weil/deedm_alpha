/**
 * 
 */
package proplogic.normalFormula;

import java.util.List;

/**
 * @author zxc
 * A resolvent is a clause (simple disjunctive formula) which is obtained from two clause by using the resolution rule. 
 */
public class Resolvent {
	// The resultClause is the resolvent of clauseOne and clauseTwo. These three objects can not be null!
	SimpleDisjunctiveFormula resultClause = null;  
	SimpleDisjunctiveFormula clauseOne = null;
	SimpleDisjunctiveFormula clauseTwo = null;
	// Because we may use the same two clause to do different resolution, we need record the removed literal.
	Literal removedOne = null;			// The literal removed from clauseOne. 
	Literal removedTwo = null;			// The literal removed from clauseTwo
	
	public Resolvent(SimpleDisjunctiveFormula result, SimpleDisjunctiveFormula one, SimpleDisjunctiveFormula two) {
		resultClause = result;
		clauseOne = one;
		clauseTwo = two;
	}

	public SimpleDisjunctiveFormula getResultClause() {
		return resultClause;
	}

	public SimpleDisjunctiveFormula getClauseOne() {
		return clauseOne;
	}

	public SimpleDisjunctiveFormula getClauseTwo() {
		return clauseTwo;
	}
	
	public Literal getRemovedOne() {
		return removedOne;
	}

	public Literal getRemovedTwo() {
		return removedTwo;
	}

	public boolean hasEqualResult(Resolvent other) {
		return resultClause.equals(other.resultClause);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = resultClause.hashCode();

		result = prime * result + clauseOne.hashCode();
		result = prime * result + clauseTwo.hashCode();
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;

		Resolvent other = (Resolvent) obj;
		if (!resultClause.equals(other.resultClause)) return false;

		if (clauseOne.equals(other.clauseOne)) {
			return clauseTwo.equals(other.clauseTwo);
		} else if (clauseOne.equals(other.clauseTwo)) {
			return clauseTwo.equals(other.clauseOne);
		} 
		return false;
	}

	@Override
	public String toString() {
		return "[" + clauseOne.toSetString() + ", " + clauseTwo.toSetString() + "] ==> " + resultClause.toSetString();
	}
	
	public String toLaTeXString() {
		return "[" + clauseOne.toSetLaTeXString() + ", " + clauseTwo.toSetLaTeXString() + "] ==> " + resultClause.toSetLaTeXString();
	}

	public static Resolvent tryResolution(SimpleDisjunctiveFormula one, SimpleDisjunctiveFormula two) {
		if (one.isContradiction() || two.isContradiction()) return null;
		
		for (Literal literalOne : one.literalList) {
			for (Literal literalTwo : two.literalList) {
				if (literalOne.isNegativeTo(literalTwo)) {
					SimpleDisjunctiveFormula resultClause = new SimpleDisjunctiveFormula();
					for (Literal literal : one.literalList) {
						if (!literal.equals(literalOne)) resultClause.addLiteral(literal);
					}
					for (Literal literal : two.literalList) {
						if (!literal.equals(literalTwo)) resultClause.addLiteral(literal);
					}
					Resolvent result = new Resolvent(resultClause, one, two);
					result.removedOne = literalOne;
					result.removedTwo = literalTwo;
					return result;
				}
			}
		}
		return null;
	}
	
	public static Resolvent tryResolution(SimpleDisjunctiveFormula one, SimpleDisjunctiveFormula two, List<Literal> avoidOne, List<Literal> avoidTwo) {
		if (one.isContradiction() || two.isContradiction()) return null;
		
		for (Literal literalOne : one.literalList) {
			if (avoidOne != null) {
				if (avoidOne.contains(literalOne)) continue;
			}
			for (Literal literalTwo : two.literalList) {
				if (avoidTwo != null) {
					if (avoidTwo.contains(literalTwo)) continue;
				}
				
				if (literalOne.isNegativeTo(literalTwo)) {
					SimpleDisjunctiveFormula resultClause = new SimpleDisjunctiveFormula();
					for (Literal literal : one.literalList) {
						if (!literal.equals(literalOne)) resultClause.addLiteral(literal);
					}
					for (Literal literal : two.literalList) {
						if (!literal.equals(literalTwo)) resultClause.addLiteral(literal);
					}
					Resolvent result = new Resolvent(resultClause, one, two);
					result.removedOne = literalOne;
					result.removedTwo = literalTwo;
					return result;
				}
			}
		}
		return null;
	}
}

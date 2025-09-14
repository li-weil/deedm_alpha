package com.deedm.legacy.algebra;

import java.util.ArrayList;
import java.util.List;

public class SVModPoly {
	public final static SVModPoly NA = new SVModPoly();
	
	private int m = 2;
	private String var = "x";
	private List<ModNumber> coeff = null;
	
	public SVModPoly(int m, int n) {
		this.m = m;
		
		this.coeff = new ArrayList<ModNumber>();
		this.coeff.add(new ModNumber(m, n));
		return; 
	}

	/**
	 * @param m 模
	 * @param var 变元名，通常就是x
	 * @param coeffNum 多项式系数数组，长度必须大于等于1，coeffNum[0]是最高次项，如果为0则其他系数都默认为0.
	 */
	public SVModPoly(int m, String var, int[] coeffNum) {
		this.m = m;
		this.var = var;
		
		this.coeff = new ArrayList<ModNumber>();
		if (coeffNum[0] == 0) {
			this.coeff.add(new ModNumber(m, 0));
			return; 
		}
		for (int i = coeffNum.length-1; i >=0; i--) {
			ModNumber number = new ModNumber(m, coeffNum[i]);
			this.coeff.add(number);
		}
	}

	public SVModPoly(int m, String var, List<ModNumber> coeffList) {
		this.m = m;
		this.var = var;
		this.coeff = coeffList;
	}

	public SVModPoly(int m, int[] coeffNum) {
		this.m = m;
		
		this.coeff = new ArrayList<ModNumber>();
		if (coeffNum[0] == 0) {
			this.coeff.add(new ModNumber(m, 0));
			return; 
		}
		for (int i = coeffNum.length-1; i >=0; i--) {
			ModNumber number = new ModNumber(m, coeffNum[i]);
			this.coeff.add(number);
		}
	}
	
	private SVModPoly() {
		
	}
	
	public SVModPoly(SVModPoly other) {
		this.m = other.m;
		this.var = other.var;
		
		this.coeff = new ArrayList<ModNumber>();
		for (int i = 0; i < other.coeff.size(); i++) {
			this.coeff.add(other.coeff.get(i));
		}
	}
	
	public int getMod() { return m; }
	public String getVariable() { return var; }
	public List<ModNumber> getCoefficent() { return coeff; }
	public int getMostPower() { return coeff.size(); }

	public boolean isZero() {
		if (coeff.size() > 1) return false;
		return coeff.get(0).isZero();
	}
	
	public boolean isUnit() {
		if (coeff.size() > 1) return false;
		return coeff.get(0).isUnit();
	}

	public SVModPoly add(SVModPoly other) {
		int size = coeff.size();
		if (size < other.coeff.size()) size = other.coeff.size();
		
		int[] resultCoeff = new int[size];
		int i = 0;
		while (i < coeff.size() && i < other.coeff.size()) {
			ModNumber result = coeff.get(i).add(other.coeff.get(i));
			resultCoeff[i] = result.getValue();
			i++;
		}
		while (i < coeff.size()) {
			ModNumber result = coeff.get(i);
			resultCoeff[i] = result.getValue();
			i++;
		}
		while (i < other.coeff.size()) {
			ModNumber result = other.coeff.get(i);
			resultCoeff[i] = result.getValue();
			i++;
		}
		int lastNonZero = resultCoeff.length-1;
		while (lastNonZero >= 0) {
			if (resultCoeff[lastNonZero] != 0) break;
			lastNonZero--;
		}
		if (lastNonZero < 0) lastNonZero = 0;
		List<ModNumber> resultList = new ArrayList<ModNumber>();
		for (i = 0; i <= lastNonZero; i++) {
			ModNumber result = new ModNumber(m, resultCoeff[i]);
			resultList.add(result);
		}
		return new SVModPoly(this.m, this.var, resultList);
	}

	public SVModPoly times(SVModPoly other) {
		int[] resultCoeff = new int[coeff.size() + other.coeff.size()];
		for (int i = 0; i < resultCoeff.length; i++) resultCoeff[i] = 0;
		
		for (int i = 0; i < coeff.size(); i++) {
			for (int j = 0; j < other.coeff.size(); j++) {
				ModNumber result = coeff.get(i).times(other.coeff.get(j));
				resultCoeff[i+j] = resultCoeff[i+j] + result.getValue();
			}
		}
		int lastNonZero = resultCoeff.length-1;
		while (lastNonZero >= 0) {
			if (resultCoeff[lastNonZero] != 0) break;
			lastNonZero--;
		}
		if (lastNonZero < 0) lastNonZero = 0;
		List<ModNumber> resultList = new ArrayList<ModNumber>();
		for (int i = 0; i <= lastNonZero; i++) {
			ModNumber result = new ModNumber(m, resultCoeff[i]);
			resultList.add(result);
		}
		return new SVModPoly(this.m, this.var, resultList);
	}
	
	public SVModPoly minus(SVModPoly other) {
		int size = coeff.size();
		if (size < other.coeff.size()) size = other.coeff.size();
		
		int[] resultCoeff = new int[size];
		int i = 0;
		while (i < coeff.size() && i < other.coeff.size()) {
			ModNumber result = coeff.get(i).minus(other.coeff.get(i));
			resultCoeff[i] = result.getValue();
			i++;
		}
		while (i < coeff.size()) {
			ModNumber result = coeff.get(i);
			resultCoeff[i] = result.getValue();
			i++;
		}
		while (i < other.coeff.size()) {
			ModNumber result = other.coeff.get(i);
			resultCoeff[i] = -result.getValue();
			i++;
		}
		int lastNonZero = resultCoeff.length-1;
		while (lastNonZero >= 0) {
			if (resultCoeff[lastNonZero] != 0) break;
			lastNonZero--;
		}
		if (lastNonZero < 0) lastNonZero = 0;
		List<ModNumber> resultList = new ArrayList<ModNumber>();
		for (i = 0; i <= lastNonZero; i++) {
			ModNumber result = new ModNumber(m, resultCoeff[i]);
			resultList.add(result);
		}
		return new SVModPoly(this.m, this.var, resultList);
	}
	
	public SVModPoly mod(SVModPoly other) {
		if (other == NA) return NA;
		if (other.isZero() || other.isUnit()) return NA;
		
		int otherSize = other.coeff.size();
		ModNumber otherHighestCoeff = other.coeff.get(otherSize-1);
		if (!otherHighestCoeff.hasInverse()) return NA;
		ModNumber inverse = otherHighestCoeff.inverse();
			
		SVModPoly result = new SVModPoly(this);
		int resultSize = result.coeff.size();
		while (resultSize >= otherSize && resultSize > 1) {
			ModNumber resHighestCoeff = result.coeff.get(resultSize-1);
			ModNumber quoHighestTimes = resHighestCoeff.times(inverse);
			
			int powerDiff = resultSize-otherSize;
			List<ModNumber> divisionCoeffList = new ArrayList<ModNumber>();
			for (int i = 0; i < powerDiff; i++) divisionCoeffList.add(new ModNumber(m, 0));
			for (int i = 0; i < otherSize; i++) {
				ModNumber divisionCoeff = other.coeff.get(i).times(quoHighestTimes);
				divisionCoeffList.add(divisionCoeff);
			}
			SVModPoly divisionModPoly = new SVModPoly(m, var, divisionCoeffList);
			
//			System.out.println("result = " + result + ", other = " + other + ", division = " + divisionModPoly + ", quo " + quoHighestTimes);
			result = result.minus(divisionModPoly);
			resultSize = result.coeff.size();
//			System.out.println("minus result = " + result + ", size = " + resultSize + ", other size = " + otherSize);
		}
		
		return result;
	}	
	
	public boolean isFactor(SVModPoly other) {
		if (other.isZero()) return true;
		if (this.isZero()) return false;
		if (this.isUnit()) return true;
		
		SVModPoly result = other.mod(this);
		return result.isZero();
	}
	
	public String toString() {
		if (this == NA) return "NotAvailable";
		
		StringBuffer message = new StringBuffer();
		
		message.append("$");
		boolean hasItem = false;
		for (int i = coeff.size()-1; i>=0; i--) {
			int value = coeff.get(i).getValue();
			if (i == 0) {
				if (hasItem && value != 0) {
					message.append("+");
					message.append(value);
				} else {
					if (!hasItem) message.append(value);
				}
			} else {
				if (value != 0) {
					if (i < coeff.size()-1) message.append("+");
					if (value == 1) {
						if (i != 1) message.append(var + "^" + i);
						else message.append(var);
					}
					else {
						if (i != 1) message.append(value + var + "^" + i);
						else message.append(value + var);
					}
					hasItem = true;
				}
			}
		}
		message.append("$");
		return message.toString();
	}

	public String toPlainString() {
		if (this == NA) return "NotAvailable";
		
		StringBuffer message = new StringBuffer();
		
		boolean hasItem = false;
		for (int i = coeff.size()-1; i>=0; i--) {
			int value = coeff.get(i).getValue();
			if (i == 0) {
				if (hasItem && value != 0) {
					message.append("+");
					message.append(value);
				} else {
					if (!hasItem) message.append(value);
				}
			} else {
				if (value != 0) {
					if (i < coeff.size()-1) message.append("+");
					if (value == 1) {
						if (i != 1) message.append(var + "^" + i);
						else message.append(var);
					}
					else {
						if (i != 1) message.append(value + var + "^" + i);
						else message.append(value + var);
					}
					hasItem = true;
				}
			}
		}
		return message.toString();
	}
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof SVModPoly)) return false;
		
		SVModPoly other = (SVModPoly)obj;
		
		if (this.coeff.size() != other.coeff.size()) return false;
		for (int i = 0; i < coeff.size(); i++) {
			if (!coeff.get(i).equals(other.coeff.get(i))) return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		
	}
}

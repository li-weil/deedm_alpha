/**
 * 
 */
package com.deedm.legacy.graph;

/**
 * @author user
 *
 */
public class DoubleMatrix {
	double[][] data = null;
	int rows = 0;
	int cols = 0;
	
	public DoubleMatrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		data = new double[rows][cols];
		for (int i = 0; i < rows; i++) 
			for (int j = 0; j < cols; j++) data[i][j] = 0;
	}

	public DoubleMatrix(double[][] data) {
		this.rows = data.length;
		this.cols = data[0].length;
		this.data = new double[rows][cols];
		for (int i = 0; i < rows; i++) 
			for (int j = 0; j < cols; j++) this.data[i][j] = data[i][j];
	}
	
	public DoubleMatrix(DoubleMatrix other) {
		this(other.data);
	}
	
	public int getRowNumber() {
		return rows;
	}
	
	public int getColNumber() {
		return cols;
	}
	
	public double get(int row, int col) {
		return data[row][col];
	}
	
	public void set(int row, int col, double value) {
		data[row][col] = value;
	}
	
	public DoubleMatrix product(DoubleMatrix other) {
		if (other.rows != cols) return null;
		DoubleMatrix result = new DoubleMatrix(rows, other.cols);
		
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < other.cols; j++) {
				double sum = 0;
				for (int k = 0; k < cols; k++) sum = sum + (data[i][k] * other.data[k][j]);
				result.data[i][j] = sum;
			}

		return result;
	}
	
	public String toString() {
		String result = "";
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				String dataString = "\\infty ";
				if (data[i][j] < Double.MAX_VALUE) dataString = data[i][j] + ""; 
				result = result + dataString + "  ";
			}
			result = result + "\n";
		}
		return result;
	}
	
	public String toLaTeXString() {
		StringBuffer result = new StringBuffer();
		result.append("\\begin{bmatrix}\n");
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				String dataString = "\\infty ";
				if (data[i][j] < Double.MAX_VALUE) dataString = data[i][j] + ""; 
				if (j < cols-1) result.append(dataString + " & ");
				else result.append(dataString + " ");
			}
			result.append(" \\\\\n");
		}
		result.append("\\end{bmatrix}\n");
		return result.toString();
	}
}

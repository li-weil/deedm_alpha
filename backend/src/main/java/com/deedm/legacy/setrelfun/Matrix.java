/**
 * 
 */
package com.deedm.legacy.setrelfun;

/**
 * @author zxc
 *
 */
public class Matrix {
	int[][] data = null;
	int rows = 0;
	int cols = 0;
	
	public Matrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		data = new int[rows][cols];
		for (int i = 0; i < rows; i++) 
			for (int j = 0; j < cols; j++) data[i][j] = 0;
	}
	
	public Matrix(int[][] data) {
		this.rows = data.length;
		this.cols = data[0].length;
		this.data = new int[rows][cols];
		for (int i = 0; i < rows; i++) 
			for (int j = 0; j < cols; j++) this.data[i][j] = data[i][j];
	}
	
	public Matrix(Matrix other) {
		this(other.data);
	}
	
	public int getRowNumber() {
		return rows;
	}
	
	public int getColNumber() {
		return cols;
	}
	
	public int get(int row, int col) {
		return data[row][col];
	}
	
	public void set(int row, int col, int value) {
		data[row][col] = value;
	}
	
	public Matrix logicAdd(Matrix other) {
		if (other.rows != rows || other.cols != cols) return null;
		Matrix result = new Matrix(rows, cols);
		
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++) result.data[i][j] = data[i][j] | other.data[i][j];
		
		return result;
	}

	public Matrix add(Matrix other) {
		if (other.rows != rows || other.cols != cols) return null;
		Matrix result = new Matrix(rows, cols);
		
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++) result.data[i][j] = data[i][j] + other.data[i][j];
		
		return result;
	}

	public Matrix logicAnd(Matrix other) {
		if (other.rows != rows || other.cols != cols) return null;
		Matrix result = new Matrix(rows, cols);
		
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++) result.data[i][j] = data[i][j] & other.data[i][j];
		
		return result;
	}

	public Matrix logicDifference(Matrix other) {
		if (other.rows != rows || other.cols != cols) return null;
		Matrix result = new Matrix(rows, cols);
		
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++) {
				if (data[i][j] >= other.data[i][j]) result.data[i][j] = data[i][j] - other.data[i][j];
				else result.data[i][j] = 0;
			}
		
		return result;
	}
	
	public Matrix logicProduct(Matrix other) {
		if (other.rows != cols) return null;
		Matrix result = new Matrix(rows, other.cols);
		
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < other.cols; j++) {
				int sum = 0;
				for (int k = 0; k < cols; k++) sum = sum | (data[i][k] & other.data[k][j]);
				result.data[i][j] = sum;
			}

		return result;
	}

	public Matrix product(Matrix other) {
		if (other.rows != cols) return null;
		Matrix result = new Matrix(rows, other.cols);
		
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < other.cols; j++) {
				int sum = 0;
				for (int k = 0; k < cols; k++) sum = sum + (data[i][k] * other.data[k][j]);
				result.data[i][j] = sum;
			}

		return result;
	}
	
	public String toString() {
		String result = "";
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				result = result + data[i][j] + "  ";
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
				if (j < cols-1) result.append(data[i][j] + " & ");
				else result.append(data[i][j] + " ");
			}
			result.append(" \\\\\n");
		}
		result.append("\\end{bmatrix}\n");
		return result.toString();
	}
	
}

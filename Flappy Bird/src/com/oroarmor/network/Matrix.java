package com.oroarmor.network;

public class Matrix {
	public int cols;
	public int rows;
	public float[][] matrix;

	public Matrix(int _rows, int _cols) {
		cols = _cols;
		rows = _rows;
		matrix = new float[rows][cols];
	}

	public static Matrix add(Matrix a, Matrix b) {

		if (a.cols != b.cols || a.rows != b.rows) {
			return a;
		}

		Matrix result = new Matrix(a.cols, a.rows);

		for (int i = 0; i < a.rows; i++) {
			for (int j = 0; j < a.cols; j++) {
				result.matrix[i][j] = a.matrix[i][j] + b.matrix[i][j];
			}
		}

		return result;
	}

	public static Matrix transpose(Matrix m) {
		Matrix result = new Matrix(m.cols, m.rows);
		for (int i = 0; i < m.rows; i++) {
			for (int j = 0; j < m.cols; j++) {
				result.matrix[j][i] = m.matrix[i][j];
			}
		}
		return result;
	}

	public void randomize() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] = (float) Math.random() * 2 - 1;
			}
		}
	}

	public void add(float n) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] += n;
			}
		}
	}

	public void add(Matrix m) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] += m.matrix[i][j];
			}
		}
	}

	public void mult(float n) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] *= n;
			}
		}
	}

	public void mult(Matrix m) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] *= m.matrix[i][j];
			}
		}
	}

	public static Matrix mult(Matrix a, Matrix b) {

		if (a.cols != b.rows) {
			return a;
		}
		Matrix result = new Matrix(a.rows, b.cols);
		for (int i = 0; i < result.rows; i++) {
			for (int j = 0; j < result.cols; j++) {

				float sum = 0;
				for (int k = 0; k < a.cols; k++) {

					sum += a.matrix[i][k] * b.matrix[k][j];

				}
				result.matrix[i][j] = sum;
			}
		}
		return result;
	}

	public static Matrix sigmoid(Matrix m) {
		Matrix result = new Matrix(m.rows, m.cols);
		for (int i = 0; i < m.rows; i++) {
			for (int j = 0; j < m.cols; j++) {
				result.matrix[i][j] = (float) (1 / (1 + Math.pow(Math.E, -1 * m.matrix[i][j])));
			}
		}
		return result;
	}

	public static Matrix dsigmoid(Matrix m) {
		Matrix result = new Matrix(m.rows, m.cols);
		for (int i = 0; i < m.rows; i++) {
			for (int j = 0; j < m.cols; j++) {
				result.matrix[i][j] = m.matrix[i][j] * (1 - m.matrix[i][j]);
			}
		}
		return result;
	}

	public void setAll(float n) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] = n;
			}
		}
	}

	public static Matrix fromArray(float[] array) {
		Matrix m = new Matrix(array.length, 1);
		for (int i = 0; i < array.length; i++) {
			m.matrix[i][0] = array[i];
		}
		return m;
	}

	public static Matrix subtract(Matrix a, Matrix b) {
		// Return a new Matrix a-b
		Matrix result = new Matrix(a.rows, a.cols);
		for (int i = 0; i < result.rows; i++) {
			for (int j = 0; j < result.cols; j++) {
				result.matrix[i][j] = a.matrix[i][j] - b.matrix[i][j];
			}
		}
		return result;
	}

	float[] toArray() {
		float[] arr = new float[rows * cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				arr[i + j * cols] = matrix[i][j];
			}
		}
		return arr;
	}

	public void print() {

		for (int i = 0; i < rows; i++) {
			System.out.print("| ");
			for (int j = 0; j < cols; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println("|");
		}
		System.out.println();
	}

	public void percent() {

		float sum = 0;
		for (int i = 0; i < matrix[0].length; i++) {
			sum += matrix[0][i];
		}
		for (int i = 0; i < matrix[0].length; i++) {
			matrix[0][i] /= sum;
		}
	}
}

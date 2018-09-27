package com.alexfoglia.server;

import java.util.Arrays;

import org.springframework.data.annotation.Id;

public class DatabaseGrid {

	@Id
	private String id;
	private int[][] matrix;
	private int n;

	public DatabaseGrid() {
		n=0;
		matrix=new int[n][n];
	}

	public DatabaseGrid(int n, int[][] matrix) {
		this.n=n;
		this.matrix=matrix;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + Arrays.deepHashCode(matrix);
		result = prime * result + n;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DatabaseGrid other = (DatabaseGrid) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (!Arrays.deepEquals(matrix, other.matrix)) {
			return false;
		}
		return n == other.n;
	}

	public int[][] getMatrix(){
		return matrix;
	}

	public String getId() {
		return this.id;
	}

	public int getN() {
		return this.n;
	}

	public boolean isEnabled(String node) {
		boolean correctFormat=node.matches("[0-9]+_[0-9]+");
		if(!correctFormat) return false;
		String[] ij= node.split("_");
		int i=Integer.parseInt(ij[0]);
		int j=Integer.parseInt(ij[1]);
		return matrix[i][j]>0;
	}

	public void setId(String id) {
		this.id=id;
	}

}

package com.alexfoglia.server;

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
		String[] i_j= node.split("_");
		int i=Integer.parseInt(i_j[0]);
		int j=Integer.parseInt(i_j[1]);
		
			return matrix[i][j]>0;
		
	}

}

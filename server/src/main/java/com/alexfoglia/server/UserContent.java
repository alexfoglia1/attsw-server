package com.alexfoglia.server;

public class UserContent {

	private int number;
	private String content;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int[][] parseMatrix() {
		int[][] matrix = new int[number][number];
		int contiter = 0;
		for (int i = 0; i < number; i++) {
			for (int j = 0; j < number; j++) {
				try {
					matrix[i][j] = Integer.parseInt(String.format("%c", content.charAt(contiter++)));
				} catch (Exception exc) {
					matrix[i][j] = 0;
				}
			}
		}
		return matrix;
	}

}

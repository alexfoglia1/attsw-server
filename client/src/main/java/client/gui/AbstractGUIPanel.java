package client.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

public abstract class AbstractGUIPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		for (int i = 0; i < gridSize(); i++) {
			for (int j = 0; j < gridSize(); j++) {
				drawOne(g, i, j);
			}
		}
	}

	private void drawOne(Graphics g, int i, int j) {
		Point[][] grid = getGrid();
		Color[][] gridColor = getGridColor();
		String[][] gridNames = getGridNames();
		g.setColor(gridColor[i][j]);
		g.fillOval((int) grid[i][j].getX() - 8 / 2, (int) grid[i][j].getY() - 8 / 2, 8, 8);
		g.setFont(g.getFont().deriveFont(Font.BOLD));
		g.drawString(gridNames[i][j], (int) grid[i][j].getX() + 8 / 2, (int) grid[i][j].getY() - 8 / 2);
	}
	public void enablePoint(String string, int i, int j, Graphics g2) {
		enablePointProcedure(string, i, j, Color.RED);
		paintComponent(g2);
	}

	public void enableNotHighlightablePoint(String string, int i, int j, Graphics g2) {
		enablePointProcedure(string, i, j, Color.BLACK);
		paintComponent(g2);
	}
	
	protected abstract void enablePointProcedure(String s, int i, int j, Color col);
	
	public abstract String[][] getGridNames();

	public abstract Color[][] getGridColor();

	public abstract int gridSize();
	
	public abstract Point[][] getGrid();

}

package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;

public class GUIpanel extends AbstractGUIPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Point[][] grid;
	private Color[][] gridColor;
	private String[][] gridNames;
	private int gridSize;
	private int offsetX;
	private int offsetY;
	private int distance;
	public static final Color DARKGREEN = Color.decode("#0e7810");
	
	public GUIpanel(int maxSize) {
		setSizes(maxSize);
		this.distance = (int) (8 * Math.sqrt(maxSize));
		setBackground(Color.WHITE);
		initGrid(maxSize);
	}

	private void setSizes(int maxSize) {
		setPreferredSize(new Dimension(512, 512));
		setMinimumSize(new Dimension(512, 512));
		setSize(new Dimension(512, 512));
		gridSize = maxSize;
	}

	public Point getLocationOf(int i, int j) {
		return grid[i][j].getLocation();
	}

	private void initGrid(int maxSize) {
		initDrawingVariables(maxSize);
		doInitGrid(maxSize);
	}

	private void doInitGrid(int maxSize) {
		for (int i = 0; i < maxSize; i++) {
			for (int j = 0; j < maxSize; j++) {
				initOne(i, j);
			}
		}
	}

	private void initOne(int i, int j) {
		grid[j][i] = new Point(offsetX + i * distance, offsetY + j * distance);
		gridColor[j][i] = getBackground();
		gridNames[j][i] = "";
	}

	private void initDrawingVariables(int maxSize) {
		grid = new Point[maxSize][maxSize];
		gridColor = new Color[maxSize][maxSize];
		gridNames = new String[maxSize][maxSize];
		offsetX = getWidth() / 2 - (maxSize * distance / 4);
		offsetY = getHeight() / 2 - (maxSize * distance / 4);
	}

	public void reset() {
		doReset();
		refreshView();
	}

	private void refreshView() {
		setVisible(false);
		repaint();
		setVisible(true);
	}

	private void doReset() {
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				resetOne(i, j);
			}
		}
	}

	private void resetOne(int i, int j) {
		gridColor[i][j] = (getBackground());
		gridNames[i][j] = "";
	}

	public void enablePoint(String toPrintInPoint, int i, int j) {
		setColorToPoint(toPrintInPoint, i, j, Color.RED);
	}

	private void setColorToPoint(String toPrintInPoint, int i, int j, Color col) {
		enablePointProcedure(toPrintInPoint, i, j, col);
		repaint();
	}

	protected void enablePointProcedure(String toPrintInPoint, int i, int j, Color col) {
		gridNames[i][j] = toPrintInPoint;
		gridColor[i][j] = (col);
	}

	

	public void highlightPath(List<String> path) {
		if (path == null) {
			unhighlight();
		} else {
			doHighlightPath(path);
		}
		refreshView();
	}

	private void doHighlightPath(List<String> path) {
		for (String e : path) {
			highlightOne(e);
		}
	}

	private void highlightOne(String e) {
		if (!e.matches("[0-9]+\\_[0-9]+")) {
			throw new IllegalArgumentException("Check syntax");
		}
		String[] pts=e.split("_");
		int i=Integer.parseInt(pts[0]);
		int j=Integer.parseInt(pts[1]);
		if (gridColor[i][j].equals(Color.RED)) {
			gridColor[i][j] = (DARKGREEN);
		}
	}

	private void unhighlight() {
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				if (gridColor[i][j].equals(DARKGREEN)) {
					gridColor[i][j] = Color.RED;
				}
			}
		}
	}

	public Color getColorInPoint(int i, int j) {
		return gridColor[i][j];
	}

	public String getPrintedNameIn(int i, int j) {
		return gridNames[i][j];
	}

	public void enableNotHighlightablePoint(String toPrintInPoint, int i, int j) {
		setColorToPoint(toPrintInPoint, i, j, Color.BLACK);
	}

	public int getOffsetY() {
		return offsetY;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getDistance() {
		return distance;
	}

	public Point[][] getAllLocations() {
		return grid.clone();
	}

	
	public String[][] getGridNames() {
		return gridNames;
	}

	
	public Color[][] getGridColor() {
		return gridColor;
	}

	
	public int gridSize() {
		return gridSize;
	}

	
	public Point[][] getGrid() {
		return grid;
	}

}
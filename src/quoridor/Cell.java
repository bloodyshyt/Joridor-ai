package quoridor;

public class Cell {
	
	public Point cell;
	public Cell prev;
	public int depth;
	
	public Cell(Point cell) {
		this.cell = cell;
		this.prev = null;
		this.depth = 0;
	}
	
	public Cell(Point cell, Cell prevCell) {
		this.cell = cell;
		this.prev = prevCell;
		this.depth = prevCell.depth + 1;
	}
	
}

package Util;

import quoridor.Point;

public class Coords {

	public final static int UP = 0;
	public final static int DOWN = 1;
	public final static int LEFT = 2;
	public final static int RIGHT = 3;
	public final static int[][] deltaCoord = new int[][] { { 0, 1 }, { 0, -1 },
			{ -1, 0 }, { 1, 0 } }; // up, down, left, right
	
	
	public static Point move(Point pt, int direction) {
		int x = pt.x, y = pt.y;
		x += deltaCoord[direction][0];
		y += deltaCoord[direction][1];
		return new Point(x, y);
	}
	
	public static void main(String[] str) {
		int x = 4, y = 4;
		Point pt = new Point(4,4);
		move(pt, DOWN);
		Str.print(pt.toString());
	}
	
}

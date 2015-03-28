package quoridor;

public class Point {
	public int x, y;

	public Point(int a, int b) {
		x = a;
		y = b;
	}	
	
	public String toString() {
		return new String("(" + x + "," + y + ")");
	}
}

package quoridor;

public class Point {
	int x, y;
	Point prev;

	Point(int a, int b) {
		x = a;
		y = b;
		prev = null;
	}
	
	Point(int a, int b, Point prev) {
		this(a, b);
		this.prev = prev;
	}
	
}

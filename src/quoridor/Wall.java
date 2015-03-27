package quoridor;

public class Wall {

	int x, y;
	int orientation;
	final static int HORIZONTAL = 0;
	final static int VERTICAL = 1;
	
	public Wall(int x, int y, int orientation) {
		this.x = x;
		this.y = y;
		this.orientation = orientation;
	}
	
	public Wall(Wall w) {
		this(w.getX(), w.getY(), w.getOrientation());
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getOrientation() {
		return orientation;
	}

}

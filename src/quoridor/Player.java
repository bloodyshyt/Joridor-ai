package quoridor;

public class Player {

	Point point;
	int nWalls;
	int winning_y;
	
	Player(int x, int y) {
		point = new Point(x, y);
		nWalls = 10;
		if(y == 0) winning_y = Quoridor.boardDim  - 1;
		if(y == Quoridor.boardDim - 1) winning_y = 0;
	}
	
	Player(int x, int y, int walls) {
		this(x, y);
		setWalls(walls);
	}
	
	Player(Player p) {
		this(p.getX(), p.getY(), p.getWalls());
	}
	
	public int getX() {return point.x;}
	public int getY() {return point.y;}
	public Point getPoint() {return point;}
	public void setXY(Point pt) {this.point = pt;}
	public int getWalls() {return nWalls;}
	public void setWalls(int walls) {nWalls = walls;}
	
	public String toString(int playerNo) {
		return new String("Player " + playerNo + " at " + point.toString() + ", " + nWalls + " left");
	}
}

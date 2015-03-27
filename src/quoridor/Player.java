package quoridor;

public class Player {

	int x, y;
	int nWalls;
	int winning_y;
	
	Player(int x, int y) {
		this.x = x;
		this.y = y;
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
	
	public int getX() {return x;}
	public int getY() {return y;}
	public void setX(int x) { this.x = x;}
	public void setY(int y) { this.y = y;}
	public int[] getXY() {return new int[] {getX(), getY()};}
	public int getWalls() {return nWalls;}
	public void setWalls(int walls) {nWalls = walls;}
	
}

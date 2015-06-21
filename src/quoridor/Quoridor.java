package quoridor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.omg.CORBA.RepositoryIdHelper;

import Util.Str;

public class Quoridor {

	public static int boardDim = 9;
	public final static int UP = 0;
	public final static int DOWN = 1;
	public final static int LEFT = 2;
	public final static int RIGHT = 3;
	
	public final static int[][] deltaCoord = new int[][] { { 0, 1 }, { 0, -1 },
			{ -1, 0 }, { 1, 0 } }; // up, down, left, right
	Player p1, p2;
	ArrayList<Wall> walls;

	public Quoridor() {
		// creates a new board
		p1 = new Player(boardDim / 2, 0);
		p2 = new Player(boardDim / 2, boardDim - 1);
		p1.setPlayerNo(1);
		p2.setPlayerNo(2);
		walls = new ArrayList<>();
	}

	public Quoridor(Quoridor Q) {
		// clone the board
		p1 = new Player(Q.p1);
		p2 = new Player(Q.p2);
		walls = new ArrayList<>();
		for (Wall w : Q.walls)
			walls.add(new Wall(w));
	}

	// helper methods
	public Move[] generateMoves(int playerNo) {
		return generateMoves(getPlayer(playerNo));
	}
	
	
	public Move[] generateMoves(Player p) {
		ArrayList<Move> legalMoves = new ArrayList<>();
		generatePlayerMoves(p, legalMoves);
		generateWallMoves(p, legalMoves);
		// error checking 
		if (legalMoves.isEmpty()) Str.println("No moves generated!");
		
		Move[] m = new Move[legalMoves.size()];
		legalMoves.toArray(m);
		return m;
	}

	private void generateWallMoves(Player p, ArrayList<Move> moves) {
		int playerNo = p.getPlayerNo();
		if(p.getWalls() > 0) {
			for(int x = 1; x < boardDim; x++) {
				for(int y = 0; y < boardDim - 1; y++) {
					if(!wallAt(x, y, Wall.HORIZONTAL) && !wallAt(x - 1, y, Wall.HORIZONTAL) 
							&& !wallAt(x, y, Wall.VERTICAL) && !wallAt(x, y + 1, Wall.VERTICAL)) {
								moves.add(new WallMove(x, y, Wall.HORIZONTAL, p.getPlayerNo()));
								moves.add(new WallMove(x, y, Wall.VERTICAL, p.getPlayerNo()));
							}
									
				}
			}
		}
	}

	private void generatePlayerMoves(Player p, ArrayList<Move> moves) {
		Point pt = p.getPoint();
		int x = pt.x, y = pt.y;
		// iterate through all the directions and see if we can move there
		for (int i = 0; i < 4; i++) {
			int adjX = x + deltaCoord[i][0], adjY = y + deltaCoord[i][1];
			
			if(canGoIn(x, y, i)) {
				moves.add(new PlayerMove(p.playerNo, adjX, adjY));
			} else
			
			if(!wallInDir(x, y, i) && pawnInDir(x, y, i)) {
				if(canGoIn(adjX, adjY, i)) 
					moves.add(new PlayerMove(p.playerNo, adjX + deltaCoord[i][0], adjY + deltaCoord[i][1]));
				else {
					for(int ii : new int[] {4 + i - 1, 4 + i +1}) {
						int j = ii % 4;
						if(canGoIn(adjX, adjY, j))
							moves.add(new PlayerMove(p.playerNo, adjX + deltaCoord[j][0], adjY + deltaCoord[j][1]));
					}
				}
			}
		}
	}

	public boolean gameOver() {
		return (p1.won() || p2.won());
	}
	public int[] BFS2(Player player) {
		int[][][] prev = new int[boardDim][boardDim][3];	// prev[x][y][prev x,y depth]
		for (int i = 0; i < boardDim; i++)
			for (int j = 0; j < boardDim; j++)
				prev[i][j] = new int[] { -1, -1, -1};	
		
		prev[player.getX()][player.getY()] = new int[] {player.getX(), player.getY(), 0};
		int[] source = new int[] {player.getX(), player.getY()};
		int[] goal = null;
		Queue<int[]> Q = new LinkedList<>();
		Q.add(source);
		
		int[] u;
		while(!Q.isEmpty()) {
			u = Q.poll();
			int x = u[0];
			int y = u[1];
			
			if(y == player.winning_y) {
				goal = u;
				break;
			}
			
			int _x, _y;
			for(int i = 0; i < 4; i++) {
				if(canGoIn(x, y, i)) {
					_x = x + deltaCoord[i][0];
					_y = y + deltaCoord[i][1];	
					prev[_x][_y] = new int[] {x, y, prev[x][y][2] + 1};
				}
			}
			
			
		}
		return null;
	}
	
	
	/**
	 * @param player Player in question
	 * @return int[] of {steps to goal, x-coord of next Step, y-coord}, 
	 * returns null if no such path exists
	 */
	public int[] BFS(Player player) {
		boolean[][] visited = new boolean[boardDim][boardDim];
		for(int i = 0; i < boardDim; i++) 
			for(int j = 0; j < boardDim; j++)
				visited[i][j] = false;
		
		Cell source = new Cell(player.point);
		visited[player.getX()][player.getY()] = true;
		Cell goal = null;
		Queue<Cell> Q = new LinkedList<>();
		Q.add(source);
		
		while(!Q.isEmpty()) {
			Cell u = Q.poll();
			int x = u.cell.x;
			int y = u.cell.y;
			
			if(y == player.winning_y) {
				goal = u;
				break;
			}
			
			int _x, _y;
			for(int i = 0; i < 4; i++) {
				_x = x + deltaCoord[i][0];
				_y = y + deltaCoord[i][1];
				if(canGoIn(u.cell, i) && !visited[_x][_y]) {
					Cell v = new Cell(new Point(_x, _y), u);
					visited[_x][_y] = true;
					Q.add(v);
				}
			}
		}
		
		if(goal == null) {
			//Str.println("Error!!!!!!!");
			return null;
		} else {
			Cell curr = goal;
			while(curr.prev != source) 
				curr = curr.prev;
			return new int[] {goal.depth, curr.cell.x, curr.cell.y};
		}
	}


	// helper methods	
	public boolean wallInDir(int x, int y, int direction) {
		if(direction == UP 		&& wallAt(x,  y, Wall.HORIZONTAL)) return true;
		if(direction == DOWN 	&& wallAt(x,  y - 1, Wall.HORIZONTAL)) return true;
		if(direction == LEFT 	&& wallAt(x,  y, Wall.VERTICAL)) return true;
		if(direction == RIGHT 	&& wallAt(x + 1,  y, Wall.VERTICAL)) return true;
		return false;
	}

	public boolean wallInDir(Point pt, int direction) {
		return wallInDir(pt.x,  pt.y, direction);
	}

	public boolean pawnInDir(int x, int y, int direction) {
		if(pawnAt(x + deltaCoord[direction][0], y + deltaCoord[direction][1]) != 0) return true;
		return false;
	}

	public boolean pawnInDir(Point pt, int direction) {
		return pawnInDir(pt.x,  pt.y,  direction);
	}

	public int[] moveIn(Point pt, int direction) {
		int x = pt.x, y = pt.y;
		return null;
		
	}

	public boolean canGoIn(Point p, int direction) {
		return canGoIn(p.x, p.y, direction);
	}

	/**
	 * Is there an empty adjacent cell in the given direction?
	 * @param x
	 * @param y
	 * @param direction
	 * @return
	 */
	public boolean canGoIn(int x, int y, int direction) {
		if(!wallInDir(x, y, direction) && !pawnInDir(x, y, direction)) return true;
		
		return false;
	}

	/**
	 * @param coords x and y coordinates
	 * @param orientation 
	 * @return whether there is a wall at the top left corner
	 */
	public boolean wallAt(int[] coords, int orientation) {
		
		// add the edge conditions
		if( (coords[0] == 0 || coords[0] == boardDim) && orientation == Wall.VERTICAL) return true;
		if( (coords[1] == boardDim - 1 || coords[1] == -1) && orientation == Wall.HORIZONTAL) return true;
		
		for (Wall w : walls) {
			if (orientation == Wall.HORIZONTAL) {
				if (w.orientation == Wall.HORIZONTAL
						&& (w.getX() == coords[0] || w.getX() == coords[0] + 1)
						&& w.getY() == coords[1])
					return true;
			}
			if (orientation == Wall.VERTICAL) {
				if (w.orientation == Wall.VERTICAL
						&& (w.getY() == coords[1] || w.getY() == coords[1] - 1)
						&& w.getX() == coords[0])
					return true;
			}
		}
		return false;
	}

	private boolean wallAt(int i, int j, int orientation) {
		return wallAt(new int[] { i, j }, orientation);
	}

	/**
	 * @param x x coord
	 * @param y y coord
	 * @return player number if pawn is there, else 0
	 */
	private int pawnAt(int x, int y) {
		if (p1.getX() == x && p1.getY() == y)
			return 1;
		if (p2.getX() == x && p2.getY() == y)
			return 2;
		return 0;
	}

	public Player getPlayer(int playerNo) {
		return (playerNo == 1) ? p1 : p2;
	}
	
	public Player getOtherPlayer(int playerNo) {
		return (playerNo == 1) ? p2 : p1;
	}
	
	public void display() {
		for(int i = boardDim - 1; i >= 0; i--) {
			printHorizontalWalls(i);
			printVerticalWalls(i);
		}
		printNumberedRow();
		Str.println(p1.toString(1));
		Str.println(p2.toString(2));
		Str.println();
	}
	
	private void printHorizontalWalls(int row) {
		System.out.print("   *");
		for(int i = 0; i < boardDim; i++) {
			if(wallAt(i, row, Wall.HORIZONTAL)) System.out.print("---*");
			else System.out.print("   *");
		}
		System.out.println();
	}
	
	private void printVerticalWalls(int row) {
		System.out.print(row + "  ");
		
		for(int i = 0; i < boardDim; i++) {
			if(wallAt(i,  row, Wall.VERTICAL)) System.out.print("|");
			else System.out.print(" ");
			
			int pawn  = pawnAt(i, row);
			if(pawn  == 1) System.out.print(" X ");
			else if(pawn  == 2) System.out.print(" O ");
			else System.out.print("   ");
		}
		
		System.out.println();
	}
	
	private void printNumberedRow() {
		String horizWall = "---";
		System.out.print(horizWall + "*");
		for(int i = 0; i < boardDim; i++)
			System.out.print(horizWall + "*");
		System.out.println();
		
		System.out.print("    ");
		for(int i = 0; i < boardDim; i++) {
			System.out.print(" " + i + "  ");
		}
		System.out.println();
	}
	
	public void debug() {
		// print player data
		Str.println(p1.toString(1));
		Str.println(p2.toString(2));
		// print wall data
		for(Wall w : walls) Str.println(w.toString());
	}
	
	public static void main(String[] str) {
		// P1 X, P2 O
		Quoridor q = new Quoridor();
		q.walls.add(new Wall(4, 4, Wall.HORIZONTAL));	// correct
		q.walls.add(new Wall(3, 3, Wall.VERTICAL));		// correct
		q.walls.add(new Wall(5, 0, Wall.HORIZONTAL));
		q.walls.add(new Wall(6, 0, Wall.VERTICAL));
		q.display();	// display is wrong
		Str.print("P1 path: " + q.BFS(q.p1)[0]);
		AI ai = new AI();
		Str.println("AI score of " + ai.evaluate(q));
	}
}

package quoridor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import org.omg.CORBA.RepositoryIdHelper;

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

	public boolean canGoIn(Point p, int direction) {
		return canGoIn(p.x, p.y, direction);
	}

	/**
	 * @param x
	 * @param y
	 * @param direction
	 * @return
	 */
	public boolean canGoIn(int x, int y, int direction) {
		// coords in clockwise fashion starting from top right corner
		// A(0) B(1)
		// C(2) D(3)
		int[] A = new int[] { x, y };
		int[] B = new int[] { x + 1, y };
		int[] C = new int[] { x, y - 1 };
		int[] D = new int[] { x + 1, y - 1 };

		if (direction == UP && !wallAt(A, Wall.HORIZONTAL)
				&& !wallAt(B, Wall.HORIZONTAL) && (pawnAt(x, y + 1) == 0))
			return true;
		if (direction == DOWN && !wallAt(B, Wall.HORIZONTAL)
				&& !wallAt(D, Wall.HORIZONTAL) && (pawnAt(x, y - 1) == 0))
			return true;
		if (direction == LEFT && !wallAt(A, Wall.VERTICAL)
				&& !wallAt(C, Wall.VERTICAL) && (pawnAt(x - 1, y) == 0))
			return true;
		if (direction == RIGHT && !wallAt(B, Wall.VERTICAL)
				&& !wallAt(D, Wall.VERTICAL) && (pawnAt(x + 1, y) == 0))
			return true;
		return false;
	}

	public boolean wallAt(int[] coords, int orientation) {
		
		// add the edge conditions
		if(coords[0] == 0 && orientation == Wall.VERTICAL) return true;
		if(coords[1] == boardDim - 1 && orientation == Wall.HORIZONTAL) return true;
		
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
	
	public void display() {
		for(int i = boardDim - 1; i >= 0; i--) {
			printHorizontalWalls(i);
			printVerticalWalls(i);
		}
		printNumberedRow();
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
	
	private void printBFSMatrix(int[][][] prev) {
		for (int y = prev.length - 1; y >= 0; y--) {
			for (int x = 0; x < prev[0].length; x++) {
				System.out.print(prev[x][y][0] + "," + prev[x][y][1] + "\t");
			}
			System.out.println();
		}
	}
	
	// returns number of steps to goal, x and y coordinate of first step in this path
	public int[] BFS(Player player) {

		int sourceVal = 100;

		int [][][] prev = new int[boardDim][boardDim][2]; // prev[x][y][prev x,
															// y];

		// initialise all as {-1, -1} (not visited)
		for (int i = 0; i < boardDim; i++)
			for (int j = 0; j < boardDim; j++)
				prev[i][j] = new int[] { -1, -1 };

		// get starting point
		int[] source = new int[] { sourceVal, sourceVal };
		prev[player.x][player.y] = source;
		Queue<int[]> Q = new LinkedList<>();
		Q.add(new int[] {player.x, player.y});

		int[] goalCell = new int[] {-1, -1};
		
		while (!Q.isEmpty()) {
			int[] point = Q.remove();
			int x = point[0];
			int y = point[1];
			
			if(y == player.winning_y) {
				goalCell[0] = x;
				goalCell[1] = y;
				break;
			}
			
			int _x, _y;

			// check adjacent spots
			for (int i = 0; i < 4; i++) {
				_x = x + deltaCoord[i][0];
				_y = y + deltaCoord[i][1];
				if ((_x < 0 || _x > boardDim - 1) // out of the board
						|| (_y < 0 || _y > boardDim - 1))
					continue;
				if ( canGoIn(x, y, i)
						&& (prev[_x][_y][0] == -1 && prev[_x][_y][0] == -1) ) {
					int[] newPoint = new int[] {_x, _y};
					prev[_x][_y][0] = x;
					prev[_x][_y][1] = y;
					Q.add(newPoint);
				}
			}
		}
		
		printBFSMatrix(prev);
		
		// find number of steps
		int steps = 0;
		int x = goalCell[0];	// will eventually hold first step
		int y = goalCell[1];	// in shortest path
		
		Deque<int[]> D = new ArrayDeque<int[]>();
		
		steps = 1; // because we are looking ahead
		System.out.println("Goal of " + goalCell[0] + "," + goalCell[1]);
		while(x != player.x && y != player.y) {
			System.out.println("We are at (" + x + "," + y + ") next stop (" + prev[x][y][0]
					+ "," + prev[x][y][1] + ")");
			x = prev[x][y][0];
			y = prev[x][y][1];
			D.add(new int[] {x,y});
			steps++;
		}
		
		//D.pop();
		int[] nextStep = D.pop();
		return new int[] {steps, nextStep[0], nextStep[1]};
	}
	

	

	/**
	 * @param player
	 * @return int[] of x, y of next tile in shortest path to goal, null if not
	 *         such path exists (i.e illegal wall move)
	 * 
	 */
	public int[] BFS2(Player player) {
		boolean[][] visited = new boolean[boardDim][boardDim];
		int[][] distTo = new int[boardDim][boardDim];
		for (int i = 0; i < boardDim; i++)
			for (int j = 0; j < boardDim; j++)
				visited[i][j] = false;

		Point source = new Point(player.getX(), player.getY());
		visited[source.x][source.y] = true;
		distTo[source.x][source.y] = 0;

		Queue<Point> Q = new LinkedList<>();
		Q.add(source);

		Point v, goalPoint = null;

		while (!Q.isEmpty()) {

			v = Q.remove();

			// check if reached goal
			// if(v.y == player.winning_y) {
			// goalPoint = v;
			// break;
			// }

			for (int i = 0; i < 4; i++) {
				int _x = v.x + deltaCoord[i][0];
				int _y = v.y + deltaCoord[i][1];
				if ((_x < 0 || _x > boardDim - 1) // out of the board
						|| (_y < 0 || _y > boardDim - 1))
					continue;
				if (canGoIn(v, i) && !visited[_x][_y]) {
					// valid move and cell not visited
					Point p = new Point(_x, _y, v);
					visited[_x][_y] = true;
					distTo[_x][_y] = distTo[v.x][v.y] + 1;
					Q.add(p);
				}
			}

			// print out dist matrix
			for (int[] b : distTo) {
				for (int c : b) {
					System.out.print(c + "\t");
				}
				System.out.println();
			}
			System.out.println();

		}

		return null;

		/*
		 * if(goalPoint == null) // there is no path return null; else { Point p
		 * = goalPoint; int nsteps = 0; while(p != source) {
		 * System.out.println(p.x + "," + p.y); p = p.prev; nsteps++; }
		 * 
		 * return new int[] {p.x, p.y, nsteps}; }
		 */
	}

	public static void main(String[] str) {
		// P1 X, P2 O
		Quoridor q = new Quoridor();
		q.walls.add(new Wall(4, 4, Wall.HORIZONTAL));	// correct
		q.walls.add(new Wall(3, 3, Wall.VERTICAL));		// correct
		q.walls.add(new Wall(5, 0, Wall.HORIZONTAL));
		q.display();	// display is wrong
		System.out.println("Go up? " + q.canGoIn(3, 5, UP));	// true - true
		System.out.println("Go down? " + q.canGoIn(3, 5, DOWN));	// false - false
		System.out.println("Go left? " + q.canGoIn(3, 5, LEFT));	// false - true 	
		System.out.println("Go right? " + q.canGoIn(3, 5, RIGHT));	// true - true
		int[] ans = q.BFS(q.p1);
		
		System.out.println("Steps to goal for player 1: " + ans[0] + " to " + ans[1] + " " + ans[2]);
	}
}

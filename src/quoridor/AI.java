package quoridor;

import Util.Str;

/**
 * @author The NG Family
 * 
 */
public class AI {
	
	int maxPlayer, minPlayer;

	public Move getNextMove(Quoridor Q, int player) {
		Move bestMove = null;
		maxPlayer = player;
		minPlayer = (maxPlayer == 1) ? 2 : 1;
		int depth = 1;
		
		bestMove = minimax(Q, depth);
		
		return bestMove;
	}
	
	private Move minimax(Quoridor Q, int depth) {
		
		double bestScore = Double.NEGATIVE_INFINITY;
		double currentScore;
		Move bestMove = null;
		
		Move[] moves = Q.generateMoves(maxPlayer);
		//Str.println("Expanding on " + moves.length + " nodes");
		for(Move m: moves) {
			Quoridor nextState = m.playMove(Q);
			currentScore = _minimax(nextState, minPlayer, depth);
			//Str.println(m.toString() + " has score of " + currentScore + " vs " + bestScore);
			if(currentScore > bestScore) {
				bestScore  = currentScore;
				bestMove = m;
			}
		}
		//Str.println("Best move:" + bestMove.toString() + " score of " + bestScore);
		return bestMove;
	}
	
	private double _minimax(Quoridor Q, int playerNo, int depth) {
		//Str.println("Minimax for P" + playerNo + " at depth " + depth);
		double bestScore = (playerNo == maxPlayer) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
		double currentScore;

		if(Q.gameOver()) {
			if(Q.getPlayer(maxPlayer).won()) return 1000000000;
			else return -1000000000;
		}
		
		if (depth == 0) {
			bestScore = evaluate(Q);
			//Str.println("Depth 0, score of " + bestScore);
		} else {
			Move[] moves = Q.generateMoves(playerNo);
			for (Move move : moves) {
				Quoridor nextState = move.playMove(Q);
				if (playerNo == maxPlayer) {
					currentScore = _minimax(nextState, minPlayer, depth - 1);
					bestScore = Math.max(bestScore, currentScore);
				} else {
					// minPlayer is the minimizing player
					currentScore = _minimax(nextState, maxPlayer, depth - 1);
					bestScore = Math.min(bestScore, currentScore);
				}

			}
		}
		return bestScore;
	}
	
	public double evaluate(Quoridor Q) {
		// evaluate the board based on the max player's perspective
		// currently we only consider shortest path length and remaining walls
		Player player = Q.getPlayer(maxPlayer);
		Player opponent = Q.getPlayer(minPlayer);
		//Q.debug();
		int[] maxBFS = Q.BFS(player), minBFS = Q.BFS(opponent);
		if(maxBFS == null || minBFS == null) return -10000000;
		//int maxPath = maxBFS[0];
		//int minPath = minBFS[0];
		int maxWalls = player.getWalls();
		int minWalls = opponent.getWalls();
		
		//Str.println("Max path: " + maxPath + " Min path: " + minPath + " Max Walls: " + maxWalls + " min walls " + minWalls);
		double score = minBFS[0] - maxBFS[0] + (maxWalls - minWalls);
		return score + 0.1 * Math.random();
	}
	
	// unit test 
	public static void main(String[] str) {
		
		// set up test bed
		// P1 4,6 8
		// P2 4,1 9
	 	// walls
		// 4,6 H
		// 4,0 H
		// 5,1 V
		Quoridor q = new Quoridor();
		WallMove wm1 = new WallMove(4, 6, Wall.HORIZONTAL, 1);
		WallMove wm2 = new WallMove(4, 0, Wall.HORIZONTAL, 1);
		WallMove wm3 = new WallMove(5, 1, Wall.VERTICAL, 2);
		q.p1.setXY(new Point(4, 6));
		q.p2.setXY(new Point(4, 1));
		for(WallMove wm : new WallMove[] {wm1, wm2, wm3})
			q = wm.playMove(q);
		q.display();
		AI ai = new AI();
		Move move = ai.getNextMove(q, 2);
		Str.println(move.toString());

	}
}

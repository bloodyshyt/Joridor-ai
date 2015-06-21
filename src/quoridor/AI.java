package quoridor;

import Util.Str;

/**
 * @author The NG Family
 * 
 */
public class AI {
	
	int maxPlayer, minPlayer;
	
	double[] mFeatureWeights;
	
	public AI() {}	// for kicks

	public AI(double[] pFeatureWeights) {
		mFeatureWeights = pFeatureWeights;
	}

	public Move getNextMove(Quoridor Q, int player) {
		Move bestMove = null;
		
		// if there are no walls left just walk towards goal
		// Test: return next step in shortest path when there are no more walls

		maxPlayer = player;
		minPlayer = (maxPlayer == 1) ? 2 : 1;
		int depth = 1;
		
		if (Q.getPlayer(player).getWalls() == 0) {
			mFeatureWeights = new double[] {-1000, 0, 0, 0, 0, 0};
			depth = 0;
		}
		
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
			Quoridor nextState = m.executeMoveOn(Q);
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

		if(Q.gameOver() != 0) {
			if(Q.getPlayer(maxPlayer).won()) return 1000000000;
			else return -1000000000;
		}
		
		if (depth == 0) {
			bestScore = evaluate(Q);
			//Str.println("Depth 0, score of " + bestScore);
		} else {
			Move[] moves = Q.generateMoves(playerNo);
			for (Move move : moves) {
				Quoridor nextState = move.executeMoveOn(Q);
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
		int[] maxBFS = Q.BFS(player);
		int[] minBFS = Q.BFS(opponent);
		if(maxBFS == null || minBFS == null) {
			//Str.println("Something fucked up");
			//Q.display();
			return -10000000;
		}
		//int maxPath = maxBFS[0];
		//int minPath = minBFS[0];
		int maxWalls = player.getWalls();
		int minWalls = opponent.getWalls();
		
		int maxManDist = Math.abs(player.getY() - player.getWinningY());
		int minManDist = Math.abs(opponent.getY() - opponent.getWinningY());
		
		/* Previously test working */
		//Str.println("Max path: " + maxPath + " Min path: " + minPath + " Max Walls: " + maxWalls + " min walls " + minWalls);
		// Evaluate Score: Min's Path - Max's Path + Max's Walls - Min's Walls
		//double score = minBFS[0] - maxBFS[0] + (maxWalls - minWalls);
		
		int[] featureScores = new int[6];
		featureScores[0] = maxBFS[0];
		featureScores[1] = minBFS[0];
		featureScores[2] = maxManDist;
		featureScores[3] = minManDist;
		featureScores[4] = maxWalls;
		featureScores[5] = minWalls;
		
		//for(double weight : mFeatureWeights) Str.println("Weight: " + weight);
		// Evalute score: Max Path, Min Path, Max ManDist, Min ManDist, Max Walls, Min Walls
		double score = 0;
		
		for(int i = 0; i < featureScores.length; i++) score += mFeatureWeights[i] * featureScores[i];
		//Str.println(score + "");
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
			q = wm.executeMoveOn(q);
		q.display();
		AI ai = new AI();
		Move move = ai.getNextMove(q, 2);
		Str.println(move.toString());

	}
}

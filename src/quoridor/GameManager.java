package quoridor;

import Util.Coords;
import Util.Str;

/**
 * @author The NG Family Manages the Quoridor object, generating moves
 */
public class GameManager {

	Quoridor game;
	Player currentPlayer;
	AI ai;
	
	public GameManager() {
		ai = new AI();
	}
	
	public void reset() {
		game = new Quoridor();
		Str.println("Game resetted");
	}
	
	public void start() {
		int turns = 0;
		currentPlayer = game.getPlayer(1);
		while(!game.gameOver()) {
			Str.println("Player " + currentPlayer.getPlayerNo() + " turn.");
			playTurn(currentPlayer);
			game.display();
			currentPlayer = game.getOtherPlayer(currentPlayer.playerNo);
			turns++;
		}
		Str.println("Game ended in " + turns + " turns");
	}
	
	
	
	private void playTurn(Player player) {
		ai = new AI();
		Move nextMove = ai.getNextMove(game, player.playerNo);
		game = nextMove.playMove(game);
	}

	public static void main(String[] str) {
		GameManager gm = new GameManager();
		gm.reset();
		gm.start();
	}

}

interface Move {

	/**
	 * @param q
	 *            Quoridor game
	 * @return new deep instance with move made
	 */
	public Quoridor playMove(Quoridor q);
	public String toString();
}

class PlayerMove implements Move {

	int playerNo, newX, newY;

	public PlayerMove(int playerNo, int newX, int newY) {
		this.playerNo = playerNo;
		this.newX = newX;
		this.newY = newY;
	}

	@Override
	public Quoridor playMove(Quoridor Q) {
		Quoridor _Q = new Quoridor(Q);
		Player p = _Q.getPlayer(playerNo);
		p.setXY(new Point(newX, newY));
		return _Q;
	}
	
	public String toString() {
		return new String("Player " + playerNo + " move to " + new Point(newX, newY).toString());
	}

}

class WallMove implements Move {

	int x, y, orientation, playerNo;

	public WallMove(int x, int y, int orient, int playerNo) {
		this.x = x;
		this.y = y;
		this.orientation = orient;
		this.playerNo = playerNo;
	}
	

	@Override
	public Quoridor playMove(Quoridor q) {
		Quoridor _q = new Quoridor(q);
		_q.walls.add(new Wall(x, y, orientation));
		_q.getPlayer(playerNo).decrememntWall();
		return _q;
	}
	
	public String toString() {
		return new String(((orientation == Wall.HORIZONTAL) ? "Horizontal"
				: "Vertical") + " wall at " + new Point(x, y).toString());
	}

}

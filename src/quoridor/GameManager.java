package quoridor;

import Util.Coords;

/**
 * @author The NG Family Manages the Quoridor object, generating moves
 */
public class GameManager {

	public static void main(String[] str) {
		PlayerMove p1MoveLeft = new PlayerMove(1, Coords.LEFT);
		Quoridor Q = new Quoridor();
		Q = p1MoveLeft.playMove(Q);
		Q.display();
	}

}

interface Move {

	/**
	 * @param q
	 *            Quoridor game
	 * @return new deep instance with move made
	 */
	public Quoridor playMove(Quoridor q);
}

class PlayerMove implements Move {

	int playerNo, direction;

	public PlayerMove(int playerNo, int direction) {
		this.playerNo = playerNo;
		this.direction = direction;
	}

	@Override
	public Quoridor playMove(Quoridor Q) {
		Quoridor _Q = new Quoridor(Q);
		Player p = _Q.getPlayer(playerNo);
		p.setXY(Coords.move(p.getPoint(), direction));
		return _Q;
	}

}

class WallMove implements Move {

	int x, y, orientation;

	public WallMove(int x, int y, int orient) {
		this.x = x;
		this.y = y;
		this.orientation = orient;
	}

	@Override
	public Quoridor playMove(Quoridor q) {
		Quoridor _q = new Quoridor(q);
		_q.walls.add(new Wall(x, y, orientation));
		return _q;
	}

}

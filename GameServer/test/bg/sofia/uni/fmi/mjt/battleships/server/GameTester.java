package bg.sofia.uni.fmi.mjt.battleships.server;

import org.junit.Test;
import static org.junit.Assert.*;

public class GameTester {

	@Test
	public void GameTest(){
		Game game = new Game("game", "creator");

		assertEquals(game.getCreator(), "creator");
		assertEquals(game.getGameName(), "game");
		assertEquals(game.getStatus(), Game.Status.PENDING);

		game.addOpponent("opponent");

		assertEquals(game.getOponent(), "opponent");
		assertEquals(game.getStatus(), Game.Status.IN_PROGRESS);
	}
}

package bg.sofia.uni.fmi.mjt.battleships.player;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class GameManagerTester {

	@Test
	public void saveGameTest(){
		GameManager.saveGame(new GameBoard(true), new GameBoard(false), "gameName",
				"opponent", true);

		File saved = new File("resources/" + "gameName" + ".txt");
		assertTrue(saved.exists());
	}
}

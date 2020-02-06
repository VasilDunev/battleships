package bg.sofia.uni.fmi.mjt.battleships.player;


import org.junit.Test;

import static org.junit.Assert.*;

public class GameBoardTester {

	@Test
	public void validateTurnTest(){
		GameBoard gameBoard = new GameBoard(true);
		assertTrue(gameBoard.validateTurn("A1"));
		assertTrue(gameBoard.validateTurn("A10"));

		assertFalse(gameBoard.validateTurn("A1 r"));
		assertFalse(gameBoard.validateTurn("A1r"));
		assertFalse(gameBoard.validateTurn("Ar"));
		assertFalse(gameBoard.validateTurn("M1"));
	}

	@Test
	public void placeShip(){
		GameBoard gameBoard = new GameBoard(true);
		assertTrue(gameBoard.placeShip("A1 right", 5));
		assertTrue(gameBoard.placeShip("J1 up", 5));
		assertTrue(gameBoard.placeShip("A10 left", 5));
		assertTrue(gameBoard.placeShip("C2 down", 5));

		assertFalse(gameBoard.placeShip("C2 right", 5));
		assertFalse(gameBoard.placeShip("A1 r ight", 5));
		assertFalse(gameBoard.placeShip("A 1 right", 5));
		assertFalse(gameBoard.placeShip("A1 up", 5));
	}

	@Test
	public void isHitTest(){
		GameBoard gameBoard = new GameBoard(true);

		gameBoard.placeShip("C2 right", 5);
		assertTrue(gameBoard.isHit("C3"));
		assertFalse(gameBoard.isHit("C1"));

		gameBoard.placeShip("A1 down", 5);
		assertTrue(gameBoard.isHit("D1"));
	}
}

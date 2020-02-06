package bg.sofia.uni.fmi.mjt.battleships.player;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

import static bg.sofia.uni.fmi.mjt.battleships.player.Player.write;

public class GamePlay {

	private static final int FIVE = 5;
	private static final int FOUR = 4;
	private static final int THREE = 3;
	private static final int TWO = 2;
	static int currentWins;

	static void startGame(String gameName, String oponentName, SocketChannel socketChannel, boolean myTurn,
	                      Scanner scanner)
			throws IOException {
		GameBoard myBoard = new GameBoard(true);
		GameBoard enemyBoard = new GameBoard(false);

		System.out.println("Place your ships by writing their first coordinate and" +
				                   " their direction.\nUse up, down, left and right!\n" +
				                   "Example: B2 down");

		placeShip(" ", FIVE, myBoard, scanner);
		placeShip(" first ", FOUR, myBoard, scanner);
		placeShip(" second ", FOUR, myBoard, scanner);
		placeShip(" first ", THREE, myBoard, scanner);
		placeShip(" second ", THREE, myBoard, scanner);
		placeShip(" third ", THREE, myBoard, scanner);
		placeShip(" first ", TWO, myBoard, scanner);
		placeShip(" second ", TWO, myBoard, scanner);
		placeShip(" third ", TWO, myBoard, scanner);
		placeShip(" fourth ", TWO, myBoard, scanner);

		System.out.println("The game starts!!");


		continueGame(myBoard, enemyBoard, gameName, oponentName, myTurn, socketChannel, scanner);
	}

	static void continueGame(GameBoard myBoard, GameBoard enemyBoard,
	                                 String gameName, String oponentName, boolean myTurn,
	                                 SocketChannel socketChannel, Scanner scanner) throws IOException {
		String lastTurnOponent = " ";

		while (true) {
			//TODO: add random stuff like tips and encouraging phrases
			if (myTurn) {
				myBoard.print();
				System.out.println();

				enemyBoard.print();
				System.out.println();

				System.out.println(oponentName + "'s last turn: " + lastTurnOponent);
				System.out.println("Enter your turn: ");
				String turn = scanner.next();

				if (turn.equals("save-game")){
					write(socketChannel, "save-game " + gameName + " " + oponentName);
					GameManager.saveGame(myBoard, enemyBoard, gameName, oponentName, true);
					scanner.nextLine();
					break;
				}

				while (!enemyBoard.validateTurn(turn)) {
					turn = scanner.next();
				}

				String messageFromServer;
				try {
					write(socketChannel, "current-turn " + oponentName + " " + turn);
					messageFromServer = Player.read(socketChannel);
				} catch (IOException exception) {
					System.out.println("The connection got lost during the game");
					throw new IOException();
				}

				if ("miss".equals(messageFromServer)) {
					myTurn = false;
					enemyBoard.markWith(turn, false);
					System.out.println("You missed!");
				} else if ("hit".equals(messageFromServer)) {
					enemyBoard.markWith(turn, true);
					if (myBoard.isWon()) {
						write(socketChannel, "WIN-WIN " + gameName);
						System.out.println("You win! Play again!");
						currentWins++;
						break;
					}

					System.out.println("You got a hit! It's your turn again!");
				}
			}
			if (!myTurn) {
				System.out.println("It's " + oponentName + "'s turn!");
				String messageFromServer;
				try {
					messageFromServer = Player.read(socketChannel);
				} catch (IOException exception) {
					System.out.println("The connection got lost during the game");
					throw new IOException();
				}

				if (messageFromServer.equals("save-game")){
					GameManager.saveGame(myBoard, enemyBoard, gameName, oponentName, false);
					break;
				}

				if (myBoard.isHit(messageFromServer)) {
					if (myBoard.isLost()) {
						write(socketChannel, "LOSE-LOSE " + gameName);
						System.out.println("You lose! Play again!");
						break;
					}

					write(socketChannel, "send-hit");
					System.out.println("You were hit!");

				} else {
					myTurn = true;
					write(socketChannel, "send-miss");
					System.out.println("You were missed!");
				}

				lastTurnOponent = messageFromServer;
			}
		}

	}

	private static void placeShip(String number, int lengthShip, GameBoard myBoard, Scanner scanner) {
		myBoard.print();
		System.out.println("Place your" + number + lengthShip + "-tile ship: ");

		String position = scanner.nextLine();

		while (!myBoard.placeShip(position, lengthShip)) {
			System.out.println("Please input a valid placing for the " + lengthShip + "-tile ship!");
			position = scanner.nextLine();
		}
	}
}

package bg.sofia.uni.fmi.mjt.battleships.player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GameBoard implements Serializable {

	private static final int TWO = 2;
	private static final int THIRTEEN = 13;
	private static final int TWENTY_THREE = 23;
	private static final int THIRTY = 30;
	private static final int THREE = 3;
	private static final int FOUR = 4;
	private static final int FIVE = 5;
	private static final int SIX = 6;
	private static final int SEVEN = 7;
	private static final int EIGHT = 8;
	private static final int NINE = 9;
	private static final int TEN = 10;
	private static final int ELEVEN = 11;
	private static final int TWELVE = 12;
	private static final int FOURTEEN = 14;
	private static final int FIFTEEN = 15;
	private static final int SIXTEEN = 16;
	private static final int SEVENTEEN = 17;
	private static final int TWENTY_ONE = 21;

	private char[][] board;
	private boolean isMine;
	private Map<Character, Integer> charsToNums;
	private int enemySips;
	private int myShips;

	GameBoard(boolean isMine) {
		this.isMine = isMine;
		this.board = new char[THIRTEEN][TWENTY_THREE];
		this.charsToNums = new HashMap<>();
		this.enemySips = THIRTY;
		this.myShips = THIRTY;
		fill();
		map();
	}

	private void map() {
		charsToNums.put('A', THREE);
		charsToNums.put('B', FOUR);
		charsToNums.put('C', FIVE);
		charsToNums.put('D', SIX);
		charsToNums.put('E', SEVEN);
		charsToNums.put('F', EIGHT);
		charsToNums.put('G', NINE);
		charsToNums.put('H', TEN);
		charsToNums.put('I', ELEVEN);
		charsToNums.put('J', TWELVE);
	}

	private void fill() {

		//First row
		for (int i = 0; i < SIX; i++) {
			this.board[0][i] = ' ';
		}

		if (isMine) {
			this.board[0][SIX] = ' ';
			this.board[0][SEVEN] = 'Y';
			this.board[0][EIGHT] = 'O';
			this.board[0][NINE] = 'U';
			this.board[0][TEN] = 'R';
		} else {
			this.board[0][SIX] = 'E';
			this.board[0][SEVEN] = 'N';
			this.board[0][EIGHT] = 'E';
			this.board[0][NINE] = 'M';
			this.board[0][TEN] = 'Y';
		}

		this.board[0][ELEVEN] = ' ';
		this.board[0][TWELVE] = 'B';
		this.board[0][THIRTEEN] = 'O';
		this.board[0][FOURTEEN] = 'A';
		this.board[0][FIFTEEN] = 'R';
		this.board[0][SIXTEEN] = 'D';

		for (int i = SEVENTEEN; i < this.board[0].length; i++) {
			this.board[0][i] = ' ';
		}

		//Second row
		this.board[1][0] = ' ';
		this.board[1][1] = ' ';

		char currNum = '1';
		for (int i = THREE, j = TWO; i < board[1].length; i += TWO, j += TWO, currNum++) {
			if (currNum == ':') {
				this.board[1][i] = '1';
				this.board[1][i + 1] = '0';
			} else {
				this.board[1][i] = currNum;
			}
			this.board[1][j] = ' ';
		}

		//Third row
		this.board[TWO][0] = ' ';
		this.board[TWO][1] = ' ';
		this.board[TWO][TWO] = ' ';

		for (int i = THREE, j = FOUR; i < board[1].length - 1; i += TWO, j += TWO) {
			this.board[TWO][i] = '_';
			this.board[TWO][j] = ' ';
		}

		//Every row's beginning
		char row = 'A';
		for (int i = THREE; i < board.length; i++, row++) {
			this.board[i][0] = row;
			this.board[i][1] = ' ';
		}

		//The map
		for (int i = THREE; i < board.length; i++) {
			for (int j = TWO; j < board[i].length; j++) {
				if (j % TWO == 0) {
					this.board[i][j] = '|';
				} else {
					this.board[i][j] = '_';
				}
			}
		}
	}

	void print() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				System.out.print(this.board[i][j]);
			}
			System.out.println();
		}
	}

	boolean validateTurn(String turn) {
		if (turn == null || !charsToNums.containsKey(turn.charAt(0))
				                    || (turn.length() < TWO || turn.length() > THREE)) {
			return false;
		}

		int firstCoordinate = this.charsToNums.get(turn.charAt(0));
		int secondCoordinate;
		if (turn.length() == THREE) {
			secondCoordinate = TWENTY_ONE;
		} else {
			secondCoordinate = turn.charAt(1) - '0';
		}

		if (turn.length() == TWO && (secondCoordinate < 1 || secondCoordinate > NINE)
				    || (turn.length() == THREE && (turn.charAt(1) != '1'
						                                   || turn.charAt(TWO) != '0'))) {
			return false;
		}

		if (turn.length() == TWO) {
			secondCoordinate = (turn.charAt(1) - '0') * TWO + 1;
		}

		return board[firstCoordinate][secondCoordinate] == '_';
	}

	void markWith(String turn, boolean isHit) {
		int firstCoordinate = this.charsToNums.get(turn.charAt(0));
		int secondCoordinate;

		if (turn.length() == THREE) {
			secondCoordinate = TWENTY_ONE;
		} else {
			secondCoordinate = (turn.charAt(1) - '0') * TWO + 1;
		}

		if (isHit) {
			this.enemySips--;
			this.board[firstCoordinate][secondCoordinate] = 'X';
		}
		else{
			this.board[firstCoordinate][secondCoordinate] = 'O';
		}

	}

	boolean isWon() {
		return this.enemySips == 0;
	}

	boolean isLost() {
		return this.myShips == 0;
	}

	boolean isHit(String turn) {
		int firstCoordinate = this.charsToNums.get(turn.charAt(0));
		int secondCoordinate;

		if (turn.length() == THREE) {
			secondCoordinate = TWENTY_ONE;
		} else {
			secondCoordinate = (turn.charAt(1) - '0') * TWO + 1;
		}

		if (this.board[firstCoordinate][secondCoordinate] == '*') {
			this.board[firstCoordinate][secondCoordinate] = 'X';
			this.myShips--;
			return true;
		} else {
			this.board[firstCoordinate][secondCoordinate] = 'O';
			return false;
		}
	}

	boolean placeShip(String position, int size) {
		if (position == null) {
			return false;
		}

		String[] placing = position.split(" ");
		int coordinatesLength = placing[0].length();

		if (!this.charsToNums.containsKey(placing[0].charAt(0)) || placing.length > TWO
				    || (placing[0].length() < TWO || placing[0].length() > THREE)) {
			return false;
		}

		int firstCoordinate = this.charsToNums.get(placing[0].charAt(0));
		int secondCoordinate;
		if (coordinatesLength == THREE) {
			secondCoordinate = TWENTY_ONE;
		} else {
			secondCoordinate = placing[0].charAt(1) - '0';
		}
		if (placing.length != TWO || (coordinatesLength == TWO && (secondCoordinate < 1 || secondCoordinate > NINE)
				                              || (coordinatesLength == THREE && (placing[0].charAt(1) != '1'
						                      || placing[0].charAt(TWO) != '0')))) {
			return false;
		}
		if (coordinatesLength == TWO) {
			secondCoordinate = (placing[0].charAt(1) - '0') * TWO + 1;
		}

		if (placing[1].equals("down")) {
			if (firstCoordinate + size - 1 < this.board.length) {
				for (int i = firstCoordinate; i <= firstCoordinate + size - 1; i++) {
					if (this.board[i][secondCoordinate] == '*') {
						return false;
					}
				}

				for (int i = firstCoordinate; i <= firstCoordinate + size - 1; i++) {
					this.board[i][secondCoordinate] = '*';
				}

				return true;
			} else {
				return false;
			}
		} else if (placing[1].equals("up")) {
			if (firstCoordinate - size + 1 > TWO) {
				for (int i = firstCoordinate; i >= firstCoordinate - size + 1; i--) {
					if (this.board[i][secondCoordinate] == '*') {
						return false;
					}
				}

				for (int i = firstCoordinate; i >= firstCoordinate - size + 1; i--) {
					this.board[i][secondCoordinate] = '*';
				}

				return true;
			} else {
				return false;
			}
		} else if (placing[1].equals("left")) {
			if (secondCoordinate - (size - 1) * TWO > TWO) {
				for (int i = secondCoordinate; i >= secondCoordinate - (size - 1) * TWO; i -= TWO) {
					if (this.board[firstCoordinate][i] == '*') {
						return false;
					}
				}

				for (int i = secondCoordinate; i >= secondCoordinate - (size - 1) * TWO; i -= TWO) {
					this.board[firstCoordinate][i] = '*';
				}

				return true;
			} else {
				return false;
			}
		} else if (placing[1].equals("right")) {
			if (secondCoordinate + (size - 1) * TWO < this.board[0].length) {
				for (int i = secondCoordinate; i <= secondCoordinate + (size - 1) * TWO; i += TWO) {
					if (this.board[firstCoordinate][i] == '*') {
						return false;
					}
				}

				for (int i = secondCoordinate; i <= secondCoordinate + (size - 1) * TWO; i += TWO) {
					this.board[firstCoordinate][i] = '*';
				}

				return true;
			} else {
				return false;
			}
		}

		return false;
	}
}

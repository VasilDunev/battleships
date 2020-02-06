package bg.sofia.uni.fmi.mjt.battleships.server;

public class Game {


	private static int currID = 1;
	private int id;
	private String gameName;
	private String creator;
	private String turn;
	private Status status;
	private String opponent;

	Game(String gameName, String creator) {
		this.gameName = gameName;
		this.creator = creator;
		this.turn = creator;
		this.status = Status.PENDING;
		this.id = currID++;
	}

	void addOpponent(String oponent){
		this.opponent = oponent;
		this.status = Status.IN_PROGRESS;
	}
	enum Status{
		PENDING, IN_PROGRESS
	}

	String getGameName() {
		return this.gameName;
	}

	Status getStatus() {
		return this.status;
	}

	String getCreator(){
		return this.creator;
	}

	String getOponent(){
		return this.opponent;
	}


	public int getId() {
		return id;
	}

	String getTurn(){
		if (turn.equals(creator)){
			turn = opponent;
			return creator;
		}
		else{
			turn = creator;
			return opponent;
		}
	}
}

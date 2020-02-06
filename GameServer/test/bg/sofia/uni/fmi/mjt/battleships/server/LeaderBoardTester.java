package bg.sofia.uni.fmi.mjt.battleships.server;

import org.junit.Test;
import static org.junit.Assert.*;

public class LeaderBoardTester {

	@Test
	public void getTop5Test(){
		LeaderBoard.addWinner("pesho");
		LeaderBoard.addWinner("pesho");
		LeaderBoard.addWinner("gosho");

		LeaderBoard.addLoser("pesho");
		LeaderBoard.addLoser("gosho");
		LeaderBoard.addLoser("gosho");

		assertEquals("pesho\ngosho\n", LeaderBoard.getTop5("winners") );
		assertEquals("gosho\npesho\n", LeaderBoard.getTop5("losers") );

	}
}

package bg.sofia.uni.fmi.mjt.battleships.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ServerProcessor {

	private static final int TWO = 2;
	private static final int THREE = 3;
	private static final int BUFFER_SIZE = 1024;

	private static ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

	static void process(SocketChannel sc) throws IOException {
		buffer.clear();
		int r = sc.read(buffer);
		if (r <= 0) {
			System.out.println("nothing to read, will close channel");
			sc.close();
			return;
		}

		buffer.flip();
		String message = new String(buffer.array(), 0, buffer.limit());
		String[] commands = message.split(" ");

		if (commands.length == TWO && "username".equals(commands[0])) {
			if (ServerFunctionalities.addUser(sc, commands[1])) {
				sc.write(StandardCharsets.UTF_8.encode(("valid")));
			} else {
				sc.write(StandardCharsets.UTF_8.encode(("username is already used")));
			}

		} else if (commands.length == 1 && "view-active-users".equals(commands[0])) {
			ServerFunctionalities.viewActiveUsers(sc);
		} else if (commands.length == 1 && "disconnect".equals(commands[0])) {
			ServerFunctionalities.onlineUsers.remove(ServerFunctionalities.onlineUsersSockets.get(sc), sc);
			ServerFunctionalities.onlineUsersSockets.remove(sc);
			sc.write(StandardCharsets.UTF_8.encode("Disconnected from server"));
			sc.close();
			ServerFunctionalities.onlineNow--;
		} else if (commands.length == TWO && "create-game".equals(commands[0])) {
			Game game = new Game(commands[1], ServerFunctionalities.onlineUsersSockets.get(sc));
			ServerFunctionalities.games.put(game.getGameName(), game);
		} else if ("join-game".equals(commands[0])) {
			ServerFunctionalities.joinGame(sc, commands);
		} else if (commands.length == THREE && "current-turn".equals(commands[0])) {
			ServerFunctionalities.currentTurn(sc, commands);
		} else if (commands.length == TWO && "WIN-WIN".equals(commands[0])) {
			ServerFunctionalities.games.remove(commands[1]);
			LeaderBoard.addWinner(ServerFunctionalities.onlineUsersSockets.get(sc));
		} else if (commands.length == THREE && "save-game".equals(commands[0])) {
			ServerFunctionalities.games.remove(commands[1]);
			ServerFunctionalities.onlineUsers.get(commands[TWO]).write(StandardCharsets.UTF_8.encode("save-game"));
		} else if (commands.length == 1 && "show-biggest-winners".equals(commands[0])) {
			sc.write(StandardCharsets.UTF_8.encode(LeaderBoard.getTop5("winners")));
		} else if (commands.length == 1 && "show-biggest-losers".equals(commands[0])) {
			sc.write(StandardCharsets.UTF_8.encode(LeaderBoard.getTop5("losers")));
		} else if (commands.length == 1 && "list-games".equals(commands[0])) {
			sc.write(StandardCharsets.UTF_8.encode(ServerFunctionalities.showCurrentGames()));
		} else if (commands.length == THREE && "load-game".equals(commands[0])) {
			if (!ServerFunctionalities.games.containsKey(commands[1])) {
				Game game = new Game(commands[1], ServerFunctionalities.onlineUsersSockets.get(sc));
				ServerFunctionalities.games.put(game.getGameName(), game);
			} else {
				String turn = ServerFunctionalities.games.get(commands[1]).getTurn();
				ServerFunctionalities.games.get(commands[1]).addOpponent(ServerFunctionalities
						                                                   .onlineUsersSockets.get(sc) + " " + turn);
				sc.write(StandardCharsets.UTF_8.encode("ready"));
				ServerFunctionalities.onlineUsers.get(ServerFunctionalities.games.get(commands[1]).getCreator())
						.write(StandardCharsets.UTF_8.encode("ready"));
			}
		}
	}


}

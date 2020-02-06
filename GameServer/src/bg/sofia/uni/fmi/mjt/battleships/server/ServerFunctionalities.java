package bg.sofia.uni.fmi.mjt.battleships.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ServerFunctionalities {

	private static final int BUFFER_SIZE = 1024;
	private static final int TWO = 2;
	private static final int FOUR = 4;
	private static final int SEVEN = 7;
	private static final int TWENTY_NINE = 29;
	private static ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

	static int onlineNow = 0;
	static Map<String, SocketChannel> onlineUsers = new HashMap<>();
	static Map<SocketChannel, String> onlineUsersSockets = new HashMap<>();
	static Map<String, Game> games = new HashMap<>();

	static boolean addUser(SocketChannel sc, String username){
		if (onlineUsers.containsKey(username)){
			return false;
		}

		if (onlineUsersSockets.containsKey(sc)){
			onlineUsers.remove(onlineUsersSockets.get(sc));
			onlineUsersSockets.remove(sc);
			onlineNow--;
		}

		onlineUsers.put(username, sc);
		onlineUsersSockets.put(sc, username);
		onlineNow++;

		return true;
	}

	static void viewActiveUsers(SocketChannel sc) throws IOException {
		int i = 0;
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, SocketChannel> entry : onlineUsers.entrySet()) {
			sb.append(entry.getKey());
			if (++i != onlineNow) {
				sb.append(", ");
			}
		}

		sc.write(StandardCharsets.UTF_8.encode(sb.toString()));
	}

	static String showCurrentGames(){
		int biggestNameGame = 4;
		int biggestNameCreator = 7;

		for(Map.Entry<String, Game> entry: games.entrySet()){
			if (entry.getKey().length() > biggestNameGame){
				biggestNameGame = entry.getKey().length();
			}
			if (entry.getValue().getCreator().length() > biggestNameCreator){
				biggestNameCreator = entry.getValue().getCreator().length();
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(" ");
		for (int i = 0; i < TWENTY_NINE + biggestNameCreator + biggestNameGame; i++) {
			sb.append("_");
		}
		sb.append(" \n");
		sb.append("| NAME");
		for(int i = 0; i < biggestNameGame - FOUR; i++){
			sb.append(" ");
		}
		sb.append(" | CREATOR");
		for(int i = 0; i < biggestNameCreator - SEVEN; i++){
			sb.append(" ");
		}
		sb.append(" |    STATUS   | PLAYERS |\n");
		sb.append("|-----");
		for(int i = 0; i < biggestNameGame - FOUR; i++){
			sb.append("-");
		}
		sb.append("-+--------");
		for(int i = 0; i < biggestNameCreator - SEVEN; i++){
			sb.append("-");
		}
		sb.append("-+-------------+---------|\n");

		for(Map.Entry<String, Game> entry: games.entrySet()){
			sb.append("| ").append(entry.getKey());
			for(int i = 0; i < biggestNameGame - entry.getKey().length(); i++){
				sb.append(" ");
			}
			sb.append(" | ").append(entry.getValue().getCreator());
			for (int i = 0; i < biggestNameCreator - entry.getValue().getCreator().length(); i++) {
				sb.append(" ");
			}
			sb.append(" | ");
			if (entry.getValue().getStatus().equals(Game.Status.PENDING)){
				sb.append("pending     | 1/2     |\n");
			}
			else{
				sb.append("in progress | 2/2     |\n");
			}
		}

		sb.append(" ");
		for (int i = 0; i < TWENTY_NINE + biggestNameCreator + biggestNameGame; i++) {
			sb.append("_");
		}
		sb.append(" \n");

		//In debug it is returned twice and in normal run nothing happens
		return sb.toString();
	}

	static void joinGame(SocketChannel sc, String[] commands) throws IOException {
		if (commands.length == TWO && "join-game".equals(commands[0])){
			if (!games.containsKey(commands[1]) || games.get(commands[1]).getStatus()
					                                       .equals(Game.Status.IN_PROGRESS)){
				sc.write(StandardCharsets.UTF_8.encode("There seems to be no " +
						                                       "pending game with this name"));
			}
			else{
				String turn = games.get(commands[1]).getTurn();
				games.get(commands[1]).addOpponent(onlineUsersSockets.get(sc)
						                                                   + " " + turn);
				sc.write(StandardCharsets.UTF_8.encode(games.get(commands[1]).getCreator() +
						                                       " " + turn));
				onlineUsers.get(games.get(commands[1]).getCreator()).write(
						StandardCharsets.UTF_8.encode(games.get(commands[1]).getOponent() +
								                              " " + turn));
			}
		} else if (commands.length == 1 && "join-game".equals(commands[0])){
			boolean isEmpty = true;
			Game game = null;
			for(Map.Entry<String, Game> entry: games.entrySet()){
				if (entry.getValue().getStatus().equals(Game.Status.PENDING) && !entry.getValue().getCreator()
						                                .equals(onlineUsersSockets.get(sc))){
					game = entry.getValue();
					isEmpty = false;
					break;
				}
			}

			if (isEmpty){
				sc.write(StandardCharsets.UTF_8.encode("There seems to be no pending game with this name. " +
						                                       "Perhaps create one"));
			}
			else{
				game.addOpponent(onlineUsersSockets.get(sc));
				String turn = game.getTurn();
				sc.write(StandardCharsets.UTF_8.encode(game.getGameName() + " " + game.getCreator()
						                                       + " " + turn));
				onlineUsers.get(game.getCreator()).write(StandardCharsets.UTF_8.encode(
						onlineUsersSockets.get(sc) + " " + turn));
			}
		}
	}

	static void currentTurn(SocketChannel sc, String[] commands) throws IOException {
		onlineUsers.get(commands[1]).write(StandardCharsets.UTF_8.encode(commands[TWO]));

		buffer.clear();
		onlineUsers.get(commands[1]).read(buffer);
		buffer.flip();
		String missOrHit = new String(buffer.array(), 0, buffer.limit());
		if(missOrHit.split(" ")[0].equals("LOSE-LOSE")){
			games.remove(missOrHit.split(" ")[1]);
			LeaderBoard.addLoser(onlineUsersSockets.get(sc));
		}
		else if (missOrHit.equals("send-hit")){
			sc.write(StandardCharsets.UTF_8.encode("hit"));
		}
		else{
			sc.write(StandardCharsets.UTF_8.encode("miss"));
		}
	}
}

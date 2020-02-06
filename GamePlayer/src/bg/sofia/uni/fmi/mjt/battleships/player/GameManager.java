package bg.sofia.uni.fmi.mjt.battleships.player;

import com.google.gson.Gson;

import java.io.*;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class GameManager {

	static void loadGame(String gameName, SocketChannel socketChannel, Scanner scanner){
		File saved = new File("resources/" + gameName + ".txt");
		if (!saved.exists()){
			System.out.println("There is no such saved game!");
			return;
		}

		try (BufferedReader br = new BufferedReader( new FileReader(saved))){
			Gson gson = new Gson();

			String myBoardJson = br.readLine();
			GameBoard myBoard = gson.fromJson(myBoardJson, GameBoard.class);

			String enemyBoardJson = br.readLine();
			GameBoard enemyBoard = gson.fromJson(enemyBoardJson, GameBoard.class);

			String opponentName = br.readLine();

			String turn = br.readLine();
			boolean myTurn = turn.equals("true");

			Player.write(socketChannel, "load-game " + gameName + " " + opponentName);
			String ready = Player.read(socketChannel);
			if ("ready".equals(ready)){
				GamePlay.continueGame(myBoard, enemyBoard, gameName, opponentName, myTurn, socketChannel, scanner);
			}

		} catch (IOException e) {
			System.out.println("There was a problem loading your game!");
		}
	}

	static void deleteGame(String gameName){
		File savedGames = new File("resources/saved-games.txt");
		if (!savedGames.exists()){
			System.out.println("There are no saved games at this time!");
			return;
		}

		File saved = new File("resources/" + gameName + ".txt");
		if (!saved.exists()){
			System.out.println("There is no such saved game!");
			return;
		}

		try (BufferedReader br = new BufferedReader( new FileReader(savedGames));
		     BufferedWriter bw = new BufferedWriter( new FileWriter(savedGames))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while(line != null){
				if (line.trim().equals(gameName)){
					line = br.readLine();
					continue;
				}
				sb.append(line).append("\n");
				line = br.readLine();
			}

			if (savedGames.delete() && saved.delete()) {
				bw.append(sb.toString());
			} else{
				System.out.println("There was a problem deleting that file");
			}

		} catch (IOException e) {
			System.out.println("There was a problem deleting your game!");
		}
	}

	static void saveGame(GameBoard myBoard, GameBoard enemyBoard,
	                             String gameName, String opponentName, boolean myTurn){
		Gson gson = new Gson();
		String myBoardJson = gson.toJson(myBoard);
		String enemyBoardJson = gson.toJson(enemyBoard);
		try (BufferedWriter br = new BufferedWriter( new FileWriter("resources/" + gameName + ".txt",
				true)); BufferedWriter savedAdd = new BufferedWriter( new FileWriter("resources" +
				                                                        "/saved-games.txt", true))) {
			savedAdd.append(gameName).append("\n");

			br.append(myBoardJson).append("\n").append(enemyBoardJson).append("\n").append(opponentName).append("\n")
					.append(myTurn? "true": "false").append("\n");
		} catch (IOException exception) {
			System.out.println("Sorry, the game could not be saved");
		}
	}

	static void printSaved(){
		File saved = new File("resources/saved-games.txt");
		if (!saved.exists()){
			System.out.println("There are no saved games at this time!");
			return;
		}

		try (BufferedReader br = new BufferedReader( new FileReader(saved))) {
			String line = br.readLine();

			while(line != null){
				System.out.println(line);
				line = br.readLine();
			}

		} catch (IOException e) {
			System.out.println("There seems to be a problem with the file!");
		}
	}


	static void printTop5(SocketChannel socketChannel) throws IOException {
		String top5 = Player.read(socketChannel);
		System.out.println(top5);
	}
}

package bg.sofia.uni.fmi.mjt.battleships.player;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Player {
	private static final int SERVER_PORT = 7777;
	private static final String SERVER_HOST = "localhost";
	private static final int TWO = 2;
	private static ByteBuffer buffer = ByteBuffer.allocate(1024);
	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		try (SocketChannel socketChannel = SocketChannel.open()) {

			socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

			System.out.println("Connected to the server.");
			System.out.println("Input username: ");
			String nick = scanner.nextLine();

			write(socketChannel, "username " + nick);
			String nicknameConfirmation = read(socketChannel);

			while(!nicknameConfirmation.equals("valid")){
				System.out.println(nicknameConfirmation);
				nick = scanner.nextLine();
				write(socketChannel, "username " + nick);
				nicknameConfirmation = read(socketChannel);
			}

			printHelp();

			while (true) {
				String sentMessage = scanner.nextLine();
				if (sentMessage == null) {
					System.out.println("Please input a command");
					continue;
				}
				write(socketChannel, sentMessage);
				String[] sentCommand = sentMessage.split(" ");

				if ("disconnect".equals(sentCommand[0])) {
					break;
				} else if ("help".equals(sentCommand[0])) {
					printHelp();
				} else if ("create-game".equals(sentCommand[0]) && sentCommand.length == TWO) {
					String currentMessage = read(socketChannel);
					GamePlay.startGame(sentCommand[1], currentMessage.split(" ")[0], socketChannel,
							currentMessage.split(" ")[1].equals(nick), scanner);
				} else if ("view-active-users".equals(sentCommand[0]) && sentCommand.length == 1) {
					String users = read(socketChannel);
					System.out.println(users);
				} else if ("join-game".equals(sentCommand[0]) && sentCommand.length > 1) {
					String messageFromServer = read(socketChannel);

					if (!"There seems to be no pending game with this name".equals(messageFromServer)) {
						GamePlay.startGame(sentCommand[1], messageFromServer.split(" ")[0], socketChannel,
								messageFromServer.split(" ")[1].equals(nick), scanner);
					}
					else{
						System.out.println(messageFromServer);
					}
				} else if ("join-game".equals(sentCommand[0])) {
					String messageFromServer = read(socketChannel);

					if (!("There seems to be no pending game with this name. Perhaps create one")
							     .equals(messageFromServer)) {
						String[] wordsFromServer = messageFromServer.split(" ");
						GamePlay.startGame(wordsFromServer[0], wordsFromServer[1], socketChannel, wordsFromServer[2]
								                                          .equals(nick), scanner);
					}
				} else if ("saved-games".equals(sentCommand[0]) && sentCommand.length == 1) {
					GameManager.printSaved();
				} else if ("load-game".equals(sentCommand[0]) && sentCommand.length == TWO){
					GameManager.loadGame(sentCommand[1], socketChannel, scanner);
				} else if ("show-current-wins".equals(sentCommand[0]) && sentCommand.length == 1){
					System.out.println("You have won " + GamePlay.currentWins + " games!");
				} else if ("show-biggest-winners".equals(sentCommand[0]) && sentCommand.length == 1){
					GameManager.printTop5(socketChannel);
				} else if ("show-biggest-losers".equals(sentCommand[0]) && sentCommand.length == 1){
					GameManager.printTop5(socketChannel);
				} else if ("delete-game".equals(sentCommand[0]) && sentCommand.length == TWO){
					GameManager.deleteGame(sentCommand[1]);
				} else if ("list-games".equals(sentCommand[0]) && sentCommand.length == 1){
					String messageFromServer = read(socketChannel);
					System.out.println(messageFromServer);
				} else{
					System.out.println("We seem to not be able to read your command. Please input it again. \n" +
							                   " If you are having trouble you can always ask for help by just \n " +
							                   "writing help in the console!");
				}
			}

		} catch (IOException e) {
			System.out.println("There seems to be a problem with the connection. Please try again!");
		}
	}

	private static void printHelp(){
		System.out.println("---------A guide to the game's functionality---------");
		System.out.println("disconnect             - to disconnect from the game");
		System.out.println("help                   - to get this message again");
		System.out.println("create-game            - to create a game. You need to input the name too!");
		System.out.println("view-active-users      - to see who is playing now too");
		System.out.println("join-game              - to see join in a game with someone else. You can" +
				           "\n                         input a specific game name or you can play with a" +
				                   " random someone");
		System.out.println("saved-games            - to see all your saved games");
		System.out.println("load-game              - to load a saved game");
		System.out.println("show-current-wins      - to see how many times you've won");
		System.out.println("show-biggest-winners   - to see the top 5 biggest winners of all time");
		System.out.println("show-biggest-losers    - to see the top 5 biggest losers of all time");
		System.out.println("save-game              - during game play to save the current game and end it for " +
				                   "the moment");
		System.out.println("delete-game            - to delete a saved game. You need to input the name too!");
		System.out.println("list-games             - to see all the current games");
		System.out.println("-----------------------------------------------------");
	}

	static void write(SocketChannel socketChannel, String toWrite) throws IOException {
		buffer.clear();
		buffer.put(toWrite.getBytes());
		buffer.flip();
		socketChannel.write(buffer);
	}

	static String read(SocketChannel socketChannel) throws IOException {
		buffer.clear();
		socketChannel.read(buffer);
		buffer.flip();
		return new String(buffer.array(), 0, buffer.limit());
	}
}

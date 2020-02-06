package bg.sofia.uni.fmi.mjt.battleships.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class GameServer {
	private static final String SERVER_HOST = "localhost";
	private static final int SERVER_PORT = 7777;
	private static final int SLEEP_MILLIS = 200;


	public static void main(String[] args) {
		try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

			serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
			serverSocketChannel.configureBlocking(false);

			Selector selector = Selector.open();
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			while (true) {
				int readyChannels = selector.select();
				if (readyChannels == 0) {
					System.out.println("Still waiting for a ready channel...");
					try {
						Thread.sleep(SLEEP_MILLIS);
					} catch (InterruptedException e) {
						System.out.println(e.getMessage());
					}
					continue;
				}

				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

				while (keyIterator.hasNext()) {
					SelectionKey key = keyIterator.next();
					if (key.isReadable()) {
						SocketChannel sc = (SocketChannel) key.channel();
						// The logic of processing is in ServerProcessor made just for that
						ServerProcessor.process(sc);

					} else if (key.isAcceptable()) {
						ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
						SocketChannel accept = sockChannel.accept();
						accept.configureBlocking(false);
						accept.register(selector, SelectionKey.OP_READ);
					}

					keyIterator.remove();
				}

			}

		} catch (IOException e) {
			System.out.println("There is a problem with the server socket");
			System.out.println(e.getMessage());
		}
	}
}


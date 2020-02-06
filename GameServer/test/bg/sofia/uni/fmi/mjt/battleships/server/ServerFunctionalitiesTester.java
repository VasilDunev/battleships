package bg.sofia.uni.fmi.mjt.battleships.server;

import org.junit.Test;
import org.mockito.Mock;

import java.nio.channels.SocketChannel;

import static org.junit.Assert.*;

public class ServerFunctionalitiesTester {

	@Mock
	SocketChannel socketChannel;

	@Test
	public void addUserTest(){
		assertTrue(ServerFunctionalities.addUser(socketChannel, "vasko"));
		assertFalse(ServerFunctionalities.addUser(socketChannel, "vasko"));
		assertTrue(ServerFunctionalities.addUser(socketChannel, "georgi"));
	}
}

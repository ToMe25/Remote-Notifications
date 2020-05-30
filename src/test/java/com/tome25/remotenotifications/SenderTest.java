package com.tome25.remotenotifications;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;

import org.junit.Test;

import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonParser;

public class SenderTest {

	@Test
	public void testSender() throws IOException, ParseException, InterruptedException {
		RemoteNotifications.initServer();
		TCPListener listener = new TCPListener(3333);
		RemoteNotifications.sender.setTcpPort(3333);
		RemoteNotifications.sender.send("Header", "Test Message");
		listener.thread.join();
		assertEquals(JsonParser.parseString("{\"header\":\"Header\",\"message\":\"Test Message\"}"), listener.received);
	}

	private class TCPListener implements Runnable {

		private final Thread thread;
		private final ServerSocket socket;
		private final int timeout = 3000;
		private JsonElement received;

		/**
		 * Creates a new TCPListener listening to the given port.
		 * 
		 * @param port the port to listen on.
		 * @throws IOException if initializing the {@link ServerSocket} fails.
		 */
		private TCPListener(int port) throws IOException {
			thread = new Thread(this, "Tcp-Listener-Thread");
			socket = new ServerSocket(port);
			thread.start();
		}

		@Override
		public void run() {
			try {
				socket.setSoTimeout(10000);
				Socket soc = socket.accept();
				int wait = 0;
				InputStream sIn = soc.getInputStream();
				while (sIn.available() == 0 && wait < timeout) {
					try {
						Thread.sleep(10);
						wait += 10;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				byte[] buffer = new byte[sIn.available()];
				sIn.read(buffer);
				received = JsonParser.parseByteArray(buffer);
				soc.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}

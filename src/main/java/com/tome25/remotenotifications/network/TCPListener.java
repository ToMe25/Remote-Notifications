package com.tome25.remotenotifications.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonParser;

/**
 * A thread listening for tcp connections on a given port.
 * 
 * @author ToMe25
 *
 */
public class TCPListener extends AbstractListener {

	private final ServerSocket socket;
	private final int timeout = 3000;
	private final int port;

	/**
	 * Creates and starts a new TCPListening listening for tcp connections on the
	 * specified port.
	 * 
	 * @param port           the port to listen on.
	 * @param receiveHandler the consumer to call when receiving a
	 *                       {@link JsonElement}.
	 * @throws IOException if initializing the {@link ServerSocket} fails.
	 */
	public TCPListener(int port, Consumer<JsonElement> receiveHandler) throws IOException {
		super("Remote-Notifications-TCP-Listener", receiveHandler);
		this.port = port;
		socket = new ServerSocket(port);
		thread.start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket soc = socket.accept();
				soc.setSoTimeout(timeout);
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
				JsonElement received = JsonParser.parseByteArray(buffer);
				handler.accept(received);
				soc.close();
			} catch (Exception e) {
				if (!isRunning()) {
					return;
				} else {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void close() {
		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

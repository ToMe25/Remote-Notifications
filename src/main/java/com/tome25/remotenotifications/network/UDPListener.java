package com.tome25.remotenotifications.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.function.BiConsumer;

import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonParser;

/**
 * A thread listening for udp packets on a given port.
 * 
 * @author ToMe25
 *
 */
public class UDPListener extends AbstractListener {

	private final DatagramSocket socket;
	private final DatagramPacket packet;
	private final int port;

	/**
	 * Creates and starts a new UDPListener listening for udp packets on the
	 * specified port.
	 * 
	 * @param port    the port to listen on.
	 * @param receiveHandler the consumer to give the received {@link JsonElement}s and the
	 *                senders {@link InetAddress} to.
	 * @throws SocketException if initializing the {@link DatagramSocket} fails.
	 */
	public UDPListener(int port, BiConsumer<JsonElement, InetAddress> receiveHandler) throws SocketException {
		super("Remote-Notifications-UDP-Listener", receiveHandler);
		this.port = port;
		socket = new DatagramSocket(port);
		packet = new DatagramPacket(new byte[1024], 1024);
		thread.start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				socket.receive(packet);
				JsonElement received = JsonParser.parseByteArray(packet.getData());
				handler.accept(received, packet.getAddress());
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
	protected void close() {
		socket.close();
	}

}

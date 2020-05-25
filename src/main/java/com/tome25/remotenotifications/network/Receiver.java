package com.tome25.remotenotifications.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.tome25.remotenotifications.notification.NotificationHandler;
import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonParser;

public class Receiver implements Runnable {

	private final Thread thread;
	private final DatagramSocket socket;
	private final DatagramPacket packet;

	/**
	 * creates and starts a new Receiver listening to the specified port.
	 * 
	 * @param port the port to listen on.
	 * @throws SocketException if initializing the Socket fails.
	 */
	public Receiver(int port) throws SocketException {
		thread = new Thread(this, "Remote-Notifications-Receiver");
		socket = new DatagramSocket(port);
		packet = new DatagramPacket(new byte[1024], 1024);
		thread.start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				socket.receive(packet);
				JsonElement notification = JsonParser.parseByteArray(packet.getData());
				NotificationHandler.displayMessage(notification.getString("header"), notification.getString("message"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

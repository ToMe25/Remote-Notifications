package com.tome25.remotenotifications.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.tome25.remotenotifications.notification.NotificationHandler;
import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonParser;

/**
 * The class receiving the Notifications to display.
 * 
 * @author ToMe25
 *
 */
public class Receiver {

	/**
	 * Creates a new Receiver listening to the given ports.
	 * 
	 * @param udpPort the port to listen on for udp packets. set to 0 to disable.
	 * @param tcpPort the port to listen on for tcp packets. set to 0 to disable
	 */
	public Receiver(int udpPort, int tcpPort) {
		if (udpPort > 0) {
			try {
				new UDPHandler(udpPort);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (tcpPort > 0) {
			try {
				new TCPHandler(tcpPort);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class UDPHandler implements Runnable {

		private final Thread thread;
		private final DatagramSocket socket;
		private final DatagramPacket packet;

		/**
		 * Creates and starts a new UDPHandler listening to the specified port.
		 * 
		 * @param port the port to listen on.
		 * @throws SocketException if initializing the Socket fails.
		 */
		public UDPHandler(int port) throws SocketException {
			thread = new Thread(this, "Remote-Notifications-Receiver-UDP-Handler");
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
					NotificationHandler.displayMessage(notification.getString("header"),
							notification.getString("message"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class TCPHandler implements Runnable {

		private final Thread thread;
		private final ServerSocket socket;
		private final int timeout = 3000;

		/**
		 * Creates and starts a new TCPHandler listening to the specified port.
		 * 
		 * @param port the port to listen on.
		 * @throws IOException if initializing the Socket fails.
		 */
		public TCPHandler(int port) throws IOException {
			thread = new Thread(this, "Remote-Notifications-Receiver-TCP-Handler");
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
					JsonElement notification = JsonParser.parseByteArray(buffer);
					NotificationHandler.displayMessage(notification.getString("header"),
							notification.getString("message"));
					soc.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}

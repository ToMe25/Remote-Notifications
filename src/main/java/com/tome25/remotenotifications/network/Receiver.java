package com.tome25.remotenotifications.network;

import java.net.InetAddress;
import java.util.function.BiConsumer;

import com.tome25.utils.json.JsonElement;

/**
 * The class receiving the Notifications to display.
 * 
 * @author ToMe25
 *
 */
public class Receiver {

	private UDPListener udpListener;
	private TCPListener tcpListener;

	/**
	 * Creates a new Receiver listening to the given ports.
	 * 
	 * @param udpPort the port to listen on for udp packets. set to 0 to disable.
	 * @param tcpPort the port to listen on for tcp packets. set to 0 to disable.
	 * @param handler the consumer to give the received {@link JsonElement}s and the
	 *                senders {@link InetAddress} to.
	 */
	public Receiver(int udpPort, int tcpPort, BiConsumer<JsonElement, InetAddress> handler) {
		if (udpPort > 0) {
			try {
				udpListener = new UDPListener(udpPort, handler);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (tcpPort > 0) {
			try {
				tcpListener = new TCPListener(tcpPort, handler);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Stops this receiver, closing all its sockets and stopping its threads.
	 */
	public void stop() {
		if (udpListener != null) {
			udpListener.stop();
		}
		if (tcpListener != null) {
			tcpListener.stop();
		}
	}

	/**
	 * Gets this receivers udp port.
	 * 
	 * @return this receivers udp port.
	 */
	public int getUDPPort() {
		return udpListener == null ? 0 : udpListener.getPort();
	}

	/**
	 * Gets this receivers tcp port.
	 * 
	 * @return this receivers tcp port.
	 */
	public int getTCPPort() {
		return tcpListener == null ? 0 : tcpListener.getPort();
	}

}

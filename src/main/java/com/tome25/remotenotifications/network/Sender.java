package com.tome25.remotenotifications.network;

import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.function.Consumer;

import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonObject;

/**
 * The class sending the notifications to the client.
 * 
 * @author ToMe25
 *
 */
public class Sender {

	private String address;
	private int udpPort;
	private int tcpPort;
	private final DatagramSocket udpSocket;
	private DatagramPacket packet;

	/**
	 * creates a new Sender sending to the specified port of the specified device.
	 * 
	 * @param receiver the target address.
	 * @param udpPort  the target udp port. set to 0 to disable.
	 * @param tcpPort  the target tcp port. set to 0 to disable.
	 * @throws SocketException      if the creation of the Socket fails.
	 * @throws UnknownHostException if no IP address for the host could be found, or
	 *                              if a scope_id was specified for a global IPv6
	 *                              address.
	 */
	public Sender(String receiver, int udpPort, int tcpPort) throws SocketException, UnknownHostException {
		udpSocket = new DatagramSocket();
		this.address = receiver;
		this.udpPort = udpPort;
		this.tcpPort = tcpPort;
	}

	/**
	 * Sends the given message to this senders target.
	 * 
	 * @param header  the notification header.
	 * @param message the notification message.
	 */
	public void send(String header, String message) {
		JsonObject json = new JsonObject("message", message);
		json.add("header", header);
		send(json);
	}

	/**
	 * Sends the given json object to this senders target.
	 * 
	 * @param message the message to send. should be
	 *                '{"header":"HEADER","message":"MESSAGE"}'
	 */
	public void send(JsonElement message) {
		send(message, e -> {
			if (!(e instanceof ConnectException) || (udpPort < 1 || tcpPort < 1)) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Sends the given json object to this senders target.
	 * 
	 * @param message          the message to send. should be
	 *                         '{"header":"HEADER","message":"MESSAGE"}'
	 * @param exceptionHandler a consumer that handles exceptions if any occur.
	 */
	public void send(JsonElement message, Consumer<Exception> exceptionHandler) {
		if (tcpPort > 0) {
			try {
				Socket tcpSocket = new Socket(address, tcpPort);
				tcpSocket.getOutputStream().write(message.toByteArray());
				tcpSocket.close();
				return;
			} catch (Exception e) {
				exceptionHandler.accept(e);
			}
		}
		if (udpPort > 0) {
			try {
				if (packet == null) {
					packet = new DatagramPacket(new byte[1024], 1024, InetAddress.getByName(address), udpPort);
				}
				packet.setData(message.toByteArray());
				udpSocket.send(packet);
			} catch (Exception e) {
				exceptionHandler.accept(e);
			}
		}
	}

	/**
	 * Sets the address to send future notifications to.
	 * 
	 * @param address the address to send future notifications to.
	 */
	public void setAddress(String address) {
		this.address = address;
		packet = null;
	}

	/**
	 * Sets the udp port of the client to send future notifications to.
	 * 
	 * @param port the port of the client to send future notifications to.
	 */
	public void setUdpPort(int port) {
		this.udpPort = port;
		packet = null;
	}

	/**
	 * Sets the tcp port of the client to send future notifications to.
	 * 
	 * @param port the port of the client to send future notifications to.
	 */
	public void setTcpPort(int port) {
		this.tcpPort = port;
	}

}

package com.tome25.remotenotifications.network;

import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

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
	 * @throws IOException if sending the packet fails.
	 */
	public void send(String header, String message) throws IOException {
		JsonObject json = new JsonObject("message", message);
		json.add("header", header);
		send(json);
	}

	/**
	 * Sends the given json object to this senders target.
	 * 
	 * @param message the message to send. should be
	 *                '{"header":"HEADER","message":"MESSAGE"}'
	 * @throws IOException if sending the packet fails.
	 */
	public void send(JsonElement message) throws IOException {
		if (tcpPort > 0) {
			try {
				Socket tcpSocket = new Socket(address, tcpPort);
				tcpSocket.getOutputStream().write(message.toByteArray());
				tcpSocket.close();
				return;
			} catch (Exception e) {
				if(!(e instanceof ConnectException)) {
					e.printStackTrace();
				}
			}
		}
		if (udpPort > 0) {
			if (packet == null) {
				packet = new DatagramPacket(new byte[1024], 1024, InetAddress.getByName(address), udpPort);
			}
			packet.setData(message.toByteArray());
			udpSocket.send(packet);
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
	 * Sets the port of the client to send future notifications to.
	 * 
	 * @param port the port of the client to send future notifications to.
	 */
	public void setPort(int port) {
		this.udpPort = port;
		packet = null;
	}

}

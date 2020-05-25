package com.tome25.remotenotifications.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonObject;

public class Sender {

	private final DatagramSocket socket;
	private final DatagramPacket packet;

	/**
	 * creates a new Sender sending to the specified port of the specified device.
	 * 
	 * @param port     the target port.
	 * @param receiver the target address.
	 * @throws SocketException      if the creation of the Socket fails.
	 * @throws UnknownHostException if no IP address for the host could be found, or
	 *                              if a scope_id was specified for a global IPv6
	 *                              address.
	 */
	public Sender(int port, String receiver) throws SocketException, UnknownHostException {
		socket = new DatagramSocket();
		packet = new DatagramPacket(new byte[1024], 1024, InetAddress.getByName(receiver), port);
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
		packet.setData(message.toByteArray());
		socket.send(packet);
	}

}

package com.tome25.remotenotifications.network;

import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.function.Consumer;

import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonObject;

/**
 * The class sending the notifications to the clients.
 * 
 * @author ToMe25
 *
 */
public class Sender {

	private final DatagramSocket udpSocket;
	private DatagramPacket packet;
	private List<UDPTCPAddress> receivers;

	/**
	 * Creates a new Sender sending to the specified port of the specified device.
	 * 
	 * @param receivers the list of addresses to send the messages to. This is the
	 *                  list that will end up getting used, so changing it will
	 *                  change where packets get send to.
	 * @throws SocketException if the creation of the Socket fails.
	 */
	public Sender(List<UDPTCPAddress> receivers) throws SocketException {
		this.receivers = receivers;
		udpSocket = new DatagramSocket();
	}

	/**
	 * Sets the addresses to send the messages to.
	 * 
	 * @param receivers the list of addresses to send the messages to. This is the
	 *                  list that will end up getting used, so changing it will
	 *                  change where packets get send to.
	 */
	public void setReceivers(List<UDPTCPAddress> receivers) {
		this.receivers = receivers;
	}

	/**
	 * Sends the given notification to this senders receivers.
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
	 * Sends the client ports to this senders receivers.
	 * 
	 * @param udp the udp port of this client.
	 * @param tcp the tcp port of this client.
	 */
	public void send(int udp, int tcp) {
		JsonObject json = new JsonObject("udp", udp);
		json.add("tcp", tcp);
		send(json);
	}

	/**
	 * Sends the given json object to this senders clients.
	 * 
	 * @param message the message to send. should be
	 *                '{"header":"HEADER","message":"MESSAGE"}'
	 */
	public void send(JsonElement message) {
		send(message, e -> {
			if (!(e instanceof ConnectException)) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Sends the given json object to this senders clients.
	 * 
	 * @param message          the message to send. should be
	 *                         '{"header":"HEADER","message":"MESSAGE"}'
	 * @param exceptionHandler a consumer that handles exceptions if any occur.
	 */
	public void send(JsonElement message, Consumer<Exception> exceptionHandler) {
		send(message, exceptionHandler, receivers);
	}

	/**
	 * Sends the given json object to the give client.
	 * 
	 * @param message          the message to send. should be
	 *                         '{"header":"HEADER","message":"MESSAGE"}'
	 * @param exceptionHandler a consumer that handles exceptions if any occur.
	 * @param clients          the clients to send the message to.
	 */
	public void send(JsonElement message, Consumer<Exception> exceptionHandler, List<UDPTCPAddress> clients) {
		clients.forEach(client -> send(message, exceptionHandler, client));
	}

	/**
	 * Sends the given json object to the give client.
	 * 
	 * @param message          the message to send. should be
	 *                         '{"header":"HEADER","message":"MESSAGE"}'
	 * @param exceptionHandler a consumer that handles exceptions if any occur.
	 * @param client           the client to send the message to.
	 */
	public void send(JsonElement message, Consumer<Exception> exceptionHandler, UDPTCPAddress client) {
		if (client.getTcpPort() > 0) {
			try {
				Socket tcpSocket = new Socket(client.getAddress(), client.getTcpPort());
				tcpSocket.getOutputStream().write(message.toByteArray());
				tcpSocket.close();
				return;
			} catch (Exception e) {
				exceptionHandler.accept(e);
			}
		}
		if (client.getUdpPort() > 0) {
			try {
				if (packet == null) {
					packet = new DatagramPacket(new byte[1024], 1024, InetAddress.getByName(client.getAddress()),
							client.getUdpPort());
				}
				packet.setData(message.toByteArray());
				packet.setAddress(InetAddress.getByName(client.getAddress()));
				packet.setPort(client.getUdpPort());
				udpSocket.send(packet);
			} catch (Exception e) {
				exceptionHandler.accept(e);
			}
		}
	}

}

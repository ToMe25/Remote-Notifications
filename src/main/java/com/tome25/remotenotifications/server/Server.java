package com.tome25.remotenotifications.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tome25.remotenotifications.network.Receiver;
import com.tome25.remotenotifications.network.Sender;
import com.tome25.remotenotifications.network.UDPTCPAddress;
import com.tome25.remotenotifications.server.config.ServerConfig;
import com.tome25.utils.json.JsonArray;
import com.tome25.utils.json.JsonObject;
import com.tome25.utils.lib.LibraryLoader;

/**
 * The server main class. This class handles initializing all the server stuff.
 * 
 * @author ToMe25
 *
 */
public class Server {

	private Sender sender;
	private Receiver receiver;
	private ServerConfig config;
	private List<UDPTCPAddress> clients = new ArrayList<UDPTCPAddress>();

	/**
	 * Initializes a new Server.
	 */
	public Server() {
		this(LibraryLoader.getMainDir());
	}

	/**
	 * Initializes a new Server.
	 * 
	 * @param rootDir the directory to put the config directory in.
	 */
	public Server(File rootDir) {
		config = new ServerConfig(rootDir);
		config.registerUpdateHandler(cfg -> updateConfig());
		config.initConfig();
		try {
			sender = new Sender(clients);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the Sender used to send notifications.
	 * 
	 * @return the Sender used to send notifications.
	 */
	public Sender getSender() {
		return sender;
	}

	/**
	 * Gets the receiver used to receive notification requests.
	 * 
	 * @return the receiver used to receive notification requests.
	 */
	public Receiver getReceiver() {
		return receiver;
	}

	/**
	 * Gets the config handler for this server.
	 * 
	 * @return the config handler for this server.
	 */
	public ServerConfig getConfig() {
		return config;
	}

	/**
	 * Updates some stuff from the newly changed config values.
	 */
	private void updateConfig() {
		JsonArray clientsJson = config.getConfig(ServerConfig.CLIENTS);
		clients.clear();
		clientsJson.forEach(client -> clients.add(new UDPTCPAddress((JsonObject) client)));
		synchronized (this) {
			if (receiver != null) {
				receiver.stop();
			}
			receiver = new Receiver(config.getConfig(ServerConfig.UDP_PORT), config.getConfig(ServerConfig.TCP_PORT),
					(json, addr) -> addClient(
							new UDPTCPAddress(addr.getHostName(), (int) json.get("udp"), (int) json.get("tcp"))));
		}
	}

	/**
	 * Removes all clients from the list of clients this server sends to.
	 */
	public void clearClients() {
		clients.clear();
		config.setConfig(ServerConfig.CLIENTS, new JsonArray());
	}

	/**
	 * Adds the given client to the list of clients to send notifications to.
	 * 
	 * @param address the address to add.
	 */
	public void addClient(UDPTCPAddress address) {
		clients.add(address);
		JsonArray clientsJson = new JsonArray(
				clients.stream().distinct().map(addr -> addr.toJson()).collect(Collectors.toList()));
		config.setConfig(ServerConfig.CLIENTS, clientsJson);
	}

	/**
	 * Gets a list containing all the clients to send notifications to.
	 * 
	 * @return a list containing all the clients to send notifications to.
	 */
	public List<UDPTCPAddress> getClients() {
		return new ArrayList<UDPTCPAddress>(clients);
	}

}

package com.tome25.remotenotifications.server;

import com.tome25.remotenotifications.network.Sender;
import com.tome25.remotenotifications.server.config.ServerConfig;
import com.tome25.remotenotifications.utility.DependencyChecker;

/**
 * The server main class. This class handles initializing all the server stuff.
 * 
 * @author ToMe25
 *
 */
public class Server {

	private Sender sender;
	private ServerConfig config;

	/**
	 * Initializes a new Server.
	 */
	public Server() {
		if (!DependencyChecker.checkDependencies()) {
			return;
		}
		config = new ServerConfig();
		config.registerUpdateHandler(cfg -> updateConfig());
		config.initConfig();
	}

	/**
	 * Gets the Sender used by this server.
	 * 
	 * @return the Sender used by this server.
	 */
	public Sender getSender() {
		return sender;
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
		String clientAddress = (String) config.getConfig(ServerConfig.CLIENT_ADDRESS);
		int udpPort = (int) config.getConfig(ServerConfig.CLIENT_UDP_PORT);
		int tcpPort = (int) config.getConfig(ServerConfig.CLIENT_TCP_PORT);
		if (sender == null) {
			try {
				sender = new Sender(clientAddress, udpPort, tcpPort);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			sender.setAddress(clientAddress);
			sender.setUdpPort(udpPort);
			sender.setTcpPort(tcpPort);
		}
	}

}

package com.tome25.remotenotifications.server.config;

import com.tome25.remotenotifications.config.ConfigHandler;

/**
 * The server config handler. This class handles the client config file, reading
 * and writing its contents using ToMe25s-Java-Utilities
 * {@link com.tome25.utils.config.Config Config} system.
 * 
 * @author ToMe25
 *
 */
public class ServerConfig extends ConfigHandler {

	/**
	 * The property name for the client address.
	 */
	public static final String CLIENT_ADDRESS = "client-address";
	/**
	 * The property name for the client udp port.
	 */
	public static final String CLIENT_UDP_PORT = "client-udp-port";
	/**
	 * The property name for the client tcp port.
	 */
	public static final String CLIENT_TCP_PORT = "client-tcp-port";
	
	@Override
	public void initConfig() {
		getConfig().addConfig("server.cfg", CLIENT_ADDRESS, "localhost",
				"The address of the device that should receive the notifications.");
		getConfig().addConfig("server.cfg", CLIENT_UDP_PORT, 3112,
				"The port of the client to send notifications to over udp.",
				"The server will only send notifications over udp if tcp is disabled, or the transmission fails.",
				"Set to 0 to disable udp sending.");
		getConfig().addConfig("server.cfg", CLIENT_TCP_PORT, 3113,
				"The port of the client to send notifications to over tcp.",
				"The server will try to send notifications over tcp first, and fall back to udp if that fails.",
				"Set to 0 to disable tcp sending.");
		getConfig().readConfig();
		updateConfig();
	}

}

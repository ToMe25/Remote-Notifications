package com.tome25.remotenotifications.server.config;

import com.tome25.remotenotifications.config.ConfigHandler;
import com.tome25.remotenotifications.network.UDPTCPAddress;
import com.tome25.utils.json.JsonArray;

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
	 * The property name for the clients list.
	 */
	public static final String CLIENTS = "clients";
	/**
	 * The property name for the udp port the server listens to.
	 */
	public static final String UDP_PORT = "udp-port";
	/**
	 * The property name for the tcp port the server listens to.
	 */
	public static final String TCP_PORT = "tcp-port";

	@Override
	public void initConfig() {
		getConfig().addConfig("server.cfg", CLIENTS, new JsonArray(new UDPTCPAddress("localhost", 3112, 3113).toJson()),
				"A list of clients to send the notifications to.",
				"The server will try to send notifications over tcp first, and fall back to udp if that fails,",
				"if both are enabled. To disable one of the protocols set its port to 0.",
				"The json format used for this list works like this: ",
				"[{\"addr\": \"CLIENT_1_ADDRESS\", \"udp\": CLIENT_1_UDP_PORT, \"tcp\": CLIENT_1_TCP_PORT},",
				"{\"addr\": \"CLIENT_2_ADDRESS\", \"udp\": CLIENT_2_UDP_PORT, \"tcp\": CLIENT_2_TCP_PORT\"}]",
				"Note that you can add as many clients as you want.",
				"Also json expects Strings(like the address) to be in double quots,",
				"while it expects numbers(the ports) not to be in any quotes.");
		getConfig().addConfig("server.cfg", UDP_PORT, 3114, "The udp port to listen on for notification requests.",
				"Set to 0 to disable.");
		getConfig().addConfig("server.cfg", TCP_PORT, 3115, "The tcp port to listen on for notification requests.",
				"Set to 0 to disable.");
		getConfig().readConfig();
		updateConfig();
	}

}

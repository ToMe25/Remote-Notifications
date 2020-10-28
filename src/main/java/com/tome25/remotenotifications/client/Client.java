package com.tome25.remotenotifications.client;

import java.io.File;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tome25.remotenotifications.client.config.ClientConfig;
import com.tome25.remotenotifications.client.config.ConfigWindow;
import com.tome25.remotenotifications.client.notification.NotificationHandler;
import com.tome25.remotenotifications.client.utility.TrayIconManager;
import com.tome25.remotenotifications.network.Receiver;
import com.tome25.remotenotifications.network.Sender;
import com.tome25.remotenotifications.network.UDPTCPAddress;
import com.tome25.utils.json.JsonArray;
import com.tome25.utils.json.JsonObject;

/**
 * The client main class. This class handles initializing all the client stuff.
 * 
 * @author ToMe25
 *
 */
public class Client {

	private static final File CODE_SOURCE = new File(
			Client.class.getProtectionDomain().getCodeSource().getLocation().getPath());

	private Receiver receiver;
	private Sender sender;
	private TrayIconManager iconManager;
	private ClientConfig config;
	private ConfigWindow configWindow;
	private List<UDPTCPAddress> servers = new ArrayList<UDPTCPAddress>();
	private List<UDPTCPAddress> notifiedServers = new ArrayList<UDPTCPAddress>();
	private final boolean dummy;

	/**
	 * Initializes a new Client.
	 */
	public Client() {
		this(CODE_SOURCE.getParentFile());
	}

	/**
	 * Initializes a new Client.
	 * 
	 * @param rootDir the directory to put the config directory in.
	 */
	public Client(File rootDir) {
		this(rootDir, -1, -1);
	}

	/**
	 * Initializes a new Client.
	 * 
	 * @param udpPort the udp port to listen for notifications on.
	 * @param tcpPort the tcp port to listen for notifications on.
	 */
	public Client(int udpPort, int tcpPort) {
		this(CODE_SOURCE.getParentFile(), udpPort, tcpPort);
	}

	/**
	 * Initializes a new Client.
	 * 
	 * @param rootDir the directory to put the config directory in.
	 * @param udpPort the udp port to listen for notifications on.
	 * @param tcpPort the tcp port to listen for notifications on.
	 */
	public Client(File rootDir, int udpPort, int tcpPort) {
		this(rootDir, udpPort, tcpPort, false);
	}

	/**
	 * Initializes a new Client.
	 * 
	 * @param udpPort the udp port to listen for notifications on.
	 * @param tcpPort the tcp port to listen for notifications on.
	 * @param dummy   whether this client should always use the dummy log
	 *                notification instead of the selected notification.
	 */
	public Client(int udpPort, int tcpPort, boolean dummy) {
		this(CODE_SOURCE.getParentFile(), udpPort, tcpPort, dummy);
	}

	/**
	 * Initializes a new Client.
	 * 
	 * @param rootDir the directory to put the config directory in.
	 * @param udpPort the udp port to listen for notifications on.
	 * @param tcpPort the tcp port to listen for notifications on.
	 * @param dummy   whether this client should always use the dummy log
	 *                notification instead of the selected notification.
	 */
	public Client(File rootDir, int udpPort, int tcpPort, boolean dummy) {
		this.dummy = dummy;
		iconManager = new TrayIconManager();
		try {
			sender = new Sender(servers);
		} catch (Exception e) {
			e.printStackTrace();
		}
		config = new ClientConfig(rootDir);
		config.initConfig();
		if (tcpPort >= 0) {
			config.setConfig(ClientConfig.TCP_PORT, tcpPort);
		}
		if (udpPort >= 0) {
			config.setConfig(ClientConfig.UDP_PORT, udpPort);
		}
		if (dummy) {
			config.setConfig(ClientConfig.NOTIFICATION_STYLE, "Dummy");
			NotificationHandler.setNotification("Dummy");
		}
		configWindow = new ConfigWindow(config);
		config.registerUpdateHandler(cfg -> updateConfig());
		updateConfig();
	}

	/**
	 * Gets the receiver used by this client.
	 * 
	 * @return the receiver used by this client.
	 */
	public Receiver getReceiver() {
		return receiver;
	}

	/**
	 * Gets the sender used to send notification requests.
	 * 
	 * @return the sender used to send notification requests.
	 */
	public Sender getSender() {
		return sender;
	}

	/**
	 * Gets the tray icon manager of this client.
	 * 
	 * @return the tray icon manager of this client.
	 */
	public TrayIconManager getIconManager() {
		return iconManager;
	}

	/**
	 * Gets the config handler for this client.
	 * 
	 * @return the config handler for this client.
	 */
	public ClientConfig getConfig() {
		return config;
	}

	/**
	 * Gets the config window for this clients config.
	 * 
	 * @return the config window for this clients config.
	 */
	public ConfigWindow getConfigWindow() {
		return configWindow;
	}

	/**
	 * Removes all servers from the list of servers this client requests
	 * notifications from.
	 */
	public void clearServers() {
		servers.clear();
		config.setConfig(ClientConfig.SERVERS, new JsonArray());
	}

	/**
	 * Adds the given server to the list of servers to request notifications from.
	 * 
	 * @param address the address to add.
	 */
	public void addServer(UDPTCPAddress address) {
		servers.add(address);
		JsonArray serversJson = new JsonArray(
				servers.stream().distinct().map(addr -> addr.toJson()).collect(Collectors.toList()));
		config.setConfig(ClientConfig.SERVERS, serversJson);
	}

	/**
	 * Gets a list containing all the servers to request notifications from.
	 * 
	 * @return a list containing all the servers to request notifications from.
	 */
	public List<UDPTCPAddress> getServers() {
		return new ArrayList<UDPTCPAddress>(servers);
	}

	/**
	 * Updates some things from the config.
	 */
	private void updateConfig() {
		if (!dummy) {
			NotificationHandler.setNotification((String) config.getConfig(ClientConfig.NOTIFICATION_STYLE));
		}
		NotificationHandler.setNotificationTime(config.getConfig(ClientConfig.NOTIFICATION_TIME));
		int oldUdp = receiver == null ? 0 : receiver.getUDPPort();
		int oldTcp = receiver == null ? 0 : receiver.getTCPPort();
		int udpPort = config.getConfig(ClientConfig.UDP_PORT);
		int tcpPort = config.getConfig(ClientConfig.TCP_PORT);
		if (oldUdp != udpPort || oldTcp != tcpPort) {
			if (receiver != null) {
				receiver.stop();
			}
			receiver = new Receiver(udpPort, tcpPort, (json, addr) -> NotificationHandler
					.displayMessage(json.getString("header"), json.getString("message")));
			notifiedServers.clear();
		}
		JsonArray serversJson = config.getConfig(ClientConfig.SERVERS);
		servers.clear();
		serversJson.forEach(server -> servers.add(new UDPTCPAddress((JsonObject) server)));
		if (sender != null) {
			JsonObject request = new JsonObject("udp", udpPort);
			request.put("tcp", tcpPort);
			for (UDPTCPAddress server : servers) {
				if (!notifiedServers.contains(server)) {
					boolean[] error = new boolean[1];
					sender.send(request, e -> {
						if (!(e instanceof ConnectException)) {
							e.printStackTrace();
						}
						error[0] = true;
					}, server);
					if (!error[0]) {
						notifiedServers.add(server);
					}
				}
			}
		}
	}

}

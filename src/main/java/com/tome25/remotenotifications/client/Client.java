package com.tome25.remotenotifications.client;

import com.tome25.remotenotifications.client.config.ClientConfig;
import com.tome25.remotenotifications.client.config.ConfigWindow;
import com.tome25.remotenotifications.client.notification.NotificationHandler;
import com.tome25.remotenotifications.client.utility.TrayIconManager;
import com.tome25.remotenotifications.network.Receiver;
import com.tome25.remotenotifications.utility.DependencyChecker;

/**
 * The client main class. This class handles initializing all the client stuff.
 * 
 * @author ToMe25
 *
 */
public class Client {

	private Receiver receiver;
	private TrayIconManager iconManager;
	private ClientConfig config;
	private ConfigWindow configWindow;

	/**
	 * Initializes a new Client.
	 */
	public Client() {
		if (!DependencyChecker.checkDependencies()) {
			return;
		}
		iconManager = new TrayIconManager();
		config = new ClientConfig();
		config.registerUpdateHandler(cfg -> updateConfig());
		config.initConfig();
		configWindow = new ConfigWindow(config);
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
	 * Updates some things from the config.
	 */
	private void updateConfig() {
		NotificationHandler.setNotification((String) getConfig().getConfig(ClientConfig.NOTIFICATION_STYLE));
		NotificationHandler.setNotificationTime((int) getConfig().getConfig(ClientConfig.NOTIFICATION_TIME));
		int oldUdp = receiver == null ? 0 : receiver.getUDPPort();
		int oldTcp = receiver == null ? 0 : receiver.getTCPPort();
		int udpPort = (int) config.getConfig(ClientConfig.UDP_PORT);
		int tcpPort = (int) config.getConfig(ClientConfig.TCP_PORT);
		if (oldUdp != udpPort || oldTcp != tcpPort) {
			if (receiver != null) {
				receiver.stop();
			}
			receiver = new Receiver(udpPort, tcpPort);
		}
	}

}

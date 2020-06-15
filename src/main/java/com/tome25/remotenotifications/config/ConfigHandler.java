package com.tome25.remotenotifications.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.tome25.remotenotifications.RemoteNotifications;
import com.tome25.remotenotifications.network.Receiver;
import com.tome25.remotenotifications.notification.NotificationHandler;
import com.tome25.utils.config.Config;

/**
 * This programs configuration handler.
 * 
 * @author ToMe25
 *
 */
public class ConfigHandler {

	private final List<Consumer<ConfigHandler>> updateHandlers = new ArrayList<Consumer<ConfigHandler>>();
	private final Config config;
	private final boolean server;
	public String clientAddress;
	public int udpPort;
	public int tcpPort;

	/**
	 * creates a new ConfigHandler.
	 * 
	 * @param server whether this ConfigHandler is serverside.
	 */
	public ConfigHandler(boolean server) {
		this.server = server;
		File configDir = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		configDir = new File(configDir.getParent(), "Remote-Notifications-Config");
		config = new Config(false, configDir, true, file -> updateConfig());
		if (server) {
			initServerConfig();
		} else {
			initClientConfig();
		}
	}

	/**
	 * initializes the server config.
	 */
	private void initClientConfig() {
		config.addConfig("client.cfg", "notification-style", "Dialog_dark_frameless",
				"The style of notification you want to see when receiving some notification to display.",
				"Valid Options are: Notify_light_none, Notify_dark_none, Notify_light_info, Notify_dark_info,",
				"Notify_light_question, Notify_dark_question, Notify_light_warn, Notify_dark_warn,",
				"Notify_light_error, Notify_dark_error, Dialog_light_frameless, Dialog_dark_frameless,",
				"Dialog_light_framed, Dialog_dark_framed, Option_none, Option_info, Option_question,",
				"Option_warn, Option_error.");
		config.addConfig("client.cfg", "notification-time", 10,
				"Some notification styles have a limited lifetime after which they dissappear, this setting controls that time.",
				"In seconds. Set to 0 to stop it from disappearing by itself.");
		config.addConfig("client.cfg", "udp-port", 3112,
				"The port to listen on for notifications that are sent over udp.", "Set to 0 to disable udp handling.");
		config.addConfig("client.cfg", "tcp-port", 3113,
				"The port to listen on for notifications that are sent over tcp.", "Set to 0 to disable tcp handling.");
		config.readConfig();
		NotificationHandler.setNotification((String) config.getConfig("notification-style"));
		NotificationHandler.setNotificationTime((int) config.getConfig("notification-time"));
		udpPort = (int) config.getConfig("udp-port");
		tcpPort = (int) config.getConfig("tcp-port");
	}

	/**
	 * initializes the server config.
	 */
	private void initServerConfig() {
		config.addConfig("server.cfg", "client-address", "localhost",
				"The address of the device that should receive the notifications.");
		config.addConfig("server.cfg", "client-udp-port", 3112,
				"The port of the client to send notifications to over udp.",
				"The server will only send notifications over udp if tcp is disabled, or the transmission fails.",
				"Set to 0 to disable udp sending.");
		config.addConfig("server.cfg", "client-tcp-port", 3113,
				"The port of the client to send notifications to over tcp.",
				"The server will try to send notifications over tcp first, and fall back to udp if that fails.",
				"Set to 0 to disable tcp sending.");
		config.readConfig();
		clientAddress = (String) config.getConfig("client-address");
		udpPort = (int) config.getConfig("client-udp-port");
		tcpPort = (int) config.getConfig("client-tcp-port");
	}

	/**
	 * Gets the config value for the given Name.
	 * 
	 * @param option the name of the config option to get.
	 * @return the config value for the given Name.
	 */
	public Object getConfig(String option) {
		return config.getConfig(option);
	}

	/**
	 * Sets the given config option to the given value. Will be synchronized to the
	 * config file.
	 * 
	 * @param <T>    the type of the value.
	 * @param option the option to set.
	 * @param value  the value to set the option to.
	 */
	public <T> void setConfig(String option, T value) {
		config.setConfig(option, value);
	}

	/**
	 * Updates the values from the config, that are stored elsewhere.
	 */
	private void updateConfig() {
		if (server) {
			clientAddress = (String) config.getConfig("client-address");
			udpPort = (int) config.getConfig("client-udp-port");
			tcpPort = (int) config.getConfig("client-tcp-port");
			if (RemoteNotifications.sender != null) {
				RemoteNotifications.sender.setAddress(clientAddress);
				RemoteNotifications.sender.setUdpPort(udpPort);
				RemoteNotifications.sender.setTcpPort(tcpPort);
			}
		} else {
			NotificationHandler.setNotification((String) config.getConfig("notification-style"));
			NotificationHandler.setNotificationTime((int) config.getConfig("notification-time"));
			int oldUdp = udpPort;
			int oldTcp = tcpPort;
			udpPort = (int) config.getConfig("udp-port");
			tcpPort = (int) config.getConfig("tcp-port");
			if (oldUdp != udpPort || oldTcp != tcpPort) {
				if (RemoteNotifications.receiver != null) {
					RemoteNotifications.receiver.stop();
				}
				RemoteNotifications.receiver = new Receiver(udpPort, tcpPort);
			}
		}
		updateHandlers.forEach(handler -> handler.accept(this));
	}

	/**
	 * Checks whether this config is serverside, or clientside.
	 * 
	 * @return whether this config is serverside, or clientside.
	 */
	protected boolean isServer() {
		return server;
	}

	/**
	 * Registeres a {@link Consumer} that will be called with this ConfigHandler
	 * every time the config file updates.
	 * 
	 * @param updateHandler the update handler.
	 */
	public void registerUpdateHandler(Consumer<ConfigHandler> updateHandler) {
		updateHandlers.add(updateHandler);
	}

}

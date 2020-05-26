package com.tome25.remotenotifications;

import java.io.File;

import com.tome25.remotenotifications.notification.NotificationHandler;
import com.tome25.utils.config.Config;

/**
 * This programs configuration handler.
 * 
 * @author ToMe25
 *
 */
public class ConfigHandler {

	private final Config config;
	public String clientAddress;
	public int port;

	/**
	 * creates a new ConfigHandler.
	 * 
	 * @param server whether this ConfigHandler is serverside.
	 */
	public ConfigHandler(boolean server) {
		File configDir = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		configDir = new File(configDir.getParent(), "Remote-Notifications-Config");
		config = new Config(configDir, false);
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
		config.addConfig("client.cfg", "notification-style", "TrayIcon_none",
				"The style of notification you want to see when receiving some notification to display.",
				"Valid Options are: TrayIcon_none, TrayIcon_info, TrayIcon_warning, TrayIcon_error,",
				"Dialog_light_frameless, Dialog_dark_frameless, Dialog_light_framed, Dialog_dark_framed.");
		config.addConfig("client.cfg", "notification-time", 10,
				"Some notification styles have a limited lifetime after which they dissappear, this setting controls that time. In seconds.");
		config.addConfig("client.cfg", "port", 3112, "The port to listen on for notifications.");
		config.readConfig();
		NotificationHandler.setNotification((String) config.getConfig("notification-style"));
		NotificationHandler.setNotificationTime((int) config.getConfig("notification-time"));
		port = (int) config.getConfig("port");
	}

	/**
	 * initializes the server config.
	 */
	private void initServerConfig() {
		config.addConfig("server.cfg", "client-address", "localhost",
				"The address of the device that should receive the notifications.");
		config.addConfig("server.cfg", "client-port", 3112, "The port of the client to send notifications to.");
		config.readConfig();
		clientAddress = (String) config.getConfig("client-address");
		port = (int) config.getConfig("client-port");
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

}

package com.tome25.remotenotifications.client.config;

import com.tome25.remotenotifications.config.ConfigHandler;

/**
 * The client config handler. This class handles the client config file, reading
 * and writing its contents using ToMe25s-Java-Utilities
 * {@link com.tome25.utils.config.Config Config} system.
 * 
 * @author ToMe25
 *
 */
public class ClientConfig extends ConfigHandler {

	/**
	 * The property name for the notification style.
	 */
	public static final String NOTIFICATION_STYLE = "notification-style";
	/**
	 * The property name for the time the notification is shown.
	 */
	public static final String NOTIFICATION_TIME = "notification-time";
	/**
	 * The property name for the udp port.
	 */
	public static final String UDP_PORT = "udp-port";
	/**
	 * The property name for the tcp port.
	 */
	public static final String TCP_PORT = "tcp-port";
	/**
	 * The property name for whether or not the user should confirm before closing
	 * the program.
	 */
	public static final String CONFIRM_EXIT = "confirm-exit";

	private boolean confirmExit;

	@Override
	public void initConfig() {
		getConfig().addConfig("client.cfg", NOTIFICATION_STYLE, "Dialog_dark_frameless",
				"The style of notification you want to see when receiving some notification to display.",
				"Valid Options are: TrayIcon_none, TrayIcon_info, TrayIcon_warning, TrayIcon_error,",
				"Dialog_light_frameless, Dialog_dark_frameless, Dialog_light_framed, Dialog_dark_framed.");
		getConfig().addConfig("client.cfg", NOTIFICATION_TIME, 10,
				"Some notification styles have a limited lifetime after which they dissappear, this setting controls that time.",
				"In seconds. Set to 0 to stop it from disappearing by itself.");
		getConfig().addConfig("client.cfg", UDP_PORT, 3112,
				"The port to listen on for notifications that are sent over udp.", "Set to 0 to disable udp handling.");
		getConfig().addConfig("client.cfg", TCP_PORT, 3113,
				"The port to listen on for notifications that are sent over tcp.", "Set to 0 to disable tcp handling.");
		getConfig().addConfig("client.cfg", CONFIRM_EXIT, true,
				"Whether there should be a confirmation window before exiting Remote-Notifications.");
		getConfig().readConfig();
		updateConfig();
	}

	@Override
	protected void updateConfig() {
		confirmExit = (boolean) getConfig().getConfig("confirm-exit");
		super.updateConfig();
	}

	/**
	 * Whether or not a confirmation window should be shown before exiting this
	 * software.
	 * 
	 * @return whether or not a confirmation window should be shown before exiting
	 *         this software.
	 */
	public boolean confirmExit() {
		return confirmExit;
	}

}

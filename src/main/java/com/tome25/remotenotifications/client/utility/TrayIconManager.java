package com.tome25.remotenotifications.client.utility;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

/**
 * The class handling all the {@link SystemTray} stuff.
 * 
 * @author ToMe25
 *
 */
public class TrayIconManager {

	private TrayIcon trayIcon;
	private SystemTray tray;

	/**
	 * Creates a new TrayIconManager.
	 */
	public TrayIconManager() {
		// Check the SystemTray is supported
		if (!SystemTray.isSupported()) {
			System.err.println("SystemTray is not supported");
			return;
		}
		try {
			trayIcon = new TrayIcon(IconHandler.getLogoImage(), "Remote-Notifications", PopupManager.createPopup());
			trayIcon.setImageAutoSize(true);
			tray = SystemTray.getSystemTray();
			tray.add(trayIcon);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays the given message.
	 * 
	 * @param header  the header of the message
	 * @param message the text of the message
	 * @param type    the type of the message
	 */
	public void displayMessage(String header, String message, MessageType type) {
		if (trayIcon != null) {
			trayIcon.displayMessage(header, message, type);
		}
	}

}

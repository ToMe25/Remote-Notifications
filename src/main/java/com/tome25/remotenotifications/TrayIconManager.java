package com.tome25.remotenotifications;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.tome25.utils.lib.JarExtractor;

public class TrayIconManager {

	private TrayIcon trayIcon;
	private SystemTray tray;
	private PopupMenu popup;

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
			File image = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
			image = new File(image.getParent(), "RemoteNotifications.png");
			if (!image.exists()) {
				JarExtractor.extractFileFromJar(
						new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()),
						"RemoteNotifications.png");
			}
			BufferedImage img = ImageIO.read(image);
			trayIcon = new TrayIcon(img, "Remote-Notifications", createPopup());
			trayIcon.setImageAutoSize(true);
			tray = SystemTray.getSystemTray();
			tray.add(trayIcon);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * creates the popup menu for this class
	 * 
	 * @return the popup menu
	 */
	protected PopupMenu createPopup() {
		MenuItem exitItem = new MenuItem("exit");
		exitItem.addActionListener(new ExitListener());
		popup = new PopupMenu();
		popup.add(exitItem);
		return popup;
	}

	/**
	 * displays the given message.
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

	private class ExitListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.exit(1);
		}

	}

}

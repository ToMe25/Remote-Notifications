package com.tome25.remotenotifications;

import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import com.tome25.remotenotifications.utility.IconHandler;
import com.tome25.remotenotifications.utility.PopupManager;

import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;

public class TrayIconManager {

	private TrayIcon trayIcon;
	private SystemTray tray;

	/**
	 * Creates a new TrayIconManager.
	 */
	public TrayIconManager() {
		SystemTray.DEBUG = true;
		tray = SystemTray.get();
		if (tray == null) {
			System.err.println("SystemTray is not supported!");
		} else {
			tray.setTooltip("Remote-Notifications");
			try {
				tray.setImage(IconHandler.getImageScaled("RemoteNotifications.png", tray.getTrayImageSize(),
						tray.getTrayImageSize()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			tray.getMenu().add(new MenuItem("Config", new OptionListener(e -> RemoteNotifications.cfgWindow.show())));
			tray.getMenu().add(new MenuItem("Exit", new OptionListener(e -> PopupManager.askExit())));
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

	private class OptionListener implements ActionListener {

		private final Consumer<ActionEvent> action;

		private OptionListener(Consumer<ActionEvent> action) {
			this.action = action;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			action.accept(e);
		}

	}

}

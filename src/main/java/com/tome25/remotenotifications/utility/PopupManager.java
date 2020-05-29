package com.tome25.remotenotifications.utility;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.tome25.remotenotifications.RemoteNotifications;

/**
 * This is the utility class for the handling of Popups.
 * 
 * @author ToMe25
 *
 */
public class PopupManager {

	/**
	 * Creates the popup menu for the TrayIcon, or the notifications to use.
	 * 
	 * @return the popup menu.
	 */
	public static PopupMenu createPopup() {
		PopupMenu popup = new PopupMenu();
		MenuItem configItem = new MenuItem("Config");
		configItem.addActionListener(new OptionListener(e -> RemoteNotifications.cfgWindow.show()));
		popup.add(configItem);
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(new OptionListener(e -> askExit()));
		popup.add(exitItem);
		return popup;
	}

	/**
	 * Asks the user whether he really wants to exit.
	 */
	public static void askExit() {
		int i = 0;
		if (RemoteNotifications.config.confirmExit) {
			JFrame frame = new JFrame();
			frame.setIconImage(IconHandler.getLogoImage());
			i = JOptionPane.showConfirmDialog(frame, "Do you really want to exit Remote-Notifications?", "Confirm Exit",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			frame.dispose();
		}
		if (i < 1) {
			System.exit(0);
		}
	}

	public static class OptionListener implements ActionListener {

		private final Consumer<ActionEvent> action;

		/**
		 * Creates a new OptionListener calling the given {@link Consumer} when an
		 * action occurs.
		 * 
		 * @param action the action for this listener.
		 */
		public OptionListener(Consumer<ActionEvent> action) {
			this.action = action;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (action != null) {
				action.accept(e);
			}
		}

	}

}

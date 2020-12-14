package com.tome25.remotenotifications.client.notification;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.tome25.remotenotifications.client.utility.IconHandler;
import com.tome25.remotenotifications.client.utility.PopupManager;

/**
 * Notifications based on {@link JDialog}s.
 * 
 * @author ToMe25
 *
 */
public enum DialogNotification implements INotification {
	LIGHT_FRAMELESS(false, false), DARK_FRAMELESS(true, false), LIGHT_FRAMED(false, true), DARK_FRAMED(true, true);

	private boolean dark;
	private boolean framed;

	/**
	 * creates a new DialogNotification.
	 * 
	 * @param dark   whether the notifications should be dark, or light.
	 * @param framed whether the notifications should be framed, or not.
	 */
	private DialogNotification(boolean dark, boolean framed) {
		this.dark = dark;
		this.framed = framed;
	}

	@Override
	public void display(String header, String message) {
		new Runner(header, message, this);
	}

	private static class Runner implements Runnable {

		private static final ThreadGroup NOTIFICATION_RUNNERS = new ThreadGroup("Notification-Runners");
		private static final Color LIGHT_COLOR = new Color(238, 238, 238);
		private static final Color DARK_COLOR = new Color(43, 43, 43);
		private static final Color LIGHT_TEXT_COLOR = new Color(51, 51, 51);
		private static final Color DARK_TEXT_COLOR = new Color(176, 176, 176);
		private final String header;
		private final String message;
		private final DialogNotification notificationType;
		private static int nr = 0;
		private final Thread thread;

		/**
		 * Creates a new Runner handling a new notification.
		 * 
		 * @param header           the header of the notification.
		 * @param message          the message of the notification.
		 * @param notificationType the type of the notification.
		 */
		private Runner(String header, String message, DialogNotification notificationType) {
			thread = new Thread(NOTIFICATION_RUNNERS, this, "Notification-Runner-" + nr);
			this.header = header;
			this.message = message;
			this.notificationType = notificationType;
			nr++;
			thread.start();
		}

		@Override
		public void run() {
			final JDialog frame = new JDialog();
			if (notificationType.dark) {
				frame.getContentPane().setBackground(DARK_COLOR);
			} else {
				frame.getContentPane().setBackground(LIGHT_COLOR);
			}
			frame.setSize(300, 100);
			if (notificationType.framed) {
				frame.setIconImage(IconHandler.getLogoImage());
			} else {
				frame.setUndecorated(true);
			}
			Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
			Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
			frame.setLocation(scrSize.width - frame.getWidth(), scrSize.height - toolHeight.bottom - frame.getHeight());
			frame.setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.weightx = 1.0f;
			constraints.weighty = 1.0f;
			constraints.insets = new Insets(5, 5, 5, 5);
			constraints.fill = GridBagConstraints.BOTH;
			JLabel headingLabel = new JLabel(header);
			try {
				headingLabel.setIcon(IconHandler.getIconScaled("RemoteNotifications.png", 32, 32));
			} catch (Exception e) {
				e.printStackTrace();
			}
			headingLabel.setOpaque(false);
			if (notificationType.dark) {
				headingLabel.setForeground(DARK_TEXT_COLOR);
			} else {
				headingLabel.setForeground(LIGHT_TEXT_COLOR);
			}
			headingLabel.setFont(new Font(headingLabel.getFont().getName(), Font.BOLD, 16));
			frame.add(headingLabel, constraints);
			constraints.anchor = GridBagConstraints.NORTH;
			if (!notificationType.framed) {
				constraints.gridx++;
				constraints.weightx = 0f;
				constraints.weighty = 0f;
				constraints.fill = GridBagConstraints.NONE;
				JButton closeButton = new JButton(new CloseAction("x", frame));
				closeButton.setMargin(new Insets(1, 4, 1, 4));
				closeButton.setFocusable(false);
				frame.add(closeButton, constraints);
				constraints.gridx = 0;
				constraints.weightx = 1.0f;
				constraints.weighty = 1.0f;
			}
			constraints.gridy++;
			constraints.insets = new Insets(5, 5, 5, 5);
			constraints.fill = GridBagConstraints.BOTH;
			JTextArea messageArea = new JTextArea(message);
			messageArea.setOpaque(false);
			if (notificationType.dark) {
				headingLabel.setForeground(DARK_TEXT_COLOR);
			} else {
				headingLabel.setForeground(LIGHT_TEXT_COLOR);
			}
			messageArea.setEditable(false);
			messageArea.setBorder(new EmptyBorder(0, 0, 0, 0));
			messageArea.setLineWrap(true);
			messageArea.setWrapStyleWord(true);
			if (notificationType.dark) {
				messageArea.setForeground(DARK_TEXT_COLOR);
			} else {
				messageArea.setForeground(LIGHT_TEXT_COLOR);
			}
			frame.add(messageArea, constraints);
			frame.setAlwaysOnTop(true);
			frame.setVisible(true);
			PopupMenu popup = PopupManager.createPopup();
			frame.add(popup);
			PopupListener popupListener = new PopupListener(popup);
			frame.addMouseListener(popupListener);
			messageArea.addMouseListener(popupListener);
			if (NotificationHandler.getNotificationTime() > 0) {
				try {
					Thread.sleep(NotificationHandler.getNotificationTime() * 1000);
					frame.dispose();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class PopupListener extends MouseAdapter {

		private final PopupMenu popup;

		/**
		 * Creates a new PopupListener showing the given {@link PopupMenu} on
		 * rightclick.
		 * 
		 * @param menu the menu to show.
		 */
		private PopupListener(PopupMenu menu) {
			popup = menu;
		}

		@Override
		public void mousePressed(MouseEvent ev) {
			if (ev.isPopupTrigger()) {
				popup.show(ev.getComponent(), ev.getX(), ev.getY());
			}
		}

		@Override
		public void mouseReleased(MouseEvent ev) {
			if (ev.isPopupTrigger()) {
				popup.show(ev.getComponent(), ev.getX(), ev.getY());
			}
		}

	}

	private static class CloseAction extends AbstractAction {

		/**
		 * This classes serverialVersionUID.
		 */
		private static final long serialVersionUID = -5271381741363360738L;
		private final JDialog frame;

		/**
		 * Creates a new CloseAction with the given text, closing the given
		 * {@link JDialog}.
		 * 
		 * @param text  the text for the {@link JButton}.
		 * @param frame the {@link JDialog} to close.
		 */
		private CloseAction(String text, JDialog frame) {
			super(text);
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			frame.dispose();
		}

	}

}

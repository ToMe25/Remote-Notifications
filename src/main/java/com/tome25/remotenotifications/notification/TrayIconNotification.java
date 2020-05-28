package com.tome25.remotenotifications.notification;

import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

import com.tome25.remotenotifications.RemoteNotifications;

/**
 * Notifications based on the java {@link TrayIcon}.
 * 
 * @author ToMe25
 *
 */
public enum TrayIconNotification implements INotification {

	NONE(MessageType.NONE), INFO(MessageType.INFO), WARNING(MessageType.WARNING), ERROR(MessageType.ERROR);

	private final MessageType type;

	/**
	 * creates a new TrayIconNotification.
	 * 
	 * @param type the {@link MessageType} to use for notifications.
	 */
	private TrayIconNotification(MessageType type) {
		this.type = type;
	}

	@Override
	public void display(String header, String message) {
		RemoteNotifications.icon.displayMessage(header, message, type);
	}

}

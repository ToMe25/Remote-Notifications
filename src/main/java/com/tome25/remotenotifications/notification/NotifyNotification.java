package com.tome25.remotenotifications.notification;

import com.tome25.remotenotifications.utility.MessageType;

import dorkbox.notify.Notify;

/**
 * A Notification based on dorkbox Notify library.
 * 
 * @author ToMe25
 *
 */
public enum NotifyNotification implements INotification {

	LIGHT_NONE(MessageType.NONE, false), DARK_NONE(MessageType.NONE, true), LIGHT_INFO(MessageType.INFO, false),
	DARK_INFO(MessageType.INFO, true), LIGHT_QUESTION(MessageType.QUESTION, false),
	DARK_QUESTION(MessageType.QUESTION, true), LIGHT_WARN(MessageType.WARN, false),
	DARK_WARN(MessageType.WARN, true), LIGHT_ERROR(MessageType.ERROR, false), DARK_ERROR(MessageType.ERROR, true);

	private final MessageType type;
	private final boolean dark;

	/**
	 * Creates a new NotifyNotification.
	 * 
	 * @param type the icon for the notification.
	 * @param dark whether this notification should be dark, or light.
	 */
	private NotifyNotification(MessageType type, boolean dark) {
		this.type = type;
		this.dark = dark;
	}

	@Override
	public void display(String header, String message) {
		Notify notify = Notify.create().title(header).text(message);
		if (dark) {
			notify.darkStyle();
		}
		notify.hideAfter(NotificationHandler.getNotificationTime() * 1000);
		switch (type) {
		case NONE:
			notify.show();
			break;

		case INFO:
			notify.showInformation();
			break;

		case QUESTION:
			notify.showConfirm();
			break;

		case WARN:
			notify.showWarning();
			break;

		case ERROR:
			notify.showError();
			break;
		}
	}

}

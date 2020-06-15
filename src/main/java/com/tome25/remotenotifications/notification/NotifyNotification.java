package com.tome25.remotenotifications.notification;

import com.tome25.remotenotifications.utility.MessageType;

import dorkbox.notify.Notify;

public enum NotifyNotification implements INotification {

	LIGHT_NONE(MessageType.NONE, false), DARK_NONE(MessageType.NONE, true), LIGHT_INFO(MessageType.INFO, false),
	DARK_INFO(MessageType.INFO, true), LIGHT_QUESTION(MessageType.QUESTION, false),
	DARK_QUESTION(MessageType.QUESTION, true), LIGHT_WARNING(MessageType.WARNING, false),
	DARK_WARNING(MessageType.WARNING, true), LIGHT_ERROR(MessageType.ERROR, false), DARK_ERROR(MessageType.ERROR, true);

	private final MessageType type;
	private final boolean dark;

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
			
		case WARNING:
			notify.showWarning();
			break;
			
		case ERROR:
			notify.showError();
			break;
		}
	}

}

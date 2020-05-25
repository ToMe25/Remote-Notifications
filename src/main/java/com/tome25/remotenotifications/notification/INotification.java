package com.tome25.remotenotifications.notification;

public interface INotification {

	/**
	 * displays a notification of this type.
	 * 
	 * @param header  the header of the notification.
	 * @param message the message of the notification.
	 */
	public void display(String header, String message);

}

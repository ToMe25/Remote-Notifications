package com.tome25.remotenotifications.client.notification;

/**
 * The interface all Notifications should implement.
 * 
 * @author ToMe25
 *
 */
public interface INotification {

	/**
	 * displays a notification of this type.
	 * 
	 * @param header  the header of the notification.
	 * @param message the message of the notification.
	 */
	public void display(String header, String message);

}

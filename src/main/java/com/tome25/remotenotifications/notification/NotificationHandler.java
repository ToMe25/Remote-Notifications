package com.tome25.remotenotifications.notification;

public class NotificationHandler {

	private static INotification notification;
	private static int notificationTime;

	/**
	 * sets the current notification type to the given notification type.
	 * 
	 * @param notification the new notification type to use.
	 */
	public static void setNotification(String notification) {
		switch (notification) {
		case "TrayIcon_none":
			NotificationHandler.notification = TrayIconNotification.NONE;
			break;

		case "TrayIcon_info":
			NotificationHandler.notification = TrayIconNotification.INFO;
			break;

		case "TrayIcon_warning":
			NotificationHandler.notification = TrayIconNotification.WARNING;
			break;

		case "TrayIcon_error":
			NotificationHandler.notification = TrayIconNotification.ERROR;
			break;

		default:
			NotificationHandler.notification = TrayIconNotification.NONE;
			break;
		}
	}

	/**
	 * sets the current notification type to the given notification type.
	 * 
	 * @param notification the new notification type to use.
	 */
	public static void setNotification(INotification notification) {
		NotificationHandler.notification = notification;
	}

	/**
	 * gets the current notification type.
	 * 
	 * @return the current notification type.
	 */
	public static INotification getNotification() {
		return notification;
	}

	/**
	 * sets the time notifications are displayed. if applicable.
	 * 
	 * @param notificationTime the time to display future notifications.
	 */
	public static void setNotificationTime(int notificationTime) {
		NotificationHandler.notificationTime = notificationTime;
	}

	/**
	 * gets the time to display notifications.
	 * 
	 * @return the time to display notifications.
	 */
	public static int getNotificationTime() {
		return notificationTime;
	}

	/**
	 * displays the given notification.
	 * 
	 * @param header  the header of the notification.
	 * @param message the message of the notification.
	 */
	public static void displayMessage(String header, String message) {
		notification.display(header, message);
	}

}

package com.tome25.remotenotifications.notification;

/**
 * The Utility class handling the notification selection.
 * 
 * @author ToMe25
 *
 */
public enum Notifications {
	TRAYICON_NONE(TrayIconNotification.NONE, "TrayIcon_none"),
	TRAYICON_INFO(TrayIconNotification.INFO, "TrayIcon_info"),
	TRAYICON_WARNING(TrayIconNotification.WARNING, "TrayIcon_warning"),
	TRAYICON_ERROR(TrayIconNotification.ERROR, "TrayIcon_error"),
	DIALOG_LIGHT_FRAMELESS(DialogNotification.LIGHT_FRAMELESS, "Dialog_light_frameless"),
	DIALOG_DARK_FRAMELESS(DialogNotification.DARK_FRAMELESS, "Dialog_dark_frameless"),
	DIALOG_LIGHT_FRAMED(DialogNotification.LIGHT_FRAMED, "Dialog_light_framed"),
	DIALOG_DARK_FRAMED(DialogNotification.DARK_FRAMED, "Dialog_dark_framed");

	private final INotification notification;
	private final String name;

	/**
	 * Initializes this notification enum.
	 * 
	 * @param notification the actual notification.
	 * @param name         the name of this notification.
	 */
	private Notifications(INotification notification, String name) {
		this.notification = notification;
		this.name = name;
	}

	/**
	 * Gets the actual notification to use.
	 * 
	 * @return the actual notification to use.
	 */
	public INotification getNotification() {
		return notification;
	}

	/**
	 * Gets this notifications name.
	 * 
	 * @return this notifications name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the names of all notifications.
	 * 
	 * @return the names of all notifications.
	 */
	public static String[] names() {
		String[] names = new String[values().length];
		int i = 0;
		for (Notifications not : values()) {
			names[i] = not.getName();
			i++;
		}
		return names;
	}
}

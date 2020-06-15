package com.tome25.remotenotifications.notification;

/**
 * The Utility class handling the notification selection.
 * 
 * @author ToMe25
 *
 */
public enum Notifications {

	DIALOG_LIGHT_FRAMELESS(DialogNotification.LIGHT_FRAMELESS, "Dialog_light_frameless"),
	DIALOG_DARK_FRAMELESS(DialogNotification.DARK_FRAMELESS, "Dialog_dark_frameless"),
	DIALOG_LIGHT_FRAMED(DialogNotification.LIGHT_FRAMED, "Dialog_light_framed"),
	DIALOG_DARK_FRAMED(DialogNotification.DARK_FRAMED, "Dialog_dark_framed"),
	NOTIFY_LIGHT_NONE(NotifyNotification.LIGHT_NONE, "Notify_light_none"),
	NOTIFY_DARK_NONE(NotifyNotification.DARK_NONE, "Notify_dark_none"),
	NOTIFY_LIGHT_INFO(NotifyNotification.LIGHT_INFO, "Notify_light_info"),
	NOTIFY_DARK_INFO(NotifyNotification.DARK_INFO, "Notify_dark_info"),
	NOTIFY_LIGHT_QUESTION(NotifyNotification.LIGHT_QUESTION, "Notify_light_question"),
	NOTIFY_DARK_QUESTION(NotifyNotification.DARK_QUESTION, "Notify_dark_question"),
	NOTIFY_LIGHT_WARN(NotifyNotification.LIGHT_WARN, "Notify_light_warn"),
	NOTIFY_DARK_WARN(NotifyNotification.DARK_WARN, "Notify_dark_warn"),
	NOTIFY_LIGHT_ERROR(NotifyNotification.LIGHT_ERROR, "Notify_light_error"),
	NOTIFY_DARK_ERROR(NotifyNotification.DARK_ERROR, "Notify_dark_error"),
	OPTION_NONE(OptionNotification.NONE, "Option_none"),
	OPTION_INFO(OptionNotification.INFO, "Option_info"),
	OPTION_QUESTION(OptionNotification.QUESTION, "Option_question"),
	OPTION_WARN(OptionNotification.WARN, "Option_warn"),
	OPTION_ERROR(OptionNotification.ERROR, "Option_error");

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

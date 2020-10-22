package com.tome25.remotenotifications.client.notification;

import java.util.logging.Logger;

import com.tome25.utils.logging.LogTracer;

public class LogNotification implements INotification {

	public static final Logger LOGGER = LogTracer.getLogger("Notifications");

	public static LogNotification INSTANCE = new LogNotification();
	
	@Override
	public void display(String header, String message) {
		header = "   " + header + "   ";
		LOGGER.info(header);
		String separator = "";
		for (int i = 0; i < header.length(); i++) {
			separator += "=";
		}
		LOGGER.info(separator);
		LOGGER.info(message);
	}

}

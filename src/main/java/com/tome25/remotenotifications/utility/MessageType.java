package com.tome25.remotenotifications.utility;

import javax.swing.JOptionPane;

/**
 * A Message Type representation, used to specify which icon to use.
 * 
 * @author ToMe25
 *
 */
public enum MessageType {

	NONE(-1), INFO(1), QUESTION(3), WARN(2), ERROR(0);

	private final int messageType;

	private MessageType(int messageType) {
		this.messageType = messageType;
	}

	/**
	 * Gets the {@link JOptionPane} messageType representation of this MessageType.
	 * 
	 * @return the {@link JOptionPane} messageType representation of this
	 *         MessageType.
	 */
	public int getMessageType() {
		return messageType;
	}

}

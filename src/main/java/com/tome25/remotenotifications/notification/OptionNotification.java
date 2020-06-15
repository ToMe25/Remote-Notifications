package com.tome25.remotenotifications.notification;

import java.text.BreakIterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.tome25.remotenotifications.utility.MessageType;

/**
 * A Notification based on JOptionPane.
 * 
 * @author ToMe25
 *
 */
public enum OptionNotification implements INotification {

	NONE(MessageType.NONE), INFO(MessageType.INFO), QUESTION(MessageType.QUESTION), WARN(MessageType.WARN),
	ERROR(MessageType.ERROR);

	private final int messageType;
    private final static int MAX_LINE_COUNT = 4;

	/**
	 * Creates a new OptionNotification.
	 * 
	 * @param type the icon for the notification.
	 */
	private OptionNotification(MessageType type) {
		messageType = type.getMessageType();
	}

	@Override
	public void display(String header, String message) {
		JFrame frame = new JFrame();
		String text = "";
		BreakIterator iter = BreakIterator.getWordInstance();
		if (message != null) {
			iter.setText(message);
			int start = iter.first(), end;
			int lines = 0;

			do {
				end = iter.next();

				if (end == BreakIterator.DONE || message.substring(start, end).length() >= 50) {
					text += message.substring(start, end == BreakIterator.DONE ? iter.last() : end)
							+ System.lineSeparator();
					lines++;
					start = end;
				}
				if (lines == MAX_LINE_COUNT) {
					if (end != BreakIterator.DONE) {
						text = text.substring(0, text.length() - System.lineSeparator().length()) + " ...";
					}
					break;
				}
			} while (end != BreakIterator.DONE);
		}
		JOptionPane.showMessageDialog(frame, text, header, messageType);
	}

}

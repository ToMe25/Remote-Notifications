package com.tome25.remotenotifications.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.tome25.remotenotifications.notification.NotificationHandler;
import com.tome25.remotenotifications.notification.Notifications;
import com.tome25.remotenotifications.utility.IconHandler;
import com.tome25.remotenotifications.utility.PopupManager;

/**
 * A gui to change the client config options.
 * 
 * @author ToMe25
 *
 */
public class ConfigWindow {

	private JFrame window;
	private final ConfigHandler cfg;
	private Map<String, NumberChangeListener> numberListeners = new HashMap<String, NumberChangeListener>();
	private Map<String, EnumChangeListener> enumListeners = new HashMap<String, EnumChangeListener>();

	/**
	 * Creates a new ConfigWindow.
	 * 
	 * @param config the {@link ConfigHandler} to edit.
	 */
	public ConfigWindow(ConfigHandler config) {
		cfg = config;
		cfg.registerUpdateHandler(cfg -> update());
		initWindow();
	}

	/**
	 * Initializes this configs window.
	 */
	private void initWindow() {
		window = new JFrame("Remote-Notifications-Config");
		window.setIconImage(IconHandler.getLogoImage());
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(10, 10));
		contentPane.setBackground(Color.WHITE);
		JLabel label = new JLabel("     Remote-Notifications-Config:");
		contentPane.add(label, BorderLayout.PAGE_START);
		contentPane.add(Box.createRigidArea(new Dimension(10, 10)), BorderLayout.LINE_START);
		contentPane.add(Box.createRigidArea(new Dimension(10, 10)), BorderLayout.LINE_END);
		contentPane.add(Box.createRigidArea(new Dimension(10, 10)), BorderLayout.PAGE_END);
		JPanel configPanel = new JPanel();
		configPanel.setPreferredSize(new Dimension(380, 200));
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.PAGE_AXIS));
		configPanel.add(createEnumSetting("notification-style", Notifications.names(),
				(String) cfg.getConfig("notification-style")));
		configPanel.add(createNumberSetting("notification-time", NotificationHandler.getNotificationTime()));
		configPanel.add(createNumberSetting("udp-port", cfg.udpPort));
		configPanel.add(createNumberSetting("tcp-port", cfg.tcpPort));
		configPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		contentPane.add(configPanel, BorderLayout.CENTER);
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Color.WHITE);
		JButton okButton = new JButton(new CloseAction("OK", window));
		okButton.setToolTipText("Applies changes and closes this window.");
		buttonsPanel.add(okButton);
		JButton exitButton = new JButton("Exit");
		exitButton.addActionListener(new PopupManager.OptionListener(e -> PopupManager.askExit()));
		exitButton.setToolTipText("Closes the Remote-Notifications client.");
		buttonsPanel.add(exitButton);
		contentPane.add(buttonsPanel, BorderLayout.PAGE_END);
		window.setContentPane(contentPane);
		window.pack();
	}

	/**
	 * Updates this config window from the config file.
	 */
	public void update() {
		numberListeners.forEach((name, listener) -> listener.field.setValue(cfg.getConfig(name)));
		enumListeners.forEach((name, listener) -> listener.field.setSelectedItem(cfg.getConfig(name)));
	}

	/**
	 * Creates a new settings panel for a number setting.
	 * 
	 * @param name  the name of the setting.
	 * @param value its default value.
	 * @return the settings panel.
	 */
	private JPanel createNumberSetting(String name, int value) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel(name);
		NumberFormat numberFormat = DecimalFormat.getInstance();
		JFormattedTextField textField = new JFormattedTextField(numberFormat);
		textField.setValue(value);
		textField.setColumns(4);
		NumberChangeListener listener = new NumberChangeListener(name, textField);
		numberListeners.put(name, listener);
		textField.addPropertyChangeListener(listener);
		panel.add(label, BorderLayout.LINE_START);
		panel.add(textField, BorderLayout.LINE_END);
		return panel;
	}

	/**
	 * Creates a new settings panel for a enum setting.
	 * 
	 * @param name   the name of the setting.
	 * @param values all possible values for this setting.
	 * @param value  its default value.
	 * @return the settings panel.
	 */
	private JPanel createEnumSetting(String name, String[] values, String value) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel(name);
		JComboBox<String> selection = new JComboBox<String>(values);
		selection.setSelectedItem(value);
		EnumChangeListener listener = new EnumChangeListener(name, selection);
		enumListeners.put(name, listener);
		selection.addActionListener(listener);
		panel.add(label, BorderLayout.LINE_START);
		panel.add(selection, BorderLayout.LINE_END);
		return panel;
	}

	/**
	 * Gets this ConfigWindows window.
	 * 
	 * @return this ConfigWindows window.
	 */
	public JFrame getWindow() {
		return window;
	}

	/**
	 * Make this ConfigWindow visible.
	 */
	public void show() {
		window.setVisible(true);
	}

	private class NumberChangeListener implements PropertyChangeListener {

		private final String name;
		private final JFormattedTextField field;

		/**
		 * Creates a new NumberChangeListener.
		 * 
		 * @param name      the name of the option to listen.
		 * @param textField the text field to listen for changes on.
		 */
		private NumberChangeListener(String name, JFormattedTextField textField) {
			this.name = name;
			field = textField;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			cfg.setConfig(name, ((Number) field.getValue()).intValue());
		}

	}

	private class EnumChangeListener implements ActionListener {

		private final String name;
		private final JComboBox<String> field;
		private Object lastSelected;

		/**
		 * Creates a new EnumChangeListener.
		 * 
		 * @param name     the name of the option to listen.
		 * @param comboBox the text field to listen for changes on.
		 */
		private EnumChangeListener(String name, JComboBox<String> comboBox) {
			this.name = name;
			field = comboBox;
			lastSelected = comboBox.getSelectedItem();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (field.getSelectedItem() != lastSelected) {
				cfg.setConfig(name, field.getSelectedItem());
				lastSelected = field.getSelectedItem();
			}
		}

	}

	private static class CloseAction extends AbstractAction {

		/**
		 * This classes serverialVersionUID.
		 */
		private static final long serialVersionUID = -1564698430944309082L;
		private final JFrame frame;

		/**
		 * Creates a new CloseAction with the given text, closing the given
		 * {@link JFrame}.
		 * 
		 * @param text  the text for the {@link JButton}.
		 * @param frame the {@link JFrame} to close.
		 */
		private CloseAction(String text, JFrame frame) {
			super(text);
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			frame.dispose();
		}

	}

}

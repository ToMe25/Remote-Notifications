package com.tome25.remotenotifications.client.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.tome25.remotenotifications.client.notification.Notifications;
import com.tome25.remotenotifications.client.utility.IconHandler;
import com.tome25.remotenotifications.client.utility.JsonFormat;
import com.tome25.remotenotifications.client.utility.PopupManager;
import com.tome25.remotenotifications.config.ConfigHandler;
import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonParser;

/**
 * A gui to change the client config options.
 * 
 * @author ToMe25
 *
 */
public class ConfigWindow {

	private JFrame window;
	private final ClientConfig cfg;
	private Map<String, NumberChangeListener> numberListeners = new HashMap<String, NumberChangeListener>();
	private Map<String, EnumChangeListener> enumListeners = new HashMap<String, EnumChangeListener>();
	private Map<String, BooleanChangeListener> booleanListeners = new HashMap<String, BooleanChangeListener>();
	private Map<String, JsonChangeListener> jsonListeners = new HashMap<String, JsonChangeListener>();

	/**
	 * Creates a new ConfigWindow.
	 * 
	 * @param config the {@link ConfigHandler} to edit.
	 */
	public ConfigWindow(ClientConfig config) {
		cfg = config;
		cfg.registerUpdateHandler(cfg -> update());
		initWindow();
	}

	/**
	 * Initializes this configs window.
	 */
	private void initWindow() {
		if (GraphicsEnvironment.isHeadless()) {
			return;
		}
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
		configPanel.add(createEnumSetting(ClientConfig.NOTIFICATION_STYLE, Notifications.names(),
				cfg.getConfig(ClientConfig.NOTIFICATION_STYLE)));
		configPanel.add(
				createNumberSetting(ClientConfig.NOTIFICATION_TIME, cfg.getConfig(ClientConfig.NOTIFICATION_TIME)));
		configPanel.add(createNumberSetting(ClientConfig.UDP_PORT, cfg.getConfig(ClientConfig.UDP_PORT)));
		configPanel.add(createNumberSetting(ClientConfig.TCP_PORT, cfg.getConfig(ClientConfig.TCP_PORT)));
		configPanel.add(createBooleanSetting(ClientConfig.CONFIRM_EXIT, cfg.confirmExit()));
		configPanel.add(createJsonSetting(ClientConfig.SERVERS, cfg.getConfig(ClientConfig.SERVERS).toString()));
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
		numberListeners.forEach((name, listener) -> listener.textField.setValue(cfg.getConfig(name)));
		enumListeners.forEach((name, listener) -> listener.comboBox.setSelectedItem(cfg.getConfig(name)));
		booleanListeners.forEach((name, listener) -> listener.checkBox.setSelected(cfg.getConfig(name)));
		jsonListeners.forEach((name, listener) -> listener.textField.setText(cfg.getConfig(name).toString()));
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
	 * Creates a new settings panel for a boolean setting.
	 * 
	 * @param name  the name of the setting.
	 * @param value its default value.
	 * @return the settings panel.
	 */
	private JPanel createBooleanSetting(String name, boolean value) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel(name);
		JCheckBox checkBox = new JCheckBox("", value);
		BooleanChangeListener listener = new BooleanChangeListener(name, checkBox);
		checkBox.setAction(listener);
		booleanListeners.put(name, listener);
		panel.add(label, BorderLayout.LINE_START);
		panel.add(checkBox, BorderLayout.LINE_END);
		return panel;
	}

	/**
	 * Creates a new settings panel for a json setting.
	 * 
	 * @param name  the name of the setting.
	 * @param value its default value.
	 * @return the settings panel.
	 */
	private JPanel createJsonSetting(String name, String value) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel(name);
		JFormattedTextField textField = new JFormattedTextField(new JsonFormat());
		try {
			textField.setValue(JsonParser.parseString(value));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		textField.setColumns(12);
		JsonChangeListener listener = new JsonChangeListener(name, textField);
		jsonListeners.put(name, listener);
		textField.addPropertyChangeListener(listener);
		panel.add(label, BorderLayout.LINE_START);
		panel.add(textField, BorderLayout.LINE_END);
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
		private final JFormattedTextField textField;

		/**
		 * Creates a new NumberChangeListener.
		 * 
		 * @param name      the name of the option to listen.
		 * @param textField the {@link JFormattedTextField} to listen for changes on.
		 */
		private NumberChangeListener(String name, JFormattedTextField textField) {
			this.name = name;
			this.textField = textField;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			cfg.setConfig(name, ((Number) textField.getValue()).intValue());
		}

	}

	private class EnumChangeListener implements ActionListener {

		private final String name;
		private final JComboBox<String> comboBox;
		private Object lastSelected;

		/**
		 * Creates a new EnumChangeListener.
		 * 
		 * @param name     the name of the option to listen.
		 * @param comboBox the {@link JComboBox} to listen for changes on.
		 */
		private EnumChangeListener(String name, JComboBox<String> comboBox) {
			this.name = name;
			this.comboBox = comboBox;
			lastSelected = comboBox.getSelectedItem();
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (comboBox.getSelectedItem() != lastSelected) {
				cfg.setConfig(name, comboBox.getSelectedItem());
				lastSelected = comboBox.getSelectedItem();
			}
		}

	}

	private class BooleanChangeListener extends AbstractAction {

		/**
		 * This classes serverialVersionUID.
		 */
		private static final long serialVersionUID = -5351813088252925510L;
		private final String name;
		private final JCheckBox checkBox;

		/**
		 * Creates a new BooleanChangeListener.
		 * 
		 * @param name     the name of the option to listen.
		 * @param checkBox the {@link JCheckBox} to listen for changes on.
		 */
		private BooleanChangeListener(String name, JCheckBox checkBox) {
			super("");
			this.name = name;
			this.checkBox = checkBox;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			cfg.setConfig(name, checkBox.isSelected());
		}

	}

	private class JsonChangeListener implements PropertyChangeListener {

		private final String name;
		private final JFormattedTextField textField;

		/**
		 * Creates a new JsonChangeListener.
		 * 
		 * @param name      the name of the option to listen.
		 * @param textField the {@link JFormattedTextField} to listen for changes on.
		 */
		private JsonChangeListener(String name, JFormattedTextField textField) {
			this.name = name;
			this.textField = textField;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			cfg.setConfig(name, (JsonElement) textField.getValue());
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

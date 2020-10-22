package com.tome25.remotenotifications.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.tome25.utils.config.Config;

/**
 * This programs configuration handler. This class handles the client
 * configuration file, reading and writing its contents using
 * ToMe25s-Java-Utilities {@link com.tome25.utils.config.Config Config} system.
 * 
 * @author ToMe25
 *
 */
public abstract class ConfigHandler {

	private final List<Consumer<ConfigHandler>> updateHandlers = new ArrayList<Consumer<ConfigHandler>>();
	private final Config config;

	/**
	 * Creates a new ConfigHandler.
	 * 
	 * @param rootDir the directory in which to create the config directory storing
	 *                all the config files for this ConfigHandler.
	 */
	public ConfigHandler(File rootDir) {
		config = new Config(false, new File(rootDir, "Remote-Notifications-Config"), true, file -> updateConfig());
	}

	/**
	 * Initializes this config object, and its config file if necessary.
	 */
	protected abstract void initConfig();

	/**
	 * Gets the config value for the given Name.
	 * 
	 * @param <T>    the option type.
	 * @param option the name of the config option to get.
	 * @return the config value for the given Name.
	 */
	public <T> T getConfig(String option) {
		return config.getConfig(option);
	}

	/**
	 * Sets the given config option to the given value. Will be synchronized to the
	 * config file.
	 * 
	 * @param <T>    the type of the value.
	 * @param option the option to set.
	 * @param value  the value to set the option to.
	 */
	public synchronized <T> void setConfig(String option, T value) {
		T oldValue = config.getConfig(option);
		config.setConfig(option, value);
		if (!oldValue.equals(value)) {
			updateConfig();
		}
	}

	/**
	 * Updates the values from the config, that are stored elsewhere.
	 */
	protected void updateConfig() {
		updateHandlers.forEach(handler -> handler.accept(this));
	}

	/**
	 * Registeres a {@link Consumer} that will be called with this ConfigHandler
	 * every time the config file updates.
	 * 
	 * @param updateHandler the update handler.
	 */
	public void registerUpdateHandler(Consumer<ConfigHandler> updateHandler) {
		updateHandlers.add(updateHandler);
	}

	/**
	 * Gets the underlying config object.
	 * 
	 * @return the underlying config object.
	 */
	protected Config getConfig() {
		return config;
	}

	/**
	 * Deletes all the config files.
	 */
	public void deleteConfig() {
		config.delete();
	}

}

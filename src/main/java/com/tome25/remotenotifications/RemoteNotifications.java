package com.tome25.remotenotifications;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tome25.remotenotifications.network.Receiver;
import com.tome25.remotenotifications.network.Sender;
import com.tome25.utils.lib.LibraryDownloader;
import com.tome25.utils.lib.LibraryLoader;

/**
 * A Tool to create popup notifications from another Device.
 * 
 * @author ToMe25
 *
 */
public class RemoteNotifications {

	public static Receiver receiver;
	public static Sender sender;
	public static TrayIconManager icon;
	public static ConfigHandler config;

	public static void main(String[] args) {
		try {
			LibraryLoader loader = new LibraryLoader(args);
			LibraryDownloader.downloadThis();
			loader.addThisToClasspath();
			com.tome25.utils.logging.LogTracer.traceOutputs(new File("Remote-Notifications.log"));// importing this
																									// would cause it to
																									// crash on loading.
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(args.length > 0 && args[0].isEmpty()) {
			List<String> argsList = new ArrayList<String>();
			for(String arg:args) {
				if(!arg.replaceAll(" ", "").isEmpty()) {
					argsList.add(arg);
				}
			}
			args = argsList.toArray(new String[0]);
		}
		if (args.length == 1 && args[0].contains(" ")) {
			args = splitArgs(args[0]);
		}
		Map<String, String> arguments = new HashMap<String, String>();
		for (String arg : args) {
			while (arg.startsWith("-")) {
				arg = arg.substring(1);
			}
			if (!arg.isEmpty()) {
				if (arg.contains("=")) {
					arguments.put(arg.substring(0, arg.indexOf('=')), arg.substring(arg.indexOf('=') + 1));
				} else {
					arguments.put(arg, "true");
				}
			}
		}
		if (arguments.containsKey("server") && arguments.get("server").equalsIgnoreCase("true")) {
			initServer();
			if (arguments.containsKey("header") && arguments.containsKey("message")) {
				try {
					sender.send(arguments.get("header"), arguments.get("message"));
					System.out.format("Sent a notification with header \"%s\" and message \"%s\".",
							arguments.get("header"), arguments.get("message"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			initClient();
		}
	}

	/**
	 * Initializes the Server.
	 */
	public static void initServer() {
		try {
			config = new ConfigHandler(true);
			sender = new Sender(config.port, config.clientAddress);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the Client.
	 */
	public static void initClient() {
		try {
			icon = new TrayIconManager();
			config = new ConfigHandler(false);
			receiver = new Receiver(config.port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts a String of arguments into a String array of arguments.
	 * 
	 * @param args the arguments String.
	 * @return the arguments String array.
	 */
	private static String[] splitArgs(String args) {
		List<String> argsList = new ArrayList<String>();
		boolean string = false;
		boolean backslash = false;
		String arg = "";
		for (char c : args.toCharArray()) {
			if (c == '"' && !backslash) {
				string = !string;
				backslash = false;
			} else if (c == '\\' && !backslash) {
				backslash = true;
			} else if (c == ' ' && !string && !backslash) {
				argsList.add(arg);
				arg = "";
				backslash = false;
			} else {
				backslash = false;
				arg += c;
			}
		}
		argsList.add(arg);
		return argsList.toArray(new String[0]);
	}
}

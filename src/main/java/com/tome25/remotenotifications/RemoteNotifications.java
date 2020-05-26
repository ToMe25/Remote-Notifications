package com.tome25.remotenotifications;

import java.io.File;
import java.util.LinkedHashMap;
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

	/**
	 * This programs main method.
	 * 
	 * @param args the start arguments.
	 */
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
		Map<String, String> arguments = ArgumentParser.parse(args);
		if (arguments.containsKey("help") && arguments.get("help").equalsIgnoreCase("true")) {
			printHelp();
		} else if (arguments.containsKey("server") && arguments.get("server").equalsIgnoreCase("true")) {
			initServer();
			if (arguments.containsKey("address")) {
				config.setConfig("client-address", arguments.get("address"));
			}
			if (arguments.containsKey("udpport")) {
				config.setConfig("client-udp-port", Integer.parseInt(arguments.get("udpport")));
			}
			if (arguments.containsKey("tcpport")) {
				config.setConfig("client-tcp-port", Integer.parseInt(arguments.get("tcpport")));
			}
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
			if (arguments.containsKey("udpport")) {
				config.setConfig("udp-port", Integer.parseInt(arguments.get("udpport")));
				receiver.stop();
				receiver = new Receiver(config.udpPort, config.tcpPort);
			}
			if (arguments.containsKey("tcpport")) {
				config.setConfig("tcp-port", Integer.parseInt(arguments.get("tcpport")));
			}
		}
	}

	/**
	 * Initializes the Server.
	 */
	public static void initServer() {
		if (!DependencyChecker.checkDependencies()) {
			return;
		}
		try {
			config = new ConfigHandler(true);
			sender = new Sender(config.clientAddress, config.udpPort, config.tcpPort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the Client.
	 */
	public static void initClient() {
		if (!DependencyChecker.checkDependencies()) {
			return;
		}
		try {
			icon = new TrayIconManager();
			config = new ConfigHandler(false);
			receiver = new Receiver(config.udpPort, config.tcpPort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints help into the system output.
	 */
	public static void printHelp() {
		System.out.println("Remote-Notifications help");
		System.out.println("=========================");
		File codeSource = new File(
				RemoteNotifications.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		System.out.format("java -jar %s [OPTIONS]%n", codeSource.getName());
		System.out.println("-------------------------");
		printOptionsHelp();
	}

	/**
	 * Prints the options part of the help output.
	 */
	private static void printOptionsHelp() {
		System.out.println("Options:");
		Map<String, String> optionsHelp = new LinkedHashMap<String, String>();
		optionsHelp.put("help", "Prints this help and stops.");
		optionsHelp.put("server", String.format(
				"Runs this program in server mode, meaning it will send notifications.%nThis will not work without -header and -message."));
		optionsHelp.put("header=HEADER", "The header to send to the client. Only works in server mode.");
		optionsHelp.put("message=MESSAGE", "The message for the notification to send. Only works in server mode.");
		optionsHelp.put("address=ADDRESS", String.format("The address to send the notification to. Only works in server mode.%nDefault is \"localhost\"."));
		optionsHelp.put("udpport=PORT", String.format(
				"The port to use for udp.%nOn the server this is the target port, on the client its the port to listen on.%nSet to 0 to disable udp. Default is 3112."));
		optionsHelp.put("tcpport=PORT", String.format(
				"The port to use for tcp.%nOn the server this is the target port, on the client its the port to listen on.%nSet to 0 to disable tcp. Default is 3113."));
		Map<String, String> optionsHelp1 = new LinkedHashMap<String, String>();
		final int[] length = new int[] { 0 };
		optionsHelp.keySet().forEach(key -> {
			String newKey = "-" + key;
			String k = key;
			if (newKey.contains("=")) {
				k = k.substring(0, k.indexOf('='));
			}
			if (ArgumentParser.ARG_TO_ALIASSES.containsKey(k)) {
				String[] k1 = new String[] { newKey };
				ArgumentParser.ARG_TO_ALIASSES.get(k).forEach(alias -> k1[0] = String.format("-%s %s", alias, k1[0]));
				newKey = k1[0];
			}
			optionsHelp1.put(newKey, optionsHelp.get(key));
			if (newKey.length() > length[0]) {
				length[0] = newKey.length();
			}
		});
		length[0] += 2;
		optionsHelp1.forEach((key, value) -> {
			key = String.format("%1$-" + length[0] + "s", key);
			value = value.replaceAll("\n", String.format("\n%1$" + (length[0] + 3) + "s", ""));
			System.out.format("  %s %s%n", key, value);
		});
	}
}

package com.tome25.remotenotifications.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class to parse command line options.
 * 
 * @author ToMe25
 *
 */
public class ArgumentParser {

	private static List<String> args = Arrays
			.asList(new String[] { "help", "server", "header", "message", "address", "udpport", "tcpport", "version" });
	public static final Map<String, List<String>> ARG_TO_ALIASSES;
	public static final Map<String, String> ALIAS_TO_ARG;

	static {
		ARG_TO_ALIASSES = new HashMap<String, List<String>>();
		ARG_TO_ALIASSES.put("help", Arrays.asList(new String[] { "info", "i" }));
		ARG_TO_ALIASSES.put("header", Arrays.asList(new String[] { "h" }));
		ARG_TO_ALIASSES.put("server", Arrays.asList(new String[] { "s" }));
		ARG_TO_ALIASSES.put("message", Arrays.asList(new String[] { "m" }));
		ARG_TO_ALIASSES.put("address", Arrays.asList(new String[] { "client", "a" }));
		ARG_TO_ALIASSES.put("udpport", Arrays.asList(new String[] { "udp", "u" }));
		ARG_TO_ALIASSES.put("tcpport", Arrays.asList(new String[] { "tcp", "t" }));
		ARG_TO_ALIASSES.put("version", Arrays.asList(new String[] { "v" }));
		ALIAS_TO_ARG = new HashMap<String, String>();
		initAliasToArg();
	}

	/**
	 * Parses the arguments String array to a easier to use Map.
	 * 
	 * @param args the arguments to parse.
	 * @return the parsed arguments.
	 */
	public static Map<String, String> parse(String[] args) {
		Map<String, String> arguments = new LinkedHashMap<String, String>();
		for (String arg : args) {
			if (!arg.trim().replaceAll(" ", "").isEmpty()) {
				while (arg.startsWith("-") || arg.startsWith(" ")) {
					arg = arg.substring(1);
				}
				if (isValidArg(arg.replaceAll(" ", "").toLowerCase())) {
					arguments.put(arg.replaceAll(" ", "").toLowerCase(), "true");
				} else if (arg.contains(" ") && isValidArg(arg.trim().substring(0, arg.indexOf(' ')).toLowerCase())) {
					arguments.put(arg.trim().substring(0, arg.indexOf(' ')).toLowerCase(),
							"true");
					arguments.putAll(parse(splitArgs(arg.substring(arg.indexOf(' ') + 1))));
				} else if (arg.contains("=") && isValidArg(arg.trim().substring(0, arg.indexOf('=')).toLowerCase())) {
					arguments.put(arg.trim().substring(0, arg.indexOf('=')).toLowerCase(),
							arg.substring(arg.indexOf('=') + 1));
					if (arg.contains(" ")) {
						arguments.putAll(parse(splitArgs(arg.substring(arg.indexOf('=') + 1))));
					}
				} else if (!arguments.isEmpty()) {
					String lastKey = "";
					Iterator<String> iterator = arguments.keySet().iterator();
					while (iterator.hasNext()) {
						lastKey = iterator.next();
					}
					arguments.put(lastKey, arguments.get(lastKey) + " " + arg);
				}
			}
		}
		ALIAS_TO_ARG.forEach((alias, key) ->

		{
			if (arguments.containsKey(alias)) {
				arguments.put(key, arguments.get(alias));
				arguments.remove(alias);
			}
		});
		return arguments;
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

	/**
	 * Initializes the ALIAS_TO_ARG map.
	 */
	private static void initAliasToArg() {
		ALIAS_TO_ARG.clear();
		ARG_TO_ALIASSES.forEach((arg, aliasses) -> {
			aliasses.forEach(alias -> ALIAS_TO_ARG.put(alias, arg));
		});
	}

	/**
	 * Checks whether the give argument is a valid argument.
	 * 
	 * @param arg the argument to check.
	 * @return whether the give argument is a valid argument.
	 */
	private static boolean isValidArg(String arg) {
		return args.contains(arg) || ALIAS_TO_ARG.containsKey(arg);
	}

}

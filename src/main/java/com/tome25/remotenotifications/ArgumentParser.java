package com.tome25.remotenotifications;

import java.util.ArrayList;
import java.util.Arrays;
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
			.asList(new String[] { "help", "info", "server", "header", "message", "client", "address" });

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
				if (ArgumentParser.args.contains(arg.replaceAll(" ", "").toLowerCase())) {
					arguments.put(arg.replaceAll(" ", "").toLowerCase(), "true");
				} else if (arg.contains("=")
						&& ArgumentParser.args.contains(arg.trim().substring(0, arg.indexOf('=')).toLowerCase())) {
					if (arg.contains(" ")) {
						arguments.putAll(parse(splitArgs(arg)));
					} else {
						arguments.put(arg.trim().substring(0, arg.indexOf('=')).toLowerCase(),
								arg.substring(arg.indexOf("=") + 1));
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
		if (arguments.containsKey("info")) {
			arguments.put("help", arguments.get("info"));
			arguments.remove("info");
		}
		if (arguments.containsKey("client")) {
			arguments.put("address", arguments.get("client"));
			arguments.remove("client");
		}
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

}

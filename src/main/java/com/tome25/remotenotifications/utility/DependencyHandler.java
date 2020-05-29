package com.tome25.remotenotifications.utility;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import com.tome25.utils.lib.LibraryDownloader;
import com.tome25.utils.lib.LibraryLoader;
import com.tome25.utils.version.VersionControl;

/**
 * A utility class that checks whether all dependencies are met.
 * 
 * @author ToMe25
 * 
 */
public class DependencyHandler {

	private static final File codeSource = new File(
			DependencyHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	private static List<Dependency> dependencies;

	/**
	 * Initializes a list of dependencies.
	 */
	public static void initDependencies() {
		dependencies = new ArrayList<Dependency>();
		dependencies.add(new Dependency("ToMe25s-Java-Utilities", new int[] { 1, 0, 130 },
				"https://github.com/ToMe25/ToMe25s-Java-Utilities/raw/master/ToMe25s-Java-Utilities.jar",
				"com.tome25.utils.version.VersionControl"));
		dependencies.add(new Dependency("jna", new int[] { 5, 2, 0 },
				"https://repo1.maven.org/maven2/net/java/dev/jna/jna/5.5.0/jna-5.5.0.jar", "com.sun.jna.Version"));
		dependencies.add(new Dependency("jna-platform", new int[] { 5, 2, 0 },
				"https://repo1.maven.org/maven2/net/java/dev/jna/jna-platform/5.5.0/jna-platform-5.5.0.jar",
				"com.tome25.remotenotifications.utility.DependencyHandler"));
		// jna-platform can't be checked, because its platform dependent.
		dependencies.add(new Dependency("slf4j", new int[] { 1, 7, 26 },
				"https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.30/slf4j-api-1.7.30.jar", "org.slf4j.Logger"));
		dependencies.add(new Dependency("javassist", new int[] { 3, 24, 1 },
				"https://repo1.maven.org/maven2/org/javassist/javassist/3.27.0-GA/javassist-3.27.0-GA.jar",
				"javassist.Loader"));
		dependencies.add(new Dependency("ShellExecutor", new int[] { 1, 1 },
				"https://repo1.maven.org/maven2/com/dorkbox/ShellExecutor/1.1/ShellExecutor-1.1.jar",
				"dorkbox.executor.ShellExecutor"));
		dependencies.add(new Dependency("SystemTray", new int[] { 3, 1, 17 },
				"https://repo1.maven.org/maven2/com/dorkbox/SystemTray/3.17/SystemTray-3.17.jar",
				"dorkbox.systemTray.SystemTray"));
	}

	/**
	 * Checks whether all libraries are at least the minimal required version.
	 * 
	 * @param download Whether any are missing/outdated libraries should get
	 *                 download. Requires a restart this software.
	 * @return whether all libraries are at least the minimal required version.
	 */
	public static boolean checkDependencies(boolean download) {
		boolean ok = true;
		for (Dependency dep : dependencies) {
			if (!isLoaded(dep.getCheckClass()) || !checkDependency(dep.getName(),
					VersionControl.getVersionArray(dep.getName()), dep.getVersion())) {
				ok = false;
				if (download) {
					if (!new LibraryDownloader(dep.getUrlStorage(), dep.getUrlStorageContent(), true, false)
							.downloadFile()) {
					}
				}
			}
		}
		if (!ok && download) {
			LibraryLoader.addLibsToClasspath();
			restart();
		}
		return ok;
	}

	/**
	 * Checks whether the given library is at least at the required version number.
	 * 
	 * @param name     the name of the library.
	 * @param current  the currently used version of the library.
	 * @param required the minimal required version of the library.
	 * @return whether the given library is at least at the required version number.
	 */
	private static boolean checkDependency(String name, int[] current, int[] required) {
		if (current == null || current.length == 0 || current.equals(new int[] { 1, 0 })) {
			// the library probably can't give us the actual version number.
			return true;
		}
		if (current.length < required.length) {
			System.err.format(
					"Remote-Notifications requires atleast version %s of %s, but the currently used version is %s.%n",
					versionArrayToString(required), name, versionArrayToString(current));
			return false;
		}
		int i = 0;
		boolean less = false;
		while (i < current.length) {
			less = less && current[i] <= required[i] || current[i] < required[i];
			i++;
		}
		if (less) {
			System.err.format(
					"Remote-Notifications requires atleast version %s of %s, but the currently used version is %s.%n",
					versionArrayToString(required), name, versionArrayToString(current));
			return false;
		} else {
			return true;
		}
	}

	/**
	 * converts the integer array form of the version number to a human readable
	 * string form.
	 * 
	 * @param version the version number to convert.
	 * @return the converted version number.
	 */
	private static String versionArrayToString(int[] version) {
		String ver = "";
		for (int i : version) {
			ver += i + ".";
		}
		if (ver.length() > 0) {
			ver = ver.substring(0, ver.length() - 1);
		}
		return ver;
	}

	/**
	 * Restarts this software with the -no-downloads argument.
	 */
	private static void restart() {
		if (codeSource.isFile()) {
			System.out.println("Downloaded some libraries, restarting to load them.");
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", codeSource.getAbsolutePath(),
					stringArrayToString(
							ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0])),
					LibraryLoader.getMainArgs() + "-no-downloads");
			pb.inheritIO();
			try {
				pb.start();
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * converts a String Array to a String. entries are separated with a space.
	 * 
	 * @param array the array to get the string representation of.
	 * @return a string representation of the given string array.
	 */
	private static String stringArrayToString(String[] array) {
		String string = "";
		for (String str : array) {
			string += str + " ";
		}
		if (string.length() > 0) {
			string = string.substring(0, string.length() - 1);
		}
		return string;
	}

	/**
	 * Checks whether the library containing the given class is loaded.
	 * 
	 * @param className the name of the class to load.
	 * @return whether the library containing the given class is loaded.
	 */
	private static boolean isLoaded(String className) {
		try {
			Class.forName(className);
		} catch (Exception e) {
			if (e instanceof ClassNotFoundException) {
				return false;
			} else {
				e.printStackTrace();
			}
		}
		return true;
	}

	private static class Dependency {

		private final String name;
		private final int[] version;
		private final String defaultUrl;
		private final String checkClass;
		private final File urlStorage;
		private static final String defaultUrlFile = "# The url to download %s from.%n%s%n";

		/**
		 * Creates a new Dependency storing all the required information about the
		 * library.
		 * 
		 * @param name               the name of the library
		 * @param minVersion         its minimum required version.
		 * @param defaultDownloadUrl the default url to download the library from.
		 * @param checkClass         a class from the library, it will get loaded to
		 *                           check whether the library is loaded.
		 */
		private Dependency(String name, int[] minVersion, String defaultDownloadUrl, String checkClass) {
			this.name = name;
			version = minVersion;
			defaultUrl = defaultDownloadUrl;
			this.checkClass = checkClass;
			urlStorage = new File(codeSource.getParent(), name + "-Download-Url.txt");
		}

		/**
		 * Gets the libraries name.
		 * 
		 * @return the libraries name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the minimum required version of the library.
		 * 
		 * @return the minimum required version of the library.
		 */
		public int[] getVersion() {
			return version;
		}

		/**
		 * Gets the default url to download the library from.
		 * 
		 * @return the default url to download the library from.
		 */
		@SuppressWarnings("unused")
		public String getDefaultUrl() {
			return defaultUrl;
		}

		/**
		 * Gets the class to load to see if this library is loaded.
		 * 
		 * @return the class to load to see if this library is loaded.
		 */
		public String getCheckClass() {
			return checkClass;
		}

		/**
		 * Gets the {@link File} to store the download url for the library in.
		 * 
		 * @return the {@link File} to store the download url for the library in.
		 */
		public File getUrlStorage() {
			return urlStorage;
		}

		/**
		 * Gets the default content for for the {@link File} to store the download url
		 * in.
		 * 
		 * @return the default content for for the {@link File} to store the download
		 *         url in.
		 */
		public String getUrlStorageContent() {
			return String.format(defaultUrlFile, name, defaultUrl);
		}

	}

}

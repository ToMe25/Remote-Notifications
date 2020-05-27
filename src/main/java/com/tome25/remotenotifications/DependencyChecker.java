package com.tome25.remotenotifications;

import com.tome25.utils.version.VersionControl;

/**
 * A utility class that checks whether all dependencies are met.
 * 
 * @author ToMe25
 * 
 */
public class DependencyChecker {

	/**
	 * The minimal required version of ToMe25s-Java-Utilities.
	 */
	private static final int[] ToMe25s_Java_Utilities_VERSION = new int[] { 1, 0, 126 };

	/**
	 * Checks whether all libraries are at least the minimal required version.
	 * 
	 * @return whether all libraries are at least the minimal required version.
	 */
	public static boolean checkDependencies() {
		boolean ok = true;
		if (!checkDependency("ToMe25s-Java-Utilities", VersionControl.getVersionArray(),
				ToMe25s_Java_Utilities_VERSION)) {
			ok = false;
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

}

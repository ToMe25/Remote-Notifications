package com.tome25.remotenotifications;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

/**
 * A utility class to check for this softwares version.
 * 
 * @author ToMe25
 *
 */
public class VersionChecker {

	private static File file;
	private static JarFile archieve;

	/**
	 * gets the major part of this softwares version number.
	 * 
	 * @return the major version.
	 */
	public static int getMajor() {
		int[] version = getVersionArray();
		if (version.length > 0) {
			return version[0];
		} else {
			return 1;
		}
	}

	/**
	 * gets the minor part of this softwares version number.
	 * 
	 * @return the minor version.
	 */
	public static int getMinor() {
		int[] version = getVersionArray();
		if (version.length > 1) {
			return version[1];
		} else {
			return 0;
		}
	}

	/**
	 * gets the build part of this softwares version number.
	 * 
	 * @return the build number.
	 */
	public static int getBuild() {
		int[] version = getVersionArray();
		if (version.length > 2) {
			return version[2];
		} else {
			return 0;
		}
	}

	/**
	 * gets this softwares version number split into its components, if this is
	 * still in a Jar, if not this will return [1, 0]. the used format is [MAJOR,
	 * MINOR, BUILD].
	 * 
	 * @return the version number.
	 */
	public static int[] getVersionArray() {
		String[] split = getVersionString().split("\\.");
		int[] version = new int[split.length];
		int i = 0;
		for (String part : split) {
			version[i] = Integer.parseInt(part);
			i++;
		}
		return version;
	}

	/**
	 * gets this softwares version number as a string, if this is still in a Jar, if
	 * not this will return 1.0. the used format is "MAJOR.MINOR.BUILD".
	 * 
	 * @return the version number.
	 */
	public static String getVersionString() {
		if (file == null) {
			file = new File(VersionChecker.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		}
		if (file.isDirectory()) {
			return "1.0";
		}
		if (archieve == null) {
			try {
				archieve = new JarFile(file);
			} catch (IOException e) {
				e.printStackTrace();
				return "1.0";
			}
		}
		try {
			return archieve.getManifest().getMainAttributes().getValue("Implementation-Version");
		} catch (IOException e) {
			e.printStackTrace();
			return "1.0";
		}
	}

}

package com.tome25.remotenotifications;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import com.tome25.utils.version.VersionControl;

/**
 * A utility class to check for this softwares version.
 * 
 * @author ToMe25
 *
 */
public class VersionChecker {

	private static String versionString;
	private static int[] versionArray;

	static {
		VersionControl.setVersionString("Remote-Notifications", getVersionString());
	}

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
			return 0;
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
	 * gets the build/patch part of this softwares version number.
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
	 * gets the build/patch part of this softwares version number.
	 * 
	 * @return the build number.
	 */
	public static int getPatch() {
		return getBuild();
	}

	/**
	 * gets this softwares version number split into its components, if this is
	 * still in a Jar, if not this will return [1, 0]. the used format is [MAJOR,
	 * MINOR, BUILD].
	 * 
	 * @return the version number.
	 */
	public static int[] getVersionArray() {
		if (versionArray == null) {
			String[] split = getVersionString().split("\\.");
			int[] version = new int[split.length];
			int i = 0;
			for (String part : split) {
				version[i] = Integer.parseInt(part);
				i++;
			}
			versionArray = version;
			VersionControl.setVersionArray("Remote-Notifications", versionArray);
		}
		return versionArray;
	}

	/**
	 * Gets this softwares version number as a string, if this is still in a Jar, if
	 * not this will return 1.0. the used format is "MAJOR.MINOR.BUILD".
	 * 
	 * @return the version number.
	 */
	public static String getVersionString() {
		if (versionString == null) {
			File file = new File(VersionChecker.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			JarFile jar = null;
			if (file.isDirectory()) {
				versionString = "1.0";
			} else {
				try {
					jar = new JarFile(file);
				} catch (IOException e) {
					e.printStackTrace();
					versionString = "1.0";
				}
			}
			if (!file.isDirectory()) {
				try {
					versionString = jar.getManifest().getMainAttributes().getValue("Implementation-Version");
				} catch (Exception e) {
					e.printStackTrace();
					versionString = "1.0";
				}
			}
			VersionControl.setVersionString("Remote-Notifications", versionString);
		}
		return versionString;
	}

}

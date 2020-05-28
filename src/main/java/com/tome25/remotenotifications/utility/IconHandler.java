package com.tome25.remotenotifications.utility;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.tome25.utils.lib.JarExtractor;

/**
 * This class is a Image handling Utility, that will load and cache the Images.
 * 
 * @author ToMe25
 *
 */
public class IconHandler {

	private static final File codeSource = new File(
			IconHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	private static Map<String, BufferedImage> images = new HashMap<String, BufferedImage>();

	/**
	 * Gets this softwares logo as a {@link BufferedImage}.
	 * 
	 * @return this softwares logo as a {@link BufferedImage}.
	 */
	public static BufferedImage getLogoImage() {
		try {
			return getImage("RemoteNotifications.png");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the image for the given name from the cache, or from the disk if it
	 * isn't in the cache yet as a {@link BufferedImage}.
	 * 
	 * @param name the name of the image to look for.
	 * @return the {@link BufferedImage}.
	 * @throws IOException if reading the image fails.
	 */
	public static BufferedImage getImage(String name) throws IOException {
		if (images.containsKey(name)) {
			return images.get(name);
		} else {
			File image = new File(codeSource.getParent(), "RemoteNotifications.png");
			if (!image.exists()) {
				try {
					JarExtractor.extractFileFromJar(codeSource, "RemoteNotifications.png");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			BufferedImage img = ImageIO.read(image);
			images.put(name, img);
			return img;
		}
	}

	/**
	 * Gets this softwares logo as a {@link ImageIcon}.
	 * 
	 * @return this softwares logo as a {@link ImageIcon}.
	 */
	public static ImageIcon getLogoIcon() {
		try {
			return getIcon("RemoteNotifications.png");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the image for the given name from the cache, or from the disk if it
	 * isn't in the cache yet as a {@link ImageIcon}.
	 * 
	 * @param name the name of the image to look for. will also be used as the
	 *             {@link ImageIcon}s description.
	 * @return the {@link ImageIcon}.
	 * @throws IOException if reading the image fails.
	 */
	public static ImageIcon getIcon(String name) throws IOException {
		return getIcon(name, name);
	}

	/**
	 * Gets the image for the given name from the cache, or from the disk if it
	 * isn't in the cache yet as a {@link ImageIcon}.
	 * 
	 * @param name        the name of the image to look for.
	 * @param description the description for the {@link ImageIcon}.
	 * @return the {@link ImageIcon}.
	 * @throws IOException if reading the image fails.
	 */
	public static ImageIcon getIcon(String name, String description) throws IOException {
		return new ImageIcon(getImage(name), description);
	}

}

package com.tome25.remotenotifications.client.utility;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * This class is a Image handling Utility, that will load and cache the Images.
 * 
 * @author ToMe25
 *
 */
public class IconHandler {

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
			BufferedImage img = ImageIO.read(IconHandler.class.getClassLoader().getResourceAsStream(name));
			images.put(name, img);
			return img;
		}
	}

	/**
	 * Gets the image for the given name from the cache, or from the disk if it
	 * isn't in the cache yet as a {@link BufferedImage}, and resizes it afterwards.
	 * 
	 * @param name   the name of the image to look for.
	 * @param width  the width or the output image.
	 * @param height the height of the output image.
	 * @return the {@link BufferedImage}.
	 * @throws IOException if reading the image fails.
	 */
	public static BufferedImage getImageScaled(String name, int width, int height) throws IOException {
		BufferedImage image = getImage(name);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = resized.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		graphics2D.drawImage(image, 0, 0, width, height, null);
		graphics2D.dispose();
		return resized;
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

	/**
	 * Gets the image for the given name from the cache, or from the disk if it
	 * isn't in the cache yet as a {@link ImageIcon}, and resizes it.
	 * 
	 * @param name   the name of the image to look for. will also be used as the
	 *               {@link ImageIcon}s description.
	 * @param width  the width or the output image.
	 * @param height the height of the output image.
	 * @return the {@link ImageIcon}.
	 * @throws IOException if reading the image fails.
	 */
	public static ImageIcon getIconScaled(String name, int width, int height) throws IOException {
		return getIconScaled(name, name, width, height);
	}

	/**
	 * Gets the image for the given name from the cache, or from the disk if it
	 * isn't in the cache yet as a {@link ImageIcon}, and resizes it.
	 * 
	 * @param name        the name of the image to look for.
	 * @param description the description for the {@link ImageIcon}.
	 * @param width       the width or the output image.
	 * @param height      the height of the output image.
	 * @return the {@link ImageIcon}.
	 * @throws IOException if reading the image fails.
	 */
	public static ImageIcon getIconScaled(String name, String description, int width, int height) throws IOException {
		return new ImageIcon(getImageScaled(name, width, height), description);
	}

}

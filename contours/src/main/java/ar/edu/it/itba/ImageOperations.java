package ar.edu.it.itba;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import ar.edu.it.itba.processing.Contour;

public class ImageOperations {

	public static void drawContourOnBuffer(BufferedImage image, Contour c) {
		int red = new Color(255, 0, 0).getRGB();
		int yellow = new Color(255, 255, 0).getRGB();
		
		for (Point p : c.getLin()) {
			int x = p.x;
			int y = p.y;
			image.setRGB(x, y, yellow);
		}
		for (Point p : c.getLout()) {
			int x = p.x;
			int y = p.y;
			image.setRGB(x, y, red);
		}
	}
}

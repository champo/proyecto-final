package ar.edu.it.itba;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import ar.edu.it.itba.processing.Contour;

public class ImageOperations {


    public static Color phiColoring[] = new Color[3 * 3 * 3];
    static {
    	int index = 0;
    	int fraction = 255 / 3;

    	for (int i = 0; i < 3; i++) {
    		for (int j = 0; j < 3; j++) {
    			for (int k = 0; k < 3; k++) {
    				phiColoring[index++] = new Color(fraction * i, fraction * j, fraction * k);
    			}
    		}
    	}
    };

	public static void drawContourOnBuffer(final BufferedImage image, final Contour c) {

		for (Point p : c.getLin()) {
			int x = p.x;
			int y = p.y;
			image.setRGB(x, y, phiColoring[c.color].getRGB());
		}
		for (Point p : c.getLout()) {
			int x = p.x;
			int y = p.y;
			image.setRGB(x, y, phiColoring[c.color].getRGB());
		}
	}
}

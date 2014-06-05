package ar.edu.it.itba.ati.provider;

import java.awt.Color;
import java.awt.image.BufferedImage;

import ar.edu.it.itba.ati.Canvas;

public class ImageFromBuffer extends CanvasProvider {

	private BufferedImage original;

	public ImageFromBuffer(BufferedImage original) {
		this.original = original;
	}

	@Override
	public Canvas getCanvas() {

		Canvas canvas = new Canvas(original.getWidth(), original.getHeight());
		for (int i = 0; i < original.getWidth(); i++) {
			for (int j = 0; j < original.getHeight(); j++) {
				Color c = new Color(original.getRGB(i, j));
				canvas.setPixel(i, j, c.getRed(), c.getGreen(), c.getBlue());
			}
		}
		return canvas;
	}

}

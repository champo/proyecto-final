package ar.edu.it.itba.ati.provider;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import ar.edu.it.itba.ati.Canvas;

public class NewImageFromStream extends CanvasProvider {

	private final String name;
	private final BufferedImage img;

	public NewImageFromStream(final InputStream f, final String name) {

		BufferedImage img = null;
		try {
			img = ImageIO.read(f);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.img = img;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Canvas getCanvas() {

		final Canvas canvas = new Canvas(img.getWidth(), img.getHeight());
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				final Color c = new Color(img.getRGB(i, j));
				canvas.setPixel(i, j, c.getRed(), c.getGreen(), c.getBlue());
			}
		}

		return canvas;
	}
}

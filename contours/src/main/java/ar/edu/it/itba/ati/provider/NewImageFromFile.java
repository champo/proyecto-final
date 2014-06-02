package ar.edu.it.itba.ati.provider;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ar.edu.it.itba.ati.Canvas;

public class NewImageFromFile extends CanvasProvider {

	private final File file;

	public NewImageFromFile(final File f) throws IOException {
		file = f;
	}

	@Override
	public String toString() {
		return file.getName() + " - " + file.getParentFile().getAbsolutePath();
	}

	@Override
	public Canvas getCanvas() {
		BufferedImage img;
		try {
			img = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		Canvas canvas = new Canvas(img.getWidth(), img.getHeight());
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				Color c = new Color(img.getRGB(i, j));
				canvas.setPixel(i, j, c.getRed(), c.getGreen(), c.getBlue());
			}
		}

		return canvas;
	}
}

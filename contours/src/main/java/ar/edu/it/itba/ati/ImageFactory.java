package ar.edu.it.itba.ati;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ar.edu.it.itba.ati.provider.ImageFromBuffer;
import ar.edu.it.itba.ati.provider.NewImageFromFile;
import ar.edu.it.itba.ati.provider.NewImageFromStream;
import ar.edu.it.itba.ati.provider.NewImageWithEmptyBackground;

public class ImageFactory {

	public static Operations fromFile(final File f) throws IOException {
		return new Operations(new NewImageFromFile(f));
	}

	public static Operations fromStream(final InputStream f, final String name) throws IOException {
		return new Operations(new NewImageFromStream(f, name));
	}

	public static Operations emptyImage(final Color background) {
		return emptyImage(background, 200, 200);
	}

	public static Operations emptyImage(final Color background, final int width, final int height) {
		return new Operations(new NewImageWithEmptyBackground(width, height,
				background.getRed(), background.getGreen(), background.getBlue()));
	}

	public static Operations fromBufferedImage(final BufferedImage image) {
		return new Operations(new ImageFromBuffer(image));
	}
}

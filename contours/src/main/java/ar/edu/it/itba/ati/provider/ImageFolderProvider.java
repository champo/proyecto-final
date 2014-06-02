package ar.edu.it.itba.ati.provider;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageFolderProvider implements FrameProvider {

	private final File[] files;

	private int index;

	public ImageFolderProvider(final String folderPath) {
		File folder = new File(folderPath);
		files = folder.listFiles();
		index = 0;
	}

	@Override
	public BufferedImage nextFrame() throws IOException {

		if (index == files.length) {
			index = 0;
		}

		return new NewImageFromFile(files[index++]).getCanvas().getBufferedImage();
	}

}

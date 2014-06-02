package ar.edu.it.itba.ati.provider;

import ar.edu.it.itba.ati.Canvas;

public class NewImageWithEmptyBackground extends CanvasProvider {

	private final int red;
	private final int green;
	private final int blue;
	private final int width;
	private final int height;

	public NewImageWithEmptyBackground(final int width, final int height, final int red, final int green, final int blue) {
		this.width = width;
		this.height = height;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public String toString() {
		return new StringBuffer()
		.append("Empty background (")
		.append(red).append(",")
		.append(green).append(",")
		.append(blue).append(")").toString();
	}

	@Override
	public Canvas getCanvas() {
		Canvas canvas = new Canvas(width, height);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				canvas.setPixel(x, y, red, green, blue);
			}
		}

		return canvas;
	}
}

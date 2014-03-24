package ar.edu.it.itba.processing;

public class RGBPoint extends ColorPoint {

	public RGBPoint(final int rgb) {

		int red = rgb >> 16 & 0xFF;
		int green = rgb >> 8 & 0xFF;
		int blue = rgb & 0xFF;

		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public RGBPoint(final int red, final int blue, final int green) {
		super(red, green, blue);
	}

}

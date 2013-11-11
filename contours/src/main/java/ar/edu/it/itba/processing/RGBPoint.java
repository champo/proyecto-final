package ar.edu.it.itba.processing;

public class RGBPoint {

	public final int red;

	public final int blue;

	public final int green;

	public RGBPoint(final int rgb) {
		red = ((rgb >> 16) & 0xFF);
		green = ((rgb >> 8) & 0xFF);
		blue = (rgb & 0xFF);
	}

	public RGBPoint(final int red, final int green, final int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

}

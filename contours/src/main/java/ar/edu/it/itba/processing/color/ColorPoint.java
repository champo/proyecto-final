package ar.edu.it.itba.processing.color;


public class ColorPoint {

	public int red;
	public int blue;
	public int green;

	public ColorPoint(final int red, final int green, final int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	protected ColorPoint() {
	}

	public double diff(final ColorPoint reference) {
		return Math.abs(red - reference.red)
				+ Math.abs(green - reference.green)
				+ Math.abs(blue - reference.blue);
	}

	public enum Type {
		HSI,
		HS,
		RGB
	}

	public static ColorPoint buildFromRGB(final Type type, final int rgb) {
		switch (type) {
		case HSI:
			return new HSIPoint(rgb);
		case HS:
			return new HSPoint(rgb);
		case RGB:
			return new RGBPoint(rgb);
		default:
			throw new IllegalArgumentException();
		}
	}


	public Type getType() {
		return Type.RGB;
	}

	public static ColorPoint build(final Type type, final int red, final int blue, final int green) {
		switch (type) {
		case HSI:
			return new HSIPoint(red, blue, green);
		case HS:
			return new HSPoint(red, blue, green);
		case RGB:
			return new RGBPoint(red, blue, green);
		default:
			throw new IllegalArgumentException();
		}
	}
}
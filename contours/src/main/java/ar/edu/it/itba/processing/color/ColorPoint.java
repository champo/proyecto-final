package ar.edu.it.itba.processing.color;

import java.awt.Color;

import ar.edu.it.itba.processing.color.ColorPoint.Type;


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

	public static ColorPoint build(final Type type, final int red, final int green, final int blue) {
		switch (type) {
		case HSI:
			return new HSIPoint(red, green, blue);
		case HS:
			return new HSPoint(red, green, blue);
		case RGB:
			return new RGBPoint(red, green, blue);
		default:
			throw new IllegalArgumentException();
		}
	}

	public static ColorPoint buildFromHSI(Type type, int hue, int saturation, int intensity) {
		Color rgb = new Color(Color.HSBtoRGB(hue, saturation, intensity));
		return ColorPoint.build(Type.RGB, rgb.getRed(), rgb.getGreen(), rgb.getBlue());
	}

	@Override
	public String toString() {
		return "ColorPoint(" + getType().name() + ", " + red + ", " + blue + ", " + green + ")";
	}
}
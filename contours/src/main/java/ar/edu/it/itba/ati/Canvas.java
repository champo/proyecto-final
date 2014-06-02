package ar.edu.it.itba.ati;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Canvas implements Cloneable {

	public double red[];
	public double blue[];
	public double green[];

	public final int width;
	public final int height;
	public final int size;

	public Canvas(final BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();
		size = width * height;

		red = new double[size];
		blue = new double[size];
		green = new double[size];
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				final Color c = new Color(image.getRGB(i, j));
				red[i + width * j] = c.getRed();
				green[i + width * j] = c.getGreen();
				blue[i + width * j] = c.getBlue();
			}
		}
	}

	public Canvas(final int width, final int height) {
		this.width = width;
		this.height = height;
		size = width * height;

		red = new double[width * height];
		blue = new double[width * height];
		green = new double[width * height];
	}

	public Canvas(final int width, final int height, final double[] red, final double[] green, final double[] blue) {
		this.width = width;
		this.height = height;
		size = width * height;

		this.red = red;
		this.blue = blue;
		this.green = green;
	}

	public Canvas(final Canvas canvas) {
		width = canvas.width;
		height = canvas.height;
		size = canvas.size;
		red = Arrays.copyOf(canvas.red, canvas.red.length);
		green = Arrays.copyOf(canvas.green, canvas.green.length);
		blue = Arrays.copyOf(canvas.blue, canvas.blue.length);
	}

	public double getRed(final int x, final int y) {
		return red[x + y * width];
	}

	public double getBlue(final int x, final int y) {
		return blue[x + y * width];
	}

	public double getGreen(final int x, final int y) {
		return green[x + y * width];
	}

	public double getGrey(final int x, final int y) {
		return red[x + y * width];
	}

	public void setPixel(final int x, final int y, final int r, final int g, final int b) {
		final int offset = x + y * width;
		red[offset] = r;
		green[offset] = g;
		blue[offset] = b;
	}

	public BufferedImage getBufferedImage() {

		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				final int offset = x + y * width;
				image.setRGB(x, y, new Color((int) red[offset], (int) green[offset], (int) blue[offset]).getRGB());
			}
		}

		return image;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double[] getGrayHistogramValues() {

		final double[] res = new double[size];
		for (int i = 0; i < size; i++) {
			res[i] = 0;
		}
		for (int i = 0; i < size; i++) {
			res[i] = (int) ((red[i] + green[i] + blue[i]) / 3);
		}
		return res;
	}

	@Override
	public Canvas clone() throws CloneNotSupportedException {
		return (Canvas) super.clone();
	}
}

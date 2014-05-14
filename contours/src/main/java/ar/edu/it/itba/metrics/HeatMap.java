package ar.edu.it.itba.metrics;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class HeatMap {

	private final static double alpha = 0.6;

	private final static int DISTANCE = 5;

	private final static double SIGMA = 2;

	private final static double DIV = 1 / (2 * Math.PI * SIGMA * SIGMA);

	private final float[][] heat;

	private final BufferedImage image;

	private final GaussianFilter filter = new GaussianFilter(0.6, DISTANCE / 2);

	public HeatMap(final BufferedImage image) {
		super();
		this.image = image;
		this.heat = new float[image.getWidth()][image.getHeight()];
	}

	public void addPoint(final Point p) {

		final int width = image.getWidth();
		final int height = image.getHeight();

		final int minX = Math.max(0, p.x - DISTANCE);
		final int maxX = Math.min(width - 1, p.x + DISTANCE);

		for (int x = minX; x < maxX; x++) {

			final int minY = Math.max(0, p.y - DISTANCE);
			final int maxY = Math.min(height - 1, p.y + DISTANCE);

			for (int y = minY; y < maxY; y++) {

				double squareSum = Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2);
				double distance = Math.sqrt(squareSum);
				if (distance < DISTANCE) {
					heat[x][y] += 2 * DISTANCE * DIV * Math.exp(-squareSum / (2 * SIGMA * SIGMA));
				}
			}

		}
	}

	public BufferedImage getFrame() {
		BufferedImage frame = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

		final int width = image.getWidth();
		final int height = image.getHeight();

//		float[][] data = filter.operate(heat);
		float[][] data = heat;

		float max = 0;
		for (int i = 0; i <  width; i++) {
			for (int j = 0; j < height; j++) {
				if (data[i][j] > max) {
					max = data[i][j];
				}
			}
		}

		for (int i = 0; i <  width; i++) {
			for (int j = 0; j < height; j++) {

				if (data[i][j] > 0) {
					float hue = 140.0f * (0.99f - data[i][j] / max) / 360.0f;

					int rgb = (int) (Color.HSBtoRGB(hue, 1, 1) * alpha + image.getRGB(i, j) * (1 - alpha));
					frame.setRGB(i, j, rgb);
				} else {
					frame.setRGB(i, j, image.getRGB(i, j));
				}
			}
		}

		return frame;
	}
}

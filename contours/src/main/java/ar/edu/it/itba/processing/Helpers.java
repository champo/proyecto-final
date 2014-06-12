package ar.edu.it.itba.processing;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ar.edu.it.itba.processing.Contour.State;
import ar.edu.it.itba.processing.color.ColorPoint;
import ar.edu.it.itba.processing.color.ColorPoint.Type;

public final class Helpers {

	private static final double MAX_PIXEL_VALUE = 256 * 3;
	private static final int HISTOGRAM_SIZE = 10;
	private static final double FRACTION_HISTOGRAM = 0.2;

	// Define the radius to find the colors for the objects to track
	public final static int RADIUS_X = 20;
	public final static int RADIUS_Y = 50;
	public final static int BUCKETS = 32;

	private Helpers() {
	}

	public static ColorPoint[] getCharacteristics(final BufferedImage frame, final Contour contour) {
		 return new ColorPoint[] { getAverageColor(frame, contour) };
//		return getMostFrequentColors(frame, contour);
	}

	public static ColorPoint[] getBackgroundCharacteristics(final BufferedImage frame,
			final Contour contour, final Contour[] contours) {
		return new ColorPoint[] { getAverageBackgroundColor(frame, contour, contours) };
	}

	public static boolean isBorder(final BufferedImage frame, final Point p) {
		return p.x == 0 || p.y == 0 || p.x == frame.getWidth() - 1 || p.y == frame.getHeight() - 1;
	}

	public static List<Point> neighbors(final Point p, final int width, final int height) {
		final List<Point> l = new ArrayList<Point>(4);

		if (p.x < width - 1) {
			l.add(new Point(p.x + 1, p.y));
		}
		if (p.y < height - 1) {
			l.add(new Point(p.x, p.y + 1));
		}
		if (p.x > 0) {
			l.add(new Point(p.x - 1, p.y));
		}
		if (p.y > 0) {
			l.add(new Point(p.x, p.y - 1));
		}
		return l;
	}

	public static List<Point> neighbors8(final Point p, final int width, final int height) {
		final List<Point> l = new ArrayList<Point>(4);

		if (p.x < width - 1) {
			l.add(new Point(p.x + 1, p.y));

			if (p.y < height - 1) {
				l.add(new Point(p.x + 1, p.y + 1));
			}

			if (p.y > 0) {
				l.add(new Point(p.x + 1, p.y - 1));
			}
		}
		if (p.y < height - 1) {
			l.add(new Point(p.x, p.y + 1));
		}
		if (p.x > 0) {
			l.add(new Point(p.x - 1, p.y));

			if (p.y < height - 1) {
				l.add(new Point(p.x - 1, p.y + 1));
			}

			if (p.y > 0) {
				l.add(new Point(p.x - 1, p.y - 1));
			}
		}
		if (p.y > 0) {
			l.add(new Point(p.x, p.y - 1));
		}

		return l;
	}


	public static int max(final int a, final int b) {
		return a > b ? a : b;
	}

	public static Double prob(final BufferedImage frame, final Contour c, final Point p) {
		ColorPoint color = ColorPoint.buildFromRGB(c.getType(), frame.getRGB(p.x, p.y));
		return Math.log((1 - diffObject(frame, c, p, color)) -  Math.log(1 - backgroundDiff(color, p, frame, c.bgDeviation, c.omegaZero)));
	}

	public static double diffObject(final BufferedImage frame, final Contour c, final Point p, final ColorPoint color) {

		if (color.red == 0 && color.green == 0 && color.blue == 0) {
			return 1;
		}

		double diff = colorDiff(color, p, frame, c.getLastStdDev(), c.omega);
		if (c.getState() == State.MISSING) {
			return diff;
		}

		double distX = Math.abs(p.x - c.getLastCentroid().x);
		double distY = Math.abs(p.y - c.getLastCentroid().y);

		diff += distX < 6 ? 0 : Math.min(1, (distX - 6) / 10);
		diff += distY < 10 ? 0 : Math.min(1, (distY - 10) / 20);

		return Math.min(1, diff);
	}

	public static ColorPoint calculateStandardDeviation(final Type type, final Point center, final BufferedImage frame) {
		double red = 0;
		double green = 0;
		double blue = 0;
		int points = 0;

		List<Point> pixels = neighbors8(center, frame.getWidth(), frame.getHeight());
		pixels.add(center);

		for (final Point p : pixels) {
			final ColorPoint v = ColorPoint.buildFromRGB(type, frame.getRGB(p.x, p.y));
			red += v.red;
			green += v.green;
			blue += v.blue;
			points++;
		}

		final double avgRed = red / points;
		final double avgGreen = green / points;
		final double avgBlue = blue / points;

		double redDeviation = 0;
		double blueDeviation = 0;
		double greenDeviation = 0;

		for (final Point p : pixels) {
			final ColorPoint v = ColorPoint.buildFromRGB(type, frame.getRGB(p.x, p.y));
			redDeviation += Math.pow(v.red - avgRed, 2);
			greenDeviation += Math.pow(v.green - avgGreen, 2);
			blueDeviation += Math.pow(v.blue - avgBlue, 2);
		}

		redDeviation = Math.sqrt(redDeviation / (points - 1));
		blueDeviation = Math.sqrt(blueDeviation / (points - 1));
		greenDeviation = Math.sqrt(greenDeviation / (points - 1));

		return ColorPoint.build(type, (int) redDeviation, (int) greenDeviation, (int) blueDeviation);
	}

	public static ColorPoint calculateStandardDeviation(final Type type, final Contour[] contours, final BufferedImage frame) {


		double red = 0;
		double green = 0;
		double blue = 0;
		int points = 0;


		final int width = frame.getWidth();
		final int height = frame.getHeight();
		final int black = Color.black.getRGB();

		Set<Point> pixels = new HashSet<>();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				boolean skip = false;
				final Point p = new Point(x, y);

				// Avoid border points
				for (Point n : neighbors8(p, width, height)) {
					if (black == frame.getRGB(n.x, n.y)) {
						skip = true;
						break;
					}
				}

				// Skip anything inside a contour
				for (Contour c : contours) {
					if (c.contains(x, y)) {
						skip = true;
						break;
					}
				}

				if (!skip) {
					final ColorPoint v = ColorPoint.buildFromRGB(type, frame.getRGB(p.x, p.y));
					red += v.red;
					green += v.green;
					blue += v.blue;
					points++;

					pixels.add(p);
				}
			}
		}

		final double avgRed = red / points;
		final double avgGreen = green / points;
		final double avgBlue = blue / points;

		double redDeviation = 0;
		double blueDeviation = 0;
		double greenDeviation = 0;

		for (final Point p : pixels) {
			final ColorPoint v = ColorPoint.buildFromRGB(type, frame.getRGB(p.x, p.y));
			redDeviation += Math.pow(v.red - avgRed, 2);
			greenDeviation += Math.pow(v.green - avgGreen, 2);
			blueDeviation += Math.pow(v.blue - avgBlue, 2);
		}

		redDeviation = Math.sqrt(redDeviation / (points - 1));
		blueDeviation = Math.sqrt(blueDeviation / (points - 1));
		greenDeviation = Math.sqrt(greenDeviation / (points - 1));

		return ColorPoint.build(type, (int) redDeviation, (int) greenDeviation, (int) blueDeviation);
	}

	public static ColorPoint calculateStandardDeviation(final Contour c, final BufferedImage frame) {

		double red = 0;
		double green = 0;
		double blue = 0;
		int points = 0;

		for (Point p : c) {
			ColorPoint stdDev = calculateStandardDeviation(c.getType(), p, frame);
			red += stdDev.red;
			green += stdDev.green;
			blue += stdDev.blue;

			points++;
		}

		return ColorPoint.build(c.getType(), (int) red / points, (int) green / points, (int) blue / points);
	}

	public static double backgroundDiff(final ColorPoint color, final Point p, final BufferedImage frame, final ColorPoint stdDev, final ColorPoint ... referenceColors) {
		if (color.red == 0 && color.green == 0 && color.blue == 0) {
			return 0;
		} else {
			return colorDiff(color, p, frame, stdDev, referenceColors);
		}

	}

	public static double colorDiff(final ColorPoint color, final Point p, final BufferedImage frame, final ColorPoint stdDev, final ColorPoint ... referenceColors) {

		double result = 255;
		int extra = 0;

		for (final ColorPoint referenceColor : referenceColors) {
			result = Math.min(result, color.diff(referenceColor));
		}

		ColorPoint pointStdDev = calculateStandardDeviation(stdDev.getType(), p, frame);

		// Desvio standard
		extra++;
		result += pointStdDev.diff(stdDev);

		return result / ((referenceColors.length + extra) * MAX_PIXEL_VALUE);
	}

	public static ColorPoint calculateBackgroundStandardDeviation(final BufferedImage frame, final Contour r, final Contour[] contours) {


		double red = 0;
		double green = 0;
		double blue = 0;
		long sum = 0;

		final int width = frame.getWidth();
		final int height = frame.getHeight();
		final int black = Color.black.getRGB();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				boolean skip = false;
				final Point p = new Point(x, y);

				// Avoid border points
				for (Point n : neighbors8(p, width, height)) {
					if (black == frame.getRGB(n.x, n.y)) {
						skip = true;
						break;
					}
				}

				// Skip anything inside a contour
				for (Contour c : contours) {
					if (c.contains(x, y)) {
						skip = true;
						break;
					}
				}

				if (!skip) {
					ColorPoint c = calculateStandardDeviation(r.getType(), p, frame);

					red += c.red;
					green += c.green;
					blue += c.blue;
					sum++;
				}
			}
		}
		return ColorPoint.build(r.getType(),
				(int) (red / sum),
				(int) (green / sum),
				(int) (blue / sum));
	}


	public static ColorPoint getAverageBackgroundColor(final BufferedImage frame, final Contour r, final Contour[] contours) {
		double red = 0;
		double green = 0;
		double blue = 0;
		long sum = 0;

		final int width = frame.getWidth();
		final int height = frame.getHeight();
		final int black = Color.black.getRGB();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				boolean skip = false;

				// Avoid border points
				if (black == frame.getRGB(x, y)) {
					continue;
				}

				// Skip anything inside a contour
				for (Contour c : contours) {
					if (c.contains(x, y)) {
						skip = true;
						break;
					}
				}

				if (!skip) {
					final ColorPoint c = ColorPoint.buildFromRGB(r.getType(), frame.getRGB(x, y));
					red += c.red;
					green += c.green;
					blue += c.blue;
					sum += 1;
				}
			}
		}
		return ColorPoint.build(r.getType(),
				(int) (red / sum),
				(int) (green / sum),
				(int) (blue / sum));
	}

	static public ColorPoint getAverageColor(final BufferedImage frame, final Contour r) {
		double red = 0;
		double green = 0;
		double blue = 0;
		int points = 0;
		for (final Point p : r) {
			final ColorPoint c = ColorPoint.buildFromRGB(r.getType(), frame.getRGB(p.x, p.y));
			red += c.red;
			green += c.green;
			blue += c.blue;
			points++;
		}
		final double avgRed = red / points;
		final double avgGreen = green / points;
		final double avgBlue = blue / points;
		return ColorPoint.build(r.getType(), (int) avgRed, (int) avgGreen, (int) avgBlue);
	}

	private static ColorPoint[] getMostFrequentColors(final BufferedImage frame, final Contour r) {

		double minHue = Double.MAX_VALUE;
		double maxHue = Double.MIN_VALUE;
		double minSaturation = Double.MAX_VALUE;
		double maxSaturation = Double.MIN_VALUE;
		double intensity = 0;
		int points = 0;

		int histogram[][] = new int[HISTOGRAM_SIZE][HISTOGRAM_SIZE];
		for (final Point p : r) {
			final ColorPoint c = ColorPoint.buildFromRGB(Type.HSI, frame.getRGB(p.x, p.y));
			minHue = Math.min(minHue, c.red);
			maxHue = Math.max(maxHue, c.red);
			minSaturation = Math.min(minSaturation, c.green);
			maxSaturation = Math.max(maxSaturation, c.green);
			intensity += c.blue;
			points++;
		}
		for (final Point p : r) {
			final ColorPoint c = ColorPoint.buildFromRGB(Type.HSI, frame.getRGB(p.x, p.y));
			int hue = (int) (HISTOGRAM_SIZE * (c.red - minHue) / (maxHue - minHue + 1));
			int saturation = (int) (HISTOGRAM_SIZE * (c.green - minSaturation) / (maxSaturation - minSaturation + 1));
			histogram[hue][saturation]++;
		}

		int bestHue = 0;
		int bestSat = 0;
		for (int i = 0; i < HISTOGRAM_SIZE; i++) {
			for (int j = 0; j < HISTOGRAM_SIZE; j++) {
				if (histogram[i][j] > histogram[bestHue][bestSat]) {
					bestHue = i;
					bestSat = j;
				}
			}
		}

		ColorPoint[] result = new ColorPoint[2];
		result[0] = ColorPoint.buildFromHSI(Type.RGB,
				(int) (bestHue * (maxHue - minHue) / HISTOGRAM_SIZE + minHue),
				(int) (bestSat * (maxSaturation - minSaturation) / HISTOGRAM_SIZE + minSaturation),
				(int) (intensity/points));

		// BFS to delete neighbors too high
		Deque<Point> queue = new LinkedList<>();
		queue.add(new Point(bestHue, bestSat));
		int bestValue = histogram[bestHue][bestSat];
		while (!queue.isEmpty()) {
			Point p = queue.poll();
			histogram[p.x][p.y] = 0;
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					if (p.x + dx >= 0 && p.x + dx < HISTOGRAM_SIZE && p.y + dy >= 0 && p.y + dy < HISTOGRAM_SIZE) {
						if (histogram[p.x + dx][p.y + dy] >= FRACTION_HISTOGRAM * bestValue) {
							queue.push(new Point(p.x + dx, p.y + dy));
						}
					}
				}
			}
		}

		int secondBestHue = 0;
		int secondBestSat = 0;
		for (int i = 0; i < HISTOGRAM_SIZE; i++) {
			for (int j = 0; j < HISTOGRAM_SIZE; j++) {
				if (histogram[i][j] > histogram[secondBestHue][secondBestSat]) {
					secondBestHue = i;
					secondBestSat = j;
				}
			}
		}

		if (histogram[secondBestHue][secondBestSat] == 0) {
			return new ColorPoint[] { result[0] };
		}
		result[1] = ColorPoint.buildFromHSI(Type.RGB,
				(int) (secondBestHue * (maxHue - minHue) / HISTOGRAM_SIZE + minHue),
				(int) (secondBestSat * (maxSaturation - minSaturation) / HISTOGRAM_SIZE + minSaturation),
				(int) (intensity/points));

		System.out.println("Selected points: " + result[0].toString() + ", " + result[1].toString());
		return result;
	}
}

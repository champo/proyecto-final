package ar.edu.it.itba.processing;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ar.edu.it.itba.processing.color.ColorPoint;
import ar.edu.it.itba.processing.color.ColorPoint.Type;

public final class Helpers {

	private static final double MAX_PIXEL_VALUE = 256 * 3;

	// Define the radiud to find the colors for the objects to track
	public final static int RADIUS_X = 20;
	public final static int RADIUS_Y = 30;
	public final static int BUCKETS = 32;
	public final static double PONDER[] = new double[]{ 1, 0, 0 };


	private Helpers() {
	}

	public static ColorPoint[] getCharacteristics(final BufferedImage frame, final Contour contour) {
		final List<ColorPoint> colors = new ArrayList<ColorPoint>();
		colors.add(getAverageColor(frame, contour));
		//colors.addAll(Arrays.asList(mostFrequentColors(frame, contour)));
		return colors.toArray(new ColorPoint[0]);
	}

	public static ColorPoint[] getBackgroundCharacteristics(final BufferedImage frame,
			final Contour contour) {
		final List<ColorPoint> colors = new ArrayList<ColorPoint>();
		colors.add(getAverageBackgroundColor(frame, contour));
		//colors.addAll(Arrays.asList(getMostFrequentBackgroundColors(frame, contour)));
		return colors.toArray(new ColorPoint[0]);
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
		return Math.log((1 - diffObject(frame, c, p, color)) -  Math.log(1 - diffBackground(color, p, frame, c.bgDeviation, c.omegaZero)));
	}

	public static double diffObject(final BufferedImage frame, final Contour c, final Point p, final ColorPoint color) {
		return diffBackground(color, p, frame, c.getLastStdDev(), c.omega);
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

		return ColorPoint.build(type, (int) redDeviation, (int) blueDeviation, (int) greenDeviation);
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

		return ColorPoint.build(c.getType(), (int) red / points, (int) blue / points, (int) green / points);
	}

	public static double diffBackground(final ColorPoint color, final Point p, final BufferedImage frame, final ColorPoint stdDev, final ColorPoint ... referenceColors) {
		double result = 0;
		int i = 0;

		for (final ColorPoint referenceColor : referenceColors) {

			result += color.diff(referenceColor) * PONDER[i];
			i++;
		}

		result += calculateStandardDeviation(stdDev.getType(), p, frame).diff(stdDev);

		return result / ((referenceColors.length + 1) * MAX_PIXEL_VALUE);
	}

	public static ColorPoint calculateBackgroundStandardDeviation(final Contour r, final BufferedImage frame) {
		int med_x = 0;
		int med_y = 0;
		for (final Point p : r.getLout()) {
			med_x += p.x;
			med_y += p.y;
		}
		med_x /= r.getLout().size();
		med_y /= r.getLout().size();
		double red = 0;
		double green = 0;
		double blue = 0;
		int sum = 0;

		final int maxX = Math.min(frame.getWidth() -1, med_x + RADIUS_X);
		final int maxY = Math.min(frame.getHeight() - 1, med_y + RADIUS_Y);

		for (int x = Math.max(0, med_x - RADIUS_X); x <= maxX; x++) {
			for (int y = Math.max(0, med_y - RADIUS_Y); y <= maxY; y++) {
				final Point p = new Point(x, y);
				if (!r.contains(x, y)) {
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


	public static ColorPoint getAverageBackgroundColor(final BufferedImage frame, final Contour r) {
		int med_x = 0;
		int med_y = 0;
		for (final Point p : r.getLout()) {
			med_x += p.x;
			med_y += p.y;
		}
		med_x /= r.getLout().size();
		med_y /= r.getLout().size();
		double red = 0;
		double green = 0;
		double blue = 0;
		double sum = 0;

		final int maxX = Math.min(frame.getWidth() -1, med_x + RADIUS_X);
		final int maxY = Math.min(frame.getHeight() - 1, med_y + RADIUS_Y);

		for (int x = Math.max(0, med_x - RADIUS_X); x <= maxX; x++) {
			for (int y = Math.max(0, med_y - RADIUS_Y); y <= maxY; y++) {
				final Point p = new Point(x, y);
				if (!r.contains(x, y)) {
					final ColorPoint c = ColorPoint.buildFromRGB(r.getType(), frame.getRGB(p.x, p.y));
					final double distFactor = Math.abs(med_x - x)/RADIUS_X + Math.abs(med_y - y)*RADIUS_Y;
					red += c.red * distFactor;
					green += c.green * distFactor;
					blue += c.blue * distFactor;
					sum += distFactor;
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

/*
	static ColorPoint[] mostFrequentColors(final BufferedImage frame, final Contour r) {
		final double red[] = new double[BUCKETS];
		final double green[] = new double[BUCKETS];
		final double blue[] = new double[BUCKETS];
		final Set<Point> visited = new HashSet<Point>();

		for (final Point p : r.getLout()) {
			visited.add(p);
		}
		final Deque<Point> queue = new LinkedList<Point>();
		for (final Point p : r.getLin()) {
			visited.add(p);
			queue.push(p);
		}
		int iterations = 0;
		while (!queue.isEmpty() && iterations < MAX_ITERATIONS) {
			iterations++;
			final Point p = queue.pop();
			final ColorPoint c = ColorPoint.buildFromRGB(r.getType(), frame.getRGB(p.x, p.y));
			red[c.red / BUCKETS] += 1;
			green[c.green / BUCKETS] += 1;
			blue[c.blue / BUCKETS] += 1;
			for (final Point n : neighbors(p, Integer.MAX_VALUE, Integer.MAX_VALUE)) {
				if (!visited.contains(n)) {
					visited.add(n);
					queue.push(n);
				}
			}
		}
		return extractThreeMostCommon(red, green, blue);
	}

	public static ColorPoint[] extractThreeMostCommon(final double[] red, final double[] green,
			final double[] blue) {
		int avgRed = 0;
		int avgGreen = 0;
		int avgBlue = 0;
		for (int i = 0; i < BUCKETS; i++) {
			avgRed = red[avgRed] > red[i] ? avgRed : i;
			avgGreen = green[avgGreen] > green[i] ? avgGreen : i;
			avgBlue = blue[avgBlue] > blue[i] ? avgBlue : i;
		}
		final ColorPoint results[] = new ColorPoint[3];
		results[0] = new ColorPoint((int) ((avgRed + 0.5) * BUCKETS),
				(int) ((avgGreen + 0.5) * BUCKETS),
				(int) ((avgBlue + 0.5) * BUCKETS));
		final double FRACTION_OF_MOST_COMMON = 0.5;
		for (int i = 0; i < BUCKETS; i++) {
			if (red[i] >= FRACTION_OF_MOST_COMMON * red[avgRed]) {
				red[i] = 0;
			}
			if (green[i] >= FRACTION_OF_MOST_COMMON * green[avgGreen]) {
				green[i] = 0;
			}
			if (blue[i] >= FRACTION_OF_MOST_COMMON * blue[avgBlue]) {
				blue[i] = 0;
			}
		}
		avgRed = 0;
		avgGreen = 0;
		avgBlue = 0;
		boolean changed = false;
		for (int i = 0; i < BUCKETS; i++) {
			if (red[avgRed] < red[i]) {
				changed = true;
				avgRed = i;
			}
			if (green[avgGreen] < green[i]) {
				changed = true;
				avgGreen = i;
			}
			if (blue[avgBlue] < blue[i]) {
				changed = true;
				avgBlue = i;
			}
		}
		if (true || !changed) {
			return new ColorPoint[] { results[0] };
		}
		results[1] = new ColorPoint((int) ((avgRed + 0.5) * BUCKETS),
				(int) ((avgGreen + 0.5) * BUCKETS),
				(int) ((avgBlue + 0.5) * BUCKETS));
		red[avgRed] = 0;
		green[avgGreen] = 0;
		blue[avgBlue] = 0;
		avgRed = 0;
		avgGreen = 0;
		avgBlue = 0;
		changed = false;
		for (int i = 0; i < BUCKETS; i++) {
			if (red[avgRed] < red[i]) {
				changed = true;
				avgRed = i;
			}
			if (green[avgGreen] < green[i]) {
				changed = true;
				avgGreen = i;
			}
			if (blue[avgBlue] < blue[i]) {
				changed = true;
				avgBlue = i;
			}
		}
		if (!changed) {
			return new ColorPoint[] { results[0], results[1] };
		}
		results[2] = new ColorPoint((int) ((avgRed + 0.5) * BUCKETS),
				(int) ((avgGreen + 0.5) * BUCKETS),
				(int) ((avgBlue + 0.5) * BUCKETS));


		return results;
	}

	public ColorPoint[] getMostFrequentBackgroundColors(final BufferedImage frame, final Contour r) {
		final int maxX = Math.min(frame.getWidth(), r.maxX() + 15);
		final int maxY = Math.min(frame.getHeight(), r.maxY() + 15);

		final double red[] = new double[BUCKETS];
		final double green[] = new double[BUCKETS];
		final double blue[] = new double[BUCKETS];
		for (int i = Math.max(0, r.minX() - 15); i < maxX; i++) {
			for (int j = Math.max(0, r.minY() - 15); j < maxY; j++) {

				if (!r.contains(i, j)) {
					final ColorPoint c = ColorPoint.buildFromRGB(r.getType(), frame.getRGB(i, j));
					red[c.red / BUCKETS] += 1;
					green[c.green / BUCKETS] += 1;
					blue[c.blue / BUCKETS] += 1;
				}
			}
		}
		return extractThreeMostCommon(red, green, blue);
	}
*/
}

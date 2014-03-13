package ar.edu.it.itba.processing;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ar.edu.it.itba.processing.Contour.State;
import ar.edu.it.itba.processing.PointMapping.Provider;

public class ActiveContour {

	private static final double MAX_PIXEL_VALUE = Math.pow(256, 3);

	// Define the radiud to find the colors for the objects to track
	public final static int RADIUS_X = 20;
	public final static int RADIUS_Y = 30;
	public final static int BUCKETS = 32;
	public final static double PONDER[] = new double[]{ 0.9, 0, 0 };

	private static final int MASK_RADIUS = 3;
	protected static final int MAX_ITERATIONS = 400*400;

	private static double SIGMA = 0.7;
	private static double[][] mask;
	static {
		final int sideLength = 2  * MASK_RADIUS + 1;
		mask = new double[sideLength][sideLength];
		final double sigmaSquared = Math.pow(SIGMA, 2);

		for (int i = -MASK_RADIUS; i < MASK_RADIUS + 1; i++) {
			for (int j = -MASK_RADIUS; j < MASK_RADIUS + 1; j++) {
				mask[i + MASK_RADIUS][j + MASK_RADIUS] = Math.pow(Math.E, - (double)(i * i + j * j) / (2 * sigmaSquared)) / (2.0 * Math.PI * sigmaSquared);
			}
		}
	}

	private final Contour[] contours;

	private final RGBPoint[][] omega;
	private final RGBPoint[][] omegaZero;
	private final RGBPoint[] backgroundDeviations;
	private final int[][] phi;

	private final PointMapping theta;

	public ActiveContour(final BufferedImage frame, final Contour... c) {
		contours = c;
		// Calculating theta makes contours defien their internal points
		// it *must* happen before anything else
		phi = new int[frame.getWidth()][frame.getHeight()];
		theta = getTheta(c);

		omega = new RGBPoint[c.length][];
		omegaZero = new RGBPoint[c.length][];
		backgroundDeviations = new RGBPoint[c.length];

		for (int i = 0; i < c.length; i++) {
			Contour contour = contours[i];

			omega[i] = getCharacteristics(frame, contour);
			omegaZero[i] = getBackgroundCharacteristics(frame, contour);
			backgroundDeviations[i] = calculateBackgroundStandardDeviation(contour, frame);
			contour.setLastStdDev(calculateStandardDeviation(contour, frame));
		}

	}

	private RGBPoint[] getCharacteristics(final BufferedImage frame, final Contour contour) {
		final List<RGBPoint> colors = new ArrayList<RGBPoint>();
		colors.add(getAverageColor(frame, contour));
		//colors.addAll(Arrays.asList(mostFrequentColors(frame, contour)));
		return arrayResult(colors);
	}


	private RGBPoint[] arrayResult(final List<RGBPoint> colors) {
		final RGBPoint[] result = new RGBPoint[colors.size()];
		int i = 0;
		for (final RGBPoint color : colors) {
			result[i++] = color;
		}
		return result;
	}

	private RGBPoint[] getBackgroundCharacteristics(final BufferedImage frame,
			final Contour contour) {
		final List<RGBPoint> colors = new ArrayList<RGBPoint>();
		colors.add(getAverageBackgroundColor(frame, contour));
		//colors.addAll(Arrays.asList(getMostFrequentBackgroundColors(frame, contour)));
		return arrayResult(colors);
	}

	public float adapt(final BufferedImage frame) {

		final long time = System.currentTimeMillis();
		final int nMax = max(frame.getHeight(), frame.getWidth());

		final boolean[] done = new boolean[contours.length];
		int completed = 0;

		for (int i = 0; i < nMax; i++) {

			for (int j = 0; j < contours.length; j++) {

				if (done[j]) {
					continue;
				}

				final Contour c = contours[j];
				final PointMapping F_d = getF(frame, c, omegaZero[j], omega[j], backgroundDeviations[j]);

				if (!applyForce(c, F_d, theta, frame)) {
					done[j] = true;
					completed++;
				}
			}

			if (completed == contours.length) {
				break;
			}
		}

		final int rounds = 2 * MASK_RADIUS + 1;
		for (int i = 0; i < rounds; i++) {

			for (final Contour c : contours) {

				if (c.getState() == State.MISSING) {
					continue;
				}

				final PointMapping F_s = new PointMapping(new Provider() {

					@Override
					public double valueForPoint(final Point p) {
						return 0;
					}
				});
				calculateGauss(c.getLin(), theta, F_s);
				calculateGauss(c.getLout(), theta, F_s);

				applyForce(c, F_s, theta, frame);
			}
		}


		for (int i = 0; i < contours.length; i++) {
			Contour c = contours[i];
			c.mutationFinished();

			if (c.getState() == State.MISSING) {
				markExpandedArea(frame, c);
			}
		}


		final long diff = System.currentTimeMillis() - time;
		System.out.println("Time difference: " + diff + " ms");

		return diff;
	}

	private void markExpandedArea(final BufferedImage frame, final Contour c) {

		int searchRadius = Math.max(15 + c.cyclesLost(), 30);
		Point center = c.getLastCentroid();

		clearContour(c);

		System.out.println("Search radius = " + searchRadius);
		System.out.println("Center = " + center);

		int maxX = Math.min(frame.getWidth() - 1, center.x + searchRadius);
		int maxY = Math.min(frame.getHeight() - 1, center.y + searchRadius);

		Set<Point> points = new HashSet<Point>();

		int count = 0;

		for (int x = Math.max(0, center.x - searchRadius); x < maxX; x++) {
			for (int y = Math.max(0, center.y - searchRadius); y < maxY; y++) {

				if (phi[x][y] == 0) {
					phi[x][y] = c.color;
					count++;

					Point p = new Point(x, y);
					points.add(p);
					theta.set(p, -3);
				}

			}
		}
		System.out.println("Got count = " + count);

		Set<Point> lout = c.getLout();
		Set<Point> lin = c.getLin();

		for (Point p : points) {
			List<Point> neighbors = neighbors(p, frame.getWidth(), frame.getHeight());
			int connected = 0;
			for (Point point : neighbors) {
				if (phi[point.x][point.y] == c.color) {
					connected++;
				}
			}

			if (connected < neighbors.size()) {
				lout.add(p);
				theta.set(p, 1);
			} else {
				c.addPoint(p);
			}
		}

		for (Point p : points) {
			List<Point> neighbors = neighbors(p, frame.getWidth(), frame.getHeight());
			int connected = 0;
			for (Point point : neighbors) {
				if (theta.getValue(point) == -3) {
					connected++;
				}
			}

			if (connected < neighbors.size()) {
				lin.add(p);
			}
		}

		for (Point point : lin) {
			theta.set(point, -1);
		}

	}

	private void clearContour(final Contour c) {

		Iterator<Point> iterator = c.iterator();
		while (iterator.hasNext()) {
			Point p = iterator.next();

			phi[p.x][p.y] = 0;
			theta.set(p, 3);

			iterator.remove();
		}

		for (Point p : c.getLout()) {
			phi[p.x][p.y] = 0;
			theta.set(p, 3);
		}

		c.getLout().clear();
		c.getLin().clear();
	}

	private void calculateGauss(final Set<Point> points, final PointMapping theta,
			final PointMapping f_s) {

		for (final Point point : points) {

			double accum = 0;

			for (int mask_x = 0; mask_x < mask[0].length; mask_x++) {
				for (int mask_y = 0; mask_y < mask.length; mask_y++) {

					final int delta_x = point.x + mask_x - MASK_RADIUS;
					final int delta_y = point.y + mask_y - MASK_RADIUS;

					accum += mask[mask_y][mask_x] * theta.getValue(delta_x, delta_y);
				}
			}

			f_s.set(point, -Math.signum(accum));

		}

	}

	private boolean applyForce(final Contour r, final PointMapping force, final PointMapping theta, final BufferedImage frame) {
		final Set<Point> lout = r.getLout();
		final Set<Point> lin = r.getLin();
		boolean changed = false;

		for (final Point p : new ArrayList<Point>(lout)) {

			if (force.getValue(p) > 0 && !isBorder(frame, p) && isTopologicallySafe(p, frame)) {
				lout.remove(p);
				lin.add(p);
				r.addPoint(p);
				theta.set(p, -1);
				for (final Point n : neighbors(p, frame.getWidth(), frame.getHeight())) {
					if (theta.getValue(n) == 3 && phi[n.x][n.y] == 0) {
						lout.add(n);
						phi[n.x][n.y] = r.color;
						theta.set(n, 1);
					}
				}
				changed = true;
			}
		}

		for (final Point l : new ArrayList<Point>(lin)) {
			int areInner = 0;
			final List<Point> neighbors = neighbors(l, frame.getWidth(), frame.getHeight());
			for (final Point n : neighbors) {
				if (theta.getValue(n) < 0) {
					areInner++;
				}
			}
			if (areInner == neighbors.size()) {
				lin.remove(l);
				theta.set(l, -3);

				changed = true;
			}
		}

		for (final Point p : new ArrayList<Point>(lin)) {
			if (force.getValue(p) < 0) {
				lin.remove(p);
				r.removePoint(p);
				lout.add(p);
				theta.set(p, 1);
				for (final Point n : neighbors(p, frame.getWidth(), frame.getHeight())) {
					if (theta.getValue(n) == -3) {
						lin.add(n);
						r.addPoint(n);
						theta.set(n, -1);
					}
				}
				changed = true;
			}
		}

		for (final Point l : new ArrayList<Point>(lout)) {
			int neighbors = 0;
			for (final Point n : neighbors(l, frame.getWidth(), frame.getHeight())) {
				if (theta.getValue(n) > 0) {
					neighbors++;
				}
			}
			if (neighbors == 4) {
				lout.remove(l);
				phi[l.x][l.y] = 0;
				theta.set(l, 3);
				changed = true;
			}
		}
		return changed;
	}

	private boolean isTopologicallySafe(final Point p, final BufferedImage frame) {

		int alpha = 0;
		boolean[] seen = new boolean[contours.length];
		for (Point n : neighbors8(p, frame.getWidth(), frame.getHeight())) {
			int object = phi[n.x][n.y];
			if (object != 0 && !seen[object - 1]) {
				seen[object - 1] = true;
				alpha++;
			}
		}

		if (alpha <= 1) {
			return true;
		}

		return false;
	}

	private static boolean isBorder(final BufferedImage frame, final Point p) {
		return p.x == 0 || p.y == 0 || p.x == frame.getWidth() - 1 || p.y == frame.getHeight() - 1;
	}

	private static List<Point> neighbors(final Point p, final int width, final int height) {
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

	private static List<Point> neighbors8(final Point p, final int width, final int height) {
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

	private PointMapping getTheta(final Contour... contours) {

		final PointMapping theta = new PointMapping(new Provider() {

			@Override
			public double valueForPoint(final Point p) {
				return 3;
			}

		});

		for (final Contour contour : contours) {
			markInternalPoints(theta, contour);
		}

		return theta;
	}

	private void markInternalPoints(final PointMapping theta, final Contour r) {

		final Set<Point> internalPoints = new HashSet<Point>();
		final Set<Point> externalPoints = new HashSet<Point>();

		for (final Point p : r.getLout()) {
			externalPoints.add(p);
            phi[p.x][p.y] = r.color;
			theta.set(p, 1);
		}
		final Deque<Point> queue = new LinkedList<Point>();
		for (final Point p : r.getLin()) {
			internalPoints.add(p);
			theta.set(p, -1);
            phi[p.x][p.y] = r.color;
			queue.push(p);
		}
		int iterations = 0;
		while (!queue.isEmpty() && iterations < MAX_ITERATIONS) {
			iterations++;
			final Point p = queue.pop();
			for (final Point n : neighbors8(p, Integer.MAX_VALUE, Integer.MAX_VALUE)) {
				if (!internalPoints.contains(n) && !externalPoints.contains(n)) {
					internalPoints.add(n);
					queue.push(n);
					theta.set(n, -3);
					phi[n.x][n.y] = r.color;
				}
			}
		}

		r.setInternalPoints(internalPoints);

		if (iterations == MAX_ITERATIONS) {
			throw new RuntimeException("The contour is not conected");
		}
	}

	static PointMapping getF(final BufferedImage frame, final Contour c, final RGBPoint omegaZero[],
			final RGBPoint[] omega, final RGBPoint stdDev) {
		return new PointMapping(new Provider() {

			@Override
			public double valueForPoint(final Point p) {
				return prob(frame, c, p, omegaZero, omega, stdDev);
			}
		});
	}

	private static Double prob(final BufferedImage frame, final Contour c, final Point p, final RGBPoint omegaZero[], final RGBPoint omega[], final RGBPoint stdDev) {
		RGBPoint color = new RGBPoint(frame.getRGB(p.x, p.y));
		return Math.log((1 - diffObject(frame, c, p, color, omega)) / (1 - diffBackground(color, p, frame, stdDev, omegaZero)));
	}

	private static double diffObject(final BufferedImage frame, final Contour c, final Point p, final RGBPoint color, final RGBPoint ... referenceColors) {
		return diffBackground(color, p, frame, c.getLastStdDev(), referenceColors);
	}

	private static RGBPoint calculateStandardDeviation(final Point center, final BufferedImage frame) {
		double red = 0;
		double green = 0;
		double blue = 0;
		int points = 0;

		List<Point> pixels = neighbors8(center, frame.getWidth(), frame.getHeight());
		pixels.add(center);

		for (final Point p : pixels) {
			final RGBPoint v = new RGBPoint(frame.getRGB(p.x, p.y));
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
			final RGBPoint v = new RGBPoint(frame.getRGB(p.x, p.y));
			redDeviation += Math.pow(v.red - avgRed, 2);
			greenDeviation += Math.pow(v.green - avgGreen, 2);
			blueDeviation += Math.pow(v.blue - avgBlue, 2);
		}

		redDeviation = Math.sqrt(redDeviation / (points - 1));
		blueDeviation = Math.sqrt(blueDeviation / (points - 1));
		greenDeviation = Math.sqrt(greenDeviation / (points - 1));

		return new RGBPoint((int) redDeviation, (int) blueDeviation, (int) greenDeviation);
	}

	private static RGBPoint calculateStandardDeviation(final Contour c, final BufferedImage frame) {

		double red = 0;
		double green = 0;
		double blue = 0;
		int points = 0;

		for (Point p : c) {
			RGBPoint stdDev = calculateStandardDeviation(p, frame);
			red += stdDev.red;
			green += stdDev.green;
			blue += stdDev.blue;

			points++;
		}

		return new RGBPoint((int) red / points, (int) blue / points, (int) green / points);
	}

	private static double diffBackground(final RGBPoint color, final Point p, final BufferedImage frame, final RGBPoint stdDev, final RGBPoint ... referenceColors) {
		double result = 0;
		int i = 0;

		for (final RGBPoint referenceColor : referenceColors) {

			result += color.diff(referenceColor) * PONDER[i];
			i++;
		}

		result += 0.1 * calculateStandardDeviation(p, frame).diff(stdDev);

		return result / ((referenceColors.length + 1) * MAX_PIXEL_VALUE);
	}

	private RGBPoint calculateBackgroundStandardDeviation(final Contour r, final BufferedImage frame) {
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
					RGBPoint c = calculateStandardDeviation(p, frame);

					red += c.red;
					green += c.green;
					blue += c.blue;
					sum++;
				}
			}
		}
		return new RGBPoint((int) (red / sum),
				(int) (green / sum),
				(int) (blue / sum));
	}


	private RGBPoint getAverageBackgroundColor(final BufferedImage frame, final Contour r) {
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
					final RGBPoint c = new RGBPoint(frame.getRGB(p.x, p.y));
					final double distFactor = Math.abs(med_x - x)/RADIUS_X + Math.abs(med_y - y)*RADIUS_Y;
					red += c.red * distFactor;
					green += c.green * distFactor;
					blue += c.blue * distFactor;
					sum += distFactor;
				}
			}
		}
		return new RGBPoint((int) (red / sum),
				(int) (green / sum),
				(int) (blue / sum));
	}

	static private RGBPoint getAverageColor(final BufferedImage frame, final Contour r) {
		double red = 0;
		double green = 0;
		double blue = 0;
		int points = 0;
		for (final Point p : r) {
			final RGBPoint c = new RGBPoint(frame.getRGB(p.x, p.y));
			red += c.red;
			green += c.green;
			blue += c.blue;
			points++;
		}
		final double avgRed = red / points;
		final double avgGreen = green / points;
		final double avgBlue = blue / points;
		return new RGBPoint((int) avgRed, (int) avgGreen, (int) avgBlue);
	}

	static RGBPoint[] mostFrequentColors(final BufferedImage frame, final Contour r) {
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
			final RGBPoint c = new RGBPoint(frame.getRGB(p.x, p.y));
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

	private static RGBPoint[] extractThreeMostCommon(final double[] red, final double[] green,
			final double[] blue) {
		int avgRed = 0;
		int avgGreen = 0;
		int avgBlue = 0;
		for (int i = 0; i < BUCKETS; i++) {
			avgRed = red[avgRed] > red[i] ? avgRed : i;
			avgGreen = green[avgGreen] > green[i] ? avgGreen : i;
			avgBlue = blue[avgBlue] > blue[i] ? avgBlue : i;
		}
		final RGBPoint results[] = new RGBPoint[3];
		results[0] = new RGBPoint((int) ((avgRed + 0.5) * BUCKETS),
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
			return new RGBPoint[] { results[0] };
		}
		results[1] = new RGBPoint((int) ((avgRed + 0.5) * BUCKETS),
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
			return new RGBPoint[] { results[0], results[1] };
		}
		results[2] = new RGBPoint((int) ((avgRed + 0.5) * BUCKETS),
				(int) ((avgGreen + 0.5) * BUCKETS),
				(int) ((avgBlue + 0.5) * BUCKETS));


		return results;
	}

	private RGBPoint[] getMostFrequentBackgroundColors(final BufferedImage frame, final Contour r) {
		final int maxX = Math.min(frame.getWidth(), r.maxX() + 15);
		final int maxY = Math.min(frame.getHeight(), r.maxY() + 15);

		final double red[] = new double[BUCKETS];
		final double green[] = new double[BUCKETS];
		final double blue[] = new double[BUCKETS];
		for (int i = Math.max(0, r.minX() - 15); i < maxX; i++) {
			for (int j = Math.max(0, r.minY() - 15); j < maxY; j++) {

				if (!r.contains(i, j)) {
					final RGBPoint c = new RGBPoint(frame.getRGB(i, j));
					red[c.red / BUCKETS] += 1;
					green[c.green / BUCKETS] += 1;
					blue[c.blue / BUCKETS] += 1;
				}
			}
		}
		return extractThreeMostCommon(red, green, blue);
	}

	static boolean endCondition(final PointMapping F_d, final BufferedImage coloredFrame, final Contour r) {

		// WARNING! FALTA CONSIDERAR PHI ACA
		for (final Point p : r.getLout()) {
			if (F_d.getValue(p) >= 0 && !isBorder(coloredFrame, p)) {
				return false;
			}
		}
		for (final Point p : r.getLin()) {
			if (F_d.getValue(p) <= 0) {
				return false;
			}
		}
		return true;
	}

	private static int max(final int a, final int b) {
		return a > b ? a : b;
	}

    public int[][] getMapping() {
        return phi;
    }

}

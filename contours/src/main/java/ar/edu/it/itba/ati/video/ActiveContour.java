package ar.edu.it.itba.ati.video;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ar.edu.it.itba.ati.video.Contour.State;
import ar.edu.it.itba.ati.video.PointMapping.Provider;

public class ActiveContour {

	// Define the radiud to find the colors for the objects to track
	public final static int RADIUS_X = 20;
	public final static int RADIUS_Y = 30;
	public final static int BUCKETS = 32;
	public int searchSize = 15;
	public final static double PONDER[] = new double[]{ 1, 0.25, 0.25 };

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

	private final Pixel[][] omega;
	private final Pixel[][] omegaZero;
	private final int[][] phi;

	private final PointMapping theta;

	public ActiveContour(final BufferedImage frame, final Contour... c) {
		contours = c;
		// Calculating theta makes contours defien their internal points
		// it *must* happen before anything else
		phi = new int[frame.getWidth()][frame.getHeight()];
		theta = getTheta(c);

		omega = new Pixel[c.length][];
		omegaZero = new Pixel[c.length][];

		for (int i = 0; i < c.length; i++) {
			omega[i] = getCharacteristics(frame, contours[i]);
			omegaZero[i] = getBackgroundCharacteristics(frame, contours[i]);
		}

	}

	private Pixel[] getCharacteristics(final BufferedImage frame, final Contour contour) {
		final List<Pixel> colors = new ArrayList<Pixel>();
		colors.add(getAverageColor(frame, contour));
		//colors.addAll(Arrays.asList(mostFrequentColors(frame, contour)));
		return arrayResult(colors);
	}


	private Pixel[] arrayResult(final List<Pixel> colors) {
		final Pixel[] result = new Pixel[colors.size()];
		int i = 0;
		for (final Pixel color : colors) {
			result[i++] = color;
		}
		return result;
	}

	private Pixel[] getBackgroundCharacteristics(final BufferedImage frame,
			final Contour contour) {
		final List<Pixel> colors = new ArrayList<Pixel>();
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
				final PointMapping F_d = getF(frame, omegaZero[j], omega[j]);

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


		for (final Contour c : contours) {
			final Set<Point> internalPoints = c.getInternalPoints();
			final Set<Point> exLout = c.getLout();
			if (c.mutationFinished() && c.getState() == State.STABLE) {
				for (final Point i : exLout) {
					phi[i.x][i.y] = 0;
					theta.set(i, 3);
				}
				for (final Point i : internalPoints) {
					if (!c.getInternalPoints().contains(i)) {
						phi[i.x][i.y] = 0;
						theta.set(i, 3);
					}
				}
				for (final Point i : c.getInternalPoints()) {
					phi[i.x][i.y] = c.color;
					theta.set(i, -3);
				}
				for (final Point i : c.getLout()) {
					phi[i.x][i.y] = c.color;
					theta.set(i, 1);
				}
				for (final Point i : c.getLin()) {
					phi[i.x][i.y] = c.color;
					theta.set(i, -1);
				}
			}

			if (c.getState() == State.MISSING) {
				markExpandedArea(frame, c);
			}
		}


		final long diff = System.currentTimeMillis() - time;
		System.out.println("Time difference: " + diff + " ms");

		return diff;
	}

	private void markExpandedArea(final BufferedImage frame, final Contour c) {

		final int searchRadius = Math.max(searchSize + c.cyclesLost(), searchSize * 2);
		final Point center = c.getLastCentroid();

		clearContour(c);

		System.out.println("Search radius = " + searchRadius);
		System.out.println("Center = " + center);

		final int maxX = Math.min(frame.getWidth() - 1, center.x + searchRadius);
		final int maxY = Math.min(frame.getHeight() - 1, center.y + searchRadius);

		final Set<Point> points = new HashSet<Point>();

		int count = 0;

		for (int x = Math.max(0, center.x - searchRadius); x < maxX; x++) {
			for (int y = Math.max(0, center.y - searchRadius); y < maxY; y++) {

				if (phi[x][y] == 0) {
					phi[x][y] = c.color;
					count++;

					final Point p = new Point(x, y);
					points.add(p);
					theta.set(p, -3);
				}

			}
		}
		System.out.println("Got count = " + count);

		final Set<Point> lout = c.getLout();
		final Set<Point> lin = c.getLin();

		for (final Point p : points) {
			final List<Point> neighbors = neighbors(p, frame.getWidth(), frame.getHeight());
			int connected = 0;
			for (final Point point : neighbors) {
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

		for (final Point p : points) {
			final List<Point> neighbors = neighbors(p, frame.getWidth(), frame.getHeight());
			int connected = 0;
			for (final Point point : neighbors) {
				if (theta.getValue(point) == -3) {
					connected++;
				}
			}

			if (connected < neighbors.size()) {
				lin.add(p);
			}
		}

		for (final Point point : lin) {
			theta.set(point, -1);
		}

	}

	private void clearContour(final Contour c) {

		final Iterator<Point> iterator = c.iterator();
		while (iterator.hasNext()) {
			final Point p = iterator.next();

			phi[p.x][p.y] = 0;
			theta.set(p, 3);

			iterator.remove();
		}

		for (final Point p : c.getLout()) {
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
			final List<Point> around = neighbors(l, frame.getWidth(), frame.getHeight());
			for (final Point n : around) {
				if (theta.getValue(n) > 0) {
					neighbors++;
				}
			}
			if (neighbors == around.size()) {
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
		final boolean[] seen = new boolean[contours.length];
		for (final Point n : neighbors8(p, frame.getWidth(), frame.getHeight())) {
			final int object = phi[n.x][n.y];
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

			if (p.x < height - 1) {
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

			if (p.x < height - 1) {
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

	static PointMapping getF(final BufferedImage frame, final Pixel omegaZero[],
			final Pixel[] omega) {
		return new PointMapping(new Provider() {

			@Override
			public double valueForPoint(final Point p) {
				return prob(new Pixel(frame.getRGB(p.x, p.y)), omegaZero, omega);
			}
		});
	}

	private static Double prob(final Pixel color, final Pixel omegaZero[], final Pixel omega[]) {
		return Math.log((1 - diffColor(color, omega)) / (1 - diffColor(color, omegaZero)));
	}

	private static double diffColor(final Pixel color, final Pixel ... referenceColors) {
		double result = 0;

		int i = 0;


		for (final Pixel referenceColor : referenceColors) {

			result += color.diff(referenceColor) * PONDER[i];
			i++;

		}
		return result / Pixel.MAX_PIXEL_VALUE;
	}

	private Pixel getAverageBackgroundColor(final BufferedImage frame, final Contour r) {
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
					final Pixel c = new Pixel(frame.getRGB(p.x, p.y));
					final double distFactor = Math.abs(med_x - x)/RADIUS_X + Math.abs(med_y - y)*RADIUS_Y;
					red += c.red * distFactor;
					green += c.green * distFactor;
					blue += c.blue * distFactor;
					sum += distFactor;
				}
			}
		}
		return new Pixel((int) (red / sum),
				(int) (green / sum),
				(int) (blue / sum));
	}

	static private Pixel getAverageColor(final BufferedImage frame, final Contour r) {
		double red = 0;
		double green = 0;
		double blue = 0;
		int points = 0;
		for (final Point p : r.getLout()) {
			final Pixel c = new Pixel(frame.getRGB(p.x, p.y));
			red += c.red;
			green += c.green;
			blue += c.blue;
			points++;
		}
		final double avgRed = red / points;
		final double avgGreen = green / points;
		final double avgBlue = blue / points;
		return new Pixel((int) avgRed, (int) avgGreen, (int) avgBlue);
	}

	static Pixel[] mostFrequentColors(final BufferedImage frame, final Contour r) {
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
			final Pixel c = new Pixel(frame.getRGB(p.x, p.y));
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

	private static Pixel[] extractThreeMostCommon(final double[] red, final double[] green,
			final double[] blue) {
		int avgRed = 0;
		int avgGreen = 0;
		int avgBlue = 0;
		for (int i = 0; i < BUCKETS; i++) {
			avgRed = red[avgRed] > red[i] ? avgRed : i;
			avgGreen = green[avgGreen] > green[i] ? avgGreen : i;
			avgBlue = blue[avgBlue] > blue[i] ? avgBlue : i;
		}
		final Pixel results[] = new Pixel[3];
		results[0] = new Pixel((int) ((avgRed + 0.5) * BUCKETS),
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
			return new Pixel[] { results[0] };
		}
		results[1] = new Pixel((int) ((avgRed + 0.5) * BUCKETS),
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
			return new Pixel[] { results[0], results[1] };
		}
		results[2] = new Pixel((int) ((avgRed + 0.5) * BUCKETS),
				(int) ((avgGreen + 0.5) * BUCKETS),
				(int) ((avgBlue + 0.5) * BUCKETS));


		return results;
	}

	private Pixel[] getMostFrequentBackgroundColors(final BufferedImage frame, final Contour r) {
		final int maxX = Math.min(frame.getWidth(), r.maxX() + 15);
		final int maxY = Math.min(frame.getHeight(), r.maxY() + 15);

		final double red[] = new double[BUCKETS];
		final double green[] = new double[BUCKETS];
		final double blue[] = new double[BUCKETS];
		for (int i = Math.max(0, r.minX() - 15); i < maxX; i++) {
			for (int j = Math.max(0, r.minY() - 15); j < maxY; j++) {

				if (!r.contains(i, j)) {
					final Pixel c = new Pixel(frame.getRGB(i, j));
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

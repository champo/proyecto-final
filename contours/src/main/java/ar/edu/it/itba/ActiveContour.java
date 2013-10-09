package ar.edu.it.itba;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ar.edu.it.itba.PointMapping.Provider;

public class ActiveContour {

	// Define the radiud to find the colors for the objects to track
	public final static int RADIUS_X = 20;
	public final static int RADIUS_Y = 30;
	public final static int ALPHA = 2;
	public final static int BETA = 3;
	public final static int BUCKETS = 32;
	public final static double PONDER[] = new double[]{ 1, 0.25, 0.25 };
	
	private static final int MASK_RADIUS = 3;
	protected static final int MAX_ITERATIONS = 400*400;
	private static double SIGMA = 0.6;
	private static double[][] mask;
	static {
		int sideLength = 2  * MASK_RADIUS + 1;
		mask = new double[sideLength][sideLength];
		double sigmaSquared = Math.pow(SIGMA, 2);

		for (int i = -MASK_RADIUS; i < MASK_RADIUS + 1; i++) {
			for (int j = -MASK_RADIUS; j < MASK_RADIUS + 1; j++) {
				mask[i + MASK_RADIUS][j + MASK_RADIUS] = Math.pow(Math.E, - (double)(i * i + j * j) / (2 * sigmaSquared)) / (2.0 * Math.PI * sigmaSquared);
			}
		}
	}

	private final Contour[] contours;

	private final Color[][] omega;
	private final Color[][] omegaZero;
        private final int[][] phi;

	private final PointMapping theta;

	public ActiveContour(final BufferedImage frame, final Contour... c) {
		contours = c;
		// Calculating theta makes contours defien their internal points
		// it *must* happen before anything else
                phi = new int[frame.getWidth()][frame.getHeight()];
		theta = getTheta(c);

		omega = new Color[c.length][];
		omegaZero = new Color[c.length][];

		for (int i = 0; i < c.length; i++) {
			omega[i] = getCharacteristics(frame, contours[i]);
			omegaZero[i] = getBackgroundCharacteristics(frame, contours[i]);
		}

	}

	private Color[] getCharacteristics(BufferedImage frame, Contour contour) {
		List<Color> colors = new ArrayList<Color>();
		colors.addAll(Arrays.asList(mostFrequentColors(frame, contour)));
		colors.add(getAverageColor(frame, contour));
		return arrayResult(colors);
	}


	private Color[] arrayResult(List<Color> colors) {
		Color[] result = new Color[colors.size()];
		int i = 0;
		for (Color color : colors) {
			result[i++] = color;
		}
		return result;
	}

	private Color[] getBackgroundCharacteristics(BufferedImage frame,
			Contour contour) {
		List<Color> colors = new ArrayList<Color>();
		colors.add(getAverageBackgroundColor(frame, contour));
		colors.addAll(Arrays.asList(getMostFrequentBackgroundColors(frame, contour)));
		return arrayResult(colors);
	}

	public Contour[] adapt(final BufferedImage frame) {
		int nMax = max(frame.getHeight(), frame.getWidth());

		boolean[] done = new boolean[contours.length];
		int completed = 0;

		for (int i = 0; i < nMax; i++) {

			for (int j = 0; j < contours.length; j++) {

				if (done[j]) {
					continue;
				}

				final Contour c = contours[j];
				final PointMapping F_d = getF(frame, omegaZero[j], omega[j]);
				applyForce(c, F_d, theta, frame);

				if (endCondition(F_d, frame, c)) {
					done[j] = true;
					completed++;
				}
			}

			if (completed == contours.length) {
				break;
			}
		}


		int rounds = 2 * MASK_RADIUS + 1;
		for (int i = 0; i < rounds; i++) {

			for (Contour c : contours) {
				PointMapping F_s = new PointMapping(new Provider() {

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

		return contours;
	}

	private void calculateGauss(final List<Point> points, final PointMapping theta,
			final PointMapping f_s) {

		for (Point point : points) {

			double accum = 0;

			for (int mask_x = 0; mask_x < mask[0].length; mask_x++) {
				for (int mask_y = 0; mask_y < mask.length; mask_y++) {

					int delta_x = point.x + mask_x - MASK_RADIUS;
					int delta_y = point.y + mask_y - MASK_RADIUS;

					accum += mask[mask_y][mask_x] * theta.getValue(delta_x, delta_y);
				}
			}

			f_s.set(point, -Math.signum(accum));

		}

	}

	private void applyForce(final Contour r, final PointMapping force, final PointMapping theta, final BufferedImage frame) {
		List<Point> lout = r.getLout();
		List<Point> lin = r.getLin();
		for (int i = 0; i < lout.size(); i++) {
			Point p = lout.get(i);
			if (force.getValue(p) > 0 && !isBorder(frame, p)) {
				lout.remove(i);
				lin.add(p);
				r.addPoint(p);
				theta.set(p, -1);
				for (Point n : neighbors(p, frame.getWidth(), frame.getHeight())) {
					if (theta.getValue(n) == 3 && phi[n.x][n.y] == 0) {
						lout.add(n);
                                                phi[p.x][p.y] = r.color;
						theta.set(n, 1);
					}
				}
				i--;
			}
		}

		for (int i = 0; i < lin.size(); i++) {
			Point l = lin.get(i);
			int areInner = 0;
			List<Point> neighbors = neighbors(l, frame.getWidth(), frame.getHeight());
			for (Point n : neighbors) {
				if (theta.getValue(n) < 0) {
					areInner++;
				}
			}
			if (areInner == neighbors.size()) {
				lin.remove(i);
				theta.set(l, -3);
				i--;
			}
		}

		for (int i = 0; i < lin.size(); i++) {
			Point p = lin.get(i);
			if (force.getValue(p) < 0) {
				lin.remove(i);
				r.removePoint(p);
				lout.add(p);
				theta.set(p, 1);
				for (Point n : neighbors(p, frame.getWidth(), frame.getHeight())) {
					if (theta.getValue(n) == -3) {
						lin.add(n);
						r.addPoint(n);
						theta.set(n, -1);
					}
				}
				i--;
			}
		}

		for (int i = 0; i < lout.size(); i++) {
			Point l = lout.get(i);
			int neighbors = 0;
			for (Point n : neighbors(l, frame.getWidth(), frame.getHeight())) {
				if (theta.getValue(n) > 0) {
					neighbors++;
				}
			}
			if (neighbors == 4) {
				lout.remove(i);
                                phi[l.x][l.y] = 0;
				theta.set(l, 3);
				i--;
			}
		}

	}

	private static boolean isBorder(final BufferedImage frame, final Point p) {
		return p.x == 0 || p.y == 0 || p.x == frame.getWidth() - 1 || p.y == frame.getHeight() - 1;
	}

	private static List<Point> neighbors(final Point p, final int width, final int height) {
		List<Point> l = new ArrayList<Point>(4);

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

	private PointMapping getTheta(final Contour... contours) {

		PointMapping theta = new PointMapping(new Provider() {

			@Override
			public double valueForPoint(final Point p) {
				return 3;
			}

		});

		for (Contour contour : contours) {
			markInternalPoints(theta, contour);
		}

		return theta;
	}

	private void markInternalPoints(final PointMapping theta, final Contour r) {

		Set<Point> internalPoints = new HashSet<Point>();
		Set<Point> externalPoints = new HashSet<Point>();

		for (Point p : r.getLout()) {
			externalPoints.add(p);
                        phi[p.x][p.y] = r.color;
			theta.set(p, 1);
		}
		final Deque<Point> queue = new LinkedList<Point>();
		for (Point p : r.getLin()) {
			internalPoints.add(p);
			theta.set(p, -1);
                        phi[p.x][p.y] = r.color;
			queue.push(p);
		}
		int iterations = 0;
		while (!queue.isEmpty() && iterations < MAX_ITERATIONS) {
			iterations++;
			final Point p = queue.pop();
			for (Point n : neighbors(p, Integer.MAX_VALUE, Integer.MAX_VALUE)) {
				if (!internalPoints.contains(n) && !externalPoints.contains(n)) {
					internalPoints.add(n);
					queue.push(n);
					theta.set(n, -3);
					phi[p.x][p.y] = r.color;
				}
			}
		}

		r.setInternalPoints(internalPoints);

		if (iterations == MAX_ITERATIONS) {
			throw new RuntimeException("The contour is not conected");
		}
	}

	static PointMapping getF(final BufferedImage frame, final Color omegaZero[],
			final Color[] omega) {
		return new PointMapping(new Provider() {

			@Override
			public double valueForPoint(final Point p) {
				return prob(new Color(frame.getRGB(p.x, p.y)), omegaZero, omega);
			}
		});
	}

	private static Double prob(final Color color, final Color omegaZero[], final Color omega[]) {
		return Math.log((1 - diffColor(color, omega)) / (1 - diffColor(color, omegaZero)));
	}

	private static double diffColor(final Color color, final Color ... referenceColors) {
		double red = 0;
		double green = 0;
		double blue = 0;
		int i = 0;
		for (Color referenceColor : referenceColors) {
			red += Math.abs(color.getRed() - referenceColor.getRed()) * PONDER[i];
			green += Math.abs(color.getGreen() - referenceColor.getGreen()) * PONDER[i];
			blue += Math.abs(color.getBlue() - referenceColor.getBlue()) * PONDER[i];
			i++;
		}
		return red * green * blue / (Math.pow(256, 3));
	}

	private Color getAverageBackgroundColor(BufferedImage frame, Contour r) {
		int med_x = 0;
		int med_y = 0;
		for (Point p : r.getLout()) {
			med_x += p.x;
			med_y += p.y;
		}
		med_x /= r.getLout().size();
		med_y /= r.getLout().size();
		double red = 0;
		double green = 0;
		double blue = 0;
		double sum = 0;
		for (int x = med_x - RADIUS_X; x <= med_x + RADIUS_X; x++) {
			for (int y = med_y - RADIUS_Y; y <= med_y + RADIUS_Y; y++) {
				Point p = new Point(x, y);
				if (!r.contains(x, y)) {
					Color c = new Color(frame.getRGB(p.x, p.y));
					double distFactor = Math.abs(med_x - x)/RADIUS_X + Math.abs(med_y - y)*RADIUS_Y;
					red += c.getRed() * distFactor;
					green += c.getGreen() * distFactor;
					blue += c.getBlue() * distFactor;
					sum += distFactor;
				}
			}
		}
		return new Color((int) (red / sum),
				(int) (green / sum),
				(int) (blue / sum));
	}

	static private Color getAverageColor(BufferedImage frame, Contour r) {
		double red = 0;
		double green = 0;
		double blue = 0;
		int points = 0;
		for (Point p : r.getLout()) {
			Color c = new Color(frame.getRGB(p.x, p.y));
			red += c.getRed();
			green += c.getGreen();
			blue += c.getBlue();
			points++;
		}
		double avgRed = red / points;
		double avgGreen = green / points;
		double avgBlue = blue / points;
		return new Color((int) avgRed, (int) avgGreen, (int) avgBlue);
	}

	static Color[] mostFrequentColors(final BufferedImage frame, final Contour r) {
		double red[] = new double[BUCKETS];
		double green[] = new double[BUCKETS];
		double blue[] = new double[BUCKETS];
		Set<Point> visited = new HashSet<Point>();

		for (Point p : r.getLout()) {
			visited.add(p);
		}
		final Deque<Point> queue = new LinkedList<Point>();
		for (Point p : r.getLin()) {
			visited.add(p);
			queue.push(p);
		}
		int iterations = 0;
		while (!queue.isEmpty() && iterations < MAX_ITERATIONS) {
			iterations++;
			final Point p = queue.pop();
			Color c = new Color(frame.getRGB(p.x, p.y));
			red[c.getRed() / BUCKETS] += 1;
			green[c.getGreen() / BUCKETS] += 1;
			blue[c.getBlue() / BUCKETS] += 1;
			for (Point n : neighbors(p, Integer.MAX_VALUE, Integer.MAX_VALUE)) {
				if (!visited.contains(n)) {
					visited.add(n);
					queue.push(n);
				}
			}
		}
		return extractThreeMostCommon(red, green, blue);
	}

	private static Color[] extractThreeMostCommon(double[] red, double[] green,
			double[] blue) {
		int avgRed = 0;
		int avgGreen = 0;
		int avgBlue = 0;
		for (int i = 0; i < BUCKETS; i++) {
			avgRed = red[avgRed] > red[i] ? avgRed : i;
			avgGreen = green[avgGreen] > green[i] ? avgGreen : i;
			avgBlue = blue[avgBlue] > blue[i] ? avgBlue : i; 
		}
		Color results[] = new Color[3];
		results[0] = new Color((int) ((avgRed + 0.5) * BUCKETS),
				(int) ((avgGreen + 0.5) * BUCKETS),
				(int) ((avgBlue + 0.5) * BUCKETS));
		double FRACTION_OF_MOST_COMMON = 0.5;
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
			return new Color[] { results[0] };
		}
		results[1] = new Color((int) ((avgRed + 0.5) * BUCKETS),
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
			return new Color[] { results[0], results[1] };
		}
		results[2] = new Color((int) ((avgRed + 0.5) * BUCKETS),
				(int) ((avgGreen + 0.5) * BUCKETS),
				(int) ((avgBlue + 0.5) * BUCKETS));
		
		
		return results;
	}

	private Color[] getMostFrequentBackgroundColors(final BufferedImage frame, final Contour r) {
		int maxX = Math.min(frame.getWidth(), r.maxX() + 15);
		int maxY = Math.min(frame.getHeight(), r.maxY() + 15);

		double red[] = new double[BUCKETS];
		double green[] = new double[BUCKETS];
		double blue[] = new double[BUCKETS];
		for (int i = Math.max(0, r.minX() - 15); i < maxX; i++) {
			for (int j = Math.max(0, r.minY() - 15); j < maxY; j++) {

				if (!r.contains(i, j)) {
					Color c = new Color(frame.getRGB(i, j));
					red[c.getRed() / BUCKETS] += 1;
					green[c.getGreen() / BUCKETS] += 1;
					blue[c.getBlue() / BUCKETS] += 1;
				}
			}
		}
		return extractThreeMostCommon(red, green, blue);
	}

	static boolean endCondition(final PointMapping F_d, final BufferedImage coloredFrame, final Contour r) {

            // WARNING! FALTA CONSIDERAR PHI ACA
		for (Point p : r.getLout()) {
			if (F_d.getValue(p) > 0) {
				return false;
			}
		}
		for (Point p : r.getLin()) {
			if (F_d.getValue(p) < 0) {
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

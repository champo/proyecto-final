package ar.edu.it.itba.processing;

import static ar.edu.it.itba.processing.Helpers.calculateBackgroundStandardDeviation;
import static ar.edu.it.itba.processing.Helpers.calculateStandardDeviation;
import static ar.edu.it.itba.processing.Helpers.getBackgroundCharacteristics;
import static ar.edu.it.itba.processing.Helpers.getCharacteristics;
import static ar.edu.it.itba.processing.Helpers.isBorder;
import static ar.edu.it.itba.processing.Helpers.max;
import static ar.edu.it.itba.processing.Helpers.neighbors;
import static ar.edu.it.itba.processing.Helpers.neighbors8;
import static ar.edu.it.itba.processing.Helpers.prob;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.edu.it.itba.processing.Contour.State;
import ar.edu.it.itba.processing.PointMapping.Provider;
import ar.edu.it.itba.processing.color.ColorPoint;
import ar.edu.it.itba.processing.color.ColorPoint.Type;

public class ActiveContour {


	private static final int BLACK = Color.black.getRGB();
	private static final int CYAN = Color.CYAN.getRGB();
	private final int DEVIATIONS = 3;

	private final double ALPHA = 0.04;

	private static final int MASK_RADIUS = 3;
	protected static final int MAX_ITERATIONS = 40 * 40;
	private static final ColorPoint whiteColorPoint = ColorPoint.build(ColorPoint.Type.RGB, 130, 150, 90);
	private static final ColorPoint whiteDeviation = ColorPoint.build(ColorPoint.Type.RGB, 10, 12, 12);

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

	public final Contour[] contours;
	private final int[][] phi;

	private final PointMapping theta;
	public static ColorPoint bgDeviation;
	private boolean invertedDetection;

	public ActiveContour(final BufferedImage frame, final Contour... c) {
		contours = c;

		for (int i = 0; i < c.length; i++) {
			Contour contour = contours[i];
			contour.idx = i + 1;
		}

		// Calculating theta makes contours define their internal points
		// it *must* happen before anything else
		phi = new int[frame.getWidth()][frame.getHeight()];
		theta = getTheta(c);

		Map<Type, ColorPoint[]> omegaZero = new HashMap<>();
		Map<Type, ColorPoint> bgDeviation = new HashMap<>();

		for (int i = 0; i < c.length; i++) {
			Contour contour = contours[i];

			contour.omega = getCharacteristics(frame, contour);
			contour.setLastStdDev(calculateStandardDeviation(contour, frame));

			Type type = contour.getType();
			if (omegaZero.containsKey(type)) {
				contour.omegaZero = omegaZero.get(type);
			} else {
				contour.omegaZero = getBackgroundCharacteristics(frame, contour, c);
				omegaZero.put(type, contour.omegaZero);
			}

			if (bgDeviation.containsKey(type)) {
				 contour.bgDeviation = bgDeviation.get(type);
			} else {
				contour.bgDeviation = calculateBackgroundStandardDeviation(frame, contour, c);
				bgDeviation.put(type, contour.bgDeviation);
			}
		}

	}

	public void setInvertedDetection(final boolean invertedDetection) {
		this.invertedDetection = invertedDetection;
	}

	public float adapt(final BufferedImage frame) {

		final long time = System.currentTimeMillis();
		final int nMax = max(frame.getHeight(), frame.getWidth());

		if (!invertedDetection) {
			for (final Contour c : contours) {
				for (Point point : c) {

					if (frame.getRGB(point.x, point.y) != CYAN) {
						continue;
					}

					for (Point i : neighbors(point, frame.getWidth(), frame.getHeight())) {
						if (frame.getRGB(i.x, i.y) != BLACK) {
							frame.setRGB(i.x, i.y, CYAN);
						}
					}
				}
			}
		}

		final boolean[] done = new boolean[contours.length];
		int completed = 0;

		for (int i = 0; i < nMax; i++) {

			for (int j = 0; j < contours.length; j++) {

				if (done[j]) {
					continue;
				}

				final Contour c = contours[j];
				final PointMapping F_d = getF(frame, c);

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
			} else {
				c.omega = learn(ALPHA, c.omega, getCharacteristics(frame, c));
			}
		}


		if (!invertedDetection) {
			return System.currentTimeMillis() - time;
		}

		final Contour c = contours[0];
		final ColorPoint omegaZero = c.omegaZero[0];

		for (int i = 0; i < frame.getWidth(); i++) {
			for (int j = 0; j < frame.getHeight(); j++) {

				// This means it not a background pixel according to AC
				int rgb = frame.getRGB(i, j);
				if (phi[i][j] != 0 || rgb == BLACK) {
					continue;
				}

				ColorPoint color = ColorPoint.buildFromRGB(omegaZero.getType(), rgb);

				if (isLike(omegaZero, color, bgDeviation)) {
					// Here we confirm it's really a background pixel
					continue;
				}

				if (isLike(whiteColorPoint, color, whiteDeviation)) {
					// Skip white lines
					continue;
				}

				// It's not background it seems, let's color it something ugly
				frame.setRGB(i, j, CYAN);
			}
		}

		return System.currentTimeMillis() - time;
	}

	private boolean isLike(final ColorPoint omegaZero, final ColorPoint color, final ColorPoint deviation) {

		return Math.abs(color.red - omegaZero.red) < DEVIATIONS * deviation.red &&
				Math.abs(color.green - omegaZero.green) < DEVIATIONS * deviation.green &&
				Math.abs(color.blue - omegaZero.blue) < DEVIATIONS * deviation.blue;
	}

	private void markExpandedArea(final BufferedImage frame, final Contour c) {

		int searchRadius = Math.max(5 + c.cyclesLost(), 10);
		Point center = c.getLastCentroid();

		clearContour(c);

		System.out.println("Search radius = " + searchRadius);
		System.out.println("Center = " + center);

		int maxX = Math.min(frame.getWidth() - 1, center.x + searchRadius);
		int maxY = Math.min(frame.getHeight() - 1, center.y + searchRadius);

		Set<Point> points = new HashSet<Point>();

		for (int x = Math.max(0, center.x - searchRadius); x < maxX; x++) {
			for (int y = Math.max(0, center.y - searchRadius); y < maxY; y++) {

				if (phi[x][y] == 0) {
					Point p = new Point(x, y);
					points.add(p);
				}

			}
		}

		markContour(frame, c, points);

	}

	private void markContour(final BufferedImage frame, final Contour c, final Set<Point> points) {

		for (Point p : points) {
			phi[p.x][p.y] = c.idx;
			theta.set(p, -3);
		}

		Set<Point> lout = c.getLout();
		Set<Point> lin = c.getLin();

		for (Point p : points) {
			List<Point> neighbors = neighbors(p, frame.getWidth(), frame.getHeight());
			int connected = 0;
			for (Point point : neighbors) {
				if (phi[point.x][point.y] == c.idx) {
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

	public void resetContourToRect(final BufferedImage frame, final Contour c, final Rectangle r) {

		int maxX = Math.min(frame.getWidth() - 1, (int) r.getMaxX());
		int maxY = Math.min(frame.getHeight() - 1, (int) r.getMaxY());

		Set<Point> points = new HashSet<Point>();

		clearContour(c);

		for (int x = Math.max(0, (int) r.getMinX()); x < maxX; x++) {
			for (int y = Math.max(0, (int) r.getMinY()); y < maxY; y++) {

				if (phi[x][y] == 0) {
					Point p = new Point(x, y);
					points.add(p);
				}

			}
		}

		markContour(frame, c, points);
		c.mutationFinished();
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
						phi[n.x][n.y] = r.idx;
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

		if (r.isInitialized()) {
			return;
		}
		final Set<Point> internalPoints = new HashSet<Point>();
		final Set<Point> externalPoints = new HashSet<Point>();

		for (final Point p : r.getLout()) {
			externalPoints.add(p);
            phi[p.x][p.y] = r.idx;
			theta.set(p, 1);
		}
		final Deque<Point> queue = new LinkedList<Point>();
		for (final Point p : r.getLin()) {
			internalPoints.add(p);
			theta.set(p, -1);
            phi[p.x][p.y] = r.idx;
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
					phi[n.x][n.y] = r.idx;
				}
			}
		}

		r.setInternalPoints(internalPoints);

		if (iterations == MAX_ITERATIONS) {
			throw new RuntimeException("The contour is not conected");
		}
	}


	private PointMapping getF(final BufferedImage frame, final Contour c) {
		return new PointMapping(new Provider() {

			@Override
			public double valueForPoint(final Point p) {
				return prob(frame, c, p);
			}
		});
	}

	public int[][] getMapping() {
        return phi;
    }

    private ColorPoint[] learn(double ALPHA, ColorPoint[] omega, ColorPoint[] characteristics) {
        return omega;
        /*
        ColorPoint[] newColour = new ColorPoint[omega.length];
        for (int i = 0; i < newColour.length; i++) {
            newColour[i] = ColorPoint.build(omega[0].getType(),
                    (int)((1-ALPHA) * omega[i].red + ALPHA * characteristics[i].red),
                    (int)((1-ALPHA) * omega[i].green + ALPHA * characteristics[i].green),
                    (int)((1-ALPHA) * omega[i].blue + ALPHA * characteristics[i].blue)
            );
        }
        return newColour;
        */
    }

}

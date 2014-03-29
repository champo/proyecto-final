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
	private final int[][] phi;

	private final PointMapping theta;

	public ActiveContour(final BufferedImage frame, final Contour... c) {
		contours = c;
		// Calculating theta makes contours defien their internal points
		// it *must* happen before anything else
		phi = new int[frame.getWidth()][frame.getHeight()];
		theta = getTheta(c);

		for (int i = 0; i < c.length; i++) {
			Contour contour = contours[i];

			contour.omega = getCharacteristics(frame, contour);
			contour.omegaZero = getBackgroundCharacteristics(frame, contour);
			contour.bgDeviation = calculateBackgroundStandardDeviation(contour, frame);
			contour.setLastStdDev(calculateStandardDeviation(contour, frame));
		}

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
			}
		}


		final long diff = System.currentTimeMillis() - time;
		System.out.println("Time difference: " + diff + " ms");

		return diff;
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


	private PointMapping getF(final BufferedImage frame, final Contour c) {
		return new PointMapping(new Provider() {

			@Override
			public double valueForPoint(final Point p) {
				return prob(frame, c, p);
			}
		});
	}

	private boolean endCondition(final PointMapping F_d, final BufferedImage coloredFrame, final Contour r) {

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

    public int[][] getMapping() {
        return phi;
    }

}

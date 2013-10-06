package ar.edu.it.itba;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.edu.it.itba.PointMapping.Provider;

public class ActiveContour {

	private static final int MASK_RADIUS = 3;
	protected static final int MAX_ITERATIONS = 400*400;
	private static double SIGMA = 0.5;
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

	private static Color omegaZero;
	private static Color omega;

	public static Contour adapt(final BufferedImage frame, final Contour c) {
		Contour r = c;
		int nMax = max(c.countRows(), c.countCols());

		// Draw a rectangle of 30 pixels more width and more height around the contour to get the characteristic average color
		int x1 = r.minX() - 15;
		int x2 = r.maxX() + 15;
		int y1 = r.minY() - 15;
		int y2 = r.maxY() + 15;

		x1 = Math.max(x1, 0);
		y1 = Math.max(y1, 0);
		x2 = Math.min(x2, frame.getWidth() - 1);
		y2 = Math.min(y2, frame.getHeight() - 1);

		if (omegaZero == null) {
			omegaZero = getAverageColor(frame, r, x1, x2, y1, y2);
		}
		if (omega == null) {
			omega = getAverageColor(frame, r);
		}
		PointMapping theta = new PointMapping(getThetaProvider(r));

		for (int i = 0; i < nMax; i++) {
			PointMapping F_d = getF(frame, omegaZero, omega);
			r = applyForce(r, F_d, theta, frame);
			if (endCondition(F_d, frame, r)) {
				break;
			}
		}


		int rounds = 2 * MASK_RADIUS + 1;
		for (int i = 0; i < rounds; i++) {

			PointMapping F_s = new PointMapping(new Provider() {

				@Override
				public double valueForPoint(final Point p) {
					return 0;
				}
			});
			calculateGauss(r.getLin(), theta, F_s);
			calculateGauss(r.getLout(), theta, F_s);

			r = applyForce(r, F_s, theta, frame);
		}

		return r;
	}

	private static void calculateGauss(final List<Point> points, final PointMapping theta,
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



	private static Contour applyForce(final Contour r, final PointMapping force, final PointMapping theta, final BufferedImage frame) {
		List<Point> lout = new ArrayList<Point>(r.getLout());
		List<Point> lin = new ArrayList<Point>(r.getLin());
		for (int i = 0; i < lout.size(); i++) {
			Point p = lout.get(i);
			if (force.getValue(p) > 0) {
				lout.remove(i);
				lin.add(p);
				theta.set(p, -1);
				for (Point n : neighbors(p, frame.getWidth(), frame.getHeight())) {
					if (theta.getValue(n) == 3) {
						lout.add(n);
						theta.set(n, 1);
					}
				}
				i--;
			}
		}

		for (int i = 0; i < lin.size(); i++) {
			Point l = lin.get(i);
			int neighbors = 0;
			for (Point n : neighbors(l, frame.getWidth(), frame.getHeight())) {
				if (theta.getValue(n) < 0) {
					neighbors++;
				}
			}
			if (neighbors == 4) {
				lin.remove(i);
				theta.set(l, -3);
				i--;
			}
		}

		for (int i = 0; i < lin.size(); i++) {
			Point p = lin.get(i);
			if (force.getValue(p) < 0) {
				lin.remove(i);
				lout.add(p);
				theta.set(p, 1);
				for (Point n : neighbors(p, frame.getWidth(), frame.getHeight())) {
					if (theta.getValue(n) == -3) {
						lin.add(n);
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
				theta.set(l, 3);
				i--;
			}
		}

		Contour result = new Contour(lout, lin);
		theta.setProvider(getThetaProvider(result));
		return result;
	}

	private static Iterable<Point> neighbors(final Point p, int width, int height) {
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

	private static Provider getThetaProvider(final Contour r) {

		return new Provider() {
			
			Set<Point> internalPoints;
			
			synchronized private void BFS() {
				internalPoints = new HashSet<Point>();
				for (Point p : r.getLout()) {
					internalPoints.add(p);
				}
				final Deque<Point> queue = new LinkedList<Point>();
				for (Point p : r.getLin()) {
					internalPoints.add(p);
					queue.push(p);
				}
				int iterations = 0;
				while (!queue.isEmpty() && iterations < MAX_ITERATIONS) {
					iterations++;
					final Point p = queue.pop();
					for (Point n : neighbors(p, Integer.MAX_VALUE, Integer.MIN_VALUE)) {
						if (!internalPoints.contains(n)) {
							internalPoints.add(n);
							queue.push(n);
						}
					}
				}
				if (iterations == MAX_ITERATIONS) {
					throw new RuntimeException("The contour is not conected");
				}
			}

			@Override
			public double valueForPoint(final Point p) {
				if (internalPoints == null) {
					BFS();
				}
				if (r.inLout(p.x, p.y)) {
					return 1;
				} else if (r.inLin(p.x, p.y)) {
					return -1;
				} else if (!internal(p.x, p.y) && !r.inLout(p.x, p.y)) {
					return 3;
				} else if (internal(p.x, p.y) && !r.inLin(p.x, p.y)) {
					return -3;
				}

				return 0;
			}

			private boolean internal(int x, int y) {
				return internalPoints.contains(new Point(x, y));
			}
		};
	}

	static PointMapping getF(final BufferedImage frame, final Color omegaZero,
			final Color omega) {
		return new PointMapping(new Provider() {

			@Override
			public double valueForPoint(final Point p) {
				return prob(new Color(frame.getRGB(p.x, p.y)), omegaZero, omega);
			}
		});
	}

	private static Double prob(final Color color, final Color omegaZero, final Color omega) {
		return Math.log((1 - diffColor(color, omega)) / (1 - diffColor(color, omegaZero)));
	}

	private static double diffColor(final Color color, final Color referenceColor) {
		double red = Math.abs(color.getRed() - referenceColor.getRed());
		double green = Math.abs(color.getGreen() - referenceColor.getGreen());
		double blue = Math.abs(color.getBlue() - referenceColor.getBlue());
		return red * green * blue / Math.pow(256, 3);
	}

	static Color getAverageColor(final BufferedImage frame, final Contour r) {
		int x1 = r.minX();
		int x2 = r.maxX();
		double avgRed = 0;
		double avgGreen = 0;
		double avgBlue = 0;
		int pixels = 0;
		for (int x = x1; x <= x2; x++) {
			List<Point> points = r.getPointsAtCol(x);
			for (int i = 1; i < points.size(); i+=2) {
				for (int y = points.get(i-1).y; y < points.get(i).y; y++) {
					Color c = new Color(frame.getRGB(x, y));
					avgRed += c.getRed();
					avgGreen += c.getGreen();
					avgBlue += c.getBlue();
					pixels++;
				}
			}
		}
		return new Color((int) (avgRed/pixels), (int) (avgGreen/pixels), (int)(avgBlue/pixels));
	}

	static Color getAverageColor(final BufferedImage frame, final Contour r, final int x1, final int x2,
			final int y1, final int y2) {
		double avgRed = 0;
		double avgGreen = 0;
		double avgBlue = 0;
		int pixels = 0;

		for (int i = x1; i <= x2; i++) {
			for (int j = y1; j <= y2; j++) {
				if (!r.contains(i, j)) {
					Color c = new Color(frame.getRGB(i, j));
					avgRed += c.getRed();
					avgGreen += c.getGreen();
					avgBlue += c.getBlue();
					pixels++;
				}
			}
		}

		return new Color((int) (avgRed/pixels), (int) (avgGreen/pixels), (int)(avgBlue/pixels));
	}

	static boolean endCondition(final PointMapping F_d, final BufferedImage coloredFrame, final Contour r) {

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
}

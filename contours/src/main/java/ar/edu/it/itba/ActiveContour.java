package ar.edu.it.itba;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiveContour {

	public static Contour adapt(final BufferedImage frame, final Contour c, final int gaussRadius) {
		Contour r = c;
		int nMax = max(c.countRows(), c.countCols());

		int x1 = r.minX() - 15;
		int x2 = r.maxX() + 15;
		int y1 = r.minY() - 15;
		int y2 = r.maxY() + 15;
		Color omegaZero = getAverageColor(frame, r, x1, x2, y1, y2);
		Color omega = getAverageColor(frame, r);

		for (int i = 0; i < nMax; i++) {
			// Draw a rectangle of 30 pixels more width and more height around the contour to get the characteristic average color
			x1 = r.minX() - 15;
			x2 = r.maxX() + 15;
			y1 = r.minY() - 15;
			y2 = r.maxY() + 15;

			PointMapping F_d = getF(frame, omegaZero, omega, x1, x2, y1, y2);

			r = firstRound(frame, r, x1, x2, y1, y2, omegaZero, omega, F_d);
			if (endCondition(F_d, frame, r)) {
				break;
			}
		}
		r = secondRound(r, gaussRadius);
		return r;
	}

	private static Contour firstRound(final BufferedImage frame, final Contour r, final int x1, final int x2, final int y1, final int y2,
			final Color omegaZero, final Color omega, final PointMapping f_d) {

		PointMapping theta = getTheta(f_d, r, frame, omegaZero, omega, x1, x2, y1, y2);

		List<Point> lout = r.getLout();
		List<Point> lin = r.getLin();
		for (int i = 0; i < lout.size(); i++) {
			Point p = lout.get(i);
			if (f_d.getValue(p) > 0) {
				lout.remove(i);
				lin.add(p);
				theta.set(p, 1);
				for (Point n : neighbors(p)) {
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
			for (Point n : neighbors(l)) {
				if (lout.contains(n)) {
					neighbors++;
				}
			}
			if (neighbors == 0) {
				lin.remove(i);
				theta.set(l, -3);
				i--;
			}
		}
		for (int i = 0; i < lin.size(); i++) {
			Point p = lin.get(i);
			if (f_d.getValue(p) < 0) {
				lin.remove(i);
				lout.add(p);
				theta.set(p, -1);
				for (Point n : neighbors(p)) {
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
			for (Point n : neighbors(l)) {
				if (lin.contains(n)) {
					neighbors++;
				}
			}
			if (neighbors < 0) {
				lout.remove(i);
				theta.set(l, 3);
				i--;
			}
		}
		return new Contour(lout, lin);
	}

	private static Iterable<Point> neighbors(final Point p) {
		List<Point> l = new ArrayList<Point>(4);
		l.add(new Point(p.x + 1, p.y));
		l.add(new Point(p.x, p.y + 1));
		l.add(new Point(p.x - 1, p.y));
		l.add(new Point(p.x, p.y - 1));
		return l;
	}

	private static PointMapping getTheta(final PointMapping f_d, final Contour r,
			final BufferedImage frame, final Color omegaZero, final Color omega, final int x1, final int x2,
			final int y1, final int y2) {

		Map<Point, Double> map = new HashMap<Point, Double>();
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y < y2; y++) {
				if (f_d.getValue(x, y) > 0 && !r.inLout(x, y)) {
					map.put(new Point(x, y), 3.);
				} else if (r.inLout(x, y)) {
					map.put(new Point(x, y), 1.);
				} else if (r.inLin(x, y)) {
					map.put(new Point(x, y), -1.);
				} else if (f_d.getValue(x, y) < 0 && !r.inLin(x, y)) {
					map.put(new Point(x, y), -3.);
				} else {
					map.put(new Point(x, y), 0.);
				}
			}
		}
		return new PointMapping(map);
	}

	static PointMapping getF(final BufferedImage frame, final Color omegaZero,
			final Color omega, final int x1, final int x2, final int y1, final int y2) {
		Map<Point, Double> map = new HashMap<Point, Double>();
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				map.put(new Point(x, y), prob(new Color(frame.getRGB(x, y)), omegaZero, omega));
			}
		}
		return new PointMapping(map);
	}

	private static Double prob(final Color color, final Color omegaZero, final Color omega) {
		return Math.log(diffColor(color, omega) / diffColor(color, omegaZero));
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

	static Color getAverageColor(final BufferedImage frame, final Contour r, int x1, int x2,
			int y1, int y2) {
		double avgRed = 0;
		double avgGreen = 0;
		double avgBlue = 0;
		int pixels = 0;

		x1 = Math.max(x1, 0);
		y1 = Math.max(y1, 0);
		x2 = Math.min(x2, frame.getWidth() - 1);
		y2 = Math.min(y2, frame.getHeight() - 1);
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

	private static Contour secondRound(final Contour c, final int gaussRadius) {
		return c;
	}

	private static int max(final int a, final int b) {
		return a > b ? a : b;
	}
}

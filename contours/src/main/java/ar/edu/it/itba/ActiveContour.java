package ar.edu.it.itba;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiveContour {

	public static Contour adapt(BufferedImage frame, Contour c, int gaussRadius) {
		Contour r = c;
		int nMax = max(c.countRows(), c.countCols());
				
		for (int i = 0; i < nMax; i++) {
			// Draw a rectangle of 30 pixels more width and more height around the contour to get the characteristic average color
			int x1 = r.minX() - 15;
			int x2 = r.maxX() + 15;
			int y1 = r.minY() - 15;
			int y2 = r.maxY() + 15;
			Color omegaZero = getAverageColor(frame, r, x1, x2, y1, y2);
			Color omega = getAverageColor(frame, r);
			PointMapping F_d = getF(frame, omegaZero, omega, x1, x2, y1, y2);

			r = firstRound(frame, r, x1, x2, y1, y2, omegaZero, omega, F_d);
			if (endCondition(F_d, frame, r)) {
				break;
			}
		}
		r = secondRound(r, gaussRadius);
		return r;
	}
	
	private static Contour firstRound(BufferedImage frame, Contour r, int x1, int x2, int y1, int y2,
			Color omegaZero, Color omega, PointMapping f_d) {
		
		PointMapping theta = getTheta(f_d, r, frame, omegaZero, omega, x1, x2, y1, y2);

		List<Point> lout = r.getLout();
		List<Point> lin = r.getLin();
		for (int i = 0; i < lout.size(); i++) {
			Point p = lout.get(i);
			if (f_d.getValue(p) < 0) {
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
				i--;
			}
		}
		for (int i = 0; i < lin.size(); i++) {
			Point p = lin.get(i);
			if (f_d.getValue(p) > 0) {
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
				i--;
			}
		}
		return new Contour(lout, lin);
	}

	private static Iterable<Point> neighbors(Point p) {
		List<Point> l = new ArrayList<Point>(4);
		l.add(new Point(p.x + 1, p.y));
		l.add(new Point(p.x, p.y + 1));
		l.add(new Point(p.x - 1, p.y));
		l.add(new Point(p.x, p.y - 1));
		return l;
	}

	private static PointMapping getTheta(PointMapping f_d, Contour r,
			BufferedImage frame, Color omegaZero, Color omega, int x1, int x2,
			int y1, int y2) {

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

	static PointMapping getF(BufferedImage frame, Color omegaZero,
			Color omega, int x1, int x2, int y1, int y2) {
		Map<Point, Double> map = new HashMap<Point, Double>();
		for (int x = x1; x <= x2; x++) {
			for (int y = y1; y <= y2; y++) {
				map.put(new Point(x, y), prob(new Color(frame.getRGB(x, y)), omegaZero, omega));
			}
		}
		return new PointMapping(map);
	}

	private static Double prob(Color color, Color omegaZero, Color omega) {
		return Math.log(diffColor(color, omega) / diffColor(color, omegaZero));
	}

	private static double diffColor(Color color, Color omegaZero) {
		double red = Math.abs(color.getRed() - omegaZero.getRed());
		double green = Math.abs(color.getGreen() - omegaZero.getGreen());
		double blue = Math.abs(color.getBlue() - omegaZero.getBlue());
		return red * green * blue / Math.pow(256, 3);
	}

	static Color getAverageColor(BufferedImage frame, Contour r) {
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

	static Color getAverageColor(BufferedImage frame, Contour r, int x1, int x2,
			int y1, int y2) {
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

	static boolean endCondition(PointMapping F_d, BufferedImage coloredFrame, Contour r) {
		
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
	
	private static Contour secondRound(Contour c, int gaussRadius) {
		return c;
	}

	private static int max(int a, int b) {
		return a > b ? a : b;
	}
}

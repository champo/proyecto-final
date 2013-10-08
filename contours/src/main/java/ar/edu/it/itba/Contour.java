package ar.edu.it.itba;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Contour implements Iterable<Point>{

	private final List<Point> points;
	private final List<Point> lin;

	private Set<Point> internalPoints;

	public Contour(final Rectangle rect) {
		points = new ArrayList<Point>(rect.width * 2 + rect.height * 2);
		lin = new ArrayList<Point>(rect.width * 2 + rect.height * 2);
		for (int i = 0; i < rect.width; i++) {
			points.add(new Point(rect.x + i, rect.y));
		}
		for (int j = 0; j < rect.height; j++) {
			points.add(new Point(rect.x + rect.width, rect.y + j));
		}
		for (int i = rect.width; i > 0; i--) {
			points.add(new Point(rect.x + i, rect.y + rect.height));
		}
		for (int j = rect.height; j > 0; j--) {
			points.add(new Point(rect.x, rect.y + j));
		}
		for (int i = 1; i < rect.width - 1; i++) {
			lin.add(new Point(rect.x + i, rect.y + 1));
		}
		for (int j = 1; j < rect.height - 1; j++) {
			lin.add(new Point(rect.x + rect.width - 1, rect.y + j));
		}
		for (int i = rect.width - 1; i > 1; i--) {
			lin.add(new Point(rect.x + i, rect.y + rect.height - 1));
		}
		for (int j = rect.height - 1; j > 1; j--) {
			lin.add(new Point(rect.x + 1, rect.y + j));
		}
	}

	public Contour(final List<Point> lout, final List<Point> lin) {
		points = lout;
		this.lin = lin;
	}

	public static Contour aroundPoint(final Point point) {
		return new Contour(new Rectangle(point.x - 5, point.y - 5, 10, 10));
	}

	public int minX() {

		int min = Integer.MAX_VALUE;
		for (Point p : points) {
			min = min(min, p.x);
		}
		return min;
	}

	public int maxX() {

		int max = Integer.MIN_VALUE;
		for (Point p : points) {
			max = max(max, p.x);
		}
		return max;
	}

	public int minY() {

		int min = Integer.MAX_VALUE;
		for (Point p : points) {
			min = min(min, p.y);
		}
		return min;
	}

	public int maxY() {

		int max = Integer.MIN_VALUE;
		for (Point p : points) {
			max = max(max, p.y);
		}
		return max;
	}

	private static int max(final int a, final int b) {
		return a > b ? a : b;
	}
	private static int min(final int a, final int b) {
		return a < b ? a : b;
	}

	@Override
	public Iterator<Point> iterator() {
		return internalPoints.iterator();
	}

	public boolean contains(final int i, final int j) {
		return internalPoints.contains(new Point(i, j));
	}

	public List<Point> getLin() {
		return lin;
	}

	public List<Point> getLout() {
		return points;
	}

	public void setInternalPoints(final Set<Point> internalPoints) {
		this.internalPoints = internalPoints;
	}

	public void addPoint(final Point p) {
		internalPoints.add(p);
	}

	public void removePoint(final Point p) {
		internalPoints.remove(p);
	}
}

package ar.edu.it.itba;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Contour implements Iterable<Point>{

	private final List<Point> points;
	private final List<Point> lin;

	private int cachedMinX = Integer.MIN_VALUE;
	private int cachedMaxX = Integer.MAX_VALUE;
	private int cachedMinY = Integer.MIN_VALUE;
	private int cachedMaxY = Integer.MAX_VALUE;
	private final Map<Integer, List<Point>> cachedLines = new HashMap<Integer, List<Point>>();

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

	public static Contour aroundPoint(Point point) {
		return new Contour(new Rectangle(point.x - 10, point.y - 10, 20, 20));
	}

	public int minX() {

		if (cachedMinX != Integer.MIN_VALUE) {
			return cachedMinX;
		}
		int min = Integer.MAX_VALUE;
		for (Point p : points) {
			min = min(min, p.x);
		}
		cachedMinX = min;
		return min;
	}

	public int maxX() {

		if (cachedMaxX != Integer.MAX_VALUE) {
			return cachedMaxX;
		}
		int max = Integer.MIN_VALUE;
		for (Point p : points) {
			max = max(max, p.x);
		}
		cachedMaxX = max;
		return max;
	}

	public int minY() {

		if (cachedMinY != Integer.MIN_VALUE) {
			return cachedMinY;
		}
		int min = Integer.MAX_VALUE;
		for (Point p : points) {
			min = min(min, p.y);
		}
		cachedMinY = min;
		return min;
	}

	public int maxY() {

		if (cachedMaxY != Integer.MAX_VALUE) {
			return cachedMaxY;
		}
		int max = Integer.MIN_VALUE;
		for (Point p : points) {
			max = max(max, p.y);
		}
		cachedMaxY = max;
		return max;
	}

	public int countCols() {
		return maxX() - minX();
	}

	public int countRows() {
		return maxY() - minY();
	}

	private static int max(final int a, final int b) {
		return a > b ? a : b;
	}
	private static int min(final int a, final int b) {
		return a < b ? a : b;
	}

	@Override
	public Iterator<Point> iterator() {
		return points.iterator();
	}

	public boolean contains(final int i, final int j) {
		List<Point> pointsInRow = getPointsAtCol(i);
		for (int k = 1; k < pointsInRow.size(); k += 2) {
			if (pointsInRow.get(k-1).y <= j && j <= pointsInRow.get(k).y) {
				return true;
			}
		}
		return false;
	}

	public List<Point> getPointsAtCol(final int x) {
		if (cachedLines.containsKey(x)) {
			return cachedLines.get(x);
		}
		List<Point> pointsInRow = new ArrayList<Point>(10);
		for (Point p : points) {
			if (p.x == x) {
				pointsInRow.add(p);
			}
		}
		Collections.sort(pointsInRow, new Comparator<Point>() {
			@Override
			public int compare(final Point o1, final Point o2) {
				return o1.y - o2.y;
			}
		});
		cachedLines.put(x, pointsInRow);
		return pointsInRow;
	}

	public List<Point> getLin() {
		return lin;
	}
	public List<Point> getLout() {
		return points;
	}

	public boolean inLout(final int x, final int y) {
		return points.contains(new Point(x, y));
	}

	public boolean inLin(final int x, final int y) {
		return lin.contains(new Point(x, y));
	}
}

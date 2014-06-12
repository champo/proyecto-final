package ar.edu.it.itba.processing;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ar.edu.it.itba.processing.color.ColorPoint;
import ar.edu.it.itba.processing.color.ColorPoint.Type;

public class Contour implements Iterable<Point> {

	enum State {
		STABLE,
		MISSING
	}

	private static final double mu = 0.5;

	private final Set<Point> points;
	private final Set<Point> lin;
	public final int color;

	public int idx;

	private Set<Point> internalPoints;

	private boolean initialized = false;
	private long accumulatedSize = 0;
	private int mutationCount = 0;

	private Point lastCentroid;

	private State state = State.MISSING;

	private int cyclesLost;

	private ColorPoint lastStdDev;

	public ColorPoint omega[];

	public ColorPoint omegaZero[];

	public ColorPoint bgDeviation;

	private ColorPoint.Type type = Type.RGB;

	public Contour(final int color, final Rectangle rect) {
                this.color = color;
		points = new HashSet<Point>();
		lin = new HashSet<Point>();
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

	public static Contour aroundPoint(final int color, final Point point) {
		return new Contour(color, new Rectangle(point.x - 8, point.y - 17, 16, 34));
	}

	public static Contour squareAroundPoint(final int color, final Point point) {
		return new Contour(color, new Rectangle(point.x - 8, point.y - 8, 16, 16));
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

	public Set<Point> getLin() {
		return lin;
	}

	public Set<Point> getLout() {
		return points;
	}

	public void setInternalPoints(final Set<Point> internalPoints) {
		this.internalPoints = internalPoints;
		this.initialized = true;
		lastCentroid = new Point(centroidX(), centroidY());
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void addPoint(final Point p) {
		internalPoints.add(p);
	}

	public void removePoint(final Point p) {
		internalPoints.remove(p);
	}

	public int centroidY() {
		double cumm = 0;
		for (Point p : internalPoints) {
			cumm += p.y;
		}
		return (int) (cumm/internalPoints.size());
	}

	public int centroidX() {
		double cumm = 0;
		for (Point p : internalPoints) {
			cumm += p.x;
		}
		return (int) (cumm/internalPoints.size());
	}
	public int averageY() {
		double cumm = 0;
		for (Point p : lin) {
			cumm += p.y;
		}
		return (int) (cumm/lin.size());
	}

	public int averageX() {
		double cumm = 0;
		for (Point p : lin) {
			cumm += p.x;
		}
		return (int) (cumm/lin.size());
	}

	public void mutationFinished() {

		checkIfMissing();

		if (state == State.STABLE) {
			accumulatedSize += currentSize();
			mutationCount++;

			lastCentroid = new Point(centroidX(), centroidY());
		} else {
			cyclesLost++;
		}
	}

	private void checkIfMissing() {

		if (currentSize() < mu * averageSize()) {

			if (state == State.STABLE) {
				System.out.println("A countor is missing OH NOES");
				state = State.MISSING;
			}

		} else {

			if (state != State.STABLE) {
				cyclesLost = 0;
				System.out.println("Got it back!");
				state = State.STABLE;
			}
		}
	}

	public long averageSize() {
		if (mutationCount == 0) {
			return currentSize();
		}

		return accumulatedSize / mutationCount;
	}

	public long currentSize() {
		return lin.size();
	}

	public State getState() {
		return state;
	}

	public Point getLastCentroid() {
		return lastCentroid;
	}

	public int cyclesLost() {
		return cyclesLost;
	}

	public ColorPoint getLastStdDev() {
		return lastStdDev;
	}

	public void setLastStdDev(final ColorPoint lastStdDev) {
		System.out.println("Std Dev = " + lastStdDev.red + ", " + lastStdDev.blue + ", " + lastStdDev.green);
		this.lastStdDev = lastStdDev;
	}

	public void setType(final ColorPoint.Type type) {
		this.type = type;
	}

	public ColorPoint.Type getType() {
		return type;
	}

	public void printValues(BufferedImage frame) {
		for (Point p : points) {
			ColorPoint point = ColorPoint.buildFromRGB(ColorPoint.Type.HSI, frame.getRGB(p.x, p.y));
			// System.out.println("(" + point.red + ", " + point.green + ")");
			System.out.println(point.red + ", " + point.green + "," + point.blue);
		}
	}
}

